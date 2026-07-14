package com.hbm.items.food;

import com.hbm.explosion.ExplosionNukeSmall;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemWaffle extends Item {

    public ItemWaffle(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity living) {
        if (!level.isClientSide && living instanceof Player) {
            ExplosionNukeSmall.explode(level, living.getX(), living.getY() + 0.5, living.getZ(), ExplosionNukeSmall.PARAMS_MEDIUM);
        }
        return super.finishUsingItem(stack, level, living);
    }
}