package com.hbm.entity.mob;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityGhost extends PathfinderMob {

    public EntityGhost(EntityType<? extends EntityGhost> type, Level world) {
        super(type, world);
        this.setInvulnerable(true);
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 8.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.2D);
    }

    @Override
    public void tick() {
        super.tick();

        if (!this.level().isClientSide) {
            double despawnRange = 50;
            AABB box = this.getBoundingBox().inflate(despawnRange);
            List<Player> players = this.level().getEntitiesOfClass(Player.class, box);
            // Исправлено: исчезаем только если НЕТ игроков в радиусе
            if (players.isEmpty()) {
                this.discard();
            }
        }
    }

    @Override
    public void setHealth(float health) {
        super.setHealth(this.getMaxHealth());
    }

    @Override
    public boolean isInvulnerable() {
        return true;
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        return false;
    }
}