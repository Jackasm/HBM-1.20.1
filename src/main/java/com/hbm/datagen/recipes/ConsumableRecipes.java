package com.hbm.datagen.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.datagen.recipes.ingredient.FluidBucketIngredient;
import com.hbm.datagen.recipes.ingredient.FluidTankIngredient;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;

import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

import java.util.function.Consumer;

import static com.hbm.util.RefStrings.MODID;

public class ConsumableRecipes extends ModRecipeProvider {

    public ConsumableRecipes(PackOutput pOutput) { super(pOutput); }

    public static void generateConsumableRecipes(Consumer<FinishedRecipe> pWriter) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOMB_CALLER_CARPET.get(), 1)
                .pattern("TTT")
                .pattern("TRT")
                .pattern("TTT")
                .define('T', Blocks.TNT)
                .define('R', ModItems.RANGEFINDER.get())
                .unlockedBy("has_tnt", has(Blocks.TNT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOMB_CALLER_NAPALM.get(), 1)
                .pattern("TTT")
                .pattern("TRT")
                .pattern("TTT")
                .define('T', ModItems.GRENADE_GASCAN.get())
                .define('R', ModItems.RANGEFINDER.get())
                .unlockedBy("has_grenade_gascan", has(ModItems.GRENADE_GASCAN.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOMB_CALLER_GAS.get(), 1)
                .pattern("TTT")
                .pattern("TRT")
                .pattern("TTT")
                .define('T', ModItems.PELLET_GAS.get())
                .define('R', ModItems.RANGEFINDER.get())
                .unlockedBy("has_pellet_gas", has(ModItems.PELLET_GAS.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOMB_CALLER_ORANGE.get(), 1)
                .pattern("T")
                .pattern("R")
                .pattern("T")
                .define('T', ModItems.GRENADE_CLOUD.get())
                .define('R', ModItems.RANGEFINDER.get())
                .unlockedBy("has_grenade_cloud", has(ModItems.GRENADE_CLOUD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOMB_CALLER_NUKE.get(), 1)
                .pattern("TRC")
                .define('T', ModAmmoItems.AMMO_NUKE_HIGH.get())
                .define('R', ModItems.RANGEFINDER.get())
                .define('C', ModItems.CIRCUIT_CONTROLLER.get())
                .unlockedBy("has_ammo_nuke", has(ModAmmoItems.AMMO_NUKE_HIGH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.BOMB_WAFFLE.get(), 1)
                .pattern("WEW")
                .pattern("MPM")
                .pattern("WEW")
                .define('W', Items.WHEAT)
                .define('E', Items.EGG)
                .define('M', Items.MILK_BUCKET)
                .define('P', ModItems.MAN_CORE.get())
                .unlockedBy("has_man_core", has(ModItems.MAN_CORE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.SCHNITZEL_VEGAN.get(), 3)
                .pattern("RWR")
                .pattern("WPW")
                .pattern("RWR")
                .define('W', ModItems.NUCLEAR_WASTE.get())
                .define('R', Items.SUGAR_CANE)
                .define('P', Items.PUMPKIN_SEEDS)
                .unlockedBy("has_nuclear_waste", has(ModItems.NUCLEAR_WASTE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.COTTON_CANDY.get(), 2)
                .pattern(" S ")
                .pattern("SPS")
                .pattern(" H ")
                .define('P', ModItems.NUGGET_PU239.get())
                .define('S', Items.SUGAR)
                .define('H', Items.STICK)
                .unlockedBy("has_plutonium", has(ModItems.NUGGET_PU239.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.APPLE_SCHRABIDIUM_NUGGET.get(), 1)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .define('S', ModItems.NUGGET_SCHRABIDIUM.get())
                .define('A', Items.APPLE)
                .unlockedBy("has_schrabidium", has(ModItems.NUGGET_SCHRABIDIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.APPLE_SCHRABIDIUM_INGOT.get(), 1)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .define('S', ModItems.INGOT_SCHRABIDIUM.get())
                .define('A', Items.APPLE)
                .unlockedBy("has_schrabidium", has(ModItems.INGOT_SCHRABIDIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.APPLE_SCHRABIDIUM_BLOCK.get(), 1)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .define('S', ModBlocks.BLOCK_SCHRABIDIUM.get().asItem())
                .define('A', Items.APPLE)
                .unlockedBy("has_schrabidium_block", has(ModBlocks.BLOCK_SCHRABIDIUM.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.APPLE_LEAD_NUGGET.get(), 1)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .define('S', ModItems.NUGGET_LEAD.get())
                .define('A', Items.APPLE)
                .unlockedBy("has_lead", has(ModItems.NUGGET_LEAD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.APPLE_LEAD_INGOT.get(), 1)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .define('S', ModItems.INGOT_LEAD.get())
                .define('A', Items.APPLE)
                .unlockedBy("has_lead", has(ModItems.INGOT_LEAD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.APPLE_LEAD_BLOCK.get(), 1)
                .pattern("SSS")
                .pattern("SAS")
                .pattern("SSS")
                .define('S', ModBlocks.BLOCK_LEAD.get().asItem())
                .define('A', Items.APPLE)
                .unlockedBy("has_lead_block", has(ModBlocks.BLOCK_LEAD.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.APPLE_EUPHEMIUM.get(), 1)
                .pattern("EEE")
                .pattern("EAE")
                .pattern("EEE")
                .define('E', ModItems.NUGGET_EUPHEMIUM.get())
                .define('A', Items.APPLE)
                .unlockedBy("has_euphemium", has(ModItems.NUGGET_EUPHEMIUM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.TEM_FLAKES_CHEAP.get(), 1)
                .requires(Items.GOLD_NUGGET)
                .requires(Items.PAPER)
                .unlockedBy("has_gold", has(Items.GOLD_NUGGET))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.TEM_FLAKES_NORMAL.get(), 1)
                .requires(Items.GOLD_NUGGET)
                .requires(Items.GOLD_NUGGET)
                .requires(Items.GOLD_NUGGET)
                .requires(Items.PAPER)
                .unlockedBy("has_gold", has(Items.GOLD_NUGGET))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.TEM_FLAKES_EXPENSIVE.get(), 1)
                .requires(Items.GOLD_INGOT)
                .requires(Items.GOLD_INGOT)
                .requires(Items.GOLD_NUGGET)
                .requires(Items.GOLD_NUGGET)
                .requires(Items.PAPER)
                .unlockedBy("has_gold", has(Items.GOLD_INGOT))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.GLOWING_STEW.get(), 1)
                .requires(Items.BOWL)
                .requires(ModBlocks.MUSH.get().asItem())
                .requires(ModBlocks.MUSH.get().asItem())
                .unlockedBy("has_mush", has(ModBlocks.MUSH.get().asItem()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BALEFIRE_SCRAMBLED.get(), 1)
                .requires(Items.BOWL)
                .requires(ModItems.EGG_BALEFIRE.get())
                .unlockedBy("has_balefire_egg", has(ModItems.EGG_BALEFIRE.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.BALEFIRE_AND_HAM.get(), 1)
                .requires(ModItems.BALEFIRE_SCRAMBLED.get())
                .requires(Items.COOKED_BEEF)
                .unlockedBy("has_balefire_scrambled", has(ModItems.BALEFIRE_SCRAMBLED.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MED_IPECAC.get(), 1)
                .requires(Items.GLASS_BOTTLE)
                .requires(Items.NETHER_WART)
                .unlockedBy("has_nether_wart", has(Items.NETHER_WART))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MED_PTSD.get(), 1)
                .requires(ModItems.MED_IPECAC.get())
                .unlockedBy("has_med_ipecac", has(ModItems.MED_IPECAC.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.PANCAKE.get(), 1)
                .requires(Items.REDSTONE)
                .requires(ModItems.POWDER_DIAMOND.get())
                .requires(Items.WHEAT)
                .requires(ModItems.BOLT_STEEL.get())
                .requires(ModItems.WIRE_COPPER.get())
                .requires(ModItems.PLATE_STEEL.get())
                .unlockedBy("has_steel", has(ModItems.PLATE_STEEL.get()))
                .save(pWriter, MODID + ":pancake_0");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.PANCAKE.get(), 1)
                .requires(Items.REDSTONE)
                .requires(ModItems.POWDER_EMERALD.get())
                .requires(Items.WHEAT)
                .requires(ModItems.BOLT_STEEL.get())
                .requires(ModItems.WIRE_COPPER.get())
                .requires(ModItems.PLATE_STEEL.get())
                .unlockedBy("has_steel", has(ModItems.PLATE_STEEL.get()))
                .save(pWriter, MODID + ":pancake_1");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHOCOLATE_MILK.get(), 1)
                .requires(ModItemTags.ANY_GLASS_PANES)
                .requires(Items.COCOA_BEANS)
                .requires(Items.MILK_BUCKET)
                .requires(FluidBucketIngredient.of(Fluids.NITROGLYCERIN.get()))
                .unlockedBy("has_glass_pane", has(ModItemTags.ANY_GLASS_PANES))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.CHOCOLATE_MILK.get(), 1)
                .requires(ModItemTags.ANY_GLASS_PANES)
                .requires(Items.COCOA_BEANS)
                .requires(Items.MILK_BUCKET)
                .requires(FluidTankIngredient.of(Fluids.NITROGLYCERIN.get()))
                .unlockedBy("has_glass_pane", has(ModItemTags.ANY_GLASS_PANES))
                .save(pWriter, MODID + ":chocolate_milk_tank");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.LOOPS.get(), 1)
                .requires(ModItems.FLAME_PONY.get())
                .requires(Items.WHEAT)
                .requires(Items.SUGAR)
                .unlockedBy("has_flame_pony", has(ModItems.FLAME_PONY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.LOOP_STEW.get(), 1)
                .requires(ModItems.LOOPS.get())
                .requires(ModItems.CAN_SMART.get())
                .requires(Items.BOWL)
                .unlockedBy("has_loops", has(ModItems.LOOPS.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COFFEE.get(), 1)
                .requires(ModItems.POWDER_COAL.get())
                .requires(Items.MILK_BUCKET)
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .unlockedBy("has_coal", has(ModItems.POWDER_COAL.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.COFFEE_RADIUM.get(), 1)
                .requires(ModItems.COFFEE.get())
                .requires(ModItems.NUGGET_RA_226.get())
                .unlockedBy("has_coffee", has(ModItems.COFFEE.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.INGOT_SMORE.get(), 1)
                .requires(Items.WHEAT)
                .requires(ModItems.MARSHMALLOW_ROASTED.get())
                .requires(Items.COCOA_BEANS)
                .unlockedBy("has_marshmallow_roasted", has(ModItems.MARSHMALLOW_ROASTED.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.MARSHMALLOW_NORMAL.get(), 1)
                .requires(Items.STICK)
                .requires(Items.SUGAR)
                .requires(Items.WHEAT_SEEDS)
                .unlockedBy("has_sugar", has(Items.SUGAR))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.FOOD, ModItems.QUESADILLA.get(), 3)
                .requires(ModItems.CHEESE.get())
                .requires(ModItems.CHEESE.get())
                .requires(Items.BREAD)
                .unlockedBy("has_cheese", has(ModItems.CHEESE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.PEAS.get(), 1)
                .pattern(" S ")
                .pattern("SNS")
                .pattern(" S ")
                .define('S', Items.WHEAT_SEEDS)
                .define('N', Items.GOLD_NUGGET)
                .unlockedBy("has_seeds", has(Items.WHEAT_SEEDS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CAN_EMPTY.get(), 1)
                .pattern("P")
                .pattern("P")
                .define('P', ModItems.PLATE_ALUMINIUM.get())
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CAN_SMART.get(), 1)
                .requires(ModItems.CAN_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(ModItems.NITER.get())
                .unlockedBy("has_can_empty", has(ModItems.CAN_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CAN_CREATURE.get(), 1)
                .requires(ModItems.CAN_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(FluidBucketIngredient.of(Fluids.DIESEL.get()))
                .unlockedBy("has_can_empty", has(ModItems.CAN_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CAN_CREATURE.get(), 1)
                .requires(ModItems.CAN_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(FluidTankIngredient.of(Fluids.DIESEL.get()))
                .unlockedBy("has_can_empty", has(ModItems.CAN_EMPTY.get()))
                .save(pWriter, MODID + ":can_creature_tank");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CAN_REDBOMB.get(), 1)
                .requires(ModItems.CAN_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(ModItems.PELLET_CLUSTER.get())
                .unlockedBy("has_can_empty", has(ModItems.CAN_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CAN_MRSUGAR.get(), 1)
                .requires(ModItems.CAN_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(ModItems.POWDER_FLUORITE.get())
                .unlockedBy("has_can_empty", has(ModItems.CAN_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CAN_OVERCHARGE.get(), 1)
                .requires(ModItems.CAN_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(ModItems.SULFUR.get())
                .unlockedBy("has_can_empty", has(ModItems.CAN_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CAN_LUNA.get(), 1)
                .requires(ModItems.CAN_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(ModItems.POWDER_METEORITE_TINY.get())
                .unlockedBy("has_can_empty", has(ModItems.CAN_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.MUCHO_MANGO.get(), 1)
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(Items.SUGAR)
                .requires(Items.ORANGE_DYE)
                .unlockedBy("has_sugar", has(Items.SUGAR))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CANTEEN_VODKA.get(), 1)
                .pattern("O")
                .pattern("P")
                .define('O', Items.POTATO)
                .define('P', ModItems.PLATE_STEEL.get())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOTTLE_EMPTY.get(), 6)
                .pattern(" G ")
                .pattern("G G")
                .pattern("GGG")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .unlockedBy("has_glass", has(ModItemTags.ANY_GLASS_PANES))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BOTTLE_NUKA.get(), 1)
                .requires(ModItems.BOTTLE_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(ModItems.POWDER_COAL.get())
                .unlockedBy("has_bottle_empty", has(ModItems.BOTTLE_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BOTTLE_CHERRY.get(), 1)
                .requires(ModItems.BOTTLE_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(Items.REDSTONE)
                .unlockedBy("has_bottle_empty", has(ModItems.BOTTLE_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BOTTLE_QUANTUM.get(), 1)
                .requires(ModItems.BOTTLE_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(ModItems.TRINITITE.get())
                .unlockedBy("has_bottle_empty", has(ModItems.BOTTLE_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BOTTLE_SPARKLE.get(), 1)
                .requires(ModItems.BOTTLE_NUKA.get())
                .requires(Items.CARROT)
                .requires(Items.GOLD_NUGGET)
                .unlockedBy("has_bottle_nuka", has(ModItems.BOTTLE_NUKA.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BOTTLE_RAD.get(), 1)
                .requires(ModItems.BOTTLE_QUANTUM.get())
                .requires(Items.CARROT)
                .requires(Items.GOLD_NUGGET)
                .unlockedBy("has_bottle_quantum", has(ModItems.BOTTLE_QUANTUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOTTLE2_EMPTY.get(), 6)
                .pattern(" G ")
                .pattern("G G")
                .pattern("G G")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .unlockedBy("has_glass", has(ModItemTags.ANY_GLASS_PANES))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BOTTLE2_KORL.get(), 1)
                .requires(ModItems.BOTTLE2_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(ModItems.POWDER_COPPER.get())
                .unlockedBy("has_bottle2_empty", has(ModItems.BOTTLE2_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BOTTLE2_FRITZ.get(), 1)
                .requires(ModItems.BOTTLE2_EMPTY.get())
                .requires(Items.POTION)
                .requires(Items.SUGAR)
                .requires(ModItems.POWDER_TUNGSTEN.get())
                .unlockedBy("has_bottle2_empty", has(ModItems.BOTTLE2_EMPTY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_EMPTY.get(), 6)
                .pattern("P")
                .pattern("C")
                .pattern("B")
                .define('P', ModItems.PLATE_IRON.get())
                .define('C', ModItems.CELL_EMPTY.get())
                .define('B', Items.IRON_BARS)
                .unlockedBy("has_iron_plate", has(ModItems.PLATE_IRON.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_ANTIDOTE.get(), 6)
                .pattern("SSS")
                .pattern("PMP")
                .pattern("SSS")
                .define('S', ModItems.SYRINGE_EMPTY.get())
                .define('P', Items.PUMPKIN_SEEDS)
                .define('M', Items.MILK_BUCKET)
                .unlockedBy("has_syringe_empty", has(ModItems.SYRINGE_EMPTY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_ANTIDOTE.get(), 6)
                .pattern("SPS")
                .pattern("SMS")
                .pattern("SPS")
                .define('S', ModItems.SYRINGE_EMPTY.get())
                .define('P', Items.PUMPKIN_SEEDS)
                .define('M', Items.MILK_BUCKET)
                .unlockedBy("has_syringe_empty", has(ModItems.SYRINGE_EMPTY.get()))
                .save(pWriter, MODID + ":syringe_antidote_1");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_ANTIDOTE.get(), 6)
                .pattern("SSS")
                .pattern("PMP")
                .pattern("SSS")
                .define('S', ModItems.SYRINGE_EMPTY.get())
                .define('P', Items.PUMPKIN_SEEDS)
                .define('M', Items.SUGAR_CANE)
                .unlockedBy("has_syringe_empty", has(ModItems.SYRINGE_EMPTY.get()))
                .save(pWriter, MODID + ":syringe_antidote_2");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_ANTIDOTE.get(), 6)
                .pattern("SPS")
                .pattern("SMS")
                .pattern("SPS")
                .define('S', ModItems.SYRINGE_EMPTY.get())
                .define('P', Items.PUMPKIN_SEEDS)
                .define('M', Items.SUGAR_CANE)
                .unlockedBy("has_syringe_empty", has(ModItems.SYRINGE_EMPTY.get()))
                .save(pWriter, MODID + ":syringe_antidote_3");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_POISON.get(), 1)
                .pattern("SLS")
                .pattern("LCL")
                .pattern("SLS")
                .define('C', ModItems.SYRINGE_EMPTY.get())
                .define('S', Items.SPIDER_EYE)
                .define('L', ModItems.POWDER_LEAD.get())
                .unlockedBy("has_syringe_empty", has(ModItems.SYRINGE_EMPTY.get()))
                .save(pWriter, MODID + ":syringe_poison_0");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_POISON.get(), 1)
                .pattern("SLS")
                .pattern("LCL")
                .pattern("SLS")
                .define('C', ModItems.SYRINGE_EMPTY.get())
                .define('S', Items.SPIDER_EYE)
                .define('L', ModItems.POWDER_POISON.get())
                .unlockedBy("has_syringe_empty", has(ModItems.SYRINGE_EMPTY.get()))
                .save(pWriter, MODID + ":syringe_poison_1");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_AWESOME.get(), 1)
                .pattern("SPS")
                .pattern("NCN")
                .pattern("SPS")
                .define('C', ModItems.SYRINGE_EMPTY.get())
                .define('S', ModItems.SULFUR.get())
                .define('P', ModItems.NUGGET_PU239.get())
                .define('N', ModItems.NUGGET_PU238.get())
                .unlockedBy("has_syringe_empty", has(ModItems.SYRINGE_EMPTY.get()))
                .save(pWriter, MODID + ":syringe_awesome_0");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_AWESOME.get(), 1)
                .pattern("SNS")
                .pattern("PCP")
                .pattern("SNS")
                .define('C', ModItems.SYRINGE_EMPTY.get())
                .define('S', ModItems.SULFUR.get())
                .define('P', ModItems.NUGGET_PU239.get())
                .define('N', ModItems.NUGGET_PU238.get())
                .unlockedBy("has_syringe_empty", has(ModItems.SYRINGE_EMPTY.get()))
                .save(pWriter, MODID + ":syringe_awesome_1");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_METAL_EMPTY.get(), 6)
                .pattern("P")
                .pattern("C")
                .pattern("B")
                .define('P', ModItems.PLATE_IRON.get())
                .define('C', ModItems.ROD_EMPTY.get())
                .define('B', Items.IRON_BARS)
                .unlockedBy("has_iron_plate", has(ModItems.PLATE_IRON.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_METAL_STIMPAK.get(), 1)
                .pattern(" N ")
                .pattern("NSN")
                .pattern(" N ")
                .define('N', Items.NETHER_WART)
                .define('S', ModItems.SYRINGE_METAL_EMPTY.get())
                .unlockedBy("has_syringe_metal_empty", has(ModItems.SYRINGE_METAL_EMPTY.get()))
                .save(pWriter, MODID + ":syringe_metal_stimpak_0");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SYRINGE_METAL_STIMPAK.get(), 1)
                .requires(ModItems.NITRA_SMALL.get())
                .requires(ModItems.NITRA_SMALL.get())
                .requires(ModItems.NITRA_SMALL.get())
                .requires(ModItems.SYRINGE_METAL_EMPTY.get())
                .unlockedBy("has_nitra_small", has(ModItems.NITRA_SMALL.get()))
                .save(pWriter, MODID + ":syringe_metal_stimpak_1");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_METAL_MEDX.get(), 1)
                .pattern(" N ")
                .pattern("NSN")
                .pattern(" N ")
                .define('N', Items.QUARTZ)
                .define('S', ModItems.SYRINGE_METAL_EMPTY.get())
                .unlockedBy("has_syringe_metal_empty", has(ModItems.SYRINGE_METAL_EMPTY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_METAL_PSYCHO.get(), 1)
                .pattern(" N ")
                .pattern("NSN")
                .pattern(" N ")
                .define('N', Items.GLOWSTONE_DUST)
                .define('S', ModItems.SYRINGE_METAL_EMPTY.get())
                .unlockedBy("has_syringe_metal_empty", has(ModItems.SYRINGE_METAL_EMPTY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_METAL_SUPER.get(), 1)
                .pattern(" N ")
                .pattern("PSP")
                .pattern("L L")
                .define('N', ModItems.BOTTLE_NUKA.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModItems.SYRINGE_METAL_STIMPAK.get())
                .define('L', Items.LEATHER)
                .unlockedBy("has_syringe_metal_stimpak", has(ModItems.SYRINGE_METAL_STIMPAK.get()))
                .save(pWriter, MODID + ":syringe_metal_super_0");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_METAL_SUPER.get(), 1)
                .pattern(" N ")
                .pattern("PSP")
                .pattern("L L")
                .define('N', ModItems.BOTTLE_NUKA.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModItems.SYRINGE_METAL_STIMPAK.get())
                .define('L', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_syringe_metal_stimpak", has(ModItems.SYRINGE_METAL_STIMPAK.get()))
                .save(pWriter, MODID + ":syringe_metal_super_1");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_METAL_SUPER.get(), 1)
                .pattern(" N ")
                .pattern("PSP")
                .pattern("L L")
                .define('N', ModItems.BOTTLE_CHERRY.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModItems.SYRINGE_METAL_STIMPAK.get())
                .define('L', Items.LEATHER)
                .unlockedBy("has_syringe_metal_stimpak", has(ModItems.SYRINGE_METAL_STIMPAK.get()))
                .save(pWriter, MODID + ":syringe_metal_super_2");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SYRINGE_METAL_SUPER.get(), 1)
                .pattern(" N ")
                .pattern("PSP")
                .pattern("L L")
                .define('N', ModItems.BOTTLE_CHERRY.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .define('S', ModItems.SYRINGE_METAL_STIMPAK.get())
                .define('L', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_syringe_metal_stimpak", has(ModItems.SYRINGE_METAL_STIMPAK.get()))
                .save(pWriter, MODID + ":syringe_metal_super_3");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SYRINGE_TAINT.get(), 1)
                .requires(ModItems.BOTTLE2_EMPTY.get())
                .requires(ModItems.SYRINGE_METAL_EMPTY.get())
                .requires(ModItems.DUCTTAPE.get())
                .requires(ModItems.POWDER_MAGIC.get())
                .requires(ModItems.NUGGET_SCHRABIDIUM.get())
                .requires(Items.POTION)
                .unlockedBy("has_syringe_metal_empty", has(ModItems.SYRINGE_METAL_EMPTY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PILL_IODINE.get(), 8)
                .pattern("IF")
                .define('I', ModItems.POWDER_IODINE.get())
                .define('F', ModItems.POWDER_FLUORITE.get())
                .unlockedBy("has_iodine", has(ModItems.POWDER_IODINE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLAN_C.get(), 1)
                .pattern("PFP")
                .define('P', ModItems.POWDER_POISON.get())
                .define('F', ModItems.POWDER_FLUORITE.get())
                .unlockedBy("has_poison", has(ModItems.POWDER_POISON.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RADX.get(), 1)
                .requires(ModItems.POWDER_COAL.get())
                .requires(ModItems.POWDER_COAL.get())
                .requires(ModItems.POWDER_FLUORITE.get())
                .unlockedBy("has_coal", has(ModItems.POWDER_COAL.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FMN.get(), 1)
                .requires(ModItems.POWDER_COAL.get())
                .requires(ModItems.POWDER_POLONIUM.get())
                .requires(ModItems.POWDER_STRONTIUM.get())
                .unlockedBy("has_polonium", has(ModItems.POWDER_POLONIUM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.FIVE_HTP.get(), 1)
                .requires(ModItems.POWDER_COAL.get())
                .requires(ModItems.POWDER_EUPHEMIUM.get())
                .requires(ModItems.CANTEEN_VODKA.get())
                .unlockedBy("has_euphemium", has(ModItems.POWDER_EUPHEMIUM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CIGARETTE.get(), 16)
                .requires(ModItems.INGOT_ASBESTOS.get())
                .requires(ModItemTags.ANY_TAR)
                .requires(ModItems.NUGGET_POLONIUM.get())
                .requires(ModItems.PLANT_TOBACCO.get())
                .unlockedBy("has_asbestos", has(ModItems.INGOT_ASBESTOS.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CRACKPIPE.get(), 1)
                .requires(ModItems.CATALYTIC_CONVERTER.get())
                .unlockedBy("has_catalytic_converter", has(ModItems.CATALYTIC_CONVERTER.get()))
                .save(pWriter);

        if(GeneralConfig.ENABLE_LBSM && GeneralConfig.ENABLE_LBSM_SIMPLE_MEDICINE_RECIPES) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SIOX.get(), 8)
                    .requires(ModItems.POWDER_COAL.get())
                    .requires(ModItems.POWDER_ASBESTOS.get())
                    .requires(Items.GOLD_NUGGET)
                    .unlockedBy("has_asbestos", has(ModItems.POWDER_ASBESTOS.get()))
                    .save(pWriter);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.XANAX.get(), 1)
                    .requires(ModItems.POWDER_COAL.get())
                    .requires(ModItems.NITER.get())
                    .requires(Items.QUARTZ)
                    .unlockedBy("has_quartz", has(Items.QUARTZ))
                    .save(pWriter);
        } else {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.SIOX.get(), 8)
                    .requires(ModItems.POWDER_COAL.get())
                    .requires(ModItems.POWDER_ASBESTOS.get())
                    .requires(ModItems.NUGGET_BISMUTH.get())
                    .unlockedBy("has_asbestos", has(ModItems.POWDER_ASBESTOS.get()))
                    .save(pWriter);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.XANAX.get(), 1)
                    .requires(ModItems.POWDER_COAL.get())
                    .requires(ModItems.NITER.get())
                    .requires(ModItems.POWDER_BROMINE.get())
                    .unlockedBy("has_bromine", has(ModItems.POWDER_BROMINE.get()))
                    .save(pWriter);
        }

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MED_BAG.get(), 1)
                .pattern("LLL")
                .pattern("SIS")
                .pattern("LLL")
                .define('L', Items.LEATHER)
                .define('S', ModItems.SYRINGE_METAL_STIMPAK.get())
                .define('I', ModItems.SYRINGE_ANTIDOTE.get())
                .unlockedBy("has_syringe_metal_stimpak", has(ModItems.SYRINGE_METAL_STIMPAK.get()))
                .save(pWriter, MODID + ":med_bag_0");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MED_BAG.get(), 1)
                .pattern("LLL")
                .pattern("SIS")
                .pattern("LLL")
                .define('L', Items.LEATHER)
                .define('S', ModItems.SYRINGE_METAL_STIMPAK.get())
                .define('I', ModItems.PILL_IODINE.get())
                .unlockedBy("has_syringe_metal_stimpak", has(ModItems.SYRINGE_METAL_STIMPAK.get()))
                .save(pWriter, MODID + ":med_bag_1");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MED_BAG.get(), 1)
                .pattern("LL")
                .pattern("SI")
                .pattern("LL")
                .define('L', Items.LEATHER)
                .define('S', ModItems.SYRINGE_METAL_SUPER.get())
                .define('I', ModItems.RADAWAY.get())
                .unlockedBy("has_syringe_metal_super", has(ModItems.SYRINGE_METAL_SUPER.get()))
                .save(pWriter, MODID + ":med_bag_2");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MED_BAG.get(), 1)
                .pattern("LLL")
                .pattern("SIS")
                .pattern("LLL")
                .define('L', ModItemTags.ANY_RUBBER_INGOT)
                .define('S', ModItems.SYRINGE_METAL_STIMPAK.get())
                .define('I', ModItems.SYRINGE_ANTIDOTE.get())
                .unlockedBy("has_syringe_metal_stimpak", has(ModItems.SYRINGE_METAL_STIMPAK.get()))
                .save(pWriter, MODID + ":med_bag_3");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MED_BAG.get(), 1)
                .pattern("LLL")
                .pattern("SIS")
                .pattern("LLL")
                .define('L', ModItemTags.ANY_RUBBER_INGOT)
                .define('S', ModItems.SYRINGE_METAL_STIMPAK.get())
                .define('I', ModItems.PILL_IODINE.get())
                .unlockedBy("has_syringe_metal_stimpak", has(ModItems.SYRINGE_METAL_STIMPAK.get()))
                .save(pWriter, MODID + ":med_bag_4");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MED_BAG.get(), 1)
                .pattern("LL")
                .pattern("SI")
                .pattern("LL")
                .define('L', ModItemTags.ANY_RUBBER_INGOT)
                .define('S', ModItems.SYRINGE_METAL_SUPER.get())
                .define('I', ModItems.RADAWAY.get())
                .unlockedBy("has_syringe_metal_super", has(ModItems.SYRINGE_METAL_SUPER.get()))
                .save(pWriter, MODID + ":med_bag_5");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.IV_EMPTY.get(), 4)
                .pattern("S")
                .pattern("I")
                .pattern("S")
                .define('S', ModItemTags.ANY_RUBBER_INGOT)
                .define('I', ModItems.PLATE_IRON.get())
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.IV_XP_EMPTY.get(), 1)
                .requires(ModItems.IV_EMPTY.get())
                .requires(ModItems.POWDER_MAGIC.get())
                .unlockedBy("has_iv_empty", has(ModItems.IV_EMPTY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RADAWAY.get(), 1)
                .requires(ModItems.IV_BLOOD.get())
                .requires(ModItems.POWDER_COAL.get())
                .requires(Items.PUMPKIN_SEEDS)
                .unlockedBy("has_iv_blood", has(ModItems.IV_BLOOD.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RADAWAY_STRONG.get(), 1)
                .requires(ModItems.RADAWAY.get())
                .requires(ModBlocks.MUSH.get().asItem())
                .unlockedBy("has_radaway", has(ModItems.RADAWAY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RADAWAY_FLUSH.get(), 1)
                .requires(ModItems.RADAWAY_STRONG.get())
                .requires(ModItems.POWDER_IODINE.get())
                .unlockedBy("has_radaway_strong", has(ModItems.RADAWAY_STRONG.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.CLADDING_PAINT.get(), 1)
                .requires(ModItems.NUGGET_LEAD.get())
                .requires(ModItems.NUGGET_LEAD.get())
                .requires(ModItems.NUGGET_LEAD.get())
                .requires(ModItems.NUGGET_LEAD.get())
                .requires(Items.CLAY_BALL)
                .requires(Items.GLASS_BOTTLE)
                .unlockedBy("has_lead", has(ModItems.NUGGET_LEAD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CLADDING_RUBBER.get(), 1)
                .pattern("RCR")
                .pattern("CDC")
                .pattern("RCR")
                .define('R', ModItemTags.ANY_RUBBER_INGOT)
                .define('C', ModItems.POWDER_COAL.get())
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CLADDING_LEAD.get(), 1)
                .pattern("DPD")
                .pattern("PRP")
                .pattern("DPD")
                .define('R', ModItems.CLADDING_RUBBER.get())
                .define('P', ModItems.PLATE_LEAD.get())
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_cladding_rubber", has(ModItems.CLADDING_RUBBER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CLADDING_DESH.get(), 1)
                .pattern("DPD")
                .pattern("PRP")
                .pattern("DPD")
                .define('R', ModItems.CLADDING_LEAD.get())
                .define('P', ModItems.PLATE_DESH.get())
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_cladding_lead", has(ModItems.CLADDING_LEAD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CLADDING_GHIORSIUM.get(), 1)
                .pattern("DPD")
                .pattern("PRP")
                .pattern("DPD")
                .define('R', ModItems.CLADDING_DESH.get())
                .define('P', ModItems.INGOT_GH336.get())
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_cladding_desh", has(ModItems.CLADDING_DESH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CLADDING_OBSIDIAN.get(), 1)
                .pattern("OOO")
                .pattern("PDP")
                .pattern("OOO")
                .define('O', Blocks.OBSIDIAN)
                .define('P', ModItems.PLATE_STEEL.get())
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_obsidian", has(Blocks.OBSIDIAN))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CLADDING_IRON.get(), 1)
                .pattern("OOO")
                .pattern("PDP")
                .pattern("OOO")
                .define('O', ModItems.PLATE_IRON.get())
                .define('P', ModItems.PLATE_POLYMER.get())
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_iron_plate", has(ModItems.PLATE_IRON.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_STEEL.get(), 1)
                .pattern("DPD")
                .pattern("PSP")
                .pattern("DPD")
                .define('D', ModItems.DUCTTAPE.get())
                .define('P', ModItems.PLATE_IRON.get())
                .define('S', ModBlocks.BLOCK_STEEL.get().asItem())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_DU.get(), 1)
                .pattern("DPD")
                .pattern("PSP")
                .pattern("DPD")
                .define('D', ModItems.DUCTTAPE.get())
                .define('P', ModItems.PLATE_IRON.get())
                .define('S', ModBlocks.BLOCK_U238.get().asItem())
                .unlockedBy("has_u238", has(ModBlocks.BLOCK_U238.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_GHIORSIUM.get(), 1)
                .pattern("DPD")
                .pattern("PSP")
                .pattern("DPD")
                .define('D', ModItems.DUCTTAPE.get())
                .define('P', ModItems.INGOT_GH336.get())
                .define('S', ModItems.INGOT_U238.get())
                .unlockedBy("has_gh336", has(ModItems.INGOT_GH336.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_POLONIUM.get(), 1)
                .pattern("DPD")
                .pattern("PSP")
                .pattern("DPD")
                .define('D', ModItems.DUCTTAPE.get())
                .define('P', ModItems.PLATE_IRON.get())
                .define('S', ModBlocks.BLOCK_POLONIUM.get().asItem())
                .unlockedBy("has_polonium", has(ModBlocks.BLOCK_POLONIUM.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_ERA.get(), 1)
                .pattern("DPD")
                .pattern("PSP")
                .pattern("DPD")
                .define('D', ModItems.DUCTTAPE.get())
                .define('P', ModItems.PLATE_IRON.get())
                .define('S', ModItems.INGOT_SEMTEX.get())
                .unlockedBy("has_semtex", has(ModItems.INGOT_SEMTEX.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_KEVLAR.get(), 1)
                .pattern("KIK")
                .pattern("IDI")
                .pattern("KIK")
                .define('K', ModItems.PLATE_KEVLAR.get())
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_kevlar", has(ModItems.PLATE_KEVLAR.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_SAPI.get(), 1)
                .pattern("PKP")
                .pattern("DPD")
                .pattern("PKP")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('K', ModItems.INSERT_KEVLAR.get())
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_insert_kevlar", has(ModItems.INSERT_KEVLAR.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_ESAPI.get(), 1)
                .pattern("PKP")
                .pattern("DSD")
                .pattern("PKP")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('K', ModItems.INSERT_SAPI.get())
                .define('D', ModItems.DUCTTAPE.get())
                .define('S', ModItems.PLATE_WEAPON_STEEL.get())
                .unlockedBy("has_insert_sapi", has(ModItems.INSERT_SAPI.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_XSAPI.get(), 1)
                .pattern("PKP")
                .pattern("DSD")
                .pattern("PKP")
                .define('P', ModItems.INGOT_ASBESTOS.get())
                .define('K', ModItems.INSERT_ESAPI.get())
                .define('D', ModItems.DUCTTAPE.get())
                .define('S', ModItems.PLATE_SATURNITE.get())
                .unlockedBy("has_insert_esapi", has(ModItems.INSERT_ESAPI.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INSERT_YHARONITE.get(), 1)
                .pattern("YIY")
                .pattern("IYI")
                .pattern("YIY")
                .define('Y', ModItems.BILLET_YHARONITE.get())
                .define('I', ModItems.INSERT_DU.get())
                .unlockedBy("has_billet_yharonite", has(ModItems.BILLET_YHARONITE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AUSTRALIUM_III.get(), 1)
                .pattern("WSW")
                .pattern("PAP")
                .pattern("SPS")
                .define('S', ModItems.PLATE_WELDED_STEEL.get())
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('A', ModItems.INGOT_AUSTRALIUM.get())
                .define('W', ModItems.WIRE_DENSE_GOLD.get())
                .unlockedBy("has_australium", has(ModItems.INGOT_AUSTRALIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SERVO_SET.get(), 1)
                .pattern("MBM")
                .pattern("PBP")
                .pattern("MBM")
                .define('M', ModItems.MOTOR.get())
                .define('B', ModItems.BOLT_STEEL.get())
                .define('P', ModItems.PLATE_IRON.get())
                .unlockedBy("has_motor", has(ModItems.MOTOR.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SERVO_SET_DESH.get(), 1)
                .pattern("MBM")
                .pattern("PSP")
                .pattern("MBM")
                .define('M', ModItems.MOTOR_DESH.get())
                .define('B', ModItems.BOLT_DURA_STEEL.get())
                .define('P', ModItems.PLATE_ADVANCED_ALLOY.get())
                .define('S', ModItems.SERVO_SET.get())
                .unlockedBy("has_servo_set", has(ModItems.SERVO_SET.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ATTACHMENT_MASK.get(), 1)
                .pattern("DID")
                .pattern("IGI")
                .pattern(" F ")
                .define('D', ModItems.DUCTTAPE.get())
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('F', ModItems.PLATE_IRON.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.ATTACHMENT_MASK_MONO.get(), 1)
                .pattern(" D ")
                .pattern("DID")
                .pattern(" F ")
                .define('D', ModItems.DUCTTAPE.get())
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('F', ModItems.PLATE_IRON.get())
                .unlockedBy("has_ducttape", has(ModItems.DUCTTAPE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PADS_RUBBER.get(), 1)
                .pattern("P P")
                .pattern("IDI")
                .pattern("P P")
                .define('P', ModItemTags.ANY_RUBBER_INGOT)
                .define('I', ModItems.PLATE_IRON.get())
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PADS_SLIME.get(), 1)
                .pattern("SPS")
                .pattern("DSD")
                .pattern("SPS")
                .define('S', Items.SLIME_BALL)
                .define('P', ModItems.PADS_RUBBER.get())
                .define('D', ModItems.DUCTTAPE.get())
                .unlockedBy("has_pads_rubber", has(ModItems.PADS_RUBBER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PADS_STATIC.get(), 1)
                .pattern("CDC")
                .pattern("ISI")
                .pattern("CDC")
                .define('C', Items.COPPER_INGOT)
                .define('D', ModItems.DUCTTAPE.get())
                .define('I', ModItemTags.ANY_RUBBER_INGOT)
                .define('S', ModItems.PADS_SLIME.get())
                .unlockedBy("has_pads_slime", has(ModItems.PADS_SLIME.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ARMOR_BATTERY.get(), 1)
                .pattern("PCP")
                .pattern("PCP")
                .pattern("PCP")
                .define('P', ModItems.PLATE_STEEL.get())
                .define('C', ModBlocks.CAPACITOR_GOLD.get().asItem())
                .unlockedBy("has_capacitor_gold", has(ModBlocks.CAPACITOR_GOLD.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ARMOR_BATTERY_MK2.get(), 1)
                .pattern("PCP")
                .pattern("PCP")
                .pattern("PCP")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('C', ModBlocks.CAPACITOR_NIOBIUM.get().asItem())
                .unlockedBy("has_capacitor_niobium", has(ModBlocks.CAPACITOR_NIOBIUM.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.ARMOR_BATTERY_MK3.get(), 1)
                .pattern("PCP")
                .pattern("PCP")
                .pattern("PCP")
                .define('P', ModItems.PLATE_GOLD.get())
                .define('C', ModBlocks.CAPACITOR_TANTALIUM.get().asItem())
                .unlockedBy("has_capacitor_tantalium", has(ModBlocks.CAPACITOR_TANTALIUM.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HORSESHOE_MAGNET.get(), 1)
                .pattern("L L")
                .pattern("I I")
                .pattern("ILI")
                .define('L', ModItems.LODESTONE.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_lodestone", has(ModItems.LODESTONE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INDUSTRIAL_MAGNET.get(), 1)
                .pattern("SMS")
                .pattern(" B ")
                .pattern("SMS")
                .define('S', ModItems.INGOT_STEEL.get())
                .define('M', ModItems.HORSESHOE_MAGNET.get())
                .define('B', ModItems.FUSION_CONDUCTOR.get())
                .unlockedBy("has_horseshoe_magnet", has(ModItems.HORSESHOE_MAGNET.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEART_CONTAINER.get(), 1)
                .pattern("HAH")
                .pattern("ACA")
                .pattern("HAH")
                .define('H', ModItems.HEART_PIECE.get())
                .define('A', ModItems.INGOT_ALUMINIUM.get())
                .define('C', ModItems.COIN_CREEPER.get())
                .unlockedBy("has_heart_piece", has(ModItems.HEART_PIECE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEART_BOOSTER.get(), 1)
                .pattern("GHG")
                .pattern("MCM")
                .pattern("GHG")
                .define('G', Items.GOLD_INGOT)
                .define('H', ModItems.HEART_CONTAINER.get())
                .define('M', ModItems.MORNING_GLORY.get())
                .define('C', ModItems.COIN_MASKMAN.get())
                .unlockedBy("has_heart_container", has(ModItems.HEART_CONTAINER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.HEART_FAB.get(), 1)
                .pattern("GHG")
                .pattern("MCM")
                .pattern("GHG")
                .define('G', ModItems.BILLET_POLONIUM.get())
                .define('H', ModItems.HEART_BOOSTER.get())
                .define('M', ModItems.COKE.get())
                .define('C', ModItems.COIN_WORM.get())
                .unlockedBy("has_heart_booster", has(ModItems.HEART_BOOSTER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INK.get(), 1)
                .pattern("FPF")
                .pattern("PIP")
                .pattern("FPF")
                .define('F', Items.RED_TULIP)
                .define('P', ModItems.ARMOR_POLISH.get())
                .define('I', Items.BLACK_DYE)
                .unlockedBy("has_armor_polish", has(ModItems.ARMOR_POLISH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BATHWATER_MK2.get(), 1)
                .pattern("MWM")
                .pattern("WBW")
                .pattern("MWM")
                .define('M', ModItems.BOTTLE_MERCURY.get())
                .define('W', ModItems.NUCLEAR_WASTE.get())
                .define('B', ModItems.BATHWATER.get())
                .unlockedBy("has_bathwater", has(ModItems.BATHWATER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModArmorItems.BACK_TESLA.get(), 1)
                .pattern("DGD")
                .pattern("GTG")
                .pattern("DGD")
                .define('D', ModItems.DUCTTAPE.get())
                .define('G', ModItems.WIRE_GOLD.get())
                .define('T', ModBlocks.TESLA.get().asItem())
                .unlockedBy("has_tesla", has(ModBlocks.TESLA.get().asItem()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MEDAL_LIQUIDATOR.get(), 1)
                .pattern("GBG")
                .pattern("BFB")
                .pattern("GBG")
                .define('G', ModItems.NUGGET_AU198.get())
                .define('B', ModItems.INGOT_BORON.get())
                .define('F', ModItems.DEBRIS_FUEL.get())
                .unlockedBy("has_debris_fuel", has(ModItems.DEBRIS_FUEL.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INJECTOR_5HTP.get(), 1)
                .requires(ModItems.FIVE_HTP.get())
                .requires(ModItems.CIRCUIT_BASIC.get())
                .requires(ModItems.PLATE_SATURNITE.get())
                .unlockedBy("has_five_htp", has(ModItems.FIVE_HTP.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INJECTOR_KNIFE.get(), 1)
                .requires(ModItems.INJECTOR_5HTP.get())
                .requires(Items.IRON_SWORD)
                .unlockedBy("has_injector_5htp", has(ModItems.INJECTOR_5HTP.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SHACKLES.get(), 1)
                .pattern("CIC")
                .pattern("C C")
                .pattern("I I")
                .define('I', ModItems.INGOT_CHAINSTEEL.get())
                .define('C', ModBlocks.STEEL_CHAIN.get().asItem())
                .unlockedBy("has_chainsteel", has(ModItems.INGOT_CHAINSTEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BLACK_DIAMOND.get(), 1)
                .pattern("NIN")
                .pattern("IGI")
                .pattern("NIN")
                .define('N', ModItems.NUGGET_AU198.get())
                .define('I', ModItems.INK.get())
                .define('G', ModItems.GEM_VOLCANIC.get())
                .unlockedBy("has_ink", has(ModItems.INK.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PROTECTION_CHARM.get(), 1)
                .pattern(" M ")
                .pattern("MDM")
                .pattern(" M ")
                .define('M', ModItems.FRAGMENT_METEORITE.get())
                .define('D', Items.DIAMOND)
                .unlockedBy("has_fragment_meteorite", has(ModItems.FRAGMENT_METEORITE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.METEOR_CHARM.get(), 1)
                .pattern(" M ")
                .pattern("MDM")
                .pattern(" M ")
                .define('M', ModItems.FRAGMENT_METEORITE.get())
                .define('D', ModItems.GEM_VOLCANIC.get())
                .unlockedBy("has_fragment_meteorite", has(ModItems.FRAGMENT_METEORITE.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NEUTRINO_LENS.get(), 1)
                .pattern("PSP")
                .pattern("SCS")
                .pattern("PSP")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('S', ModItems.INGOT_STARMETAL.get())
                .define('C', ModItems.CIRCUIT_BISMOID.get())
                .unlockedBy("has_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GAS_TESTER.get(), 1)
                .pattern("G")
                .pattern("C")
                .pattern("I")
                .define('G', ModItems.PLATE_GOLD.get())
                .define('C', ModItems.CIRCUIT_VACUUM_TUBE.get())
                .define('I', ModItems.PLATE_IRON.get())
                .unlockedBy("has_gold_plate", has(ModItems.PLATE_GOLD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DEFUSER_GOLD.get(), 1)
                .pattern("GPG")
                .pattern("PRP")
                .pattern("GPG")
                .define('G', Items.GUNPOWDER)
                .define('P', ModItems.PLATE_GOLD.get())
                .define('R', Items.MUSIC_DISC_13)
                .unlockedBy("has_gold_plate", has(ModItems.PLATE_GOLD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BALLISTIC_GAUNTLET.get(), 1)
                .pattern(" WS")
                .pattern("WRS")
                .pattern(" RS")
                .define('W', ModItems.WIRE_COPPER.get())
                .define('R', ModItems.RING_STARMETAL.get())
                .define('S', ModItems.PLATE_STEEL.get())
                .unlockedBy("has_ring_starmetal", has(ModItems.RING_STARMETAL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NIGHT_VISION.get(), 1)
                .pattern("P P")
                .pattern("GCG")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('G', ModItemTags.ANY_GLASS_BLOCKS)
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .unlockedBy("has_circuit_basic", has(ModItems.CIRCUIT_BASIC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.STEALTH_BOY.get(), 1)
                .pattern(" B")
                .pattern("LI")
                .pattern("LC")
                .define('B', Blocks.STONE_BUTTON)
                .define('L', Items.LEATHER)
                .define('I', ModItems.INGOT_STEEL.get())
                .define('C', ModItems.CIRCUIT_BASIC.get())
                .unlockedBy("has_circuit_basic", has(ModItems.CIRCUIT_BASIC.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GAS_MASK_FILTER.get(), 1)
                .pattern("I")
                .pattern("F")
                .define('I', ModItems.PLATE_IRON.get())
                .define('F', ModItems.FILTER_COAL.get())
                .unlockedBy("has_filter_coal", has(ModItems.FILTER_COAL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GAS_MASK_FILTER_MONO.get(), 1)
                .pattern("ZZZ")
                .pattern("ZCZ")
                .pattern("ZZZ")
                .define('Z', ModItems.NUGGET_ZIRCONIUM.get())
                .define('C', ModItems.CATALYST_CLAY.get())
                .unlockedBy("has_zirconium", has(ModItems.NUGGET_ZIRCONIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GAS_MASK_FILTER_COMBO.get(), 1)
                .pattern("ZCZ")
                .pattern("CFC")
                .pattern("ZCZ")
                .define('Z', ModItems.INGOT_ZIRCONIUM.get())
                .define('C', ModItems.CATALYST_CLAY.get())
                .define('F', ModItems.GAS_MASK_FILTER.get())
                .unlockedBy("has_gas_mask_filter", has(ModItems.GAS_MASK_FILTER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GAS_MASK_FILTER_RAG.get(), 1)
                .pattern("I")
                .pattern("F")
                .define('I', ModItems.PLATE_IRON.get())
                .define('F', ModArmorItems.MASK_WET.get())
                .unlockedBy("has_mask_wet", has(ModArmorItems.MASK_WET.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.COMBAT, ModItems.GAS_MASK_FILTER_PISS.get(), 1)
                .pattern("I")
                .pattern("F")
                .define('I', ModItems.PLATE_IRON.get())
                .define('F', ModArmorItems.MASK_PISS.get())
                .unlockedBy("has_mask_piss", has(ModArmorItems.MASK_PISS.get()))
                .save(pWriter);
    }
}