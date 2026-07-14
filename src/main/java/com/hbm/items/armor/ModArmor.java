package com.hbm.items.armor;

import com.hbm.items.ModArmorItems;
import com.hbm.util.RefStrings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;

public class ModArmor extends ArmorItem {

    public ModArmor(ArmorMaterial material, ArmorItem.Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {

        if(stack.getItem() == ModArmorItems.JACKET.get()) {
            return RefStrings.MODID + ":textures/models/armor/jacket.png";
        }
        if(stack.getItem() == ModArmorItems.JACKET_2.get()) {
            return RefStrings.MODID + ":textures/models/armor/jacket_2.png";
        }

        return null;
    }
}