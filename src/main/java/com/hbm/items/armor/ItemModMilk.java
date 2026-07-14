package com.hbm.items.armor;

import com.hbm.handler.ArmorModHandler;
import com.hbm.potion.HbmPotion;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ItemModMilk extends ItemArmorMod {

    public ItemModMilk(Properties properties) {
        super(ArmorModHandler.EXTRA, true, true, true, true, properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Removes bad potion effects").withStyle(ChatFormatting.WHITE));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public void addDesc(List<Component> list, ItemStack stack, ItemStack armor) {
        list.add(Component.literal("  " + stack.getHoverName().getString() + " (Removes bad potion effects)")
                .withStyle(ChatFormatting.WHITE));
    }

    @Override
    public void modUpdate(LivingEntity entity, ItemStack armor) {
        List<MobEffect> effectsToRemove = new ArrayList<>();

        for (MobEffectInstance effect : entity.getActiveEffects()) {
            MobEffect potion = effect.getEffect();
            if (HbmPotion.isBadEffect(potion)) {
                effectsToRemove.add(potion);
            }
        }

        for (MobEffect effect : effectsToRemove) {
            entity.removeEffect(effect);
        }
    }
}