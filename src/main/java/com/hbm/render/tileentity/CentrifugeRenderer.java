package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachineCentrifuge;
import com.hbm.blocks.machine.MachineGasCent;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.machine.TileEntityMachineCentrifuge;
import com.hbm.tileentity.machine.TileEntityMachineGasCent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class CentrifugeRenderer implements BlockEntityRenderer<BlockEntity> {

    public CentrifugeRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(@NotNull BlockEntity tileEntity, float partialTicks, PoseStack stack, @NotNull MultiBufferSource buffer, int light, int overlay) {
        stack.pushPose();
        stack.translate(0.5D, 0, 0.5D);

        Direction facing = Direction.NORTH;

        if (tileEntity instanceof TileEntityMachineCentrifuge centrifuge) {
            BlockState state = centrifuge.getBlockState();
            facing = state.getValue(MachineCentrifuge.FACING);
        } else if (tileEntity instanceof TileEntityMachineGasCent gasCent) {
            BlockState state = gasCent.getBlockState();
            facing = state.getValue(MachineGasCent.FACING);
        }

        switch (facing) {
            case EAST -> stack.mulPose(Axis.YP.rotationDegrees(90));
            case SOUTH -> stack.mulPose(Axis.YP.rotationDegrees(180));
            case WEST -> stack.mulPose(Axis.YP.rotationDegrees(270));
            default -> {}
        }

        ResourceLocation texture;

        if (tileEntity instanceof TileEntityMachineGasCent) {
            texture = HBMResourceManager.gascent_tex;
            stack.mulPose(Axis.YP.rotationDegrees(180));
            HBMResourceManager.gascent.renderPart(stack, buffer, "Centrifuge", texture, light, overlay);
            HBMResourceManager.gascent.renderPart(stack, buffer, "Flag", texture, light, overlay);
        } else {
            texture = HBMResourceManager.centrifuge_tex;
            HBMResourceManager.centrifuge.renderAll(stack, buffer, texture, light, overlay);
        }

        stack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull BlockEntity tileEntity) {
        return false;
    }
}