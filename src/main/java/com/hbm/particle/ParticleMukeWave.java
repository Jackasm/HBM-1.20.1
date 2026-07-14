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
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ParticleMukeWave extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation.ResLocation(RefStrings.MODID, "textures/particles/shockwave.png");

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 1);
            RenderSystem.depthMask(false);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            RenderSystem.disableCull();
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.enableCull();
            RenderSystem.depthMask(true);
            RenderSystem.blendFunc(770, 771);
        }

        @Override
        public String toString() {
            return RefStrings.MODID + ":muke_wave";
        }
    };

    private float waveScale = 45F;

    public ParticleMukeWave(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);
        this.lifetime = 25;
        this.hasPhysics = false;
    }

    public void setup(float scale, int maxAge) {
        this.waveScale = scale;
        this.lifetime = maxAge;
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        // Альфа уменьшается со временем
        this.alpha = 1.0F - ((float) this.age + partialTicks) / (float) this.lifetime;

        // Масштаб: (1 - exp(-age * 0.125)) * waveScale
        float scale = (1.0F - (float) Math.exp((this.age + partialTicks) * -0.125)) * waveScale;

        Vec3 camPos = camera.getPosition();
        float pX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float pY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float pZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        // Кватернион для поворота к камере (оставляем горизонтальным, не поворачиваем)
        // В оригинале частица всегда лежит на плоскости Y=-0.25 от центра

        // UV координаты (весь спрайт)
        float u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;

        // Четыре угла квадрата
        float halfSize = scale;
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-halfSize, -0.25F, -halfSize);
        vertices[1] = new Vector3f(-halfSize, -0.25F, halfSize);
        vertices[2] = new Vector3f(halfSize, -0.25F, halfSize);
        vertices[3] = new Vector3f(halfSize, -0.25F, -halfSize);

        // Смещаем в позицию частицы
        for (int i = 0; i < 4; i++) {
            vertices[i].add(pX, pY, pZ);
        }

        int light = LightTexture.pack(15, 15); // Максимальная яркость как в оригинале (240)

        // Рендерим квад
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(1.0F, 1.0F, 1.0F, this.alpha)
                .uv(u1, v1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(1.0F, 1.0F, 1.0F, this.alpha)
                .uv(u1, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(1.0F, 1.0F, 1.0F, this.alpha)
                .uv(u0, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(1.0F, 1.0F, 1.0F, this.alpha)
                .uv(u0, v1)
                .uv2(light)
                .endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
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
            ParticleMukeWave particle = new ParticleMukeWave(world, x, y, z);
            // Если нужно установить скорость или другие параметры из xSpeed/ySpeed/zSpeed
            return particle;
        }
    }
}