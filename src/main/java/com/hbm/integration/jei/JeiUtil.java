package com.hbm.integration.jei;

import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.OreDictStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.ItemEnumMulti;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

public class JeiUtil {

    public static List<ItemStack> getStacksFromAStack(AStack aStack) {
        List<ItemStack> result = new ArrayList<>();
        if (aStack == null) return result;

        if (aStack instanceof ComparableStack comp) {
            ItemStack stack = comp.toStack();
            if (!stack.isEmpty()) {
                stack.setCount(aStack.getStackSize());
                result.add(stack);
            }
        }
        else if (aStack instanceof OreDictStack ore) {
            List<ItemStack> stacks = ore.toStacks();
            int requiredSize = aStack.getStackSize();
            for (ItemStack stack : stacks) {
                ItemStack copy = stack.copy();
                copy.setCount(requiredSize);
                result.add(copy);
            }
            if (result.size() > 5) result = result.subList(0, 5);
        }
        else if (aStack instanceof TagStack tagStack) {
            TagKey<Item> tag = tagStack.tag;
            var items = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tag);
            int requiredSize = tagStack.stacksize;
            for (Item item : items) {
                if (item instanceof ItemEnumMulti<?> multi) {
                    // Разворачиваем все enum-варианты
                    Class<?> enumClass = multi.theEnum;
                    Object[] enumConstants = enumClass.getEnumConstants();
                    for (Object constant : enumConstants) {
                        ItemStack variant = new ItemStack(item, requiredSize);
                        variant.getOrCreateTag().putInt("CustomModelData", ((Enum<?>) constant).ordinal());
                        result.add(variant);
                    }
                } else {
                    ItemStack stack = new ItemStack(item, requiredSize);
                    result.add(stack);
                }
            }
            if (result.size() > 5) result = result.subList(0, 5);
        }
        return result;
    }

    public static ItemStack getItemStackFromMaterial(com.hbm.inventory.material.Mats.MaterialStack materialStack) {
        return getItemStackFromMaterial(materialStack, "ingots");
    }

    public static ItemStack getItemStackFromMaterial(com.hbm.inventory.material.Mats.MaterialStack materialStack, String shape) {
        if (materialStack == null || materialStack.material == null) return ItemStack.EMPTY;

        String matName = materialStack.material.names[0];
        int amount = materialStack.amount;

        // Определяем количество штук (для слитков 144 = 1 шт)
        int quantity = Math.max(1, amount / 144);

        // 1. Пробуем найти по тегу forge:<shape>/<matName>
        TagKey<Item> tag = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), ResLocation("forge", shape + "/" + matName));
        var tagEntries = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tag);
        var iterator = tagEntries.iterator();
        if (iterator.hasNext()) {
            return new ItemStack(iterator.next(), quantity);
        }

        // 2. Пробуем найти по имени <shape>_<matName>
        // Убираем последнюю 's' у shape, если она есть
        String singularShape = shape.endsWith("s") ? shape.substring(0, shape.length() - 1) : shape;
        ResourceLocation itemId = ResLocation("hbm", singularShape + "_" + matName);
        if (ForgeRegistries.ITEMS.containsKey(itemId)) {
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            return new ItemStack(Objects.requireNonNull(item), quantity);
        }

        // 3. Возвращаем ItemScraps как fallback
        return com.hbm.items.machine.ItemScraps.create(materialStack);
    }
}