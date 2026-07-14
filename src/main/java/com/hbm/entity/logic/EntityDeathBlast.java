package com.hbm.entity.logic;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import static com.hbm.items.weapon.sedna.factory.BulletConfigRegistry.MASKMAN_BOLT;

public class EntityDeathBlast extends Entity {

    public static final int MAX_AGE = 60;

    public EntityDeathBlast(EntityType<? extends EntityDeathBlast> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        // Нет данных для синхронизации
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        // Нет данных для загрузки
    }

    @Override
    protected void addAdditionalSaveData(@NotNull CompoundTag tag) {
        // Нет данных для сохранения
    }

    @Override
    public void tick() {
        if (this.tickCount >= MAX_AGE && !this.level().isClientSide) {
            this.discard();

            // Ядерный взрыв без радиации
            EntityNukeExplosionMK5.statFacNoRad(this.level(), 40, this.getX(), this.getY(), this.getZ());

            // Выпускаем лазерные лучи во все стороны
            int count = 100;
            for (int i = 0; i < count; i++) {
                Vec3 vec = new Vec3(0.2, 0, 0);
                vec = vec.yRot((float) (2 * Math.PI * i / (float) count));

                EntityBulletBaseMK4 laser = new EntityBulletBaseMK4(
                        this.level(),
                        null, // shooter
                        MASKMAN_BOLT,
                        15, // baseDamage
                        0F, // gunSpread
                        this.getX(), // posX
                        this.getY() + 2, // posY
                        this.getZ(), // posZ
                        vec.x, // motionX
                        -0.01, // motionY
                        vec.z // motionZ
                );
                laser.setPos(this.getX(), this.getY() + 2, this.getZ());
                laser.setDeltaMovement(vec.x, -0.01, vec.z);
                this.level().addFreshEntity(laser);
            }

            // Партиклы и звук
            CompoundTag data = new CompoundTag();
            data.putString("type", "muke");
            PacketDispatcher.sendAuxParticleNT(data, this.getX(), this.getY() + 0.5, this.getZ(), this);

            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.MUKE_EXPLOSION.get(), SoundSource.HOSTILE, 25.0F, 0.9F);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 25000;
    }

    @Override
    @NotNull
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}