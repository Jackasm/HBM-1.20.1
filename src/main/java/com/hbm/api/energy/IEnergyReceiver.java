package com.hbm.api.energy;

import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.EnergyNodespace;
import com.hbm.util.Compat;
import com.hbm.util.Library;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public interface IEnergyReceiver extends IEnergyHandler {

    /**
     * Передаёт энергию потребителю, возвращает непоглощённый остаток.
     * @param power количество энергии для передачи
     * @return энергия, которую не удалось принять
     */
    default long transferPower(long power) {
        long newPower = this.getPower() + power;
        if (newPower <= this.getMaxPower()) {
            this.setPower(newPower);
            return 0;
        }
        long capacity = this.getMaxPower() - this.getPower();
        this.setPower(this.getMaxPower());
        return power - capacity;
    }

    /**
     * Максимальная скорость приёма за тик.
     */
    default long getReceiverSpeed() {
        return this.getMaxPower();
    }

    /**
     * Попытка подписаться на узел проводника.
     */
    default void trySubscribe(Level world, Library.PosDir posDir) {
        trySubscribe(world, posDir.pos(), posDir.dir());
    }

    /**
     * Попытка подписаться на узел проводника.
     */
    default void trySubscribe(Level world, BlockPos pos, Direction dir) {
        BlockEntity te = Compat.getTileStandard(world, pos);

        if (te instanceof IEnergyConductor conductor) {
            if (!conductor.canConnect(dir.getOpposite())) return;
            EnergyNode node = EnergyNodespace.getNode(world, pos);
            if (node != null && node.net != null) {
                node.net.addReceiver(this);
            }
        }
    }

    /**
     * Отписывается от узла проводника (при разрушении потребителя).
     */
    default void tryUnsubscribe(Level world, BlockPos pos) {
        BlockEntity te = world.getBlockEntity(pos);
        if (te instanceof IEnergyConductor) {
            EnergyNode node = EnergyNodespace.getNode(world, pos);
            if (node != null && node.net != null) {
                node.net.removeReceiver(this);
            }
        }
    }

    /**
     * Приоритет потребителя (выше – получает энергию раньше).
     */
    enum ConnectionPriority {
        LOWEST, LOW, NORMAL, HIGH, HIGHEST
    }

    default ConnectionPriority getPriority() {
        return ConnectionPriority.NORMAL;
    }
}