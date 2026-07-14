package com.hbm.entity.effect;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class EntityRagingVortex extends EntityBlackHole {

    private int timer = 0;

    public EntityRagingVortex(EntityType<?> type, Level level) {
        super(type, level);
    }

    public EntityRagingVortex(EntityType<?> type, Level level, float size) {
        super(type, level, size);
    }

    @Override
    public void tick() {
        timer++;

        if (timer > 20) {
            timer -= 20;
        }

        float pulse = (float) (Math.sin(timer) * Math.PI / 20D) * 0.35F;

        float dec = 0.0F;

        if (random.nextInt(100) == 0) {
            dec = 0.1F;
            this.level().explode(null, this.getX(), this.getY(), this.getZ(), 10.0F, false, Level.ExplosionInteraction.MOB);
        }

        float currentSize = this.entityData.get(SIZE);
        this.entityData.set(SIZE, currentSize - pulse - dec);

        if (this.entityData.get(SIZE) <= 0) {
            this.discard();
            return;
        }

        super.tick();
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.timer = tag.getInt("vortexTimer");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("vortexTimer", this.timer);
    }
}