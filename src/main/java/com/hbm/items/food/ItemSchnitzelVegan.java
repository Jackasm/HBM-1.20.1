package com.hbm.items.food;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemSchnitzelVegan extends Item {

    public ItemSchnitzelVegan(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity living) {
        if (!level.isClientSide && living instanceof Player player) {
            player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 10 * 20, 0));
            player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 30 * 20, 0));
            player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 3 * 60 * 20, 4));
            player.addEffect(new MobEffectInstance(MobEffects.WITHER, 3 * 20, 0));
            player.setSecondsOnFire(5);
            player.setDeltaMovement(player.getDeltaMovement().x, 2, player.getDeltaMovement().z);
        }
        return super.finishUsingItem(stack, level, living);
    }
}

