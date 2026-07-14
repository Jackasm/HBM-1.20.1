package com.hbm.explosion.vanillant.standard;

import java.util.HashMap;
import java.util.List;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.ICustomDamageHandler;
import com.hbm.explosion.vanillant.interfaces.IEntityProcessor;
import com.hbm.explosion.vanillant.interfaces.IEntityRangeMutator;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

public class EntityProcessorStandard implements IEntityProcessor {

    protected IEntityRangeMutator range;
    protected ICustomDamageHandler damage;
    protected boolean allowSelfDamage = false;

    @Override
    public HashMap<Player, Vec3> process(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {
        HashMap<Player, Vec3> affectedPlayers = new HashMap<>();

        size *= 2.0F;

        if(range != null) {
            size = range.mutateRange(explosion, size);
        }

        AABB aabb = new AABB(
                x - size - 1.0D, y - size - 1.0D, z - size - 1.0D,
                x + size + 1.0D, y + size + 1.0D, z + size + 1.0D
        );

        List<Entity> list = world.getEntities(allowSelfDamage ? null : explosion.exploder, aabb);

        ForgeEventFactory.onExplosionDetonate(world, explosion.compat, list, explosion.size * 2.0F);
        Vec3 explosionPos = new Vec3(x, y, z);

        for(Entity entity : list) {
            double distanceScaled = entity.distanceToSqr(x, y, z) / (size * size);

            if(distanceScaled <= 1.0D) {
                Vec3 entityPos = entity.position().add(0, entity.getEyeHeight(), 0);
                Vec3 delta = entityPos.subtract(explosionPos);
                double distance = delta.length();

                if(distance != 0.0D) {
                    Vec3 direction = delta.normalize();

                    // Плотность блоков (упрощенная)
                    double density = 1.0D; // Можно реализовать более сложную логику
                    double knockback = (1.0D - Math.sqrt(distanceScaled)) * density;

                    DamageSource source = explosion.exploder != null ?
                            world.damageSources().explosion(explosion.exploder, explosion.exploder) :
                            world.damageSources().explosion(explosion.compat);

                    entity.hurt(source, calculateDamage(distanceScaled, density, knockback, size));

                    double enchKnockback = knockback;
                    if(entity instanceof LivingEntity living) {
                        int blastProtection = EnchantmentHelper.getEnchantmentLevel(
                                Enchantments.BLAST_PROTECTION,
                                living
                        );

                        if(blastProtection > 0) {
                            enchKnockback = knockback * (1.0 - (blastProtection * 0.15));
                        }
                    }

                    entity.setDeltaMovement(entity.getDeltaMovement().add(
                            direction.x * enchKnockback,
                            direction.y * enchKnockback,
                            direction.z * enchKnockback
                    ));

                    if(entity instanceof Player player && !player.isSpectator()) {
                        affectedPlayers.put(player, direction.scale(knockback));
                    }

                    if(damage != null) {
                        damage.handleAttack(explosion, entity, distanceScaled);
                    }
                }
            }
        }

        return affectedPlayers;
    }

    public float calculateDamage(double distanceScaled, double density, double knockback, float size) {
        return (float) ((int) ((knockback * knockback + knockback) / 2.0D * 8.0D * size + 1.0D));
    }

    public EntityProcessorStandard withRangeMod(float mod) {
        range = (explosion, range) -> range * mod;
        return this;
    }

    public EntityProcessorStandard withDamageMod(ICustomDamageHandler damage) {
        this.damage = damage;
        return this;
    }

    public EntityProcessorStandard allowSelfDamage() {
        this.allowSelfDamage = true;
        return this;
    }
}