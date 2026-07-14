package com.hbm.entity.mob;

import com.hbm.entity.ModEntities;
import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.BossEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class EntityQuackos extends EntityDuck {

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    public EntityQuackos(EntityType<? extends EntityQuackos> type, Level world) {
        super(type, world);
        this.setInvulnerable(true);
    }

    public EntityQuackos(Level world) {
        this(ModEntities.QUACKOS.get(), world);
    }

    @Override
    public EntityQuackos getBreedOffspring(@NotNull ServerLevel level, @NotNull AgeableMob otherParent) {
        return new EntityQuackos(ModEntities.QUACKOS.get(), level);
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public void setHealth(float health) {
        // Всегда восстанавливаем здоровье до максимума - не убиваем
        super.setHealth(this.getMaxHealth());
    }

    @Override
    public void kill() {
        // Не даём убить
        if (this.level().isClientSide) {
            super.kill();
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        // Полная неуязвимость
        return false;
    }

    @Override
    public @NotNull InteractionResult mobInteract(@NotNull Player player, @NotNull InteractionHand hand) {
        if (super.mobInteract(player, hand).consumesAction()) {
            return InteractionResult.SUCCESS;
        }

        if (!this.level().isClientSide && (this.getPassengers().isEmpty() || this.getPassengers().get(0) == player)) {
            player.startRiding(this);
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void die(@NotNull DamageSource source) {
        // Ничего не делаем - бессмертны
    }

    public void despawn() {
        if (!this.level().isClientSide) {
            // Создаём эффект частиц
            for (int i = 0; i < 150; i++) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "bf");
                double x = this.getX() + this.random.nextDouble() * 20 - 10;
                double y = this.getY() + this.random.nextDouble() * 25;
                double z = this.getZ() + this.random.nextDouble() * 20 - 10;

                PacketDispatcher.sendAuxParticleNT(data, x, y, z, null);
            }

            // Выпадает 3 яйца утки
            this.spawnAtLocation(new ItemStack(ModItems.DUCK_SPAWN_EGG.get()), 3);
        }
        this.discard();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        // Если утка улетела слишком далеко вниз - телепортируем обратно
        if (!this.level().isClientSide && this.getY() < -30) {
            this.teleportTo(
                    this.getX() + this.random.nextGaussian() * 30,
                    256,
                    this.getZ() + this.random.nextGaussian() * 30
            );
        }
    }

    @Override
    protected void positionRider(@NotNull Entity passenger, @NotNull MoveFunction moveFunction) {
        super.positionRider(passenger, moveFunction);

        if (passenger instanceof LivingEntity living) {
            float f = (float) Math.sin(this.yBodyRot * Math.PI / 180.0F);
            float f1 = (float) Math.cos(this.yBodyRot * Math.PI / 180.0F);
            float f2 = 0.1F;
            float f3 = 0.0F;

            living.setPos(
                    this.getX() + (f2 * f),
                    this.getY() + (this.getBbHeight() - 0.125F) + living.getMyRidingOffset() + f3,
                    this.getZ() - (f2 * f1)
            );
            living.setYRot(this.yBodyRot);
            living.setXRot(this.getXRot());
            living.yRotO = this.yBodyRot;
        }
    }

    @Override
    public boolean isLeashed() {
        return false;
    }

    @Override
    public boolean canBeLeashed(@NotNull Player player) {
        return false;
    }

    @Override
    public void tick() {
        super.tick();

        // Обновляем босс-бар
        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }
}