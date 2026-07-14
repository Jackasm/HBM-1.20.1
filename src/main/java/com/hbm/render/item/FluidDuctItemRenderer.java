package com.hbm.render.item;

import com.hbm.blocks.network.FluidDuctStandard;
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

public class FluidDuctItemRenderer extends BlockEntityWithoutLevelRenderer  {

    public FluidDuctItemRenderer() {
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
                poseStack.translate(0.5, 0.5, 0);
                poseStack.scale(0.9f, 0.9f, 0.9f);
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
                poseStack.translate(0.5, 0.7, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0, 0.5);
                poseStack.scale(0.75f, 0.75f, 0.75f);
        }

        int pipeType = 0;
        Item item = stack.getItem();
        if (item == ModItems.FLUID_DUCT_SILVER.get()) {
            pipeType = 1;
        } else if (item == ModItems.FLUID_DUCT_COLORED.get()) {
            pipeType = 2;
        }

        ResourceLocation texture = FluidDuctStandard.TEXTURES.get(pipeType);
        if (texture == null) {
            texture = FluidDuctStandard.TEXTURES.get(0);
        }

        if (pipeType == 2) {
            // Для цветной трубы рендерим с цветом из жидкости
            int color = 0x888888; // серый по умолчанию

            float r = ((color >> 16) & 0xFF) / 255.0f;
            float g = ((color >> 8) & 0xFF) / 255.0f;
            float b = (color & 0xFF) / 255.0f;

            // Рендерим всю модель с цветом
            HBMResourceManager.fluid_duct.renderAll(
                    poseStack, buffer, texture, packedLight, packedOverlay);

            ResourceLocation overlay = FluidDuctStandard.OVERLAYS.get(pipeType);
            HBMResourceManager.fluid_duct.renderAllColored(
                    poseStack, buffer, overlay, packedLight, packedOverlay, r, g, b, 1.0f
            );
        } else {
            // Обычная и серебряная труба - без цвета
            HBMResourceManager.fluid_duct.renderAll(
                    poseStack, buffer, texture, packedLight, packedOverlay
            );
        }

        poseStack.popPose();
    }
}
