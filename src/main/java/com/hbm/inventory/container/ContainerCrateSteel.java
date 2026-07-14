package com.hbm.inventory.container;

import com.hbm.tileentity.storage.TileEntityCrateSteel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ContainerCrateSteel extends AbstractContainerMenu {

    public final TileEntityCrateSteel crate;

    public ContainerCrateSteel(int id, Inventory inv, TileEntityCrateSteel crate) {
        super(ModContainers.CRATE_STEEL.get(), id);
        this.crate = crate;

        if (crate != null) {
            crate.startOpen(inv.player);
        }

        IItemHandler handler = Objects.requireNonNull(crate).getInventory();

        // Слоты ящика: 6 рядов x 9 колонок = 54 слота
        for (int row = 0; row < 6; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = row * 9 + col;
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                addSlot(new SlotItemHandler(handler, slotIndex, x, y));
            }
        }

        // Инвентарь игрока
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = col + row * 9 + 9;
                int x = 8 + col * 18;
                int y = 140 + row * 18;
                addSlot(new Slot(inv, slotIndex, x, y));
            }
        }

        // Хотбар
        for (int col = 0; col < 9; col++) {
            int x = 8 + col * 18;
            int y = 198;
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

            if (index < 54) {
                if (!moveItemStackTo(slotStack, 54, 90, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(slotStack, 0, 54, false)) {
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