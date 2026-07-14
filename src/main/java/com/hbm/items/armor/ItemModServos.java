package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.UUID;

public class ItemModServos extends ItemArmorMod {

    private static final UUID SERVOS_CHEST_UUID = UUID.fromString("8a2f3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");
    private static final UUID SERVOS_LEGS_UUID = UUID.fromString("9a2f3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");

    private final boolean isDesh;

    public ItemModServos(Properties properties, boolean isDesh) {
        super(ArmorModHandler.SERVOS, false, true, true, false, properties);
        this.isDesh = isDesh;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (!isDesh) {
            tooltip.add(Component.literal(ChatFormatting.DARK_PURPLE + "Chestplate: Haste I / Damage +50%"));
            tooltip.add(Component.literal(ChatFormatting.DARK_PURPLE + "Leggings: Speed +25% / Jump II"));
        } else {
            tooltip.add(Component.literal(ChatFormatting.DARK_PURPLE + "Chestplate: Haste III / Damage +150%"));
            tooltip.add(Component.literal(ChatFormatting.DARK_PURPLE + "Leggings: Speed +50% / Jump III"));
        }
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (armor.getItem() instanceof ArmorItem armorItem) {
            EquipmentSlot slot = armorItem.getEquipmentSlot();

            if (slot == EquipmentSlot.CHEST) {
                if (!isDesh) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 60, 0));
                } else {
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 60, 2));
                }
            }

            if (slot == EquipmentSlot.LEGS) {
                if (!isDesh) {
                    entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 1));
                } else {
                    entity.addEffect(new MobEffectInstance(MobEffects.JUMP, 60, 2));
                }
            }
        }
    }

    public AttributeModifier getDamageModifier(EquipmentSlot slot) {
        if (slot != EquipmentSlot.CHEST) return null;

        double amount = isDesh ? 1.5 : 0.5;

        return new AttributeModifier(SERVOS_CHEST_UUID, "NTM Armor Mod Servos Damage", amount, AttributeModifier.Operation.MULTIPLY_BASE);
    }

    public AttributeModifier getSpeedModifier(EquipmentSlot slot) {
        if (slot != EquipmentSlot.LEGS) return null;

        double amount = isDesh ? 0.5 : 0.25;

        return new AttributeModifier(SERVOS_LEGS_UUID, "NTM Armor Mod Servos Speed", amount, AttributeModifier.Operation.MULTIPLY_BASE);
    }
}