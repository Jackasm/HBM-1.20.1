package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

public class EntitySawblade extends ThrowableProjectile implements IEntityAdditionalSpawnData {

    private static final EntityDataAccessor<Integer> ROTATION = SynchedEntityData.defineId(EntitySawblade.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> META = SynchedEntityData.defineId(EntitySawblade.class, EntityDataSerializers.INT);

    protected int stuckBlockX = -1;
    protected int stuckBlockY = -1;
    protected int stuckBlockZ = -1;
    protected Block stuckBlock;
    protected boolean inGround;

    public EntitySawblade(EntityType<? extends EntitySawblade> type, Level level) {
        super(type, level);
    }

    // Конструктор для создания через код
    public EntitySawblade(Level level, double x, double y, double z) {
        this(ModEntities.SAWBLADE.get(), level);
        this.setPos(x, y, z);
    }

    // Конструктор с направлением
    public EntitySawblade setOrientation(int rot) {
        this.entityData.set(ROTATION, rot);
        return this;
    }

    public int getOrientation() {
        return this.entityData.get(ROTATION);
    }

    public int getMeta() {
        return this.entityData.get(META);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ROTATION, 0);
        this.entityData.define(META, 0);
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    public @NotNull InteractionResult interact(@NotNull Player player, net.minecraft.world.@NotNull InteractionHand hand) {
        if (!level().isClientSide) {
            if (player.getInventory().add(new ItemStack(ModItems.SAWBLADE.get()))) {
                this.discard();
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (level().isClientSide) return;

        // Обработка попадания в сущность
        if (result.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) result;
            Entity target = entityHit.getEntity();

            if (target.isAlive()) {
                target.hurt(ModDamageSource.causeRubbleDamage(target.level()), 1000F);
                if (!target.isAlive() && target instanceof LivingEntity living) {
                    CompoundTag vdat = new CompoundTag();
                    vdat.putString("type", "giblets");
                    vdat.putInt("ent", living.getId());
                    vdat.putInt("cDiv", 5);

                    // Вместо PacketThreading.createAllAroundThreadedPacket используем:
                    PacketDispatcher.sendAuxParticleNT(vdat,
                            target.getX(), target.getY() + target.getBbHeight() * 0.5, target.getZ(),
                            target.level(), target.blockPosition());

                    level().playSound(null, target.getX(), target.getY(), target.getZ(),
                            SoundEvents.ZOMBIE_BREAK_WOODEN_DOOR, SoundSource.BLOCKS, 2.0F,
                            0.95F + level().random.nextFloat() * 0.2F);
                }
            }
        }

        // Обработка попадания в блок
        if (this.tickCount > 1 && result.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) result;
            int orientation = this.entityData.get(ROTATION);

            if (orientation < 6) {
                Vec3 motion = this.getDeltaMovement();
                if (motion.length() < 0.75) {
                    this.entityData.set(ROTATION, orientation + 6);
                    orientation += 6;
                } else {
                    Direction side = blockHit.getDirection();
                    this.setDeltaMovement(
                            motion.x * (1 - Math.abs(side.getStepX()) * 2),
                            motion.y * (1 - Math.abs(side.getStepY()) * 2),
                            motion.z * (1 - Math.abs(side.getStepZ()) * 2)
                    );
                    level().explode(this, blockHit.getLocation().x, blockHit.getLocation().y, blockHit.getLocation().z, 3F, Level.ExplosionInteraction.NONE);

                    BlockPos blockPos = blockHit.getBlockPos();
                    BlockState state = level().getBlockState(blockPos);
                    if (state.getBlock().getExplosionResistance() < 50) {
                        level().destroyBlock(blockPos, false);
                    }
                }
            }

            if (orientation >= 6) {
                this.stuckBlockX = blockHit.getBlockPos().getX();
                this.stuckBlockY = blockHit.getBlockPos().getY();
                this.stuckBlockZ = blockHit.getBlockPos().getZ();
                this.stuckBlock = level().getBlockState(blockHit.getBlockPos()).getBlock();
                this.inGround = true;
                this.setDeltaMovement(0, 0, 0);
                this.setNoGravity(true);
            }
        }
    }

    @Override
    public void tick() {
        if (!level().isClientSide) {
            int orientation = this.entityData.get(ROTATION);
            if (orientation >= 6 && !this.inGround) {
                this.entityData.set(ROTATION, orientation - 6);
            }
        }

        if (this.inGround) {
            if (level().getBlockState(new BlockPos(stuckBlockX, stuckBlockY, stuckBlockZ)).getBlock() == this.stuckBlock) {
                this.setDeltaMovement(0, 0, 0);
                this.setNoGravity(true);
                return;
            } else {
                this.inGround = false;
                this.stuckBlock = null;
                this.setNoGravity(false);
                this.setDeltaMovement(
                        this.random.nextFloat() * 0.2F,
                        this.random.nextFloat() * 0.2F,
                        this.random.nextFloat() * 0.2F
                );
            }
        }

        // Обновление поворота на основе движения
        Vec3 motion = this.getDeltaMovement();
        if (motion.lengthSqr() > 0.01) {
            float horizontal = (float) Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            this.setYRot((float) (Math.atan2(motion.x, motion.z) * (180.0 / Math.PI)));
            this.setXRot((float) (Math.atan2(motion.y, horizontal) * (180.0 / Math.PI)));
        }

        // Клиентские эффекты
        if (level().isClientSide && this.tickCount > 2) {
            level().addParticle(ParticleTypes.CRIT,
                    this.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                    this.getY() + (this.random.nextDouble() - 0.5) * 0.5,
                    this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                    0, 0, 0);
        }

        super.tick();
    }

    @Override
    protected float getGravity() {
        return inGround ? 0 : 0.03F;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("rot", this.getOrientation());
        nbt.putInt("stuckX", stuckBlockX);
        nbt.putInt("stuckY", stuckBlockY);
        nbt.putInt("stuckZ", stuckBlockZ);
        nbt.putBoolean("inGround", inGround);
        if (stuckBlock != null) {
            nbt.putString("stuckBlock", BuiltInRegistries.BLOCK.getKey(stuckBlock).toString());
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        this.setOrientation(nbt.getInt("rot"));
        this.stuckBlockX = nbt.getInt("stuckX");
        this.stuckBlockY = nbt.getInt("stuckY");
        this.stuckBlockZ = nbt.getInt("stuckZ");
        this.inGround = nbt.getBoolean("inGround");
        if (nbt.contains("stuckBlock")) {
            ResourceLocation loc = ResLocation(nbt.getString("stuckBlock"));
            this.stuckBlock = BuiltInRegistries.BLOCK.get(loc);
            if (this.stuckBlock == null) {
                this.stuckBlock = Blocks.AIR;
            }
        }
    }

    // IEntityAdditionalSpawnData методы
    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeInt(this.getOrientation());
        buffer.writeDouble(this.getX());
        buffer.writeDouble(this.getY());
        buffer.writeDouble(this.getZ());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf buffer) {
        this.setOrientation(buffer.readInt());
        this.setPos(buffer.readDouble(), buffer.readDouble(), buffer.readDouble());
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}