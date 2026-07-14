package com.hbm.entity.projectile;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public abstract class EntityBeamBase extends Entity {

    private static final EntityDataAccessor<String> PLAYER_NAME =
            SynchedEntityData.defineId(EntityBeamBase.class, EntityDataSerializers.STRING);

    public EntityBeamBase(EntityType<? extends EntityBeamBase> type, Level level) {
        super(type, level);
        this.setNoGravity(true);
        this.noCulling = true; // Аналог ignoreFrustumCheck в старых версиях
    }

    public EntityBeamBase(EntityType<? extends EntityBeamBase> type, Level level, Player player) {
        this(type, level);

        // Сохраняем имя игрока в синхронизированных данных
        this.entityData.set(PLAYER_NAME, player.getDisplayName().getString());

        // Позиционирование луча относительно игрока
        Vec3 lookVec = player.getLookAngle();
        Vec3 rightVec = lookVec.yRot((float) -Math.toRadians(90)); // Поворот на -90 градусов вокруг Y

        float lateralOffset = 0.075F;
        float forwardOffset = 0.1F;

        rightVec = rightVec.scale(lateralOffset);
        Vec3 forwardVec = lookVec.scale(forwardOffset);

        // Позиция луча
        double posX = player.getX() + rightVec.x + forwardVec.x;
        double posY = player.getY() + player.getEyeHeight() + forwardVec.y;
        double posZ = player.getZ() + rightVec.z + forwardVec.z;

        this.setPos(posX, posY, posZ);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(PLAYER_NAME, "");
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag compound) {
        // Пустая реализация, если не нужно сохранять дополнительные данные
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag compound) {
        // Пустая реализация, если не нужно сохранять дополнительные данные
    }

    /**
     * Получить имя игрока, создавшего луч
     */
    public String getPlayerName() {
        return this.entityData.get(PLAYER_NAME);
    }

    /**
     * Установить имя игрока, создавшего луч
     */
    public void setPlayerName(String name) {
        this.entityData.set(PLAYER_NAME, name);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public float getLightLevelDependentMagicValue() {
        return 1.0F; // Полная яркость
    }



    /**
     * Получить игрока, создавшего луч
     * Внимание: работает только если игрок онлайн и в том же мире
     */
    @OnlyIn(Dist.CLIENT)
    public Player getPlayer() {
        if (this.level().isClientSide) {
            String playerName = getPlayerName();
            if (!playerName.isEmpty()) {
                // На клиенте можем попробовать найти игрока по имени
                return this.level().players().stream()
                        .filter(p -> p.getDisplayName().getString().equals(playerName))
                        .findFirst()
                        .orElse(null);
            }
        }
        return null;
    }

    /**
     * Получить направление взгляда игрока, создавшего луч
     */
    @OnlyIn(Dist.CLIENT)
    public Vec3 getPlayerLookAngle() {
        Player player = getPlayer();
        if (player != null) {
            return player.getLookAngle();
        }
        return Vec3.ZERO;
    }

    /**
     * Проверка, является ли переданный игрок создателем луча
     */
    public boolean isOwner(Player player) {
        String ownerName = getPlayerName();
        return !ownerName.isEmpty() && player.getDisplayName().getString().equals(ownerName);
    }

    /**
     * Обновление позиции луча относительно игрока
     */
    public void updatePositionFromPlayer(Player player) {
        Vec3 lookVec = player.getLookAngle();
        Vec3 rightVec = lookVec.yRot((float) -Math.toRadians(90));

        float lateralOffset = 0.075F;
        float forwardOffset = 0.1F;

        rightVec = rightVec.scale(lateralOffset);
        Vec3 forwardVec = lookVec.scale(forwardOffset);

        double posX = player.getX() + rightVec.x + forwardVec.x;
        double posY = player.getY() + player.getEyeHeight() + forwardVec.y;
        double posZ = player.getZ() + rightVec.z + forwardVec.z;

        this.setPos(posX, posY, posZ);
    }

    /**
     * Проверка, может ли луч быть видимым за пределами стандартной области видимости
     * Аналог ignoreFrustumCheck в старых версиях
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        // Луч всегда рендерится, независимо от расстояния
        return true;
    }

    /**
     * Получить размер ограничивающей рамки для рендеринга
     */
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        // Всегда рендерим луч
        return true;
    }
}