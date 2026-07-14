package com.hbm.explosion;

import com.hbm.blocks.ModBlocks;
import com.hbm.util.ArmorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ExplosionThermo {

    public static void freeze(Level level, BlockPos pos, int bombStartStrength) {
        int r = bombStartStrength * 2;
        int r2 = r * r;
        int r22 = r2 / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int xx = -r; xx < r; xx++) {
            int X = xx + pos.getX();
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + pos.getY();
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + pos.getZ();
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22 + level.random.nextInt(Math.max(1, r22 / 2))) {
                        mutable.set(X, Y, Z);
                        freezeDest(level, mutable);
                    }
                }
            }
        }
    }

    public static void snow(Level level, BlockPos pos, int bound) {
        int r = bound;
        int r2 = r * r;
        int r22 = r2 / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int xx = -r; xx < r; xx++) {
            int X = xx + pos.getX();
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + pos.getY();
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + pos.getZ();
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        mutable.set(X, Y, Z);
                        BlockPos above = mutable.above();
                        BlockState aboveState = level.getBlockState(above);
                        if (Blocks.SNOW.canSurvive(Blocks.SNOW.defaultBlockState(), level, above) &&
                                (aboveState.isAir() || aboveState.is(Blocks.FIRE))) {
                            level.setBlock(above, Blocks.SNOW.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    public static void scorch(Level level, BlockPos pos, int bombStartStrength) {
        int r = bombStartStrength * 2;
        int r2 = r * r;
        int r22 = r2 / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int xx = -r; xx < r; xx++) {
            int X = xx + pos.getX();
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + pos.getY();
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + pos.getZ();
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22 + level.random.nextInt(Math.max(1, r22 / 2))) {
                        mutable.set(X, Y, Z);
                        scorchDest(level, mutable);
                    }
                }
            }
        }
    }

    public static void scorchLight(Level level, BlockPos pos, int bombStartStrength) {
        int r = bombStartStrength * 2;
        int r2 = r * r;
        int r22 = r2 / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int xx = -r; xx < r; xx++) {
            int X = xx + pos.getX();
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + pos.getY();
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + pos.getZ();
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22 + level.random.nextInt(Math.max(1, r22 / 2))) {
                        mutable.set(X, Y, Z);
                        scorchDestLight(level, mutable);
                    }
                }
            }
        }
    }

    public static void freezeDest(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block == ModBlocks.VOLCANIC_LAVA_BLOCK.get()) {
            level.setBlock(pos, Blocks.COBBLESTONE.defaultBlockState(), 3);
        } else if (block == Blocks.GRASS_BLOCK) {
            level.setBlock(pos, ModBlocks.FROZEN_GRASS.get().defaultBlockState(), 3);
        } else if (block == Blocks.DIRT) {
            level.setBlock(pos, ModBlocks.FROZEN_DIRT.get().defaultBlockState(), 3);
        } else if (block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || block == Blocks.SPRUCE_LOG ||
                block == Blocks.JUNGLE_LOG || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG) {
            level.setBlock(pos, ModBlocks.FROZEN_LOG.get().defaultBlockState(), 3);
        } else if (block == Blocks.OAK_PLANKS || block == Blocks.BIRCH_PLANKS || block == Blocks.SPRUCE_PLANKS ||
                block == Blocks.JUNGLE_PLANKS || block == Blocks.ACACIA_PLANKS || block == Blocks.DARK_OAK_PLANKS) {
            level.setBlock(pos, ModBlocks.FROZEN_PLANKS.get().defaultBlockState(), 3);
        } else if (block == ModBlocks.WASTE_LOG.get()) {
            level.setBlock(pos, ModBlocks.FROZEN_LOG.get().defaultBlockState(), 3);
        } else if (block == ModBlocks.WASTE_PLANKS.get()) {
            level.setBlock(pos, ModBlocks.FROZEN_PLANKS.get().defaultBlockState(), 3);
        } else if (block == Blocks.STONE || block == Blocks.COBBLESTONE || block == Blocks.STONE_BRICKS) {
            level.setBlock(pos, Blocks.PACKED_ICE.defaultBlockState(), 3);
        } else if (block == Blocks.OAK_LEAVES || block == Blocks.BIRCH_LEAVES || block == Blocks.SPRUCE_LEAVES ||
                block == Blocks.JUNGLE_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.DARK_OAK_LEAVES) {
            level.setBlock(pos, Blocks.SNOW.defaultBlockState(), 3);
        } else if (block == Blocks.LAVA || block == Blocks.LAVA) {
            level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
        } else if (block == Blocks.WATER || block == Blocks.WATER) {
            level.setBlock(pos, Blocks.ICE.defaultBlockState(), 3);
        }
    }

    public static void scorchDest(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block == Blocks.GRASS_BLOCK || block == ModBlocks.FROZEN_GRASS.get()) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        } else if (block == Blocks.DIRT || block == ModBlocks.FROZEN_DIRT.get()) {
            level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 3);
        } else if (block == Blocks.NETHERRACK) {
            level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
        } else if (block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || block == Blocks.SPRUCE_LOG ||
                block == Blocks.JUNGLE_LOG || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG ||
                block == ModBlocks.FROZEN_LOG.get()) {
            level.setBlock(pos, ModBlocks.WASTE_LOG.get().defaultBlockState(), 3);
        } else if (block == Blocks.OAK_PLANKS || block == Blocks.BIRCH_PLANKS || block == Blocks.SPRUCE_PLANKS ||
                block == Blocks.JUNGLE_PLANKS || block == Blocks.ACACIA_PLANKS || block == Blocks.DARK_OAK_PLANKS ||
                block == ModBlocks.FROZEN_PLANKS.get()) {
            level.setBlock(pos, ModBlocks.WASTE_PLANKS.get().defaultBlockState(), 3);
        } else if (block == Blocks.STONE || block == Blocks.COBBLESTONE || block == Blocks.STONE_BRICKS ||
                block == Blocks.OBSIDIAN) {
            level.setBlock(pos, Blocks.LAVA.defaultBlockState(), 3);
        } else if (block == Blocks.OAK_LEAVES || block == Blocks.BIRCH_LEAVES || block == Blocks.SPRUCE_LEAVES ||
                block == Blocks.JUNGLE_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.DARK_OAK_LEAVES) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else if (block == Blocks.WATER || block == Blocks.WATER) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else if (block == Blocks.PACKED_ICE) {
            level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
        } else if (block == Blocks.ICE) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    public static void scorchDestLight(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block == Blocks.GRASS_BLOCK || block == ModBlocks.FROZEN_GRASS.get()) {
            level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
        } else if (block == Blocks.DIRT || block == ModBlocks.FROZEN_DIRT.get() || block == ModBlocks.WASTE_EARTH.get()) {
            level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 3);
        } else if (block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || block == Blocks.SPRUCE_LOG ||
                block == Blocks.JUNGLE_LOG || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG ||
                block == ModBlocks.FROZEN_LOG.get()) {
            level.setBlock(pos, ModBlocks.WASTE_LOG.get().defaultBlockState(), 3);
        } else if (block == Blocks.OAK_PLANKS || block == Blocks.BIRCH_PLANKS || block == Blocks.SPRUCE_PLANKS ||
                block == Blocks.JUNGLE_PLANKS || block == Blocks.ACACIA_PLANKS || block == Blocks.DARK_OAK_PLANKS ||
                block == ModBlocks.FROZEN_PLANKS.get()) {
            level.setBlock(pos, ModBlocks.WASTE_PLANKS.get().defaultBlockState(), 3);
        } else if (block == Blocks.OBSIDIAN) {
            level.setBlock(pos, ModBlocks.GRAVEL_OBSIDIAN.get().defaultBlockState(), 3);
        } else if (block == Blocks.OAK_LEAVES || block == Blocks.BIRCH_LEAVES || block == Blocks.SPRUCE_LEAVES ||
                block == Blocks.JUNGLE_LEAVES || block == Blocks.ACACIA_LEAVES || block == Blocks.DARK_OAK_LEAVES) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else if (block == Blocks.WATER || block == Blocks.WATER) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else if (block == Blocks.PACKED_ICE) {
            level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
        } else if (block == Blocks.ICE) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else if (block == Blocks.SAND) {
            level.setBlock(pos, Blocks.GLASS.defaultBlockState(), 3);
        } else if (block == Blocks.CLAY) {
            level.setBlock(pos, Blocks.TERRACOTTA.defaultBlockState(), 3);
        }
    }

    public static void freezer(Level level, BlockPos pos, int bombStartStrength) {
        float f = bombStartStrength;
        double wat = bombStartStrength;
        bombStartStrength *= 2;

        AABB box = new AABB(
                pos.getX() - wat, pos.getY() - wat, pos.getZ() - wat,
                pos.getX() + wat, pos.getY() + wat, pos.getZ() + wat
        );

        List<Entity> list = level.getEntities(null, box);
        Vec3 center = new Vec3(pos.getX(), pos.getY(), pos.getZ());

        for (Entity entity : list) {
            double distance = Math.sqrt(entity.distanceToSqr(center.x, center.y, center.z)) / bombStartStrength;

            if (distance <= 1.0 && !(entity instanceof Ocelot) && entity instanceof LivingEntity living) {
                BlockPos entityPos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
                for (int a = -2; a <= 0; a++) {
                    for (int b = 0; b < 3; b++) {
                        for (int c = -1; c <= 1; c++) {
                            BlockPos icePos = entityPos.offset(a, b, c);
                            level.setBlock(icePos, Blocks.ICE.defaultBlockState(), 3);
                        }
                    }
                }

                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 2 * 60 * 20, 4));
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 90 * 20, 2));
                living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 3 * 60 * 20, 2));
            }
        }
    }

    public static void setEntitiesOnFire(Level level, BlockPos pos, int radius) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        AABB box = new AABB(
                x - radius, y - radius, z - radius,
                x + radius, y + radius, z + radius
        );

        List<Entity> list = level.getEntities(null, box);

        for (Entity entity : list) {
            if (entity.distanceToSqr(x, y, z) <= radius * radius) {
                if (!(entity instanceof Player player && ArmorUtil.checkForAsbestos(player))) {
                    if (entity instanceof LivingEntity living) {
                        living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 15 * 20, 4));
                    }
                    entity.setSecondsOnFire(10);
                }
            }
        }
    }
}