package com.hbm.api.energy;

import com.hbm.uninos.*;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IEnergyProvider extends IEnergyHandler {

    /**
     * Потребляет указанное количество энергии (без проверок).
     * @param power количество для уменьшения
     */
    default void usePower(long power) {
        this.setPower(this.getPower() - power);
    }

    /**
     * Максимальная скорость отдачи за тик (обычно равна getMaxPower()).
     */
    default long getProviderSpeed() {
        return this.getMaxPower();
    }

    /**
     * Попытка передать энергию в сторону (вызывается каждый тик из TileEntity).
     * @param world уровень
     * @param pos позиция текущего блока
     * @param dir направление передачи
     */
    default void tryProvide(Level world, BlockPos pos, Direction dir) {
        BlockEntity te = world.getBlockEntity(pos);

        if (te instanceof IEnergyConductor conductor) {
            if (conductor.canConnect(dir.getOpposite())) {
                EnergyNode node = EnergyNodespace.getNode(world, pos);
                if (node != null && node.net != null) {
                    node.net.addProvider(this);
                }
            }
        }

        if (te instanceof IEnergyReceiver receiver && te != this) {
            if (receiver.canConnect(dir.getOpposite())) {
                long provides = Math.min(this.getPower(), this.getProviderSpeed());
                long receives = Math.min(receiver.getMaxPower() - receiver.getPower(), receiver.getReceiverSpeed());
                long toTransfer = Math.min(provides, receives);
                toTransfer -= receiver.transferPower(toTransfer);
                this.usePower(toTransfer);
            }
        }
    }
}