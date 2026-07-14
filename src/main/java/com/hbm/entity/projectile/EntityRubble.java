package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;

import com.hbm.network.PacketDispatcher;

import com.hbm.util.ModDamageSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;


public class EntityRubble extends ThrowableProjectile implements IEntityAdditionalSpawnData {

    public static final EntityDataAccessor<Integer> BLOCK_ID = SynchedEntityData.defineId(EntityRubble.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> META = SynchedEntityData.defineId(EntityRubble.class, EntityDataSerializers.INT);

    public EntityRubble(EntityType<? extends EntityRubble> type, Level level) {
        super(type, level);
    }

    public EntityRubble(Level world) {
        this(ModEntities.RUBBLE.get(), world);
    }

    public EntityRubble(Level world, double x, double y, double z) {
        this(ModEntities.RUBBLE.get(), world);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(BLOCK_ID, 0);
        this.entityData.define(META, 0);
    }

    @Override
    protected void onHit(HitResult result) {
        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) result;
            Entity target = entityHit.getEntity();

            byte damage = 15;
            target.hurt(ModDamageSource.causeRubbleDamage(target.level()), damage);
        }

        if (this.tickCount > 2) {
            this.discard();

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GRAVEL_BREAK, SoundSource.BLOCKS, 1.5F, 1.0F);

            if (!this.level().isClientSide()) {
                int blockId = this.entityData.get(BLOCK_ID);
                int meta = this.entityData.get(META);

                CompoundTag data = new CompoundTag();
                data.putString("type", "rubble_burst");
                data.putInt("blockId", blockId);
                data.putInt("meta", meta);
                data.putInt("count", 15);

                PacketDispatcher.sendAuxParticleNT(data, this.getX(), this.getY(), this.getZ(),
                        this.level(), this.blockPosition());
            }
        }
    }

    private CompoundTag createParticleData(int blockId, int meta) {
        CompoundTag data = new CompoundTag();
        data.putString("type", "rubble_burst");
        data.putInt("blockId", blockId);
        data.putInt("meta", meta);
        data.putInt("count", 10);
        return data;
    }

    @Override
    protected float getGravity() {
        return 0.1F;
    }

    public void setMetaBasedOnBlock(BlockState state) {
        Block block = state.getBlock();
        int blockId = Block.getId(block.defaultBlockState());
        int meta = 0;

        this.entityData.set(BLOCK_ID, blockId);
        this.entityData.set(META, meta);
    }

    // Метод для совместимости со старым кодом
    public void setMetaBasedOnBlock(Block block, int meta) {
        int blockId = Block.getId(block.defaultBlockState());
        this.entityData.set(BLOCK_ID, blockId);
        this.entityData.set(META, meta);
    }

    public int getBlockId() {
        return this.entityData.get(BLOCK_ID);
    }

    public int getMeta() {
        return this.entityData.get(META);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("blockId", this.entityData.get(BLOCK_ID));
        compound.putInt("meta", this.entityData.get(META));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        if (compound.contains("blockId")) {
            this.entityData.set(BLOCK_ID, compound.getInt("blockId"));
        }
        if (compound.contains("meta")) {
            this.entityData.set(META, compound.getInt("meta"));
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(this.entityData.get(BLOCK_ID));
        buffer.writeInt(this.entityData.get(META));
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.entityData.set(BLOCK_ID, additionalData.readInt());
        this.entityData.set(META, additionalData.readInt());
    }

    @Override
    public void tick() {
        super.tick();

        // Автоматическое удаление через 100 тиков (5 секунд)
        if (this.tickCount > 100) {
            this.discard();
        }
    }

    // Метод для создания EntityRubble с блоком
    public static EntityRubble createWithBlock(Level world, double x, double y, double z, BlockState state) {
        EntityRubble rubble = new EntityRubble(world, x, y, z);
        rubble.setMetaBasedOnBlock(state);
        return rubble;
    }

    // Метод для создания EntityRubble с движением
    public static EntityRubble createWithMotion(Level world, double x, double y, double z,
                                                double motionX, double motionY, double motionZ,
                                                BlockState state) {
        EntityRubble rubble = createWithBlock(world, x, y, z, state);
        rubble.setDeltaMovement(motionX, motionY, motionZ);
        return rubble;
    }
}