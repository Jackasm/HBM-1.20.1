package com.hbm.entity.mob.glyphid;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class EntityGlyphidBrawler extends EntityGlyphid {

    public int timer = 0;
    protected Entity lastTarget;
    protected double lastX;
    protected double lastY;
    protected double lastZ;

    public EntityGlyphidBrawler(EntityType<? extends EntityGlyphidBrawler> type, Level world) {
        super(type, world);
    }

    @Override
    public float getScale() { return 1.25F; }

    @Override
    public GlyphidStats.StatBundle getStats() {
        return GlyphidStats.getStats().getBrawler();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityGlyphid.createAttributes()
                .add(Attributes.MAX_HEALTH, GlyphidStats.getStats().getBrawler().health)
                .add(Attributes.MOVEMENT_SPEED, GlyphidStats.getStats().getBrawler().speed)
                .add(Attributes.ATTACK_DAMAGE, GlyphidStats.getStats().getBrawler().damage);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        Entity e = this.getTarget();
        if (e != null && this.isAlive()) {

            this.lastX = e.getX();
            this.lastY = e.getY();
            this.lastZ = e.getZ();

            if (--timer <= 0) {
                leap();
                timer = 80 + this.random.nextInt(30);
            }
        }
    }

    public void leap() {
        if (!this.level().isClientSide && this.getTarget() instanceof LivingEntity && this.distanceTo(this.getTarget()) < 20) {
            Entity e = this.getTarget();

            double velX = e.getX() - lastX;
            double velY = e.getY() - lastY;
            double velZ = e.getZ() - lastZ;

            if (this.lastTarget != e) {
                velX = velY = velZ = 0;
            }

            int prediction = 60;
            Vec3 delta = new Vec3(
                    e.getX() - this.getX() + velX * prediction,
                    (e.getY() + e.getBbHeight() / 2) - (this.getY() + 1) + velY * prediction,
                    e.getZ() - this.getZ() + velZ * prediction
            );
            double len = delta.length();
            if (len < 3) return;
            double targetYaw = -Math.atan2(delta.x, delta.z);

            double x = Math.sqrt(delta.x * delta.x + delta.z * delta.z);
            double y = delta.y;
            double v0 = 1.5;
            double v02 = v0 * v0;
            double g = 0.01;
            double targetPitch = Math.atan((v02 + Math.sqrt(v02 * v02 - g * (g * x * x + 2 * y * v02)) * 1) / (g * x));
            Vec3 fireVec = null;
            if (!Double.isNaN(targetPitch)) {
                fireVec = new Vec3(v0, 0, 0);
                fireVec = fireVec.xRot((float) (-targetPitch / 3.5));
                fireVec = fireVec.yRot((float) -(targetYaw + Math.PI * 0.5));
            }
            if (fireVec != null) {
                this.setThrowableHeading(fireVec.x, fireVec.y, fireVec.z, (float) v0, this.random.nextFloat());
            }
        }
    }

    public void setThrowableHeading(double motionX, double motionY, double motionZ, float velocity, float inaccuracy) {
        float throwLen = Mth.sqrt((float) (motionX * motionX + motionY * motionY + motionZ * motionZ));
        motionX /= throwLen;
        motionY /= throwLen;
        motionZ /= throwLen;
        motionX += this.random.nextGaussian() * 0.0075D * inaccuracy;
        motionY += this.random.nextGaussian() * 0.0075D * inaccuracy;
        motionZ += this.random.nextGaussian() * 0.0075D * inaccuracy;
        motionX *= velocity;
        motionY *= velocity;
        motionZ *= velocity;
        this.setDeltaMovement(motionX, motionY, motionZ);
        float hyp = Mth.sqrt((float) (motionX * motionX + motionZ * motionZ));
        this.setYRot((float) (Math.atan2(motionX, motionZ) * 180.0D / Math.PI));
        this.setXRot((float) (Math.atan2(motionY, hyp) * 180.0D / Math.PI));
        this.yRotO = this.getYRot();
        this.xRotO = this.getXRot();
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        if (source.is(DamageTypes.FALL) && amount <= 10) return false;
        return super.hurt(source, amount);
    }

    @Override
    public boolean isArmorBroken(float amount) {
        return this.random.nextInt(100) <= Math.min(Math.pow(amount * 0.25, 2), 100);
    }
}