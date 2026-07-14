package com.hbm.entity.projectile;

import com.hbm.config.BombConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityMist;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

public class EntityBombletZeta extends ThrowableProjectile {

    public int type = 0;

    public EntityBombletZeta(EntityType<? extends EntityBombletZeta> type, Level level) {
        super(type, level);
    }

    public EntityBombletZeta(EntityType<? extends EntityBombletZeta> type, Level level, double x, double y, double z) {
        super(type, x, y, z, level);
    }

    @Override
    protected void defineSynchedData() {
        // No data to sync
    }

    @Override
    public void tick() {
        // Store previous position for interpolation
        this.xo = this.getX();
        this.yo = this.getY();
        this.zo = this.getZ();

        // Update position
        this.setPos(
                this.getX() + this.getDeltaMovement().x,
                this.getY() + this.getDeltaMovement().y,
                this.getZ() + this.getDeltaMovement().z
        );

        // Apply drag and gravity
        this.setDeltaMovement(
                this.getDeltaMovement().x * 0.99,
                this.getDeltaMovement().y * 0.99 - 0.05,
                this.getDeltaMovement().z * 0.99
        );

        this.rotation();

        // Check for collision with blocks
        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        if (!this.level().isClientSide && this.level().getBlockState(pos).getBlock() != Blocks.AIR) {
            this.onImpact();
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

    private void onImpact() {
        if (!this.level().isClientSide) {
            switch (type) {
                case 0: {
                    ExplosionVNT vnt = new ExplosionVNT(level(),
                            this.getX() + 0.5, this.getY() + 1.5, this.getZ() + 0.5, 4F);
                    vnt.setBlockAllocator(new BlockAllocatorStandard());
                    vnt.setBlockProcessor(new BlockProcessorStandard());
                    vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, 100));
                    vnt.setPlayerProcessor(new PlayerProcessorStandard());
                    vnt.setSFX(new ExplosionEffectWeapon(15, 3.5F, 1.25F));
                    vnt.explode();
                    break;
                }
                case 1: {
                    ExplosionVNT vnt = new ExplosionVNT(level(),
                            this.getX() + 0.5, this.getY() + 1.5, this.getZ() + 0.5, 4F);
                    vnt.setBlockAllocator(new BlockAllocatorStandard());
                    vnt.setBlockProcessor(new BlockProcessorStandard()
                            .withBlockEffect(new BlockMutatorFire(Blocks.FIRE)));
                    vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, 100));
                    vnt.setPlayerProcessor(new PlayerProcessorStandard());
                    vnt.setSFX(new ExplosionEffectWeapon(15, 5F, 1.75F));
                    vnt.explode();
                    break;
                }
                case 2: {
                    level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            SoundEvents.LAVA_EXTINGUISH, SoundSource.NEUTRAL, 5.0F,
                            2.6F + (random.nextFloat() - random.nextFloat()) * 0.8F);

                    EntityMist mist = new EntityMist(ModEntities.MIST.get(), level());
                    mist.setFluidType(Fluids.CHLORINE.get());
                    mist.setPos(
                            this.getX() - this.getDeltaMovement().x,
                            this.getY() - this.getDeltaMovement().y,
                            this.getZ() - this.getDeltaMovement().z
                    );
                    mist.setArea(15, 7.5F);
                    level().addFreshEntity(mist);
                    break;
                }
                case 4: {
                    level().addFreshEntity(EntityNukeExplosionMK5.statFac(level(),
                            (int) (BombConfig.fatmanRadius.get() * 1.5),
                            this.getX(), this.getY(), this.getZ()));

                    CompoundTag data = new CompoundTag();
                    data.putString("type", "muke");
                    if (random.nextInt(100) == 0) {
                        data.putBoolean("balefire", true);
                    }
                    PacketDispatcher.sendAuxParticleNT(data, this.getX(), this.getY() + 0.5, this.getZ(), this);
                    level().playSound(null, this.getX(), this.getY(), this.getZ(),
                            ModSounds.MUKE_EXPLOSION.get(), SoundSource.NEUTRAL, 15.0F, 1.0F);
                    break;
                }
            }
            this.discard();
        }
    }

    @Override
    protected void onHit(@NotNull HitResult result) {
        // No immediate impact, handled in tick
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        // No immediate impact, handled in tick
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 25000;
    }
}