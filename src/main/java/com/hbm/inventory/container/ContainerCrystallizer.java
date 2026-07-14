package com.hbm.inventory.container;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.inventory.SlotCraftingOutput;
import com.hbm.inventory.SlotUpgrade;
import com.hbm.items.ModItems;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.tileentity.machine.TileEntityMachineCrystallizer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerCrystallizer extends AbstractContainerMenu {

    private final TileEntityMachineCrystallizer crystallizer;
    private final IItemHandler handler;

    public ContainerCrystallizer(int windowId, Inventory playerInv, TileEntityMachineCrystallizer tile) {
        super(ModContainers.CRYSTALLIZER.get(), windowId);
        this.crystallizer = tile;
        this.handler = tile.getInventory();

        // Input (0)
        this.addSlot(new SlotItemHandler(handler, 0, 62, 45));
        // Battery (1)
        this.addSlot(new SlotItemHandler(handler, 1, 152, 72));
        // Output (2)
        this.addSlot(new SlotCraftingOutput(playerInv.player, handler, 2, 113, 45));
        // Fluid input (3)
        this.addSlot(new SlotItemHandler(handler, 3, 17, 18));
        // Fluid output (4)
        this.addSlot(new SlotCraftingOutput(playerInv.player, handler, 4, 17, 54));
        // Upgrades (5-6)
        this.addSlot(new SlotUpgrade(handler, 5, 80, 18));
        this.addSlot(new SlotUpgrade(handler, 6, 98, 18));
        // Fluid ID (7)
        this.addSlot(new SlotItemHandler(handler, 7, 35, 72));

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, 8 + j * 18, 122 + i * 18));
            }
        }

        // Player hotbar
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(playerInv, i, 8 + i * 18, 180));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack rStack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);

        if (slot.hasItem()) {
            ItemStack stack = slot.getItem();
            rStack = stack.copy();

            if (index <= 7) {
                if (!this.moveItemStackTo(stack, 8, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Battery
                if (stack.getItem() instanceof IBatteryItem || stack.getItem() == ModItems.BATTERY_CREATIVE.get()) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
                }
                // Fluid identifier
                else if (stack.getItem() instanceof IItemFluidIdentifier) {
                    if (!this.moveItemStackTo(stack, 7, 8, false)) return ItemStack.EMPTY;
                }
                // Upgrade
                else if (stack.getItem() instanceof ItemMachineUpgrade) {
                    if (!this.moveItemStackTo(stack, 5, 7, false)) return ItemStack.EMPTY;
                }
                // Input
                else {
                    if (!this.moveItemStackTo(stack, 0, 1, false)) return ItemStack.EMPTY;
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
        return crystallizer.isUsableByPlayer(player);
    }

    public TileEntityMachineCrystallizer getCrystallizer() {
        return crystallizer;
    }
}