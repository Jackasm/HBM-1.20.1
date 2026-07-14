package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityBuilding extends ThrowableProjectile {

    public EntityBuilding(EntityType<? extends EntityBuilding> type, Level level) {
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
            for (int i = 0; i < 100; i++) {
                CompoundTag data = new CompoundTag();
                data.putString("type", "bf");
                PacketDispatcher.sendAuxParticleNT(data,
                        this.getX() + (this.random.nextDouble() - 0.5) * 15,
                        this.getY() + (this.random.nextDouble() - 0.5) * 15,
                        this.getZ() + (this.random.nextDouble() - 0.5) * 15,
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
                this.getDeltaMovement().y - 0.03,
                this.getDeltaMovement().z
        );

        if (this.getDeltaMovement().y < -1.5) {
            this.setDeltaMovement(this.getDeltaMovement().x, -1.5, this.getDeltaMovement().z);
        }

        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        if (!this.level().isClientSide && this.level().getBlockState(pos).getBlock() != Blocks.AIR) {
            this.onImpact();
        }
    }

    private void onImpact() {
        if (!this.level().isClientSide) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.OLD_EXPLOSION.get(), SoundSource.HOSTILE, 10000.0F, 0.5F + this.random.nextFloat() * 0.1F);

            ExplosionLarge.spawnParticles(this.level(), this.getX(), this.getY() + 3, this.getZ(), 150);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 6);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 5);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 4);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 3);
            ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY() + 1, this.getZ(), 24, 3);

            AABB aabb = new AABB(
                    this.getX() - 8, this.getY() - 8, this.getZ() - 8,
                    this.getX() + 8, this.getY() + 8, this.getZ() + 8
            );
            List<Entity> entities = this.level().getEntities(null, aabb);

            DamageSource source = ModDamageSource.createDamageSource(ModDamageSource.BUILDING, null, null, this.level());
            for (Entity e : entities) {
                e.hurt(source, 1000);
            }

            for (int i = 0; i < 250; i++) {
                Vec3 vec = new Vec3(1, 0, 0);
                vec = vec.zRot((float) (-this.random.nextFloat() * Math.PI / 2));
                vec = vec.yRot((float) (this.random.nextFloat() * Math.PI * 2));

                EntityRubble rubble = new EntityRubble(ModEntities.RUBBLE.get(), this.level());
                rubble.setPos(this.getX(), this.getY() + 3, this.getZ());
                rubble.setMetaBasedOnBlock(Blocks.BRICKS, 0);
                rubble.setDeltaMovement(vec.x, vec.y, vec.z);
                this.level().addFreshEntity(rubble);
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
        return distance < 25000;
    }
}