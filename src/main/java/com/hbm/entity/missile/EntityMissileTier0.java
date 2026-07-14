package com.hbm.entity.missile;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.BombConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityBlackHole;
import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.effect.EntityEMPBlast;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModItems;

import com.hbm.world.WorldUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityMissileTier0 extends EntityMissileBaseNT {

    public EntityMissileTier0(EntityType<? extends EntityMissileTier0> entityType, Level level) {
        super(entityType, level);
    }

    public EntityMissileTier0(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileTier0> entityType) {
        super(level, x, y, z, targetX, targetZ, entityType);
    }

    @Override
    public List<ItemStack> getDebris() {
        List<ItemStack> list = new ArrayList<>();
        list.add(new ItemStack(ModItems.WIRE_ALUMINIUM.get(), 4));
        list.add(new ItemStack(ModItems.PLATE_TITANIUM.get(), 4));
        list.add(new ItemStack(ModItems.SHELL_ALUMINIUM.get(), 2));
        list.add(new ItemStack(ModItems.DUCTTAPE.get(), 1));
        return list;
    }

    @Override
    protected float getContrailScale() {
        return 0.5F;
    }

    public static class EntityMissileTest extends EntityMissileTier0 {
        public EntityMissileTest(EntityType<? extends EntityMissileTest> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileTest(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileTest> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return null;
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_TEST.get());
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            if (!(mop instanceof BlockHitResult blockHit)) return;
            BlockPos pos = blockHit.getBlockPos();
            int x = pos.getX();
            int y = pos.getY();
            int z = pos.getZ();

            int range = 50;

            for (int iX = -range; iX <= range; iX++) {
                for (int iY = -range; iY <= range; iY++) {
                    for (int iZ = -range; iZ <= range; iZ++) {
                        double dist = Math.sqrt(iX * iX + iY * iY + iZ * iZ);
                        if (dist > range) continue;

                        BlockPos checkPos = new BlockPos(x + iX, y + iY, z + iZ);
                        Block block = level().getBlockState(checkPos).getBlock();
                        int charMeta = (int) MathHelper.clamp(12 - (dist / range) * (dist / range) * 13, 0, 12);

                        if (block.defaultBlockState().isSolid()) {
                            if (block != ModBlocks.SELLAFIELD_SLAKED.get()) {
                                level().setBlock(checkPos, ModBlocks.SELLAFIELD_SLAKED.get().defaultBlockState(), 3);
                            }
                        } else {
                            level().setBlock(checkPos, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    public static class EntityMissileMicro extends EntityMissileTier0 {
        public EntityMissileMicro(EntityType<? extends EntityMissileMicro> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileMicro(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileMicro> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            ExplosionNukeSmall.explode(level(), getX(), getY() + 0.5, getZ(), ExplosionNukeSmall.PARAMS_HIGH);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModAmmoItems.AMMO_NUKE_HIGH.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_MICRO.get());
        }
    }

    public static class EntityMissileSchrabidium extends EntityMissileTier0 {
        public EntityMissileSchrabidium(EntityType<? extends EntityMissileSchrabidium> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileSchrabidium(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileSchrabidium> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            EntityNukeExplosionMK3 ex = EntityNukeExplosionMK3.statFacFleija((ServerLevel) level(), getX(), getY(), getZ(), BombConfig.aSchrabRadius.get());
            if (!ex.isRemoved()) {
                WorldUtil.loadAndSpawnEntityInWorld(ex);
                EntityCloudFleija cloud = new EntityCloudFleija(ModEntities.CLOUD_FLEIJA.get(), level());
                cloud.setPos(getX(), getY(), getZ());
                level().addFreshEntity(cloud);
            }
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return null;
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_SCHRABIDIUM.get());
        }
    }

    public static class EntityMissileBHole extends EntityMissileTier0 {
        public EntityMissileBHole(EntityType<? extends EntityMissileBHole> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileBHole(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileBHole> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            level().explode(this, getX(), getY(), getZ(), 1.5F, Level.ExplosionInteraction.TNT);
            EntityBlackHole bl = new EntityBlackHole(ModEntities.BLACK_HOLE.get(), level());
            bl.setPos(getX(), getY(), getZ());
            level().addFreshEntity(bl);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.GRENADE_BLACK_HOLE.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_BHOLE.get());
        }
    }

    public static class EntityMissileTaint extends EntityMissileTier0 {
        public EntityMissileTaint(EntityType<? extends EntityMissileTaint> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileTaint(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileTaint> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            BlockPos pos = ((BlockHitResult) mop).getBlockPos();
            level().explode(this, getX(), getY(), getZ(), 5.0F, Level.ExplosionInteraction.TNT);

            for (int i = 0; i < 100; i++) {
                int a = random.nextInt(11) + pos.getX() - 5;
                int b = random.nextInt(11) + pos.getY() - 5;
                int c = random.nextInt(11) + pos.getZ() - 5;
                BlockPos checkPos = new BlockPos(a, b, c);
                Block block = level().getBlockState(checkPos).getBlock();
                if (block.defaultBlockState().isSolid() && !level().isEmptyBlock(checkPos)) {
                    level().setBlock(checkPos, ModBlocks.TAINT.get().defaultBlockState(), 2);
                }
            }
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModItems.POWDER_SPARK_MIX.get());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_TAINT.get());
        }
    }

    public static class EntityMissileEMP extends EntityMissileTier0 {
        public EntityMissileEMP(EntityType<? extends EntityMissileEMP> entityType, Level level) {
            super(entityType, level);
        }

        public EntityMissileEMP(Level level, double x, double y, double z, int targetX, int targetZ, EntityType<? extends EntityMissileEMP> entityType) {
            super(level, x, y, z, targetX, targetZ, entityType);
        }

        @Override
        public void onMissileImpact(HitResult mop) {
            BlockPos pos = new BlockPos((int) getX(), (int) getY(), (int) getZ());
            ExplosionNukeGeneric.empBlast(level(), pos.getX(), pos.getY(), pos.getZ(), 50);
            EntityEMPBlast wave = new EntityEMPBlast(level(), 50);
            wave.setPos(getX(), getY(), getZ());
            level().addFreshEntity(wave);
        }

        @Override
        public ItemStack getDebrisRareDrop() {
            return new ItemStack(ModBlocks.EMP_BOMB.get().asItem());
        }

        @Override
        public ItemStack getMissileItemForInfo() {
            return new ItemStack(ModItems.MISSILE_EMP.get());
        }
    }

    private static class MathHelper {
        public static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }
    }
}