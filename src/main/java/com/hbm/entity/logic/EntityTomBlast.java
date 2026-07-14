package com.hbm.entity.logic;

import com.hbm.explosion.ExplosionNukeGeneric;
import com.hbm.explosion.ExplosionTom;
import com.hbm.saveddata.TomSaveData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkHooks;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class EntityTomBlast extends EntityExplosionChunkloading {

    public int age = 0;
    public int destructionRange = 0;
    public ExplosionTom exp;
    public int speed = 1;
    public boolean did = false;

    public EntityTomBlast(EntityType<? extends EntityTomBlast> type, Level level) {
        super(type, level);
    }

    @Override
    protected void defineSynchedData() {
        // Нет данных для синхронизации
    }

    @Override
    public void tick() {
        super.tick();

        if (!level().isClientSide) {
            loadChunk(new BlockPos((int) Math.floor(getX() / 16D), 0, (int) Math.floor(getZ() / 16D)));
        }

        if (!this.did) {
            exp = new ExplosionTom((int) this.getX(), (int) this.getY(), (int) this.getZ(), this.level(), this.destructionRange);
            this.did = true;
        }

        speed += 1;

        boolean flag = false;
        for (int i = 0; i < this.speed; i++) {
            flag = exp.update();

            if (flag) {
                this.discard();
                TomSaveData data = TomSaveData.forWorld(level());
                Objects.requireNonNull(data).impact = true;
                data.fire = 1F;
                data.setDirty();
            }
        }

        if (random.nextInt(5) == 0) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
        }

        if (!flag) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.LIGHTNING_BOLT_THUNDER, SoundSource.HOSTILE, 10000.0F, 0.8F + this.random.nextFloat() * 0.2F);
            ExplosionNukeGeneric.dealDamage(this.level(), this.getX(), this.getY(), this.getZ(), this.destructionRange * 2);
        }

        age++;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        age = tag.getInt("age");
        destructionRange = tag.getInt("destructionRange");
        speed = tag.getInt("speed");
        did = tag.getBoolean("did");

        if (exp != null) {
            exp.readFromNbt(tag, "exp_");
        }

        this.did = true;
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("age", age);
        tag.putInt("destructionRange", destructionRange);
        tag.putInt("speed", speed);
        tag.putBoolean("did", did);

        if (exp != null) {
            exp.saveToNbt(tag, "exp_");
        }
    }

    @Override
    @NotNull
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }
}