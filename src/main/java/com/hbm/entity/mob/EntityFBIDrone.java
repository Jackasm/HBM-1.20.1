package com.hbm.entity.mob;

import com.hbm.entity.ModEntities;
import com.hbm.entity.grenade.EntityGrenadeStrong;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class EntityFBIDrone extends EntityUFOBase {

    private int attackCooldown;

    public EntityFBIDrone(EntityType<? extends EntityFBIDrone> type, Level level) {
        super(type, level);
    }

    public EntityFBIDrone(Level level) {
        this(ModEntities.FBI_DRONE.get(), level);
    }

    @Override
    protected void customServerAiStep() {
        super.customServerAiStep();

        if (this.courseChangeCooldown > 0) this.courseChangeCooldown--;
        if (this.scanCooldown > 0) this.scanCooldown--;

        if (!this.level().isClientSide) {
            if (attackCooldown > 0) attackCooldown--;

            if (this.target != null && attackCooldown <= 0) {
                Vec3 vec = new Vec3(
                        this.getX() - this.target.getX(),
                        this.getY() - this.target.getY(),
                        this.getZ() - this.target.getZ()
                );

                if (Math.abs(vec.x) < 5 && Math.abs(vec.z) < 5 && vec.y > 3) {
                    attackCooldown = 60;
                    EntityGrenadeStrong grenade = new EntityGrenadeStrong(ModEntities.GRENADE_STRONG.get(), this.level());
                    grenade.setPos(this.getX(), this.getY(), this.getZ());
                    this.level().addFreshEntity(grenade);
                }
            }
        }

        if (this.courseChangeCooldown > 0) {
            approachPosition(this.target == null ? 0.25D : 0.5D);
        }
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 35.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.3D)
                .add(Attributes.FOLLOW_RANGE, 100.0D);
    }

    @Override
    protected int getScanRange() {
        return 100;
    }

    @Override
    protected int targetHeightOffset() {
        return 7 + this.random.nextInt(4);
    }

    @Override
    protected int wanderHeightOffset() {
        return 7 + this.random.nextInt(4);
    }
}