package com.hbm.items.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModIron extends ItemArmorMod {

    public ItemModIron(Properties properties) {
        super(ArmorModHandler.CLADDING, true, true, true, true, properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("+0.5 knockback resistance").withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.literal(""));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        list.add(Component.literal("  " + stack.getHoverName().getString() + " (+0.5 knockback resistance)")
                .withStyle(ChatFormatting.WHITE));
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot slot, ItemStack armor) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot != null && armor.getItem() instanceof ArmorItem armorItem) {
            int slotIndex = slot.getIndex();
            multimap.put(Attributes.KNOCKBACK_RESISTANCE,
                    new AttributeModifier(ArmorModHandler.fixedUUIDs[slotIndex],
                            "NTM Armor Mod Knockback", 0.5, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }
}