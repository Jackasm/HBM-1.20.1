package com.hbm.entity.logic;

import com.hbm.entity.ModEntities;
import com.hbm.entity.item.EntityParachuteCrate;
import com.hbm.itempool.ItemPoolsC130;
import com.hbm.sound.AudioWrapper;
import com.hbm.sound.SoundHelper;
import com.hbm.util.EnumUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.sound.ModSounds.BOMBER_LOOP;

public class EntityC130 extends EntityPlaneBase {

    protected AudioWrapper audio;
    public C130PayloadType payload = C130PayloadType.SUPPLIES;

    private static final EntityDataAccessor<Float> DATA_SOUND = SynchedEntityData.defineId(EntityC130.class, EntityDataSerializers.FLOAT);

    public EntityC130(EntityType<? extends EntityPlaneBase> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    @Override
    public @NotNull EntityDimensions getDimensions(@NotNull Pose pose) {
        return EntityDimensions.fixed(8.0F, 4.0F);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_SOUND, 0F);
    }

    @Override
    public void tick() {
        super.tick();

        if (level().isClientSide) {
            handleClientSounds();
        } else {
            this.entityData.set(DATA_SOUND, this.health > 0 && !this.isRemoved() ? 1.0F : 0F);

            if (this.tickCount == this.getLifetime() / 2 && this.health > 0) {
                spawnCrate();
            }
        }
    }

    @Override
    public void onAddedToWorld() {
        super.onAddedToWorld();
        if (!level().isClientSide) {
            this.entityData.set(DATA_SOUND, 1.0F);
        }
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        if (!level().isClientSide) {
            this.entityData.set(DATA_SOUND, 0F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClientSounds() {
        if (this.entityData.get(DATA_SOUND) > 0) {
            if (audio == null || !audio.isPlaying()) {
                audio = SoundHelper.createLoopedSound(BOMBER_LOOP.get(),
                        (float) getX(), (float) getY(), (float) getZ(), 20F, 250F, 1F, 20);
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

    private void spawnCrate() {
        EntityParachuteCrate crate = new EntityParachuteCrate(ModEntities.PARACHUTE_CRATE.get(), level());
        crate.setPos(getX() - getDeltaMovement().x * 7, getY() - 10, getZ() - getDeltaMovement().z * 7);
        crate.payload = payload;
        if (payload == C130PayloadType.SUPPLIES) {
            crate.items.addAll(ItemPoolsC130.getSuppliesPool().generate(random, 5));
        } else if (payload == C130PayloadType.WEAPONS) {
            crate.items.addAll(ItemPoolsC130.getWeaponsPool().generate(random, 1 + random.nextInt(2)));
            crate.items.addAll(ItemPoolsC130.getAmmoPool().generate(random, 6));
        }

        level().addFreshEntity(crate);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.payload = EnumUtil.grabEnumSafely(C130PayloadType.class, tag.getInt("payload"));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("payload", this.payload.ordinal());
    }

    public void fac(Level level, double x, double y, double z, C130PayloadType payload) {
        Vec3 vector = new Vec3(level.random.nextDouble() - 0.5, 0, level.random.nextDouble() - 0.5).normalize();
        vector = vector.scale(2);

        this.payload = payload;

        this.setPos(x - vector.x * 100, y + 100, z - vector.z * 100);
        this.setRot(0.0F, 0.0F);

        // Загружаем соседние чанки
        BlockPos centerPos = BlockPos.containing(x, y, z);
        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                BlockPos chunkPos = centerPos.offset(dx * 16, 0, dz * 16);
                level.getChunkAt(chunkPos);
            }
        }

        this.setDeltaMovement(vector.x, 0.0D, vector.z);
        updateRotation();
    }

    public enum C130PayloadType {
        SUPPLIES,
        WEAPONS,
        A_FUCKING_FUEL_TRUCK
    }
}