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
public class ParticleRocketFlame extends Particle {

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
            return RefStrings.MODID + ":rocket_flame";
        }
    };

    private final TextureManager textureManager;
    private int age;
    private int maxAge;
    private float scale;

    public ParticleRocketFlame(ClientLevel level, double x, double y, double z,
                               double motionX, double motionY, double motionZ) {
        super(level, x, y, z);
        this.textureManager = Minecraft.getInstance().getTextureManager();
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.maxAge = 300 + this.random.nextInt(50);
        this.scale = 1.0F;
        this.lifetime = this.maxAge;
        this.hasPhysics = false;
    }

    public ParticleRocketFlame setScale(float scale) {
        this.scale = scale;
        return this;
    }

    public ParticleRocketFlame setMaxAge(int maxAge) {
        this.maxAge = maxAge;
        this.lifetime = maxAge;
        return this;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.maxAge) {
            this.remove();
            return;
        }

        this.xd *= 0.91D;
        this.yd *= 0.91D;
        this.zd *= 0.91D;

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

        // Фиксированный seed для стабильности (как в оригинале используется getEntityId)
        Random rand = new Random(this.hashCode());

        float progress = (float) this.age / (float) this.maxAge;
        float dark = 1.0F - Math.min(progress / 0.25F, 1.0F);
        float alpha = (float) Math.pow(1.0F - Math.min(progress, 1.0F), 0.5);

        for (int i = 0; i < 10; i++) {
            float add = rand.nextFloat() * 0.3F;

            // Цвета как в оригинале: красный + оранжевый (без зеленого!)
            float r = 1.0F * dark + add;
            float g = 0.6F * dark + add * 0.5F;  // Оранжевый оттенок
            float b = 0.0F + add * 0.3F;         // Минимум синего для теплого свечения
            float a = alpha * 0.75F;

            // Ограничиваем значения в пределах 0-1
            r = Math.min(r, 1.0F);
            g = Math.min(g, 1.0F);
            b = Math.min(b, 1.0F);

            float spread = (float) Math.pow(progress * 4.0F, 1.5) + 1.0F;
            spread *= this.scale;

            float size = (rand.nextFloat() * 0.5F + 0.1F + progress * 2.0F) * this.scale;

            float pX = baseX + (float) ((rand.nextGaussian() - 1.0) * 0.2 * spread);
            float pY = baseY + (float) ((rand.nextGaussian() - 1.0) * 0.5 * spread);
            float pZ = baseZ + (float) ((rand.nextGaussian() - 1.0) * 0.2 * spread);

            Vector3f[] vertices = new Vector3f[4];
            vertices[0] = new Vector3f(-size, -size, 0);
            vertices[1] = new Vector3f(-size, size, 0);
            vertices[2] = new Vector3f(size, size, 0);
            vertices[3] = new Vector3f(size, -size, 0);

            for (int j = 0; j < 4; j++) {
                vertices[j].rotate(rotation);
                vertices[j].add(pX, pY, pZ);
            }

            int light = LightTexture.pack(15, 15);

            // ОРАНЖЕВО-КРАСНОЕ ПЛАМЯ
            buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                    .color(r, g, b, a)
                    .uv(1, 1)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                    .color(r, g, b, a)
                    .uv(1, 0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                    .color(r, g, b, a)
                    .uv(0, 0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                    .color(r, g, b, a)
                    .uv(0, 1)
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
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel level,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleRocketFlame(level, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}