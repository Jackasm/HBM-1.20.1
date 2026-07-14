package com.hbm.integration.jei;

import mezz.jei.api.recipe.RecipeType;

import static com.hbm.util.RefStrings.MODID;

public class HBMJeiRecipeTypes {
    public static final RecipeType<HBMRecipeWrapper> ANVIL =
            RecipeType.create(MODID, "anvil_ntm", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> ANVIL_CONSTRUCTION =
            RecipeType.create(MODID, "anvil_construction", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> PRESS =
            RecipeType.create(MODID, "press", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> BLAST_FURNACE =
            RecipeType.create(MODID, "blast_furnace", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> COMBINATION_FURNACE =
            RecipeType.create(MODID, "combination_furnace", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> ROTARY_FURNACE =
            RecipeType.create(MODID, "rotary_furnace", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> CRUCIBLE =
            RecipeType.create(MODID, "crucible", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> SOLDERING_STATION =
            RecipeType.create(MODID, "soldering_station", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> ARC_WELDER =
            RecipeType.create(MODID, "arc_welder", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> GAS_CENTRIFUGE =
            RecipeType.create(MODID, "gas_centrifuge", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> CRYSTALLIZER =
            RecipeType.create(MODID, "crystallizer", HBMRecipeWrapper.class);

    public static final RecipeType<HBMRecipeWrapper> CENTRIFUGE =
            RecipeType.create(MODID, "centrifuge", HBMRecipeWrapper.class);

}