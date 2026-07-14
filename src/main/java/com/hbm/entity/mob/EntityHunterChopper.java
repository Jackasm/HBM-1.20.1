package com.hbm.entity.mob;

import com.hbm.entity.projectile.EntityBullet;
import com.hbm.entity.projectile.EntityChopperMine;

import com.hbm.extprop.IRadiationImmune;
import com.hbm.items.ModItems;

import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.sound.ModSounds;
import com.hbm.util.Library;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityHunterChopper extends FlyingMob implements IRadiationImmune {

    private static final EntityDataAccessor<Byte> ATTACK_STATE = SynchedEntityData.defineId(EntityHunterChopper.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Float> FLOAT_1 = SynchedEntityData.defineId(EntityHunterChopper.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> FLOAT_2 = SynchedEntityData.defineId(EntityHunterChopper.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Byte> DYING_STATE = SynchedEntityData.defineId(EntityHunterChopper.class, EntityDataSerializers.BYTE);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.hbm.hunter_chopper"),
            BossEvent.BossBarColor.RED,
            BossEvent.BossBarOverlay.PROGRESS
    );

    public int courseChangeCooldown;
    public double waypointX;
    public double waypointY;
    public double waypointZ;
    private Entity targetedEntity;
    public int prevAttackCounter;
    public int attackCounter;
    public int mineDropCounter;
    public boolean isDying = false;

    public EntityHunterChopper(EntityType<? extends EntityHunterChopper> type, Level level) {
        super(type, level);
        this.setSize(8.25F, 3.0F);
        this.setInvulnerable(false);
        this.xpReward = 500;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.getEntityData().define(ATTACK_STATE, (byte) 0);
        this.getEntityData().define(FLOAT_1, 0.0F);
        this.getEntityData().define(FLOAT_2, 0.0F);
        this.getEntityData().define(DYING_STATE, (byte) 0);
    }

    @Override
    public boolean isInvulnerableTo(@NotNull DamageSource source) {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (this.isInvulnerableTo(source) || source.getEntity() != null) {
            // Проверяем, не является ли это прямым уроном от сущности
            if (source.getEntity() != null) {
                // В оригинале было условие source instanceof EntityDamageSource, но в 1.20.1 это не отдельный класс
                // Поэтому просто пропускаем, если урон от сущности (кроме некоторых)
            }
        }

        if (source.is(ModDamageSource.SHRAPNEL) || source.is(ModDamageSource.NUCLEAR_BLAST) ||
                source.is(ModDamageSource.BLACKHOLE) || source.is(DamageTypeTags.IS_EXPLOSION) ||
                ModDamageSource.getIsTau(source) || ModDamageSource.getIsSubatomic(source) || ModDamageSource.getIsDischarge(source)) {
            // полный урон
        } else {
            amount *= 0.1F;
        }

        if (this.getHealth() <= 0.1F) {
            return false;
        }

        if (this.isInvulnerableTo(source) || source.getEntity() != null) {
            // дополнительная проверка
            return false;
        }

        if (amount >= this.getHealth()) {
            this.initDeath();
            this.setIsDying(true);
            this.setHealth(0.1F);
            return false;
        }

        if (this.random.nextInt(15) == 0) {
            if (!this.level().isClientSide && !this.isDying) {
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 5.0F, Level.ExplosionInteraction.MOB);
                this.dropDamageItem();
            }
        }

        for (int j = 0; j < 3; j++) {
            double d0 = this.random.nextDouble() / 20 * (this.random.nextInt(2) == 0 ? -1 : 1);
            double d1 = this.random.nextDouble() / 20 * (this.random.nextInt(2) == 0 ? -1 : 1);
            double d2 = this.random.nextDouble() / 20 * (this.random.nextInt(2) == 0 ? -1 : 1);
            for (int i = 0; i < 8; i++) {
                if (this.level().isClientSide) {
                    this.level().addParticle(ParticleTypes.FIREWORK, this.getX(), this.getY(), this.getZ(),
                            d0 * i * 0.25, d1 * i * 0.25, d2 * i * 0.25);
                }
            }
        }

        return super.hurt(source, amount);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            // Обновление босс-бара
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }

        if (!this.level().isClientSide && this.level().getDifficulty() == net.minecraft.world.Difficulty.PEACEFUL) {
            this.discard();
            return;
        }

        if (!isDying) {
            // Основная логика из updateEntityActionState
            this.updateAi();
        } else {
            // Логика падения и взрывов при смерти
            this.fallAndExplode();
        }

        // Вращение
        this.updateRotation();
    }

    private void updateAi() {
        if (!this.level().isClientSide) {
            // Звук (в оригинале игрался каждый тик, но это слишком часто; оставим раз в несколько тиков)
            if (this.tickCount % 20 == 0) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.NULL_CHOPPER.get(), SoundSource.HOSTILE, 10.0F, 0.5F);
            }
        }

        this.prevAttackCounter = this.attackCounter;
        double d0 = this.waypointX - this.getX();
        double d1 = this.waypointY - this.getY();
        double d2 = this.waypointZ - this.getZ();
        double d3 = d0 * d0 + d1 * d1 + d2 * d2;

        if (d3 < 1.0D || d3 > 3600.0D) {
            if (this.targetedEntity != null) {
                this.waypointX = targetedEntity.getX() + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0F;
                this.waypointZ = targetedEntity.getZ() + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0F;
                this.waypointY = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING, (int) waypointX, (int) waypointZ) + 10 + this.random.nextInt(15);
            } else {
                this.waypointX = this.getX() + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0F;
                this.waypointZ = this.getZ() + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0F;
                this.waypointY = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING, (int) waypointX, (int) waypointZ) + 10 + this.random.nextInt(15);
            }
        }

        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown += this.random.nextInt(5) + 2;
            d3 = Math.sqrt(d3);
            if (this.isCourseTraversable(this.waypointX, this.waypointY, this.waypointZ, d3)) {
                this.setDeltaMovement(this.getDeltaMovement().add(d0 / d3 * 0.1D, d1 / d3 * 0.1D, d2 / d3 * 0.1D));
            } else {
                this.waypointX = this.getX() + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0F;
                this.waypointZ = this.getZ() + (this.random.nextFloat() * 2.0F - 1.0F) * 16.0F;
                this.waypointY = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING, (int) waypointX, (int) waypointZ) + 10 + this.random.nextInt(15);
            }
        }

        if (this.targetedEntity != null && this.targetedEntity.isRemoved()) {
            this.targetedEntity = null;
        }

        if (this.targetedEntity == null || this.attackCounter <= 0) {
            this.targetedEntity = Library.getClosestEntityForChopper(this.level(), this.getX(), this.getY(), this.getZ(), 250);
        }

        double d4 = 64.0D;
        if (this.targetedEntity != null && this.targetedEntity.distanceToSqr(this) < d4 * d4) {
            double d8 = 2.0D;
            Vec3 vec3 = this.getViewVector(1.0F);
            double xStart = this.getX() + vec3.x * d8;
            double yStart = this.getY() - 0.5;
            double zStart = this.getZ() + vec3.z * d8;
            double d5 = this.targetedEntity.getX() - xStart;
            double d6 = this.targetedEntity.getY() + this.targetedEntity.getBbHeight() / 2.0F - yStart;
            double d7 = this.targetedEntity.getZ() - zStart;

            ++this.attackCounter;
            if (this.attackCounter >= 200) {
                this.attackCounter -= 200;
            }

            if (this.attackCounter % 2 == 0 && this.attackCounter >= 120) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.OSIPR_SHOOT.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
                EntityBullet bullet = new EntityBullet(this.level(), this, 3.0F, 35, 45, false, "chopper");
                Vec3 vec2 = new Vec3(d5 - 1 + this.random.nextInt(3), d6 - 1 + this.random.nextInt(3), d7 - 1 + this.random.nextInt(3)).normalize();
                double motion = 3.0;
                bullet.setDeltaMovement(vec2.x * motion, vec2.y * motion, vec2.z * motion);
                bullet.setDamage(3 + this.random.nextInt(5));
                bullet.setPos(xStart, yStart, zStart);
                this.level().addFreshEntity(bullet);
            }

            if (this.attackCounter == 80) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.CHOPPER_CHARGE.get(), SoundSource.HOSTILE, 5.0F, 1.0F);
            }

            this.mineDropCounter++;
            if (this.mineDropCounter > 100 && this.random.nextInt(15) == 0) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        ModSounds.CHOPPER_DROP.get(), SoundSource.HOSTILE, 15.0F, 1.0F);
                EntityChopperMine mine = new EntityChopperMine(this.level(), this.getX(), this.getY() - 0.5, this.getZ(), 0, -0.3, 0, this);
                this.mineDropCounter = 0;
                this.level().addFreshEntity(mine);

                if (this.random.nextInt(3) == 0) {
                    EntityChopperMine mine1 = new EntityChopperMine(this.level(), this.getX(), this.getY() - 0.5, this.getZ(), 1, -0.3, 0, this);
                    EntityChopperMine mine2 = new EntityChopperMine(this.level(), this.getX(), this.getY() - 0.5, this.getZ(), 0, -0.3, 1, this);
                    EntityChopperMine mine3 = new EntityChopperMine(this.level(), this.getX(), this.getY() - 0.5, this.getZ(), -1, -0.3, 0, this);
                    EntityChopperMine mine4 = new EntityChopperMine(this.level(), this.getX(), this.getY() - 0.5, this.getZ(), 0, -0.3, -1, this);
                    this.level().addFreshEntity(mine1);
                    this.level().addFreshEntity(mine2);
                    this.level().addFreshEntity(mine3);
                    this.level().addFreshEntity(mine4);
                }
            }
        } else {
            if (this.attackCounter > 0) {
                this.attackCounter = 0;
            }
        }

        if (!this.level().isClientSide) {
            byte b1 = this.getEntityData().get(ATTACK_STATE);
            byte b0 = (byte) (this.attackCounter > 10 ? 1 : 0);
            if (b1 != b0) {
                this.getEntityData().set(ATTACK_STATE, b0);
            }
        }
    }

    private void fallAndExplode() {
        this.setDeltaMovement(this.getDeltaMovement().add(0, -0.08, 0));
        double speed = Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z);
        if (speed * 1.2 < 1.8) {
            this.setDeltaMovement(this.getDeltaMovement().x * 1.2, this.getDeltaMovement().y, this.getDeltaMovement().z * 1.2);
        }

        if (this.random.nextInt(20) == 0) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 5.0F, Level.ExplosionInteraction.MOB);
        }

        if (!this.level().isClientSide) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "exhaust");
            data.putString("mode", "meteor");
            data.putInt("count", 10);
            data.putDouble("width", 1);
            PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(data, this.getX(), this.getY(), this.getZ()), this.level(), this.blockPosition(), 100);
        }

        this.setYRot(this.getYRot() + 20.0F);

        if (this.onGround()) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 15.0F, Level.ExplosionInteraction.MOB);
            this.dropItems();
            this.discard();
        }

        if (this.tickCount % 2 == 0) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.NULL_CRASHING.get(), SoundSource.HOSTILE, 10.0F, 0.5F);
        }
    }

    private void updateRotation() {
        float f3 = (float) Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z);
        float targetYaw;
        if (this.targetedEntity == null) {
            targetYaw = (float) (Math.atan2(this.getDeltaMovement().x, this.getDeltaMovement().z) * 180.0D / Math.PI);
        } else {
            targetYaw = (float) (Math.atan2(this.getX() - this.targetedEntity.getX(), this.getZ() - this.targetedEntity.getZ()) * 180.0D / Math.PI);
        }
        float deltaYaw = targetYaw - this.getYRot();
        deltaYaw = net.minecraft.util.Mth.wrapDegrees(deltaYaw);
        if (deltaYaw > 10) deltaYaw = 10;
        if (deltaYaw < -10) deltaYaw = -10;
        this.setYRot(this.getYRot() + deltaYaw);

        float targetPitch = (float) (Math.atan2(this.getDeltaMovement().y, f3) * 180.0D / Math.PI);
        this.setXRot(targetPitch);

        // Ограничение питча
        if (this.getXRot() <= 330 && this.getXRot() >= 30) {
            if (this.getXRot() < 180) this.setXRot(30);
            else this.setXRot(330);
        }
    }

    private boolean isCourseTraversable(double targetX, double targetY, double targetZ, double distance) {
        double d4 = (targetX - this.getX()) / distance;
        double d5 = (targetY - this.getY()) / distance;
        double d6 = (targetZ - this.getZ()) / distance;
        AABB aabb = this.getBoundingBox();
        for (int i = 1; i < distance; ++i) {
            aabb = aabb.move(d4, d5, d6);
            if (!this.level().noCollision(this, aabb)) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHitIn) {
        super.dropCustomDeathLoot(source, looting, recentlyHitIn);
        this.dropItems();
    }

    protected void dropItems() {
        this.spawnAtLocation(ModItems.COMBINE_SCRAP.get(), this.random.nextInt(8) + 1);
        this.spawnAtLocation(ModItems.PLATE_COMBINE_STEEL.get(), this.random.nextInt(5) + 1);
    }

    protected void dropDamageItem() {
        if (this.random.nextInt(10) < 6) {
            this.spawnAtLocation(ModItems.COMBINE_SCRAP.get(), 1);
        } else {
            this.spawnAtLocation(ModItems.PLATE_COMBINE_STEEL.get(), 1);
        }
    }

    public void initDeath() {
        this.level().explode(this, this.getX(), this.getY(), this.getZ(), 10.0F, Level.ExplosionInteraction.MOB);
        if (!this.isDying) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.CHOPPER_DAMAGE.get(), SoundSource.HOSTILE, 10.0F, 1.0F);
        }
        this.isDying = true;
    }

    public void setIsDying(boolean b) {
        this.getEntityData().set(DYING_STATE, (byte) (b ? 1 : 0));
    }

    public boolean getIsDying() {
        return this.getEntityData().get(DYING_STATE) == 1;
    }

    @Override
    public boolean checkSpawnRules(@NotNull LevelAccessor level, @NotNull MobSpawnType type) {
        return this.random.nextInt(20) == 0 && super.checkSpawnRules(level, type) &&
                this.level().getDifficulty() != net.minecraft.world.Difficulty.PEACEFUL;
    }

    @Override
    public int getMaxSpawnClusterSize() {
        return 1;
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        // здесь можно загрузить состояние, если нужно
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        // сохранить состояние, если нужно
    }

    public boolean isAttackActive() {
        return this.getEntityData().get(ATTACK_STATE) != 0;
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public boolean canChangeDimensions() {
        return false;
    }

    @Override
    public boolean isPushedByFluid() {
        return false;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return FlyingMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 750.0D);
    }

    private void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }
}