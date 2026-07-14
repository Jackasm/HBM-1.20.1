package com.hbm.items.machine;

public class ItemTurretChip extends ItemTurretBiometry {

    public ItemTurretChip(Properties properties) {
        super(properties);
    }

    public static Properties createProperties() {
        return new Properties()
                .stacksTo(1);
    }
}