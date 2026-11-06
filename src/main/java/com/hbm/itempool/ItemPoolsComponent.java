package com.hbm.itempool;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;
import com.hbm.items.fluid.ItemFluidCanister;
import com.hbm.items.tool.ItemBlowtorch;
import net.minecraft.world.item.Items;

import static com.hbm.itempool.ItemPoolHelper.weighted;

public class ItemPoolsComponent {

    public static final String POOL_MACHINE_PARTS = "POOL_MACHINE_PARTS";
    public static final String POOL_NUKE_FUEL = "POOL_NUKE_FUEL";
    public static final String POOL_SILO = "POOL_SILO";
    public static final String POOL_OFFICE_TRASH = "POOL_OFFICE_TRASH";
    public static final String POOL_FILING_CABINET = "POOL_FILING_CABINET";
    public static final String POOL_SOLID_FUEL = "POOL_SOLID_FUEL";
    public static final String POOL_VAULT_LAB = "POOL_VAULT_LAB";
    public static final String POOL_VAULT_LOCKERS = "POOL_VAULT_LOCKERS";
    public static final String POOL_METEOR_SAFE = "POOL_METEOR_SAFE";
    public static final String POOL_OIL_RIG = "POOL_OIL_RIG";
    public static final String POOL_RTG = "POOL_RTG";
    public static final String POOL_REPAIR_MATERIALS = "POOL_REPAIR_MATERIALS";

    public static void init() {

        // machine parts
        new ItemPool(POOL_MACHINE_PARTS)
                .add(weighted(ModItems.PLATE_STEEL.get(), 1, 5, 5))
                .add(weighted(ModItems.SHELL_STEEL.get(), 1, 3, 3))
                .add(weighted(ModItems.PLATE_POLYMER.get(), 1, 6, 5))
                .add(weighted(ModItems.BOLT_STEEL.get(), 4, 16, 3))
                .add(weighted(ModItems.BOLT_TUNGSTEN.get(), 4, 16, 3))
                .add(weighted(ModItems.COIL_TUNGSTEN.get(), 1, 2, 5))
                .add(weighted(ModItems.MOTOR.get(), 1, 2, 4))
                .add(weighted(ModItems.TANK_STEEL.get(), 1, 2, 3))
                .add(weighted(ModItems.COIL_COPPER.get(), 1, 3, 4))
                .add(weighted(ModItems.COIL_COPPER_TORUS.get(), 1, 2, 3))
                .add(weighted(ModItems.WIRE_FINE_RED_COPPER.get(), 1, 8, 5))
                .add(weighted(ModItems.PISTON_SELENIUM.get(), 1, 1, 3))
                .add(weighted(ModItems.BATTERY_ADVANCED_CELL.get(), 1, 1, 3))
                .add(weighted(ModItems.CIRCUIT_VACUUM_TUBE.get(), 1, 2, 4))
                .add(weighted(ModItems.CIRCUIT_PCB.get(), 1, 3, 5))
                .add(weighted(ModItems.CIRCUIT_CAPACITOR.get(), 1, 1, 3))
                .add(weighted(ModItems.BLADE_TITANIUM.get(), 1, 8, 1))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 0, 1, 1, 1));

        // fuel isotopes
        new ItemPool(POOL_NUKE_FUEL)
                .add(weighted(ModItems.BILLET_URANIUM.get(), 1, 4, 4))
                .add(weighted(ModItems.BILLET_TH232.get(), 1, 3, 3))
                .add(weighted(ModItems.BILLET_URANIUM_FUEL.get(), 1, 3, 5))
                .add(weighted(ModItems.BILLET_MOX_FUEL.get(), 1, 3, 5))
                .add(weighted(ModItems.BILLET_THORIUM_FUEL.get(), 1, 3, 3))
                .add(weighted(ModItems.BILLET_RA226BE.get(), 1, 2, 2))
                .add(weighted(ModItems.BILLET_BERYLLIUM.get(), 1, 1, 1))
                .add(weighted(ModItems.NUGGET_U233.get(), 1, 1, 1))
                .add(weighted(ModItems.NUGGET_URANIUM_FUEL.get(), 1, 1, 1))
                .add(weighted(ModItems.ROD_ZIRNOX_EMPTY.get(), 1, 3, 3))
                .add(weighted(ModItems.INGOT_GRAPHITE.get(), 1, 4, 3))
                .add(weighted(ModItems.PILE_ROD_URANIUM.get(), 2, 5, 3))
                .add(weighted(ModItems.PILE_ROD_SOURCE.get(), 1, 2, 2))
                .add(weighted(ModItems.REACHER.get(), 1, 1, 3))
                .add(weighted(ModItems.SCREWDRIVER.get(), 1, 1, 2));

        // missile parts in silos
        new ItemPool(POOL_SILO)
                .add(weighted(ModItems.MISSILE_GENERIC.get(), 1, 1, 4))
                .add(weighted(ModItems.MISSILE_INCENDIARY.get(), 1, 1, 4))
                .add(weighted(ModArmorItems.GAS_MASK_M65.get(), 1, 1, 5))
                .add(weighted(ModItems.BATTERY_ADVANCED.get(), 1, 1, 5))
                .add(weighted(ModItems.DESIGNATOR.get(), 1, 1, 5))
                .add(weighted(ModItems.THRUSTER_SMALL.get(), 1, 1, 5))
                .add(weighted(ModItems.THRUSTER_MEDIUM.get(), 1, 1, 4))
                .add(weighted(ModItems.FUEL_TANK_SMALL.get(), 1, 1, 5))
                .add(weighted(ModItems.FUEL_TANK_MEDIUM.get(), 1, 1, 4))
                .add(weighted(ModItems.BOMB_CALLER_CARPET.get(), 1, 1, 1))
                .add(weighted(ModItems.BOMB_CALLER_ORANGE.get(), 1, 1, 1))
                .add(weighted(ModItems.BOTTLE_NUKA.get(), 1, 3, 10));

        // office trash
        new ItemPool(POOL_OFFICE_TRASH)
                .add(weighted(Items.PAPER, 1, 12, 10))
                .add(weighted(Items.BOOK, 1, 3, 4))
                .add(weighted(ModItems.TWINKIE.get(), 1, 2, 6))
                .add(weighted(ModItems.COFFEE.get(), 1, 1, 4))
                .add(weighted(ModItems.FLAME_POLITICS.get(), 1, 1, 2))
                .add(weighted(ModItems.RING_PULL.get(), 1, 1, 4))
                .add(weighted(ModItems.CAN_EMPTY.get(), 1, 1, 2))
                .add(weighted(ModItems.CAN_CREATURE.get(), 1, 2, 2))
                .add(weighted(ModItems.CAN_SMART.get(), 1, 3, 2))
                .add(weighted(ModItems.CAN_MRSUGAR.get(), 1, 2, 2))
                .add(weighted(ModItems.CAP_NUKA.get(), 1, 16, 2))
                .add(weighted(ModItems.BOOK_GUIDE.get(), 1, 1, 1))               // meta 3
                .add(weighted(ModBlocks.DECO_COMPUTER.get().asItem(), 1, 1, 1))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 0, 1, 1, 1));

        // filing cabinet
        new ItemPool(POOL_FILING_CABINET)
                .add(weighted(Items.PAPER, 1, 12, 240))
                .add(weighted(Items.BOOK, 1, 3, 90))
                .add(weighted(Items.MAP, 1, 1, 50))
                .add(weighted(Items.WRITABLE_BOOK, 1, 1, 30))
                .add(weighted(ModItems.CIGARETTE.get(), 1, 16, 20))
                .add(weighted(ModItems.TOOTHPICKS.get(), 1, 16, 10))
                .add(weighted(ModItems.DUST.get(), 1, 1, 40))
                .add(weighted(ModItems.DUST_TINY.get(), 1, 3, 75))
                .add(weighted(ModItems.INK.get(), 1, 1, 1))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 5));

        // solid fuel
        new ItemPool(POOL_SOLID_FUEL)
                .add(weighted(ModItems.SOLID_FUEL.get(), 1, 5, 1))
                .add(weighted(ModItems.SOLID_FUEL_PRESTO.get(), 1, 2, 2))
                .add(weighted(ModItems.BALL_DYNAMITE.get(), 1, 4, 2))
                .add(weighted(ModItems.COKE_PETROLEUM.get(), 1, 3, 1))
                .add(weighted(Items.REDSTONE, 1, 3, 1))
                .add(weighted(ModItems.NITER.get(), 1, 3, 1));

        // vault lab
        new ItemPool(POOL_VAULT_LAB)
                .add(weighted(ItemBlowtorch.getEmptyTool(ModItems.BLOWTORCH.get()), 1, 1, 4))
                .add(weighted(ModItems.CHEMISTRY_SET.get(), 1, 1, 15))
                .add(weighted(ModItems.SCREWDRIVER.get(), 1, 1, 10))
                .add(weighted(ModItems.NUGGET_MERCURY.get(), 1, 1, 3))
                .add(weighted(ModItems.MORNING_GLORY.get(), 1, 1, 1))
                .add(weighted(ModItems.FILTER_COAL.get(), 1, 1, 5))
                .add(weighted(ModItems.DUST.get(), 1, 3, 25))
                .add(weighted(Items.PAPER, 1, 2, 15))
                .add(weighted(ModItems.CELL_EMPTY.get(), 1, 1, 5))
                .add(weighted(Items.GLASS_BOTTLE, 1, 1, 5))
                .add(weighted(ModItems.POWDER_IODINE.get(), 1, 1, 1))
                .add(weighted(ModItems.POWDER_BROMINE.get(), 1, 1, 1))
                .add(weighted(ModItems.POWDER_COBALT.get(), 1, 1, 1))
                .add(weighted(ModItems.POWDER_NEODYMIUM.get(), 1, 1, 1))
                .add(weighted(ModItems.POWDER_BORON.get(), 1, 1, 1))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1, 1));     // meta 1

        // vault lockers
        new ItemPool(POOL_VAULT_LOCKERS)
                .add(weighted(ModArmorItems.ROBES_HELMET.get(), 1, 1, 1))
                .add(weighted(ModArmorItems.ROBES_CHESTPLATE.get(), 1, 1, 1))
                .add(weighted(ModArmorItems.ROBES_LEGGINGS.get(), 1, 1, 1))
                .add(weighted(ModArmorItems.ROBES_BOOTS.get(), 1, 1, 1))
                .add(weighted(ModArmorItems.JACKET.get(), 1, 1, 1))
                .add(weighted(ModArmorItems.JACKET_2.get(), 1, 1, 1))
                .add(weighted(ModArmorItems.GAS_MASK_M65.get(), 1, 1, 2))
                .add(weighted(ModArmorItems.GAS_MASK_MONO.get(), 1, 1, 2))
                .add(weighted(ModArmorItems.GOGGLES.get(), 1, 1, 2))
                .add(weighted(ModItems.GAS_MASK_FILTER.get(), 1, 1, 4))
                .add(weighted(ModItems.FLAME_OPINION.get(), 1, 3, 5))
                .add(weighted(ModItems.FLAME_CONSPIRACY.get(), 1, 3, 5))
                .add(weighted(ModItems.FLAME_POLITICS.get(), 1, 3, 5))
                .add(weighted(ModItems.DEFINITELYFOOD.get(), 2, 7, 5))
                .add(weighted(ModItems.CIGARETTE.get(), 1, 8, 5))
                .add(weighted(ModItems.ARMOR_POLISH.get(), 1, 1, 3))
                .add(weighted(ModItems.GUN_KIT_1.get(), 1, 1, 3))
                .add(weighted(ModItems.RAG.get(), 1, 3, 5))
                .add(weighted(Items.PAPER, 1, 6, 7))
                .add(weighted(Items.CLOCK, 1, 1, 3))
                .add(weighted(Items.BOOK, 1, 5, 10))
                .add(weighted(Items.EXPERIENCE_BOTTLE, 1, 3, 1))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 0, 1, 1, 1))
                .add(weighted(ModItems.BLUEPRINT_FOLDER.get(), 1, 1, 1, 1));

        // meteor safe
        new ItemPool(POOL_METEOR_SAFE)
                .add(weighted(ModItems.BOOK_OF_.get(), 1, 1, 1))
                .add(weighted(ModItems.STAMP_BOOK.get(), 0, 1, 1, 1))
                .add(weighted(ModItems.STAMP_BOOK.get(), 1, 1, 1, 1))
                .add(weighted(ModItems.STAMP_BOOK.get(), 2, 1, 1, 1))
                .add(weighted(ModItems.STAMP_BOOK.get(), 3, 1, 1, 1))
                .add(weighted(ModItems.STAMP_BOOK.get(), 4, 1, 1, 1))
                .add(weighted(ModItems.STAMP_BOOK.get(), 5, 1, 1, 1))
                .add(weighted(ModItems.STAMP_BOOK.get(), 6, 1, 1, 1))
                .add(weighted(ModItems.STAMP_BOOK.get(), 7, 1, 1, 1));

        // oil rig
        new ItemPool(POOL_OIL_RIG)
                .add(weighted(ModItems.OIL_DETECTOR.get(), 1, 1, 1))
                .add(weighted(ItemFluidCanister.createForFluid(Fluids.OIL.get()), 1, 4, 5))
                .add(weighted(ModBlocks.MACHINE_FRACTION_TOWER.get().asItem(), 0, 1, 1))
                .add(weighted(ModBlocks.FRACTION_SPACER.get().asItem(), 0, 1, 1))
                .add(weighted(ModItems.CIRCUIT_ANALOG.get(), 1, 4, 1))
                .add(weighted(ModItems.CIRCUIT_CAPACITOR.get(), 1, 1, 3));

        // RTG
        new ItemPool(POOL_RTG)
                .add(weighted(ModItems.PELLET_RTG_DEPLETED_LEAD.get(), 1, 1, 40))
                .add(weighted(ModItems.PELLET_RTG_WEAK.get(), 0, 1, 1));

        // repair materials
        new ItemPool(POOL_REPAIR_MATERIALS)
                .add(weighted(ModItems.INGOT_ALUMINIUM.get(), 2, 8, 3))
                .add(weighted(ModItems.INGOT_STEEL.get(), 0, 12, 4))
                .add(weighted(ModItems.PLATE_ALUMINIUM.get(), 5, 12, 3))
                .add(weighted(ModItems.PLATE_IRON.get(), 6, 16, 3))
                .add(weighted(ModItems.PLATE_STEEL.get(), 2, 12, 2))
                .add(weighted(ModItems.INGOT_TUNGSTEN.get(), 0, 2, 1))
                .add(weighted(ModBlocks.DECO_ALUMINIUM.get().asItem(), 12, 24, 4))
                .add(weighted(ModBlocks.DECO_STEEL.get().asItem(), 5, 12, 2))
                .add(weighted(ModBlocks.BLOCK_ALUMINIUM.get().asItem(), 0, 2, 1))
                .add(weighted(ModBlocks.BLOCK_STEEL.get().asItem(), 0, 1, 1))
                .add(weighted(ModItems.BOLT_STEEL.get(), 4, 16, 3))
                .add(weighted(ModItems.CIRCUIT_VACUUM_TUBE.get(), 1, 2, 4))
                .add(weighted(ModItems.CIRCUIT_ANALOG.get(), 1, 3, 5))
                .add(weighted(ModItems.CIRCUIT_CAPACITOR.get(), 1, 1, 3));
    }
}