package com.hbm.entity.mob;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.entity.ModEntities;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.extprop.IRadiationImmune;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.items.weapon.sedna.factory.XFactoryMobs;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.BossEvent;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityUFO extends FlyingMob implements Enemy, IRadiationImmune {

    private static final EntityDataAccessor<Byte> BEAM = SynchedEntityData.defineId(EntityUFO.class, EntityDataSerializers.BYTE);
    private static final EntityDataAccessor<Integer> WAYPOINT_X = SynchedEntityData.defineId(EntityUFO.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAYPOINT_Y = SynchedEntityData.defineId(EntityUFO.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Integer> WAYPOINT_Z = SynchedEntityData.defineId(EntityUFO.class, EntityDataSerializers.INT);

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            this.getDisplayName(),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    public int courseChangeCooldown;
    public int scanCooldown;
    public int hurtCooldown;
    public int beamTimer;
    private Entity target;
    private final List<Entity> secondaries = new ArrayList<>();

    public EntityUFO(EntityType<? extends FlyingMob> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
        this.xpReward = 500;
        this.deathTime = -30;
    }

    public EntityUFO(Level level) {
        this(ModEntities.UFO.get(), level);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(BEAM, (byte) 0);
        this.entityData.define(WAYPOINT_X, 0);
        this.entityData.define(WAYPOINT_Y, 0);
        this.entityData.define(WAYPOINT_Z, 0);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return FlyingMob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20000.0D)
                .add(Attributes.FOLLOW_RANGE, 100.0D);
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (hurtCooldown > 0) return false;
        boolean hit = super.hurt(source, amount);
        if (hit) hurtCooldown = 5;
        return hit;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (!this.level().isClientSide) {
            if (this.level().getDifficulty() == Difficulty.PEACEFUL) {
                this.discard();
                return;
            }
            if (this.hurtCooldown > 0) hurtCooldown--;
        }

        if (this.courseChangeCooldown > 0) this.courseChangeCooldown--;
        if (this.scanCooldown > 0) this.scanCooldown--;

        if (this.target != null && !this.target.isAlive()) {
            this.target = null;
        }

        if (this.scanCooldown <= 0) {
            AABB box = this.getBoundingBox().inflate(100, 50, 100);
            List<Entity> entities = this.level().getEntities(this, box);
            this.secondaries.clear();
            this.target = null;

            for (Entity entity : entities) {
                if (!entity.isAlive() || !canAttackClass(entity.getClass())) continue;

                if (entity instanceof Player player) {
                    if (player.getAbilities().instabuild) continue;
                    if (player.hasEffect(MobEffects.INVISIBILITY)) continue;

                    if (this.target == null) {
                        this.target = entity;
                    } else {
                        if (this.distanceToSqr(entity) < this.distanceToSqr(this.target)) {
                            this.target = entity;
                        }
                    }
                }

                if (entity instanceof LivingEntity && this.distanceToSqr(entity) < 100 * 100 &&
                        this.hasLineOfSight(entity) && entity != this.target) {
                    this.secondaries.add(entity);
                }
            }

            if (this.target == null && !this.secondaries.isEmpty()) {
                this.target = this.secondaries.get(this.random.nextInt(this.secondaries.size()));
            }

            this.scanCooldown = 50;
        }

        if (this.target != null && this.courseChangeCooldown <= 0) {
            Vec3 vec = new Vec3(
                    this.getX() - this.target.getX(),
                    0,
                    this.getZ() - this.target.getZ()
            );

            if (this.random.nextInt(3) > 0) {
                vec = vec.yRot((float) (Math.PI * 2 * this.random.nextFloat()));
            }

            double length = vec.length();
            double overshoot = 35;

            int wX = (int) Math.floor(this.target.getX() - vec.x / length * overshoot);
            int wZ = (int) Math.floor(this.target.getZ() - vec.z / length * overshoot);
            int wY = Math.max(
                    this.level().getHeight(Heightmap.Types.WORLD_SURFACE, wX, wZ) + 20 + this.random.nextInt(15),
                    (int) this.target.getY() + 15
            );

            this.setWaypoint(wX, wY, wZ);
            this.courseChangeCooldown = 40 + this.random.nextInt(20);
        }

        if (!this.level().isClientSide) {
            updateBeamAndAttacks();
        }

        // Движение
        this.setDeltaMovement(0, 0, 0);

        if (this.courseChangeCooldown > 0) {
            double speed = this.target instanceof Player ? 5.0D : 2.0D;
            Vec3 delta = new Vec3(
                    getWaypointX() - this.getX(),
                    getWaypointY() - this.getY(),
                    getWaypointZ() - this.getZ()
            );
            double len = delta.length();

            if (len > 5) {
                if (isCourseTraversable(getWaypointX(), getWaypointY(), getWaypointZ(), len)) {
                    this.setDeltaMovement(
                            delta.x * speed / len,
                            delta.y * speed / len,
                            delta.z * speed / len
                    );
                } else {
                    this.courseChangeCooldown = 0;
                }
            }
        }
    }

    private void updateBeamAndAttacks() {
        if (this.beamTimer <= 0 && this.getBeam()) {
            this.setBeam(false);
        }

        if (this.target != null) {
            double dist = Math.abs(this.target.getX() - this.getX()) + Math.abs(this.target.getZ() - this.getZ());
            if (dist < 25) this.beamTimer = 30;
        }

        if (this.beamTimer > 0) {
            this.beamTimer--;
            if (!this.getBeam()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.BEACON_ACTIVATE, SoundSource.HOSTILE, 10.0F, 1.0F);
                this.setBeam(true);
            }

            int ix = (int) Math.floor(this.getX());
            int iz = (int) Math.floor(this.getZ());
            int iy = 0;

            for (int i = (int) Math.ceil(this.getY()); i >= 0; i--) {
                if (this.level().getBlockState(new BlockPos(ix, i, iz)).getBlock() != Blocks.AIR) {
                    iy = i;
                    break;
                }
            }

            if (iy < this.getY()) {
                AABB box = new AABB(this.getX(), iy, this.getZ(), this.getX(), this.getY(), this.getZ()).inflate(5, 0, 5);
                List<Entity> entities = this.level().getEntities(this, box);

                for (Entity e : entities) {
                    if (canAttackClass(e.getClass())) {
                        e.hurt(ModDamageSource.causeCombineDamage(this, e), 1000F);
                        e.setSecondsOnFire(5);
                        if (e instanceof LivingEntity living) {
                            ContaminationUtil.contaminate(living, HazardType.RADIATION, ContaminationType.CREATIVE, 5F);
                        }
                    }
                }

                CompoundTag data = new CompoundTag();
                data.putString("type", "ufo");
                PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(data, this.getX(), iy + 0.5, this.getZ()),
                        this.level(), BlockPos.containing(this.getX(), iy + 0.5, this.getZ()), 150);
                PacketDispatcher.sendToAllAround(new AuxParticlePacketNT(data,
                                this.getX() + this.getDeltaMovement().x * 0.5,
                                iy + 0.5,
                                this.getZ() + this.getDeltaMovement().z * 0.5),
                        this.level(), BlockPos.containing(this.getX(), iy + 0.5, this.getZ()), 150);
            }
        }

        // Атаки
        int cycle = this.tickCount % 300;
        if (cycle < 200) {
            if (this.tickCount % 4 == 0) {
                if (!this.secondaries.isEmpty()) {
                    Entity e = this.secondaries.get(this.random.nextInt(this.secondaries.size()));
                    if (!e.isAlive()) this.secondaries.remove(e);
                    else laserAttack(e);
                } else if (this.target != null) {
                    laserAttack(this.target);
                }
            } else if (this.tickCount % 4 == 2 && this.target != null) {
                laserAttack(this.target);
            }
        } else {
            if (this.tickCount % 20 == 0) {
                if (!this.secondaries.isEmpty()) {
                    Entity e = this.secondaries.get(this.random.nextInt(this.secondaries.size()));
                    if (!e.isAlive()) this.secondaries.remove(e);
                    else rocketAttack(e);
                } else if (this.target != null) {
                    rocketAttack(this.target);
                }
            } else if (this.tickCount % 20 == 10 && this.target != null) {
                rocketAttack(this.target);
            }
        }
    }

    private void laserAttack(Entity target) {
        Vec3 vec = new Vec3(this.getX() - target.getX(), 0, this.getZ() - target.getZ());
        vec = vec.yRot((float) Math.toRadians(-80 + this.random.nextInt(160)));
        vec = vec.normalize();

        double pivotX = this.getX() - vec.x * 10;
        double pivotY = this.getY() + 0.5;
        double pivotZ = this.getZ() - vec.z * 10;

        Vec3 heading = new Vec3(
                target.getX() - pivotX,
                target.getY() + target.getBbHeight() / 2 - pivotY,
                target.getZ() - pivotZ
        ).normalize();

        BulletConfig config = BulletConfigRegistry.WORM_LASER;
        EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(this.level(), null, config, 1.0F, 0F, 0, 0, 0);
        bullet.setOwner(this);
        bullet.setPos(pivotX, pivotY, pivotZ);
        bullet.shoot(heading.x, heading.y, heading.z, 2F, 0.02F);
        this.level().addFreshEntity(bullet);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.BEACON_DEACTIVATE, SoundSource.HOSTILE, 5.0F, 1.0F);
    }

    private void rocketAttack(Entity target) {
        Vec3 heading = new Vec3(
                target.getX() - this.getX(),
                target.getY() + target.getBbHeight() / 2 - this.getY() - 0.5D,
                target.getZ() - this.getZ()
        ).normalize();

        // Используем фабричный метод из XFactoryMobs
        EntityBulletBaseMK4 bullet = XFactoryMobs.createUfoRocket(
                this.level(),
                this,
                new Vec3(this.getX(), this.getY() - 0.5D, this.getZ()),
                heading,
                target
        );

        this.level().addFreshEntity(bullet);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.HOSTILE, 5.0F, 1.0F);
    }

    protected boolean canAttackClass(Class<?> clazz) {
        return clazz != this.getClass() && clazz != EntityBulletBaseMK4.class;
    }

    private boolean isCourseTraversable(double targetX, double targetY, double targetZ, double distance) {
        double d4 = (targetX - this.getX()) / distance;
        double d5 = (targetY - this.getY()) / distance;
        double d6 = (targetZ - this.getZ()) / distance;
        AABB aabb = this.getBoundingBox();

        for (int i = 1; i < distance; ++i) {
            aabb = aabb.move(d4, d5, d6);
            if (!this.level().getBlockCollisions(this, aabb).iterator().hasNext()) {
                return false;
            }
        }
        return true;
    }

    @Override
    protected float getSoundVolume() {
        return 10.0F;
    }

    @Override
    public void tickDeath() {
        if (this.getBeam()) this.setBeam(false);

        this.setDeltaMovement(0, -0.05, 0);

        if (this.deathTime == -10) {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    SoundEvents.IRON_GOLEM_DAMAGE, SoundSource.HOSTILE, 10.0F, 1.0F);
        }

        if (this.deathTime == 19 && !this.level().isClientSide) {
            this.level().explode(this, this.getX(), this.getY(), this.getZ(), 10.0F, Level.ExplosionInteraction.MOB);
            ExplosionNukeSmall.explode(this.level(), this.getX(), this.getY(), this.getZ(), ExplosionNukeSmall.PARAMS_MEDIUM);

            for (Player player : this.level().getEntitiesOfClass(Player.class, this.getBoundingBox().inflate(200, 200, 200))) {
                player.getInventory().add(new ItemStack(ModItems.COIN_UFO.get()));

                if (player instanceof ServerPlayer sp) {
                    ModCriteriaTriggers.BOSS_UFO.trigger(sp);
                }
            }
        }

        super.tickDeath();
    }

    public void setBeam(boolean b) {
        this.entityData.set(BEAM, (byte) (b ? 1 : 0));
    }

    public boolean getBeam() {
        return this.entityData.get(BEAM) == 1;
    }

    public void setWaypoint(int x, int y, int z) {
        this.entityData.set(WAYPOINT_X, x);
        this.entityData.set(WAYPOINT_Y, y);
        this.entityData.set(WAYPOINT_Z, z);
    }

    public int getWaypointX() { return this.entityData.get(WAYPOINT_X); }
    public int getWaypointY() { return this.entityData.get(WAYPOINT_Y); }
    public int getWaypointZ() { return this.entityData.get(WAYPOINT_Z); }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 500000;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
    }

    @Override
    public boolean fireImmune() {
        return true;
    }
}