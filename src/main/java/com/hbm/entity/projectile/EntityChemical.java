package com.hbm.entity.projectile;

import com.hbm.entity.ModEntities;
import com.hbm.entity.mob.glyphid.EntityGlyphid;
import com.hbm.extprop.HbmLivingProps;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.*;

import com.hbm.main.MainRegistry;
import com.hbm.particle.helper.FlameCreator;
import com.hbm.util.*;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.ThrowableProjectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.List;

public class EntityChemical extends ThrowableProjectile {

    private static final EntityDataAccessor<Integer> FLUID_TYPE = SynchedEntityData.defineId(EntityChemical.class, EntityDataSerializers.INT);

    private LivingEntity thrower;

    public EntityChemical(EntityType<? extends EntityChemical> type, Level world) {
        super(type, world);
        this.setInvulnerable(true);
    }

    public EntityChemical(Level world, LivingEntity thrower, double sideOffset, double heightOffset, double frontOffset) {
        super(ModEntities.CHEMICAL.get(), world);
        this.thrower = thrower;
        this.setOwner(thrower);
        this.setInvulnerable(true);
        // Позиция рассчитывается в конструкторе
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(FLUID_TYPE, 0);
    }

    public EntityChemical setFluid(FluidTypeHBM fluid) {
        this.entityData.set(FLUID_TYPE, Fluids.getID(fluid));
        return this;
    }

    public FluidTypeHBM getFluidType() {
        return Fluids.fromID(this.entityData.get(FLUID_TYPE));
    }

    @Override
    public void tick() {
        if (!this.level().isClientSide) {
            if (this.tickCount > this.getMaxAge()) {
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

            if (type.hasTrait(FT_Gaseous.class) || type.hasTrait(FT_Gaseous_ART.class)) {
                double intensity = 1.0D - (double) this.tickCount / (double) this.getMaxAge();
                AABB aabb = this.getBoundingBox().inflate(intensity * 2.5, intensity * 2.5, intensity * 2.5);
                List<Entity> affected = this.level().getEntities(this, aabb);
                for (Entity e : affected) {
                    this.affect(e, intensity);
                }
            }
        } else {
            FluidTypeHBM type = getFluidType();
            ChemicalStyle style = getStyle();

            if (type == Fluids.BALEFIRE.get()) {
                if(MainRegistry.proxy.me().distanceTo(this) < 100)
                    FlameCreator.composeEffectClient(level(), getX(), getY() - 0.125, getZ(), FlameCreator.META_BALEFIRE);
            } else if (style == ChemicalStyle.LIQUID) {
                Color color = new Color(type.getColor());
                CompoundTag data = new CompoundTag();
                data.putString("type", "vanillaExt");
                data.putString("mode", "colordust");
                data.putDouble("posX", this.getX());
                data.putDouble("posY", this.getY());
                data.putDouble("posZ", this.getZ());
                data.putDouble("mX", this.getDeltaMovement().x + this.random.nextGaussian() * 0.05);
                data.putDouble("mY", this.getDeltaMovement().y - 0.2 + this.random.nextGaussian() * 0.05);
                data.putDouble("mZ", this.getDeltaMovement().z + this.random.nextGaussian() * 0.05);
                data.putFloat("r", color.getRed() / 255F);
                data.putFloat("g", color.getGreen() / 255F);
                data.putFloat("b", color.getBlue() / 255F);
                MainRegistry.proxy.effectNT(data);
            } else if (style == ChemicalStyle.BURNING) {
                if(MainRegistry.proxy.me().distanceTo(this) < 100)
                    FlameCreator.composeEffectClient(level(), getX(), getY() - 0.125, getZ(), FlameCreator.META_FIRE);
            }
        }
        super.tick();
    }

    protected void affect(Entity entity, double intensity) {

        ChemicalStyle style = getStyle();
        FluidTypeHBM type = getFluidType();
        LivingEntity living = entity instanceof LivingEntity ? (LivingEntity) entity : null;

        if(style == ChemicalStyle.LIQUID || style == ChemicalStyle.BURNING) //ignore range penalty for liquids
            intensity = 1D;

        if(style == ChemicalStyle.AMAT) {
            DamageSource radSource = ModDamageSource.createDamageSource(
                    ModDamageSource.RADIATION,
                    this,
                    this.thrower,
                    this.level()
            );
            EntityDamageUtil.attackEntityFromIgnoreIFrame(entity, radSource, 1F);
            if(living != null) {
                ContaminationUtil.contaminate(living, HazardType.RADIATION, ContaminationType.CREATIVE, 50F * (float) intensity);
                return;
            }
        }

        if(style == ChemicalStyle.LIGHTNING) {
            DamageSource elecSource = ModDamageSource.createDamageSource(
                    ModDamageSource.ELECTRICITY,
                    this,
                    this.thrower,
                    this.level()
            );
            EntityDamageUtil.attackEntityFromIgnoreIFrame(entity, elecSource, 0.5F);
            if(living != null) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 9));
                living.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 9));
                return;
            }
        }

        if(type.getTemperature() >= 100) {
            DamageSource boilSource = ModDamageSource.createCustomSource(
                    ModDamageSource.s_boil,
                    this.thrower,
                    this.level()
            );
            EntityDamageUtil.attackEntityFromIgnoreIFrame(entity, boilSource, Math.min(0.25F + (type.getTemperature() - 100) * 0.001F, 15F));

            if(type.getTemperature() >= 500) {
                entity.setSecondsOnFire(10);
            }
        }

        if(style == ChemicalStyle.LIQUID || style == ChemicalStyle.GAS) {
            if(type.getTemperature() < -20) {
                if(living != null) {
                    DamageSource coldSource = ModDamageSource.createCustomSource(
                            ModDamageSource.s_boil,
                            this.thrower,
                            this.level()
                    );
                    EntityDamageUtil.attackEntityFromIgnoreIFrame(entity, coldSource, Math.min(0.25F + (-type.getTemperature()) * 0.01F, 2F));
                }
            }

            if(type.hasTrait(FT_Delicious.class)) {
                if(living != null && living.isAlive()) {
                    living.heal(2F * (float) intensity);
                }
            }
        }

        if(style == ChemicalStyle.LIQUID) {
            if(type.hasTrait(FT_Flammable.class)) {
                if(living != null) {
                    HbmLivingProps.setOil(living, 300);
                }
            }
            if(type.hasTrait(FT_Delicious.class)) {
                if(living != null && living.isAlive()) {
                    living.heal(2F * (float) intensity);
                }
            }
        }

        if(this.isExtinguishing()) {
            entity.clearFire();
        }

        if(style == ChemicalStyle.BURNING) {
            FT_Combustible trait = type.getTrait(FT_Combustible.class);
            DamageSource fireSource = ModDamageSource.createCustomSource(
                    ModDamageSource.s_flamethrower,
                    this.thrower,
                    this.level()
            );
            EntityDamageUtil.attackEntityFromIgnoreIFrame(entity, fireSource, 0.2F + (trait != null ? (Math.min(trait.getCombustionEnergy() / 100_000F, 15F)) : 0));
            entity.setSecondsOnFire(5);
        }

        if(style == ChemicalStyle.GASFLAME) {
            FT_Flammable flammable = type.getTrait(FT_Flammable.class);
            FT_Combustible combustible = type.getTrait(FT_Combustible.class);

            float heat = Math.max(flammable != null ? flammable.getHeatEnergy() / 50_000F : 0,
                    combustible != null ? Math.min(combustible.getCombustionEnergy() / 100_000F, 15F) : 0);
            heat *= intensity;
            DamageSource flameSource = ModDamageSource.createCustomSource(
                    ModDamageSource.s_flamethrower,
                    this.thrower,
                    this.level()
            );
            EntityDamageUtil.attackEntityFromIgnoreIFrame(entity, flameSource, (0.2F + heat) * (float) intensity);
            entity.setSecondsOnFire((int) Math.ceil(5 * intensity));
        }

        if(type.hasTrait(FT_Corrosive.class)) {
            FT_Corrosive trait = type.getTrait(FT_Corrosive.class);

            if(living != null) {
                DamageSource acidSource = ModDamageSource.createDamageSource(
                        ModDamageSource.ACID,
                        this,
                        this.thrower,
                        this.level()
                );
                EntityDamageUtil.attackEntityFromIgnoreIFrame(living, acidSource, trait.getRating() / 50F);
                for(int i = 0; i < 4; i++) {
                    ArmorUtil.damageSuit(living, i, trait.getRating() / 40);
                }
            }
        }

        if(type.hasTrait(FT_VentRadiation.class)) {
            FT_VentRadiation trait = type.getTrait(FT_VentRadiation.class);
            if(living != null) {
                ContaminationUtil.contaminate(living, HazardType.RADIATION, ContaminationType.CREATIVE, trait.getRadPerMB() * 5);
            }
            RadiationEvents.incrementRadiation(this.level(),
                    BlockPos.containing(entity.getX(), entity.getY(), entity.getZ()),
                    trait.getRadPerMB() * 5);
        }

        if(type.hasTrait(FT_Poison.class)) {
            FT_Poison trait = type.getTrait(FT_Poison.class);

            if(living != null) {
                living.addEffect(new MobEffectInstance(
                        trait.isWithering() ? MobEffects.WITHER : MobEffects.POISON,
                        (int) (5 * 20 * intensity)
                ));
            }
        }

        if(type.hasTrait(FT_Toxin.class)) {
            FT_Toxin trait = type.getTrait(FT_Toxin.class);

            if(living != null) {
                trait.affect(living, intensity);
            }
        }

        if(type.hasTrait(FT_Pheromone.class)){
            FT_Pheromone pheromone = type.getTrait(FT_Pheromone.class);

            if(living != null) {
                living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2 * 60 * 20, 2));
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 5 * 60 * 20, 1));
                living.addEffect(new MobEffectInstance(MobEffects.DIG_SPEED, 2 * 60 * 20, 4));

                if (living instanceof EntityGlyphid && pheromone.getType() == 1) {
                    living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 5 * 60 * 20, 4));
                    living.addEffect(new MobEffectInstance(MobEffects.FIRE_RESISTANCE, 60 * 20, 0));
                    living.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 60 * 20, 19));
                } else if (living instanceof Player && pheromone.getType() == 2) {
                    living.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 2 * 60 * 20, 2));
                }
            }
        }

        if(type == Fluids.XPJUICE.get()) {
            if(entity instanceof Player player) {
                EnchantmentUtil.addExperience(player, 1, false);
                this.discard();
            }
        }

        if(type == Fluids.ENDERJUICE.get()) {
            this.teleportRandomly(entity);
        }
    }

    public ChemicalStyle getStyle() {
        return getStyleFromType(this.getFluidType());
    }

    public static ChemicalStyle getStyleFromType(FluidTypeHBM type) {
        if (type == Fluids.IONGEL.get()) {
            return ChemicalStyle.LIGHTNING;
        }
        if (type.isAntimatter()) {
            return ChemicalStyle.AMAT;
        }
        if (type.hasTrait(FT_Gaseous.class) || type.hasTrait(FT_Gaseous_ART.class)) {
            if (type.hasTrait(FT_Flammable.class) || type.hasTrait(FT_Combustible.class)) {
                return ChemicalStyle.GASFLAME;
            }
            return ChemicalStyle.GAS;
        }
        if (type.hasTrait(FT_Liquid.class)) {
            if (type.hasTrait(FT_Combustible.class)) {
                return ChemicalStyle.BURNING;
            }
            return ChemicalStyle.LIQUID;
        }
        return ChemicalStyle.NULL;
    }

    protected boolean isExtinguishing() {
        return this.getStyle() == ChemicalStyle.LIQUID &&
                this.getFluidType().getTemperature() < 50 &&
                !this.getFluidType().hasTrait(FT_Flammable.class);
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

                this.level().addParticle(net.minecraft.core.particles.ParticleTypes.PORTAL, d7, d8, d9, f, f1, f2);
            }

            this.level().playSound(null, targetX, targetY, targetZ,
                    net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT,
                    net.minecraft.sounds.SoundSource.HOSTILE, 1.0F, 1.0F);
            entity.playSound(net.minecraft.sounds.SoundEvents.ENDERMAN_TELEPORT, 1.0F, 1.0F);
        }
    }

    public int getMaxAge() {
        return switch (this.getStyle()) {
            case AMAT -> 100;
            case LIGHTNING -> 5;
            case BURNING -> 600;
            case GAS -> 60;
            case GASFLAME -> 20;
            case LIQUID -> 600;
            default -> 100;
        };
    }

    @Override
    public void addAdditionalSaveData(@NotNull CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("fluid", this.entityData.get(FLUID_TYPE));
    }

    @Override
    public void readAdditionalSaveData(@NotNull CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.entityData.set(FLUID_TYPE, tag.getInt("fluid"));
    }

    public enum ChemicalStyle {
        AMAT,
        LIGHTNING,
        LIQUID,
        GAS,
        GASFLAME,
        BURNING,
        NULL
    }
}