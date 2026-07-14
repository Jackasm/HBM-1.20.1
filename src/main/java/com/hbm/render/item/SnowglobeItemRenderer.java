package com.hbm.render.item;

import com.hbm.blocks.generic.BlockSnowglobe.SnowglobeType;
import com.hbm.render.tileentity.RenderSnowglobe;
import com.hbm.util.EnumUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SnowglobeItemRenderer extends BlockEntityWithoutLevelRenderer {

    public SnowglobeItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, ItemDisplayContext context,
                             PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        // Масштабирование в зависимости от контекста
        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(15));
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45));
                break;
            case GROUND:
                poseStack.translate(0.5, 0.1, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
                break;
            default:
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
        }

        SnowglobeType type = getTypeFromStack(stack);
        RenderSnowglobe.renderSnowglobe(type, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private static SnowglobeType getTypeFromStack(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int customModelData = 0;
        if (tag != null && tag.contains("CustomModelData")) {
            customModelData = tag.getInt("CustomModelData");
        }
        return EnumUtil.grabEnumSafely(SnowglobeType.class, customModelData);
    }
}