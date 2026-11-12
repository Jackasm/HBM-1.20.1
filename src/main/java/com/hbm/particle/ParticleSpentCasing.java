package com.hbm.particle;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;
import java.util.Random;


@OnlyIn(Dist.CLIENT)
public class ParticleSpentCasing extends Particle {

    public static final Random rand = new Random();
    private static final float D_SCALE = 0.05F;
    // The OBJ casing owns no smoke vertices or smoke buffer. Its smoke
    // counterpart is a separate ParticleCasingSmokeTrail in another batch.

    private final SpentCasing config;
    private float momentumPitch, momentumYaw;

    // Вращение частицы
    public float rotationPitch;
    public float rotationYaw;
    public float prevRotationPitch;
    public float prevRotationYaw;

    // Кастомный RenderType для гильз
    public static final ParticleRenderType PARTICLE_RENDER_TYPE = new ParticleRenderType() {
        @Override
        public void begin(BufferBuilder builder, @NotNull TextureManager textureManager) {
            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getRendertypeEntityCutoutNoCullShader);
            RenderSystem.setShaderTexture(0, HBMResourceManager.casings_tex);
            builder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.NEW_ENTITY);
        }

        @Override
        public void end(Tesselator tesselator) {
            tesselator.end();
            RenderSystem.disableBlend();
        }

        @Override
        public String toString() {
            return "HBM:SPENT_CASING";
        }
    };

    public ParticleSpentCasing(ClientLevel world, double x, double y, double z,
                               double mx, double my, double mz, float momentumPitch, float momentumYaw,
                               SpentCasing config) {
        super(world, x, y, z);
        // Unlike the six-argument Particle constructor, the original
        // spent casing does not randomize the supplied ejection velocity.
        this.xd = mx;
        this.yd = my;
        this.zd = mz;
        this.momentumPitch = momentumPitch;
        this.momentumYaw = momentumYaw;
        this.config = config;

        this.lifetime = config.getMaxAge();

        this.gravity = 1.0F;
        this.alpha = 1.0F;

        this.hasPhysics = true;
        this.setSize(D_SCALE * Math.max(config.getScaleX(), config.getScaleZ()) * 2,
                D_SCALE * config.getScaleY());
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

        this.yd -= 0.04D * this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98D;
        this.yd *= 0.98D;
        this.zd *= 0.98D;

        if (this.onGround) {
            this.xd *= 0.7D;
            this.zd *= 0.7D;
            this.rotationPitch = (float) (Math.floor(this.rotationPitch / 180F + 0.5F)) * 180F;
            this.momentumYaw *= 0.7F;
            this.onGround = false;
        }

        prevRotationPitch = rotationPitch;
        prevRotationYaw = rotationYaw;

        rotationPitch += momentumPitch;
        rotationYaw += momentumYaw;

    }

    public void addPosition(double x, double y, double z) {
        this.setPos(this.x + x, this.y + y, this.z + z);
    }

    /** World-space anchor for the separately rendered smoke trail. */
    public Vec3 smokeWorldPosition() {
        return new Vec3(this.x, this.y, this.z);
    }

    /** Suppress new smoke after the casing disappears; old nodes fade out. */
    public boolean isSmokeSourceAlive() {
        return this.isAlive();
    }

    @Override
    public void move(double motionX, double motionY, double motionZ) {
        // Particle.move records stoppedByCollision after its first downward
        // contact. That is correct for ordinary dust but stops a bouncing
        // spent casing from moving again. Instead, use the SAME vanilla
        // Entity collision clipping helper with the original bounce ratios.
        Vec3 intended = new Vec3(motionX, motionY, motionZ);
        Vec3 resolved = this.hasPhysics && intended.lengthSqr() < 10000D
                ? net.minecraft.world.entity.Entity.collideBoundingBox(
                        null, intended, this.getBoundingBox(), this.level, List.of())
                : intended;

        this.setBoundingBox(this.getBoundingBox().move(resolved));
        this.setLocationFromBoundingbox();

        boolean hitX = Math.abs(resolved.x - motionX) > 1.0E-7D;
        boolean hitY = Math.abs(resolved.y - motionY) > 1.0E-7D;
        boolean hitZ = Math.abs(resolved.z - motionZ) > 1.0E-7D;
        this.onGround = hitY && motionY < 0.0D;

        if (hitX) {
            this.xd *= -0.25D;
            momentumYaw = Math.abs(momentumYaw) > 1.0E-7F
                    ? momentumYaw * -0.75F
                    : (float) rand.nextGaussian() * 10F * config.getBounceYaw();
        }
        if (hitY) {
            this.yd *= -0.5D;
            boolean rotFromSpeed = Math.abs(this.yd) > 0.04D;
            if (rotFromSpeed || Math.abs(momentumPitch) > 1.0E-7F) {
                momentumPitch *= -0.75F;
                if (rotFromSpeed) {
                    float mult = (float) com.hbm.util.BobMathUtil.safeClamp(motionY / 0.2F, -1F, 1F);
                    momentumPitch += (float) rand.nextGaussian() * 10F * config.getBouncePitch() * mult;
                    momentumYaw += (float) rand.nextGaussian() * 10F * config.getBounceYaw() * mult;
                }
            }
        }
        if (hitZ) {
            this.zd *= -0.25D;
            momentumYaw = Math.abs(momentumYaw) > 1.0E-7F
                    ? momentumYaw * -0.75F
                    : (float) rand.nextGaussian() * 10F * config.getBounceYaw();
        }
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        // The particle engine owns this entire casing batch; do not change
        // RenderSystem state or submit smoke from an individual casing.
        double pX = Mth.lerp(partialTicks, this.xo, this.x);
        double pY = Mth.lerp(partialTicks, this.yo, this.y);
        double pZ = Mth.lerp(partialTicks, this.zo, this.z);

        float yaw = Mth.lerp(partialTicks, prevRotationYaw, rotationYaw);
        float pitch = Mth.lerp(partialTicks, prevRotationPitch, rotationPitch);
        renderCasing(buffer, camera, pX, pY, pZ, yaw, pitch, partialTicks);
    }

    private void renderCasing(VertexConsumer buffer, Camera camera, double pX, double pY, double pZ,
                              float yaw, float pitch, float partialTicks) {
        PoseStack poseStack = new PoseStack();
        Vec3 cameraPos = camera.getPosition();

        poseStack.translate(pX - cameraPos.x, pY - cameraPos.y, pZ - cameraPos.z);
        poseStack.scale(D_SCALE, D_SCALE, D_SCALE);
        poseStack.mulPose(Axis.YP.rotationDegrees(180 - yaw));
        poseStack.mulPose(Axis.XP.rotationDegrees(-pitch));
        poseStack.scale(config.getScaleX(), config.getScaleY(), config.getScaleZ());

        int packedLight = getLightColor(partialTicks);

        int index = 0;
        for (String name : config.getType().partNames) {
            int col = this.config.getColors()[index];
            Color color = new Color(col);
            float r = color.getRed() / 255.0F;
            float g = color.getGreen() / 255.0F;
            float b = color.getBlue() / 255.0F;

            HBMResourceManager.casings.renderPartColored(
                    poseStack,
                    buffer,
                    name,
                    packedLight,
                    OverlayTexture.NO_OVERLAY,
                    r, g, b, this.alpha
            );
            index++;
        }
    }

    @Override
    protected int getLightColor(float partialTick) {
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        int blockLight = this.level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK, pos);
        int skyLight = this.level.getBrightness(net.minecraft.world.level.LightLayer.SKY, pos);
        return LightTexture.pack(blockLight, skyLight);
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return PARTICLE_RENDER_TYPE;
    }

}