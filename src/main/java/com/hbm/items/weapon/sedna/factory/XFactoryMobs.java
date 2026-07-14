package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.ExplosionNukeGeneric;

import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.network.PacketDispatcher;
import com.hbm.sound.ModSounds;
import com.hbm.util.BobMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Random;

import static com.hbm.items.weapon.sedna.factory.BulletConfigRegistry.*;

/**
 * Фабрика конфигураций снарядов для мобов/NPC
 * Аналог старого GunNPCFactory для системы Sedna
 * Все конфиги регистрируются здесь, без использования BulletConfigSyncingUtil
 */
public class XFactoryMobs {

    private static final Random RANDOM = new Random();
    private static final int SEARCH_RADIUS = 50;



    // ==================== ОБРАБОТЧИКИ ====================

    /**
     * Обновление Maskman Orb - спавнит болты вокруг
     */
    public static void updateMaskmanOrb(Entity entity) {
        if (!(entity instanceof EntityBulletBaseMK4 bullet)) return;
        if (bullet.level().isClientSide()) return;
        if (bullet.tickCount % 10 != 5) return;

        Level world = bullet.level();
        if (!(world instanceof ServerLevel)) return;

        AABB searchBox = new AABB(
                bullet.getX() - SEARCH_RADIUS,
                bullet.getY() - SEARCH_RADIUS,
                bullet.getZ() - SEARCH_RADIUS,
                bullet.getX() + SEARCH_RADIUS,
                bullet.getY() + SEARCH_RADIUS,
                bullet.getZ() + SEARCH_RADIUS
        );

        List<Player> players = world.getEntitiesOfClass(Player.class, searchBox);

        for (Player player : players) {
            Vec3 motion = new Vec3(
                    player.getX() - bullet.getX(),
                    (player.getY() + player.getEyeHeight()) - bullet.getY(),
                    player.getZ() - bullet.getZ()
            ).normalize();

            EntityBulletBaseMK4 bolt = createBullet(
                    world,
                    bullet.getOwner(),
                    MASKMAN_BOLT,
                    bullet.getX(),
                    bullet.getY(),
                    bullet.getZ(),
                    motion,
                    0.5F
            );
            bolt.setNoGravity(true);
            world.addFreshEntity(bolt);
        }
    }

    /**
     * Попадание Maskman Tracer - спавнит метеор
     */
    public static void onMaskmanTracerImpact(EntityBulletBaseMK4 bullet, HitResult hitResult) {
        if (bullet.level().isClientSide()) return;

        EntityBulletBaseMK4 meteor = createBullet(
                bullet.level(),
                bullet.getOwner(),
                MASKMAN_METEOR,
                bullet.getX(),
                bullet.getY() + 30 + RANDOM.nextInt(10),
                bullet.getZ(),
                new Vec3(0, -1, 0),
                1.0F
        );
        bullet.level().addFreshEntity(meteor);
    }

    /**
     * Обновление Maskman Meteor - частицы огня на клиенте
     */
    public static void updateMaskmanMeteor(Entity entity) {
        if (!(entity instanceof EntityBulletBaseMK4 bullet)) return;
        if (!bullet.level().isClientSide()) return;

        RandomSource rand = bullet.level().random;
        for (int i = 0; i < 5; i++) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("type", "vanillaExt");
            nbt.putString("mode", "flame");
            nbt.putDouble("x", bullet.getX() + rand.nextDouble() * 0.5 - 0.25);
            nbt.putDouble("y", bullet.getY() + rand.nextDouble() * 0.5 - 0.25);
            nbt.putDouble("z", bullet.getZ() + rand.nextDouble() * 0.5 - 0.25);
            PacketDispatcher.sendAuxParticleNT(nbt, bullet.getX(), bullet.getY(), bullet.getZ(), null);
        }
    }

    /**
     * Обновление UFO Rocket - самонаведение
     */
    public static void updateUfoRocket(Entity entity) {
        if (!(entity instanceof EntityBulletBaseMK4 bullet)) return;
        if (bullet.level().isClientSide()) return;

        CompoundTag data = bullet.getPersistentData();
        int targetId = data.getInt("homingTarget");
        Entity target = bullet.level().getEntity(targetId);

        if (target == null || !target.isAlive()) {
            chooseTarget(bullet);
            target = bullet.level().getEntity(data.getInt("homingTarget"));
            if (target == null) return;
        }

        if (bullet.distanceTo(target) < 5) {
            if (bullet.getConfig().onImpact != null) {
                bullet.getConfig().onImpact.accept(bullet, null);
            }
            bullet.discard();
            return;
        }

        Vec3 delta = new Vec3(
                target.getX() - bullet.getX(),
                target.getY() + target.getBbHeight() / 2 - bullet.getY(),
                target.getZ() - bullet.getZ()
        ).normalize();

        double speed = bullet.getDeltaMovement().length();
        bullet.setDeltaMovement(delta.x * speed, delta.y * speed, delta.z * speed);
    }

    /**
     * Попадание UFO Rocket - взрыв и эффекты
     */
    public static void onUfoRocketImpact(EntityBulletBaseMK4 bullet, HitResult hitResult) {
        Level world = bullet.level();

        world.playSound(null, bullet.getX(), bullet.getY(), bullet.getZ(),
                ModSounds.ENTITY_UFOBLAST.get(),
                SoundSource.PLAYERS,
                5.0F, 0.9F + world.random.nextFloat() * 0.2F);

        world.playSound(null, bullet.getX(), bullet.getY(), bullet.getZ(),
                SoundEvents.FIREWORK_ROCKET_BLAST,
                SoundSource.PLAYERS,
                5.0F, 0.5F);

        ExplosionNukeGeneric.dealDamage(world, bullet.getX(), bullet.getY(), bullet.getZ(), 10, 50);

        for (int i = 0; i < 3; i++) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "plasmablast");
            data.putFloat("r", 0.0F);
            data.putFloat("g", 0.75F);
            data.putFloat("b", 1.0F);
            data.putFloat("pitch", -30F + 30F * i);
            data.putFloat("yaw", world.random.nextFloat() * 180F);
            data.putFloat("scale", 5F);

            Entity owner = bullet.getOwner();
            PacketDispatcher.sendAuxParticleNT(
                    data,
                    bullet.getX(), bullet.getY(), bullet.getZ(),
                    owner != null ? owner : bullet
            );
        }
    }

    // ==================== ВСПОМОГАТЕЛЬНЫЕ МЕТОДЫ ====================

    /**
     * Создание пули с позицией и направлением
     */
    public static EntityBulletBaseMK4 createBullet(Level level, Entity shooter, BulletConfig config,
                                                   double x, double y, double z, Vec3 direction, float speed) {
        LivingEntity livingShooter = shooter instanceof LivingEntity ? (LivingEntity) shooter : null;
        EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(
                level,
                livingShooter,
                config,
                config.damageMult,
                0F,
                x, y, z,
                direction.x * speed,
                direction.y * speed,
                direction.z * speed
        );
        bullet.setOwner(shooter);
        return bullet;
    }

    /**
     * Выбор цели для самонаведения
     */
    private static void chooseTarget(EntityBulletBaseMK4 bullet) {
        Level world = bullet.level();
        CompoundTag data = bullet.getPersistentData();

        final double ANGLE = 90.0;
        final double RANGE = 100.0;

        Vec3 mot = bullet.getDeltaMovement().normalize();

        AABB searchBox = new AABB(
                bullet.getX() - RANGE,
                bullet.getY() - RANGE,
                bullet.getZ() - RANGE,
                bullet.getX() + RANGE,
                bullet.getY() + RANGE,
                bullet.getZ() + RANGE
        );

        List<LivingEntity> entities = world.getEntitiesOfClass(LivingEntity.class, searchBox);

        LivingEntity target = null;
        double targetAngle = ANGLE;

        for (LivingEntity e : entities) {
            if (!e.isAlive() || e == bullet.getOwner()) continue;

            Vec3 delta = new Vec3(
                    e.getX() - bullet.getX(),
                    e.getY() + e.getBbHeight() / 2 - bullet.getY(),
                    e.getZ() - bullet.getZ()
            );

            // Проверка видимости (raytrace)
            Vec3 start = new Vec3(bullet.getX(), bullet.getY(), bullet.getZ());
            Vec3 end = new Vec3(e.getX(), e.getY() + e.getBbHeight() / 2, e.getZ());
            BlockHitResult rayTrace = world.clip(
                    new ClipContext(start, end, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null)
            );

            if (rayTrace != null && rayTrace.getType() != HitResult.Type.MISS) continue;

            double distance = bullet.distanceToSqr(e);
            if (distance > RANGE * RANGE) continue;

            double deltaAngle = BobMathUtil.getCrossAngle(mot, delta);

            if (deltaAngle < targetAngle) {
                target = e;
                targetAngle = deltaAngle;
            }
        }

        if (target != null) {
            data.putInt("homingTarget", target.getId());
        }
    }

    /**
     * Создание лазерного снаряда для UFO
     */
    public static EntityBulletBaseMK4 createWormLaser(Level world, Entity shooter, Vec3 pos, Vec3 heading) {
        LivingEntity livingShooter = shooter instanceof LivingEntity ? (LivingEntity) shooter : null;
        EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(
                world,
                livingShooter,
                WORM_LASER,
                WORM_LASER.damageMult,
                0F,
                pos.x, pos.y, pos.z,
                heading.x * 2F,
                heading.y * 2F,
                heading.z * 2F
        );
        bullet.setOwner(shooter);
        return bullet;
    }

    /**
     * Создание ракетного снаряда для UFO с самонаведением
     */
    public static EntityBulletBaseMK4 createUfoRocket(Level world, Entity shooter, Vec3 pos, Vec3 heading, Entity target) {
        LivingEntity livingShooter = shooter instanceof LivingEntity ? (LivingEntity) shooter : null;
        EntityBulletBaseMK4 bullet = new EntityBulletBaseMK4(
                world,
                livingShooter,
                UFO_ROCKET,
                UFO_ROCKET.damageMult,
                0F,
                pos.x, pos.y, pos.z,
                heading.x * 2F,
                heading.y * 2F,
                heading.z * 2F
        );
        bullet.setOwner(shooter);
        if (target != null) {
            bullet.getPersistentData().putInt("homingTarget", target.getId());
        }
        return bullet;
    }
}
