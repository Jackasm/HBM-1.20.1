package com.hbm.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
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

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ParticlePlasmaBlast extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation(MODID, "textures/particles/shockwave.png");

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 1);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
            RenderSystem.disableCull();

        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();

            // Восстанавливаем состояние
            RenderSystem.depthMask(true);
            RenderSystem.enableCull();
            RenderSystem.blendFunc(770, 771);

        }

        @Override
        public String toString() {
            return MODID + ":plasma_blast";
        }
    };

    private final float rotationPitch;
    private final float rotationYaw;
    private final float baseScale;

    public ParticlePlasmaBlast(ClientLevel world, double x, double y, double z,
                               float r, float g, float b,
                               float pitch, float yaw, float scale) {
        super(world, x, y, z);

        this.lifetime = 20;
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.alpha = 1.0F;
        this.rotationPitch = pitch;
        this.rotationYaw = yaw;
        this.baseScale = scale;


        // Аддитивное смешивание для эффекта свечения
        this.hasPhysics = false;
    }

    public void setMaxAge(int maxAge) {
        this.lifetime = maxAge;
    }



    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        if (this.age++ >= this.lifetime) {
            this.remove();
        }

        // Обновляем альфа-канал
        float ageFraction = (float)this.age / (float)this.lifetime;
        this.alpha = 1.0F - ageFraction;



    }

    @Override
    public int getLightColor(float partialTick) {
        return 240; // Высокая яркость для эффекта свечения
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        // Создаем кватернион для вращения
        Quaternionf rotation = new Quaternionf();
        rotation.rotateY((float)Math.toRadians(this.rotationYaw));
        rotation.rotateX((float)Math.toRadians(this.rotationPitch));

        // Векторы для углов квадрата (в плоскости XZ)
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-1.0F, 0.0F, -1.0F);
        vertices[1] = new Vector3f(-1.0F, 0.0F, 1.0F);
        vertices[2] = new Vector3f(1.0F, 0.0F, 1.0F);
        vertices[3] = new Vector3f(1.0F, 0.0F, -1.0F);

        // Рассчитываем текущий размер с интерполяцией
        float ageWithPartial = (float)this.age + partialTicks;
        float scaleProgress = (float)Math.exp(ageWithPartial * -0.125);
        float scale = (1.0F - scaleProgress) * this.baseScale;

        // Применяем вращение, масштаб и позицию
        for (int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(rotation);
            vertex.mul(scale);
            vertex.add(x, y, z);
        }

        int light = this.getLightColor(partialTicks);

        // UV координаты для текстуры (полная текстура)
        float u0 = 0.0F;
        float u1 = 1.0F;
        float v0 = 0.0F;
        float v1 = 1.0F;

        // Рисуем квадрат (в порядке против часовой стрелки)
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(u1, v1)  // правый нижний
                .uv2(light)
                .endVertex();

        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(u1, v0)  // правый верхний
                .uv2(light)
                .endVertex();

        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(u0, v0)  // левый верхний
                .uv2(light)
                .endVertex();

        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(u0, v1)  // левый нижний
                .uv2(light)
                .endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        private final float pitch;
        private final float yaw;
        private final float scale;
        private final float r, g, b;

        // Конструктор с параметрами
        public Factory(float pitch, float yaw, float scale, float r, float g, float b) {
            this.pitch = pitch;
            this.yaw = yaw;
            this.scale = scale;
            this.r = r;
            this.g = g;
            this.b = b;
        }

        // Конструктор по умолчанию
        public Factory() {
            this(0, 0, 2.0F, 0.5F, 0.5F, 1.0F);
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel world,
                                       double x, double y, double z,
                                       double motionX, double motionY, double motionZ) {
            return new ParticlePlasmaBlast(world, x, y, z, r, g, b, pitch, yaw, scale);
        }
    }

}