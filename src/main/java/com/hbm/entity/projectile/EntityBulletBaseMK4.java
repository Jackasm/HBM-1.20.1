package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.items.weapon.sedna.BulletConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;

import java.util.UUID;

public class EntityBulletBaseMK4 extends ThrowableProjectile implements IEntityAdditionalSpawnData {

    public BulletConfig config;
    public float damage;
    private int ricochets = 0;
    private Entity lockonTarget = null;
    private UUID lockonTargetUUID = null;

    // For rendering tracers
    public double velocity = 0;
    public double prevVelocity = 0;
    public double accel;

    private BlockPos stuckPos;
    private Direction stuckSide;
    private boolean isStuck;

    private static MultiBufferSource.BufferSource bufferSource;
    private static int packedLight;

    private UUID ownerUUID;

    public MultiBufferSource.BufferSource getBufferSource() {
        return bufferSource;
    }

    public void setBufferSource(MultiBufferSource.BufferSource source) {
        bufferSource = source;
    }

    public void setPackedLight(int packedLight)
    {
        EntityBulletBaseMK4.packedLight = packedLight;
    }

    public int getPackedLight()
    {
        return packedLight;
    }

    @OnlyIn(Dist.CLIENT)
    private transient Matrix4f renderPoseMatrix;

    @OnlyIn(Dist.CLIENT)
    public void setRenderPose(Matrix4f poseMatrix) {
        this.renderPoseMatrix = poseMatrix;
    }

    @OnlyIn(Dist.CLIENT)
    public Matrix4f getRenderPose() {
        return renderPoseMatrix;
    }

    // Очистка для избежания утечек памяти
    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        this.renderPoseMatrix = null;
    }
    // Constructor for entity registration
    public EntityBulletBaseMK4(EntityType<? extends EntityBulletBaseMK4> type, Level level) {
        super(type, level);
        this.setNoGravity(true); // Gravity handled by config
    }

    // Custom constructor
    // Для стандартных пушек
    public EntityBulletBaseMK4(Level level, @Nullable LivingEntity shooter, BulletConfig config,
                               float baseDamage, float gunSpread, double sideOffset,
                               double heightOffset, double frontOffset) {
        super(ModEntities.BULLET.get(), level);
        this.config = config;
        this.damage = baseDamage * config.damageMult;

        if (shooter != null) {
            this.setOwner(shooter);
            this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());

            float yaw = shooter.getYRot();
            float pitch = shooter.getXRot();

            this.setYRot(yaw);
            this.setXRot(pitch);

            Vec3 offset = new Vec3(sideOffset, heightOffset, frontOffset);
            offset = offset.xRot(-pitch * Mth.DEG_TO_RAD);
            offset = offset.yRot(-yaw * Mth.DEG_TO_RAD);

            this.setPos(this.getX() + offset.x,
                    this.getY() + offset.y,
                    this.getZ() + offset.z);
        }

        // Вычисляем начальное направление
        float yaw = this.getYRot();
        float pitch = this.getXRot();

        double motionX = -Mth.sin(yaw * Mth.DEG_TO_RAD) * Mth.cos(pitch * Mth.DEG_TO_RAD);
        double motionY = -Mth.sin(pitch * Mth.DEG_TO_RAD);
        double motionZ =  Mth.cos(yaw * Mth.DEG_TO_RAD) * Mth.cos(pitch * Mth.DEG_TO_RAD);

        // Используем отдельный метод для установки движения с разбросом
        this.shoot(motionX, motionY, motionZ, config.velocity, gunSpread);
    }

    // Turret constructor
    public EntityBulletBaseMK4(Level level, BulletConfig config, float baseDamage,
                               float gunSpread, float yaw, float pitch) {
        super(ModEntities.BULLET.get(), level);

        this.config = config;
        this.damage = baseDamage * config.damageMult;

        this.setYRot(yaw * Mth.RAD_TO_DEG);
        this.setXRot(-pitch * Mth.RAD_TO_DEG);

        double motionX = -Mth.sin(this.getYRot() * Mth.DEG_TO_RAD) *
                Mth.cos(this.getXRot() * Mth.DEG_TO_RAD);
        double motionZ = Mth.cos(this.getYRot() * Mth.DEG_TO_RAD) *
                Mth.cos(this.getXRot() * Mth.DEG_TO_RAD);
        double motionY = -Mth.sin(this.getXRot() * Mth.DEG_TO_RAD);

        this.shoot(motionX, motionY, motionZ, config.velocity, gunSpread);
    }

    //Missile constructor
    public EntityBulletBaseMK4(Level level, @Nullable LivingEntity shooter, BulletConfig config,
                               float baseDamage, float gunSpread,
                               double posX, double posY, double posZ,
                               double motionX, double motionY, double motionZ) {
        super(ModEntities.BULLET.get(), level);
        this.config = config;
        this.damage = baseDamage * config.damageMult;

        if (shooter != null) {
            this.setOwner(shooter);
        }

        this.setPos(posX, posY, posZ);
        this.setDeltaMovement(motionX, motionY, motionZ);

        // Устанавливаем вращение на основе движения
        double horizontal = Math.sqrt(motionX * motionX + motionZ * motionZ);
        this.setYRot((float)(Math.atan2(motionX, motionZ) * Mth.RAD_TO_DEG));
        this.setXRot((float)(Math.atan2(motionY, horizontal) * Mth.RAD_TO_DEG));

        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        // Добавляем разброс, если нужно
        if (gunSpread > 0) {
            this.shoot(motionX, motionY, motionZ, config.velocity, gunSpread);
        }
    }

    @Override
    protected void defineSynchedData() {
        // Define synced data if needed
    }

    @Override
    public void tick() {

        if (config == null) {
            this.discard();
            return;
        }

        // Store previous position for velocity calculation
        Vec3 prevPos = this.position();

        if (accel != 0) {
            Vec3 motion = this.getDeltaMovement();
            if (motion.lengthSqr() > 0) {
                // Сохраняем направление, увеличиваем скорость
                Vec3 direction = motion.normalize();
                double currentSpeed = motion.length();
                double newSpeed = currentSpeed + accel;
                this.setDeltaMovement(direction.scale(newSpeed));
            }
        }

        super.tick();
        // Apply gravity from config
        if (config.gravity != 0) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -config.gravity, 0));
        }

        // Update velocity for rendering
        Vec3 currentPos = this.position();
        Vec3 delta = currentPos.subtract(prevPos);
        this.prevVelocity = this.velocity;
        this.velocity = delta.length();

        // Lock-on targeting
        if (this.lockonTarget == null && this.lockonTargetUUID != null) {
            // Try to find target by UUID
            if (this.level() instanceof ServerLevel serverLevel) {
                Entity entity = serverLevel.getEntity(this.lockonTargetUUID);
                if (entity != null) {
                    this.lockonTarget = entity;
                }
            }
        }

        if (this.lockonTarget != null && !this.lockonTarget.isRemoved()) {
            Vec3 motion = this.getDeltaMovement();
            double vel = motion.length();
            Vec3 targetPos = this.lockonTarget.position().add(0, this.lockonTarget.getBbHeight() / 2, 0);
            Vec3 deltaToTarget = targetPos.subtract(this.position());

            float turn = Math.min(0.005F * this.tickCount, 1F);
            Vec3 newMotion = new Vec3(
                    Mth.lerp(turn, motion.x, deltaToTarget.x),
                    Mth.lerp(turn, motion.y, deltaToTarget.y),
                    Mth.lerp(turn, motion.z, deltaToTarget.z)
            ).normalize().scale(vel);

            this.setDeltaMovement(newMotion);
        }

        // Update rotation based on movement
        if (!this.onGround() && velocity > 0) {
            Vec3 motion = this.getDeltaMovement();
            double horizontal = Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            this.setYRot((float)(Math.atan2(motion.x, motion.z) * Mth.RAD_TO_DEG));
            this.setXRot((float)(Math.atan2(motion.y, horizontal) * Mth.RAD_TO_DEG));

            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        // Call update handler from config
        if (config.onUpdate != null) {
            config.onUpdate.accept(this);
        }

        // Check expiration
        if (this.tickCount > config.expires) {
            this.discard();
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (this.level().isClientSide) return;

        if (config.onImpact != null) {
            config.onImpact.accept(this, result);
        }

        if (this.isRemoved()) return;

        if (config.onRicochet != null) {
            config.onRicochet.accept(this, result);
        }

        if (result.getType() == HitResult.Type.ENTITY && config.onEntityHit != null) {
            config.onEntityHit.accept(this, result);
        }

        super.onHit(result);
    }

    @Override
    protected float getGravity() {
        return (float) config.gravity;
    }

    @Override
    public boolean isNoGravity() {
        return config.gravity == 0;
    }

    // Getters and setters
    public BulletConfig getConfig() {
        return config;
    }

    public void setConfig(BulletConfig config) {
        this.config = config;
    }

    public float getDamage() {
        return damage;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    public int getRicochets() {
        return ricochets;
    }

    public void setRicochets(int ricochets) {
        this.ricochets = ricochets;
    }

    public Entity getLockonTarget() {
        return lockonTarget;
    }

    public void setLockonTarget(Entity lockonTarget) {
        this.lockonTarget = lockonTarget;
        if (lockonTarget != null) {
            this.lockonTargetUUID = lockonTarget.getUUID();
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putFloat("Damage", this.damage);
        compound.putInt("Ricochets", this.ricochets);
        compound.putInt("ConfigId", config != null ? config.id : -1);

        if (lockonTargetUUID != null) {
            compound.putUUID("LockonTarget", lockonTargetUUID);
        }

        if (this.ownerUUID != null) {
            compound.putUUID("OwnerUUID", this.ownerUUID);
        }
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.damage = compound.getFloat("Damage");
        this.ricochets = compound.getInt("Ricochets");

        int configId = compound.getInt("ConfigId");
        if (configId >= 0 && configId < BulletConfig.configs.size()) {
            this.config = BulletConfig.configs.get(configId);
        }

        if (compound.hasUUID("LockonTarget")) {
            this.lockonTargetUUID = compound.getUUID("LockonTarget");
        }

        if (compound.hasUUID("OwnerUUID")) {
            this.ownerUUID = compound.getUUID("OwnerUUID");
        }
    }

    // Network handling
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeBoolean(this.ownerUUID != null);
        if (this.ownerUUID != null) {
            buffer.writeUUID(this.ownerUUID);
        }

        buffer.writeInt(config != null ? config.id : -1);
        buffer.writeFloat(this.damage);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        if (additionalData.readBoolean()) {
            UUID ownerUUID = additionalData.readUUID();
            this.ownerUUID = ownerUUID; // Сохраняем UUID
            // Опционально: пытаемся найти владельца
            Entity owner = findEntityByUUID(ownerUUID);
            if (owner instanceof LivingEntity) {
                this.setOwner(owner);
            }
        }

        int configId = additionalData.readInt();
        if (configId >= 0 && configId < BulletConfig.configs.size()) {
            this.config = BulletConfig.configs.get(configId);
        }

        this.damage = additionalData.readFloat();
    }

    private Entity findEntityByUUID(UUID uuid) {
        Level level = this.level();
        // Проверяем, что мы на серверной стороне
        if (level instanceof ServerLevel serverLevel) {
            // Используем правильный серверный метод для поиска
            return serverLevel.getEntity(uuid);
        }
        return null;
    }

    // Helper methods for shooting
    public void shoot(double motionX, double motionY, double motionZ, float velocity, float inaccuracy) {
        // Нормализуем направление
        double length = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
        motionX /= length;
        motionY /= length;
        motionZ /= length;

        // Добавляем разброс (inaccuracy)
        if (inaccuracy > 0) {
            double spread = 0.075D * inaccuracy;
            motionX += this.random.nextGaussian() * spread;
            motionY += this.random.nextGaussian() * spread;
            motionZ += this.random.nextGaussian() * spread;

            // Перенормируем после добавления разброса
            length = Math.sqrt(motionX * motionX + motionY * motionY + motionZ * motionZ);
            motionX /= length;
            motionY /= length;
            motionZ /= length;
        }

        // Устанавливаем скорость
        this.setDeltaMovement(motionX * velocity, motionY * velocity, motionZ * velocity);

        // Устанавливаем вращение на основе направления
        double horizontal = Math.sqrt(motionX * motionX + motionZ * motionZ);
        this.setYRot((float)(Math.atan2(motionX, motionZ) * Mth.RAD_TO_DEG));
        this.setXRot((float)(Math.atan2(motionY, horizontal) * Mth.RAD_TO_DEG));

        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        // Сбрасываем accel
        this.accel = 0;
    }

    // Check if bullet should penetrate
    public boolean doesPenetrate() {
        return config != null && config.doesPenetrate;
    }

    // Check if bullet is spectral (ignores block)
    public boolean isSpectral() {
        return config != null && config.isSpectral;
    }

    // Get self-damage delay
    public int getSelfDamageDelay() {
        return config != null ? config.selfDamageDelay : 0;
    }

    public void setStuck(BlockPos pos, Direction side) {
        this.stuckPos = pos;
        this.stuckSide = side;
        this.isStuck = true;
        this.setNoGravity(true);
        this.setDeltaMovement(Vec3.ZERO);
    }

    public BlockPos getStuckPos() {
        return stuckPos;
    }

    public Direction getStuckSide() {
        return stuckSide;
    }

    public boolean isStuck() {
        return isStuck;
    }

    public UUID getOwnerUUID() {
        return this.ownerUUID;
    }

    @Override
    public void setOwner(@Nullable Entity owner) {
        super.setOwner(owner);
        if (owner != null) {
            this.ownerUUID = owner.getUUID();
        } else {
            this.ownerUUID = null;
        }
    }
}