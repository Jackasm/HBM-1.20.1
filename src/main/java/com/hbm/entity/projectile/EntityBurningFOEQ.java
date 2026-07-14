package com.hbm.entity.projectile;

import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.ExplosionNukeGeneric;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
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

public class EntityBurningFOEQ extends ThrowableProjectile {

    public EntityBurningFOEQ(EntityType<? extends EntityBurningFOEQ> type, Level level) {
        super(type, level);
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

        this.setPos(
                this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z
        );

        if (this.getDeltaMovement().y > -4) {
            this.setDeltaMovement(
                    this.getDeltaMovement().x,
                    this.getDeltaMovement().y - 0.1,
                    this.getDeltaMovement().z
            );
        }

        this.rotation();

        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        if (!this.level().isClientSide && this.level().getBlockState(pos).getBlock() != Blocks.AIR) {
            for (int i = 0; i < 25; i++) {
                ExplosionLarge.explode(this.level(),
                        this.getX() + 0.5F + this.random.nextGaussian() * 5,
                        this.getY() + 0.5F + this.random.nextGaussian() * 5,
                        this.getZ() + 0.5F + this.random.nextGaussian() * 5,
                        10.0F, this.random.nextBoolean(), false, false);
            }
            ExplosionNukeGeneric.waste(this.level(), new BlockPos((int) this.getX(), (int) this.getY(), (int) this.getZ()), 35);
            this.discard();
        }
    }

    public void rotation() {
        double motionX = this.getDeltaMovement().x;
        double motionY = this.getDeltaMovement().y;
        double motionZ = this.getDeltaMovement().z;

        float f2 = Mth.sqrt((float) (motionX * motionX + motionZ * motionZ));
        this.setYRot((float) (Mth.atan2(motionX, motionZ) * 180.0D / Math.PI));
        this.setXRot((float) (Mth.atan2(motionY, f2) * 180.0D / Math.PI) - 90);

        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
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
        return distance < 100000;
    }

}