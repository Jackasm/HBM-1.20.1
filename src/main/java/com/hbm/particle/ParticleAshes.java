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
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ParticleAshes extends Particle {

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
            return RefStrings.MODID + ":ashes";
        }
    };

    private float rotationPitch;
    private float prevRotationPitch;
    private boolean onGround;
    private final int particleId;
    private float alpha = 1.0F;
    private final float size;

    public ParticleAshes(ClientLevel world, double x, double y, double z, float scale) {
        super(world, x, y, z);
        this.lifetime = 100 + random.nextInt(20);
        this.size = scale * 0.9F + random.nextFloat() * 0.2F;
        this.gravity = 0.01F;
        this.hasPhysics = true;
        this.friction = 0.95F;

        // Цвет
        float brightness = random.nextFloat() * 0.1F + 0.1F;
        this.rCol = brightness;
        this.gCol = brightness;
        this.bCol = brightness;

        // Движение
        this.xd = (random.nextDouble() - 0.5) * 0.1;
        this.yd = random.nextDouble() * 0.1;
        this.zd = (random.nextDouble() - 0.5) * 0.1;

        // ID для вариаций вращения
        this.particleId = (int)((x + y + z + System.nanoTime()) % 10000);
        this.rotationPitch = random.nextFloat() * 360F;
        this.prevRotationPitch = this.rotationPitch;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.prevRotationPitch = this.rotationPitch;

        this.age++;
        if (this.age >= this.lifetime) {
            this.remove();
            return;
        }

        this.yd -= this.gravity;

        if (!this.onGround) {
            this.rotationPitch += (float) (2 * ((this.particleId % 2) - 0.5));
        }

        this.xd *= this.friction;
        this.yd *= 0.99D;
        this.zd *= this.friction;

        boolean wasOnGround = this.onGround;
        this.move(this.xd, this.yd, this.zd);

        // Проверка на землю
        if (this.y == this.yo && this.yd == 0) {
            this.onGround = true;
        }

        if (!wasOnGround && this.onGround) {
            this.rotationPitch = random.nextFloat() * 360F;
        }

        // Дым
        if (this.particleId % 5 == 0 && this.onGround && random.nextInt(15) == 0) {
            level.addParticle(ParticleTypes.SMOKE,
                    this.x, this.y + 0.125, this.z,
                    0, 0.05, 0);
        }

        // Альфа-канал (затухание)
        float timeLeft = this.lifetime - this.age;
        this.alpha = timeLeft < 40 ? timeLeft / 40F : 1.0F;
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        int light = this.getLightColor(partialTicks);
        float rot = Mth.lerp(partialTicks, this.prevRotationPitch, this.rotationPitch);

        if (this.onGround) {
            // Рендер на земле (плоская текстура)
            renderOnGround(buffer, x, y, z, rot, light);
        } else {
            // Рендер в воздухе (билборд)
            renderInAir(buffer, x, y, z, light);
        }
    }

    private void renderOnGround(VertexConsumer buffer, float x, float y, float z, float rot, int light) {
        float size = this.size * 0.5F;

        // UV координаты (весь спрайт)
        float u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;

        Quaternionf rotation = new Quaternionf().rotateY(rot * ((float)Math.PI / 180F));

        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-size, 0.0F, -size);
        vertices[1] = new Vector3f(-size, 0.0F, size);
        vertices[2] = new Vector3f(size, 0.0F, size);
        vertices[3] = new Vector3f(size, 0.0F, -size);

        for (int i = 0; i < 4; i++) {
            vertices[i].rotate(rotation);
            vertices[i].add(x, y + 0.05F, z);
        }

        // Два треугольника для квадрата
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u0, v1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u0, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u1, v0)
                .uv2(light)
                .endVertex();

        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u0, v1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u1, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u1, v1)
                .uv2(light)
                .endVertex();
    }

    private void renderInAir(VertexConsumer buffer, float x, float y, float z, int light) {
        float size = this.size * 0.5F;

        // UV координаты
        float u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;

        Quaternionf rotation = new Quaternionf().rotateX((float)Math.PI / 4);

        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-size, -size, 0.0F);
        vertices[1] = new Vector3f(-size, size, 0.0F);
        vertices[2] = new Vector3f(size, size, 0.0F);
        vertices[3] = new Vector3f(size, -size, 0.0F);

        for (int i = 0; i < 4; i++) {
            vertices[i].rotate(rotation);
            vertices[i].add(x, y, z);
        }

        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u0, v1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u0, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u1, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(rCol, gCol, bCol, alpha)
                .uv(u1, v1)
                .uv2(light)
                .endVertex();
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
            return new ParticleAshes(world, x, y, z, (float)zSpeed);
        }
    }
}