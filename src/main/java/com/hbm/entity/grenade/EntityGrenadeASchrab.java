package com.hbm.entity.grenade;

import com.hbm.config.BombConfig;
import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityCloudFleija;
import com.hbm.entity.logic.EntityNukeExplosionMK3;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadeASchrab extends EntityGrenadeBase {

    public EntityGrenadeASchrab(EntityType<? extends EntityGrenadeBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadeASchrab(EntityType<? extends EntityGrenadeBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadeASchrab(EntityType<? extends EntityGrenadeBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide) {
            EntityNukeExplosionMK3 explosion = EntityNukeExplosionMK3.statFacFleija(
                    (ServerLevel) this.level(), this.getX(), this.getY(), this.getZ(), BombConfig.A_SCHRAB_RADIUS);

            if (!explosion.isRemoved()) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.GENERIC_EXPLODE, SoundSource.HOSTILE,
                        100.0F, this.level().random.nextFloat() * 0.1F + 0.9F);
                this.level().addFreshEntity(explosion);

                EntityCloudFleija cloud = new EntityCloudFleija(ModEntities.CLOUD_FLEIJA.get(), this.level());
                cloud.setPos(this.getX(), this.getY(), this.getZ());
                cloud.setScale(BombConfig.A_SCHRAB_RADIUS);
                this.level().addFreshEntity(cloud);
            }

            this.discard();
        }
    }
}