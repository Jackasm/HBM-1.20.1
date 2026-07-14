package com.hbm.entity.projectile;

import com.hbm.entity.grenade.EntityGrenadeZOMG;
import com.hbm.explosion.ExplosionChaos;

import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class EntityRainbow extends Projectile {

    private static final EntityDataAccessor<Byte> CRITICAL = SynchedEntityData.defineId(EntityRainbow.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> COLOR_R = SynchedEntityData.defineId(EntityRainbow.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> COLOR_G = SynchedEntityData.defineId(EntityRainbow.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> COLOR_B = SynchedEntityData.defineId(EntityRainbow.class, EntityDataSerializers.BYTE);

    private BlockPos tilePos = BlockPos.ZERO;
    private BlockState tileState = null;
    private int inData;
    private boolean inGround;
    public int shake;
    private int ticksInAir;
    private double damage = 2.0D;
    private int knockbackStrength;
    public double gravity = 0.0D;

    public EntityRainbow(EntityType<? extends EntityRainbow> type, Level level) {
        super(type, level);
        this.setBoundingBox(this.getBoundingBox().inflate(0.5F));
        this.noPhysics = true;
    }

    // Конструктор из гранаты ZOMG
    public EntityRainbow(EntityType<? extends EntityRainbow> type, Level level, @Nullable Player shooter, float velocity, int dmgMin, int dmgMax, EntityGrenadeZOMG grenade) {
        super(type, level);
        this.setOwner(shooter);
        this.setPos(grenade.getX(), grenade.getY() + grenade.getEyeHeight(), grenade.getZ());
        this.setYRot(grenade.getYRot());
        this.setXRot(grenade.getXRot());

        this.setPos(
                this.getX() - Math.cos(this.getYRot() * Math.PI / 180.0F) * 0.16F,
                this.getY() - 0.1D,
                this.getZ() - Math.sin(this.getYRot() * Math.PI / 180.0F) * 0.16F
        );

        double motionX = -Math.sin(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F);
        double motionY = -Math.sin(this.getXRot() * Math.PI / 180.0F);
        double motionZ = Math.cos(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F);

        this.shoot(motionX, motionY, motionZ, velocity * 1.5F, 1.0F);
        this.randomizeColor();
    }

    // Обычный конструктор со стрелком
    public EntityRainbow(EntityType<? extends EntityRainbow> type, Level level, LivingEntity shooter, float velocity) {
        super(type, level);
        this.setOwner(shooter);
        setupFromShooter(shooter, velocity, 1.0F);
    }

    public EntityRainbow(EntityType<? extends EntityRainbow> type, Level level, double x, double y, double z, double mx, double my, double mz, double grav) {
        super(type, level);
        this.setPos(x + 0.5, y + 0.5, z + 0.5);
        this.setDeltaMovement(mx, my, mz);
        this.gravity = grav;
    }

    private void setupFromShooter(LivingEntity shooter, float velocity, float spread) {
        this.setPos(shooter.getX(), shooter.getEyeY(), shooter.getZ());
        this.setYRot(shooter.getYRot());
        this.setXRot(shooter.getXRot());

        this.setPos(
                this.getX() - Math.cos(this.getYRot() * Math.PI / 180.0F) * 0.16F,
                this.getY() - 0.1D,
                this.getZ() - Math.sin(this.getYRot() * Math.PI / 180.0F) * 0.16F
        );

        double motionX = -Math.sin(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F);
        double motionY = -Math.sin(this.getXRot() * Math.PI / 180.0F);
        double motionZ = Math.cos(this.getYRot() * Math.PI / 180.0F) * Math.cos(this.getXRot() * Math.PI / 180.0F);

        this.shoot(motionX, motionY, motionZ, velocity * 1.5F, spread);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CRITICAL, (byte) 0);
        this.entityData.define(COLOR_R, (byte) 0);
        this.entityData.define(COLOR_G, (byte) 0);
        this.entityData.define(COLOR_B, (byte) 0);
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        double length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;

        x += this.random.nextGaussian() * 0.0545D * inaccuracy;
        y += this.random.nextGaussian() * 0.0545D * inaccuracy;
        z += this.random.nextGaussian() * 0.0545D * inaccuracy;

        x *= velocity;
        y *= velocity;
        z *= velocity;

        this.setDeltaMovement(x, y, z);

        float f3 = (float) Math.sqrt(x * x + z * z);
        this.setYRot((float) (Math.atan2(x, z) * 180.0D / Math.PI));
        this.setXRot((float) (Math.atan2(y, f3) * 180.0D / Math.PI));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.ticksInAir = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.tickCount > 100) {
            this.discard();
            return;
        }

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            Vec3 motion = this.getDeltaMovement();
            float f = (float) Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            this.setYRot((float) (Math.atan2(motion.x, motion.z) * 180.0D / Math.PI));
            this.setXRot((float) (Math.atan2(motion.y, f) * 180.0D / Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        // Проверка на столкновение с блоком
        BlockPos currentPos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        BlockState state = this.level().getBlockState(currentPos);
        if (!state.isAir() && state.getBlock() != Blocks.BEDROCK) {
            if (!this.level().isClientSide) {
                    ExplosionChaos.explodeZOMG(this.level(), BlockPos.containing(this.getX(), this.getY(), this.getZ()), 5);
                }

                return;
        }


        if (this.shake > 0) {
            --this.shake;
        } else {
            ++this.ticksInAir;

            // Обработка попаданий
            HitResult hitResult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
            if (hitResult.getType() != HitResult.Type.MISS) {
                this.onHit(hitResult);
            }

            // Движение
            Vec3 motion = this.getDeltaMovement();
            double dX = this.getX() + motion.x;
            double dY = this.getY() + motion.y;
            double dZ = this.getZ() + motion.z;

            this.setPos(dX, dY, dZ);

            // Частицы в воде
            if (this.isInWater()) {
                for (int i = 0; i < 4; i++) {
                    this.level().addParticle(ParticleTypes.BUBBLE,
                            this.getX() - motion.x * 0.25,
                            this.getY() - motion.y * 0.25,
                            this.getZ() - motion.z * 0.25,
                            motion.x, motion.y, motion.z);
                }
            }

            // Замедление и гравитация
            float drag = 0.99F;
            this.setDeltaMovement(this.getDeltaMovement().multiply(drag, drag, drag));
            this.setDeltaMovement(this.getDeltaMovement().add(0, -gravity, 0));
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        if (this.level().isClientSide) return;

        Entity target = result.getEntity();
        float speed = (float) this.getDeltaMovement().length();
        int damageAmount = (int) Math.ceil(speed * this.damage);

        if (this.getIsCritical()) {
            damageAmount += this.random.nextInt(damageAmount / 2 + 2);
        }

        DamageSource source = this.getOwner() == null ?
                ModDamageSource.causeSubatomicDamage(this, this) :
                ModDamageSource.causeSubatomicDamage(this, this.getOwner());

        if (this.isOnFire() && !(target instanceof EnderMan)) {
            target.setSecondsOnFire(5);
        }

        if (target.hurt(source, damageAmount)) {
            if (target instanceof LivingEntity livingTarget) {
                if (this.knockbackStrength > 0) {
                    Vec3 knockback = this.getDeltaMovement().normalize()
                            .scale(this.knockbackStrength * 0.6);
                    livingTarget.setDeltaMovement(
                            livingTarget.getDeltaMovement().add(knockback.x, 0.1, knockback.z)
                    );
                }

                if (this.getOwner() instanceof LivingEntity livingOwner) {
                    // Эффекты зачарований
                }

                if (this.getOwner() instanceof ServerPlayer sp && target != this.getOwner()) {
                    // Статистика
                }
            }

            if (!(target instanceof EnderMan)) {
                target.hurt(source, 100000F);
                ExplosionChaos.explodeZOMG(this.level(), BlockPos.containing(this.getX(), this.getY(), this.getZ()), 5);
            }
        }

        this.discard();
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.tilePos = result.getBlockPos();
            this.tileState = this.level().getBlockState(this.tilePos);
            this.inData = this.tileState.getBlock().defaultBlockState().getLightEmission();
            ExplosionChaos.explodeZOMG(this.level(), BlockPos.containing(this.getX(), this.getY(), this.getZ()), 5);
        }
    }

    @Override
    protected boolean canHitEntity(Entity target) {
        return super.canHitEntity(target) && (target != this.getOwner() || this.ticksInAir >= 5);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putShort("xTile", (short) this.tilePos.getX());
        tag.putShort("yTile", (short) this.tilePos.getY());
        tag.putShort("zTile", (short) this.tilePos.getZ());
        tag.putShort("life", (short) this.ticksInAir);
        tag.putInt("inTile", Block.getId(this.tileState != null ? this.tileState : Blocks.AIR.defaultBlockState()));
        tag.putByte("inData", (byte) this.inData);
        tag.putByte("shake", (byte) this.shake);
        tag.putByte("inGround", (byte) (this.inGround ? 1 : 0));
        tag.putDouble("damage", this.damage);
        tag.putByte("colorR", this.entityData.get(COLOR_R));
        tag.putByte("colorG", this.entityData.get(COLOR_G));
        tag.putByte("colorB", this.entityData.get(COLOR_B));
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.tilePos = new BlockPos(tag.getShort("xTile"), tag.getShort("yTile"), tag.getShort("zTile"));
        this.ticksInAir = tag.getShort("life");
        int blockId = tag.getInt("inTile");
        this.tileState = Block.stateById(blockId);
        this.inData = tag.getByte("inData") & 255;
        this.shake = tag.getByte("shake") & 255;
        this.inGround = tag.getByte("inGround") == 1;
        this.damage = tag.getDouble("damage");

        if (tag.contains("colorR")) {
            this.entityData.set(COLOR_R, tag.getByte("colorR"));
        }
        if (tag.contains("colorG")) {
            this.entityData.set(COLOR_G, tag.getByte("colorG"));
        }
        if (tag.contains("colorB")) {
            this.entityData.set(COLOR_B, tag.getByte("colorB"));
        } else {
            this.randomizeColor();
        }
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public void randomizeColor() {
        this.entityData.set(COLOR_R, (byte) this.random.nextInt(2));
        this.entityData.set(COLOR_G, (byte) this.random.nextInt(2));
        this.entityData.set(COLOR_B, (byte) this.random.nextInt(2));
    }

    public byte getColorR() {
        return this.entityData.get(COLOR_R);
    }

    public byte getColorG() {
        return this.entityData.get(COLOR_G);
    }

    public byte getColorB() {
        return this.entityData.get(COLOR_B);
    }

    public void setIsCritical(boolean critical) {
        byte b = this.entityData.get(CRITICAL);
        this.entityData.set(CRITICAL, critical ? (byte) (b | 1) : (byte) (b & -2));
    }

    public boolean getIsCritical() {
        return (this.entityData.get(CRITICAL) & 1) != 0;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public double getDamage() {
        return this.damage;
    }

    public void setKnockbackStrength(int knockback) {
        this.knockbackStrength = knockback;
    }
}