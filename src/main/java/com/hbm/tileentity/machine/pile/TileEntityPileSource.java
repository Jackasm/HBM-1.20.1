package com.hbm.tileentity.machine.pile;

import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Objects;

public class TileEntityPileSource extends TileEntityPileBase {

    public TileEntityPileSource(BlockPos pos, BlockState state) {
        super(ModTileEntity.PILE_SOURCE.get(), pos, state);
    }

    public void tick() {
        if (!Objects.requireNonNull(level).isClientSide) {
            BlockState state = getBlockState();
            boolean isSource = state.getBlock() == ModBlocks.BLOCK_GRAPHITE_SOURCE.get();
            int n = isSource ? 1 : 2;

            for (int i = 0; i < 12; i++) {
                this.castRay(n);
            }
        }
    }
}