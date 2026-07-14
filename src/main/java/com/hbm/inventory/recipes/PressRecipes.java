package com.hbm.inventory.recipes;

import com.hbm.items.DictFrame;
import com.hbm.items.ModToolItems;
import com.hbm.util.HBMEnums;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;


public class PressRecipes {

    protected static final Map<RecipeKey, ItemStack> recipes = new HashMap<>();

    private record RecipeKey(StampType stampType, Object input) {} // Object может быть TagKey<Item> или ResourceLocation

    static {
        initializeRecipes();
    }

    public static List<PressRecipeWrapper> getAllRecipes() {
        List<PressRecipeWrapper> list = new ArrayList<>();
        for (Map.Entry<RecipeKey, ItemStack> entry : recipes.entrySet()) {
            list.add(new PressRecipeWrapper(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    public static ItemStack getOutput(ItemStack input, ItemStack stamp) {
        if(input.isEmpty() || stamp.isEmpty()) {
            return ItemStack.EMPTY;
        }

        StampType stampType = getStampType(stamp);
        if(stampType == null) {
            return ItemStack.EMPTY;
        }

        // Проверяем рецепты для этого типа штампа
        for(RecipeKey key : recipes.keySet()) {
            if(key.stampType == stampType && matchesInput(input, key.input)) {
                return recipes.get(key).copy();
            }
        }

        return ItemStack.EMPTY;
    }

    public static StampType getStampTypePublic(ItemStack stamp) {
        return getStampType(stamp);
    }

    public static List<Object> getAllInputsForStamp(StampType stampType) {
        List<Object> inputs = new ArrayList<>();
        for (RecipeKey key : recipes.keySet()) {
            if (key.stampType() == stampType) {
                inputs.add(key.input());
            }
        }
        return inputs;
    }

    @SuppressWarnings("unchecked")
    private static boolean matchesInput(ItemStack input, Object recipeInput) {
        if(recipeInput instanceof TagKey) {
            // Проверка по тегу
            return input.is((TagKey<Item>) recipeInput);
        } else if(recipeInput instanceof ResourceLocation) {
            // Проверка по конкретному предмету
            return Objects.equals(ForgeRegistries.ITEMS.getKey(input.getItem()), recipeInput);
        }
        return false;
    }

    private static StampType getStampType(ItemStack stamp) {
        String stampName = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stamp.getItem())).getPath();

        if (stampName.contains("flat")) return StampType.FLAT;
        if (stampName.contains("plate")) return StampType.PLATE;
        if (stampName.contains("wire")) return StampType.WIRE;
        if (stampName.contains("circuit")) return StampType.CIRCUIT;
        if (stampName.contains("stamp_9")) return StampType.C9;
        if (stampName.contains("stamp_50")) return StampType.C50;
        if (stampName.contains("stamp_book")) return StampType.BOOK;
        if (stampName.contains("printing")) {
            // Определяем конкретный printing stamp
            String num = stampName.replace("stamp_printing", "");
            try {
                return StampType.valueOf("PRINTING" + num);
            } catch (IllegalArgumentException e) {
                return null;
            }
        }
        return null;
    }



    private static void initializeRecipes() {
        // === FLAT STAMP RECIPES ===
        addRecipe(StampType.FLAT, ModItemTags.DUSTS_NETHER_QUARTZ, new ItemStack(Items.QUARTZ));
        addRecipe(StampType.FLAT, ModItemTags.DUSTS_LAPIS, new ItemStack(Items.LAPIS_LAZULI));
        addRecipe(StampType.FLAT, ModItemTags.DUSTS_DIAMOND, new ItemStack(Items.DIAMOND));
        addRecipe(StampType.FLAT, ModItemTags.DUSTS_EMERALD, new ItemStack(Items.EMERALD));
        addRecipe(StampType.FLAT, ModItemTags.DUSTS_COAL, DictFrame.fromOne(ModItems.BRIQUETTE.get(), HBMEnums.EnumBriquetteType.COAL));
        addRecipe(StampType.FLAT, ModItemTags.DUSTS_LIGNITE, DictFrame.fromOne(ModItems.BRIQUETTE.get(), HBMEnums.EnumBriquetteType.LIGNITE));

        // Конкретные предметы для FLAT
        addRecipe(StampType.FLAT, ForgeRegistries.ITEMS.getKey(ModItems.BIOMASS.get()),
                new ItemStack(ModItems.BIOMASS_COMPRESSED.get()));
        addRecipe(StampType.FLAT, ForgeRegistries.ITEMS.getKey(ModToolItems.METEORITE_SWORD_REFORGED.get()),
                new ItemStack(ModToolItems.METEORITE_SWORD_HARDENED.get()));
        addRecipe(StampType.FLAT, ForgeRegistries.ITEMS.getKey(ModItems.POWDER_SAWDUST.get()),
                DictFrame.fromOne(ModItems.BRIQUETTE.get(), HBMEnums.EnumBriquetteType.WOOD));
        addRecipe(StampType.FLAT, ForgeRegistries.ITEMS.getKey(Items.JUNGLE_LOG),
                new ItemStack(ModItems.BALL_RESIN.get()));

        // === PLATE STAMP RECIPES ===
        addRecipe(StampType.PLATE, ModItemTags.INGOTS_IRON, new ItemStack(ModItems.PLATE_IRON.get()));
        addRecipe(StampType.PLATE, ModItemTags.INGOTS_GOLD, new ItemStack(ModItems.PLATE_GOLD.get()));
        addRecipe(StampType.PLATE, ModItemTags.INGOTS_COPPER, new ItemStack(ModItems.PLATE_COPPER.get()));
        addRecipe(StampType.PLATE, ModItemTags.INGOTS_STEEL, new ItemStack(ModItems.PLATE_STEEL.get()));
        addRecipe(StampType.PLATE, ModItemTags.INGOTS_LEAD, new ItemStack(ModItems.PLATE_LEAD.get()));
        addRecipe(StampType.PLATE, ModItemTags.INGOTS_ALUMINUM, new ItemStack(ModItems.PLATE_ALUMINIUM.get()));
        addRecipe(StampType.PLATE, ModItemTags.INGOTS_TITANIUM, new ItemStack(ModItems.PLATE_TITANIUM.get()));

        // Конкретные предметы для специальных сплавов
        addRecipe(StampType.PLATE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_ADVANCED_ALLOY.get()),
                new ItemStack(ModItems.PLATE_ADVANCED_ALLOY.get()));
        addRecipe(StampType.PLATE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_SCHRABIDIUM.get()),
                new ItemStack(ModItems.PLATE_SCHRABIDIUM.get()));
        addRecipe(StampType.PLATE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_COMBINE_STEEL.get()),
                new ItemStack(ModItems.PLATE_COMBINE_STEEL.get()));
        addRecipe(StampType.PLATE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_GUNMETAL.get()),
                new ItemStack(ModItems.PLATE_GUNMETAL.get()));
        addRecipe(StampType.PLATE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_WEAPON_STEEL.get()),
                new ItemStack(ModItems.PLATE_WEAPON_STEEL.get()));
        addRecipe(StampType.PLATE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_SATURNITE.get()),
                new ItemStack(ModItems.PLATE_SATURNITE.get()));
        addRecipe(StampType.PLATE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_DURA_STEEL.get()),
                new ItemStack(ModItems.PLATE_DURA_STEEL.get()));

        // === WIRE STAMP RECIPES ===
        addRecipe(StampType.WIRE, ModItemTags.INGOTS_COPPER, new ItemStack(ModItems.WIRE_COPPER.get(), 8));
        addRecipe(StampType.WIRE, ModItemTags.INGOTS_GOLD, new ItemStack(ModItems.WIRE_GOLD.get(), 8));


        // Конкретные предметы для специальных проводов
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_TUNGSTEN.get()),
                new ItemStack(ModItems.WIRE_TUNGSTEN.get(), 8));
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_GRAPHITE.get()),
                new ItemStack(ModItems.WIRE_CARBON.get(), 8));
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_ADVANCED_ALLOY.get()),
                new ItemStack(ModItems.WIRE_ADVANCED_ALLOY.get(), 8));
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_RED_COPPER.get()),
                new ItemStack(ModItems.WIRE_RED_COPPER.get(), 8));
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_STEEL.get()),
                new ItemStack(ModItems.WIRE_STEEL.get(), 8));
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_ALUMINIUM.get()),
                new ItemStack(ModItems.WIRE_ALUMINIUM.get(), 8));
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_LEAD.get()),
                new ItemStack(ModItems.WIRE_LEAD.get(), 8));
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_ZIRCONIUM.get()),
                new ItemStack(ModItems.WIRE_ZIRCONIUM.get(), 8));
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_SCHRABIDIUM.get()),
                new ItemStack(ModItems.WIRE_SCHRABIDIUM.get(), 8));
        addRecipe(StampType.WIRE, ForgeRegistries.ITEMS.getKey(ModItems.INGOT_MAGNETIZED_TUNGSTEN.get()),
                new ItemStack(ModItems.WIRE_MAGNETIZED_TUNGSTEN.get(), 8));


        // === CIRCUIT STAMP RECIPES ===
        addRecipe(StampType.CIRCUIT, ForgeRegistries.ITEMS.getKey(ModItems.BILLET_SILICON.get()),
                new ItemStack(ModItems.CIRCUIT_SILICON.get()));

        // === CALIBER STAMP RECIPES ===
        addRecipe(StampType.C9, ForgeRegistries.ITEMS.getKey(ModItems.PLATE_GUNMETAL.get()),
                new ItemStack(ModItems.CASING_SMALL.get(), 4));
        addRecipe(StampType.C50, ForgeRegistries.ITEMS.getKey(ModItems.PLATE_GUNMETAL.get()),
                new ItemStack(ModItems.CASING_LARGE.get(), 2));
        addRecipe(StampType.C9, ForgeRegistries.ITEMS.getKey(ModItems.PLATE_STEEL.get()),
                new ItemStack(ModItems.CASING_SMALL_STEEL.get(), 4));
        addRecipe(StampType.C50, ForgeRegistries.ITEMS.getKey(ModItems.PLATE_STEEL.get()),
                new ItemStack(ModItems.CASING_LARGE_STEEL.get(), 2));

        // === PRINTING STAMP RECIPES ===
        addRecipe(StampType.BOOK, ForgeRegistries.ITEMS.getKey(Items.PAPER),
                new ItemStack(ModItems.BOOK_GUIDE.get()));

        // Страницы
        for(int i = 1; i <= 8; i++) {
            addRecipe(StampType.valueOf("PRINTING" + i), ForgeRegistries.ITEMS.getKey(Items.PAPER),
                    DictFrame.fromOne(ModItems.PAGE_OF.get(), HBMEnums.EnumPages.values()[i-1]));
        }
    }

    private static void addRecipe(StampType stamp, TagKey<Item> input, ItemStack output) {
        recipes.put(new RecipeKey(stamp, input), output);
    }

    private static void addRecipe(StampType stamp, ResourceLocation input, ItemStack output) {
        recipes.put(new RecipeKey(stamp, input), output);
    }

    public enum StampType {
        FLAT, PLATE, WIRE, CIRCUIT, C9, C50, BOOK,
        PRINTING1, PRINTING2, PRINTING3, PRINTING4,
        PRINTING5, PRINTING6, PRINTING7, PRINTING8
    }

    public static class PressRecipeWrapper {
        private final RecipeKey key;
        private final ItemStack output;

        public PressRecipeWrapper(RecipeKey key, ItemStack output) {
            this.key = key;
            this.output = output;
        }

        public StampType getStampType() { return key.stampType(); }
        public Object getInput() { return key.input(); }
        public ItemStack getOutput() { return output.copy(); }
    }
}