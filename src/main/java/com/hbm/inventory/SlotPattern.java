package com.hbm.inventory;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class SlotPattern extends Slot {

    protected boolean canHover = true;
    protected boolean allowStackSize = false;

    public SlotPattern(Container inv, int index, int x, int y) {
        super(inv, index, x, y);
    }

    public SlotPattern(Container inv, int index, int x, int y, boolean allowStackSize) {
        super(inv, index, x, y);
        this.allowStackSize = allowStackSize;
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        if (stack != null && !stack.isEmpty()) {
            stack = stack.copy();
            if (!allowStackSize) {
                stack.setCount(1);
            }
        }
        super.set(stack);
    }

    public SlotPattern disableHover() {
        this.canHover = false;
        return this;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isActive() {
        return canHover;
    }
}