package com.hbm.integration.jei.press;

import com.hbm.integration.jei.HBMRecipeWrapper;
import com.hbm.inventory.recipes.PressRecipes;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

import static com.hbm.util.ResLocation.ResLocation;

public class PressRecipeHandler {

    public static List<HBMRecipeWrapper> getRecipes() {
        List<HBMRecipeWrapper> recipes = new ArrayList<>();

        // Группируем рецепты по типу операции (input + output + stamp type)
        Map<RecipeKey, List<ItemStack>> recipeGroups = new HashMap<>();

        // Получаем все возможные штампы
        List<ItemStack> allStamps = getAllStampItems();

        // Собираем все рецепты и группируем их
        for (ItemStack stamp : allStamps) {
            PressRecipes.StampType stampType = PressRecipes.getStampTypePublic(stamp);
            if (stampType == null) continue;

            List<Object> inputs = PressRecipes.getAllInputsForStamp(stampType);

            for (Object input : inputs) {
                List<ItemStack> inputStacks = convertRecipeInputToItemStacks(input);
                for (ItemStack inputStack : inputStacks) {
                    ItemStack output = PressRecipes.getOutput(inputStack, stamp);
                    if (!output.isEmpty()) {
                        // Создаем ключ для группировки (одинаковый input + output + stamp type)
                        RecipeKey key = new RecipeKey(inputStack, output, stampType);

                        // Добавляем штамп в группу
                        recipeGroups.computeIfAbsent(key, k -> new ArrayList<>())
                                .add(stamp.copy());
                    }
                }
            }
        }

        // Создаем анимированные рецепты для каждой группы
        for (Map.Entry<RecipeKey, List<ItemStack>> entry : recipeGroups.entrySet()) {
            RecipeKey key = entry.getKey();
            List<ItemStack> stampVariants = entry.getValue();

            // Сортируем штампы по материалу для красивого отображения
            stampVariants.sort(Comparator.comparing(stack ->
                    BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath()
            ));

            recipes.add(new PressRecipeWrapper(
                    key.input.copy(),
                    stampVariants,
                    key.output.copy()
            ));
        }

        return recipes;
    }

    // Вспомогательный класс для группировки рецептов
        private record RecipeKey(ItemStack input, ItemStack output, PressRecipes.StampType stampType) {

        @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (o == null || getClass() != o.getClass()) return false;
                RecipeKey recipeKey = (RecipeKey) o;
                return ItemStack.isSameItemSameTags(input, recipeKey.input) &&
                        ItemStack.isSameItemSameTags(output, recipeKey.output) &&
                        stampType == recipeKey.stampType;
            }

            @Override
            public int hashCode() {
                return Objects.hash(
                        BuiltInRegistries.ITEM.getKey(input.getItem()),
                        BuiltInRegistries.ITEM.getKey(output.getItem()),
                        stampType
                );
            }
        }

    private static List<ItemStack> getAllStampItems() {
        List<ItemStack> stamps = new ArrayList<>();

        // Материалы штампов
        String[] materials = {"stone", "iron", "steel", "titanium", "obsidian", "desh"};
        String[] types = {"flat", "plate", "wire", "circuit"};

        // Добавляем штампы по материалам и типам
        for (String material : materials) {
            for (String type : types) {
                addStampIfExists(stamps, "stamp_" + material + "_" + type);
            }
        }

        // CALIBER stamps
        addStampIfExists(stamps, "stamp_9");
        addStampIfExists(stamps, "stamp_50");
        addStampIfExists(stamps, "stamp_44");
        addStampIfExists(stamps, "stamp_357");

        // BOOK stamp
        addStampIfExists(stamps, "stamp_book");

        // PRINTING stamps
        for (int i = 1; i <= 8; i++) {
            addStampIfExists(stamps, "stamp_printing" + i);
        }

        return stamps;
    }

    private static void addStampIfExists(List<ItemStack> stamps, String stampName) {
        Item stampItem = BuiltInRegistries.ITEM.get(ResLocation(RefStrings.MODID, stampName));
        if (stampItem != Items.AIR) {
            stamps.add(new ItemStack(stampItem));
        }
    }

    @SuppressWarnings("unchecked")
    private static List<ItemStack> convertRecipeInputToItemStacks(Object input) {
        List<ItemStack> stacks = new ArrayList<>();

        if (input instanceof TagKey) {
            // Для тегов добавляем несколько представительных предметов
            TagKey<Item> tag = (TagKey<Item>) input;
            addRepresentativeItemsFromTag(tag, stacks);

        } else if (input instanceof ResourceLocation itemId) {
            // Для конкретных предметов просто добавляем их
            Item item = BuiltInRegistries.ITEM.get(itemId);
            if (item != Items.AIR) {
                stacks.add(new ItemStack(item));
            }
        }

        return stacks;
    }

    private static void addRepresentativeItemsFromTag(TagKey<Item> tag, List<ItemStack> stacks) {
        // Добавляем до 3 представительных предметов из тега
        int count = 0;
        for (Holder<Item> holder : BuiltInRegistries.ITEM.getTagOrEmpty(tag)) {
            if (count >= 3) break; // Ограничиваем количество чтобы не засорять JEI
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

        // Добавляем известные предметы для распространённых тегов
        if (tagPath.contains("dust")) {
            // Пыли
            if (tagPath.contains("iron")) stacks.add(new ItemStack(Items.IRON_INGOT));
            else if (tagPath.contains("gold")) stacks.add(new ItemStack(Items.GOLD_INGOT));
            else if (tagPath.contains("copper")) stacks.add(new ItemStack(Items.COPPER_INGOT));
            else if (tagPath.contains("quartz")) stacks.add(new ItemStack(Items.QUARTZ));
            else if (tagPath.contains("lapis")) stacks.add(new ItemStack(Items.LAPIS_LAZULI));
            else if (tagPath.contains("diamond")) stacks.add(new ItemStack(Items.DIAMOND));
            else if (tagPath.contains("emerald")) stacks.add(new ItemStack(Items.EMERALD));
            else if (tagPath.contains("coal")) stacks.add(new ItemStack(Items.COAL));

        } else if (tagPath.contains("ingot")) {
            // Слитки
            if (tagPath.contains("iron")) stacks.add(new ItemStack(Items.IRON_INGOT));
            else if (tagPath.contains("gold")) stacks.add(new ItemStack(Items.GOLD_INGOT));
            else if (tagPath.contains("copper")) stacks.add(new ItemStack(Items.COPPER_INGOT));
            else if (tagPath.contains("steel")) stacks.add(new ItemStack(ModItems.INGOT_STEEL.get()));
            else if (tagPath.contains("lead")) stacks.add(new ItemStack(ModItems.INGOT_LEAD.get()));
            else if (tagPath.contains("aluminum") || tagPath.contains("aluminium")) stacks.add(new ItemStack(ModItems.INGOT_ALUMINIUM.get()));
            else if (tagPath.contains("titanium")) stacks.add(new ItemStack(ModItems.INGOT_TITANIUM.get()));
        }

        // Если всё равно пусто, добавляем заглушку
        if (stacks.isEmpty()) {
            stacks.add(new ItemStack(Items.BARRIER)); // Визуальный индикатор что что-то не так
        }
    }
}