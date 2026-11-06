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
public class LanternItemRenderer extends BlockEntityWithoutLevelRenderer {

    public LanternItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int packedLight, int packedOverlay) {

        poseStack.pushPose();
        applyTransform(context, poseStack);

        boolean rusty = false;
        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem bi) {
            rusty = bi.getBlock() instanceof com.hbm.blocks.generic.BlockLanternBehemoth;
        }

        ResourceLocation tex = rusty ? HBMResourceManager.lantern_rusty_tex : HBMResourceManager.lantern_tex;
        HBMResourceManager.lantern.renderPart(poseStack, buffer, "Lantern", tex, packedLight, packedOverlay);

        float r, g;
        float mult = (float) (Math.sin(System.currentTimeMillis() / 200D) / 2 + 0.5);
        if (rusty) {
            r = mult; g = 0F;
        } else {
            r = 1F * (mult * 0.1F + 0.9F);
            g = 1F * (mult * 0.1F + 0.9F);
            // синий канал 0.7
            HBMResourceManager.lantern.renderPartColored(
                    poseStack, buffer, "Light",
                    tex, packedLight, packedOverlay,
                    r, g, 0.7F * (mult * 0.1F + 0.9F), 1F
            );
            poseStack.popPose();
            return;
        }

        HBMResourceManager.lantern.renderPartColored(
                poseStack, buffer, "Light",
                tex, packedLight, packedOverlay,
                r, g, 0F, 1F
        );

        poseStack.popPose();
    }

    private void applyTransform(ItemDisplayContext context, PoseStack poseStack) {
        switch (context) {
            case GUI -> {
                poseStack.translate(0.1, 0.1, 0.5);
                poseStack.scale(0.2f, 0.2f, 0.2f);
                poseStack.mulPose(Axis.ZN.rotationDegrees(45));

            }
            case GROUND -> {
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.scale(0.35f, 0.35f, 0.35f);
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
            }
            default -> {
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.5f, 0.5f, 0.5f);
            }
        }
    }
}