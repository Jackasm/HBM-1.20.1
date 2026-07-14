package com.hbm.items.weapon.sedna.factory;

import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.entity.projectile.EntityCoin;
import com.hbm.items.weapon.sedna.BulletConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.impl.ItemGunNI4NI;
import com.hbm.items.weapon.sedna.mags.MagazineBelt;
import com.hbm.main.ClientProxy;
import com.hbm.render.anim.AnimationEnums;
import com.hbm.render.anim.BusAnimation;
import com.hbm.render.anim.BusAnimationSequence;
import com.hbm.render.anim.BusAnimationKeyframe.IType;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;

import static com.hbm.items.weapon.sedna.factory.BulletConfigRegistry.tau_uranium;
import static com.hbm.items.weapon.sedna.factory.BulletConfigRegistry.tau_uranium_charge;
import static com.hbm.sound.ModSounds.WEAPON_FIRE_TAURELEASE;

public class XFactoryAccelerator {


    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_TAU = (stack, ctx) -> { };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_TAU_PRIMARY_RELEASE = (stack, ctx) -> {
        if(ctx.getPlayer() == null || GunItem.getLastAnim(stack, ctx.configIndex) != AnimationEnums.GunAnimation.CYCLE) return;
        ctx.getPlayer().level().playSound(
                null,
                ctx.getPlayer().getX(), ctx.getPlayer().getY(), ctx.getPlayer().getZ(),
                WEAPON_FIRE_TAURELEASE.get(),
                ctx.getPlayer().getSoundSource(),
                1F, 1F);
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_RECOIL_COILGUN = (stack, ctx) -> GunItem.setupRecoil(10, (float) (Objects.requireNonNull(ctx.getPlayer()).getRandom().nextGaussian() * 1.5));

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_TAU_SECONDARY_PRESS = (stack, ctx) -> {
        if(ctx.getPlayer() == null) return;
        if(Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, ctx.inventory) <= 0) return;
        MagazineBelt.setMagType(stack, tau_uranium_charge.id);
        GunItem.playAnimation(ctx.getPlayer(), stack, AnimationEnums.GunAnimation.SPINUP, ctx.configIndex);
        MagazineBelt.getMagType(stack); //caches the last loaded ammo
    };

    public static Consumer<Entity> LAMBDA_UPDATE_TUNGSTEN = (entity) -> breakInPath(entity, 1.25F);
    public static Consumer<Entity> LAMBDA_UPDATE_FERRO = (entity) -> breakInPath(entity, 2.5F);

    public static void breakInPath(Entity entity, float threshold) {
        // Получаем вектор перемещения
        Vec3 vec = new Vec3(
                entity.getX() - entity.xo,
                entity.getY() - entity.yo,
                entity.getZ() - entity.zo
        );

        // Вычисляем длину перемещения
        double motion = Math.max(vec.length(), 0.1);
        vec = vec.normalize();

        // Проходим по пути перемещения
        for (double d = 0; d < motion; d += 0.5) {
            // Вычисляем точку на пути
            double dX = entity.getX() - vec.x * d;
            double dY = entity.getY() - vec.y * d;
            double dZ = entity.getZ() - vec.z * d;

            if (entity.level().isClientSide()) {
                // Клиентская часть: создаем эффект
                CompoundTag nbt = new CompoundTag();
                nbt.putString("type", "vanillaExt");
                nbt.putString("mode", "fireworks");
                nbt.putDouble("posX", dX);
                nbt.putDouble("posY", dY);
                nbt.putDouble("posZ", dZ);

                ClientProxy proxy = new ClientProxy();
                proxy.effectNT(nbt);

            } else {
                // Серверная часть: разрушаем блоки
                BlockPos pos = BlockPos.containing(dX, dY, dZ);
                BlockState state = entity.level().getBlockState(pos);

                // Проверяем, можно ли разрушить блок
                if (!state.isAir() &&
                        state.getDestroySpeed(entity.level(), pos) >= 0 &&
                        state.getDestroySpeed(entity.level(), pos) < threshold) {

                    // Разрушаем блок
                    entity.level().destroyBlock(pos, false);
                }
            }
        }
    }

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_TAU_SECONDARY_RELEASE = (stack, ctx) -> {
        if(ctx.getPlayer() == null) return;
        int timer = GunItem.getAnimTimer(stack, ctx.configIndex);

        if(timer >= 10 && GunItem.getLastAnim(stack, ctx.configIndex) == AnimationEnums.GunAnimation.SPINUP) {
            GunItem.playAnimation(ctx.getPlayer(), stack, AnimationEnums.GunAnimation.ALT_CYCLE, ctx.configIndex);
            int unitsUsed = 1 + Math.min(12, timer / 10);

            LivingEntity entity = ctx.entity;
            int index = ctx.configIndex;

            Receiver primary = ctx.config.getReceivers(stack)[0];
            BulletConfig config = tau_uranium_charge;

            Vec3 offset = primary.getProjectileOffset(stack);
            double forwardOffset = offset.x;
            double heightOffset = offset.y;
            double sideOffset = offset.z;

            float damage = Lego.getStandardWearDamage(stack, ctx.config, index) * unitsUsed * 5;
            float spread = Lego.calcSpread(ctx, stack, primary, config, true, index, false);

            EntityBulletBeamBase beam = new EntityBulletBeamBase(
                    entity.level(),
                    entity,
                    config,
                    damage,
                    spread,
                    sideOffset,
                    heightOffset,
                    forwardOffset
            );

            entity.level().addFreshEntity(beam);

            GunItem.setWear(stack, index, Math.min(GunItem.getWear(stack, index) + config.wear * unitsUsed, ctx.config.getDurability(stack)));
            MagazineBelt.setMagType(stack, tau_uranium.id);

        } else {
            MagazineBelt.setMagType(stack, tau_uranium.id);
            GunItem.playAnimation(ctx.getPlayer(), stack, AnimationEnums.GunAnimation.CYCLE_DRY, ctx.configIndex);
        }
    };

    public static BiConsumer<ItemStack, GunItem.LambdaContext> LAMBDA_NI4NI_SECONDARY_PRESS = (stack, ctx) -> {
        if(ctx.getPlayer() == null) return;
        Player player = ctx.getPlayer();
        double yOffset = 0.5D;

        if(ItemGunNI4NI.getCoinCount(stack) > 0) {
            Vec3 lookVec = player.getLookAngle().scale(0.8D);
            EntityCoin coin = new EntityCoin(player.level());
            coin.setPos(
                    player.getX(),
                    player.getY() + player.getEyeHeight() - yOffset - 0.125,
                    player.getZ()
            );
            coin.setDeltaMovement(
                    lookVec.x * 0.8D,            // motionX
                    lookVec.y * 0.8D + 0.5,      // motionY + 0.5
                    lookVec.z * 0.8D             // motionZ
            );
            coin.setYRot(player.getYRot());
            coin.setOwner(player);
            player.level().addFreshEntity(coin);

            player.level().playSound(
                    null,
                    player.getX(), player.getY(), player.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP,player.getSoundSource(),
                    1.0F, 1F + player.getRandom().nextFloat() * 0.25F);

            ItemGunNI4NI.setCoinCount(stack, ItemGunNI4NI.getCoinCount(stack) - 1);
        }
    };

    @SuppressWarnings("incomplete-switch") 
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_TAU_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(45, 0, 0, 0).addPos(0, 0, 0, 500, IType.SIN_FULL));
        case CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -0.5, 50).addPos(0, 0, 0, 150, IType.SIN_FULL))
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, -5, 50, IType.SIN_DOWN).addPos(0, 0, 5, 100, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP));
        case ALT_CYCLE -> new BusAnimation()
                .addBus("RECOIL", new BusAnimationSequence().addPos(0, 0, -3, 100, IType.SIN_DOWN).addPos(0, 0, 0, 250, IType.SIN_FULL))
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, -5, 50, IType.SIN_DOWN).addPos(0, 0, 5, 100, IType.SIN_FULL).addPos(0, 0, 0, 50, IType.SIN_UP));
        case CYCLE_DRY -> new BusAnimation();
        case INSPECT -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(2, 0, 0, 150, IType.SIN_DOWN).addPos(0, 0, 0, 100, IType.SIN_FULL))
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, -360 * 3, 500 * 3, IType.SIN_DOWN));
        case SPINUP -> new BusAnimation()
                .addBus("ROTATE", new BusAnimationSequence().addPos(0, 0, 360 * 6, 3000, IType.SIN_UP).addPos(0, 0, 0, 0).addPos(0, 0, 360 * 40, 500 * 20));
        default -> null;
    };

    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_COILGUN_ANIMS = (stack, type) -> {
        if(type == AnimationEnums.GunAnimation.EQUIP) return new BusAnimation().addBus("RELOAD", new BusAnimationSequence().addPos(1, 0, 0, 0).addPos(0, 0, 0, 250));
        if(type == AnimationEnums.GunAnimation.CYCLE) return new BusAnimation().addBus("RECOIL", new BusAnimationSequence().addPos(GunItem.getIsAiming(stack) ? 0.5 : 1, 0, 0, 100).addPos(0, 0, 0, 200));
        if(type == AnimationEnums.GunAnimation.RELOAD) return new BusAnimation().addBus("RELOAD", new BusAnimationSequence().addPos(1, 0, 0, 250).addPos(1, 0, 0, 500).addPos(0, 0, 0, 250));
        return null;
    };

    @SuppressWarnings("incomplete-switch") 
    public static BiFunction<ItemStack, AnimationEnums.GunAnimation, BusAnimation> LAMBDA_NI4NI_ANIMS = (stack, type) -> switch (type) {
        case EQUIP -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(-360 * 2, 0, 0, 500));
        case CYCLE -> {
            boolean aiming = GunItem.getIsAiming(stack);
            yield new BusAnimation()
                    .addBus("RECOIL", new BusAnimationSequence().addPos(aiming ? -5 : -30, 0, 0, 100, IType.SIN_DOWN).addPos(0, 0, 0, 150, IType.SIN_FULL))
                    .addBus("DRUM", new BusAnimationSequence().hold(50).addPos(0, 0, 120, 300, IType.SIN_FULL));
        }
        case INSPECT -> new BusAnimation()
                .addBus("EQUIP", new BusAnimationSequence().addPos(-360 * 3, 0, 0, 750).hold(100).addPos(0, 0, 0, 750));
        default -> null;
    };
}
