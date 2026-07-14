package com.hbm.entity.mob.glyphid;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;

public class EntityGlyphidBlaster extends EntityGlyphidBombardier {

    public EntityGlyphidBlaster(EntityType<? extends EntityGlyphidBlaster> type, Level world) {
        super(type, world);
    }

    @Override
    public float getScale() { return 1.25F; }

    @Override
    public GlyphidStats.StatBundle getStats() {
        return GlyphidStats.getStats().getBlaster();
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityGlyphid.createAttributes()
                .add(Attributes.MAX_HEALTH, GlyphidStats.getStats().getBlaster().health)
                .add(Attributes.MOVEMENT_SPEED, GlyphidStats.getStats().getBlaster().speed)
                .add(Attributes.ATTACK_DAMAGE, GlyphidStats.getStats().getBlaster().damage);
    }

    @Override
    public boolean isArmorBroken(float amount) {
        return this.random.nextInt(100) <= Math.min(Math.pow(amount * 0.25, 2), 100);
    }

    @Override
    public float getBombDamage() {
        return 15F;
    }

    @Override
    public int getBombCount() {
        return 10;
    }

    @Override
    public float getSpreadMult() {
        return 0.5F;
    }

    @Override
    public double getV0() {
        return 1.25D;
    }
}