package com.hbm.entity.mob.ai;

import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.target.TargetGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import java.util.function.Predicate;

/**
 * Порт EntityAINearestAttackableTargetNT из 1.7.10.
 * Ключевое отличие от ванильного NearestAttackableTargetGoal — кастомный searchRange,
 * а не FOLLOW_RANGE моба. Позволяет задавать разный радиус обнаружения для разных
 * целей (RAIDER 48, SCOUT 32, AMBUSHER 16 и т.д.).
 */
public class EntityAINearestAttackableTargetNT<T extends LivingEntity> extends TargetGoal {

    private static final int DEFAULT_RANDOM_INTERVAL = 10;

    protected final Class<T> targetType;
    protected final int randomInterval;
    protected final double searchRange;

    @Nullable
    protected LivingEntity target;
    protected TargetingConditions targetConditions;

    /** Сортировка по расстоянию — ближайший первым. */
    private final Comparator<LivingEntity> sorter;

    public EntityAINearestAttackableTargetNT(PathfinderMob mob, Class<T> targetType,
                                             boolean mustSee, boolean mustReach,
                                             double searchRange) {
        this(mob, targetType, DEFAULT_RANDOM_INTERVAL, mustSee, mustReach, null, searchRange);
    }

    public EntityAINearestAttackableTargetNT(PathfinderMob mob, Class<T> targetType,
                                             int interval, boolean mustSee, boolean mustReach,
                                             @Nullable Predicate<LivingEntity> selector,
                                             double searchRange) {
        super(mob, mustSee, mustReach);
        this.targetType = targetType;
        this.randomInterval = reducedTickDelay(interval);
        this.searchRange = searchRange;
        this.setFlags(EnumSet.of(Flag.TARGET));

        this.targetConditions = TargetingConditions
                .forCombat()
                .range(searchRange)
                .selector(selector);

        this.sorter = Comparator.comparingDouble(e -> e.distanceToSqr(mob));
    }

    @Override
    public boolean canUse() {
        if (this.randomInterval > 0 && this.mob.getRandom().nextInt(this.randomInterval) != 0) {
            return false;
        }
        this.findTarget();
        return this.target != null;
    }

    protected void findTarget() {
        // Используем встроенный в Mob поиск по AABB + предикат
        List<T> candidates = this.mob.level().getEntitiesOfClass(
                this.targetType,
                this.mob.getBoundingBox().inflate(this.searchRange),
                e -> this.targetConditions.test(this.mob, e)
        );

        if (candidates.isEmpty()) {
            this.target = null;
            return;
        }

        candidates.sort((a, b) -> Double.compare(
                a.distanceToSqr(this.mob), b.distanceToSqr(this.mob)));

        this.target = candidates.get(0);
    }

    @Override
    public void start() {
        this.mob.setTarget(this.target);
        super.start();
    }

    @Override
    public void stop() {
        this.mob.setTarget(null);
        this.target = null;
        super.stop();
    }
}
