package com.hbm.entity.mob.ai;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.items.weapon.sedna.factory.XFactoryMobs;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class EntityAIMaskmanMinigun extends Goal {

    private final Mob owner;
    private LivingEntity target;
    private final int delay;
    private int timer;

    public EntityAIMaskmanMinigun(Mob owner, boolean checkSight, boolean nearbyOnly, int delay) {
        this.owner = owner;
        this.delay = delay;
        this.timer = delay;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        LivingEntity target = this.owner.getTarget();
        if (target == null) {
            return false;
        } else {
            this.target = target;
            double dist = new Vec3(
                    target.getX() - owner.getX(),
                    target.getY() - owner.getY(),
                    target.getZ() - owner.getZ()
            ).length();
            return dist > 5 && dist < 10;
        }
    }

    @Override
    public boolean canContinueToUse() {
        return this.canUse() || !this.owner.getNavigation().isDone();
    }

    @Override
    public void tick() {
        if (this.target == null) return;

        timer--;

        if (timer <= 0) {
            timer = delay;

            Vec3 pos = new Vec3(this.owner.getX(), this.owner.getY() + 0.5, this.owner.getZ());
            Vec3 heading = new Vec3(
                    this.target.getX() - this.owner.getX(),
                    this.target.getY() + this.target.getBbHeight() / 2 - this.owner.getY() - 0.5,
                    this.target.getZ() - this.owner.getZ()
            ).normalize();

            EntityBulletBaseMK4 bullet = XFactoryMobs.createBullet(
                    this.owner.level(),
                    this.owner,
                    BulletConfigRegistry.MASKMAN_BULLET,
                    pos.x, pos.y, pos.z,
                    heading, 1.0F
            );
            this.owner.level().addFreshEntity(bullet);
            this.owner.level().playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(),
                    SoundEvents.DISPENSER_LAUNCH, SoundSource.HOSTILE, 1.0F, 1.0F);
        }

        this.owner.setYRot(this.owner.getYHeadRot());
    }
}