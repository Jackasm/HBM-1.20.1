package com.hbm.items.weapon.sedna;


import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.bomb.BlockDetonatable;
import com.hbm.blocks.generic.BlockDecoCRT;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.items.weapon.sedna.factory.ConfettiUtil;
import com.hbm.items.weapon.sedna.factory.GunFactory;
import com.hbm.particle.SpentCasing;
import com.hbm.sound.ModSounds;
import com.hbm.util.BobMathUtil;
import com.hbm.util.EntityDamageUtil;
import com.hbm.util.HBMEnums;
import net.minecraft.core.Direction;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.Tags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.hbm.util.ModDamageSource.*;

public class BulletConfig implements Cloneable {

    public static List<BulletConfig> configs = new ArrayList<>();

    public int id;

    // Ammo properties
    public ItemStack ammo;
    public ItemStack casingItem;
    public int casingAmount;
    /** How much ammo is added to a standard mag when loading one item */
    public int ammoReloadCount = 1;

    // Projectile properties
    public float velocity = 10.0f;
    public float spread = 0.0f;
    public static final float defaultSpread = 0.005F;
    public float wear = 1.0f;
    public int projectilesMin = 1;
    public int projectilesMax = 1;
    public ProjectileType pType = ProjectileType.BULLET;

    // Damage properties
    public float damageMult = 1.0f;
    public float armorThresholdNegation = 0.0f;
    public float armorPiercingPercent = 0.0f;
    public float knockbackMult = 0.1f;
    public float headshotMult = 1.25f;
    public DamageClass dmgClass = DamageClass.PHYSICAL;

    // Ricochet properties
    public float ricochetAngle = 5.0f;
    public int maxRicochetCount = 2;
    /** Whether damage dealt to an entity is subtracted from the projectile's damage on penetration */
    public boolean damageFalloffByPen = true;

    // Lifetime properties
    public double gravity = 0;
    public int expires = 30;
    public boolean impactsEntities = true;
    public boolean doesPenetrate = false;
    /** Whether projectiles ignore block entirely */
    public boolean isSpectral = false;
    public int selfDamageDelay = 2;

    // Visual properties
    public boolean blackPowder = false;
    public boolean renderRotations = true;
    public SpentCasing casing;
    private Supplier<ItemStack> casingSupplier;
    private int casingCount;
    private ItemStack cachedCasing;

    // Lambda handlers
    public Consumer<Entity> onUpdate;
    public BiConsumer<EntityBulletBaseMK4, HitResult> onImpact;
    public BiConsumer<EntityBulletBeamBase, HitResult> onImpactBeam;
    public BiConsumer<EntityBulletBaseMK4, HitResult> onRicochet = LAMBDA_STANDARD_RICOCHET;
    public BiConsumer<EntityBulletBaseMK4, HitResult> onEntityHit = LAMBDA_STANDARD_ENTITY_HIT;

    //From deprecated BulletConfiguration
    // Visual properties
    public int style = 0;           // Тип пули (STYLE_NORMAL, STYLE_ROCKET, STYLE_BOLT и т.д.)
    public int trail = 0;           // Тип трейла (BOLT_LACUNAE, BOLT_WORM и т.д.)
    public String vPFX = "";        // Vanilla particle эффект (reddust, bluedust, flame и т.д.)
    // Explosive properties
    public float explosive = 0F;     // Радиус взрыва
    public int incendiary = 0;       // Длительность горения (в тиках)
    public int emp = 0;              // Радиус EMP эффекта
    public double jolt = 0;          // Сила толчка (для ExplosionLarge.jolt)
    public int nuke = 0;             // Радиус ядерного взрыва
    public int shrapnel = 0;         // Количество осколков
    public int chlorine = 0;         // Хлор (отравление)
    public int caustic = 0;          // Коррозия (повреждение брони)
    public boolean destroysBlocks = false;  // Разрушает ли блоки
    public boolean blockDamage = true;      // Наносит ли урон блокам
    public boolean instakill = false;       // Мгновенное убийство
    // Bullet properties
    public int leadChance = 0;       // Шанс отравления свинцом (в процентах)
    public float dmgMin = 0;         // Минимальный урон
    public float dmgMax = 0;         // Максимальный урон
    public int ricochetCount = 0;    // Текущее количество рикошетов (хранится в пуле)
    public double bounceMod = 0.8;   // Модификатор скорости после рикошета
    public int HBRC = 0;             // Шанс рикошета при большом угле
    public int LBRC = 0;             // Шанс рикошета при малом угле
    public boolean doesBreakGlass = false; // Разбивает ли стекло
    public boolean liveAfterImpact = false; // Продолжает ли существовать после попадания
    public int rainbow = 0;          // Эффект радуги (Fleija)
    public int maxRicochet = 2;      // Максимальное количество рикошетов
    // Sound properties
    public int plink = 0;            // Тип звука при рикошете (PLINK_BULLET, PLINK_GRENADE и т.д.)
    // Damage properties
    public String damageType = "bullet";  // Тип урона
    public boolean dmgProj = true;        // Урон от снаряда
    public boolean dmgFire = false;       // Огненный урон
    public boolean dmgExplosion = false;  // Взрывной урон
    public boolean dmgBypass = false;     // Игнорирует броню
    // NPC/Special properties
    public boolean laser = false;     // Лазерный луч
    public boolean bounce = false;    // Рикошет без потери скорости
    public static final int STYLE_NONE = -1;
    public static final int STYLE_NORMAL = 0;
    public static final int STYLE_PISTOL = 1;
    public static final int STYLE_FLECHETTE = 2;
    public static final int STYLE_PELLET = 3;
    public static final int STYLE_BOLT = 4;
    public static final int STYLE_FOLLY = 5;
    public static final int STYLE_ROCKET = 6;
    public static final int STYLE_STINGER = 7;
    public static final int STYLE_GRENADE = 10;
    public static final int STYLE_BF = 11;
    public static final int STYLE_ORB = 12;
    public static final int STYLE_METEOR = 13;
    public static final int STYLE_APDS = 14;
    public static final int STYLE_BLADE = 15;
    public static final int STYLE_TAU = 17;
    public static final int STYLE_LEADBURSTER = 18;
    // Константы для trail (Bolt types)
    public static final int BOLT_LACUNAE = 0;
    public static final int BOLT_NIGHTMARE = 1;
    public static final int BOLT_LASER = 2;
    public static final int BOLT_ZOMG = 3;
    public static final int BOLT_WORM = 4;
    public static final int BOLT_GLASS_CYAN = 5;
    public static final int BOLT_GLASS_BLUE = 6;
    // Константы для plink (Ricochet sound)
    public static final int PLINK_NONE = 0;
    public static final int PLINK_BULLET = 1;
    public static final int PLINK_GRENADE = 2;
    public static final int PLINK_ENERGY = 3;
    public static final int PLINK_SING = 4;

    // Renderers
    public BiConsumer<EntityBulletBaseMK4, Float> renderer;
    public BiConsumer<EntityBulletBeamBase, Float> rendererBeam;

    public BulletConfig() {
        this.id = configs.size();
        configs.add(this);
    }

    public BulletConfig setItem(GunFactory.EnumAmmo ammoEnum) {
        if (ammoEnum != null && ammoEnum.getAmmoItem() != null) {
            this.ammo = new ItemStack(ammoEnum.getAmmoItem());
        }
        return this;
    }

    /** Required for the clone() operation to reset the ID */
    public BulletConfig forceReRegister() {
        this.id = configs.size();
        configs.add(this);
        return this;
    }

    public enum ProjectileType {
        BULLET,
        BULLET_CHUNKLOADING,
        BEAM
    }

    /* FLUID API SETTERS */

    public BulletConfig setBeam() {
        this.pType = ProjectileType.BEAM;
        return this;
    }

    public BulletConfig setChunkloading() {
        this.pType = ProjectileType.BULLET_CHUNKLOADING;
        return this;
    }



    public BulletConfig setItem(ItemStack stack) {
        if (stack == null) {
            this.ammo = ItemStack.EMPTY;
            System.err.println("Warning: Setting null ItemStack to BulletConfig");
        } else {
            this.ammo = stack.copy();
        }
        return this;
    }

    public ItemStack getCasing() {
        if (cachedCasing != null) {
            return cachedCasing.copy();
        }
        if (casingSupplier != null) {
            cachedCasing = casingSupplier.get();
            return cachedCasing.copy();
        }
        return ItemStack.EMPTY;
    }

    public BulletConfig setCasing(@NotNull ItemStack item, int amount) {
        this.casingItem = item.copy();
        this.casingAmount = amount;
        return this;
    }

    public BulletConfig setCasing(Supplier<Item> itemSupplier, int count) {
        this.casingSupplier = () -> new ItemStack(itemSupplier.get(), count);
        return this;
    }

    public BulletConfig setCasing(@NotNull HBMEnums.EnumCasingType casingType, int amount) {
        // Нужно получить ItemStack из EnumCasingType
        // Посмотрим, как это делается в других местах вашего кода

        // Вариант 1: Если есть статический метод в HBMEnums
        // ItemStack stack = HBMEnums.getCasingItemStack(casingType);

        // Вариант 2: Если есть соответствие в ModItems
        // ItemStack stack = getCasingItemStackFromType(casingType);

        // Вариант 3: Временная заглушка, пока не найдем правильный способ
        ItemStack stack = ItemStack.EMPTY;

        // Попробуем найти в вашем коде, как это обычно делается
        // Проверьте другие фабрики (XFactory12ga.java и т.д.)

        return setCasing(stack, amount);
    }



    public BulletConfig setAmmoFromRegistry(GunFactory.EnumAmmo ammoEnum) {
        return this.setItem(ammoEnum);
    }



    public BulletConfig setReloadCount(int ammoReloadCount) {
        this.ammoReloadCount = ammoReloadCount;
        return this;
    }

    public BulletConfig setVel(float velocity) {
        this.velocity = velocity;
        return this;
    }

    public BulletConfig setSpread(float spread) {
        this.spread = spread;
        return this;
    }

    public BulletConfig setWear(float wear) {
        this.wear = wear;
        return this;
    }

    public BulletConfig setProjectiles(int amount) {
        this.projectilesMin = amount;
        this.projectilesMax = amount;
        return this;
    }

    public BulletConfig setProjectiles(int min, int max) {
        this.projectilesMin = min;
        this.projectilesMax = max;
        return this;
    }

    public BulletConfig setDamage(float damageMult) {
        this.damageMult = damageMult;
        return this;
    }

    public BulletConfig setThresholdNegation(float armorThresholdNegation) {
        this.armorThresholdNegation = armorThresholdNegation;
        return this;
    }

    public BulletConfig setArmorPiercing(float armorPiercingPercent) {
        this.armorPiercingPercent = armorPiercingPercent;
        return this;
    }

    public BulletConfig setKnockback(float knockbackMult) {
        this.knockbackMult = knockbackMult;
        return this;
    }

    public BulletConfig setHeadshot(float headshotMult) {
        this.headshotMult = headshotMult;
        return this;
    }

    public BulletConfig setupDamageClass(DamageClass clazz) {
        this.dmgClass = clazz;
        return this;
    }

    public BulletConfig setRicochetAngle(float angle) {
        this.ricochetAngle = angle;
        return this;
    }

    public BulletConfig setRicochetCount(int count) {
        this.maxRicochetCount = count;
        return this;
    }

    public BulletConfig setDamageFalloffByPen(boolean falloff) {
        this.damageFalloffByPen = falloff;
        return this;
    }

    public BulletConfig setGrav(double gravity) {
        this.gravity = gravity;
        return this;
    }

    public BulletConfig setLife(int expires) {
        this.expires = expires;
        return this;
    }

    public BulletConfig setImpactsEntities(boolean impact) {
        this.impactsEntities = impact;
        return this;
    }

    public BulletConfig setDoesPenetrate(boolean pen) {
        this.doesPenetrate = pen;
        return this;
    }

    public BulletConfig setSpectral(boolean spectral) {
        this.isSpectral = spectral;
        return this;
    }

    public BulletConfig setSelfDamageDelay(int delay) {
        this.selfDamageDelay = delay;
        return this;
    }

    public BulletConfig setBlackPowder(boolean bp) {
        this.blackPowder = bp;
        return this;
    }

    public BulletConfig setRenderRotations(boolean rot) {
        this.renderRotations = rot;
        return this;
    }

    public BulletConfig setCasing(@Nullable SpentCasing casing) {
        this.casing = casing;
        return this;
    }

    public BulletConfig setRenderer(@Nullable BiConsumer<EntityBulletBaseMK4, Float> renderer) {
        this.renderer = renderer;
        return this;
    }

    public BulletConfig setRendererBeam(@Nullable BiConsumer<EntityBulletBeamBase, Float> renderer) {
        this.rendererBeam = renderer;
        return this;
    }

    public BulletConfig setOnUpdate(@Nullable Consumer<Entity> lambda) {
        this.onUpdate = lambda;
        return this;
    }

    public BulletConfig setOnRicochet(@Nullable BiConsumer<EntityBulletBaseMK4, HitResult> lambda) {
        this.onRicochet = lambda;
        return this;
    }

    public BulletConfig setOnImpact(@Nullable BiConsumer<EntityBulletBaseMK4, HitResult> lambda) {
        this.onImpact = lambda;
        return this;
    }

    public BulletConfig setOnBeamImpact(BiConsumer<EntityBulletBeamBase, HitResult> lambda) {
        this.onImpactBeam = lambda; return this;
    }

    public BulletConfig setOnEntityHit(@Nullable BiConsumer<EntityBulletBaseMK4, HitResult> lambda) {
        this.onEntityHit = lambda;
        return this;
    }

    /* DAMAGE SOURCE CREATION */

    @SuppressWarnings("incomplete-switch")
    public static DamageSource getDamage(Entity projectile, @Nullable LivingEntity shooter, DamageClass dmgClass) {

        ResourceKey<DamageType> typeKey = switch(dmgClass) {
            case PHYSICAL -> PHYSICAL;
            case FIRE -> FIRE;
            case EXPLOSIVE -> EXPLOSIVE;
            case ELECTRIC -> ELECTRICITY;
            case LASER -> LASER;
            case SUBATOMIC -> SUBATOMIC;
            default -> DAMAGE_TYPE_GENERIC;
        };

        Level level = projectile != null ? projectile.level() :
                shooter != null ? shooter.level() : null;

        if (level == null) {
            return new DamageSource(RegistryAccess.EMPTY.registryOrThrow(Registries.DAMAGE_TYPE)
                    .getHolderOrThrow(DamageTypes.GENERIC), projectile, shooter);
        }

        var registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var damageType = registry.getHolderOrThrow(typeKey);

        if (shooter != null && projectile != null) {
            return new DamageSource(damageType, projectile, shooter);
        } else if (shooter != null) {
            return new DamageSource(damageType, shooter);
        } else if (projectile != null) {
            return new DamageSource(damageType, projectile);
        } else {
            return new DamageSource(damageType);
        }
    }

    public static DamageSource getDamage(RegistryAccess registryAccess, @Nullable LivingEntity shooter, DamageClass dmgClass) {
        ResourceKey<DamageType> typeKey = switch(dmgClass) {
            case PHYSICAL -> DamageTypes.GENERIC;
            case FIRE -> DamageTypes.ON_FIRE;
            case EXPLOSIVE -> DamageTypes.EXPLOSION;
            case ELECTRIC -> DamageTypes.LIGHTNING_BOLT;
            case LASER -> DamageTypes.GENERIC;
            case SUBATOMIC -> DamageTypes.GENERIC;
            default -> DamageTypes.GENERIC;
        };

        var registry = registryAccess.registryOrThrow(Registries.DAMAGE_TYPE);
        var damageType = registry.getHolderOrThrow(typeKey);

        if (shooter != null) {
            return new DamageSource(damageType, shooter);
        } else {
            return new DamageSource(damageType);
        }
    }

    /* STANDARD LAMBDA HANDLERS */

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_RICOCHET = (bullet, hitResult) -> {

        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;

            BlockState state = bullet.level().getBlockState(blockHit.getBlockPos());
            Block block = state.getBlock();

            if (state.is(Tags.Blocks.GLASS) || state.is(Tags.Blocks.GLASS_PANES)) {
                bullet.level().destroyBlock(blockHit.getBlockPos(), false);
                bullet.setPos(hitResult.getLocation());
                return;
            }

            if (block instanceof BlockDetonatable detonatable) {
                detonatable.onShot(bullet.level(), blockHit.getBlockPos());
            }

            if (block == ModBlocks.DECO_CRT.get()) {
                int meta = state.getValue(BlockDecoCRT.META);
                int newMeta = (meta % 4) + 4;
                bullet.level().setBlock(blockHit.getBlockPos(), state.setValue(BlockDecoCRT.META, newMeta), 3);
            }

            // Calculate ricochet angle
            Direction dir = blockHit.getDirection();
            Vec3 face = new Vec3(dir.getStepX(), dir.getStepY(), dir.getStepZ());
            Vec3 vel = bullet.getDeltaMovement().normalize();

            double angle = Math.abs(BobMathUtil.getCrossAngle(vel, face) - 90);

            if (angle <= bullet.getConfig().ricochetAngle) {
                bullet.setRicochets(bullet.getRicochets() + 1);

                if (bullet.getRicochets() > bullet.getConfig().maxRicochetCount) {
                    bullet.setPos(hitResult.getLocation());
                    bullet.discard();
                    return;
                }

                // Reflect velocity based on hit side
                Vec3 motion = bullet.getDeltaMovement();
                switch (dir) {
                    case UP:
                    case DOWN:
                        bullet.setDeltaMovement(motion.x, -motion.y, motion.z);
                        break;
                    case NORTH:
                    case SOUTH:
                        bullet.setDeltaMovement(motion.x, motion.y, -motion.z);
                        break;
                    case WEST:
                    case EAST:
                        bullet.setDeltaMovement(-motion.x, motion.y, motion.z);
                        break;
                }

                bullet.level().playSound(null, bullet.getX(), bullet.getY(), bullet.getZ(),
                        ModSounds.RICOCHET.get(), bullet.getSoundSource(), 0.25f, 1.0f);
                bullet.setPos(hitResult.getLocation());

            } else {
                bullet.setPos(hitResult.getLocation());
                bullet.discard();
            }
        }
    };

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_ENTITY_HIT = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();
            Entity owner = bullet.getOwner();
            LivingEntity thrower = owner instanceof LivingEntity ? (LivingEntity) owner : null;

            // Skip if hitting self too soon
            if (entity == thrower && bullet.tickCount < bullet.getConfig().selfDamageDelay) {
                return;
            }

            // Skip if entity is already dead
            if (entity instanceof LivingEntity living && !living.isAlive()) {
                return;
            }

            float intendedDamage = bullet.getDamage();

            // Apply headshot multiplier
            if (entity instanceof LivingEntity living && bullet.getConfig().headshotMult > 1.0f) {
                double headHeight = living.getEyeHeight();
                double headY = living.getY() + living.getBbHeight() - headHeight;

                if (hitResult.getLocation().y > headY) {
                    intendedDamage *= bullet.getConfig().headshotMult;
                }
            }

            // Create damage source
            DamageSource source;
            if (thrower != null) {
                source = bullet.level().damageSources().mobAttack(thrower);
            } else {
                source = bullet.level().damageSources().generic();
            }
            source = new DamageSource(source.typeHolder(), bullet, thrower);

            if (!(entity instanceof LivingEntity living)) {
                // Damage non-living entities (simple damage)
                entity.hurt(source, intendedDamage);
                return;
            }

            float prevHealth = living.getHealth();

            // ИСПОЛЬЗУЕМ ТОЛЬКО EntityDamageUtil.attackEntityFromNT!
            // Он сам внутри использует ArmorResistanceHandler
            EntityDamageUtil.attackEntityFromNT(
                    living,
                    source,
                    intendedDamage,
                    true,
                    bullet.getConfig().knockbackMult,
                    bullet.getConfig().armorThresholdNegation,
                    bullet.getConfig().armorPiercingPercent / 100f  // Если у тебя в процентах
            );

            float newHealth = living.getHealth();

            // Apply damage falloff for penetration
            if (bullet.getConfig().damageFalloffByPen) {
                float damageDealt = Math.max(prevHealth - newHealth, 0);
                bullet.setDamage(bullet.getDamage() - damageDealt * 0.5f);
            }

            // Kill bullet if no penetration or out of damage
            if (!bullet.getConfig().doesPenetrate || bullet.getDamage() <= 0) {
                bullet.setPos(hitResult.getLocation());
                bullet.discard();
            }

            // Handle death effects
            if (!living.isAlive()) {
                ConfettiUtil.decideConfetti(living, source);
            }
        }
    };

    public static final BiConsumer<EntityBulletBeamBase, HitResult> LAMBDA_STANDARD_BEAM_HIT = (beam, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();
            LivingEntity thrower = beam.getThrower();

            // Skip if hitting self too soon (если есть задержка)
            if (entity == thrower && beam.tickCount < beam.getConfig().selfDamageDelay) {
                return;
            }

            float intendedDamage = beam.getDamage();

            // Create damage source
            DamageSource source = beam.getConfig().getDamage(beam, beam.getThrower(), DamageClass.LASER);

            if (!(entity instanceof LivingEntity living)) {
                // Damage non-living entities (simple damage)
                entity.hurt(source, intendedDamage);
                return;
            }

            // Используем EntityDamageUtil.attackEntityFromNT
            EntityDamageUtil.attackEntityFromNT(
                    living,
                    source,
                    intendedDamage,
                    true,
                    beam.getConfig().knockbackMult,
                    beam.getConfig().armorThresholdNegation,
                    beam.getConfig().armorPiercingPercent / 100f
            );

            // Handle death effects
            if (entity instanceof LivingEntity && !living.isAlive()) {
                ConfettiUtil.decideConfetti(living, source);
            }
        }
    };

    public static BiConsumer<EntityBulletBeamBase, HitResult> LAMBDA_BEAM_HIT = (beam, hitResult) -> {

        // Проверяем тип попадания
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();

            // Проверяем, является ли entity живым существом и живое ли оно еще
            if (entity instanceof LivingEntity living) {
                if (living.getHealth() <= 0) return;
            }

            // Получаем источник урона
            DamageSource source = beam.getConfig().getDamage(beam, beam.getThrower(), DamageClass.LASER);

            // Если entity не является LivingEntity
            if (!(entity instanceof LivingEntity)) {
                EntityDamageUtil.attackEntityFromIgnoreIFrame(entity, source, beam.getDamage());
                return;
            }

            // Если entity является LivingEntity
            LivingEntity living = (LivingEntity) entity;
            EntityDamageUtil.attackEntityFromNT(
                    living,
                    source,
                    beam.getDamage(),
                    true,
                    beam.getConfig().knockbackMult,
                    beam.getConfig().armorThresholdNegation,
                    beam.getConfig().armorPiercingPercent
            );
        }
    };

    private static void applyKnockback(LivingEntity entity, float damage, float knockbackMult) {
        if (knockbackMult > 0) {
            // Направление от пули
            Vec3 knockbackDir = entity.position().subtract(entity.getLookAngle()).normalize();
            if (knockbackDir.lengthSqr() < 0.0001) {
                knockbackDir = entity.getLookAngle().reverse();
            }

            Vec3 knockback = knockbackDir.scale(damage * knockbackMult * 0.1f);
            entity.push(knockback.x, Math.max(knockback.y, 0.1), knockback.z);
        }
    }

    /* UTILITY METHODS */

    public float getRandomProjectileCount() {
        if (projectilesMin == projectilesMax) {
            return projectilesMin;
        }
        return projectilesMin + bulletRand.nextInt(projectilesMax - projectilesMin + 1);
    }

    public boolean hasCasing() {
        return casingItem != null && !casingItem.isEmpty() && casingAmount > 0;
    }

    public boolean isBeam() {
        return pType == ProjectileType.BEAM;
    }

    public boolean isChunkloading() {
        return pType == ProjectileType.BULLET_CHUNKLOADING;
    }

    /* CLONE SUPPORT */

    @Override
    public BulletConfig clone() {
        try {
            BulletConfig clone = (BulletConfig) super.clone();
            clone.forceReRegister();

            // Deep copy mutable objects
            if (this.ammo != null) {
                clone.ammo = this.ammo.copy();
            }
            if (this.casingItem != null) {
                clone.casingItem = this.casingItem.copy();
            }

            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(); // Can't happen
        }
    }

    public BulletConfig setStyle(int style) {
        this.style = style;
        return this;
    }

    public BulletConfig setTrail(int trail) {
        this.trail = trail;
        return this;
    }

    public BulletConfig setVFX(String vfx) {
        this.vPFX = vfx;
        return this;
    }

    public BulletConfig setExplosive(float explosive) {
        this.explosive = explosive;
        return this;
    }

    public BulletConfig setIncendiary(int incendiary) {
        this.incendiary = incendiary;
        return this;
    }

    public BulletConfig setEMP(int emp) {
        this.emp = emp;
        return this;
    }

    public BulletConfig setJolt(double jolt) {
        this.jolt = jolt;
        return this;
    }

    public BulletConfig setNuke(int nuke) {
        this.nuke = nuke;
        return this;
    }

    public BulletConfig setShrapnel(int shrapnel) {
        this.shrapnel = shrapnel;
        return this;
    }

    public BulletConfig setLeadChance(int chance) {
        this.leadChance = chance;
        return this;
    }

    public BulletConfig setDamageRange(float min, float max) {
        this.dmgMin = min;
        this.dmgMax = max;
        return this;
    }

    public BulletConfig setMaxRicochet(int max) {
        this.maxRicochet = max;
        return this;
    }

    public BulletConfig setBounceMod(double mod) {
        this.bounceMod = mod;
        return this;
    }

    public BulletConfig setRicochetChance(int hbrc, int lbrc) {
        this.HBRC = hbrc;
        this.LBRC = lbrc;
        return this;
    }

    public BulletConfig setBreakGlass(boolean breakGlass) {
        this.doesBreakGlass = breakGlass;
        return this;
    }

    public BulletConfig setLiveAfterImpact(boolean live) {
        this.liveAfterImpact = live;
        return this;
    }


    public BulletConfig setRainbow(int radius) {
        this.rainbow = radius;
        return this;
    }

    public BulletConfig setPlinkSound(int plink) {
        this.plink = plink;
        return this;
    }

    public BulletConfig setDamageType(String type) {
        this.damageType = type;
        return this;
    }

    public BulletConfig setBlockDamage(boolean dmg) {
        this.blockDamage = dmg;
        return this;
    }

    public BulletConfig setDestroysBlocks(boolean destroys) {
        this.destroysBlocks = destroys;
        return this;
    }

    // Helper for random number generation (replaces bullet.rand)
    private static final java.util.Random bulletRand = new java.util.Random();

    public enum DamageClass {
        PHYSICAL,
        FIRE,
        EXPLOSIVE,
        ELECTRIC,
        LASER,
        MICROWAVE,
        SUBATOMIC,
        OTHER
    }
}