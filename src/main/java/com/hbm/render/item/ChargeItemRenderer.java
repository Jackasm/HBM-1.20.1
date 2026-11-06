package com.hbm.render.item;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class ChargeItemRenderer extends BlockEntityWithoutLevelRenderer {

    public ChargeItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();
        applyTransformations(context, poseStack);

        HFRWavefrontObject model = getModel(stack);
        ResourceLocation texture = getTexture(stack);
        model.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private static void applyTransformations(ItemDisplayContext context, PoseStack poseStack) {
        switch (context) {
            case GUI -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(-135));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
            case FIXED -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
            case THIRD_PERSON_RIGHT_HAND, THIRD_PERSON_LEFT_HAND -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.4f, 0.4f, 0.4f);
            }
            case FIRST_PERSON_RIGHT_HAND, FIRST_PERSON_LEFT_HAND -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.35f, 0.35f, 0.35f);
                poseStack.mulPose(Axis.YP.rotationDegrees(180));
            }
            default -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
        }
    }

    private static HFRWavefrontObject getModel(ItemStack stack) {
        Block block = ((net.minecraft.world.item.BlockItem) stack.getItem()).getBlock();
        if (block == ModBlocks.CHARGE_DYNAMITE.get() || block == ModBlocks.CHARGE_MINER.get()) {
            return HBMResourceManager.charge_dynamite;
        }
        return HBMResourceManager.charge_c4;
    }

    private static ResourceLocation getTexture(ItemStack stack) {
        Block block = ((net.minecraft.world.item.BlockItem) stack.getItem()).getBlock();
        if (block == ModBlocks.CHARGE_DYNAMITE.get()) return HBMResourceManager.charge_dynamite_tex;
        if (block == ModBlocks.CHARGE_MINER.get())    return HBMResourceManager.charge_miner_tex;
        if (block == ModBlocks.CHARGE_C4.get())       return HBMResourceManager.charge_c4_tex;
        if (block == ModBlocks.CHARGE_SEMTEX.get())   return HBMResourceManager.charge_semtex_tex;
        return HBMResourceManager.charge_dynamite_tex;
    }
}