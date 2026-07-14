package com.hbm.datagen.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.*;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;

import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;

import static com.hbm.util.RefStrings.MODID;

public class ModRecipeProvider extends RecipeProvider implements IConditionBuilder {


    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    @Override
    protected void buildRecipes(@NotNull Consumer<FinishedRecipe> pWriter) {
        // Генерация рецептов плавки руд
        generateOreSmeltingRecipes(pWriter);

        // Генерация рецептов плавки порошков в слитки
        generateSmeltingRecipes(pWriter);

        // Генерация обычных рецептов
        generateCraftingRecipes(pWriter);


        generateColoredConcreteRecipes(pWriter);

        WeaponRecipes.generateWeaponRecipes(pWriter);
        ArmorRecipes.generateArmorRecipes(pWriter);
        ConsumableRecipes.generateConsumableRecipes(pWriter);
        PowderRecipes.generatePowderRecipes(pWriter);
        MineralRecipes.generateMineralRecipes(pWriter);
        ToolRecipes.generateToolRecipes(pWriter);

    }

    private void generateOreSmeltingRecipes(Consumer<FinishedRecipe> pWriter) {
        // Рецепты для блоков руд
        oreSmelting(pWriter, ModBlocks.ORE_ALUMINIUM.get(), ModItems.INGOT_ALUMINIUM.get(), 0.5f, 200, "ingot_aluminium");
        oreSmelting(pWriter, ModBlocks.ORE_TITANIUM.get(), ModItems.INGOT_TITANIUM.get(), 0.5f, 200, "ingot_titanium");
        oreSmelting(pWriter, ModBlocks.ORE_TUNGSTEN.get(), ModItems.INGOT_TUNGSTEN.get(), 0.5f, 200, "ingot_tungsten");
        oreSmelting(pWriter, ModBlocks.ORE_LEAD.get(), ModItems.INGOT_LEAD.get(), 0.5f, 200, "ingot_lead");
        oreSmelting(pWriter, ModBlocks.ORE_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), 0.5f, 200, "ingot_beryllium");
        oreSmelting(pWriter, ModBlocks.ORE_COBALT.get(), ModItems.INGOT_COBALT.get(), 0.5f, 200, "ingot_cobalt");
        oreSmelting(pWriter, ModBlocks.ORE_ZINC.get(), ModItems.INGOT_ZINC.get(), 0.5f, 200, "ingot_zinc");
        oreSmelting(pWriter, ModBlocks.ORE_URANIUM.get(), ModItems.INGOT_URANIUM.get(), 0.5f, 200, "ingot_uranium");
        oreSmelting(pWriter, ModBlocks.ORE_NETHER_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), 0.5f, 200, "ingot_plutonium");
        oreSmelting(pWriter, ModBlocks.ORE_THORIUM.get(), ModItems.INGOT_THORIUM.get(), 0.5f, 200, "ingot_thorium");


        // Рецепты для предметов руд (сырая руда)
        oreSmelting(pWriter, ModItems.RAW_ALUMINIUM.get(), ModItems.INGOT_ALUMINIUM.get(), 0.5f, 200, "ingot_aluminium");
        oreSmelting(pWriter, ModItems.RAW_TITANIUM.get(), ModItems.INGOT_TITANIUM.get(), 0.5f, 200, "ingot_titanium");
        oreSmelting(pWriter, ModItems.RAW_TUNGSTEN.get(), ModItems.INGOT_TUNGSTEN.get(), 0.5f, 200, "ingot_tungsten");
        oreSmelting(pWriter, ModItems.RAW_LEAD.get(), ModItems.INGOT_LEAD.get(), 0.5f, 200, "ingot_lead");
        oreSmelting(pWriter, ModItems.RAW_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), 0.5f, 200, "ingot_beryllium");
        oreSmelting(pWriter, ModItems.RAW_COBALT.get(), ModItems.INGOT_COBALT.get(), 0.5f, 200, "ingot_cobalt");
        oreSmelting(pWriter, ModItems.RAW_ZINC.get(), ModItems.INGOT_ZINC.get(), 0.5f, 200, "ingot_zinc");
        oreSmelting(pWriter, ModItems.RAW_URANIUM.get(), ModItems.INGOT_URANIUM.get(), 0.5f, 200, "ingot_uranium");
        oreSmelting(pWriter, ModItems.RAW_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), 0.5f, 200, "ingot_plutonium");
        oreSmelting(pWriter, ModItems.RAW_THORIUM.get(), ModItems.INGOT_THORIUM.get(), 0.5f, 200, "ingot_thorium");
    }

    private void generateSmeltingRecipes(Consumer<FinishedRecipe> pWriter) {
        // Рецепты плавки порошков в слитки
        powderSmelting(pWriter, ModItems.POWDER_ALUMINIUM.get(), ModItems.INGOT_ALUMINIUM.get(), 0.5f, 200, "ingot_aluminium");
        powderSmelting(pWriter, ModItems.POWDER_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), 0.5f, 200, "ingot_beryllium");
        powderSmelting(pWriter, ModItems.POWDER_COBALT.get(), ModItems.INGOT_COBALT.get(), 0.5f, 200, "ingot_cobalt");
        powderSmelting(pWriter, ModItems.POWDER_COPPER.get(), Items.COPPER_INGOT, 0.5f, 200, "ingot_copper");
        powderSmelting(pWriter, ModItems.POWDER_GOLD.get(), Items.GOLD_INGOT, 0.5f, 200, "ingot_gold");
        powderSmelting(pWriter, ModItems.POWDER_IRON.get(), Items.IRON_INGOT, 0.5f, 200, "ingot_steel");
        powderSmelting(pWriter, ModItems.POWDER_LEAD.get(), ModItems.INGOT_LEAD.get(), 0.5f, 200, "ingot_lead");
        powderSmelting(pWriter, ModItems.POWDER_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), 0.5f, 200, "ingot_plutonium");
        powderSmelting(pWriter, ModItems.POWDER_THORIUM.get(), ModItems.INGOT_THORIUM.get(), 0.5f, 200, "ingot_thorium");
        powderSmelting(pWriter, ModItems.POWDER_TITANIUM.get(), ModItems.INGOT_TITANIUM.get(), 0.5f, 200, "ingot_titanium");
        powderSmelting(pWriter, ModItems.POWDER_TUNGSTEN.get(), ModItems.INGOT_TUNGSTEN.get(), 0.5f, 200, "ingot_tungsten");
        powderSmelting(pWriter, ModItems.POWDER_MAGNETIZED_TUNGSTEN.get(), ModItems.INGOT_MAGNETIZED_TUNGSTEN.get(), 0.5f, 200, "ingot_magnetized_tungsten");
        powderSmelting(pWriter, ModItems.POWDER_URANIUM.get(), ModItems.INGOT_URANIUM.get(), 0.5f, 200, "ingot_uranium");
        powderSmelting(pWriter, ModItems.POWDER_ZINC.get(), ModItems.INGOT_ZINC.get(), 0.5f, 200, "ingot_zinc");
        powderSmelting(pWriter, ModItems.BALL_FIRECLAY.get(), ModItems.INGOT_FIREBRICK.get(), 0.5f, 200, "ingot_firebrick");

        // Прочее
        oreSmelting(pWriter, ModItems.BALL_RESIN.get(), ModItems.INGOT_BIORUBBER.get(), 0.5f, 200, "ingot_biorubber");
        oreSmelting(pWriter, ModItems.CRYSTAL_ALUMINIUM.get(), ModItems.INGOT_ALUMINIUM.get(), 0.5f, 200, "aluminium_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), 0.5f, 200, "beryllium_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_COBALT.get(), ModItems.INGOT_COBALT.get(), 0.5f, 200, "cobalt_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_COPPER.get(), Items.COPPER_INGOT, 0.5f, 200, "copper_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_GOLD.get(), Items.GOLD_INGOT, 0.5f, 200, "gold_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_IRON.get(), Items.IRON_INGOT, 0.5f, 200, "iron_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_LEAD.get(), ModItems.INGOT_LEAD.get(), 0.5f, 200, "lead_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_OSMIRIDIUM.get(), ModItems.INGOT_OSMIRIDIUM.get(), 0.5f, 200, "osmiridium_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), 0.5f, 200, "plutonium_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_SCHRABIDIUM.get(), ModItems.INGOT_SCHRABIDIUM.get(), 0.5f, 200, "schrabidium_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_SCHRARANIUM.get(), ModItems.INGOT_SCHRARANIUM.get(), 0.5f, 200, "schraranium_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_STARMETAL.get(), ModItems.INGOT_STARMETAL.get(), 0.5f, 200, "starmetal_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_THORIUM.get(), ModItems.INGOT_THORIUM.get(), 0.5f, 200, "thorium_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_TITANIUM.get(), ModItems.INGOT_TITANIUM.get(), 0.5f, 200, "titanium_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_TUNGSTEN.get(), ModItems.INGOT_TUNGSTEN.get(), 0.5f, 200, "tungsten_ingot");
        oreSmelting(pWriter, ModItems.CRYSTAL_URANIUM.get(), ModItems.INGOT_URANIUM.get(), 0.5f, 200, "uranium_ingot");

    }

    private void generateColoredConcreteRecipes(Consumer<FinishedRecipe> pWriter) {
        String[] colors = {"white", "orange", "magenta", "light_blue", "yellow", "lime",
                "pink", "gray", "light_gray", "cyan", "purple", "blue", "brown",
                "green", "red", "black"};

        Item[] dyes = {
                Items.WHITE_DYE, Items.ORANGE_DYE, Items.MAGENTA_DYE, Items.LIGHT_BLUE_DYE,
                Items.YELLOW_DYE, Items.LIME_DYE, Items.PINK_DYE, Items.GRAY_DYE,
                Items.LIGHT_GRAY_DYE, Items.CYAN_DYE, Items.PURPLE_DYE, Items.BLUE_DYE,
                Items.BROWN_DYE, Items.GREEN_DYE, Items.RED_DYE, Items.BLACK_DYE
        };

        for (int i = 0; i < colors.length; i++) {
            String color = colors[i];
            Item dye = dyes[i];

            // Рецепт: 8 бетонных блоков + краситель = 8 покрашенных бетонных блоков
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CONCRETE_COLORED.get(), 8)
                    .requires(ModBlocks.CONCRETE.get(), 8)
                    .requires(dye)
                    .unlockedBy("has_concrete", has(ModBlocks.CONCRETE.get()))
                    .save(pWriter, MODID + ":concrete_colored_" + color);

            // Рецепт перекрашивания: 1 покрашенный бетон + краситель = 1 покрашенный бетон нового цвета
            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CONCRETE_COLORED.get(), 1)
                    .requires(ModBlocks.CONCRETE_COLORED.get())
                    .requires(dye)
                    .unlockedBy("has_concrete_colored", has(ModBlocks.CONCRETE_COLORED.get()))
                    .save(pWriter, MODID + ":concrete_recolor_to_" + color);
        }

        // Рецепты для concrete_colored_ext (специальные текстуры)
        String[] extTypes = {"machine", "machine_stripe", "indigo", "purple", "pink", "hazard", "sand", "bronze"};
        Item[] extDyes = {
                Items.GRAY_DYE, Items.LIGHT_GRAY_DYE, Items.BLUE_DYE, Items.PURPLE_DYE,
                Items.PINK_DYE, Items.ORANGE_DYE, Items.YELLOW_DYE, Items.BROWN_DYE
        };

        for (int i = 0; i < extTypes.length; i++) {
            String type = extTypes[i];
            Item dye = extDyes[i];

            ShapelessRecipeBuilder.shapeless(RecipeCategory.BUILDING_BLOCKS, ModBlocks.CONCRETE_COLORED_EXT.get(), 8)
                    .requires(ModBlocks.CONCRETE.get(), 8)
                    .requires(dye)
                    .unlockedBy("has_concrete", has(ModBlocks.CONCRETE.get()))
                    .save(pWriter, MODID + ":concrete_colored_ext_" + type);
        }
    }

    protected static void oreSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient,
                                      ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredient,
                pResult, pExperience, pCookingTime, pGroup, "_from_smelting");
        oreCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredient,
                pResult, pExperience, pCookingTime / 2, pGroup, "_from_blasting");
    }

    protected static void powderSmelting(Consumer<FinishedRecipe> pFinishedRecipeConsumer, ItemLike pIngredient,
                                         ItemLike pResult, float pExperience, int pCookingTime, String pGroup) {
        powderCooking(pFinishedRecipeConsumer, RecipeSerializer.SMELTING_RECIPE, pIngredient,
                pResult, pExperience, pCookingTime, pGroup, "_from_smelting");
        powderCooking(pFinishedRecipeConsumer, RecipeSerializer.BLASTING_RECIPE, pIngredient,
                pResult, pExperience, pCookingTime / 2, pGroup, "_from_blasting");
    }

    protected static void oreCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer,
                                     RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer,
                                     ItemLike pIngredient, ItemLike pResult, float pExperience,
                                     int pCookingTime, String pGroup, String pRecipeName) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of(pIngredient), RecipeCategory.MISC, pResult,
                        pExperience, pCookingTime, pCookingSerializer)
                .group(pGroup).unlockedBy(getHasName(pIngredient), has(pIngredient))
                .save(pFinishedRecipeConsumer, MODID + ":" + getItemName(pResult) +
                        pRecipeName + "_" + getItemName(pIngredient));
    }

    protected static void powderCooking(Consumer<FinishedRecipe> pFinishedRecipeConsumer,
                                        RecipeSerializer<? extends AbstractCookingRecipe> pCookingSerializer,
                                        ItemLike pIngredient, ItemLike pResult, float pExperience,
                                        int pCookingTime, String pGroup, String pRecipeName) {
        SimpleCookingRecipeBuilder.generic(Ingredient.of(pIngredient), RecipeCategory.MISC, pResult,
                        pExperience, pCookingTime, pCookingSerializer)
                .group(pGroup).unlockedBy(getHasName(pIngredient), has(pIngredient))
                .save(pFinishedRecipeConsumer, MODID + ":" + getItemName(pResult) +
                        pRecipeName + "_from_" + getItemName(pIngredient));
    }

    private void generateCraftingRecipes(Consumer<FinishedRecipe> pWriter) {

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOLT_STEEL.get(), 4)
                .pattern("I")
                .pattern("I")
                .define('I', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_steel_ingot", has(ModItems.INGOT_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOLT_LEAD.get(), 4)
                .pattern("I")
                .pattern("I")
                .define('I', ModItems.INGOT_LEAD.get())
                .unlockedBy("has_lead_ingot", has(ModItems.INGOT_LEAD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOLT_TUNGSTEN.get(), 4)
                .pattern("I")
                .pattern("I")
                .define('I', ModItems.INGOT_TUNGSTEN.get())
                .unlockedBy("has_tungsten_ingot", has(ModItems.INGOT_TUNGSTEN.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOLT_DURA_STEEL.get(), 4)
                .pattern("I")
                .pattern("I")
                .define('I', ModItems.INGOT_DURA_STEEL.get())
                .unlockedBy("has_dura_steel_ingot", has(ModItems.INGOT_DURA_STEEL.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ANVIL_IRON.get())
                .pattern("III")
                .pattern(" B ")
                .pattern("III")
                .define('I', Items.IRON_INGOT)
                .define('B', Blocks.IRON_BLOCK)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(pWriter, MODID + ":ntm_anvil_iron");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GEAR_LARGE.get(), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('I', ModItems.PLATE_IRON.get())
                .define('C', Items.COPPER_INGOT)
                .unlockedBy("has_iron_plate", has(ModItems.PLATE_IRON.get()))
                .save(pWriter, MODID + ":gear_large_iron");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.GEAR_LARGE.get(), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('I', ModItems.PLATE_STEEL.get())
                .define('C', ModItems.INGOT_TITANIUM.get())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .save(pWriter, MODID + ":gear_large_steel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.RED_CABLE.get(), 16)
                .pattern(" P ")
                .pattern("WWW")
                .pattern(" P ")
                .define('P', ModItems.PLATE_POLYMER.get())
                .define('W', ModItems.WIRE_RED_COPPER.get())
                .unlockedBy("has_polymer_plate", has(ModItems.PLATE_POLYMER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.DUCTTAPE.get(), 4)
                .pattern("F")
                .pattern("P")
                .pattern("S")
                .define('F', Items.STRING)
                .define('P', Items.PAPER)
                .define('S', Items.SLIME_BALL)
                .unlockedBy("has_string", has(Items.STRING))
                .unlockedBy("has_paper", has(Items.PAPER))
                .unlockedBy("has_slime", has(Items.SLIME_BALL))
                .save(pWriter, MODID + ":ducttape");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FLUID_DUCT.get(), 8)
                .pattern("SAS")
                .pattern("   ")
                .pattern("SAS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(pWriter, MODID + ":fluid_duct");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FLUID_DUCT_SILVER.get(), 8)
                .pattern("IAI")
                .pattern("   ")
                .pattern("IAI")
                .define('I', ModItems.PLATE_IRON.get())
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .unlockedBy("has_iron_plate", has(ModItems.PLATE_IRON.get()))
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(pWriter, MODID + ":fluid_duct_silver");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FLUID_DUCT_COLORED.get(), 8)
                .pattern("ASA")
                .pattern("   ")
                .pattern("ASA")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(pWriter, MODID + ":fluid_duct_colored");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BARREL_PLASTIC.get(), 1)
                .pattern("IPI")
                .pattern("I I")
                .pattern("IPI")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('P', ModItems.PLATE_ALUMINIUM.get())
                .unlockedBy("has_polymer_plate", has(ModItems.PLATE_POLYMER.get()))
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(pWriter, MODID + ":barrel_plastic");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BARREL_IRON.get(), 1)
                .pattern("IPI")
                .pattern("I I")
                .pattern("IPI")
                .define('I', ModItems.PLATE_IRON.get())
                .define('P', Items.IRON_INGOT)
                .unlockedBy("has_iron_plate", has(ModItems.PLATE_IRON.get()))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(pWriter, MODID + ":barrel_iron");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModBlocks.BARREL_IRON.get(), 1)
                .requires(ModBlocks.BARREL_CORRODED.get())
                .requires(ModItemTags.ANY_TAR)
                .unlockedBy("has_corroded_barrel", has(ModBlocks.BARREL_CORRODED.get()))
                .unlockedBy("has_tar", has(ModItemTags.ANY_TAR))
                .save(pWriter, MODID + ":barrel_iron_from_corroded");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BARREL_STEEL.get(), 1)
                .pattern("IPI")
                .pattern("ITI")
                .pattern("IPI")
                .define('I', ModItems.PLATE_STEEL.get())
                .define('P', ModItems.INGOT_STEEL.get())
                .define('T', ModItemTags.ANY_TAR)
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .unlockedBy("has_steel_ingot", has(ModItems.INGOT_STEEL.get()))
                .unlockedBy("has_tar", has(ModItemTags.ANY_TAR))
                .save(pWriter, MODID + ":barrel_steel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BARREL_TCALLOY.get(), 1)
                .pattern("IPI")
                .pattern("I I")
                .pattern("IPI")
                .define('I', ModItems.INGOT_TCALLOY.get())
                .define('P', ModItems.PLATE_TITANIUM.get())
                .unlockedBy("has_tcalloy_ingot", has(ModItems.INGOT_TCALLOY.get()))
                .unlockedBy("has_titanium_plate", has(ModItems.PLATE_TITANIUM.get()))
                .save(pWriter, MODID + ":barrel_tcalloy");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BARREL_ANTIMATTER.get(), 1)
                .pattern("IPI")
                .pattern("IBI")
                .pattern("IPI")
                .define('I', ModItems.PLATE_SATURNITE.get()) // или BIGMT.plate()
                .define('P', ModItems.COIL_ADVANCED_ALLOY_TORUS.get())
                .define('B', ModItems.BATTERY_SC_TECHNETIUM.get())
                .unlockedBy("has_saturnite_plate", has(ModItems.PLATE_SATURNITE.get()))
                .unlockedBy("has_advanced_torus", has(ModItems.COIL_ADVANCED_ALLOY_TORUS.get()))
                .unlockedBy("has_technetium_battery", has(ModItems.BATTERY_SC_TECHNETIUM.get()))
                .save(pWriter, MODID + ":barrel_antimatter");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FLUID_TANK.get(), 8)
                .pattern("121")
                .pattern("1G1")
                .pattern("121")
                .define('1', ModItems.PLATE_ALUMINIUM.get())
                .define('2', ModItems.PLATE_IRON.get())
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .unlockedBy("has_iron_plate", has(ModItems.PLATE_IRON.get()))
                .unlockedBy("has_glass_pane", has(ModItemTags.ANY_GLASS_PANES))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FLUID_BARREL.get(), 2)
                .pattern("121")
                .pattern("1G1")
                .pattern("121")
                .define('1', ModItems.PLATE_STEEL.get())
                .define('2', ModItems.PLATE_ALUMINIUM.get())
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .unlockedBy("has_aluminium_plate", has(ModItemTags.ANY_GLASS_PANES))
                .unlockedBy("has_glass_pane", has(Items.GLASS_PANE))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.FLUID_CANISTER.get(), 2)
                .pattern("S ")
                .pattern("AA")
                .pattern("AA")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('A', ModItems.PLATE_ALUMINIUM.get())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .unlockedBy("has_aluminium_plate", has(ModItems.PLATE_ALUMINIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_COPPER.get(), 1)
                .pattern("WWW")
                .pattern("WIW")
                .pattern("WWW")
                .define('W', ModItems.WIRE_RED_COPPER.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_red_copper_wire", has(ModItems.WIRE_RED_COPPER.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_ADVANCED_ALLOY.get(), 1)
                .pattern("WWW")
                .pattern("WIW")
                .pattern("WWW")
                .define('W', ModItems.WIRE_ADVANCED_ALLOY.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_alloy_wire", has(ModItems.WIRE_ADVANCED_ALLOY.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_GOLD.get(), 1)
                .pattern("WWW")
                .pattern("WIW")
                .pattern("WWW")
                .define('W', ModItems.WIRE_GOLD.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_gold_wire", has(ModItems.WIRE_GOLD.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_TUNGSTEN.get(), 1)
                .pattern("WWW")
                .pattern("WIW")
                .pattern("WWW")
                .define('W', ModItems.WIRE_TUNGSTEN.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_tungsten_wire", has(ModItems.WIRE_TUNGSTEN.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_MAGNETIZED_TUNGSTEN.get(), 1)
                .pattern("WWW")
                .pattern("WIW")
                .pattern("WWW")
                .define('W', ModItems.WIRE_MAGNETIZED_TUNGSTEN.get())
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_magtung_wire", has(ModItems.WIRE_MAGNETIZED_TUNGSTEN.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_COPPER_TORUS.get(), 2)
                .pattern(" C ")
                .pattern("CPC")
                .pattern(" C ")
                .define('C', ModItems.COIL_COPPER.get())
                .define('P', ModItems.PLATE_IRON.get())
                .unlockedBy("has_copper_coil", has(ModItems.COIL_COPPER.get()))
                .save(pWriter, MODID + ":coil_copper_torus_iron");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_COPPER_TORUS.get(), 2)
                .pattern(" C ")
                .pattern("CPC")
                .pattern(" C ")
                .define('C', ModItems.COIL_COPPER.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .unlockedBy("has_copper_coil", has(ModItems.COIL_COPPER.get()))
                .save(pWriter, MODID + ":coil_copper_torus_steel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_ADVANCED_ALLOY_TORUS.get(), 2)
                .pattern(" C ")
                .pattern("CPC")
                .pattern(" C ")
                .define('C', ModItems.COIL_ADVANCED_ALLOY.get())
                .define('P', ModItems.PLATE_IRON.get())
                .unlockedBy("has_advanced_coil", has(ModItems.COIL_ADVANCED_ALLOY.get()))
                .save(pWriter, MODID + ":coil_advanced_torus_iron");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_ADVANCED_ALLOY_TORUS.get(), 2)
                .pattern(" C ")
                .pattern("CPC")
                .pattern(" C ")
                .define('C', ModItems.COIL_ADVANCED_ALLOY.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .unlockedBy("has_advanced_coil", has(ModItems.COIL_ADVANCED_ALLOY.get()))
                .save(pWriter, MODID + ":coil_advanced_torus_steel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_GOLD_TORUS.get(), 2)
                .pattern(" C ")
                .pattern("CPC")
                .pattern(" C ")
                .define('C', ModItems.COIL_GOLD.get())
                .define('P', ModItems.PLATE_IRON.get())
                .unlockedBy("has_gold_coil", has(ModItems.COIL_GOLD.get()))
                .save(pWriter, MODID + ":coil_gold_torus_iron");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.COIL_GOLD_TORUS.get(), 2)
                .pattern(" C ")
                .pattern("CPC")
                .pattern(" C ")
                .define('C', ModItems.COIL_GOLD.get())
                .define('P', ModItems.PLATE_STEEL.get())
                .unlockedBy("has_gold_coil", has(ModItems.COIL_GOLD.get()))
                .save(pWriter, MODID + ":coil_gold_torus_steel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOTOR.get(), 2)
                .pattern(" R ")
                .pattern("ICI")
                .pattern("ITI")
                .define('R', ModItems.WIRE_RED_COPPER.get())
                .define('C', ModItems.COIL_COPPER.get())
                .define('T', ModItems.COIL_COPPER_TORUS.get())
                .define('I', ModItems.PLATE_IRON.get())
                .unlockedBy("has_mingrade_wire", has(ModItems.WIRE_RED_COPPER.get()))
                .save(pWriter, MODID + ":motor_iron");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOTOR.get(), 2)
                .pattern(" R ")
                .pattern("ICI")
                .pattern(" T ")
                .define('R', ModItems.WIRE_RED_COPPER.get())
                .define('C', ModItems.COIL_COPPER.get())
                .define('T', ModItems.COIL_COPPER_TORUS.get())
                .define('I', ModItems.PLATE_STEEL.get())
                .unlockedBy("has_mingrade_wire", has(ModItems.WIRE_RED_COPPER.get()))
                .save(pWriter, MODID + ":motor_steel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MOTOR_DESH.get(), 1)
                .pattern("PCP")
                .pattern("DMD")
                .pattern("PCP")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('C', ModItems.COIL_GOLD_TORUS.get())
                .define('D', ModItems.INGOT_DESH.get())
                .define('M', ModItems.MOTOR.get())
                .unlockedBy("has_plastic", has(ModItemTags.ANY_PLASTIC_INGOT))
                .unlockedBy("has_desh_ingot", has(ModItems.INGOT_DESH.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLATE_POLYMER.get(), 8)
                .pattern("DD")
                .define('D', ModItemTags.ANY_PLASTIC_INGOT)
                .unlockedBy("has_plastic", has(ModItemTags.ANY_PLASTIC_INGOT))
                .save(pWriter, MODID + ":plate_polymer_from_plastic");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLATE_POLYMER.get(), 8)
                .pattern("DD")
                .define('D', ModItemTags.ANY_RUBBER_INGOT)
                .unlockedBy("has_rubber", has(ModItemTags.ANY_RUBBER_INGOT))
                .save(pWriter, MODID + ":plate_polymer_from_rubber");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLATE_POLYMER.get(), 16)
                .pattern("DD")
                .define('D', ModItems.INGOT_FIBERGLASS.get())
                .unlockedBy("has_fiber_ingot", has(ModItems.INGOT_FIBERGLASS.get()))
                .save(pWriter, MODID + ":plate_polymer_from_fiber");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLATE_POLYMER.get(), 16)
                .pattern("DD")
                .define('D', ModItems.INGOT_ASBESTOS.get())
                .unlockedBy("has_asbestos_ingot", has(ModItems.INGOT_ASBESTOS.get()))
                .save(pWriter, MODID + ":plate_polymer_from_asbestos");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLATE_POLYMER.get(), 4)
                .pattern("SWS")
                .define('S', Items.STRING)
                .define('W', ModItemTags.ANY_WOOL)
                .unlockedBy("has_string", has(Items.STRING))
                .unlockedBy("has_wool", has(ModItemTags.ANY_WOOL))
                .save(pWriter, MODID + ":plate_polymer_from_string_wool");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLATE_POLYMER.get(), 4)
                .pattern("BB")
                .define('B', Items.BRICK)
                .unlockedBy("has_brick", has(Items.BRICK))
                .save(pWriter, MODID + ":plate_polymer_from_brick");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.PLATE_POLYMER.get(), 4)
                .pattern("BB")
                .define('B', Items.NETHER_BRICK)
                .unlockedBy("has_nether_brick", has(Items.NETHER_BRICK))
                .save(pWriter, MODID + ":plate_polymer_from_nether_brick");

// Vacuum Tube
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_VACUUM_TUBE.get(), 1)
                .pattern("G")
                .pattern("W")
                .pattern("I")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('W', ModItems.WIRE_TUNGSTEN.get())
                .define('I', ModItems.PLATE_POLYMER.get())
                .unlockedBy("has_glass_pane", has(ModItemTags.ANY_GLASS_PANES))
                .save(pWriter, MODID + ":circuit_vacuum_tube_tungsten");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_VACUUM_TUBE.get(), 1)
                .pattern("G")
                .pattern("W")
                .pattern("I")
                .define('G', ModItemTags.ANY_GLASS_PANES)
                .define('W', ModItems.WIRE_CARBON.get())
                .define('I', ModItems.PLATE_POLYMER.get())
                .unlockedBy("has_glass_pane", has(ModItemTags.ANY_GLASS_PANES))
                .save(pWriter, MODID + ":circuit_vacuum_tube_carbon");

// Capacitor
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CAPACITOR.get(), 1)
                .pattern("I")
                .pattern("N")
                .pattern("W")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('N', ModItems.NUGGET_NIOBIUM.get())
                .define('W', ModItems.WIRE_ALUMINIUM.get())
                .unlockedBy("has_niobium_nugget", has(ModItems.NUGGET_NIOBIUM.get()))
                .save(pWriter, MODID + ":circuit_capacitor_aluminium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CAPACITOR.get(), 1)
                .pattern("I")
                .pattern("N")
                .pattern("W")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('N', ModItems.NUGGET_NIOBIUM.get())
                .define('W', ModItems.WIRE_COPPER.get())
                .unlockedBy("has_niobium_nugget", has(ModItems.NUGGET_NIOBIUM.get()))
                .save(pWriter, MODID + ":circuit_capacitor_copper");

// Capacitor x2
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CAPACITOR.get(), 2)
                .pattern("IAI")
                .pattern("W W")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('A', ModItems.POWDER_ALUMINIUM.get())
                .define('W', ModItems.WIRE_ALUMINIUM.get())
                .unlockedBy("has_aluminium_wire", has(ModItems.WIRE_ALUMINIUM.get()))
                .save(pWriter, MODID + ":circuit_capacitor_x2_aluminium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CAPACITOR.get(), 2)
                .pattern("IAI")
                .pattern("W W")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('A', ModItems.POWDER_ALUMINIUM.get())
                .define('W', ModItems.WIRE_COPPER.get())
                .unlockedBy("has_copper_wire", has(ModItems.WIRE_COPPER.get()))
                .save(pWriter, MODID + ":circuit_capacitor_x2_copper");

// Tantalium Capacitor
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CAPACITOR_TANTALIUM.get(), 1)
                .pattern("I")
                .pattern("N")
                .pattern("W")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('N', ModItems.NUGGET_TANTALIUM.get())
                .define('W', ModItems.WIRE_ALUMINIUM.get())
                .unlockedBy("has_tantalium_nugget", has(ModItems.NUGGET_TANTALIUM.get()))
                .save(pWriter, MODID + ":circuit_capacitor_tantalium_aluminium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CAPACITOR_TANTALIUM.get(), 1)
                .pattern("I")
                .pattern("N")
                .pattern("W")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('N', ModItems.NUGGET_TANTALIUM.get())
                .define('W', ModItems.WIRE_COPPER.get())
                .unlockedBy("has_tantalium_nugget", has(ModItems.NUGGET_TANTALIUM.get()))
                .save(pWriter, MODID + ":circuit_capacitor_tantalium_copper");

// PCB
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_PCB.get(), 1)
                .pattern("I")
                .pattern("P")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('P', ModItems.PLATE_COPPER.get())
                .unlockedBy("has_copper_plate", has(ModItems.PLATE_COPPER.get()))
                .save(pWriter);

// PCB x4
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_PCB.get(), 4)
                .pattern("I")
                .pattern("P")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('P', ModItems.PLATE_GOLD.get())
                .unlockedBy("has_gold_plate", has(ModItems.PLATE_GOLD.get()))
                .save(pWriter, MODID + ":circuit_pcb_x4");

// Chip
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CHIP.get(), 1)
                .pattern("I")
                .pattern("S")
                .pattern("W")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('S', ModItems.CIRCUIT_SILICON.get())
                .define('W', ModItems.WIRE_COPPER.get())
                .unlockedBy("has_silicon_chip", has(ModItems.CIRCUIT_SILICON.get()))
                .save(pWriter, MODID + ":circuit_chip_copper");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CHIP.get(), 1)
                .pattern("I")
                .pattern("S")
                .pattern("W")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('S', ModItems.CIRCUIT_SILICON.get())
                .define('W', ModItems.WIRE_GOLD.get())
                .unlockedBy("has_gold_wire", has(ModItems.WIRE_GOLD.get()))
                .save(pWriter, MODID + ":circuit_chip_gold");

// Bismoid Chip
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CHIP_BISMOID.get(), 1)
                .pattern("III")
                .pattern("SNS")
                .pattern("WWW")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('S', ModItems.CIRCUIT_SILICON.get())
                .define('N', ModItemTags.ANY_BISMOID_NUGGET)
                .define('W', ModItems.WIRE_COPPER.get())
                .unlockedBy("has_bismoid_nugget", has(ModItemTags.ANY_BISMOID_NUGGET))
                .save(pWriter, MODID + ":circuit_chip_bismoid_copper");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CHIP_BISMOID.get(), 1)
                .pattern("III")
                .pattern("SNS")
                .pattern("WWW")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('S', ModItems.CIRCUIT_SILICON.get())
                .define('N', ModItemTags.ANY_BISMOID_NUGGET)
                .define('W', ModItems.WIRE_GOLD.get())
                .unlockedBy("has_gold_wire", has(ModItems.WIRE_GOLD.get()))
                .save(pWriter, MODID + ":circuit_chip_bismoid_gold");

// Quantum Chip
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CHIP_QUANTUM.get(), 1)
                .pattern("HHH")
                .pattern("SIS")
                .pattern("WWW")
                .define('H', ModItemTags.ANY_HARDPLASTIC_INGOT)
                .define('S', ModItems.WIRE_DENSE_BSCCO.get())
                .define('I', ModItems.PELLET_CHARGED.get())
                .define('W', ModItems.WIRE_COPPER.get())
                .unlockedBy("has_charged_pellet", has(ModItems.PELLET_CHARGED.get()))
                .save(pWriter, MODID + ":circuit_chip_quantum_copper");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CHIP_QUANTUM.get(), 1)
                .pattern("HHH")
                .pattern("SIS")
                .pattern("WWW")
                .define('H', ModItemTags.ANY_HARDPLASTIC_INGOT)
                .define('S', ModItems.WIRE_DENSE_BSCCO.get())
                .define('I', ModItems.PELLET_CHARGED.get())
                .define('W', ModItems.WIRE_GOLD.get())
                .unlockedBy("has_gold_wire", has(ModItems.WIRE_GOLD.get()))
                .save(pWriter, MODID + ":circuit_chip_quantum_gold");

// Controller Chassis
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_CONTROLLER_CHASSIS.get(), 1)
                .pattern("PPP")
                .pattern("CBB")
                .pattern("PPP")
                .define('P', ModItemTags.ANY_PLASTIC_INGOT)
                .define('C', ModItems.CRT_DISPLAY.get())
                .define('B', ModItems.CIRCUIT_PCB.get())
                .unlockedBy("has_crt_display", has(ModItems.CRT_DISPLAY.get()))
                .save(pWriter);

// Atomic Clock
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.CIRCUIT_ATOMIC_CLOCK.get(), 1)
                .pattern("ICI")
                .pattern("CSC")
                .pattern("ICI")
                .define('I', ModItems.PLATE_POLYMER.get())
                .define('C', ModItems.CIRCUIT_CHIP.get())
                .define('S', ModItems.POWDER_STRONTIUM.get())
                .unlockedBy("has_chip", has(ModItems.CIRCUIT_CHIP.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MACHINE_TRANSFORMER.get(), 1)
                .pattern("SCS")
                .pattern("MDM")
                .pattern("SCS")
                .define('S', Tags.Items.INGOTS_IRON)
                .define('C', ModItems.CIRCUIT_CAPACITOR.get())
                .define('M', ModItems.COIL_ADVANCED_ALLOY.get())
                .define('D', ModItems.INGOT_RED_COPPER.get())
                .unlockedBy("has_circuit_capacitor", has(ModItems.CIRCUIT_CAPACITOR.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.MACHINE_TRANSFORMER_DNT.get(), 1)
                .pattern("SDS")
                .pattern("MCM")
                .pattern("MCM")
                .define('S', ModItems.INGOT_STARMETAL.get())
                .define('D', ModItems.INGOT_DESH.get())
                .define('M', ModItems.WIRE_DENSE_MAGTUNG.get())
                .define('C', ModItems.CIRCUIT_BISMOID.get())
                .unlockedBy("has_circuit_bismoid", has(ModItems.CIRCUIT_BISMOID.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModBlocks.ANVIL_LEAD.get())
                .pattern("III")
                .pattern(" B ")
                .pattern("III")
                .define('I', ModItems.INGOT_LEAD.get())
                .define('B', ModBlocks.BLOCK_LEAD.get())
                .unlockedBy("has_ingot_lead", has(ModItems.INGOT_LEAD.get()))
                .save(pWriter, MODID + ":ntm_anvil_lead");


        Item[] bricks = new Item[] {Items.BRICK, Items.NETHER_BRICK};

        for(Item brick : bricks) {
            String brickName = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(brick)).getPath();

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STAMP_STONE_FLAT.get())
                    .pattern("III")
                    .pattern("SSS")
                    .define('I', brick)
                    .define('S', Items.STONE)
                    .unlockedBy("has_" + brickName, has(brick))
                    .save(pWriter, MODID + ":stamp_stone_flat_" + brickName);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STAMP_IRON_FLAT.get())
                    .pattern("III")
                    .pattern("SSS")
                    .define('I', brick)
                    .define('S', Items.IRON_INGOT)
                    .unlockedBy("has_" + brickName, has(brick))
                    .save(pWriter, MODID + ":stamp_iron_flat_" + brickName);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STAMP_STEEL_FLAT.get())
                    .pattern("III")
                    .pattern("SSS")
                    .define('I', brick)
                    .define('S', ModItems.INGOT_STEEL.get())
                    .unlockedBy("has_" + brickName, has(brick))
                    .save(pWriter, MODID + ":stamp_steel_flat_" + brickName);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STAMP_TITANIUM_FLAT.get())
                    .pattern("III")
                    .pattern("SSS")
                    .define('I', brick)
                    .define('S', ModItems.INGOT_TITANIUM.get())
                    .unlockedBy("has_" + brickName, has(brick))
                    .save(pWriter, MODID + ":stamp_titanium_flat_" + brickName);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STAMP_OBSIDIAN_FLAT.get())
                    .pattern("III")
                    .pattern("SSS")
                    .define('I', brick)
                    .define('S', Items.OBSIDIAN)
                    .unlockedBy("has_" + brickName, has(brick))
                    .save(pWriter, MODID + ":stamp_obsidian_flat_" + brickName);

            ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, ModItems.STAMP_DESH_FLAT.get())
                    .pattern("BDB")
                    .pattern("DSD")
                    .pattern("BDB")
                    .define('B', brick)
                    .define('D', ModItems.INGOT_DESH.get())
                    .define('S', ModItems.INGOT_FERROURANIUM.get())
                    .unlockedBy("has_" + brickName, has(brick))
                    .save(pWriter, MODID + ":stamp_desh_flat_" + brickName);

        }

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.MACHINE_PRESS.get())
                .pattern("IRI")
                .pattern("IPI")
                .pattern("IBI")
                .define('I', Items.IRON_INGOT)
                .define('R', Blocks.FURNACE)
                .define('B', Blocks.IRON_BLOCK)
                .define('P', Blocks.PISTON)
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(pWriter, MODID + ":machine_press");

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, ModItems.MACHINE_BLAST_FURNACE_EXTENSION.get())
                .pattern(" C ")
                .pattern("BGB")
                .pattern("BGB")
                .define('C', ModItems.PLATE_COPPER.get())
                .define('B', ModItems.INGOT_FIREBRICK.get())
                .define('G', ModBlocks.STEEL_GRATE.get())
                .unlockedBy("has_ingot_firebrick", has(ModItems.INGOT_FIREBRICK.get()))
                .save(pWriter, MODID + ":blast_furnace_extension");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_GRATE.get(), 4)
                .pattern("SS")
                .pattern("SS")
                .define('S', ModBlocks.STEEL_BEAM.get())
                .unlockedBy("has_steel_beam", has(ModBlocks.STEEL_BEAM.get()))
                .save(pWriter, MODID + ":steel_grate_from_beam");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_BEAM.get(), 8)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .define('S', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_ingot_steel", has(ModItems.INGOT_STEEL.get()))
                .save(pWriter, MODID + ":steel_beam_from_ingot");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_SCAFFOLD.get(), 8)
                .pattern("SSS")
                .pattern(" S ")
                .pattern("SSS")
                .define('S', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_ingot_steel", has(ModItems.INGOT_STEEL.get()))
                .save(pWriter, MODID + ":steel_scaffold");

        ShapedRecipeBuilder.shaped(RecipeCategory.BUILDING_BLOCKS, ModBlocks.STEEL_BEAM.get(), 8)
                .pattern("S")
                .pattern("S")
                .pattern("S")
                .define('S', ModBlocks.STEEL_SCAFFOLD.get())
                .unlockedBy("has_steel_scaffold", has(ModBlocks.STEEL_SCAFFOLD.get()))
                .save(pWriter, MODID + ":steel_beam_from_scaffold");

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.GLYPHID_MEAT.get()),
                        RecipeCategory.FOOD, ModItems.GLYPHID_MEAT_GRILLED.get(), 0.35F, 200)
                .unlockedBy("has_glyphid_meat", has(ModItems.GLYPHID_MEAT.get()))
                .save(pWriter, MODID + ":glyphid_meat_grilled_from_smelting");

        SimpleCookingRecipeBuilder.campfireCooking(Ingredient.of(ModItems.GLYPHID_MEAT.get()),
                        RecipeCategory.FOOD, ModItems.GLYPHID_MEAT_GRILLED.get(), 0.35F, 600)
                .unlockedBy("has_glyphid_meat", has(ModItems.GLYPHID_MEAT.get()))
                .save(pWriter, MODID + ":glyphid_meat_grilled_from_campfire_cooking");

        SimpleCookingRecipeBuilder.smoking(Ingredient.of(ModItems.GLYPHID_MEAT.get()),
                        RecipeCategory.FOOD, ModItems.GLYPHID_MEAT_GRILLED.get(), 0.35F, 100)
                .unlockedBy("has_glyphid_meat", has(ModItems.GLYPHID_MEAT.get()))
                .save(pWriter, MODID + ":glyphid_meat_grilled_from_smoking");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.RAG.get(), 4)
                .requires(Items.STRING, 2)
                .requires(Ingredient.of(ItemTags.WOOL), 2)
                .unlockedBy("has_string", has(Items.STRING))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BALL_RESIN.get(), 1)
                .pattern("DD")
                .pattern("DD")
                .define('D', Blocks.DANDELION)
                .unlockedBy("has_dandelion", has(Blocks.DANDELION))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.DECORATIONS, Items.TORCH, 4)
                .pattern("C")
                .pattern("S")
                .define('C', ModItems.LIGNITE.get())
                .define('S', Items.STICK)
                .unlockedBy("has_lignite", has(ModItems.LIGNITE.get()))
                .save(pWriter, MODID + ":torch_from_lignite");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.TANK_STEEL.get(), 2)
                .pattern("STS")
                .pattern("S S")
                .pattern("STS")
                .define('S', ModItems.PLATE_STEEL.get())
                .define('T', ModItems.PLATE_TITANIUM.get())
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .unlockedBy("has_titanium_plate", has(ModItems.PLATE_TITANIUM.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.FURNACE_IRON.get(), 1)
                .pattern("III")
                .pattern("IFI")
                .pattern("BBB")
                .define('I', Items.IRON_INGOT)
                .define('F', Blocks.FURNACE)
                .define('B', Blocks.STONE_BRICKS)
                .unlockedBy("has_iron", has(Items.IRON_INGOT))
                .unlockedBy("has_furnace", has(Blocks.FURNACE))
                .unlockedBy("has_stone_bricks", has(Blocks.STONE_BRICKS))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.SAWBLADE.get(), 1)
                .pattern("III")
                .pattern("ICI")
                .pattern("III")
                .define('I', ModItems.PLATE_STEEL.get())
                .define('C', Items.IRON_INGOT)
                .unlockedBy("has_steel_plate", has(ModItems.PLATE_STEEL.get()))
                .unlockedBy("has_iron_ingot", has(Items.IRON_INGOT))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.LAUNCH_CODE.get(), 1)
                .requires(ModItems.LAUNCH_CODE_PIECE.get(), 8)
                .requires(ModItems.CIRCUIT_ADVANCED.get())
                .unlockedBy("has_launch_code_piece", has(Items.CLAY_BALL))
                .save(pWriter);

    }


}