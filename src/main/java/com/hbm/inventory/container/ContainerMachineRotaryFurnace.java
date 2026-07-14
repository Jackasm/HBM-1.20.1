package com.hbm.inventory.container;

import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.tileentity.machine.TileEntityMachineRotaryFurnace;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerMachineRotaryFurnace extends AbstractContainerMenu {

    private final TileEntityMachineRotaryFurnace furnace;

    public ContainerMachineRotaryFurnace(int windowId, Inventory playerInv, TileEntityMachineRotaryFurnace tile) {
        super(ModContainers.ROTARY_FURNACE.get(), windowId);
        this.furnace = tile;

        // Inputs
        this.addSlot(new SlotItemHandler(tile.getItemHandler(), 0, 8, 18));
        this.addSlot(new SlotItemHandler(tile.getItemHandler(), 1, 26, 18));
        this.addSlot(new SlotItemHandler(tile.getItemHandler(), 2, 44, 18));
        // Fluid ID
        this.addSlot(new SlotItemHandler(tile.getItemHandler(), 3, 8, 54));
        // Solid fuel
        this.addSlot(new SlotItemHandler(tile.getItemHandler(), 4, 44, 54));

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 104 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 162));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack rStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            rStack = stack.copy();

            if (index <= 4) {
                if (!this.moveItemStackTo(stack, 5, this.slots.size(), true)) return ItemStack.EMPTY;
            } else {
                if (stack.getBurnTime(RecipeType.SMELTING) > 0) {
                    if (!this.moveItemStackTo(stack, 4, 5, false)) return ItemStack.EMPTY;
                } else if (stack.getItem() instanceof IItemFluidIdentifier) {
                    if (!this.moveItemStackTo(stack, 3, 4, false)) return ItemStack.EMPTY;
                } else {
                    if (!this.moveItemStackTo(stack, 0, 3, false)) return ItemStack.EMPTY;
                }
            }

            if (stack.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }

        return rStack;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return furnace.isUsableByPlayer(player);
    }

    public TileEntityMachineRotaryFurnace getFurnace() {
        return furnace;
    }
}