package com.hbm.render.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockChargeBase;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.tileentity.bomb.TileEntityCharge;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderExplosiveCharge implements BlockEntityRenderer<TileEntityCharge> {

    public RenderExplosiveCharge(BlockEntityRendererProvider.Context ignoredCtx) {
    }

    @Override
    public void render(@NotNull TileEntityCharge te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        Direction facing = te.getBlockState().getValue(BlockChargeBase.FACING);
        applyFacingRotation(poseStack, facing);

        // --- Модель заряда ---
        HFRWavefrontObject model = getModel(te);
        ResourceLocation texture = getTexture(te);
        model.renderAll(poseStack, buffer, texture, packedLight, packedOverlay);

        // --- Текст таймера ---
        renderTimerText(te, poseStack, buffer, packedLight);

        poseStack.popPose();
    }

    /** Те же повороты, что и в оригинале (case 0..5). */
    private static void applyFacingRotation(PoseStack poseStack, Direction facing) {
        switch (facing) {
            case DOWN  -> poseStack.mulPose(Axis.ZP.rotationDegrees(180)); // case 0
            case UP    -> { /* case 1 — без поворота */ }
            case NORTH -> { poseStack.mulPose(Axis.YP.rotationDegrees(90));  poseStack.mulPose(Axis.ZP.rotationDegrees(-90)); } // case 2
            case SOUTH -> { poseStack.mulPose(Axis.YP.rotationDegrees(-90)); poseStack.mulPose(Axis.ZP.rotationDegrees(-90)); } // case 3
            case WEST  -> { poseStack.mulPose(Axis.YP.rotationDegrees(180)); poseStack.mulPose(Axis.ZP.rotationDegrees(-90)); } // case 4
            case EAST  -> { poseStack.mulPose(Axis.ZP.rotationDegrees(-90)); } // case 5
        }
    }

    private static void renderTimerText(TileEntityCharge te, PoseStack poseStack,
                                        MultiBufferSource buffer, int packedLight) {
        String text = te.getMinutes() + ":" + te.getSeconds();

        poseStack.pushPose();

        // Оригинал: glTranslatef(-0.05F, 0.315F - 0.5F, 0.15F); scale(0.0125, -0.0125, 0.0125)
        poseStack.translate(-0.05F, 0.315F - 0.5F, 0.15F);
        float f = 0.0125F;
        poseStack.scale(f, -f, f);

        // Оригинал: rotatef(90, 0, 1, 0); rotatef(90, 1, 0, 0)
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
        poseStack.mulPose(Axis.XP.rotationDegrees(90));

        Font font = Minecraft.getInstance().font;

        // glDepthMask(false) -> DisplayMode.SEE_THROUGH
        font.drawInBatch(
                text,
                0, 0,
                0x00FF00,
                false,
                poseStack.last().pose(),
                buffer,
                Font.DisplayMode.SEE_THROUGH,
                0,
                packedLight
        );

        poseStack.popPose();
    }

    private static HFRWavefrontObject getModel(TileEntityCharge te) {
        Block block = te.getBlockState().getBlock();
        if (block == ModBlocks.CHARGE_DYNAMITE.get() || block == ModBlocks.CHARGE_MINER.get()) {
            return HBMResourceManager.charge_dynamite;
        }
        return HBMResourceManager.charge_c4;
    }

    private static ResourceLocation getTexture(TileEntityCharge te) {
        Block block = te.getBlockState().getBlock();
        if (block == ModBlocks.CHARGE_DYNAMITE.get()) return HBMResourceManager.charge_dynamite_tex;
        if (block == ModBlocks.CHARGE_MINER.get())    return HBMResourceManager.charge_miner_tex;
        if (block == ModBlocks.CHARGE_C4.get())       return HBMResourceManager.charge_c4_tex;
        if (block == ModBlocks.CHARGE_SEMTEX.get())   return HBMResourceManager.charge_semtex_tex;
        return HBMResourceManager.charge_dynamite_tex;
    }
}