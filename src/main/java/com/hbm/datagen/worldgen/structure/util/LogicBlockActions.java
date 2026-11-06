package com.hbm.datagen.worldgen.structure.util;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.LogicBlock;
import com.hbm.entity.ModEntities;
import com.hbm.entity.item.EntityFallingBlockNT;
import com.hbm.entity.missile.EntityMissileTier2;
import com.hbm.entity.mob.CustomSkeleton;
import com.hbm.entity.mob.EntityUndeadSoldier;
import com.hbm.entity.mob.util.MobUtil;

import com.hbm.itempool.ItemPool;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModGunItems;
import com.hbm.items.ModItems;
import com.hbm.tileentity.TileEntityDoorGeneric;
import com.hbm.tileentity.TileEntityLockableBase;
import com.hbm.tileentity.block.TileEntitySkeletonHolder;
import com.hbm.tileentity.bomb.TileEntityCharge;

import com.hbm.tileentity.storage.TileEntityCrateBase;
import com.hbm.util.ContaminationUtil;

import com.hbm.world.WorldUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.function.Consumer;

public class LogicBlockActions {

    public static LinkedHashMap<String, Consumer<LogicBlock.TileEntityLogicBlock>> actions;

    public static Consumer<LogicBlock.TileEntityLogicBlock> PHASE_ABERRATOR = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        if (tile.phase == 1 || tile.phase == 2) {
            tile.player = world.getNearestPlayer(x, y, z, 25, false);
            if (tile.timer == 0) {
                Vec3 vec = new Vec3(20, 0, 0);
                for (int i = 0; i < 10; i++) {
                    if (vec.x > 8) vec = vec.add(world.random.nextInt(10) - 5, 0, 0);

                    EntityUndeadSoldier mob = new EntityUndeadSoldier(ModEntities.UNDEAD_SOLDIER.get(), world);
                    for (int j = 0; j < 7; j++) {
                        int spawnX = (int) (x + 0.5 + vec.x);
                        int spawnZ = (int) (z + 0.5 + vec.z);
                        mob.moveTo(spawnX, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, spawnX, spawnZ), spawnZ, i * 36F, 0);
                        if (mob.checkSpawnRules(world, net.minecraft.world.entity.MobSpawnType.NATURAL)) {
                            if (tile.player != null) mob.setTarget(tile.player);
                            world.addFreshEntity(mob);
                            break;
                        }
                    }
                    vec = vec.yRot((float) Math.toRadians(36D));
                }
            }
        }
        if (tile.phase > 2) {
            var te = world.getBlockEntity(new BlockPos(x, y + 18, z));
            if (te instanceof TileEntitySkeletonHolder skeleton) {
                if (world.random.nextInt(5) == 0) {
                    skeleton.item = new ItemStack(ModItems.ITEM_SECRET_ABERRATOR.get(), 1);

                } else {
                    skeleton.item = new ItemStack(ModItems.CLAY_TABLET.get(), 1);
                    skeleton.item.getOrCreateTag().putInt("CustomModelData", 1);
                }
                skeleton.setChanged();
                world.sendBlockUpdated(new BlockPos(x, y + 18, z), world.getBlockState(new BlockPos(x, y + 18, z)), world.getBlockState(new BlockPos(x, y + 18, z)), 3);
            }
            world.setBlockAndUpdate(new BlockPos(x, y, z), Blocks.OBSIDIAN.defaultBlockState());
        }
    };

    public static Consumer<LogicBlock.TileEntityLogicBlock> COLLAPSE_ROOF_RAD_5 = (tile) -> collapseRoof(tile, 4);

    public static Consumer<LogicBlock.TileEntityLogicBlock> COLLAPSE_ROOF_RAD_10 = (tile) -> collapseRoof(tile, 8);

    private static void collapseRoof(LogicBlock.TileEntityLogicBlock tile, int r) {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        if (tile.phase == 0) return;

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
                        BlockPos p = new BlockPos(X, Y, Z);
                        var state = world.getBlockState(p);
                        if (state.getBlock().getExplosionResistance() <= 70) {
                            EntityFallingBlockNT falling = new EntityFallingBlockNT(
                                    ModEntities.FALLING_BLOCK_NT.get(), world,
                                    X + 0.5, Y + 0.5, Z + 0.5, state);
                            world.addFreshEntity(falling);
                        }
                    }
                }
            }
        }
        world.setBlockAndUpdate(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState());
    }

    public static Consumer<LogicBlock.TileEntityLogicBlock> FODDER_WAVE = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        if (tile.phase == 1) {
            Vec3 vec = new Vec3(5, 0, 0);
            for (int i = 0; i < 10; i++) {
                Zombie mob = new Zombie(EntityType.ZOMBIE, world);
                mob.moveTo(x + 0.5 + vec.x,
                        world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z),
                        z + 0.5 + vec.z,
                        i * 36F, 0);
                MobUtil.assignItemsToEntity(mob, MobUtil.ADVANCED_ARMOR_POOLS, world.random);
                mob.setAggressive(true);
                world.addFreshEntity(mob);
                vec = vec.yRot((float) Math.toRadians(36D));
            }
            world.setBlockAndUpdate(new BlockPos(x, y, z), ModBlocks.BLOCK_STEEL.get().defaultBlockState());
        }
    };

    public static Consumer<LogicBlock.TileEntityLogicBlock> SKELETONS_GUN_TIER_1 = (tile) -> spawnSkeleton(tile, 1);
    public static Consumer<LogicBlock.TileEntityLogicBlock> SKELETONS_GUN_TIER_2 = (tile) -> spawnSkeleton(tile, 2);
    public static Consumer<LogicBlock.TileEntityLogicBlock> SKELETONS_GUN_TIER_3 = (tile) -> spawnSkeleton(tile, 3);

    private static void spawnSkeleton(LogicBlock.TileEntityLogicBlock tile, int tier) {
        Level world = tile.getLevel();
        if (world == null) return;
        BlockPos pos = tile.getBlockPos();

        if (tile.phase != 1) return;

        // Выбор пулов по tier'у
        ItemPool gunPool;
        Map<Integer, ItemPool> armorPool;
        switch (tier) {
            case 1 -> { gunPool = MobUtil.SKELETON_GUN_POOLS_T1; armorPool = MobUtil.RANGED_ARMOR_POOLS; }
            case 2 -> { gunPool = MobUtil.SKELETON_GUN_POOLS_T2; armorPool = MobUtil.RANGED_ARMOR_POOLS; }
            default -> { gunPool = MobUtil.SKELETON_GUN_POOLS_T3; armorPool = MobUtil.ADVANCED_RANGED_ARMOR_POOLS; }
        }

        for (int i = 0; i < 3; i++) {
            CustomSkeleton mob = new CustomSkeleton(ModEntities.CUSTOM_SKELETON.get(), world);

            mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);

            // 1. finalizeSpawn — он поставит лук, но нам плевать
            mob.finalizeSpawn((ServerLevelAccessor) world,
                    world.getCurrentDifficultyAt(pos),
                    MobSpawnType.NATURAL, null, null);

            // 2. Броня из пула (слоты 1..4, оружие не трогает)
            MobUtil.assignItemsToEntity(mob, armorPool, world.random);

            // 3. Оружие — ЗАМЕНЯЕТ лук, как в decorateSkeleton
            ItemStack gun = gunPool.generateOne(world.random);
            if (!gun.isEmpty()) {
                mob.setItemSlot(EquipmentSlot.MAINHAND, gun);
            } else {
                // на случай, если пул вернул empty — вернём лук, чтобы скелет не бегал с пустыми руками
                mob.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(net.minecraft.world.item.Items.BOW));
            }

            // 4. Пересобрать goals, чтобы RangedGunAttackGoal подхватился
            mob.reassessWeaponGoal();

            world.addFreshEntity(mob);
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    public static Consumer<LogicBlock.TileEntityLogicBlock> ZOMBIES_TIER_1 = (tile) -> spawnUndead(tile, 1);

    public static Consumer<LogicBlock.TileEntityLogicBlock> ZOMBIES_TIER_2 = (tile) -> spawnUndead(tile, 2);

    private static void spawnUndead(LogicBlock.TileEntityLogicBlock tile, int tier) {
        Level world = tile.getLevel();
        if (world == null) return;
        BlockPos pos = tile.getBlockPos();

        if (tile.phase != 1) return;

        for (int i = 0; i < 3; i++) {
            EntityUndeadSoldier mob = new EntityUndeadSoldier(ModEntities.UNDEAD_SOLDIER.get(), world);
            mob.setType(EntityUndeadSoldier.TYPE_ZOMBIE);
            mob.moveTo(pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0);

            if (tier == 1) {
                MobUtil.assignItemsToEntity(mob, MobUtil.COMMON_ARMOR_POOLS, world.random);
            } else {
                MobUtil.assignItemsToEntity(mob, MobUtil.ADVANCED_ARMOR_POOLS, world.random);
            }

            mob.reassessWeaponGoal();
            mob.setAggressive(true);

            world.addFreshEntity(mob);
            world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
        }
    }

    public static Consumer<LogicBlock.TileEntityLogicBlock> PUZZLE_TEST = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        if (tile.phase == 2) {
            world.setBlockAndUpdate(new BlockPos(x, y, z), ModBlocks.CRATE_STEEL.get().defaultBlockState());

            LightningBolt bolt = new LightningBolt(EntityType.LIGHTNING_BOLT, world);
            bolt.moveTo(x, world.getHeight(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, x, z) + 2, z);
            world.addFreshEntity(bolt);

            var te = world.getBlockEntity(new BlockPos(x, y, z));
            if (te instanceof TileEntityCrateBase crate) {
                Objects.requireNonNull(crate).setItem(15, new ItemStack(ModGunItems.GUN_BOLTER.get()));
            }
        }
    };

    public static Consumer<LogicBlock.TileEntityLogicBlock> MISSILE_STRIKE = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        if (tile.phase != 1) return;

        Player player = world.getNearestPlayer(x, y, z, 25, false);
        if (player != null) {
            player.displayClientMessage(Component.literal(ChatFormatting.LIGHT_PURPLE + "[COMMAND UNIT]" + ChatFormatting.RESET + " Missile Fired"), false);
        }

        Direction dir = tile.direction;
        Direction parallel = dir.getClockWise();

        EntityMissileTier2.EntityMissileStrong missile =
                new EntityMissileTier2.EntityMissileStrong(
                        world,
                        x + dir.getStepX() * 300,
                        200,
                        z + dir.getStepZ() * 300,
                        x + parallel.getStepX() * 30 + dir.getStepX() * 30,
                        z + parallel.getStepZ() * 30 + dir.getStepZ() * 30,
                        ModEntities.MISSILE_STRONG.get());
        WorldUtil.loadAndSpawnEntityInWorld(missile);

        world.setBlockAndUpdate(new BlockPos(x, y, z), ModBlocks.BLOCK_ELECTRICAL_SCRAP.get().defaultBlockState());
    };

    public static Consumer<LogicBlock.TileEntityLogicBlock> RAD_CONTAINMENT_SYSTEM = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        Direction direction = tile.direction.getOpposite();
        Direction rot = direction.getClockWise();

        AABB bb = new AABB(
                x - rot.getStepX(), y - 1, z - rot.getStepZ(),
                x + rot.getStepX() + direction.getStepX() * 15, y + 1, z + rot.getStepZ() + direction.getStepZ() * 15
        ).inflate(2, 2, 2);

        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, bb);

        for (LivingEntity e : entities) {
            Vec3 vec = new Vec3(e.getX() - (x + 0.5), (e.getY() + e.getEyeHeight()) - (y + 0.5), e.getZ() - (z + 0.5));
            double len = vec.length();
            vec = vec.normalize();
            len = Math.max(len, 1D);

            float res = 0;
            for (int i = 1; i < len; i++) {
                int ix = (int) Math.floor(x + 0.5 + vec.x * i);
                int iy = (int) Math.floor(y + 0.5 + vec.y * i);
                int iz = (int) Math.floor(z + 0.5 + vec.z * i);
                res += world.getBlockState(new BlockPos(ix, iy, iz)).getBlock().getExplosionResistance();
            }
            if (res < 1) res = 1;

            float eRads = 100F;
            eRads /= res;
            eRads /= (float) (len * len);

            ContaminationUtil.contaminate(e, ContaminationUtil.HazardType.RADIATION, ContaminationUtil.ContaminationType.HAZMAT2, eRads);
        }

        if (tile.phase == 2 && tile.timer > 40) {
            Player player = world.getNearestPlayer(x, y, z, 25, false);
            if (player != null) {
                player.displayClientMessage(Component.literal(
                        ChatFormatting.LIGHT_PURPLE + "[RAD CONTAINMENT SYSTEM]" +
                                ChatFormatting.RESET + " Diagnostics found containment failure, commencing lockdown"), false);
            }

            for (int i = 1; i < 20; i++) {
                int checkX = x + direction.getStepX() * i;
                int checkY = y + 1;
                int checkZ = z + direction.getStepZ() * i;
                BlockPos checkPos = new BlockPos(checkX, checkY, checkZ);
                Block block = world.getBlockState(checkPos).getBlock();
                var te = world.getBlockEntity(checkPos);

                if (block instanceof BlockDummyable) {
                    BlockPos core = ((BlockDummyable) block).findCore(world, checkPos);
                    if (core != null) te = world.getBlockEntity(core);
                }

                if (te instanceof TileEntityDoorGeneric door) {
                    Objects.requireNonNull(door).setPins(456);
                    door.close();
                    door.lock();
                    break;
                }
            }
            tile.phase = 3;
        }
    };

    public static Consumer<LogicBlock.TileEntityLogicBlock> POWER_LOCK = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        AABB box = new AABB(x, y, z, x + 1, y - 2, z + 1).inflate(3, 3, 3);
        if (tile.phase == 0 && !world.getEntitiesOfClass(Player.class, box).isEmpty()) {
            Player player = world.getNearestPlayer(x, y, z, 300, false);
            if (player != null) {
                player.displayClientMessage(Component.literal(
                        ChatFormatting.LIGHT_PURPLE + "[POWER LOCK]" +
                                ChatFormatting.RESET + " Low Power Warning! Locking Safe"), false);
            }
            tile.phase++;

            TileEntityLockableBase safe = null;
            for (Direction dir : Direction.values()) {
                BlockPos p = new BlockPos(x + dir.getStepX(), y + dir.getStepY(), z + dir.getStepZ());
                if (world.getBlockEntity(p) instanceof TileEntityLockableBase lockable) {
                    safe = lockable;
                    break;
                }
            }
            if (safe != null) safe.setPins(world.random.nextInt(999));
        }
    };

    public static Consumer<LogicBlock.TileEntityLogicBlock> BOMB_TRAP = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        Direction direction = tile.direction.getOpposite();

        if (tile.phase == 1) {
            BlockPos trapPos = new BlockPos(x, y, z + direction.getStepZ());
            world.setBlockAndUpdate(trapPos, ModBlocks.CHARGE_C4.get().defaultBlockState());

            var te = world.getBlockEntity(trapPos);
            if (te instanceof TileEntityCharge bomb) {
                Objects.requireNonNull(bomb).timer = 2400;
                bomb.started = true;
            }

            world.setBlockAndUpdate(new BlockPos(x, y, z),
                    tile.disguise != null ? tile.disguise.defaultBlockState() : Blocks.AIR.defaultBlockState());
        }
    };

    public static Consumer<LogicBlock.TileEntityLogicBlock> BOMB_CRANE = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        if (tile.phase == 0) {
            world.setBlockAndUpdate(new BlockPos(x, y + 1, z), ModBlocks.CHARGE_C4.get().defaultBlockState());
            var te = world.getBlockEntity(new BlockPos(x, y + 1, z));
            if (te instanceof TileEntityCharge bomb) Objects.requireNonNull(bomb).timer = 1200;
        }

        if (tile.phase >= 1) {
            var te = world.getBlockEntity(new BlockPos(x, y + 1, z));
            if (te instanceof TileEntityCharge bomb) Objects.requireNonNull(bomb).started = true;
            world.setBlockAndUpdate(new BlockPos(x, y, z), ModBlocks.BLOCK_STEEL.get().defaultBlockState());
        }
    };

    public static Consumer<LogicBlock.TileEntityLogicBlock> DEAD_GUY_CRANE = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        if (tile.phase == 1) {
            world.setBlockAndUpdate(new BlockPos(x, y, z), ModBlocks.SKELETON_HOLDER.get().defaultBlockState());
            var te = world.getBlockEntity(new BlockPos(x, y, z));

            List<Player> players = world.getEntitiesOfClass(Player.class, new AABB(x, y, z, x + 1, y - 2, z + 1).inflate(25, 25, 25));
            Player player = players.isEmpty() ? null : players.get(0);

            if (te instanceof TileEntitySkeletonHolder skeleton) {
                if (player != null && player.getInventory().contains(new ItemStack(ModGunItems.GUN_HANGMAN.get()))) {
                    skeleton.item = new ItemStack(ModItems.CLAY_TABLET.get(), 1);
                } else {
                    skeleton.item = new ItemStack(ModGunItems.GUN_HANGMAN.get());
                }
                skeleton.setChanged();
                world.sendBlockUpdated(new BlockPos(x, y, z), world.getBlockState(new BlockPos(x, y, z)), world.getBlockState(new BlockPos(x, y, z)), 3);
            }
        }
    };

    public static Consumer<LogicBlock.TileEntityLogicBlock> DEAD_GUY_BASE_TOWER = (tile) -> {
        Level world = tile.getLevel();
        if (world == null) return;
        int x = tile.getBlockPos().getX();
        int y = tile.getBlockPos().getY();
        int z = tile.getBlockPos().getZ();

        if (tile.phase == 1) {
            world.setBlockAndUpdate(new BlockPos(x, y, z), ModBlocks.SKELETON_HOLDER.get().defaultBlockState());
            var te = world.getBlockEntity(new BlockPos(x, y, z));

            if (te instanceof TileEntitySkeletonHolder skeleton) {
                int roll = world.random.nextInt(100);
                ItemStack ammo;

                if (roll < 44) {
                    ammo = new ItemStack(ModAmmoItems.AMMO_CT_MORTAR.get(), 3);
                    skeleton.item = ammo;
                } else if (roll < 71) {
                    ammo = new ItemStack(ModAmmoItems.AMMO_CT_MORTAR_CHARGE.get(), 1);
                    skeleton.item = ammo;
                } else if (roll < 92) {
                    ammo = new ItemStack(ModAmmoItems.AMMO_NUKE_STANDARD.get(), 1);
                    skeleton.item = ammo;
                } else if (roll < 96) {
                    skeleton.item = new ItemStack(ModItems.ITEM_SECRET_ABERRATOR.get());
                } else {
                    skeleton.item = new ItemStack(ModItems.ITEM_SECRET_FOLLY.get());
                }
                skeleton.setChanged();
            }
        }
    };

    public static List<String> getActionNames() {
        return new ArrayList<>(actions.keySet());
    }

    static {
        initialize();
    }

    public static void initialize() {
        actions = new LinkedHashMap<>();
        actions.put("FODDER_WAVE", FODDER_WAVE);
        actions.put("POWER_LOCK", POWER_LOCK);
        actions.put("COLLAPSE_ROOF_RAD_5", COLLAPSE_ROOF_RAD_5);
        actions.put("COLLAPSE_ROOF_RAD_10", COLLAPSE_ROOF_RAD_10);
        actions.put("BOMB_TRAP", BOMB_TRAP);
        actions.put("BOMB_CRANE", BOMB_CRANE);
        actions.put("DEAD_GUY_CRANE", DEAD_GUY_CRANE);

        actions.put("SKELETON_GUN_TIER_1", SKELETONS_GUN_TIER_1);
        actions.put("SKELETON_GUN_TIER_2", SKELETONS_GUN_TIER_2);
        actions.put("SKELETON_GUN_TIER_3", SKELETONS_GUN_TIER_3);

        actions.put("ZOMBIE_TIER_1", ZOMBIES_TIER_1);
        actions.put("ZOMBIE_TIER_2", ZOMBIES_TIER_2);

        actions.put("ABERRATOR", PHASE_ABERRATOR);
        actions.put("PUZZLE_TEST", PUZZLE_TEST);
        actions.put("MISSILE_STRIKE", MISSILE_STRIKE);
        actions.put("IRRADIATE_ENTITIES_AOE", RAD_CONTAINMENT_SYSTEM);
    }

    private static void equipArmor(LivingEntity mob, ItemStack helmet, ItemStack chest, ItemStack legs, ItemStack boots) {
        mob.setItemSlot(EquipmentSlot.HEAD, helmet);
        mob.setItemSlot(EquipmentSlot.CHEST, chest);
        mob.setItemSlot(EquipmentSlot.LEGS, legs);
        mob.setItemSlot(EquipmentSlot.FEET, boots);
    }

    private static void spawnMob(Level world, LivingEntity mob, int x, int y, int z) {
        mob.moveTo(x + 0.5, y, z + 0.5, mob.getYRot(), mob.getXRot());
        // Экипировка уже стоит до этого
        world.addFreshEntity(mob);
    }
}