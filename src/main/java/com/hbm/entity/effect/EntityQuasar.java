package com.hbm.entity.effect;

import com.hbm.entity.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityQuasar extends EntityBlackHole {

    public EntityQuasar(EntityType<? extends EntityQuasar> type, Level level) {
        super(type, level);
        this.setInvulnerable(true);
    }

    public EntityQuasar(Level level, float size) {
        super(ModEntities.QUASAR.get(), level, size);
        this.setInvulnerable(true);
    }

    @Override
    public void tick() {
        super.tick();
    }
}