package com.hbm.entity.mob;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.factory.BulletConfigRegistry;
import com.hbm.network.PacketDispatcher;
import com.hbm.potion.HbmPotion;
import com.hbm.tileentity.machine.TileEntityTesla;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.RangedAttackGoal;
import net.minecraft.world.entity.monster.RangedAttackMob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class EntityTaintCrab extends EntityCyberCrab implements RangedAttackMob {

    public List<double[]> targets = new ArrayList<>();

    public EntityTaintCrab(EntityType<? extends EntityCyberCrab> type, Level level) {
        super(type, level);
        this.noCulling = true;
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();
        this.goalSelector.addGoal(1, new RangedAttackGoal(this, 0.5D, 5, 5, 50.0F));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 25.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.5D)
                .add(Attributes.FOLLOW_RANGE, 50.0D)
                .add(Attributes.ATTACK_DAMAGE, 5.0D);
    }

    @Override
    public void tick() {
        super.tick();

        targets = TileEntityTesla.zap(level(), getX(), getY() + 1.25, getZ(), 10, this);

        AABB box = new AABB(
                getX() - 5, getY() - 5, getZ() - 5,
                getX() + 5, getY() + 5, getZ() + 5
        );

        List<LivingEntity> entities = level().getEntitiesOfClass(LivingEntity.class, box, e -> !(e instanceof EntityCyberCrab));

        for (LivingEntity e : entities) {
            e.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 10, 15));
        }
    }

    @Override
    protected void dropCustomDeathLoot(@NotNull DamageSource source, int looting, boolean recentlyHit) {
        super.dropCustomDeathLoot(source, looting, recentlyHit);

        // Обычный дроп
        this.spawnAtLocation(new ItemStack(ModItems.COIL_ADVANCED_ALLOY.get()));

        // Редкий дроп
        if (this.random.nextInt(100) == 0) {
            this.spawnAtLocation(new ItemStack(ModItems.COIL_MAGNETIZED_TUNGSTEN.get()));
        }
    }


    @Override
    public void performRangedAttack(LivingEntity target, float distanceFactor) {
        EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(level(), this, BulletConfigRegistry.r762_fmj, 10F, 0F, 0F, 0F, 0F);

        Vec3 motion = new Vec3(
                getX() - target.getX(),
                getY() - target.getZ() - target.getBbHeight() / 2,
                getZ() - target.getZ()
        ).normalize();

        bullet.shoot(motion.x, motion.y, motion.z, 1.0F, 0F);

        CompoundTag data = new CompoundTag();
        data.putString("type", "vanilla");
        data.putString("mode", "flame");
        data.putDouble("mX", bullet.getDeltaMovement().x * 0.3);
        data.putDouble("mY", bullet.getDeltaMovement().y * 0.3);
        data.putDouble("mZ", bullet.getDeltaMovement().z * 0.3);

        if (level() instanceof ServerLevel) {
            PacketDispatcher.sendAuxParticleNT(data, bullet.getX(), bullet.getY(), bullet.getZ(), target);
        }

        level().addFreshEntity(bullet);
        this.playSound(SoundEvents.CROSSBOW_SHOOT, 1.0F, 0.5F);
    }
}