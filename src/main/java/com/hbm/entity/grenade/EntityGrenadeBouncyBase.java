package com.hbm.entity.grenade;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public abstract class EntityGrenadeBouncyBase extends Projectile {

    protected LivingEntity thrower;
    protected String throwerName;
    protected int timer = 0;

    public EntityGrenadeBouncyBase(EntityType<? extends EntityGrenadeBouncyBase> type, Level level) {
        super(type, level);
        this.setBoundingBox(this.getBoundingBox().inflate(0.25F));
    }

    public EntityGrenadeBouncyBase(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, LivingEntity thrower) {
        super(type, level);
        this.thrower = thrower;
        this.setOwner(thrower);
        this.setBoundingBox(this.getBoundingBox().inflate(0.25F));

        this.setPos(thrower.getX(), thrower.getY() + thrower.getEyeHeight(), thrower.getZ());
        this.setYRot(thrower.getYRot());
        this.setXRot(thrower.getXRot());

        this.setPos(
                this.getX() - Math.cos(this.getYRot() * Math.PI / 180.0F) * 0.16F,
                this.getY() - 0.1D,
                this.getZ() - Math.sin(this.getYRot() * Math.PI / 180.0F) * 0.16F
        );

        float f = 0.4F;
        double motionX = -Math.sin(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F) * f;
        double motionZ = Math.cos(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F) * f;
        double motionY = -Math.sin((this.getXRot() + this.getGravityAngle()) * Math.PI / 180.0F) * f;

        this.shoot(motionX, motionY, motionZ, this.getVelocity(), 1.0F);
        this.setXRot(0);
        this.xRotO = 0;
    }

    public EntityGrenadeBouncyBase(EntityType<? extends EntityGrenadeBouncyBase> type, Level level, double x, double y, double z) {
        super(type, level);
        this.setBoundingBox(this.getBoundingBox().inflate(0.25F));
        this.setPos(x, y, z);
    }

    @Override
    protected void defineSynchedData() {
        // Нет данных для синхронизации
    }

    protected float getVelocity() {
        return 1.5F;
    }

    protected float getGravityAngle() {
        return 0.0F;
    }

    protected float getGravityVelocity() {
        return 0.03F;
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        double length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;

        x += this.random.nextGaussian() * 0.0075D * inaccuracy;
        y += this.random.nextGaussian() * 0.0075D * inaccuracy;
        z += this.random.nextGaussian() * 0.0075D * inaccuracy;

        x *= velocity;
        y *= velocity;
        z *= velocity;

        this.setDeltaMovement(x, y, z);

        float f3 = (float) Math.sqrt(x * x + z * z);
        this.setYRot((float) (Math.atan2(x, z) * 180.0D / Math.PI));
        this.setXRot((float) (Math.atan2(y, f3) * 180.0D / Math.PI));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public void tick() {
        super.tick();

        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        this.xRotO = this.getXRot();

        // Вращение на основе скорости
        Vec3 motion = this.getDeltaMovement();
        this.setXRot(this.getXRot() - (float) motion.length() * 25);

        // Проверка на отскок
        boolean bounced = false;
        Vec3 start = this.position();
        Vec3 end = start.add(motion);

        ClipContext context = new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this);
        BlockHitResult hitResult = this.level().clip(context);

        if (hitResult.getType() != HitResult.Type.MISS) {
            Vec3 hitVec = hitResult.getLocation();

            this.setPos(
                    this.getX() + (hitVec.x - this.getX()) * 0.6,
                    this.getY() + (hitVec.y - this.getY()) * 0.6,
                    this.getZ() + (hitVec.z - this.getZ()) * 0.6
            );

            switch (hitResult.getDirection()) {
                case DOWN:
                case UP:
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1, -1, 1));
                    break;
                case NORTH:
                case SOUTH:
                    this.setDeltaMovement(this.getDeltaMovement().multiply(1, 1, -1));
                    break;
                case WEST:
                case EAST:
                    this.setDeltaMovement(this.getDeltaMovement().multiply(-1, 1, 1));
                    break;
            }

            bounced = true;

            if (motion.length() > 0.05) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.SLIME_JUMP, SoundSource.HOSTILE, 2.0F, 1.0F);
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(this.getBounceMod()));
        }

        if (!bounced) {
            this.setPos(this.getX() + motion.x, this.getY() + motion.y, this.getZ() + motion.z);
        }

        this.setYRot((float) (Math.atan2(motion.x, motion.z) * 180.0D / Math.PI));

        while (this.getYRot() - this.yRotO < -180.0F) {
            this.yRotO -= 360.0F;
        }
        while (this.getYRot() - this.yRotO >= 180.0F) {
            this.yRotO += 360.0F;
        }

        this.setYRot(this.yRotO + (this.getYRot() - this.yRotO) * 0.2F);

        float drag = 0.99F;
        float gravity = this.getGravityVelocity();

        if (this.isInWater()) {
            for (int i = 0; i < 4; i++) {
                this.level().addParticle(ParticleTypes.BUBBLE,
                        this.getX() - motion.x * 0.25,
                        this.getY() - motion.y * 0.25,
                        this.getZ() - motion.z * 0.25,
                        motion.x, motion.y, motion.z);
            }
            drag = 0.8F;
        }

        this.setDeltaMovement(this.getDeltaMovement().scale(drag));
        this.setDeltaMovement(this.getDeltaMovement().add(0, -gravity, 0));

        timer++;

        if (timer >= getMaxTimer() && !this.level().isClientSide) {
            explode();

        }
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.timer = tag.getInt("timer");
        if (tag.contains("throwerName")) {
            this.throwerName = tag.getString("throwerName");
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("timer", this.timer);
        if (this.thrower != null) {
            tag.putString("throwerName", this.thrower.getName().getString());
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public float getShadowRadius() {
        return 0.0F;
    }

    public LivingEntity getThrower() {
        if (this.thrower == null && this.throwerName != null && !this.throwerName.isEmpty()) {
            if (this.level() instanceof ServerLevel serverLevel) {
                for (Player player : serverLevel.players()) {
                    if (player.getName().getString().equals(this.throwerName)) {
                        this.thrower = player;
                        break;
                    }
                }
            }
        }
        return this.thrower;
    }

    public abstract void explode();

    protected abstract int getMaxTimer();

    protected abstract double getBounceMod();
}