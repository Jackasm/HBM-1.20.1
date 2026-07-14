package com.hbm.entity.effect;

import com.hbm.entity.ModEntities;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.particle.helper.FlameCreator;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityFireLingering extends Entity {

    public static final int TYPE_DIESEL = 0;
    public static final int TYPE_BALEFIRE = 1;
    public static final int TYPE_PHOSPHORUS = 2;
    public static final int TYPE_OXY = 3;
    public static final int TYPE_BLACK = 4;

    private static final EntityDataAccessor<Integer> FIRE_TYPE =
            SynchedEntityData.defineId(EntityFireLingering.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> WIDTH =
            SynchedEntityData.defineId(EntityFireLingering.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT =
            SynchedEntityData.defineId(EntityFireLingering.class, EntityDataSerializers.FLOAT);

    public int maxAge = 150;

    public EntityFireLingering(EntityType<? extends EntityFireLingering> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noCulling = true;
    }

    public EntityFireLingering setArea(float width, float height) {
        this.entityData.set(WIDTH, width);
        this.entityData.set(HEIGHT, height);
        this.refreshDimensions();
        return this;
    }

    public EntityFireLingering setDuration(int duration) {
        this.maxAge = duration;
        return this;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(FIRE_TYPE, TYPE_DIESEL);
        this.entityData.define(WIDTH, 0.0F);
        this.entityData.define(HEIGHT, 0.0F);
    }

    public EntityFireLingering setType(int type) {
        this.entityData.set(FIRE_TYPE, type);
        return this;
    }

    public int getFireType() { // Переименован, чтобы избежать конфликта с getType() из Entity
        return this.entityData.get(FIRE_TYPE);
    }

    public float getFireWidth() {
        return this.entityData.get(WIDTH);
    }

    public float getFireHeight() {
        return this.entityData.get(HEIGHT);
    }

    @Override
    public void tick() {
        super.tick();

        // Обновляем размеры
        this.refreshDimensions();

        if (!this.level().isClientSide()) {
            // Проверяем истечение времени жизни
            if (this.tickCount >= maxAge) {
                this.discard();
                return;
            }

            // Поиск сущностей в области
            float width = getFireWidth();
            float height = getFireHeight();
            AABB aabb = new AABB(
                    this.getX() - width / 2, this.getY(),
                    this.getZ() - width / 2,
                    this.getX() + width / 2, this.getY() + height,
                    this.getZ() + width / 2
            );

            List<Entity> affected = this.level().getEntities(this, aabb);

            for (Entity e : affected) {
                if (e instanceof LivingEntity living) {
                    HbmLivingProps props = HbmLivingProps.getData(living);
                    if (props != null) {
                        int fireType = getFireType();
                        switch (fireType) {
                            case TYPE_DIESEL:
                                if (props.fire < 60) props.fire = 60;
                                break;
                            case TYPE_PHOSPHORUS:
                                if (props.fire < 300) props.fire = 300;
                                break;
                            case TYPE_BALEFIRE:
                                if (props.balefire < 100) props.balefire = 100;
                                break;
                            case TYPE_BLACK:
                                if (props.blackFire < 200) props.blackFire = 200;
                                else props.blackFire += 5;
                                break;
                        }
                    }
                } else {
                    // Для неживых сущностей устанавливаем огонь
                    e.setSecondsOnFire(4);
                }
            }
        } else {
            // Клиентская часть: частицы
            spawnClientParticles();
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnClientParticles() {
        float width = getFireWidth();
        float height = getFireHeight();
        int particleCount = width >= 5 ? 2 : 1;
        int fireType = getFireType();

        for (int i = 0; i < particleCount; i++) {
            double x = this.getX() - width / 2 + this.random.nextDouble() * width;
            double z = this.getZ() - width / 2 + this.random.nextDouble() * width;

            // Ищем поверхность для частиц
            Vec3 up = new Vec3(x, this.getY() + height, z);
            Vec3 down = new Vec3(x, this.getY() - height, z);

            // Используем BlockHitResult для поиска поверхности
            net.minecraft.world.phys.BlockHitResult hitResult = this.level().clip(
                    new net.minecraft.world.level.ClipContext(
                            up, down,
                            net.minecraft.world.level.ClipContext.Block.OUTLINE,
                            net.minecraft.world.level.ClipContext.Fluid.NONE,
                            this
                    )
            );

            double particleY = hitResult.getType() != net.minecraft.world.phys.HitResult.Type.MISS ?
                    hitResult.getLocation().y : down.y;

            // Спавним частицы в зависимости от типа
            spawnParticlesByType(fireType, x, particleY, z);
        }
    }

    @OnlyIn(Dist.CLIENT)
    private void spawnParticlesByType(int type, double x, double y, double z) {
        Level level = this.level();

        // Конвертируем координаты для совместимости с FlameCreator

        switch (type) {
            case TYPE_DIESEL:
                // Используем оригинальный вызов для обычного огня
                // FlameCreator.composeEffectClient(worldObj, x, down.yCoord, z, FlameCreator.META_FIRE);
                FlameCreator.composeEffectClient(level, x, y, z, FlameCreator.META_FIRE);
                break;

            case TYPE_PHOSPHORUS:
                // То же самое для фосфорного огня
                FlameCreator.composeEffectClient(level, x, y, z, FlameCreator.META_FIRE);
                break;

            case TYPE_BALEFIRE:
                // Для баллистического огня
                FlameCreator.composeEffectClient(level, x, y, z, FlameCreator.META_BALEFIRE);
                break;

            case TYPE_BLACK:
                // Для черного огня
                FlameCreator.composeEffectClient(level, x, y, z, FlameCreator.META_BLACK);
                break;
        }
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        // Сущность уничтожается при загрузке (как в оригинале)
        this.discard();
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        // Не сохраняем
    }

    @Override
    public boolean save(@NotNull CompoundTag compound) {
        return false; // Не сохраняем в NBT
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void refreshDimensions() {
        super.refreshDimensions();
        // Обновляем ограничивающую рамку на основе ширины и высоты
        float width = getFireWidth();
        float height = getFireHeight();
        if (width > 0 && height > 0) {
            this.setBoundingBox(new AABB(
                    this.getX() - width / 2, this.getY(),
                    this.getZ() - width / 2,
                    this.getX() + width / 2, this.getY() + height,
                    this.getZ() + width / 2
            ));
        }
    }

    // Методы для удобного создания сущности
    public static EntityFireLingering create(Level level, Vec3 position, float width, float height, int duration, int type) {
        EntityFireLingering fire = new EntityFireLingering(ModEntities.FIRE_LINGERING.get(), level);
        fire.setPos(position.x, position.y, position.z);
        fire.setArea(width, height)
                .setDuration(duration)
                .setType(type);
        return fire;
    }
}