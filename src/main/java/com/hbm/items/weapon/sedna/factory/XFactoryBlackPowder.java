package com.hbm.items.weapon.sedna.factory;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.GunItem.LambdaContext;

import com.hbm.render.anim.AnimationEnums.GunAnimation;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;

import net.minecraft.world.item.ItemStack;

public class XFactoryBlackPowder {


    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_RECOIL_PEPPERBOX = (stack, ctx) -> GunItem.setupRecoil(10, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, GunAnimation, BusAnimation> LAMBDA_PEPPERBOX_ANIMS = (stack, type) -> switch (type) {
        case CYCLE -> new BusAnimation()
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 0, 1025).addPos(60, 0, 0, 250))
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(45, 0, 0, 150, IType.SIN_DOWN).addPos(45, 0, 0, 50).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("HAMMER", new BusAnimationSequence().addPos(80, 0, 0, 25).addPos(80, 0, 0, 1000).addPos(0, 0, 0, 250))
                .addBus("TRIGGER", new BusAnimationSequence().addPos(1, 0, 0, 25).addPos(1, 0, 0, 250).addPos(0, 0, 0, 100));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 0, 525).addPos(60, 0, 0, 250))
                .addBus("HAMMER", new BusAnimationSequence().addPos(80, 0, 0, 25).addPos(80, 0, 0, 500).addPos(0, 0, 0, 250))
                .addBus("TRIGGER", new BusAnimationSequence().addPos(1, 0, 0, 25).addPos(1, 0, 0, 250).addPos(0, 0, 0, 100));
        case EQUIP -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(-25, 0, 0, 0).addPos(0, 0, 0, 200, IType.SIN_DOWN));
        case RELOAD -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(90, 0, 0, 500, IType.SIN_FULL).addPos(90, 0, 0, 1600).addPos(0, 0, 0, 500, IType.SIN_FULL).addPos(-5, 0, 0, 200, IType.SIN_UP).addPos(0, 0, 0, 200, IType.SIN_DOWN))
                .addBus("TRANSLATE", new BusAnimationSequence().addPos(0, -12, 5, 500, IType.SIN_FULL).addPos(0, -12, 5, 700).addPos(0, -13, 5, 200).addPos(0, -12, 5, 200).addPos(0, -12, 5, 500).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("LOADER", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 5, -5, 0).addPos(0, 0, -0.1, 500, IType.SIN_FULL).addPos(0, 0, -1, 200).addPos(0, 0, -1, 200).addPos(0, 0, -0.1, 200).addPos(0, 5, -5, 500, IType.SIN_FULL).addPos(0, 0, 0, 0))
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 0, 2600).addPos(-360, 0, 0, 750, IType.SIN_FULL))
                .addBus("SHOT", new BusAnimationSequence().addPos(1, 0, 0, 1400).addPos(0, 0, 0, 0));
        case INSPECT -> new BusAnimation()
                .addBus("ROTATE", new BusAnimationSequence().addPos(-360, 0, 0, 750, IType.SIN_FULL))
                .addBus("RECOIL", new BusAnimationSequence().addPos(-5, 0, 0, 200, IType.SIN_UP).addPos(0, 0, 0, 200, IType.SIN_DOWN));
        case JAMMED -> new BusAnimation()
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 0, 1300).addPos(60, 0, 0, 500, IType.SIN_FULL).addPos(60, 0, 0, 400).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("TRANSLATE", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, -6, 0, 400, IType.SIN_FULL).addPos(0, -6, 0, 2000).addPos(0, 0, 0, 400, IType.SIN_FULL))
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(45, 0, 0, 400, IType.SIN_FULL).addPos(45, 0, 0, 2000).addPos(0, 0, 0, 400, IType.SIN_FULL));
        default -> null;
    };
}