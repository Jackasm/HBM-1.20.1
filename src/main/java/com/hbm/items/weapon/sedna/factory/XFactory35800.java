package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityFireLingering;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class XFactory35800 {

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_ABERRATOR =
            (stack, ctx) -> GunItem.setupRecoil(10,
                    (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    public static BiConsumer<EntityBulletBeamBase, HitResult> LAMBDA_BLACK_IMPACT = (bullet, hitResult) -> {

        // Обработка попадания в сущность
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            Entity hit = ((EntityHitResult) hitResult).getEntity();
            if (hit instanceof LivingEntity living) {
                // Точно как в оригинале: добавляем 200 к blackFire
                HbmLivingProps.getData(living).blackFire += 200;
            }
        }

        // Обработка попадания в блок
        if (hitResult.getType() == HitResult.Type.BLOCK) {
            // Создаем EntityFireLingering с черным огнем
            EntityFireLingering fire = new EntityFireLingering(
                    ModEntities.FIRE_LINGERING.get(),
                    bullet.level()
            );

            // Точно как в оригинале: 7.5F, 2F, 200, TYPE_BLACK
            fire.setArea(7.5F, 2F)
                    .setDuration(200)
                    .setType(EntityFireLingering.TYPE_BLACK);

            // Позиционируем в точке попадания
            Vec3 hitPos = hitResult.getLocation();
            fire.setPos(hitPos.x, hitPos.y, hitPos.z);

            // Добавляем в мир
            bullet.level().addFreshEntity(fire);
        }

        // Вызов стандартного обработчика (если он есть в 1.20.1)
        if (BulletConfig.LAMBDA_STANDARD_BEAM_HIT != null) {
            BulletConfig.LAMBDA_STANDARD_BEAM_HIT.accept(bullet, hitResult);
        }
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_ABERRATOR = (stack, type) -> {
        boolean aim = GunItem.getIsAiming(stack);
        int ammo = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmountBeforeReload(stack);
        return switch (type) {
            case EQUIP -> new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(360, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("RISE", new BusAnimationSequence().addPos(0, -3, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
            case CYCLE -> new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(aim ? -15 : -25, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("SIGHT", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(aim ? 5 : 15, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -1.125, 50, IType.SIN_DOWN).addPos(0, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP))
                    .addBus(ammo <= 1 ? "NULL" : "BULLET", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0.375, 1.125, 150, IType.SIN_UP))
                    .addBus("HAMMER", new BusAnimationSequence().addPos(45, 0, 0, 50).addPos(-45, 0, -1.125, 50, IType.SIN_DOWN).addPos(-20, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP));
            case CYCLE_DRY -> new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(-5, 0, 0, 100, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 550).addPos(0, 0, -1.125, 150, IType.SIN_FULL).addPos(0, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP))
                    .addBus("HAMMER", new BusAnimationSequence().addPos(45, 0, 0, 50).addPos(45, 0, 0, 500).addPos(-45, 0, -1.125, 150, IType.SIN_FULL).addPos(-20, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP));
            case RELOAD -> new BusAnimation()
                    .addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 20, 150, IType.SIN_FULL).addPos(0, 0, 20, 50).addPos(0, 0, -45, 150, IType.SIN_UP).addPos(0, 0, 0, 150, IType.SIN_FULL))
                    .addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 350).addPos(0, -2, 0, 0).addPos(-15, -5, 0, 350).addPos(-15, 0, 0, 0).addPos(-15, 0, 0, 700).addPos(3, 3, 0, 0).addPos(0, -2, 0, 250, IType.SIN_DOWN).addPos(0, -2, 0, 50).addPos(0, 0, 0, 150, IType.SIN_DOWN))
                    .addBus("MAGROLL", new BusAnimationSequence().addPos(0, 0, 0, 350).addPos(0, 0, -180, 250).addPos(0, 0, 0, 0))
                    .addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 750).addPos(5, 0, 0, 150, IType.SIN_FULL).addPos(-190, 0, 0, 500, IType.SIN_FULL).addPos(-190, 0, 0, 450).addPos(-360, 0, 0, 350, IType.SIN_DOWN).addPos(0, 0, 0, 0))
                    .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 2350).addPos(-5, 0, 0, 100, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 2200).addPos(0, 0, -1.125, 150, IType.SIN_FULL).addPos(0, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP))
                    .addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 0, 2250).addPos(-45, 0, -1.125, 100, IType.SIN_FULL).addPos(-20, 0, -1.125, 50).addPos(0, 0, 0, 150, IType.SIN_UP))
                    .addBus("BULLET", new BusAnimationSequence().addPos(ammo > 0 ? 0 : -100, 0, 0, 0).addPos(ammo > 0 ? 0 : -100, 0, 0, 2400).addPos(0, 0, 0, 0).addPos(0, 0.375, 1.125, 150, IType.SIN_UP));
            case INSPECT -> new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 0).addPos(-720, 0, 0, 1000, IType.SIN_FULL).addPos(-720, 0, 0, 250).addPos(0, 0, 0, 1000, IType.SIN_FULL));
            default -> null;
        };

    };
}
