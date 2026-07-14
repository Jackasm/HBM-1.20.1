package com.hbm.entity.missile;

import com.hbm.entity.ModEntities;
import com.hbm.entity.logic.EntityEMP;
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

public abstract class EntityMissileTier2 extends EntityMissileBaseNT {

    public EntityMissileTier2(EntityType<? extends EntityMissileTier2> entityType, Level level) {
        super(entityType, level);
    }

    public EntityMissileTier2(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileTier2> entityType) {
        super(level, x, y, z, targetX, targetZ, entityType);
    }

    @Override
    public List<ItemStack> getDebris() {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(ModItems.PLATE_STEEL.get(), 10));
        list.add(new ItemStack(ModItems.PLATE_TITANIUM.get(), 6));
        list.add(new ItemStack(ModItems.THRUSTER_MEDIUM.get(), 1));
        return list;
    }

    @Override
    public String getUnlocalizedName() {
        return "radar.target.tier2";
    }

    @Override
    public int getBlipLevel() {
        return 2; // TIER2
    }

    public static class EntityMissileStrong extends EntityMissileTier2 {
        public EntityMissileStrong(EntityType<? extends EntityMissileStrong> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileStrong(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileStrong> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            this.explodeStandard(30F, 32, false);
            ExplosionCreator.composeEffectStandard(level(), getX(), getY(), getZ());
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_GENERIC_MEDIUM.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_STRONG.get());
        }
    }

    public static class EntityMissileIncendiaryStrong extends EntityMissileTier2 {
        public EntityMissileIncendiaryStrong(EntityType<? extends EntityMissileIncendiaryStrong> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileIncendiaryStrong(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileIncendiaryStrong> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            this.explodeStandard(30F, 32, true);
            ExplosionCreator.composeEffectStandard(level(), getX(), getY(), getZ());
            ExplosionChaos.flameDeath(level(), BlockPos.containing(getX(), getY(), getZ()), 25);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_INCENDIARY_MEDIUM.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_INCENDIARY_STRONG.get());
        }
    }

    public static class EntityMissileClusterStrong extends EntityMissileTier2 {
        public EntityMissileClusterStrong(EntityType<? extends EntityMissileClusterStrong> entityType, Level level) {
            super(entityType, level);
            this.isCluster = true;
        }

        public EntityMissileClusterStrong(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileClusterStrong> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
            this.isCluster = true;
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            level().explode(this, getX(), getY(), getZ(), 15F, Level.ExplosionInteraction.TNT);
            ExplosionChaos.cluster(level(), BlockPos.containing(getX(), getY(), getZ()), 50, 100);
        }

        @Override
        public void cluster() {
            this.onMissileImpact(null);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_CLUSTER_MEDIUM.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_CLUSTER_STRONG.get());
        }
    }

    public static class EntityMissileBusterStrong extends EntityMissileTier2 {
        public EntityMissileBusterStrong(EntityType<? extends EntityMissileBusterStrong> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileBusterStrong(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileBusterStrong> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            for (int i = 0; i < 20; i++) {
                level().explode(this, getX(), getY() - i, getZ(), 7.5F, Level.ExplosionInteraction.TNT);
            }
            ExplosionLarge.spawnParticles(level(), getX(), getY(), getZ(), 8);
            ExplosionLarge.spawnShrapnels(level(), getX(), getY(), getZ(), 8);
            ExplosionLarge.spawnRubble(level(), getX(), getY(), getZ(), 8);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_BUSTER_MEDIUM.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_BUSTER_STRONG.get());
        }
    }

    public static class EntityMissileEMPStrong extends EntityMissileTier2 {
        public EntityMissileEMPStrong(EntityType<? extends EntityMissileEMPStrong> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileEMPStrong(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileEMPStrong> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            EntityEMP emp = new EntityEMP(ModEntities.EMP.get(), level());
            emp.setPos(getX(), getY(), getZ());
            level().addFreshEntity(emp);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_GENERIC_MEDIUM.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_EMP_STRONG.get());
        }
    }
}