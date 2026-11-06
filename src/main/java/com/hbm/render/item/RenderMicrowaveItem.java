package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderMicrowaveItem extends BlockEntityWithoutLevelRenderer {

    public RenderMicrowaveItem() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int combinedLight, int combinedOverlay) {

        poseStack.pushPose();

        switch (context) {
            case GUI -> {
                poseStack.translate(0.5, -0.2, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0.05, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIXED -> {
                poseStack.translate(0.5, -0.1, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.6, 0.3, 0.6);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(145));
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.9, -0.1, 0.3);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.YP.rotationDegrees(145));
            }
            default -> {
                poseStack.translate(0.5, -0.2, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
        }

        HBMResourceManager.microwave.renderPart(poseStack, buffer, "mainbody_Cube.001",
                HBMResourceManager.microwave_tex, combinedLight, combinedOverlay);
        HBMResourceManager.microwave.renderPart(poseStack, buffer, "window_Cube.002",
                HBMResourceManager.microwave_tex, combinedLight, combinedOverlay);
        HBMResourceManager.microwave.renderPart(poseStack, buffer, "plate_Cylinder",
                HBMResourceManager.microwave_tex, combinedLight, combinedOverlay);

        poseStack.popPose();
    }
}