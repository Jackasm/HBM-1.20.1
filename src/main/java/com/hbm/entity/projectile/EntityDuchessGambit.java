package com.hbm.entity.projectile;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityDuchessGambit extends ThrowableProjectile {

    private static final EntityDataAccessor<Boolean> DATA_EXPLODED = SynchedEntityData.defineId(EntityDuchessGambit.class, EntityDataSerializers.BOOLEAN);

    public EntityDuchessGambit(Level level) {
        this(EntityType.SNOWBALL, level);
    }


    public EntityDuchessGambit(EntityType<? extends ThrowableProjectile> type, Level level) {
        super(type, level);
        this.setNoGravity(false);
        this.setInvulnerable(true);
    }

    // Альтернативный конструктор для удобства
    public EntityDuchessGambit(Level level, LivingEntity shooter) {
        super(EntityType.SNOWBALL, shooter, level); // Используем снежок как базовый тип
        this.setNoGravity(false);
        this.setInvulnerable(true);
    }

    // Простой конструктор для создания сущности
    public EntityDuchessGambit(Level level, double x, double y, double z) {
        super(EntityType.SNOWBALL, x, y, z, level);
        this.setNoGravity(false);
        this.setInvulnerable(true);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(DATA_EXPLODED, false);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide() && this.tickCount == 1) {
            for (int i = 0; i < 50; i++) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "bf");

                double x = this.getX() + (random.nextDouble() - 0.5) * 5;
                double y = this.getY() + (random.nextDouble() - 0.5) * 7;
                double z = this.getZ() + (random.nextDouble() - 0.5) * 20;

                PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(data, x, y, z),
                        this.level(), BlockPos.containing(x, y, z), 150);
            }
        }

        // Сохраняем предыдущую позицию для интерполяции
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        // Обновляем позицию
        this.setPos(this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z);

        // Гравитация
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, motion.y - 0.03, motion.z);

        if (this.getDeltaMovement().y < -1.5) {
            this.setDeltaMovement(motion.x, -1.5, motion.z);
        }

        // Проверка столкновения с землей
        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        if (!this.level().isEmptyBlock(pos)) {
            // Звук приземления
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    com.hbm.sound.ModSounds.ALARM_GAMBIT.get(), this.getSoundSource(), 10000.0F, 1.0F);

            this.discard();

            // Поиск сущностей в зоне поражения
            AABB aabb = new AABB(this.getX() - 5, this.getY() - 2, this.getZ() - 9,
                    this.getX() + 5, this.getY() + 2, this.getZ() + 9);
            List<Entity> list = this.level().getEntities(this, aabb);

            // Нанесение урона сущностям
            for (Entity e : list) {
                Entity owner = this.getOwner();
                if (owner instanceof LivingEntity livingOwner) {
                    e.hurt(ModDamageSource.createDamageSource(ModDamageSource.BOAT, this, livingOwner, this.level()), 1000F);
                } else {
                    e.hurt(ModDamageSource.createDamageSource(ModDamageSource.BOAT, this, null, this.level()), 1000F);
                }
            }

            if (!this.level().isClientSide()) {
                // Создание взрывов
                ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ() - 6, 2, true, false, false);
                ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ() - 3, 2, true, false, false);
                ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ(), 2, true, false, false);
                ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ() + 3, 2, true, false, false);
                ExplosionLarge.explode(this.level(), this.getX(), this.getY(), this.getZ() + 6, 2, true, false, false);

                // Установка блока лодки
                BlockPos boatPos = new BlockPos((int) (this.getX() - 0.5), (int) (this.getY() + 0.5), (int) (this.getZ() - 0.5));
                this.level().setBlock(boatPos, ModBlocks.BOAT.get().defaultBlockState(), 3);
            }

            // Создание ударных волн
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 3);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 2.5);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 2);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 1.5);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 1);
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        // Обработка столкновения - уже сделана в tick()
    }

    @Override
    protected float getGravity() {
        return 0.03F;
    }

    @Override
    public boolean isPickable() {
        return false;
    }

    @Override
    public boolean displayFireAnimation() {
        return false;
    }

    // Для кастомной дистанции рендеринга
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 25000 * 25000;
    }

    // Для сетевой синхронизации
    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // Добавьте эти методы для регистрации сущности

}