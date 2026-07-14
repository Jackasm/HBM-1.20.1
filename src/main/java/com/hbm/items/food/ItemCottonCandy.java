package com.hbm.items.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemCottonCandy extends Item {

    public ItemCottonCandy(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity living) {
        if (!level.isClientSide && living instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.POISON, 15 * 20, 0));
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 5 * 20, 0));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 25 * 20, 2));
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 25 * 20, 2));
            player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 4));
        }
        return super.finishUsingItem(stack, level, living);
    }
}
