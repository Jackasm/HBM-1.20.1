package com.hbm.items.food;

import com.hbm.items.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class ItemMarshmallow extends Item {

    private final int type;

    public ItemMarshmallow(Properties properties, int type) {
        super(properties);
        this.type = type;
    }

    public static ItemStack createRoasted() {
        return new ItemStack(ModItems.MARSHMALLOW_ROASTED.get());
    }

    public static ItemStack createNormal() {
        return new ItemStack(ModItems.MARSHMALLOW_NORMAL.get());
    }
}