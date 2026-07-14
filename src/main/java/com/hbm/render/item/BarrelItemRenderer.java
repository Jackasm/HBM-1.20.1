package com.hbm.render.item;

import com.hbm.items.ModItems;
import com.hbm.main.HBMResourceManager;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;


public class BarrelItemRenderer extends BlockEntityWithoutLevelRenderer {
    public BarrelItemRenderer() {
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
                poseStack.translate(0.55, 0.1, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(15));
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45));
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
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.75f, 0.75f, 0.75f);
        }



        ResourceLocation texture;
        Item item = stack.getItem();
        if (item == ModItems.BARREL_IRON.get()) {
            texture = HBMResourceManager.barrel_iron_tex;
        } else if (item == ModItems.BARREL_STEEL.get()) {
            texture = HBMResourceManager.barrel_steel_tex;
        }else if (item == ModItems.BARREL_PLASTIC.get()) {
            texture = HBMResourceManager.barrel_plastic_tex;
        }else if (item == ModItems.BARREL_CORRODED.get()) {
            texture = HBMResourceManager.barrel_corroded_tex;
        }else if (item == ModItems.BARREL_TCALLOY.get()) {
            texture = HBMResourceManager.barrel_tcalloy_tex;
        }else {
            texture = HBMResourceManager.barrel_antimatter_tex;
        }
        HBMResourceManager.barrel.renderAll(
                poseStack, buffer, texture, packedLight, packedOverlay
        );

        poseStack.popPose();
    }
}