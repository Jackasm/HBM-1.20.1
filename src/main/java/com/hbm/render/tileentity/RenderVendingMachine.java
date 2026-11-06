package com.hbm.render.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.machine.BlockVendingMachine;
import com.hbm.main.HBMResourceManager;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class RenderVendingMachine implements BlockEntityRenderer<BlockVendingMachine.TileEntityVendingMachine> {

    public RenderVendingMachine(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull BlockVendingMachine.TileEntityVendingMachine tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        BlockState state = tile.getBlockState();
        if (!(state.getBlock() instanceof BlockVendingMachine vending)) return;

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        Direction facing = state.getValue(BlockDummyable.FACING);
        float rot = switch (facing) {
            case NORTH -> 90F;
            case WEST  -> 180F;
            case SOUTH -> 270F;
            case EAST  -> 0F;
            default -> 0F;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));

        String part = (vending.getVariant() == BlockVendingMachine.VendingVariant.SODA) ? "Soda" : "Obamna";
        HBMResourceManager.vending_machine.renderPart(poseStack, buffer, part,
                HBMResourceManager.vending_machine_tex, packedLight, packedOverlay);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockVendingMachine.TileEntityVendingMachine tile) {
        return true;
    }
}