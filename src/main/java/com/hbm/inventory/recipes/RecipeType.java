package com.hbm.inventory.recipes;

import java.util.*;
import java.util.function.Supplier;

public enum RecipeType {
    ANVIL("Наковальня", () -> {
        List<Object> all = new ArrayList<>();
        all.addAll(AnvilRecipes.getSmithing());
        all.addAll(AnvilRecipes.getConstruction());
        // fluidRecipes уже включены в getConstruction()? Проверьте: в getConstruction() вы добавляете fluidRecipes,
        // так что достаточно.
        return all;
    }),

    PRESS("Пресс", PressRecipes::getAllRecipes),
    BLAST_FURNACE("Плавильная печь", () -> BlastFurnaceRecipes.recipes),
    COMBINATION_FURNACE("Комбинированная печь", CombinationRecipes::getAllRecipes),
    ROTARY_FURNACE("Роторная печь", () -> RotaryFurnaceRecipes.recipes),
    CRUCIBLE("Тигель", () -> CrucibleRecipes.recipes),
    SOLDERING_STATION("Паяльная станция", () -> SolderingRecipes.recipes),
    ARC_WELDER("Дуговая сварка", () -> ArcWelderRecipes.recipes),
    GAS_CENTRIFUGE("Газовая центрифуга", GasCentrifugeRecipes::getAllRecipes),
    CRYSTALLIZER("Кристаллизатор", CrystallizerRecipes::getAllRecipes),
    CENTRIFUGE("Центрифуга", CentrifugeRecipes::getAllRecipes);

    private final String displayName;
    private final Supplier<List<?>> recipeSupplier;

    RecipeType(String displayName, Supplier<List<?>> recipeSupplier) {
        this.displayName = displayName;
        this.recipeSupplier = recipeSupplier;
    }

    public String getDisplayName() { return displayName; }
    public List<?> getRecipes() { return recipeSupplier.get(); }
    public boolean hasRecipes() { return !getRecipes().isEmpty(); }
}