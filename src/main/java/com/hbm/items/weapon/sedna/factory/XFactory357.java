package com.hbm.items.weapon.sedna.factory;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class XFactory357 {

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_ATLAS = (stack, ctx)
            -> GunItem.setupRecoil(10, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_ATLAS_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(-90, 0, 0, 0)
                        .addPos(0, 0, 0, 350, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(0, 0, 0, 50)
                        .addPos(0, 0, -3, 50)
                        .addPos(0, 0, 0, 250))
                .addBus("HAMMER", new BusAnimationSequence()
                        .addPos(0, 0, 1, 50)
                        .addPos(0, 0, 1, 300)
                        .addPos(0, 0, 0, 200))
                .addBus("DRUM", new BusAnimationSequence()
                        .addPos(0, 0, 0, 250)
                        .addPos(0, 0, 1, 200));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("HAMMER", new BusAnimationSequence()
                        .addPos(0, 0, 1, 50)
                        .addPos(0, 0, 1, 200)
                        .addPos(0, 0, 0, 200))
                .addBus("DRUM", new BusAnimationSequence()
                        .addPos(0, 0, 0, 250)
                        .addPos(0, 0, 1, 200));
        case RELOAD -> new BusAnimation()
                .addBus("LATCH", new BusAnimationSequence()
                        .addPos(0, 0, 90, 300)
                        .addPos(0, 0, 90, 2000)
                        .addPos(0, 0, 0, 150))
                .addBus("FRONT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 200)
                        .addPos(0, 0, 45, 150)
                        .addPos(0, 0, 45, 2000)
                        .addPos(0, 0, 0, 75))
                .addBus("RELOAD_ROT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 300)
                        .addPos(60, 0, 0, 500)
                        .addPos(60, 0, 0, 500)
                        .addPos(0, -90, -90, 0)
                        .addPos(0, -90, -90, 600)
                        .addPos(0, 0, 0, 300)
                        .addPos(0, 0, 0, 100)
                        .addPos(-45, 0, 0, 50)
                        .addPos(-45, 0, 0, 100)
                        .addPos(0, 0, 0, 300))
                .addBus("RELOAD_MOVE", new BusAnimationSequence()
                        .addPos(0, 0, 0, 300)
                        .addPos(0, -15, 0, 1000)
                        .addPos(0, 0, 0, 450))
                .addBus("DRUM_PUSH", new BusAnimationSequence()
                        .addPos(0, 0, 0, 1600)
                        .addPos(0, 0, -5, 0)
                        .addPos(0, 0, 0, 300));
        case INSPECT -> new BusAnimation()
                .addBus("LATCH", new BusAnimationSequence()
                        .addPos(0, 0, 90, 300)
                        .addPos(0, 0, 90, 1000)
                        .addPos(0, 0, 0, 150))
                .addBus("FRONT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 200)
                        .addPos(0, 0, 45, 150)
                        .addPos(0, 0, 45, 1000)
                        .addPos(0, 0, 0, 75))
                .addBus("RELOAD_ROT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 300)
                        .addPos(45, 0, 0, 500, IType.SIN_FULL)
                        .addPos(45, 0, 0, 500)
                        .addPos(-45, 0, 0, 50)
                        .addPos(-45, 0, 0, 100)
                        .addPos(0, 0, 0, 300))
                .addBus("RELOAD_MOVE", new BusAnimationSequence()
                        .addPos(0, 0, 0, 300)
                        .addPos(0, -2.5, 0, 500, IType.SIN_FULL)
                        .addPos(0, -2.5, 0, 500)
                        .addPos(0, 0, 0, 350));
        case JAMMED -> new BusAnimation()
                .addBus("LATCH", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, 90, 300)
                        .addPos(0, 0, 90, 1000)
                        .addPos(0, 0, 0, 150))
                .addBus("FRONT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, 0, 200)
                        .addPos(0, 0, 45, 150)
                        .addPos(0, 0, 45, 1000)
                        .addPos(0, 0, 0, 75))
                .addBus("RELOAD_ROT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, 0, 300)
                        .addPos(45, 0, 0, 500, IType.SIN_FULL)
                        .addPos(45, 0, 0, 500)
                        .addPos(-45, 0, 0, 50)
                        .addPos(-45, 0, 0, 100)
                        .addPos(0, 0, 0, 300))
                .addBus("RELOAD_MOVE", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, 0, 300)
                        .addPos(0, -2.5, 0, 500, IType.SIN_FULL)
                        .addPos(0, -2.5, 0, 500)
                        .addPos(0, 0, 0, 350));
        default -> null;
    };

}
