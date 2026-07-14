package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class XFactory75Bolt {

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> Lego.handleStandardSmoke(ctx.entity, stack, 2000, 0.05D, 1.1D, 0);

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_BOLT = (stack, ctx) -> GunItem.setupRecoil((float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5),
            (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_TINY_EXPLODE = (bullet, hitResult) -> {
        // Проверяем, чтобы не взорваться сразу после выстрела и не нанести урон стрелку
        if (hitResult.getType() == HitResult.Type.ENTITY && bullet.tickCount < 3) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();
            if (entity == bullet.getOwner()) {
                return; // Не взрываемся, если попали в стрелка в первые 3 тика
            }
        }

        // Вызываем маленький взрыв
        Lego.tinyExplode(bullet, hitResult, 2F);

        // Уничтожаем пулю
        bullet.discard();
    };

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_INC = (bullet, hitResult) -> {
        if (hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();
            if (entity instanceof LivingEntity living) {
                int currentPhosphorus = HbmLivingProps.getPhosphorus(living);
                if (currentPhosphorus < 300) {
                    HbmLivingProps.setPhosphorus(living, 300);
                }
            }
        }
    };

    public static BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_STANDARD_EXPLODE = (bullet, mop) -> {
        Lego.standardExplode(bullet, mop, 5F);
        bullet.discard();
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_BOLTER_ANIMS = (stack, type) -> switch (type) {
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(1, 0, 0, 25)
                        .addPos(0, 0, 0, 75));
        case RELOAD -> new BusAnimation()
                .addBus("TILT", new BusAnimationSequence()
                        .addPos(1, 0, 0, 250)
                        .addPos(1, 0, 0, 1500)
                        .addPos(0, 0, 0, 250))
                .addBus("MAG", new BusAnimationSequence()
                        .addPos(0, 0, 1, 500)
                        .addPos(1, 0, 1, 500)
                        .addPos(0, 0, 0, 500));
        case JAMMED -> new BusAnimation()
                .addBus("TILT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(1, 0, 0, 250)
                        .addPos(1, 0, 0, 700)
                        .addPos(0, 0, 0, 250))
                .addBus("MAG", new BusAnimationSequence()
                        .addPos(0, 0, 0, 750)
                        .addPos(0.6, 0, 0, 250)
                        .addPos(0, 0, 0, 250));
        default -> null;
    };
}
