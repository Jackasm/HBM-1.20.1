package com.hbm.datagen.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockCoke;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemRTGPelletDepleted;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;

public class MineralRecipes extends ModRecipeProvider {
    public MineralRecipes(PackOutput pOutput) { super(pOutput); }

    public static void generateMineralRecipes(Consumer<FinishedRecipe> pWriter) {

        add1To9Pair(ModItems.DUST.get(), ModItems.DUST_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_COAL.get(), ModItems.POWDER_COAL_TINY.get(), pWriter);
        add1To9Pair(ModItems.INGOT_MERCURY.get(), ModItems.NUGGET_MERCURY.get(), pWriter);

        // Блоки -> слитки и обратно
        add1To9Pair(ModBlocks.BLOCK_ALUMINIUM.get(), ModItems.INGOT_ALUMINIUM.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_GRAPHITE.get(), ModItems.INGOT_GRAPHITE.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_BORON.get(), ModItems.INGOT_BORON.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_SCHRARANIUM.get(), ModItems.INGOT_SCHRARANIUM.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_SCHRABIDATE.get(), ModItems.INGOT_SCHRABIDATE.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_COLTAN.get(), ModItems.FRAGMENT_COLTAN.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_SMORE.get(), ModItems.INGOT_SMORE.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_SEMTEX.get(), ModItems.INGOT_SEMTEX.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_C4.get(), ModItems.INGOT_C4.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_POLYMER.get(), ModItems.INGOT_POLYMER.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_BAKELITE.get(), ModItems.INGOT_BAKELITE.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_RUBBER.get(), ModItems.INGOT_RUBBER.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_LANTHANIUM.get(), ModItems.INGOT_LANTHANIUM.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_RA226.get(), ModItems.INGOT_RA226.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_ACTINIUM.get(), ModItems.INGOT_ACTINIUM.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_CADMIUM.get(), ModItems.INGOT_CADMIUM.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_TCALLOY.get(), ModItems.INGOT_TCALLOY.get(), pWriter);
        add1To9Pair(ModBlocks.BLOCK_CDALLOY.get(), ModItems.INGOT_CDALLOY.get(), pWriter);

        for (int i = 0; i < BlockCoke.CokeType.values().length; i++) {
            add1To9PairSameMeta(ModBlocks.BLOCK_COKE.get().asItem(), ModItems.COKE.get(), i, pWriter);
        }

        addMineralSet(ModItems.NUGGET_NIOBIUM.get(), ModItems.INGOT_NIOBIUM.get(), ModBlocks.BLOCK_NIOBIUM.get(), pWriter);
        addMineralSet(ModItems.NUGGET_BISMUTH.get(), ModItems.INGOT_BISMUTH.get(), ModBlocks.BLOCK_BISMUTH.get(), pWriter);
        addMineralSet(ModItems.NUGGET_TANTALIUM.get(), ModItems.INGOT_TANTALIUM.get(), ModBlocks.BLOCK_TANTALIUM.get(), pWriter);
        addMineralSet(ModItems.NUGGET_ZIRCONIUM.get(), ModItems.INGOT_ZIRCONIUM.get(), ModBlocks.BLOCK_ZIRCONIUM.get(), pWriter);
        addMineralSet(ModItems.NUGGET_DINEUTRONIUM.get(), ModItems.INGOT_DINEUTRONIUM.get(), ModBlocks.BLOCK_DINEUTRONIUM.get(), pWriter);
        addMineralSet(ModItems.NUCLEAR_WASTE_VITRIFIED_TINY.get(), ModItems.NUCLEAR_WASTE_VITRIFIED.get(), ModBlocks.BLOCK_WASTE_VITRIFIED.get(), pWriter);

        // Силикон
        add1To9Pair(ModItems.INGOT_SILICON.get(), ModItems.NUGGET_SILICON.get(), pWriter);

// Порошки
        add1To9Pair(ModItems.POWDER_BORON.get(), ModItems.POWDER_BORON_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_SR90.get(), ModItems.POWDER_SR90_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_XE135.get(), ModItems.POWDER_XE135_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_CS137.get(), ModItems.POWDER_CS137_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_I131.get(), ModItems.POWDER_I131_TINY.get(), pWriter);

// Слитки и самородки
        add1To9Pair(ModItems.INGOT_TECHNETIUM.get(), ModItems.NUGGET_TECHNETIUM.get(), pWriter);
        add1To9Pair(ModItems.INGOT_CO60.get(), ModItems.NUGGET_CO60.get(), pWriter);
        add1To9Pair(ModItems.INGOT_SR90.get(), ModItems.NUGGET_SR90.get(), pWriter);
        add1To9Pair(ModItems.INGOT_AU198.get(), ModItems.NUGGET_AU198.get(), pWriter);
        add1To9Pair(ModItems.INGOT_PB209.get(), ModItems.NUGGET_PB209.get(), pWriter);
        add1To9Pair(ModItems.INGOT_RA226.get(), ModItems.NUGGET_RA_226.get(), pWriter);
        add1To9Pair(ModItems.INGOT_ACTINIUM.get(), ModItems.NUGGET_ACTINIUM.get(), pWriter);
        add1To9Pair(ModItems.INGOT_ARSENIC.get(), ModItems.NUGGET_ARSENIC.get(), pWriter);

// Плутоний-241 и америций
        add1To9Pair(ModItems.INGOT_PU241.get(), ModItems.NUGGET_PU241.get(), pWriter);
        add1To9Pair(ModItems.INGOT_AM241.get(), ModItems.NUGGET_AM241.get(), pWriter);
        add1To9Pair(ModItems.INGOT_AM242.get(), ModItems.NUGGET_AM242.get(), pWriter);
        add1To9Pair(ModItems.INGOT_AM_MIX.get(), ModItems.NUGGET_AM_MIX.get(), pWriter);
        add1To9Pair(ModItems.INGOT_AMERICIUM_FUEL.get(), ModItems.NUGGET_AMERICIUM_FUEL.get(), pWriter);

// GH336
        add1To9Pair(ModItems.INGOT_GH336.get(), ModItems.NUGGET_GH336.get(), pWriter);

// TODO: Портировать рецепты для ядерных отходов
// Для длинных отходов (WasteLong)
// for(int i = 0; i < ItemWasteLong.WasteClass.values().length; i++) {
//     add1To9PairSameMeta(ModItems.NUCLEAR_WASTE_LONG.get(), ModItems.NUCLEAR_WASTE_LONG_TINY.get(), i, pWriter);
//     add1To9PairSameMeta(ModItems.NUCLEAR_WASTE_LONG_DEPLETED.get(), ModItems.NUCLEAR_WASTE_LONG_DEPLETED_TINY.get(), i, pWriter);
// }
//
// Для коротких отходов (WasteShort)
// for(int i = 0; i < ItemWasteShort.WasteClass.values().length; i++) {
//     add1To9PairSameMeta(ModItems.NUCLEAR_WASTE_SHORT.get(), ModItems.NUCLEAR_WASTE_SHORT_TINY.get(), i, pWriter);
//     add1To9PairSameMeta(ModItems.NUCLEAR_WASTE_SHORT_DEPLETED.get(), ModItems.NUCLEAR_WASTE_SHORT_DEPLETED_TINY.get(), i, pWriter);
// }
//
// Необходимо создать классы:
// - ItemWasteLong с enum WasteClass (как в оригинале)
// - ItemWasteShort с enum WasteClass (как в оригинале)
// И зарегистрировать предметы в ModItems:
// - NUCLEAR_WASTE_LONG (ItemEnumMulti<WasteClass>)
// - NUCLEAR_WASTE_LONG_TINY (ItemEnumMulti<WasteClass>)
// - NUCLEAR_WASTE_LONG_DEPLETED (ItemEnumMulti<WasteClass>)
// - NUCLEAR_WASTE_LONG_DEPLETED_TINY (ItemEnumMulti<WasteClass>)
// - NUCLEAR_WASTE_SHORT (ItemEnumMulti<WasteClass>)
// - NUCLEAR_WASTE_SHORT_TINY (ItemEnumMulti<WasteClass>)
// - NUCLEAR_WASTE_SHORT_DEPLETED (ItemEnumMulti<WasteClass>)
// - NUCLEAR_WASTE_SHORT_DEPLETED_TINY (ItemEnumMulti<WasteClass>)
//
// Пример регистрации предмета:
// public static final RegistryObject<Item> NUCLEAR_WASTE_LONG = register(PARTS_TAB, "nuclear_waste_long",
//         () -> new ItemEnumMulti<>(new Item.Properties(), WasteLongClass.class, true, 0), ItemModelType.ENUM_ITEM, WasteLongClass.class);
//
// Пример использования add1To9PairSameMeta:
// public static void add1To9PairSameMeta(Item one, Item nine, int meta, Consumer<FinishedRecipe> pWriter) {
//     // Создаёт рецепты: 1 предмет с meta -> 9 предметов с той же meta, и обратно
// }
        // Fallout: 1 блок = 9 единиц
        add1To9Pair(ModBlocks.BLOCK_FALLOUT.get(), ModItems.FALLOUT.get(), pWriter);

// 2 блока fallout из 4 единиц fallout
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_FALLOUT.get(), 2)
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.FALLOUT.get())
                .unlockedBy("has_fallout", has(ModItems.FALLOUT.get()))
                .save(pWriter, "fallout_block_from_fallout");

        // Плутониевая смесь (самородок -> слиток -> блок)
        addMineralSet(ModItems.NUGGET_PU_MIX.get(), ModItems.INGOT_PU_MIX.get(), ModBlocks.BLOCK_PU_MIX.get(), pWriter);

// Нептуниевое топливо (слиток -> самородок)
        add1To9Pair(ModItems.INGOT_NEPTUNIUM_FUEL.get(), ModItems.NUGGET_NEPTUNIUM_FUEL.get(), pWriter);

        addBillet(ModItems.BILLET_COBALT.get(), ModItems.INGOT_COBALT.get(), ModItems.NUGGET_COBALT.get(), pWriter);
        addBillet(ModItems.BILLET_CO60.get(), ModItems.INGOT_CO60.get(), ModItems.NUGGET_CO60.get(), pWriter);
        addBillet(ModItems.BILLET_SR90.get(), ModItems.INGOT_SR90.get(), ModItems.NUGGET_SR90.get(), pWriter);
        addBillet(ModItems.BILLET_URANIUM.get(), ModItems.INGOT_URANIUM.get(), ModItems.NUGGET_URANIUM.get(), pWriter);
        addBillet(ModItems.BILLET_U233.get(), ModItems.INGOT_U233.get(), ModItems.NUGGET_U233.get(), pWriter);
        addBillet(ModItems.BILLET_U235.get(), ModItems.INGOT_U235.get(), ModItems.NUGGET_U235.get(), pWriter);
        addBillet(ModItems.BILLET_U238.get(), ModItems.INGOT_U238.get(), ModItems.NUGGET_U238.get(), pWriter);
        addBillet(ModItems.BILLET_TH232.get(), ModItems.INGOT_TH232.get(), ModItems.NUGGET_TH232.get(), pWriter);
        addBillet(ModItems.BILLET_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), ModItems.NUGGET_PLUTONIUM.get(), pWriter);
        addBillet(ModItems.BILLET_PU238.get(), ModItems.INGOT_PU238.get(), ModItems.NUGGET_PU238.get(), pWriter);
        addBillet(ModItems.BILLET_PU239.get(), ModItems.INGOT_PU239.get(), ModItems.NUGGET_PU239.get(), pWriter);
        addBillet(ModItems.BILLET_PU240.get(), ModItems.INGOT_PU240.get(), ModItems.NUGGET_PU240.get(), pWriter);
        addBillet(ModItems.BILLET_PU241.get(), ModItems.INGOT_PU241.get(), ModItems.NUGGET_PU241.get(), pWriter);
        addBillet(ModItems.BILLET_PU_MIX.get(), ModItems.INGOT_PU_MIX.get(), ModItems.NUGGET_PU_MIX.get(), pWriter);
        addBillet(ModItems.BILLET_AM241.get(), ModItems.INGOT_AM241.get(), ModItems.NUGGET_AM241.get(), pWriter);
        addBillet(ModItems.BILLET_AM242.get(), ModItems.INGOT_AM242.get(), ModItems.NUGGET_AM242.get(), pWriter);
        addBillet(ModItems.BILLET_AM_MIX.get(), ModItems.INGOT_AM_MIX.get(), ModItems.NUGGET_AM_MIX.get(), pWriter);
        addBillet(ModItems.BILLET_NEPTUNIUM.get(), ModItems.INGOT_NEPTUNIUM.get(), ModItems.NUGGET_NEPTUNIUM.get(), pWriter);
        addBillet(ModItems.BILLET_POLONIUM.get(), ModItems.INGOT_POLONIUM.get(), ModItems.NUGGET_POLONIUM.get(), pWriter);
        addBillet(ModItems.BILLET_TECHNETIUM.get(), ModItems.INGOT_TECHNETIUM.get(), ModItems.NUGGET_TECHNETIUM.get(), pWriter);
        addBillet(ModItems.BILLET_AU198.get(), ModItems.INGOT_AU198.get(), ModItems.NUGGET_AU198.get(), pWriter);
        addBillet(ModItems.BILLET_PB209.get(), ModItems.INGOT_PB209.get(), ModItems.NUGGET_PB209.get(), pWriter);
        addBillet(ModItems.BILLET_RA226.get(), ModItems.INGOT_RA226.get(), ModItems.NUGGET_RA_226.get(), pWriter);
        addBillet(ModItems.BILLET_ACTINIUM.get(), ModItems.INGOT_ACTINIUM.get(), ModItems.NUGGET_ACTINIUM.get(), pWriter);
        addBillet(ModItems.BILLET_SCHRABIDIUM.get(), ModItems.INGOT_SCHRABIDIUM.get(), ModItems.NUGGET_SCHRABIDIUM.get(), pWriter);
        addBillet(ModItems.BILLET_SOLINIUM.get(), ModItems.INGOT_SOLINIUM.get(), ModItems.NUGGET_SOLINIUM.get(), pWriter);
        addBillet(ModItems.BILLET_GH336.get(), ModItems.INGOT_GH336.get(), ModItems.NUGGET_GH336.get(), pWriter);
        addBillet(ModItems.BILLET_URANIUM_FUEL.get(), ModItems.INGOT_URANIUM_FUEL.get(), ModItems.NUGGET_URANIUM_FUEL.get(), pWriter);
        addBillet(ModItems.BILLET_THORIUM_FUEL.get(), ModItems.INGOT_THORIUM_FUEL.get(), ModItems.NUGGET_THORIUM_FUEL.get(), pWriter);
        addBillet(ModItems.BILLET_PLUTONIUM_FUEL.get(), ModItems.INGOT_PLUTONIUM_FUEL.get(), ModItems.NUGGET_PLUTONIUM_FUEL.get(), pWriter);
        addBillet(ModItems.BILLET_NEPTUNIUM_FUEL.get(), ModItems.INGOT_NEPTUNIUM_FUEL.get(), ModItems.NUGGET_NEPTUNIUM_FUEL.get(), pWriter);
        addBillet(ModItems.BILLET_MOX_FUEL.get(), ModItems.INGOT_MOX_FUEL.get(), ModItems.NUGGET_MOX_FUEL.get(), pWriter);
        addBillet(ModItems.BILLET_LES.get(), ModItems.INGOT_LES.get(), ModItems.NUGGET_LES.get(), pWriter);
        addBillet(ModItems.BILLET_SCHRABIDIUM_FUEL.get(), ModItems.INGOT_SCHRABIDIUM_FUEL.get(), ModItems.NUGGET_SCHRABIDIUM_FUEL.get(), pWriter);
        addBillet(ModItems.BILLET_HES.get(), ModItems.INGOT_HES.get(), ModItems.NUGGET_HES.get(), pWriter);
        addBillet(ModItems.BILLET_AUSTRALIUM.get(), ModItems.INGOT_AUSTRALIUM.get(), ModItems.NUGGET_AUSTRALIUM.get(), pWriter);
        addBillet(ModItems.BILLET_AUSTRALIUM_GREATER.get(), ModItems.NUGGET_AUSTRALIUM_GREATER.get(), pWriter);
        addBillet(ModItems.BILLET_AUSTRALIUM_LESSER.get(), ModItems.NUGGET_AUSTRALIUM_LESSER.get(), pWriter);
        addBillet(ModItems.BILLET_NUCLEAR_WASTE.get(), ModItems.NUCLEAR_WASTE.get(), ModItems.NUCLEAR_WASTE_TINY.get(), pWriter);
        addBillet(ModItems.BILLET_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), ModItems.NUGGET_BERYLLIUM.get(), pWriter);
        addBillet(ModItems.BILLET_ZIRCONIUM.get(), ModItems.INGOT_ZIRCONIUM.get(), ModItems.NUGGET_ZIRCONIUM.get(), pWriter);
        addBillet(ModItems.BILLET_BISMUTH.get(), ModItems.INGOT_BISMUTH.get(), ModItems.NUGGET_BISMUTH.get(), pWriter);
        addBillet(ModItems.BILLET_SILICON.get(), ModItems.INGOT_SILICON.get(), ModItems.NUGGET_SILICON.get(), pWriter);

        // Ториевое топливо (6 биллетов)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_THORIUM_FUEL.get(), 6)
                .requires(ModItems.BILLET_TH232.get(), 5)
                .requires(ModItems.BILLET_U233.get())
                .unlockedBy("has_billet_th232", has(ModItems.BILLET_TH232.get()))
                .save(pWriter, "billet_thorium_fuel_from_th232_u233");

        // Урановое топливо (6 биллетов)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_URANIUM_FUEL.get(), 6)
                .requires(ModItems.BILLET_U238.get(), 5)
                .requires(ModItems.BILLET_U235.get())
                .unlockedBy("has_billet_u238", has(ModItems.BILLET_U238.get()))
                .save(pWriter, "billet_uranium_fuel_from_u238_u235");

        // Плутониевое топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_PLUTONIUM_FUEL.get(), 3)
                .requires(ModItems.BILLET_U238.get(), 2)
                .requires(ModItems.BILLET_PU_MIX.get())
                .unlockedBy("has_billet_pu_mix", has(ModItems.BILLET_PU_MIX.get()))
                .save(pWriter, "billet_plutonium_fuel_from_u238_pu_mix");

        // Смесь плутония (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_PU_MIX.get(), 3)
                .requires(ModItems.BILLET_PU239.get(), 2)
                .requires(ModItems.BILLET_PU240.get())
                .unlockedBy("has_billet_pu239", has(ModItems.BILLET_PU239.get()))
                .save(pWriter, "billet_pu_mix_from_pu239_pu240");

        // Америциевое топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_AMERICIUM_FUEL.get(), 3)
                .requires(ModItems.BILLET_U238.get(), 2)
                .requires(ModItems.BILLET_AM_MIX.get())
                .unlockedBy("has_billet_am_mix", has(ModItems.BILLET_AM_MIX.get()))
                .save(pWriter, "billet_americium_fuel_from_u238_am_mix");

        // Америциевое топливо (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_AMERICIUM_FUEL.get())
                .requires(ModItems.NUGGET_AM_MIX.get(), 2)
                .requires(ModItems.NUGGET_U238.get(), 4)
                .unlockedBy("has_nugget_am_mix", has(ModItems.NUGGET_AM_MIX.get()))
                .save(pWriter, "billet_americium_fuel_from_nuggets");

        // Смесь америция (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_AM_MIX.get(), 3)
                .requires(ModItems.BILLET_AM241.get())
                .requires(ModItems.BILLET_AM242.get(), 2)
                .unlockedBy("has_billet_am241", has(ModItems.BILLET_AM241.get()))
                .save(pWriter, "billet_am_mix_from_am241_am242");

        // Нептуниевое топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_NEPTUNIUM_FUEL.get(), 3)
                .requires(ModItems.BILLET_U238.get(), 2)
                .requires(ModItems.BILLET_NEPTUNIUM.get())
                .unlockedBy("has_billet_neptunium", has(ModItems.BILLET_NEPTUNIUM.get()))
                .save(pWriter, "billet_neptunium_fuel_from_u238_neptunium");

        // MOX топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_MOX_FUEL.get(), 3)
                .requires(ModItems.BILLET_URANIUM_FUEL.get(), 2)
                .requires(ModItems.BILLET_PU_MIX.get())
                .unlockedBy("has_billet_uranium_fuel", has(ModItems.BILLET_URANIUM_FUEL.get()))
                .save(pWriter, "billet_mox_fuel_from_uranium_pu_mix");

        // Шрабридиевое топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_SCHRABIDIUM_FUEL.get(), 3)
                .requires(ModItems.BILLET_SCHRABIDIUM.get())
                .requires(ModItems.BILLET_NEPTUNIUM.get())
                .requires(ModItems.BILLET_BERYLLIUM.get())
                .unlockedBy("has_billet_schrabidium", has(ModItems.BILLET_SCHRABIDIUM.get()))
                .save(pWriter, "billet_schrabidium_fuel_from_billets");

        // RTG пеллеты
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG.get())
                .requires(ModItems.BILLET_PU238.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_pu238", has(ModItems.BILLET_PU238.get()))
                .save(pWriter, "pellet_rtg");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_RADIUM.get())
                .requires(ModItems.BILLET_RA226.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_ra226", has(ModItems.BILLET_RA226.get()))
                .save(pWriter, "pellet_rtg_radium");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_WEAK.get())
                .requires(ModItems.BILLET_U238.get(), 2)
                .requires(ModItems.BILLET_PU238.get())
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_u238", has(ModItems.BILLET_U238.get()))
                .save(pWriter, "pellet_rtg_weak");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_STRONTIUM.get())
                .requires(ModItems.BILLET_SR90.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_sr90", has(ModItems.BILLET_SR90.get()))
                .save(pWriter, "pellet_rtg_strontium");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_COBALT.get())
                .requires(ModItems.BILLET_CO60.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_co60", has(ModItems.BILLET_CO60.get()))
                .save(pWriter, "pellet_rtg_cobalt");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_ACTINIUM.get())
                .requires(ModItems.BILLET_ACTINIUM.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_actinium", has(ModItems.BILLET_ACTINIUM.get()))
                .save(pWriter, "pellet_rtg_actinium");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_POLONIUM.get())
                .requires(ModItems.BILLET_POLONIUM.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_polonium", has(ModItems.BILLET_POLONIUM.get()))
                .save(pWriter, "pellet_rtg_polonium");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_LEAD.get())
                .requires(ModItems.BILLET_PB209.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_pb209", has(ModItems.BILLET_PB209.get()))
                .save(pWriter, "pellet_rtg_lead");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_GOLD.get())
                .requires(ModItems.BILLET_AU198.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_au198", has(ModItems.BILLET_AU198.get()))
                .save(pWriter, "pellet_rtg_gold");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_AMERICIUM.get())
                .requires(ModItems.BILLET_AM241.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_am241", has(ModItems.BILLET_AM241.get()))
                .save(pWriter, "pellet_rtg_americium");

        for (ItemRTGPelletDepleted.DepletedRTGMaterial material : ItemRTGPelletDepleted.DepletedRTGMaterial.values()) {
            int meta = material.ordinal();

            // Создаём результат
            ItemStack result = switch (material) {
                case BISMUTH -> new ItemStack(ModItems.BILLET_BISMUTH.get(), 3);
                case MERCURY -> new ItemStack(ModItems.INGOT_MERCURY.get(), 2);
                case NEPTUNIUM -> new ItemStack(ModItems.BILLET_NEPTUNIUM.get(), 3);
                case LEAD -> new ItemStack(ModItems.INGOT_LEAD.get(), 2);
                case ZIRCONIUM -> new ItemStack(ModItems.BILLET_ZIRCONIUM.get(), 3);
            };

            ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.getItem(), result.getCount())
                    .requires(ModItems.PELLET_RTG_DEPLETED.get())
                    .unlockedBy("has_pellet_rtg_depleted", has(ModItems.PELLET_RTG_DEPLETED.get()))
                    .save(pWriter, "pellet_rtg_depleted_to_" + material.name().toLowerCase() + "_" + meta);
        }

        // 9 предметов -> 1 блок
// Блоки из слитков/компонентов


        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_FLUORITE.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.FLUORITE.get())
                .unlockedBy("has_fluorite", has(ModItems.FLUORITE.get()))
                .save(pWriter, "block_fluorite_from_fluorite");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_NITER.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.NITER.get())
                .unlockedBy("has_niter", has(ModItems.NITER.get()))
                .save(pWriter, "block_niter_from_niter");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_RED_COPPER.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_RED_COPPER.get())
                .unlockedBy("has_ingot_red_copper", has(ModItems.INGOT_RED_COPPER.get()))
                .save(pWriter, "block_red_copper_from_ingot_red_copper");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_STEEL.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_STEEL.get())
                .unlockedBy("has_ingot_steel", has(ModItems.INGOT_STEEL.get()))
                .save(pWriter, "block_steel_from_ingot_steel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_SULFUR.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.SULFUR.get())
                .unlockedBy("has_sulfur", has(ModItems.SULFUR.get()))
                .save(pWriter, "block_sulfur_from_sulfur");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_TITANIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_TITANIUM.get())
                .unlockedBy("has_ingot_titanium", has(ModItems.INGOT_TITANIUM.get()))
                .save(pWriter, "block_titanium_from_ingot_titanium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_TUNGSTEN.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_TUNGSTEN.get())
                .unlockedBy("has_ingot_tungsten", has(ModItems.INGOT_TUNGSTEN.get()))
                .save(pWriter, "block_tungsten_from_ingot_tungsten");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_URANIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_URANIUM.get())
                .unlockedBy("has_ingot_uranium", has(ModItems.INGOT_URANIUM.get()))
                .save(pWriter, "block_uranium_from_ingot_uranium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_THORIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_TH232.get())
                .unlockedBy("has_ingot_th232", has(ModItems.INGOT_TH232.get()))
                .save(pWriter, "block_thorium_from_ingot_th232");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_LEAD.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_LEAD.get())
                .unlockedBy("has_ingot_lead", has(ModItems.INGOT_LEAD.get()))
                .save(pWriter, "block_lead_from_ingot_lead");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_TRINITITE.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.TRINITITE.get())
                .unlockedBy("has_trinitite", has(ModItems.TRINITITE.get()))
                .save(pWriter, "block_trinitite_from_trinitite");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_WASTE.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.NUCLEAR_WASTE.get())
                .unlockedBy("has_nuclear_waste", has(ModItems.NUCLEAR_WASTE.get()))
                .save(pWriter, "block_waste_from_nuclear_waste");

// Блоки из scrap и dust (2x2 и 3x3)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_SCRAP.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.SCRAP.get())
                .unlockedBy("has_scrap", has(ModItems.SCRAP.get()))
                .save(pWriter, "block_scrap_from_scrap_2x2");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_SCRAP.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.DUST.get())
                .unlockedBy("has_dust", has(ModItems.DUST.get()))
                .save(pWriter, "block_scrap_from_dust_3x3");

// Блоки из слитков
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_BERYLLIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_BERYLLIUM.get())
                .unlockedBy("has_ingot_beryllium", has(ModItems.INGOT_BERYLLIUM.get()))
                .save(pWriter, "block_beryllium_from_ingot_beryllium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_SCHRABIDIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_SCHRABIDIUM.get())
                .unlockedBy("has_ingot_schrabidium", has(ModItems.INGOT_SCHRABIDIUM.get()))
                .save(pWriter, "block_schrabidium_from_ingot_schrabidium");

// Schrabidium Cluster
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_SCHRABIDIUM_CLUSTER.get())
                .pattern("#S#")
                .pattern("SXS")
                .pattern("#S#")
                .define('#', ModItems.INGOT_SCHRABIDIUM.get())
                .define('S', ModItems.INGOT_STARMETAL.get())
                .define('X', ModItems.INGOT_SCHRABIDATE.get())
                .unlockedBy("has_ingot_schrabidium", has(ModItems.INGOT_SCHRABIDIUM.get()))
                .save(pWriter, "block_schrabidium_cluster");

// Другие блоки
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_EUPHEMIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_EUPHEMIUM.get())
                .unlockedBy("has_ingot_euphemium", has(ModItems.INGOT_EUPHEMIUM.get()))
                .save(pWriter, "block_euphemium_from_ingot_euphemium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_ADVANCED_ALLOY.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_ADVANCED_ALLOY.get())
                .unlockedBy("has_ingot_advanced_alloy", has(ModItems.INGOT_ADVANCED_ALLOY.get()))
                .save(pWriter, "block_advanced_alloy_from_ingot_advanced_alloy");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_MAGNETIZED_TUNGSTEN.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_MAGNETIZED_TUNGSTEN.get())
                .unlockedBy("has_ingot_magnetized_tungsten", has(ModItems.INGOT_MAGNETIZED_TUNGSTEN.get()))
                .save(pWriter, "block_magnetized_tungsten_from_ingot_magnetized_tungsten");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_COMBINE_STEEL.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_COMBINE_STEEL.get())
                .unlockedBy("has_ingot_combine_steel", has(ModItems.INGOT_COMBINE_STEEL.get()))
                .save(pWriter, "block_combine_steel_from_ingot_combine_steel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_AUSTRALIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_AUSTRALIUM.get())
                .unlockedBy("has_ingot_australium", has(ModItems.INGOT_AUSTRALIUM.get()))
                .save(pWriter, "block_australium_from_ingot_australium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_DESH.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_DESH.get())
                .unlockedBy("has_ingot_desh", has(ModItems.INGOT_DESH.get()))
                .save(pWriter, "block_desh_from_ingot_desh");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_DURA_STEEL.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_DURA_STEEL.get())
                .unlockedBy("has_ingot_dura_steel", has(ModItems.INGOT_DURA_STEEL.get()))
                .save(pWriter, "block_dura_steel_from_ingot_dura_steel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_METEOR_COBBLE.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.FRAGMENT_METEORITE.get())
                .unlockedBy("has_fragment_meteorite", has(ModItems.FRAGMENT_METEORITE.get()))
                .save(pWriter, "block_meteor_cobble_from_fragment_meteorite");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_METEOR_BROKEN.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.FRAGMENT_METEORITE.get())
                .unlockedBy("has_fragment_meteorite", has(ModItems.FRAGMENT_METEORITE.get()))
                .save(pWriter, "block_meteor_broken_from_fragment_meteorite");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_YELLOWCAKE.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.POWDER_YELLOWCAKE.get())
                .unlockedBy("has_powder_yellowcake", has(ModItems.POWDER_YELLOWCAKE.get()))
                .save(pWriter, "block_yellowcake_from_powder_yellowcake");

        // Блоки из слитков
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_STARMETAL.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_STARMETAL.get())
                .unlockedBy("has_ingot_starmetal", has(ModItems.INGOT_STARMETAL.get()))
                .save(pWriter, "block_starmetal_from_ingot_starmetal");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_U233.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_U233.get())
                .unlockedBy("has_ingot_u233", has(ModItems.INGOT_U233.get()))
                .save(pWriter, "block_u233_from_ingot_u233");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_U235.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_U235.get())
                .unlockedBy("has_ingot_u235", has(ModItems.INGOT_U235.get()))
                .save(pWriter, "block_u235_from_ingot_u235");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_U238.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_U238.get())
                .unlockedBy("has_ingot_u238", has(ModItems.INGOT_U238.get()))
                .save(pWriter, "block_u238_from_ingot_u238");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_URANIUM_FUEL.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_URANIUM_FUEL.get())
                .unlockedBy("has_ingot_uranium_fuel", has(ModItems.INGOT_URANIUM_FUEL.get()))
                .save(pWriter, "block_uranium_fuel_from_ingot_uranium_fuel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_NEPTUNIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_NEPTUNIUM.get())
                .unlockedBy("has_ingot_neptunium", has(ModItems.INGOT_NEPTUNIUM.get()))
                .save(pWriter, "block_neptunium_from_ingot_neptunium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_POLONIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_POLONIUM.get())
                .unlockedBy("has_ingot_polonium", has(ModItems.INGOT_POLONIUM.get()))
                .save(pWriter, "block_polonium_from_ingot_polonium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_PLUTONIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_PLUTONIUM.get())
                .unlockedBy("has_ingot_plutonium", has(ModItems.INGOT_PLUTONIUM.get()))
                .save(pWriter, "block_plutonium_from_ingot_plutonium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_PU238.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_PU238.get())
                .unlockedBy("has_ingot_pu238", has(ModItems.INGOT_PU238.get()))
                .save(pWriter, "block_pu238_from_ingot_pu238");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_PU239.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_PU239.get())
                .unlockedBy("has_ingot_pu239", has(ModItems.INGOT_PU239.get()))
                .save(pWriter, "block_pu239_from_ingot_pu239");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_PU240.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_PU240.get())
                .unlockedBy("has_ingot_pu240", has(ModItems.INGOT_PU240.get()))
                .save(pWriter, "block_pu240_from_ingot_pu240");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_MOX_FUEL.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_MOX_FUEL.get())
                .unlockedBy("has_ingot_mox_fuel", has(ModItems.INGOT_MOX_FUEL.get()))
                .save(pWriter, "block_mox_fuel_from_ingot_mox_fuel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_PLUTONIUM_FUEL.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_PLUTONIUM_FUEL.get())
                .unlockedBy("has_ingot_plutonium_fuel", has(ModItems.INGOT_PLUTONIUM_FUEL.get()))
                .save(pWriter, "block_plutonium_fuel_from_ingot_plutonium_fuel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_THORIUM_FUEL.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_THORIUM_FUEL.get())
                .unlockedBy("has_ingot_thorium_fuel", has(ModItems.INGOT_THORIUM_FUEL.get()))
                .save(pWriter, "block_thorium_fuel_from_ingot_thorium_fuel");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_SOLINIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_SOLINIUM.get())
                .unlockedBy("has_ingot_solinium", has(ModItems.INGOT_SOLINIUM.get()))
                .save(pWriter, "block_solinium_from_ingot_solinium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_SCHRABIDIUM_FUEL.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_SCHRABIDIUM_FUEL.get())
                .unlockedBy("has_ingot_schrabidium_fuel", has(ModItems.INGOT_SCHRABIDIUM_FUEL.get()))
                .save(pWriter, "block_schrabidium_fuel_from_ingot_schrabidium_fuel");

// Блоки из других материалов
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_LITHIUM.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.LITHIUM.get())
                .unlockedBy("has_lithium", has(ModItems.LITHIUM.get()))
                .save(pWriter, "block_lithium_from_lithium");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_WHITE_PHOSPHORUS.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_PHOSPHORUS.get())
                .unlockedBy("has_ingot_phosphorus", has(ModItems.INGOT_PHOSPHORUS.get()))
                .save(pWriter, "block_white_phosphorus_from_ingot_phosphorus");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_RED_PHOSPHORUS.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.POWDER_FIRE.get())
                .unlockedBy("has_powder_fire", has(ModItems.POWDER_FIRE.get()))
                .save(pWriter, "block_red_phosphorus_from_powder_fire");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_INSULATOR.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.PLATE_POLYMER.get())
                .unlockedBy("has_plate_polymer", has(ModItems.PLATE_POLYMER.get()))
                .save(pWriter, "block_insulator_from_plate_polymer");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_ASBESTOS.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_ASBESTOS.get())
                .unlockedBy("has_ingot_asbestos", has(ModItems.INGOT_ASBESTOS.get()))
                .save(pWriter, "block_asbestos_from_ingot_asbestos");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_FIBERGLASS.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_FIBERGLASS.get())
                .unlockedBy("has_ingot_fiberglass", has(ModItems.INGOT_FIBERGLASS.get()))
                .save(pWriter, "block_fiberglass_from_ingot_fiberglass");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_COBALT.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.INGOT_COBALT.get())
                .unlockedBy("has_ingot_cobalt", has(ModItems.INGOT_COBALT.get()))
                .save(pWriter, "block_cobalt_from_ingot_cobalt");

// Порошки и tiny порошки
        add1To9Pair(ModItems.POWDER_STEEL.get(), ModItems.POWDER_STEEL_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_LITHIUM.get(), ModItems.POWDER_LITHIUM_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_COBALT.get(), ModItems.POWDER_COBALT_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_NEODYMIUM.get(), ModItems.POWDER_NEODYMIUM_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_NIOBIUM.get(), ModItems.POWDER_NIOBIUM_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_CERIUM.get(), ModItems.POWDER_CERIUM_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_LANTHANIUM.get(), ModItems.POWDER_LANTHANIUM_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_ACTINIUM.get(), ModItems.POWDER_ACTINIUM_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_METEORITE.get(), ModItems.POWDER_METEORITE_TINY.get(), pWriter);
        add1To9Pair(ModItems.POWDER_PALEOGENITE.get(), ModItems.POWDER_PALEOGENITE_TINY.get(), pWriter);
        add1To9Pair(ModItems.INGOT_OSMIRIDIUM.get(), ModItems.NUGGET_OSMIRIDIUM.get(), pWriter);

// Ядерные отходы
        add1To9Pair(ModItems.NUCLEAR_WASTE.get(), ModItems.NUCLEAR_WASTE_TINY.get(), pWriter);

// Бутылка ртути (специальный рецепт)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOTTLE_MERCURY.get())
                .pattern("###")
                .pattern("#B#")
                .pattern("###")
                .define('#', ModItems.INGOT_MERCURY.get())
                .define('B', Items.GLASS_BOTTLE)
                .unlockedBy("has_ingot_mercury", has(ModItems.INGOT_MERCURY.get()))
                .save(pWriter, "bottle_mercury");

// Ртуть из бутылки (8 штук)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_MERCURY.get(), 8)
                .requires(ModItems.BOTTLE_MERCURY.get())
                .unlockedBy("has_bottle_mercury", has(ModItems.BOTTLE_MERCURY.get()))
                .save(pWriter, "mercury_from_bottle");

// Балафайр яйцо
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EGG_BALEFIRE.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.EGG_BALEFIRE_SHARD.get())
                .unlockedBy("has_egg_balefire_shard", has(ModItems.EGG_BALEFIRE_SHARD.get()))
                .save(pWriter, "egg_balefire");

// Осколки балафайр яйца
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.EGG_BALEFIRE_SHARD.get(), 9)
                .requires(ModItems.EGG_BALEFIRE.get())
                .unlockedBy("has_egg_balefire", has(ModItems.EGG_BALEFIRE.get()))
                .save(pWriter, "egg_balefire_shard_from_egg");

// Нитро
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NITRA.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.NITRA_SMALL.get())
                .unlockedBy("has_nitra_small", has(ModItems.NITRA_SMALL.get()))
                .save(pWriter, "nitra_from_small");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.NITRA_SMALL.get(), 4)
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, "nitra_small_from_nitra");

// Контейнер для боеприпасов
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AMMO_CONTAINER.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.NITRA.get())
                .unlockedBy("has_nitra", has(ModItems.NITRA.get()))
                .save(pWriter, "ammo_container");

// Поляризованное стекло (4 штуки)
// Для GLASS_POLARIZED нужен отдельный предмет или ItemStack с CustomModelData
// Т.к. в стандартных рецептах нельзя указать CustomModelData для ингредиента,
// создаём отдельный предмет для GLASS_POLARIZED или используем SimpleRecipe
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GLASS_POLARIZED.get(), 4)
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.PART_GENERIC.get())
                .unlockedBy("has_part_generic", has(ModItems.PART_GENERIC.get()))
                .save(pWriter, "glass_polarized");

        // Осколок балафайр яйца из порошка балафайр (2x2)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EGG_BALEFIRE_SHARD.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.POWDER_BALEFIRE.get())
                .unlockedBy("has_powder_balefire", has(ModItems.POWDER_BALEFIRE.get()))
                .save(pWriter, "egg_balefire_shard_from_powder_balefire");

// Ячейка балафайр из 9 осколков
        add9To1(ModItems.EGG_BALEFIRE_SHARD.get(), ModItems.CELL_BALEFIRE.get(), pWriter);

// Евфемий
        add1To9Pair(ModItems.INGOT_EUPHEMIUM.get(), ModItems.NUGGET_EUPHEMIUM.get(), pWriter);

        // Шрабридиевое топливо (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_SCHRABIDIUM_FUEL.get())
                .requires(ModItems.NUGGET_SCHRABIDIUM.get(), 3)
                .requires(ModItems.NUGGET_NEPTUNIUM.get(), 3)
                .requires(ModItems.NUGGET_BERYLLIUM.get(), 3)
                .unlockedBy("has_nugget_schrabidium", has(ModItems.NUGGET_SCHRABIDIUM.get()))
                .save(pWriter, "ingot_schrabidium_fuel_from_nuggets");

// HES (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_HES.get())
                .requires(ModItems.NUGGET_SCHRABIDIUM.get(), 5)
                .requires(ModItems.NUGGET_NEPTUNIUM.get(), 2)
                .requires(ModItems.NUGGET_BERYLLIUM.get(), 2)
                .unlockedBy("has_nugget_schrabidium", has(ModItems.NUGGET_SCHRABIDIUM.get()))
                .save(pWriter, "ingot_hes_from_nuggets");

// LES (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_LES.get())
                .requires(ModItems.NUGGET_SCHRABIDIUM.get())
                .requires(ModItems.NUGGET_NEPTUNIUM.get(), 4)
                .requires(ModItems.NUGGET_BERYLLIUM.get(), 4)
                .unlockedBy("has_nugget_schrabidium", has(ModItems.NUGGET_SCHRABIDIUM.get()))
                .save(pWriter, "ingot_les_from_nuggets");

// Смесь плутония (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_PU_MIX.get())
                .requires(ModItems.NUGGET_PU239.get(), 6)
                .requires(ModItems.NUGGET_PU240.get(), 3)
                .unlockedBy("has_nugget_pu239", has(ModItems.NUGGET_PU239.get()))
                .save(pWriter, "ingot_pu_mix_from_nuggets");

// Смесь плутония (из tiny-версий)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_PU_MIX.get())
                .requires(ModItems.NUGGET_PU239.get(), 6) // tiny в оригинале, используем nugget
                .requires(ModItems.NUGGET_PU240.get(), 3)
                .unlockedBy("has_nugget_pu239", has(ModItems.NUGGET_PU239.get()))
                .save(pWriter, "ingot_pu_mix_from_tiny");

// Смесь америция (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_AM_MIX.get())
                .requires(ModItems.NUGGET_AM241.get(), 3)
                .requires(ModItems.NUGGET_AM242.get(), 6)
                .unlockedBy("has_nugget_am241", has(ModItems.NUGGET_AM241.get()))
                .save(pWriter, "ingot_am_mix_from_nuggets");

// Смесь америция (из tiny-версий)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_AM_MIX.get())
                .requires(ModItems.NUGGET_AM241.get(), 3) // tiny в оригинале, используем nugget
                .requires(ModItems.NUGGET_AM242.get(), 6)
                .unlockedBy("has_nugget_am241", has(ModItems.NUGGET_AM241.get()))
                .save(pWriter, "ingot_am_mix_from_tiny");

        // Огнеупорная глина (4 штуки) из глины и пыли алюминия
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_FIRECLAY.get(), 4)
                .requires(Items.CLAY_BALL, 3)
                .requires(ModItems.POWDER_ALUMINIUM.get())
                .unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
                .save(pWriter, "ball_fireclay_from_clay_aluminium");

// Огнеупорная глина (4 штуки) из глины и алюминиевой руды
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_FIRECLAY.get(), 4)
                .requires(Items.CLAY_BALL, 3)
                .requires(ModItems.ORE_ALUMINIUM.get()) // или руда алюминия
                .unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
                .save(pWriter, "ball_fireclay_from_clay_ore_aluminium");

// Огнеупорная глина (4 штуки) из глины, известняка и песка
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_FIRECLAY.get(), 4)
                .requires(Items.CLAY_BALL, 2)
                .requires(ModBlocks.STONE_RESOURCE_LIMESTONE.get()) // известняк
                .requires(Items.SAND)
                .unlockedBy("has_clay_ball", has(Items.CLAY_BALL))
                .save(pWriter, "ball_fireclay_from_clay_limestone_sand");

        /*
TODO: Портировать рецепты для побочных продуктов руды (ore_byproduct)
Для этого нужно:
1. Создать класс ItemOreByproduct с enum EnumByproduct (B_IRON, B_COPPER, B_LITHIUM, B_SILICON, B_LEAD, B_TITANIUM, B_ALUMINIUM, B_SULFUR, B_CALCIUM, B_BISMUTH, B_RADIUM, B_TECHNETIUM, B_POLONIUM, B_URANIUM)
2. Зарегистрировать ORE_BYPRODUCT в ModItems как ItemEnumMulti с ItemModelType.ENUM_ITEM
3. Для каждого типа создать рецепт сжатия 9 штук в соответствующий предмет

Пример реализации:
for (ItemOreByproduct.EnumByproduct byproduct : ItemOreByproduct.EnumByproduct.values()) {
    int meta = byproduct.ordinal();
    ItemStack input = new ItemStack(ModItems.ORE_BYPRODUCT.get());
    input.getOrCreateTag().putInt("CustomModelData", meta);

    ItemStack result = switch (byproduct) {
        case B_IRON -> new ItemStack(ModItems.POWDER_IRON.get());
        case B_COPPER -> new ItemStack(ModItems.POWDER_COPPER.get());
        case B_LITHIUM -> new ItemStack(ModItems.POWDER_LITHIUM.get());
        case B_SILICON -> new ItemStack(ModItems.NUGGET_SILICON.get(), 3);
        case B_LEAD -> new ItemStack(ModItems.POWDER_LEAD.get());
        case B_TITANIUM -> new ItemStack(ModItems.POWDER_TITANIUM.get());
        case B_ALUMINIUM -> new ItemStack(ModItems.POWDER_ALUMINIUM.get());
        case B_SULFUR -> new ItemStack(ModItems.SULFUR.get());
        case B_CALCIUM -> new ItemStack(ModItems.POWDER_CALCIUM.get());
        case B_BISMUTH -> new ItemStack(ModItems.POWDER_BISMUTH.get());
        case B_RADIUM -> new ItemStack(ModItems.POWDER_RA226.get());
        case B_TECHNETIUM -> new ItemStack(ModItems.BILLET_TECHNETIUM.get());
        case B_POLONIUM -> new ItemStack(ModItems.BILLET_POLONIUM.get());
        case B_URANIUM -> new ItemStack(ModItems.POWDER_URANIUM.get());
    };

    ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, result.getItem(), result.getCount())
            .requires(input.getItem())
            .unlockedBy("has_ore_byproduct", has(ModItems.ORE_BYPRODUCT.get()))
            .save(pWriter, "ore_byproduct_to_" + byproduct.name().toLowerCase());
}
*/


    }

    /**
     * Добавляет два рецепта: один предмет -> 9 штук (разжатие) и 9 штук -> один предмет (сжатие).
     */
    public static void add1To9Pair(Item one, Item nine, Consumer<FinishedRecipe> pWriter) {
        add1To9(one, nine, pWriter);
        add9To1(nine, one, pWriter);
    }

    public static void add1To9Pair(Block one, Item nine, Consumer<FinishedRecipe> pWriter) {
        add1To9(one.asItem(), nine, pWriter);
        add9To1(nine, one.asItem(), pWriter);
    }

    public static void add1To9Pair(ItemStack one, ItemStack nine, Consumer<FinishedRecipe> pWriter) {
        add1To9(one.getItem(), nine.getItem(), pWriter);
        add9To1(nine.getItem(), one.getItem(), pWriter);
    }

    /**
     * Для предметов с одинаковой метой (CustomModelData) добавляет пару рецептов.
     * Использует SimpleRecipe для установки CustomModelData в результате.
     */
    public static void add1To9PairSameMeta(Item one, Item nine, int meta, Consumer<FinishedRecipe> pWriter) {
        String oneName = one.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String nineName = nine.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String recipeName = oneName + "_to_" + nineName + "_" + meta;

        // 1 -> 9
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nine)
                .requires(one)
                .unlockedBy("has_" + oneName + "_" + meta, has(one))
                .save(pWriter, recipeName);

        // 9 -> 1
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, one)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', nine)
                .unlockedBy("has_" + nineName + "_" + meta, has(nine))
                .save(pWriter, recipeName + "_reverse");
    }

    /**
     * Полный набор: самородок -> слиток -> блок.
     */
    public static void addMineralSet(Item nugget, Item ingot, Block block, Consumer<FinishedRecipe> pWriter) {
        // 1 слиток = 9 самородков
        add1To9(ingot, nugget, pWriter);
        // 9 самородков = 1 слиток
        add9To1(nugget, ingot, pWriter);
        // 1 блок = 9 слитков
        add1To9Pair(block, ingot, pWriter);
    }

    // ----- Разжатие (1 -> 9) -----
    public static void add1To9(Item one, Item nine, Consumer<FinishedRecipe> pWriter) {
        String oneName = one.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String nineName = nine.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nine)
                .requires(one)
                .unlockedBy("has_" + oneName, has(one))
                .save(pWriter, oneName + "_to_" + nineName);
    }

    // ----- Сжатие (9 -> 1) -----
    public static void add9To1(Item nine, Item one, Consumer<FinishedRecipe> pWriter) {
        String nineName = nine.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String oneName = one.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, one)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', nine)
                .unlockedBy("has_" + nineName, has(nine))
                .save(pWriter, nineName + "_to_" + oneName);
    }

    // ----- Биллеты (специальные формы) -----
    public static void addBillet(Item billet, Item nugget, Consumer<FinishedRecipe> pWriter) {
        String billetName = billet.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String nuggetName = nugget.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");

        // billet = 6 nugget (2 ряда по 3)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, billet)
                .pattern("###")
                .pattern("###")
                .define('#', nugget)
                .unlockedBy("has_" + nuggetName, has(nugget))
                .save(pWriter, billetName + "_from_nuggets");

        // 6 nugget из биллета
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget)
                .requires(billet)
                .unlockedBy("has_" + billetName, has(billet))
                .save(pWriter, nuggetName + "_from_billet");
    }

    public static void addBillet(Item billet, Item ingot, Item nugget, Consumer<FinishedRecipe> pWriter) {
        addBillet(billet, nugget, pWriter);

        String billetName = billet.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String ingotName = ingot.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");

        // 2 слитка из 3 биллетов
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot)
                .requires(billet, 3)
                .unlockedBy("has_" + billetName, has(billet))
                .save(pWriter, ingotName + "_from_billets");

        // 3 биллета из 1 слитка
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, billet)
                .pattern("##")
                .define('#', ingot)
                .unlockedBy("has_" + ingotName, has(ingot))
                .save(pWriter, billetName + "_from_ingot");
    }

    // Вспомогательный метод для получения Item с CustomModelData в рецепте
    // Для этого нужно использовать custom serializer или создавать отдельные рецепты
    private static ItemStack createStackWithMeta(Item item, int count, int meta) {
        ItemStack stack = new ItemStack(item, count);
        if (meta > 0) {
            stack.getOrCreateTag().putInt("CustomModelData", meta);
        }
        return stack;
    }
}