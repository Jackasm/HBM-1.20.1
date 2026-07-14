package com.hbm.particle;

import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ParticleFoam extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation.ResLocation(RefStrings.MODID, "textures/particles/particle_base.png");

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return RefStrings.MODID + ":foam";
        }
    };

    private int age;
    public int maxAge;
    private float baseScale = 1.0F;
    private float maxScale = 1.5F;
    private float quadSize;

    private List<TrailPoint> trail = new ArrayList<>();
    private int trailLength = 15;
    private float initialVelocity;
    private float buoyancy = 0.05F;
    private float jitter = 0.15F;
    private float drag = 0.96F;
    private int explosionPhase; // 0=burst up, 1=peak, 2=settle

    private static class TrailPoint {
        double x, y, z;

        public TrailPoint(double x, double y, double z) {
            this.x = x;
            this.y = y;
            this.z = z;
        }
    }

    public ParticleFoam(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.maxAge = 60 + this.random.nextInt(60);
        this.gravity = 0.005F + this.random.nextFloat() * 0.015F;

        this.initialVelocity = 2.0F + this.random.nextFloat() * 3.0F;
        this.yd = this.initialVelocity;

        double angle = this.random.nextDouble() * Math.PI * 2;
        double strength = this.random.nextDouble() * 0.5;
        this.xd = Math.cos(angle) * strength;
        this.zd = Math.sin(angle) * strength;

        this.explosionPhase = 0;
        this.quadSize = 0.3F + this.random.nextFloat() * 0.7F;
        this.alpha = 1.0F;
    }

    public void setMotion(double x, double y, double z) {
        this.xd = x;
        this.yd = y;
        this.zd = z;
    }

    public void setBaseScale(float f) { this.baseScale = f; }
    public void setMaxScale(float f) { this.maxScale = f; }
    public void setTrailLength(int length) { this.trailLength = length; }
    public void setBuoyancy(float buoyancy) { this.buoyancy = buoyancy; }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        trail.add(0, new TrailPoint(x, y, z));

        while (trail.size() > trailLength) {
            trail.remove(trail.size() - 1);
        }

        this.age++;

        if (this.age >= this.maxAge) {
            this.remove();
            return;
        }

        float phaseRatio = (float) age / (float) maxAge;
        if (phaseRatio < 0.3F) {
            explosionPhase = 0;

            if (phaseRatio < 0.15F) {
                this.yd += buoyancy * 6.0F;
            } else {
                this.yd += buoyancy * (1.0F - (phaseRatio / 0.3F)) * 2.0F;
            }

            this.quadSize = baseScale + (maxScale - baseScale) * (phaseRatio / 0.3F);
        } else if (phaseRatio < 0.6F) {
            explosionPhase = 1;
            this.yd *= 0.98F;
            this.quadSize = maxScale;
        } else {
            explosionPhase = 2;
            this.yd -= this.gravity;
            this.quadSize = maxScale * (1.0F - ((phaseRatio - 0.6F) / 0.4F) * 0.7F);
        }

        this.alpha = 0.8F * (1.0F - phaseRatio * phaseRatio);

        this.xd += (this.random.nextFloat() - 0.5F) * jitter;
        this.zd += (this.random.nextFloat() - 0.5F) * jitter;

        this.xd *= drag;
        this.yd *= drag;
        this.zd *= drag;

        this.move(this.xd, this.yd, this.zd);

        if (this.onGround) {
            this.remove();
        }
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        renderFoamBubbles(buffer, camera, partialTicks, this.x, this.y, this.z, this.quadSize, this.alpha);

        for (int i = 1; i < trail.size(); i++) {
            TrailPoint point = trail.get(i);
            float trailScale = this.quadSize * (1.0F - (float)i / trailLength);
            float trailAlpha = this.alpha * (1.0F - (float)i / trailLength) * 0.7F;

            renderFoamBubbles(buffer, camera, partialTicks, point.x, point.y, point.z, trailScale, trailAlpha);
        }
    }

    private void renderFoamBubbles(VertexConsumer buffer, Camera camera, float partialTicks,
                                   double x, double y, double z, float scale, float alpha) {
        Vec3 camPos = camera.getPosition();
        float baseX = (float) (Mth.lerp(partialTicks, this.xo, x) - camPos.x());
        float baseY = (float) (Mth.lerp(partialTicks, this.yo, y) - camPos.y());
        float baseZ = (float) (Mth.lerp(partialTicks, this.zo, z) - camPos.z());

        Quaternionf rotation = new Quaternionf().rotationYXZ(
                -camera.getYRot() * ((float) Math.PI / 180F),
                camera.getXRot() * ((float) Math.PI / 180F),
                0.0F
        );

        Random urandom = new Random(this.hashCode() + (long)(x * 100) + (long)(y * 10) + (long)z);
        int bubbleCount = explosionPhase == 0 ? 8 : (explosionPhase == 1 ? 6 : 4);

        for (int i = 0; i < bubbleCount; i++) {
            float whiteness = 0.9F + urandom.nextFloat() * 0.1F;
            float bubbleScale = scale * (urandom.nextFloat() * 0.5F + 0.75F);
            float offset = explosionPhase == 0 ? 0.4F : (explosionPhase == 1 ? 0.6F : 0.9F);

            float pX = (float) (baseX + (urandom.nextGaussian() * offset));
            float pY = (float)(baseY + (urandom.nextGaussian() * offset * 0.7F));
            float pZ = (float)(baseZ + (urandom.nextGaussian() * offset));

            Vector3f[] vertices = new Vector3f[4];
            vertices[0] = new Vector3f(-bubbleScale, -bubbleScale, 0);
            vertices[1] = new Vector3f(-bubbleScale, bubbleScale, 0);
            vertices[2] = new Vector3f(bubbleScale, bubbleScale, 0);
            vertices[3] = new Vector3f(bubbleScale, -bubbleScale, 0);

            for (int j = 0; j < 4; j++) {
                vertices[j].rotate(rotation);
                vertices[j].add(pX, pY, pZ);
            }

            int light = this.getLightColor(partialTicks);

            float u0 = 0;
            float u1 = 1;
            float v0 = 0;
            float v1 = 1;

            buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                    .color(whiteness, whiteness, whiteness, alpha)
                    .uv(u1, v1)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                    .color(whiteness, whiteness, whiteness, alpha)
                    .uv(u1, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                    .color(whiteness, whiteness, whiteness, alpha)
                    .uv(u0, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                    .color(whiteness, whiteness, whiteness, alpha)
                    .uv(u0, v1)
                    .uv2(light)
                    .endVertex();
        }
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    @Override
    protected int getLightColor(float partialTick) {
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.hasChunkAt(pos) ? LightTexture.pack(15, 15) : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory() {}

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel world,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleFoam(world, x, y, z);
        }
    }
}