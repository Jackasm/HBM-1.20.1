package com.hbm.inventory.container;

import com.hbm.items.ModItems;
import com.hbm.tileentity.machine.TileEntityCrucible;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerCrucible extends AbstractContainerMenu {

    private final TileEntityCrucible crucible;

    public ContainerCrucible(int windowId, Inventory playerInv, TileEntityCrucible tile) {
        super(ModContainers.CRUCIBLE.get(), windowId);
        this.crucible = tile;

        // Template
        this.addSlot(new SlotItemHandler(tile.getItemHandler(), 0, 107, 81) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getItem() == ModItems.CRUCIBLE_TEMPLATE.get();
            }
        });

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                this.addSlot(new SlotItemHandler(tile.getItemHandler(), j + i * 3 + 1, 107 + j * 18, 18 + i * 18) {
                    @Override
                    public boolean mayPlace(@NotNull ItemStack stack) {
                        return crucible.isItemSmeltable(stack);
                    }
                });
            }
        }

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 132 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 190));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack original = slot.getItem();
            stack = original.copy();

            if (index <= 9) {
                if (!this.moveItemStackTo(original, 10, this.slots.size(), true)) return ItemStack.EMPTY;
            } else if (!this.moveItemStackTo(original, 0, 10, false)) {
                return ItemStack.EMPTY;
            }

            if (original.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return crucible.isUsableByPlayer(player);
    }

    public TileEntityCrucible getCrucible() {
        return crucible;
    }
}