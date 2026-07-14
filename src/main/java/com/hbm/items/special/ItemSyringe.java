package com.hbm.items.special;

import com.hbm.api.fluid.IFillableItem;
import com.hbm.config.VersatileConfig;
import com.hbm.handler.ArmorModHandler;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;
import com.hbm.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ItemSyringe extends Item {

    private final Random rand = new Random();

    public ItemSyringe(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (this == ModItems.SYRINGE_METAL_STIMPAK.get() && !VersatileConfig.hasPotionSickness(player)) {
            if (!level.isClientSide) {
                player.heal(5);

                stack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SYRINGE.get(), player.getSoundSource(), 1.0F, 1.0F);

                if (stack.isEmpty()) {
                    return InteractionResultHolder.success(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()));
                }

                if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                    player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
                }

                VersatileConfig.applyPotionSickness(player, 5);
            }
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.SYRINGE_METAL_MEDX.get() && !VersatileConfig.hasPotionSickness(player)) {
            if (!level.isClientSide) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4 * 60 * 20, 2));

                stack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SYRINGE.get(), player.getSoundSource(), 1.0F, 1.0F);

                if (stack.isEmpty()) {
                    return InteractionResultHolder.success(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()));
                }

                if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                    player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
                }

                VersatileConfig.applyPotionSickness(player, 5);
            }
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.SYRINGE_METAL_PSYCHO.get() && !VersatileConfig.hasPotionSickness(player)) {
            if (!level.isClientSide) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2 * 60 * 20, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2 * 60 * 20, 0));

                stack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SYRINGE.get(), player.getSoundSource(), 1.0F, 1.0F);

                if (stack.isEmpty()) {
                    return InteractionResultHolder.success(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()));
                }

                if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                    player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
                }

                VersatileConfig.applyPotionSickness(player, 5);
            }
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.SYRINGE_METAL_SUPER.get() && !VersatileConfig.hasPotionSickness(player)) {
            if (!level.isClientSide) {
                player.heal(25);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 0));

                stack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SYRINGE.get(), player.getSoundSource(), 1.0F, 1.0F);

                if (stack.isEmpty()) {
                    return InteractionResultHolder.success(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()));
                }

                if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                    player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
                }

                VersatileConfig.applyPotionSickness(player, 15);
            }
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.MED_BAG.get() && !VersatileConfig.hasPotionSickness(player)) {
            if (!level.isClientSide) {
                player.setHealth(player.getMaxHealth());

                player.removeEffect(MobEffects.BLINDNESS);
                player.removeEffect(MobEffects.CONFUSION);
                player.removeEffect(MobEffects.DIG_SLOWDOWN);
                player.removeEffect(MobEffects.HUNGER);
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                player.removeEffect(MobEffects.POISON);
                player.removeEffect(MobEffects.WEAKNESS);
                player.removeEffect(MobEffects.WITHER);
                player.removeEffect(HbmPotion.RADIATION.get());

                VersatileConfig.applyPotionSickness(player, 15);

                stack.shrink(1);
            }
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.RADAWAY.get()) {
            if (!level.isClientSide) {
                player.addEffect(new MobEffectInstance(HbmPotion.RADAWAY.get(), 5 * 20, 9));

                stack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.RADAWAY.get(), player.getSoundSource(), 1.0F, 1.0F);
            }
            VersatileConfig.applyPotionSickness(player, 5);
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.RADAWAY_STRONG.get()) {
            if (!level.isClientSide) {
                int duration = 7 * 20;
                int volume = 19;

                if (!player.hasEffect(HbmPotion.RADAWAY.get())) {
                    player.addEffect(new MobEffectInstance(HbmPotion.RADAWAY.get(), duration, volume));
                } else {
                    MobEffectInstance existing = player.getEffect(HbmPotion.RADAWAY.get());
                    if (existing != null) {
                        int newDuration = existing.getDuration() + duration;
                        player.addEffect(new MobEffectInstance(HbmPotion.RADAWAY.get(), newDuration, volume));
                    }
                }

                stack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.RADAWAY.get(), player.getSoundSource(), 1.0F, 1.0F);
            }
            VersatileConfig.applyPotionSickness(player, 7);
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.RADAWAY_FLUSH.get()) {
            if (!level.isClientSide) {
                player.addEffect(new MobEffectInstance(HbmPotion.RADAWAY.get(), 10 * 20, 29));

                stack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.RADAWAY.get(), player.getSoundSource(), 1.0F, 1.0F);
            }
            VersatileConfig.applyPotionSickness(player, 10);
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.SYRINGE_TAINT.get()) {
            if (!level.isClientSide) {
                player.addEffect(new MobEffectInstance(HbmPotion.TAINT.get(), 60 * 20, 0));
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 20, 0));

                stack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.SYRINGE.get(), player.getSoundSource(), 1.0F, 1.0F);
            }

            if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
            }

            if (!player.getInventory().add(new ItemStack(ModItems.BOTTLE2_EMPTY.get()))) {
                player.drop(new ItemStack(ModItems.BOTTLE2_EMPTY.get()), false);
            }
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.GAS_MASK_FILTER_MONO.get() && player.getInventory().armor.get(3) != null &&
                player.getInventory().armor.get(3).getItem() == ModArmorItems.GAS_MASK_MONO.get()) {
            if (!level.isClientSide) {
                ItemStack armor = player.getInventory().armor.get(3);
                if (armor.getDamageValue() == 0) return InteractionResultHolder.pass(stack);

                armor.setDamageValue(0);

                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.GASMASK_SCREW.get(), player.getSoundSource(), 1.0F, 1.0F);
                stack.shrink(1);
            }
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.JETPACK_TANK.get() && player.getInventory().armor.get(2) != null) {
            if (!level.isClientSide) {
                ItemStack jetpack = player.getInventory().armor.get(2);

                if (jetpack == null) return InteractionResultHolder.pass(stack);

                if (jetpack.getItem() instanceof ArmorItem && ArmorModHandler.hasMods(jetpack)) {
                    ItemStack[] mods = ArmorModHandler.pryMods(jetpack);
                    if (mods != null && mods.length > ArmorModHandler.PLATE_ONLY) {
                        jetpack = mods[ArmorModHandler.PLATE_ONLY];
                    }
                }

                if (jetpack == null || !(jetpack.getItem() instanceof IFillableItem fillable)) {
                    return InteractionResultHolder.pass(stack);
                }

                if (!fillable.acceptsFluid(Fluids.KEROSENE.get(), jetpack)) {
                    return InteractionResultHolder.pass(stack);
                }

                if (fillable.tryFill(Fluids.KEROSENE.get(), 1000, jetpack) < 1000) {
                    level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.JETPACK_TANK.get(), player.getSoundSource(), 1.0F, 1.0F);
                    stack.shrink(1);
                }

                // Возвращаем модифицированный джетпак обратно
                if (jetpack.getItem() != player.getInventory().armor.get(2).getItem()) {
                    ArmorModHandler.applyMod(player.getInventory().armor.get(2), jetpack);
                }
            }
            return InteractionResultHolder.success(stack);
        }

        if (this == ModItems.CBT_DEVICE.get()) {
            if (!level.isClientSide) {
                player.addEffect(new MobEffectInstance(HbmPotion.BANG.get(), 30, 0));

                stack.shrink(1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), ModSounds.VICE.get(), player.getSoundSource(), 1.0F, 1.0F);
            }
            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        Level level = target.level();

        if (this == ModItems.SYRINGE_METAL_STIMPAK.get() && !VersatileConfig.hasPotionSickness(target)) {
            if (!level.isClientSide) {
                target.heal(5);
                VersatileConfig.applyPotionSickness(target, 5);

                stack.shrink(1);
                level.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SYRINGE.get(), target.getSoundSource(), 1.0F, 1.0F);

                if (attacker instanceof Player player) {
                    if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                        player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
                    }
                }
            }
            return true;
        }

        if (this == ModItems.SYRINGE_METAL_MEDX.get() && !VersatileConfig.hasPotionSickness(target)) {
            if (!level.isClientSide) {
                target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 4 * 60 * 20, 2));
                VersatileConfig.applyPotionSickness(target, 5);

                stack.shrink(1);
                level.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SYRINGE.get(), target.getSoundSource(), 1.0F, 1.0F);

                if (attacker instanceof Player player) {
                    if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                        player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
                    }
                }
            }
            return true;
        }

        if (this == ModItems.SYRINGE_METAL_PSYCHO.get() && !VersatileConfig.hasPotionSickness(target)) {
            if (!level.isClientSide) {
                target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2 * 60 * 20, 0));
                target.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2 * 60 * 20, 0));
                VersatileConfig.applyPotionSickness(target, 5);

                stack.shrink(1);
                level.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SYRINGE.get(), target.getSoundSource(), 1.0F, 1.0F);

                if (attacker instanceof Player player) {
                    if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                        player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
                    }
                }
            }
            return true;
        }

        if (this == ModItems.SYRINGE_METAL_SUPER.get() && !VersatileConfig.hasPotionSickness(target)) {
            if (!level.isClientSide) {
                target.heal(25);
                target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10 * 20, 0));
                VersatileConfig.applyPotionSickness(target, 15);

                stack.shrink(1);
                level.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SYRINGE.get(), target.getSoundSource(), 1.0F, 1.0F);

                if (attacker instanceof Player player) {
                    if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                        player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
                    }
                }
            }
            return true;
        }

        if (this == ModItems.SYRINGE_TAINT.get()) {
            if (!level.isClientSide) {
                target.addEffect(new MobEffectInstance(HbmPotion.TAINT.get(), 60 * 20, 0));
                target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 5 * 20, 0));

                stack.shrink(1);
                level.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SYRINGE.get(), target.getSoundSource(), 1.0F, 1.0F);

                if (attacker instanceof Player player) {
                    if (!player.getInventory().add(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()))) {
                        player.drop(new ItemStack(ModItems.SYRINGE_METAL_EMPTY.get()), false);
                    }
                    if (!player.getInventory().add(new ItemStack(ModItems.BOTTLE2_EMPTY.get()))) {
                        player.drop(new ItemStack(ModItems.BOTTLE2_EMPTY.get()), false);
                    }
                }
            }
            return true;
        }

        if (this == ModItems.SYRINGE_MKUNICORN.get()) {
            if (!level.isClientSide) {
                HbmLivingProps.setContagion(target, 3 * 60 * 60 * 20);
                level.playSound(null, target.getX(), target.getY(), target.getZ(), ModSounds.SYRINGE.get(), target.getSoundSource(), 1.0F, 1.0F);
                stack.shrink(1);
            }
            return true;
        }

        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.SYRINGE_ANTIDOTE.get()) {
            tooltip.add(Component.translatable("item.hbm.syringe_antidote.desc"));
        }
        if (this == ModItems.SYRINGE_AWESOME.get()) {
            tooltip.add(Component.translatable("item.hbm.syringe_awesome.desc"));
        }
        if (this == ModItems.SYRINGE_METAL_MEDX.get()) {
            tooltip.add(Component.translatable("item.hbm.syringe_metal_medx.desc"));
        }
        if (this == ModItems.SYRINGE_METAL_PSYCHO.get()) {
            tooltip.add(Component.translatable("item.hbm.syringe_metal_psycho.desc1"));
            tooltip.add(Component.translatable("item.hbm.syringe_metal_psycho.desc2"));
        }
        if (this == ModItems.SYRINGE_METAL_STIMPAK.get()) {
            tooltip.add(Component.translatable("item.hbm.syringe_metal_stimpak.desc"));
        }
        if (this == ModItems.SYRINGE_METAL_SUPER.get()) {
            tooltip.add(Component.translatable("item.hbm.syringe_metal_super.desc1"));
            tooltip.add(Component.translatable("item.hbm.syringe_metal_super.desc2"));
        }
        if (this == ModItems.SYRINGE_POISON.get()) {
            tooltip.add(Component.translatable("item.hbm.syringe_poison.desc"));
        }
        if (this == ModItems.MED_BAG.get()) {
            tooltip.add(Component.translatable("item.hbm.med_bag.desc1"));
            tooltip.add(Component.translatable("item.hbm.med_bag.desc2"));
        }
        if (this == ModItems.RADAWAY.get()) {
            tooltip.add(Component.translatable("item.hbm.radaway.desc"));
        }
        if (this == ModItems.RADAWAY_STRONG.get()) {
            tooltip.add(Component.translatable("item.hbm.radaway_strong.desc"));
        }
        if (this == ModItems.RADAWAY_FLUSH.get()) {
            tooltip.add(Component.translatable("item.hbm.radaway_flush.desc"));
        }
        if (this == ModItems.SYRINGE_TAINT.get()) {
            tooltip.add(Component.translatable("item.hbm.syringe_taint.desc1"));
            tooltip.add(Component.translatable("item.hbm.syringe_taint.desc2"));
            tooltip.add(Component.translatable("item.hbm.syringe_taint.desc3"));
        }
        if (this == ModItems.GAS_MASK_FILTER.get()) {
            tooltip.add(Component.translatable("item.hbm.gas_mask_filter.desc"));
        }
        if (this == ModItems.GAS_MASK_FILTER_MONO.get()) {
            tooltip.add(Component.translatable("item.hbm.gas_mask_filter_mono.desc"));
        }
        if (this == ModItems.JETPACK_TANK.get()) {
            tooltip.add(Component.translatable("item.hbm.jetpack_tank.desc"));
        }
        if (this == ModItems.GUN_KIT_1.get()) {
            tooltip.add(Component.translatable("item.hbm.gun_kit_1.desc"));
        }
        if (this == ModItems.GUN_KIT_2.get()) {
            tooltip.add(Component.translatable("item.hbm.gun_kit_2.desc"));
        }
        if (this == ModItems.SYRINGE_MKUNICORN.get()) {
            tooltip.add(Component.literal("?").setStyle(Style.EMPTY.withColor(0xff0000)));
        }
    }
}