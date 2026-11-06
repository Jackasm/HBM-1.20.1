package com.hbm.entity.mob.ai;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.items.weapon.sedna.factory.XFactoryMobs;
import com.hbm.sound.ModSounds;
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
        this.setFlags(EnumSet.of(Flag.LOOK));
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

        this.owner.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        timer--;

        if (timer <= 0) {
            timer = delay;

            // ===== ЛОКАЛЬНОЕ СМЕЩЕНИЕ ОТНОСИТЕЛЬНО МОБА =====
            // localRight:    + вправо от моба, - влево
            // localUp:       + вверх от ног моба
            // localForward:  + вперёд (куда смотрит моб), - назад
            double localRight = -1.0D;   // минус = вправо от моба (подберёшь знак)
            double localUp = 3.0D;       // уровень груди
            double localForward = 0.0D;
            // =================================================

            float yaw = this.owner.getYRot();
            float rad = (float) Math.toRadians(yaw);

            // Вектор "вперёд" (look direction моба)
            double lookX = -Math.sin(rad);
            double lookZ =  Math.cos(rad);

            // Вектор "вправо" (перпендикулярно look, в горизонтальной плоскости)
            double rightX = -Math.cos(rad);
            double rightZ = -Math.sin(rad);

            // Перевод локального смещения в мировые координаты
            double offX = localRight * rightX + localForward * lookX;
            double offY = localUp;
            double offZ = localRight * rightZ + localForward * lookZ;

            double spawnX = this.owner.getX() + offX;
            double spawnY = this.owner.getY() + offY;
            double spawnZ = this.owner.getZ() + offZ;

            Vec3 pos = new Vec3(spawnX, spawnY, spawnZ);
            Vec3 heading = new Vec3(
                    this.target.getX() - spawnX,
                    this.target.getY() + this.target.getBbHeight() / 2 - spawnY,
                    this.target.getZ() - spawnZ
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