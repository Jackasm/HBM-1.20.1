package com.hbm.inventory;

import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FluidContainer {

    // The full container (e.g. deuterium cell)
    @NotNull
    public ItemStack fullContainer;

    // The empty container (e.g. empty cell), can be null for blocks
    @Nullable
    public ItemStack emptyContainer;

    // The type of the contained liquid (e.g. deuterium)
    @NotNull
    public FluidTypeHBM type;

    // The amount of liquid stored in mB (e.g. 1000)
    public int content;

    public FluidContainer(@NotNull ItemStack full, @Nullable ItemStack empty, @NotNull FluidTypeHBM type, int amount) {
        this.fullContainer = full;
        this.emptyContainer = empty;
        this.type = type;
        this.content = amount;
    }

    /**
     * Creates a copy of this fluid container
     */
    @NotNull
    public FluidContainer copy() {
        ItemStack fullCopy = fullContainer.copy();
        ItemStack emptyCopy = emptyContainer != null ? emptyContainer.copy() : null;
        return new FluidContainer(fullCopy, emptyCopy, type, content);
    }
}