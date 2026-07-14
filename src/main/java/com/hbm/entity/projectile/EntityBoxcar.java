package com.hbm.entity.projectile;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.network.PacketDispatcher;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityBoxcar extends ThrowableProjectile {

    public EntityBoxcar(EntityType<? extends EntityBoxcar> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        // Нет данных для синхронизации
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public void tick() {
        // Сохраняем предыдущие позиции для интерполяции
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        if (!this.level().isClientSide && this.tickCount == 1) {
            for (int i = 0; i < 50; i++) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "bf");
                PacketDispatcher.sendAuxParticleNT(data,
                        this.getX() + (this.random.nextDouble() - 0.5) * 3,
                        this.getY() + (this.random.nextDouble() - 0.5) * 15,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 3,
                        this);
            }
        }

        // Обновляем позицию
        this.setPos(
                this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z
        );

        // Применяем гравитацию
        this.setDeltaMovement(
                this.getDeltaMovement().x,
                this.getDeltaMovement().y - 0.03,
                this.getDeltaMovement().z
        );

        // Ограничиваем скорость падения
        if (this.getDeltaMovement().y < -1.5) {
            this.setDeltaMovement(this.getDeltaMovement().x, -1.5, this.getDeltaMovement().z);
        }

        // Проверяем столкновение с блоками
        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        if (!this.level().isClientSide && this.level().getBlockState(pos).getBlock() != Blocks.AIR) {
            this.onImpact();
        }
    }

    private void onImpact() {
        if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.ANVIL_LAND, SoundSource.HOSTILE, 100.0F, 1.0F);

            // Создаём ударные волны
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 3);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 2.5);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 2);

            // Наносим урон всем сущностям в радиусе
            AABB aabb = new AABB(
                    this.getX() - 2, this.getY() - 2, this.getZ() - 2,
                    this.getX() + 2, this.getY() + 2, this.getZ() + 2
            );
            List<Entity> entities = this.level().getEntities(null, aabb);

            for (Entity e : entities) {
                e.hurt(ModDamageSource.causeBoxcarDamage(this.level()), 1000);
            }

            // Ставим блок Boxcar
            this.level().setBlock(
                    BlockPos.containing(Math.floor(this.getX()), Math.floor(this.getY() + 0.5), Math.floor(this.getZ())),
                    ModBlocks.BOXCAR.get().defaultBlockState(),
                    3
            );

            this.discard();
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        // Обработка попадания - ничего не делаем, вся логика в tick
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        // Обработка попадания в блок - ничего не делаем, вся логика в tick
    }

    @Override
    @NotNull
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 25000;
    }
}