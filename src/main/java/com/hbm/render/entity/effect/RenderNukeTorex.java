package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.effect.EntityNukeTorex.Cloudlet;
import com.hbm.entity.effect.TorexVisualMath;

import com.hbm.handler.ModEventHandlerClient;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderNukeTorex extends EntityRenderer<EntityNukeTorex> {

    private static final ResourceLocation CLOUDLET_TEX = ResLocation(RefStrings.MODID, "textures/particles/particle_base.png");
    private static final ResourceLocation FLASH_TEX = ResLocation(RefStrings.MODID, "textures/particles/flare.png");

    private final Comparator<Cloudlet> cloudSorter;

    public RenderNukeTorex(EntityRendererProvider.Context context) {
        super(context);
        // Сортируем облачка по удаленности от игрока (дальние сначала для корректного прозрачного рендеринга)
        cloudSorter = (c1, c2) -> {
            Player player = Minecraft.getInstance().player;
            if (player == null) return 0;
            double dist1 = player.distanceToSqr(c1.posX, c1.posY, c1.posZ);
            double dist2 = player.distanceToSqr(c2.posX, c2.posY, c2.posZ);
            return Double.compare(dist2, dist1); // дальше -> раньше
        };
    }

    @Override
    public boolean shouldRender(@NotNull EntityNukeTorex entity, @NotNull Frustum frustum, double x, double y, double z) {
        // Всегда рендерим, так как эффект может быть виден издалека
        return true;
    }

    @Override
    public void render(@NotNull EntityNukeTorex entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        // A newly tracked entity can arrive before its synchronized start time.
        // Never display an erroneous initial flash while waiting for that snapshot.
        if (entity.getStartGameTime() < 0L) return;
        poseStack.pushPose();
        // Перемещаемся в позицию сущности (рендеринг в локальных координатах)
        poseStack.translate(0, 0, 0);

        Camera camera = this.entityRenderDispatcher.camera;
        boolean fogEnabled = false; // в 1.20.1 нет прямого доступа, но можно выключить через ShaderInstance, обычно не нужно

        renderCloudlets(entity, partialTicks, poseStack, buffer, camera);
        if (entity.tickCount < 101) {
            renderFlash(entity, partialTicks, poseStack, buffer, camera);
        }

        // Эффект вспышки и тряски камеры (логика осталась как в оригинале)
        // Late-tracking clients may see the remaining age-correct flare, but
        // must not publish a *new* initial flash event on reconnect.
        if (!entity.isLateTrackingClient() && entity.tickCount < 10
                && System.currentTimeMillis() - ModEventHandlerClient.flashTimestamp > 1000) {
            ModEventHandlerClient.flashTimestamp = System.currentTimeMillis();
        }
        if (entity.didPlaySound && !entity.didShake && System.currentTimeMillis() - ModEventHandlerClient.shakeTimestamp > 1000) {
            ModEventHandlerClient.shakeTimestamp = System.currentTimeMillis();
            entity.didShake = true;
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                // Имитируем тряску камеры: можно добавить эффект через Camera или просто hurtTime
                player.hurtTime = 15;
                player.hurtDuration = 15;
            }
        }

        poseStack.popPose();
    }

    private void renderCloudlets(EntityNukeTorex cloud, float partialTicks, PoseStack poseStack,
                                 MultiBufferSource buffer, Camera camera) {
        // Сортируем облачка по удаленности
        ArrayList<Cloudlet> cloudlets = new ArrayList<>(cloud.cloudlets);
        cloudlets.sort(cloudSorter);

        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucent(CLOUDLET_TEX));
        for (Cloudlet cloudlet : cloudlets) {
            Vec3 interpPos = cloudlet.getInterpPos(partialTicks);
            double x = interpPos.x - cloud.getX();
            double y = interpPos.y - cloud.getY();
            double z = interpPos.z - cloud.getZ();
            tessellateCloudlet(builder, poseStack, x, y, z, cloudlet, camera, partialTicks);
        }
    }

    // Use the cloudlets' matching NEW_ENTITY vertex layout and no-cull shader.
    // Earlier flash rendering used an incompatible attribute layout and could
    // cull the camera-facing quad. Keep regular depth and alpha-additive blending;
    // onset relative to sound still requires actual client verification.
    private static final RenderType ADDITIVE_FLASH = RenderType.create(
            "hbm_flash_additive",
            DefaultVertexFormat.NEW_ENTITY,
            VertexFormat.Mode.QUADS,
            256,
            false,
            true,
            RenderType.CompositeState.builder()
                    .setTextureState(new RenderStateShard.TextureStateShard(FLASH_TEX, false, false))
                    .setTransparencyState(new RenderStateShard.TransparencyStateShard(
                            "additive",
                            () -> {
                                RenderSystem.enableBlend();
                                // Legacy RenderTorex uses SRC_ALPHA + ONE: alpha must control flare intensity.
                                RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);
                            },
                            () -> {
                                RenderSystem.disableBlend();
                                RenderSystem.defaultBlendFunc();
                            }
                    ))
                    // Vertex writes contain overlay + normals. Match the same
                    // NEW_ENTITY shader/layout as the working cloudlet render.
                    .setLightmapState(new RenderStateShard.LightmapStateShard(true))
                    .setOverlayState(new RenderStateShard.OverlayStateShard(true))
                    .setCullState(new RenderStateShard.CullStateShard(false))
                    .setShaderState(new RenderStateShard.ShaderStateShard(GameRenderer::getRendertypeEntityTranslucentShader))
                    .setDepthTestState(new RenderStateShard.DepthTestStateShard("<=", 515))
                    .setWriteMaskState(new RenderStateShard.WriteMaskStateShard(true, false))
                    .createCompositeState(false)
    );

    private void renderFlash(EntityNukeTorex cloud, float partialTicks, PoseStack poseStack,
                             MultiBufferSource buffer, Camera camera) {
        float alpha = com.hbm.entity.effect.TorexVisualMath.flashAlpha(cloud.tickCount + partialTicks);
        if (alpha <= 0F) return;

        VertexConsumer builder = buffer.getBuffer(ADDITIVE_FLASH);
        Random rand = new Random(cloud.getId());
        for (int i = 0; i < 3; i++) {
            float x = (float) (rand.nextGaussian() * 0.5 * cloud.rollerSize);
            float y = (float) ((rand.nextGaussian() * 0.5 * cloud.rollerSize) + cloud.coreHeight);
            float z = (float) (rand.nextGaussian() * 0.5 * cloud.rollerSize);
            tessellateFlash(builder, poseStack, x, y, z, (float) (25 * cloud.rollerSize), alpha, camera);
        }
    }


    private void tessellateCloudlet(VertexConsumer builder, PoseStack poseStack,
                                    double x, double y, double z, Cloudlet cloudlet, Camera camera, float partialTicks) {
        float alpha = cloudlet.getAlpha();
        float scale = cloudlet.getScale();

        poseStack.pushPose();
        poseStack.translate(x, y, z);

        float brightness = cloudlet.type == EntityNukeTorex.TorexType.CONDENSATION ? 0.9F : 0.75F * cloudlet.colorMod;
        // Legacy order: HDR interpolate -> cloudlet brightness -> packed RGB
        // clamp. Never clip RGB before multiplication by brightness.
        Vec3 color = TorexVisualMath.vertexColor(cloudlet.getInterpColor(partialTicks), brightness);
        float r = (float) color.x;
        float g = (float) color.y;
        float b = (float) color.z;

        var pose = poseStack.last().pose();
        var normalMat = poseStack.last().normal();
        int light = 15728880;
        int overlay = OverlayTexture.NO_OVERLAY;

        // Получаем направления камеры из org.joml.Vector3f и конвертируем в Vec3
        org.joml.Vector3f rightJoml = camera.getLeftVector(); // это левый вектор, нам нужен правый
        org.joml.Vector3f upJoml = camera.getUpVector();

        // Создаем Vec3 из JOML векторов (правый вектор = -левый)
        Vec3 right = new Vec3(-rightJoml.x(), -rightJoml.y(), -rightJoml.z());
        Vec3 up = new Vec3(upJoml.x(), upJoml.y(), upJoml.z());

        // Вычисляем 4 угла квада в локальном пространстве, ориентированные на камеру
        Vec3 v1 = right.scale(-scale).add(up.scale(-scale));
        Vec3 v2 = right.scale(-scale).add(up.scale(scale));
        Vec3 v3 = right.scale(scale).add(up.scale(scale));
        Vec3 v4 = right.scale(scale).add(up.scale(-scale));

        builder.vertex(pose, (float)v1.x, (float)v1.y, (float)v1.z)
                .color(r, g, b, alpha)
                .uv(1, 1)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, 0, 0, 1)
                .endVertex();

        builder.vertex(pose, (float)v2.x, (float)v2.y, (float)v2.z)
                .color(r, g, b, alpha)
                .uv(1, 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, 0, 0, 1)
                .endVertex();

        builder.vertex(pose, (float)v3.x, (float)v3.y, (float)v3.z)
                .color(r, g, b, alpha)
                .uv(0, 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, 0, 0, 1)
                .endVertex();

        builder.vertex(pose, (float)v4.x, (float)v4.y, (float)v4.z)
                .color(r, g, b, alpha)
                .uv(0, 1)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, 0, 0, 1)
                .endVertex();

        poseStack.popPose();
    }

    private void tessellateFlash(VertexConsumer builder, PoseStack poseStack,
                                 double x, double y, double z, float scale, float alpha, Camera camera) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);

        var pose = poseStack.last().pose();
        var normalMat = poseStack.last().normal();
        int light = 15728880;
        int overlay = OverlayTexture.NO_OVERLAY;

        // Получаем направления камеры
        org.joml.Vector3f rightJoml = camera.getLeftVector();
        org.joml.Vector3f upJoml = camera.getUpVector();

        // Конвертируем в Vec3 (правый = -левый)
        Vec3 right = new Vec3(-rightJoml.x(), -rightJoml.y(), -rightJoml.z());
        Vec3 up = new Vec3(upJoml.x(), upJoml.y(), upJoml.z());

        Vec3 v1 = right.scale(-scale).add(up.scale(-scale));
        Vec3 v2 = right.scale(-scale).add(up.scale(scale));
        Vec3 v3 = right.scale(scale).add(up.scale(scale));
        Vec3 v4 = right.scale(scale).add(up.scale(-scale));

        builder.vertex(pose, (float)v1.x, (float)v1.y, (float)v1.z)
                .color(1, 1, 1, alpha)
                .uv(1, 1)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, 0, 0, 1)
                .endVertex();

        builder.vertex(pose, (float)v2.x, (float)v2.y, (float)v2.z)
                .color(1, 1, 1, alpha)
                .uv(1, 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, 0, 0, 1)
                .endVertex();

        builder.vertex(pose, (float)v3.x, (float)v3.y, (float)v3.z)
                .color(1, 1, 1, alpha)
                .uv(0, 0)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, 0, 0, 1)
                .endVertex();

        builder.vertex(pose, (float)v4.x, (float)v4.y, (float)v4.z)
                .color(1, 1, 1, alpha)
                .uv(0, 1)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normalMat, 0, 0, 1)
                .endVertex();

        poseStack.popPose();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityNukeTorex entity) {
        return CLOUDLET_TEX; // не используется, но нужно вернуть что-то
    }
}