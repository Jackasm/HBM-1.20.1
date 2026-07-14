package com.hbm.inventory.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.ModItemTags;
import com.hbm.items.ModItems;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CrystallizerRecipes {

    protected static final Map<Object, CrystallizerRecipe> recipes = new HashMap<>();
    private static final Map<Object, Integer> amounts = new HashMap<>();

    private static final int BASE_TIME = 600;
    private static final int UTILITY_TIME = 100;

    public static void register() {
        FluidStackHBM peroxide = new FluidStackHBM(Fluids.PEROXIDE.get(), 500);
        FluidStackHBM sulfuric = new FluidStackHBM(Fluids.SULFURIC_ACID.get(), 500);
        //TODO Портировать все рецепты

        // Простые руды -> кристаллы
        registerRecipe(ModItemTags.ORE_COAL, new CrystallizerRecipe(ModItems.CRYSTAL_COAL.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_IRON, new CrystallizerRecipe(ModItems.CRYSTAL_IRON.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_GOLD, new CrystallizerRecipe(ModItems.CRYSTAL_GOLD.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_REDSTONE, new CrystallizerRecipe(ModItems.CRYSTAL_REDSTONE.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_LAPIS, new CrystallizerRecipe(ModItems.CRYSTAL_LAPIS.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_DIAMOND, new CrystallizerRecipe(ModItems.CRYSTAL_DIAMOND.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_EMERALD, new CrystallizerRecipe(ModItems.CRYSTAL_EMERALD.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_NETHER_QUARTZ, new CrystallizerRecipe(new ItemStack(net.minecraft.world.item.Items.QUARTZ, 2), BASE_TIME), peroxide);

        // Руды с серной кислотой
        registerRecipe(ModItemTags.ORE_URANIUM, new CrystallizerRecipe(ModItems.CRYSTAL_URANIUM.get(), BASE_TIME), sulfuric);
        registerRecipe(ModItemTags.ORE_THORIUM, new CrystallizerRecipe(ModItems.CRYSTAL_THORIUM.get(), BASE_TIME), sulfuric);
        registerRecipe(ModItemTags.ORE_PLUTONIUM, new CrystallizerRecipe(ModItems.CRYSTAL_PLUTONIUM.get(), BASE_TIME), sulfuric);
        registerRecipe(ModItemTags.ORE_TITANIUM, new CrystallizerRecipe(ModItems.CRYSTAL_TITANIUM.get(), BASE_TIME), sulfuric);
        registerRecipe(ModItemTags.ORE_TUNGSTEN, new CrystallizerRecipe(ModItems.CRYSTAL_TUNGSTEN.get(), BASE_TIME), sulfuric);
        registerRecipe(ModItemTags.ORE_LEAD, new CrystallizerRecipe(ModItems.CRYSTAL_LEAD.get(), BASE_TIME), sulfuric);
        registerRecipe(ModItemTags.ORE_COBALT, new CrystallizerRecipe(ModItems.CRYSTAL_COBALT.get(), BASE_TIME), sulfuric);
        registerRecipe(ModItemTags.ORE_BERYLLIUM, new CrystallizerRecipe(ModItems.CRYSTAL_BERYLLIUM.get(), BASE_TIME), sulfuric);
        registerRecipe(ModItemTags.ORE_SCHRABIDIUM, new CrystallizerRecipe(ModItems.CRYSTAL_SCHRABIDIUM.get(), BASE_TIME), sulfuric);

        // Руды без кислоты
        registerRecipe(ModItemTags.ORE_COPPER, new CrystallizerRecipe(ModItems.CRYSTAL_COPPER.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_ALUMINIUM, new CrystallizerRecipe(ModItems.CRYSTAL_ALUMINIUM.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_ZINC, new CrystallizerRecipe(ModItems.CRYSTAL_ZINC.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_NITER, new CrystallizerRecipe(ModItems.CRYSTAL_NITER.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_SULFUR, new CrystallizerRecipe(ModItems.CRYSTAL_SULFUR.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_FLUORITE, new CrystallizerRecipe(ModItems.CRYSTAL_FLUORITE.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_CINNABAR, new CrystallizerRecipe(ModItems.CRYSTAL_CINNABAR.get(), BASE_TIME), peroxide);
        registerRecipe(ModItemTags.ORE_RARE_EARTH, new CrystallizerRecipe(ModItems.CRYSTAL_RARE.get(), BASE_TIME), peroxide);

        // === ДОПОЛНИТЕЛЬНЫЕ РЕЦЕПТЫ ===

        // Киноварь -> ртуть
        registerRecipe(new TagStack(ModItemTags.ORE_CINNABAR, 1),
                new CrystallizerRecipe(ModItems.INGOT_MERCURY.get(), BASE_TIME).prod(0.25F), sulfuric);

        // Уголь -> графит
        registerRecipe(Blocks.COAL_BLOCK,
                new CrystallizerRecipe(ModBlocks.BLOCK_GRAPHITE.get(), 200), peroxide);

        registerRecipe(Items.ROTTEN_FLESH,
                new CrystallizerRecipe(Items.LEATHER, UTILITY_TIME), peroxide);

        // Песок -> стекловолокно
        registerRecipe(new TagStack(net.minecraft.tags.ItemTags.SAND, 1),
                new CrystallizerRecipe(ModItems.INGOT_FIBERGLASS.get(), UTILITY_TIME), peroxide);

        // Цемент
        registerRecipe(ModItems.POWDER_CALCIUM.get(),
                new CrystallizerRecipe(new ItemStack(ModItems.POWDER_CEMENT.get(), 8), UTILITY_TIME),
                new FluidStackHBM(Fluids.REDMUD.get(), 75));

        // Кремний -> кварц
        registerRecipe(ModItems.INGOT_SILICON.get(),
                new CrystallizerRecipe(new ItemStack(net.minecraft.world.item.Items.QUARTZ, 2), UTILITY_TIME),
                new FluidStackHBM(Fluids.OXYGEN.get(), 250));
    }

    public static List<CrystallizerRecipe> getAllRecipes() {
        return new ArrayList<>(recipes.values());
    }

    public static CrystallizerRecipe getOutput(ItemStack stack, FluidTypeHBM type) {
        if (stack == null || stack.isEmpty()) return null;

        // Проверяем по предмету
        for (Map.Entry<Object, CrystallizerRecipe> entry : recipes.entrySet()) {
            Object key = entry.getKey();
            if (key instanceof TagStack tagStack) {
                if (tagStack.matchesRecipe(stack, true)) {
                    return entry.getValue();
                }
            } else if (key instanceof ItemStack matchStack) {
                if (ItemStack.isSameItemSameTags(matchStack, stack)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    public static Map<Object, CrystallizerRecipe> getRecipes() {
        return recipes;
    }

    public static class CrystallizerRecipe {
        public FluidTypeHBM acid;
        public int acidAmount;
        public int itemAmount = 1;
        public int duration;
        public float productivity = 0F;
        public ItemStack output;

        public CrystallizerRecipe(ItemStack output, int duration) {
            this.output = output;
            this.duration = duration;
            this.acidAmount = 500;
            this.acid = Fluids.PEROXIDE.get();
        }

        public CrystallizerRecipe setAcid(FluidTypeHBM acid, int amount) {
            this.acid = acid;
            this.acidAmount = amount;
            return this;
        }

        public CrystallizerRecipe(Item output, int duration) {
            this(new ItemStack(output), duration);
        }

        public CrystallizerRecipe(Block output, int duration) {
            this(new ItemStack(output), duration);
        }

        public CrystallizerRecipe setReq(int amount) {
            this.itemAmount = amount;
            return this;
        }

        public CrystallizerRecipe prod(float productivity) {
            this.productivity = productivity;
            return this;
        }
    }

    private static void registerRecipe(Object input, CrystallizerRecipe recipe, FluidStackHBM stack) {
        recipe.acidAmount = stack.fill;
        recipe.acid = stack.type;
        recipes.put(input, recipe);
    }

    private static void registerRecipe(Item input, CrystallizerRecipe recipe, FluidStackHBM stack) {
        registerRecipe(new ItemStack(input), recipe, stack);
    }

    private static void registerRecipe(TagKey<Item> tag, CrystallizerRecipe recipe, FluidStackHBM stack) {
        registerRecipe(new TagStack(tag, 1), recipe, stack);
    }
}