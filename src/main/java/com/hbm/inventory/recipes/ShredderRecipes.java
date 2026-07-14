package com.hbm.inventory.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.recipes.common.AStack;
import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.inventory.recipes.common.TagStack;
import com.hbm.items.ModItems;
import com.hbm.util.RefStrings;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;


import java.util.HashMap;
import java.util.Map;

import static com.hbm.items.ModItemTags.ANY_QUARTZ_BLOCK;
import static com.hbm.util.ResLocation.ResLocation;

public class ShredderRecipes {

    public static HashMap<AStack, ItemStack> shredderRecipes = new HashMap<>();

    public static void register() {
        // Базовые рецепты
        setRecipe(ModItems.SCRAP.get(), new ItemStack(ModItems.DUST.get()));
        setRecipe(ModItems.DUST.get(), new ItemStack(ModItems.DUST.get()));
        setRecipe(ModItems.DUST_TINY.get(), new ItemStack(ModItems.DUST_TINY.get()));
        setRecipe(Blocks.GLOWSTONE, new ItemStack(Items.GLOWSTONE_DUST, 4));
        setRecipe(new TagStack(ANY_QUARTZ_BLOCK), new ItemStack(ModItems.POWDER_QUARTZ.get(), 4));
        setRecipe(Blocks.NETHER_QUARTZ_ORE, new ItemStack(ModItems.POWDER_QUARTZ.get(), 2));
        setRecipe(Blocks.STONE, new ItemStack(Blocks.GRAVEL));
        setRecipe(Blocks.COBBLESTONE, new ItemStack(Blocks.GRAVEL));
        setRecipe(Blocks.GRAVEL, new ItemStack(Blocks.SAND));
        setRecipe(Blocks.DIRT, new ItemStack(ModItems.DUST.get()));
        setRecipe(Blocks.SAND, new ItemStack(ModItems.DUST.get(), 2));
        setRecipe(Blocks.CLAY, new ItemStack(Items.CLAY_BALL, 4));
        setRecipe(Blocks.TNT, new ItemStack(Items.GUNPOWDER, 5));

        setRecipe(Blocks.CHAIN, new ItemStack(ModItems.POWDER_IRON_TINY.get(), 11));

        setRecipe(new TagStack(ItemTags.LOGS), new ItemStack(ModItems.POWDER_SAWDUST.get(), 4));
        setRecipe(new TagStack(ItemTags.PLANKS), new ItemStack(ModItems.POWDER_SAWDUST.get(), 1));
        setRecipe(new TagStack(ItemTags.SAPLINGS), new ItemStack(Items.STICK, 1));

// Блоки из мода
        setRecipe(ModBlocks.CONCRETE.get(), new ItemStack(Blocks.GRAVEL));
        setRecipe(ModBlocks.CONCRETE_SMOOTH.get(), new ItemStack(Blocks.GRAVEL));
        setRecipe(ModBlocks.BRICK_CONCRETE.get(), new ItemStack(Blocks.GRAVEL));
        setRecipe(ModBlocks.BRICK_OBSIDIAN.get(), new ItemStack(ModBlocks.GRAVEL_OBSIDIAN.get()));
        setRecipe(Blocks.OBSIDIAN, new ItemStack(ModBlocks.GRAVEL_OBSIDIAN.get()));
        setRecipe(ModBlocks.BRICK_LIGHT.get(), new ItemStack(Items.CLAY_BALL, 4));
        setRecipe(ModBlocks.BLOCK_METEOR.get(), new ItemStack(ModItems.POWDER_METEORITE.get(), 10));
        setRecipe(ModBlocks.METEOR_POLISHED.get(), new ItemStack(ModItems.POWDER_METEORITE.get()));
        setRecipe(ModBlocks.METEOR_BRICK.get(), new ItemStack(ModItems.POWDER_METEORITE.get()));
        setRecipe(ModBlocks.ORE_RARE.get(), new ItemStack(ModItems.POWDER_DESH_MIX.get()));

// Фрагменты
        setRecipe(ModItems.FRAGMENT_NEODYMIUM.get(), new ItemStack(ModItems.POWDER_NEODYMIUM_TINY.get()));
        setRecipe(ModItems.FRAGMENT_COBALT.get(), new ItemStack(ModItems.POWDER_COBALT_TINY.get()));
        setRecipe(ModItems.FRAGMENT_NIOBIUM.get(), new ItemStack(ModItems.POWDER_NIOBIUM_TINY.get()));
        setRecipe(ModItems.FRAGMENT_CERIUM.get(), new ItemStack(ModItems.POWDER_CERIUM_TINY.get()));
        setRecipe(ModItems.FRAGMENT_LANTHANIUM.get(), new ItemStack(ModItems.POWDER_LANTHANIUM_TINY.get()));
        setRecipe(ModItems.FRAGMENT_ACTINIUM.get(), new ItemStack(ModItems.POWDER_ACTINIUM_TINY.get()));
        setRecipe(ModItems.FRAGMENT_BORON.get(), new ItemStack(ModItems.POWDER_BORON_TINY.get()));
        setRecipe(ModItems.FRAGMENT_METEORITE.get(), new ItemStack(ModItems.POWDER_METEORITE_TINY.get()));

// Кристаллы
        addCrystalRecipe(ModItems.CRYSTAL_COAL.get(), ModItems.POWDER_COAL.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_IRON.get(), ModItems.POWDER_IRON.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_GOLD.get(), ModItems.POWDER_GOLD.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_REDSTONE.get(), Items.REDSTONE, 8);
        addCrystalRecipe(ModItems.CRYSTAL_LAPIS.get(), ModItems.POWDER_LAPIS.get(), 8);
        addCrystalRecipe(ModItems.CRYSTAL_DIAMOND.get(), ModItems.POWDER_DIAMOND.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_URANIUM.get(), ModItems.POWDER_URANIUM.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_PLUTONIUM.get(), ModItems.POWDER_PLUTONIUM.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_THORIUM.get(), ModItems.POWDER_THORIUM.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_TITANIUM.get(), ModItems.POWDER_TITANIUM.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_SULFUR.get(), ModItems.SULFUR.get(), 8);
        addCrystalRecipe(ModItems.CRYSTAL_NITER.get(), ModItems.NITER.get(), 8);
        addCrystalRecipe(ModItems.CRYSTAL_COPPER.get(), ModItems.POWDER_COPPER.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_TUNGSTEN.get(), ModItems.POWDER_TUNGSTEN.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_ALUMINIUM.get(), ModItems.POWDER_ALUMINIUM.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_FLUORITE.get(), ModItems.FLUORITE.get(), 8);
        addCrystalRecipe(ModItems.CRYSTAL_BERYLLIUM.get(), ModItems.POWDER_BERYLLIUM.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_LEAD.get(), ModItems.POWDER_LEAD.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_SCHRABIDIUM.get(), ModItems.POWDER_SCHRABIDIUM.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_RARE.get(), ModItems.POWDER_DESH_MIX.get(), 2);
        addCrystalRecipe(ModItems.CRYSTAL_PHOSPHORUS.get(), ModItems.POWDER_FIRE.get(), 8);
        addCrystalRecipe(ModItems.CRYSTAL_LITHIUM.get(), ModItems.POWDER_LITHIUM.get(), 3);
        addCrystalRecipe(ModItems.CRYSTAL_STARMETAL.get(), ModItems.POWDER_DURA_STEEL.get(), 6);
        addCrystalRecipe(ModItems.CRYSTAL_COBALT.get(), ModItems.POWDER_COBALT.get(), 3);

        // Стальные конструкции
        setRecipe(ModBlocks.STEEL_POLES.get(), new ItemStack(ModItems.POWDER_STEEL_TINY.get(), 2));
        setRecipe(ModBlocks.STEEL_BEAM.get(), new ItemStack(ModItems.POWDER_STEEL_TINY.get(), 3));
        setRecipe(ModBlocks.STEEL_CHAIN.get(), new ItemStack(ModItems.POWDER_STEEL.get()));
        setRecipe(ModBlocks.STEEL_GRATE.get(), new ItemStack(ModItems.POWDER_STEEL_TINY.get(), 3));

        // Ящики
        setRecipe(ModBlocks.CRATE_IRON.get(), new ItemStack(ModItems.POWDER_IRON.get(), 8));
        setRecipe(ModBlocks.CRATE_STEEL.get(), new ItemStack(ModItems.POWDER_STEEL.get(), 8));
        setRecipe(ModBlocks.CRATE_DESH.get(), new ItemStack(ModItems.POWDER_DESH.get(), 8));
        setRecipe(ModBlocks.CRATE_TUNGSTEN.get(), new ItemStack(ModItems.POWDER_TUNGSTEN.get(), 8));
        setRecipe(Blocks.ANVIL, new ItemStack(ModItems.POWDER_IRON.get(), 31));

        // Катушки
        setRecipe(ModItems.COIL_COPPER.get(), new ItemStack(ModItems.POWDER_RED_COPPER.get()));
        setRecipe(ModItems.COIL_GOLD.get(), new ItemStack(ModItems.POWDER_GOLD.get()));
        setRecipe(ModItems.COIL_TUNGSTEN.get(), new ItemStack(ModItems.POWDER_TUNGSTEN.get()));

        // Шерсть
        Block[] wools = {
                Blocks.WHITE_WOOL, Blocks.ORANGE_WOOL, Blocks.MAGENTA_WOOL, Blocks.LIGHT_BLUE_WOOL,
                Blocks.YELLOW_WOOL, Blocks.LIME_WOOL, Blocks.PINK_WOOL, Blocks.GRAY_WOOL,
                Blocks.LIGHT_GRAY_WOOL, Blocks.CYAN_WOOL, Blocks.PURPLE_WOOL, Blocks.BLUE_WOOL,
                Blocks.BROWN_WOOL, Blocks.GREEN_WOOL, Blocks.RED_WOOL, Blocks.BLACK_WOOL
        };
        for (Block wool : wools) {
            setRecipe(wool, new ItemStack(Items.STRING, 4));
        }

        // Терракота
        Block[] terracottas = {
                Blocks.TERRACOTTA, Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA,
                Blocks.MAGENTA_TERRACOTTA, Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA,
                Blocks.LIME_TERRACOTTA, Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA,
                Blocks.LIGHT_GRAY_TERRACOTTA, Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA,
                Blocks.BLUE_TERRACOTTA, Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA,
                Blocks.RED_TERRACOTTA, Blocks.BLACK_TERRACOTTA
        };
        for (Block terracotta : terracottas) {
            setRecipe(terracotta, new ItemStack(Items.CLAY_BALL, 4));
        }

        registerTagRecipes();
        registerOreAndRawRecipes();
    }

    private static void registerTagRecipes() {
        // Свои слитки -> пыль
        addIngotDustRecipe(ModItems.INGOT_ALUMINIUM.get(), ModItems.POWDER_ALUMINIUM.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_TITANIUM.get(), ModItems.POWDER_TITANIUM.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_TUNGSTEN.get(), ModItems.POWDER_TUNGSTEN.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_LEAD.get(), ModItems.POWDER_LEAD.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_STEEL.get(), ModItems.POWDER_STEEL.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_URANIUM.get(), ModItems.POWDER_URANIUM.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_PLUTONIUM.get(), ModItems.POWDER_PLUTONIUM.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_THORIUM.get(), ModItems.POWDER_THORIUM.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_DESH.get(), ModItems.POWDER_DESH.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_SCHRABIDATE.get(), ModItems.POWDER_SCHRABIDATE.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_SCHRABIDIUM.get(), ModItems.POWDER_SCHRABIDIUM.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_ADVANCED_ALLOY.get(), ModItems.POWDER_ADVANCED_ALLOY.get(), 1);
        addIngotDustRecipe(ModItems.INGOT_DURA_STEEL.get(), ModItems.POWDER_DURA_STEEL.get(), 1);

        // Блоки -> 9 пыли для тех, у кого есть блоки
        addBlockDustRecipe(ModBlocks.BLOCK_STEEL.get(), ModItems.POWDER_STEEL.get(), 9);
        addBlockDustRecipe(ModBlocks.BLOCK_ALUMINIUM.get(), ModItems.POWDER_ALUMINIUM.get(), 9);
        addBlockDustRecipe(ModBlocks.BLOCK_TITANIUM.get(), ModItems.POWDER_TITANIUM.get(), 9);
        addBlockDustRecipe(ModBlocks.BLOCK_TUNGSTEN.get(), ModItems.POWDER_TUNGSTEN.get(), 9);
        addBlockDustRecipe(ModBlocks.BLOCK_LEAD.get(), ModItems.POWDER_LEAD.get(), 9);
        addBlockDustRecipe(ModBlocks.BLOCK_URANIUM.get(), ModItems.POWDER_URANIUM.get(), 9);
        addBlockDustRecipe(ModBlocks.BLOCK_PLUTONIUM.get(), ModItems.POWDER_PLUTONIUM.get(), 9);
        addBlockDustRecipe(ModBlocks.BLOCK_THORIUM.get(), ModItems.POWDER_THORIUM.get(), 9);
        addBlockDustRecipe(ModBlocks.BLOCK_SCHRABIDATE.get(), ModItems.POWDER_SCHRABIDATE.get(), 9);
    }

    private static void registerOreAndRawRecipes() {
        // Собираем все порошки с их материалами
        Map<String, Item> dustMap = new HashMap<>();
        for (Item item : ForgeRegistries.ITEMS) {
            ResourceLocation id = ForgeRegistries.ITEMS.getKey(item);
            if (id != null && id.getPath().startsWith("powder_")) {
                String material = id.getPath().substring(7);
                dustMap.put(material, item);
            }
        }

        // Собираем все руды и raw материалы
        for (Block block : ForgeRegistries.BLOCKS) {
            ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);
            if (id == null) continue;

            String name = id.getPath();

            // Обработка руд
            if (name.startsWith("ore_")) {
                String material = name.substring(4);
                if (material.contains("_deepslate")) {
                    material = material.substring(0, material.indexOf("_deepslate"));
                }

                Item dust = dustMap.get(material);
                if (dust != null) {
                    setRecipe(block, new ItemStack(dust, 2));
                }
            }

            // Обработка raw руд
            String rawName = "raw_" + name.replace("ore_", "");
            Item rawItem = ForgeRegistries.ITEMS.getValue(ResLocation(RefStrings.MODID, rawName));
            if (rawItem != null && rawItem != Items.AIR) {
                String material = name.substring(4);
                if (material.contains("_deepslate")) {
                    material = material.substring(0, material.indexOf("_deepslate"));
                }
                Item dust = dustMap.get(material);
                if (dust != null) {
                    setRecipe(rawItem, new ItemStack(dust, 1));
                }
            }
        }

        registerVanillaOreRecipes(dustMap);
    }

    private static void registerVanillaOreRecipes(Map<String, Item> dustMap) {
        // Ванильные руды -> соответствующие пыли
        Map<Block, String> vanillaOres = new HashMap<>();
        vanillaOres.put(Blocks.IRON_ORE, "iron");
        vanillaOres.put(Blocks.DEEPSLATE_IRON_ORE, "iron");
        vanillaOres.put(Blocks.COPPER_ORE, "copper");
        vanillaOres.put(Blocks.DEEPSLATE_COPPER_ORE, "copper");
        vanillaOres.put(Blocks.GOLD_ORE, "gold");
        vanillaOres.put(Blocks.DEEPSLATE_GOLD_ORE, "gold");
        vanillaOres.put(Blocks.COAL_ORE, "coal");
        vanillaOres.put(Blocks.DEEPSLATE_COAL_ORE, "coal");
        vanillaOres.put(Blocks.DIAMOND_ORE, "diamond");
        vanillaOres.put(Blocks.DEEPSLATE_DIAMOND_ORE, "diamond");
        vanillaOres.put(Blocks.EMERALD_ORE, "emerald");
        vanillaOres.put(Blocks.DEEPSLATE_EMERALD_ORE, "emerald");
        vanillaOres.put(Blocks.LAPIS_ORE, "lapis");
        vanillaOres.put(Blocks.DEEPSLATE_LAPIS_ORE, "lapis");
        vanillaOres.put(Blocks.REDSTONE_ORE, "redstone");
        vanillaOres.put(Blocks.DEEPSLATE_REDSTONE_ORE, "redstone");
        vanillaOres.put(Blocks.NETHER_QUARTZ_ORE, "quartz");
        vanillaOres.put(Blocks.NETHER_GOLD_ORE, "gold");

        for (var entry : vanillaOres.entrySet()) {
            Item dust = dustMap.get(entry.getValue());
            if (dust != null) {
                setRecipe(entry.getKey(), new ItemStack(dust, 2));
            }
        }

        // Ванильные raw материалы
        Map<Item, String> vanillaRaws = new HashMap<>();
        vanillaRaws.put(Items.RAW_IRON, "iron");
        vanillaRaws.put(Items.RAW_COPPER, "copper");
        vanillaRaws.put(Items.RAW_GOLD, "gold");

        for (var entry : vanillaRaws.entrySet()) {
            Item dust = dustMap.get(entry.getValue());
            if (dust != null) {
                setRecipe(entry.getKey(), new ItemStack(dust, 1));
            }
        }
    }

    private static void addIngotDustRecipe(Item ingot, Item dust, int count) {
        ItemStack dustStack = new ItemStack(dust, count);
        setRecipe(new ComparableStack(ingot), dustStack);
    }

    private static void addBlockDustRecipe(Block block, Item dust, int count) {
        ItemStack dustStack = new ItemStack(dust, count);
        setRecipe(new ComparableStack(block), dustStack);
    }

    private static void addCrystalRecipe(Item crystal, Item output, int count) {
        setRecipe(new ComparableStack(crystal), new ItemStack(output, count));
    }

    public static void setRecipe(ItemStack in, ItemStack out) {
        setRecipe(new ComparableStack(in), out);
    }

    public static void setRecipe(Block in, ItemStack out) {
        setRecipe(new ComparableStack(in), out);
    }

    public static void setRecipe(Item in, ItemStack out) {
        setRecipe(new ComparableStack(in), out);
    }

    public static void setRecipe(AStack in, ItemStack out) {
        shredderRecipes.put(in, out);
    }


    public static void setRecipe(ComparableStack in, ItemStack out) {
        if (!shredderRecipes.containsKey(in)) {
            shredderRecipes.put(in, out);
        }
    }

    public static ItemStack getShredderResult(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return new ItemStack(ModItems.SCRAP.get());
        }

        for (var entry : shredderRecipes.entrySet()) {
            if (entry.getKey().matchesRecipe(stack, true)) {
                return entry.getValue().copy();
            }
        }

        return new ItemStack(ModItems.SCRAP.get());
    }
}