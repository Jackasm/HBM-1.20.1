package com.hbm.tileentity.turret;

import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;

import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Повреждённая версия турели-сентея.
 * Не требует питания, всегда включена, стреляет только левым стволом базовыми патронами.
 * Не имеет инвентаря и не расходует боеприпасы.
 */
public class TileEntityTurretSentryDamaged extends TileEntityTurretSentry {

    // ========== Конструктор ==========
    public TileEntityTurretSentryDamaged(BlockPos pos, BlockState state) {
        super(ModTileEntity.TURRET_SENTRY_DAMAGED.get(), pos, state);
    }

    // ========== Переопределение параметров ==========
    @Override
    public boolean hasPower() {
        // Не требует энергии
        return true;
    }

    @Override
    public boolean isOn() {
        // Всегда активна
        return true;
    }

    @Override
    public long getMaxPower() {
        // Не используется, но нужно для интерфейса
        return 0;
    }

    @Override
    public long getConsumption() {
        // Не потребляет энергию
        return 0;
    }

    // ========== Поиск цели ==========
    @Override
    protected void seekNewTarget() {
        Entity lastTarget = this.target;
        super.seekNewTarget();
        if (lastTarget != this.target && this.target != null && level != null && !level.isClientSide) {
            level.playSound(null, worldPosition, ModSounds.TURRET_SENTRY_LOCKON.get(), SoundSource.BLOCKS, 2.0F, 1.5F);
        }
    }

    // ========== Проверка цели ==========
    @Override
    protected boolean entityAcceptableTarget(Entity e) {
        // Игроки в креативе — нет
        if (e instanceof Player && ((Player) e).isCreative()) return false;

        // Только живые существа
        return e instanceof LivingEntity;
    }

    // ========== Стрельба ==========
    @Override
    public void updateFiringTick() {
        timer++;
        if (timer % 10 == 0) {
            // Всегда используем FMJ патроны
            BulletConfig conf = BulletConfigRegistry.p9_fmj;
            if (conf != null) {
                // Не расходуем патроны (повреждённая турель имеет бесконечный боезапас)
                // Звук
                Objects.requireNonNull(level).playSound(null, worldPosition, ModSounds.TURRET_SENTRY_FIRE.get(), SoundSource.BLOCKS, 2.0F, 1.0F);
                // Спавн пули
                spawnBullet(conf, 5F);

                // Анимация левого ствола
                didJustShootLeft = true;
                // Не переключаем стороны — всегда левый ствол
            }
        }
    }

    // ========== Боеприпасы ==========
    @Override
    public BulletConfig getFirstConfigLoaded() {
        // Всегда возвращаем базовые патроны
        return BulletConfigRegistry.p9_fmj;
    }

    @Override
    public void conusmeAmmo(BulletConfig config) {
        // Не расходуем патроны
    }

    // ========== Инвентарь ==========


    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack) {
        // Ничего нельзя положить
        return false;
    }

    // ========== NBT ==========
    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        // Ничего дополнительного не загружаем
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        // Ничего дополнительного не сохраняем
    }

    // ========== Синхронизация ==========
    @Override
    public void serialize(ByteBuf buf) {
        // Вызываем супер, который отправит didJustShootLeft/Right
        super.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
    }

    // ========== Вспомогательные методы ==========
    private double getDistanceToEntity(Entity e) {
        Vec3 pos = getTurretPos();
        Vec3 ent = getEntityPos(e);
        return pos.distanceTo(ent);
    }
}