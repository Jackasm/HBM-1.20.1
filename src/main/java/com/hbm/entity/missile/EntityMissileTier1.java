package com.hbm.entity.missile;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.items.ModItems;
import com.hbm.particle.helper.ExplosionCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityMissileTier1 extends EntityMissileBaseNT {

    public EntityMissileTier1(EntityType<? extends EntityMissileTier1> entityType, Level level) {
        super(entityType, level);
    }

    public EntityMissileTier1(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileTier1> entityType) {
        super(level, x, y, z, targetX, targetZ, entityType);
    }

    @Override
    public List<ItemStack> getDebris() {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(ModItems.PLATE_TITANIUM.get(), 4));
        list.add(new ItemStack(ModItems.THRUSTER_SMALL.get(), 1));
        return list;
    }

    @Override
    protected float getContrailScale() {
        return 0.5F;
    }

    public static class EntityMissileGeneric extends EntityMissileTier1 {
        public EntityMissileGeneric(EntityType<? extends EntityMissileGeneric> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileGeneric(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileGeneric> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            this.explodeStandard(15F, 24, false);
            ExplosionCreator.composeEffectSmall(level(), getX(), getY(), getZ());
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_GENERIC_SMALL.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_GENERIC.get());
        }
    }

    public static class EntityMissileDecoy extends EntityMissileTier1 {
        public EntityMissileDecoy(EntityType<? extends EntityMissileDecoy> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileDecoy(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileDecoy> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            level().explode(this, getX(), getY(), getZ(), 4F, Level.ExplosionInteraction.NONE);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.INGOT_STEEL.get());
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
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_DECOY.get());
        }
    }

    public static class EntityMissileIncendiary extends EntityMissileTier1 {
        public EntityMissileIncendiary(EntityType<? extends EntityMissileIncendiary> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileIncendiary(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileIncendiary> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            this.explodeStandard(15F, 24, true);
            ExplosionCreator.composeEffectSmall(level(), getX(), getY(), getZ());
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_INCENDIARY_SMALL.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_INCENDIARY.get());
        }
    }

    public static class EntityMissileCluster extends EntityMissileTier1 {
        public EntityMissileCluster(EntityType<? extends EntityMissileCluster> entityType, Level level) {
            super(entityType, level);
            this.isCluster = true;
        }

        public EntityMissileCluster(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileCluster> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
            this.isCluster = true;
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            level().explode(this, getX(), getY(), getZ(), 5F, Level.ExplosionInteraction.TNT);
            ExplosionChaos.cluster(level(), BlockPos.containing(getX(), getY(), getZ()), 25, 100);
        }

        @Override
        public void cluster() {
            this.onMissileImpact(null);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_CLUSTER_SMALL.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_CLUSTER.get());
        }
    }

    public static class EntityMissileBunkerBuster extends EntityMissileTier1 {
        public EntityMissileBunkerBuster(EntityType<? extends EntityMissileBunkerBuster> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileBunkerBuster(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileBunkerBuster> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            for (int i = 0; i < 15; i++) {
                level().explode(this, getX(), getY() - i, getZ(), 5F, Level.ExplosionInteraction.TNT);
            }
            ExplosionLarge.spawnParticles(level(), getX(), getY(), getZ(), 5);
            ExplosionLarge.spawnShrapnels(level(), getX(), getY(), getZ(), 5);
            ExplosionLarge.spawnRubble(level(), getX(), getY(), getZ(), 5);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_BUSTER_SMALL.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_BUSTER.get());
        }
    }
}