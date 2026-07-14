package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class TurretSentryItemRenderer extends BlockEntityWithoutLevelRenderer {

    public TurretSentryItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.1, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case GROUND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.2f, 0.2f, 0.2f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.2f, 0.2f, 0.2f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        int light = (context == ItemDisplayContext.GROUND) ? 0xF000F0 : packedLight;
        poseStack.mulPose(Axis.YP.rotationDegrees(90));

        ResourceLocation texture = HBMResourceManager.turret_sentry_tex;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "Base", light, packedOverlay);
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "Pivot", light, packedOverlay);
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "Body", light, packedOverlay);
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "Drum", light, packedOverlay);
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "BarrelL", light, packedOverlay);
        HBMResourceManager.turret_sentry.renderPart(poseStack, consumer, "BarrelR", light, packedOverlay);

        poseStack.popPose();
    }
}