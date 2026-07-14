package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityFireLingering;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.main.MainRegistry;
import com.hbm.network.PacketDispatcher;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.sound.ModSounds;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.*;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import static com.hbm.util.ResLocation.ResLocation;

public class XFactoryEnergy {

    public static final ResourceLocation scope_luna =ResLocation(RefStrings.MODID, "textures/misc/scope_amat.png");

    public static BiConsumer<EntityBulletBeamBase, HitResult> LAMBDA_IR_HIT = (beam, hitResult) -> {
        // Сначала стандартный урон от луча
        BulletConfig.LAMBDA_STANDARD_BEAM_HIT.accept(beam, hitResult);

        // Обработка попадания в сущность
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            if (entity instanceof LivingEntity living) {
                HbmLivingProps props = HbmLivingProps.getData(living);
                if (props != null && props.fire < 100) {
                    props.fire = 100; // 5 секунд огня
                }
                living.setSecondsOnFire(5); // Дополнительно стандартный огонь
            }
        }

        // Обработка попадания в блок
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            Level world = beam.level();
            BlockPos hitPos = blockHit.getBlockPos();
            BlockState hitBlock = world.getBlockState(hitPos);
            Direction side = blockHit.getDirection();

            // Проверяем горючесть блока
            if (hitBlock.isFlammable(world, hitPos, side.getOpposite())) {
                BlockPos adjacentPos = hitPos.relative(side);
                BlockState adjacentBlock = world.getBlockState(adjacentPos);

                // Пытаемся поставить огонь на соседний блок
                if (adjacentBlock.isAir() && net.minecraft.world.level.block.FireBlock.canBePlacedAt(world, adjacentPos, side.getOpposite())) {
                    world.setBlockAndUpdate(adjacentPos, Blocks.FIRE.defaultBlockState());
                    return;
                }
            }

            // Создаем EntityFireLingering (должен быть зарегистрирован в ModEntities)
            EntityFireLingering fire = new EntityFireLingering(
                    ModEntities.FIRE_LINGERING.get(),
                    world
            );

            fire.setArea(2, 1)
                    .setDuration(100)
                    .setType(EntityFireLingering.TYPE_DIESEL);

            fire.setPos(
                    hitResult.getLocation().x,
                    hitResult.getLocation().y,
                    hitResult.getLocation().z
            );

            world.addFreshEntity(fire);
        }
    };

    public static BiConsumer<EntityBulletBeamBase, HitResult> LAMBDA_LIGHTNING_HIT = (beam, hitResult) -> {

        Level world = beam.level();

        Vec3 hitPos;
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            BlockHitResult blockHit = (BlockHitResult) hitResult;
            Direction dir = blockHit.getDirection();
            hitPos = hitResult.getLocation()
                    .add(dir.getStepX() * 0.5,
                            dir.getStepY() * 0.5,
                            dir.getStepZ() * 0.5);
        } else {
            hitPos = hitResult.getLocation();
        }

        ExplosionVNT vnt = new ExplosionVNT(world,
                hitPos.x, hitPos.y, hitPos.z,
                2F, beam.getThrower());

        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, beam.getDamage())
                .setupPiercing(Objects.requireNonNull(beam.getConfig()).armorThresholdNegation,
                        beam.getConfig().armorPiercingPercent)
                .setDamageClass(beam.getConfig().dmgClass));

        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.explode();

        world.playSound(null,
                hitPos.x, hitPos.y, hitPos.z,
                ModSounds.ENTITY_UFOBLAST.get(),
                SoundSource.PLAYERS,
                5.0F, 0.9F + world.random.nextFloat() * 0.2F);

        world.playSound(null,
                hitPos.x, hitPos.y, hitPos.z,
                SoundEvents.FIREWORK_ROCKET_BLAST,
                SoundSource.PLAYERS,
                5.0F, 0.5F);

        // Эффекты частиц
        float yaw = world.random.nextFloat() * 180F;
        for(int i = 0; i < 3; i++) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "plasmablast");
            data.putFloat("r", 0.5F);
            data.putFloat("g", 0.5F);
            data.putFloat("b", 1.0F);
            data.putFloat("pitch", -60F + 60F * i);
            data.putFloat("yaw", yaw);
            data.putFloat("scale", 2F);

            LivingEntity shooter = beam.getThrower();
            if (!world.isClientSide()) {
                PacketDispatcher.sendAuxParticleNT(
                        data,
                        hitPos.x, hitPos.y, hitPos.z,
                        shooter
                );
            }
        }

        // Эффекты на сущность
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            if (entity instanceof LivingEntity living) {
                // Добавляем эффекты замедления
                living.addEffect(new MobEffectInstance(
                        MobEffects.MOVEMENT_SLOWDOWN,
                        60, // 3 секунды (20 тиков в секунду)
                        9,  // Уровень 10 (0-based)
                        false, // Не частицы
                        true,  // Показывать иконку
                        true   // Показывать частицы
                ));

                living.addEffect(new MobEffectInstance(
                        MobEffects.DIG_SLOWDOWN,
                        60,
                        9,
                        false,
                        true,
                        true
                ));

                // Дополнительно: эффект молнии/шока
                living.addEffect(new MobEffectInstance(
                        MobEffects.CONFUSION,
                        40, // 2 секунды
                        0,
                        false,
                        true,
                        true
                ));

                // Визуальный эффект молнии
                if (!world.isClientSide()) {
                    world.addFreshEntity(new LightningBolt(
                            EntityType.LIGHTNING_BOLT,
                            world
                    ) {
                        {
                            this.setPos(hitPos.x, hitPos.y, hitPos.z);
                            this.setVisualOnly(true); // Только визуальный эффект
                        }
                    });
                }
            }
        }
    };

    public static BiConsumer<EntityBulletBeamBase, HitResult> LAMBDA_LIGHTNING_SPLIT = (beam, hitResult) -> {
        // Сначала стандартный удар молнии
        LAMBDA_LIGHTNING_HIT.accept(beam, hitResult);

        // Работаем только если попали в сущность
        if (hitResult.getType() != HitResult.Type.ENTITY) return;

        Level world = beam.level();
        Entity mainTarget = ((EntityHitResult) hitResult).getEntity();
        Vec3 hitPos = hitResult.getLocation();

        double range = 20.0;

        // Получаем все живые сущности в радиусе
        AABB searchArea = new AABB(hitPos, hitPos).inflate(range);
        List<LivingEntity> potentialTargets = world.getEntitiesOfClass(
                LivingEntity.class,
                searchArea,
                entity -> {
                    // Исключаем самого стрелка и основную цель
                    if (entity == beam.getThrower()) return false;
                    if (entity == mainTarget) return false;

                    // Проверяем дистанцию
                    double distance = entity.position().distanceTo(hitPos);
                    return distance <= range;
                }
        );

        // Перемешиваем список для случайного порядка
        Collections.shuffle(potentialTargets);

        // Создаем дополнительные лучи
        for (LivingEntity target : potentialTargets) {
            if (target == beam.getThrower() || target == mainTarget) continue;

            // Вектор от точки попадания к цели
            Vec3 targetPos = target.position().add(0, target.getBbHeight() / 2, 0);
            Vec3 delta = targetPos.subtract(hitPos);
            double distance = delta.length();

            if (distance > range) continue;

            // Создаем дочерний луч
            EntityBulletBeamBase sub = new EntityBulletBeamBase(
                    ModEntities.BULLET_BEAM.get(),
                    world,
                    beam.getThrower(),
                    beam.getConfig(), // Или специальный конфиг для дочернего луча
                    beam.getDamage() * 0.5f // Например, половина урона
            );

            // Устанавливаем позицию в точке попадания
            sub.setPos(hitPos.x, hitPos.y, hitPos.z);

            // Устанавливаем направление к цели
            sub.setRotationsFromVector(delta);

            // Выполняем hitscan на расстояние до цели
            sub.performHitscanExternal(distance);

            // Добавляем в мир
            world.addFreshEntity(sub);
        }
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_ENERGY = (stack, ctx) -> { };

    @SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_TESLA_ANIMS = (stack, type) -> {
        int amount = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory());
        return switch (type) {
            case EQUIP -> new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 1000, IType.SIN_DOWN));
            case CYCLE -> new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, GunItem.getIsAiming(stack) ? -0.5 : -1, 100, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("CYCLE", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0, 22.5, 350))
                    .addBus("COUNT", new BusAnimationSequence().addPos(amount, 0, 0, 0));
            case CYCLE_DRY -> new BusAnimation()
                    .addBus("CYCLE", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0, 22.5, 350));
            case INSPECT -> new BusAnimation()
                    .addBus("YOMI", new BusAnimationSequence().addPos(8, -4, 0, 0).addPos(4, -1, 0, 500, IType.SIN_DOWN).addPos(4, -1, 0, 1000).addPos(6, -6, 0, 500, IType.SIN_UP))
                    .addBus("SQUEEZE", new BusAnimationSequence().addPos(1, 1, 1, 0).addPos(1, 1, 1, 750).addPos(1, 1, 0.5, 125).addPos(1, 1, 1, 125));
            default -> null;
        };

    };

    @SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_LASER_PISTOL = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_FULL));
        case RELOAD -> new BusAnimation()
                .addBus("LATCH", new BusAnimationSequence().addPos(0, -20, 0, 100).hold(1900).addPos(0, 0, 0, 100))
                .addBus("LIFT", new BusAnimationSequence().hold(100).addPos(-45, 0, 0, 250, IType.SIN_FULL).hold(500).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("JOLT", new BusAnimationSequence().hold(350).addPos(0, 0, 0.5, 100, IType.SIN_FULL).addPos(0, 0, -1.5, 100, IType.SIN_UP).addPos(0, 0, 0, 150, IType.SIN_FULL).holdUntil(2100).addPos(-0.0625, 0, 0, 50, IType.SIN_UP).addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("BATTERY", new BusAnimationSequence().hold(550).addPos(0, 0, 5, 250).hold(550).setPos(0, -2, -2).addPos(0, 0, -2, 250, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_UP));
        case JAMMED -> new BusAnimation()
                .addBus("LATCH", new BusAnimationSequence().hold(500).addPos(0, -20, 0, 100).hold(250).addPos(0, 0, 0, 100))
                .addBus("JOLT", new BusAnimationSequence().hold(950).addPos(-0.0625, 0, 0, 50, IType.SIN_UP).addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("EQUIP", new BusAnimationSequence().hold(1500).addPos(7.5, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL));
        case INSPECT -> new BusAnimation()
                .addBus("SWIRL", new BusAnimationSequence().addPos(-720, 0, 0, 750, IType.SIN_FULL).hold(500).addPos(0, 0, 0, 750, IType.SIN_FULL));
        default -> null;
    };

    @SuppressWarnings("incomplete-switch") public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_LASRIFLE = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_FULL));
        case RELOAD -> new BusAnimation()
                .addBus("LEVER", new BusAnimationSequence().addPos(-90, 0, 0, 350, IType.SIN_UP).addPos(-90, 0, 0, 1500).addPos(0, 0, 0, 350, IType.SIN_UP))
                .addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 350).addPos(0, -5, 0, 350, IType.SIN_UP).addPos(0, -5, 0, 500).addPos(0, -0.25, 0, 500, IType.SIN_FULL).addPos(0, -0.25, 0, 150).addPos(0, 0, 0, 350))
                .addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 1700).addPos(-2, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
        case JAMMED -> new BusAnimation()
                .addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-90, 0, 0, 350, IType.SIN_UP).addPos(-90, 0, 0, 600).addPos(0, 0, 0, 350, IType.SIN_UP))
                .addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 350).addPos(0, -2, 0, 200, IType.SIN_UP).addPos(0, -0.25, 0, 250, IType.SIN_FULL).addPos(0, -0.25, 0, 150).addPos(0, 0, 0, 350))
                .addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 800).addPos(-2, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
        case INSPECT -> new BusAnimation()
                .addBus("LEVER", new BusAnimationSequence().addPos(-90, 0, 0, 350, IType.SIN_UP).addPos(-90, 0, 0, 600).addPos(0, 0, 0, 350, IType.SIN_UP))
                .addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 350).addPos(0, -2, 0, 200, IType.SIN_UP).addPos(0, -0.25, 0, 250, IType.SIN_FULL).addPos(0, -0.25, 0, 150).addPos(0, 0, 0, 350))
                .addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 800).addPos(-2, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
        default -> null;
    };
}
