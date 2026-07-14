package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.items.ModItems;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class EntityCog extends ThrowableProjectile {

    private static final EntityDataAccessor<Integer> ORIENTATION = SynchedEntityData.defineId(EntityCog.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> META = SynchedEntityData.defineId(EntityCog.class, EntityDataSerializers.INT);

    private static final EntityDataAccessor<Boolean> IN_GROUND = SynchedEntityData.defineId(EntityCog.class, EntityDataSerializers.BOOLEAN);

    public EntityCog(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
    }

    public EntityCog(Level level, double x, double y, double z) {
        super(ModEntities.COG.get(), level);
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ORIENTATION, 0);
        this.entityData.define(META, 0);
        this.entityData.define(IN_GROUND, false);
    }

    public void setOrientation(int rot) {
        this.entityData.set(ORIENTATION, rot);
    }

    public EntityCog setMeta(int meta) {
        this.entityData.set(META, meta);
        return this;
    }

    public int getOrientation() {
        return this.entityData.get(ORIENTATION);
    }

    public int getMeta() {
        return this.entityData.get(META);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, net.minecraft.world.@NotNull InteractionHand hand) {
        if (!this.level().isClientSide) {
            ItemStack stack = new ItemStack(ModItems.GEAR_LARGE.get(), 1);
            // Устанавливаем CustomModelData через NBT
            stack.getOrCreateTag().putInt("CustomModelData", this.getMeta());

            if (player.getInventory().add(stack)) {
                this.discard();
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity entity = result.getEntity();
        if (entity.isAlive()) {
            entity.hurt(ModDamageSource.causeRubbleDamage(this.level()), 1000);
            if (!entity.isAlive() && entity instanceof LivingEntity) {
                this.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                        SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, this.getSoundSource(), 2.0F, 0.95F + this.random.nextFloat() * 0.2F);
            }
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (this.tickCount > 1) {
            int orientation = this.getOrientation();

            if (orientation < 6) {
                Vec3 motion = this.getDeltaMovement();
                if (motion.length() < 0.75) {
                    this.setOrientation(orientation + 6);
                } else {
                    Direction side = result.getDirection();
                    this.setDeltaMovement(
                            motion.x * (1 - Math.abs(side.getStepX()) * 2),
                            motion.y * (1 - Math.abs(side.getStepY()) * 2),
                            motion.z * (1 - Math.abs(side.getStepZ()) * 2)
                    );
                    this.level().explode(this, this.getX(), this.getY(), this.getZ(), 3F, false, Level.ExplosionInteraction.TNT);

                    BlockPos hitPos = result.getBlockPos();
                    if (this.level().getBlockState(hitPos).getExplosionResistance(this.level(), hitPos, null) < 50) {
                        this.level().destroyBlock(hitPos, false);
                    }
                }
            }
        }
    }

    public void setInGround(boolean inGround) {
        this.entityData.set(IN_GROUND, inGround);
    }

    public boolean isInGround() {
        return this.entityData.get(IN_GROUND);
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide && this.tickCount > 1 && result.getType() == HitResult.Type.BLOCK) {
            int orientation = this.getOrientation();
            if (orientation >= 6) {
                this.setDeltaMovement(0, 0, 0);
                this.setInGround(true);
            }
        }
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            int orientation = this.getOrientation();
            if (orientation >= 6 && !this.isInGround()) {
                this.setOrientation(orientation - 6);
            }
        }
        super.tick();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return true;
    }

    @Override
    protected float getGravity() {
        return isInGround() ? 0 : 0.03F;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("rot", this.getOrientation());
        nbt.putInt("meta", this.getMeta());
        nbt.putBoolean("inGround", this.isInGround());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setOrientation(nbt.getInt("rot"));
        this.setMeta(nbt.getInt("meta"));
        this.setInGround(nbt.getBoolean("inGround"));
    }
}