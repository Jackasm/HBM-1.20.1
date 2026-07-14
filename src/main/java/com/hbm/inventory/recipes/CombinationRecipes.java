package com.hbm.inventory.recipes;

import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.DictFrame;
import com.hbm.items.ModItems;
import com.hbm.util.HBMEnums;
import com.hbm.util.Tuple;
import com.hbm.util.Tuple.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CombinationRecipes {

    public static HashMap<Object, Tuple.Pair<ItemStack, FluidStackHBM>> recipes = new HashMap<>();

    public static final String KEY_LOG = "logs";
    public static final String KEY_SAPLING = "saplings";

    public static void register() {

        recipes.put(new ComparableStack(Items.COAL), new Pair<>(DictFrame.fromOne(ModItems.COKE.get(), HBMEnums.EnumCokeType.COAL),
                new FluidStackHBM(Fluids.COALCREOSOTE.get(), 100)));
        recipes.put(new ComparableStack(ModItems.POWDER_COAL.get()), new Pair<>(DictFrame.fromOne(ModItems.COKE.get(),
                HBMEnums.EnumCokeType.COAL), new FluidStackHBM(Fluids.COALCREOSOTE.get(), 100)));
        recipes.put(new ComparableStack(DictFrame.fromOne(ModItems.BRIQUETTE.get(), HBMEnums.EnumBriquetteType.COAL)),
                new Pair<>(DictFrame.fromOne(ModItems.COKE.get(), HBMEnums.EnumCokeType.COAL), new FluidStackHBM(Fluids.COALCREOSOTE.get(), 150)));

        recipes.put(new ComparableStack(ModItems.LIGNITE.get()), new Pair<>(DictFrame.fromOne(ModItems.COKE.get(),
                HBMEnums.EnumCokeType.LIGNITE), new FluidStackHBM(Fluids.COALCREOSOTE.get(), 50)));
        recipes.put(new ComparableStack(ModItems.POWDER_LIGNITE.get()), new Pair<>(DictFrame.fromOne(ModItems.COKE.get(),
                HBMEnums.EnumCokeType.LIGNITE), new FluidStackHBM(Fluids.COALCREOSOTE.get(), 50)));
        recipes.put(new ComparableStack(DictFrame.fromOne(ModItems.BRIQUETTE.get(), HBMEnums.EnumBriquetteType.LIGNITE)),
                new Pair<>(DictFrame.fromOne(ModItems.COKE.get(), HBMEnums.EnumCokeType.LIGNITE),
                        new FluidStackHBM(Fluids.COALCREOSOTE.get(), 100)));


        recipes.put(new ComparableStack(ModItems.POWDER_CHLOROCALCITE.get()), new Pair<>(new ItemStack(ModItems.POWDER_CALCIUM.get()), new FluidStackHBM(Fluids.CHLORINE.get(), 250)));
        recipes.put(new ComparableStack(ModItems.POWDER_MOLYSITE.get()), new Pair<>(new ItemStack(Items.IRON_INGOT), new FluidStackHBM(Fluids.CHLORINE.get(), 250)));
        recipes.put(new ComparableStack(ModItems.CRYSTAL_CINNABAR.get()), new Pair<>(new ItemStack(ModItems.SULFUR.get()), new FluidStackHBM(Fluids.MERCURY.get(), 100)));
        recipes.put(new ComparableStack(Items.GLOWSTONE_DUST), new Pair<>(new ItemStack(ModItems.SULFUR.get()), new FluidStackHBM(Fluids.CHLORINE.get(), 100)));
        recipes.put(new ComparableStack(ModItems.GEM_SODALITE.get()), new Pair<>(new ItemStack(ModItems.POWDER_SODIUM.get()), new FluidStackHBM(Fluids.CHLORINE.get(), 100)));
        recipes.put(new ComparableStack(DictFrame.fromOne(ModItems.CHUNK_ORE.get(), HBMEnums.EnumChunkType.CRYOLITE)),
                new Pair<>(new ItemStack(ModItems.POWDER_ALUMINIUM.get(), 1), new FluidStackHBM(Fluids.LYE.get(), 150)));
        recipes.put(new ComparableStack(ModItems.POWDER_SODIUM.get()), new Pair<>(null, new FluidStackHBM(Fluids.SODIUM.get(), 100)));
        recipes.put(new ComparableStack(ModItems.POWDER_LIMESTONE.get()), new Pair<>(new ItemStack(ModItems.POWDER_CALCIUM.get()), new FluidStackHBM(Fluids.CARBONDIOXIDE.get(), 50)));

        recipes.put(KEY_LOG, new Pair<>(new ItemStack(Items.CHARCOAL), new FluidStackHBM(Fluids.WOODOIL.get(), 250)));
        recipes.put(KEY_SAPLING, new Pair<>(DictFrame.fromOne(ModItems.POWDER_ASH.get(), HBMEnums.EnumAshType.WOOD), new FluidStackHBM(Fluids.WOODOIL.get(), 50)));
        recipes.put(new ComparableStack(DictFrame.fromOne(ModItems.BRIQUETTE.get(), HBMEnums.EnumBriquetteType.WOOD)),
                new Pair<>(new ItemStack(Items.CHARCOAL), new FluidStackHBM(Fluids.WOODOIL.get(), 500)));

        recipes.put(new ComparableStack(ModItems.OIL_TAR_CRUDE.get()),
                new Pair<>(DictFrame.fromOne(ModItems.COKE.get(), HBMEnums.EnumCokeType.PETROLEUM), null));
        recipes.put(new ComparableStack(ModItems.OIL_TAR_CRACK.get()),
                new Pair<>(DictFrame.fromOne(ModItems.COKE.get(), HBMEnums.EnumCokeType.PETROLEUM), null));
        recipes.put(new ComparableStack(ModItems.OIL_TAR_COAL.get()),
                new Pair<>(DictFrame.fromOne(ModItems.COKE.get(), HBMEnums.EnumCokeType.COAL), null));
        recipes.put(new ComparableStack(ModItems.OIL_TAR_WOOD.get()),
                new Pair<>(DictFrame.fromOne(ModItems.COKE.get(), HBMEnums.EnumCokeType.COAL), null));

        recipes.put(new ComparableStack(Items.SUGAR_CANE), new Pair<>(new ItemStack(Items.SUGAR, 2), new FluidStackHBM(Fluids.ETHANOL.get(), 50)));
        recipes.put(new ComparableStack(Items.CLAY_BALL), new Pair<>(new ItemStack(Items.BRICK, 1), null));

    }

    public static List<CombinationRecipeWrapper> getAllRecipes() {
        List<CombinationRecipeWrapper> list = new ArrayList<>();
        for (Map.Entry<Object, Tuple.Pair<ItemStack, FluidStackHBM>> entry : recipes.entrySet()) {
            list.add(new CombinationRecipeWrapper(entry.getKey(), entry.getValue()));
        }
        return list;
    }

    public static class CombinationRecipeWrapper {
        private final Object input;
        private final Tuple.Pair<ItemStack, FluidStackHBM> output;

        public CombinationRecipeWrapper(Object input, Tuple.Pair<ItemStack, FluidStackHBM> output) {
            this.input = input;
            this.output = output;
        }

        public Object getInput() { return input; }
        public ItemStack getOutputItem() { return output.key() == null ? null : output.key().copy(); }
        public FluidStackHBM getOutputFluid() { return output.value() == null ? null : output.value().copy(); }
    }

    public static Tuple.Pair<ItemStack, FluidStackHBM> getOutput(ItemStack stack) {

        if (stack == null || stack.isEmpty())
            return null;

        // Создаем ComparableStack из ItemStack
        ComparableStack comp = new ComparableStack(stack);
        comp.makeSingular(); // Устанавливаем stacksize = 1

        if (recipes.containsKey(comp)) {
            Tuple.Pair<ItemStack, FluidStackHBM> out = recipes.get(comp);
            return new Tuple.Pair<>(
                    out.key() == null ? null : out.key().copy(),
                    out.value() == null ? null : out.value().copy()
            );
        }

        String[] dictKeys = getOreDictKeys(stack);

        for (String key : dictKeys) {
            if (recipes.containsKey(key)) {
                Tuple.Pair<ItemStack, FluidStackHBM> out = recipes.get(key);
                return new Tuple.Pair<>(
                        out.key() == null ? null : out.key().copy(),
                        out.value() == null ? null : out.value().copy()
                );
            }
        }

        // 2. Проверка по тегам (отрезаем namespace)
        List<String> tags = new ArrayList<>();
        stack.getTags().forEach(tag -> {
            String fullTag = tag.location().toString();
            String pathOnly = fullTag.substring(fullTag.indexOf(':') + 1);
            tags.add(pathOnly);
        });

        for (String tag : tags) {
            if (recipes.containsKey(tag)) {
                Tuple.Pair<ItemStack, FluidStackHBM> out = recipes.get(tag);
                return new Tuple.Pair<>(
                        out.key() == null ? null : out.key().copy(),
                        out.value() == null ? null : out.value().copy()
                );
            }
        }

        return null;
    }

    private static String[] getOreDictKeys(ItemStack stack) {
        List<String> keys = new ArrayList<>();

        stack.getTags().forEach(tag -> keys.add(tag.location().toString()));

        return keys.toArray(new String[0]);
    }
}
