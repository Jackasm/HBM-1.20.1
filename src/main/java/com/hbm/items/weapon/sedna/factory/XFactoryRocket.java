package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityFireLingering;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.impl.ItemGunStinger;
import com.hbm.main.MainRegistry;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.util.EntityDamageUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class XFactoryRocket {

    public static Consumer<Entity> LAMBDA_STANDARD_ACCELERATE = (entity) -> {
        EntityBulletBaseMK4 bullet = (EntityBulletBaseMK4) entity;
        if(bullet.accel < 7) bullet.accel += 0.4D;
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE_HEAT = (bullet, hitResult) -> {

        // Если попали в сущность и пуля только что создана - выходим
        if (hitResult.getType() == HitResult.Type.ENTITY && bullet.tickCount < 3) return;

        // Стандартный взрыв
        Lego.standardExplode(bullet, hitResult, 3.5F);
        bullet.discard();

        // Дополнительный урон по живым сущностям
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity entity = ((EntityHitResult) hitResult).getEntity();
            DamageSource source = BulletConfig.getDamage(bullet, (LivingEntity) bullet.getOwner(), BulletConfig.DamageClass.EXPLOSIVE);

            if (entity instanceof LivingEntity living) {
                EntityDamageUtil.attackEntityFromNT(
                        living,
                        source,
                        bullet.getDamage() * 3F,
                        true, 0.5F, 5F, 0.2F
                );
            } else {
                entity.hurt(source, bullet.getDamage() * 3F);
            }
        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE_DEMO = (bullet, hitResult) -> {

        // Если попали в сущность и пуля только что создана - выходим
        if (hitResult.getType() == HitResult.Type.ENTITY && bullet.tickCount < 3) return;

        // Создаем взрыв
        ExplosionVNT vnt = new ExplosionVNT(
                bullet.level(),
                hitResult.getLocation().x,
                hitResult.getLocation().y,
                hitResult.getLocation().z,
                5F,
                bullet.getOwner()
        );

        vnt.setBlockAllocator(new BlockAllocatorStandard());
        vnt.setBlockProcessor(new BlockProcessorStandard());
        vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, bullet.getDamage())
                .setupPiercing(bullet.getConfig().armorThresholdNegation,
                        bullet.getConfig().armorPiercingPercent));
        vnt.setPlayerProcessor(new PlayerProcessorStandard());
        vnt.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
        vnt.explode();

        bullet.discard();
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_ROCKET = (stack, ctx) -> { };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE_INC = (bullet, hitResult) -> spawnFire(bullet, hitResult, false, 300);

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE_PHOSPHORUS = (bullet, hitResult) -> spawnFire(bullet, hitResult, true, 600);


    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_STINGER_SECONDARY_PRESS = (stack, ctx)
            -> ItemGunStinger.setIsLockingOn(stack, true);
    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_STINGER_SECONDARY_RELEASE = (stack, ctx)
            -> ItemGunStinger.setIsLockingOn(stack, false);

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_MISSILE_LAUNCHER_PRIMARY_PRESS = (stack, ctx) -> {
        if(GunItem.getIsAiming(stack)) {
            int target = ItemGunStinger.getLockonTarget(ctx.getPlayer(), 150D, 20D);
            if(target != -1) {
                GunItem.setLockonTarget(stack, target);
                GunItem.setIsLockedOn(stack, true);
            }
        }
        Lego.LAMBDA_STANDARD_CLICK_PRIMARY.accept(stack, ctx);
        GunItem.setIsLockedOn(stack, false);
    };

    public static void spawnFire(EntityBulletBaseMK4 bullet, HitResult hitResult, boolean phosphorus, int duration) {
        // Если попали в сущность и пуля только что создана - выходим
        if (hitResult.getType() == HitResult.Type.ENTITY && bullet.tickCount < 3) return;

        Level world = bullet.level();

        // Стандартный взрыв
        Lego.standardExplode(bullet, hitResult, 3F);

        // Создаем пожар
        EntityFireLingering fire = new EntityFireLingering(
                ModEntities.FIRE_LINGERING.get(),
                world
        );
        fire.setArea(6, 2)
                .setDuration(duration)
                .setType(phosphorus ? EntityFireLingering.TYPE_PHOSPHORUS : EntityFireLingering.TYPE_DIESEL);

        fire.setPos(
                hitResult.getLocation().x,
                hitResult.getLocation().y,
                hitResult.getLocation().z
        );

        world.addFreshEntity(fire);
        bullet.discard();

        // Распространение огня на соседние блоки
        int baseX = (int) Math.floor(hitResult.getLocation().x);
        int baseY = (int) Math.floor(hitResult.getLocation().y);
        int baseZ = (int) Math.floor(hitResult.getLocation().z);

        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                for (int dz = -2; dz <= 2; dz++) {
                    int x = baseX + dx;
                    int y = baseY + dy;
                    int z = baseZ + dz;
                    BlockPos pos = new BlockPos(x, y, z);

                    if (world.isEmptyBlock(pos)) {
                        // Проверяем все 6 направлений
                        for (Direction dir : Direction.values()) {
                            BlockPos neighborPos = pos.relative(dir);

                            // Проверяем, горючий ли соседний блок
                            BlockState neighborState = world.getBlockState(neighborPos);
                            if (neighborState.isFlammable(world, neighborPos, dir.getOpposite())) {
                                // Пытаемся поставить огонь
                                if (net.minecraft.world.level.block.FireBlock.canBePlacedAt(world, pos, dir.getOpposite())) {
                                    world.setBlockAndUpdate(pos, Blocks.FIRE.defaultBlockState());
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    public static Consumer<Entity> LAMBDA_STEERING_ACCELERATE = (entity) -> {
        EntityBulletBaseMK4 bullet = (EntityBulletBaseMK4) entity;

        // Ускорение
        if (bullet.accel < 4) bullet.accel += 0.4D;

        // Проверка стрелка
        if (bullet.getOwner() == null || !(bullet.getOwner() instanceof Player player)) return;

        // Проверка дистанции
        Vec3 bulletPos = bullet.position();
        Vec3 playerPos = player.position();
        if (bulletPos.distanceTo(playerPos) > 100) return;

        // Проверка оружия в руке
        ItemStack heldItem = player.getMainHandItem();
        if (heldItem.isEmpty() || !(heldItem.getItem() instanceof GunItem) ||
                !GunItem.getIsAiming(heldItem)) return;

        // Raytrace для определения точки прицеливания
        HitResult hitResult = rayTrace(player, 200, 1);
        if (hitResult == null || hitResult.getLocation() == null) return;

        // Вектор от пули к точке прицеливания
        Vec3 targetVec = new Vec3(
                hitResult.getLocation().x - bulletPos.x,
                hitResult.getLocation().y - bulletPos.y,
                hitResult.getLocation().z - bulletPos.z
        );

        // Минимальная дистанция для коррекции
        if (targetVec.length() < 3) return;

        // Нормализуем вектор направления
        targetVec = targetVec.normalize();

        // Текущая скорость пули
        Vec3 motion = bullet.getDeltaMovement();
        double speed = motion.length();

        // Устанавливаем новое направление с сохранением скорости
        bullet.setDeltaMovement(
                targetVec.x * speed,
                targetVec.y * speed,
                targetVec.z * speed
        );
    };

    public static HitResult rayTrace(Player player, double length, float partialTicks) {
        Vec3 start = player.getEyePosition(partialTicks);
        Vec3 look = player.getViewVector(partialTicks);
        Vec3 end = start.add(look.scale(length));

        // Используем ClipContext для настройки того, что нужно проверять
        ClipContext.Fluid fluidMode = ClipContext.Fluid.NONE; // или ANY, если нужны жидкости
        ClipContext.Block blockMode = ClipContext.Block.COLLIDER; // или OUTLINE, VISUAL

        ClipContext context = new ClipContext(start, end, blockMode, fluidMode, player);
        return player.level().clip(context);
    }

    public static HitResult rayTrace(Player player, double length, float partialTicks,
                                     boolean allowLiquids, boolean ignoreBlocksWithoutCollisions) {
        Vec3 start = player.getEyePosition(partialTicks);
        Vec3 look = player.getViewVector(partialTicks);
        Vec3 end = start.add(look.scale(length));

        ClipContext.Fluid fluidMode = allowLiquids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE;
        ClipContext.Block blockMode = ignoreBlocksWithoutCollisions ?
                ClipContext.Block.OUTLINE : ClipContext.Block.COLLIDER;

        ClipContext context = new ClipContext(start, end, blockMode, fluidMode, player);
        return player.level().clip(context);
    }

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_PANZERSCHRECK_ANIMS = (stack, type) -> {
        boolean empty = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0)
                .getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory()) <= 0;
        switch(type) {
            case EQUIP: return new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
            case RELOAD: return new BusAnimation()
                    .addBus("RELOAD", new BusAnimationSequence().addPos(90, 0, 0, 750, IType.SIN_FULL).addPos(90, 0, 0, 1000).addPos(0, 0, 0, 750, IType.SIN_FULL))
                    .addBus("ROCKET", new BusAnimationSequence().addPos(0, -3, -6, 0).addPos(0, -3, -6, 750).addPos(0, 0, -6.5, 500, IType.SIN_DOWN).addPos(0, 0, 0, 350, IType.SIN_UP));
            case JAMMED: empty = false;
            case INSPECT:
                return new BusAnimation()
                        .addBus("RELOAD", new BusAnimationSequence().addPos(90, 0, 0, 750, IType.SIN_FULL).addPos(90, 0, 0, 500).addPos(0, 0, 0, 750, IType.SIN_FULL))
                        .addBus("ROCKET", new BusAnimationSequence().addPos(0, empty ? -3 : 0, 0, 0));
        }
        return null;
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_QUADRO_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50).addPos(0, 0, 0, 50));
        case RELOAD -> new BusAnimation()
                .addBus("RELOAD_ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 500, IType.SIN_FULL).addPos(0, 0, 60, 1500).addPos(0, 0, 0, 750, IType.SIN_FULL))
                .addBus("RELOAD_PUSH", new BusAnimationSequence().addPos(-1, -1, 0, 0).addPos(-1, -1, 0, 500).addPos(-1, 0, 0, 350).addPos(0, 0, 0, 1000));
        case JAMMED, INSPECT -> new BusAnimation()
                .addBus("RELOAD_ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 750, IType.SIN_FULL).addPos(0, 0, 60, 500).addPos(0, 0, 0, 750, IType.SIN_FULL));
        default -> null;
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_MISSILE_LAUNCHER_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 1000, IType.SIN_DOWN));
        case RELOAD -> new BusAnimation()
                .addBus("BARREL", new BusAnimationSequence().addPos(0, 0, 1.5, 150).addPos(0, 0, 1.5, 2100).addPos(0, 0, 0, 150))
                .addBus("OPEN", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(90, 0, 0, 500, IType.SIN_FULL).addPos(90, 0, 0, 1000).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 2250).addPos(-1, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_UP))
                .addBus("MISSILE", new BusAnimationSequence().addPos(-10, 0, 0, 0).addPos(-10, 0, 0, 750).addPos(3, 0, 2, 0).addPos(0, 0, -6, 350, IType.SIN_FULL).addPos(0, 0, 0, 350, IType.SIN_UP));
        case JAMMED, INSPECT -> new BusAnimation()
                .addBus("BARREL", new BusAnimationSequence().addPos(0, 0, 1.5, 150).addPos(0, 0, 1.5, 1350).addPos(0, 0, 0, 150))
                .addBus("OPEN", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(90, 0, 0, 500, IType.SIN_FULL).addPos(90, 0, 0, 250).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 1500).addPos(-1, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_UP));
        default -> null;
    };
}
