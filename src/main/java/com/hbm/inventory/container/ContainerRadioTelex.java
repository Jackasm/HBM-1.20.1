package com.hbm.inventory.container;

import com.hbm.tileentity.network.TileEntityRadioTelex;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerRadioTelex extends AbstractContainerMenu {

    public final TileEntityRadioTelex telex;

    public ContainerRadioTelex(int windowId, Inventory playerInv, TileEntityRadioTelex telex) {
        super(ModContainers.RADIO_TELEX.get(), windowId);
        this.telex = telex;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return telex.hasPermission(player);
    }
}