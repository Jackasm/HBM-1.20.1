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

public class NukeN2ItemRenderer extends BlockEntityWithoutLevelRenderer {

    public NukeN2ItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        if (HBMResourceManager.n2 == null) {
            return;
        }

        poseStack.pushPose();

        switch (context) {
            case GUI:
                poseStack.translate(0.2, 0.2, 0.5);
                poseStack.scale(0.13f, 0.13f, 0.13f);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.mulPose(Axis.XP.rotationDegrees(-45));

                break;
            case GROUND:
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(1.0f, 1.0f, 1.0f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(1.0f, 1.0f, 1.0f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.13f, 0.13f, 0.13f);
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.13f, 0.13f, 0.13f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
        }

        HBMResourceManager.n2.renderAll(
                poseStack, buffer, HBMResourceManager.n2_tex, packedLight, packedOverlay
        );

        poseStack.popPose();
    }
}