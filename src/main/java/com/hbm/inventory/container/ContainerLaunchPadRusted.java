package com.hbm.inventory.container;

import com.hbm.api.item.IDesignatorItem;
import com.hbm.items.ModItems;
import com.hbm.tileentity.bomb.TileEntityLaunchPadRusted;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class ContainerLaunchPadRusted extends AbstractContainerMenu {

    private final TileEntityLaunchPadRusted launchpad;
    private final IItemHandler handler;

    public ContainerLaunchPadRusted(int windowId, Inventory invPlayer, TileEntityLaunchPadRusted launchpad) {
        super(ModContainers.LAUNCH_PAD_RUSTED.get(), windowId);
        this.launchpad = launchpad;
        this.handler = launchpad.getInventory();

        // Slot 0: Output (missile)
        this.addSlot(new SlotItemHandler(handler, 0, 26, 72) {
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return false;
            }
        });
        // Slot 1: Launch Code
        this.addSlot(new SlotItemHandler(handler, 1, 116, 45));
        // Slot 2: Launch Key
        this.addSlot(new SlotItemHandler(handler, 2, 134, 45));
        // Slot 3: Designator
        this.addSlot(new SlotItemHandler(handler, 3, 26, 99));

        // Player inventory
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                this.addSlot(new Slot(invPlayer, j + i * 9 + 9, 8 + j * 18, 154 + i * 18));
            }
        }
        for (int i = 0; i < 9; i++) {
            this.addSlot(new Slot(invPlayer, i, 8 + i * 18, 212));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot != null && slot.hasItem()) {
            ItemStack stack = slot.getItem();
            result = stack.copy();
            if (index <= 3) {
                if (!this.moveItemStackTo(stack, 4, this.slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else {
                if (stack.getItem() instanceof IDesignatorItem) {
                    if (!this.moveItemStackTo(stack, 3, 4, false)) return ItemStack.EMPTY;
                } else if (stack.getItem() == ModItems.LAUNCH_CODE.get()) {
                    if (!this.moveItemStackTo(stack, 1, 2, false)) return ItemStack.EMPTY;
                } else if (stack.getItem() == ModItems.LAUNCH_KEY.get()) {
                    if (!this.moveItemStackTo(stack, 2, 3, false)) return ItemStack.EMPTY;
                } else {
                    return ItemStack.EMPTY;
                }
            }
            if (stack.isEmpty()) slot.set(ItemStack.EMPTY);
            else slot.setChanged();
        }
        return result;
    }

    public TileEntityLaunchPadRusted getLaunchpad() {
        return launchpad;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return launchpad.isUsableByPlayer(player);
    }
}