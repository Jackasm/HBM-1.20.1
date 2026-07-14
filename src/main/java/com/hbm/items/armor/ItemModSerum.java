package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemModSerum extends ItemArmorMod {

    public ItemModSerum(Properties properties) {
        super(ArmorModHandler.EXTRA, true, true, true, true, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Cures poison and gives strength").withStyle(ChatFormatting.GREEN));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        list.add(Component.literal("  " + stack.getHoverName().getString() + " (replaces poison with strength)")
                .withStyle(ChatFormatting.BLUE));
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        if (!entity.level().isClientSide && entity.hasEffect(MobEffects.POISON)) {
            entity.removeEffect(MobEffects.POISON);
            entity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 100, 4));
        }
    }
}