package com.hbm.entity.effect;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityVortex extends EntityBlackHole {

    public EntityVortex(EntityType<?> type, Level level) {
        super(type, level);
    }

    public EntityVortex(EntityType<?> type, Level level, float size) {
        super(type, level, size);
    }

    @Override
    public void tick() {
        float currentSize = this.entityData.get(SIZE);
        this.entityData.set(SIZE, currentSize - 0.0025F);

        if (this.entityData.get(SIZE) <= 0) {
            this.discard();
            return;
        }

        super.tick();
    }
}