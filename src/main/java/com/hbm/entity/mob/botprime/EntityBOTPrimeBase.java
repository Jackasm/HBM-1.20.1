package com.hbm.entity.mob.botprime;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.sound.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class EntityBOTPrimeBase extends EntityWormBaseNT {

    public int attackCounter = 0;

    public EntityBOTPrimeBase(EntityType<? extends EntityBOTPrimeBase> type, Level level) {
        super(type, level);
        this.setSize(2.0F, 2.0F);
        this.setInvulnerable(false);
        this.setNoGravity(true);
        this.noPhysics = true;
        this.dragInAir = 0.995F;
        this.dragInGround = 0.98F;
        this.knockbackDivider = 1.0D;
    }

    public boolean canEntityBeSeenThroughNonSolids(Entity entity) {
        if (entity == null) return false;
        Vec3 start = new Vec3(this.getX(), this.getY() + (double) this.getEyeHeight(), this.getZ());
        Vec3 end = new Vec3(entity.getX(), entity.getY() + (double) entity.getEyeHeight(), entity.getZ());
        return this.level().clip(new net.minecraft.world.level.ClipContext(
                start, end,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE,
                this
        )).getType() == net.minecraft.world.phys.HitResult.Type.MISS;
    }

    @Override
    protected SoundEvent getHurtSound(@NotNull DamageSource source) {
        return net.minecraft.sounds.SoundEvents.BLAZE_HURT;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return ModSounds.BOMB_DET.get();
    }

    protected void laserAttack(Entity target, boolean head) {
        if (!(target instanceof LivingEntity living)) return;

        if (head) {
            for (int i = 0; i < 5; i++) {
                EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(
                        this.level(),
                        this,
                        BulletConfigRegistry.WORM_LASER,
                        getAttackStrength(target),
                        0F,
                        0D,
                        0D,
                        0D
                );
                this.level().addFreshEntity(bullet);
            }
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    net.minecraft.sounds.SoundEvents.BEACON_POWER_SELECT, // Заменить на кастомный позже
                    net.minecraft.sounds.SoundSource.HOSTILE, 5.0F, 0.75F);
        } else {
            EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(
                    this.level(),
                    this,
                    BulletConfigRegistry.WORM_BOLT,
                    getAttackStrength(target),
                    0F,
                    0D,
                    0D,
                    0D
            );
            this.level().addFreshEntity(bullet);
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                    ModSounds.BALLS_LASER.get(), SoundSource.HOSTILE, 5.0F, 0.75F);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 15000.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D);
    }

    private void setSize(float width, float height) {
        this.setBoundingBox(this.getBoundingBox().setMinX(-width / 2).setMinY(0).setMinZ(-width / 2)
                .setMaxX(width / 2).setMaxY(height).setMaxZ(width / 2));
    }
}