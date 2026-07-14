package com.hbm.inventory.container;

import com.hbm.inventory.SlotSmelting;
import com.hbm.inventory.SlotUpgrade;
import com.hbm.tileentity.machine.TileEntityFurnaceIron;
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

public class ContainerFurnaceIron extends AbstractContainerMenu {

    protected TileEntityFurnaceIron furnace;
    private final ContainerData data;

    public ContainerFurnaceIron(int windowId, Inventory invPlayer, Player player, TileEntityFurnaceIron furnace) {
        super(ModContainers.FURNACE_IRON.get(), windowId);
        this.furnace = furnace;
        this.data = new SimpleContainerData(4); // progress, processingTime, maxBurnTime, burnTime

        IItemHandler handler = furnace.getItemHandler();

        // Input slot
        this.addSlot(new SlotItemHandler(handler, 0, 53, 17));
        // Fuel slots
        this.addSlot(new SlotItemHandler(handler, 1, 53, 53));
        this.addSlot(new SlotItemHandler(handler, 2, 71, 53));
        // Output slot
        this.addSlot(new SlotSmelting(player, handler, 3, 125, 35));
        // Upgrade slot
        this.addSlot(new SlotUpgrade(handler, 4, 17, 35));

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

        // Initialize data
        this.data.set(0, furnace.progress);
        this.data.set(1, furnace.processingTime);
        this.data.set(2, furnace.maxBurnTime);
        this.data.set(3, furnace.burnTime);

        this.addDataSlots(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack stack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot != null && slot.hasItem()) {
            ItemStack originalStack = slot.getItem();
            stack = originalStack.copy();

            if (index <= 4) {
                if (!this.moveItemStackTo(originalStack, 5, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
                slot.onQuickCraft(originalStack, stack);
            } else if (!this.moveItemStackTo(originalStack, 0, 5, false)) {
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
    public int getProgress() {
        return data.get(0);
    }

    public int getProcessingTime() {
        return data.get(1);
    }

    public int getMaxBurnTime() {
        return data.get(2);
    }

    public int getBurnTime() {
        return data.get(3);
    }

    public TileEntityFurnaceIron getFurnace() {
        return furnace;
    }
}