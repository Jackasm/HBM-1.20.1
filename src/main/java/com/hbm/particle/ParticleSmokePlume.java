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
public class ParticleSmokePlume extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation.ResLocation(RefStrings.MODID, "textures/particles/contrail.png");

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
            return RefStrings.MODID + ":smoke_plume";
        }
    };

    private final TextureManager textureManager;
    private int maxAge;
    private float prevScale;
    private float currentScale;
    private float red, green, blue;

    public ParticleSmokePlume(ClientLevel level, double x, double y, double z,
                              double motionX, double motionY, double motionZ) {
        super(level, x, y, z);
        this.textureManager = Minecraft.getInstance().getTextureManager();
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.maxAge = 80 + this.random.nextInt(20);
        this.lifetime = this.maxAge;
        this.currentScale = 0.25F;
        this.prevScale = 0.25F;
        this.hasPhysics = false;

        // Цвет как в оригинале (серовато-белый с небольшим разбросом)
        float colorVar = random.nextFloat() * 0.25F + 0.75F;
        this.red = colorVar;
        this.green = colorVar;
        this.blue = colorVar;

        // Устанавливаем размер частицы
        this.setSize(0.25F, 0.25F);
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.prevScale = this.currentScale;

        float progress = (float) this.age / (float) this.maxAge;
        this.alpha = 1.0F - progress;
        this.currentScale = 0.25F + progress * 2.0F;

        if (this.age++ >= this.maxAge) {
            this.remove();
            return;
        }

        this.move(this.xd, this.yd + (this.currentScale - this.prevScale), this.zd);

        if (this.onGround) {
            double length = Math.sqrt(this.xd * this.xd + this.yd * this.yd + this.zd * this.zd);
            if (length > 0) {
                this.yd = length;
            }
        }

        this.xd *= 0.925D;
        this.yd *= 0.925D;
        this.zd *= 0.925D;
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

        float scale = Mth.lerp(partialTicks, this.prevScale, this.currentScale);
        float currentAlpha = this.alpha;

        Random rand = new Random(50);

        for (int i = 0; i < 6; i++) {
            double dx = (rand.nextGaussian() - 1.0D) * 0.5D;
            double dy = (rand.nextGaussian() - 1.0D) * 0.15D;
            double dz = (rand.nextGaussian() - 1.0D) * 0.5D;
            double size = scale * (0.5 + rand.nextDouble() * 0.5);

            float pX = baseX + (float) dx;
            float pY = baseY + (float) dy;
            float pZ = baseZ + (float) dz;

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

            // Небольшой разброс цвета для каждой копии
            float colorVar = 0.1F + rand.nextFloat() * 0.25F;
            float r = this.red * (0.9F + colorVar * 0.2F);
            float g = this.green * (0.9F + colorVar * 0.2F);
            float b = this.blue * (0.9F + colorVar * 0.2F);

            buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                    .color(r, g, b, currentAlpha)
                    .uv(u1, v1)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                    .color(r, g, b, currentAlpha)
                    .uv(u1, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                    .color(r, g, b, currentAlpha)
                    .uv(u0, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                    .color(r, g, b, currentAlpha)
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
            return new ParticleSmokePlume(world, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}