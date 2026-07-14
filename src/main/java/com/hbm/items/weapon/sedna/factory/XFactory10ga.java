package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.items.weapon.sedna.*;
import com.hbm.items.weapon.sedna.GunItem.LambdaContext;

import com.hbm.render.anim.AnimationEnums.GunAnimation;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class XFactory10ga {


    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_TINY_EXPLODE = (bullet, hitResult) -> {
        if(hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            if(bullet.tickCount < 3 && entityHit.getEntity() == bullet.getOwner()) return;
        }
        Lego.tinyExplode(bullet, hitResult, 1.5F);
        bullet.discard();
    };

    public static final BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_DOUBLE_BARREL = (stack, ctx) -> GunItem.setupRecoil(10, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    @SuppressWarnings("incomplete-switch")
    public static final BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_DOUBLE_BARREL_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(-60, 0, 0, 0)
                        .addPos(0, 0, -3, 500, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(0, 0, -1, 50)
                        .addPos(0, 0, 0, 250))
                .addBus("BUCKLE", new BusAnimationSequence()
                        .addPos(0, -60, 0, 50)
                        .addPos(0, 0, 0, 250));
        case RELOAD -> new BusAnimation()
                .addBus("TURN", new BusAnimationSequence()
                        .addPos(0, 30, 0, 350, IType.SIN_FULL)
                        .addPos(0, 30, 0, 1150)
                        .addPos(0, 0, 0, 350, IType.SIN_FULL))
                .addBus("LEVER", new BusAnimationSequence()
                        .addPos(0, 0, 0, 250)
                        .addPos(0, 0, -90, 100, IType.SIN_FULL)
                        .addPos(0, 0, -90, 1300)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("BARREL", new BusAnimationSequence()
                        .addPos(0, 0, 0, 300)
                        .addPos(60, 0, 0, 150, IType.SIN_UP)
                        .addPos(60, 0, 0, 1150)
                        .addPos(0, 0, 0, 150, IType.SIN_UP))
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 350)
                        .addPos(-5, 0, 0, 150, IType.SIN_FULL)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL)
                        .addPos(0, 0, 0, 700)
                        .addPos(-5, 0, 0, 100, IType.SIN_FULL)
                        .addPos(0, 0, 0, 100, IType.SIN_UP) //1500
                        .addPos(45, 0, 0, 150)
                        .addPos(45, 0, 0, 150)
                        .addPos(-5, 0, 0, 150, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL)) //2050
                .addBus("SHELLS", new BusAnimationSequence()
                        .addPos(0, 0, 0, 450)
                        .addPos(0, 0, -2.5, 100)
                        .addPos(0, -5, -5, 350, IType.SIN_DOWN)
                        .addPos(0, -3, -2, 0)
                        .addPos(0, 0, -2, 250)
                        .addPos(0, 0, 0, 150, IType.SIN_UP)) //1300
                .addBus("SHELL_FLIP", new BusAnimationSequence()
                        .addPos(0, 0, 0, 450)
                        .addPos(-360, 0, 0, 450)
                        .addPos(0, 0, 0, 0));
        case INSPECT -> new BusAnimation()
                .addBus("LEVER", new BusAnimationSequence()
                        .addPos(0, 0, 0, 250)
                        .addPos(0, 0, -90, 100, IType.SIN_FULL)
                        .addPos(0, 0, -90, 800)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("BARREL", new BusAnimationSequence()
                        .addPos(0, 0, 0, 300)
                        .addPos(60, 0, 0, 150, IType.SIN_UP)
                        .addPos(60, 0, 0, 650)
                        .addPos(0, 0, 0, 150, IType.SIN_UP))
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 350)
                        .addPos(-5, 0, 0, 150, IType.SIN_FULL)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL)
                        .addPos(0, 0, 0, 200)
                        .addPos(-5, 0, 0, 100, IType.SIN_FULL)
                        .addPos(0, 0, 0, 100, IType.SIN_UP) //1500
                        .addPos(45, 0, 0, 150)
                        .addPos(45, 0, 0, 150)
                        .addPos(-5, 0, 0, 150, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL));
        default -> null;
    };
}