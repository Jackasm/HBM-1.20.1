package com.hbm.particle;

import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class ParticleRadiationFog extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation.ResLocation(RefStrings.MODID, "textures/particles/fog.png");

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
            return RefStrings.MODID + ":radiation_fog";
        }
    };

    private final TextureManager textureManager;
    private int maxAge;
    private float red, green, blue;
    private float scale;

    public ParticleRadiationFog(ClientLevel level, double x, double y, double z) {
        this(level, x, y, z, 0.85F, 0.9F, 0.5F, 7.5F);
    }

    public ParticleRadiationFog(ClientLevel level, double x, double y, double z, float red, float green, float blue, float scale) {
        super(level, x, y, z);
        this.textureManager = Minecraft.getInstance().getTextureManager();
        this.maxAge = 400; // 400 тиков в оригинале
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.scale = scale;
        this.hasPhysics = false;
        this.lifetime = 400;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        this.xd *= 0.9599999785423279D;
        this.yd *= 0.9599999785423279D;
        this.zd *= 0.9599999785423279D;

        if (this.onGround) {
            this.xd *= 0.699999988079071D;
            this.zd *= 0.699999988079071D;
        }

        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float baseX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float baseY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float baseZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        Quaternionf rotation = new Quaternionf().rotationYXZ(
                -camera.getYRot() * ((float) Math.PI / 180F),
                camera.getXRot() * ((float) Math.PI / 180F),
                0.0F
        );

        float progress = (float) this.age / (float) this.lifetime;
        float alpha = (float) Math.sin(progress * Math.PI) * 0.125F;

        Random rand = new Random(50);

        for (int i = 0; i < 25; i++) {
            double dx = (rand.nextGaussian() - 1.0D) * 2.5D;
            double dy = (rand.nextGaussian() - 1.0D) * 0.15D;
            double dz = (rand.nextGaussian() - 1.0D) * 2.5D;
            double size = rand.nextDouble() * this.scale;

            float pX = baseX + (float) dx + (float) (rand.nextGaussian() * 0.5);
            float pY = baseY + (float) dy + (float) (rand.nextGaussian() * 0.5);
            float pZ = baseZ + (float) dz + (float) (rand.nextGaussian() * 0.5);

            Vector3f[] vertices = new Vector3f[4];
            vertices[0] = new Vector3f((float) -size, (float) -size, 0);
            vertices[1] = new Vector3f((float) -size, (float) size, 0);
            vertices[2] = new Vector3f((float) size, (float) size, 0);
            vertices[3] = new Vector3f((float) size, (float) -size, 0);

            for (int j = 0; j < 4; j++) {
                vertices[j].rotate(rotation);
                vertices[j].add(pX, pY, pZ);
            }

            int light = LightTexture.pack(15, 15);

            float u0 = 0;
            float u1 = 1;
            float v0 = 0;
            float v1 = 1;

            buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                    .color(red, green, blue, alpha)
                    .uv(u1, v1)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                    .color(red, green, blue, alpha)
                    .uv(u1, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                    .color(red, green, blue, alpha)
                    .uv(u0, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                    .color(red, green, blue, alpha)
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
        return LightTexture.pack(15, 15);
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory() {}

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel world,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleRadiationFog(world, x, y, z);
        }
    }
}