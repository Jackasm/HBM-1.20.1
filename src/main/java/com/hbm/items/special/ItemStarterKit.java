package com.hbm.items.special;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.ItemFluidBarrel;


import com.hbm.sound.ModSounds;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemStarterKit extends Item {

    public ItemStarterKit(Properties properties) {
        super(properties);
    }

    private void giveHaz(Level level, Player player, int tier) {
        if (level.isClientSide) return;

        // Сбрасываем текущую броню
        for (int i = 0; i < 4; i++) {
            ItemStack armor = player.getInventory().armor.get(i);
            if (!armor.isEmpty()) {
                player.drop(armor, false);
            }
        }

        switch (tier) {
            case 0:
                player.getInventory().armor.set(3, new ItemStack(ModArmorItems.HAZMAT_HELMET.get()));
                player.getInventory().armor.set(2, new ItemStack(ModArmorItems.HAZMAT_CHESTPLATE.get()));
                player.getInventory().armor.set(1, new ItemStack(ModArmorItems.HAZMAT_LEGGINGS.get()));
                player.getInventory().armor.set(0, new ItemStack(ModArmorItems.HAZMAT_BOOTS.get()));
                break;
            case 1:
                player.getInventory().armor.set(3, new ItemStack(ModArmorItems.HAZMAT_HELMET_RED.get()));
                player.getInventory().armor.set(2, new ItemStack(ModArmorItems.HAZMAT_CHESTPLATE_RED.get()));
                player.getInventory().armor.set(1, new ItemStack(ModArmorItems.HAZMAT_LEGGINGS_RED.get()));
                player.getInventory().armor.set(0, new ItemStack(ModArmorItems.HAZMAT_BOOTS_RED.get()));
                break;
            case 2:
                player.getInventory().armor.set(3, new ItemStack(ModArmorItems.HAZMAT_HELMET_GREY.get()));
                player.getInventory().armor.set(2, new ItemStack(ModArmorItems.HAZMAT_CHESTPLATE_GREY.get()));
                player.getInventory().armor.set(1, new ItemStack(ModArmorItems.HAZMAT_LEGGINGS_GREY.get()));
                player.getInventory().armor.set(0, new ItemStack(ModArmorItems.HAZMAT_BOOTS_GREY.get()));
                break;
        }
    }

    private void addItem(Player player, ItemStack stack) {
        if (!stack.isEmpty()) {
            ItemHandlerHelper.giveItemToPlayer(player, stack);
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.sidedSuccess(stack, true);
        }

        // Обработка всех типов наборов
        if (stack.getItem() == ModItems.NUKE_STARTER_KIT.get()) {
            handleStarterKit(level, player);
        } else if (stack.getItem() == ModItems.NUKE_ADVANCED_KIT.get()) {
            handleAdvancedKit(level, player);
        } else if (stack.getItem() == ModItems.NUKE_COMMERCIALLY_KIT.get()) {
            handleCommerciallyKit(level, player);
        } else if (stack.getItem() == ModItems.NUKE_ELECTRIC_KIT.get()) {
            handleElectricKit(level, player);
        } else if (stack.getItem() == ModItems.GADGET_KIT.get()) {
            handleGadgetKit(level, player);
        } else if (stack.getItem() == ModItems.BOY_KIT.get()) {
            handleBoyKit(level, player);
        } else if (stack.getItem() == ModItems.MAN_KIT.get()) {
            handleManKit(level, player);
        } else if (stack.getItem() == ModItems.MIKE_KIT.get()) {
            handleMikeKit(level, player);
        } else if (stack.getItem() == ModItems.TSAR_KIT.get()) {
            handleTsarKit(level, player);
        } else if (stack.getItem() == ModItems.MULTI_KIT.get()) {
            handleMultiKit(level, player);
        } else if (stack.getItem() == ModItems.CUSTOM_KIT.get()) {
            handleCustomKit(level, player);
        } else if (stack.getItem() == ModItems.GRENADE_KIT.get()) {
            handleGrenadeKit(level, player);
        } else if (stack.getItem() == ModItems.FLEIJA_KIT.get()) {
            handleFleijaKit(level, player);
        } else if (stack.getItem() == ModItems.SOLINIUM_KIT.get()) {
            handleSoliniumKit(level, player);
        } else if (stack.getItem() == ModItems.PROTOTYPE_KIT.get()) {
            handlePrototypeKit(level, player);
        } else if (stack.getItem() == ModItems.MISSILE_KIT.get()) {
            handleMissileKit(level, player);
        } else if (stack.getItem() == ModItems.STEALTH_BOY.get()) {
            player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 30 * 20, 1, false, false, true));
        } else if (stack.getItem() == ModItems.EUPHEMIUM_KIT.get()) {
            handleEuphemiumKit(level, player);
        } else if (stack.getItem() == ModItems.HAZMAT_KIT.get()) {
            giveHaz(level, player, 0);
        } else if (stack.getItem() == ModItems.HAZMAT_RED_KIT.get()) {
            giveHaz(level, player, 1);
        } else if (stack.getItem() == ModItems.HAZMAT_GREY_KIT.get()) {
            giveHaz(level, player, 2);
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                ModSounds.UNPACK.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        stack.shrink(1);

        return InteractionResultHolder.sidedSuccess(stack, true);
    }

    // Часть 2 — методы обработки наборов
    private void handleStarterKit(Level level, Player player) {
        addItem(player, new ItemStack(ModItems.INGOT_URANIUM.get(), 32));
        addItem(player, new ItemStack(ModItems.POWDER_YELLOWCAKE.get(), 32));
        //addItem(player, new ItemStack(ModItems.TEMPLATE_FOLDER.get()));
        addItem(player, new ItemStack(ModBlocks.MACHINE_PRESS.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_DIFURNACE_OFF.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_GASCENT.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_REACTOR_BREEDING.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_ASSEMBLY_MACHINE.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_CHEMICAL_PLANT.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.REACTOR_RESEARCH.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_TURBINE.get().asItem(), 2));
        addItem(player, new ItemStack(ModItems.RADAWAY.get(), 8));
        addItem(player, new ItemStack(ModItems.RADX.get(), 2));
        addItem(player, new ItemStack(ModItems.STAMP_TITANIUM_FLAT.get(), 3));
        addItem(player, new ItemStack(ModItems.INGOT_STEEL.get(), 64));
        addItem(player, new ItemStack(ModItems.INGOT_LEAD.get(), 64));
        addItem(player, new ItemStack(Items.COPPER_INGOT, 64));
        addItem(player, new ItemStack(ModArmorItems.GAS_MASK_M65.get()));
        addItem(player, new ItemStack(ModItems.GEIGER_COUNTER.get()));

        giveHaz(level, player, 1);
    }

    private void handleAdvancedKit(Level level, Player player) {
        addItem(player, new ItemStack(ModItems.POWDER_YELLOWCAKE.get(), 64));
        addItem(player, new ItemStack(ModItems.POWDER_PLUTONIUM.get(), 64));
        addItem(player, new ItemStack(ModItems.INGOT_STEEL.get(), 64));
        addItem(player, new ItemStack(Items.COPPER_INGOT, 64));
        addItem(player, new ItemStack(ModItems.INGOT_TUNGSTEN.get(), 64));
        addItem(player, new ItemStack(ModItems.INGOT_LEAD.get(), 64));
        addItem(player, new ItemStack(ModItems.INGOT_POLYMER.get(), 64));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_DIFURNACE_OFF.get().asItem(), 3));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_GASCENT.get().asItem(), 3));
        addItem(player, new ItemStack(ModBlocks.MACHINE_CENTRIFUGE.get().asItem(), 2));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_UF6_TANK.get().asItem(), 2));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_PUF6_TANK.get().asItem(), 2));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_REACTOR_BREEDING.get().asItem(), 2));
        //addItem(player, new ItemStack(ModBlocks.REACTOR_RESEARCH.get().asItem(), 4));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_TURBINE.get().asItem(), 4));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_RADGEN.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_RTG_GREY.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_ASSEMBLY_MACHINE.get().asItem(), 3));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_CHEMICAL_PLANT.get().asItem(), 2));
        addItem(player, new ItemStack(ModBlocks.MACHINE_FLUIDTANK.get().asItem()));
        addItem(player, new ItemStack(ModItems.PELLET_RTG.get(), 3));
        addItem(player, new ItemStack(ModItems.PELLET_RTG_WEAK.get(), 3));
        addItem(player, new ItemStack(ModItems.CELL_EMPTY.get(), 32));
        addItem(player, new ItemStack(ModItems.ROD_EMPTY.get(), 32));
        // Бочки с жидкостями нужно создавать через метод createForFluid
        addItem(player, createFluidBarrel(Fluids.COOLANT.get(), 4));
        addItem(player, new ItemStack(ModItems.RADAWAY_STRONG.get(), 4));
        addItem(player, new ItemStack(ModItems.RADX.get(), 4));
        addItem(player, new ItemStack(ModItems.PILL_IODINE.get()));
        addItem(player, new ItemStack(ModItems.GEIGER_COUNTER.get()));
        addItem(player, new ItemStack(ModItems.SURVEY_SCANNER.get()));
        addItem(player, new ItemStack(ModArmorItems.GAS_MASK_M65.get()));

        giveHaz(level, player, 2);
    }

    private ItemStack createFluidBarrel(FluidTypeHBM fluid, int count) {
        ItemStack stack = ItemFluidBarrel.createForFluid(fluid);
        stack.setCount(count);
        return stack;
    }

    private void handleCommerciallyKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.REACTOR_RESEARCH.get().asItem(), 8));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_REACTOR_BREEDING.get().asItem(), 8));
        addItem(player, new ItemStack(ModBlocks.MACHINE_FLUIDTANK.get().asItem(), 8));
        addItem(player, new ItemStack(ModItems.BILLET_PU238BE.get(), 40));
        addItem(player, new ItemStack(ModItems.INGOT_U233.get(), 40));
        addItem(player, new ItemStack(ModItems.INGOT_URANIUM_FUEL.get(), 32));
        addItem(player, new ItemStack(ModItems.INGOT_PLUTONIUM_FUEL.get(), 16));
        addItem(player, new ItemStack(ModItems.INGOT_MOX_FUEL.get(), 8));
        addItem(player, new ItemStack(ModItems.INF_WATER_MK2.get(), 3));
        addItem(player, new ItemStack(ModItems.ROD_EMPTY.get(), 64));
        //addItem(player, new ItemStack(ModItems.ROD_DUAL_EMPTY.get(), 64));
        //addItem(player, new ItemStack(ModItems.ROD_QUAD_EMPTY.get(), 64));
        addItem(player, new ItemStack(ModItems.FLUID_TANK_LEAD.get(), 64));
        addItem(player, new ItemStack(ModItems.FLUID_BARREL.get(), 64));
        addItem(player, new ItemStack(ModBlocks.BARREL_STEEL.get().asItem(), 16));
        addItem(player, new ItemStack(ModItems.PLATE_IRON.get(), 64));
        addItem(player, new ItemStack(net.minecraft.world.item.Items.BLUE_DYE, 64));
        //addItem(player, new ItemStack(ModItems.TEMPLATE_FOLDER.get()));
        addItem(player, new ItemStack(ModItems.RADAWAY_FLUSH.get(), 8));
        addItem(player, new ItemStack(ModItems.IV_BLOOD.get(), 8));
        addItem(player, new ItemStack(ModItems.PILL_IODINE.get(), 8));
        addItem(player, new ItemStack(ModItems.GAS_MASK_FILTER_COMBO.get(), 3));

        giveHaz(level, player, 2);
    }

    private void handleElectricKit(Level level, Player player) {
        addItem(player, new ItemStack(ModItems.COIL_COPPER.get(), 16));
        addItem(player, new ItemStack(ModItems.COIL_GOLD.get(), 8));
        addItem(player, new ItemStack(ModItems.COIL_TUNGSTEN.get(), 8));
        addItem(player, new ItemStack(ModItems.MOTOR.get(), 4));
        addItem(player, new ItemStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 16));
        addItem(player, new ItemStack(ModItems.CIRCUIT_CAPACITOR.get(), 16));
        addItem(player, new ItemStack(ModItems.CIRCUIT_BASIC.get(), 16));
        addItem(player, new ItemStack(ModItems.WIRING_RED_COPPER.get()));
        addItem(player, new ItemStack(ModItems.MAGNETRON.get(), 5));
        addItem(player, new ItemStack(ModItems.PISTON_SELENIUM.get(), 3));
        addItem(player, createFluidBarrel(Fluids.DIESEL.get(), 16));
        addItem(player, createFluidBarrel(Fluids.BIOFUEL.get(), 16));
        addItem(player, new ItemStack(ModItems.BATTERY_ADVANCED_CELL_4.get(), 2));
        addItem(player, new ItemStack(ModItems.BATTERY_LITHIUM.get(), 2));
        addItem(player, new ItemStack(ModItems.BATTERY_POTATO.get()));
        addItem(player, new ItemStack(ModItems.SCREWDRIVER.get()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_EXCAVATOR.get().asItem()));
        addItem(player, new ItemStack(ModBlocks.MACHINE_DIESEL.get().asItem(), 2));
        addItem(player, new ItemStack(ModBlocks.RED_CABLE.get().asItem(), 64));
        //addItem(player, new ItemStack(ModBlocks.RED_WIRE_COATED.get().asItem(), 16));
        //addItem(player, new ItemStack(ModBlocks.RED_PYLON.get().asItem(), 8));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_BATTERY.get().asItem(), 4));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_LITHIUM_BATTERY.get().asItem(), 2));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_CONVERTER_HE_RF.get().asItem()));
        //addItem(player, new ItemStack(ModBlocks.MACHINE_CONVERTER_RF_HE.get().asItem()));
    }

    private void handleGadgetKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.NUKE_GADGET.get().asItem()));
        //addItem(player, new ItemStack(ModItems.EARLY_EXPLOSIVE_LENSES.get(), 4));
        //addItem(player, new ItemStack(ModItems.GADGET_WIREING.get()));
        //addItem(player, new ItemStack(ModItems.GADGET_CORE.get()));

        giveHaz(level, player, 0);
    }

    private void handleBoyKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.NUKE_BOY.get().asItem()));
        addItem(player, new ItemStack(ModItems.BOY_SHIELDING.get()));
        addItem(player, new ItemStack(ModItems.BOY_TARGET.get()));
        addItem(player, new ItemStack(ModItems.BOY_BULLET.get()));
        //addItem(player, new ItemStack(ModItems.BOY_PROPELLANT.get()));
        //addItem(player, new ItemStack(ModItems.BOY_IGNITER.get()));

        giveHaz(level, player, 0);
    }

    private void handleManKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.NUKE_MAN.get().asItem()));
        //addItem(player, new ItemStack(ModItems.EARLY_EXPLOSIVE_LENSES.get(), 4));
        //addItem(player, new ItemStack(ModItems.MAN_IGNITER.get()));
        addItem(player, new ItemStack(ModItems.MAN_CORE.get()));

        giveHaz(level, player, 0);
    }

    private void handleMikeKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.NUKE_MIKE.get().asItem()));
        //addItem(player, new ItemStack(ModItems.EXPLOSIVE_LENSES.get(), 4));
        addItem(player, new ItemStack(ModItems.MAN_CORE.get()));
        //addItem(player, new ItemStack(ModItems.MIKE_CORE.get()));
        //addItem(player, new ItemStack(ModItems.MIKE_DEUT.get()));
        //addItem(player, new ItemStack(ModItems.MIKE_COOLING_UNIT.get()));

        giveHaz(level, player, 0);
    }

    private void handleTsarKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.NUKE_TSAR.get().asItem()));
        //addItem(player, new ItemStack(ModItems.EXPLOSIVE_LENSES.get(), 4));
        addItem(player, new ItemStack(ModItems.MAN_CORE.get()));
        //addItem(player, new ItemStack(ModItems.TSAR_CORE.get()));

        giveHaz(level, player, 0);
    }

    private void handleMultiKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.BOMB_MULTI.get().asItem(), 6));
        addItem(player, new ItemStack(net.minecraft.world.level.block.Blocks.TNT, 26));
        addItem(player, new ItemStack(net.minecraft.world.item.Items.GUNPOWDER, 2));
        addItem(player, new ItemStack(ModItems.PELLET_CLUSTER.get(), 2));
        addItem(player, new ItemStack(ModItems.POWDER_FIRE.get(), 2));
        addItem(player, new ItemStack(ModItems.POWDER_POISON.get(), 2));
        addItem(player, new ItemStack(ModItems.PELLET_GAS.get(), 2));
    }

    private void handleCustomKit(Level level, Player player) {
        addItem(player, new ItemStack(ModBlocks.NUKE_CUSTOM.get().asItem()));
        addItem(player, new ItemStack(ModItems.CUSTOM_TNT.get(), 6));
        addItem(player, new ItemStack(ModItems.CUSTOM_NUKE.get(), 4));
        addItem(player, new ItemStack(ModItems.CUSTOM_HYDRO.get(), 2));
        addItem(player, new ItemStack(ModItems.CUSTOM_AMAT.get(), 2));
        addItem(player, new ItemStack(ModItems.CUSTOM_DIRTY.get(), 3));
        addItem(player, new ItemStack(ModItems.CUSTOM_SCHRAB.get()));
        addItem(player, new ItemStack(ModItems.CUSTOM_FALL.get()));
    }

    private void handleGrenadeKit(Level level, Player player) {
        addItem(player, new ItemStack(ModItems.GRENADE_GENERIC.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_STRONG.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_FRAG.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_FIRE.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_SHRAPNEL.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_CLUSTER.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_FLARE.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_ELECTRIC.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_POISON.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_GAS.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_CLOUD.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_PINK_CLOUD.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_SMART.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_MIRV.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_BREACH.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_BURST.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_PULSE.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_PLASMA.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_TAU.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_SCHRABIDIUM.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_LEMON.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_GASCAN.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_MK2.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_ASCHRAB.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_NUKE.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_NUCLEAR.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_ZOMG.get(), 16));
        addItem(player, new ItemStack(ModItems.GRENADE_BLACK_HOLE.get(), 16));
    }

    private void handleFleijaKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.NUKE_FLEIJA.get().asItem()));
        //addItem(player, new ItemStack(ModItems.FLEIJA_IGNITER.get(), 2));
        //addItem(player, new ItemStack(ModItems.FLEIJA_PROPELLANT.get(), 3));
        //addItem(player, new ItemStack(ModItems.FLEIJA_CORE.get(), 6));

        giveHaz(level, player, 2);
    }

    private void handleSoliniumKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.NUKE_SOLINIUM.get().asItem()));
        //addItem(player, new ItemStack(ModItems.SOLINIUM_IGNITER.get(), 4));
        //addItem(player, new ItemStack(ModItems.SOLINIUM_PROPELLANT.get(), 4));
        //addItem(player, new ItemStack(ModItems.SOLINIUM_CORE.get()));

        giveHaz(level, player, 1);
    }

    private void handlePrototypeKit(Level level, Player player) {
        //addItem(player, new ItemStack(ModBlocks.NUKE_PROTOTYPE.get().asItem()));
        //addItem(player, new ItemStack(ModItems.IGNITER.get()));
        addItem(player, new ItemStack(ModItems.CELL_SAS3.get(), 4));
        //addItem(player, new ItemStack(ModItems.ROD_QUAD.get(), 4, ItemBreedingRod.BreedingRodType.URANIUM.ordinal()));
        //addItem(player, new ItemStack(ModItems.ROD_QUAD.get(), 4, ItemBreedingRod.BreedingRodType.LEAD.ordinal()));
        //addItem(player, new ItemStack(ModItems.ROD_QUAD.get(), 2, ItemBreedingRod.BreedingRodType.NP237.ordinal()));

        giveHaz(level, player, 2);
    }

    private void handleMissileKit(Level level, Player player) {
        addItem(player, new ItemStack(ModBlocks.LAUNCH_PAD.get().asItem()));
        addItem(player, new ItemStack(ModItems.DESIGNATOR.get()));
        addItem(player, new ItemStack(ModItems.DESIGNATOR_RANGE.get()));
        addItem(player, new ItemStack(ModItems.DESIGNATOR_MANUAL.get()));
        addItem(player, new ItemStack(ModItems.BATTERY_SCHRABIDIUM_CELL_4.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_GENERIC.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_STRONG.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_BURST.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_INCENDIARY.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_INCENDIARY_STRONG.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_INFERNO.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_CLUSTER.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_CLUSTER_STRONG.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_RAIN.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_BUSTER.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_BUSTER_STRONG.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_DRILL.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_NUCLEAR.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_NUCLEAR_CLUSTER.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_VOLCANO.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_DOOMSDAY.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_TAINT.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_MICRO.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_BHOLE.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_SCHRABIDIUM.get()));
        addItem(player, new ItemStack(ModItems.MISSILE_EMP.get()));
    }

    private void handleEuphemiumKit(Level level, Player player) {
        addItem(player, new ItemStack(ModArmorItems.EUPHEMIUM_HELMET.get()));
        addItem(player, new ItemStack(ModArmorItems.EUPHEMIUM_CHESTPLATE.get()));
        addItem(player, new ItemStack(ModArmorItems.EUPHEMIUM_LEGGINGS.get()));
        addItem(player, new ItemStack(ModArmorItems.EUPHEMIUM_BOOTS.get()));
        addItem(player, new ItemStack(ModBlocks.STATUE_ELB_F.get().asItem()));
    }
    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Item item = stack.getItem();

        boolean isBigKit = item == ModItems.NUKE_STARTER_KIT.get() ||
                item == ModItems.NUKE_ADVANCED_KIT.get() ||
                item == ModItems.NUKE_COMMERCIALLY_KIT.get() ||
                item == ModItems.NUKE_ELECTRIC_KIT.get() ||
                item == ModItems.GADGET_KIT.get() ||
                item == ModItems.BOY_KIT.get() ||
                item == ModItems.MAN_KIT.get() ||
                item == ModItems.MIKE_KIT.get() ||
                item == ModItems.TSAR_KIT.get() ||
                item == ModItems.PROTOTYPE_KIT.get() ||
                item == ModItems.FLEIJA_KIT.get() ||
                item == ModItems.SOLINIUM_KIT.get() ||
                item == ModItems.GRENADE_KIT.get() ||
                item == ModItems.MISSILE_KIT.get() ||
                item == ModItems.MULTI_KIT.get();

        boolean hasHazmat = item == ModItems.NUKE_STARTER_KIT.get() ||
                item == ModItems.NUKE_ADVANCED_KIT.get() ||
                item == ModItems.NUKE_COMMERCIALLY_KIT.get() ||
                item == ModItems.GADGET_KIT.get() ||
                item == ModItems.BOY_KIT.get() ||
                item == ModItems.MAN_KIT.get() ||
                item == ModItems.MIKE_KIT.get() ||
                item == ModItems.TSAR_KIT.get() ||
                item == ModItems.PROTOTYPE_KIT.get() ||
                item == ModItems.FLEIJA_KIT.get() ||
                item == ModItems.SOLINIUM_KIT.get() ||
                item == ModItems.HAZMAT_KIT.get();

        if (isBigKit) {
            tooltip.add(Component.literal("Please empty inventory before opening!"));
        }
        if (hasHazmat) {
            tooltip.add(Component.literal("Armor will be displaced by hazmat suit."));
        }
    }
}