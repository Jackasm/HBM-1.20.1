package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
import com.hbm.main.HBMResourceManager;
import com.hbm.main.MainRegistry;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import com.hbm.util.RefStrings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

import static com.hbm.util.ResLocation.ResLocation;

public class XFactory556mm {

    public static final ResourceLocation scope = ResLocation(RefStrings.MODID, "textures/misc/scope_bolt.png");

    public static final BiConsumer<EntityBulletBaseMK4, HitResult> LAMBDA_INCENDIARY = (bullet, hitResult) -> {
        if(hitResult.getType() == HitResult.Type.ENTITY) {
            EntityHitResult entityHit = (EntityHitResult) hitResult;
            Entity entity = entityHit.getEntity();
            if(entity instanceof LivingEntity living) {
                float phosphorus = HbmLivingProps.getPhosphorus(living);
                if(phosphorus < 300) {
                    HbmLivingProps.setPhosphorus(living, 300);
                }
            }
        }
    };

    public static Function<ItemStack, String> LAMBDA_NAME_G3 = (stack) -> {
        if(WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SILENCER) &&
                WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.NO_STOCK) &&
                WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.FURNITURE_BLACK) &&
                WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SCOPE)) return stack.getDescriptionId() + "_infiltrator";
        if(!WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.NO_STOCK) &&
                WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.FURNITURE_GREEN)) return stack.getDescriptionId() + "_a3";
        return null;
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_SMOKE = (stack, ctx) -> Lego.handleStandardSmoke(ctx.entity, stack, 1500, 0.075D, 1.1D, 0);

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_STG77_DECIDER = (stack, ctx) -> {
        int index = ctx.configIndex;
        GunItem.GunState lastState = GunItem.getState(stack, index);
        GunStateDecider.deciderStandardFinishDraw(stack, lastState, index);
        GunStateDecider.deciderStandardClearJam(stack, lastState, index);
        GunStateDecider.deciderStandardReload(stack, ctx, lastState, 0, index);
        GunStateDecider.deciderAutoRefire(stack, ctx, lastState, 0, index, () -> GunItem.getSecondary(stack, index));
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_G3 = (stack, ctx) -> GunItem.setupRecoil((float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.25),
            (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.25));

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_ZEBRA = (stack, ctx) -> GunItem.setupRecoil((float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.125),
            (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.125));

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_STG = (stack, ctx) -> { };

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_G3_ANIMS = (stack, type) -> {
        boolean empty = Objects.requireNonNull(((GunItem) stack.getItem()).getConfig(stack, 0)
                .getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, MainRegistry.proxy.me().getInventory()) <= 0;
        return switch (type) {
            case EQUIP -> new BusAnimation()
                    .addBus("EQUIP", new BusAnimationSequence()
                            .addPos(45, 0, 0, 0)
                            .addPos(0, 0, 0, 500, IType.SIN_FULL));
            case CYCLE -> new BusAnimation()
                    .addBus("BOLT", new BusAnimationSequence()
                            .addPos(0, 0, 0, 20)
                            .addPos(0, 0, -4.5, 40)
                            .addPos(0, 0, 0, 40))
                    .addBus("RECOIL", new BusAnimationSequence()
                            .addPos(0, 0, (GunItem.getIsAiming(stack) || !WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.NO_STOCK)) ? -0.25 : -0.75, 25, IType.SIN_DOWN)
                            .addPos(0, 0, 0, 75, IType.SIN_FULL));
            case CYCLE_DRY -> new BusAnimation()
                    .addBus("BOLT", new BusAnimationSequence()
                            .addPos(0, 0, 0, 250)
                            .addPos(0, 0, -0.3125, 100).hold(25)
                            .addPos(0, 0, -2.75, 130).hold(50)
                            .addPos(0, 0, -2.4375, 50)
                            .addPos(0, 0, 0, 85))
                    .addBus("PLUG", new BusAnimationSequence()
                            .addPos(0, 0, 0, 250).hold(125)
                            .addPos(0, 0, -2.4375, 130).hold(100)
                            .addPos(0, 0, 0, 85))
                    .addBus("HANDLE", new BusAnimationSequence()
                            .addPos(0, 0, 0, 250)
                            .addPos(0, 90, 0, 100).hold(25).hold(180)
                            .addPos(0, 0, 0, 50))
                    .addBus("LIFT", new BusAnimationSequence()
                            .addPos(0, 0, 0, 400)
                            .addPos(-1, 0, 0, 100, IType.SIN_DOWN)
                            .addPos(0, 0, 0, 100, IType.SIN_FULL));
            case RELOAD -> new BusAnimation()
                    .addBus("MAG", new BusAnimationSequence()
                            .addPos(0, -8, 0, 250, IType.SIN_UP)    //250
                            .addPos(0, -8, 0, 1050)                    //1300
                            .addPos(0, 0, 0, 250))                    //1550
                    .addBus("BOLT", new BusAnimationSequence()
                            .addPos(0, 0, 0, 200)                    //200
                            .addPos(0, 0, -0.3125, 100)                //300
                            .hold(10)                                //310
                            .addPos(0, 0, -3.25, 200)                //510
                            .holdUntil(1875)                        //1875
                            .addPos(0, 0, -2.9375, 50)                //1925
                            .addPos(0, 0, 0, 100))                    //2025
                    .addBus("PLUG", new BusAnimationSequence()
                            .addPos(0, 0, 0, 310)                    //310
                            .addPos(0, 0, -2.9375, 200)                //510
                            .holdUntil(1925)                        //1925
                            .addPos(0, 0, 0, 100))                    //2025
                    .addBus("HANDLE", new BusAnimationSequence()
                            .addPos(0, 0, 0, 200)                    //200
                            .addPos(0, 90, 0, 100)                    //300
                            .hold(210)                                //510
                            .addPos(0, 90, 45, 75)                    //685
                            .holdUntil(1775)                        //1775
                            .addPos(0, 90, 0, 100)                    //1875
                            .addPos(0, 0, 0, 50))                    //1925
                    .addBus("LIFT", new BusAnimationSequence()
                            .addPos(0, 0, 0, 750)                    //750
                            .addPos(-25, 0, 0, 500, IType.SIN_FULL)    //1250
                            .holdUntil(1550)                        //1550
                            .addPos(-26, 0, 0, 100, IType.SIN_DOWN)    //1650
                            .addPos(-25, 0, 0, 100, IType.SIN_FULL)    //1750
                            .holdUntil(2000)                        //2000
                            .addPos(0, 0, 0, 500, IType.SIN_FULL))    //3500
                    .addBus("BULLET", new BusAnimationSequence().addPos(empty ? 1 : 0, 0, 0, 0).addPos(0, 0, 0, 1000));
            case INSPECT -> new BusAnimation()
                    .addBus("MAG", new BusAnimationSequence()
                            .addPos(0, -1, 0, 150)                    //150
                            .addPos(2, -1, 0, 150)                    //300
                            .addPos(2, 8, 0, 350, IType.SIN_DOWN)    //650
                            .addPos(2, -2, 0, 350, IType.SIN_UP)    //1000
                            .addPos(2, -1, 0, 50)                    //1050
                            .addPos(2, -1, 0, 100)                    //1150
                            .addPos(0, -1, 0, 150, IType.SIN_FULL)    //1300
                            .addPos(0, 0, 0, 150, IType.SIN_UP))    //1450
                    .addBus("SPEEN", new BusAnimationSequence()
                            .addPos(0, 0, 0, 300)
                            .addPos(0, 360, 360, 700))
                    .addBus("LIFT", new BusAnimationSequence()
                            .addPos(0, 0, 0, 1450)
                            .addPos(-2, 0, 0, 100, IType.SIN_DOWN)
                            .addPos(0, 0, 0, 100, IType.SIN_FULL))
                    .addBus("BULLET", new BusAnimationSequence()
                            .addPos(empty ? 1 : 0, 0, 0, 0));
            case JAMMED -> new BusAnimation()
                    .addBus("LIFT", new BusAnimationSequence()
                            .addPos(0, 0, 0, 500)
                            .addPos(-25, 0, 0, 250, IType.SIN_FULL)
                            .addPos(-25, 0, 0, 1250)
                            .addPos(0, 0, 0, 350, IType.SIN_FULL))
                    .addBus("BOLT", new BusAnimationSequence()
                            .addPos(0, 0, 0, 1000)
                            .addPos(0, 0, -3.25, 150)
                            .addPos(0, 0, 0, 100)
                            .addPos(0, 0, 0, 250)
                            .addPos(0, 0, -3.25, 150)
                            .addPos(0, 0, 0, 100))
                    .addBus("PLUG", new BusAnimationSequence()
                            .addPos(0, 0, 0, 1000)
                            .addPos(0, 0, -3.25, 150)
                            .addPos(0, 0, 0, 100)
                            .addPos(0, 0, 0, 250)
                            .addPos(0, 0, -3.25, 150)
                            .addPos(0, 0, 0, 100));
            default -> null;
        };

    };
    /*
    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_STG77_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(45, 0, 0, 0)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence()
                        .addPos(0, 0, GunItem.getIsAiming(stack) ? -0.125 : -0.375, 25, IType.SIN_DOWN)
                        .addPos(0, 0, 0, 75, IType.SIN_FULL))
                .addBus("SAFETY", new BusAnimationSequence()
                        .addPos(0.25, 0, 0, 0)
                        .addPos(0.25, 0, 0, 2000)
                        .addPos(0, 0, 0, 50));
        case CYCLE_DRY -> HBMResourceManager.getWeaponAnimation("stg77", "FireDry");
        case RELOAD -> HBMResourceManager.getWeaponAnimation("stg77", "Reload");
        case INSPECT -> HBMResourceManager.getWeaponAnimation("stg77", "Inspect");
        default -> null;
    };

     */

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_STG77_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, GunItem.getIsAiming(stack) ? -0.125 : -0.375, 25, IType.SIN_DOWN).addPos(0, 0, 0, 75, IType.SIN_FULL))
                .addBus("SAFETY", new BusAnimationSequence().addPos(0.25, 0, 0, 0).addPos(0.25, 0, 0, 2000).addPos(0, 0, 0, 50));
        case CYCLE_DRY -> new BusAnimation()
                .addBus("BOLT", new BusAnimationSequence().addPos(0, 0, 0, 250).addPos(0, 0, -2, 150).addPos(0, 0, 0, 100, IType.SIN_UP))
                .addBus("SAFETY", new BusAnimationSequence().addPos(0.25, 0, 0, 0).addPos(0.25, 0, 0, 2000).addPos(0, 0, 0, 50));
        case RELOAD -> new BusAnimation()
                .addBus("BOLT", new BusAnimationSequence().addPos(0, 0, -2, 150).addPos(0, 0, -2, 1600).addPos(0, 0, 0, 100, IType.SIN_UP))
                .addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0, 20, 50).addPos(0, 0, 20, 1500).addPos(0, 0, 0, 50))
                .addBus("LIFT", new BusAnimationSequence().addPos(0, 0, 0, 200).addPos(-2, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL));
        case INSPECT -> new BusAnimation()
                .addBus("BOLT", new BusAnimationSequence().addPos(0, 0, -2, 150).addPos(0, 0, -2, 6100).addPos(0, 0, 0, 100, IType.SIN_UP))
                .addBus("HANDLE", new BusAnimationSequence().addPos(0, 0, 0, 150).addPos(0, 0, 20, 50).addPos(0, 0, 20, 6000).addPos(0, 0, 0, 50))
                .addBus("INSPECT_LEVER", new BusAnimationSequence().addPos(0, 0, 0, 500).addPos(0, 0, -10, 100).addPos(0, 0, -10, 100).addPos(0, 0, 0, 100))
                .addBus("INSPECT_BARREL", new BusAnimationSequence().addPos(0, 0, 0, 600).addPos(0, 0, 20, 150).addPos(0, 0, 0, 400).addPos(0, 0, 0, 500).addPos(15, 0, 0, 500).addPos(15, 0, 0, 2000).addPos(0, 0, 0, 500).addPos(0, 0, 0, 500).addPos(0, 0, 20, 200).addPos(0, 0, 20, 400).addPos(0, 0, 0, 150))
                .addBus("INSPECT_MOVE", new BusAnimationSequence().addPos(0, 0, 0, 750).addPos(0, 0, 6, 1000).addPos(2, 0, 3, 500, IType.SIN_FULL).addPos(2, 0.75, 0, 500, IType.SIN_FULL).addPos(2, 0.75, 0, 1000).addPos(2, 0, 3, 500, IType.SIN_FULL).addPos(0, 0, 6, 500).addPos(0, 0, 0, 1000))
                .addBus("INSPECT_GUN", new BusAnimationSequence().addPos(0, 0, 0, 1750).addPos(15, 0, -70, 500, IType.SIN_FULL).addPos(15, 0, -70, 1500).addPos(0, 0, 0, 500, IType.SIN_FULL));
        default -> null;
    };
}
