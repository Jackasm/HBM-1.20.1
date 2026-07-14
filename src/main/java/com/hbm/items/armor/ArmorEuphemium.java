package com.hbm.items.armor;

import com.hbm.items.ModArmorItems;

import com.hbm.util.ArmorUtil;
import com.hbm.util.RefStrings;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ArmorEuphemium extends ArmorItem {

    public ArmorEuphemium(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (stack.getItem() == ModArmorItems.EUPHEMIUM_HELMET.get() ||
                stack.getItem() == ModArmorItems.EUPHEMIUM_CHESTPLATE.get() ||
                stack.getItem() == ModArmorItems.EUPHEMIUM_BOOTS.get()) {
            return RefStrings.MODID + ":textures/models/armor/euphemium_layer_1.png";
        }
        if (stack.getItem() == ModArmorItems.EUPHEMIUM_LEGGINGS.get()) {
            return RefStrings.MODID + ":textures/models/armor/euphemium_layer_2.png";
        }
        return null;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if (ArmorUtil.checkArmor(player,
                ModArmorItems.EUPHEMIUM_HELMET.get(),
                ModArmorItems.EUPHEMIUM_CHESTPLATE.get(),
                ModArmorItems.EUPHEMIUM_LEGGINGS.get(),
                ModArmorItems.EUPHEMIUM_BOOTS.get())) {

            player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 20, 127, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 20, 127, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 20, 127, true, false, true));
            player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 20, 127, true, false, true));

            if (player.getDeltaMovement().y < -0.25D) {
                player.setDeltaMovement(player.getDeltaMovement().x, -0.25D, player.getDeltaMovement().z);
                player.fallDistance = 0;
            }
        }
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return Rarity.EPIC;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        // do literally nothing
    }
}