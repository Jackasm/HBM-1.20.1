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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderToasterItem extends BlockEntityWithoutLevelRenderer {

    public RenderToasterItem() {
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
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(1f, 1f, 1f);
                poseStack.mulPose(Axis.XP.rotationDegrees(15));
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0.05, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIXED -> {
                poseStack.translate(0.5, 0.05, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5, 0.05, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
            case THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5, 0.05, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
            case HEAD -> {
                poseStack.translate(0.5, 0.05, 0.5);
                poseStack.scale(0.6f, 0.6f, 0.6f);
            }
            default -> {
                poseStack.translate(0.5, 0.05, 0.5);
                poseStack.scale(0.7f, 0.7f, 0.7f);
                poseStack.mulPose(Axis.YP.rotationDegrees(45 + 180));
            }
        }

        ResourceLocation texture = getTexture(stack);
        HBMResourceManager.toaster.renderAll(poseStack, buffer, texture, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    private ResourceLocation getTexture(ItemStack stack) {
        var item = stack.getItem();
        if (item == com.hbm.items.ModItems.DECO_TOASTER_IRON.get())  return HBMResourceManager.toaster_iron_tex;
        if (item == com.hbm.items.ModItems.DECO_TOASTER_STEEL.get()) return HBMResourceManager.toaster_steel_tex;
        if (item == com.hbm.items.ModItems.DECO_TOASTER_WOOD.get())  return HBMResourceManager.toaster_wood_tex;
        return HBMResourceManager.toaster_iron_tex;
    }
}