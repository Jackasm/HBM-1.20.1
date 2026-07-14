package com.hbm.datagen.recipes;

import com.hbm.config.GeneralConfig;
import com.hbm.datagen.recipes.ingredient.AnyAshIngredient;
import com.hbm.datagen.recipes.ingredient.FluidBucketIngredient;
import com.hbm.datagen.recipes.ingredient.FluidTankIngredient;
import com.hbm.inventory.fluid.Fluids;
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

public class PowderRecipes extends ModRecipeProvider {
    public PowderRecipes(PackOutput pOutput) { super(pOutput); }

    public static void generatePowderRecipes(Consumer<FinishedRecipe> pWriter) {

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALLISTITE.get(), 3)
                .requires(Items.GUNPOWDER)
                .requires(ModItems.NITER.get())
                .requires(Items.SUGAR)
                .unlockedBy("has_gunpowder", has(Items.GUNPOWDER))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_DYNAMITE.get(), 2)
                .requires(ModItems.NITER.get())
                .requires(Items.SUGAR)
                .requires(Blocks.SAND)
                .requires(ModItemTags.TOOL_CHEMISTRYSET)
                .unlockedBy("has_niter", has(ModItems.NITER.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_TNT.get(), 4)
                .requires(FluidBucketIngredient.of(Fluids.AROMATICS.get()))
                .requires(ModItems.NITER.get())
                .requires(ModItemTags.TOOL_CHEMISTRYSET)
                .unlockedBy("has_fluid_bucket", has(ModItems.FLUID_BUCKET.get()))
                .save(pWriter, MODID + ":ball_tnt_bucket");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_TNT.get(), 4)
                .requires(FluidTankIngredient.of(Fluids.AROMATICS.get()))
                .requires(ModItems.NITER.get())
                .requires(ModItemTags.TOOL_CHEMISTRYSET)
                .unlockedBy("has_fluid_tank", has(ModItems.FLUID_TANK.get()))
                .save(pWriter, MODID + ":ball_tnt_tank");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_C4.get(), 4)
                .requires(FluidBucketIngredient.of(Fluids.UNSATURATEDS.get()))
                .requires(ModItems.NITER.get())
                .requires(ModItemTags.TOOL_CHEMISTRYSET)
                .unlockedBy("has_fluid_bucket", has(ModItems.FLUID_BUCKET.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_C4.get(), 4)
                .requires(FluidTankIngredient.of(Fluids.UNSATURATEDS.get()))
                .requires(ModItems.NITER.get())
                .requires(ModItemTags.TOOL_CHEMISTRYSET)
                .unlockedBy("has_fluid_tank", has(ModItems.FLUID_TANK.get()))
                .save(pWriter, MODID + ":ingot_c4_tank");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_SEMTEX_MIX.get(), 3)
                .requires(ModItems.SOLID_FUEL.get())
                .requires(ModItems.CORDITE.get())
                .requires(ModItems.NITER.get())
                .unlockedBy("has_solid_fuel", has(ModItems.SOLID_FUEL.get()))
                .save(pWriter, MODID + ":powder_semtex_mix_cordite");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_SEMTEX_MIX.get(), 1)
                .requires(ModItems.SOLID_FUEL.get())
                .requires(ModItems.BALLISTITE.get())
                .requires(ModItems.NITER.get())
                .unlockedBy("has_solid_fuel", has(ModItems.SOLID_FUEL.get()))
                .save(pWriter, MODID + ":powder_semtex_mix_ballistite");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CLAY_BALL, 4)
                .requires(ModItemTags.ANY_SAND)
                .requires(ModItems.DUST.get())
                .requires(ModItems.DUST.get())
                .requires(Items.WATER_BUCKET)
                .unlockedBy("has_dust", has(ModItems.DUST.get()))
                .save(pWriter, MODID + ":clay_ball_from_sand");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.CLAY_BALL, 4)
                .requires(Blocks.CLAY)
                .unlockedBy("has_clay_block", has(Blocks.CLAY))
                .save(pWriter, MODID + ":clay_ball_from_block");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_CEMENT.get(), 4)
                .requires(ModItems.POWDER_LIMESTONE.get())
                .requires(Items.CLAY_BALL)
                .requires(Items.CLAY_BALL)
                .requires(Items.CLAY_BALL)
                .unlockedBy("has_limestone", has(ModItems.POWDER_LIMESTONE.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_STEEL_DUSTED.get(), 1)
                .requires(ModItems.INGOT_STEEL.get())
                .requires(ModItems.POWDER_COAL.get())
                .unlockedBy("has_steel", has(ModItems.INGOT_STEEL.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER, 3)
                .requires(ModItems.SULFUR.get())
                .requires(ModItems.NITER.get())
                .requires(Items.COAL)
                .unlockedBy("has_sulfur", has(ModItems.SULFUR.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, Items.GUNPOWDER, 3)
                .requires(ModItems.SULFUR.get())
                .requires(ModItems.NITER.get())
                .requires(Items.CHARCOAL)
                .unlockedBy("has_sulfur", has(ModItems.SULFUR.get()))
                .save(pWriter, MODID + ":gunpowder_charcoal");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_POWER.get(), 3)
                .requires(Items.GLOWSTONE_DUST)
                .requires(ModItems.POWDER_DIAMOND.get())
                .requires(ModItems.POWDER_MAGNETIZED_TUNGSTEN.get())
                .unlockedBy("has_glowstone", has(Items.GLOWSTONE_DUST))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_NITAN_MIX.get(), 6)
                .requires(ModItems.POWDER_NEPTUNIUM.get())
                .requires(ModItems.POWDER_IODINE.get())
                .requires(ModItems.POWDER_THORIUM.get())
                .requires(ModItems.POWDER_ASTATINE.get())
                .requires(ModItems.POWDER_NEODYMIUM_TINY.get())
                .requires(ModItems.POWDER_CAESIUM.get())
                .unlockedBy("has_neptunium", has(ModItems.POWDER_NEPTUNIUM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_NITAN_MIX.get(), 6)
                .requires(ModItems.POWDER_STRONTIUM.get())
                .requires(ModItems.POWDER_COBALT.get())
                .requires(ModItems.POWDER_BROMINE.get())
                .requires(ModItems.POWDER_TENNESSINE.get())
                .requires(ModItems.POWDER_NIOBIUM.get())
                .requires(ModItems.POWDER_CERIUM_TINY.get())
                .unlockedBy("has_strontium", has(ModItems.POWDER_STRONTIUM.get()))
                .save(pWriter, MODID + ":powder_nitan_mix_alt");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_SPARK_MIX.get(), 3)
                .requires(ModItems.POWDER_DESH.get())
                .requires(ModItems.POWDER_EUPHEMIUM.get())
                .requires(ModItems.POWDER_POWER.get())
                .unlockedBy("has_desh", has(ModItems.POWDER_DESH.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_METEORITE.get(), 4)
                .requires(ModItems.POWDER_IRON.get())
                .requires(ModItems.POWDER_COPPER.get())
                .requires(ModItems.LITHIUM.get())
                .requires(Items.QUARTZ)
                .unlockedBy("has_iron_powder", has(ModItems.POWDER_IRON.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_THERMITE.get(), 4)
                .requires(ModItems.POWDER_IRON.get())
                .requires(ModItems.POWDER_IRON.get())
                .requires(ModItems.POWDER_IRON.get())
                .requires(ModItems.POWDER_ALUMINIUM.get())
                .unlockedBy("has_iron_powder", has(ModItems.POWDER_IRON.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_DESH_MIX.get(), 1)
                .requires(ModItems.POWDER_BORON_TINY.get())
                .requires(ModItems.POWDER_BORON_TINY.get())
                .requires(ModItems.POWDER_LANTHANIUM_TINY.get())
                .requires(ModItems.POWDER_LANTHANIUM_TINY.get())
                .requires(ModItems.POWDER_CERIUM_TINY.get())
                .requires(ModItems.POWDER_COBALT_TINY.get())
                .requires(ModItems.POWDER_LITHIUM_TINY.get())
                .requires(ModItems.POWDER_NEODYMIUM_TINY.get())
                .requires(ModItems.POWDER_NIOBIUM_TINY.get())
                .unlockedBy("has_boron", has(ModItems.POWDER_BORON_TINY.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_DESH_MIX.get(), 9)
                .requires(ModItems.POWDER_BORON.get())
                .requires(ModItems.POWDER_BORON.get())
                .requires(ModItems.POWDER_LANTHANIUM.get())
                .requires(ModItems.POWDER_LANTHANIUM.get())
                .requires(ModItems.POWDER_CERIUM.get())
                .requires(ModItems.POWDER_COBALT.get())
                .requires(ModItems.POWDER_LITHIUM.get())
                .requires(ModItems.POWDER_NEODYMIUM.get())
                .requires(ModItems.POWDER_NIOBIUM.get())
                .unlockedBy("has_boron", has(ModItems.POWDER_BORON.get()))
                .save(pWriter, MODID + ":powder_desh_mix_large");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_DESH_READY.get(), 1)
                .requires(ModItems.POWDER_DESH_MIX.get())
                .requires(ModItems.INGOT_MERCURY.get())
                .requires(ModItems.INGOT_MERCURY.get())
                .requires(ModItems.POWDER_COAL.get())
                .unlockedBy("has_desh_mix", has(ModItems.POWDER_DESH_MIX.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_FLUX.get(), 1)
                .requires(Items.CHARCOAL)
                .requires(ModItemTags.ANY_SAND)
                .unlockedBy("has_charcoal", has(Items.CHARCOAL))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_FLUX.get(), 2)
                .requires(ModItems.POWDER_COAL.get())
                .requires(ModItemTags.ANY_SAND)
                .unlockedBy("has_coal", has(ModItems.POWDER_COAL.get()))
                .save(pWriter, MODID + ":powder_flux_coal");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_FLUX.get(), 4)
                .requires(ModItems.POWDER_FLUORITE.get())
                .requires(ModItemTags.ANY_SAND)
                .unlockedBy("has_fluorite", has(ModItems.POWDER_FLUORITE.get()))
                .save(pWriter, MODID + ":powder_flux_fluorite");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_FLUX.get(), 8)
                .requires(ModItems.POWDER_LEAD.get())
                .requires(ModItems.SULFUR.get())
                .requires(ModItemTags.ANY_SAND)
                .unlockedBy("has_lead", has(ModItems.POWDER_LEAD.get()))
                .save(pWriter, MODID + ":powder_flux_lead");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_FLUX.get(), 12)
                .requires(ModItems.POWDER_LIMESTONE.get())
                .requires(ModItemTags.ANY_SAND)
                .unlockedBy("has_limestone", has(ModItems.POWDER_LIMESTONE.get()))
                .save(pWriter, MODID + ":powder_flux_limestone");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_FLUX.get(), 12)
                .requires(ModItems.POWDER_CALCIUM.get())
                .requires(ModItemTags.ANY_SAND)
                .unlockedBy("has_calcium", has(ModItems.POWDER_CALCIUM.get()))
                .save(pWriter, MODID + ":powder_flux_calcium");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_FLUX.get(), 16)
                .requires(ModItems.POWDER_BORAX.get())
                .requires(ModItemTags.ANY_SAND)
                .unlockedBy("has_borax", has(ModItems.POWDER_BORAX.get()))
                .save(pWriter, MODID + ":powder_flux_borax");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_FERTILIZER.get(), 4)
                .requires(ModItems.POWDER_CALCIUM.get())
                .requires(ModItems.POWDER_FIRE.get())
                .requires(ModItems.NITER.get())
                .requires(ModItems.SULFUR.get())
                .unlockedBy("has_calcium", has(ModItems.POWDER_CALCIUM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_FERTILIZER.get(), 4)
                .requires(AnyAshIngredient.of())
                .requires(ModItems.POWDER_FIRE.get())
                .requires(ModItems.NITER.get())
                .requires(ModItems.SULFUR.get())
                .unlockedBy("has_ash", has(ModItems.POWDER_ASH.get()))
                .save(pWriter, MODID + ":powder_fertilizer_ash");

        if(GeneralConfig.ENABLE_LBSM && GeneralConfig.ENABLE_LBSM_SIMPLE_CRAFTING) {
            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_ADVANCED_ALLOY.get(), 4)
                    .requires(Items.REDSTONE)
                    .requires(ModItems.POWDER_IRON.get())
                    .requires(ModItems.POWDER_COAL.get())
                    .requires(ModItems.POWDER_COPPER.get())
                    .unlockedBy("has_iron", has(ModItems.POWDER_IRON.get()))
                    .save(pWriter, MODID + ":powder_advanced_alloy_1");

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_ADVANCED_ALLOY.get(), 4)
                    .requires(ModItems.POWDER_IRON.get())
                    .requires(ModItems.POWDER_COAL.get())
                    .requires(ModItems.POWDER_RED_COPPER.get())
                    .requires(ModItems.POWDER_RED_COPPER.get())
                    .unlockedBy("has_iron", has(ModItems.POWDER_IRON.get()))
                    .save(pWriter, MODID + ":powder_advanced_alloy_2");

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_ADVANCED_ALLOY.get(), 4)
                    .requires(Items.REDSTONE)
                    .requires(ModItems.POWDER_COPPER.get())
                    .requires(ModItems.POWDER_STEEL.get())
                    .requires(ModItems.POWDER_STEEL.get())
                    .unlockedBy("has_steel", has(ModItems.POWDER_STEEL.get()))
                    .save(pWriter, MODID + ":powder_advanced_alloy_3");

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_ADVANCED_ALLOY.get(), 2)
                    .requires(ModItems.POWDER_RED_COPPER.get())
                    .requires(ModItems.POWDER_STEEL.get())
                    .unlockedBy("has_red_copper", has(ModItems.POWDER_RED_COPPER.get()))
                    .save(pWriter, MODID + ":powder_advanced_alloy_4");

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_RED_COPPER.get(), 2)
                    .requires(Items.REDSTONE)
                    .requires(ModItems.POWDER_COPPER.get())
                    .unlockedBy("has_copper", has(ModItems.POWDER_COPPER.get()))
                    .save(pWriter);

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_DURA_STEEL.get(), 2)
                    .requires(ModItems.POWDER_STEEL.get())
                    .requires(ModItems.POWDER_TUNGSTEN.get())
                    .unlockedBy("has_steel", has(ModItems.POWDER_STEEL.get()))
                    .save(pWriter, MODID + ":powder_dura_steel_tungsten");

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_DURA_STEEL.get(), 2)
                    .requires(ModItems.POWDER_STEEL.get())
                    .requires(ModItems.POWDER_COBALT.get())
                    .unlockedBy("has_steel", has(ModItems.POWDER_STEEL.get()))
                    .save(pWriter, MODID + ":powder_dura_steel_cobalt");

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_DURA_STEEL.get(), 4)
                    .requires(ModItems.POWDER_IRON.get())
                    .requires(ModItems.POWDER_COAL.get())
                    .requires(ModItems.POWDER_TUNGSTEN.get())
                    .requires(ModItems.POWDER_TUNGSTEN.get())
                    .unlockedBy("has_iron", has(ModItems.POWDER_IRON.get()))
                    .save(pWriter, MODID + ":powder_dura_steel_iron_tungsten");

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.POWDER_DURA_STEEL.get(), 4)
                    .requires(ModItems.POWDER_IRON.get())
                    .requires(ModItems.POWDER_COAL.get())
                    .requires(ModItems.POWDER_COBALT.get())
                    .requires(ModItems.POWDER_COBALT.get())
                    .unlockedBy("has_iron", has(ModItems.POWDER_IRON.get()))
                    .save(pWriter, MODID + ":powder_dura_steel_iron_cobalt");

            ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.INGOT_FIREBRICK.get(), 4)
                    .pattern("BN")
                    .pattern("NB")
                    .define('B', Items.BRICK)
                    .define('N', Items.NETHER_BRICK)
                    .unlockedBy("has_brick", has(Items.BRICK))
                    .save(pWriter);
        }
    }
}
