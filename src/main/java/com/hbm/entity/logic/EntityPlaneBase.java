package com.hbm.entity.logic;

import java.util.ArrayList;
import java.util.List;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.particle.helper.ExplosionSmallCreator;
import com.hbm.particle.helper.GasFlameCreator;
import com.hbm.sound.ModSounds;
import com.hbm.util.ModDamageSource;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.world.ForgeChunkManager;
import org.jetbrains.annotations.NotNull;

public abstract class EntityPlaneBase extends Entity implements IChunkLoader {

    protected static final EntityDataAccessor<Float> DATA_HEALTH = SynchedEntityData.defineId(EntityPlaneBase.class, EntityDataSerializers.FLOAT);

    // Для интерполяции на клиенте
    protected int lerpSteps;
    protected double lerpX;
    protected double lerpY;
    protected double lerpZ;
    protected double lerpYaw;
    protected double lerpPitch;

    // Для чанк-лоадера
    protected List<ChunkPos> forcedChunks = new ArrayList<>();
    protected String modId = RefStrings.MODID;
    protected boolean chunksForced = false;

    public float health = getMaxHealth();
    public int timer = getLifetime();

    public EntityPlaneBase(EntityType<?> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public float getMaxHealth() { return 50F; }
    public int getLifetime() { return 200; }

    @Override public boolean canBeCollidedWith() { return this.health > 0; }

    @Override
    public boolean hurt(DamageSource source, float amount) {
        if (source.is(ModDamageSource.NUCLEAR_BLAST)) return false;
        if (this.isInvulnerableTo(source)) return false;
        if (!this.isRemoved() && !this.level().isClientSide && this.health > 0) {
            health -= amount;
            this.entityData.set(DATA_HEALTH, health);
            if (this.health <= 0) this.killPlane();
        }
        return true;
    }

    protected void killPlane() {
        if (!level().isClientSide) {
            ExplosionSmallCreator.composeEffect(level(), getX(), getY(), getZ(), 25, 3.5F, 2F);
            level().playSound(null, getX(), getY(), getZ(), ModSounds.PLANE_SHOT_DOWN.get(), getSoundSource(), 25.0F, 1.0F);
        }
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_HEALTH, getMaxHealth());
    }

    @Override
    public void init(ServerLevel level, String modId) {
        this.modId = modId;
        if (!level.isClientSide && !chunksForced) {
            ChunkPos chunkPos = level.getChunkAt(blockPosition()).getPos();
            forceChunk(level, chunkPos, true, false);
            chunksForced = true;
        }
    }

    protected void forceChunk(ServerLevel level, ChunkPos chunkPos, boolean add, boolean ticking) {
        if (add) {
            forcedChunks.add(chunkPos);
            ForgeChunkManager.forceChunk(level, modId, this, chunkPos.x, chunkPos.z, true, ticking);
        } else {
            forcedChunks.remove(chunkPos);
            ForgeChunkManager.forceChunk(level, modId, this, chunkPos.x, chunkPos.z, false, ticking);
        }
    }

    @Override
    public void loadNeighboringChunks(ServerLevel level, ChunkPos centerChunk, String modId) {
        if (!level.isClientSide) {
            for (ChunkPos chunk : forcedChunks) {
                ForgeChunkManager.forceChunk(level, modId, this, chunk.x, chunk.z, false, false);
            }
            forcedChunks.clear();

            forcedChunks.add(centerChunk);
            ForgeChunkManager.forceChunk(level, modId, this, centerChunk.x, centerChunk.z, true, false);
        }
    }

    @Override
    public void clearChunkLoader() {
        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            for (ChunkPos chunk : forcedChunks) {
                ForgeChunkManager.forceChunk(serverLevel, modId, this, chunk.x, chunk.z, false, false);
            }
            forcedChunks.clear();
            chunksForced = false;
        }
    }

    @Override
    public void tick() {
        // Обновляем health в datawatcher на сервере
        if (!level().isClientSide) {
            this.entityData.set(DATA_HEALTH, health);
        } else {
            health = this.entityData.get(DATA_HEALTH);
        }

        if (level().isClientSide) {
            // Клиентская интерполяция
            clientTick();
        } else {
            // Серверная логика
            serverTick();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void clientTick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();

        if (this.lerpSteps > 0) {
            // Интерполяция позиции
            double x = this.getX() + (this.lerpX - this.getX()) / (double) this.lerpSteps;
            double y = this.getY() + (this.lerpY - this.getY()) / (double) this.lerpSteps;
            double z = this.getZ() + (this.lerpZ - this.getZ()) / (double) this.lerpSteps;

            // Интерполяция вращения (yaw)
            double yawDiff = this.lerpYaw - (double) this.getYRot();
            while (yawDiff < -180.0D) yawDiff += 360.0D;
            while (yawDiff >= 180.0D) yawDiff -= 360.0D;

            // Интерполяция вращения (pitch)
            double pitchDiff = this.lerpPitch - (double) this.getXRot();
            while (pitchDiff < -180.0D) pitchDiff += 360.0D;
            while (pitchDiff >= 180.0D) pitchDiff -= 360.0D;

            this.setYRot((float) ((double) this.getYRot() + yawDiff / (double) this.lerpSteps));
            this.setXRot((float) ((double) this.getXRot() + pitchDiff / (double) this.lerpSteps));

            this.lerpSteps--;
            this.setPos(x, y, z);
        }
    }

    private void serverTick() {
        this.xo = this.xOld = getX();
        this.yo = this.yOld = getY();
        this.zo = this.zOld = getZ();
        this.xRotO = this.getXRot();
        this.yRotO = this.getYRot();

        Vec3 motion = this.getDeltaMovement();
        this.setPos(getX() + motion.x, getY() + motion.y, getZ() + motion.z);
        this.updateRotation();

        if (this.health <= 0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -0.025, 0));

            for (int i = 0; i < 10; i++) {
                GasFlameCreator.spawnEffect(this.level(),
                        getX() + random.nextGaussian() * 0.5 - motion.x * 2,
                        getY() + random.nextGaussian() * 0.5 - motion.y * 2,
                        getZ() + random.nextGaussian() * 0.5 - motion.z * 2,
                        0.0, 0.1, 0.0, 1);
            }

            BlockPos pos = BlockPos.containing(getX(), getY(), getZ());
            if (!level().getBlockState(pos).isAir() || getY() < level().getMinBuildHeight()) {
                this.discard();
                new ExplosionVNT(level(), getX(), getY(), getZ(), 15F).makeStandard().explode();
                level().playSound(null, getX(), getY(), getZ(), ModSounds.PLANE_CRASH.get(), getSoundSource(), 25.0F, 1.0F);
                return;
            }
        } else {
            Vec3 currentMotion = this.getDeltaMovement();
            this.setDeltaMovement(currentMotion.x, 0, currentMotion.z);
        }

        if (this.tickCount > timer) this.discard();

        if (level() instanceof ServerLevel serverLevel && chunksForced) {
            ChunkPos centerChunk = new ChunkPos(BlockPos.containing(getX(), getY(), getZ()));
            loadNeighboringChunks(serverLevel, centerChunk, modId);
        }
    }

    protected void updateRotation() {
        Vec3 motion = this.getDeltaMovement();
        float motionHorizontal = (float) Math.sqrt(motion.x * motion.x + motion.z * motion.z);

        if (motionHorizontal > 0.001F) {
            this.setYRot((float) (Math.atan2(motion.x, motion.z) * 180.0D / Math.PI));
            this.setXRot((float) (Math.atan2(motion.y, motionHorizontal) * 180.0D / Math.PI) - 90);
        }

        // Нормализация углов
        while (this.getXRot() - this.xRotO < -180.0F) this.xRotO -= 360.0F;
        while (this.getXRot() - this.xRotO >= 180.0F) this.xRotO += 360.0F;
        while (this.getYRot() - this.yRotO < -180.0F) this.yRotO -= 360.0F;
        while (this.getYRot() - this.yRotO >= 180.0F) this.yRotO += 360.0F;
    }

    @OnlyIn(Dist.CLIENT)
    public void setVelocity(double velX, double velY, double velZ) {
        this.setDeltaMovement(velX, velY, velZ);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void lerpTo(double x, double y, double z, float yaw, float pitch, int steps, boolean teleport) {
        this.lerpX = x;
        this.lerpY = y;
        this.lerpZ = z;
        this.lerpYaw = yaw;
        this.lerpPitch = pitch;
        this.lerpSteps = steps;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        this.clearChunkLoader();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.tickCount = tag.getInt("ticksExisted");
        this.health = tag.getFloat("health");
        this.entityData.set(DATA_HEALTH, this.health);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("ticksExisted", this.tickCount);
        tag.putFloat("health", this.entityData.get(DATA_HEALTH));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }
}