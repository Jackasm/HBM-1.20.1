package com.hbm.items.food;

import com.hbm.config.VersatileConfig;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.FakePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ItemEnergy extends Item {

    private Supplier<Item> containerSupplier = null;
    private Supplier<Item> capSupplier = null;
    private boolean requiresOpener = false;

    public ItemEnergy(Properties properties) {
        super(properties);
    }

    public ItemEnergy makeCan(Supplier<Item> container, Supplier<Item> cap) {
        this.containerSupplier = container;
        this.capSupplier = cap;
        this.requiresOpener = false;
        return this;
    }

    public ItemEnergy makeBottle(Supplier<Item> bottle, Supplier<Item> cap) {
        this.containerSupplier = bottle;
        this.capSupplier = cap;
        this.requiresOpener = true;
        return this;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity entity) {
        if (!(entity instanceof Player player)) {
            return super.finishUsingItem(stack, level, entity);
        }

        if (!player.getAbilities().instabuild) {
            stack.shrink(1);
        }

        if (!level.isClientSide) {
            if (player instanceof FakePlayer) {
                level.explode(player, player.getX(), player.getY(), player.getZ(), 5.0F, Level.ExplosionInteraction.TNT);
                return super.finishUsingItem(stack, level, entity);
            }

            VersatileConfig.applyPotionSickness(player, 5);

            // Can effects
            if (this == ModItems.CAN_SMART.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 0));
            }
            if (this == ModItems.CAN_CREATURE.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30 * 20, 1));
            }
            if (this == ModItems.CAN_REDBOMB.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 30 * 20, 1));
            }
            if (this == ModItems.CAN_MRSUGAR.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 30 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 30 * 20, 2));
            }
            if (this == ModItems.CAN_OVERCHARGE.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 0));
            }
            if (this == ModItems.CAN_LUNA.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 30 * 20, 2));
            }
            if (this == ModItems.CAN_BEPIS.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 3));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 3));
            }
            if (this == ModItems.CAN_BREEN.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 30 * 20, 0));
            }
            if (this == ModItems.CAN_MUG.get()) {
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 3 * 60 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60 * 20, 2));
            }

            // Bottle effects
            if (this == ModItems.CHOCOLATE_MILK.get()) {
                ExplosionLarge.explode(level, player.getX(), player.getY(), player.getZ(), 50, true, false, false);
            }
            if (this == ModItems.BOTTLE_NUKA.get()) {
                player.heal(4.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 30 * 20, 1));
                ContaminationUtil.contaminate(player, HazardType.RADIATION, ContaminationType.RAD_BYPASS, 5.0F);
            }
            if (this == ModItems.BOTTLE_CHERRY.get()) {
                player.heal(6.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 0));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 30 * 20, 2));
                ContaminationUtil.contaminate(player, HazardType.RADIATION, ContaminationType.RAD_BYPASS, 5.0F);
            }
            if (this == ModItems.BOTTLE_QUANTUM.get()) {
                player.heal(10.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 1));
                ContaminationUtil.contaminate(player, HazardType.RADIATION, ContaminationType.RAD_BYPASS, 15.0F);
            }
            if (this == ModItems.BOTTLE2_KORL.get()) {
                player.heal(6.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 30 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 30 * 20, 2));
            }
            if (this == ModItems.BOTTLE2_FRITZ.get()) {
                player.heal(6.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 30 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 30 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.JUMP, 30 * 20, 2));
            }
            if (this == ModItems.BOTTLE_SPARKLE.get()) {
                player.heal(10.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 120 * 20, 1));
                ContaminationUtil.contaminate(player, HazardType.RADIATION, ContaminationType.RAD_BYPASS, 5.0F);
            }
            if (this == ModItems.BOTTLE_RAD.get()) {
                player.heal(10.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 120 * 20, 1));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 120 * 20, 2));
                player.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 120 * 20, 0));
                player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 120 * 20, 4));
                player.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 120 * 20, 1));
                ContaminationUtil.contaminate(player, HazardType.RADIATION, ContaminationType.RAD_BYPASS, 15.0F);
            }
            if (this == ModItems.COFFEE.get()) {
                player.heal(10.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20, 2));
            }
            if (this == ModItems.COFFEE_RADIUM.get()) {
                player.heal(10.0F);
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 60 * 20, 2));
                HbmLivingProps.incrementRadiation(player, 500.0F);
                // Achievement removed in 1.20.1
            }

            // Return container items
            if (!player.getAbilities().instabuild) {
                if (this.capSupplier != null) {
                    Item cap = capSupplier.get();
                    if (cap != null) {
                        player.getInventory().add(new ItemStack(cap));
                    }
                }
                if (this.containerSupplier != null) {
                    Item container = containerSupplier.get();
                    if (container != null) {
                        if (stack.isEmpty()) {
                            return new ItemStack(container);
                        }
                        player.getInventory().add(new ItemStack(container));
                    }
                }
            }

            player.containerMenu.sendAllDataToRemote();
        }

        return stack;
    }

    @Override
    public ItemStack getCraftingRemainingItem(ItemStack stack) {
        if (containerSupplier != null) {
            Item container = containerSupplier.get();
            if (container != null) {
                return new ItemStack(container);
            }
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean hasCraftingRemainingItem(ItemStack stack) {
        return containerSupplier != null;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack) {
        return 32;
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (VersatileConfig.hasPotionSickness(player)) {
            return InteractionResultHolder.fail(stack);
        }
        if (this.requiresOpener && !player.getInventory().contains(ModToolItems.BOTTLE_OPENER.get().getDefaultInstance())) {
            return InteractionResultHolder.fail(stack);
        }

        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.CAN_SMART.get()) {
            tooltip.add(Component.translatable("hbm.text.cheap_and_full_of_bubbles"));
        }
        if (this == ModItems.CAN_CREATURE.get()) {
            tooltip.add(Component.translatable("hbm.text.basically_gasoline_in_a_tin_can"));
        }
        if (this == ModItems.CAN_REDBOMB.get()) {
            tooltip.add(Component.translatable("hbm.text.liquefied_explosives"));
        }
        if (this == ModItems.CAN_MRSUGAR.get()) {
            tooltip.add(Component.translatable("hbm.text.an_intellectual_drink_for_the_chosen_ones"));
        }
        if (this == ModItems.CAN_OVERCHARGE.get()) {
            tooltip.add(Component.translatable("hbm.text.possible_side_effects_include_heart_attacks_seiz"));
        }
        if (this == ModItems.CAN_LUNA.get()) {
            tooltip.add(Component.translatable("hbm.text.contains_actual_selenium_and_star_metal_tastes_l"));
        }
        if (this == ModItems.CAN_BEPIS.get()) {
            tooltip.add(Component.translatable("hbm.text.beppp"));
        }
        if (this == ModItems.CAN_BREEN.get()) {
            tooltip.add(Component.translatable("hbm.text.don_t_drink_the_water_they_put_something_in_it_t"));
            tooltip.add(Component.translatable("hbm.text.i_don_t_even_know_how_i_got_here"));
        }
        if (this == ModItems.CHOCOLATE_MILK.get()) {
            tooltip.add(Component.translatable("hbm.text.regular_chocolate_milk_safe_to_drink"));
            tooltip.add(Component.translatable("hbm.text.totally_not_made_from_nitroglycerine"));
        }
        if (this == ModItems.BOTTLE_NUKA.get()) {
            tooltip.add(Component.translatable("hbm.text.contains_about_210_kcal_and_1500_msv"));
        }
        if (this == ModItems.BOTTLE_CHERRY.get()) {
            tooltip.add(Component.translatable("hbm.text.now_with_severe_radiation_poisoning_in_every_sev"));
        }
        if (this == ModItems.BOTTLE_QUANTUM.get()) {
            tooltip.add(Component.translatable("hbm.text.comes_with_a_colorful_mix_of_over_70_isotopes"));
        }
        if (this == ModItems.BOTTLE2_KORL.get()) {
            tooltip.add(Component.translatable("hbm.text.contains_actual_orange_juice"));
        }
        if (this == ModItems.BOTTLE2_FRITZ.get()) {
            tooltip.add(Component.translatable("hbm.text.moremore_caffeine"));
        }
        if (this == ModItems.BOTTLE_SPARKLE.get()) {
            tooltip.add(Component.translatable("hbm.text.the_most_delicious_beverage_in_the_wasteland"));
        }
        if (this == ModItems.BOTTLE_RAD.get()) {
            tooltip.add(Component.translatable("hbm.text.tastes_like_radish_and_radiation"));
        }

        if (this.requiresOpener) {
            tooltip.add(Component.translatable("hbm.text.requires_bottle_opener"));
        }
    }

    // Фабричный метод
    public static Properties createProperties() {
        return new Properties()
                .stacksTo(16);
    }
}