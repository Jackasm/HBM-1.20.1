package com.hbm.entity.missile;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.BombConfig;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.items.ModItems;
import com.hbm.world.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityMissileTier4 extends EntityMissileBaseNT {

    public EntityMissileTier4(EntityType<? extends EntityMissileTier4> entityType, Level level) {
        super(entityType, level);
    }

    public EntityMissileTier4(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileTier4> entityType) {
        super(level, x, y, z, targetX, targetZ, entityType);
    }

    @Override
    public List<ItemStack> getDebris() {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(ModItems.PLATE_TITANIUM.get(), 16));
        list.add(new ItemStack(ModItems.PLATE_STEEL.get(), 20));
        list.add(new ItemStack(ModItems.PLATE_ALUMINIUM.get(), 12));
        list.add(new ItemStack(ModItems.THRUSTER_LARGE.get(), 1));
        return list;
    }

    @Override
    public String getUnlocalizedName() {
        return "radar.target.tier4";
    }

    @Override
    public int getBlipLevel() {
        return 4; // TIER4
    }

    @Override
    protected void spawnContrail() {
        int rot = this.entityData.get(ROTATION);

        Vec3 thrust = new Vec3(0, 0, 1);
        thrust = switch (rot) {
            case 2 -> thrust.yRot((float) -Math.PI / 2F);
            case 4 -> thrust.yRot((float) -Math.PI);
            case 3 -> thrust.yRot((float) -Math.PI / 2F * 3F);
            default -> thrust;
        };
        thrust = thrust.yRot((this.getYRot() + 90) * (float) Math.PI / 180F);
        thrust = thrust.xRot(this.getXRot() * (float) Math.PI / 180F);
        thrust = thrust.yRot(-(this.getYRot() + 90) * (float) Math.PI / 180F);

        this.spawnContrailWithOffset(thrust.x, thrust.y, thrust.z);
        this.spawnContrailWithOffset(0, 0, 0);
        this.spawnContrailWithOffset(-thrust.x, -thrust.z, -thrust.z);
    }

    public static class EntityMissileNuclear extends EntityMissileTier4 {
        public EntityMissileNuclear(EntityType<? extends EntityMissileNuclear> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileNuclear(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileNuclear> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            WorldUtil.loadAndSpawnEntityInWorld(
                    EntityNukeExplosionMK5.statFac(level(), BombConfig.missileRadius.get(), getX(), getY(), getZ()));
            EntityNukeTorex.statFacStandard(level(), getX(), getY(), getZ(), BombConfig.missileRadius.get());
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_NUCLEAR.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_NUCLEAR.get());
        }
    }

    public static class EntityMissileMirv extends EntityMissileTier4 {
        public EntityMissileMirv(EntityType<? extends EntityMissileMirv> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileMirv(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileMirv> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            WorldUtil.loadAndSpawnEntityInWorld(
                    EntityNukeExplosionMK5.statFac(level(), BombConfig.missileRadius.get() * 2, getX(), getY(), getZ()));
            EntityNukeTorex.statFacStandard(level(), getX(), getY(), getZ(), BombConfig.missileRadius.get() * 2);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_MIRV.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_NUCLEAR_CLUSTER.get());
        }
    }

    public static class EntityMissileVolcano extends EntityMissileTier4 {
        public EntityMissileVolcano(EntityType<? extends EntityMissileVolcano> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileVolcano(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileVolcano> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            ExplosionLarge.explode(level(), getX(), getY(), getZ(), 10.0F, true, true, true);
            BlockPos center = BlockPos.containing(getX(), getY(), getZ());
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        BlockPos pos = center.offset(x, y, z);
                        if (level().isEmptyBlock(pos)) {
                            level().setBlock(pos, ModBlocks.VOLCANIC_LAVA_BLOCK.get().defaultBlockState(), 3);
                        }
                    }
                }
            }
            level().setBlock(center, ModBlocks.VOLCANO_CORE.get().defaultBlockState(), 3);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_VOLCANO.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_VOLCANO.get());
        }
    }

    public static class EntityMissileDoomsday extends EntityMissileTier4 {
        public EntityMissileDoomsday(EntityType<? extends EntityMissileDoomsday> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileDoomsday(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileDoomsday> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            WorldUtil.loadAndSpawnEntityInWorld(
                    EntityNukeExplosionMK5.statFac(level(), BombConfig.missileRadius.get() * 2, getX(), getY(), getZ()).moreFallout(100));
            EntityNukeTorex.statFacStandard(level(), getX(), getY(), getZ(), BombConfig.missileRadius.get() * 2);
        }

        @Override
        public List<ItemStack> getDebris() {
            return null;
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return null;
        }

        @Override
        public String getUnlocalizedName() {
            return "radar.target.doomsday";
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_DOOMSDAY.get());
        }
    }

    public static class EntityMissileDoomsdayRusted extends EntityMissileDoomsday {
        public EntityMissileDoomsdayRusted(EntityType<? extends EntityMissileDoomsdayRusted> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileDoomsdayRusted(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileDoomsdayRusted> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            WorldUtil.loadAndSpawnEntityInWorld(
                    EntityNukeExplosionMK5.statFac(level(), BombConfig.missileRadius.get(), getX(), getY(), getZ()).moreFallout(100));
            EntityNukeTorex.statFacStandard(level(), getX(), getY(), getZ(), BombConfig.missileRadius.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_DOOMSDAY_RUSTED.get());
        }
    }
}