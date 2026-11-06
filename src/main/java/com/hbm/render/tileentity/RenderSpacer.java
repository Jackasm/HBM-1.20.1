package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.oil.TileEntitySpacer;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderSpacer implements BlockEntityRenderer<TileEntitySpacer> {

    public RenderSpacer(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntitySpacer tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        // В 1.20.1 poseStack уже локализован относительно позиции BE,
        // поэтому x/y/z из 1.7.10 заменяются на сдвиг к центру блока (0.5, 0, 0.5).
        poseStack.translate(0.5, 0, 0.5);

        HBMResourceManager.fraction_spacer.renderAll(poseStack, buffer,
                HBMResourceManager.fraction_spacer_tex, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntitySpacer tile) {
        return true;
    }
}