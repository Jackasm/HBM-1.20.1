package com.hbm.tileentity.network;

import com.hbm.blocks.network.RadioTorchBase;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityRadioTorchSender extends TileEntityRadioTorchBase {

    public TileEntityRadioTorchSender(BlockPos pos, BlockState state) {
        // Свой тип обязателен: с базовым типом getTicker не узнаёт блок и не
        // регистрирует тикер — передатчик молча стоял без тика (#165).
        super(ModTileEntity.RADIO_TORCH_SENDER.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) {
            super.tick();
            return;
        }

        // FACING в порте смотрит НА опору (в 1.7.10 её давала meta.getOpposite()),
        // поэтому getOpposite() здесь читал блок с другой стороны факела.
        Direction dir = getBlockState().getValue(RadioTorchBase.FACING);
        BlockPos attachPos = worldPosition.relative(dir);
        BlockState attachState = level.getBlockState(attachPos);

        int input = level.getBestNeighborSignal(attachPos);

        if (attachState.hasAnalogOutputSignal()) {
            input = attachState.getBlock().getAnalogOutputSignal(attachState, level, attachPos);
        }

        boolean shouldSend = this.polling;

        if (input != this.lastState) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
            this.lastState = input;
            shouldSend = true;
        }

        if (shouldSend && !this.channel.isEmpty()) {
            String toSend = this.customMap ? this.mapping[input] : (input + "");
            if (toSend != null && !toSend.isEmpty()) {
                RTTYSystem.broadcast(level, this.channel, toSend);
            }
        }

        super.tick();
    }
}