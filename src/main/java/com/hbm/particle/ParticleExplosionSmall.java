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

import java.awt.Color;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class ParticleExplosionSmall extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation(MODID, "textures/particles/particle_base.png");

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771); // STANDARD_BLEND
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }

        @Override
        public String toString() {
            return MODID + ":explosion_small";
        }
    };

    private final float hue;
    private final float initialScale;
    private float rotationPitch;
    private float prevRotationPitch;
    private final float particleGravity;
    private float quadSize;

    // Конструктор как в оригинале
    public ParticleExplosionSmall(ClientLevel world, double x, double y, double z, float scale, float speedMult) {
        super(world, x, y, z);

        this.lifetime = 25 + random.nextInt(10);
        this.initialScale = scale * 0.9F + random.nextFloat() * 0.2F;
        this.quadSize = this.initialScale;

        // Скорость как в оригинале
        this.xd = world.random.nextGaussian() * speedMult;
        this.yd = 0;
        this.zd = world.random.nextGaussian() * speedMult;

        this.particleGravity = random.nextFloat() * -0.01F;

        // Цвет как в оригинале
        this.hue = 20F + random.nextFloat() * 20F;
        Color color = Color.getHSBColor(hue / 255F, 1F, 1F);
        this.rCol = color.getRed() / 255F;
        this.gCol = color.getGreen() / 255F;
        this.bCol = color.getBlue() / 255F;
        this.alpha = 1.0F;

        this.hasPhysics = false; // Аналог noClip = true
        this.rotationPitch = 0;
        this.prevRotationPitch = 0;
        this.friction = 0.65F; // Для замедления motionX *= 0.65D
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

        // Сохраняем предыдущее вращение как в оригинале
        this.prevRotationPitch = this.rotationPitch;

        // Гравитация как в оригинале
        this.yd -= particleGravity;

        // Вращение как в оригинале (используем hashCode() вместо getEntityId())
        float ageScaled = (float) this.age / (float) this.lifetime;
        this.rotationPitch += (float) ((1 - ageScaled) * 5 * ((this.hashCode() % 2) - 0.5));

        // Замедление движения как в оригинале (motionX *= 0.65D)
        this.xd *= this.friction;
        this.zd *= this.friction;

        // Движение
        this.move(this.xd, this.yd, this.zd);

        // Обновление цвета и прозрачности как в оригинале
        Color color = Color.getHSBColor(
                hue / 255F,
                Math.max(1F - ageScaled * 2F, 0),
                Mth.clamp(1.25F - ageScaled * 2F, hue * 0.01F - 0.1F, 1F)
        );

        this.rCol = color.getRed() / 255F;
        this.gCol = color.getGreen() / 255F;
        this.bCol = color.getBlue() / 255F;

        // Прозрачность как в оригинале
        this.alpha = (float) Math.pow(1 - Math.min(ageScaled, 1), 0.25);

        // Размер как в оригинале (с учетом interp в оригинальном renderParticle)
        this.quadSize = (0.25F + 1 - (float)Math.pow(1 - ageScaled, 4) + this.age * 0.02F) * this.initialScale;
    }

    // Метод для получения размера частицы

    public float getQuadSize() {
        return this.quadSize;
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    @Override
    protected int getLightColor(float partialTick) {
        // Увеличиваем яркость для эффекта свечения как в оригинале
        return 240 << 20 | 240 << 4; // 0xF000F0
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        // Интерполируем вращение как в оригинале
        float rotation = Mth.lerp(partialTicks, this.prevRotationPitch, this.rotationPitch);

        Quaternionf quaternion = camera.rotation();
        Quaternionf rotationQuat = new Quaternionf(quaternion);
        rotationQuat.rotateZ(rotation * (float)Math.PI / 180.0F); // Поворачиваем по Z (pitch)

        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-1.0F, -1.0F, 0.0F);
        vertices[1] = new Vector3f(-1.0F, 1.0F, 0.0F);
        vertices[2] = new Vector3f(1.0F, 1.0F, 0.0F);
        vertices[3] = new Vector3f(1.0F, -1.0F, 0.0F);

        float size = this.getQuadSize();

        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(rotationQuat); // Применяем вращение
            vertex.mul(size);
            vertex.add(x, y, z);
        }

        int light = this.getLightColor(partialTicks);

        // Прозрачность как в оригинале (alpha * 0.5F в renderParticle)
        float renderAlpha = this.alpha * 0.5F;

        // Рендерим частицу
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(rCol, gCol, bCol, renderAlpha)
                .uv(0, 1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(rCol, gCol, bCol, renderAlpha)
                .uv(0, 0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(rCol, gCol, bCol, renderAlpha)
                .uv(1, 0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(rCol, gCol, bCol, renderAlpha)
                .uv(1, 1)
                .uv2(light)
                .endVertex();
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory() {
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel world,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            // xSpeed = scale, ySpeed = speedMult как в оригинале
            return new ParticleExplosionSmall(world, x, y, z, (float)xSpeed, (float)ySpeed);
        }
    }
}