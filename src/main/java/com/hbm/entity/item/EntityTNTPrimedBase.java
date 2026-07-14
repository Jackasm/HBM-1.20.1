package com.hbm.entity.item;

import com.hbm.blocks.bomb.IFuckingExplode;
import com.hbm.entity.ModEntities;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class EntityTNTPrimedBase extends Entity {

    private static final EntityDataAccessor<Integer> DATA_BLOCK_ID =
            SynchedEntityData.defineId(EntityTNTPrimedBase.class, EntityDataSerializers.INT);

    public boolean detonateOnCollision = false;
    public int fuse = 80;
    @Nullable
    private LivingEntity owner;

    public EntityTNTPrimedBase(EntityType<? extends EntityTNTPrimedBase> type, Level level) {
        super(type, level);
        this.blocksBuilding = true;
    }

    public EntityTNTPrimedBase(Level level, double x, double y, double z,
                               @Nullable LivingEntity owner, Block bomb) {
        this(ModEntities.TNT_PRIMED_BASE.get(), level);
        this.setPos(x, y, z);

        double dx = level.random.nextDouble() * 0.02 - 0.01;
        double dy = 0.2;
        double dz = level.random.nextDouble() * 0.02 - 0.01;

        this.setDeltaMovement(dx, dy, dz);
        this.xo = x;
        this.yo = y;
        this.zo = z;
        this.owner = owner;

        BlockState defaultState = bomb.defaultBlockState();
        this.entityData.set(DATA_BLOCK_ID, Block.getId(defaultState));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_BLOCK_ID, 0);
    }

    @Override
    protected Entity.@NotNull MovementEmission getMovementEmission() {
        return MovementEmission.NONE;
    }

    @Override
    public boolean isPickable() {
        return !this.isRemoved();
    }

    @Override
    public void tick() {
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }

        this.move(MoverType.SELF, this.getDeltaMovement());
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));

        if (this.onGround()) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, 0.5, 0.7));
        }

        this.fuse--;

        if (this.fuse <= 0 || (this.detonateOnCollision && this.horizontalCollision)) {
            this.discard();

            if (!this.level().isClientSide()) {
                this.explode();
            }
        } else {
            if (this.level().isClientSide()) {
                this.spawnSmokeParticles();
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnSmokeParticles() {
        for (int i = 0; i < 2; i++) {
            double x = this.getX() + (this.random.nextDouble() * 0.6 - 0.3);
            double y = this.getY() + 0.5;
            double z = this.getZ() + (this.random.nextDouble() * 0.6 - 0.3);

            this.level().addParticle(net.minecraft.core.particles.ParticleTypes.SMOKE,
                    x, y, z, 0.0, 0.0, 0.0);
        }
    }

    private void explode() {
        IFuckingExplode bomb = this.getBomb();
        if (bomb != null) {
            BlockPos pos = this.blockPosition();
            bomb.explodeEntity(this.level(), pos, this.owner);
        }
    }

    @Nullable
    public IFuckingExplode getBomb() {
        Block block = this.getBlock();
        if (block instanceof IFuckingExplode) {
            return (IFuckingExplode) block;
        }
        return null;
    }

    public Block getBlock() {
        int blockId = this.entityData.get(DATA_BLOCK_ID);
        return Block.stateById(blockId).getBlock();
    }

    public void setBlock(Block block) {
        BlockState defaultState = block.defaultBlockState();
        int blockId = Block.getId(defaultState);
        this.entityData.set(DATA_BLOCK_ID, blockId);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("Fuse", this.fuse);
        tag.putInt("Tile", this.entityData.get(DATA_BLOCK_ID));
        tag.putBoolean("DetonateOnCollision", this.detonateOnCollision);

        if (this.owner != null) {
            tag.putUUID("Owner", this.owner.getUUID());
        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.fuse = tag.getInt("Fuse");
        this.entityData.set(DATA_BLOCK_ID, tag.getInt("Tile"));
        this.detonateOnCollision = tag.getBoolean("DetonateOnCollision");

        if (tag.hasUUID("Owner")) {
            this.owner = this.level().getPlayerByUUID(tag.getUUID("Owner"));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    @Nullable
    public LivingEntity getOwner() {
        return this.owner;
    }

    public void setOwner(@Nullable LivingEntity owner) {
        this.owner = owner;
    }

}