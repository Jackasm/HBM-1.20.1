package com.hbm.items.machine;

import com.hbm.items.ItemEnumMulti;
import net.minecraft.world.item.Item;

public class ItemRTGPelletDepleted extends ItemEnumMulti<ItemRTGPelletDepleted.DepletedRTGMaterial> {

    public ItemRTGPelletDepleted(Properties properties) {
        super(properties, DepletedRTGMaterial.class, true);
    }

    public enum DepletedRTGMaterial {
        BISMUTH,
        MERCURY,
        NEPTUNIUM,
        LEAD,
        ZIRCONIUM
    }
}