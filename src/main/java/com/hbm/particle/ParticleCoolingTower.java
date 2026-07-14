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
import net.minecraft.core.BlockPos;
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
public class ParticleCoolingTower extends Particle {

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
            return RefStrings.MODID + ":cooling_tower";
        }
    };

    // Параметры частицы
    private float baseScale = 1.0F;
    private float maxScale = 1.0F;
    private float lift = 0.3F;
    private float strafe = 0.075F;
    private boolean windDir = true;
    private float alphaMod = 0.25F;
    private float quadSize;

    // Текущий цвет
    private float red;
    private float green;
    private float blue;

    public ParticleCoolingTower(ClientLevel level, double x, double y, double z) {
        super(level, x, y, z);

        this.red = 0.9F + level.random.nextFloat() * 0.05F;
        this.green = 0.9F + level.random.nextFloat() * 0.05F;
        this.blue = 0.9F + level.random.nextFloat() * 0.05F;
        this.alpha = 0.25F;
        this.hasPhysics = false;
        this.quadSize = baseScale;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
    }

    public void setBaseScale(float f) {
        this.baseScale = f;
        this.quadSize = f;
    }

    public void setMaxScale(float f) { this.maxScale = f; }
    public void setLift(float f) { this.lift = f; }
    public void setLife(int i) { this.lifetime = i; }
    public void setStrafe(float f) { this.strafe = f; }
    public void noWind() { this.windDir = false; }
    public void alphaMod(float mod) { this.alphaMod = mod; }

    public void setColor(float r, float g, float b) {
        this.red = r;
        this.green = g;
        this.blue = b;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;

        float ageScale = (float) this.age / (float) this.lifetime;

        // Альфа уменьшается со временем
        this.alpha = alphaMod - ageScale * alphaMod;

        // Масштаб меняется по квадратичному закону
        this.quadSize = baseScale + (float) Math.pow((maxScale * ageScale - baseScale), 2);

        this.age++;

        // Вертикальное движение
        if (lift > 0 && this.yd < this.lift) {
            this.yd += 0.01F;
        }
        if (lift < 0 && this.yd > this.lift) {
            this.yd -= 0.01F;
        }

        // Горизонтальный разброс
        this.xd += random.nextGaussian() * strafe * ageScale;
        this.zd += random.nextGaussian() * strafe * ageScale;

        // Направление ветра
        if (windDir) {
            this.xd += 0.02 * ageScale;
            this.zd -= 0.01 * ageScale;
        }

        if (this.age == this.lifetime) {
            this.remove();
        }

        this.move(this.xd, this.yd, this.zd);

        // Затухание скорости
        this.xd *= 0.925;
        this.yd *= 0.925;
        this.zd *= 0.925;
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        Vec3 camPos = camera.getPosition();
        float x = (float)(Mth.lerp(partialTicks, this.xo, this.x) - camPos.x());
        float y = (float)(Mth.lerp(partialTicks, this.yo, this.y) - camPos.y());
        float z = (float)(Mth.lerp(partialTicks, this.zo, this.z) - camPos.z());

        int light = this.getLightColor(partialTicks);
        float scale = this.quadSize;

        // Всегда рендерим как билборд (повёрнут к камере)
        renderBillboard(buffer, x, y, z, scale, light);
    }

    private void renderBillboard(VertexConsumer buffer, float x, float y, float z, float scale, int light) {
        float halfSize = scale * 0.5F;

        // UV координаты (весь спрайт)
        float u0 = 0;
        float u1 = 1;
        float v0 = 0;
        float v1 = 1;

        // Кватернион для поворота к камере
        Quaternionf rotation = new Quaternionf().rotationYXZ(
                -Minecraft.getInstance().gameRenderer.getMainCamera().getYRot() * ((float)Math.PI / 180F),
                Minecraft.getInstance().gameRenderer.getMainCamera().getXRot() * ((float)Math.PI / 180F),
                0.0F
        );

        Vector3f[] vertices = new Vector3f[4];
        vertices[0] = new Vector3f(-halfSize, -halfSize, 0.0F);
        vertices[1] = new Vector3f(-halfSize, halfSize, 0.0F);
        vertices[2] = new Vector3f(halfSize, halfSize, 0.0F);
        vertices[3] = new Vector3f(halfSize, -halfSize, 0.0F);

        for (int i = 0; i < 4; i++) {
            vertices[i].rotate(rotation);
            vertices[i].add(x, y, z);
        }

        // Рендерим квад
        buffer.vertex(vertices[0].x(), vertices[0].y(), vertices[0].z())
                .color(red, green, blue, alpha)
                .uv(u0, v1)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[1].x(), vertices[1].y(), vertices[1].z())
                .color(red, green, blue, alpha)
                .uv(u0, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[2].x(), vertices[2].y(), vertices[2].z())
                .color(red, green, blue, alpha)
                .uv(u1, v0)
                .uv2(light)
                .endVertex();
        buffer.vertex(vertices[3].x(), vertices[3].y(), vertices[3].z())
                .color(red, green, blue, alpha)
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
            return new ParticleCoolingTower(world, x, y, z);
        }
    }
}