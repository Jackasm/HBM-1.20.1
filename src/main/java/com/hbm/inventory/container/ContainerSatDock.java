package com.hbm.inventory.container;

import com.hbm.items.machine.ItemSatChip;
import com.hbm.tileentity.machine.TileEntityMachineSatDock;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerSatDock extends AbstractContainerMenu {

    private final TileEntityMachineSatDock tileSatelliteDock;

    public ContainerSatDock(int windowId, Inventory invPlayer, TileEntityMachineSatDock tesd) {
        super(ModContainers.SAT_DOCK.get(), windowId);
        this.tileSatelliteDock = tesd;

        // Storage slots (15 slots for cargo)
        int[] xPos = {62, 80, 98, 116, 134};
        int[] yPos = {17, 35, 53};

        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                int index = row * 5 + col;
                int x = 62 + col * 18;
                int y = 17 + row * 18;

                this.addSlot(new SlotItemHandler(tesd.getInventory(), index, x, y) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return false;
                    }
                });
            }
        }

        // Chip slot (slot 15)
        this.addSlot(new SlotItemHandler(tesd.getInventory(), 15, 26, 35) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() instanceof ItemSatChip;
            }
        });

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slotIndex) {
        ItemStack var3 = ItemStack.EMPTY;
        Slot slot = this.slots.get(slotIndex);

        if (slot != null && slot.hasItem()) {
            ItemStack var5 = slot.getItem();
            var3 = var5.copy();

            if (slotIndex <= 15) {
                if (!this.moveItemStackTo(var5, 16, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(var5, 0, 15, false)) {
                return ItemStack.EMPTY;
            }

            if (var5.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return var3;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return tileSatelliteDock.stillValid(player);
    }
}