package com.hbm.items.ammo;

import com.hbm.items.weapon.sedna.factory.GunFactory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemAmmo extends Item {
    private final GunFactory.EnumAmmo ammoType;

    public ItemAmmo(GunFactory.EnumAmmo ammoType, Properties properties) {
        super(properties);
        this.ammoType = ammoType;
    }

    public GunFactory.EnumAmmo getAmmoType() {
        return ammoType;
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        // Локализованное имя боеприпаса
        return "item.ammo" + "_standard." + ammoType.name().toLowerCase() + ".name";
    }
}