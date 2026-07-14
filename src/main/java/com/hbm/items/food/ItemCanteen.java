package com.hbm.items.food;

import com.hbm.config.VersatileConfig;
import com.hbm.items.ModItems;
import com.hbm.main.MainRegistry;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemCanteen extends Item {

    public ItemCanteen(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (stack.getDamageValue() > 0 && entity.tickCount % 20 == 0) {
            stack.setDamageValue(stack.getDamageValue() - 1);
        }
    }

    @Override
    public @NotNull ItemStack finishUsingItem(ItemStack stack, @NotNull Level level, @NotNull LivingEntity living) {
        stack.setDamageValue(stack.getMaxDamage());

        if (living instanceof Player player) {
            if (stack.getItem() == ModItems.CANTEEN_VODKA.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10 * 20, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 2));
            }
            VersatileConfig.applyPotionSickness(player, 5);
        }

        return super.finishUsingItem(stack, level, living);
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 10;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (stack.getDamageValue() == 0 && !VersatileConfig.hasPotionSickness(player)) {
            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }

        return InteractionResultHolder.fail(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.CANTEEN_VODKA.get()) {
            tooltip.add(Component.literal("Cooldown: 3 minutes"));
            tooltip.add(Component.literal("Nausea I for 10 seconds"));
            tooltip.add(Component.literal("Strength III for 30 seconds"));
            tooltip.add(Component.empty());

            if (MainRegistry.polaroidID == 11) {
                tooltip.add(Component.literal("Time to get hammered & sickled!"));
            } else {
                tooltip.add(Component.literal("Smells like disinfectant, tastes like disinfectant."));
            }
        }
    }
}