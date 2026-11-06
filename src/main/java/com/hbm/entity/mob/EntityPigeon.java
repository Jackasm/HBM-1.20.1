package com.hbm.entity.mob;

import com.hbm.entity.mob.ai.EntityAIEatBread;
import com.hbm.items.tool.ItemFertilizer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Голубь из 1.7.10: ходит по земле, клюёт хлеб, от него жиреет и удобряет землю,
 * а при испуге (атака, огонь или просто раз в ~30 секунд) взлетает.
 *
 * <p>Состояние полёта и жирность — синхронизируемые данные, как dataWatcher в оригинале.
 * Поля {@code fallTime}/{@code dest} крутят взмах крыла в модели и тоже обновляются
 * с обеих сторон, иначе анимация дёргается.</p>
 */
public class EntityPigeon extends PathfinderMob implements IFlyingCreature {

    private static final EntityDataAccessor<Integer> DATA_FLYING_STATE =
            SynchedEntityData.defineId(EntityPigeon.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Boolean> DATA_FAT =
            SynchedEntityData.defineId(EntityPigeon.class, EntityDataSerializers.BOOLEAN);

    /** Взмах крыла: интерполируется между предыдущим и текущим тиком в модели. */
    public float fallTime;
    public float dest;
    public float prevDest;
    public float prevFallTime;
    public float offGroundTimer = 1.0F;

    public EntityPigeon(EntityType<? extends EntityPigeon> type, Level level) {
        super(type, level);
    }

    public static AttributeSupplier.Builder createAttributes() {
        // В 1.7.10 у EntityCreature по умолчанию 10 HP и скорость 0.25 — оставляем как было.
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 10.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_FLYING_STATE, STATE_WALKING);
        this.entityData.define(DATA_FAT, false);
    }

    @Override
    protected void registerGoals() {
        // Порядок как в 1.7.10: взлёт и посадка нулевыми, плавание только на земле,
        // потом еда, потом блуждание (тоже только на земле).
        this.goalSelector.addGoal(0, new StartFlyingGoal(this));
        this.goalSelector.addGoal(0, new StopFlyingGoal(this));
        this.goalSelector.addGoal(1, new WalkingOnlyFloatGoal(this));
        this.goalSelector.addGoal(2, new EntityAIEatBread(this, 0.4D));
        this.goalSelector.addGoal(5, new WalkingOnlyWanderGoal(this, 0.2D));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(7, new RandomLookAroundGoal(this));
    }

    // ---------- Состояние ----------

    @Override
    public int getFlyingState() {
        return this.entityData.get(DATA_FLYING_STATE);
    }

    @Override
    public void setFlyingState(int state) {
        this.entityData.set(DATA_FLYING_STATE, state);
    }

    public boolean isFat() {
        return this.entityData.get(DATA_FAT);
    }

    public void setFat(boolean fat) {
        this.entityData.set(DATA_FAT, fat);
    }

    // ---------- Движение ----------

    @Override
    public void tick() {
        super.tick();

        // Взмах крыла: та же арифметика, что в onLivingUpdate оригинала.
        this.prevFallTime = this.fallTime;
        this.prevDest = this.dest;
        this.dest += (this.onGround() ? -1 : 4) * 0.3F;
        this.dest = Mth.clamp(this.dest, 0.0F, 1.0F);

        if (!this.onGround() && this.offGroundTimer < 1.0F) {
            this.offGroundTimer = 1.0F;
        }
        this.offGroundTimer = (float) (this.offGroundTimer * 0.9D);
        this.fallTime += this.offGroundTimer * 2.0F;
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.getFlyingState() == STATE_FLYING) {
            if (!this.level().isClientSide) {
                int ground = this.level().getHeight(Heightmap.Types.MOTION_BLOCKING,
                        Mth.floor(this.getX()), Mth.floor(this.getZ()));
                boolean ceil = this.getY() - ground > 10;

                double motionY = this.random.nextGaussian() * 0.05D
                        + (ceil ? 0 : 0.04D)
                        + (this.isInWater() ? 0.2D : 0);
                if (this.onGround()) {
                    motionY = Math.abs(motionY) + 0.1D;
                }

                Vec3 look = this.getLookAngle();
                this.setDeltaMovement(look.x * 0.35D, motionY, look.z * 0.35D);

                if (this.random.nextInt(20) == 0) {
                    this.setYRot(this.getYRot() + (float) this.random.nextGaussian() * 30F);
                }

                if (this.isFat() && this.random.nextInt(50) == 0) {
                    this.fertilizeBelow();
                }
            }
        } else if (!this.onGround() && this.getDeltaMovement().y < 0) {
            this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.8D, 1.0D));
        }
    }

    @Override
    public void travel(@NotNull Vec3 input) {
        if (this.getFlyingState() == STATE_FLYING) {
            // В полёте голубь не «идёт»: движение задаётся напрямую, как в оригинале
            // через moveEntityWithHeading.
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.91D, 0.98D, 0.91D));
            return;
        }
        super.travel(input);
    }

    /** «Жирный» голубь удобряет блок под собой — единственный источник бесплатного bonemeal в моде. */
    private void fertilizeBelow() {
        if (this.level().isClientSide) return;

        int x = Mth.floor(this.getX());
        int z = Mth.floor(this.getZ());
        int startY = Mth.floor(this.getY()) - 1;

        if (this.level() instanceof net.minecraft.server.level.ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.SPLASH, this.getX(), this.getY(), this.getZ(), 3, 0.2, 0.2, 0.2, 0.0);
        }

        for (int i = 0; i < 25; i++) {
            if (ItemFertilizer.fertilize(this.level(), new BlockPos(x, startY - i, z), null, true)) {
                this.level().levelEvent(2005, new BlockPos(x, startY - i, z), 0);
                break;
            }
        }

        if (this.random.nextInt(10) == 0) {
            this.setFat(false);
        }
    }

    // ---------- Урон и дроп ----------

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        // Сильный удар разносит голубя в перья, как в оригинале.
        if (amount >= this.getMaxHealth() * 2.0F && !this.level().isClientSide) {
            this.discard();

            for (int i = 0; i < 10; i++) {
                Vec3 vec = new Vec3(this.random.nextGaussian(), this.random.nextGaussian(), this.random.nextGaussian())
                        .normalize();

                ItemEntity feather = new ItemEntity(this.level(),
                        this.getX() + vec.x, this.getY() + this.getBbHeight() / 2.0D + vec.y, this.getZ() + vec.z,
                        new ItemStack(Items.FEATHER));
                feather.setDeltaMovement(vec.x * 0.5D, vec.y * 0.5D, vec.z * 0.5D);
                this.level().addFreshEntity(feather);
            }

            return true;
        }

        return super.hurt(source, amount);
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        int feathers = this.random.nextInt(3) + this.random.nextInt(1 + looting);
        for (int i = 0; i < feathers; i++) {
            this.spawnAtLocation(Items.FEATHER);
        }

        int meat = this.isFat() ? 3 : 1;
        if (this.isOnFire()) {
            this.spawnAtLocation(Items.COOKED_CHICKEN, meat);
        } else {
            this.spawnAtLocation(Items.CHICKEN, meat);
        }
    }

    // ---------- Мелочи поведения ----------

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, @NotNull net.minecraft.world.level.block.state.BlockState state,
                                   @NotNull BlockPos pos) {
        // Голубь не получает урон от падения (в оригинале fall() пустой).
    }

    @Override
    public boolean isIgnoringBlockTriggers() {
        return true;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return null;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return null;
    }

    @Override
    protected void playStepSound(@NotNull BlockPos pos, @NotNull net.minecraft.world.level.block.state.BlockState state) {
        this.playSound(SoundEvents.CHICKEN_STEP, 0.15F, 1.0F);
    }

    // ---------- Цели ----------

    /** Взлетает, если его ударили, он горит или просто раз в ~30 секунд. */
    private static class StartFlyingGoal extends Goal {
        private final EntityPigeon pigeon;

        private StartFlyingGoal(EntityPigeon pigeon) {
            this.pigeon = pigeon;
        }

        @Override
        public boolean canUse() {
            return this.pigeon.getFlyingState() == STATE_WALKING
                    && (this.pigeon.getLastHurtByMob() != null
                        || this.pigeon.isOnFire()
                        || this.pigeon.getRandom().nextInt(600) == 0);
        }

        @Override
        public void start() {
            this.pigeon.setFlyingState(STATE_FLYING);
        }
    }

    /** Садится обратно случайно. */
    private static class StopFlyingGoal extends Goal {
        private final EntityPigeon pigeon;

        private StopFlyingGoal(EntityPigeon pigeon) {
            this.pigeon = pigeon;
        }

        @Override
        public boolean canUse() {
            return this.pigeon.getFlyingState() == STATE_FLYING && this.pigeon.getRandom().nextInt(200) == 0;
        }

        @Override
        public void start() {
            this.pigeon.setFlyingState(STATE_WALKING);
        }
    }

    /** Плавание разрешено только на земле — в полёте оно мешало бы набирать высоту. */
    private static class WalkingOnlyFloatGoal extends FloatGoal {
        private final EntityPigeon pigeon;

        private WalkingOnlyFloatGoal(EntityPigeon pigeon) {
            super(pigeon);
            this.pigeon = pigeon;
        }

        @Override
        public boolean canUse() {
            return this.pigeon.getFlyingState() == STATE_WALKING && super.canUse();
        }
    }

    /** Блуждание тоже только пешком: в полёте траекторию задаёт aiStep. */
    private static class WalkingOnlyWanderGoal extends WaterAvoidingRandomStrollGoal {
        private final EntityPigeon pigeon;

        private WalkingOnlyWanderGoal(EntityPigeon pigeon, double speed) {
            super(pigeon, speed);
            this.pigeon = pigeon;
        }

        @Override
        public boolean canUse() {
            return this.pigeon.getFlyingState() == STATE_WALKING && super.canUse();
        }

        @Override
        public boolean canContinueToUse() {
            return this.pigeon.getFlyingState() == STATE_WALKING && super.canContinueToUse();
        }
    }
}
