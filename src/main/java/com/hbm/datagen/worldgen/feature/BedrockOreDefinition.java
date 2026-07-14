package com.hbm.datagen.worldgen.feature;

import com.hbm.inventory.FluidStackHBM;
import net.minecraft.world.item.ItemStack;

public class BedrockOreDefinition {
    public final ItemStack stack;
    public final FluidStackHBM acid;
    public final int tier;
    public final int color;
    public final String id;

    public BedrockOreDefinition(ItemStack stack, int tier, int color) {
        this(stack, tier, color, null);
    }

    public BedrockOreDefinition(ItemStack stack, int tier, int color, FluidStackHBM acid) {
        this.stack = stack;
        this.tier = tier;
        this.color = color;
        this.acid = acid;
        this.id = stack.toString();
    }
}