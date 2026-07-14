package com.hbm.inventory.recipes;

import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.OreDictStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.ItemEnumMulti;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

public class RecipeDisplayUtil {

    /**
     * Преобразует AStack в список ItemStack для отображения
     */
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
            int count = 0;
            for (Item item : items) {
                if (count >= 5) break;
                if (item instanceof ItemEnumMulti<?> multi) {
                    Class<?> enumClass = multi.theEnum;
                    Object[] enumConstants = enumClass.getEnumConstants();
                    for (Object constant : enumConstants) {
                        if (count >= 5) break;
                        ItemStack variant = new ItemStack(item, requiredSize);
                        variant.getOrCreateTag().putInt("CustomModelData", ((Enum<?>) constant).ordinal());
                        result.add(variant);
                        count++;
                    }
                } else {
                    ItemStack stack = new ItemStack(item, requiredSize);
                    result.add(stack);
                    count++;
                }
            }
        }
        return result;
    }

    /**
     * Преобразует MaterialStack в ItemStack для отображения
     */
    public static ItemStack getStackFromMaterial(MaterialStack materialStack) {
        return getStackFromMaterial(materialStack, "ingots");
    }

    public static ItemStack getStackFromMaterial(MaterialStack materialStack, String shape) {
        if (materialStack == null || materialStack.material == null) return ItemStack.EMPTY;

        String matName = materialStack.material.names[0];
        int amount = materialStack.amount;
        int quantity = Math.max(1, amount / 144);

        // Пробуем тег forge:<shape>/<matName>
        TagKey<Item> tag = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(),
                ResLocation("forge", shape + "/" + matName));
        var tagEntries = Objects.requireNonNull(ForgeRegistries.ITEMS.tags()).getTag(tag);
        var iterator = tagEntries.iterator();
        if (iterator.hasNext()) {
            return new ItemStack(iterator.next(), quantity);
        }

        // Пробуем hbm:<singularShape>_<matName>
        String singularShape = shape.endsWith("s") ? shape.substring(0, shape.length() - 1) : shape;
        var itemId = ResLocation("hbm", singularShape + "_" + matName);
        if (ForgeRegistries.ITEMS.containsKey(itemId)) {
            Item item = ForgeRegistries.ITEMS.getValue(itemId);
            return new ItemStack(Objects.requireNonNull(item), quantity);
        }

        // Fallback - ItemScraps
        return com.hbm.items.machine.ItemScraps.create(materialStack);
    }

    // === Получение выходов для всех типов рецептов ===
    public static List<ItemStack> getDisplayItems(Object recipeObject) {
        List<ItemStack> result = new ArrayList<>();

        // 1. CrucibleRecipe
        if (recipeObject instanceof CrucibleRecipes.CrucibleRecipe crucible) {
            if (crucible.output != null) {
                for (MaterialStack stack : crucible.output) {
                    ItemStack item = getStackFromMaterial(stack);
                    if (!item.isEmpty()) result.add(item);
                }
            }
            return result;
        }

        // 2. SolderingRecipe
        if (recipeObject instanceof SolderingRecipes.SolderingRecipe soldering) {
            if (soldering.output != null) {
                result.add(soldering.output.copy());
            }
            return result;
        }

        // 3. PressRecipeWrapper
        if (recipeObject instanceof PressRecipes.PressRecipeWrapper press) {
            result.add(press.getOutput().copy());
            return result;
        }

        // 4. CombinationRecipeWrapper
        if (recipeObject instanceof CombinationRecipes.CombinationRecipeWrapper combo) {
            ItemStack item = combo.getOutputItem();
            if (item != null && !item.isEmpty()) {
                result.add(item.copy());
            }
            return result;
        }

        // 5. GasCentrifugeRecipeWrapper
        if (recipeObject instanceof GasCentrifugeRecipeWrapper gas) {
            return gas.getDisplayOutputs();
        }

        // 6. CentrifugeRecipeWrapper
        if (recipeObject instanceof CentrifugeRecipes.CentrifugeRecipeWrapper cent) {
            return cent.getOutputs();
        }

        // 7. AnvilSmithingRecipe
        if (recipeObject instanceof AnvilRecipes.AnvilSmithingRecipe smithing) {
            result.add(smithing.getSimpleOutput().copy());
            return result;
        }

        // 8. AnvilConstructionRecipe
        if (recipeObject instanceof AnvilRecipes.AnvilConstructionRecipe construction) {
            for (AnvilRecipes.AnvilOutput output : construction.output) {
                result.add(output.getStack().copy());
            }
            return result;
        }

        // 9. BlastFurnaceRecipe (если есть)
        if (recipeObject instanceof BlastFurnaceRecipes.BlastFurnaceRecipe blast) {
            ItemStack output = blast.output(); // record -> метод output()
            if (output != null && !output.isEmpty()) {
                result.add(output.copy());
            }
            return result;
        }

        // 10. RotaryFurnaceRecipe (если есть)
        if (recipeObject instanceof RotaryFurnaceRecipes.RotaryFurnaceRecipe rotary) {
            if (rotary.output != null) {
                ItemStack item = getStackFromMaterial(rotary.output);
                if (!item.isEmpty()) result.add(item);
            }
            return result;
        }

        // 11. ArcWelderRecipe (если есть)
        if (recipeObject instanceof ArcWelderRecipes.ArcWelderRecipeWrapper arc) {
            result.add(arc.getOutputItem().copy());
            return result;
        }

        // 12. CrystallizerRecipe (если есть)
        if (recipeObject instanceof CrystallizerRecipes.CrystallizerRecipe crystallizer) {
            if (crystallizer.output != null) {
                result.add(crystallizer.output.copy());
            }
            return result;
        }

        return result;
    }

    /**
     * Получить ингредиенты из рецепта для отображения
     */
    public static List<ItemStack> getDisplayIngredients(Object recipeObject) {
        List<ItemStack> result = new ArrayList<>();

        if (recipeObject instanceof CrucibleRecipes.CrucibleRecipe crucible) {
            if (crucible.input != null) {
                for (MaterialStack stack : crucible.input) {
                    ItemStack item = getStackFromMaterial(stack);
                    if (!item.isEmpty()) result.add(item);
                }
            }
            return result;
        }

        if (recipeObject instanceof SolderingRecipes.SolderingRecipe soldering) {
            // Toppings
            if (soldering.toppings != null) {
                for (AStack aStack : soldering.toppings) {
                    result.addAll(getStacksFromAStack(aStack));
                }
            }
            // PCB
            if (soldering.pcb != null) {
                for (AStack aStack : soldering.pcb) {
                    result.addAll(getStacksFromAStack(aStack));
                }
            }
            // Solder
            if (soldering.solder != null) {
                for (AStack aStack : soldering.solder) {
                    result.addAll(getStacksFromAStack(aStack));
                }
            }
            return result;
        }

        // Добавьте другие типы рецептов

        return result;
    }
}