package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.items.weapon.sedna.BulletConfig;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class EntityBulletBeamBase extends Entity implements IEntityAdditionalSpawnData {

    private static final EntityDataAccessor<Integer> CONFIG_ID =
            SynchedEntityData.defineId(EntityBulletBeamBase.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> DAMAGE =
            SynchedEntityData.defineId(EntityBulletBeamBase.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> BEAM_LENGTH =
            SynchedEntityData.defineId(EntityBulletBeamBase.class, EntityDataSerializers.FLOAT);

    @Nullable
    private LivingEntity thrower;
    public UUID throwerUUID;
    public BulletConfig config;
    public Vec3 heading;
    private double beamLengthClient;
    private static MultiBufferSource.BufferSource bufferSource;

    public MultiBufferSource.BufferSource getBufferSource() {
        return bufferSource;
    }

    public void setBufferSource(MultiBufferSource.BufferSource source) {
        bufferSource = source;
    }

    @OnlyIn(Dist.CLIENT)
    private Matrix4f renderPose;

    public EntityBulletBeamBase(EntityType<? extends EntityBulletBeamBase> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noCulling = true; // Аналог ignoreFrustumCheck
        this.setInvulnerable(true);
        this.heading = Vec3.ZERO;
    }

    // Основной конструктор
    public EntityBulletBeamBase(EntityType<? extends EntityBulletBeamBase> type, Level level,
                                @Nullable LivingEntity thrower, BulletConfig config, float baseDamage) {
        this(type, level);
        this.thrower = thrower;
        if (thrower != null) {
            this.throwerUUID = thrower.getUUID();
        }
        this.setBulletConfig(config);
        this.setDamage(baseDamage * config.damageMult);
    }

    // Конструктор с параметрами позиционирования
    public EntityBulletBeamBase(Level level,
                                @org.jetbrains.annotations.Nullable LivingEntity thrower, BulletConfig config, float baseDamage,
                                float angularInaccuracy, double sideOffset,
                                double heightOffset, double frontOffset) {
        super(ModEntities.BULLET_BEAM.get(), level);
        this.setBulletConfig(config);
        this.thrower = thrower;
        if (thrower != null) {
            this.throwerUUID = thrower.getUUID();
        }
        this.setDamage(baseDamage * config.damageMult);

        // Устанавливаем позицию и поворот
        this.setPos(
                Objects.requireNonNull(thrower).getX(),
                thrower.getY() + thrower.getEyeHeight(),
                thrower.getZ()
        );

        // Применяем неточность
        float yaw = thrower.getYRot() + (float) random.nextGaussian() * angularInaccuracy;
        float pitch = thrower.getXRot() + (float) random.nextGaussian() * angularInaccuracy;

        this.setYRot(yaw);
        this.setXRot(pitch);

        // Применяем смещение
        Vec3 offset = new Vec3(sideOffset, heightOffset, frontOffset);
        offset = offset.xRot(-pitch * Mth.DEG_TO_RAD);
        offset = offset.yRot(-yaw * Mth.DEG_TO_RAD);

        this.setPos(this.getX() + offset.x,
                this.getY() + offset.y,
                this.getZ() + offset.z);

        // Вычисляем направление
        double range = 250.0;
        this.heading = calculateHeading(yaw, pitch).scale(range);

        performHitscan();
    }

    @OnlyIn(Dist.CLIENT)
    public void setRenderPose(Matrix4f pose) {
        this.renderPose = pose;
    }

    @OnlyIn(Dist.CLIENT)
    public Matrix4f getRenderPose() {
        return renderPose;
    }

    @Override
    public void remove(@NotNull RemovalReason reason) {
        super.remove(reason);
        this.renderPose = null;
    }





    @Override
    protected void defineSynchedData() {
        this.entityData.define(CONFIG_ID, -1);
        this.entityData.define(DAMAGE, 0.0F);
        this.entityData.define(BEAM_LENGTH, 0.0F);
    }

    // Вспомогательный метод для вычисления направления
    private Vec3 calculateHeading(float yaw, float pitch) {
        // Всегда вычисляем из углов сущности
        float yawRad = yaw * Mth.DEG_TO_RAD;
        float pitchRad = pitch * Mth.DEG_TO_RAD;

        // Стандартная формула Minecraft для направления взгляда
        double x = -Mth.sin(yawRad) * Mth.cos(pitchRad);
        double y = -Mth.sin(pitchRad);
        double z = Mth.cos(yawRad) * Mth.cos(pitchRad);

        return new Vec3(x, y, z);
    }

    public void setBulletConfig(BulletConfig config) {
        this.config = config;
        if (config != null) {
            this.entityData.set(CONFIG_ID, config.id);
        }
    }

    @Nullable
    public BulletConfig getConfig() {
        if (this.config != null) return this.config;

        int id = this.entityData.get(CONFIG_ID);
        if (id >= 0 && id < BulletConfig.configs.size()) {
            this.config = BulletConfig.configs.get(id);
            return this.config;
        }
        return null;
    }

    public float getDamage() {
        return this.entityData.get(DAMAGE);
    }

    public void setDamage(float damage) {
        this.entityData.set(DAMAGE, damage);
    }

    public float getBeamLength() {
        return this.entityData.get(BEAM_LENGTH);
    }

    public void setBeamLength(float length) {
        this.entityData.set(BEAM_LENGTH, length);
    }

    @Nullable
    public LivingEntity getThrower() {
        return thrower;
    }

    public void setRotationsFromVector(Vec3 delta) {
        double length = delta.length();
        if (length > 0) {
            this.setXRot((float)(-Math.asin(delta.y / length) * Mth.RAD_TO_DEG));
            this.setYRot((float)(-Math.atan2(delta.x, delta.z) * Mth.RAD_TO_DEG));
        }

        this.heading = calculateHeading(this.getYRot(), this.getXRot());
    }

    public void performHitscanExternal(double range) {
        if (this.heading != null) {
            this.heading = this.heading.normalize().scale(range);
        } else {
            this.heading = calculateHeading(this.getYRot(), this.getXRot()).scale(range);
        }
        performHitscan();
    }

    @Override
    public void tick() {
        super.tick();

        BulletConfig config = getConfig();
        if (config == null) {
            this.discard();
            return;
        }

        // Вызываем обработчик обновления из конфига
        if (config.onUpdate != null) {
            config.onUpdate.accept(this);
        }

        // Проверяем истечение времени жизни
        if (this.tickCount > config.expires) {
            this.discard();
        }
    }

    protected void performHitscan() {
        if (this.level().isClientSide) return;

        Vec3 startPos = this.position();
        Vec3 endPos = startPos.add(this.heading);

        HitResult hitResult = null;
        if (!this.isSpectral()) {
            hitResult = this.level().clip(new net.minecraft.world.level.ClipContext(
                    startPos, endPos,
                    net.minecraft.world.level.ClipContext.Block.OUTLINE,
                    net.minecraft.world.level.ClipContext.Fluid.NONE,
                    this
            ));
        }

        if (hitResult != null) {
            endPos = hitResult.getLocation();
        }

        // Проверка столкновений с сущностями
        if (!this.level().isClientSide && this.doesImpactEntities()) {
            AABB scanBox = this.getBoundingBox().expandTowards(this.heading).inflate(1.0);
            List<Entity> entities = this.level().getEntities(this, scanBox);

            Entity closestEntity = null;
            double closestDistance = Double.MAX_VALUE;
            Vec3 closestHitPos = null;

            for (Entity entity : entities) {
                if (entity.isRemoved()) continue;
                if (entity == thrower) continue;
                if (!entity.isAlive()) continue;

                // Проверяем столкновение с сущностью
                AABB entityBox = entity.getBoundingBox().inflate(0.3);
                Vec3 entityHit = entityBox.clip(startPos, endPos).orElse(null);

                if (entityHit != null) {
                    double distance = startPos.distanceToSqr(entityHit);
                    this.beamLengthClient = distance;
                    this.setBeamLength((float) this.beamLengthClient);

                    if (this.doesPenetrate()) {
                        // Если проникает, обрабатываем все сущности
                        onImpact(new EntityHitResult(entity, entityHit));
                    } else {
                        // Иначе находим ближайшую
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestEntity = entity;
                            closestHitPos = entityHit;
                        }
                    }
                }
            }

            if (!this.doesPenetrate() && closestEntity != null) {
                hitResult = new EntityHitResult(closestEntity, closestHitPos);
            }
        }

        // Обработка попадания в блок
        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = null;
            if (hitResult instanceof BlockHitResult) {
                blockHit = (BlockHitResult) hitResult;
            }
            BlockPos blockPos = Objects.requireNonNull(blockHit).getBlockPos();

            if (this.level().getBlockState(blockPos).is(Blocks.NETHER_PORTAL)) {
                this.handleInsidePortal(blockPos);
            } else {
                onImpact(hitResult);
            }

            this.beamLengthClient = startPos.distanceTo(blockHit.getLocation());
        } else if (hitResult != null && hitResult.getType() == HitResult.Type.ENTITY) {
            onImpact(hitResult);
            this.beamLengthClient = startPos.distanceTo(hitResult.getLocation());
        } else {
            this.beamLengthClient = startPos.distanceTo(endPos);
        }

        this.setBeamLength((float) this.beamLengthClient);
    }

    protected void onImpact(HitResult hitResult) {
        if (this.level().isClientSide) return;

        BulletConfig config = getConfig();
        if (config != null && config.onImpactBeam != null) {
            config.onImpactBeam.accept(this, hitResult);
        }
    }

    public boolean doesImpactEntities() {
        BulletConfig config = getConfig();
        return config != null && config.impactsEntities;
    }

    public boolean doesPenetrate() {
        BulletConfig config = getConfig();
        return config != null && config.doesPenetrate;
    }

    public boolean isSpectral() {
        BulletConfig config = getConfig();
        return config != null && config.isSpectral;
    }

    // NBT handling
    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        if (compound.contains("ThrowerUUID")) {
            this.throwerUUID = compound.getUUID("ThrowerUUID");
        }
        if (compound.contains("ConfigId")) {
            int configId = compound.getInt("ConfigId");
            if (configId >= 0 && configId < BulletConfig.configs.size()) {
                this.config = BulletConfig.configs.get(configId);
            }
        }

        if (compound.contains("Damage")) {
            this.setDamage(compound.getFloat("Damage"));
        }

        if (compound.contains("BeamLength")) {
            this.beamLengthClient = compound.getDouble("BeamLength");
        }

        // Сущность уничтожается при загрузке (как в оригинале)
        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        if (throwerUUID != null) {
            compound.putUUID("ThrowerUUID", throwerUUID);
        }
        if (config != null) {
            compound.putInt("ConfigId", config.id);
        }
        compound.putFloat("Damage", this.getDamage());
        compound.putDouble("BeamLength", this.beamLengthClient);
    }

    // Network handling
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        buffer.writeDouble(this.beamLengthClient);
        buffer.writeFloat(this.getYRot());
        buffer.writeFloat(this.getXRot());

        if (throwerUUID != null) {
            buffer.writeBoolean(true);
            buffer.writeUUID(throwerUUID);
        } else {
            buffer.writeBoolean(false);
        }

        // Отправляем ID конфига
        buffer.writeInt(config != null ? config.id : -1);
        buffer.writeFloat(this.getDamage());

        buffer.writeDouble(this.heading.x);
        buffer.writeDouble(this.heading.y);
        buffer.writeDouble(this.heading.z);
        buffer.writeDouble(this.heading.z);
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        this.beamLengthClient = additionalData.readDouble();
        this.setYRot(additionalData.readFloat());
        this.setXRot(additionalData.readFloat());
        if (additionalData.readBoolean()) {
            this.throwerUUID = additionalData.readUUID();
        }

        int configId = additionalData.readInt();
        if (configId >= 0 && configId < BulletConfig.configs.size()) {
            this.config = BulletConfig.configs.get(configId);
        }

        this.setDamage(additionalData.readFloat());

        double headingX = additionalData.readDouble();
        double headingY = additionalData.readDouble();
        double headingZ = additionalData.readDouble();
        this.heading = new Vec3(headingX, headingY, headingZ);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public boolean save(@NotNull CompoundTag compound) {
        return false;
    }

}