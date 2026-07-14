package com.hbm.entity.projectile;

import com.hbm.blocks.bomb.BlockDetonatable;
import com.hbm.entity.ModEntities;
import com.hbm.entity.grenade.EntityGrenadeTau;
import com.hbm.entity.mob.EntityCreeperNuclear;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.MushroomCow;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.monster.*;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;

public class EntityBullet extends Projectile {

    private static final EntityDataAccessor<Byte> CRITICAL = SynchedEntityData.defineId(EntityBullet.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> TAU = SynchedEntityData.defineId(EntityBullet.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Byte> CHOPPER = SynchedEntityData.defineId(EntityBullet.class, EntityDataSerializers.BYTE);

    private BlockPos tilePos = BlockPos.ZERO;
    private BlockState tileState = null;
    private int tileData;
    private boolean inGround;
    private int inGroundTime;
    public int shake;
    private int ticksInAir;
    public double damage;
    private int knockbackStrength;
    private boolean instakill = false;
    private boolean rad = false;
    public boolean antidote = false;
    public boolean pip = false;
    public boolean fire = false;
    public double gravity = 0.0D;

    public EntityBullet(EntityType<? extends EntityBullet> type, Level level) {
        super(type, level);
    }

    // Конструкторы
    public EntityBullet(Level level, double x, double y, double z) {
        super(ModEntities.BULLET_LEGACY.get(), level);
        this.setPos(x, y, z);
    }

    public EntityBullet(Level level, LivingEntity shooter, float velocity, int dmgMin, int dmgMax, boolean instakill, boolean rad) {
        super(ModEntities.BULLET_LEGACY.get(), level);
        this.setOwner(shooter);
        this.instakill = instakill;
        this.rad = rad;
        this.damage = dmgMin + random.nextInt(dmgMax - dmgMin + 1);
        setupFromShooter(shooter, velocity, 1.0F);
    }

    public EntityBullet(Level level, LivingEntity shooter, float velocity, int dmgMin, int dmgMax, boolean instakill, String isTau) {
        super(ModEntities.BULLET_LEGACY.get(), level);
        this.setOwner(shooter);
        this.instakill = instakill;
        this.damage = dmgMin + random.nextInt(dmgMax - dmgMin + 1);
        setupFromShooter(shooter, velocity, 1.0F);
        this.setTau("tauDay".equals(isTau));
        this.setChopper("chopper".equals(isTau));
        this.setCritical(!"chopper".equals(isTau));
    }

    public EntityBullet(Level level, LivingEntity shooter, float velocity, int dmgMin, int dmgMax,
                        boolean instakill, String isTau, EntityGrenadeTau grenade) {
        super(ModEntities.BULLET_LEGACY.get(), level);
        this.setOwner(shooter);
        this.instakill = instakill;
        this.damage = dmgMin + random.nextInt(dmgMax - dmgMin + 1);

        this.setPos(grenade.getX(), grenade.getY() + grenade.getEyeHeight(), grenade.getZ());

        this.setYRot(grenade.getYRot());
        this.setXRot(grenade.getXRot());

        this.setPos(
                this.getX() - Mth.cos(this.getYRot() * Mth.DEG_TO_RAD) * 0.16F,
                this.getY() - 0.1D,
                this.getZ() - Mth.sin(this.getYRot() * Mth.DEG_TO_RAD) * 0.16F
        );

        double motionX = -Mth.sin(this.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(this.getXRot() * Mth.DEG_TO_RAD);
        double motionY = -Mth.sin(this.getXRot() * Mth.DEG_TO_RAD);
        double motionZ = Mth.cos(this.getYRot() * Mth.DEG_TO_RAD) * Mth.cos(this.getXRot() * Mth.DEG_TO_RAD);

        this.shoot(motionX, motionY, motionZ, velocity * 1.5F, 1.0F);

        this.setTau("tauDay".equals(isTau));
        this.setCritical(true);
    }

    public EntityBullet(Level level, LivingEntity shooter, float velocity) {
        super(ModEntities.BULLET_LEGACY.get(), level);
        this.setOwner(shooter);
        setupFromShooter(shooter, velocity, 1.0F);
    }

    public EntityBullet(Level level, LivingEntity shooter, float velocity, float spread) {
        super(ModEntities.BULLET_LEGACY.get(), level);
        this.setOwner(shooter);
        setupFromShooter(shooter, velocity, spread);
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

    public EntityBullet(Level level, int x, int y, int z, double mx, double my, double mz, double grav) {
        super(ModEntities.BULLET_LEGACY.get(), level);
        this.setPos(x + 0.5, y + 0.5, z + 0.5);
        this.setDeltaMovement(mx, my, mz);
        this.gravity = grav;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(CRITICAL, (byte) 0);
        this.entityData.define(TAU, (byte) 0);
        this.entityData.define(CHOPPER, (byte) 0);
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
        this.inGroundTime = 0;
    }

    public void shoot2(double x, double y, double z, float velocity, float inaccuracy) {
        double length = Math.sqrt(x * x + y * y + z * z);
        x /= length;
        y /= length;
        z /= length;

        x += this.random.nextGaussian() * 0.0425D * inaccuracy;
        y += this.random.nextGaussian() * 0.0425D * inaccuracy;
        z += this.random.nextGaussian() * 0.0425D * inaccuracy;

        x *= velocity;
        y *= velocity;
        z *= velocity;

        this.setDeltaMovement(x, y, z);

        float f3 = (float) Math.sqrt(x * x + z * z);
        this.setYRot((float) (Math.atan2(x, z) * 180.0D / Math.PI));
        this.setXRot((float) (Math.atan2(y, f3) * 180.0D / Math.PI));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
        this.inGroundTime = 0;
    }

    @Override
    public void tick() {
        super.tick();

        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            Vec3 motion = this.getDeltaMovement();
            float f = (float) Math.sqrt(motion.x * motion.x + motion.z * motion.z);
            this.setYRot((float) (Math.atan2(motion.x, motion.z) * 180.0D / Math.PI));
            this.setXRot((float) (Math.atan2(motion.y, f) * 180.0D / Math.PI));
            this.yRotO = this.getYRot();
            this.xRotO = this.getXRot();
        }

        // Проверка блока под пулей
        if (this.tilePos != null && this.tilePos != BlockPos.ZERO) {
            BlockState state = this.level().getBlockState(this.tilePos);
            Block block = state.getBlock();

            if (!state.isAir()) {
                AABB aabb = state.getCollisionShape(this.level(), this.tilePos).bounds().move(this.tilePos);
                if (aabb != null && aabb.contains(this.position()) && !this.isCritical()) {
                    this.inGround = true;
                }

                if (block instanceof BlockDetonatable det) {
                    det.onShot(this.level(), this.tilePos);
                }

                if (state.is(Blocks.GLASS) || state.is(Blocks.GLASS_PANE) ||
                        state.is(BlockTags.IMPERMEABLE)) {  // Все непроницаемые блоки, включая стекло
                    this.level().setBlock(this.tilePos, Blocks.AIR.defaultBlockState(), 3);
                    this.level().playSound(null, this.tilePos, SoundEvents.GLASS_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            }
        }

        if (this.shake > 0) {
            --this.shake;
        }

        if (this.inGround && !this.isCritical()) {
            this.discard();
            return;
        } else {
            ++this.ticksInAir;
        }

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

        // Частицы
        if (this.isCritical()) {
            for (int i = 0; i < 4; ++i) {
                if (this.isTau()) {
                    this.level().addParticle(new DustParticleOptions(new Vector3f(1.0F, 0.0F, 0.0F), 1.0F),
                            this.getX() + motion.x * i / 4.0D,
                            this.getY() + motion.y * i / 4.0D,
                            this.getZ() + motion.z * i / 4.0D,
                            -motion.x, -motion.y + 0.2D, -motion.z);
                } else {
                    this.level().addParticle(ParticleTypes.FIREWORK,
                            this.getX() + motion.x * i / 4.0D,
                            this.getY() + motion.y * i / 4.0D,
                            this.getZ() + motion.z * i / 4.0D,
                            -motion.x, -motion.y + 0.2D, -motion.z);
                }
            }
        }

        // Замедление и гравитация
        float drag = 0.99F;
        if (this.isInWater()) {
            for (int i = 0; i < 4; ++i) {
                this.level().addParticle(ParticleTypes.BUBBLE,
                        this.getX() - motion.x * 0.25,
                        this.getY() - motion.y * 0.25,
                        this.getZ() - motion.z * 0.25,
                        motion.x, motion.y, motion.z);
            }
            drag = 0.8F;
        }

        this.setDeltaMovement(motion.multiply(drag, drag, drag));
        this.setDeltaMovement(this.getDeltaMovement().add(0, -gravity, 0));

        // Лимит времени жизни
        if (this.tickCount > 250) {
            this.discard();
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        Entity target = result.getEntity();
        Entity shooter = this.getOwner();

        float speed = (float) this.getDeltaMovement().length();
        int damageAmount = (int) Math.ceil(speed * this.damage);

        if (this.isCritical()) {
            damageAmount += this.random.nextInt(damageAmount / 2 + 2);
        }

        DamageSource source = getDamageSource(shooter);

        if (this.fire || this.isOnFire()) {
            target.setSecondsOnFire(5);
        }

        if (target.hurt(source, damageAmount)) {
            if (target instanceof LivingEntity living) {
                handleSpecialEffects(living);

                if (this.knockbackStrength > 0) {
                    double knockbackX = -Math.sin(this.getYRot() * Math.PI / 180.0F) * this.knockbackStrength * 0.6;
                    double knockbackZ = Math.cos(this.getYRot() * Math.PI / 180.0F) * this.knockbackStrength * 0.6;
                    target.setDeltaMovement(target.getDeltaMovement().add(knockbackX, 0.1D, knockbackZ));
                }

                if (shooter instanceof LivingEntity livingShooter) {
                    // Эффекты зачарований
                }

                if (shooter instanceof ServerPlayer sp && target != shooter) {
                    // Статистика
                }
            }

            if (!this.level().isClientSide && this.instakill && !(target instanceof Player)) {
                if (target instanceof LivingEntity living) {
                    living.setHealth(0);
                }
            }
        } else {
            // Повторная попытка с другим уроном
            target.hurt(source, (float) this.damage);
        }

        if (!(target instanceof ItemFrame)) {
            this.discard();
        }
    }

    @Override
    protected void onHitBlock(BlockHitResult result) {
        if (!this.isCritical()) {
            this.tilePos = result.getBlockPos();
            this.tileState = this.level().getBlockState(this.tilePos);
            this.tileData = this.tileState.getBlock().defaultBlockState().getLightEmission();
            this.inGround = true;
            this.shake = 7;

            Vec3 hitVec = result.getLocation();
            Vec3 pos = this.position();
            Vec3 delta = hitVec.subtract(pos).normalize();
            this.setPos(pos.x - delta.x * 0.05, pos.y - delta.y * 0.05, pos.z - delta.z * 0.05);

            BlockState state = this.level().getBlockState(this.tilePos);
            if (!state.isAir()) {
                state.getBlock().stepOn(this.level(), this.tilePos, state, this);
            }
        }
    }

    private DamageSource getDamageSource(Entity shooter) {
        if (!this.isCritical() && !this.isChopper()) {
            return shooter == null ?
                    ModDamageSource.causeBulletDamage(this, this) :
                    ModDamageSource.causeBulletDamage(this, shooter);
        } else if (!this.isChopper()) {
            return shooter == null ?
                    ModDamageSource.causeTauDamage(this, this) :
                    ModDamageSource.causeTauDamage(this, shooter);
        } else if (!this.isCritical()) {
            return shooter == null ?
                    ModDamageSource.causeDisplacementDamage(this, this) :
                    ModDamageSource.causeDisplacementDamage(this, shooter);
        }
        return this.damageSources().generic();
    }

    private void handleSpecialEffects(LivingEntity living) {
        if (this.rad) {
            if (living instanceof Player p && ArmorUtil.checkForHazmat(p)) {
                return;
            }

            if (living instanceof Creeper) {
                EntityCreeperNuclear creep = new EntityCreeperNuclear(ModEntities.CREEPER_NUCLEAR.get(), this.level());
                creep.moveTo(living.getX(), living.getY(), living.getZ(), living.getYRot(), living.getXRot());
                if (!living.isRemoved() && !this.level().isClientSide) {
                    this.level().addFreshEntity(creep);
                }
                living.discard();
            } else if (living instanceof Villager) {
                Zombie zombie = new Zombie(EntityType.ZOMBIE, this.level());
                zombie.moveTo(living.getX(), living.getY(), living.getZ(), living.getYRot(), living.getXRot());
                living.discard();
                if (!this.level().isClientSide) {
                    this.level().addFreshEntity(zombie);
                }
            } else if (!(living instanceof EntityCreeperNuclear) &&
                    !(living instanceof MushroomCow) &&
                    !(living instanceof Zombie)) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 2 * 60 * 20, 2));
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 20, 4));
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 1 * 60 * 20, 1));
            }
        }

        if (this.antidote) {
            living.removeAllEffects();
        }
    }

    @Override
    public void playerTouch(Player player) {
        if (!this.level().isClientSide && this.inGround && this.shake <= 0) {
            // Подбор пули
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
        tag.putShort("life", (short) this.inGroundTime);
        tag.putInt("inTile", Block.getId(this.tileState != null ? this.tileState : Blocks.AIR.defaultBlockState()));
        tag.putByte("inData", (byte) this.tileData);
        tag.putByte("shake", (byte) this.shake);
        tag.putByte("inGround", (byte) (this.inGround ? 1 : 0));
        tag.putDouble("damage", this.damage);
        tag.putBoolean("instakill", this.instakill);
        tag.putBoolean("rad", this.rad);
        tag.putBoolean("antidote", this.antidote);
        tag.putBoolean("pip", this.pip);
        tag.putBoolean("fire", this.fire);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.tilePos = new BlockPos(tag.getShort("xTile"), tag.getShort("yTile"), tag.getShort("zTile"));
        this.inGroundTime = tag.getShort("life");
        int blockId = tag.getInt("inTile");
        this.tileState = Block.stateById(blockId);
        this.tileData = tag.getByte("inData") & 255;
        this.shake = tag.getByte("shake") & 255;
        this.inGround = tag.getByte("inGround") == 1;
        this.damage = tag.getDouble("damage");
        this.instakill = tag.getBoolean("instakill");
        this.rad = tag.getBoolean("rad");
        this.antidote = tag.getBoolean("antidote");
        this.pip = tag.getBoolean("pip");
        this.fire = tag.getBoolean("fire");
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @OnlyIn(Dist.CLIENT)
    public int getLightColor(float partialTicks) {
        if (this.isCritical() || this.isChopper()) {
            return 15728880;
        }

        // Стандартное освещение
        BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());
        return this.level().getLightEngine().getRawBrightness(pos, 0);
    }

    // Геттеры и сеттеры
    public void setCritical(boolean critical) {
        byte b = this.entityData.get(CRITICAL);
        this.entityData.set(CRITICAL, critical ? (byte) (b | 1) : (byte) (b & -2));
    }

    public boolean isCritical() {
        return (this.entityData.get(CRITICAL) & 1) != 0;
    }

    public void setTau(boolean tau) {
        byte b = this.entityData.get(TAU);
        this.entityData.set(TAU, tau ? (byte) (b | 1) : (byte) (b & -2));
    }

    public boolean isTau() {
        return (this.entityData.get(TAU) & 1) != 0;
    }

    public void setChopper(boolean chopper) {
        byte b = this.entityData.get(CHOPPER);
        this.entityData.set(CHOPPER, chopper ? (byte) (b | 1) : (byte) (b & -2));
    }

    public boolean isChopper() {
        return (this.entityData.get(CHOPPER) & 1) != 0;
    }

    public void setDamage(double damage) {
        this.damage = damage;
    }

    public void setKnockback(int knockback) {
        this.knockbackStrength = knockback;
    }

}