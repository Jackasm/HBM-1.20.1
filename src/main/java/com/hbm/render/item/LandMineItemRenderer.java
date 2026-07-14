package com.hbm.render.item;

import com.hbm.blocks.ModBlocks;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class LandMineItemRenderer extends BlockEntityWithoutLevelRenderer {
    public LandMineItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();

        // Общие настройки для разных контекстов
        switch (context) {
            case GUI:
                poseStack.translate(0.55, 0.1, 0.5);
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
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.35f, 0.35f, 0.35f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.75f, 0.75f, 0.75f);
        }

        Item item = stack.getItem();
        ResourceLocation texture;
        if (item == ModBlocks.MINE_AP.get().asItem()) {
            poseStack.translate(-0.2, 0.5, 0);
            poseStack.scale(0.7f, 0.7f, 0.7f);
            poseStack.mulPose(Axis.XP.rotationDegrees(45));
            texture = HBMResourceManager.mine_ap_grass_tex;
            HBMResourceManager.mine_ap.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.MINE_HE.get().asItem()) {
            poseStack.translate(-0.2, 0.5, 0);
            poseStack.scale(2f, 2f, 2f);
            poseStack.mulPose(Axis.XP.rotationDegrees(45));
            texture = HBMResourceManager.mine_he_tex;
            HBMResourceManager.mine_he.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.MINE_SHRAP.get().asItem()) {
            poseStack.translate(-0.2, 0.5, 0);
            poseStack.scale(0.7f, 0.7f, 0.7f);
            poseStack.mulPose(Axis.XP.rotationDegrees(45));
            texture = HBMResourceManager.mine_shrap_tex;
            HBMResourceManager.mine_ap.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        } else if (item == ModBlocks.MINE_FAT.get().asItem()) {
            poseStack.scale(0.6f, 0.6f, 0.6f);
            poseStack.translate(-0.1, 0.3, 0);
            texture = HBMResourceManager.mine_fat_tex;
            VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
            HBMResourceManager.mine_fat.renderAll(poseStack, vertexConsumer, packedLight, packedOverlay);
        } else if (item == ModBlocks.MINE_NAVAL.get().asItem()) {
            texture = HBMResourceManager.mine_naval_tex;
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.translate(-0.2, 1.2, 0);
            HBMResourceManager.mine_naval.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }
}