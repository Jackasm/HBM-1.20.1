package com.hbm.items.food;

import com.hbm.items.ModItems;

import com.hbm.potion.HbmPotion;
import com.hbm.util.ModDamageSource;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAppleSchrabidium extends Item {

    private final int tier;

    public ItemAppleSchrabidium(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, Level level, @NotNull LivingEntity living) {
        if (!level.isClientSide && living instanceof Player player) {
            if (stack.getItem() == ModItems.APPLE_SCHRABIDIUM_NUGGET.get() ||
                    stack.getItem() == ModItems.APPLE_SCHRABIDIUM_INGOT.get() ||
                    stack.getItem() == ModItems.APPLE_SCHRABIDIUM_BLOCK.get()) {
                switch (tier) {
                    case 0 -> {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 600, 4));
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 6000, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 6000, 0));
                    }
                    case 1 -> {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 1200, 4));
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 1200, 4));
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 1200, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 1200, 4));
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 1200, 2));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 1200, 2));
                        player.addEffect(new MobEffectInstance(MobEffects.JUMP, 1200, 4));
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 1200, 9));
                        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 1200, 4));
                        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, 1200, 9));
                    }
                    case 2 -> {
                        player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, Integer.MAX_VALUE, 4));
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, Integer.MAX_VALUE, 1));
                        player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, Integer.MAX_VALUE, 0));
                        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, Integer.MAX_VALUE, 9));
                        player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, Integer.MAX_VALUE, 4));
                        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, Integer.MAX_VALUE, 3));
                        player.addEffect(new MobEffectInstance(MobEffects.JUMP, Integer.MAX_VALUE, 4));
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, Integer.MAX_VALUE, 24));
                        player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, Integer.MAX_VALUE, 14));
                        player.addEffect(new MobEffectInstance(MobEffects.WATER_BREATHING, Integer.MAX_VALUE, 99));
                    }
                }
            }

            if (stack.getItem() == ModItems.APPLE_LEAD_NUGGET.get() ||
                    stack.getItem() == ModItems.APPLE_LEAD_INGOT.get() ||
                    stack.getItem() == ModItems.APPLE_LEAD_BLOCK.get()) {
                switch (tier) {
                    case 0 -> player.addEffect(new MobEffectInstance(HbmPotion.LEAD.get(), 15 * 20, 2));
                    case 1 -> player.addEffect(new MobEffectInstance(HbmPotion.LEAD.get(), 60 * 20, 4));
                    case 2 -> player.hurt(ModDamageSource.causeLeadDamage(player), 500F);
                }
            }
        }
        return super.finishUsingItem(stack, level, living);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        return switch (tier) {
            case 0 -> Rarity.UNCOMMON;
            case 1 -> Rarity.RARE;
            case 2 -> Rarity.EPIC;
            default -> Rarity.COMMON;
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (tier == 2) {
            tooltip.add(Component.translatable("item.hbm.apple_schrabidium.tooltip").withStyle(ChatFormatting.LIGHT_PURPLE));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@NotNull ItemStack stack) {
        return tier == 2;
    }
}