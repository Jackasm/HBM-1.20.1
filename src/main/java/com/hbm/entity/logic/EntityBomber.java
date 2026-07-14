package com.hbm.entity.logic;

import com.hbm.config.GeneralConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.projectile.EntityBombletZeta;
import com.hbm.entity.projectile.EntityBoxcar;
import com.hbm.explosion.ExplosionChaos;

import com.hbm.sound.AudioWrapper;
import com.hbm.sound.ModSounds;
import com.hbm.sound.SoundHelper;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityBomber extends EntityPlaneBase {

    /* This was probably the dumbest fucking way that I could have handled this. Not gonna change it now, be glad I made a superclass at all. */
    public int bombStart = 75;
    public int bombStop = 125;
    public int bombRate = 3;
    public int type = 0;

    protected AudioWrapper audio;

    public EntityBomber(EntityType<?> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(Style.STYLE, (byte) 0);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            if (this.getEntityData().get(Style.STYLE) > 0) {
                if (audio == null || !audio.isPlaying()) {
                    int bomberType = this.getEntityData().get(Style.STYLE);
                    audio = SoundHelper.createLoopedSound(bomberType <= 4 ? ModSounds.BOMBER_SMALL_LOOP.get() : ModSounds.BOMBER_LOOP.get(), (float) getX(), (float) getY(), (float) getZ(), 2F, 250F, 1F, 20);
                    audio.startSound();
                }
                audio.keepAlive();
                audio.updatePosition((float) getX(), (float) getY(), (float) getZ());
            } else {
                if (audio != null && audio.isPlaying()) {
                    audio.stopSound();
                    audio = null;
                }
            }
        }

        if (!level().isClientSide && this.health > 0 && this.tickCount > bombStart && this.tickCount < bombStop && this.tickCount % bombRate == 0) {

            if (type == 3) {
                level().playSound(null, getX() + 0.5F, getY() + 0.5F, getZ() + 0.5F,
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 5.0F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
                ExplosionChaos.spawnPoisonCloud((ServerLevel) level(), getX(), getY() - 1F, getZ(), 10, 0.5, 3);

            } else if (type == 5) {
                // Stinger rockets - nothing happens here, handled elsewhere

            } else if (type == 6) {
                level().playSound(null, getX() + 0.5F, getY() + 0.5F, getZ() + 0.5F,
                        ModSounds.MISSILE_TAKE_OFF.get(), SoundSource.HOSTILE, 10.0F, 0.9F + random.nextFloat() * 0.2F);
                EntityBoxcar rocket = new EntityBoxcar(ModEntities.BOXCAR.get(), level());
                rocket.setPos(getX() + random.nextDouble() - 0.5, getY() - random.nextDouble(), getZ() + random.nextDouble() - 0.5);
                level().addFreshEntity(rocket);

            } else if (type == 7) {
                level().playSound(null, getX() + 0.5F, getY() + 0.5F, getZ() + 0.5F,
                        SoundEvents.FIRE_EXTINGUISH, SoundSource.HOSTILE, 5.0F, 2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);
                int height = level().getHeightmapPos(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE,
                        BlockPos.containing(getX(), 0, getZ())).getY();
                ExplosionChaos.spawnPoisonCloud((ServerLevel) level(), getX(), height + 2, getZ(), 10, 1, 2);

            } else {
                level().playSound(null, getX() + 0.5F, getY() + 0.5F, getZ() + 0.5F,
                        ModSounds.BOMB_WHISTLE.get(), SoundSource.HOSTILE, 10.0F, 0.9F + random.nextFloat() * 0.2F);
                EntityBombletZeta zeta = new EntityBombletZeta(ModEntities.BOMBLET_ZETA.get(), level());
                zeta.rotation();
                zeta.type = type;
                zeta.setPos(getX() + random.nextDouble() - 0.5, getY() - random.nextDouble(), getZ() + random.nextDouble() - 0.5);
                if (type == 0) {
                    zeta.setDeltaMovement(getDeltaMovement().x + random.nextGaussian() * 0.15, 0, getDeltaMovement().z + random.nextGaussian() * 0.15);
                } else {
                    zeta.setDeltaMovement(getDeltaMovement().x, 0, getDeltaMovement().z);
                }
                level().addFreshEntity(zeta);
            }
        }
    }

    public void fac(Level level, double x, double y, double z) {

        Vec3 vector = new Vec3(level.random.nextDouble() - 0.5, 0, level.random.nextDouble() - 0.5);
        vector = vector.normalize();
        vector = vector.scale(GeneralConfig.enableBomberShortMode.get() ? 1 : 2);

        this.setPos(x - vector.x * 100, y + 50, z - vector.z * 100);
        this.loadNeighboringChunks((ServerLevel) level, new ChunkPos((int) (x / 16), (int) (z / 16)), RefStrings.MODID);

        this.setDeltaMovement(vector.x, 0, vector.z);

        this.rotation();

        int i = 1;

        int rand = level.random.nextInt(7);

        switch(rand) {
            case 0, 1 -> i = 1;
            case 2, 3 -> i = 2;
            case 4 -> i = 5;
            case 5 -> i = 6;
            case 6 -> i = 7;
        }

        if(level.random.nextInt(100) == 0) {
            rand = level.random.nextInt(4);
            switch(rand) {
                case 0 -> i = 0;
                case 1 -> i = 3;
                case 2 -> i = 4;
                case 3 -> i = 8;
            }
        }

        this.getEntityData().set(Style.STYLE, (byte) i);
    }

    public static EntityBomber statFacCarpet(Level level, double x, double y, double z) {
        EntityBomber bomber = new EntityBomber(ModEntities.BOMBER.get(), level);
        bomber.timer = 200;
        bomber.bombStart = 50;
        bomber.bombStop = 100;
        bomber.bombRate = 2;
        bomber.fac(level, x, y, z);
        bomber.type = 0;
        return bomber;
    }

    public static EntityBomber statFacNapalm(Level level, double x, double y, double z) {
        EntityBomber bomber = new EntityBomber(ModEntities.BOMBER.get(), level);
        bomber.timer = 200;
        bomber.bombStart = 50;
        bomber.bombStop = 100;
        bomber.bombRate = 5;
        bomber.fac(level, x, y, z);
        bomber.type = 1;
        return bomber;
    }

    public static EntityBomber statFacChlorine(Level level, double x, double y, double z) {
        EntityBomber bomber = new EntityBomber(ModEntities.BOMBER.get(), level);
        bomber.timer = 200;
        bomber.bombStart = 50;
        bomber.bombStop = 100;
        bomber.bombRate = 4;
        bomber.fac(level, x, y, z);
        bomber.type = 2;
        return bomber;
    }

    public static EntityBomber statFacOrange(Level level, double x, double y, double z) {
        EntityBomber bomber = new EntityBomber(ModEntities.BOMBER.get(), level);
        bomber.timer = 200;
        bomber.bombStart = 75;
        bomber.bombStop = 125;
        bomber.bombRate = 1;
        bomber.fac(level, x, y, z);
        bomber.type = 3;
        return bomber;
    }

    public static EntityBomber statFacABomb(Level level, double x, double y, double z) {
        EntityBomber bomber = new EntityBomber(ModEntities.BOMBER.get(), level);
        bomber.timer = 200;
        bomber.bombStart = 60;
        bomber.bombStop = 70;
        bomber.bombRate = 65;
        bomber.fac(level, x, y, z);
        int i = 1;

        int rand = level.random.nextInt(3);

        switch(rand) {
            case 0 -> i = 5;
            case 1 -> i = 6;
            case 2 -> i = 7;
        }
        if(level.random.nextInt(100) == 0) i = 8;

        bomber.getEntityData().set(Style.STYLE, (byte) i);
        bomber.type = 4;
        return bomber;
    }

    public static EntityBomber statFacStinger(Level level, double x, double y, double z) {
        EntityBomber bomber = new EntityBomber(ModEntities.BOMBER.get(), level);
        bomber.timer = 200;
        bomber.bombStart = 50;
        bomber.bombStop = 150;
        bomber.bombRate = 10;
        bomber.fac(level, x, y, z);
        bomber.getEntityData().set(Style.STYLE, (byte) 4);
        bomber.type = 5;
        return bomber;
    }

    public static EntityBomber statFacBoxcar(Level level, double x, double y, double z) {
        EntityBomber bomber = new EntityBomber(ModEntities.BOMBER.get(), level);
        bomber.timer = 200;
        bomber.bombStart = 50;
        bomber.bombStop = 150;
        bomber.bombRate = 10;
        bomber.fac(level, x, y, z);
        bomber.getEntityData().set(Style.STYLE, (byte) 6);
        bomber.type = 6;
        return bomber;
    }

    public static EntityBomber statFacPC(Level level, double x, double y, double z) {
        EntityBomber bomber = new EntityBomber(ModEntities.BOMBER.get(), level);
        bomber.timer = 200;
        bomber.bombStart = 75;
        bomber.bombStop = 125;
        bomber.bombRate = 1;
        bomber.fac(level, x, y, z);
        bomber.getEntityData().set(Style.STYLE, (byte) 6);
        bomber.type = 7;
        return bomber;
    }

    public void rotation() {
        // Этот метод нужен для поворота бомбардировщика
        // Реализация зависит от того, как EntityPlaneBase работает
        double motionX = getDeltaMovement().x;
        double motionZ = getDeltaMovement().z;
        float yaw = (float) (Math.atan2(motionZ, motionX) * 180.0D / Math.PI) - 90.0F;
        this.setYRot(yaw);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        bombStart = tag.getInt("bombStart");
        bombStop = tag.getInt("bombStop");
        bombRate = tag.getInt("bombRate");
        type = tag.getInt("type");
        this.getEntityData().set(Style.STYLE, tag.getByte("style"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("bombStart", bombStart);
        tag.putInt("bombStop", bombStop);
        tag.putInt("bombRate", bombRate);
        tag.putInt("type", type);
        tag.putByte("style", this.getEntityData().get(Style.STYLE));
    }

    // Внутренний класс для синхронизации данных
    public static class Style {
        public static final EntityDataAccessor<Byte> STYLE =
                SynchedEntityData.defineId(EntityBomber.class, EntityDataSerializers.BYTE);
    }
}