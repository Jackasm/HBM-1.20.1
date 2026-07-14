package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityCloudFleijaRainbow;
import com.hbm.entity.grenade.EntityGrenadeZOMG;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;


public class EntityExplosiveBeam extends Projectile {

    private static final EntityDataAccessor<Byte> CRITICAL = SynchedEntityData.defineId(EntityExplosiveBeam.class, EntityDataSerializers.BYTE);

    private BlockPos tilePos = BlockPos.ZERO;
    private BlockState tileState = null;
    private int inData;
    private boolean inGround;
    public int shake;
    private int ticksInAir;
    private double damage = 2.0D;
    public double gravity = 0.0D;

    public EntityExplosiveBeam(EntityType<? extends EntityExplosiveBeam> type, Level level) {
        super(type, level);
        this.setBoundingBox(new AABB(this.getX(), this.getY(), this.getZ(), this.getX(), this.getY(), this.getZ()).inflate(0.5F));
    }

    // Конструктор для обычного выстрела
    public EntityExplosiveBeam(Level level, LivingEntity shooter, float velocity) {
        super(ModEntities.EXPLOSIVE_BEAM.get(), level);
        this.setOwner(shooter);
        setupFromShooter(shooter, velocity, 1.0F);
    }

    // Конструктор из гранаты ZOMG
    public EntityExplosiveBeam(Level level, LivingEntity shooter, float velocity, EntityGrenadeZOMG grenade) {
        super(ModEntities.EXPLOSIVE_BEAM.get(), level);
        this.setOwner(shooter);
        this.setPos(grenade.getX(), grenade.getY() + grenade.getEyeHeight(), grenade.getZ());
        this.setYRot(grenade.getYRot());
        this.setXRot(grenade.getXRot());

        this.setPos(
                this.getX() - Math.cos(this.getYRot() * Math.PI / 180.0F) * 0.16F,
                this.getY() - 0.1D,
                this.getZ() - Math.sin(this.getYRot() * Math.PI / 180.0F) * 0.16F
        );

        double motionX = -Math.sin(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F);
        double motionY = -Math.sin(this.getXRot() * Math.PI / 180.0F);
        double motionZ = Math.cos(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F);

        this.shoot(motionX, motionY, motionZ, velocity * 1.5F, 1.0F);
    }

    public EntityExplosiveBeam(Level level, int x, int y, int z, double mx, double my, double mz, double grav) {
        super(ModEntities.EXPLOSIVE_BEAM.get(), level);
        this.setPos(x + 0.5, y + 0.5, z + 0.5);
        this.setDeltaMovement(mx, my, mz);
        this.gravity = grav;
    }

    private void setupFromShooter(LivingEntity shooter, float velocity, float spread) {
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.setYRot(shooter.getYRot());
        this.setXRot(shooter.getXRot());

        this.setPos(
                this.getX() - Math.cos(this.getYRot() * Math.PI / 180.0F) * 0.16F,
                this.getY() - 0.1D,
                this.getZ() - Math.sin(this.getYRot() * Math.PI / 180.0F) * 0.16F
        );

        double motionX = -Math.sin(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F);
        double motionY = -Math.sin(this.getXRot() * Math.PI / 180.0F);
        double motionZ = Math.cos(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F);

        this.shoot(motionX, motionY, motionZ, velocity * 1.5F, spread);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CRITICAL, (byte) 0);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        double length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;

        x += this.random.nextGaussian() * 0.0025D * inaccuracy;
        y += this.random.nextGaussian() * 0.0025D * inaccuracy;
        z += this.random.nextGaussian() * 0.0025D * inaccuracy;

        x *= velocity;
        y *= velocity;
        z *= velocity;

        this.setDeltaMovement(x, y, z);

        float f3 = (float) Math.sqrt(x * x + z * z);
        this.setYRot((float) (Math.atan2(x, z) * 180.0D / Math.PI));
        this.setXRot((float) (Math.atan2(y, f3) * 180.0D / Math.PI));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.ticksInAir = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 100) {
            this.discard();
        }

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            Vec3 motion = this.getDeltaMovement();
            float f = (float) Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            this.setYRot((float) (Math.atan2(motion.x, motion.z) * 180.0D / Math.PI));
            this.setXRot((float) (Math.atan2(motion.y, f) * 180.0D / Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        // Проверка на столкновение с блоком
        if (this.tilePos != null && this.tilePos != BlockPos.ZERO) {
            BlockState state = this.level().getBlockState(this.tilePos);
            if (!state.isAir()) {
                this.discard();
                this.explode();
                return;
            }
        }

        if (this.shake > 0) {
            --this.shake;
        } else {
            ++this.ticksInAir;

            // Обработка попаданий
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }

            // Движение
            Vec3 motion = this.getDeltaMovement();
            double dX = this.getX() + motion.x;
            double dY = this.getY() + motion.y;
            double dZ = this.getZ() + motion.z;

            this.setPos(dX, dY, dZ);

            // Проверка на воду
            if (this.isInWater()) {
                this.discard();
                this.explode();
                return;
            }

            // Замедление и гравитация
            float drag = 0.99F;
            this.setDeltaMovement(this.getDeltaMovement().multiply(drag, drag, drag));
            this.setDeltaMovement(this.getDeltaMovement().add(0, -gravity, 0));
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (!this.level().isClientSide) {
            this.explode();
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.tilePos = result.getBlockPos();
            this.tileState = this.level().getBlockState(this.tilePos);
            this.inData = this.tileState.getBlock().defaultBlockState().getLightEmission();
            this.explode();
        }
    }

    private void explode() {
        if (!this.level().isClientSide && !this.isRemoved()) {
            // Создаём взрыв MK3
            EntityNukeExplosionMK3 explosion = EntityNukeExplosionMK3.statFacFleija((ServerLevel) this.level(), this.getX(), this.getY(), this.getZ(), 10);
            if (explosion != null) {
                this.level().addFreshEntity(explosion);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 100.0F, 0.1F + this.random.nextFloat() * 0.1F);

                // Создаём облако
                EntityCloudFleijaRainbow cloud = new EntityCloudFleijaRainbow(ModEntities.CLOUD_FLEIJA_RAINBOW.get(), this.level());
                cloud.setPos(this.getX(), this.getY(), this.getZ());
                cloud.setScale(10);
                this.level().addFreshEntity(cloud);
            }
            this.discard();
        }
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        return super.canHitEntity(target) && (target != this.getOwner() || this.ticksInAir >= 5);
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("xTile", (short) this.tilePos.getX());
        tag.putShort("yTile", (short) this.tilePos.getY());
        tag.putShort("zTile", (short) this.tilePos.getZ());
        tag.putShort("life", (short) this.ticksInAir);
        tag.putInt("inTile", Block.getId(this.tileState != null ? this.tileState : Blocks.AIR.defaultBlockState()));
        tag.putByte("inData", (byte) this.inData);
        tag.putByte("shake", (byte) this.shake);
        tag.putByte("inGround", (byte) (this.inGround ? 1 : 0));
        tag.putDouble("damage", this.damage);
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.tilePos = new BlockPos(tag.getShort("xTile"), tag.getShort("yTile"), tag.getShort("zTile"));
        this.ticksInAir = tag.getShort("life");
        int blockId = tag.getInt("inTile");
        this.tileState = Block.stateById(blockId);
        this.inData = tag.getByte("inData") & 255;
        this.shake = tag.getByte("shake") & 255;
        this.inGround = tag.getByte("inGround") == 1;
        this.damage = tag.getDouble("damage");
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void setIsCritical(boolean critical) {
        byte b = this.entityData.get(CRITICAL);
        this.entityData.set(CRITICAL, critical ? (byte) (b | 1) : (byte) (b & -2));
    }

    public boolean getIsCritical() {
        return (this.entityData.get(CRITICAL) & 1) != 0;
    }
}