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
public class ParticleBlackPowderSmoke extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation(MODID, "textures/particles/particle_base.png");

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.depthMask(false);
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(770, 771);
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
            return MODID + ":black_powder_smoke";
        }
    };

    private final float hue;
    private final float baseSize;


    public ParticleBlackPowderSmoke(ClientLevel world, double x, double y, double z,
                                    double motionX, double motionY, double motionZ, float scale) {
        super(world, x, y, z, motionX, motionY, motionZ);
        this.lifetime = 30 + random.nextInt(15);
        this.baseSize = scale * 0.9F + random.nextFloat() * 0.2F;
        this.gravity = 0F;
        this.hue = 20F + random.nextFloat() * 20F;
        Color color = Color.getHSBColor(hue / 255F, 1F, 1F);
        this.rCol = color.getRed() / 255F;
        this.gCol = color.getGreen() / 255F;
        this.bCol = color.getBlue() / 255F;
        this.alpha = 0.25F;
        this.hasPhysics = false;
        this.friction = 0.96F;

        // Добавляем случайное отклонение к движению
        this.xd = motionX * (0.9 + random.nextDouble() * 0.2);
        this.yd = motionY * (0.9 + random.nextDouble() * 0.2);
        this.zd = motionZ * (0.9 + random.nextDouble() * 0.2);

        // Добавляем небольшой разброс
        this.xd += (random.nextDouble() - 0.5) * 0.05;
        this.yd += (random.nextDouble() - 0.5) * 0.05;
        this.zd += (random.nextDouble() - 0.5) * 0.05;
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

        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= this.friction;
        this.yd *= this.friction;
        this.zd *= this.friction;

        float ageRatio = (float) this.age / (float) this.lifetime;
        Color color = Color.getHSBColor(
                hue / 255F,
                Mth.clamp(1F - ageRatio * 4F, 0F, 1F),
                Mth.clamp(1.25F - ageRatio * 2F, 0.7F, 1F)
        );

        this.rCol = color.getRed() / 255F;
        this.gCol = color.getGreen() / 255F;
        this.bCol = color.getBlue() / 255F;
        this.alpha = (float) Math.pow(1 - Math.min(ageRatio, 1), 0.25) * 0.25F;
    }


    public float getQuadSize(float scale) {
        float ageRatio = (float) this.age / (float) this.lifetime;
        return this.baseSize * (0.25f + ageRatio + this.age * 0.025f);
    }

    @Override
    public int getLightColor(float partialTick) {
        return 15728880; // FULL_BRIGHT
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

        Quaternionf rotation = camera.rotation();
        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-1.0F, -1.0F, 0.0F);
        vertices[1] = new Vector3f(-1.0F, 1.0F, 0.0F);
        vertices[2] = new Vector3f(1.0F, 1.0F, 0.0F);
        vertices[3] = new Vector3f(1.0F, -1.0F, 0.0F);

        float size = this.getQuadSize(partialTicks);

        for(int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(rotation);
            vertex.mul(size);
            vertex.add(x, y, z);
        }

        int light = this.getLightColor(partialTicks);

        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(0, 1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(0, 0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(1, 0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(rCol, gCol, bCol, alpha)
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
                                       double motionX, double motionY, double motionZ) {
            return new ParticleBlackPowderSmoke(world, x, y, z, motionX, motionY, motionZ, 1.0f);
        }
    }
}