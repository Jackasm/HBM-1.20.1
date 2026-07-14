package com.hbm.entity.mob.glyphid;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class EntityGlyphidDigger extends EntityGlyphid {
    public EntityGlyphidDigger(EntityType<? extends EntityGlyphidDigger> type, Level world) {
        super(type, world);
    }

    @Override
    public float getScale() { return 1.3F; }


    @Override
    public GlyphidStats.StatBundle getStats() {
        return GlyphidStats.getStats().getDigger();
    }

    @Override
    public boolean canDig() { return true; }

    @Override
    public boolean isArmorBroken(float amount) {
        return this.random.nextInt(100) <= Math.min(Math.pow(amount * 0.25, 2), 100);
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityGlyphid.createAttributes()
                .add(Attributes.MAX_HEALTH, 50.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.ATTACK_DAMAGE, 10.0D);
    }
}
