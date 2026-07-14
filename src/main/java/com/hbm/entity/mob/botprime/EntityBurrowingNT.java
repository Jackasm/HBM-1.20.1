package com.hbm.entity.mob.botprime;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public abstract class EntityBurrowingNT extends Mob {

    protected float dragInAir;
    protected float dragInGround;

    public EntityBurrowingNT(EntityType<? extends Mob> type, Level level) {
        super(type, level);
    }

    protected void fall(float dist) {
    }

    public boolean getIsHead() {
        return false;
    }

    @Override
    protected void checkFallDamage(double y, boolean onGround, net.minecraft.world.level.block.state.@NotNull BlockState state, net.minecraft.core.@NotNull BlockPos pos) {
    }

    @Override
    public void travel(@NotNull Vec3 travelVector) {
        float drag = this.dragInGround;

        if (!this.isInWall() && !this.isInWater() && !this.isInLava()) {
            drag = this.dragInAir;
        }

        if (!getIsHead()) {
            drag *= 0.9F;
        }

        // Применяем движение
        super.travel(travelVector);

        // Замедление
        this.setDeltaMovement(this.getDeltaMovement().multiply(drag, drag, drag));
    }

    @Override
    public boolean onClimbable() {
        return false;
    }
}