package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class NukeCustomItemRenderer  extends BlockEntityWithoutLevelRenderer {

    public NukeCustomItemRenderer()
    {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        applyTransformations(context, poseStack);
        renderItemModel(poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void applyTransformations(ItemDisplayContext context, PoseStack poseStack) {

        switch (context) {
            case GUI:
                poseStack.translate(0.7, 0.45, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(-135));
                break;
            case GROUND:
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.8, 0.5, 0.5);
                poseStack.scale(0.25f, 0.25f, 0.25f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
                break;
            default:
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.75f, 0.75f, 0.75f);
        }
    }

    private void renderItemModel(PoseStack poseStack, MultiBufferSource buffer,
                                 int packedLight, int packedOverlay) {
        ResourceLocation texture = HBMResourceManager.nuke_custom_tex;
        HBMResourceManager.nuke_custom.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
    }
}
