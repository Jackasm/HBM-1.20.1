package com.hbm.entity.projectile;

import com.hbm.explosion.ExplosionChaos;
import com.hbm.explosion.ExplosionNukeGeneric;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;


public class EntitySchrab extends Projectile {

    private static final EntityDataAccessor<Byte> CRITICAL = SynchedEntityData.defineId(EntitySchrab.class, EntityDataSerializers.BYTE);

    private BlockPos tilePos = BlockPos.ZERO;
    private BlockState tileState = null;
    private int inData;
    private boolean inGround;
    public int shake;
    private int ticksInAir;
    private double damage = 2.0D;
    private int knockbackStrength;
    public double gravity = 0.0D;

    public EntitySchrab(EntityType<? extends EntitySchrab> type, Level level) {
        super(type, level);
        this.setBoundingBox(this.getBoundingBox().inflate(0.5F));
    }

    // Конструктор для взрывающихся фрагментов
    public EntitySchrab(EntityType<? extends EntitySchrab> type, Level level, double x, double y, double z,
                        double mx, double my, double mz, double grav) {
        super(type, level);
        this.setPos(x + 0.5, y + 0.5, z + 0.5);
        this.setDeltaMovement(mx, my, mz);
        this.gravity = grav;
    }

    // Конструктор с стрелком
    public EntitySchrab(EntityType<? extends EntitySchrab> type, Level level, LivingEntity shooter, float velocity) {
        super(type, level);
        this.setOwner(shooter);
        setupFromShooter(shooter, velocity, 1.0F);
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
    }

    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        double length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;

        x += this.random.nextGaussian() * 0.0075D * inaccuracy;
        y += this.random.nextGaussian() * 0.0075D * inaccuracy;
        z += this.random.nextGaussian() * 0.0075D * inaccuracy;

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

        if (this.tickCount > 250) {
            this.discard();
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
        if (this.tilePos != null && this.tilePos != BlockPos.ZERO) {
            BlockState state = this.level().getBlockState(this.tilePos);
            if (!state.isAir()) {
                VoxelShape shape = state.getCollisionShape(this.level(), this.tilePos);
                if (!shape.isEmpty()) {
                    AABB aabb = shape.bounds().move(this.tilePos);
                    if (aabb.contains(this.position())) {
                        this.inGround = true;
                    }
                }
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround) {
            if (!this.level().isClientSide) {
                BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
                this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.5F, Level.ExplosionInteraction.MOB);
                ExplosionChaos.burn(this.level(), pos, 10);
                ExplosionChaos.flameDeath(this.level(), pos, 20);
                ExplosionNukeGeneric.wasteNoSchrab(this.level(), pos, 20);
                ExplosionChaos.poison(this.level(), this.getX(), this.getY(), this.getZ(), 20);
            }

            LightningBolt lightning = new LightningBolt(EntityType.LIGHTNING_BOLT, this.level());
            lightning.setPos(this.getX(), this.getY(), this.getZ());
            this.level().addFreshEntity(lightning);
            this.discard();
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

            // Частицы дыма
            for (int i = 0; i < 4; i++) {
                this.level().addParticle(ParticleTypes.SMOKE,
                        this.getX(), this.getY(), this.getZ(),
                        0, 0, 0);
            }

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
            if (this.isInWater()) {
                drag = 0.8F;
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(drag));
            this.setDeltaMovement(this.getDeltaMovement().add(0, -gravity, 0));
        }
    }

    @Override
    protected void onHitEntity(@NotNull EntityHitResult result) {
        if (this.level().isClientSide) return;

        Entity target = result.getEntity();
        float speed = (float) this.getDeltaMovement().length();
        int damageAmount = (int) Math.ceil(speed * this.damage);

        if (this.getIsCritical()) {
            damageAmount += this.random.nextInt(damageAmount / 2 + 2);
        }

        DamageSource source = this.getOwner() == null ?
                this.damageSources().magic() :
                this.damageSources().indirectMagic(this, this.getOwner());

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
                    EnchantmentHelper.doPostHurtEffects(livingTarget, livingOwner);
                    EnchantmentHelper.doPostDamageEffects(livingOwner, livingTarget);
                }

                if (this.getOwner() instanceof ServerPlayer sp && target != this.getOwner()) {
                    sp.connection.send(new ClientboundGameEventPacket(ClientboundGameEventPacket.ARROW_HIT_PLAYER, 0.0F));
                }
            }

            if (!(target instanceof EnderMan)) {
                if (!this.level().isClientSide) {
                    this.level().explode(this, this.getX(), this.getY(), this.getZ(), 2.5F, Level.ExplosionInteraction.MOB);
                }
                this.discard();
            }
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(-0.1, -0.1, -0.1));
            this.setYRot(this.getYRot() + 180.0F);
            this.yRotO += 180.0F;
            this.ticksInAir = 0;
        }
    }

    @Override
    protected void onHitBlock(@NotNull BlockHitResult result) {
        if (!this.level().isClientSide) {
            this.tilePos = result.getBlockPos();
            this.tileState = this.level().getBlockState(this.tilePos);
            this.inData = this.tileState.getBlock().defaultBlockState().getLightEmission();

            Vec3 hitVec = result.getLocation();
            Vec3 pos = this.position();
            Vec3 delta = hitVec.subtract(pos).normalize();
            this.setPos(
                    this.getX() - delta.x * 0.05,
                    this.getY() - delta.y * 0.05,
                    this.getZ() - delta.z * 0.05
            );

            this.playSound(SoundEvents.ARROW_HIT, 1.0F, 1.2F / (this.random.nextFloat() * 0.2F + 0.9F));
            this.inGround = true;
            this.shake = 7;
            this.setIsCritical(false);

            BlockState state = this.level().getBlockState(this.tilePos);
            if (!state.isAir()) {
                state.getBlock().stepOn(this.level(), this.tilePos, state, this);
            }
        }
    }

    @Override
    protected boolean canHitEntity(@NotNull Entity target) {
        return super.canHitEntity(target) && (target != this.getOwner() || this.ticksInAir >= 5);
    }

    @Override
    public void playerTouch(@NotNull Player player) {
        if (!this.level().isClientSide && this.inGround && this.shake <= 0) {
            this.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((this.random.nextFloat() - this.random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
            player.take(this, 1);
            this.discard();
        }
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
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
    }

    @Override
    protected void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.tilePos = new BlockPos(tag.getShort("xTile"), tag.getShort("yTile"), tag.getShort("zTile"));
        this.ticksInAir = tag.getShort("life");
        int blockId = tag.getInt("inTile");
        this.tileState = Block.stateById(blockId);
        this.inData = tag.getByte("inData") & 255;
        this.shake = tag.getByte("shake") & 255;
        this.inGround = tag.getByte("inGround") == 1;
        this.damage = tag.getDouble("damage");
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
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