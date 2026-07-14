package com.hbm.inventory.container;

import com.hbm.inventory.CargoItemHandler;
import com.hbm.tileentity.machine.TileEntityCargoContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerCargoContainer extends AbstractContainerMenu {

    private final TileEntityCargoContainer tile;
    private final CargoItemHandler handler;

    public ContainerCargoContainer(int windowId, Inventory inv, TileEntityCargoContainer tile) {
        super(ModContainers.CARGO_CONTAINER.get(), windowId);
        this.tile = tile;
        this.handler = tile.getItemHandler();
        int slotSize = 18;

        int offsetToCenter = 40;

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                this.addSlot(new Slot(inv, col + row * 9 + 9,
                        8 + col * slotSize + offsetToCenter,
                        64 + row * slotSize));
            }
        }

        for (int col = 0; col < 9; col++) {
            this.addSlot(new Slot(inv, col,
                    8 + col * slotSize + offsetToCenter,
                    122));
        }
    }

    public TileEntityCargoContainer getTile() {
        return tile;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        Slot slot = this.slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;

        ItemStack stackInSlot = slot.getItem();
        ItemStack copy = stackInSlot.copy();

        ItemStack remaining = handler.insertItem(0, stackInSlot, false);

        if (remaining.getCount() != stackInSlot.getCount()) {
            slot.set(remaining);
            player.containerMenu.broadcastChanges();
        }

        if (remaining.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        }

        return copy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return tile.stillValid(player);
    }

}