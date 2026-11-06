package com.hbm.tileentity.machine.oil;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class TileEntitySpacer extends BlockEntity {

    private AABB renderBox;

    public TileEntitySpacer(BlockPos pos, BlockState state) {
        super(ModTileEntity.SPACER.get(), pos, state);
    }

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        if (renderBox == null) {
            renderBox = new AABB(
                    worldPosition.getX() - 1,
                    worldPosition.getY(),
                    worldPosition.getZ() - 1,
                    worldPosition.getX() + 2,
                    worldPosition.getY() + 1,
                    worldPosition.getZ() + 2
            );
        }
        return renderBox;
    }
}