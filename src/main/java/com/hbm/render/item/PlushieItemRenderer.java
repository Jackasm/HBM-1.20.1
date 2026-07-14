package com.hbm.render.item;

import com.hbm.blocks.generic.BlockPlushie.PlushieType;
import com.hbm.items.block.ItemPlushie;
import com.hbm.render.tileentity.RenderPlushie;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

public class PlushieItemRenderer extends BlockEntityWithoutLevelRenderer {

    public PlushieItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, ItemDisplayContext context,
                             PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        PlushieType type = ItemPlushie.getType(stack);

        // Разные позиции для разных типов
        switch (context) {
            case GUI:
                poseStack.translate(0.5, 0.2, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
                poseStack.mulPose(Axis.YN.rotationDegrees(45));
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

        RenderPlushie.renderPlushie(type, poseStack, buffer, packedLight, packedOverlay);

        poseStack.popPose();
    }
}