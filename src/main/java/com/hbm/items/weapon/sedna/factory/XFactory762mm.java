package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.main.MainRegistry;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class XFactory762mm {


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
            Lego.tinyExplode(bullet, hitResult, 1.5F);

            // Уничтожаем пулю
            bullet.discard();
        };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> Lego.handleStandardSmoke(ctx.entity, stack, 1500, 0.075D, 1.1D, 0);

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_CARBINE = (stack, ctx) -> GunItem.setupRecoil(5, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.5));

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_MINIGUN = (stack, ctx) -> GunItem.setupRecoil((float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.5),
            (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.5));

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_LACUNAE = (stack, ctx) -> { };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_CARBINE_ANIMS = (stack, type) -> {
        int ammo = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory());
        return switch (type) {
            case EQUIP -> new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
            case CYCLE -> new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, GunItem.getIsAiming(stack) ? -0.25 : -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, -1, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_UP))
                    .addBus(ammo <= 1 ? "NULL" : "REL", new BusAnimationSequence().addPos(0, 0, 0.25, 50).addPos(0, 0.125, 1.25, 100, IType.SIN_UP));
            case CYCLE_DRY -> new BusAnimation()
                    .addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, -1, 50).addPos(0, 0, 0, 100, IType.SIN_UP));
            case RELOAD -> new BusAnimation()
                    .addBus("MAG", new BusAnimationSequence().addPos(0, -4, 0, 250, IType.SIN_UP).addPos(0, -4, 0, 750).addPos(0, 0, 0, 500, IType.SIN_DOWN))
                    .addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 1000))
                    .addBus("BULLET", new BusAnimationSequence().addPos(ammo == 0 ? 1 : 0, 0, 0, 0).addPos(0, 0, 0, 1000));
            case RELOAD_END -> new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence().addPos(-25, 0, 0, 0).addPos(-25, 0, 0, 750).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, -1, 50).addPos(0, 0, 0, 100, IType.SIN_UP))
                    .addBus("REL", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, 0.25, 150).addPos(0, 0.125, 1.25, 100, IType.SIN_UP));
            case JAMMED -> new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence().addPos(-25, 0, 0, 0).addPos(-25, 0, 0, 750).addPos(0, 0, 0, 500, IType.SIN_FULL).addPos(0, 0, 0, 250).addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 750).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, -1, 50).addPos(0, 0, -0.25, 100, IType.SIN_UP).addPos(0, 0, -0.25, 1250).addPos(0, 0, -1, 100, IType.SIN_DOWN).addPos(0, 0, -1, 50).addPos(0, 0, 0, 100, IType.SIN_UP))
                    .addBus("REL", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, 0.25, 150).addPos(0, 0.125, 1, 100, IType.SIN_UP).addPos(0, 0.125, 1, 1250).addPos(0, 0.125, 0.25, 100, IType.SIN_DOWN).addPos(0, 0.125, 1, 100, IType.SIN_UP));
            case INSPECT -> new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence().addPos(-25, 0, 0, 250, IType.SIN_FULL).addPos(-25, 0, 0, 1500).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -0.75, 150, IType.SIN_DOWN).addPos(0, 0, -0.75, 1000).addPos(0, 0, 0, 100, IType.SIN_UP))
                    .addBus(ammo == 0 ? "NULL" : "REL", new BusAnimationSequence().addPos(0, 0.125, 1.25, 0).addPos(0, 0.125, 1.25, 500).addPos(0, 0.125, 0.5, 150, IType.SIN_DOWN).addPos(0, 0.125, 0.5, 1000).addPos(0, 0.125, 1.25, 100, IType.SIN_UP));
            default -> null;
        };

    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_MINIGUN_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 1000, IType.SIN_FULL));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, GunItem.getIsAiming(stack) ? -0.25 : -0.5, 0).addPos(0, 0, GunItem.getIsAiming(stack) ? -0.25 : -0.5, 100).addPos(0, 0, 0, 150, IType.SIN_FULL))
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 50).addPos(0, 0, 720, 1000, IType.SIN_DOWN));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 50).addPos(0, 0, 720, 1000, IType.SIN_DOWN));
        case RELOAD -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(-15, 0, 0, 250, IType.SIN_DOWN).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 60, 50).addPos(0, 0, 720, 1000, IType.SIN_DOWN));
        case INSPECT -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(3, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, -720, 1000, IType.SIN_DOWN));
        default -> null;
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_MAS36_ANIMS = (stack, type) -> {
        int mag = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory());
        double turn = -90;
        double pullAmount = GunItem.getIsAiming(stack) ? -1F : -1.5D;
        return switch (type) {
            case EQUIP -> new BusAnimation()
                    .addBus("STOCK", new BusAnimationSequence().setPos(-158, 0, 0).hold(500).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("EQUIP", new BusAnimationSequence().setPos(45, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL).hold(500).addPos(1, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
            case CYCLE -> new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
                    .addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
                    .addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR))
                    .addBus("LIFT", new BusAnimationSequence().hold(600).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("BULLET", mag <= 1 ? new BusAnimationSequence().setPos(-100, 0, 0) : new BusAnimationSequence().hold(850).addPos(0, 0.1875, 1.5, 200, IType.LINEAR));
            case CYCLE_DRY -> new BusAnimation()
                    .addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
                    .addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR))
                    .addBus("LIFT", new BusAnimationSequence().hold(600).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("BULLET", new BusAnimationSequence().setPos(-100, 0, 0));
            case RELOAD -> new BusAnimation()
                    .addBus("BOLT_TURN", new BusAnimationSequence().addPos(0, 0, turn, 150).holdUntil(2000).addPos(0, 0, 0, 150))
                    .addBus("BOLT_PULL", new BusAnimationSequence().hold(100).addPos(0, 0, -1.5D, 250, IType.SIN_UP).holdUntil(1800).addPos(0, 0, 0, 200, IType.LINEAR))
                    .addBus("BULLET", new BusAnimationSequence().setPos(-100, 0, 0).holdUntil(1200).setPos(0, 0, 0).hold(600).addPos(0, 0.1875, 1.5, 200, IType.LINEAR))
                    .addBus("LIFT", new BusAnimationSequence().hold(200).addPos(30, 0, 0, 500, IType.SIN_FULL).holdUntil(1200).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("SHOW_CLIP", new BusAnimationSequence().setPos(1, 1, 1))
                    .addBus("CLIP", new BusAnimationSequence().setPos(2, -3, 0).hold(250).addPos(0.5, 1, 0, 500, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL).hold(400).addPos(-0.5, 0.5, 0, 150).addPos(-3, -3, 0, 250, IType.SIN_UP))
                    .addBus("BULLETS", new BusAnimationSequence().setPos(2, -3, 0).hold(250).addPos(0.5, 1, 0, 500, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL).hold(150).addPos(0, -1.5, 0, 250, IType.SIN_DOWN));
            case JAMMED -> new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence().hold(250).addPos(-15, 0, 0, 500, IType.SIN_FULL).holdUntil(1650).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).holdUntil(1250).addPos(0, 0, 0, 150))
                    .addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).addPos(0, 0, 0, 200, IType.LINEAR).addPos(0, 0, pullAmount, 250, IType.SIN_UP).addPos(0, 0, 0, 200, IType.LINEAR));
            case INSPECT -> new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence().hold(350).addPos(-3, 0, 0, 150, IType.SIN_DOWN).holdUntil(1050).addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("BOLT_TURN", new BusAnimationSequence().addPos(0, 0, turn, 150).holdUntil(1050).addPos(0, 0, 0, 150))
                    .addBus("BOLT_PULL", new BusAnimationSequence().hold(100).addPos(0, 0, -1D, 250, IType.SIN_UP).hold(500).addPos(0, 0, 0, 200, IType.LINEAR))
                    .addBus("BULLET", mag == 0 ? new BusAnimationSequence().setPos(-100, 0, 0) : new BusAnimationSequence().setPos(0, 0.1875, 1.5).hold(100).addPos(0, 0.125, 0.5, 250, IType.SIN_UP).hold(500).addPos(0, 0.1875, 1.5, 200, IType.LINEAR));
            default -> null;
        };

    };

}


