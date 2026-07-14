package com.hbm.items.weapon.sedna.factory;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.mods.WeaponModManager;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import net.minecraft.world.item.ItemStack;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class XFactory22lr {

    public static Function<ItemStack, String> LAMBDA_NAME_AM180 = (stack) -> {
        if(WeaponModManager.hasUpgrade(stack, 0, WeaponModManager.SILENCER)) return stack.getDescriptionId() + "_silenced";
        return null;
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_SMOKE = (stack, ctx)
            -> Lego.handleStandardSmoke(ctx.entity, stack, 3000, 0.05D, 1.1D, 0);

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_AM180 = (stack, ctx)
            -> GunItem.setupRecoil((float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.25), (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 0.25));

    @SuppressWarnings("incomplete-switch")
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_AM180_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence()
                        .addPos(45, 0, 0, 0)
                        .addPos(0, 0, 0, 500, IType.SIN_FULL));
        case CYCLE -> HBMResourceManager.getWeaponAnimation("am180", "Fire");
        case CYCLE_DRY -> HBMResourceManager.getWeaponAnimation("am180", "FireDry");
        case RELOAD -> HBMResourceManager.getWeaponAnimation("am180", "Reload");
        case JAMMED -> HBMResourceManager.getWeaponAnimation("am180", "Jammed");
        case INSPECT -> HBMResourceManager.getWeaponAnimation("am180", "Inspect");
        default -> null;
    };
}
