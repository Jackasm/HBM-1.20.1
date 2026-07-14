package com.hbm.entity.projectile;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.network.PacketDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class EntityTorpedo extends ThrowableProjectile {

    public EntityTorpedo(EntityType<? extends EntityTorpedo> type, Level level) {
        super(type, level);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    protected void defineSynchedData() {
        // Нет данных для синхронизации
    }

    @Override
    public void tick() {
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        if (!this.level().isClientSide && this.tickCount == 1) {
            for (int i = 0; i < 15; i++) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "bf");
                PacketDispatcher.sendAuxParticleNT(data,
                        this.getX() + (this.random.nextDouble() - 0.5) * 2,
                        this.getY() + (this.random.nextDouble() - 0.5) * 1,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 2,
                        this);
            }
        }

        this.setPos(
                this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z
        );

        this.setDeltaMovement(
                this.getDeltaMovement().x,
                this.getDeltaMovement().y - 0.04,
                this.getDeltaMovement().z
        );

        if (this.getDeltaMovement().y < -2.5) {
            this.setDeltaMovement(this.getDeltaMovement().x, -2.5, this.getDeltaMovement().z);
        }

        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        if (!this.level().isClientSide && this.level().getBlockState(pos).getBlock() != Blocks.AIR) {
            this.onImpact();
        }
    }

    private void onImpact() {
        if (!this.level().isClientSide) {
            ExplosionVNT vnt = new ExplosionVNT(this.level(), this.getX(), this.getY(), this.getZ(), 20F);
            vnt.setBlockAllocator(new BlockAllocatorStandard());
            vnt.setBlockProcessor(new BlockProcessorStandard());
            vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, 150));
            vnt.setPlayerProcessor(new PlayerProcessorStandard());
            vnt.explode();

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