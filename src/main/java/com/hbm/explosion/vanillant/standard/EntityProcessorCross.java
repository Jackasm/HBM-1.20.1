package com.hbm.explosion.vanillant.standard;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.interfaces.ICustomDamageHandler;
import com.hbm.explosion.vanillant.interfaces.IEntityProcessor;
import com.hbm.explosion.vanillant.interfaces.IEntityRangeMutator;

import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityProcessorCross implements IEntityProcessor {

    protected double nodeDist = 2D;
    protected IEntityRangeMutator range;
    protected ICustomDamageHandler damage;
    protected double knockbackMult = 1D;
    protected boolean allowSelfDamage = false;

    public EntityProcessorCross(double nodeDist) {
        this.nodeDist = nodeDist;
    }

    public EntityProcessorCross setAllowSelfDamage() {
        this.allowSelfDamage = true;
        return this;
    }

    public EntityProcessorCross setKnockback(double mult) {
        this.knockbackMult = mult;
        return this;
    }

    @Override
    public HashMap<Player, Vec3> process(ExplosionVNT explosion, Level world, double x, double y, double z, float size) {

        HashMap<Player, Vec3> affectedPlayers = new HashMap<>();

        size *= 2.0F;

        if (range != null) {
            size = range.mutateRange(explosion, size);
        }

        double minX = x - (double) size - 1.0D;
        double maxX = x + (double) size + 1.0D;
        double minY = y - (double) size - 1.0D;
        double maxY = y + (double) size + 1.0D;
        double minZ = z - (double) size - 1.0D;
        double maxZ = z + (double) size + 1.0D;

        AABB searchBox = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        List<Entity> list = world.getEntities(allowSelfDamage ? null : explosion.exploder, searchBox);

        ForgeEventFactory.onExplosionDetonate(world, explosion.compat, list, size);

        Vec3[] nodes = new Vec3[7];

        for (int i = 0; i < 7; i++) {
            Direction dir = Direction.from3DDataValue(i);
            nodes[i] = new Vec3(x + dir.getStepX() * nodeDist,
                    y + dir.getStepY() * nodeDist,
                    z + dir.getStepZ() * nodeDist);
        }

        HashMap<Entity, Float> damageMap = new HashMap<>();

        for (Entity entity : list) {

            double xDist = (entity.getBoundingBox().minX <= x && entity.getBoundingBox().maxX >= x) ? 0 :
                    Math.min(Math.abs(entity.getBoundingBox().minX - x), Math.abs(entity.getBoundingBox().maxX - x));
            double yDist = (entity.getBoundingBox().minY <= y && entity.getBoundingBox().maxY >= y) ? 0 :
                    Math.min(Math.abs(entity.getBoundingBox().minY - y), Math.abs(entity.getBoundingBox().maxY - y));
            double zDist = (entity.getBoundingBox().minZ <= z && entity.getBoundingBox().maxZ >= z) ? 0 :
                    Math.min(Math.abs(entity.getBoundingBox().minZ - z), Math.abs(entity.getBoundingBox().maxZ - z));
            double dist = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
            double distanceScaled = dist / size;

            if (distanceScaled <= 1.0D) {

                double deltaX = entity.getX() - x;
                double deltaY = entity.getY() + entity.getEyeHeight() - y;
                double deltaZ = entity.getZ() - z;
                double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

                if (distance != 0.0D) {

                    deltaX /= distance;
                    deltaY /= distance;
                    deltaZ /= distance;

                    double density = 0;

                    for (Vec3 vec : nodes) {
                        BlockPos pos = BlockPos.containing(vec);
                        double d = getBlockDensity(world, vec, entity.getBoundingBox());
                        if (d > density) {
                            density = d;
                        }
                    }

                    double knockback = (1.0D - distanceScaled) * density;

                    float dmg = calculateDamage(distanceScaled, density, knockback, size);
                    if (!damageMap.containsKey(entity) || damageMap.get(entity) < dmg) {
                        damageMap.put(entity, dmg);
                    }

                    double enchKnockback = knockback; // EnchantmentProtection убран

                    if (!(entity instanceof com.hbm.entity.projectile.EntityBulletBaseMK4)) {
                        entity.setDeltaMovement(
                                entity.getDeltaMovement().add(deltaX * enchKnockback * knockbackMult,
                                        deltaY * enchKnockback * knockbackMult,
                                        deltaZ * enchKnockback * knockbackMult)
                        );
                    }

                    if (entity instanceof Player player) {
                        affectedPlayers.put(player, new Vec3(deltaX * knockback * knockbackMult,
                                deltaY * knockback * knockbackMult,
                                deltaZ * knockback * knockbackMult));
                    }
                }
            }
        }

        for (Map.Entry<Entity, Float> entry : damageMap.entrySet()) {

            Entity entity = entry.getKey();
            attackEntity(entity, explosion, entry.getValue());

            if (damage != null) {
                double xDist = (entity.getBoundingBox().minX <= x && entity.getBoundingBox().maxX >= x) ? 0 :
                        Math.min(Math.abs(entity.getBoundingBox().minX - x), Math.abs(entity.getBoundingBox().maxX - x));
                double yDist = (entity.getBoundingBox().minY <= y && entity.getBoundingBox().maxY >= y) ? 0 :
                        Math.min(Math.abs(entity.getBoundingBox().minY - y), Math.abs(entity.getBoundingBox().maxY - y));
                double zDist = (entity.getBoundingBox().minZ <= z && entity.getBoundingBox().maxZ >= z) ? 0 :
                        Math.min(Math.abs(entity.getBoundingBox().minZ - z), Math.abs(entity.getBoundingBox().maxZ - z));
                double dist = Math.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
                double distanceScaled = dist / size;
                damage.handleAttack(explosion, entity, distanceScaled);
            }
        }

        return affectedPlayers;
    }

    protected float calculateDamage(double distanceScaled, double density, double knockback, float size) {
        return (float) ((int) ((knockback * knockback + knockback) / 2.0D * 8.0D * size + 1.0D));
    }

    protected void attackEntity(Entity entity, ExplosionVNT source, float amount) {
        entity.hurt(getExplosionDamageSource(source), amount);
    }

    protected DamageSource getExplosionDamageSource(ExplosionVNT source) {
        Level level = source.world;
        if (level == null) return level.damageSources().generic();

        return ModDamageSource.createDamageSource(
                ModDamageSource.EXPLOSIVE,
                source.compat != null ? source.compat.getDirectSourceEntity() : null,
                source.exploder instanceof LivingEntity ? (LivingEntity) source.exploder : null,
                level
        );
    }

    private double getBlockDensity(Level world, Vec3 vec, AABB box) {
        // Имитация старого getBlockDensity
        ClipContext context = new ClipContext(vec,
                new Vec3(box.minX + (box.maxX - box.minX) * 0.5,
                        box.minY + (box.maxY - box.minY) * 0.5,
                        box.minZ + (box.maxZ - box.minZ) * 0.5),
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                null);
        return world.clip(context).getType() == net.minecraft.world.phys.HitResult.Type.MISS ? 1.0 : 0.0;
    }

    public EntityProcessorCross withRangeMod(float mod) {
        this.range = (explosion, range) -> range * mod;
        return this;
    }

    public EntityProcessorCross withDamageMod(ICustomDamageHandler damage) {
        this.damage = damage;
        return this;
    }
}