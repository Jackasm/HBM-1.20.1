package com.hbm.inventory.container;

import com.hbm.tileentity.storage.TileEntitySafe;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ContainerSafe extends AbstractContainerMenu {

    public final TileEntitySafe safe;

    public ContainerSafe(int id, Inventory inv, TileEntitySafe safe) {
        super(ModContainers.SAFE.get(), id);
        this.safe = safe;

        if (safe != null) {
            safe.startOpen(inv.player);
        }

        IItemHandler handler = Objects.requireNonNull(safe).getInventory();

        // Слоты сейфа: 3 ряда x 5 колонок = 15 слотов
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 5; col++) {
                int slotIndex = row * 5 + col;
                int x = 44 + col * 18;
                int y = 18 + row * 18;
                addSlot(new SlotItemHandler(handler, slotIndex, x, y));
            }
        }

        // Инвентарь игрока
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = col + row * 9 + 9;
                int x = 8 + col * 18;
                int y = 84 + row * 18;
                addSlot(new Slot(inv, slotIndex, x, y));
            }
        }

        // Хотбар
        for (int col = 0; col < 9; col++) {
            int x = 8 + col * 18;
            int y = 142;
            addSlot(new Slot(inv, col, x, y));
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return safe != null && safe.stillValid(player);
    }

    @Override
    public void removed(@NotNull Player player) {
        super.removed(player);
        if (safe != null) {
            safe.stopOpen(player);
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = slots.get(index);

        if (slot.hasItem()) {
            ItemStack slotStack = slot.getItem();
            stack = slotStack.copy();

            if (index < 15) {
                if (!moveItemStackTo(slotStack, 15, 51, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(slotStack, 0, 15, false)) {
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