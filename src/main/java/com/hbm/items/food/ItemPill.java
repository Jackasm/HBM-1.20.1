package com.hbm.items.food;

import com.hbm.config.VersatileConfig;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ModDamageSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ItemPill extends Item {

    private final Random rand = new Random();

    public ItemPill(Properties properties) {
        super(properties.food(new FoodProperties.Builder()
                .nutrition(0)
                .saturationMod(0)
                .alwaysEat()
                .build()));
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!level.isClientSide && entity instanceof Player player) {

            VersatileConfig.applyPotionSickness(player, 5);

            if (this == ModItems.PILL_IODINE.get()) {
                player.removeEffect(MobEffects.BLINDNESS);
                player.removeEffect(MobEffects.CONFUSION);
                player.removeEffect(MobEffects.DIG_SLOWDOWN);
                player.removeEffect(MobEffects.HUNGER);
                player.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                player.removeEffect(MobEffects.POISON);
                player.removeEffect(MobEffects.WEAKNESS);
                player.removeEffect(MobEffects.WITHER);
                player.removeEffect(HbmPotion.RADIATION.get());
            }

            if (this == ModItems.PLAN_C.get()) {
                for (int i = 0; i < 10; i++) {
                    if (rand.nextBoolean()) {
                        player.hurt(ModDamageSource.euthanizedSelf(player), 1000);
                    } else {
                        player.hurt(ModDamageSource.euthanizedSelf2(player), 1000);
                    }
                }
            }

            if (this == ModItems.PILL_RED.get()) {
                player.addEffect(new MobEffectInstance(HbmPotion.DEATH.get(), 60 * 60 * 20, 0));
            }

            if (this == ModItems.RADX.get()) {
                player.addEffect(new MobEffectInstance(HbmPotion.RADX.get(), 3 * 60 * 20, 0));
            }

            if (this == ModItems.SIOX.get()) {
                HbmLivingProps.setAsbestos(player, 0);
                HbmLivingProps.setBlackLung(player, Math.min(HbmLivingProps.getBlackLung(player), HbmLivingProps.MAX_BLACK_LUNG / 5));
            }

            if (this == ModItems.PILL_HERBAL.get()) {
                HbmLivingProps.setAsbestos(player, 0);
                HbmLivingProps.setBlackLung(player, Math.min(HbmLivingProps.getBlackLung(player), HbmLivingProps.MAX_BLACK_LUNG / 5));
                HbmLivingProps.incrementRadiation(player, -100F);

                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 10 * 20, 0));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 10 * 60 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 10 * 60 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.POISON, 5 * 20, 2));

                MobEffectInstance potionSickness = new MobEffectInstance(HbmPotion.POTIONSICKNESS.get(), 10 * 60 * 20);
                player.addEffect(potionSickness);
            }

            if (this == ModItems.XANAX.get()) {
                float digamma = HbmLivingProps.getDigamma(player);
                HbmLivingProps.setDigamma(player, Math.max(digamma - 0.5F, 0F));
            }

            if (this == ModItems.CHOCOLATE.get()) {
                if (rand.nextInt(25) == 0) {
                    player.hurt(ModDamageSource.overdose(player), 1000);
                }
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 60 * 20, 3));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20, 3));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 60 * 20, 3));
            }

            if (this == ModItems.FMN.get()) {
                float digamma = HbmLivingProps.getDigamma(player);
                HbmLivingProps.setDigamma(player, Math.min(digamma, 2F));
                player.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 60, 0));
            }

            if (this == ModItems.FIVE_HTP.get()) {
                HbmLivingProps.setDigamma(player, 0);
                player.addEffect(new MobEffectInstance(HbmPotion.STABILITY.get(), 10 * 60 * 20, 0));
            }
        }

        if (entity instanceof Player player && !player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        return stack;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 10;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (VersatileConfig.hasPotionSickness(player)) {
            return InteractionResultHolder.pass(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        String unloc = this.getDescriptionId() + ".desc";
        String loc = net.minecraft.network.chat.Component.translatable(unloc).getString();

        if (!unloc.equals(loc)) {
            String[] lines = loc.split("\\$");
            for (String line : lines) {
                tooltip.add(Component.literal(line));
            }
        }
    }
}