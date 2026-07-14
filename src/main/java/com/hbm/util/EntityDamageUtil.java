package com.hbm.util;

import java.util.*;
import com.hbm.handler.ArmorResistanceHandler;

import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeHooks;

public class EntityDamageUtil {

    // Кэш для хранения lastHurt, так как поле protected
    private static final Map<LivingEntity, Float> LAST_HURT_CACHE = new HashMap<>();
    private static final Map<LivingEntity, Float> PREV_HEALTH_CACHE = new HashMap<>();

    /** Shitty hack, if the first attack fails, it retries with damage + previous damage, allowing damage to penetrate */
    @Deprecated
    public static boolean attackEntityFromIgnoreIFrame(Entity victim, DamageSource src, float damage) {
        if(!victim.hurt(src, damage)) {
            if(victim instanceof LivingEntity living) {
                if(living.invulnerableTime > living.invulnerableDuration / 2.0F) {
                    float lastDamage = getLastHurt(living);
                    damage += lastDamage;
                }
            }
            return victim.hurt(src, damage);
        } else {
            return true;
        }
    }

    /** New and improved entity damage calc - only use this one */
    public static boolean attackEntityFromNT(LivingEntity living, DamageSource source, float amount,
                                             boolean ignoreIFrame, double knockbackMultiplier,
                                             float pierceDT, float pierceDR) {
        if(living instanceof Player player && source.getEntity() instanceof Player attacker) {
            if(!player.canHarmPlayer(attacker)) return false;
        }

        ArmorResistanceHandler.setupPierce(pierceDT, pierceDR);
        boolean ret = attackEntityFromNTInternal(living, source, amount, ignoreIFrame, knockbackMultiplier);
        ArmorResistanceHandler.resetPierce();
        return ret;
    }

    private static boolean attackEntityFromNTInternal(LivingEntity living, DamageSource source, float amount,
                                                      boolean ignoreIFrame, double knockbackMultiplier) {
        return attackEntitySuperCompatibility(living, source, amount, ignoreIFrame, knockbackMultiplier);
    }

    /**
     * MK2 SEDNA damage system
     */
    private static boolean attackEntitySuperCompatibility(LivingEntity living, DamageSource source, float amount,
                                                          boolean ignoreIFrame, double knockbackMultiplier) {
        // disable iframes
        if(ignoreIFrame) {
            setLastHurt(living, 0F);
            living.invulnerableTime = 0;
        }

        // cache last velocity
        Vec3 motion = living.getDeltaMovement();

        // apply damage
        boolean ret = living.hurt(source, amount);

        // restore last velocity
        living.setDeltaMovement(motion);

        // apply own knockback
        Entity entity = source.getEntity();
        if(entity != null) {
            double deltaX = entity.getX() - living.getX();
            double deltaZ;

            for(deltaZ = entity.getZ() - living.getZ(); deltaX * deltaX + deltaZ * deltaZ < 1.0E-4D;
                deltaZ = (Math.random() - Math.random()) * 0.01D) {
                deltaX = (Math.random() - Math.random()) * 0.01D;
            }

            living.yRotO = (float) (Math.atan2(deltaZ, deltaX) * 180.0D / Math.PI) - living.getYRot();
            if(knockbackMultiplier > 0) knockBack(living, entity, amount, deltaX, deltaZ, knockbackMultiplier);
        }
        return ret;
    }



    public static void knockBack(LivingEntity living, Entity attacker, float damage, double motionX, double motionZ, double multiplier) {
        if(living.getRandom().nextDouble() >= living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE)) {
            living.hasImpulse = true;
            double horizontal = Math.sqrt(motionX * motionX + motionZ * motionZ);
            double magnitude = 0.4D * multiplier;
            Vec3 motion = living.getDeltaMovement();
            motion = motion.scale(0.5D);
            motion = motion.add(-motionX / horizontal * magnitude, magnitude, -motionZ / horizontal * magnitude);

            if(motion.y() > 0.2D) {
                motion = new Vec3(motion.x(), 0.2D * multiplier, motion.z());
            }

            living.setDeltaMovement(motion);
        }
    }

    public static void damageEntityNT(LivingEntity living, DamageSource source, float amount) {
        if(!living.isInvulnerableTo(source)) {
            amount = ForgeHooks.onLivingHurt(living, source, amount);
            if(amount <= 0) return;

            // Use ArmorResistanceHandler for DT/DR calculation
            amount = ArmorResistanceHandler.calculateDamage(living, source, amount);

            // Additional calculations with potions and enchantments
            amount = applyPotionDamageCalculations(living, source, amount);

            if(amount != 0.0F) {
                float health = living.getHealth();
                living.setHealth(health - amount);
                living.getCombatTracker().recordDamage(source, amount);
            }
        }
    }

    @Deprecated
    public static boolean attackEntityFromNT(LivingEntity living, DamageSource source, float amount) {
        return attackEntityFromNT(living, source, amount, false, 1.0, 0, 0);
    }

    public static float applyPotionDamageCalculations(LivingEntity living, DamageSource source, float amount) {
        if(source.is(DamageTypeTags.BYPASSES_EFFECTS)) {
            return amount;
        } else {
            if(living.hasEffect(MobEffects.DAMAGE_RESISTANCE) && source != living.damageSources().fellOutOfWorld()) {
                int resistance = (Objects.requireNonNull(living.getEffect(MobEffects.DAMAGE_RESISTANCE)).getAmplifier() + 1) * 5;
                int j = 25 - resistance;
                float f1 = amount * (float) j;
                amount = f1 / 25.0F;
            }

            if(amount <= 0.0F) {
                return 0.0F;
            } else {
                int resistance = EnchantmentHelper.getDamageProtection(living.getArmorSlots(), source);

                if(resistance > 20) {
                    resistance = 20;
                }

                if(resistance > 0 && resistance <= 20) {
                    int j = 25 - resistance;
                    float f1 = amount * (float) j;
                    amount = f1 / 25.0F;
                }

                return amount;
            }
        }
    }

    public static void setBeenAttacked(LivingEntity living) {
        living.hurtMarked = living.getRandom().nextDouble() >= living.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE);
    }

    public static HitResult getMouseOver(Player attacker, double reach) {
        Level world = attacker.level();
        HitResult objectMouseOver;
        Entity pointedEntity = null;

        objectMouseOver = rayTrace(attacker, reach, 1F);

        Vec3 pos = getPosition(attacker);
        Vec3 look = attacker.getViewVector(1F);
        Vec3 end = pos.add(look.x * reach, look.y * reach, look.z * reach);
        Vec3 hitvec = null;
        float grace = 1.0F;

        AABB searchBox = attacker.getBoundingBox()
                .expandTowards(look.x * reach, look.y * reach, look.z * reach)
                .inflate(grace);

        List<Entity> list = world.getEntities(attacker, searchBox, Entity::isPickable);

        double closest = reach;

        for(Entity entity : list) {
            if(entity.isPickable()) {
                AABB axisalignedbb = entity.getBoundingBox().inflate(entity.getPickRadius());

                // Исправление: clip() возвращает Optional<Vec3>
                Optional<Vec3> optionalHit = axisalignedbb.clip(pos, end);

                if(axisalignedbb.contains(pos)) {
                    if(0.0D <= closest) {
                        pointedEntity = entity;
                        hitvec = pos; // Используем позицию игрока
                        closest = 0.0D;
                    }
                } else if(optionalHit.isPresent()) {
                    Vec3 hitLocation = optionalHit.get();
                    double dist = pos.distanceTo(hitLocation);

                    if(dist < closest || closest == 0.0D) {
                        if(entity == attacker.getVehicle() && !entity.canRiderInteract()) {
                            if(closest == 0.0D) {
                                pointedEntity = entity;
                                hitvec = hitLocation;
                            }
                        } else {
                            pointedEntity = entity;
                            hitvec = hitLocation;
                            closest = dist;
                        }
                    }
                }
            }
        }

        if(pointedEntity != null && (closest < reach || objectMouseOver == null)) {
            objectMouseOver = new EntityHitResult(pointedEntity, hitvec);
        }

        return objectMouseOver;
    }

    public static HitResult rayTrace(Player player, double dist, float interp) {
        Vec3 pos = getPosition(player);
        Vec3 look = player.getViewVector(interp);
        Vec3 end = pos.add(look.x * dist, look.y * dist, look.z * dist);
        return player.level().clip(new ClipContext(pos, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));
    }

    public static Vec3 getPosition(Player player) {
        return new Vec3(player.getX(), player.getY() + player.getEyeHeight(), player.getZ());
    }

    // ============ ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ДЛЯ КЭША ============

    private static float getLastHurt(LivingEntity living) {
        return LAST_HURT_CACHE.getOrDefault(living, 0F);
    }

    private static void setLastHurt(LivingEntity living, float amount) {
        LAST_HURT_CACHE.put(living, amount);
    }

    private static float getPrevHealth(LivingEntity living) {
        return PREV_HEALTH_CACHE.getOrDefault(living, living.getHealth());
    }

    private static void setPrevHealth(LivingEntity living, float health) {
        PREV_HEALTH_CACHE.put(living, health);
    }

    private static boolean canHarmCreativePlayer(DamageSource source, Player player) {
        // Источники урона, которые могут навредить игроку в креативном режиме
        return source == player.damageSources().fellOutOfWorld() || // выпадение из мира
                source == player.damageSources().genericKill() || // команда /kill
                source.is(net.minecraft.tags.DamageTypeTags.BYPASSES_INVULNERABILITY) || // обходит неуязвимость
                (source.getEntity() instanceof Player attacker && attacker.isCreative() &&
                        attacker != player); // PvP в креативном режиме (если включено)
    }

    // Метод для очистки кэша (вызывать при смерти сущности или при необходимости)
    public static void clearCache(LivingEntity living) {
        LAST_HURT_CACHE.remove(living);
        PREV_HEALTH_CACHE.remove(living);
    }

    public static void clearAllCache() {
        LAST_HURT_CACHE.clear();
        PREV_HEALTH_CACHE.clear();
    }

}