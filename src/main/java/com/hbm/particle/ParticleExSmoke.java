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
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ParticleExSmoke extends Particle {

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
            return RefStrings.MODID + ":ex_smoke";
        }
    };

    private int age;
    public int maxAge;

    public ParticleExSmoke(ClientLevel level, double x, double y, double z,
                           double motionX, double motionY, double motionZ) {
        super(level, x, y, z);
        this.xd = motionX;
        this.yd = motionY;
        this.zd = motionZ;
        this.maxAge = 100 + this.random.nextInt(40);
        this.hasPhysics = false;

        // Начальный цвет - серый
        this.rCol = 0.5F;
        this.gCol = 0.5F;
        this.bCol = 0.5F;
        this.alpha = 1.0F;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        // Альфа уменьшается со временем
        this.alpha = 1.0F - ((float) this.age / (float) this.maxAge);

        this.age++;

        if (this.age >= this.maxAge) {
            this.remove();
            return;
        }

        // Затухание скорости
        this.xd *= 0.76;
        this.yd *= 0.76;
        this.zd *= 0.76;

        this.move(this.xd, this.yd, this.zd);
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        RandomSource urandom = RandomSource.create(this.hashCode());

        Vec3 camPos = camera.getPosition();
        float baseX = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float baseY = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float baseZ = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        for (int i = 0; i < 6; i++) {

            float gray = urandom.nextFloat() * 0.25F + 0.25F;
            float scale = urandom.nextFloat() + 0.5F;

            float pX = baseX + (float) ((urandom.nextGaussian() - 1.0) * 0.75);
            float pY = baseY + (float) ((urandom.nextGaussian() - 1.0) * 0.75);
            float pZ = baseZ + (float) ((urandom.nextGaussian() - 1.0) * 0.75);

            Quaternionf rotation = new Quaternionf().rotationYXZ(
                    -camera.getYRot() * ((float) Math.PI / 180F),
                    camera.getXRot() * ((float) Math.PI / 180F),
                    0.0F
            );

            float u0 = 0;
            float u1 = 1;
            float v0 = 0;
            float v1 = 1;

            Vector3f[] vertices = new Vector3f[4];
            vertices[0] = new Vector3f(-scale, -scale, 0.0F);
            vertices[1] = new Vector3f(-scale, scale, 0.0F);
            vertices[2] = new Vector3f(scale, scale, 0.0F);
            vertices[3] = new Vector3f(scale, -scale, 0.0F);

            for (int j = 0; j < 4; j++) {
                vertices[j].rotate(rotation);
                vertices[j].add(pX, pY, pZ);
            }

            int light = this.getLightColor(partialTicks);

            buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                    .color(gray, gray, gray, this.alpha)
                    .uv(u1, v1)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                    .color(gray, gray, gray, this.alpha)
                    .uv(u1, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                    .color(gray, gray, gray, this.alpha)
                    .uv(u0, v0)
                    .uv2(light)
                    .endVertex();
            buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                    .color(gray, gray, gray, this.alpha)
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
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.hasChunkAt(pos) ? LightTexture.pack(15, 15) : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory() {}

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel world,
                                       double x, double y, double z,
                                       double xSpeed, double ySpeed, double zSpeed) {
            return new ParticleExSmoke(world, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}