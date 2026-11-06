package com.hbm.blocks.network;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.network.TileEntityRadioTorchLogic;
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

/**
 * Логический RTTY-факел: сравнивает сигнал канала с таблицей и выдаёт редстоун
 * номером подошедшей строки.
 *
 * <p>Порт {@code ref:blocks/network/RadioTorchLogic.java}: в 1.7.10 блок
 * {@code canProvidePower} и отдавал {@code lastState} слабым сигналом — здесь
 * то же самое.</p>
 */
public class RadioTorchLogic extends RadioTorchRWBase {

    public RadioTorchLogic(Properties properties) {
        super(properties, "rtty_logic_off", "rtty_logic_on");
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityRadioTorchLogic(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.RADIO_TORCH_LOGIC.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityRadioTorchLogic logic) logic.tick();
            };
        }
        return null;
    }

    @Override
    public int getSignal(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                         @NotNull Direction side) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityRadioTorchLogic logic) {
            return logic.lastState;
        }
        return 0;
    }

    @Override
    public boolean isSignalSource(@NotNull BlockState state) {
        return true;
    }
}
