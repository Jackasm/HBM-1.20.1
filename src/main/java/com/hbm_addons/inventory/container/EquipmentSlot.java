package com.hbm_addons.inventory.container;

import com.hbm_addons.entity.mob.EntityEncounterVillager;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Slot-обёртка над экипировкой моба.
 * Читает/пишет напрямую в EntityEncounterVillager через getItemBySlot/setItemSlot.
 */
public class EquipmentSlot extends Slot {

    /** Порядок слотов экипировки: 0..5 соответствует позициям в сетке GUI. */
    public static final net.minecraft.world.entity.EquipmentSlot[] ORDER = {
            net.minecraft.world.entity.EquipmentSlot.HEAD,
            net.minecraft.world.entity.EquipmentSlot.CHEST,
            net.minecraft.world.entity.EquipmentSlot.LEGS,
            net.minecraft.world.entity.EquipmentSlot.FEET,
            net.minecraft.world.entity.EquipmentSlot.MAINHAND,
            net.minecraft.world.entity.EquipmentSlot.OFFHAND
    };

    private final EntityEncounterVillager mob;
    private final net.minecraft.world.entity.EquipmentSlot equipSlot;

    public EquipmentSlot(EntityEncounterVillager mob,
                         net.minecraft.world.entity.EquipmentSlot equipSlot,
                         int x, int y) {
        // Пустой контейнер-заглушка, реальные данные — в mob
        super(new SimpleContainer(1), 0, x, y);
        this.mob = mob;
        this.equipSlot = equipSlot;
    }

    @Override
    public @NotNull ItemStack getItem() {
        return mob.getItemBySlot(equipSlot);
    }

    @Override
    public void set(@NotNull ItemStack stack) {
        mob.setItemSlot(equipSlot, stack);
        this.setChanged();
    }

    @Override
    public void setChanged() {
        // Экипировка живёт в мобе, отдельного контейнера нет.
    }

    @Override
    public @NotNull ItemStack remove(int amount) {
        ItemStack current = mob.getItemBySlot(equipSlot);
        if (current.isEmpty()) return ItemStack.EMPTY;
        ItemStack split = current.split(amount);
        mob.setItemSlot(equipSlot, current);
        return split;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        // Разрешаем только подходящий тип для слота брони
        if (equipSlot.getType() == Type.ARMOR) {
            if (!(stack.getItem() instanceof ArmorItem armorItem)) return false;
            return armorItem.getEquipmentSlot() == equipSlot;
        }
        return true; // MAINHAND / OFFHAND — что угодно
    }

    @Override
    public boolean mayPickup(@NotNull Player player) {
        return true;
    }

    @Override
    public int getMaxStackSize() {
        return 1; // экипировка всегда 1 предмет
    }

    public net.minecraft.world.entity.EquipmentSlot getEquipSlot() {
        return equipSlot;
    }
}