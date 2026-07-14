package com.hbm.explosion;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.VersatileConfig;
import com.hbm.entity.grenade.EntityGrenadeASchrab;
import com.hbm.entity.grenade.EntityGrenadeNuclear;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityExplosiveBeam;
import com.hbm.items.ModArmorItems;
import com.hbm.util.ArmorUtil;
import com.hbm.util.Library;
import com.hbm.util.ModDamageSource;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.Ocelot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.List;
import java.util.Random;

public class ExplosionNukeGeneric {

    private static final Random random = new Random();

    public static void empBlast(Level level, int x, int y, int z, int bombStartStrength) {
        int r = bombStartStrength;
        int r2 = r * r;
        int r22 = r2 / 2;
        for (int xx = -r; xx < r; xx++) {
            int X = xx + x;
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + y;
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + z;
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        emp(level, new BlockPos(X, Y, Z));
                    }
                }
            }
        }
    }

    public static void dealDamage(Level level, double x, double y, double z, double radius) {
        dealDamage(level, x, y, z, radius, 250F);
    }

    public static void dealDamage(Level level, double x, double y, double z, double radius, float maxDamage) {
        AABB box = new AABB(x, y, z, x, y, z).inflate(radius);
        List<Entity> list = level.getEntities(null, box);

        for (Entity e : list) {
            double dist = e.distanceToSqr(x, y, z);
            if (dist <= radius * radius) {
                double entX = e.getX();
                double entY = e.getY() + e.getEyeHeight();
                double entZ = e.getZ();

                if (!isExplosionExempt(e) && !Library.isObstructed(level, x, y, z, entX, entY, entZ)) {
                    double damage = maxDamage * (radius - Math.sqrt(dist)) / radius;
                    DamageSource nuclearBlastSource = ModDamageSource.createDamageSource(
                            ModDamageSource.NUCLEAR_BLAST, null, null, e.level());
                    e.hurt(nuclearBlastSource, (float) damage);
                    e.setSecondsOnFire(5);

                    double knockX = e.getX() - x;
                    double knockY = e.getY() + e.getEyeHeight() - y;
                    double knockZ = e.getZ() - z;

                    Vec3 knock = new Vec3(knockX, knockY, knockZ).normalize();

                    e.setDeltaMovement(e.getDeltaMovement().add(
                            knock.x * 0.2,
                            knock.y * 0.2,
                            knock.z * 0.2
                    ));
                }
            }
        }
    }

    private static boolean isExplosionExempt(Entity e) {
        if (e instanceof Ocelot ||
                e instanceof EntityGrenadeASchrab ||
                e instanceof EntityGrenadeNuclear ||
                e instanceof EntityExplosiveBeam ||
                e instanceof EntityBulletBaseMK4 ||
                (e instanceof Player &&
                        ArmorUtil.checkArmor((Player) e, ModArmorItems.EUPHEMIUM_HELMET.get(),
                                ModArmorItems.EUPHEMIUM_CHESTPLATE.get(),
                                ModArmorItems.EUPHEMIUM_LEGGINGS.get(),
                                ModArmorItems.EUPHEMIUM_BOOTS.get()))) {
            return true;
        }

        if (e instanceof Player player && player.getAbilities().instabuild) {
            return true;
        }

        return false;
    }

    public static void vapor(Level level, int x, int y, int z, int bombStartStrength) {
        int r = bombStartStrength * 2;
        int r2 = r * r;
        int r22 = r2 / 2;
        for (int xx = -r; xx < r; xx++) {
            int X = xx + x;
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + y;
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + z;
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22)
                        vaporDest(level, new BlockPos(X, Y, Z));
                }
            }
        }
    }

    public static int destruction(Level level, BlockPos pos) {
        int rand;
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            Block b = state.getBlock();
            float resistance = state.getBlock().getExplosionResistance();

            if (resistance >= 200f) {
                int protection = (int) (resistance / 300f);
                if (b == ModBlocks.BRICK_CONCRETE.get()) {
                    rand = random.nextInt(8);
                    if (rand == 0) {
                        level.setBlock(pos, Blocks.GRAVEL.defaultBlockState(), 3);
                        return 0;
                    }
                } else if (b == ModBlocks.BRICK_LIGHT.get()) {
                    rand = random.nextInt(3);
                    if (rand == 0) {
                        level.setBlock(pos, ModBlocks.WASTE_PLANKS.get().defaultBlockState(), 3);
                        return 0;
                    } else if (rand == 1) {
                        level.setBlock(pos, ModBlocks.BLOCK_SCRAP.get().defaultBlockState(), 3);
                        return 0;
                    }
                } else if (b == ModBlocks.BRICK_OBSIDIAN.get()) {
                    rand = random.nextInt(20);
                    if (rand == 0) {
                        level.setBlock(pos, Blocks.OBSIDIAN.defaultBlockState(), 3);
                    }
                } else if (b == Blocks.OBSIDIAN) {
                    level.setBlock(pos, ModBlocks.GRAVEL_OBSIDIAN.get().defaultBlockState(), 3);
                    return 0;
                } else if (random.nextInt(protection + 3) == 0) {
                    level.setBlock(pos, ModBlocks.BLOCK_SCRAP.get().defaultBlockState(), 3);
                }
                return protection;
            } else {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            }
        }
        return 0;
    }

    public static int vaporDest(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            Block b = state.getBlock();
            float resistance = state.getBlock().getExplosionResistance();

            if (resistance < 0.5f ||
                    b == Blocks.COBWEB ||
                    b == ModBlocks.RED_CABLE.get() ||
                    b instanceof LiquidBlock) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                return 0;
            } else if (resistance <= 3.0f && !state.canOcclude()) {
                if (b != Blocks.CHEST && b != Blocks.FARMLAND) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                    return 0;
                }
            }

            if (state.isFlammable(level, pos, Direction.UP) &&
                    level.getBlockState(pos.above()).isAir()) {
                level.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), 3);
            }
            return (int) (resistance / 300f);
        }
        return 0;
    }

    public static void waste(Level level, BlockPos pos, int radius) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();
        int r = radius;
        int r2 = r * r;
        int r22 = r2 / 2;
        for (int xx = -r; xx < r; xx++) {
            int X = xx + x;
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + y;
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + z;
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22 + level.random.nextInt(r22 / 5)) {
                        if (!level.getBlockState(new BlockPos(X, Y, Z)).isAir())
                            wasteDest(level, new BlockPos(X, Y, Z));
                    }
                }
            }
        }
    }

    public static void wasteDest(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            int rand;
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (block == Blocks.OAK_DOOR || block == Blocks.IRON_DOOR) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            } else if (block == Blocks.GRASS_BLOCK) {
                level.setBlock(pos, ModBlocks.WASTE_EARTH.get().defaultBlockState(), 3);
            } else if (block == Blocks.MYCELIUM) {
                level.setBlock(pos, ModBlocks.WASTE_MYCELIUM.get().defaultBlockState(), 3);
            } else if (block == Blocks.SAND) {
                rand = random.nextInt(20);
                if (rand == 1) {
                    level.setBlock(pos, ModBlocks.WASTE_TRINITITE.get().defaultBlockState(), 3);
                }
            } else if (block == Blocks.RED_SAND) {
                rand = random.nextInt(20);
                if (rand == 1) {
                    level.setBlock(pos, ModBlocks.WASTE_TRINITITE_RED.get().defaultBlockState(), 3);
                }
            } else if (block == Blocks.CLAY) {
                level.setBlock(pos, Blocks.TERRACOTTA.defaultBlockState(), 3);
            } else if (block == Blocks.MOSSY_COBBLESTONE) {
                level.setBlock(pos, Blocks.COAL_ORE.defaultBlockState(), 3);
            } else if (block == Blocks.COAL_ORE) {
                rand = random.nextInt(10);
                if (rand == 1 || rand == 2 || rand == 3) {
                    level.setBlock(pos, Blocks.DIAMOND_ORE.defaultBlockState(), 3);
                }
                if (rand == 9) {
                    level.setBlock(pos, Blocks.EMERALD_ORE.defaultBlockState(), 3);
                }
            } else if (block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || block == Blocks.SPRUCE_LOG ||
                    block == Blocks.JUNGLE_LOG || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG) {
                level.setBlock(pos, ModBlocks.WASTE_LOG.get().defaultBlockState(), 3);
            } else if (block == Blocks.BROWN_MUSHROOM_BLOCK) {
                boolean isStem = state.getValue(BlockStateProperties.NORTH) && state.getValue(BlockStateProperties.SOUTH) &&
                        state.getValue(BlockStateProperties.EAST) && state.getValue(BlockStateProperties.WEST) &&
                        state.getValue(BlockStateProperties.UP) && state.getValue(BlockStateProperties.DOWN);

                if (isStem) {
                    level.setBlock(pos, ModBlocks.WASTE_LOG.get().defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            } else if (block == Blocks.RED_MUSHROOM_BLOCK) {
                boolean isStem = state.getValue(BlockStateProperties.NORTH) && state.getValue(BlockStateProperties.SOUTH) &&
                        state.getValue(BlockStateProperties.EAST) && state.getValue(BlockStateProperties.WEST) &&
                        state.getValue(BlockStateProperties.UP) && state.getValue(BlockStateProperties.DOWN);

                if (isStem) {
                    level.setBlock(pos, ModBlocks.WASTE_LOG.get().defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            } else if (state.is(BlockTags.LOGS) && block != ModBlocks.WASTE_LOG.get()) {
                // Для любых других бревен (например, из других модов)
                level.setBlock(pos, ModBlocks.WASTE_LOG.get().defaultBlockState(), 3);
            } else if (state.is(BlockTags.PLANKS) && block != ModBlocks.WASTE_PLANKS.get()) {
                level.setBlock(pos, ModBlocks.WASTE_PLANKS.get().defaultBlockState(), 3);
            } else if (block == ModBlocks.ORE_URANIUM.get()) {
                rand = random.nextInt(VersatileConfig.getSchrabOreChance());
                if (rand == 1) {
                    level.setBlock(pos, ModBlocks.ORE_SCHRABIDIUM.get().defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, ModBlocks.ORE_URANIUM_SCORCHED.get().defaultBlockState(), 3);
                }
            } else if (block == ModBlocks.ORE_NETHER_URANIUM.get()) {
                rand = random.nextInt(VersatileConfig.getSchrabOreChance());
                if (rand == 1) {
                    level.setBlock(pos, ModBlocks.ORE_NETHER_SCHRABIDIUM.get().defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, ModBlocks.ORE_NETHER_URANIUM_SCORCHED.get().defaultBlockState(), 3);
                }
            } else if (block == ModBlocks.ORE_GNEISS_URANIUM.get()) {
                rand = random.nextInt(VersatileConfig.getSchrabOreChance());
                if (rand == 1) {
                    level.setBlock(pos, ModBlocks.ORE_GNEISS_SCHRABIDIUM.get().defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, ModBlocks.ORE_GNEISS_URANIUM_SCORCHED.get().defaultBlockState(), 3);
                }
            }
        }
    }

    public static void wasteNoSchrab(Level level, BlockPos pos, int radius) {
        int r = radius;
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
                    if (ZZ < r22 + level.random.nextInt(Math.max(1, r22 / 5))) {
                        mutable.set(X, Y, Z);
                        if (!level.getBlockState(mutable).isAir()) {
                            wasteDestNoSchrab(level, mutable);
                        }
                    }
                }
            }
        }
    }

    public static void wasteDestNoSchrab(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            int rand;
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (block == Blocks.GLASS || block == Blocks.WHITE_STAINED_GLASS ||
                    block == Blocks.OAK_DOOR || block == Blocks.IRON_DOOR ||
                    block == Blocks.OAK_LEAVES || block == Blocks.BIRCH_LEAVES ||
                    block == Blocks.SPRUCE_LEAVES || block == Blocks.JUNGLE_LEAVES ||
                    block == Blocks.ACACIA_LEAVES || block == Blocks.DARK_OAK_LEAVES) {
                level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
            } else if (block == Blocks.GRASS_BLOCK) {
                level.setBlock(pos, ModBlocks.WASTE_EARTH.get().defaultBlockState(), 3);
            } else if (block == Blocks.MYCELIUM) {
                level.setBlock(pos, ModBlocks.WASTE_MYCELIUM.get().defaultBlockState(), 3);
            } else if (block == Blocks.SAND) {
                rand = random.nextInt(20);
                if (rand == 1) {
                    level.setBlock(pos, ModBlocks.WASTE_TRINITITE.get().defaultBlockState(), 3);
                }
            } else if (block == Blocks.RED_SAND) {
                rand = random.nextInt(20);
                if (rand == 1) {
                    level.setBlock(pos, ModBlocks.WASTE_TRINITITE_RED.get().defaultBlockState(), 3);
                }
            } else if (block == Blocks.CLAY) {
                level.setBlock(pos, Blocks.TERRACOTTA.defaultBlockState(), 3);
            } else if (block == Blocks.MOSSY_COBBLESTONE) {
                level.setBlock(pos, Blocks.COAL_ORE.defaultBlockState(), 3);
            } else if (block == Blocks.COAL_ORE) {
                rand = random.nextInt(30);
                if (rand == 1 || rand == 2 || rand == 3) {
                    level.setBlock(pos, Blocks.DIAMOND_ORE.defaultBlockState(), 3);
                }
                if (rand == 29) {
                    level.setBlock(pos, Blocks.EMERALD_ORE.defaultBlockState(), 3);
                }
            } else if (block == Blocks.OAK_LOG || block == Blocks.BIRCH_LOG || block == Blocks.SPRUCE_LOG ||
                    block == Blocks.JUNGLE_LOG || block == Blocks.ACACIA_LOG || block == Blocks.DARK_OAK_LOG) {
                level.setBlock(pos, ModBlocks.WASTE_LOG.get().defaultBlockState(), 3);
            } else if (block == Blocks.OAK_PLANKS || block == Blocks.BIRCH_PLANKS || block == Blocks.SPRUCE_PLANKS ||
                    block == Blocks.JUNGLE_PLANKS || block == Blocks.ACACIA_PLANKS || block == Blocks.DARK_OAK_PLANKS) {
                level.setBlock(pos, ModBlocks.WASTE_PLANKS.get().defaultBlockState(), 3);
            } else if (block == Blocks.BROWN_MUSHROOM_BLOCK) {
                // Проверяем, является ли блок стеблем (все грани = true)
                boolean isStem = state.getValue(BlockStateProperties.NORTH) && state.getValue(BlockStateProperties.SOUTH) &&
                        state.getValue(BlockStateProperties.EAST) && state.getValue(BlockStateProperties.WEST) &&
                        state.getValue(BlockStateProperties.UP) && state.getValue(BlockStateProperties.DOWN);

                if (isStem) {
                    level.setBlock(pos, ModBlocks.WASTE_LOG.get().defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            } else if (block == Blocks.RED_MUSHROOM_BLOCK) {
                boolean isStem = state.getValue(BlockStateProperties.NORTH) && state.getValue(BlockStateProperties.SOUTH) &&
                        state.getValue(BlockStateProperties.EAST) && state.getValue(BlockStateProperties.WEST) &&
                        state.getValue(BlockStateProperties.UP) && state.getValue(BlockStateProperties.DOWN);

                if (isStem) {
                    level.setBlock(pos, ModBlocks.WASTE_LOG.get().defaultBlockState(), 3);
                } else {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }
    }

    public static void emp(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            Block b = state.getBlock();
            BlockEntity te = level.getBlockEntity(pos);

            if (te != null && te instanceof IEnergyStorage energy) {
                energy.extractEnergy(energy.getEnergyStored(), false);
                if (random.nextInt(5) < 1)
                    level.setBlock(pos, ModBlocks.BLOCK_ELECTRICAL_SCRAP.get().defaultBlockState(), 3);
            }
            if ((b == ModBlocks.FUSION_CONDUCTOR.get() || b == ModBlocks.FUSION_MOTOR.get() ||
                    b == ModBlocks.FUSION_HEATER.get()) && random.nextInt(10) == 0)
                level.setBlock(pos, ModBlocks.BLOCK_ELECTRICAL_SCRAP.get().defaultBlockState(), 3);
        }
    }

    public static void solinium(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (block == Blocks.GRASS_BLOCK || block == Blocks.MYCELIUM ||
                    block == ModBlocks.WASTE_EARTH.get() || block == ModBlocks.WASTE_MYCELIUM.get()) {
                level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
                return;
            }

            // Список органических блоков
            if (block == Blocks.CACTUS ||
                    block == Blocks.SPONGE || block == Blocks.WET_SPONGE) {
                level.removeBlock(pos, false);
                return;
            }

            // Проверка через теги
            if (state.is(BlockTags.LEAVES) ||
                    state.is(BlockTags.FLOWERS) ||
                    state.is(BlockTags.SAPLINGS) ||
                    state.is(BlockTags.CROPS) ||
                    state.is(BlockTags.LOGS) ||
                    state.is(BlockTags.PLANKS) ||
                    state.is(BlockTags.CORAL_BLOCKS)) {
                level.removeBlock(pos, false);
            }
        }
    }
}