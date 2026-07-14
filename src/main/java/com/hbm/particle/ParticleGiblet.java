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
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ParticleGiblet extends Particle {

    private static final ResourceLocation TEXTURE =
            ResLocation.ResLocation(RefStrings.MODID, "textures/particles/meat.png");

    private static final ParticleRenderType RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.disableBlend();
            RenderSystem.depthMask(true);
            RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
            RenderSystem.setShaderTexture(0, TEXTURE);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
        }

        @Override
        public String toString() {
            return RefStrings.MODID + ":giblet";
        }
    };

    private float momentumYaw;
    private float momentumPitch;
    private float rotationYaw;
    private float rotationPitch;
    private float prevRotationYaw;
    private float prevRotationPitch;

    private float particleSize; // заменяет quadSize

    public ParticleGiblet(ClientLevel world, double x, double y, double z,
                          double mX, double mY, double mZ) {
        super(world, x, y, z);
        this.xd = mX;
        this.yd = mY;
        this.zd = mZ;
        this.lifetime = 140 + random.nextInt(20);
        this.gravity = 2.0F;
        this.hasPhysics = true;
        this.particleSize = 0.1F; // размер частицы

        this.momentumYaw = (float) random.nextGaussian() * 15F;
        this.momentumPitch = (float) random.nextGaussian() * 15F;

        this.rotationYaw = random.nextFloat() * 360F;
        this.rotationPitch = random.nextFloat() * 360F;
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    @Override
    public void tick() {
        super.tick();

        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;

        if (!this.onGround) {
            this.rotationYaw += this.momentumYaw;
            this.rotationPitch += this.momentumPitch;

            // Создаем частицы пыли при ударе
            if (random.nextInt(3) == 0) {
                BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.REDSTONE_BLOCK.defaultBlockState());
                this.level.addParticle(option,
                        this.x, this.y, this.z,
                        0, 0, 0);
            }
        }
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float) (Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float) (Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float) (Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        // Интерполируем вращение
        float yaw = Mth.lerp(partialTicks, this.prevRotationYaw, this.rotationYaw);
        float pitch = Mth.lerp(partialTicks, this.prevRotationPitch, this.rotationPitch);

        Quaternionf rotation = new Quaternionf().rotateY((float) Math.toRadians(-yaw))
                .rotateX((float) Math.toRadians(pitch));

        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-1.0F, -1.0F, 0.0F);
        vertices[1] = new Vector3f(-1.0F, 1.0F, 0.0F);
        vertices[2] = new Vector3f(1.0F, 1.0F, 0.0F);
        vertices[3] = new Vector3f(1.0F, -1.0F, 0.0F);

        float size = this.particleSize; // используем свое поле

        for (int i = 0; i < 4; ++i) {
            Vector3f vertex = vertices[i];
            vertex.rotate(rotation);
            vertex.mul(size);
            vertex.add(x, y, z);
        }

        int light = this.getLightColor(partialTicks);

        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(0, 1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(0, 0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(1, 0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(this.rCol, this.gCol, this.bCol, this.alpha)
                .uv(1, 1)
                .uv2(light)
                .endVertex();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return RENDER_TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {
        public Factory() {
        }

        @Override
        public Particle createParticle(@NotNull SimpleParticleType type, @NotNull ClientLevel world,
                                       double x, double y, double z,
                                       double motionX, double motionY, double motionZ) {
            return new ParticleGiblet(world, x, y, z, motionX, motionY, motionZ);
        }
    }
}