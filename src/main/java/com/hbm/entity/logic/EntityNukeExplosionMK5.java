package com.hbm.entity.logic;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.config.BombConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityFalloutRain;
import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.explosion.ExplosionNukeRayBatched;
import com.hbm.interfaces.IExplosionRay;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityNukeExplosionMK5 extends EntityExplosionChunkloading {

    // Strength of the blast
    public int strength;
    // How many rays are calculated per tick
    public int speed;
    public int length;
    private long explosionStart;
    public boolean fallout = true;
    private int falloutAdd = 0;

    private IExplosionRay explosion;

    public EntityNukeExplosionMK5(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    public void tick() {
        if (strength == 0) {
            this.clearChunkLoader();
            this.discard();
            return;
        }

        if (!level().isClientSide) {
            loadChunk(new BlockPos((int) Math.floor(getX() / 16D), 0, (int) Math.floor(getZ() / 16D)));
        }

        if (!level().isClientSide) {
            for (Player player : this.level().players()) {
                if (player instanceof ServerPlayer sp) {
                    ModCriteriaTriggers.NUCLEAR_EXPLOSION.trigger(sp);
                }
            }
        }

        if (!level().isClientSide && fallout && explosion != null && this.tickCount < 10 && strength >= 75) {
            radiate(2_500_000F / (this.tickCount * 5 + 1), this.length * 2);
        }

        ExplosionNukeGeneric.dealDamage(level(), getX(), getY(), getZ(), this.length * 2);

        if (explosion == null) {
            explosionStart = System.currentTimeMillis();
            explosion = new ExplosionNukeRayBatched(level(), (int) getX(), (int) getY(), (int) getZ(), strength, speed, length);
        }

        if (!explosion.isComplete()) {
            explosion.cacheChunksTick(BombConfig.MK5);
            explosion.destructionTick(BombConfig.MK5);
        } else {
            if (fallout) {
                EntityFalloutRain fallout = new EntityFalloutRain(ModEntities.FALLOUT_RAIN.get(), level());
                fallout.setPos(getX(), getY(), getZ());
                fallout.setScale((int) (this.length * 2.5 + falloutAdd) * BombConfig.FALLOUT_RANGE / 100);
                level().addFreshEntity(fallout);
            }
            this.clearChunkLoader();
            this.discard();
        }
    }

    private void radiate(float rads, double range) {
        AABB box = new AABB(
                getX() - range, getY() - range, getZ() - range,
                getX() + range, getY() + range, getZ() + range
        );

        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, box);

        for (LivingEntity e : entities) {
            Vec3 vec = new Vec3(
                    e.getX() - getX(),
                    e.getY() + e.getEyeHeight() - getY(),
                    e.getZ() - getZ()
            );
            double len = vec.length();
            vec = vec.normalize();

            float res = 0;

            for (int i = 1; i < len; i++) {
                int ix = (int) Math.floor(getX() + vec.x * i);
                int iy = (int) Math.floor(getY() + vec.y * i);
                int iz = (int) Math.floor(getZ() + vec.z * i);
                BlockPos pos = new BlockPos(ix, iy, iz);

                res += level().getBlockState(pos).getBlock().getExplosionResistance();
            }

            if (res < 1) res = 1;

            float eRads = rads;
            eRads /= res;
            eRads /= (float) (len * len);

            ContaminationUtil.contaminate(e, HazardType.RADIATION, ContaminationType.RAD_BYPASS, eRads);
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        if (explosion != null) {
            explosion.cancel();
        }
        super.remove(reason);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        this.tickCount = tag.getInt("ticksExisted");
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        tag.putInt("ticksExisted", this.tickCount);
    }

    public static EntityNukeExplosionMK5 statFac(Level level, int r, double x, double y, double z) {

        if (r == 0) r = 25;

        r *= 2;

        EntityNukeExplosionMK5 mk5 = new EntityNukeExplosionMK5(ModEntities.NUKE_EXPLOSION_MK5.get(), level);
        mk5.strength = r;
        mk5.speed = (int) Math.ceil(100000.0 / mk5.strength);
        mk5.setPos(x, y, z);
        mk5.length = mk5.strength / 2;
        mk5.loadChunk(new BlockPos((int) Math.floor(x / 16D), 0, (int) Math.floor(z / 16D)));
        return mk5;
    }

    public static EntityNukeExplosionMK5 statFacNoRad(Level level, int r, double x, double y, double z) {
        EntityNukeExplosionMK5 mk5 = statFac(level, r, x, y, z);
        mk5.fallout = false;
        return mk5;
    }

    public EntityNukeExplosionMK5 moreFallout(int fallout) {
        this.falloutAdd = fallout;
        return this;
    }
}