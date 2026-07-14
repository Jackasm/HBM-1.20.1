package com.hbm.items.armor;

import com.google.common.collect.Multimap;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

import java.util.List;

public class ItemArmorMod extends Item {

    public int type;
    public boolean helmet;
    public boolean chestplate;
    public boolean leggings;
    public boolean boots;

    public ItemArmorMod(int type, boolean helmet, boolean chestplate,
                        boolean leggings, boolean boots, Properties properties) {
        super(properties);
        this.type = type;
        this.helmet = helmet;
        this.chestplate = chestplate;
        this.leggings = leggings;
        this.boots = boots;
    }

    // Методы для совместимости
    public boolean canApplyToArmor(ItemStack armor) {
        if(armor.getItem() instanceof ArmorItem armorItem) {
            EquipmentSlot slot = armorItem.getEquipmentSlot();
            return (slot == EquipmentSlot.HEAD && helmet) ||
                    (slot == EquipmentSlot.CHEST && chestplate) ||
                    (slot == EquipmentSlot.LEGS && leggings) ||
                    (slot == EquipmentSlot.FEET && boots);
        }
        return false;
    }

    public void addDesc(List<Component> list, ItemStack modStack, ItemStack armorStack) {
        // Базовая реализация (пустая или с дефолтной логикой)
    }

    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot slot, ItemStack armor) {
        return null;
    }

    public void modUpdate(LivingEntity entity, ItemStack armor) { }

    public void modDamage(LivingHurtEvent event, ItemStack armor) { }
}