package com.hbm.particle;

import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.TextureManager;
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
public class ParticleMukeFlash extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation.ResLocation(RefStrings.MODID, "textures/particles/flare.png");

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
            return RefStrings.MODID + ":muke_flash";
        }
    };

    private final TextureManager textureManager;
    private final boolean balefire;

    public ParticleMukeFlash(ClientLevel level, double x, double y, double z, boolean balefire) {
        super(level, x, y, z);
        this.textureManager = Minecraft.getInstance().getTextureManager();
        this.balefire = balefire;
        this.lifetime = 20;
        this.hasPhysics = false;
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

        if (this.age == 15) {
            // Stem
            for (double d = 0.0D; d <= 1.8D; d += 0.1) {
                ParticleMukeCloud cloud = getCloud(this.level, this.x, this.y, this.z,
                        random.nextGaussian() * 0.05, d + random.nextGaussian() * 0.02, random.nextGaussian() * 0.05);
                Minecraft.getInstance().particleEngine.add(cloud);
            }

            // Ground
            for (int i = 0; i < 100; i++) {
                ParticleMukeCloud cloud = getCloud(this.level, this.x, this.y + 0.5, this.z,
                        random.nextGaussian() * 0.5, random.nextInt(5) == 0 ? 0.02 : 0, random.nextGaussian() * 0.5);
                Minecraft.getInstance().particleEngine.add(cloud);
            }

            // Mush
            for (int i = 0; i < 75; i++) {
                double dx = random.nextGaussian() * 0.5;
                double dz = random.nextGaussian() * 0.5;

                if (dx * dx + dz * dz > 1.5) {
                    dx *= 0.5;
                    dz *= 0.5;
                }

                double dy = 1.8 + (random.nextDouble() * 3 - 1.5) * (0.75 - (dx * dx + dz * dz)) * 0.5;

                ParticleMukeCloud cloud = getCloud(this.level, this.x, this.y, this.z,
                        dx, dy + random.nextGaussian() * 0.02, dz);
                Minecraft.getInstance().particleEngine.add(cloud);
            }
        }
    }

    private ParticleMukeCloud getCloud(ClientLevel level, double x, double y, double z, double mx, double my, double mz) {
        if (this.balefire) {
            return new ParticleMukeCloudBF(level, x, y, z, mx, my, mz);
        } else {
            return new ParticleMukeCloud(level, x, y, z, mx, my, mz);
        }
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

        float progress = (float) this.age + partialTicks;
        float alpha = 1.0F - (progress / (float) this.lifetime);
        float scale = progress * 3.0F + 1.0F;

        Random rand = new Random();

        for (int i = 0; i < 24; i++) {
            rand.setSeed(i * 31L + 1);

            float pX = baseX + (float) (rand.nextDouble() * 15 - 7.5);
            float pY = baseY + (float) (rand.nextDouble() * 7.5 - 3.75);
            float pZ = baseZ + (float) (rand.nextDouble() * 15 - 7.5);

            float u0 = 0;
            float u1 = 1;
            float v0 = 0;
            float v1 = 1;

            Vector3f[] vertices = new Vector3f[4];
            vertices[0] = new Vector3f(-scale, -scale, 0);
            vertices[1] = new Vector3f(-scale, scale, 0);
            vertices[2] = new Vector3f(scale, scale, 0);
            vertices[3] = new Vector3f(scale, -scale, 0);

            for (int j = 0; j < 4; j++) {
                vertices[j].rotate(rotation);
                vertices[j].add(pX, pY, pZ);
            }

            int light = this.getLightColor(partialTicks);

            buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                    .color(1.0F, 0.9F, 0.75F, alpha * 0.5F)
                    .uv(u1, v1)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                    .color(1.0F, 0.9F, 0.75F, alpha * 0.5F)
                    .uv(u1, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                    .color(1.0F, 0.9F, 0.75F, alpha * 0.5F)
                    .uv(u0, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                    .color(1.0F, 0.9F, 0.75F, alpha * 0.5F)
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
}