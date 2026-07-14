package com.hbm.entity.mob;

import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.entity.mob.ai.EntityAIMaskmanCasualApproach;
import com.hbm.entity.mob.ai.EntityAIMaskmanLasergun;
import com.hbm.entity.mob.ai.EntityAIMaskmanMinigun;
import com.hbm.extprop.IRadiationImmune;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.util.ArmorUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityMaskMan extends Monster implements RangedAttackMob, IRadiationImmune {

    private final ServerBossEvent bossEvent = new ServerBossEvent(
            Component.translatable("entity.hbm.maskman"),
            BossEvent.BossBarColor.PURPLE,
            BossEvent.BossBarOverlay.PROGRESS
    );

    public EntityMaskMan(EntityType<? extends EntityMaskMan> type, Level level) {
        super(type, level);
        this.xpReward = 100;
    }


    @Override
    protected void registerGoals() {
        // Основные задачи
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new EntityAIMaskmanCasualApproach(this, Player.class, 1.0D, false));
        this.goalSelector.addGoal(2, new EntityAIMaskmanMinigun(this, true, true, 3));
        this.goalSelector.addGoal(3, new EntityAIMaskmanLasergun(this, true, true));

        // Дополнительные задачи
        this.goalSelector.addGoal(4, new WaterAvoidingRandomStrollGoal(this, 1.0D));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));

        // Целевые задачи
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(2, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Monster.createMonsterAttributes()
                .add(Attributes.MOVEMENT_SPEED, 0.25D)
                .add(Attributes.FOLLOW_RANGE, 100.0D)
                .add(Attributes.ATTACK_DAMAGE, 15.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 1.0D)
                .add(Attributes.MAX_HEALTH, 1000.0D);
    }

    @Override
    public boolean hurt(@NotNull DamageSource source, float amount) {
        // Проверка на яйцо (пасхалка)
        if (source.getDirectEntity() instanceof net.minecraft.world.entity.projectile.ThrownEgg) {
            if (this.random.nextInt(10) == 0) {
                this.xpReward = 0;
                this.setHealth(0);
                return true;
            }
        }

        // Сопротивление урону
        if (source.is(DamageTypes.ON_FIRE) || source.is(DamageTypes.IN_FIRE) || source.is(DamageTypes.LAVA)) {
            amount = 0;
        }
        if (source.is(DamageTypes.MAGIC)) {
            amount = 0;
        }
        if (source.is(DamageTypes.ARROW) || source.is(DamageTypes.THROWN)) {
            amount *= 0.25F;
        }
        if (source.is(DamageTypes.EXPLOSION) || source.is(DamageTypes.PLAYER_EXPLOSION)) {
            amount *= 0.5F;
        }

        // Ограничение урона
        if (amount > 50) {
            amount = 50 + (amount - 50) * 0.25F;
        }

        return super.hurt(source, amount);
    }

    @Override
    public void aiStep() {
        // Сохраняем предыдущее здоровье для проверки перехода за половину
        float prevHealth = this.getHealth();

        super.aiStep();

        // Проверка: здоровье упало ниже половины максимального
        if (prevHealth >= this.getMaxHealth() / 2 &&
                this.getHealth() < this.getMaxHealth() / 2 &&
                this.isAlive()) {

            if (!this.level().isClientSide) {
                this.level().explode(this, this.getX(), this.getY() + 4, this.getZ(),
                        2.5F, Level.ExplosionInteraction.MOB);
            }
        }
    }

    @Override
    public void die(@NotNull DamageSource source) {
        super.die(source);

        if (!this.level().isClientSide) {
            AABB box = this.getBoundingBox().inflate(50, 50, 50);
            List<Player> players = this.level().getEntitiesOfClass(Player.class, box);

            for (Player player : players) {
                if (player instanceof ServerPlayer sp) {
                    ModCriteriaTriggers.KILL_MASKMAN.trigger(sp);
                }
            }
        }
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        if (!this.level().isClientSide) {
            // Маска с фильтром
            ItemStack mask = new ItemStack(ModArmorItems.GAS_MASK_M65.get());
            ArmorUtil.installGasMaskFilter(mask, new ItemStack(ModItems.GAS_MASK_FILTER_COMBO.get()));
            this.spawnAtLocation(mask, 0.0F);

            // Монеты и прочее
            this.spawnAtLocation(ModItems.COIN_MASKMAN.get(), 1);
            this.spawnAtLocation(ModItems.BOTTLED_CLOUD.get(), 1);
            this.spawnAtLocation(Items.SKELETON_SKULL, 1);
        }
    }

    @Override
    public boolean removeWhenFarAway(double distance) {
        return false;
    }

    @Override
    public boolean fireImmune() {
        return true;
    }

    @Override
    public boolean canBeAffected(net.minecraft.world.effect.@NotNull MobEffectInstance effect) {
        // Иммунитет к эффектам
        return false;
    }

    @Override
    public void performRangedAttack(@NotNull LivingEntity target, float distanceFactor) {
        // Ружейная атака - делегируется AI
        // EntityAIMaskmanMinigun и EntityAIMaskmanLasergun обрабатывают это
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        // Восстанавливаем здоровье босс-бара
        if (this.hasCustomName()) {
            this.bossEvent.setName(this.getDisplayName());
        }
    }

    @Override
    public void setCustomName(@org.jetbrains.annotations.Nullable Component name) {
        super.setCustomName(name);
        this.bossEvent.setName(this.getDisplayName());
    }

    @Override
    public void startSeenByPlayer(@NotNull ServerPlayer player) {
        super.startSeenByPlayer(player);
        this.bossEvent.addPlayer(player);
    }

    @Override
    public void stopSeenByPlayer(@NotNull ServerPlayer player) {
        super.stopSeenByPlayer(player);
        this.bossEvent.removePlayer(player);
    }

    @Override
    public void tick() {
        super.tick();

        // Обновление босс-бара
        if (!this.level().isClientSide) {
            this.bossEvent.setProgress(this.getHealth() / this.getMaxHealth());
        }
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public boolean shouldRenderAtSqrDistance(double distance) {
        return distance < 500000;
    }
}