package com.hbm.items;


import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockConcreteColoredExt;
import com.hbm.blocks.generic.BlockNTMFlower;
import com.hbm.blocks.generic.BlockPlushie;
import com.hbm.blocks.generic.BlockSnowglobe;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.gui.GUIScreenBobmazon.Offer;
import com.hbm.inventory.gui.GUIScreenBobmazon.Requirement;
import com.hbm.items.block.ItemBlockConcreteColoredExt;
import com.hbm.items.block.ItemPlushie;
import com.hbm.items.block.ItemSnowglobe;
import com.hbm.items.fluid.ItemFluidBarrel;
import com.hbm.items.machine.ItemBattery;
import com.hbm.items.special.ItemKitCustom;
import com.hbm.items.special.ItemKitNBT;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public class BobmazonOfferFactory {

    public static List<Offer> standard = new ArrayList<>();
    public static List<Offer> special = new ArrayList<>();

    public static void init() {
        standard.clear();
        special.clear();

        // gear
        standard.add(new Offer(new ItemStack(Blocks.TORCH, 64), Requirement.NONE, 2));
        standard.add(new Offer(new ItemStack(ModItems.DEFINITELYFOOD.get(), 16), Requirement.NONE, 4));
        standard.add(new Offer(new ItemStack(ModItems.NITRA.get(), 4), Requirement.CHEMICS, 16));
        standard.add(new Offer(new ItemStack(ModItems.GUN_KIT_1.get(), 1), Requirement.ASSEMBLY, 16));
        standard.add(new Offer(new ItemStack(ModItems.GEIGER_COUNTER.get(), 1), Requirement.NONE, 16));
        standard.add(new Offer(new ItemStack(ModItems.MATCHSTICK.get(), 16), Requirement.STEEL, 2));

        // blueprints
        standard.add(new Offer(blueprintFolder(0, 1), Requirement.ASSEMBLY, 64));
        standard.add(new Offer(blueprintFolder(1, 1), Requirement.OIL, 256));

        // plants
        standard.add(new Offer(new ItemStack(Blocks.OAK_SAPLING, 1), Requirement.STEEL, 12, 9));
        standard.add(new Offer(flowerStack(BlockNTMFlower.EnumFlowerType.FOXGLOVE, 1), Requirement.STEEL, 16, 5));
        standard.add(new Offer(flowerStack(BlockNTMFlower.EnumFlowerType.TOBACCO, 1), Requirement.STEEL, 16, 9));
        standard.add(new Offer(flowerStack(BlockNTMFlower.EnumFlowerType.NIGHTSHADE, 1), Requirement.STEEL, 16, 3));
        standard.add(new Offer(flowerStack(BlockNTMFlower.EnumFlowerType.WEED, 1), Requirement.STEEL, 4, 10));
        standard.add(new Offer(flowerStack(BlockNTMFlower.EnumFlowerType.CD0, 1), Requirement.NUCLEAR, 64, 8));

        // deco
        for (BlockConcreteColoredExt.EnumConcreteType conc : BlockConcreteColoredExt.EnumConcreteType.values()) {
            standard.add(new Offer(ItemBlockConcreteColoredExt.withType(conc.ordinal(), 16), Requirement.CHEMICS, 4));
        }
        for (BlockSnowglobe.SnowglobeType globe : BlockSnowglobe.SnowglobeType.values()) {
            if (globe != BlockSnowglobe.SnowglobeType.NONE) {
                standard.add(new Offer(ItemSnowglobe.withType(globe), Requirement.CHEMICS, 128));
            }
        }

        for (int i = 1; i < BlockPlushie.PlushieType.values().length; i++) {
            standard.add(new Offer(ItemPlushie.withType(BlockPlushie.PlushieType.values()[i]), Requirement.OIL, 16, i < 3 ? 10 : 0));
        }

        special.add(new Offer(new ItemStack(Items.IRON_INGOT, 64), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.INGOT_STEEL.get(), 64), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(Items.COPPER_INGOT, 64), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.INGOT_RED_COPPER.get(), 64), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.INGOT_TITANIUM.get(), 64), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.INGOT_TUNGSTEN.get(), 64), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.INGOT_COBALT.get(), 64), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.INGOT_DESH.get(), 64), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.INGOT_TANTALIUM.get(), 64), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModItems.INGOT_BISMUTH.get(), 16), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModItems.INGOT_SCHRABIDIUM.get(), 16), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModItems.INGOT_EUPHEMIUM.get(), 8), Requirement.STEEL, 16));
        special.add(new Offer(new ItemStack(ModItems.INGOT_DINEUTRONIUM.get(), 1), Requirement.STEEL, 16));
        special.add(new Offer(new ItemStack(ModItems.INGOT_STARMETAL.get(), 16), Requirement.STEEL, 8));
        special.add(new Offer(new ItemStack(ModItems.INGOT_SEMTEX.get(), 16), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.INGOT_U235.get(), 16), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.INGOT_PU239.get(), 16), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.AMMO_CONTAINER.get(), 16), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModItems.NUKE_STARTER_KIT.get()), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModItems.NUKE_ADVANCED_KIT.get()), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModItems.NUKE_COMMERCIALLY_KIT.get()), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModItems.BOY_KIT.get()), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModItems.PROTOTYPE_KIT.get()), Requirement.STEEL, 10));
        special.add(new Offer(new ItemStack(ModItems.MISSILE_KIT.get()), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModItems.GRENADE_KIT.get()), Requirement.STEEL, 5));
        special.add(new Offer(new ItemStack(ModArmorItems.JETPACK_VECTOR.get()), Requirement.STEEL, 2));
        special.add(new Offer(new ItemStack(ModItems.JETPACK_TANK.get()), Requirement.STEEL, 2));
        special.add(new Offer(new ItemStack(ModItems.GUN_KIT_1.get(), 1), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModItems.GUN_KIT_2.get(), 1), Requirement.STEEL, 3));
        special.add(new Offer(new ItemStack(ModBlocks.STRUCT_LAUNCHER_CORE.get().asItem(), 1), Requirement.STEEL, 3));
        special.add(new Offer(new ItemStack(ModBlocks.STRUCT_LAUNCHER_CORE_LARGE.get().asItem(), 1), Requirement.STEEL, 3));
        special.add(new Offer(new ItemStack(ModBlocks.STRUCT_LAUNCHER.get().asItem(), 40), Requirement.STEEL, 7));
        special.add(new Offer(new ItemStack(ModBlocks.STRUCT_SCAFFOLD.get().asItem(), 11), Requirement.STEEL, 7));
        special.add(new Offer(new ItemStack(ModItems.LOOT_10.get(), 1), Requirement.STEEL, 2));
        special.add(new Offer(new ItemStack(ModItems.LOOT_15.get(), 1), Requirement.STEEL, 2));
        special.add(new Offer(new ItemStack(ModItems.LOOT_MISC.get(), 1), Requirement.STEEL, 2));
        special.add(new Offer(new ItemStack(ModBlocks.CRATE_CAN.get().asItem(), 1), Requirement.STEEL, 1));
        special.add(new Offer(new ItemStack(ModBlocks.CRATE_AMMO.get().asItem(), 1), Requirement.STEEL, 2));
        special.add(new Offer(new ItemStack(ModToolItems.CRUCIBLE.get(), 1), Requirement.STEEL, 10));
        special.add(new Offer(new ItemStack(ModItems.SPAWN_CHOPPER.get(), 1), Requirement.STEEL, 10));
        special.add(new Offer(new ItemStack(ModItems.SPAWN_WORM.get(), 1), Requirement.STEEL, 10));
        special.add(new Offer(new ItemStack(ModItems.SPAWN_UFO.get(), 1), Requirement.STEEL, 10));
        special.add(new Offer(new ItemStack(ModItems.SAT_LASER.get(), 1), Requirement.HIDDEN, 8));
        special.add(new Offer(new ItemStack(ModItems.SAT_GERALD.get(), 1), Requirement.HIDDEN, 32));
        special.add(new Offer(new ItemStack(ModItems.BILLET_YHARONITE.get(), 4), Requirement.HIDDEN, 16));
        special.add(new Offer(new ItemStack(ModItems.INGOT_CHAINSTEEL.get(), 1), Requirement.HIDDEN, 16));
        special.add(new Offer(new ItemStack(ModItems.INGOT_ELECTRONIUM.get(), 1), Requirement.HIDDEN, 16));
        special.add(new Offer(new ItemStack(ModItems.BOOK_OF_.get(), 1), Requirement.HIDDEN, 16));
        special.add(new Offer(new ItemStack(ModToolItems.MESE_PICKAXE.get(), 1), Requirement.HIDDEN, 16));
        special.add(new Offer(new ItemStack(ModItems.MYSTERYSHOVEL.get(), 1), Requirement.HIDDEN, 16));
        special.add(new Offer(new ItemStack(ModBlocks.NTM_DIRT.get().asItem(), 1), Requirement.HIDDEN, 16));
        special.add(new Offer(new ItemStack(ModItems.EUPHEMIUM_KIT.get(), 1), Requirement.HIDDEN, 64));

        special.add(new Offer(ItemKitCustom.create("Fusion Man", "For the nuclear physicist on the go", 0xff00ff, 0x800080,
                //TODO Сделать эти блоки и вещи
                //new ItemStack(ModBlocks.ITER.get().asItem()),
                //new ItemStack(ModBlocks.PLASMA_HEATER.get().asItem()),
                //new ItemStack(ModItems.FUSION_SHIELD_VAPORWAVE.get()),
                ItemBattery.getFullBattery(ModItems.BATTERY_SPARK.get()),
                //new ItemStack(ModBlocks.MACHINE_CHEMPLANT.get().asItem(), 10),
                new ItemStack(ModBlocks.MACHINE_FLUIDTANK.get().asItem(), 8),
               // new ItemStack(ModBlocks.RED_WIRE_COATED.get().asItem(), 64),
                new ItemStack(ModBlocks.RED_CABLE.get().asItem(), 64),
                barrels(Fluids.DEUTERIUM.get(), 64),
                barrels(Fluids.TRITIUM.get(), 64),
                barrels(Fluids.XENON.get(), 64),
                barrels(Fluids.MERCURY.get(), 64),
                //new ItemStack(ModBlocks.RED_PYLON_LARGE.get().asItem(), 8),
                //new ItemStack(ModBlocks.SUBSTATION.get().asItem(), 4),
                //new ItemStack(ModBlocks.RED_PYLON.get().asItem(), 16),
                new ItemStack(ModBlocks.RED_CONNECTOR.get().asItem(), 64),
                new ItemStack(ModItems.WIRING_RED_COPPER.get(), 1),
                //new ItemStack(ModBlocks.MACHINE_CHUNGUS.get().asItem(), 1),
                //new ItemStack(ModBlocks.MACHINE_LARGE_TURBINE.get().asItem(), 3),
                //new ItemStack(ModItems.TEMPLATE_FOLDER.get(), 1),
                new ItemStack(Items.PAPER, 64),
                new ItemStack(Items.BLUE_DYE, 64)
        ), Requirement.HIDDEN, 64));

        special.add(new Offer(ItemKitCustom.create("Maid's Cleaning Utensils", "For the hard to reach spots", 0x00ff00, 0x008000,
                new ItemStack(ModGunItems.GUN_M2.get()),
                new ItemStack(ModAmmoItems.AMMO_BMG50_DU.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_BMG50_DU.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_BMG50_DU.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_BMG50_DU.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_BMG50_DU.get(), 64),
                new ItemStack(ModGunItems.GUN_AUTOSHOTGUN.get()),
                new ItemStack(ModAmmoItems.AMMO_G12_MAGNUM.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_G12_MAGNUM.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_G12_MAGNUM.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_G12_EXPLOSIVE.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_G12_EXPLOSIVE.get(), 64)
        ), Requirement.HIDDEN, 64));

        special.add(new Offer(ItemKitNBT.create(
                new ItemStack(ModToolItems.ROD_OF_DISCORD.get()).setHoverName(Component.literal("Cock Joke")),
                conserveStack(5, 64).setHoverName(Component.literal("Class A Horse Semen")),
                new ItemStack(ModToolItems.PIPE_LEAD.get()).setHoverName(Component.literal("Get Nutted, Dumbass")),
                new ItemStack(ModItems.GEM_ALEXANDRITE.get())
        ).setHoverName(Component.literal("The Nut Bucket")), Requirement.HIDDEN, 64));

        special.add(new Offer(ItemKitNBT.create(
                new ItemStack(ModArmorItems.RPA_HELMET.get()),
                new ItemStack(ModArmorItems.RPA_CHESTPLATE.get()),
                new ItemStack(ModArmorItems.RPA_LEGGINGS.get()),
                new ItemStack(ModArmorItems.RPA_BOOTS.get()),
                new ItemStack(ModGunItems.GUN_MINIGUN_LACUNAE.get()),
                new ItemStack(ModAmmoItems.AMMO_CAPACITOR_OVERCHARGE.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_CAPACITOR_OVERCHARGE.get(), 64),
                new ItemStack(ModAmmoItems.AMMO_CAPACITOR_OVERCHARGE.get(), 64)
        ).setHoverName(Component.literal("Frenchie's Reward")), Requirement.HIDDEN, 32));
    }

    public static List<Offer> getOffers(ItemStack stack) {
        if (stack != null) {
            if (stack.getItem() == ModItems.BOBMAZON.get()) return standard;
            if (stack.getItem() == ModItems.BOBMAZON_HIDDEN.get()) return special;
        }
        return null;
    }

    private static ItemStack barrels(FluidTypeHBM fluid, int count) {
        ItemStack stack = ItemFluidBarrel.createForFluid(fluid);
        stack.setCount(count);
        return stack;
    }

    private static ItemStack flowerStack(BlockNTMFlower.EnumFlowerType type, int count) {
        ItemStack stack = new ItemStack(ModBlocks.PLANT_FLOWER.get().asItem(), count);
        stack.setDamageValue(type.ordinal());
        return stack;
    }

    private static ItemStack plushieStack(int ordinal, int count) {
        ItemStack stack = new ItemStack(ModBlocks.PLUSHIE.get().asItem(), count);
        stack.setDamageValue(ordinal);
        return stack;
    }

    private static ItemStack snowglobeStack(int ordinal, int count) {
        ItemStack stack = new ItemStack(ModBlocks.SNOWGLOBE.get().asItem(), count);
        stack.setDamageValue(ordinal);
        return stack;
    }

    private static ItemStack blueprintFolder(int meta, int count) {
        ItemStack stack = new ItemStack(ModItems.BLUEPRINT_FOLDER.get(), count);
        stack.setDamageValue(meta);
        return stack;
    }

    private static ItemStack conserveStack(int meta, int count) {
        ItemStack stack = new ItemStack(ModItems.CANNED_CONSERVE.get(), count);
        stack.setDamageValue(meta);
        return stack;
    }
}