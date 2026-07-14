package com.hbm.explosion;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.ModEntities;
import com.hbm.entity.grenade.EntityGrenadeTau;
import com.hbm.entity.grenade.EntityGrenadeZOMG;
import com.hbm.entity.item.EntityFallingBlockNT;
import com.hbm.entity.particle.EntityCloudFX;
import com.hbm.entity.particle.EntityModFX;
import com.hbm.entity.particle.EntityOrangeFX;
import com.hbm.entity.particle.EntityPinkCloudFX;
import com.hbm.entity.projectile.EntityBullet;
import com.hbm.entity.projectile.EntityRainbow;
import com.hbm.entity.projectile.EntityRocket;
import com.hbm.entity.projectile.EntitySchrab;
import com.hbm.potion.HbmPotion;
import com.hbm.sound.ModSounds;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Sheep;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

public class ExplosionChaos {

    private final static Random random = new Random();

    public static void zomgMeSinPi(Level level, double x, double y, double z, int count, @Nullable Entity shooter,
                                   EntityGrenadeZOMG zomg) {

        for (int i = 0; i < count; i++) {
            double d1 = random.nextDouble();
            double d2 = random.nextDouble();
            double d3 = random.nextDouble();

            if (random.nextInt(2) == 0) d1 *= -1;
            if (random.nextInt(2) == 0) d2 *= -1;
            if (random.nextInt(2) == 0) d3 *= -1;

            EntityRainbow entityZomg = new EntityRainbow(
                    ModEntities.RAINBOW.get(),
                    level,
                    shooter instanceof Player player ? player : null,
                    1.0F, 10000, 100000,
                    zomg
            );

            entityZomg.setDeltaMovement(d1, d2, d3);
            entityZomg.setOwner(shooter);

            level.addFreshEntity(entityZomg);
            level.playSound(null, zomg.getX(), zomg.getY(), zomg.getZ(),
                    ModSounds.ZOMG_SHOOT.get(), SoundSource.HOSTILE, 10.0F, 0.8F + (random.nextFloat() * 0.4F));
        }
    }

    public static void explodeZOMG(Level level, BlockPos pos, int bombStartStrength) {
        int r = bombStartStrength;
        int r2 = r * r;
        int r22 = r2 / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int xx = -r; xx < r; xx++) {
            int X = xx + pos.getX();
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + pos.getY();
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + pos.getZ();
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        mutable.set(X, Y, Z);
                        BlockState state = level.getBlockState(mutable);

                        // Проверяем, не является ли блок бедроком на уровне 0 или ниже
                        if (!(state.is(Blocks.BEDROCK) && Y <= level.getMinBuildHeight())) {
                            level.setBlock(mutable, Blocks.AIR.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    public static void frag(Level level, BlockPos pos, int count, boolean flame, Entity shooter) {
        for (int i = 0; i < count; i++) {

            double theta = random.nextDouble() * 2 * Math.PI;
            double phi = random.nextDouble() * Math.PI;

            double d1 = Math.sin(phi) * Math.cos(theta);
            double d2 = Math.cos(phi);
            double d3 = Math.sin(phi) * Math.sin(theta);

            float speed = 0.15f + random.nextFloat();

            Arrow arrow = new Arrow(EntityType.ARROW, level);

            double spawnX = pos.getX() + 0.5 + d1 * 1.5;
            double spawnY = pos.getY() + 0.5 + d2 * 1.5;
            double spawnZ = pos.getZ() + 0.5 + d3 * 1.5;

            arrow.setPos(spawnX, spawnY, spawnZ);

            arrow.setDeltaMovement(d1 * speed, d2 * speed, d3 * speed);

            arrow.setCritArrow(true);

            if (flame) {
                arrow.setSecondsOnFire(1000);
            }

            arrow.setBaseDamage(2.5);

            level.addFreshEntity(arrow);
        }
    }

    public static void schrab(Level level, BlockPos pos, int count, int gravity) {
        for (int i = 0; i < count; i++) {
            double d1 = random.nextDouble();
            double d2 = random.nextDouble();
            double d3 = random.nextDouble();

            if (random.nextInt(2) == 0) d1 *= -1;
            if (random.nextInt(2) == 0) d3 *= -1;

            EntitySchrab fragment = new EntitySchrab(
                    ModEntities.SCHRAB.get(),
                    level,
                    pos.getX(), pos.getY(), pos.getZ(),
                    d1, d2, d3,
                    0.0125D
            );

            level.addFreshEntity(fragment);
        }
    }

    public static void spawnVolley(Level level, BlockPos pos, int count, double speed) {
        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        for (int i = 0; i < count; i++) {
            // Используем конструктор с Level, x, y, z, mx, my, mz
            EntityOrangeFX fx = new EntityOrangeFX(
                    level,
                    x + (random.nextDouble() - 0.5) * 2.0,
                    y + random.nextDouble() * 2.0,
                    z + (random.nextDouble() - 0.5) * 2.0,
                    random.nextGaussian() * speed,
                    random.nextDouble() * speed * 7.5D,
                    random.nextGaussian() * speed
            );

            level.addFreshEntity(fx);
        }
    }

    public static void poison(Level level, double x, double y, double z, double range) {
        AABB box = new AABB(
                x - range, y - range, z - range,
                x + range, y + range, z + range
        );

        List<LivingEntity> affected = level.getEntitiesOfClass(LivingEntity.class, box);

        for (LivingEntity entity : affected) {
            if (entity.distanceToSqr(x, y, z) > range * range) continue;

            if (ArmorRegistry.hasAnyProtection(entity, 3, ArmorRegistry.HazardClass.GAS_LUNG, ArmorRegistry.HazardClass.GAS_BLISTERING)) {
                ArmorUtil.damageGasMaskFilter(entity, 1);
            } else {
                entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 5 * 20, 0));
                entity.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 20, 2));
                entity.addEffect(new MobEffectInstance(MobEffects.WITHER, 1 * 20, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30 * 20, 1));
                entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30 * 20, 2));
            }
        }
    }

    public static void pulse(Level level, BlockPos pos, int bombStartStrength) {
        int r = bombStartStrength;
        int r2 = r * r;
        int r22 = r2 / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int xx = -r; xx < r; xx++) {
            int X = xx + pos.getX();
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + pos.getY();
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + pos.getZ();
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        mutable.set(X, Y, Z);
                        BlockState state = level.getBlockState(mutable);

                        if (state.getBlock().getExplosionResistance() <= 70) {
                            EntityFallingBlockNT falling = new EntityFallingBlockNT(
                                    ModEntities.FALLING_BLOCK_NT.get(),
                                    level,
                                    X + 0.5, Y + 0.5, Z + 0.5,
                                    state
                            );
                            {
                                double dx = (X + 0.5) - (pos.getX() + 0.5);
                                double dy = (Y + 0.5) - (pos.getY() + 0.5);
                                double dz = (Z + 0.5) - (pos.getZ() + 0.5);

                                // Нормализуем и умножаем на силу
                                double length = Math.sqrt(dx * dx + dy * dy + dz * dz);
                                if (length > 0) {
                                    double force = 2.0 * (1 - length / 7); // Сила уменьшается с расстоянием
                                    falling.setDeltaMovement(
                                            dx / length * force,
                                            dy / length * force + 0.5, // Плюс немного вверх
                                            dz / length * force
                                    );
                                }
                            }
                            /* Вариант:
                            falling.setDeltaMovement(
                                (level.random.nextDouble() - 0.5) * 2,
                                level.random.nextDouble() * 1.5,
                                (level.random.nextDouble() - 0.5) * 2
                            );

                             */


                            level.addFreshEntity(falling);
                        }
                    }
                }
            }
        }
    }

    public static void plasma(Level level, BlockPos pos, int radius) {
        int r = radius;
        int r2 = r * r;
        int r22 = r2 / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int xx = -r; xx < r; xx++) {
            int X = xx + pos.getX();
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + pos.getY();
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + pos.getZ();
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22 + level.random.nextInt(Math.max(1, r22 / 2))) {
                        mutable.set(X, Y, Z);
                        BlockState state = level.getBlockState(mutable);

                        if (state.getBlock() != ModBlocks.STATUE_ELB_F.get()) {
                            level.setBlock(mutable, ModBlocks.PLASMA.get().defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    public static void tauMeSinPi(Level level, double x, double y, double z, int count, Entity shooter,
                                  EntityGrenadeTau tau) {

        if (!(shooter instanceof Player player)) return;

        for (int i = 0; i < count; i++) {
            double d1 = random.nextDouble();
            double d2 = random.nextDouble();
            double d3 = random.nextDouble();

            if (random.nextInt(2) == 0) d1 *= -1;
            if (random.nextInt(2) == 0) d2 *= -1;
            if (random.nextInt(2) == 0) d3 *= -1;

            EntityBullet fragment;

            if (random.nextInt(5) == 0) {
                fragment = new EntityBullet(level, player, 3.0F, 35, 45, false, "tauDay", tau);
                fragment.setDamage(random.nextInt(301) + 100);
            } else {
                fragment = new EntityBullet(level, player, 3.0F, 35, 45, false, "eyyOk", tau);
                fragment.setDamage(random.nextInt(11) + 35);
            }

            fragment.setDeltaMovement(d1 * 5, d2 * 5, d3 * 5);
            fragment.setOwner(shooter);
            fragment.setCritical(true);

            level.addFreshEntity(fragment);
        }
    }

    public static void spawnPoisonCloud(ServerLevel level, double x, double y, double z, int count, double speed, int type) {
        for (int i = 0; i < count; i++) {
            EntityModFX fx;

            if (type == 1) {
                fx = new EntityCloudFX(level, x, y, z, random.nextGaussian() * speed, random.nextGaussian() * speed, random.nextGaussian() * speed);
            } else if (type == 2) {
                fx = new EntityPinkCloudFX(level, x, y, z, random.nextGaussian() * speed, random.nextGaussian() * speed, random.nextGaussian() * speed);
            } else {
                fx = new EntityOrangeFX(level, x, y, z, random.nextGaussian() * speed, random.nextGaussian() * speed, random.nextGaussian() * speed);
            }

            level.addFreshEntity(fx);
        }
    }

    public static void pinkCloud(Level level, double x, double y, double z, double range) {
        AABB box = new AABB(
                x - range, y - range, z - range,
                x + range, y + range, z + range
        );

        List<LivingEntity> affected = level.getEntitiesOfClass(LivingEntity.class, box);

        for (LivingEntity entity : affected) {
            if (entity.distanceToSqr(x, y, z) > range * range) continue;

            ArmorUtil.damageSuit(entity, 0, 25);
            ArmorUtil.damageSuit(entity, 1, 25);
            ArmorUtil.damageSuit(entity, 2, 25);
            ArmorUtil.damageSuit(entity, 3, 25);

            DamageSource pcSource = ModDamageSource.createDamageSource(
                    ModDamageSource.PC, null, null, level);
            entity.hurt(pcSource, 5);
        }
    }

    public static void cloud(Level level, double x, double y, double z, double range) {
        AABB box = new AABB(
                x - range, y - range, z - range,
                x + range, y + range, z + range
        );

        List<LivingEntity> affected = level.getEntitiesOfClass(LivingEntity.class, box);

        for (LivingEntity entity : affected) {
            if (entity.distanceToSqr(x, y, z) > range * range) continue;

            ArmorUtil.damageSuit(entity, 0, 25);
            ArmorUtil.damageSuit(entity, 1, 25);
            ArmorUtil.damageSuit(entity, 2, 25);
            ArmorUtil.damageSuit(entity, 3, 25);

            if (ArmorUtil.checkForHazmat(entity)) continue;

            if (entity.hasEffect(HbmPotion.TAINT.get())) {
                entity.removeEffect(HbmPotion.TAINT.get());
                entity.addEffect(new MobEffectInstance(HbmPotion.MUTATION.get(), 1 * 60 * 60 * 20, 0));
            }

            DamageSource cloudSource = ModDamageSource.createDamageSource(
                    ModDamageSource.CLOUD, null, null, level);
            entity.hurt(cloudSource, 5);
        }
    }

    public static void burn(Level level, BlockPos pos, int bound) {
        int r = bound;
        int r2 = r * r;
        int r22 = r2 / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int xx = -r; xx < r; xx++) {
            int X = xx + pos.getX();
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + pos.getY();
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + pos.getZ();
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        mutable.set(X, Y, Z);
                        BlockPos above = mutable.above();
                        BlockState state = level.getBlockState(mutable);
                        BlockState aboveState = level.getBlockState(above);

                        if (!state.isAir() && (aboveState.isAir() || aboveState.is(Blocks.SNOW))) {
                            level.setBlock(above, Blocks.FIRE.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    public static void flameDeath(Level level, BlockPos pos, int bound) {
        int r = bound;
        int r2 = r * r;
        int r22 = r2 / 2;
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        for (int xx = -r; xx < r; xx++) {
            int X = xx + pos.getX();
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + pos.getY();
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + pos.getZ();
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        mutable.set(X, Y, Z);
                        BlockPos above = mutable.above();
                        BlockState state = level.getBlockState(mutable);

                        if (state.isFlammable(level, mutable, Direction.UP) &&
                                level.getBlockState(above).isAir()) {
                            level.setBlock(above, Blocks.FIRE.defaultBlockState(), 3);
                        }
                    }
                }
            }
        }
    }

    public static void cluster(Level level, BlockPos pos, int count, int gravity) {
        for (int i = 0; i < count; i++) {
            double d1 = random.nextDouble();
            double d2 = random.nextDouble();
            double d3 = random.nextDouble();

            if (random.nextInt(2) == 0) d1 *= -1;
            if (random.nextInt(2) == 0) d3 *= -1;

            EntityRocket fragment = new EntityRocket(
                    ModEntities.ROCKET.get(),
                    level,
                    pos.getX(), pos.getY(), pos.getZ(),
                    d1, d2, d3,
                    0.0125D
            );

            level.addFreshEntity(fragment);
        }
    }

    public static void floater(Level level, int x, int y, int z, int radi, int height) {

        int r = radi;
        int r2 = r * r;
        int r22 = r2 / 2;

        for (int xx = -r; xx < r; xx++) {
            int X = xx + x;
            int XX = xx * xx;
            for (int yy = -r; yy < r; yy++) {
                int Y = yy + y;
                int YY = XX + yy * yy;
                for (int zz = -r; zz < r; zz++) {
                    int Z = zz + z;
                    int ZZ = YY + zz * zz;
                    if (ZZ < r22) {
                        BlockPos pos = new BlockPos(X, Y, Z);
                        BlockState state = level.getBlockState(pos);

                        level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);

                        if (!state.isAir()) {
                            BlockPos newPos = new BlockPos(X, Y + height, Z);
                            level.setBlock(newPos, state, 3);
                        }
                    }
                }
            }
        }
    }

    public static void move(Level level, int x, int y, int z, int radius, int a, int b, int c) {
        float f = radius;
        double wat = radius;

        radius *= 2.0F;
        int minX = Mth.floor(x - wat - 1.0D);
        int maxX = Mth.floor(x + wat + 1.0D);
        int minY = Mth.floor(y - wat - 1.0D);
        int maxY = Mth.floor(y + wat + 1.0D);
        int minZ = Mth.floor(z - wat - 1.0D);
        int maxZ = Mth.floor(z + wat + 1.0D);

        AABB aabb = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        List<Entity> list = level.getEntities((Entity) null, aabb);

        for (Entity entity : list) {
            double distance = entity.distanceToSqr(x, y, z) / (radius * radius);
            if (distance <= 1.0D) {
                double d5 = entity.getX() - x;
                double d6 = entity.getY() + entity.getEyeHeight() - y;
                double d7 = entity.getZ() - z;

                if (entity instanceof LivingEntity living && !(entity instanceof Sheep)) {
                    int rand = level.random.nextInt(2);
                    if (rand == 0) {
                        living.setCustomName(Component.literal("Dinnerbone"));
                    } else {
                        living.setCustomName(Component.literal("Grumm"));
                    }
                }

                if (entity instanceof Sheep sheep) {
                    sheep.setCustomName(Component.literal("jeb_"));
                }

                double d9 = Math.sqrt(d5 * d5 + d6 * d6 + d7 * d7);
                if (d9 < wat) {
                    entity.setPos(entity.getX() + a, entity.getY() + b, entity.getZ() + c);
                }
            }
        }

        radius = (int) f;
    }
}
