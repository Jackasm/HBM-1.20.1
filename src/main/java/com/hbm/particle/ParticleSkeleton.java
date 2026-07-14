package com.hbm.particle;

import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class ParticleSkeleton extends Particle {

    public static final ResourceLocation texture = ResLocation.ResLocation(RefStrings.MODID, "textures/particles/skeleton.png");
    public static final ResourceLocation texture_ext = ResLocation.ResLocation(RefStrings.MODID, "textures/particles/skoilet.png");
    public static final ResourceLocation texture_blood = ResLocation.ResLocation(RefStrings.MODID, "textures/particles/skeleton_blood.png");
    public static final ResourceLocation texture_blood_ext = ResLocation.ResLocation(RefStrings.MODID, "textures/particles/skoilet_blood.png");
    public static final HFRWavefrontObject skeleton = new HFRWavefrontObject(ResLocation.ResLocation(RefStrings.MODID, "models/effects/skeleton.obj"));

    protected EnumSkeletonType type;
    public ResourceLocation useTexture;
    public ResourceLocation useTextureExt;

    private final float momentumYaw;
    private final float momentumPitch;
    private int initialDelay;
    public float rotationYaw;
    public float rotationPitch;
    public float prevRotationYaw;
    public float prevRotationPitch;

    private final TextureManager textureManager;
    private float alpha = 1.0F;

    public ParticleSkeleton(TextureManager textureManager, ClientLevel world, double x, double y, double z,
                            float r, float g, float b, EnumSkeletonType type) {
        super(world, x, y, z);
        this.textureManager = textureManager;
        this.type = type;

        this.lifetime = 200 + random.nextInt(20);
        this.rCol = r;
        this.gCol = g;
        this.bCol = b;
        this.gravity = 0.02F;
        this.initialDelay = 20;
        this.hasPhysics = true;

        this.momentumPitch = random.nextFloat() * 5 * (random.nextBoolean() ? 1 : -1);
        this.momentumYaw = random.nextFloat() * 5 * (random.nextBoolean() ? 1 : -1);

        this.useTexture = texture;
        this.useTextureExt = texture_ext;
    }

    public ParticleSkeleton makeGib() {
        this.initialDelay = -2;
        this.useTexture = texture_blood;
        this.useTextureExt = texture_blood_ext;
        this.gravity = 0.04F;
        this.lifetime = 100 + random.nextInt(20);
        return this;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.prevRotationPitch = this.rotationPitch;
        this.prevRotationYaw = this.rotationYaw;

        if(initialDelay-- > 0) return;

        if(initialDelay == -1) {
            this.xd = random.nextGaussian() * 0.025;
            this.zd = random.nextGaussian() * 0.025;
        }

        if(this.age++ >= this.lifetime) {
            this.remove();
            return;
        }

        boolean wasOnGround = this.onGround;

        this.yd -= this.gravity;
        this.move(this.xd, this.yd, this.zd);
        this.xd *= 0.98D;
        this.yd *= 0.98D;
        this.zd *= 0.98D;

        if(!this.onGround) {
            this.rotationPitch += this.momentumPitch;
            this.rotationYaw += this.momentumYaw;
        } else {
            this.xd = 0;
            this.yd = 0;
            this.zd = 0;

            if(!wasOnGround) {
                // Play sound
                Player player = Minecraft.getInstance().player;
                if(player != null) {
                    player.playSound(net.minecraft.sounds.SoundEvents.SKELETON_HURT, 0.25F, 0.8F + random.nextFloat() * 0.4F);
                }
            }
        }

        float timeLeft = this.lifetime - this.age;
        this.alpha = timeLeft < 40 ? timeLeft / 40F : 1.0F;
    }

    @Override
    public void render(@NotNull VertexConsumer buffer, @NotNull Camera camera, float partialTicks) {
        // Вместо VertexConsumer используем прямой рендеринг OpenGL для OBJ моделей
        renderSkeletonModel(camera, partialTicks);
    }

    private void renderSkeletonModel(Camera camera, float partialTicks) {
        PoseStack poseStack = new PoseStack();
        poseStack.pushPose();

        Vec3 camPos = camera.getPosition();
        double pX = Mth.lerp(partialTicks, this.xo, this.x);
        double pY = Mth.lerp(partialTicks, this.yo, this.y);
        double pZ = Mth.lerp(partialTicks, this.zo, this.z);

        poseStack.translate(pX - camPos.x(), pY - camPos.y(), pZ - camPos.z());

        float yaw = Mth.lerp(partialTicks, this.prevRotationYaw, this.rotationYaw);
        float pitch = Mth.lerp(partialTicks, this.prevRotationPitch, this.rotationPitch);

        poseStack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(yaw)));
        poseStack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(pitch)));

        int light = this.getLightColor(partialTicks);
        int lX = light & 0xFFFF;
        int lY = (light >> 16) & 0xFFFF;
        RenderSystem.setShaderLights(new Vector3f(lX / 256.0F, lY / 256.0F, 0), new Vector3f(1, 1, 1));

        RenderSystem.enableCull();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(rCol, gCol, bCol, alpha);

        switch(type) {
            case SKULL:
                textureManager.bindForSetup(useTexture);
                skeleton.renderPart(poseStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(useTexture)), "Skull", light, 0);
                break;
            case TORSO:
                textureManager.bindForSetup(useTexture);
                skeleton.renderPart(poseStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(useTexture)), "Torso", light, 0);
                break;
            case LIMB:
                textureManager.bindForSetup(useTexture);
                skeleton.renderPart(poseStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(useTexture)), "Limb", light, 0);
                break;
            case SKULL_VILLAGER:
                textureManager.bindForSetup(useTextureExt);
                skeleton.renderPart(poseStack, Minecraft.getInstance().renderBuffers().bufferSource().getBuffer(RenderType.entityCutout(useTextureExt)), "SkullVillager", light, 0);
                break;
        }

        Minecraft.getInstance().renderBuffers().bufferSource().endBatch();

        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        poseStack.popPose();
    }

    @Override
    public @NotNull ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @Override
    protected int getLightColor(float partialTick) {
        BlockPos pos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.hasChunkAt(pos) ? LightTexture.pack(15, 15) : 0;
    }

    public void setDeltaMovement(double x, double y, double z) {
        this.xd = x;
        this.yd = y;
        this.zd = z;
    }

    public enum EnumSkeletonType {
        SKULL, TORSO, LIMB, SKULL_VILLAGER
    }
}