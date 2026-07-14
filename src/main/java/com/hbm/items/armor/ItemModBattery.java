package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;

public class ItemModBattery extends ItemArmorMod {

    public double mod;

    public ItemModBattery(double mod) {
        super(ArmorModHandler.BATTERY, true, true, true, true, new Properties());
        this.mod = mod;
    }
}
