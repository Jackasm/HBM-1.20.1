package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.factory.ConfettiUtil;
import com.hbm.util.EntityDamageUtil;

import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.damagesource.DamageSource;

public class EntityProcessorCrossSmooth extends EntityProcessorCross {

    protected float fixedDamage;
    protected float pierceDT = 0;
    protected float pierceDR = 0;
    protected BulletConfig.DamageClass clazz = BulletConfig.DamageClass.EXPLOSIVE;
    protected double knockbackMultiplier = 1.0D;

    public EntityProcessorCrossSmooth(double nodeDist, float fixedDamage) {
        super(nodeDist);
        this.fixedDamage = fixedDamage;
        this.allowSelfDamage = true; // Разрешаем урон самому себе
    }

    public EntityProcessorCrossSmooth setupPiercing(float pierceDT, float pierceDR) {
        this.pierceDT = pierceDT;
        this.pierceDR = pierceDR;
        return this;
    }

    public EntityProcessorCrossSmooth setDamageClass(BulletConfig.DamageClass clazz) {
        this.clazz = clazz;
        return this;
    }

    public EntityProcessorCrossSmooth setKnockback(double multiplier) {
        this.knockbackMultiplier = multiplier;
        return this;
    }

    @Override
    public void attackEntity(Entity entity, ExplosionVNT source, float amount) {
        if(!entity.isAlive()) return;

        // Уменьшаем урон самому себе
        if(source.exploder == entity) amount *= 0.5F;

        // Создаем DamageSource в зависимости от класса урона
        RegistryAccess registryAccess = source.world.registryAccess();
        DamageSource dmg = BulletConfig.getDamage(registryAccess,
                source.exploder instanceof LivingEntity ? (LivingEntity) source.exploder : null,
                clazz);

        if(!(entity instanceof LivingEntity)) {
            entity.hurt(dmg, amount);
        } else {
            EntityDamageUtil.attackEntityFromNT((LivingEntity) entity, dmg, amount, true, 0F, pierceDT, pierceDR);
            if(!entity.isAlive()) ConfettiUtil.decideConfetti((LivingEntity) entity, dmg);
        }
    }

    @Override
    public float calculateDamage(double distanceScaled, double density, double knockback, float size) {
        if(density < 0.125) return 0; // Простой хак для низкой плотности
        return (float) (fixedDamage * (1 - distanceScaled));
    }
}