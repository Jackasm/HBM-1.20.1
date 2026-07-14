package com.hbm.entity.missile;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionLarge;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.items.ModItems;
import com.hbm.particle.helper.ExplosionCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityMissileTier3 extends EntityMissileBaseNT {

    public EntityMissileTier3(EntityType<? extends EntityMissileTier3> entityType, Level level) {
        super(entityType, level);
    }

    public EntityMissileTier3(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileTier3> entityType) {
        super(level, x, y, z, targetX, targetZ, entityType);
    }

    @Override
    public List<ItemStack> getDebris() {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(ModItems.PLATE_STEEL.get(), 16));
        list.add(new ItemStack(ModItems.PLATE_TITANIUM.get(), 10));
        list.add(new ItemStack(ModItems.THRUSTER_LARGE.get(), 1));
        return list;
    }

    @Override
    public String getUnlocalizedName() {
        return "radar.target.tier3";
    }

    @Override
    public int getBlipLevel() {
        return 3; // TIER3
    }

    @Override
    protected void spawnContrail() {
        Vec3 thrust = new Vec3(0, 0, 0.5);
        thrust = thrust.yRot((this.getYRot() + 90) * (float) Math.PI / 180F);
        thrust = thrust.xRot(this.getXRot() * (float) Math.PI / 180F);
        thrust = thrust.yRot(-(this.getYRot() + 90) * (float) Math.PI / 180F);

        this.spawnContrailWithOffset(thrust.x, thrust.y, thrust.z);
        this.spawnContrailWithOffset(-thrust.z, thrust.y, thrust.x);
        this.spawnContrailWithOffset(-thrust.x, -thrust.z, -thrust.z);
        this.spawnContrailWithOffset(thrust.z, -thrust.z, -thrust.x);
    }

    public static class EntityMissileBurst extends EntityMissileTier3 {
        public EntityMissileBurst(EntityType<? extends EntityMissileBurst> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileBurst(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileBurst> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            this.explodeStandard(50F, 48, false);
            ExplosionCreator.composeEffectLarge(level(), getX(), getY(), getZ());
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_GENERIC_LARGE.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_BURST.get());
        }
    }

    public static class EntityMissileInferno extends EntityMissileTier3 {
        public EntityMissileInferno(EntityType<? extends EntityMissileInferno> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileInferno(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileInferno> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            this.explodeStandard(50F, 48, true);
            ExplosionCreator.composeEffectLarge(level(), getX(), getY(), getZ());
            ExplosionChaos.burn(level(), BlockPos.containing(getX(), getY(), getZ()), 10);
            ExplosionChaos.flameDeath(level(), BlockPos.containing(getX(), getY(), getZ()), 25);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_INCENDIARY_LARGE.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_INFERNO.get());
        }
    }

    public static class EntityMissileRain extends EntityMissileTier3 {
        public EntityMissileRain(EntityType<? extends EntityMissileRain> entityType, Level level) {
            super(entityType, level);
            this.isCluster = true;
        }

        public EntityMissileRain(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileRain> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
            this.isCluster = true;
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            level().explode(this, getX(), getY(), getZ(), 25F, Level.ExplosionInteraction.TNT);
            ExplosionChaos.cluster(level(), BlockPos.containing(getX(), getY(), getZ()), 100, 100);
        }

        @Override
        public void cluster() {
            this.onMissileImpact(null);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_CLUSTER_LARGE.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_RAIN.get());
        }
    }

    public static class EntityMissileDrill extends EntityMissileTier3 {
        public EntityMissileDrill(EntityType<? extends EntityMissileDrill> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileDrill(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileDrill> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            for (int i = 0; i < 30; i++) {
                ExplosionVNT explosion = new ExplosionVNT(level(), getX(), getY() - i, getZ(), 10F);
                explosion.addAllAttrib(new ExplosionVNT.ExAttrib[]{ExplosionVNT.ExAttrib.ERRODE});
                explosion.explode();
            }
            ExplosionLarge.spawnParticles(level(), getX(), getY(), getZ(), 25);
            ExplosionLarge.spawnShrapnels(level(), getX(), getY(), getZ(), 12);
            ExplosionLarge.jolt(level(), getX(), getY(), getZ(), 10, 50, 1);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.WARHEAD_BUSTER_LARGE.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_DRILL.get());
        }
    }
}