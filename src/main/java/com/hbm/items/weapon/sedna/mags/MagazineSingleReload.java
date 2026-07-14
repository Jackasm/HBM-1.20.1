package com.hbm.items.weapon.sedna.mags;

import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

/** Uses individual bullets which are loaded one by one */
public class MagazineSingleReload extends MagazineSingleTypeBase {

    public MagazineSingleReload(int index, int capacity) {
        super(index, capacity);
    }

    /** Reloads all rounds at once. If the mag is empty, the mag's type will change to the first valid ammo type */
    @Override
    public void reloadAction(ItemStack stack, Container inventory) {
        standardReload(stack, inventory, 1);
    }

    // Альтернативный вариант если нужно работать с инвентарем игрока
    public void reloadAction(ItemStack stack, Player player) {
        standardReload(stack, player.getInventory(), 1);
    }

    // Или если нужно использовать Inventory напрямую
    public void reloadAction(ItemStack stack, Inventory inventory) {
        standardReload(stack, inventory, 1);
    }
}