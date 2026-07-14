package com.hbm.integration.jei.BlastFurnace;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.recipes.BlastFurnaceRecipes;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class BlastFurnaceRecipeHandler {

    private static final List<ItemStack> FUEL_VARIANTS = createFuelVariants();

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        Map<Item, List<BlastFurnaceRecipes.BlastFurnaceRecipe>> recipeGroups = new HashMap<>();

        List<BlastFurnaceRecipes.BlastFurnaceRecipe> allRecipes = BlastFurnaceRecipes.getRecipes();

        for (BlastFurnaceRecipes.BlastFurnaceRecipe recipe : allRecipes) {
            if (BlastFurnaceRecipes.isRecipeHidden(recipe.output())) {
                continue;
            }

            Item outputItem = recipe.output().getItem();
            recipeGroups.computeIfAbsent(outputItem, k -> new ArrayList<>()).add(recipe);
        }

        for (Map.Entry<Item, List<BlastFurnaceRecipes.BlastFurnaceRecipe>> groupEntry : recipeGroups.entrySet()) {
            Item outputItem = groupEntry.getKey();
            List<BlastFurnaceRecipes.BlastFurnaceRecipe> groupRecipes = groupEntry.getValue();

            List<RecipeEntry> recipeEntries = new ArrayList<>();

            for (BlastFurnaceRecipes.BlastFurnaceRecipe recipe : groupRecipes) {
                List<ItemStack> input1Stacks = convertRecipeInputToItemStacks(recipe.input1());
                List<ItemStack> input2Stacks = convertRecipeInputToItemStacks(recipe.input2());

                for (ItemStack input1 : input1Stacks) {
                    for (ItemStack input2 : input2Stacks) {
                        RecipeEntry recipeEntry = new RecipeEntry(
                                input1.copy(),
                                input2.copy(),
                                recipe.output().copy()
                        );

                        if (!containsRecipeEntry(recipeEntries, recipeEntry)) {
                            recipeEntries.add(recipeEntry);
                        }
                    }
                }
            }

            recipeEntries.sort(Comparator.comparing(e -> BuiltInRegistries.ITEM.getKey(e.input1.getItem()).getPath()));

            List<ItemStack> allInput1 = new ArrayList<>();
            List<ItemStack> allInput2 = new ArrayList<>();
            List<ItemStack> allOutputs = new ArrayList<>();

            for (RecipeEntry recipeEntry : recipeEntries) {
                allInput1.add(recipeEntry.input1);
                allInput2.add(recipeEntry.input2);
                allOutputs.add(recipeEntry.output);
            }

            ItemStack baseOutput = new ItemStack(outputItem, 1);

            recipes.add(new BlastFurnaceRecipeWrapper(allInput1, allInput2, FUEL_VARIANTS, allOutputs, baseOutput));
        }

        return recipes;
    }

    // Создаем список вариантов топлива
    private static List<ItemStack> createFuelVariants() {
        List<ItemStack> fuels = new ArrayList<>();

        // Добавляем все виды топлива из вашей реализации
        fuels.add(new ItemStack(Items.COAL));
        fuels.add(new ItemStack(Items.CHARCOAL));
        fuels.add(new ItemStack(Items.COAL_BLOCK));
        fuels.add(new ItemStack(Items.BLAZE_ROD));
        fuels.add(new ItemStack(Items.BLAZE_POWDER));
        fuels.add(new ItemStack(Items.LAVA_BUCKET));

        // Также можно добавить кастомное топливо из HBM, если есть
        addItemIfExists(fuels, "hbm:coke_coal");
        addItemIfExists(fuels, "hbm:coke_lignite");
        addItemIfExists(fuels, "hbm:coke_petroleum");
        addItemIfExists(fuels, "hbm:solid_fuel");

        return fuels;
    }

    // Вспомогательный класс для хранения комбинаций рецептов
    private static class RecipeEntry {
        public final ItemStack input1;
        public final ItemStack input2;
        public final ItemStack output;

        public RecipeEntry(ItemStack input1, ItemStack input2, ItemStack output) {
            this.input1 = input1;
            this.input2 = input2;
            this.output = output;
        }
    }

    private static boolean containsRecipeEntry(List<RecipeEntry> list, RecipeEntry entry) {
        for (RecipeEntry existing : list) {
            if (ItemStack.isSameItemSameTags(existing.input1, entry.input1) &&
                    ItemStack.isSameItemSameTags(existing.input2, entry.input2) &&
                    ItemStack.isSameItemSameTags(existing.output, entry.output)) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static List<ItemStack> convertRecipeInputToItemStacks(Object input) {
        List<ItemStack> stacks = new ArrayList<>();

        if (input instanceof TagKey) {
            // Для тегов добавляем несколько представительных предметов
            TagKey<Item> tag = (TagKey<Item>) input;
            addRepresentativeItemsFromTag(tag, stacks);

        } else if (input instanceof ResourceLocation itemId) {
            // Для конкретных предметов по ResourceLocation
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item != Items.AIR) {
                stacks.add(new ItemStack(item));
            }

        } else if (input instanceof Item item) {
            // Для конкретных предметов
            stacks.add(new ItemStack(item));

        } else if (input instanceof ItemStack itemStack) {
            // Для ItemStack (например, канистры с жидкостью)
            stacks.add(itemStack.copy());
        }

        // Если список пустой, добавляем заглушку
        if (stacks.isEmpty()) {
            stacks.add(new ItemStack(Items.BARRIER));
        }

        return stacks;
    }

    private static void addRepresentativeItemsFromTag(TagKey<Item> tag, List<ItemStack> stacks) {
        // Добавляем до 3 представительных предметов из тега
        int count = 0;
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            if (count >= 3) break;
            Item item = holder.value();
            stacks.add(new ItemStack(item));
            count++;
        }

        // Если не нашли предметов по тегу, пытаемся найти через известные теги
        if (stacks.isEmpty()) {
            addFallbackItemsForTag(tag, stacks);
        }
    }

    private static void addFallbackItemsForTag(TagKey<Item> tag, List<ItemStack> stacks) {
        String tagPath = tag.location().getPath();

        // Добавляем известные предметы для распространённых тегов доменной печи
        if (tagPath.contains("coal")) {
            stacks.add(new ItemStack(Items.COAL));
        } else if (tagPath.contains("coke") || tagPath.contains("any_coke")) {
            // Добавляем различные виды кокса из HBM
            addItemIfExists(stacks, "hbm:coke_coal");
            addItemIfExists(stacks, "hbm:coke_lignite");
            addItemIfExists(stacks, "hbm:coke_petroleum");
        } else if (tagPath.contains("iron") && tagPath.contains("ore")) {
            stacks.add(new ItemStack(Items.IRON_ORE));
            addItemIfExists(stacks, "hbm:ore_iron");
            addItemIfExists(stacks, "hbm:ore_gneiss_iron");
        } else if (tagPath.contains("copper") && tagPath.contains("ingot")) {
            stacks.add(new ItemStack(Items.COPPER_INGOT));
            addItemIfExists(stacks, "hbm:ingot_copper");
        } else if (tagPath.contains("redstone") && tagPath.contains("dust")) {
            stacks.add(new ItemStack(Items.REDSTONE));
        }

        // Если всё равно пусто, добавляем заглушку
        if (stacks.isEmpty()) {
            stacks.add(new ItemStack(Items.BARRIER));
        }
    }

    private static void addItemIfExists(List<ItemStack> stacks, String itemId) {
        Item item = BuiltInRegistries.ITEM.get(ResourceLocation.parse(itemId));
        if (item != Items.AIR) {
            stacks.add(new ItemStack(item));
        }
    }
}