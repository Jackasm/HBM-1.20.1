package com.hbm.inventory.container;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

/**
 * Общий контейнер RTTY-факелов: слотов нет, всё настройки живут в TE и
 * уезжают через {@code receiveControl}.
 *
 * <p>Тип TE намеренно не зафиксирован: у sender/receiver/logic это
 * {@code TileEntityRadioTorchBase}, у reader и controller — свои TE.
 * {@link #getTE()} существует для экранов базового вида; остальные берут
 * сущность через {@link #getBE()}.</p>
 */
public class ContainerRadioTorch extends AbstractContainerMenu {

    private final BlockEntity te;

    public ContainerRadioTorch(int id, Inventory inv, BlockEntity te) {
        super(ModContainers.RADIO_TORCH.get(), id);
        this.te = te;
    }

    public BlockEntity getBE() { return te; }

    /** TE факела базового вида (sender, receiver, logic). */
    public com.hbm.tileentity.network.TileEntityRadioTorchBase getTE() {
        return (com.hbm.tileentity.network.TileEntityRadioTorchBase) te;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (te.getLevel() == null || te.getLevel().getBlockEntity(te.getBlockPos()) != te) return false;
        return player.distanceToSqr(
                te.getBlockPos().getX() + 0.5,
                te.getBlockPos().getY() + 0.5,
                te.getBlockPos().getZ() + 0.5) <= 256.0;
    }
}
