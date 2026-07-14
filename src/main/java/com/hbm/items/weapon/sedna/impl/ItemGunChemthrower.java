package com.hbm.items.weapon.sedna.impl;

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

import com.hbm.api.fluid.IFillableItem;
//import com.hbm.entity.projectile.EntityChemical;
import com.hbm.entity.projectile.EntityChemical;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.weapon.sedna.GunConfig;
import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.items.weapon.sedna.Receiver;
import com.hbm.items.weapon.sedna.mags.IMagazine;
import com.hbm.items.weapon.sedna.mags.MagazineFluid;
import com.hbm.render.anim.AnimationEnums.GunAnimation;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.level.Level;

public class ItemGunChemthrower extends GunItem implements IFillableItem {

    public static final int CONSUMPTION = 3;

    public ItemGunChemthrower(WeaponQuality quality, GunConfig... cfg) {
        super(quality, cfg);
    }

    @Override
    public boolean acceptsFluid(FluidTypeHBM type, ItemStack stack) {
        return getFluidType(stack) == type || getMagCount(stack) == 0;
    }

    public static final int transferSpeed = 50;

    @Override
    public int tryFill(FluidTypeHBM type, int amount, ItemStack stack) {
        if(!acceptsFluid(type, stack)) return amount;
        if(getMagCount(stack) == 0) setMagType(stack, Fluids.getID(type));

        int fill = getMagCount(stack);
        int req = Objects.requireNonNull(this.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)).getCapacity(stack) - fill;
        int toFill = Math.min(amount, req);
        toFill = Math.min(toFill, transferSpeed);
        setMagCount(stack, fill + toFill);

        return amount - toFill;
    }

    @Override
    public boolean isFull(ItemStack stack) {
        IMagazine mag = Objects.requireNonNull(
                this.getConfig(stack, 0).getReceivers(stack)[0].getMagazine(stack)
        );
        return getMagCount(stack) >= mag.getCapacity(stack);
    }

    public FluidTypeHBM getFluidType(ItemStack stack) {
        return Fluids.fromID(getMagType(stack));
    }

    @Override
    public boolean providesFluid(FluidTypeHBM type, ItemStack stack) {
        return getFluidType(stack) == type;
    }

    @Override
    public int tryEmpty(FluidTypeHBM type, int amount, ItemStack stack) {
        int fill = getMagCount(stack);
        int toUnload = Math.min(fill, amount);
        toUnload = Math.min(toUnload, transferSpeed);
        setMagCount(stack, fill - toUnload);
        return toUnload;
    }

    @Override
    public FluidTypeHBM getFirstFluidType(ItemStack stack) {
        return Fluids.fromID(getMagType(stack));
    }

    @Override
    public int getFill(ItemStack stack) {
        return getMagCount(stack);
    }

    public static int getMagType(ItemStack stack) {
        return GunItem.getInt(stack, MagazineFluid.KEY_MAG_TYPE + 0);
    }

    public static void setMagType(ItemStack stack, int value) {
        GunItem.setInt(stack, MagazineFluid.KEY_MAG_TYPE + 0, value);
    }

    public static int getMagCount(ItemStack stack) {
        return GunItem.getInt(stack, MagazineFluid.KEY_MAG_COUNT + 0);
    }

    public static void setMagCount(ItemStack stack, int value) {
        GunItem.setInt(stack, MagazineFluid.KEY_MAG_COUNT + 0, value);
    }

    public static BiFunction<ItemStack, LambdaContext, Boolean> LAMBDA_CAN_FIRE =
            (stack, ctx) -> Objects.requireNonNull(ctx.config.getReceivers(stack)[0].getMagazine(stack)).getAmount(stack, ctx.inventory) >= CONSUMPTION;

    public static BiConsumer<ItemStack, LambdaContext> LAMBDA_FIRE = (stack, ctx) -> {
        LivingEntity entity = ctx.entity;
        Player player = ctx.getPlayer();
        int index = ctx.configIndex;

        // Анимация стрельбы
        GunItem.playAnimation(player, stack, GunAnimation.CYCLE, ctx.configIndex);

        Receiver primary = ctx.config.getReceivers(stack)[0];
        IMagazine mag = primary.getMagazine(stack);

        Vec3 offset = primary.getProjectileOffset(stack);
        double forwardOffset = offset.x();
        double heightOffset = offset.y();
        double sideOffset = offset.z();

        Level level = entity.level();

        EntityChemical chem = new EntityChemical(level, entity, sideOffset, heightOffset, forwardOffset);
        chem.setFluid((FluidTypeHBM) Objects.requireNonNull(mag).getType(stack, ctx.inventory));

        level.addFreshEntity(chem);

        // Используем боеприпасы
        mag.useUpAmmo(stack, ctx.inventory, CONSUMPTION);

        // Износ оружия
        GunItem.setWear(stack, index,
                Math.min(GunItem.getWear(stack, index) + 1F, ctx.config.getDurability(stack)));
    };
}