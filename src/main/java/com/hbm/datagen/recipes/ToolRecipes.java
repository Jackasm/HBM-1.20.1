package com.hbm.datagen.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

import static com.hbm.util.RefStrings.MODID;

public class ToolRecipes extends ModRecipeProvider {

    private static Consumer<FinishedRecipe> writer;

    public static final String[] patternSword = new String[] {"X", "X", "#"};
    public static final String[] patternPick = new String[] {"XXX", " # ", " # "};
    public static final String[] patternAxe = new String[] {"XX", "X#", " #"};
    public static final String[] patternShovel = new String[] {"X", "#", "#"};
    public static final String[] patternHoe = new String[] {"XX", " #", " #"};

    public ToolRecipes(PackOutput pOutput) {super(pOutput);}

    public static void generateToolRecipes(Consumer<FinishedRecipe> pWriter){

        writer = pWriter;

        addSword(   ModItems.INGOT_STEEL.get(), ModToolItems.STEEL_SWORD.get());
        addPickaxe( ModItems.INGOT_STEEL.get(), ModToolItems.STEEL_PICKAXE.get());
        addAxe(     ModItems.INGOT_STEEL.get(), ModToolItems.STEEL_AXE.get());
        addShovel(  ModItems.INGOT_STEEL.get(), ModToolItems.STEEL_SHOVEL.get());
        addHoe(     ModItems.INGOT_STEEL.get(), ModToolItems.STEEL_HOE.get());

        addSword(   ModItems.INGOT_TITANIUM.get(), ModToolItems.TITANIUM_SWORD.get());
        addPickaxe( ModItems.INGOT_TITANIUM.get(), ModToolItems.TITANIUM_PICKAXE.get());
        addAxe(     ModItems.INGOT_TITANIUM.get(), ModToolItems.TITANIUM_AXE.get());
        addShovel(  ModItems.INGOT_TITANIUM.get(), ModToolItems.TITANIUM_SHOVEL.get());
        addHoe(     ModItems.INGOT_TITANIUM.get(), ModToolItems.TITANIUM_HOE.get());

        addSword(   ModItems.INGOT_COBALT.get(), ModToolItems.COBALT_SWORD.get());
        addPickaxe( ModItems.INGOT_COBALT.get(), ModToolItems.COBALT_PICKAXE.get());
        addAxe(     ModItems.INGOT_COBALT.get(), ModToolItems.COBALT_AXE.get());
        addShovel(  ModItems.INGOT_COBALT.get(), ModToolItems.COBALT_SHOVEL.get());
        addHoe(     ModItems.INGOT_COBALT.get(), ModToolItems.COBALT_HOE.get());

        addSword(   ModItems.INGOT_ADVANCED_ALLOY.get(), ModToolItems.ADVANCED_ALLOY_SWORD.get());
        addPickaxe( ModItems.INGOT_ADVANCED_ALLOY.get(), ModToolItems.ADVANCED_ALLOY_PICKAXE.get());
        addAxe(     ModItems.INGOT_ADVANCED_ALLOY.get(), ModToolItems.ADVANCED_ALLOY_AXE.get());
        addShovel(  ModItems.INGOT_ADVANCED_ALLOY.get(), ModToolItems.ADVANCED_ALLOY_SHOVEL.get());
        addHoe(     ModItems.INGOT_ADVANCED_ALLOY.get(), ModToolItems.ADVANCED_ALLOY_HOE.get());

        addSword(   ModItems.INGOT_CMB_STEEL.get(), ModToolItems.CMB_SWORD.get());
        addPickaxe( ModItems.INGOT_CMB_STEEL.get(), ModToolItems.CMB_PICKAXE.get());
        addAxe(     ModItems.INGOT_CMB_STEEL.get(), ModToolItems.CMB_AXE.get());
        addShovel(  ModItems.INGOT_CMB_STEEL.get(), ModToolItems.CMB_SHOVEL.get());
        addHoe(     ModItems.INGOT_CMB_STEEL.get(), ModToolItems.CMB_HOE.get());

        addSword(   ModItems.INGOT_DESH.get(), ModToolItems.DESH_SWORD.get());
        addPickaxe( ModItems.INGOT_DESH.get(), ModToolItems.DESH_PICKAXE.get());
        addAxe(     ModItems.INGOT_DESH.get(), ModToolItems.DESH_AXE.get());
        addShovel(  ModItems.INGOT_DESH.get(), ModToolItems.DESH_SHOVEL.get());
        addHoe(     ModItems.INGOT_DESH.get(), ModToolItems.DESH_HOE.get());

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.ELEC_SWORD.get(), 1)
                .pattern("RPR")
                .pattern("RPR")
                .pattern(" B ")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('R', ModItems.BOLT_DURA_STEEL.get())
                .define('B', ModItems.BATTERY_LITHIUM.get())
                .unlockedBy("has_plastic", has(ModItemTags.ANY_PLASTIC_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.ELEC_PICKAXE.get(), 1)
                .pattern("RDM")
                .pattern(" PB")
                .pattern(" P ")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('D', ModItems.INGOT_DURA_STEEL.get())
                .define('R', ModItems.BOLT_DURA_STEEL.get())
                .define('M', ModItems.MOTOR.get())
                .define('B', ModItems.BATTERY_LITHIUM.get())
                .unlockedBy("has_plastic", has(ModItemTags.ANY_PLASTIC_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.ELEC_AXE.get(), 1)
                .pattern(" DP")
                .pattern("RRM")
                .pattern(" PB")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('D', ModItems.INGOT_DURA_STEEL.get())
                .define('R', ModItems.BOLT_DURA_STEEL.get())
                .define('M', ModItems.MOTOR.get())
                .define('B', ModItems.BATTERY_LITHIUM.get())
                .unlockedBy("has_plastic", has(ModItemTags.ANY_PLASTIC_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.ELEC_SHOVEL.get(), 1)
                .pattern("  P")
                .pattern("RRM")
                .pattern("  B")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('R', ModItems.BOLT_DURA_STEEL.get())
                .define('M', ModItems.MOTOR.get())
                .define('B', ModItems.BATTERY_LITHIUM.get())
                .unlockedBy("has_plastic", has(ModItemTags.ANY_PLASTIC_INGOT))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModToolItems.CENTRI_STICK.get(), 1)
                .requires(ModItems.CENTRIFUGE_ELEMENT.get())
                .requires(ModItems.ENERGY_CORE.get())
                .requires(Items.STICK)
                .unlockedBy("has_centrifuge_element", has(ModItems.CENTRIFUGE_ELEMENT.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.SMASHING_HAMMER.get(), 1)
                .pattern("STS")
                .pattern("SPS")
                .pattern(" P ")
                .define('S', ModBlocks.BLOCK_STEEL.get().asItem())
                .define('T', ModBlocks.BLOCK_TUNGSTEN.get().asItem())
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .unlockedBy("has_steel", has(ModBlocks.BLOCK_STEEL.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModToolItems.METEORITE_SWORD.get(), 1)
                .pattern("  B")
                .pattern("GB ")
                .pattern("SG ")
                .define('B', ModItems.BLADE_METEORITE.get())
                .define('G', ModItems.PLATE_GOLD.get())
                .define('S', Items.STICK)
                .unlockedBy("has_blade_meteorite", has(ModItems.BLADE_METEORITE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.DWARVEN_PICKAXE.get(), 1)
                .pattern("CIC")
                .pattern(" S ")
                .pattern(" S ")
                .define('C', ModItems.INGOT_RED_COPPER.get())
                .define('I', Items.IRON_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_red_copper", has(ModItems.INGOT_RED_COPPER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.DRAX.get(), 1)
                .pattern("BDS")
                .pattern("CDC")
                .pattern("FMF")
                .define('B', ModToolItems.STARMETAL_PICKAXE.get())
                .define('S', ModToolItems.STARMETAL_SHOVEL.get())
                .define('C', ModItems.INGOT_COBALT.get())
                .define('F', ModItems.FUSION_CORE.get())
                .define('D', ModItems.INGOT_DESH.get())
                .define('M', ModItems.MOTOR_DESH.get())
                .unlockedBy("has_starmetal_pickaxe", has(ModToolItems.STARMETAL_PICKAXE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.DRAX_MK2.get(), 1)
                .pattern("SCS")
                .pattern("IDI")
                .pattern("FEF")
                .define('S', ModItems.INGOT_STARMETAL.get())
                .define('C', ModItems.CRYSTAL_TRIXITE.get())
                .define('I', ModItems.INGOT_SATURNITE.get())
                .define('D', ModToolItems.DRAX.get())
                .define('F', ModItems.FUSION_CORE.get())
                .define('E', ModItems.CIRCUIT_ADVANCED.get())
                .unlockedBy("has_drax", has(ModToolItems.DRAX.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.DRAX_MK3.get(), 1)
                .pattern("ECE")
                .pattern("CDC")
                .pattern("SBS")
                .define('E', ModBlocks.BLOCK_EUPHEMIUM_CLUSTER.get().asItem())
                .define('C', ModItems.CRYSTAL_SCHRABIDIUM.get())
                .define('D', ModToolItems.DRAX_MK2.get())
                .define('S', ModItems.CIRCUIT_BISMOID.get())
                .define('B', ModItems.BATTERY_SPARK.get())
                .unlockedBy("has_drax_mk2", has(ModToolItems.DRAX_MK2.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.BISMUTH_PICKAXE.get(), 1)
                .pattern(" BM")
                .pattern("BPB")
                .pattern("TB ")
                .define('B', ModItems.INGOT_BISMUTH.get())
                .define('M', ModItems.INGOT_METEORITE.get())
                .define('P', ModToolItems.STARMETAL_PICKAXE.get())
                .define('T', ModItems.BOLT_TUNGSTEN.get())
                .unlockedBy("has_bismuth", has(ModItems.INGOT_BISMUTH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.VOLCANIC_PICKAXE.get(), 1)
                .pattern(" BM")
                .pattern("BPB")
                .pattern("TB ")
                .define('B', ModItems.GEM_VOLCANIC.get())
                .define('M', ModItems.INGOT_METEORITE.get())
                .define('P', ModToolItems.STARMETAL_PICKAXE.get())
                .define('T', ModItems.BOLT_TUNGSTEN.get())
                .unlockedBy("has_volcanic", has(ModItems.GEM_VOLCANIC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.CHLOROPHYTE_PICKAXE.get(), 1)
                .pattern(" SD")
                .pattern("APS")
                .pattern("FA ")
                .define('S', ModItems.BLADES_STEEL.get())
                .define('D', ModItems.POWDER_CHLOROPHYTE.get())
                .define('A', ModItems.INGOT_FIBERGLASS.get())
                .define('P', ModToolItems.BISMUTH_PICKAXE.get())
                .define('F', ModItems.BOLT_DURA_STEEL.get())
                .unlockedBy("has_chlorophyte", has(ModItems.POWDER_CHLOROPHYTE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.CHLOROPHYTE_PICKAXE.get(), 1)
                .pattern(" SD")
                .pattern("APS")
                .pattern("FA ")
                .define('S', ModItems.BLADES_STEEL.get())
                .define('D', ModItems.POWDER_CHLOROPHYTE.get())
                .define('A', ModItems.INGOT_FIBERGLASS.get())
                .define('P', ModToolItems.VOLCANIC_PICKAXE.get())
                .define('F', ModItems.BOLT_DURA_STEEL.get())
                .unlockedBy("has_chlorophyte", has(ModItems.POWDER_CHLOROPHYTE.get()))
                .save(pWriter, MODID + ":chlorophyte_pickaxe_volcanic");

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.MESE_PICKAXE.get(), 1)
                .pattern(" SD")
                .pattern("APS")
                .pattern("FA ")
                .define('S', ModItems.BLADES_DESH.get())
                .define('D', ModItems.POWDER_DINEUTRONIUM.get())
                .define('A', ModItems.PLATE_PAA.get())
                .define('P', ModToolItems.CHLOROPHYTE_PICKAXE.get())
                .define('F', ModItems.SHIMMER_HANDLE.get())
                .unlockedBy("has_mese", has(ModItems.POWDER_DINEUTRONIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.BISMUTH_AXE.get(), 1)
                .pattern(" BM")
                .pattern("BPB")
                .pattern("TB ")
                .define('B', ModItems.INGOT_BISMUTH.get())
                .define('M', ModItems.INGOT_METEORITE.get())
                .define('P', ModToolItems.STARMETAL_AXE.get())
                .define('T', ModItems.BOLT_TUNGSTEN.get())
                .unlockedBy("has_bismuth", has(ModItems.INGOT_BISMUTH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.VOLCANIC_AXE.get(), 1)
                .pattern(" BM")
                .pattern("BPB")
                .pattern("TB ")
                .define('B', ModItems.GEM_VOLCANIC.get())
                .define('M', ModItems.INGOT_METEORITE.get())
                .define('P', ModToolItems.STARMETAL_AXE.get())
                .define('T', ModItems.BOLT_TUNGSTEN.get())
                .unlockedBy("has_volcanic", has(ModItems.GEM_VOLCANIC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.CHLOROPHYTE_AXE.get(), 1)
                .pattern(" SD")
                .pattern("APS")
                .pattern("FA ")
                .define('S', ModItems.BLADES_STEEL.get())
                .define('D', ModItems.POWDER_CHLOROPHYTE.get())
                .define('A', ModItems.INGOT_FIBERGLASS.get())
                .define('P', ModToolItems.BISMUTH_AXE.get())
                .define('F', ModItems.BOLT_DURA_STEEL.get())
                .unlockedBy("has_chlorophyte", has(ModItems.POWDER_CHLOROPHYTE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.CHLOROPHYTE_AXE.get(), 1)
                .pattern(" SD")
                .pattern("APS")
                .pattern("FA ")
                .define('S', ModItems.BLADES_STEEL.get())
                .define('D', ModItems.POWDER_CHLOROPHYTE.get())
                .define('A', ModItems.INGOT_FIBERGLASS.get())
                .define('P', ModToolItems.VOLCANIC_AXE.get())
                .define('F', ModItems.BOLT_DURA_STEEL.get())
                .unlockedBy("has_chlorophyte", has(ModItems.POWDER_CHLOROPHYTE.get()))
                .save(pWriter, MODID + ":chlorophyte_axe_volcanic");

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.MESE_AXE.get(), 1)
                .pattern(" SD")
                .pattern("APS")
                .pattern("FA ")
                .define('S', ModItems.BLADES_DESH.get())
                .define('D', ModItems.POWDER_DINEUTRONIUM.get())
                .define('A', ModItems.PLATE_PAA.get())
                .define('P', ModToolItems.CHLOROPHYTE_AXE.get())
                .define('F', ModItems.SHIMMER_HANDLE.get())
                .unlockedBy("has_mese", has(ModItems.POWDER_DINEUTRONIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.CHAINSAW.get(), 1)
                .pattern("CCH")
                .pattern("BBP")
                .pattern("CCE")
                .define('H', ModItems.SHELL_STEEL.get())
                .define('B', ModItems.BLADES_STEEL.get())
                .define('P', ModItems.PISTON_SELENIUM.get())
                .define('C', ModBlocks.STEEL_CHAIN.get().asItem())
                .define('E', ModItems.FLUID_CANISTER.get())
                .unlockedBy("has_chain", has(ModBlocks.STEEL_CHAIN.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.CROWBAR.get(), 1)
                .pattern("II")
                .pattern(" I")
                .pattern(" I")
                .define('I', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_steel", has(ModItems.INGOT_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.BOTTLE_OPENER.get(), 1)
                .pattern("S")
                .pattern("P")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('P', ItemTags.PLANKS)
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, Items.SADDLE, 1)
                .pattern("LLL")
                .pattern("LRL")
                .pattern(" S ")
                .define('S', ModItems.INGOT_STEEL.get())
                .define('L', Items.LEATHER)
                .define('R', ModItems.PLANT_ROPE.get())
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(pWriter, MODID + ":saddle_from_rope");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MATCHSTICK.get(), 16)
                .pattern("I")
                .pattern("S")
                .define('I', ModItems.SULFUR.get())
                .define('S', Items.STICK)
                .unlockedBy("has_sulfur", has(ModItems.SULFUR.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MATCHSTICK.get(), 24)
                .pattern("I")
                .pattern("S")
                .define('I', ModItems.POWDER_FIRE.get())
                .define('S', Items.STICK)
                .unlockedBy("has_red_phosphorus", has(ModItems.POWDER_FIRE.get()))
                .save(pWriter, MODID + ":matchstick_phosphorus");

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.WOOD_GAVEL.get(), 1)
                .pattern("SWS")
                .pattern(" R ")
                .pattern(" R ")
                .define('S', ItemTags.WOODEN_SLABS)
                .define('W', ItemTags.LOGS)
                .define('R', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.LEAD_GAVEL.get(), 1)
                .pattern("PIP")
                .pattern("IGI")
                .pattern("PIP")
                .define('P', ModItems.PELLET_BUCKSHOT.get())
                .define('I', ModItems.INGOT_LEAD.get())
                .define('G', ModToolItems.WOOD_GAVEL.get())
                .unlockedBy("has_wood_gavel", has(ModToolItems.WOOD_GAVEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.PIPE_LEAD.get(), 1)
                .pattern("II")
                .pattern(" I")
                .pattern(" I")
                .define('I', ModItems.PIPE_LEAD.get())
                .unlockedBy("has_lead_pipe", has(ModItems.PIPE_LEAD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.ULLAPOOL_CABER.get(), 1)
                .pattern("ITI")
                .pattern(" S ")
                .pattern(" S ")
                .define('I', ModItems.PLATE_IRON.get())
                .define('T', Blocks.TNT)
                .define('S', Items.STICK)
                .unlockedBy("has_tnt", has(Blocks.TNT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.RANGEFINDER.get(), 1)
                .pattern("GRC")
                .pattern("  S")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('R', Items.REDSTONE)
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('S', ModItems.PLATE_STEEL.get())
                .unlockedBy("has_circuit_basic", has(ModItems.CIRCUIT_BASIC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DESIGNATOR.get(), 1)
                .pattern("  A")
                .pattern("#B#")
                .pattern("#B#")
                .define('#', ModItemTags.ANY_PLASTIC_INGOT)
                .define('A', ModItems.PLATE_STEEL.get())
                .define('B', ModItems.CIRCUIT_BASIC.get())
                .unlockedBy("has_circuit_basic", has(ModItems.CIRCUIT_BASIC.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.DESIGNATOR_RANGE.get(), 1)
                .requires(ModItems.RANGEFINDER.get())
                .requires(ModItems.DESIGNATOR.get())
                .requires(ModItemTags.ANY_PLASTIC_INGOT)
                .unlockedBy("has_designator", has(ModItems.DESIGNATOR.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DESIGNATOR_MANUAL.get(), 1)
                .pattern("  A")
                .pattern("#C#")
                .pattern("#B#")
                .define('#', ModItemTags.ANY_PLASTIC_INGOT)
                .define('A', ModItems.PLATE_LEAD.get())
                .define('B', ModItems.CIRCUIT_ADVANCED.get())
                .define('C', ModItems.DESIGNATOR.get())
                .unlockedBy("has_designator", has(ModItems.DESIGNATOR.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.DESIGNATOR_ARTY_RANGE.get(), 1)
                .requires(ModItems.RANGEFINDER.get())
                .requires(ModItems.CIRCUIT_ADVANCED.get())
                .requires(ModItemTags.ANY_PLASTIC_INGOT)
                .unlockedBy("has_rangefinder", has(ModItems.RANGEFINDER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.LINKER.get(), 1)
                .pattern("I I")
                .pattern("ICI")
                .pattern("GGG")
                .define('I', ModItems.PLATE_IRON.get())
                .define('G', ModItems.PLATE_GOLD.get())
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .unlockedBy("has_circuit_advanced", has(ModItems.CIRCUIT_ADVANCED.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.OIL_DETECTOR.get(), 1)
                .pattern("W I")
                .pattern("WCI")
                .pattern("PPP")
                .define('W', ModItems.WIRE_GOLD.get())
                .define('I', ModItems.INGOT_RED_COPPER.get())
                .define('C', ModItems.CIRCUIT_ANALOG.get())
                .define('P', ModItems.PLATE_CAST_STEEL.get())
                .unlockedBy("has_circuit_analog", has(ModItems.CIRCUIT_ANALOG.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.TURRET_CHIP.get(), 1)
                .pattern("WWW")
                .pattern("CPC")
                .pattern("WWW")
                .define('W', ModItems.WIRE_GOLD.get())
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .unlockedBy("has_circuit_advanced", has(ModItems.CIRCUIT_ADVANCED.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SURVEY_SCANNER.get(), 1)
                .pattern("SWS")
                .pattern(" G ")
                .pattern("PCP")
                .define('W', ModItems.WIRE_GOLD.get())
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('S', ModItems.PLATE_CAST_STEEL.get())
                .define('G', Items.GOLD_INGOT)
                .unlockedBy("has_circuit_advanced", has(ModItems.CIRCUIT_ADVANCED.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.GEIGER_COUNTER.get(), 1)
                .pattern("GPP")
                .pattern("WCS")
                .pattern("WBB")
                .define('W', ModItems.WIRE_GOLD.get())
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .define('G', Items.GOLD_INGOT)
                .define('S', ModItems.PLATE_CAST_STEEL.get())
                .define('B', ModItems.INGOT_BERYLLIUM.get())
                .unlockedBy("has_circuit_basic", has(ModItems.CIRCUIT_BASIC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DOSIMETER.get(), 1)
                .pattern("WGW")
                .pattern("WCW")
                .pattern("WBW")
                .define('W', ItemTags.PLANKS)
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('C', ModItems.CIRCUIT_VACUUM_TUBE.get())
                .define('B', ModItems.INGOT_BERYLLIUM.get())
                .unlockedBy("has_circuit_vacuum_tube", has(ModItems.CIRCUIT_VACUUM_TUBE.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModBlocks.GEIGER.get().asItem(), 1)
                .requires(ModItems.GEIGER_COUNTER.get())
                .unlockedBy("has_geiger_counter", has(ModItems.GEIGER_COUNTER.get()))
                .save(pWriter, MODID + ":geiger_from_counter");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.DIGAMMA_DIAGNOSTIC.get(), 1)
                .requires(ModItems.GEIGER_COUNTER.get())
                .requires(ModItems.BILLET_POLONIUM.get())
                .requires(ModItems.INGOT_ASBESTOS.get())
                .unlockedBy("has_geiger_counter", has(ModItems.GEIGER_COUNTER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.POLLUTION_DETECTOR.get(), 1)
                .pattern("SFS")
                .pattern("SCS")
                .pattern(" S ")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('F', ModItems.FILTER_COAL.get())
                .define('C', ModItems.CIRCUIT_VACUUM_TUBE.get())
                .unlockedBy("has_circuit_vacuum_tube", has(ModItems.CIRCUIT_VACUUM_TUBE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ORE_DENSITY_SCANNER.get(), 1)
                .pattern("VVV")
                .pattern("CSC")
                .pattern("GGG")
                .define('V', ModItems.CIRCUIT_VACUUM_TUBE.get())
                .define('C', ModItems.CIRCUIT_CAPACITOR.get())
                .define('S', ModItems.CIRCUIT_CONTROLLER_CHASSIS.get())
                .define('G', ModItems.PLATE_GOLD.get())
                .unlockedBy("has_circuit_vacuum_tube", has(ModItems.CIRCUIT_VACUUM_TUBE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.DEFUSER.get(), 1)
                .pattern(" PS")
                .pattern("P P")
                .pattern(" P ")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('S', ModItems.PLATE_STEEL.get())
                .unlockedBy("has_plastic", has(ModItemTags.ANY_PLASTIC_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.COLTAN_TOOL.get(), 1)
                .pattern("ACA")
                .pattern("CXC")
                .pattern("ACA")
                .define('A', ModItems.INGOT_ADVANCED_ALLOY.get())
                .define('C', ModItems.CRYSTAL_CINNABAR.get())
                .define('X', Items.COMPASS)
                .unlockedBy("has_advanced_alloy", has(ModItems.INGOT_ADVANCED_ALLOY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.REACHER.get(), 1)
                .pattern("BIB")
                .pattern("P P")
                .pattern("B B")
                .define('B', ModItems.BOLT_TUNGSTEN.get())
                .define('I', ModItems.INGOT_TUNGSTEN.get())
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_tungsten", has(ModItems.INGOT_TUNGSTEN.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SAT_DESIGNATOR.get(), 1)
                .pattern("RRD")
                .pattern("PIC")
                .pattern("  P")
                .define('P', ModItems.PLATE_GOLD.get())
                .define('R', Items.REDSTONE)
                .define('C', ModItems.CIRCUIT_ADVANCED.get())
                .define('D', ModItems.SAT_CHIP.get())
                .define('I', Items.GOLD_INGOT)
                .unlockedBy("has_circuit_advanced", has(ModItems.CIRCUIT_ADVANCED.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.TOOLS, ModItems.SAT_RELAY.get(), 1)
                .requires(ModItems.SAT_CHIP.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModItems.RADAR_LINKER.get())
                .unlockedBy("has_sat_chip", has(ModItems.SAT_CHIP.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SETTINGS_TOOL.get(), 1)
                .pattern(" P ")
                .pattern("PCP")
                .pattern("III")
                .define('P', ModItems.PLATE_IRON.get())
                .define('C', ModItems.CIRCUIT_ANALOG.get())
                .define('I', ModItems.PLATE_POLYMER.get())
                .unlockedBy("has_circuit_analog", has(ModItems.CIRCUIT_ANALOG.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.PIPETTE.get(), 1)
                .pattern("  L")
                .pattern(" G ")
                .pattern("G  ")
                .define('L', ModItemTags.ANY_RUBBER_INGOT)
                .define('G', Items.GLASS)
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.PIPETTE_BORON.get(), 1)
                .pattern("  P")
                .pattern(" B ")
                .pattern("B  ")
                .define('P', ModItems.INGOT_RUBBER.get())
                .define('B', ModBlocks.GLASS_BORON.get().asItem())
                .unlockedBy("has_boron_glass", has(ModBlocks.GLASS_BORON.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.PIPETTE_LABORATORY.get(), 1)
                .pattern("  C")
                .pattern(" R ")
                .pattern("P  ")
                .define('C', ModItems.CIRCUIT_CHIP.get())
                .define('R', ModItems.INGOT_RUBBER.get())
                .define('P', ModItems.PIPETTE_BORON.get())
                .unlockedBy("has_pipette_boron", has(ModItems.PIPETTE_BORON.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SIPHON.get(), 1)
                .pattern(" GR")
                .pattern(" GR")
                .pattern(" G ")
                .define('G', Items.GLASS)
                .define('R', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_clear_glass", has(Items.GLASS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.MIRROR_TOOL.get(), 1)
                .pattern(" A ")
                .pattern(" IA")
                .pattern("I  ")
                .define('A', ModItems.INGOT_ALUMINIUM.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_aluminium", has(ModItems.INGOT_ALUMINIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.RBMK_TOOL.get(), 1)
                .pattern(" A ")
                .pattern(" IA")
                .pattern("I  ")
                .define('A', ModItems.INGOT_LEAD.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_lead", has(ModItems.INGOT_LEAD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.POWER_NET_TOOL.get(), 1)
                .pattern("WRW")
                .pattern(" I ")
                .pattern(" B ")
                .define('W', ModItems.WIRE_RED_COPPER.get())
                .define('R', Items.REDSTONE)
                .define('I', Items.IRON_INGOT)
                .define('B', ModItems.BATTERY_GENERIC.get())
                .unlockedBy("has_wire", has(ModItems.WIRE_RED_COPPER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ANALYSIS_TOOL.get(), 1)
                .pattern("  G")
                .pattern(" S ")
                .pattern("S  ")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('S', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_steel", has(ModItems.INGOT_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.TOOLBOX.get(), 1)
                .pattern("CCC")
                .pattern("CIC")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_copper_plate", has(ModItems.PLATE_COPPER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SCREWDRIVER.get(), 1)
                .pattern("  I")
                .pattern(" I ")
                .pattern("S  ")
                .define('S', ModItems.INGOT_STEEL.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_steel", has(ModItems.INGOT_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.SCREWDRIVER_DESH.get(), 1)
                .pattern("  I")
                .pattern(" I ")
                .pattern("S  ")
                .define('S', ModItemTags.ANY_PLASTIC_INGOT)
                .define('I', ModItems.INGOT_DESH.get())
                .unlockedBy("has_desh", has(ModItems.INGOT_DESH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.HAND_DRILL.get(), 1)
                .pattern(" D")
                .pattern("S ")
                .pattern(" S")
                .define('D', ModItems.INGOT_DURA_STEEL.get())
                .define('S', Items.STICK)
                .unlockedBy("has_dura_steel", has(ModItems.INGOT_DURA_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.HAND_DRILL_DESH.get(), 1)
                .pattern(" D")
                .pattern("S ")
                .pattern(" S")
                .define('D', ModItems.INGOT_DESH.get())
                .define('S', ModItemTags.ANY_PLASTIC_INGOT)
                .unlockedBy("has_desh", has(ModItems.INGOT_DESH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.CHEMISTRY_SET.get(), 1)
                .pattern("GIG")
                .pattern("GCG")
                .define('G', ModItemTags.ANY_GLASS_BLOCKS)
                .define('I', Items.IRON_INGOT)
                .define('C', ModItems.INGOT_RED_COPPER.get())
                .unlockedBy("has_glass", has(ModItemTags.ANY_GLASS_BLOCKS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.CHEMISTRY_SET_BORON.get(), 1)
                .pattern("GIG")
                .pattern("GCG")
                .define('G', ModBlocks.GLASS_BORON.get().asItem())
                .define('I', ModItems.INGOT_STEEL.get())
                .define('C', ModItems.INGOT_COBALT.get())
                .unlockedBy("has_boron_glass", has(ModBlocks.GLASS_BORON.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.BLOWTORCH.get(), 1)
                .pattern("CC ")
                .pattern(" I ")
                .pattern("CCC")
                .define('C', ModItems.PLATE_CAST_STEEL.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_cast_steel_plate", has(ModItems.PLATE_CAST_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.ACETYLENE_TORCH.get(), 1)
                .pattern("SS ")
                .pattern(" PS")
                .pattern(" T ")
                .define('S', ModItems.PLATE_CAST_STEEL.get())
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('T', ModItems.TANK_STEEL.get())
                .unlockedBy("has_cast_steel_plate", has(ModItems.PLATE_CAST_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.BOLTGUN.get(), 1)
                .pattern("DPS")
                .pattern(" RD")
                .pattern(" D ")
                .define('D', ModItems.INGOT_DURA_STEEL.get())
                .define('P', ModItems.PISTON_PNEUMATIC.get())
                .define('R', ModItems.INGOT_RUBBER.get())
                .define('S', ModItems.SHELL_STEEL.get())
                .unlockedBy("has_dura_steel", has(ModItems.INGOT_DURA_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.REBAR_PLACER.get(), 1)
                .pattern("RDR")
                .pattern("DWD")
                .pattern("RDR")
                .define('R', ModBlocks.REBAR.get().asItem())
                .define('D', ModItems.DUCTTAPE.get())
                .define('W', ModToolItems.WRENCH.get())
                .unlockedBy("has_rebar", has(ModBlocks.REBAR.get().asItem()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BOBMAZON.get(), 1)
                .requires(Items.BOOK)
                .requires(Items.GOLD_NUGGET)
                .requires(Items.STRING)
                .requires(Items.BLUE_DYE)
                .unlockedBy("has_book", has(Items.BOOK))
                .save(pWriter);

        /* TODO ItemModMinecart
        CraftingManager.addRecipeAuto(ItemModMinecart.createCartItem(EnumCartBase.WOOD, EnumMinecart.EMPTY), new Object[] { "P P", "WPW", 'P',KEY_SLAB, 'W', KEY_PLANKS });
		CraftingManager.addRecipeAuto(ItemModMinecart.createCartItem(EnumCartBase.STEEL, EnumMinecart.EMPTY), new Object[] { "P P", "IPI", 'P', STEEL.plate(), 'I', STEEL.ingot() });
		CraftingManager.addShapelessAuto(ItemModMinecart.createCartItem(EnumCartBase.PAINTED, EnumMinecart.EMPTY), new Object[] { ItemModMinecart.createCartItem(EnumCartBase.STEEL, EnumMinecart.EMPTY), KEY_RED });

        for(EnumCartBase base : EnumCartBase.values()) {

                    if(EnumMinecart.DESTROYER.supportsBase(base))	CraftingManager.addRecipeAuto(ItemModMinecart.createCartItem(base, EnumMinecart.DESTROYER), new Object[] { "S S", "BLB", "SCS", 'S', STEEL.ingot(), 'B', ModItems.blades_steel, 'L', Fluids.LAVA.getDict(1000), 'C', ItemModMinecart.createCartItem(base, EnumMinecart.EMPTY) });
                    if(EnumMinecart.POWDER.supportsBase(base))		CraftingManager.addRecipeAuto(ItemModMinecart.createCartItem(base, EnumMinecart.POWDER), new Object[] { "PPP", "PCP", "PPP", 'P', Items.gunpowder, 'C', ItemModMinecart.createCartItem(base, EnumMinecart.EMPTY) });
                    if(EnumMinecart.SEMTEX.supportsBase(base))		CraftingManager.addRecipeAuto(ItemModMinecart.createCartItem(base, EnumMinecart.SEMTEX), new Object[] { "S", "C", 'S', ModBlocks.semtex, 'C', ItemModMinecart.createCartItem(base, EnumMinecart.EMPTY) });
                }
        net.minecraft.item.crafting.CraftingManager.getInstance().addRecipe(DictFrame.fromOne(ModItems.cart, EnumMinecart.CRATE), new Object[] { "C", "S", 'C', ModBlocks.crate_steel, 'S', Items.minecart }).func_92100_c();

         */

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOAT_RUBBER.get(), 1)
                .pattern("L L")
                .pattern("LLL")
                .define('L', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(pWriter);

        if (GeneralConfig.ENABLE_LBSM && GeneralConfig.ENABLE_LBSM_SIMPLE_TOOL_RECIPES) {
            addSword(ModBlocks.BLOCK_COBALT.get().asItem(), ModToolItems.COBALT_DECORATED_SWORD.get());
            addPickaxe(ModBlocks.BLOCK_COBALT.get().asItem(), ModToolItems.COBALT_DECORATED_PICKAXE.get());
            addAxe(ModBlocks.BLOCK_COBALT.get().asItem(), ModToolItems.COBALT_DECORATED_AXE.get());
            addShovel(ModBlocks.BLOCK_COBALT.get().asItem(), ModToolItems.COBALT_DECORATED_SHOVEL.get());
            addHoe(ModBlocks.BLOCK_COBALT.get().asItem(), ModToolItems.COBALT_DECORATED_HOE.get());
            addSword(ModItems.INGOT_STARMETAL.get(), ModToolItems.STARMETAL_SWORD.get());
            addPickaxe(ModItems.INGOT_STARMETAL.get(), ModToolItems.STARMETAL_PICKAXE.get());
            addAxe(ModItems.INGOT_STARMETAL.get(), ModToolItems.STARMETAL_AXE.get());
            addShovel(ModItems.INGOT_STARMETAL.get(), ModToolItems.STARMETAL_SHOVEL.get());
            addHoe(ModItems.INGOT_STARMETAL.get(), ModToolItems.STARMETAL_HOE.get());
            addSword(ModItems.INGOT_SCHRABIDIUM.get(), ModToolItems.SCHRABIDIUM_SWORD.get());
            addPickaxe(ModItems.INGOT_SCHRABIDIUM.get(), ModToolItems.SCHRABIDIUM_PICKAXE.get());
            addAxe(ModItems.INGOT_SCHRABIDIUM.get(), ModToolItems.SCHRABIDIUM_AXE.get());
            addShovel(ModItems.INGOT_SCHRABIDIUM.get(), ModToolItems.SCHRABIDIUM_SHOVEL.get());
            addHoe(ModItems.INGOT_SCHRABIDIUM.get(), ModToolItems.SCHRABIDIUM_HOE.get());
        } else {
            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModToolItems.STARMETAL_SWORD.get(), 1)
                    .pattern(" I ")
                    .pattern(" B ")
                    .pattern("ISI")
                    .define('I', ModItems.INGOT_STARMETAL.get())
                    .define('S', ModItems.RING_STARMETAL.get())
                    .define('B', ModToolItems.COBALT_DECORATED_SWORD.get())
                    .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                    .save(pWriter);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.STARMETAL_PICKAXE.get(), 1)
                    .pattern("ISI")
                    .pattern(" B ")
                    .pattern(" I ")
                    .define('I', ModItems.INGOT_STARMETAL.get())
                    .define('S', ModItems.RING_STARMETAL.get())
                    .define('B', ModToolItems.COBALT_DECORATED_PICKAXE.get())
                    .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                    .save(pWriter);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.STARMETAL_AXE.get(), 1)
                    .pattern("IS")
                    .pattern("IB")
                    .pattern(" I")
                    .define('I', ModItems.INGOT_STARMETAL.get())
                    .define('S', ModItems.RING_STARMETAL.get())
                    .define('B', ModToolItems.COBALT_DECORATED_AXE.get())
                    .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                    .save(pWriter);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.STARMETAL_SHOVEL.get(), 1)
                    .pattern("I")
                    .pattern("B")
                    .pattern("I")
                    .define('I', ModItems.INGOT_STARMETAL.get())
                    .define('B', ModToolItems.COBALT_DECORATED_SHOVEL.get())
                    .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                    .save(pWriter);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.STARMETAL_HOE.get(), 1)
                    .pattern("IS")
                    .pattern(" B")
                    .pattern(" I")
                    .define('I', ModItems.INGOT_STARMETAL.get())
                    .define('S', ModItems.RING_STARMETAL.get())
                    .define('B', ModToolItems.COBALT_DECORATED_HOE.get())
                    .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                    .save(pWriter);

            ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModToolItems.SCHRABIDIUM_SWORD.get(), 1)
                    .pattern("I")
                    .pattern("W")
                    .pattern("S")
                    .define('I', ModBlocks.BLOCK_SCHRABIDIUM.get().asItem())
                    .define('W', ModToolItems.DESH_SWORD.get())
                    .define('S', ModItemTags.ANY_PLASTIC_INGOT)
                    .unlockedBy("has_schrabidium", has(ModBlocks.BLOCK_SCHRABIDIUM.get().asItem()))
                    .save(pWriter);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.SCHRABIDIUM_PICKAXE.get(), 1)
                    .pattern("BSB")
                    .pattern(" W ")
                    .pattern(" P ")
                    .define('B', ModItems.BLADES_DESH.get())
                    .define('S', ModBlocks.BLOCK_SCHRABIDIUM.get().asItem())
                    .define('W', ModToolItems.DESH_PICKAXE.get())
                    .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                    .unlockedBy("has_schrabidium", has(ModBlocks.BLOCK_SCHRABIDIUM.get().asItem()))
                    .save(pWriter);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.SCHRABIDIUM_AXE.get(), 1)
                    .pattern("BS")
                    .pattern("BW")
                    .pattern(" P")
                    .define('B', ModItems.BLADES_DESH.get())
                    .define('S', ModBlocks.BLOCK_SCHRABIDIUM.get().asItem())
                    .define('W', ModToolItems.DESH_AXE.get())
                    .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                    .unlockedBy("has_schrabidium", has(ModBlocks.BLOCK_SCHRABIDIUM.get().asItem()))
                    .save(pWriter);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.SCHRABIDIUM_SHOVEL.get(), 1)
                    .pattern("B")
                    .pattern("W")
                    .pattern("P")
                    .define('B', ModBlocks.BLOCK_SCHRABIDIUM.get().asItem())
                    .define('W', ModToolItems.DESH_SHOVEL.get())
                    .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                    .unlockedBy("has_schrabidium", has(ModBlocks.BLOCK_SCHRABIDIUM.get().asItem()))
                    .save(pWriter);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModToolItems.SCHRABIDIUM_HOE.get(), 1)
                    .pattern("IW")
                    .pattern(" S")
                    .pattern(" S")
                    .define('I', ModItems.INGOT_SCHRABIDIUM.get())
                    .define('W', ModToolItems.DESH_HOE.get())
                    .define('S', ModItemTags.ANY_PLASTIC_INGOT)
                    .unlockedBy("has_schrabidium", has(ModItems.INGOT_SCHRABIDIUM.get()))
                    .save(pWriter);
        }
    }

    public static void addSword(Item ingot, Item sword) {
        addTool(ingot, sword, patternSword);
    }
    public static void addPickaxe(Item ingot, Item pick) {
        addTool(ingot, pick, patternPick);
    }
    public static void addAxe(Item ingot, Item axe) {
        addTool(ingot, axe, patternAxe);
    }
    public static void addShovel(Item ingot, Item shovel) {
        addTool(ingot, shovel, patternShovel);
    }
    public static void addHoe(Item ingot, Item hoe) {
        addTool(ingot, hoe, patternHoe);
    }

    public static void addTool(Item material, Item tool, String[] pattern) {
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, tool, 1)
                .pattern(pattern[0])
                .pattern(pattern[1])
                .pattern(pattern[2])
                .define('X', material)
                .define('#', Items.STICK)
                .unlockedBy("has_material", has(material))
                .save(writer, MODID + ":" + tool.getDescriptionId().replace("item.hbm.", ""));
    }
}
