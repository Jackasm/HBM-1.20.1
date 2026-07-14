package com.hbm.items.armor;

import com.hbm.util.RefStrings;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class MaskOfInfamy extends ArmorItem {

    public MaskOfInfamy(Properties properties) {
        super(ArmorMaterials.IRON, Type.HELMET, properties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return RefStrings.MODID + ":textures/models/armor/mask_of_infamy.png";
    }
}