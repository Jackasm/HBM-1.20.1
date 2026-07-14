package com.hbm.particle;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.hbm.particle.helper.FlameCreator;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
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
public class ParticleFlamethrower extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation(MODID, "textures/particles/particle_base.png");

    private final int type;
    private float rotationPitch;
    private float prevRotationPitch;
    private float quadSize;
    private final float baseScale;


    public ParticleFlamethrower(ClientLevel world, double x, double y, double z, int type) {
        super(world, x, y, z);

        this.type = type;
        this.lifetime = 20 + random.nextInt(10);
        this.baseScale = 0.5F;
        this.quadSize = this.baseScale; // Важно: устанавливаем quadSize

        // Движение как в оригинале
        this.xd = world.random.nextGaussian() * 0.02;
        this.yd = 0;
        this.zd = world.random.nextGaussian() * 0.02;

        // Определяем начальный цвет как в оригинале
        float colorValue;
        if(type == FlameCreator.META_BALEFIRE) {
            colorValue = 65F + random.nextFloat() * 35F;
        } else if(type == FlameCreator.META_DIGAMMA) {
            colorValue = 0F - random.nextFloat() * 15F;
        } else {
            colorValue = 15F + random.nextFloat() * 25F;
        }
        float initialColor = colorValue;

        // Устанавливаем начальный цвет
        Color color = Color.getHSBColor(initialColor / 255F, 1F, 1F);
        this.rCol = color.getRed() / 255F;
        this.gCol = color.getGreen() / 255F;
        this.bCol = color.getBlue() / 255F;
        this.alpha = 1.0F;

        // Специальные цвета для OXY и BLACK
        if(type == FlameCreator.META_OXY) {
            this.rCol = 1.0F;
            this.gCol = 1.0F;
            this.bCol = 1.0F;
        }
        if(type == FlameCreator.META_BLACK) {
            this.rCol = 1.0F;
            this.gCol = 1.0F;
            this.bCol = 1.0F;
        }

        this.hasPhysics = false;
        this.rotationPitch = random.nextFloat() * 360F;
        this.prevRotationPitch = this.rotationPitch;
        this.friction = 0.91F; // Для замедления motionX *= 0.91D
        this.gravity = 0.01F; // Для motionY += 0.01D
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

        this.prevRotationPitch = this.rotationPitch;
        this.rotationPitch += (float) (30 * ((this.hashCode() % 2) - 0.5));
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;
        this.yd += this.gravity;
        this.move(this.xd, this.yd, this.zd);

        float ageRatio = (float) this.age / (float) this.lifetime;
        this.quadSize = this.baseScale * (ageRatio * 1.25F + 0.25F);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return FLAMETHROWER_RENDER_TYPE;
    }

    @Override
    protected int getLightColor(float partialTick) {
        // Как в оригинале: return 15728880
        return 15728880; // 240 << 20 | 240 << 4
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, Camera camera, float partialTick) {
        Vec3 cameraPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTick, this.xo, this.x) - cameraPos.x());
        float y = (float)(Mth.lerp(partialTick, this.yo, this.y) - cameraPos.y());
        float z = (float)(Mth.lerp(partialTick, this.zo, this.z) - cameraPos.z());

        // Интерполируем вращение
        float rotation = Mth.lerp(partialTick, this.prevRotationPitch, this.rotationPitch);

        float ageRatio = (this.age + partialTick) / (float)this.lifetime;

        // Вычисляем цвет и альфа как в оригинале
        float renderR = this.rCol;
        float renderG = this.gCol;
        float renderB = this.bCol;
        float renderAlpha;

        if(type == FlameCreator.META_OXY) {
            renderAlpha = 1 - ageRatio;
            float add = ageRatio * 1.25F - 0.25F;
            renderR -= add;
            renderG -= add * 0.75F;
            // B остается без изменений
        } else if(type == FlameCreator.META_BLACK) {
            renderAlpha = 1 - ageRatio;
            float add = ageRatio * 2F - 0.25F;
            renderR -= add * 0.75F;
            renderG -= add;
            renderB -= add * 0.5F;
        } else {
            renderAlpha = (float)Math.pow(1 - Math.min(ageRatio, 1), 0.5);
            float add = 0.75F - ageRatio;
            renderR += add;
            renderG += add;
            renderB += add;
            renderAlpha *= 0.5F; // Как в оригинале: particleAlpha * 0.5F
        }

        // Ограничиваем значения цвета
        renderR = Mth.clamp(renderR, 0.0F, 1.0F);
        renderG = Mth.clamp(renderG, 0.0F, 1.0F);
        renderB = Mth.clamp(renderB, 0.0F, 1.0F);
        renderAlpha = Mth.clamp(renderAlpha, 0.0F, 1.0F);

        // Размер частицы
        float size = this.getQuadSize();

        // Получаем квадратичные координаты для спрайта
        float u0 = 0.0F;
        float u1 = 1.0F;
        float v0 = 0.0F;
        float v1 = 1.0F;

        // Создаем кватернион для вращения
        Quaternionf quaternion = camera.rotation();
        Quaternionf rotationQuat = new Quaternionf(quaternion);
        rotationQuat.rotateZ(rotation * Mth.DEG_TO_RAD);

        // Создаем вершины
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-1.0F, -1.0F, 0.0F);
        vertices[1] = new Vector3f(-1.0F, 1.0F, 0.0F);
        vertices[2] = new Vector3f(1.0F, 1.0F, 0.0F);
        vertices[3] = new Vector3f(1.0F, -1.0F, 0.0F);

        // Преобразуем вершины
        for (Vector3f vertex : vertices) {
            vertex.rotate(rotationQuat);
            vertex.mul(size * 0.5F); // Умножаем на 0.5, так как size - это диаметр
            vertex.add(x, y, z);
        }

        int light = this.getLightColor(partialTick);

        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(renderR, renderG, renderB, renderAlpha)
                .uv(u0, v1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(renderR, renderG, renderB, renderAlpha)
                .uv(u0, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(renderR, renderG, renderB, renderAlpha)
                .uv(u1, v0)
                .uv2(light)
                .endVertex();

        // Второй треугольник
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(renderR, renderG, renderB, renderAlpha)
                .uv(u0, v1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(renderR, renderG, renderB, renderAlpha)
                .uv(u1, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(renderR, renderG, renderB, renderAlpha)
                .uv(u1, v1)
                .uv2(light)
                .endVertex();
    }

    private static final ParticleRenderType FLAMETHROWER_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc(); // Используем дефолтный blend func
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            builder.begin(VertexFormat.Mode.TRIANGLES, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.depthMask(true);
            RenderSystem.disableBlend();
        }

        @Override
        public String toString() {
            return MODID + ":flamethrower";
        }
    };

    private float getQuadSize()
    {
        return this.quadSize;
    }
}