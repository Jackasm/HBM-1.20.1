package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class BoxcarItemRenderer extends BlockEntityWithoutLevelRenderer {

    public BoxcarItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        if (HBMResourceManager.boxcar == null) {
            return;
        }

        poseStack.pushPose();

        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.1f, 0.1f, 0.1f);
                poseStack.mulPose(Axis.XP.rotationDegrees(25));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case GROUND:
                poseStack.translate(0.5, 0.2, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:

            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.1f, 0.1f, 0.1f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0.2, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        HBMResourceManager.boxcar.renderAll(
                poseStack, buffer, HBMResourceManager.boxcar_tex, packedLight, packedOverlay
        );

        poseStack.popPose();
    }
}