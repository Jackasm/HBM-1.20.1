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

public class EntityAIMaskmanLasergun extends Goal {

    private final Mob owner;
    private LivingEntity target;
    private EnumLaserAttack attack;
    private int timer;
    private int attackCount;

    public EntityAIMaskmanLasergun(Mob owner, boolean checkSight, boolean nearbyOnly) {
        this.owner = owner;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
        this.attack = EnumLaserAttack.values()[owner.getRandom().nextInt(3)];
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
            return dist > 10;
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
            timer = attack.delay;

            switch (attack) {
                case ORB:
                    // Создаем Orb с помощью XFactoryMobs
                    Vec3 orbPos = new Vec3(this.owner.getX(), this.owner.getY() + 0.5, this.owner.getZ());
                    Vec3 orbHeading = new Vec3(
                            this.target.getX() - this.owner.getX(),
                            this.target.getY() + this.target.getBbHeight() / 2 - this.owner.getY() - 0.5,
                            this.target.getZ() - this.owner.getZ()
                    ).normalize();

                    EntityBulletBaseMK4 orb = XFactoryMobs.createBullet(
                            this.owner.level(),
                            this.owner,
                            BulletConfigRegistry.MASKMAN_ORB,
                            orbPos.x, orbPos.y, orbPos.z,
                            orbHeading, 2.0F
                    );
                    orb.setDeltaMovement(orb.getDeltaMovement().add(0, 0.5, 0));
                    this.owner.level().addFreshEntity(orb);
                    this.owner.level().playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(),
                            SoundEvents.BEACON_AMBIENT, SoundSource.HOSTILE, 1.0F, 1.0F);
                    break;

                case MISSILE:
                    Vec3 missilePos = new Vec3(this.owner.getX(), this.owner.getY() - 0.5, this.owner.getZ());
                    Vec3 missileHeading = new Vec3(
                            this.target.getX() - this.owner.getX(),
                            this.target.getY() + this.target.getBbHeight() / 2 - this.owner.getY() - 0.5,
                            this.target.getZ() - this.owner.getZ()
                    ).normalize();

                    EntityBulletBaseMK4 missile = XFactoryMobs.createBullet(
                            this.owner.level(),
                            this.owner,
                            BulletConfigRegistry.MASKMAN_ROCKET,
                            missilePos.x, missilePos.y, missilePos.z,
                            missileHeading, 0.05F
                    );
                    // Добавляем вертикальный компонент
                    missile.setDeltaMovement(
                            missile.getDeltaMovement().x,
                            0.5 + this.owner.getRandom().nextDouble() * 0.5,
                            missile.getDeltaMovement().z
                    );
                    this.owner.level().addFreshEntity(missile);
                    this.owner.level().playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(),
                            SoundEvents.FIREWORK_ROCKET_LAUNCH, SoundSource.HOSTILE, 1.0F, 1.0F);
                    break;

                case SPLASH:
                    for (int i = 0; i < 5; i++) {
                        Vec3 splashPos = new Vec3(this.owner.getX(), this.owner.getY() + 0.5, this.owner.getZ());
                        Vec3 splashHeading = new Vec3(
                                this.target.getX() - this.owner.getX() + (this.owner.getRandom().nextGaussian() * 0.5),
                                this.target.getY() + this.target.getBbHeight() / 2 - this.owner.getY() - 0.5 +
                                        (this.owner.getRandom().nextGaussian() * 0.5),
                                this.target.getZ() - this.owner.getZ() + (this.owner.getRandom().nextGaussian() * 0.5)
                        ).normalize();

                        EntityBulletBaseMK4 tracer = XFactoryMobs.createBullet(
                                this.owner.level(),
                                this.owner,
                                BulletConfigRegistry.MASKMAN_TRACER,
                                splashPos.x, splashPos.y, splashPos.z,
                                splashHeading, 1.0F
                        );
                        this.owner.level().addFreshEntity(tracer);
                    }
                    this.owner.level().playSound(null, this.owner.getX(), this.owner.getY(), this.owner.getZ(),
                            SoundEvents.ARROW_SHOOT, SoundSource.HOSTILE, 1.0F, 1.0F);
                    break;

                default:
                    break;
            }

            attackCount++;

            if (attackCount >= attack.amount) {
                attackCount = 0;
                int newAtk = attack.ordinal() + this.owner.getRandom().nextInt(EnumLaserAttack.values().length - 1);
                attack = EnumLaserAttack.values()[newAtk % EnumLaserAttack.values().length];
            }
        }

        this.owner.setYRot(this.owner.getYHeadRot());
    }

    private enum EnumLaserAttack {
        ORB(60, 5),
        MISSILE(10, 10),
        SPLASH(40, 3);

        public final int delay;
        public final int amount;

        EnumLaserAttack(int delay, int amount) {
            this.delay = delay;
            this.amount = amount;
        }
    }
}