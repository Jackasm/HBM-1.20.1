package com.hbm.datagen.recipes;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.items.machine.ItemRTGPelletDepleted;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.function.Consumer;
import java.util.function.Function;

import static com.hbm.util.RefStrings.MODID;

public class MineralRecipes {

    private static Consumer<FinishedRecipe> writer;
    private static Function<Item, InventoryChangeTrigger.TriggerInstance> has;

    public static void generateMineralRecipes(Consumer<FinishedRecipe> pWriter,
                                              Function<Item, InventoryChangeTrigger.TriggerInstance> pHas) {

        writer = pWriter;
        has = pHas;

        addMineralSet(ModItems.NUGGET_NIOBIUM.get(), ModItems.INGOT_NIOBIUM.get(), ModBlocks.BLOCK_NIOBIUM.get());
        addMineralSet(ModItems.NUGGET_BISMUTH.get(), ModItems.INGOT_BISMUTH.get(), ModBlocks.BLOCK_BISMUTH.get());
        addMineralSet(ModItems.NUGGET_TANTALIUM.get(), ModItems.INGOT_TANTALIUM.get(), ModBlocks.BLOCK_TANTALIUM.get());
        addMineralSet(ModItems.NUGGET_ZIRCONIUM.get(), ModItems.INGOT_ZIRCONIUM.get(), ModBlocks.BLOCK_ZIRCONIUM.get());
        addMineralSet(ModItems.NUGGET_DINEUTRONIUM.get(), ModItems.INGOT_DINEUTRONIUM.get(), ModBlocks.BLOCK_DINEUTRONIUM.get());
        addMineralSet(ModItems.NUCLEAR_WASTE_VITRIFIED_TINY.get(), ModItems.NUCLEAR_WASTE_VITRIFIED.get(), ModBlocks.BLOCK_WASTE_VITRIFIED.get());
        addMineralSet(ModItems.NUGGET_URANIUM.get(), ModItems.INGOT_URANIUM.get(), ModBlocks.BLOCK_URANIUM.get());
        addMineralSet(ModItems.NUGGET_TH232.get(), ModItems.INGOT_TH232.get(), ModBlocks.BLOCK_THORIUM.get());
        addMineralSet(ModItems.NUGGET_LEAD.get(), ModItems.INGOT_LEAD.get(), ModBlocks.BLOCK_LEAD.get());
        addMineralSet(ModItems.NUGGET_PU_MIX.get(), ModItems.INGOT_PU_MIX.get(), ModBlocks.BLOCK_PU_MIX.get());
        addMineralSet(ModItems.NUGGET_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), ModBlocks.BLOCK_BERYLLIUM.get());
        addMineralSet(ModItems.NUGGET_SCHRABIDIUM.get(), ModItems.INGOT_SCHRABIDIUM.get(), ModBlocks.BLOCK_SCHRABIDIUM.get());
        addMineralSet(ModItems.NUGGET_EUPHEMIUM.get(), ModItems.INGOT_EUPHEMIUM.get(), ModBlocks.BLOCK_EUPHEMIUM.get());
        addMineralSet(ModItems.NUGGET_AUSTRALIUM.get(), ModItems.INGOT_AUSTRALIUM.get(), ModBlocks.BLOCK_AUSTRALIUM.get());
        addMineralSet(ModItems.NUGGET_U233.get(), ModItems.INGOT_U233.get(), ModBlocks.BLOCK_U233.get());
        addMineralSet(ModItems.NUGGET_U235.get(), ModItems.INGOT_U235.get(), ModBlocks.BLOCK_U235.get());
        addMineralSet(ModItems.NUGGET_U238.get(), ModItems.INGOT_U238.get(), ModBlocks.BLOCK_U238.get());
        addMineralSet(ModItems.NUGGET_URANIUM_FUEL.get(), ModItems.INGOT_URANIUM_FUEL.get(), ModBlocks.BLOCK_URANIUM_FUEL.get());
        addMineralSet(ModItems.NUGGET_NEPTUNIUM.get(), ModItems.INGOT_NEPTUNIUM.get(), ModBlocks.BLOCK_NEPTUNIUM.get());
        addMineralSet(ModItems.NUGGET_POLONIUM.get(), ModItems.INGOT_POLONIUM.get(), ModBlocks.BLOCK_POLONIUM.get());
        addMineralSet(ModItems.NUGGET_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), ModBlocks.BLOCK_PLUTONIUM.get());
        addMineralSet(ModItems.NUGGET_PU238.get(), ModItems.INGOT_PU238.get(), ModBlocks.BLOCK_PU238.get());
        addMineralSet(ModItems.NUGGET_PU239.get(), ModItems.INGOT_PU239.get(), ModBlocks.BLOCK_PU239.get());
        addMineralSet(ModItems.NUGGET_PU240.get(), ModItems.INGOT_PU240.get(), ModBlocks.BLOCK_PU240.get());
        addMineralSet(ModItems.NUGGET_MOX_FUEL.get(), ModItems.INGOT_MOX_FUEL.get(), ModBlocks.BLOCK_MOX_FUEL.get());
        addMineralSet(ModItems.NUGGET_PLUTONIUM_FUEL.get(), ModItems.INGOT_PLUTONIUM_FUEL.get(), ModBlocks.BLOCK_PLUTONIUM_FUEL.get());
        addMineralSet(ModItems.NUGGET_THORIUM_FUEL.get(), ModItems.INGOT_THORIUM_FUEL.get(), ModBlocks.BLOCK_THORIUM_FUEL.get());
        addMineralSet(ModItems.NUGGET_SOLINIUM.get(), ModItems.INGOT_SOLINIUM.get(), ModBlocks.BLOCK_SOLINIUM.get());
        addMineralSet(ModItems.NUGGET_SCHRABIDIUM_FUEL.get(), ModItems.INGOT_SCHRABIDIUM_FUEL.get(), ModBlocks.BLOCK_SCHRABIDIUM_FUEL.get());
        addMineralSet(ModItems.NUGGET_COBALT.get(), ModItems.INGOT_COBALT.get(), ModBlocks.BLOCK_COBALT.get());

        add1To9Pair(ModItems.DUST.get(), ModItems.DUST_TINY.get());
        add1To9Pair(ModItems.POWDER_COAL.get(), ModItems.POWDER_COAL_TINY.get());
        add1To9Pair(ModItems.INGOT_MERCURY.get(), ModItems.NUGGET_MERCURY.get());
        add1To9Pair(ModItems.INGOT_SILICON.get(), ModItems.NUGGET_SILICON.get());

        // Блоки -> слитки и обратно
        add1To9Pair(ModBlocks.BLOCK_ALUMINIUM.get(), ModItems.INGOT_ALUMINIUM.get());
        add1To9Pair(ModBlocks.BLOCK_GRAPHITE.get(), ModItems.INGOT_GRAPHITE.get());
        add1To9Pair(ModBlocks.BLOCK_BORON.get(), ModItems.INGOT_BORON.get());
        add1To9Pair(ModBlocks.BLOCK_SCHRARANIUM.get(), ModItems.INGOT_SCHRARANIUM.get());
        add1To9Pair(ModBlocks.BLOCK_SCHRABIDATE.get(), ModItems.INGOT_SCHRABIDATE.get());
        add1To9Pair(ModBlocks.BLOCK_COLTAN.get(), ModItems.FRAGMENT_COLTAN.get());
        add1To9Pair(ModBlocks.BLOCK_SMORE.get(), ModItems.INGOT_SMORE.get());
        add1To9Pair(ModBlocks.BLOCK_SEMTEX.get(), ModItems.INGOT_SEMTEX.get());
        add1To9Pair(ModBlocks.BLOCK_C4.get(), ModItems.INGOT_C4.get());
        add1To9Pair(ModBlocks.BLOCK_POLYMER.get(), ModItems.INGOT_POLYMER.get());
        add1To9Pair(ModBlocks.BLOCK_BAKELITE.get(), ModItems.INGOT_BAKELITE.get());
        add1To9Pair(ModBlocks.BLOCK_RUBBER.get(), ModItems.INGOT_RUBBER.get());
        add1To9Pair(ModBlocks.BLOCK_LANTHANIUM.get(), ModItems.INGOT_LANTHANIUM.get());
        add1To9Pair(ModBlocks.BLOCK_RA226.get(), ModItems.INGOT_RA226.get());
        add1To9Pair(ModBlocks.BLOCK_ACTINIUM.get(), ModItems.INGOT_ACTINIUM.get());
        add1To9Pair(ModBlocks.BLOCK_CADMIUM.get(), ModItems.INGOT_CADMIUM.get());
        add1To9Pair(ModBlocks.BLOCK_TCALLOY.get(), ModItems.INGOT_TCALLOY.get());
        add1To9Pair(ModBlocks.BLOCK_CDALLOY.get(), ModItems.INGOT_CDALLOY.get());
        add1To9Pair(ModBlocks.BLOCK_FLUORITE.get(), ModItems.FLUORITE.get());
        add1To9Pair(ModBlocks.BLOCK_NITER.get(), ModItems.NITER.get());
        add1To9Pair(ModBlocks.BLOCK_RED_COPPER.get(), ModItems.INGOT_RED_COPPER.get());
        add1To9Pair(ModBlocks.BLOCK_STEEL.get(), ModItems.INGOT_STEEL.get());
        add1To9Pair(ModBlocks.BLOCK_SULFUR.get(), ModItems.SULFUR.get());
        add1To9Pair(ModBlocks.BLOCK_TITANIUM.get(), ModItems.INGOT_TITANIUM.get());
        add1To9Pair(ModBlocks.BLOCK_TUNGSTEN.get(), ModItems.INGOT_TUNGSTEN.get());
        add1To9Pair(ModBlocks.BLOCK_TRINITITE.get(), ModItems.TRINITITE.get());
        add1To9Pair(ModBlocks.BLOCK_WASTE.get(), ModItems.NUCLEAR_WASTE.get());
        add1To9Pair(ModBlocks.BLOCK_ADVANCED_ALLOY.get(), ModItems.INGOT_ADVANCED_ALLOY.get());
        add1To9Pair(ModBlocks.BLOCK_MAGNETIZED_TUNGSTEN.get(), ModItems.INGOT_MAGNETIZED_TUNGSTEN.get());
        add1To9Pair(ModBlocks.BLOCK_COMBINE_STEEL.get(), ModItems.INGOT_COMBINE_STEEL.get());
        add1To9Pair(ModBlocks.BLOCK_DESH.get(), ModItems.INGOT_DESH.get());
        add1To9Pair(ModBlocks.BLOCK_DURA_STEEL.get(), ModItems.INGOT_DURA_STEEL.get());
        add1To9Pair(ModBlocks.BLOCK_YELLOWCAKE.get(), ModItems.POWDER_YELLOWCAKE.get());
        add1To9Pair(ModBlocks.BLOCK_STARMETAL.get(), ModItems.INGOT_STARMETAL.get());
        add1To9Pair(ModBlocks.BLOCK_LITHIUM.get(), ModItems.LITHIUM.get());
        add1To9Pair(ModBlocks.BLOCK_WHITE_PHOSPHORUS.get(), ModItems.INGOT_PHOSPHORUS.get());
        add1To9Pair(ModBlocks.BLOCK_RED_PHOSPHORUS.get(), ModItems.POWDER_FIRE.get());
        add1To9Pair(ModBlocks.BLOCK_INSULATOR.get(), ModItems.PLATE_POLYMER.get());
        add1To9Pair(ModBlocks.BLOCK_ASBESTOS.get(), ModItems.INGOT_ASBESTOS.get());
        add1To9Pair(ModBlocks.BLOCK_FIBERGLASS.get(), ModItems.INGOT_FIBERGLASS.get());

        add1To9Pair(ModBlocks.BLOCK_COKE_COAL.get().asItem(), ModItems.COKE_COAL.get());
        add1To9Pair(ModBlocks.BLOCK_COKE_LIGNITE.get().asItem(), ModItems.COKE_LIGNITE.get());
        add1To9Pair(ModBlocks.BLOCK_COKE_PETROLEUM.get().asItem(), ModItems.COKE_PETROLEUM.get());

        //Порошки
        add1To9Pair(ModItems.POWDER_BORON.get(), ModItems.POWDER_BORON_TINY.get());
        add1To9Pair(ModItems.POWDER_SR90.get(), ModItems.POWDER_SR90_TINY.get());
        add1To9Pair(ModItems.POWDER_XE135.get(), ModItems.POWDER_XE135_TINY.get());
        add1To9Pair(ModItems.POWDER_CS137.get(), ModItems.POWDER_CS137_TINY.get());
        add1To9Pair(ModItems.POWDER_I131.get(), ModItems.POWDER_I131_TINY.get());

        // Слитки и самородки
        add1To9Pair(ModItems.INGOT_TECHNETIUM.get(), ModItems.NUGGET_TECHNETIUM.get());
        add1To9Pair(ModItems.INGOT_CO60.get(), ModItems.NUGGET_CO60.get());
        add1To9Pair(ModItems.INGOT_SR90.get(), ModItems.NUGGET_SR90.get());
        add1To9Pair(ModItems.INGOT_AU198.get(), ModItems.NUGGET_AU198.get());
        add1To9Pair(ModItems.INGOT_PB209.get(), ModItems.NUGGET_PB209.get());
        add1To9Pair(ModItems.INGOT_RA226.get(), ModItems.NUGGET_RA_226.get());
        add1To9Pair(ModItems.INGOT_ACTINIUM.get(), ModItems.NUGGET_ACTINIUM.get());
        add1To9Pair(ModItems.INGOT_ARSENIC.get(), ModItems.NUGGET_ARSENIC.get());
        add1To9Pair(ModItems.INGOT_PU241.get(), ModItems.NUGGET_PU241.get());
        add1To9Pair(ModItems.INGOT_AM241.get(), ModItems.NUGGET_AM241.get());
        add1To9Pair(ModItems.INGOT_AM242.get(), ModItems.NUGGET_AM242.get());
        add1To9Pair(ModItems.INGOT_AM_MIX.get(), ModItems.NUGGET_AM_MIX.get());
        add1To9Pair(ModItems.INGOT_AMERICIUM_FUEL.get(), ModItems.NUGGET_AMERICIUM_FUEL.get());
        add1To9Pair(ModItems.INGOT_GH336.get(), ModItems.NUGGET_GH336.get());
        add1To9Pair(ModItems.INGOT_NEPTUNIUM_FUEL.get(), ModItems.NUGGET_NEPTUNIUM_FUEL.get());

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
        add1To9Pair(ModBlocks.BLOCK_FALLOUT.get(), ModItems.FALLOUT.get());

        // 2 блока fallout из 4 единиц fallout
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.BLOCK_FALLOUT.get(), 2)
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.FALLOUT.get())
                .unlockedBy("has_fallout", has.apply(ModItems.FALLOUT.get()))
                .save(pWriter, MODID + ":" + "fallout_block_from_fallout");



        addBillet(ModItems.BILLET_COBALT.get(), ModItems.INGOT_COBALT.get(), ModItems.NUGGET_COBALT.get());
        addBillet(ModItems.BILLET_CO60.get(), ModItems.INGOT_CO60.get(), ModItems.NUGGET_CO60.get());
        addBillet(ModItems.BILLET_SR90.get(), ModItems.INGOT_SR90.get(), ModItems.NUGGET_SR90.get());
        addBillet(ModItems.BILLET_URANIUM.get(), ModItems.INGOT_URANIUM.get(), ModItems.NUGGET_URANIUM.get());
        addBillet(ModItems.BILLET_U233.get(), ModItems.INGOT_U233.get(), ModItems.NUGGET_U233.get());
        addBillet(ModItems.BILLET_U235.get(), ModItems.INGOT_U235.get(), ModItems.NUGGET_U235.get());
        addBillet(ModItems.BILLET_U238.get(), ModItems.INGOT_U238.get(), ModItems.NUGGET_U238.get());
        addBillet(ModItems.BILLET_TH232.get(), ModItems.INGOT_TH232.get(), ModItems.NUGGET_TH232.get());
        addBillet(ModItems.BILLET_PLUTONIUM.get(), ModItems.INGOT_PLUTONIUM.get(), ModItems.NUGGET_PLUTONIUM.get());
        addBillet(ModItems.BILLET_PU238.get(), ModItems.INGOT_PU238.get(), ModItems.NUGGET_PU238.get());
        addBillet(ModItems.BILLET_PU239.get(), ModItems.INGOT_PU239.get(), ModItems.NUGGET_PU239.get());
        addBillet(ModItems.BILLET_PU240.get(), ModItems.INGOT_PU240.get(), ModItems.NUGGET_PU240.get());
        addBillet(ModItems.BILLET_PU241.get(), ModItems.INGOT_PU241.get(), ModItems.NUGGET_PU241.get());
        addBillet(ModItems.BILLET_PU_MIX.get(), ModItems.INGOT_PU_MIX.get(), ModItems.NUGGET_PU_MIX.get());
        addBillet(ModItems.BILLET_AM241.get(), ModItems.INGOT_AM241.get(), ModItems.NUGGET_AM241.get());
        addBillet(ModItems.BILLET_AM242.get(), ModItems.INGOT_AM242.get(), ModItems.NUGGET_AM242.get());
        addBillet(ModItems.BILLET_AM_MIX.get(), ModItems.INGOT_AM_MIX.get(), ModItems.NUGGET_AM_MIX.get());
        addBillet(ModItems.BILLET_NEPTUNIUM.get(), ModItems.INGOT_NEPTUNIUM.get(), ModItems.NUGGET_NEPTUNIUM.get());
        addBillet(ModItems.BILLET_POLONIUM.get(), ModItems.INGOT_POLONIUM.get(), ModItems.NUGGET_POLONIUM.get());
        addBillet(ModItems.BILLET_TECHNETIUM.get(), ModItems.INGOT_TECHNETIUM.get(), ModItems.NUGGET_TECHNETIUM.get());
        addBillet(ModItems.BILLET_AU198.get(), ModItems.INGOT_AU198.get(), ModItems.NUGGET_AU198.get());
        addBillet(ModItems.BILLET_PB209.get(), ModItems.INGOT_PB209.get(), ModItems.NUGGET_PB209.get());
        addBillet(ModItems.BILLET_RA226.get(), ModItems.INGOT_RA226.get(), ModItems.NUGGET_RA_226.get());
        addBillet(ModItems.BILLET_ACTINIUM.get(), ModItems.INGOT_ACTINIUM.get(), ModItems.NUGGET_ACTINIUM.get());
        addBillet(ModItems.BILLET_SCHRABIDIUM.get(), ModItems.INGOT_SCHRABIDIUM.get(), ModItems.NUGGET_SCHRABIDIUM.get());
        addBillet(ModItems.BILLET_SOLINIUM.get(), ModItems.INGOT_SOLINIUM.get(), ModItems.NUGGET_SOLINIUM.get());
        addBillet(ModItems.BILLET_GH336.get(), ModItems.INGOT_GH336.get(), ModItems.NUGGET_GH336.get());
        addBillet(ModItems.BILLET_URANIUM_FUEL.get(), ModItems.INGOT_URANIUM_FUEL.get(), ModItems.NUGGET_URANIUM_FUEL.get());
        addBillet(ModItems.BILLET_THORIUM_FUEL.get(), ModItems.INGOT_THORIUM_FUEL.get(), ModItems.NUGGET_THORIUM_FUEL.get());
        addBillet(ModItems.BILLET_PLUTONIUM_FUEL.get(), ModItems.INGOT_PLUTONIUM_FUEL.get(), ModItems.NUGGET_PLUTONIUM_FUEL.get());
        addBillet(ModItems.BILLET_NEPTUNIUM_FUEL.get(), ModItems.INGOT_NEPTUNIUM_FUEL.get(), ModItems.NUGGET_NEPTUNIUM_FUEL.get());
        addBillet(ModItems.BILLET_MOX_FUEL.get(), ModItems.INGOT_MOX_FUEL.get(), ModItems.NUGGET_MOX_FUEL.get());
        addBillet(ModItems.BILLET_LES.get(), ModItems.INGOT_LES.get(), ModItems.NUGGET_LES.get());
        addBillet(ModItems.BILLET_SCHRABIDIUM_FUEL.get(), ModItems.INGOT_SCHRABIDIUM_FUEL.get(), ModItems.NUGGET_SCHRABIDIUM_FUEL.get());
        addBillet(ModItems.BILLET_HES.get(), ModItems.INGOT_HES.get(), ModItems.NUGGET_HES.get());
        addBillet(ModItems.BILLET_AUSTRALIUM.get(), ModItems.INGOT_AUSTRALIUM.get(), ModItems.NUGGET_AUSTRALIUM.get());
        addBillet(ModItems.BILLET_AUSTRALIUM_GREATER.get(), ModItems.NUGGET_AUSTRALIUM_GREATER.get());
        addBillet(ModItems.BILLET_AUSTRALIUM_LESSER.get(), ModItems.NUGGET_AUSTRALIUM_LESSER.get());
        addBillet(ModItems.BILLET_NUCLEAR_WASTE.get(), ModItems.NUCLEAR_WASTE.get(), ModItems.NUCLEAR_WASTE_TINY.get());
        addBillet(ModItems.BILLET_BERYLLIUM.get(), ModItems.INGOT_BERYLLIUM.get(), ModItems.NUGGET_BERYLLIUM.get());
        addBillet(ModItems.BILLET_ZIRCONIUM.get(), ModItems.INGOT_ZIRCONIUM.get(), ModItems.NUGGET_ZIRCONIUM.get());
        addBillet(ModItems.BILLET_BISMUTH.get(), ModItems.INGOT_BISMUTH.get(), ModItems.NUGGET_BISMUTH.get());
        addBillet(ModItems.BILLET_SILICON.get(), ModItems.INGOT_SILICON.get(), ModItems.NUGGET_SILICON.get());

        // Ториевое топливо (6 биллетов)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_THORIUM_FUEL.get(), 6)
                .requires(ModItems.BILLET_TH232.get(), 5)
                .requires(ModItems.BILLET_U233.get())
                .unlockedBy("has_billet_th232", has.apply(ModItems.BILLET_TH232.get()))
                .save(pWriter, MODID + ":" + "billet_thorium_fuel_from_th232_u233");

        // Урановое топливо (6 биллетов)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_URANIUM_FUEL.get(), 6)
                .requires(ModItems.BILLET_U238.get(), 5)
                .requires(ModItems.BILLET_U235.get())
                .unlockedBy("has_billet_u238", has.apply(ModItems.BILLET_U238.get()))
                .save(pWriter, MODID + ":" + "billet_uranium_fuel_from_u238_u235");

        // Плутониевое топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_PLUTONIUM_FUEL.get(), 3)
                .requires(ModItems.BILLET_U238.get(), 2)
                .requires(ModItems.BILLET_PU_MIX.get())
                .unlockedBy("has_billet_pu_mix", has.apply(ModItems.BILLET_PU_MIX.get()))
                .save(pWriter, MODID + ":" + "billet_plutonium_fuel_from_u238_pu_mix");

        // Смесь плутония (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_PU_MIX.get(), 3)
                .requires(ModItems.BILLET_PU239.get(), 2)
                .requires(ModItems.BILLET_PU240.get())
                .unlockedBy("has_billet_pu239", has.apply(ModItems.BILLET_PU239.get()))
                .save(pWriter, MODID + ":" + "billet_pu_mix_from_pu239_pu240");

        // Америциевое топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_AMERICIUM_FUEL.get(), 3)
                .requires(ModItems.BILLET_U238.get(), 2)
                .requires(ModItems.BILLET_AM_MIX.get())
                .unlockedBy("has_billet_am_mix", has.apply(ModItems.BILLET_AM_MIX.get()))
                .save(pWriter, MODID + ":" + "billet_americium_fuel_from_u238_am_mix");

        // Америциевое топливо (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_AMERICIUM_FUEL.get())
                .requires(ModItems.NUGGET_AM_MIX.get(), 2)
                .requires(ModItems.NUGGET_U238.get(), 4)
                .unlockedBy("has_nugget_am_mix", has.apply(ModItems.NUGGET_AM_MIX.get()))
                .save(pWriter, MODID + ":" + "billet_americium_fuel_from_nuggets");

        // Смесь америция (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_AM_MIX.get(), 3)
                .requires(ModItems.BILLET_AM241.get())
                .requires(ModItems.BILLET_AM242.get(), 2)
                .unlockedBy("has_billet_am241", has.apply(ModItems.BILLET_AM241.get()))
                .save(pWriter, MODID + ":" + "billet_am_mix_from_am241_am242");

        // Нептуниевое топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_NEPTUNIUM_FUEL.get(), 3)
                .requires(ModItems.BILLET_U238.get(), 2)
                .requires(ModItems.BILLET_NEPTUNIUM.get())
                .unlockedBy("has_billet_neptunium", has.apply(ModItems.BILLET_NEPTUNIUM.get()))
                .save(pWriter, MODID + ":" + "billet_neptunium_fuel_from_u238_neptunium");

        // MOX топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_MOX_FUEL.get(), 3)
                .requires(ModItems.BILLET_URANIUM_FUEL.get(), 2)
                .requires(ModItems.BILLET_PU_MIX.get())
                .unlockedBy("has_billet_uranium_fuel", has.apply(ModItems.BILLET_URANIUM_FUEL.get()))
                .save(pWriter, MODID + ":" + "billet_mox_fuel_from_uranium_pu_mix");

        // Шрабридиевое топливо (3 биллета)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BILLET_SCHRABIDIUM_FUEL.get(), 3)
                .requires(ModItems.BILLET_SCHRABIDIUM.get())
                .requires(ModItems.BILLET_NEPTUNIUM.get())
                .requires(ModItems.BILLET_BERYLLIUM.get())
                .unlockedBy("has_billet_schrabidium", has.apply(ModItems.BILLET_SCHRABIDIUM.get()))
                .save(pWriter, MODID + ":" + "billet_schrabidium_fuel_from_billets");

        // RTG пеллеты
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG.get())
                .requires(ModItems.BILLET_PU238.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_pu238", has.apply(ModItems.BILLET_PU238.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_RADIUM.get())
                .requires(ModItems.BILLET_RA226.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_ra226", has.apply(ModItems.BILLET_RA226.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_WEAK.get())
                .requires(ModItems.BILLET_U238.get(), 2)
                .requires(ModItems.BILLET_PU238.get())
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_u238", has.apply(ModItems.BILLET_U238.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_STRONTIUM.get())
                .requires(ModItems.BILLET_SR90.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_sr90", has.apply(ModItems.BILLET_SR90.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_COBALT.get())
                .requires(ModItems.BILLET_CO60.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_co60", has.apply(ModItems.BILLET_CO60.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_ACTINIUM.get())
                .requires(ModItems.BILLET_ACTINIUM.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_actinium", has.apply(ModItems.BILLET_ACTINIUM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_POLONIUM.get())
                .requires(ModItems.BILLET_POLONIUM.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_polonium", has.apply(ModItems.BILLET_POLONIUM.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_LEAD.get())
                .requires(ModItems.BILLET_PB209.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_pb209", has.apply(ModItems.BILLET_PB209.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_GOLD.get())
                .requires(ModItems.BILLET_AU198.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_au198", has.apply(ModItems.BILLET_AU198.get()))
                .save(pWriter);

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.PELLET_RTG_AMERICIUM.get())
                .requires(ModItems.BILLET_AM241.get(), 3)
                .requires(ModItems.PLATE_IRON.get())
                .unlockedBy("has_billet_am241", has.apply(ModItems.BILLET_AM241.get()))
                .save(pWriter);

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
                    .unlockedBy("has_pellet_rtg_depleted", has.apply(ModItems.PELLET_RTG_DEPLETED.get()))
                    .save(pWriter, MODID + ":" + "pellet_rtg_depleted_to_" + material.name().toLowerCase() + "_" + meta);
        }


        // Порошки и tiny порошки
        add1To9Pair(ModItems.POWDER_STEEL.get(), ModItems.POWDER_STEEL_TINY.get());
        add1To9Pair(ModItems.POWDER_LITHIUM.get(), ModItems.POWDER_LITHIUM_TINY.get());
        add1To9Pair(ModItems.POWDER_COBALT.get(), ModItems.POWDER_COBALT_TINY.get());
        add1To9Pair(ModItems.POWDER_NEODYMIUM.get(), ModItems.POWDER_NEODYMIUM_TINY.get());
        add1To9Pair(ModItems.POWDER_NIOBIUM.get(), ModItems.POWDER_NIOBIUM_TINY.get());
        add1To9Pair(ModItems.POWDER_CERIUM.get(), ModItems.POWDER_CERIUM_TINY.get());
        add1To9Pair(ModItems.POWDER_LANTHANIUM.get(), ModItems.POWDER_LANTHANIUM_TINY.get());
        add1To9Pair(ModItems.POWDER_ACTINIUM.get(), ModItems.POWDER_ACTINIUM_TINY.get());
        add1To9Pair(ModItems.POWDER_METEORITE.get(), ModItems.POWDER_METEORITE_TINY.get());
        add1To9Pair(ModItems.POWDER_PALEOGENITE.get(), ModItems.POWDER_PALEOGENITE_TINY.get());
        add1To9Pair(ModItems.INGOT_OSMIRIDIUM.get(), ModItems.NUGGET_OSMIRIDIUM.get());

        // Ядерные отходы
        add1To9Pair(ModItems.NUCLEAR_WASTE.get(), ModItems.NUCLEAR_WASTE_TINY.get());

        // Бутылка ртути (специальный рецепт)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.BOTTLE_MERCURY.get())
                .pattern("###")
                .pattern("#B#")
                .pattern("###")
                .define('#', ModItems.INGOT_MERCURY.get())
                .define('B', Items.GLASS_BOTTLE)
                .unlockedBy("has_ingot_mercury", has.apply(ModItems.INGOT_MERCURY.get()))
                .save(pWriter);

        // Ртуть из бутылки (8 штук)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_MERCURY.get(), 8)
                .requires(ModItems.BOTTLE_MERCURY.get())
                .unlockedBy("has_bottle_mercury", has.apply(ModItems.BOTTLE_MERCURY.get()))
                .save(pWriter, MODID + ":" + "mercury_from_bottle");

        // Балафайр яйцо
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EGG_BALEFIRE.get())
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', ModItems.EGG_BALEFIRE_SHARD.get())
                .unlockedBy("has_egg_balefire_shard", has.apply(ModItems.EGG_BALEFIRE_SHARD.get()))
                .save(pWriter);

        // Осколки балафайр яйца
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.EGG_BALEFIRE_SHARD.get(), 9)
                .requires(ModItems.EGG_BALEFIRE.get())
                .unlockedBy("has_egg_balefire", has.apply(ModItems.EGG_BALEFIRE.get()))
                .save(pWriter, MODID + ":" + "egg_balefire_shard_from_egg");

        // Нитро
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.NITRA.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.NITRA_SMALL.get())
                .unlockedBy("has_nitra_small", has.apply(ModItems.NITRA_SMALL.get()))
                .save(pWriter, MODID + ":" + "nitra_from_small");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.NITRA_SMALL.get(), 4)
                .requires(ModItems.NITRA.get())
                .unlockedBy("has_nitra", has.apply(ModItems.NITRA.get()))
                .save(pWriter, MODID + ":" + "nitra_small_from_nitra");

        // Контейнер для боеприпасов
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.AMMO_CONTAINER.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.NITRA.get())
                .unlockedBy("has_nitra", has.apply(ModItems.NITRA.get()))
                .save(pWriter);

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModBlocks.GLASS_POLARIZED.get(), 4)
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.PART_GENERIC_GLASS_POLARIZED.get())
                .unlockedBy("has_part_generic_glass_polarized", has.apply(ModItems.PART_GENERIC_GLASS_POLARIZED.get()))
                .save(pWriter);

        // Осколок балафайр яйца из порошка балафайр (2x2)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, ModItems.EGG_BALEFIRE_SHARD.get())
                .pattern("##")
                .pattern("##")
                .define('#', ModItems.POWDER_BALEFIRE.get())
                .unlockedBy("has_powder_balefire", has.apply(ModItems.POWDER_BALEFIRE.get()))
                .save(pWriter, MODID + ":" + "egg_balefire_shard_from_powder_balefire");

        // Ячейка балафайр из 9 осколков
        add9To1(ModItems.EGG_BALEFIRE_SHARD.get(), ModItems.CELL_BALEFIRE.get());


        // Шрабридиевое топливо (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_SCHRABIDIUM_FUEL.get())
                .requires(ModItems.NUGGET_SCHRABIDIUM.get(), 3)
                .requires(ModItems.NUGGET_NEPTUNIUM.get(), 3)
                .requires(ModItems.NUGGET_BERYLLIUM.get(), 3)
                .unlockedBy("has_nugget_schrabidium", has.apply(ModItems.NUGGET_SCHRABIDIUM.get()))
                .save(pWriter, MODID + ":" + "ingot_schrabidium_fuel_from_nuggets");

        // HES (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_HES.get())
                .requires(ModItems.NUGGET_SCHRABIDIUM.get(), 5)
                .requires(ModItems.NUGGET_NEPTUNIUM.get(), 2)
                .requires(ModItems.NUGGET_BERYLLIUM.get(), 2)
                .unlockedBy("has_nugget_schrabidium", has.apply(ModItems.NUGGET_SCHRABIDIUM.get()))
                .save(pWriter, MODID + ":" + "ingot_hes_from_nuggets");

        // LES (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_LES.get())
                .requires(ModItems.NUGGET_SCHRABIDIUM.get())
                .requires(ModItems.NUGGET_NEPTUNIUM.get(), 4)
                .requires(ModItems.NUGGET_BERYLLIUM.get(), 4)
                .unlockedBy("has_nugget_schrabidium", has.apply(ModItems.NUGGET_SCHRABIDIUM.get()))
                .save(pWriter, MODID + ":" + "ingot_les_from_nuggets");

        // Смесь плутония (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_PU_MIX.get())
                .requires(ModItems.NUGGET_PU239.get(), 6)
                .requires(ModItems.NUGGET_PU240.get(), 3)
                .unlockedBy("has_nugget_pu239", has.apply(ModItems.NUGGET_PU239.get()))
                .save(pWriter, MODID + ":" + "ingot_pu_mix_from_nuggets");

        // Смесь плутония (из tiny-версий)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_PU_MIX.get())
                .requires(ModItems.NUGGET_PU239.get(), 6) // tiny в оригинале, используем nugget
                .requires(ModItems.NUGGET_PU240.get(), 3)
                .unlockedBy("has_nugget_pu239", has.apply(ModItems.NUGGET_PU239.get()))
                .save(pWriter, MODID + ":" + "ingot_pu_mix_from_tiny");

        // Смесь америция (из самородков)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_AM_MIX.get())
                .requires(ModItems.NUGGET_AM241.get(), 3)
                .requires(ModItems.NUGGET_AM242.get(), 6)
                .unlockedBy("has_nugget_am241", has.apply(ModItems.NUGGET_AM241.get()))
                .save(pWriter, MODID + ":" + "ingot_am_mix_from_nuggets");

        // Смесь америция (из tiny-версий)
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.INGOT_AM_MIX.get())
                .requires(ModItems.NUGGET_AM241.get(), 3) // tiny в оригинале, используем nugget
                .requires(ModItems.NUGGET_AM242.get(), 6)
                .unlockedBy("has_nugget_am241", has.apply(ModItems.NUGGET_AM241.get()))
                .save(pWriter, MODID + ":" + "ingot_am_mix_from_tiny");

        // Огнеупорная глина (4 штуки) из глины и пыли алюминия
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_FIRECLAY.get(), 4)
                .requires(Items.CLAY_BALL, 3)
                .requires(ModItems.POWDER_ALUMINIUM.get())
                .unlockedBy("has_clay_ball", has.apply(Items.CLAY_BALL))
                .save(pWriter, MODID + ":" + "ball_fireclay_from_clay_aluminium");

        // Огнеупорная глина (4 штуки) из глины и алюминиевой руды
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_FIRECLAY.get(), 4)
                .requires(Items.CLAY_BALL, 3)
                .requires(ModItems.ORE_ALUMINIUM.get()) // или руда алюминия
                .unlockedBy("has_clay_ball", has.apply(Items.CLAY_BALL))
                .save(pWriter, MODID + ":" + "ball_fireclay_from_clay_ore_aluminium");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_FIRECLAY.get(), 4)
                .requires(Items.CLAY_BALL, 3)
                .requires(ModItems.RAW_ALUMINIUM.get()) // или руда алюминия
                .unlockedBy("has_clay_ball", has.apply(Items.CLAY_BALL))
                .save(pWriter, MODID + ":" + "ball_fireclay_from_clay_raw_aluminium");

        // Огнеупорная глина (4 штуки) из глины, известняка и песка
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ModItems.BALL_FIRECLAY.get(), 4)
                .requires(Items.CLAY_BALL, 2)
                .requires(ModBlocks.STONE_RESOURCE_LIMESTONE.get()) // известняк
                .requires(Items.SAND)
                .unlockedBy("has_clay_ball", has.apply(Items.CLAY_BALL))
                .save(pWriter, MODID + ":" + "ball_fireclay_from_clay_limestone_sand");

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
            .unlockedBy("has_ore_byproduct", has.apply(ModItems.ORE_BYPRODUCT.get()))
            .save(pWriter, "ore_byproduct_to_" + byproduct.name().toLowerCase());
}
*/


    }

    /**
     * Добавляет два рецепта: один предмет -> 9 штук (разжатие) и 9 штук -> один предмет (сжатие).
     */
    public static void add1To9Pair(Item one, Item nine) {
        add1To9(one, nine);
        add9To1(nine, one);
    }

    public static void add1To9Pair(Block one, Item nine) {
        add1To9(one.asItem(), nine);
        add9To1(nine, one.asItem());
    }

    /**
     * Полный набор: самородок -> слиток -> блок.
     */
    public static void addMineralSet(Item nugget, Item ingot, Block block) {
        // 1 слиток = 9 самородков
        add1To9(ingot, nugget);
        // 9 самородков = 1 слиток
        add9To1(nugget, ingot);
        // 1 блок = 9 слитков
        add1To9Pair(block, ingot);
    }

    // ----- Разжатие (1 -> 9) -----
    public static void add1To9(Item one, Item nine) {
        String oneName = one.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String nineName = nine.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");

        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nine, 9)
                .requires(one)
                .unlockedBy("has_" + oneName, has.apply(one))
                .save(writer, MODID + ":" + oneName + "_to_" + nineName);
    }

    // ----- Сжатие (9 -> 1) -----
    public static void add9To1(Item nine, Item one) {
        String nineName = nine.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String oneName = one.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");

        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, one)
                .pattern("###")
                .pattern("###")
                .pattern("###")
                .define('#', nine)
                .unlockedBy("has_" + nineName, has.apply(nine))
                .save(writer, MODID + ":" + nineName + "_to_" + oneName);
    }

    // ----- Биллеты (специальные формы) -----
    public static void addBillet(Item billet, Item nugget) {
        String billetName = billet.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String nuggetName = nugget.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");

        // billet = 6 nugget (2 ряда по 3)
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, billet)
                .pattern("###")
                .pattern("###")
                .define('#', nugget)
                .unlockedBy("has_" + nuggetName, has.apply(nugget))
                .save(writer, MODID + ":" + billetName + "_from_nuggets");

        // 6 nugget из биллета
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, nugget, 6)
                .requires(billet)
                .unlockedBy("has_" + billetName, has.apply(billet))
                .save(writer, MODID + ":" + nuggetName + "_from_billet");
    }

    public static void addBillet(Item billet, Item ingot, Item nugget) {
        addBillet(billet, nugget);

        String billetName = billet.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");
        String ingotName = ingot.getDescriptionId().replace("item.hbm.", "").replace("block.hbm.", "");

        // 2 слитка из 3 биллетов
        ShapelessRecipeBuilder.shapeless(RecipeCategory.MISC, ingot, 2)
                .requires(billet, 3)
                .unlockedBy("has_" + billetName, has.apply(billet))
                .save(writer, MODID + ":" + ingotName + "_from_billets");

        // 3 биллета из 1 слитка
        ShapedRecipeBuilder.shaped(RecipeCategory.MISC, billet, 3)
                .pattern("##")
                .define('#', ingot)
                .unlockedBy("has_" + ingotName, has.apply(ingot))
                .save(writer, MODID + ":" + billetName + "_from_ingot");
    }
}