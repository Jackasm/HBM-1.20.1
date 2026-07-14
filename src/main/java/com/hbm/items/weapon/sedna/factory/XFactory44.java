package com.hbm.items.weapon.sedna.factory;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
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
import java.util.function.Function;

public class XFactory44 {
    public static final ResourceLocation scope_lilmac = ResLocation.ResLocation(RefStrings.MODID, "textures/misc/scope_44.png");

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_HENRY = (stack, ctx)
            -> GunItem.setupRecoil(5, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1));

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_NOPIP = (stack, ctx)
            -> GunItem.setupRecoil(10, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_HANGMAN = (stack, ctx)
            -> GunItem.setupRecoil(5, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1));

    public static Function<ItemStack, String> LAMBDA_NAME_NOPIP = (stack) -> {
        if(WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SCOPE)) return stack.getDescriptionId() + "_scoped";
        return null;
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> SMACK_A_FUCKER = (stack, ctx) -> {
        if(GunItem.getState(stack, ctx.configIndex) == GunItem.GunState.IDLE || GunItem.getLastAnim(stack, ctx.configIndex) == AnimationEnums.GunAnimation.CYCLE) {
            GunItem.setIsAiming(stack, false);
            GunItem.setState(stack, ctx.configIndex, GunItem.GunState.DRAWING);
            GunItem.setTimer(stack, ctx.configIndex, ctx.config.getInspectDuration(stack));
            GunItem.playAnimation(ctx.getPlayer(), stack, AnimationEnums.GunAnimation.INSPECT, ctx.configIndex);
        }
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_HENRY_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(-90, 0, 0, 0).addPos(0, 0, -3, 350, IType.SIN_DOWN))
                .addBus("SIGHT", new BusAnimationSequence().addPos(80, 0, 0, 0).addPos(80, 0, 0, 500).addPos(0, 0, -3, 250, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -1, 50).addPos(0, 0, 0, 250))
                .addBus("SIGHT", new BusAnimationSequence().addPos(35, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200))
                .addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(0, 0, 45, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
                .addBus("HAMMER", new BusAnimationSequence().addPos(30, 0, 0, 50).addPos(30, 0, 0, 550).addPos(0, 0, 0, 200));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200))
                .addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(0, 0, 45, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
                .addBus("HAMMER", new BusAnimationSequence().addPos(30, 0, 0, 50).addPos(30, 0, 0, 550).addPos(0, 0, 0, 200));
        case RELOAD -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence().addPos(-60, 0, 0, 400, IType.SIN_FULL))
                .addBus("TWIST", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -90, 200, IType.SIN_FULL))
                .addBus("BULLET", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(3, 0, -6, 0).addPos(0, 0, 1, 300, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL));
        case RELOAD_CYCLE -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence().addPos(-60, 0, 0, 0))
                .addBus("TWIST", new BusAnimationSequence().addPos(0, 0, -90, 0))
                .addBus("BULLET", new BusAnimationSequence().addPos(3, 0, -6, 0).addPos(0, 0, 1, 300, IType.SIN_FULL).addPos(0, 0, 0, 250, IType.SIN_FULL));
        case RELOAD_END -> {
            boolean empty = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getAmountBeforeReload(stack) <= 0;
            yield new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence().addPos(-60, 0, 0, 0).addPos(-60, 0, 0, 300).addPos(0, 0, 0, 400, IType.SIN_FULL))
                    .addBus("TWIST", new BusAnimationSequence().addPos(0, 0, -90, 0).addPos(0, 0, 0, 200, IType.SIN_FULL))
                    .addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(empty ? -90 : 0, 0, 0, 200).addPos(0, 0, 0, 200))
                    .addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(0, 0, empty ? 45 : 0, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP));
        }
        case JAMMED -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence().addPos(-60, 0, 0, 0).addPos(-60, 0, 0, 300).addPos(0, 0, 0, 400, IType.SIN_FULL))
                .addBus("TWIST", new BusAnimationSequence().addPos(0, 0, -90, 0).addPos(0, 0, 0, 200, IType.SIN_FULL))
                .addBus("LEVER", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200).addPos(0, 0, 0, 500).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200).addPos(0, 0, 0, 200).addPos(-90, 0, 0, 200).addPos(0, 0, 0, 200))
                .addBus("TURN", new BusAnimationSequence().addPos(0, 0, 0, 700).addPos(0, 0, 45, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP).addPos(0, 0, 0, 500).addPos(0, 0, 45, 200, IType.SIN_FULL).addPos(0, 0, 45, 600).addPos(0, 0, 0, 200, IType.SIN_FULL));
        case INSPECT -> new BusAnimation()
                .addBus("YEET", new BusAnimationSequence().addPos(0, 2, 0, 200, IType.SIN_DOWN).addPos(0, 0, 0, 200, IType.SIN_UP))
                .addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 360, 400));
        default -> null;
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_NOPIP_ANIMS = (stack, type) -> switch (type) {
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -3, 50).addPos(0, 0, 0, 250))
                .addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 1, 50).addPos(0, 0, 1, 400).addPos(0, 0, 0, 200))
                .addBus("DRUM", new BusAnimationSequence().addPos(0, 0, 0, 450).addPos(0, 0, 1, 200));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 1, 50).addPos(0, 0, 1, 300 + 100).addPos(0, 0, 0, 200))
                .addBus("DRUM", new BusAnimationSequence().addPos(0, 0, 0, 450).addPos(0, 0, 1, 200));
        case EQUIP ->
                new BusAnimation().addBus("ROTATE", new BusAnimationSequence().addPos(90, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
        case RELOAD -> new BusAnimation()
                .addBus("RELAOD_TILT", new BusAnimationSequence().addPos(-15, 0, 0, 100).addPos(65, 0, 0, 100).addPos(45, 0, 0, 50).addPos(0, 0, 0, 200).addPos(0, 0, 0, 1450).addPos(-80, 0, 0, 100).addPos(-80, 0, 0, 100).addPos(0, 0, 0, 200))
                .addBus("RELOAD_CYLINDER", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(90, 0, 0, 100).addPos(90, 0, 0, 1700).addPos(0, 0, 0, 70))
                .addBus("RELOAD_LIFT", new BusAnimationSequence().addPos(0, 0, 0, 350).addPos(-45, 0, 0, 250).addPos(-45, 0, 0, 350).addPos(-15, 0, 0, 200).addPos(-15, 0, 0, 1050).addPos(0, 0, 0, 100))
                .addBus("RELOAD_JOLT", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(2, 0, 0, 50).addPos(0, 0, 0, 100))
                .addBus("RELOAD_BULLETS", new BusAnimationSequence().addPos(0, 0, 0, 650).addPos(10, 0, 0, 300).addPos(10, 0, 0, 200).addPos(0, 0, 0, 700))
                .addBus("RELOAD_BULLETS_CON", new BusAnimationSequence().addPos(1, 0, 0, 0).addPos(1, 0, 0, 950).addPos(0, 0, 0, 1));
        case INSPECT, JAMMED -> new BusAnimation()
                .addBus("RELAOD_TILT", new BusAnimationSequence().addPos(-15, 0, 0, 100).addPos(65, 0, 0, 100).addPos(45, 0, 0, 50).addPos(0, 0, 0, 200).addPos(0, 0, 0, 200).addPos(-80, 0, 0, 100).addPos(-80, 0, 0, 100).addPos(0, 0, 0, 200))
                .addBus("RELOAD_CYLINDER", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(90, 0, 0, 100).addPos(90, 0, 0, 450).addPos(0, 0, 0, 70));
        default -> null;
    };
    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_LILMAC_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation().addBus("SPIN", new BusAnimationSequence().addPos(-360, 0, 0, 350));
        default -> LAMBDA_NOPIP_ANIMS.apply(stack, type);
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_HANGMAN_ANIMS = (stack, type) -> switch (type) {
        case EQUIP ->
                new BusAnimation().addBus("EQUIP", new BusAnimationSequence().addPos(60, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_DOWN));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, 0, 50).addPos(0, 0, -3, 50).addPos(0, 0, 0, 250));
        case RELOAD -> new BusAnimation()
                .addBus("LID", new BusAnimationSequence().addPos(0, 0, -90, 250).addPos(0, 0, -90, 1500).addPos(0, 0, 0, 250))
                .addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, -10, 0, 250, IType.SIN_UP).addPos(0, -10, 0, 500).addPos(0, 0, 0, 350, IType.SIN_FULL))
                .addBus("BULLETS", new BusAnimationSequence().addPos(1, 1, 1, 0).addPos(0, 0, 0, 500))
                .addBus("EQUIP", new BusAnimationSequence().addPos(-15, 0, 0, 500, IType.SIN_FULL).addPos(-15, 0, 0, 850).addPos(-25, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 350, IType.SIN_FULL))
                .addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 25, 250, IType.SIN_FULL).addPos(0, 0, 25, 1000).addPos(0, 0, 0, 250, IType.SIN_FULL));
        case INSPECT -> new BusAnimation()
                .addBus("TURN", new BusAnimationSequence().addPos(0, 170, 0, 500, IType.SIN_UP).addPos(0, 170, 0, 550).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 110, 500, IType.SIN_FULL).addPos(0, 0, 110, 550).addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("SMACK", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 1, 150, IType.SIN_DOWN).addPos(0, 0, -3, 150, IType.SIN_UP).addPos(0, 0, 0, 350, IType.SIN_FULL));
        case JAMMED -> new BusAnimation()
                .addBus("LID", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -90, 250).addPos(0, 0, -90, 300).addPos(0, 0, 0, 250))
                .addBus("MAG", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 0, 250).addPos(0, -3, 0, 150, IType.SIN_UP).addPos(0, 0, 0, 150, IType.SIN_FULL))
                .addBus("EQUIP", new BusAnimationSequence().addPos(0, 0, 0, 1000).addPos(-10, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 350, IType.SIN_FULL))
                .addBus("ROLL", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, 25, 250, IType.SIN_FULL).addPos(0, 0, 25, 300).addPos(0, 0, 0, 250, IType.SIN_FULL));
        default -> null;
    };
}
