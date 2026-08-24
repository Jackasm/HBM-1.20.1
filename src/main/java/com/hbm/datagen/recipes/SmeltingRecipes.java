package com.hbm.datagen.recipes;

import com.google.gson.JsonObject;
import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.recipes.ingredient.ScrapPlasticIngredient;
import com.hbm.items.ModItems;
import com.hbm.items.ModToolItems;
import com.hbm.items.special.ItemHot;
import com.hbm.util.RefStrings;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.hbm.util.ResLocation.ResLocation;

public class SmeltingRecipes {

    public static void generateSmeltingRecipes(Consumer<FinishedRecipe> writer,
                                               Function<Item, InventoryChangeTrigger.TriggerInstance> has) {

        SimpleCookingRecipeBuilder.smelting(
                        Ingredient.of(ModItems.GLYPHID_MEAT.get()),
                        RecipeCategory.FOOD,
                        ModItems.GLYPHID_MEAT_GRILLED.get(),
                        0.35F,
                        200
                )
                .unlockedBy("has_glyphid_meat", has.apply(ModItems.GLYPHID_MEAT.get()))
                .save(writer, RefStrings.MODID + ":glyphid_meat_grilled_from_smelting");

        SimpleCookingRecipeBuilder.campfireCooking(
                        Ingredient.of(ModItems.GLYPHID_MEAT.get()),
                        RecipeCategory.FOOD,
                        ModItems.GLYPHID_MEAT_GRILLED.get(),
                        0.35F,
                        600
                )
                .unlockedBy("has_glyphid_meat", has.apply(ModItems.GLYPHID_MEAT.get()))
                .save(writer, RefStrings.MODID + ":glyphid_meat_grilled_from_campfire_cooking");

        SimpleCookingRecipeBuilder.smoking(
                        Ingredient.of(ModItems.GLYPHID_MEAT.get()),
                        RecipeCategory.FOOD,
                        ModItems.GLYPHID_MEAT_GRILLED.get(),
                        0.35F,
                        100
                )
                .unlockedBy("has_glyphid_meat", has.apply(ModItems.GLYPHID_MEAT.get()))
                .save(writer, RefStrings.MODID + ":glyphid_meat_grilled_from_smoking");

        addSmeltingRecipe(writer, ModBlocks.ORE_THORIUM.get().asItem(), ModItems.INGOT_TH232.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_THORIUM_DEEPSLATE.get().asItem(), ModItems.INGOT_TH232.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_URANIUM.get().asItem(), ModItems.INGOT_URANIUM.get(), 6.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_URANIUM_DEEPSLATE.get().asItem(), ModItems.INGOT_URANIUM.get(), 6.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_URANIUM_SCORCHED.get().asItem(), ModItems.INGOT_URANIUM.get(), 6.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_NETHER_URANIUM.get().asItem(), ModItems.INGOT_URANIUM.get(), 12.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_NETHER_URANIUM_SCORCHED.get().asItem(), ModItems.INGOT_URANIUM.get(), 12.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_NETHER_PLUTONIUM.get().asItem(), ModItems.INGOT_PLUTONIUM.get(), 24.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_TITANIUM.get().asItem(), ModItems.INGOT_TITANIUM.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_TITANIUM_DEEPSLATE.get().asItem(), ModItems.INGOT_TITANIUM.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_TUNGSTEN.get().asItem(), ModItems.INGOT_TUNGSTEN.get(), 6.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_TUNGSTEN_DEEPSLATE.get().asItem(), ModItems.INGOT_TUNGSTEN.get(), 6.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_NETHER_TUNGSTEN.get().asItem(), ModItems.INGOT_TUNGSTEN.get(), 12.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_ALUMINIUM.get().asItem(), ModItems.CHUNK_ORE_CRYOLITE.get(), 2.5F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_ALUMINIUM_DEEPSLATE.get().asItem(), ModItems.CHUNK_ORE_CRYOLITE.get(), 2.5F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_LEAD.get().asItem(), ModItems.INGOT_LEAD.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_LEAD_DEEPSLATE.get().asItem(), ModItems.INGOT_LEAD.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_BERYLLIUM.get().asItem(), ModItems.INGOT_BERYLLIUM.get(), 2.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_BERYLLIUM_DEEPSLATE.get().asItem(), ModItems.INGOT_BERYLLIUM.get(), 2.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_SCHRABIDIUM.get().asItem(), ModItems.INGOT_SCHRABIDIUM.get(), 128.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_NETHER_SCHRABIDIUM.get().asItem(), ModItems.INGOT_SCHRABIDIUM.get(), 256.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_COBALT.get().asItem(), ModItems.INGOT_COBALT.get(), 2.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_COBALT_DEEPSLATE.get().asItem(), ModItems.INGOT_COBALT.get(), 2.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_NETHER_COBALT.get().asItem(), ModItems.INGOT_COBALT.get(), 2.0F, has);

        addSmeltingRecipe(writer, ModItems.RAW_THORIUM.get(), ModItems.INGOT_TH232.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModItems.RAW_URANIUM.get(), ModItems.INGOT_URANIUM.get(), 6.0F, has);
        addSmeltingRecipe(writer, ModItems.RAW_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), 6.0F, has);
        addSmeltingRecipe(writer, ModItems.RAW_TITANIUM.get(), ModItems.INGOT_TITANIUM.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModItems.RAW_TUNGSTEN.get(), ModItems.INGOT_TUNGSTEN.get(), 6.0F, has);
        addSmeltingRecipe(writer, ModItems.RAW_ALUMINIUM.get(), ModItems.CHUNK_ORE_CRYOLITE.get(), 2.5F, has);
        addSmeltingRecipe(writer, ModItems.RAW_LEAD.get(), ModItems.INGOT_LEAD.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModItems.RAW_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), 2.0F, has);
        addSmeltingRecipe(writer, ModItems.RAW_COBALT.get(), ModItems.INGOT_COBALT.get(), 2.0F, has);

        addSmeltingRecipe(writer, ModBlocks.ORE_METEOR_IRON.get().asItem(), Items.IRON_INGOT, 10.0F, has, 16);
        addSmeltingRecipe(writer, ModBlocks.ORE_METEOR_COPPER.get().asItem(), Items.COPPER_INGOT, 10.0F, has, 16);
        addSmeltingRecipe(writer, ModBlocks.ORE_METEOR_ALUMINIUM.get().asItem(), ModItems.CHUNK_ORE_CRYOLITE.get(), 10.0F, has, 16);
        addSmeltingRecipe(writer, ModBlocks.ORE_METEOR_RARE_EARTH.get().asItem(), ModItems.CHUNK_ORE_RARE.get(), 10.0F, has, 16);
        addSmeltingRecipe(writer, ModBlocks.ORE_METEOR_COBALT.get().asItem(), ModItems.INGOT_COBALT.get(), 10.0F, has, 4);

        addSmeltingRecipe(writer, ModBlocks.ORE_GNEISS_IRON.get().asItem(), Items.IRON_INGOT, 5.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_GNEISS_GOLD.get().asItem(), Items.GOLD_INGOT, 5.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_GNEISS_URANIUM.get().asItem(), ModItems.INGOT_URANIUM.get(), 12.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_GNEISS_URANIUM_SCORCHED.get().asItem(), ModItems.INGOT_URANIUM.get(), 12.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_GNEISS_COPPER.get().asItem(), Items.COPPER_INGOT, 5.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_GNEISS_LITHIUM.get().asItem(), ModItems.LITHIUM.get(), 10.0F, has);
        addSmeltingRecipe(writer, ModBlocks.ORE_GNEISS_SCHRABIDIUM.get().asItem(), ModItems.INGOT_SCHRABIDIUM.get(), 128.0F, has);

        addSmeltingRecipe(writer, ModBlocks.ORE_AUSTRALIUM.get().asItem(), ModItems.NUGGET_AUSTRALIUM.get(), 2.5F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_AUSTRALIUM.get(), ModItems.INGOT_AUSTRALIUM.get(), 5.0F, has);

        addSmeltingRecipe(writer, ModItems.BRIQUETTE_COAL.get(), ModItems.COKE_COAL.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.BRIQUETTE_LIGNITE.get(), ModItems.COKE_LIGNITE.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.BRIQUETTE_WOOD.get(), Items.CHARCOAL, 1.0F, has);

        addSmeltingRecipe(writer, ModItems.POWDER_LEAD.get(), ModItems.INGOT_LEAD.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_NEPTUNIUM.get(), ModItems.INGOT_NEPTUNIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_POLONIUM.get(), ModItems.INGOT_POLONIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_SCHRABIDIUM.get(), ModItems.INGOT_SCHRABIDIUM.get(), 5.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_SCHRABIDATE.get(), ModItems.INGOT_SCHRABIDATE.get(), 5.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_EUPHEMIUM.get(), ModItems.INGOT_EUPHEMIUM.get(), 10.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_ALUMINIUM.get(), ModItems.INGOT_ALUMINIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_COPPER.get(), Items.COPPER_INGOT, 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_GOLD.get(), Items.GOLD_INGOT, 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_IRON.get(), Items.IRON_INGOT, 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_TITANIUM.get(), ModItems.INGOT_TITANIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_COBALT.get(), ModItems.INGOT_COBALT.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_TUNGSTEN.get(), ModItems.INGOT_TUNGSTEN.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_URANIUM.get(), ModItems.INGOT_URANIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_THORIUM.get(), ModItems.INGOT_TH232.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_ADVANCED_ALLOY.get(), ModItems.INGOT_ADVANCED_ALLOY.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_COMBINE_STEEL.get(), ModItems.INGOT_COMBINE_STEEL.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_MAGNETIZED_TUNGSTEN.get(), ModItems.INGOT_MAGNETIZED_TUNGSTEN.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_RED_COPPER.get(), ModItems.INGOT_RED_COPPER.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_STEEL.get(), ModItems.INGOT_STEEL.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_LITHIUM.get(), ModItems.LITHIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_DURA_STEEL.get(), ModItems.INGOT_DURA_STEEL.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_POLYMER.get(), ModItems.INGOT_POLYMER.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_BAKELITE.get(), ModItems.INGOT_BAKELITE.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_LANTHANIUM.get(), ModItems.INGOT_LANTHANIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_ACTINIUM.get(), ModItems.INGOT_ACTINIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_BORON.get(), ModItems.INGOT_BORON.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_DESH.get(), ModItems.INGOT_DESH.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_DINEUTRONIUM.get(), ModItems.INGOT_DINEUTRONIUM.get(), 5.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_ASBESTOS.get(), ModItems.INGOT_ASBESTOS.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_ZIRCONIUM.get(), ModItems.INGOT_ZIRCONIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_TCALLOY.get(), ModItems.INGOT_TCALLOY.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_AU198.get(), ModItems.INGOT_AU198.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_SR90.get(), ModItems.INGOT_SR90.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_RA226.get(), ModItems.INGOT_RA226.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_TANTALIUM.get(), ModItems.INGOT_TANTALIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_NIOBIUM.get(), ModItems.INGOT_NIOBIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_BISMUTH.get(), ModItems.INGOT_BISMUTH.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_CALCIUM.get(), ModItems.INGOT_CALCIUM.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.POWDER_CADMIUM.get(), ModItems.INGOT_CADMIUM.get(), 1.0F, has);

        addSmeltingRecipe(writer, ModItems.BALL_RESIN.get(), ModItems.INGOT_BIORUBBER.get(), 0.1F, has);

        addSmeltingRecipe(writer, ModItems.ARC_ELECTRODE_BURNT_GRAPHITE.get(), ModItems.INGOT_GRAPHITE.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModItems.ARC_ELECTRODE_BURNT_LANTHANIUM.get(), ModItems.INGOT_LANTHANIUM.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModItems.ARC_ELECTRODE_BURNT_DESH.get(), ModItems.INGOT_DESH.get(), 3.0F, has);
        addSmeltingRecipe(writer, ModItems.ARC_ELECTRODE_BURNT_SATURNITE.get(), ModItems.INGOT_SATURNITE.get(), 3.0F, has);

        addSmeltingRecipe(writer, ModItems.COMBINE_SCRAP.get(), ModItems.INGOT_COMBINE_STEEL.get(), 1.0F, has);
        addSmeltingRecipe(writer, ModItems.BALL_FIRECLAY.get(), ModItems.INGOT_FIREBRICK.get(), 0.1F, has);

        addSmeltingRecipe(writer, ModItems.PLANT_FLOWER_TOBACCO.get(), ModItems.PLANT_ITEM_TOBACCO.get(), 0.1F, has);

        addSmeltingRecipe(writer, Items.GRAVEL, Items.COBBLESTONE, 0.0F, has);
        addSmeltingRecipe(writer, ModBlocks.GRAVEL_OBSIDIAN.get().asItem(), Items.OBSIDIAN, 0.0F, has);
        addSmeltingRecipe(writer, ModBlocks.GRAVEL_DIAMOND.get().asItem(), Items.DIAMOND, 3.0F, has);
        addSmeltingRecipe(writer, ModBlocks.SAND_URANIUM.get().asItem(), ModBlocks.GLASS_URANIUM.get().asItem(), 0.25F, has);
        addSmeltingRecipe(writer, ModBlocks.SAND_POLONIUM.get().asItem(), ModBlocks.GLASS_POLONIUM.get().asItem(), 0.75F, has);
        addSmeltingRecipe(writer, ModBlocks.WASTE_TRINITITE.get().asItem(), ModBlocks.GLASS_TRINITITE.get().asItem(), 0.25F, has);
        addSmeltingRecipe(writer, ModBlocks.WASTE_TRINITITE_RED.get().asItem(), ModBlocks.GLASS_TRINITITE.get().asItem(), 0.25F, has);
        addSmeltingRecipe(writer, ModBlocks.SAND_BORON.get().asItem(), ModBlocks.GLASS_BORON.get().asItem(), 0.25F, has);
        addSmeltingRecipe(writer, ModBlocks.SAND_LEAD.get().asItem(), ModBlocks.GLASS_LEAD.get().asItem(), 0.25F, has);
        addSmeltingRecipe(writer, ModBlocks.ASH_DIGAMMA.get().asItem(), ModBlocks.GLASS_ASH.get().asItem(), 10.0F, has);
        addSmeltingRecipe(writer, ModBlocks.BASALT.get().asItem(), ModBlocks.BASALT_SMOOTH.get().asItem(), 0.1F, has);

        addSmeltingRecipe(writer, ModItems.INGOT_SCHRARANIUM.get(), ModItems.NUGGET_SCHRABIDIUM.get(), 2.0F, has);
        addSmeltingRecipe(writer, ModItems.LODESTONE.get(), ModItems.CRYSTAL_IRON.get(), 5.0F, has);

        addSmeltingRecipe(writer, ModItems.CRYSTAL_IRON.get(), Items.IRON_INGOT, 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_GOLD.get(), Items.GOLD_INGOT, 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_REDSTONE.get(), Items.REDSTONE, 2.0F, has, 6);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_DIAMOND.get(), Items.DIAMOND, 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_URANIUM.get(), ModItems.INGOT_URANIUM.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_THORIUM.get(), ModItems.INGOT_TH232.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_TITANIUM.get(), ModItems.INGOT_TITANIUM.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_SULFUR.get(), ModItems.SULFUR.get(), 2.0F, has, 6);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_NITER.get(), ModItems.NITER.get(), 2.0F, has, 6);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_COPPER.get(), Items.COPPER_INGOT, 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_TUNGSTEN.get(), ModItems.INGOT_TUNGSTEN.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_ALUMINIUM.get(), ModItems.INGOT_ALUMINIUM.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_FLUORITE.get(), ModItems.FLUORITE.get(), 2.0F, has, 6);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_LEAD.get(), ModItems.INGOT_LEAD.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_SCHRARANIUM.get(), ModItems.NUGGET_SCHRABIDIUM.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_SCHRABIDIUM.get(), ModItems.INGOT_SCHRABIDIUM.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_RARE.get(), ModItems.POWDER_DESH_MIX.get(), 2.0F, has);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_PHOSPHORUS.get(), ModItems.POWDER_FIRE.get(), 2.0F, has, 6);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_LITHIUM.get(), ModItems.LITHIUM.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_COBALT.get(), ModItems.INGOT_COBALT.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_STARMETAL.get(), ModItems.INGOT_STARMETAL.get(), 2.0F, has, 2);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_TRIXITE.get(), ModItems.INGOT_PLUTONIUM.get(), 2.0F, has, 4);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_CINNABAR.get(), ModItems.CINNABAR.get(), 2.0F, has, 4);
        addSmeltingRecipe(writer, ModItems.CRYSTAL_OSMIRIDIUM.get(), ModItems.INGOT_OSMIRIDIUM.get(), 2.0F, has);

        addHotSmelting(writer, ModItems.INGOT_CHAINSTEEL.get(), ModItems.INGOT_CHAINSTEEL.get(), ((ItemHot) ModItems.INGOT_CHAINSTEEL.get()).maxHeat, has);
        addHotSmelting(writer, ModItems.INGOT_METEORITE.get(), ModItems.INGOT_METEORITE.get(), ((ItemHot) ModItems.INGOT_METEORITE.get()).maxHeat, has);
        addHotSmelting(writer, ModItems.INGOT_METEORITE_FORGED.get(), ModItems.INGOT_METEORITE_FORGED.get(), ((ItemHot) ModItems.INGOT_METEORITE_FORGED.get()).maxHeat, has);
        addHotSmelting(writer, ModItems.BLADE_METEORITE.get(), ModItems.BLADE_METEORITE.get(), ((ItemHot) ModItems.BLADE_METEORITE.get()).maxHeat, has);

        ItemStack swordInput = new ItemStack(ModToolItems.METEORITE_SWORD.get());
        ItemStack swordOutput = new ItemStack(ModToolItems.METEORITE_SWORD_SEARED.get());
        swordOutput.getOrCreateTag().putInt("heat", 200);
        addHotSmeltingStack(writer, swordInput, swordOutput, 0.0F, has);

        addSmeltingIngredient(writer, ScrapPlasticIngredient.of(), ModItems.INGOT_POLYMER.get(), 0.1F, has);

        /* TODO плавка бедроковой руды
        for(EnumBedrockOre ore : EnumBedrockOre.values()) {
			int i = ore.ordinal();
			GameRegistry.addSmelting(new ItemStack(ModItems.ore_bedrock, 1, i), new ItemStack(Blocks.cobblestone, 16), 0.1F);
		}
         */

        for (int i = 0; i < 10; i++) {
            ItemStack input = new ItemStack(ModItems.INGOT_STEEL_DUSTED.get());
            input.getOrCreateTag().putInt("CustomModelData", i);

            ItemStack output = input.copy();
            output.getOrCreateTag().putInt("heat", ((ItemHot) ModItems.INGOT_STEEL_DUSTED.get()).maxHeat);

            addHotSmeltingStack(writer, input, output, 1.0F, has);
        }
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> writer, Item input, Item output, float experience,
                                          Function<Item, InventoryChangeTrigger.TriggerInstance> has) {
        addSmeltingRecipe(writer, input, output, experience, has, 1);
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> writer, Item input, Item output, float experience,
                                          Function<Item, InventoryChangeTrigger.TriggerInstance> has, int count) {
        String inputName = input.getDescriptionId().replace("block.hbm.", "").replace("item.hbm.", "");
        String outputName = output.getDescriptionId().replace("block.hbm.", "").replace("item.hbm.", "");

        writer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(@NotNull JsonObject json) {
                json.addProperty("type", "minecraft:smelting");
                json.addProperty("category", "misc");
                json.addProperty("cookingtime", 200);
                json.addProperty("experience", experience);

                JsonObject ingredient = new JsonObject();
                ingredient.addProperty("item", BuiltInRegistries.ITEM.getKey(input).toString());
                json.add("ingredient", ingredient);

                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("item", BuiltInRegistries.ITEM.getKey(output).toString());
                if (count > 1) {
                    resultObj.addProperty("count", count);
                }
                json.add("result", resultObj);
            }

            @Override
            public @NotNull ResourceLocation getId() {
                return ResLocation("hbm", "smelting/" + inputName + "_to_" + outputName + (count > 1 ? "_x" + count : ""));
            }

            @Override
            public @NotNull RecipeSerializer<?> getType() {
                return RecipeSerializer.SMELTING_RECIPE;
            }

            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        });
    }

    private static void addHotSmelting(Consumer<FinishedRecipe> writer, Item input, Item output, int heat,
                                       Function<Item, InventoryChangeTrigger.TriggerInstance> has) {
        ItemStack result = new ItemStack(output);
        CompoundTag tag = new CompoundTag();
        tag.putInt("heat", heat);
        result.setTag(tag);

        writer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(@NotNull JsonObject json) {
                json.addProperty("type", "minecraft:smelting");
                json.addProperty("category", "misc");
                json.addProperty("cookingtime", 200);
                json.addProperty("experience", 0.0F);

                JsonObject ingredient = new JsonObject();
                ingredient.addProperty("item", BuiltInRegistries.ITEM.getKey(input).toString());
                json.add("ingredient", ingredient);

                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("item", BuiltInRegistries.ITEM.getKey(output).toString());

                JsonObject nbt = new JsonObject();
                nbt.addProperty("heat", heat);
                resultObj.add("nbt", nbt);

                json.add("result", resultObj);
            }

            @Override
            public @NotNull ResourceLocation getId() {
                return ResLocation("hbm", "smelting/" + input.getDescriptionId().replace("item.hbm.", "") + "_hot");
            }

            @Override
            public @NotNull RecipeSerializer<?> getType() {
                return RecipeSerializer.SMELTING_RECIPE;
            }

            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        });
    }

    private static void addSmeltingIngredient(Consumer<FinishedRecipe> writer, Ingredient input, Item output, float experience,
                                              Function<Item, InventoryChangeTrigger.TriggerInstance> has) {

        SimpleCookingRecipeBuilder.smelting(
                        input,
                        RecipeCategory.MISC,
                        output,
                        experience,
                        200
                )
                .unlockedBy("has_scrap_plastic", has.apply(ModItems.SCRAP_PLASTIC.get()))
                .save(writer);
    }

    private static void addHotSmeltingStack(Consumer<FinishedRecipe> writer, ItemStack input, ItemStack output, float experience,
                                            Function<Item, InventoryChangeTrigger.TriggerInstance> has) {
        String inputName = input.getItem().getDescriptionId().replace("item.hbm.", "");

        writer.accept(new FinishedRecipe() {
            @Override
            public void serializeRecipeData(@NotNull JsonObject json) {
                json.addProperty("type", "minecraft:smelting");
                json.addProperty("category", "misc");
                json.addProperty("cookingtime", 200);
                json.addProperty("experience", experience);

                JsonObject ingredient = new JsonObject();
                ingredient.addProperty("item", BuiltInRegistries.ITEM.getKey(input.getItem()).toString());

                if (input.hasTag()) {
                    JsonObject nbt = new JsonObject();
                    if (Objects.requireNonNull(input.getTag()).contains("CustomModelData")) {
                        nbt.addProperty("CustomModelData", input.getTag().getInt("CustomModelData"));
                    }
                    if (nbt.size() > 0) {
                        ingredient.add("nbt", nbt);
                    }
                }
                json.add("ingredient", ingredient);

                JsonObject resultObj = new JsonObject();
                resultObj.addProperty("item", BuiltInRegistries.ITEM.getKey(output.getItem()).toString());

                if (output.hasTag()) {
                    JsonObject resultNbt = new JsonObject();
                    if (Objects.requireNonNull(output.getTag()).contains("CustomModelData")) {
                        resultNbt.addProperty("CustomModelData", output.getTag().getInt("CustomModelData"));
                    }
                    if (output.getTag().contains("heat")) {
                        resultNbt.addProperty("heat", output.getTag().getInt("heat"));
                    }
                    if (resultNbt.size() > 0) {
                        resultObj.add("nbt", resultNbt);
                    }
                }

                json.add("result", resultObj);
            }

            @Override
            public @NotNull ResourceLocation getId() {
                int variant = input.hasTag() ? Objects.requireNonNull(input.getTag()).getInt("CustomModelData") : 0;
                return ResLocation("hbm", "smelting/" + inputName + "_hot_" + variant);
            }

            @Override
            public @NotNull RecipeSerializer<?> getType() {
                return RecipeSerializer.SMELTING_RECIPE;
            }

            @Override
            public JsonObject serializeAdvancement() {
                return null;
            }

            @Override
            public ResourceLocation getAdvancementId() {
                return null;
            }
        });
    }
}

