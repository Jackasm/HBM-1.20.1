package com.hbm.entity.particle;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class EntityModFX extends Entity {

    public int particleTextureIndexX;
    public int particleTextureIndexY;
    public float particleTextureJitterX;
    public float particleTextureJitterY;
    public int particleMaxAge;
    public float particleScale;
    public float particleGravity;
    public float particleRed = 1.0F;
    public float particleGreen = 1.0F;
    public float particleBlue = 1.0F;
    public float particleAlpha = 1.0F;
    public int particleAge;
    public int maxAge;

    public EntityModFX(EntityType<?> type, Level level) {
        super(type, level);
        this.setBoundingBox(this.getBoundingBox().inflate(0.2F));
        this.particleTextureJitterX = this.random.nextFloat() * 3.0F;
        this.particleTextureJitterY = this.random.nextFloat() * 3.0F;
        this.particleScale = (this.random.nextFloat() * 0.5F + 0.5F) * 2.0F;
    }

    public EntityModFX(EntityType<?> type, Level level, double x, double y, double z, double mx, double my, double mz) {
        this(type, level);
        this.setPos(x, y, z);
        this.xo = x;
        this.yo = y;
        this.zo = z;

        this.setDeltaMovement(
                mx + (random.nextDouble() * 2.0 - 1.0) * 0.4,
                my + (random.nextDouble() * 2.0 - 1.0) * 0.4,
                mz + (random.nextDouble() * 2.0 - 1.0) * 0.4
        );

        float f = (float) (Math.random() + Math.random() + 1.0) * 0.15F;
        Vec3 motion = this.getDeltaMovement();
        float f1 = (float) Math.sqrt(motion.x * motion.x + motion.y * motion.y + motion.z * motion.z);

        if (f1 > 0) {
            this.setDeltaMovement(
                    motion.x / f1 * f * 0.4,
                    motion.y / f1 * f * 0.4 + 0.1,
                    motion.z / f1 * f * 0.4
            );
        }
    }

    public EntityModFX multiplyVelocity(float multiplier) {
        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(
                motion.x * multiplier,
                (motion.y - 0.1) * multiplier + 0.1,
                motion.z * multiplier
        );
        return this;
    }

    public EntityModFX multipleParticleScaleBy(float multiplier) {
        this.setBoundingBox(this.getBoundingBox().inflate(multiplier - 1));
        this.particleScale *= multiplier;
        return this;
    }

    public void setRBGColorF(float r, float g, float b) {
        this.particleRed = r;
        this.particleGreen = g;
        this.particleBlue = b;
    }

    public void setAlphaF(float alpha) {
        this.particleAlpha = alpha;
    }

    public float getRedColorF() {
        return this.particleRed;
    }

    public float getGreenColorF() {
        return this.particleGreen;
    }

    public float getBlueColorF() {
        return this.particleBlue;
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

        Vec3 motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x, motion.y - 0.04 * this.particleGravity, motion.z);

        this.move(net.minecraft.world.entity.MoverType.SELF, this.getDeltaMovement());

        motion = this.getDeltaMovement();
        this.setDeltaMovement(motion.x * 0.98, motion.y * 0.98, motion.z * 0.98);

        if (this.onGround()) {
            motion = this.getDeltaMovement();
            this.setDeltaMovement(motion.x * 0.7, motion.y, motion.z * 0.7);
        }
    }

    public void setParticleTextureIndex(int index) {
        this.particleTextureIndexX = index % 16;
        this.particleTextureIndexY = index / 16;
    }

    public void nextTextureIndexX() {
        ++this.particleTextureIndexX;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.particleAge = tag.getInt("age");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("age", this.particleAge);
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