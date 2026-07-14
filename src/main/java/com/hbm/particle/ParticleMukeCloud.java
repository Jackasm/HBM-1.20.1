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
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ParticleMukeCloud extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation.ResLocation(RefStrings.MODID, "textures/particles/explosion.png");

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc(); // GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.enableCull();
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.depthMask(true);
        }

        @Override
        public String toString() {
            return RefStrings.MODID + ":muke_cloud";
        }
    };

    private float friction;
    private float particleScale = 3.0F;

    public ParticleMukeCloud(ClientLevel level, double x, double y, double z,
                             double mx, double my, double mz) {
        super(level, x, y, z);

        this.xd = mx;
        this.yd = my;
        this.zd = mz;

        if (my > 0) {
            this.friction = 0.9F;
            if (my > 0.1) {
                this.lifetime = 92 + random.nextInt(11) + (int)(my * 20);
            } else {
                this.lifetime = 72 + random.nextInt(11);
            }
        } else if (my == 0) {
            this.friction = 0.95F;
            this.lifetime = 52 + random.nextInt(11);
        } else {
            this.friction = 0.85F;
            this.lifetime = 122 + random.nextInt(31);
            this.age = 80;
        }

        this.hasPhysics = false; // noClip будет установлен в tick
    }

    @Override
    public void tick() {
        // noClip в первые 2 тика
        this.hasPhysics = this.age > 2;

        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        this.age++;
        if (this.age >= this.lifetime) {
            this.remove();
            return;
        }

        this.yd -= 0.04D * this.gravity;

        this.move(this.xd, this.yd, this.zd);

        this.xd *= friction;
        this.yd *= friction;
        this.zd *= friction;

        if (this.onGround) {
            this.xd *= 0.7D;
            this.zd *= 0.7D;
        }
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        if (this.age >= this.lifetime) {
            return;
        }

        // Вычисляем индекс текстуры на основе возраста
        int texIndex = this.age * 25 / this.lifetime;
        float f0 = 1.0F / 5.0F; // 5x5 сетка

        float uMin = (texIndex % 5) * f0;
        float uMax = uMin + f0;
        float vMin = (texIndex / 5) * f0;
        float vMax = vMin + f0;

        Vec3 camPos = camera.getPosition();
        float pX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float pY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float pZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        // Поворот к камере
        Quaternionf rotation = new Quaternionf().rotationYXZ(
                -camera.getYRot() * ((float) Math.PI / 180F),
                camera.getXRot() * ((float) Math.PI / 180F),
                0.0F
        );

        float scale = this.particleScale;

        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-scale, -scale, 0.0F);
        vertices[1] = new Vector3f(-scale, scale, 0.0F);
        vertices[2] = new Vector3f(scale, scale, 0.0F);
        vertices[3] = new Vector3f(scale, -scale, 0.0F);

        for (int i = 0; i < 4; i++) {
            vertices[i].rotate(rotation);
            vertices[i].add(pX, pY, pZ);
        }

        int light = LightTexture.pack(15, 15); // Максимальная яркость

        // Рендерим квад
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(1.0F, 1.0F, 1.0F, this.alpha)
                .uv(uMax, vMax)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(1.0F, 1.0F, 1.0F, this.alpha)
                .uv(uMax, vMin)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(1.0F, 1.0F, 1.0F, this.alpha)
                .uv(uMin, vMin)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(1.0F, 1.0F, 1.0F, this.alpha)
                .uv(uMin, vMax)
                .uv2(light)
                .endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    protected ResourceLocation getTexture() {
        return TEXTURE;
    }

    @Override
    protected int getLightColor(float partialTick) {
        return LightTexture.pack(15, 15); // Максимальная яркость
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory() {}

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel world,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleMukeCloud(world, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}