package com.hbm.blocks.network;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.network.TileEntityFoundrySlagtap;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FoundrySlagtap extends FoundryOutlet {

    public FoundrySlagtap(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityFoundrySlagtap(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.FOUNDRY_SLAGTAP.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityFoundrySlagtap slagtap) {
                    TileEntityFoundrySlagtap.serverTick(lvl, pos, st, slagtap);
                }
            };
        }
        return null;
    }
}