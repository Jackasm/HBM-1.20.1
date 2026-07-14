package com.hbm.render.item;

import com.hbm.blocks.machine.MachineCapacitor;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CapacitorItemRenderer extends BlockEntityWithoutLevelRenderer {

    public CapacitorItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        applyTransformations(context, poseStack);

        Item item = stack.getItem();
        if (item instanceof BlockItem blockItem && blockItem.getBlock() instanceof MachineCapacitor capacitor) {
            ResourceLocation[] textures = capacitor.getTextures();

            // Top
            HBMResourceManager.capacitor.renderPart(poseStack, buffer, "Top", textures[0], packedLight, packedOverlay);
            // Side
            HBMResourceManager.capacitor.renderPart(poseStack, buffer, "Side", textures[1], packedLight, packedOverlay);
            // Bottom
            HBMResourceManager.capacitor.renderPart(poseStack, buffer, "Bottom", textures[2], packedLight, packedOverlay);
            // InnerTop
            HBMResourceManager.capacitor.renderPart(poseStack, buffer, "InnerTop", textures[3], packedLight, packedOverlay);
            // InnerSide
            HBMResourceManager.capacitor.renderPart(poseStack, buffer, "InnerSide", textures[4], packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    private void applyTransformations(ItemDisplayContext context, PoseStack poseStack) {
        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
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
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.3f, 0.3f, 0.3f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.75f, 0.75f, 0.75f);
        }
    }
}