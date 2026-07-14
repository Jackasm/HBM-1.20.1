package com.hbm.entity.mob.glyphid;

import com.hbm.entity.logic.EntityWaypoint;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.BlockAllocatorStandard;
import com.hbm.explosion.vanillant.standard.BlockProcessorStandard;
import com.hbm.explosion.vanillant.standard.EntityProcessorStandard;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EntityGlyphidNuclear extends EntityGlyphid {
    public int deathTicks;

    public EntityGlyphidNuclear(EntityType<? extends EntityGlyphidNuclear> type, Level world) {
        super(type, world);
        this.setInvulnerable(true);
    }

    @Override
    public float getScale() { return 2.0F; }


    @Override
    public GlyphidStats.StatBundle getStats() {
        return GlyphidStats.getStats().getNuclear();
    }

    @Override
    public boolean doesInfectedSpawnMaggots() { return false; }

    @Override
    public boolean isArmorBroken(float amount) {
        return this.random.nextInt(100) <= Math.min(Math.pow(amount * 0.12, 2), 100);
    }

    @Override
    public void aiStep() {
        super.aiStep();

        if (this.tickCount % 20 == 0) {
            if (isAtDestination() && getCurrentTask() == TASK_FOLLOW) {
                setCurrentTask(TASK_IDLE, null);
            }

            if (getCurrentTask() == TASK_TERRAFORM) {
                this.setHealth(0);
            }
        }
    }

    @Override
    public void communicate(int task, @Nullable EntityWaypoint waypoint) {
        int radius = waypoint != null ? waypoint.radius : 4;
        AABB bb = this.getBoundingBox().inflate(radius);

        List<Entity> bugs = this.level().getEntities(this, bb);
        for (Entity e : bugs) {
            if (e instanceof EntityGlyphidScout && ((EntityGlyphid) e).getCurrentTask() != task) {
                ((EntityGlyphid) e).setCurrentTask(task, waypoint);
            }
        }
    }

    @Override
    protected void tickDeath() {
        ++this.deathTicks;

        if (!this.level().isClientSide) {
            if (deathTicks == 1) {
                communicate(TASK_INITIATE_RETREAT, null);
            }

            if (deathTicks == 90) {
                int radius = 8;
                AABB bb = this.getBoundingBox().inflate(radius);
                List<Entity> bugs = this.level().getEntities(this, bb);
                for (Entity e : bugs) {
                    if (e instanceof EntityGlyphid glyphid) {
                        glyphid.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20, 6));
                        glyphid.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 300, 1));
                    }
                }
            }

            if (deathTicks == 100) {
                ExplosionVNT vnt = new ExplosionVNT(
                        this.level(),
                        this.getX(), this.getY(), this.getZ(),
                        25, this
                );
                vnt.setBlockAllocator(new BlockAllocatorStandard(24));
                vnt.setBlockProcessor(new BlockProcessorStandard().setNoDrop());
                vnt.setEntityProcessor(new EntityProcessorStandard());
                vnt.setPlayerProcessor(new PlayerProcessorStandard());
                vnt.explode();

                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.GENERIC_EXPLODE, this.getSoundSource(), 15.0F, 1.0F);

                this.discard();
            } else if (this.deathTicks % 10 == 0) {
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(),
                        SoundEvents.NOTE_BLOCK_PLING.get(), this.getSoundSource(), 5.0F, 1.0F);
            }
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return EntityGlyphid.createAttributes()
                .add(Attributes.MAX_HEALTH, 100.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.ATTACK_DAMAGE, 50.0D);
    }
}