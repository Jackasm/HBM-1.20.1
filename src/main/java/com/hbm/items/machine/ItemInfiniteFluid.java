package com.hbm.items.machine;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.items.ModItems;
import net.minecraft.world.item.Item;


public class ItemInfiniteFluid extends Item {

    private FluidTypeHBM type;
    private int amount;
    private int chance;

    public ItemInfiniteFluid(Properties properties, FluidTypeHBM type, int amount) {
        this(properties, type, amount, 1);
    }

    public ItemInfiniteFluid(Properties properties, FluidTypeHBM type, int amount, int chance) {
        super(properties);
        this.type = type;
        this.amount = amount;
        this.chance = chance;
    }

    public FluidTypeHBM getType() { return this.type; }
    public int getAmount() { return this.amount; }
    public int getChance() { return this.chance; }
    public boolean allowPressure(int pressure) { return this == ModItems.FLUID_BARREL_INFINITE.get() || pressure == 0; }
}
