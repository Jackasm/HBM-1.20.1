package com.hbm.entity.grenade;

import com.hbm.explosion.ExplosionChaos;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EntityGrenadePC extends EntityGrenadeBase {

    public EntityGrenadePC(EntityType<? extends EntityGrenadeBase> type, Level level) {
        super(type, level);
    }

    public EntityGrenadePC(EntityType<? extends EntityGrenadeBase> type, Level level, LivingEntity thrower) {
        super(type, level, thrower);
    }

    public EntityGrenadePC(EntityType<? extends EntityGrenadeBase> type, Level level, double x, double y, double z) {
        super(type, level, x, y, z);
    }

    @Override
    public void explode() {
        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            this.discard();

            BlockPos pos = BlockPos.containing(this.getX(), this.getY(), this.getZ());

            // playAuxSFX заменён на levelEvent
            serverLevel.levelEvent(2002, pos, 0);

            ExplosionChaos.spawnPoisonCloud(serverLevel, this.getX(), this.getY(), this.getZ(), 500, 2, 2);
        }
    }
}