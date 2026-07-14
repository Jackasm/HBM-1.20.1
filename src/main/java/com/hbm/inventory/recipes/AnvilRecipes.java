package com.hbm.inventory.recipes;

import com.hbm.config.GeneralConfig;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.fluid.ItemFluidCanister;
import com.hbm.items.fluid.ItemFluidID;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;


public class AnvilRecipes{

    public static List<AnvilSmithingRecipe> smithingRecipes = new ArrayList<>();
    public static List<AnvilConstructionRecipe> constructionRecipes = new ArrayList<>();
    public static List<AnvilConstructionRecipe> fluidRecipes = new ArrayList<>();

    public static void register() {
        registerSmithing();
        registerConstruction();
        initFluidRecipes();
    }

    public static List<Object> getAllRecipes() {
        List<Object> all = new ArrayList<>();
        all.addAll(smithingRecipes);
        all.addAll(constructionRecipes);
        all.addAll(fluidRecipes);
        return all;
    }

    public static void initFluidRecipes() {
        if (!fluidRecipes.isEmpty()) return;
        for (FluidTypeHBM fluid : Fluids.getAllFluids()) {
            ItemStack output = ItemFluidID.createForFluid(fluid);
            if (output.isEmpty()) continue;

            // Вход 1: железная пластина
            AStack ironPlate = new ComparableStack(new ItemStack(ModItems.PLATE_IRON.get()));
            // Вход 2: любой краситель (тег forge:dyes)
            TagKey<Item> dyeTag = ItemTags.create(ResLocation("forge", "dyes"));
            AStack anyDye = new TagStack(dyeTag);

            // Выход: идентификатор жидкости
            AnvilOutput anvilOutput = new AnvilOutput(output);
            AnvilConstructionRecipe recipe = new AnvilConstructionRecipe(new AStack[]{ironPlate, anyDye}, anvilOutput);
            recipe.setOverlay(OverlayType.NONE);
            recipe.setTier(1);
            fluidRecipes.add(recipe);
        }
    }

    public static void registerSmithing() {

        Block[] anvils = new Block[]{ModBlocks.ANVIL_IRON.get(), ModBlocks.ANVIL_LEAD.get()};

        for(Block anvil : anvils) {
            smithingRecipes.add(new AnvilSmithingRecipe(1, new ItemStack(ModBlocks.ANVIL_STEEL.get(), 1),
                    new ComparableStack(anvil), new ComparableStack(ModItems.INGOT_STEEL.get(), 10)).makeShapeless());
            smithingRecipes.add(new AnvilSmithingRecipe(1, new ItemStack(ModBlocks.ANVIL_DESH.get(), 1),
                    new ComparableStack(anvil), new ComparableStack(ModItems.INGOT_DESH.get(), 10)).makeShapeless());
            smithingRecipes.add(new AnvilSmithingRecipe(1, new ItemStack(ModBlocks.ANVIL_SATURNITE.get(), 1),
                    new ComparableStack(anvil), new ComparableStack(ModItems.INGOT_SATURNITE.get(), 10)).makeShapeless());
            smithingRecipes.add(new AnvilSmithingRecipe(1, new ItemStack(ModBlocks.ANVIL_FERROURANIUM.get(), 1),
                    new ComparableStack(anvil), new ComparableStack(ModItems.INGOT_FERROURANIUM.get(), 10)).makeShapeless());
            smithingRecipes.add(new AnvilSmithingRecipe(1, new ItemStack(ModBlocks.ANVIL_BISMUTH_BRONZE.get(), 1),
                    new ComparableStack(anvil), new ComparableStack(ModItems.INGOT_BISMUTH_BRONZE.get(), 10)).makeShapeless());
            smithingRecipes.add(new AnvilSmithingRecipe(1, new ItemStack(ModBlocks.ANVIL_ARSENIC_BRONZE.get(), 1),
                    new ComparableStack(anvil), new ComparableStack(ModItems.INGOT_ARSENIC_BRONZE.get(), 10)).makeShapeless());
            smithingRecipes.add(new AnvilSmithingRecipe(1, new ItemStack(ModBlocks.ANVIL_SCHRABIDATE.get(), 1),
                    new ComparableStack(anvil), new ComparableStack(ModItems.INGOT_SCHRABIDATE.get(), 10)).makeShapeless());
            smithingRecipes.add(new AnvilSmithingRecipe(1, new ItemStack(ModBlocks.ANVIL_DNT.get(), 1),
                    new ComparableStack(anvil), new ComparableStack(ModItems.INGOT_DNT.get(), 10)).makeShapeless());
            smithingRecipes.add(new AnvilSmithingRecipe(1, new ItemStack(ModBlocks.ANVIL_OSMIRIDIUM.get(), 1),
                    new ComparableStack(anvil), new ComparableStack(ModItems.INGOT_OSMIRIDIUM.get(), 10)).makeShapeless());
        }


    }

    public static void registerConstruction() {

        registerConstructionRecipes();
        registerConstructionAmmo();
        registerStampRecipes();
        registerConstructionRecycling();
    }


    private static void registerStampRecipes() {
        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_STONE_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_STONE_PLATE.get(), 1))
        ).setTier(1).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_STONE_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_STONE_CIRCUIT.get(), 1))
        ).setTier(1).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_STONE_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_STONE_WIRE.get(), 1))
        ).setTier(1).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_IRON_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_IRON_PLATE.get(), 1))
        ).setTier(1).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_IRON_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_IRON_CIRCUIT.get(), 1))
        ).setTier(1).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_IRON_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_IRON_WIRE.get(), 1))
        ).setTier(1).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_STEEL_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_STEEL_PLATE.get(), 1))
        ).setTier(2).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_STEEL_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_STEEL_CIRCUIT.get(), 1))
        ).setTier(2).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_STEEL_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_STEEL_WIRE.get(), 1))
        ).setTier(2).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_TITANIUM_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_TITANIUM_PLATE.get(), 1))
        ).setTier(2).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_TITANIUM_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_TITANIUM_CIRCUIT.get(), 1))
        ).setTier(2).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_TITANIUM_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_TITANIUM_WIRE.get(), 1))
        ).setTier(2).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_OBSIDIAN_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_OBSIDIAN_PLATE.get(), 1))
        ).setTier(3).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_OBSIDIAN_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_OBSIDIAN_CIRCUIT.get(), 1))
        ).setTier(3).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_OBSIDIAN_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_OBSIDIAN_WIRE.get(), 1))
        ).setTier(3).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_DESH_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_DESH_PLATE.get(), 1))
        ).setTier(3).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_DESH_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_DESH_CIRCUIT.get(), 1))
        ).setTier(3).setOverlay(OverlayType.TOOLS));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.STAMP_DESH_FLAT.get()),
                new AnvilOutput(new ItemStack(ModItems.STAMP_DESH_WIRE.get(), 1))
        ).setTier(3).setOverlay(OverlayType.TOOLS));

    }

    public static void registerConstructionRecipes() {

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.PLATE_TITANIUM.get(), 2),
                        new ComparableStack(ModItems.INGOT_STEEL.get(), 1),
                        new ComparableStack(ModItems.BOLT_STEEL.get())
                },
                new AnvilOutput(new ItemStack(ModItems.PLATE_ARMOR_TITANIUM.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.PLATE_IRON.get(), 2),
                        new ComparableStack(ModItems.COIL_COPPER.get(), 1),
                        new ComparableStack(ModItems.COIL_COPPER_TORUS.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.MOTOR.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(Blocks.STONE_BRICKS, 4),
                        new ComparableStack(ModItems.INGOT_FIREBRICK.get(), 4),
                        new ComparableStack(ModItems.PLATE_COPPER.get(), 4)
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_BLAST_FURNACE.get()))
        ).setTier(1));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(new ItemStack(Blocks.STONE_BRICKS, 8)),
                        new ComparableStack(new ItemStack(ModItems.INGOT_FIREBRICK.get(), 16)),
                        new ComparableStack(new ItemStack(Items.IRON_INGOT, 4)),
                        new ComparableStack(new ItemStack(ModItems.PLATE_COPPER.get(), 8)),
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_ROTARY_FURNACE.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(new ItemStack(ModItems.INGOT_FIREBRICK.get(), 20)),
                        new ComparableStack(new ItemStack(Items.COPPER_INGOT, 8)),
                        new ComparableStack(new ItemStack(ModItems.PLATE_STEEL.get(), 8)),
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_CRUCIBLE.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(Blocks.FURNACE),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 8),
                        new ComparableStack(Items.COPPER_INGOT, 8)
                },
                new AnvilOutput(new ItemStack(ModBlocks.HEATER_FIREBOX.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.INGOT_FIREBRICK.get(), 16),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 4),
                        new ComparableStack(Items.COPPER_INGOT, 8)
                },
                new AnvilOutput(new ItemStack(ModBlocks.HEATER_OVEN.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(Blocks.STONE, 8),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 2),
                        new ComparableStack(Items.IRON_INGOT, 4)
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_ASHPIT.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.TANK_STEEL.get(), 4),
                        new ComparableStack(ModItems.PIPE_STEEL.get(), 8),
                        new ComparableStack(ModItems.INGOT_TITANIUM.get(), 12),
                        new ComparableStack(Items.COPPER_INGOT, 8)
                },
                new AnvilOutput(new ItemStack(ModBlocks.HEATER_OILBURNER.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(Blocks.COBBLESTONE, 8),
                        new TagStack(ItemTags.PLANKS, 16),
                        new ComparableStack(ModItems.PLATE_COPPER.get(), 8),
                        new ComparableStack(ModItems.PIPE_LEAD.get(), 2),
                },
                new AnvilOutput(new ItemStack(ModBlocks.PUMP_STEAM.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(Blocks.STONE_BRICKS, 8),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 16),
                        new ComparableStack(ModItems.PIPE_LEAD.get(), 4),
                        new ComparableStack(ModItems.MOTOR.get(), 2),
                        new ComparableStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 4)
                },
                new AnvilOutput(new ItemStack(ModBlocks.PUMP_ELECTRIC.get()))
        ).setTier(3));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(Blocks.STONE_BRICKS, 16),
                        new ComparableStack(Items.IRON_INGOT, 4),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 16),
                        new ComparableStack(Items.COPPER_INGOT, 8),
                        new ComparableStack(ModBlocks.STEEL_GRATE.get(), 16)
                },
                new AnvilOutput(new ItemStack(ModBlocks.FURNACE_STEEL.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(Blocks.STONE_BRICKS, 8),
                        new TagStack(ItemTags.LOGS, 16),
                        new ComparableStack(ModItems.PLATE_COPPER.get(), 2),
                        new ComparableStack(Items.BRICK, 16)
                },
                new AnvilOutput(new ItemStack(ModItems.FURNACE_COMBINATION.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new TagStack(ItemTags.PLANKS, 16),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 6),
                        new ComparableStack(Items.COPPER_INGOT, 8),
                        new ComparableStack(Items.IRON_INGOT, 4),
                        new ComparableStack(ModItems.SAWBLADE.get())
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_SAWMILL.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.INGOT_STEEL.get(), 4),
                        new ComparableStack(ModItems.PLATE_COPPER.get(), 16),
                        new ComparableStack(ModItems.PLATE_POLYMER.get(), 8)
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_HEAT_BOILER.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 4),
                        new ComparableStack(Items.IRON_INGOT, 12),
                        new ComparableStack(Items.COPPER_INGOT, 2),
                        new ComparableStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 2),
                        new ComparableStack(ModItems.SAWBLADE.get())
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_AUTOSAW.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new TagStack(ItemTags.PLANKS, 16),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 6),
                        new ComparableStack(Items.COPPER_INGOT, 8),
                        new ComparableStack(ModItems.COIL_COPPER.get(), 4),
                        new ComparableStack(ModItems.GEAR_LARGE.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_STIRLING.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 16),
                        new ComparableStack(ModItems.INGOT_BERYLLIUM.get(), 6),
                        new ComparableStack(Items.COPPER_INGOT, 8),
                        new ComparableStack(ModItems.COIL_GOLD.get(), 16),
                        new ComparableStack(ModItems.GEAR_LARGE.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_STIRLING_STEEL.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModBlocks.REINFORCED_STONE.get(), 16),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 12),
                        new ComparableStack(ModItems.SHELL_STEEL.get(), 2),
                        new ComparableStack(ModItems.COIL_COPPER.get(), 4),
                        new ComparableStack(ModItems.GEAR_LARGE.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_STEAM_ENGINE.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.PLATE_CAST_STEEL.get(), 2),
                        new ComparableStack(ModItems.COIL_COPPER.get(), 4),
                        new ComparableStack(ModItems.BOLT_TUNGSTEN.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_VACUUM_TUBE.get(), 2)
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_SOLDERING_STATION.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.PLATE_CAST_STEEL.get(), 4),
                        new ComparableStack(ModItems.INGOT_TUNGSTEN.get(), 8),
                        new ComparableStack(ModBlocks.MACHINE_TRANSFORMER.get(), 1),
                        new TagStack(ModItemTags.ANY_ELECTRODE, 2)
                },
                new AnvilOutput(new ItemStack(ModBlocks.MACHINE_ARC_WELDER.get()))
        ).setTier(2));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.INGOT_ALUMINIUM.get()),
                new AnvilOutput(new ItemStack(ModBlocks.DECO_ALUMINIUM.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.INGOT_BERYLLIUM.get()),
                new AnvilOutput(new ItemStack(ModBlocks.DECO_BERYLLIUM.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.INGOT_LEAD.get()),
                new AnvilOutput(new ItemStack(ModBlocks.DECO_LEAD.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.INGOT_RED_COPPER.get()),
                new AnvilOutput(new ItemStack(ModBlocks.DECO_RED_COPPER.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.INGOT_STEEL.get()),
                new AnvilOutput(new ItemStack(ModBlocks.DECO_STEEL.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.INGOT_TITANIUM.get()),
                new AnvilOutput(new ItemStack(ModBlocks.DECO_TITANIUM.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.INGOT_TUNGSTEN.get()),
                new AnvilOutput(new ItemStack(ModBlocks.DECO_TUNGSTEN.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.INGOT_STAINLESS.get()),
                new AnvilOutput(new ItemStack(ModBlocks.DECO_STAINLESS.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));


        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.PLATE_IRON.get(), 1),
                new AnvilOutput(new ItemStack(ModBlocks.FLUID_DUCT_BOX_SILVER.get(), 1))
        ).setTier(2).setOverlay(OverlayType.CONSTRUCTION));
        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.PLATE_IRON.get(), 1),
                new AnvilOutput(new ItemStack(ModBlocks.FLUID_DUCT_BOX_COPPER.get(), 1 ))
        ).setTier(2).setOverlay(OverlayType.CONSTRUCTION));
        constructionRecipes.add(new AnvilConstructionRecipe(
                AStack.of(ModItems.PLATE_IRON.get(), 1),
                new AnvilOutput(new ItemStack(ModBlocks.FLUID_DUCT_BOX_WHITE.get(), 1 ))
        ).setTier(2).setOverlay(OverlayType.CONSTRUCTION));
        constructionRecipes.add(new AnvilConstructionRecipe(new AStack[] {
                AStack.of(ModItems.PLATE_IRON.get(), 1),
                new ComparableStack(ModItems.PLATE_POLYMER.get())},
                new AnvilOutput(new ItemStack(ModBlocks.FLUID_DUCT_EXHAUST.get(), 8)))
                .setTier(2).setOverlay(OverlayType.CONSTRUCTION));

    }


    public static void registerConstructionAmmo() {
        // Grenade Generic (4 шт) - из материалов
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new TagStack(ModItemTags.INGOTS_IRON, 2),
                        new TagStack(ModItemTags.WIRE_FINE, 1),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 1),
                        new ComparableStack(Blocks.TNT, 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_GENERIC.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Strong (2 шт) - улучшенная из обычной
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_GENERIC.get(), 1),
                        new ComparableStack(Items.GUNPOWDER, 4)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_STRONG.get(), 2))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Frag (2 шт) - осколочная
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_GENERIC.get(), 1),
                        new TagStack(ItemTags.PLANKS, 8),
                        new ComparableStack(Blocks.GRAVEL, 4)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_FRAG.get(), 2))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Fire (2 шт) - зажигательная
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_GENERIC.get(), 8),
                        new ComparableStack(ItemFluidCanister.createForFluid(Fluids.NAPALM.get()), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_FIRE.get(), 8))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Gascan (8 шт) - из канистр с топливом
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ItemFluidCanister.createForFluid(Fluids.NAPALM.get()), 1),
                        new ComparableStack(Items.FLINT, 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_GASCAN.get(), 8))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Aschrab (1 шт) - анти-шрабидиевая
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new TagStack(ModItemTags.GLASS, 2),
                        new TagStack(ModItemTags.WIRE_FINE, 1),
                        new ComparableStack(ModItems.PLATE_STEEL.get(), 2),
                        new ComparableStack(ModItems.CELL_ANTI_SCHRABIDIUM.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_ASCHRAB.get(), 1))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade MK2 (2 шт) - улучшенная из сильной
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_STRONG.get(), 1),
                        new ComparableStack(Items.GUNPOWDER, 6)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_MK2.get(), 2))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));



        // Grenade MIRV (1 шт) - кассетная
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_SMART.get(), 8),
                        new ComparableStack(ModItems.GRENADE_GENERIC.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_MIRV.get(), 1))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Burst (1 шт) - взрывная кассетная
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_BREACH.get(), 8),
                        new ComparableStack(ModItems.GRENADE_GENERIC.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_BURST.get(), 1))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

                // Grenade Poison (2 шт) - ядовитая
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_GENERIC.get(), 1),
                        new ComparableStack(ModItems.POWDER_POISON.get(), 4)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_POISON.get(), 2))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Gas (2 шт) - газовая
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_GENERIC.get(), 1),
                        new ComparableStack(ModItems.PELLET_GAS.get(), 4)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_GAS.get(), 2))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Lemon (1 шт) - лимонная
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.LEMON.get(), 1),
                        new ComparableStack(ModItems.GRENADE_STRONG.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_LEMON.get(), 1))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Smart (4 шт) - умная
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_STRONG.get(), 4),
                        new ComparableStack(ModItems.CIRCUIT_CHIP.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_SMART.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Breach (1 шт) - пробивная
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.GRENADE_SMART.get(), 2),
                        new ComparableStack(ModItems.PLATE_SATURNITE.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_BREACH.get(), 1))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Cloud (1 шт) - облачная
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.SULFUR.get(), 4),
                        new ComparableStack(ModItems.POWDER_POISON.get(), 4),
                        new ComparableStack(ModItems.POWDER_COPPER.get(), 4),
                        new ComparableStack(ItemFluidCanister.createForFluid(Fluids.PEROXIDE.get()).getItem(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_CLOUD.get(), 1))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Pink Cloud (1 шт) - розовое облако
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.POWDER_SPARK_MIX.get(), 2),
                        new ComparableStack(ModItems.POWDER_MAGIC.get(), 4),
                        new ComparableStack(ModItems.GRENADE_CLOUD.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_PINK_CLOUD.get(), 1))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Nuclear Waste Pearl (1 шт) - жемчужина радиоактивных отходов
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ModItems.NUCLEAR_WASTE_TINY.get(), 8),
                        new ComparableStack(ModBlocks.BLOCK_FALLOUT.get().asItem(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.NUCLEAR_WASTE_PEARL.get(), 1))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        // Grenade Kyiv (1 шт) - коктейль Молотова
        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new ComparableStack(ItemFluidCanister.createForFluid(Fluids.NAPALM.get()).getItem(), 1),
                        new ComparableStack(ModItems.BOTTLE2_EMPTY.get(), 1),
                        new ComparableStack(ModItems.RAG.get(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.GRENADE_KYIV.get(), 1))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new AStack[] {
                        new TagStack(ModItemTags.ANY_HARDPLASTIC_INGOT, 4),
                        new ComparableStack(ModBlocks.GLASS_BORON.get().asItem(), 1)
                },
                new AnvilOutput(new ItemStack(ModItems.DISPERSER_CANISTER.get(), 4))
        ).setTier(1).setOverlay(OverlayType.CONSTRUCTION));


    }


    public static void registerConstructionRecycling() {
        // Рециклинг
        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.DECO_TITANIUM.get().asItem(), 4),
                new AnvilOutput[] {new AnvilOutput(new ItemStack(ModItems.INGOT_TITANIUM.get(), 1))}
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.DECO_RED_COPPER.get().asItem(), 4),
                new AnvilOutput[] {new AnvilOutput(new ItemStack(ModItems.INGOT_RED_COPPER.get(), 1))}
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.DECO_TUNGSTEN.get().asItem(), 4),
                new AnvilOutput[] {new AnvilOutput(new ItemStack(ModItems.INGOT_TUNGSTEN.get(), 1))}
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.DECO_ALUMINIUM.get().asItem(), 4),
                new AnvilOutput[] {new AnvilOutput(new ItemStack(ModItems.INGOT_ALUMINIUM.get(), 1))}
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.DECO_STEEL.get().asItem(), 4),
                new AnvilOutput[] {new AnvilOutput(new ItemStack(ModItems.INGOT_STEEL.get(), 1))}
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.DECO_LEAD.get().asItem(), 4),
                new AnvilOutput[] {new AnvilOutput(new ItemStack(ModItems.INGOT_LEAD.get(), 1))}
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.DECO_BERYLLIUM.get().asItem(), 4),
                new AnvilOutput[] {new AnvilOutput(new ItemStack(ModItems.INGOT_BERYLLIUM.get(), 1))}
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.DECO_STAINLESS.get().asItem(), 4),
                new AnvilOutput[] {new AnvilOutput(new ItemStack(ModItems.INGOT_STAINLESS.get(), 1))}
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.DECO_RUSTY_STEEL.get().asItem(), 4),
                new AnvilOutput[] {new AnvilOutput(new ItemStack(ModItems.INGOT_STEEL.get(), 1))}
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModItems.FILING_CABINET_ITEM.get()),
                new AnvilOutput[] {
                        new AnvilOutput(new ItemStack(ModItems.PLATE_STEEL.get(), 2)),
                        new AnvilOutput(new ItemStack(ModItems.PLATE_STEEL.get(), 2), 0.5F),
                        new AnvilOutput(new ItemStack(ModItems.PLATE_POLYMER.get(), 2), 0.25F),
                        new AnvilOutput(new ItemStack(ModItems.SCRAP.get(), 1))

                }
        ).setTier(1));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModItems.EGG_GLYPHID.get()),
                new AnvilOutput[] {
                        new AnvilOutput(new ItemStack(ModItems.GLYPHID_MEAT.get(), 2)),
                        new AnvilOutput(new ItemStack(ModItems.GLYPHID_MEAT.get(), 1), 0.5F),
                        new AnvilOutput(new ItemStack(Items.BONE, 1), 0.75F),
                        new AnvilOutput(new ItemStack(Items.EXPERIENCE_BOTTLE, 1), 0.5F)
                }
        ).setTier(1).setOverlay(OverlayType.RECYCLING));

        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.FLUID_DUCT_BOX_SILVER.get(), 1),
                new AnvilOutput(new ItemStack(ModItems.PLATE_IRON.get()))
        ).setTier(2).setOverlay(OverlayType.RECYCLING));
        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.FLUID_DUCT_BOX_COPPER.get(), 1),
                new AnvilOutput(new ItemStack(ModItems.PLATE_COPPER.get()))
        ).setTier(2).setOverlay(OverlayType.RECYCLING));
        constructionRecipes.add(new AnvilConstructionRecipe(
                new ComparableStack(ModBlocks.FLUID_DUCT_BOX_WHITE.get(), 1),
                new AnvilOutput(new ItemStack(ModItems.PLATE_ALUMINIUM.get()))
        ).setTier(2).setOverlay(OverlayType.RECYCLING));
        constructionRecipes.add(new AnvilConstructionRecipe(new ComparableStack(ModBlocks.FLUID_DUCT_EXHAUST.get(), 8),
                new AnvilOutput[] {
                        new AnvilOutput(new ItemStack(ModItems.PLATE_IRON.get())),
                        new AnvilOutput(new ItemStack(ModItems.PLATE_POLYMER.get()))
        }).setTier(2));
    }

    public static List<AnvilSmithingRecipe> getSmithing() {
        return smithingRecipes;
    }

    public static List<AnvilConstructionRecipe> getConstruction() {
        List<AnvilConstructionRecipe> all = new ArrayList<>();
        all.addAll(constructionRecipes);
        all.addAll(fluidRecipes);
        return all;
    }
    public static void updateSmithingTiersFromConfig() {
        for(AnvilSmithingRecipe recipe : smithingRecipes) {
            recipe.updateTierFromConfig();
        }
    }
    // ========== ВНУТРЕННИЕ КЛАССЫ ==========

    public static class AnvilConstructionRecipe {
        public List<AStack> input = new ArrayList<>();
        public List<AnvilOutput> output = new ArrayList<>();
        public int tierLower = 0;
        public int tierUpper = -1;
        OverlayType overlay = OverlayType.NONE;

        public AnvilConstructionRecipe(AStack input, AnvilOutput output) {
            this.input.add(input);
            this.output.add(output);
            this.setOverlay(OverlayType.SMITHING);
        }

        public AnvilConstructionRecipe(AStack[] input, AnvilOutput output) {
            this.input.addAll(Arrays.asList(input));
            this.output.add(output);
            this.setOverlay(OverlayType.CONSTRUCTION);
        }

        public AnvilConstructionRecipe(AStack input, AnvilOutput[] output) {
            this.input.add(input);
            this.output.addAll(Arrays.asList(output));
            this.setOverlay(OverlayType.RECYCLING);
        }


        public AnvilConstructionRecipe setTier(int tier) {
            this.tierLower = tier;
            if(GeneralConfig.ENABLE_LBSM &&
                    GeneralConfig.enableLBSMUnlockAnvil != null && GeneralConfig.ENABLE_LBSM_UNLOCK_ANVIL) {
                this.tierLower = 1;
            }
            return this;
        }

        public AnvilConstructionRecipe setTierRange(int lower, int upper) {
            this.tierLower = lower;
            this.tierUpper = upper;
            if(GeneralConfig.ENABLE_LBSM &&
                    GeneralConfig.enableLBSMUnlockAnvil != null && GeneralConfig.ENABLE_LBSM_UNLOCK_ANVIL) {
                this.tierLower = this.tierUpper = 1;
            }
            return this;
        }

        public boolean isTierValid(int tier) {
            if(this.tierUpper == -1)
                return tier >= this.tierLower;
            return tier >= this.tierLower && tier <= this.tierUpper;
        }

        public AnvilConstructionRecipe setOverlay(OverlayType overlay) {
            this.overlay = overlay;
            return this;
        }

        public OverlayType getOverlay() {
            return this.overlay;
        }

        public ItemStack getDisplay() {
            switch(this.overlay) {
                case NONE:
                case CONSTRUCTION:
                case SMITHING:
                    if(!this.output.isEmpty()) {
                        return this.output.get(0).stack.copy();
                    }
                    break;
                case RECYCLING:
                    for(AStack stack : this.input) {
                        if(stack instanceof ComparableStack)
                            return ((ComparableStack)stack).toStack();
                    }
                    break;
            }

            if(!this.output.isEmpty()) {
                return this.output.get(0).stack.copy();
            }
            return new ItemStack(Items.IRON_PICKAXE);
        }
    }

    public static class AnvilOutput {
        public ItemStack stack;
        public float chance;

        public AnvilOutput(ItemStack stack) {
            this(stack, 1F);
        }

        public AnvilOutput(ItemStack stack, float chance) {
            this.stack = stack;
            this.chance = chance;
        }

        public ItemStack getStack() {
            return stack;
        }

        public float getChance() {
            return chance;
        }
    }

    public enum OverlayType {
        NONE,
        CONSTRUCTION,
        RECYCLING,
        SMITHING,
        TOOLS
    }

    public static class AnvilSmithingRecipe {
        public int tier;
        protected ItemStack output;
        protected AStack left;
        protected AStack right;
        protected boolean shapeless = false;

        public AnvilSmithingRecipe(int tier, ItemStack out, AStack left, AStack right) {
            this.tier = tier;
            this.output = out;
            this.left = left;
            this.right = right;
            if(GeneralConfig.ENABLE_LBSM &&
                    GeneralConfig.enableLBSMUnlockAnvil != null && GeneralConfig.ENABLE_LBSM_UNLOCK_ANVIL) {
                this.tier = 1;
            }
        }

        public void updateTierFromConfig() {
            if(GeneralConfig.ENABLE_LBSM &&
                    GeneralConfig.enableLBSMUnlockAnvil != null && GeneralConfig.ENABLE_LBSM_UNLOCK_ANVIL) {
                this.tier = 1;
            }
        }

        public AnvilSmithingRecipe makeShapeless() {
            this.shapeless = true;
            return this;
        }

        public boolean matches(ItemStack left, ItemStack right) {
            return matchesInt(left, right) != -1;
        }

        public int matchesInt(ItemStack left, ItemStack right) {
            if(doesStackMatch(left, this.left) && doesStackMatch(right, this.right))
                return 0;

            if(shapeless) {
                return doesStackMatch(right, this.left) && doesStackMatch(left, this.right) ? 1 : -1;
            }

            return -1;
        }

        public boolean doesStackMatch(ItemStack input, AStack recipe) {
            return recipe.matchesRecipe(input, false);
        }

        public List<ItemStack> getLeft() {
            return left.extractForNEI();
        }

        public List<ItemStack> getRight() {
            return right.extractForNEI();
        }

        public ItemStack getSimpleOutput() {
            return output.copy();
        }

        public int amountConsumed(int index, boolean mirrored) {
            if(index == 0)
                return mirrored ? right.getStackSize() : left.getStackSize();
            if(index == 1)
                return mirrored ? left.getStackSize() : right.getStackSize();

            return 0;
        }

        public int getTier() {
            return tier;
        }

    }


}