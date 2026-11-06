package com.hbm.entity.mob.ai;

import com.hbm.entity.mob.EntityPigeon;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Items;

import java.util.EnumSet;
import java.util.List;

/**
 * Голубь идёт к лежащему хлебу и клюёт его, пока не станет жирным.
 *
 * <p>Порт {@code EntityAIEatBread} из 1.7.10: там таск искал {@link ItemEntity}
 * с хлебом в радиусе 10 блоков и на третьем тике «съедал» его, ставя флаг жирности.
 * Жирный голубь потом удобряет землю — см. {@link EntityPigeon}.</p>
 */
public class EntityAIEatBread extends Goal {

    private final EntityPigeon pigeon;
    private final double speed;
    private ItemEntity item;

    public EntityAIEatBread(EntityPigeon pigeon, double speed) {
        this.pigeon = pigeon;
        this.speed = speed;
        this.setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (this.pigeon.isFat() || this.pigeon.getFlyingState() != EntityPigeon.STATE_WALKING) {
            return false;
        }

        List<ItemEntity> items = this.pigeon.level().getEntitiesOfClass(
                ItemEntity.class, this.pigeon.getBoundingBox().inflate(10.0D),
                entity -> entity.getItem().is(Items.BREAD));

        if (items.isEmpty()) {
            return false;
        }

        this.item = items.get(0);
        return true;
    }

    @Override
    public boolean canContinueToUse() {
        return this.item != null && this.item.isAlive() && this.canUse();
    }

    @Override
    public void start() {
        // Ничего: цель удержания уже найдена в canUse.
    }

    @Override
    public void tick() {
        if (this.item == null) return;

        this.pigeon.getLookControl().setLookAt(this.item, 30.0F, (float) this.pigeon.getMaxHeadXRot());

        if (this.pigeon.distanceToSqr(this.item) > 1.0D) {
            this.pigeon.getNavigation().moveTo(this.item, this.speed);
            return;
        }

        this.pigeon.getNavigation().stop();

        if (this.pigeon.getRandom().nextInt(3) != 0) {
            return;
        }

        this.item.getItem().shrink(1);
        if (this.item.getItem().isEmpty()) {
            this.item.discard();
        }

        this.pigeon.setFat(true);
        this.pigeon.playSound(SoundEvents.GENERIC_EAT,
                0.5F + 0.5F * this.pigeon.getRandom().nextInt(2),
                (this.pigeon.getRandom().nextFloat() - this.pigeon.getRandom().nextFloat()) * 0.2F + 1.0F);
    }

    @Override
    public void stop() {
        this.item = null;
        this.pigeon.getNavigation().stop();
    }
}
