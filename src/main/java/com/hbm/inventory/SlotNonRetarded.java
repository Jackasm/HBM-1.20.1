package com.hbm.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

/**
 * Because vanilla slots have shit idiot brain fungus that prevent them from working as expected.
 * @author hbm
 */

public class SlotNonRetarded extends Slot {

    public SlotNonRetarded(Container inventory, int id, int x, int y) {
        super(inventory, id, x, y);
    }

    /**
     * Dear mojang: Why wasn't that the standard to begin with? What do IInventories have isItemValidForSlot for when by default nothing fucking uses it?
     */
    @Override
    public boolean mayPlace(ItemStack stack) {
        return container.canPlaceItem(this.getSlotIndex(), stack);
    }

    /**
     * Because if slots have higher stacksizes than the maximum allowed by the tile, the display just stops working.
     * Why was that necessary? Sure it's not intended but falsifying information isn't very cool.
     */
    @Override
    public int getMaxStackSize() {
        return Math.max(this.container.getMaxStackSize(), this.hasItem() ? this.getItem().getCount() : 1);
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return this.getMaxStackSize();
    }
}