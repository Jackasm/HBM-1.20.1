package com.hbm.entity.missile;

import com.hbm.blocks.ModBlocks;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.particle.helper.GasFlameCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class EntityMinerRocket extends Entity {

    public int timer = 0;

    public EntityMinerRocket(EntityType<?> type, Level level) {
        super(type, level);
        this.setBoundingBox(new AABB(0, 0, 0, 1, 3, 1));
    }

    @Override
    protected void defineSynchedData() {
        this.getEntityData().define(MODE, 0);
        this.getEntityData().define(FREQ, 0);
    }

    @Override
    public void tick() {
        int mode = this.getEntityData().get(MODE);
        double motionY = 0;

        if (mode == 0) {
            motionY = -0.75;
        } else if (mode == 1) {
            motionY = 0;
        } else if (mode == 2) {
            motionY = 1;
        }

        this.setPos(this.getX(), this.getY() + motionY, this.getZ());

        if (mode == 0 && this.level().getBlockState(BlockPos.containing(this.getX() - 0.5, this.getY() - 0.5, this.getZ() - 0.5)).getBlock() == ModBlocks.SAT_DOCK.get()) {
            this.getEntityData().set(MODE, 1);
            this.setPos(this.getX(), (int) this.getY(), this.getZ());
        } else if (this.level().getBlockState(BlockPos.containing(this.getX() - 0.5, this.getY() + 1, this.getZ() - 0.5)).getBlock() != Blocks.AIR
                && !this.level().isClientSide && mode != 1) {
            ExplosionLarge.explodeFire(this.level(), this.getX() - 0.5, this.getY(), this.getZ() - 0.5, 10F, true, false, true);
            this.discard();
        }

        if (mode == 1) {
            if (!this.level().isClientSide && this.tickCount % 4 == 0) {
                ExplosionLarge.spawnShock(this.level(), this.getX(), this.getY(), this.getZ(), 1 + this.random.nextInt(3), 1 + this.random.nextGaussian());
            }

            timer++;

            if (timer > 100) {
                this.getEntityData().set(MODE, 2);
            }
        }

        if (mode != 1 && !this.level().isClientSide && this.tickCount % 2 == 0) {
            GasFlameCreator.spawnEffect(this.level(), this.getX(), this.getY() - 0.5, this.getZ(), 0.0, -1.0, 0.0, 6.5F);
        }

        if (mode == 2 && this.getY() > 300) {
            this.discard();
        }
    }

    public void setFreq(int freq) {
        this.getEntityData().set(FREQ, freq);
    }

    public int getFreq() {
        return this.getEntityData().get(FREQ);
    }

    public boolean isReturning() {
        return this.getEntityData().get(MODE) == 1;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.getEntityData().set(MODE, tag.getInt("mode"));
        this.getEntityData().set(FREQ, tag.getInt("sat"));
        timer = tag.getInt("timer");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("mode", this.getEntityData().get(MODE));
        tag.putInt("sat", this.getEntityData().get(FREQ));
        tag.putInt("timer", timer);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    // Synched data keys
    public static final EntityDataAccessor<Integer> MODE =
            SynchedEntityData.defineId(EntityMinerRocket.class,
                    EntityDataSerializers.INT);
    public static final EntityDataAccessor<Integer> FREQ =
            SynchedEntityData.defineId(EntityMinerRocket.class,
                    EntityDataSerializers.INT);
}