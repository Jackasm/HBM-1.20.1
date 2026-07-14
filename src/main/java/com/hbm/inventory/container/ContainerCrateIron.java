package com.hbm.inventory.container;

import com.hbm.tileentity.storage.TileEntityCrateIron;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ContainerCrateIron extends AbstractContainerMenu {

    public final TileEntityCrateIron crate;
    private final Inventory playerInv;

    public ContainerCrateIron(int id, Inventory inv, TileEntityCrateIron crate) {
        super(ModContainers.CRATE_IRON.get(), id);
        this.crate = crate;
        this.playerInv = inv;

        if (crate != null) {
            crate.startOpen(inv.player);
        }

        IItemHandler handler = Objects.requireNonNull(crate).getInventory();

        // Слоты ящика: 4 ряда x 9 колонок = 36 слотов
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = row * 9 + col;
                int x = 8 + col * 18;
                int y = 18 + row * 18;
                addSlot(new SlotItemHandler(handler, slotIndex, x, y));
            }
        }

        // Инвентарь игрока (3 ряда x 9 колонок)
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                int slotIndex = col + row * 9 + 9;
                int x = 8 + col * 18;
                int y = 104 + row * 18;
                addSlot(new Slot(playerInv, slotIndex, x, y));
            }
        }

        // Хотбар
        for (int col = 0; col < 9; col++) {
            int x = 8 + col * 18;
            int y = 162;
            addSlot(new Slot(playerInv, col, x, y));
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

            // Ящик: 0-35, Инвентарь: 36-71, Хотбар: 72-80
            if (index < 36) {
                if (!moveItemStackTo(slotStack, 36, 81, true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (!moveItemStackTo(slotStack, 0, 36, false)) {
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