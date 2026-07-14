package com.hbm.entity.mob.botprime;

import com.hbm.entity.ModEntities;
import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityBOTPrimeHead extends EntityBOTPrimeBase {

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.hbm.bot_prime_head"),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    public EntityBOTPrimeHead(EntityType<? extends EntityBOTPrimeHead> type, Level level) {
        super(type, level);
        this.xpReward = 1000;
        this.wasNearGround = false;
        this.attackRange = 150.0D;
        this.setSize(3.0F, 3.0F);
        this.maxSpeed = 1.0D;
        this.fallSpeed = 0.006D;
    }


    public static AttributeSupplier.Builder createAttributes() {
        return EntityBOTPrimeBase.createAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.15D);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, 0, false, false, null));
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public boolean getIsHead() {
        return true;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (super.hurt(source, amount)) {
            this.dmgCooldown = 10;
            return true;
        }
        return false;
    }

    @Override
    public SpawnGroupData finalizeSpawn(@NotNull ServerLevelAccessor level, @NotNull DifficultyInstance difficulty, @NotNull MobSpawnType spawnType, @Nullable SpawnGroupData spawnData, @Nullable CompoundTag data) {
        setHeadID(this.getId());

        int x = (int) Math.floor(this.getX());
        int y = (int) Math.floor(this.getY());
        int z = (int) Math.floor(this.getZ());

        for (int i = 0; i < 74; i++) {
            EntityBOTPrimeBody bodyPart = new EntityBOTPrimeBody(ModEntities.BOT_PRIME_BODY.get(),this.level());
            bodyPart.setPartNumber(i);
            bodyPart.setPos(x, y, z);
            bodyPart.setHeadID(this.getId());
            this.level().addFreshEntity(bodyPart);
        }

        this.setPos(x, y, z);
        this.spawnPoint = new BlockPos(x, y, z);

        return super.finalizeSpawn(level, difficulty, spawnType, spawnData, data);
    }

    @Override
    protected void customServerAiStep() {
        this.updateEntityActionState();
        super.customServerAiStep();

        updateHeadMovement();

        if (this.getHealth() < this.getMaxHealth() && this.tickCount % 6 == 0) {
            if (this.targetedEntity != null) {
                this.heal(1.0F);
            } else if (this.hurtTime == 0) {
                this.heal(4.0F);
            }
        }

        if (this.targetedEntity != null && this.targetedEntity.distanceToSqr(this) < this.attackRange * this.attackRange) {
            if (this.canEntityBeSeenThroughNonSolids(this.targetedEntity)) {
                this.attackCounter++;
                if (this.attackCounter == 30) {
                    this.laserAttack(this.targetedEntity, true);
                    this.attackCounter = 0;
                }
            } else {
                this.attackCounter = 0;
            }
        } else {
            this.attackCounter = 0;
        }
    }

    @Override
    public void tick() {
        super.tick();

        double dx = this.getDeltaMovement().x;
        double dy = this.getDeltaMovement().y;
        double dz = this.getDeltaMovement().z;
        float f3 = (float) Math.sqrt(dx * dx + dz * dz);
        this.setYRot((float) (Math.atan2(dx, dz) * 180.0D / Math.PI));
        this.setXRot((float) (Math.atan2(dy, f3) * 180.0D / Math.PI));
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);

        AABB box = this.getBoundingBox().inflate(200, 200, 200);
        List<Player> players = this.level().getEntitiesOfClass(Player.class, box);

        for (Player player : players) {
            // Достижение bossWorm
            player.getInventory().add(new ItemStack(ModItems.COIN_WORM.get()));
        }
    }

    @Override
    public float getAttackStrength(Entity target) {
        return 1000.0F;
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.addAdditionalSaveData(nbt);
        nbt.putInt("spawnX", this.spawnPoint.getX());
        nbt.putInt("spawnY", this.spawnPoint.getY());
        nbt.putInt("spawnZ", this.spawnPoint.getZ());
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag nbt) {
        super.readAdditionalSaveData(nbt);
        int x = nbt.getInt("spawnX");
        int y = nbt.getInt("spawnY");
        int z = nbt.getInt("spawnZ");
        this.spawnPoint = new BlockPos(x, y, z);
    }

    protected void updateHeadMovement() {
        double deltaX = this.waypointX - this.getX();
        double deltaY = this.waypointY - this.getY();
        double deltaZ = this.waypointZ - this.getZ();
        double deltaSq = deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ;

        if (this.courseChangeCooldown-- <= 0) {
            this.courseChangeCooldown += this.random.nextInt(5) + 2;
            deltaSq = Math.sqrt(deltaSq);

            if (this.getDeltaMovement().x * this.getDeltaMovement().x +
                    this.getDeltaMovement().y * this.getDeltaMovement().y +
                    this.getDeltaMovement().z * this.getDeltaMovement().z < this.maxSpeed) {

                if (!this.isCourseTraversable()) {
                    deltaSq *= 8.0D;
                }

                double moverSpeed = this.getAttribute(Attributes.MOVEMENT_SPEED).getBaseValue();
                this.setDeltaMovement(this.getDeltaMovement().add(
                        deltaX / deltaSq * moverSpeed,
                        deltaY / deltaSq * moverSpeed,
                        deltaZ / deltaSq * moverSpeed
                ));
            }
        }

        if (!this.isCourseTraversable()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0, -this.fallSpeed, 0));
        }

        if (this.dmgCooldown > 0) {
            this.dmgCooldown--;
        }

        this.aggroCooldown--;

        if (this.getTarget() != null) {
            if (this.aggroCooldown <= 0) {
                this.targetedEntity = this.getTarget();
                this.aggroCooldown = 20;
            }
        } else if (this.targetedEntity == null) {
            this.waypointX = this.spawnPoint.getX() - 50 + this.random.nextInt(100);
            this.waypointY = this.spawnPoint.getY() - 30 + this.random.nextInt(60);
            this.waypointZ = this.spawnPoint.getZ() - 50 + this.random.nextInt(100);
        }

        this.setYRot(-(float) (Math.atan2(this.getDeltaMovement().x, this.getDeltaMovement().z) * 180.0F / Math.PI));
        this.setXRot((float) (Math.atan2(this.getDeltaMovement().y, Math.sqrt(this.getDeltaMovement().x * this.getDeltaMovement().x + this.getDeltaMovement().z * this.getDeltaMovement().z)) * 180.0D / Math.PI));

        if (this.targetedEntity != null && this.targetedEntity.distanceToSqr(this) < this.attackRange * this.attackRange) {
            if (this.wasNearGround || this.canFly) {
                this.waypointX = this.targetedEntity.getX();
                this.waypointY = this.targetedEntity.getY();
                this.waypointZ = this.targetedEntity.getZ();

                if (this.random.nextInt(80) == 0 && this.getY() > this.surfaceY && !this.isCourseTraversable()) {
                    this.wasNearGround = false;
                }
            } else {
                this.waypointX = this.targetedEntity.getX();
                this.waypointY = 10.0D;
                this.waypointZ = this.targetedEntity.getZ();

                if (this.getY() < 15.0D) {
                    this.wasNearGround = true;
                }
            }
        }
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    private void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }
}