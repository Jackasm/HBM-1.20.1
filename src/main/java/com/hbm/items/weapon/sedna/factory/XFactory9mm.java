package com.hbm.items.weapon.sedna.factory;

import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
import com.hbm.main.HBMResourceManager;
import com.hbm.main.MainRegistry;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.util.EntityDamageUtil;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class XFactory9mm {

    public static final Function<ItemStack, String> LAMBDA_NAME_GREASEGUN = (stack) -> {
        if(WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.GREASEGUN_CLEAN)) {
            return stack.getDescriptionId() + "_m3";
        }
        return null;
    };

    public static final Function<ItemStack, String> LAMBDA_NAME_UZI = (stack) -> {
        if(WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SILENCER)) {
            return stack.getDescriptionId() + "_richter";
        }
        return null;
    };

    public static final BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_GREASEGUN = (stack, ctx)
            -> GunItem.setupRecoil(2, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.5));

    public static final BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_LAG = (stack, ctx)
            -> GunItem.setupRecoil(5, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    public static final BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_UZI = (stack, ctx)
            -> GunItem.setupRecoil(1, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.25));

    public static final BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_FIRE_LAG = (stack, ctx) -> {
        AnimationEnums.GunAnimation type = GunItem.getLastAnim(stack, ctx.configIndex);
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);
        Player player = ctx.getPlayer();

        if(player != null && type == AnimationEnums.GunAnimation.INSPECT && timer > 20 && timer < 60) {
            int index = ctx.configIndex;
            Receiver primary = ctx.config.getReceivers(stack)[0];
            IMagazine mag = primary.getMagazine(stack);
            BulletConfig config = (BulletConfig) Objects.requireNonNull(mag).getType(stack, ctx.inventory);
            mag.useUpAmmo(stack, ctx.inventory, 1);

            float currentWear = GunItem.getWear(stack, index);
            float maxDura = ctx.config.getDurability(stack);
            GunItem.setWear(stack, index, Math.min(currentWear + config.wear, maxDura));


             player.level().playSound(null, player.getX(), player.getY(), player.getZ(),
                     Objects.requireNonNull(primary.getFireSound(stack)), player.getSoundSource(),
                            primary.getFireVolume(stack), primary.getFirePitch(stack));

            GunItem.setState(stack, index, GunItem.GunState.COOLDOWN);
            GunItem.setTimer(stack, index, primary.getDelayAfterFire(stack));

            EntityDamageUtil.attackEntityFromNT(player,
                    BulletConfig.getDamage(player, player, BulletConfig.DamageClass.PHYSICAL),
                    1000F, true, 1D, 5F, 0F);
        } else {
            Lego.doStandardFire(stack, ctx, AnimationEnums.GunAnimation.CYCLE, true);
        }
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_GREASEGUN_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(80, 0, 0, 0)
                        .addPos(80, 0, 0, 500)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("STOCK", new BusAnimationSequence()
                        .addPos(0, 0, -4, 0)
                        .addPos(0, 0, -4, 200)
                        .addPos(0, 0, 0, 300, IType.SIN_FULL));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(0, 0, GunItem.getIsAiming(stack) ? -0.25 : -0.5, 50, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("FLAP", new BusAnimationSequence()
                        .addPos(0, 0, 15, 100, IType.SIN_DOWN)
                        .addPos(0, 0, -5, 100, IType.SIN_FULL)
                        .addPos(0, 0, 0, 50, IType.SIN_FULL));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(-25, 0, 0, 250, IType.SIN_FULL)
                        .addPos(-25, 0, 0, 750)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("TURN", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, -45, 250, IType.SIN_FULL)
                        .addPos(0, 0, -45, 750)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("HANDLE", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, 0, 250)
                        .addPos(-90, 0, 0, 250, IType.SIN_FULL)
                        .addPos(0, 0, 0, 250, IType.SIN_FULL));
        case RELOAD -> {
            boolean empty = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0)
                    .getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory()) <= 0;
            yield new BusAnimation()
                    .addBus("MAG", new BusAnimationSequence()
                            .addPos(0, -8, 0, 250, IType.SIN_UP)
                            .addPos(0, -8, 0, 750)
                            .addPos(0, 0, 0, 500, IType.SIN_DOWN))
                    .addBus("LIFT", new BusAnimationSequence()
                            .addPos(0, 0, 0, 500)
                            .addPos(-25, 0, 0, 250, IType.SIN_FULL)
                            .addPos(-25, 0, 0, 1750)
                            .addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("TURN", new BusAnimationSequence()
                            .addPos(0, 0, 0, 1750)
                            .addPos(0, 0, -45, 250, IType.SIN_FULL)
                            .addPos(0, 0, -45, 500)
                            .addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("HANDLE", new BusAnimationSequence()
                            .addPos(0, 0, 0, 2000)
                            .addPos(-90, 0, 0, 250, IType.SIN_FULL)
                            .addPos(0, 0, 0, 250, IType.SIN_FULL))
                    .addBus("BULLET", new BusAnimationSequence()
                            .addPos(empty ? 1 : 0, 0, 0, 0)
                            .addPos(0, 0, 0, 1000));
        }
        case JAMMED -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(-25, 0, 0, 250, IType.SIN_FULL)
                        .addPos(-25, 0, 0, 1500)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("TURN", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, -45, 250, IType.SIN_FULL)
                        .addPos(0, 0, -45, 1500)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("HANDLE", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, 0, 250)
                        .addPos(-90, 0, 0, 250, IType.SIN_FULL)
                        .addPos(0, 0, 0, 250, IType.SIN_FULL)
                        .addPos(0, 0, 0, 250)
                        .addPos(-90, 0, 0, 250, IType.SIN_FULL)
                        .addPos(0, 0, 0, 250, IType.SIN_FULL));
        case INSPECT -> new BusAnimation()
                .addBus("TURN", new BusAnimationSequence()
                        .addPos(0, 0, -45, 150)
                        .addPos(0, 0, 45, 150)
                        .addPos(0, 0, 45, 50)
                        .addPos(0, 0, 0, 250)
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, 45, 150)
                        .addPos(0, 0, -45, 150)
                        .addPos(0, 0, 0, 150))
                .addBus("FLAP", new BusAnimationSequence()
                        .addPos(0, 0, 0, 300)
                        .addPos(0, 0, 180, 150)
                        .addPos(0, 0, 180, 850)
                        .addPos(0, 0, 0, 150));
        default -> null;
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_LAG_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(-90, 0, 0, 0)
                        .addPos(0, 0, 0, 350, IType.SIN_DOWN));
        case CYCLE -> HBMResourceManager.getWeaponAnimation("lag", "Firing");
        //.addBus("HAMMER", new BusAnimationSequence().addPos(0, 0, 25, 50).addPos(0, 0, 25, 50).addPos(0, 0, 0, 100, IType.SIN_DOWN));
        case CYCLE_DRY -> HBMResourceManager.getWeaponAnimation("lag", "Dryfire");
        case RELOAD -> HBMResourceManager.getWeaponAnimation("lag", "Reload");
        case JAMMED -> HBMResourceManager.getWeaponAnimation("lag", "Jam");
        case INSPECT -> Objects.requireNonNull(HBMResourceManager.getWeaponAnimation("lag", "Inspect"))
                .addBus("ADD_TRANS", new BusAnimationSequence()
                        .addPos(-4, 0, -3, 500)
                        .addPos(-4, 0, -3, 2000)
                        .addPos(0, 0, 0, 500))
                .addBus("ADD_ROT", new BusAnimationSequence()
                        .addPos(0, -2, 5, 500)
                        .addPos(0, -2, 5, 2000)
                        .addPos(0, 0, 0, 500));
        default -> null;
    };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_UZI_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(80, 0, 0, 0)
                        .addPos(80, 0, 0, 500)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("STOCKBACK", new BusAnimationSequence()
                        .addPos(-200, 0, 0, 0)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("STOCKFRONT", new BusAnimationSequence()
                        .addPos(180, 0, 0, 0)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(0, 0, GunItem.getIsAiming(stack) ? -0.5 : -0.75, 25, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 75, IType.SIN_FULL));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 250)
                        .addPos(-25, 0, 0, 250, IType.SIN_FULL)
                        .addPos(-25, 0, 0, 500)
                        .addPos(0, 0, 0, 250, IType.SIN_FULL))
                .addBus("SLIDE", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, -2, 150, IType.SIN_FULL)
                        .addPos(0, 0, 0, 50, IType.SIN_UP));
        case RELOAD -> {
            boolean empty = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0)
                    .getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory()) <= 0;
            yield new BusAnimation()
                    .addBus("MAG", new BusAnimationSequence()
                            .addPos(0, 0, 0, 250)
                            .addPos(0, -10, 0, 250, IType.SIN_UP)
                            .addPos(0, -10, 0, 750)
                            .addPos(0, 0, 0, 500, IType.SIN_DOWN))
                    .addBus("LIFT", new BusAnimationSequence()
                            .addPos(-25, 0, 0, 250, IType.SIN_FULL)
                            .addPos(-25, 0, 0, 2000)
                            .addPos(0, 0, 0, 500, IType.SIN_FULL))
                    .addBus("SLIDE", new BusAnimationSequence()
                            .addPos(0, 0, 0, 2000)
                            .addPos(0, 0, -2, 150, IType.SIN_FULL)
                            .addPos(0, 0, 0, 50, IType.SIN_UP))
                    .addBus("BULLET", new BusAnimationSequence()
                            .addPos(empty ? 0 : 1, 0, 0, 0)
                            .addPos(empty ? 0 : 1, 0, 0, 500)
                            .addPos(1, 0, 0, 0));
        }
        case JAMMED -> new BusAnimation()
                .addBus("LIFT", new BusAnimationSequence()
                        .addPos(0, 0, 0, 500)
                        .addPos(-25, 0, 0, 250, IType.SIN_FULL)
                        .addPos(-25, 0, 0, 1250)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL))
                .addBus("SLIDE", new BusAnimationSequence()
                        .addPos(0, 0, 0, 1000)
                        .addPos(0, 0, -2, 150, IType.SIN_FULL)
                        .addPos(0, 0, 0, 50, IType.SIN_UP)
                        .addPos(0, 0, 0, 500)
                        .addPos(0, 0, -2, 150, IType.SIN_FULL)
                        .addPos(0, 0, 0, 50, IType.SIN_UP));
        case INSPECT -> new BusAnimation()
                .addBus("YEET", new BusAnimationSequence()
                        .addPos(0, -1, 0, 100)
                        .addPos(0, 0, 0, 100, IType.SIN_UP)
                        .addPos(0, 12, 0, 350, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 350, IType.SIN_UP)
                        .addPos(0, -1, 0, 50, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("SPEEN", new BusAnimationSequence()
                        .addPos(0, 0, 0, 250)
                        .addPos(-360, 0, 0, 600));
        default -> null;
    };
}
