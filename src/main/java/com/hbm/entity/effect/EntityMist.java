package com.hbm.entity.effect;

import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.extprop.HbmLivingProps;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.*;

import com.hbm.main.MainRegistry;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.EntityDamageUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EntityMist extends Entity {

    private static final EntityDataAccessor<Integer> FLUID_TYPE = SynchedEntityData.defineId(EntityMist.class, EntityDataSerializers.INT);
    private static final EntityDataAccessor<Float> WIDTH = SynchedEntityData.defineId(EntityMist.class, EntityDataSerializers.FLOAT);
    private static final EntityDataAccessor<Float> HEIGHT = SynchedEntityData.defineId(EntityMist.class, EntityDataSerializers.FLOAT);

    public int maxAge = 150;

    public EntityMist(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public EntityMist(Level level) {
        super(ModEntities.MIST.get(), level);
        this.noPhysics = true;
    }

    public EntityMist setArea(float width, float height) {
        this.entityData.set(WIDTH, width);
        this.entityData.set(HEIGHT, height);
        return this;
    }

    public EntityMist setDuration(int duration) {
        this.maxAge = duration;
        return this;
    }

    public EntityMist setFluidType(FluidTypeHBM fluid) {
        this.entityData.set(FLUID_TYPE, Fluids.getID(fluid));
        return this;
    }

    public FluidTypeHBM getFluidType() {
        return Fluids.fromID(this.entityData.get(FLUID_TYPE));
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(FLUID_TYPE, 0);
        this.entityData.define(WIDTH, 0.0F);
        this.entityData.define(HEIGHT, 0.0F);
    }

    @Override
    public void tick() {
        float height = this.entityData.get(HEIGHT);
        this.setBoundingBox(new AABB(this.getX(), this.getY(), this.getZ(),
                this.getX(), this.getY(), this.getZ())
                .inflate(this.entityData.get(WIDTH) / 2, 0, this.entityData.get(WIDTH) / 2)
                .expandTowards(0, height, 0));

        if (!this.level().isClientSide) {
            if (this.tickCount >= this.maxAge) {
                this.discard();
                return;
            }

            FluidTypeHBM type = this.getFluidType();

            if (type.hasTrait(FT_VentRadiation.class)) {
                FT_VentRadiation trait = type.getTrait(FT_VentRadiation.class);
                RadiationEvents.incrementRadiation(this.level(),
                        BlockPos.containing(this.getX(), this.getY(), this.getZ()),
                        trait.getRadPerMB() * 2);
            }

            double intensity = 1.0D - (double) this.tickCount / (double) this.maxAge;

            if (type.hasTrait(FT_Flammable.class) && this.isOnFire()) {
                this.level().explode(this, this.getX(), this.getY() + height / 2, this.getZ(),
                        (float) intensity * 15F, Level.ExplosionInteraction.NONE);
                this.discard();
                return;
            }

            AABB aabb = this.getBoundingBox();
            List<Entity> affected = this.level().getEntities(this, aabb);

            for (Entity e : affected) {
                this.affect(e, intensity);
            }
        } else {
            for (int i = 0; i < 2; i++) {
                double x = this.getX() + (random.nextDouble() - 0.5) * this.entityData.get(WIDTH);
                double y = this.getY() + random.nextDouble() * height;
                double z = this.getZ() + (random.nextDouble() - 0.5) * this.entityData.get(WIDTH);

                CompoundTag fx = new CompoundTag();
                fx.putString("type", "tower");
                fx.putFloat("lift", 0.5F);
                fx.putFloat("base", 0.75F);
                fx.putFloat("max", 2F);
                fx.putInt("life", 50 + this.level().random.nextInt(10));
                fx.putInt("color", this.getFluidType().getColor());
                fx.putDouble("posX", x);
                fx.putDouble("posY", y);
                fx.putDouble("posZ", z);
                MainRegistry.proxy.effectNT(fx);
            }
        }
    }

    protected void affect(Entity entity, double intensity) {
        FluidTypeHBM type = this.getFluidType();
        LivingEntity living = entity instanceof LivingEntity ? (LivingEntity) entity : null;

        if (type.getTemperature() >= 100) {
            EntityDamageUtil.attackEntityFromIgnoreIFrame(entity,
                    this.damageSources().hotFloor(), 0.2F + (type.getTemperature() - 100) * 0.02F);

            if (type.getTemperature() >= 500) {
                entity.setSecondsOnFire(10);
            }
        }

        if (type.getTemperature() < -20 && living != null) {
            EntityDamageUtil.attackEntityFromIgnoreIFrame(entity,
                    this.damageSources().freeze(), 0.2F + (type.getTemperature() + 20) * -0.05F);
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 2));
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 4));
        }

        if (type.hasTrait(FluidTraits.DELICIOUS.getClass()) && living != null && living.isAlive()) {
            living.heal(2.0F * (float) intensity);
        }

        if (type.hasTrait(FT_Flammable.class) && type.hasTrait(FT_Liquid.class) && living != null) {
            HbmLivingProps.setOil(living, 200);
        }

        if (this.isExtinguishing(type)) {
            entity.clearFire();
        }

        if (type.hasTrait(FT_Corrosive.class)) {
            FT_Corrosive trait = type.getTrait(FT_Corrosive.class);

            if (living != null) {
                DamageSource acidSource = ModDamageSource.createDamageSource(
                        ModDamageSource.ACID, null, null, entity.level());
                EntityDamageUtil.attackEntityFromIgnoreIFrame(living,
                        acidSource, trait.getRating() / 60F);
                for (int i = 0; i < 4; i++) {
                    ArmorUtil.damageSuit(living, i, trait.getRating() / 50);
                }
            }
        }

        if (type.hasTrait(FT_VentRadiation.class)) {
            FT_VentRadiation trait = type.getTrait(FT_VentRadiation.class);
            if (living != null) {
                ContaminationUtil.contaminate(living, HazardType.RADIATION,
                        ContaminationType.CREATIVE, trait.getRadPerMB() * 5);
            }
        }

        if (type.hasTrait(FT_Poison.class)) {
            FT_Poison trait = type.getTrait(FT_Poison.class);

            if (living != null) {
                living.addEffect(new MobEffectInstance(
                        trait.isWithering() ? MobEffects.WITHER : MobEffects.POISON,
                        (int) (5 * 20 * intensity)));
            }
        }

        if (type.hasTrait(FT_Toxin.class)) {
            FT_Toxin trait = type.getTrait(FT_Toxin.class);

            if (living != null) {
                trait.affect(living, intensity);
            }
        }

        if (type == Fluids.ENDERJUICE.get() && living != null) {
            teleportRandomly(living);
        }

        if (type.hasTrait(FT_Pheromone.class)) {
            FT_Pheromone pheromone = type.getTrait(FT_Pheromone.class);

            if (living != null) {

                if ((living instanceof EntityGlyphid && pheromone.getType() == 1) ||
                        (living instanceof Player && pheromone.getType() == 2)) {
                    int mult = pheromone.getType();

                    living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, mult * 60 * 20, 1));
                    living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, mult * 60 * 20, 1));
                    living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, mult * 2 * 20, 0));
                    living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, mult * 60 * 20, 0));
                    living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, mult * 60 * 20, 1));
                    living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, mult * 60 * 20, 0));
                }

            }
        }
    }

    protected boolean isExtinguishing(FluidTypeHBM type) {
        return type.getTemperature() < 50 && !type.hasTrait(FT_Flammable.class);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setFluidType(Fluids.fromID(tag.getInt("type")));
        this.setArea(tag.getFloat("width"), tag.getFloat("height"));
        this.maxAge = tag.getInt("maxAge");
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putInt("type", Fluids.getID(this.getFluidType()));
        tag.putFloat("width", this.entityData.get(WIDTH));
        tag.putFloat("height", this.entityData.get(HEIGHT));
        tag.putInt("maxAge", this.maxAge);
    }

    @Override
    public @NotNull Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean displayFireAnimation() {
        return false;
    }

    @Override
    public void move(net.minecraft.world.entity.@NotNull MoverType type, @NotNull Vec3 pos) { }

    @Override
    public void setDeltaMovement(double x, double y, double z) { }

    @Override
    public void setPos(double x, double y, double z) {
        if (this.tickCount == 0) super.setPos(x, y, z);
    }

    public static SprayStyle getStyleFromType(FluidTypeHBM type) {
        if (type.hasTrait(FT_Viscous.class)) {
            return SprayStyle.NULL;
        }
        if (type.hasTrait(FT_Gaseous.class) || type.hasTrait(FT_Gaseous_ART.class)) {
            return SprayStyle.GAS;
        }
        if (type.hasTrait(FT_Liquid.class)) {
            return SprayStyle.MIST;
        }
        return SprayStyle.NULL;
    }

    public enum SprayStyle {
        MIST,   // liquids that have been sprayed into a mist
        GAS,    // things that were already gaseous
        NULL
    }

    public void teleportRandomly(Entity entity) {
        double x = this.getX() + (this.random.nextDouble() - 0.5D) * 64.0D;
        double y = this.getY() + (this.random.nextInt(64) - 32);
        double z = this.getZ() + (this.random.nextDouble() - 0.5D) * 64.0D;
        this.teleportTo(entity, x, y, z);
    }

    public void teleportTo(Entity entity, double x, double y, double z) {
        double targetX = entity.getX();
        double targetY = entity.getY();
        double targetZ = entity.getZ();
        entity.setPos(x, y, z);
        boolean flag = false;
        int i = (int) Math.floor(entity.getX());
        int j = (int) Math.floor(entity.getY());
        int k = (int) Math.floor(entity.getZ());

        if (entity.level().hasChunk(i >> 4, k >> 4)) {
            boolean flag1 = false;

            while (!flag1 && j > entity.level().getMinBuildHeight()) {
                BlockPos pos = new BlockPos(i, j - 1, k);
                BlockState state = entity.level().getBlockState(pos);

                if (state.isSolid()) {
                    flag1 = true;
                } else {
                    entity.setPos(entity.getX(), entity.getY() - 1, entity.getZ());
                    j--;
                }
            }

            if (flag1) {
                entity.setPos(entity.getX(), entity.getY(), entity.getZ());

                if (entity.level().noCollision(entity) && !entity.level().containsAnyLiquid(entity.getBoundingBox())) {
                    flag = true;
                }
            }
        }

        if (!flag) {
            entity.setPos(targetX, targetY, targetZ);
        } else {
            for (int l = 0; l < 128; ++l) {
                double d6 = (double) l / 127.0D;
                float f = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f1 = (this.random.nextFloat() - 0.5F) * 0.2F;
                float f2 = (this.random.nextFloat() - 0.5F) * 0.2F;
                double d7 = targetX + (entity.getX() - targetX) * d6 + (this.random.nextDouble() - 0.5D) * entity.getBbWidth() * 2.0D;
                double d8 = targetY + (entity.getY() - targetY) * d6 + this.random.nextDouble() * entity.getBbHeight();
                double d9 = targetZ + (entity.getZ() - targetZ) * d6 + (this.random.nextDouble() - 0.5D) * entity.getBbWidth() * 2.0D;

                this.level().addParticle(ParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
            }

            this.level().playSound(null, targetX, targetY, targetZ,
                    SoundEvents.ENDERMAN_TELEPORT, SoundSource.HOSTILE, 1.0F, 1.0F);
            entity.playSound(SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }
}