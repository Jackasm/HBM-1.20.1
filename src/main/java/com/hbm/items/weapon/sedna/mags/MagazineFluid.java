package com.hbm.items.weapon.sedna.mags;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.GunItem; // или GunItem, если переименовали
import com.hbm.particle.SpentCasing;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;

public class MagazineFluid implements IMagazine<FluidTypeHBM> {

    public static final String KEY_MAG_COUNT = "magcount";
    public static final String KEY_MAG_TYPE = "magtype";
    public static final String KEY_MAG_PREV = "magprev";
    public static final String KEY_MAG_AFTER = "magafter";

    /** A number so the gun tell multiple mags apart */
    public int index;
    /** How much ammo this mag can hold */
    public int capacity;

    public MagazineFluid(int index, int capacity) {
        this.index = index;
        this.capacity = capacity;
    }

    @Override
    public FluidTypeHBM getType(ItemStack stack, Container inventory) {
        int id = this.getMagType(stack, index);
        return Fluids.fromID(id);
    }

    @Override
    public void setType(ItemStack stack, FluidTypeHBM type) {
        this.setMagType(stack, index, Fluids.getID(type));
    }

    @Override
    public int getCapacity(ItemStack stack) {
        return capacity;
    }

    @Override
    public void useUpAmmo(ItemStack stack, Container inventory, int amount) {
        this.setAmount(stack, this.getAmount(stack, inventory) - amount);
    }

    @Override
    public int getAmount(ItemStack stack, Container inventory) {
        return getMagCount(stack, index);
    }

    @Override
    public void setAmount(ItemStack stack, int amount) {
        setMagCount(stack, index, amount);
    }

    @Override
    public boolean canReload(ItemStack stack, Container inventory) {
        return false;
    }

    @Override
    public void initNewType(ItemStack stack, Container inventory) {
        // Инициализация нового типа жидкости
    }

    @Override
    public void reloadAction(ItemStack stack, Container inventory) {
        // Действие при перезарядке
    }

    @Override
    public SpentCasing getCasing(ItemStack stack, Container inventory) {
        return null; // Для жидкостного оружия нет гильз
    }

    @Override
    public ItemStack getIconForHUD(ItemStack stack, Player player) {
        return new ItemStack(ModItems.FLUID_ICON.get(), 1); // Предполагается, что ModItems.FLUID_ICON зарегистрирован
    }

    @Override
    public String reportAmmoStateForHUD(ItemStack stack, Player player) {
        return getAmount(stack, player.getInventory()) + "mB";
    }

    @Override
    public void setAmountBeforeReload(ItemStack stack, int amount) {
        GunItem.setInt(stack, KEY_MAG_PREV + index, amount);
    }

    @Override
    public int getAmountBeforeReload(ItemStack stack) {
        return GunItem.getInt(stack, KEY_MAG_PREV + index);
    }

    @Override
    public void setAmountAfterReload(ItemStack stack, int amount) {
        GunItem.setInt(stack, KEY_MAG_AFTER + index, amount);
    }

    @Override
    public int getAmountAfterReload(ItemStack stack) {
        return GunItem.getInt(stack, KEY_MAG_AFTER + index);
    }

    public static int getMagType(ItemStack stack, int index) {
        return GunItem.getInt(stack, KEY_MAG_TYPE + index);
    }

    public static void setMagType(ItemStack stack, int index, int value) {
        GunItem.setInt(stack, KEY_MAG_TYPE + index, value);
    }

    public static int getMagCount(ItemStack stack, int index) {
        return GunItem.getInt(stack, KEY_MAG_COUNT + index);
    }

    public static void setMagCount(ItemStack stack, int index, int value) {
        GunItem.setInt(stack, KEY_MAG_COUNT + index, value);
    }
}