package com.hbm.inventory.recipes.common;

import com.hbm.items.ModItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;

import java.util.List;

/**
 * Абстрактный класс для ингредиентов рецептов
 */
public abstract class AStack {

    public int stacksize = 1;
    public abstract boolean matchesRecipe(ItemStack stack, boolean container);
    public abstract List<ItemStack> extractForNEI();
    public abstract int getStackSize();
    public abstract AStack copy();
    public abstract boolean isIngredientSame(AStack stack);

    // Статические методы для создания ингредиентов
    public static AStack of(TagKey<Item> tag) {
        return new TagStack(tag);
    }

    public static AStack of(TagKey<Item> tag, int count) {
        return new TagStack(tag, count);
    }

    public static AStack of(Item item) {
        return new ComparableStack(item);
    }

    public static AStack of(Item item, int count) {
        return new ComparableStack(item, count);
    }

    public static AStack of(ItemStack stack) {
        return new ComparableStack(stack);
    }

    // Новые методы для блоков
    public static AStack of(Block block) {
        return new ComparableStack(block);
    }

    public static AStack of(Block block, int count) {
        return new ComparableStack(block, count);
    }

    public ItemStack extractForCyclingDisplay(int cycle) {
        List<ItemStack> list = extractForNEI();
        cycle *= 50;

        if(list.isEmpty()) return new ItemStack(ModItems.NOTHING.get());
        return list.get((int)(System.currentTimeMillis() % (cycle * list.size()) / cycle));
    }
}