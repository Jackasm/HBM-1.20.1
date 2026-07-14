package com.hbm.items.tool;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.entity.projectile.EntityRubble;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.items.ModToolItems;
import com.hbm.main.MainRegistry;
import com.hbm.particle.helper.ExplosionCreator;
import com.hbm.potion.HbmPotion;
import com.hbm.render.item.weapon.BigSwordItemRender;
import com.hbm.render.item.weapon.RedstoneSwordItemRender;
import com.hbm.render.item.weapon.WeaponSpecialRenderer;
import com.hbm.sound.ModSounds;
import com.hbm.util.ArmorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.function.Consumer;

public class WeaponSpecial extends SwordItem {

    private static final Random rand = new Random();
    private static final UUID WEAPON_MODIFIER_UUID = UUID.fromString("CB3F55D3-645C-4F38-A497-9C13A33DB5CF");

    public WeaponSpecial(Tier tier, Properties properties) {
        super(tier, (int)tier.getAttackDamageBonus(), tier.getSpeed(), properties);
    }

    @Override
    public @NotNull Rarity getRarity(@NotNull ItemStack stack) {
        if (this == ModToolItems.SCHRABIDIUM_HAMMER.get()) {
            return Rarity.RARE;
        }
        if (this == ModToolItems.ULLAPOOL_CABER.get()) {
            return Rarity.UNCOMMON;
        }
        if (this == ModToolItems.SHIMMER_SLEDGE.get() || this == ModToolItems.SHIMMER_AXE.get()) {
            return Rarity.EPIC;
        }
        return Rarity.COMMON;
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        Level level = target.level();

        if (this == ModToolItems.SCHRABIDIUM_HAMMER.get()) {
            if (!level.isClientSide) {
                target.setHealth(0.0F);
            }
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.BONK.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
        }

        if (this == ModToolItems.BOTTLE_OPENER.get()) {
            if (!level.isClientSide) {
                int i = rand.nextInt(7);
                if (i == 0)
                    target.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 5 * 60 * 20, 0));
                if (i == 1)
                    target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5 * 60 * 20, 2));
                if (i == 2)
                    target.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5 * 60 * 20, 2));
                if (i == 3)
                    target.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 1 * 60 * 20, 0));
            }
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 3.0F, 1.0F);
        }

        if (this == ModToolItems.ULLAPOOL_CABER.get()) {
            if (!level.isClientSide) {
                level.explode(null, target.getX(), target.getY(), target.getZ(), 7.5F, Level.ExplosionInteraction.TNT);
            }
            stack.hurtAndBreak(505, attacker, e -> {});
        }

        if (this == ModToolItems.SHIMMER_SLEDGE.get()) {
            Vec3 look = attacker.getLookAngle();
            double dX = look.x * 5;
            double dY = look.y * 5;
            double dZ = look.z * 5;

            target.setDeltaMovement(target.getDeltaMovement().add(dX, dY, dZ));
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.BANG.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
        }

        if (this == ModToolItems.SHIMMER_AXE.get()) {
            target.setHealth(target.getHealth() / 2);
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.SLICE.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
        }

        if (this == ModToolItems.WOOD_GAVEL.get()) {
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.WHACK.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
        }

        if (this == ModToolItems.LEAD_GAVEL.get()) {
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.WHACK.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
            target.addEffect(new MobEffectInstance(HbmPotion.LEAD.get(), 15 * 20, 4));
        }

        if (this == ModToolItems.DIAMOND_GAVEL.get()) {
            float ded = target.getMaxHealth() / 3;
            target.setHealth(target.getHealth() - ded);
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.WHACK.get(), SoundSource.PLAYERS, 3.0F, 1.0F);
        }

        if (this == ModToolItems.WRENCH.get()) {
            Vec3 look = attacker.getLookAngle();
            double dX = look.x * 0.5;
            double dY = look.y * 0.5;
            double dZ = look.z * 0.5;

            target.setDeltaMovement(target.getDeltaMovement().add(dX, dY, dZ));
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    SoundEvents.ANVIL_LAND, SoundSource.PLAYERS, 3.0F, 0.75F);
        }

        if (this == ModToolItems.MEMESPOON.get() && !level.isClientSide) {
            if (!(attacker instanceof Player player)) return false;

            if (player.fallDistance >= 2) {
                level.playSound(null, target.getX(), target.getY(), target.getZ(),
                        ModSounds.BANG.get(), SoundSource.PLAYERS, 3.0F, 0.75F);
                target.hurt(level.damageSources().playerAttack(player), 50F);
            }

            if (player.fallDistance >= 20 && !player.getAbilities().instabuild) {
                ExplosionVNT vnt = new ExplosionVNT(level, target.getX(), target.getY() + target.getBbHeight() / 2D, target.getZ(), 15, player);
                vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, 150).setupPiercing(25, 0.5F));
                vnt.setPlayerProcessor(new PlayerProcessorStandard());
                ExplosionCreator.composeEffectSmall(level, target.getX(), target.getY() + target.getBbHeight() / 2D, target.getZ());
                vnt.explode();
            }
        }

        if (this == ModToolItems.STOPSIGN.get() || this == ModToolItems.SOPSIGN.get()) {
            level.playSound(null, target.getX(), target.getY(), target.getZ(),
                    ModSounds.STOP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        return false;
    }

    @Override
    public boolean onBlockStartBreak(ItemStack stack, BlockPos pos, Player player) {
        Level level = player.level();

        if (this == ModToolItems.SHIMMER_SLEDGE.get()) {
            if (level.getBlockState(pos).getBlock() != Blocks.AIR &&
                    level.getBlockState(pos).getDestroySpeed(level, pos) < 6000) {

                EntityRubble rubble = new EntityRubble(level);
                rubble.setPos(pos.getX() + 0.5F, pos.getY(), pos.getZ() + 0.5F);
                rubble.setMetaBasedOnBlock(level.getBlockState(pos));

                Vec3 look = player.getLookAngle();
                double dX = look.x * 5;
                double dY = look.y * 5;
                double dZ = look.z * 5;

                rubble.setDeltaMovement(dX, dY, dZ);
                level.playSound(null, rubble.getX(), rubble.getY(), rubble.getZ(),
                        ModSounds.BANG.get(), SoundSource.PLAYERS, 3.0F, 1.0F);

                if (!level.isClientSide) {
                    level.addFreshEntity(rubble);
                    level.removeBlock(pos, false);
                }
            }
            return true;
        }

        if (this == ModToolItems.SHIMMER_AXE.get()) {
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    ModSounds.KAPENG.get(), SoundSource.PLAYERS, 3.0F, 1.0F);

            if (!level.isClientSide) {
                if (level.getBlockState(pos).getBlock() != Blocks.AIR &&
                        level.getBlockState(pos).getDestroySpeed(level, pos) < 6000) {
                    level.removeBlock(pos, false);
                }
                BlockPos up = pos.above();
                if (level.getBlockState(up).getBlock() != Blocks.AIR &&
                        level.getBlockState(up).getDestroySpeed(level, up) < 6000) {
                    level.removeBlock(up, false);
                }
                BlockPos down = pos.below();
                if (level.getBlockState(down).getBlock() != Blocks.AIR &&
                        level.getBlockState(down).getDestroySpeed(level, down) < 6000) {
                    level.removeBlock(down, false);
                }
            }
            return true;
        }

        return false;
    }

    @Override
    public @NotNull Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(@NotNull EquipmentSlot slot) {
        Multimap<Attribute, AttributeModifier> multimap = HashMultimap.create();

        if (slot == EquipmentSlot.MAINHAND) {
            if (this == ModToolItems.SCHRABIDIUM_HAMMER.get()) {
                multimap.put(Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(WEAPON_MODIFIER_UUID, "Weapon modifier", -0.5, AttributeModifier.Operation.MULTIPLY_BASE));
            }
            if (this == ModToolItems.SHIMMER_SLEDGE.get() || this == ModToolItems.SHIMMER_AXE.get()) {
                multimap.put(Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(WEAPON_MODIFIER_UUID, "Weapon modifier", -0.2, AttributeModifier.Operation.MULTIPLY_BASE));
            }
            if (this == ModToolItems.WRENCH_FLIPPED.get()) {
                multimap.put(Attributes.MOVEMENT_SPEED,
                        new AttributeModifier(WEAPON_MODIFIER_UUID, "Weapon modifier", -0.1, AttributeModifier.Operation.MULTIPLY_BASE));
            }
        }

        return multimap;
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity, int slot, boolean selected) {
        if (entity instanceof Player player) {
            if (ArmorUtil.checkForFiend(player)) {
                if (player instanceof ServerPlayer sp) {
                    ModCriteriaTriggers.FIEND.trigger(sp);
                }
            } else if (ArmorUtil.checkForFiend2(player)) {
                if (player instanceof ServerPlayer sp) {
                    ModCriteriaTriggers.FIEND2.trigger(sp);
                }
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModToolItems.SCHRABIDIUM_HAMMER.get()) {
            tooltip.add(Component.literal("Even though it says \"+1000000000\""));
            tooltip.add(Component.literal("damage\", it's actually \"onehit anything\""));
        }
        if (this == ModToolItems.ULLAPOOL_CABER.get()) {
            tooltip.add(Component.literal("High-yield Scottish face removal."));
            tooltip.add(Component.literal("A sober person would throw it..."));
        }
        if (this == ModToolItems.BOTTLE_OPENER.get()) {
            tooltip.add(Component.literal("My very own bottle opener."));
            tooltip.add(Component.literal("Use with caution!"));
        }
        if (this == ModToolItems.SHIMMER_SLEDGE.get()) {
            if (MainRegistry.polaroidID == 11) {
                tooltip.add(Component.literal("shimmer no"));
                tooltip.add(Component.literal("drop that hammer"));
                tooltip.add(Component.literal("you're going to hurt somebody"));
                tooltip.add(Component.literal("shimmer no"));
                tooltip.add(Component.literal("shimmer pls"));
            } else {
                tooltip.add(Component.literal("Breaks everything, even portals."));
            }
        }
        if (this == ModToolItems.SHIMMER_AXE.get()) {
            if (MainRegistry.polaroidID == 11) {
                tooltip.add(Component.literal("shim's toolbox does an e-x-p-a-n-d"));
            } else {
                tooltip.add(Component.literal("Timber!"));
            }
        }
        if (this == ModToolItems.WRENCH_FLIPPED.get()) {
            tooltip.add(Component.literal("Wrench 2: The Wrenchening"));
        }
        if (this == ModToolItems.MEMESPOON.get()) {
            tooltip.add(Component.literal("Level 10 Shovel").withStyle(ChatFormatting.DARK_GRAY));
            tooltip.add(Component.literal("Deals crits while the wielder is rocket jumping").withStyle(ChatFormatting.AQUA));
            tooltip.add(Component.literal("20% slower firing speed").withStyle(ChatFormatting.RED));
            tooltip.add(Component.literal("No random critical hits").withStyle(ChatFormatting.RED));
        }
        if (this == ModToolItems.WOOD_GAVEL.get()) {
            tooltip.add(Component.literal("Thunk!"));
        }
        if (this == ModToolItems.LEAD_GAVEL.get()) {
            tooltip.add(Component.literal("You are hereby sentenced to lead poisoning."));
        }
        if (this == ModToolItems.DIAMOND_GAVEL.get()) {
            tooltip.add(Component.literal("The joke! It makes sense now!!"));
            tooltip.add(Component.literal(""));
            tooltip.add(Component.literal("Deals as much damage as it needs to.").withStyle(ChatFormatting.BLUE));
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                Item item = WeaponSpecial.this;

                if (item == ModToolItems.REDSTONE_SWORD.get()) {
                    return new RedstoneSwordItemRender();
                }

                if (item == ModToolItems.BIG_SWORD.get()) {
                    return new BigSwordItemRender();
                }

                return new WeaponSpecialRenderer();
            }
        });
    }
}