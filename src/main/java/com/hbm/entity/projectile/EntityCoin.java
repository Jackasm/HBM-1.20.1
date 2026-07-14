package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import org.jetbrains.annotations.NotNull;

public class EntityCoin extends ThrowableItemProjectile implements IEntityAdditionalSpawnData {

    // Размеры монеты
    private static final float WIDTH = 1.0F;
    private static final float HEIGHT = 0.1F; // Монета плоская

    public EntityCoin(EntityType<? extends EntityCoin> type, Level world) {
        super(type, world);
        this.refreshDimensions();
    }

    // Конструктор для создания через код
    public EntityCoin(Level world, double x, double y, double z) {
        this(ModEntities.COIN.get(), world); // Предполагается, что вы зарегистрировали EntityType
        this.setPos(x, y, z);
    }

    public EntityCoin(Level world) {
        this(ModEntities.COIN.get(), world);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        // Здесь можно добавить синхронизируемые данные, если нужно
    }

    @Override
    public void setPos(double x, double y, double z) {
        super.setPos(x, y, z);
        // Обновляем bounding box с учетом нового размера
        this.setBoundingBox(this.makeBoundingBox());
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        // Создаем AABB с учетом нашего размера
        float halfWidth = WIDTH / 2.0F;
        return new AABB(
                this.getX() - halfWidth,
                this.getY(),
                this.getZ() - halfWidth,
                this.getX() + halfWidth,
                this.getY() + HEIGHT,
                this.getZ() + halfWidth
        );
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        if (!this.level().isClientSide) {
            if (result.getType() == HitResult.Type.BLOCK) {
                // При столкновении с блоком удаляем монету
                this.discard();

                // Эффект при ударе
                for (int i = 0; i < 8; i++) {
                    this.level().addParticle(ParticleTypes.CRIT,
                            this.getX(), this.getY(), this.getZ(),
                            (this.random.nextDouble() - 0.5) * 0.2,
                            0.1 + this.random.nextDouble() * 0.2,
                            (this.random.nextDouble() - 0.5) * 0.2);
                }
            }
        }
    }

    @Override
    protected float getGravity() {
        // Гравитация для монеты (меньше, чем у обычных предметов)
        return 0.02F;
    }

    @Override
    protected @NotNull Item getDefaultItem() {
        // Предмет, который будет рендериться (можно заменить на свою монету)
        return Items.GOLD_NUGGET;
    }

    // Методы для совместимости с оригиналом
    public void setPosition(double x, double y, double z) {
        this.setPos(x, y, z);
    }

    // Аналог canBeCollidedWith
    @Override
    public boolean isPickable() {
        return true;
    }

    // Аналог canAttackWithItem
    @Override
    public boolean isAttackable() {
        return true;
    }

    @Override
    public void tick() {
        // Сохраняем предыдущую позицию для интерполяции
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();

        // Применяем сопротивление воздуха как в оригинале (airDrag = 1F)
        Vec3 motion = this.getDeltaMovement();

        // Если нужно сопротивление воздуха (оригинал возвращал 1F для getAirDrag)
        // motion = motion.scale(0.99D); // 1% сопротивления за тик

        // Применяем гравитацию
        motion = motion.add(0, -this.getGravity(), 0);

        this.setDeltaMovement(motion);

        // Вызываем супер метод для стандартной логики движения
        super.tick();

        // Поворот монеты в полете
        this.setYRot(this.getYRot() + 10F);

        // Визуальные эффекты при полете
        if (this.level().isClientSide && this.tickCount > 2) {
            for (int i = 0; i < 2; i++) {
                double motionX = motion.x * 0.2 + (this.random.nextDouble() - 0.5) * 0.02;
                double motionY = motion.y * 0.2 + (this.random.nextDouble() - 0.5) * 0.02;
                double motionZ = motion.z * 0.2 + (this.random.nextDouble() - 0.5) * 0.02;

                this.level().addParticle(ParticleTypes.CRIT,
                        this.getX() + (this.random.nextDouble() - 0.5) * 0.5,
                        this.getY() + (this.random.nextDouble() - 0.5) * 0.5,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 0.5,
                        motionX, motionY, motionZ);
            }
        }

    }

    // IEntityAdditionalSpawnData для сетевой синхронизации
    @Override
    public void writeSpawnData(FriendlyByteBuf buffer) {
        // Записываем дополнительные данные для синхронизации
    }

    @Override
    public void readSpawnData(FriendlyByteBuf additionalData) {
        // Читаем дополнительные данные
    }

}