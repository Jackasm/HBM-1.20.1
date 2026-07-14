package com.hbm.util;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class ModDamageSource {

    // ResourceKey для всех кастомных типов урона
    public static final ResourceKey<DamageType> NUCLEAR_BLAST = register("nuclear_blast");
    public static final ResourceKey<DamageType> MUD_POISONING = register("mud_poisoning");
    public static final ResourceKey<DamageType> ACID = register("acid");
    public static final ResourceKey<DamageType> EUTHANIZED = register("euthanized");
    public static final ResourceKey<DamageType> TAU = register("tau");
    public static final ResourceKey<DamageType> RADIATION = register("radiation");
    public static final ResourceKey<DamageType> DIGAMMA = register("digamma");
    public static final ResourceKey<DamageType> TELEPORTER = register("teleporter");
    public static final ResourceKey<DamageType> CHEATER = register("cheater");
    public static final ResourceKey<DamageType> RUBBLE = register("rubble");
    public static final ResourceKey<DamageType> SHRAPNEL = register("shrapnel");
    public static final ResourceKey<DamageType> BLACKHOLE = register("blackhole");
    public static final ResourceKey<DamageType> TURBOFAN = register("turbofan");
    public static final ResourceKey<DamageType> METEORITE = register("meteorite");
    public static final ResourceKey<DamageType> BOXCAR = register("boxcar");
    public static final ResourceKey<DamageType> BOAT = register("boat");
    public static final ResourceKey<DamageType> BUILDING = register("building");
    public static final ResourceKey<DamageType> TAINT = register("taint");
    public static final ResourceKey<DamageType> AMS = register("ams");
    public static final ResourceKey<DamageType> AMS_CORE = register("ams_core");
    public static final ResourceKey<DamageType> BROADCAST = register("broadcast");
    public static final ResourceKey<DamageType> BANG = register("bang");
    public static final ResourceKey<DamageType> PC = register("pc");
    public static final ResourceKey<DamageType> CLOUD = register("cloud");
    public static final ResourceKey<DamageType> LEAD = register("lead");
    public static final ResourceKey<DamageType> ENERVATION = register("enervation");
    public static final ResourceKey<DamageType> ELECTRICITY = register("electricity");
    public static final ResourceKey<DamageType> EXHAUST = register("exhaust");
    public static final ResourceKey<DamageType> SPIKES = register("spikes");
    public static final ResourceKey<DamageType> LUNAR = register("lunar");
    public static final ResourceKey<DamageType> MONOXIDE = register("monoxide");
    public static final ResourceKey<DamageType> BLACK_LUNG = register("black_lung");
    public static final ResourceKey<DamageType> MKU = register("mku");
    public static final ResourceKey<DamageType> VACUUM = register("vacuum");
    public static final ResourceKey<DamageType> OVERDOSE = register("overdose");
    public static final ResourceKey<DamageType> MICROWAVE = register("microwave");
    public static final ResourceKey<DamageType> NITAN = register("nitan");
    public static final ResourceKey<DamageType> PHOSPHORUS = register("phosphorus");
    public static final ResourceKey<DamageType> HOT = register("hot");
    public static final ResourceKey<DamageType> EXPLOSIVE = register("explosive");
    public static final ResourceKey<DamageType> HYDRO = register("hydro");
    public static final ResourceKey<DamageType> BLINDING = register("blinding");
    public static final ResourceKey<DamageType> FIBROSIS = register("fibrosis");
    public static final ResourceKey<DamageType> ASBESTOS = register("asbestos");
    public static final ResourceKey<DamageType> SOOT = register("soot");
    public static final ResourceKey<DamageType> PHYSICAL = register("physical");
    public static final ResourceKey<DamageType> FIRE = register("fire");
    public static final ResourceKey<DamageType> LASER = register("laser");
    public static final ResourceKey<DamageType> SUBATOMIC = register("subatomic");
    public static final ResourceKey<DamageType> DAMAGE_TYPE_GENERIC = register("generic");
    public static final ResourceKey<DamageType> EUTHANIZED_SELF = register("euthanized_self");
    public static final ResourceKey<DamageType> EUTHANIZED_SELF2 = register("euthanized_self2");
    public static final ResourceKey<DamageType> DISPLACEMENT = register("displacement");

    // Константы для обратной совместимости
    public static final String s_bullet = "revolverBullet";
    public static final String s_emplacer = "chopperBullet";
    public static final String s_tau = "tau";
    public static final String s_combineball = "cmb";
    public static final String s_zomg_prefix = "subAtomic";
    public static final String s_euthanized = "euthanized";
    public static final String s_emp = "electrified";
    public static final String s_flamethrower = "flamethrower";
    public static final String s_immolator = "plasma";
    public static final String s_cryolator = "ice";
    public static final String s_laser = "laser";
    public static final String s_boil = "boil";
    public static final String s_acid = "acidPlayer";

    // Добавьте этот метод в класс ModDamageSource
    public static void bootstrap(BootstapContext<DamageType> context) {
        // Регистрация всех типов урона
        context.register(NUCLEAR_BLAST, new DamageType("nuclear_blast", DamageScaling.ALWAYS, 0.1F));
        context.register(MUD_POISONING, new DamageType("mud_poisoning", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(ACID, new DamageType("acid", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(EUTHANIZED, new DamageType("euthanized", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(TAU, new DamageType("tau", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F, DamageEffects.HURT));
        context.register(RADIATION, new DamageType("radiation", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(DIGAMMA, new DamageType("digamma", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(TELEPORTER, new DamageType("teleporter", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(CHEATER, new DamageType("cheater", DamageScaling.ALWAYS, 0.0F));
        context.register(RUBBLE, new DamageType("rubble", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(SHRAPNEL, new DamageType("shrapnel", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(BLACKHOLE, new DamageType("blackhole", DamageScaling.ALWAYS, 0.1F));
        context.register(TURBOFAN, new DamageType("turbofan", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(METEORITE, new DamageType("meteorite", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(BOXCAR, new DamageType("boxcar", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(BOAT, new DamageType("boat", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(BUILDING, new DamageType("building", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(TAINT, new DamageType("taint", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(AMS, new DamageType("ams", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(AMS_CORE, new DamageType("ams_core", DamageScaling.ALWAYS, 0.1F));
        context.register(BROADCAST, new DamageType("broadcast", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(BANG, new DamageType("bang", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(PC, new DamageType("pc", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(CLOUD, new DamageType("cloud", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(LEAD, new DamageType("lead", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(ENERVATION, new DamageType("enervation", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(ELECTRICITY, new DamageType("electricity", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F, DamageEffects.HURT));
        context.register(EXHAUST, new DamageType("exhaust", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(SPIKES, new DamageType("spikes", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(LUNAR, new DamageType("lunar", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(MONOXIDE, new DamageType("monoxide", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(BLACK_LUNG, new DamageType("black_lung", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(MKU, new DamageType("mku", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(VACUUM, new DamageType("vacuum", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(OVERDOSE, new DamageType("overdose", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(MICROWAVE, new DamageType("microwave", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(NITAN, new DamageType("nitan", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(PHOSPHORUS, new DamageType("phosphorus", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F, DamageEffects.BURNING));
        context.register(HOT, new DamageType("hot", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F, DamageEffects.BURNING));
        context.register(EXPLOSIVE, new DamageType("explosive", DamageScaling.ALWAYS, 0.1F, DamageEffects.HURT));
        context.register(HYDRO, new DamageType("hydro", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(BLINDING, new DamageType("blinding", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(FIBROSIS, new DamageType("fibrosis", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(ASBESTOS, new DamageType("asbestos", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(SOOT, new DamageType("soot", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.0F));
        context.register(PHYSICAL, new DamageType("physical", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(FIRE, new DamageType("fire", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F, DamageEffects.BURNING));
        context.register(LASER, new DamageType("laser", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(SUBATOMIC, new DamageType("subatomic", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(DAMAGE_TYPE_GENERIC, new DamageType("generic", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(EUTHANIZED_SELF, new DamageType("euthanized_self", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(EUTHANIZED_SELF2, new DamageType("euthanized_self2", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
        context.register(DISPLACEMENT, new DamageType("displacement", DamageScaling.WHEN_CAUSED_BY_LIVING_NON_PLAYER, 0.1F));
    }

    private static ResourceKey<DamageType> register(String name) {
        return ResourceKey.create(Registries.DAMAGE_TYPE,
                ResLocation.ResLocation(RefStrings.MODID, name));
    }

    // Фабричные методы для создания DamageSource с сущностями

    public static DamageSource nuclearBlast(Level level) {
        return createDamageSource(NUCLEAR_BLAST, null, null, level);
    }

    public static DamageSource euthanizedSelf(LivingEntity entity) {
        return createDamageSource(EUTHANIZED_SELF, entity, entity, entity.level());
    }

    public static DamageSource euthanizedSelf2(LivingEntity entity) {
        return createDamageSource(EUTHANIZED_SELF2, entity, entity, entity.level());
    }

    public static DamageSource causeMeteoriteDamage(Level level) {
        return createDamageSource(METEORITE, null, null, level);
    }

    public static DamageSource causeBoxcarDamage(Level level) {
        return createDamageSource(BOXCAR, null, null, level);
    }

    public static DamageSource overdose(LivingEntity entity) {
        return createDamageSource(OVERDOSE, entity, entity, entity.level());
    }

    public static DamageSource broadcast(Level level) {
        return createDamageSource(BROADCAST, null, null, level);
    }

    public static DamageSource mudPoisoning(LivingEntity entity) {
        return createDamageSource(MUD_POISONING, entity, entity, entity.level());
    }

    public static DamageSource mudPoisoning(Level level) {
        return createDamageSource(MUD_POISONING, null, null, level);
    }

    public static DamageSource taint(LivingEntity entity) {
        return createDamageSource(TAINT, entity, entity, entity.level());
    }

    public static DamageSource causeDisplacementDamage(Entity directEntity, Entity causingEntity) {
        if (directEntity != null && causingEntity != null) {
            return createDamageSource(DISPLACEMENT, directEntity, causingEntity, directEntity.level());
        }
        return createCustomSource(s_emplacer, directEntity,
                directEntity != null ? directEntity.level() : null);
    }

    public static DamageSource causeSubatomicDamage(Entity directEntity, Entity causingEntity) {
        if (directEntity != null && causingEntity != null) {
            return createDamageSource(SUBATOMIC, directEntity, causingEntity, directEntity.level());
        }
        return createCustomSource(s_zomg_prefix, directEntity,
                directEntity != null ? directEntity.level() : null);
    }

    public static DamageSource causeBulletDamage(Entity projectile, Entity target) {
        if (projectile != null && target != null) {
            Entity owner = getEntityOwner(projectile);
            if (owner instanceof LivingEntity livingOwner) {
                return target.damageSources().mobProjectile(projectile, livingOwner);
            }
        }
        return createCustomSource(s_bullet, projectile,
                projectile != null ? projectile.level() :
                        target != null ? target.level() : null);
    }

    public static DamageSource causeRubbleDamage(Level level) {
        return createDamageSource(RUBBLE, null, null, level);
    }

    public static DamageSource causeShrapnelDamage(Level level) {
        return createDamageSource(SHRAPNEL, null, null, level);
    }

    public static DamageSource causeTauDamage(LivingEntity entity) {
        return createDamageSource(TAU, entity, entity, entity.level());
    }

    public static DamageSource causeTauDamage(Entity projectile, Entity target) {
        if (projectile != null && target != null) {
            Entity owner = getEntityOwner(projectile);
            if (owner instanceof LivingEntity livingOwner) {
                return createDamageSource(TAU, projectile, livingOwner, target.level());
            }
        }
        return createCustomSource(s_tau, projectile,
                target != null ? target.level() :
                        projectile != null ? projectile.level() : null);
    }

    public static DamageSource causeLaserDamage(Entity laser, Entity target) {
        if (laser != null && target != null) {
            Entity owner = getEntityOwner(laser);
            if (owner instanceof LivingEntity livingOwner) {
                return target.damageSources().mobProjectile(laser, livingOwner);
            }
        }
        return createCustomSource(s_laser, laser,
                laser != null ? laser.level() :
                        target != null ? target.level() : null);
    }

    public static DamageSource causeCombineDamage(Entity projectile, Entity target) {
        if (projectile != null && target != null) {
            Entity owner = getEntityOwner(projectile);
            if (owner instanceof LivingEntity livingOwner) {
                return target.damageSources().mobProjectile(projectile, livingOwner);
            }
        }
        return createCustomSource(s_combineball, projectile,
                target != null ? target.level() :
                        projectile != null ? projectile.level() : null);
    }

    public static DamageSource euthanized(Entity attacker, Entity target) {
        if (attacker != null && target != null) {
            if (attacker instanceof LivingEntity livingAttacker) {
                return createDamageSource(EUTHANIZED, attacker, livingAttacker, target.level());
            }
        }
        return createCustomSource(s_euthanized, attacker,
                target != null ? target.level() :
                        attacker != null ? attacker.level() : null);
    }

    // Методы для специфических типов урона
    public static DamageSource causeRadiation(Level level, @Nullable Entity source, @Nullable Entity attacker) {
        return createDamageSource(RADIATION, source, attacker, level);
    }

    public static DamageSource causeDigamma(Level level, @Nullable Entity source, @Nullable Entity attacker) {
        return createDamageSource(DIGAMMA, source, attacker, level);
    }

    public static DamageSource causeDigammaDamage(LivingEntity entity) {
        return createDamageSource(DIGAMMA, entity, entity, entity.level());
    }

    public static DamageSource causeAsbestosDamage(LivingEntity entity) {
        return createDamageSource(ASBESTOS, entity, entity, entity.level());
    }

    public static DamageSource causeBlackLungDamage(LivingEntity entity) {
        return createDamageSource(BLACK_LUNG, entity, entity, entity.level());
    }

    public static DamageSource causePhosphorusDamage(LivingEntity entity) {
        return createDamageSource(PHOSPHORUS, entity, entity, entity.level());
    }

    public static DamageSource causeExplosiveDamage(LivingEntity entity) {
        return createDamageSource(EXPLOSIVE, entity, entity, entity.level());
    }

    public static DamageSource causeLeadDamage(LivingEntity entity) {
        return createDamageSource(LEAD, entity, entity, entity.level());
    }

    public static DamageSource causeElectricity(Level level, @Nullable Entity source, @Nullable Entity attacker) {
        return createDamageSource(ELECTRICITY, source, attacker, level);
    }

    public static DamageSource causeAcid(Level level, @Nullable Entity source, @Nullable Entity attacker) {
        return createDamageSource(ACID, source, attacker, level);
    }

    public static DamageSource causePhosphorus(Level level, @Nullable Entity source, @Nullable Entity attacker) {
        return createDamageSource(PHOSPHORUS, source, attacker, level);
    }

    // Универсальный метод создания DamageSource из ResourceKey
    public static DamageSource createDamageSource(ResourceKey<DamageType> type,
                                                  @Nullable Entity directEntity,
                                                  @Nullable Entity causingEntity,
                                                  Level level) {
        var registry = level.registryAccess().registryOrThrow(Registries.DAMAGE_TYPE);
        var damageType = registry.getHolderOrThrow(type);

        if (causingEntity instanceof LivingEntity livingCauser) {
            if (directEntity != null && directEntity != causingEntity) {
                return new DamageSource(damageType, directEntity, livingCauser);
            } else {
                return new DamageSource(damageType, livingCauser);
            }
        } else if (directEntity != null) {
            return new DamageSource(damageType, directEntity);
        } else {
            return new DamageSource(damageType);
        }
    }

    // Методы для обратной совместимости (создают кастомный DamageSource с GENERIC)
    public static DamageSource createCustomSource(String type, @Nullable Entity attacker, @Nullable Level level) {
        if (level == null) return new DamageSource(level.damageSources().generic().typeHolder(), attacker);
        return new CustomDamageSource(type, attacker, level);
    }

    // Методы проверки типа урона
    public static boolean getIsBullet(DamageSource source) {
        return source.is(net.minecraft.tags.DamageTypeTags.IS_PROJECTILE) ||
                source.getMsgId().contains("bullet");
    }

    public static boolean getIsTau(DamageSource source) {
        return source.is(TAU) || source.getMsgId().contains("tau");
    }

    public static boolean getIsPoison(DamageSource source) {
        return source.is(EUTHANIZED) ||
                source.is(net.minecraft.tags.DamageTypeTags.IS_DROWNING) ||
                source.getMsgId().contains("euthanized");
    }

    public static boolean getIsCmb(DamageSource source) {
        return source.getMsgId().contains("cmb");
    }

    public static boolean getIsSubatomic(DamageSource source) {
        return source.getMsgId().startsWith("subAtomic");
    }

    public static boolean getIsFire(DamageSource source) {
        return source.is(net.minecraft.tags.DamageTypeTags.IS_FIRE) ||
                source.getMsgId().contains("flamethrower");
    }

    public static boolean getIsPlasma(DamageSource source) {
        return source.getMsgId().contains("plasma");
    }

    public static boolean getIsLiquidNitrogen(DamageSource source) {
        return source.is(net.minecraft.tags.DamageTypeTags.IS_FREEZING) ||
                source.getMsgId().contains("ice");
    }

    public static boolean getIsLaser(DamageSource source) {
        return source.getMsgId().contains("laser");
    }

    public static boolean getIsAcid(DamageSource source) {
        return source.is(ACID) || source.getMsgId().contains("acid");
    }

    public static boolean getIsRadiation(DamageSource source) {
        return source.is(RADIATION) ||
                source.is(DIGAMMA) ||
                source.getMsgId().contains("radiation") ||
                source.getMsgId().contains("digamma");
    }

    public static boolean getIsDigamma(DamageSource source) {
        return source.is(DIGAMMA) || source.getMsgId().contains("digamma");
    }

    public static boolean getIsElectricity(DamageSource source) {
        return source.is(ELECTRICITY) ||
                source.is(net.minecraft.tags.DamageTypeTags.IS_LIGHTNING) ||
                source.getMsgId().contains("electricity");
    }

    public static boolean getIsNuclearBlast(DamageSource source) {
        return source.is(NUCLEAR_BLAST) || source.getMsgId().contains("nuclear");
    }

    public static boolean getIsDischarge(DamageSource source) {
        return source.is(ELECTRICITY) || source.getMsgId().contains("discharge");
    }

    // Кастомный DamageSource для обратной совместимости
    private static class CustomDamageSource extends DamageSource {
        private final String type;
        private final Entity attacker;
        private final Level level;

        public CustomDamageSource(String type, @Nullable Entity attacker, Level level) {
            super(level.registryAccess()
                            .registryOrThrow(Registries.DAMAGE_TYPE)
                            .getHolderOrThrow(DamageTypes.GENERIC),
                    attacker);
            this.type = type;
            this.attacker = attacker;
            this.level = level;
        }

        @Override
        public @NotNull Component getLocalizedDeathMessage(LivingEntity victim) {
            String key = "death.attack." + type;
            if (attacker != null) {
                return Component.translatable(key + ".player",
                        victim.getDisplayName(), attacker.getDisplayName());
            }
            return Component.translatable(key, victim.getDisplayName());
        }

        @Override
        public @NotNull String getMsgId() {
            return type;
        }
    }

    // Получение владельца сущности
    private static Entity getEntityOwner(Entity entity) {
        if (entity instanceof OwnableEntity ownable) {
            Entity owner = ownable.getOwner();
            if (owner != null) return owner;
        }
        if (entity instanceof Projectile proj) {
            Entity owner = proj.getOwner();
            if (owner != null) return owner;
        }
        if (entity instanceof TamableAnimal tameable) {
            Entity owner = tameable.getOwner();
            if (owner != null) return owner;
        }

        var persistentData = entity.getPersistentData();

        if (persistentData.hasUUID("OwnerUUID")) {
            try {
                UUID ownerUUID = persistentData.getUUID("OwnerUUID");
                if (entity.level() != null) {
                    Entity owner = entity.level().getPlayerByUUID(ownerUUID);
                    if (owner != null) return owner;
                }
            } catch (Exception ignored) {}
        }

        return entity;
    }

}