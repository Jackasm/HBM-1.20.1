package com.hbm.inventory.container;

import com.hbm.tileentity.storage.TileEntityCrateDesh;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ContainerCrateDesh extends AbstractContainerMenu {

    public final TileEntityCrateDesh crate;

    public ContainerCrateDesh(int id, Inventory inv, TileEntityCrateDesh crate) {
        super(ModContainers.CRATE_DESH.get(), id);
        this.crate = crate;

        if (crate != null) {
            crate.startOpen(inv.player);
        }

        IItemHandler handler = Objects.requireNonNull(crate).getInventory();

        // Слоты ящика: 8 рядов x 13 колонок = 104 слота
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 13; col++) {
                int slotIndex = row * 13 + col;
                if (slotIndex >= 104) break;
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                addSlot(new SlotItemHandler(handler, slotIndex, x, y));
            }
        }

        // Инвентарь игрока
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = col + row * 9 + 9;
                int x = 44 + col * 18;
                int y = 174 + row * 18;
                addSlot(new Slot(inv, slotIndex, x, y));
            }
        }

        // Хотбар
        for (int col = 0; col < 9; col++) {
            int x = 44 + col * 18;
            int y = 232;
            addSlot(new Slot(inv, col, x, y));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return crate != null && crate.stillValid(player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (crate != null) {
            crate.stopOpen(player);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if (index < 104) {
                if (!moveItemStackTo(slotStack, 104, 140, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(slotStack, 0, 104, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (slotStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return stack;
    }
}