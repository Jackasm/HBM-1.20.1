package com.hbm.render.tileentity;

import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.oil.TileEntityMachineFractionTower;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderFractionTower implements BlockEntityRenderer<TileEntityMachineFractionTower> {

    public RenderFractionTower(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityMachineFractionTower tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int combinedLight, int combinedOverlay) {

        poseStack.pushPose();
        // В 1.20.1 poseStack уже локализован относительно позиции BE → x/y/z из 1.7.10 заменяются на сдвиг к центру блока
        poseStack.translate(0.5, 0, 0.5);

        HBMResourceManager.fraction_tower.renderAll(poseStack, buffer,
                HBMResourceManager.fraction_tower_tex, combinedLight, combinedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineFractionTower tile) {
        return true;
    }
}