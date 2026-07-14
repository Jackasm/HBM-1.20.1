package com.hbm.items.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.handler.ArmorModHandler;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemModHealth extends ItemArmorMod {

    private static final UUID HEALTH_MODIFIER = UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890");

    private final float health;

    public ItemModHealth(Properties properties, float health) {
        super(ArmorModHandler.EXTRA, false, true, false, false, properties);
        this.health = health;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        String color = System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.RED.getName() : ChatFormatting.LIGHT_PURPLE.getName();
        float healthDisplay = Math.round(health * 10) * 0.1F;

        tooltip.add(Component.literal("+" + healthDisplay + " health")
                .withStyle(Objects.requireNonNull(ChatFormatting.getByName(color))));

        if (this == ModItems.BLACK_DIAMOND.get()) {
            tooltip.add(Component.empty());
            tooltip.add(Component.literal("Nostalgia").withStyle(ChatFormatting.DARK_GRAY));
        }

        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        String color = System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.RED.getName() : ChatFormatting.LIGHT_PURPLE.getName();
        float healthDisplay = Math.round(health * 10) * 0.1F;

        list.add(Component.literal("  " + stack.getHoverName().getString() + " (+" + healthDisplay + " health)")
                .withStyle(Objects.requireNonNull(ChatFormatting.getByName(color))));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot slot, ItemStack armor) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot != null && slot.getType() == EquipmentSlot.Type.ARMOR) {
            multimap.put(Attributes.MAX_HEALTH,
                    new AttributeModifier(HEALTH_MODIFIER, "NTM Armor Mod Health", health, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }
}