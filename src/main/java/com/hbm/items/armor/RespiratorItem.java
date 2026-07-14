package com.hbm.items.armor;

import com.hbm.util.ArmorRegistry;
import net.minecraft.world.item.Item;

public class RespiratorItem extends Item {

    private final ArmorRegistry.HazardClass[] protections;

    public RespiratorItem(Properties properties, ArmorRegistry.HazardClass... protections) {
        super(properties);
        this.protections = protections;
    }

    public boolean protectsFrom(ArmorRegistry.HazardClass hazard) {
        for (ArmorRegistry.HazardClass protection : protections) {
            if (protection == hazard) {
                return true;
            }
        }
        return false;
    }
}