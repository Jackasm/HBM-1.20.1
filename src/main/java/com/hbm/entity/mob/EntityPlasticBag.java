package com.hbm.entity.mob;

import com.hbm.items.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

/**
 * Мусорный пакет, плавающий в океане. Поведение скопировано со спрута в 1.7.10
 * (это и в оригинале признано: «copy-pasted shit from the squid class»).
 *
 * <p>Пакет ничего не боится: любой урон его уничтожает и роняет пластиковый пакет —
 * предмет {@code plastic_bag}. Дроп в 1.7.10 был «плавучим», чтобы не тонуть в океане;
 * здесь используется обычный {@link net.minecraft.world.entity.item.ItemEntity} —
 * отклонение записано в PR.</p>
 */
public class EntityPlasticBag extends WaterAnimal {

    /** Угол, вокруг которого строится случайное движение; крутит и рендер. */
    public float rotation;
    public float prevRotation;

    private float randomMotionSpeed;
    private float rotationVelocity;
    private float randomMotionVecX;
    private float randomMotionVecY;
    private float randomMotionVecZ;

    public EntityPlasticBag(EntityType<? extends EntityPlasticBag> type, Level level) {
        super(type, level);
        this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
    }

    public static net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder createAttributes() {
        // У EntityWaterMob в 1.7.10 было 10 HP по умолчанию — столько же и здесь.
        return net.minecraft.world.entity.Mob.createMobAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MAX_HEALTH, 10.0D);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
    }

    @Override
    public void aiStep() {
        super.aiStep();

        this.prevRotation = this.rotation;
        this.rotation += this.rotationVelocity;

        if (this.rotation > Mth.TWO_PI) {
            this.rotation -= Mth.TWO_PI;

            if (this.random.nextInt(10) == 0) {
                this.rotationVelocity = 1.0F / (this.random.nextFloat() + 1.0F) * 0.2F;
            }
        }

        if (this.isInWater()) {
            float f;

            if (this.rotation < Mth.PI) {
                f = this.rotation / Mth.PI;

                if (f > 0.75D) {
                    this.randomMotionSpeed = 0.1F;
                }
            } else {
                this.randomMotionSpeed *= 0.999F;
            }

            if (!this.level().isClientSide) {
                this.setDeltaMovement(this.randomMotionVecX * this.randomMotionSpeed,
                        this.randomMotionVecY * this.randomMotionSpeed,
                        this.randomMotionVecZ * this.randomMotionSpeed);
            }

            float horizontal = Mth.sqrt((float) (this.getDeltaMovement().x * this.getDeltaMovement().x
                    + this.getDeltaMovement().z * this.getDeltaMovement().z));

            this.yBodyRot += (-((float) Mth.atan2(this.getDeltaMovement().x, this.getDeltaMovement().z)) * (180F / Mth.PI)
                    - this.yBodyRot) * 0.1F;
            this.setYRot(this.yBodyRot);
            this.setXRot((float) (Mth.atan2(this.getDeltaMovement().y, horizontal) * 180.0D / Mth.PI));
        } else {
            if (!this.level().isClientSide) {
                this.setDeltaMovement(0.0D, this.getDeltaMovement().y - 0.08D, 0.0D);
                this.setDeltaMovement(this.getDeltaMovement().multiply(1.0D, 0.98D, 1.0D));
            }
        }

        if (this.level().isClientSide && this.isInWater() && this.random.nextInt(12) == 0) {
            this.level().addParticle(ParticleTypes.BUBBLE,
                    this.getX() + (this.random.nextDouble() - 0.5D) * 0.3D,
                    this.getY() + 0.3D,
                    this.getZ() + (this.random.nextDouble() - 0.5D) * 0.3D,
                    0.0D, 0.02D, 0.0D);
        }
    }

    @Override
    public void travel(@NotNull Vec3 input) {
        // Как moveEntityWithHeading в оригинале: пакет просто дрейфует по своему вектору.
        this.move(MoverType.SELF, this.getDeltaMovement());
    }

    @Override
    public void tick() {
        super.tick();

        // Старение и перевыбор случайного вектора — updateEntityActionState оригинала.
        if (this.tickCount > 100) {
            this.randomMotionVecX = this.randomMotionVecY = this.randomMotionVecZ = 0.0F;
        } else if (this.random.nextInt(50) == 0
                || (!this.isInWater()
                    && this.randomMotionVecX == 0.0F && this.randomMotionVecY == 0.0F && this.randomMotionVecZ == 0.0F)) {
            float f = this.random.nextFloat() * Mth.TWO_PI;
            this.randomMotionVecX = Mth.cos(f) * 0.2F;
            this.randomMotionVecY = -0.1F + this.random.nextFloat() * 0.2F;
            this.randomMotionVecZ = Mth.sin(f) * 0.2F;
        }
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (!this.level().isClientSide) {
            // В 1.7.10 пакет умирал от любого урона и всегда ронял пластиковый пакет.
            this.spawnAtLocation(new ItemStack(ModItems.PLASTIC_BAG.get()));
            this.discard();
        }

        return true;
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        // Весь дроп отдаёт hurt(): пакет умирает от любого удара.
    }

    @Override
    public boolean causeFallDamage(float fallDistance, float multiplier, @NotNull DamageSource source) {
        return false;
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
    public boolean isPushedByFluid() {
        return false;
    }
}
