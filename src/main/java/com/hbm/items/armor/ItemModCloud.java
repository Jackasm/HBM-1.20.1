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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

public class ItemModCloud extends ItemArmorMod implements IArmorModDash {

    private static final UUID SPEED_UUID = UUID.fromString("1d11e63e-28c4-4e14-b09f-fe0bd1be708f");

    public ItemModCloud(Properties properties) {
        super(ArmorModHandler.PLATE_ONLY, false, true, false, false, properties);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot slot, ItemStack armor) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot.getType() == EquipmentSlot.Type.ARMOR) {
            multimap.put(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(SPEED_UUID, "CLOUD SPEED", 0.125, AttributeModifier.Operation.MULTIPLY_BASE));
        }

        return multimap;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> list, @NotNull TooltipFlag flag) {
        list.add(Component.translatable("item.mod_cloud.desc").withStyle(ChatFormatting.WHITE));
        list.add(Component.empty());
        super.appendHoverText(stack, level, list, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        list.add(Component.literal("  ").append(stack.getHoverName())
                .append(Component.translatable("item.mod_cloud.desc_short"))
                .withStyle(ChatFormatting.RED));
    }

    @Override
    public int getDashes() {
        return 3;
    }

    // Альтернативный метод для получения модификаторов через getAttributeModifiers
    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        // Этот метод вызывается, когда предмет находится в слоте брони
        if (slot == EquipmentSlot.CHEST) {
            Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();
            multimap.put(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(SPEED_UUID, "CLOUD SPEED", 0.125, AttributeModifier.Operation.MULTIPLY_BASE));
            return multimap;
        }
        return super.getAttributeModifiers(slot, stack);
    }
}