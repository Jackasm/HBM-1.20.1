package com.hbm.blocks.network;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.network.TileEntityRadioTorchReceiver;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RadioTorchReceiver extends RadioTorchRWBase {

    public RadioTorchReceiver(Properties properties) {
        super(properties, "rtty_rec_off", "rtty_rec_on");
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityRadioTorchReceiver(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.RADIO_TORCH_RECEIVER.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityRadioTorchReceiver r) r.tick();
            };
        }
        return null;
    }

    /** Аналог isProvidingWeakPower. */
    @Override
    public int getSignal(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                         @NotNull Direction side) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityRadioTorchReceiver r) {
            return r.lastState;
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(@NotNull BlockState state) {
        return true;
    }
}