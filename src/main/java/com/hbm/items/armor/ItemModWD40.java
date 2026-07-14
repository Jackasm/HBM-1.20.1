package com.hbm.items.armor;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.handler.ArmorModHandler;
import com.hbm.main.MainRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ItemModWD40 extends ItemArmorMod {

    private static final UUID HEALTH_MODIFIER = UUID.fromString("c3d4e5f6-a7b8-90c1-d2e3-f4a5b6c7d8e9");

    public ItemModWD40(Properties properties) {
        super(ArmorModHandler.EXTRA, true, true, true, true, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        String color = System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.BLUE.getName() : ChatFormatting.YELLOW.getName();

        tooltip.add(Component.literal("Highly reduces damage taken by armor, +2 HP")
                .withStyle(Objects.requireNonNull(ChatFormatting.getByName(color))));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        String color = System.currentTimeMillis() % 1000 < 500 ? ChatFormatting.BLUE.getName() : ChatFormatting.YELLOW.getName();

        list.add(Component.literal("  " + stack.getHoverName().getString() + " (-80% armor wear / +2 HP)")
                .withStyle(Objects.requireNonNull(ChatFormatting.getByName(color))));
    }

    @Override
    public void modDamage(LivingHurtEvent event, ItemStack armor) {
        if (!event.getEntity().level().isClientSide && armor.getDamageValue() > 0 && event.getEntity().getRandom().nextInt(5) != 0) {
            armor.setDamageValue(armor.getDamageValue() - 1);
        }
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getModifiers(EquipmentSlot slot, ItemStack armor) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot != null && slot.getType() == EquipmentSlot.Type.ARMOR) {
            multimap.put(Attributes.MAX_HEALTH,
                    new AttributeModifier(HEALTH_MODIFIER, "NTM Armor Mod Health", 4, AttributeModifier.Operation.ADDITION));
        }

        return multimap;
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (entity.level().isClientSide && entity.hurtTime > 0) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "vanillaExt");
            data.putString("mode", "reddust");
            data.putDouble("posX", entity.getX() + (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth() * 2);
            data.putDouble("posY", entity.getY() + entity.getRandom().nextDouble() * entity.getBbHeight());
            data.putDouble("posZ", entity.getZ() + (entity.getRandom().nextDouble() - 0.5) * entity.getBbWidth() * 2);
            data.putDouble("mX", 0.01);
            data.putDouble("mY", 0.5);
            data.putDouble("mZ", 0.8);
            MainRegistry.proxy.effectNT(data);
        }
    }
}