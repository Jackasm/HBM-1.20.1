package com.hbm.render.item;

import com.hbm.blocks.generic.BlockBobble.BobbleType;
import com.hbm.items.deco.ItemBobble;
import com.hbm.render.tileentity.RenderBobble;
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
public class RenderBobbleItem extends BlockEntityWithoutLevelRenderer {

    public RenderBobbleItem() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context,
                             @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                             int combinedLight, int combinedOverlay) {

        BobbleType type = ItemBobble.getType(stack);

        poseStack.pushPose();
        // Трансформации под контекст
        switch (context) {
            case GUI -> {
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.mulPose(Axis.XP.rotationDegrees(20));
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case GROUND -> {
                poseStack.translate(0.5, 0.1, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            case FIXED -> {
                poseStack.translate(0.5, 0.25, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(135));
            }
            case FIRST_PERSON_LEFT_HAND, FIRST_PERSON_RIGHT_HAND,
                 THIRD_PERSON_LEFT_HAND, THIRD_PERSON_RIGHT_HAND -> {
                poseStack.translate(0.5, 0.35, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
            default -> {
                poseStack.translate(0.5, 0.35, 0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
            }
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        // Масштаб модели — как у блока
        poseStack.scale(0.25F, 0.25F, 0.25F);

        // Анимация работает и в инвентаре/руке — как в духе HBM
        long time = System.currentTimeMillis();
        RenderBobble.renderBobble(type, time, poseStack, buffer, combinedLight, combinedOverlay, false);

        poseStack.popPose();
    }
}