package com.hbm.items.special;

import com.hbm.config.BombConfig;
import com.hbm.config.WeaponConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.effect.EntityRagingVortex;
import com.hbm.entity.effect.EntityVortex;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.interfaces.IBomb;
import com.hbm.items.ModItems;
import com.hbm.sound.ModSounds;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDrop extends Item {

    public ItemDrop(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        if (entity == null) return false;

        if (this == ModItems.BETA.get()) {
            entity.discard();
            return true;
        }

        if (stack.getItem() == ModItems.DETONATOR_DEADMAN.get()) {
            if (!entity.level().isClientSide) {

                CompoundTag tag = stack.getTag();
                if (tag != null) {
                    int x = tag.getInt("x");
                    int y = tag.getInt("y");
                    int z = tag.getInt("z");
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = entity.level().getBlockState(pos);
                    Block block = state.getBlock();

                    if (block instanceof IBomb bomb) {
                        if (!entity.level().isClientSide) {
                            bomb.explode(entity.level(), pos);
                        }
                    }
                }

                entity.level().explode(null, entity.getX(), entity.getY(), entity.getZ(), 0.0F, Level.ExplosionInteraction.NONE);
                entity.discard();
            }
        }

        if (stack.getItem() == ModItems.DETONATOR_DE.get()) {
            if (!entity.level().isClientSide && WeaponConfig.DROP_DEAD) {
                entity.level().explode(null, entity.getX(), entity.getY(), entity.getZ(), 15.0F, Level.ExplosionInteraction.TNT);

            }

            entity.discard();
        }

        if (entity.onGround()) {

            if (stack.getItem() == ModItems.CELL_ANTIMATTER.get() && WeaponConfig.DROP_CELL) {
                if (!entity.level().isClientSide) {
                    new ExplosionVNT(entity.level(), entity.getX(), entity.getY(), entity.getZ(), 3F).makeAmat().explode();
                }
            }
            if (stack.getItem() == ModItems.PELLET_ANTIMATTER.get() && WeaponConfig.DROP_CELL) {
                if (!entity.level().isClientSide) {
                    new ExplosionVNT(entity.level(), entity.getX(), entity.getY(), entity.getZ(), 20F).makeAmat().explode();
                }
            }
            if (stack.getItem() == ModItems.CELL_ANTI_SCHRABIDIUM.get() && WeaponConfig.DROP_CELL) {
                if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
                    EntityNukeExplosionMK3 ex = EntityNukeExplosionMK3.statFacFleija(serverLevel, entity.getX(), entity.getY(), entity.getZ(), BombConfig.A_SCHRAB_RADIUS);
                    if (ex != null && !ex.isRemoved()) {
                        entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 100.0F, entity.level().random.nextFloat() * 0.1F + 0.9F);
                        entity.level().addFreshEntity(ex);

                        EntityCloudFleija cloud = new EntityCloudFleija(ModEntities.CLOUD_FLEIJA.get(), serverLevel, BombConfig.A_SCHRAB_RADIUS);
                        cloud.setPos(entity.getX(), entity.getY(), entity.getZ());
                        entity.level().addFreshEntity(cloud);
                    }
                }
            }
            if (stack.getItem() == ModItems.SINGULARITY.get() && WeaponConfig.DROP_SING) {
                if (!entity.level().isClientSide) {
                    EntityVortex vortex = new EntityVortex(ModEntities.VORTEX.get(), entity.level(), 1.5F);
                    vortex.setPos(entity.getX(), entity.getY(), entity.getZ());
                    entity.level().addFreshEntity(vortex);
                }
            }
            if (stack.getItem() == ModItems.SINGULARITY_COUNTER_RESONANT.get() && WeaponConfig.DROP_SING) {
                if (!entity.level().isClientSide) {
                    EntityVortex vortex = new EntityVortex(ModEntities.VORTEX.get(),entity.level(), 2.5F);
                    vortex.setPos(entity.getX(), entity.getY(), entity.getZ());
                    entity.level().addFreshEntity(vortex);
                }
            }
            if (stack.getItem() == ModItems.SINGULARITY_SUPER_HEATED.get() && WeaponConfig.DROP_SING) {
                if (!entity.level().isClientSide) {
                    EntityVortex vortex = new EntityVortex(ModEntities.VORTEX.get(),entity.level(), 2.5F);
                    vortex.setPos(entity.getX(), entity.getY(), entity.getZ());
                    entity.level().addFreshEntity(vortex);
                }
            }
            if (stack.getItem() == ModItems.BLACK_HOLE.get() && WeaponConfig.DROP_SING) {
                if (!entity.level().isClientSide) {
                    EntityBlackHole blackHole = new EntityBlackHole(ModEntities.BLACK_HOLE.get(), entity.level(), 1.5F);
                    blackHole.setPos(entity.getX(), entity.getY(), entity.getZ());
                    entity.level().addFreshEntity(blackHole);
                }
            }
            if (stack.getItem() == ModItems.SINGULARITY_SPARK.get() && WeaponConfig.DROP_SING) {
                if (!entity.level().isClientSide) {
                    EntityRagingVortex vortex = new EntityRagingVortex(ModEntities.RAGING_VORTEX.get(), entity.level(), 3.5F);
                    vortex.setPos(entity.getX(), entity.getY(), entity.getZ());
                    entity.level().addFreshEntity(vortex);
                }
            }
            if (stack.getItem() == ModItems.CRYSTAL_XEN.get() && WeaponConfig.DROP_CRYS) {
                if (!entity.level().isClientSide) {
                    ExplosionChaos.floater(entity.level(), (int) entity.getX(), (int) entity.getY(), (int) entity.getZ(), 25, 75);
                    ExplosionChaos.move(entity.level(), (int) entity.getX(), (int) entity.getY(), (int) entity.getZ(), 25, 0, 75, 0);
                }
            }

            entity.discard();
            return true;
        }

        return false;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.CELL_ANTIMATTER.get()) {
            tooltip.add(Component.translatable("hbm.text.warning_exposure_to_matter_will"));
            tooltip.add(Component.translatable("hbm.text.lead_to_violent_annihilation"));
        }
        if (this == ModItems.PELLET_ANTIMATTER.get()) {
            tooltip.add(Component.translatable("hbm.text.very_heavy_antimatter_cluster"));
            tooltip.add(Component.translatable("hbm.text.gets_rid_of_black_holes"));
        }
        if (this == ModItems.CELL_ANTI_SCHRABIDIUM.get()) {
            tooltip.add(Component.translatable("hbm.text.warning_exposure_to_matter_will"));
            tooltip.add(Component.translatable("hbm.text.create_a_f_lkvangr_field"));
        }
        if (this == ModItems.SINGULARITY.get()) {
            tooltip.add(Component.translatable("hbm.text.you_may_be_asking"));
            tooltip.add(Component.literal("\"But HBM, a manifold with an undefined"));
            tooltip.add(Component.literal("state of spacetime? How is this possible?\""));
            tooltip.add(Component.translatable("hbm.text.long_answer_short"));
            tooltip.add(Component.literal("\"I have no idea!\""));
        }
        if (this == ModItems.SINGULARITY_COUNTER_RESONANT.get()) {
            tooltip.add(Component.translatable("hbm.text.nullifies_resonance_of_objects_in"));
            tooltip.add(Component.translatable("hbm.text.non_euclidean_space_creates_variable"));
            tooltip.add(Component.translatable("hbm.text.gravity_well_spontaneously_spawns"));
            tooltip.add(Component.translatable("hbm.text.tesseracts_if_a_tesseract_happens_to"));
            tooltip.add(Component.translatable("hbm.text.appear_near_you_do_not_look_directly"));
            tooltip.add(Component.translatable("hbm.text.at_it"));
        }
        if (this == ModItems.SINGULARITY_SUPER_HEATED.get()) {
            tooltip.add(Component.translatable("hbm.text.continuously_heats_up_matter_by"));
            tooltip.add(Component.translatable("hbm.text.resonating_every_planck_second"));
            tooltip.add(Component.translatable("hbm.text.tends_to_catch_fire_or_to_create"));
            tooltip.add(Component.translatable("hbm.text.small_plasma_arcs_not_edible"));
        }
        if (this == ModItems.BLACK_HOLE.get()) {
            tooltip.add(Component.translatable("hbm.text.contains_a_regular_singularity"));
            tooltip.add(Component.translatable("hbm.text.in_the_center_large_enough_to"));
            tooltip.add(Component.translatable("hbm.text.stay_stable_it_s_not_the_end"));
            tooltip.add(Component.translatable("hbm.text.of_the_world_as_we_know_it"));
            tooltip.add(Component.translatable("hbm.text.and_i_don_t_feel_fine"));
        }
        if (this == ModItems.DETONATOR_DEADMAN.get()) {
            tooltip.add(Component.translatable("hbm.text.shift_right_click_to_set_position"));
            tooltip.add(Component.translatable("hbm.text.drop_to_detonate"));
            CompoundTag tag = stack.getTag();
            if (tag == null) {
                tooltip.add(Component.translatable("hbm.text.no_position_set"));
            } else {
                tooltip.add(Component.translatable("hbm.text.set_pos_to", tag.getInt("x"), tag.getInt("y"), tag.getInt("z")));
            }
        }
        if (this == ModItems.DETONATOR_DE.get()) {
            tooltip.add(Component.translatable("hbm.text.explodes_when_dropped"));
        }

        tooltip.add(Component.literal("[").withStyle(ChatFormatting.RED)
                .append(Component.translatable("trait.drop"))
                .append(Component.literal("]").withStyle(ChatFormatting.RED)));
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        ItemStack stack = context.getItemInHand();
        Player player = context.getPlayer();
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();

        if (this != ModItems.DETONATOR_DEADMAN.get()) {
            return super.useOn(context);
        }

        CompoundTag tag = stack.getOrCreateTag();

        if (player != null && player.isShiftKeyDown()) {
            tag.putInt("x", pos.getX());
            tag.putInt("y", pos.getY());
            tag.putInt("z", pos.getZ());

            if (level.isClientSide) {
                player.displayClientMessage(Component.translatable("hbm.text.position_set"), true);
            }

            level.playSound(player, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TECH_BOOP.get(), SoundSource.PLAYERS, 2.0F, 1.0F);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}