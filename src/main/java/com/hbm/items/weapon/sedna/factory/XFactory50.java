package com.hbm.items.weapon.sedna.factory;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class XFactory50 {

    public static final ResourceLocation scope = ResLocation.ResLocation(RefStrings.MODID, "textures/misc/scope_amat.png");
    public static final ResourceLocation scope_thermal = ResLocation.ResLocation(RefStrings.MODID, "textures/misc/scope_penance.png");

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> {
        Lego.handleStandardSmoke(ctx.entity, stack, 2000, 0.05D, 1.1D, 0);
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_AMAT = (stack, ctx) -> {
        GunItem.setupRecoil(12.5F, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1));
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_M2 = (stack, ctx) -> {
        GunItem.setupRecoil((float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.5),
                (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.5));
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_AMAT_ANIMS = (stack, type) -> {
        double turn = -60;
        double pullAmount = -2.5;
        double side = 4;
        double down = -2;
        double detach = 0.5;
        double apex = 7;

        switch(type) {
            case EQUIP: return new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("BIPOD", new BusAnimationSequence().hold(500).addPos(80, 0, 0, 350).addPos(80, 25, 0, 150));
            case CYCLE: return new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
                    .addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
                    .addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR))
                    .addBus("LIFT", new BusAnimationSequence().hold(600).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL));
            case CYCLE_DRY: return new BusAnimation()
                    .addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
                    .addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR))
                    .addBus("LIFT", new BusAnimationSequence().hold(600).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL));
            case RELOAD: return new BusAnimation()
                    .addBus("MAG", new BusAnimationSequence().addPos(0, -10, 0, 350, IType.SIN_UP).addPos(0, 0, 0, 650, IType.SIN_UP))
                    .addBus("LIFT", new BusAnimationSequence().hold(1000).addPos(-2, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL).hold(450).addPos(-3, 0, 0, 150, IType.SIN_DOWN).hold(300).addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("BOLT_TURN", new BusAnimationSequence().hold(1500).addPos(0, 0, turn, 150).hold(700).addPos(0, 0, 0, 150))
                    .addBus("BOLT_PULL", new BusAnimationSequence().hold(1600).addPos(0, 0, pullAmount, 250, IType.SIN_UP).hold(250).addPos(0, 0, 0, 200, IType.LINEAR));
            case JAMMED: return new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence().hold(250).addPos(-15, 0, 0, 500, IType.SIN_FULL).holdUntil(1650).addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("BOLT_TURN", new BusAnimationSequence().hold(250).addPos(0, 0, turn, 150).holdUntil(1250).addPos(0, 0, 0, 150))
                    .addBus("BOLT_PULL", new BusAnimationSequence().hold(350).addPos(0, 0, pullAmount, 250, IType.SIN_UP).addPos(0, 0, 0, 200, IType.LINEAR).addPos(0, 0, pullAmount, 250, IType.SIN_UP).addPos(0, 0, 0, 200, IType.LINEAR));
            case INSPECT: return new BusAnimation()
                    .addBus("SCOPE_THROW", new BusAnimationSequence().addPos(0, detach, 0, 100, IType.SIN_FULL).addPos(side, down, 0, 500, IType.SIN_FULL).addPos(side, down - 0.5, 0, 100).addPos(side, apex, 0, 350, IType.SIN_FULL).addPos(side, down - 0.5, 0, 350, IType.SIN_DOWN).addPos(side, down, 0, 100).hold(250).addPos(0, detach, 0, 500, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("SCOPE_SPIN", new BusAnimationSequence().hold(700).addPos(-360, 0, 0, 700));
        }

        return null;
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_M2_ANIMS = (stack, type) -> {
        switch(type) {
            case EQUIP: return new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence().addPos(80, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
            case CYCLE: return new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.25, 25).addPos(0, 0, 0, 75));
        }

        return null;
    };
}
