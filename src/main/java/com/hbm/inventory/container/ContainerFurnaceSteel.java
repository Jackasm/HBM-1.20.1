package com.hbm.inventory.container;

import com.hbm.inventory.SlotSmelting;
import com.hbm.tileentity.machine.TileEntityFurnaceSteel;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerFurnaceSteel extends AbstractContainerMenu {

    protected TileEntityFurnaceSteel furnace;
    private final ContainerData data;

    public ContainerFurnaceSteel(int windowId, Inventory invPlayer, Player player, TileEntityFurnaceSteel furnace) {
        super(ModContainers.FURNACE_STEEL.get(), windowId);
        this.furnace = furnace;
        this.data = new SimpleContainerData(7); // 3 прогресса + 3 бонуса + тепло

        IItemHandler handler = furnace.getItemHandler();

        // Input slots
        this.addSlot(new SlotItemHandler(handler, 0, 35, 17));
        this.addSlot(new SlotItemHandler(handler, 1, 35, 35));
        this.addSlot(new SlotItemHandler(handler, 2, 35, 53));

        this.addSlot(new SlotSmelting(player, handler, 3, 125, 17));
        this.addSlot(new SlotSmelting(player, handler, 4, 125, 35));
        this.addSlot(new SlotSmelting(player, handler, 5, 125, 53));

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

        // Initialize container data
        for (int i = 0; i < 3; i++) {
            this.data.set(i, furnace.progress[i]);
            this.data.set(i + 3, furnace.bonus[i]);
        }
        this.data.set(6, furnace.heat);

        this.addDataSlots(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            stack = originalStack.copy();

            // If slot is in furnace (0-5)
            if (index <= 5) {
                // Try to move to player inventory (6-41)
                if (!this.moveItemStackTo(originalStack, 6, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, stack);
            }
            // If slot is in player inventory, try to move to input slots (0-2)
            else if (!this.moveItemStackTo(originalStack, 0, 3, false)) {
                return ItemStack.EMPTY;
            }

            if (originalStack.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (originalStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, originalStack);
        }

        return stack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        BlockPos pos = furnace.getBlockPos();
        return player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) <= 64.0;
    }

    // Getters for GUI
    public int getProgress(int index) {
        return data.get(index);
    }

    public int getBonus(int index) {
        return data.get(index + 3);
    }

    public int getHeat() {
        return data.get(6);
    }

    public TileEntityFurnaceSteel getFurnace() {
        return furnace;
    }
}