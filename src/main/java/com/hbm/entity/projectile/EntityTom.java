package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityCloudTom;
import com.hbm.entity.logic.EntityTomBlast;
import com.hbm.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundSource;
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

public class EntityTom extends ThrowableProjectile {

    public EntityTom(EntityType<? extends EntityTom> type, Level level) {
        super(type, level);
    }

    public EntityTom(Level level) {
        this(ModEntities.TOM.get(), level);
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

        if (this.tickCount % 100 == 0) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.ALARM_CHIME.get(), SoundSource.HOSTILE, 10000.0F, 1.0F);
        }

        this.setDeltaMovement(0, -0.5, 0);

        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        if (this.level().getBlockState(pos).getBlock() != Blocks.AIR || this.getY() < 10) {
            if (!this.level().isClientSide) {
                EntityTomBlast tom = new EntityTomBlast(ModEntities.TOM_BLAST.get(), this.level());
                tom.setPos(this.getX(), this.getY(), this.getZ());
                tom.destructionRange = 600;
                this.level().addFreshEntity(tom);

                EntityCloudTom cloud = new EntityCloudTom(ModEntities.CLOUD_TOM.get(), this.level(), 500);
                cloud.setPos(this.getX(), this.getY(), this.getZ());
                this.level().addFreshEntity(cloud);
            }
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
        return distance < 500000;
    }

}