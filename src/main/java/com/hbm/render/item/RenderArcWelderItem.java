package com.hbm.render.item;

import com.hbm.main.HBMResourceManager;
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
public class RenderArcWelderItem extends BlockEntityWithoutLevelRenderer {

    public RenderArcWelderItem() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext context, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int combinedLight, int combinedOverlay) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0.3, 0.5);
        poseStack.scale(0.25f, 0.25f, 0.25f);
        poseStack.mulPose(Axis.XP.rotationDegrees(15));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));

        HBMResourceManager.arc_welder.renderAll(poseStack, buffer,
                HBMResourceManager.arc_welder_tex, combinedLight, combinedOverlay);
        poseStack.popPose();
    }
}