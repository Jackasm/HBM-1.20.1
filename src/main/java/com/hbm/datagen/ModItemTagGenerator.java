package com.hbm.datagen;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.items.ModItems;
import com.hbm.items.ModItemTags;
import com.hbm.util.RefStrings;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class ModItemTagGenerator extends ItemTagsProvider {

    public ModItemTagGenerator(PackOutput output,
                               CompletableFuture<HolderLookup.Provider> lookupProvider,
                               CompletableFuture<TagLookup<Block>> blockTags,
                               @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {

        // === АВТОМАТИЧЕСКАЯ РЕГИСТРАЦИЯ КОНТЕЙНЕРОВ ===
        registerFluidContainers();

        // Автоматическая генерация тегов на основе autogen
        for (NTMMaterial material : Mats.orderedList) {
            for (MaterialShapes shape : material.autogen) {
                if (shape == null || shape.noAutogen) continue;

                // Формируем имя предмета: shape_material
                String shapeName = shape.name().toLowerCase();
                String itemName = shapeName + "_" + material.names[0];

                // Проверяем, существует ли такой предмет
                ResourceLocation itemId = ResLocation(RefStrings.MODID, itemName);
                if (ForgeRegistries.ITEMS.containsKey(itemId)) {
                    Item item = ForgeRegistries.ITEMS.getValue(itemId);
                    if (item != null) {
                        String tagPath = shapeName + "s/" + material.names[0];
                        TagKey<Item> tag = ItemTags.create(ResLocation("forge", tagPath));
                        tag(tag).add(item);
                    }
                }
            }
        }

        // === ВАНИЛЬНЫЕ ТЕГИ ===

        // Топливо (уголь)
        tag(ItemTags.COALS)
                .add(ModItems.LIGNITE.get())
                .add(Items.COAL);

        // === ТЕГИ РУД ДЛЯ КРИСТАЛЛИЗАТОРА ===
// Основные руды
        tag(ModItemTags.ORE_IRON)
                .add(Items.IRON_ORE)
                .add(Items.DEEPSLATE_IRON_ORE)
                .add(Items.RAW_IRON);

        tag(ModItemTags.ORE_GOLD)
                .add(Items.GOLD_ORE)
                .add(Items.DEEPSLATE_GOLD_ORE)
                .add(Items.RAW_GOLD);

        tag(ModItemTags.ORE_COPPER)
                .add(Items.COPPER_ORE)
                .add(Items.DEEPSLATE_COPPER_ORE)
                .add(Items.RAW_COPPER);

        tag(ModItemTags.ORE_COAL)
                .add(Items.COAL_ORE)
                .add(Items.DEEPSLATE_COAL_ORE);

        tag(ModItemTags.ORE_REDSTONE)
                .add(Items.REDSTONE_ORE)
                .add(Items.DEEPSLATE_REDSTONE_ORE);

        tag(ModItemTags.ORE_LAPIS)
                .add(Items.LAPIS_ORE)
                .add(Items.DEEPSLATE_LAPIS_ORE);

        tag(ModItemTags.ORE_DIAMOND)
                .add(Items.DIAMOND_ORE)
                .add(Items.DEEPSLATE_DIAMOND_ORE);

        tag(ModItemTags.ORE_EMERALD)
                .add(Items.EMERALD_ORE)
                .add(Items.DEEPSLATE_EMERALD_ORE);

        tag(ModItemTags.ORE_NETHER_QUARTZ)
                .add(Items.NETHER_QUARTZ_ORE);

        tag(ModItemTags.ORE_URANIUM)
                .add(ModItems.RAW_URANIUM.get())
                .add(ModBlocks.ORE_URANIUM.get().asItem())
                .add(ModBlocks.ORE_URANIUM_DEEPSLATE.get().asItem())
                .add(ModBlocks.ORE_URANIUM_SCORCHED.get().asItem())
                .add(ModBlocks.ORE_GNEISS_URANIUM.get().asItem())
                .add(ModBlocks.ORE_GNEISS_URANIUM_SCORCHED.get().asItem())
                .add(ModBlocks.ORE_NETHER_URANIUM.get().asItem())
                .add(ModBlocks.ORE_NETHER_URANIUM_SCORCHED.get().asItem())
                .add(ModBlocks.ORE_SELLAFIELD_URANIUM_SCORCHED.get().asItem());

        tag(ModItemTags.ORE_THORIUM)
                .add(ModItems.RAW_THORIUM.get())
                .add(ModBlocks.ORE_THORIUM.get().asItem())
                .add(ModBlocks.ORE_THORIUM_DEEPSLATE.get().asItem());

        tag(ModItemTags.ORE_PLUTONIUM)
                .add(ModItems.RAW_PLUTONIUM.get())
                .add(ModBlocks.ORE_NETHER_PLUTONIUM.get().asItem());

        tag(ModItemTags.ORE_LEAD)
                .add(ModItems.RAW_LEAD.get())
                .add(ModBlocks.ORE_LEAD.get().asItem())
                .add(ModBlocks.ORE_LEAD_DEEPSLATE.get().asItem());

        tag(ModItemTags.ORE_TITANIUM)
                .add(ModItems.RAW_TITANIUM.get())
                .add(ModBlocks.ORE_TITANIUM.get().asItem())
                .add(ModBlocks.ORE_TITANIUM_DEEPSLATE.get().asItem());

        tag(ModItemTags.ORE_TUNGSTEN)
                .add(ModItems.RAW_TUNGSTEN.get())
                .add(ModBlocks.ORE_TUNGSTEN.get().asItem())
                .add(ModBlocks.ORE_TUNGSTEN_DEEPSLATE.get().asItem());

        tag(ModItemTags.ORE_ALUMINIUM)
                .add(ModItems.RAW_ALUMINIUM.get())
                .add(ModBlocks.ORE_ALUMINIUM.get().asItem())
                .add(ModBlocks.ORE_ALUMINIUM_DEEPSLATE.get().asItem())
                .add(ModBlocks.ORE_METEOR_ALUMINIUM.get().asItem());

        tag(ModItemTags.ORE_ZINC)
                .add(ModItems.RAW_ZINC.get())
                .add(ModBlocks.ORE_ZINC.get().asItem())
                .add(ModBlocks.ORE_ZINC_DEEPSLATE.get().asItem());

        tag(ModItemTags.ORE_COBALT)
                .add(ModItems.RAW_COBALT.get())
                .add(ModBlocks.ORE_COBALT.get().asItem())
                .add(ModBlocks.ORE_COBALT_DEEPSLATE.get().asItem())
                .add(ModBlocks.ORE_METEOR_COBALT.get().asItem())
                .add(ModBlocks.ORE_NETHER_COBALT.get().asItem());

        tag(ModItemTags.ORE_BERYLLIUM)
                .add(ModItems.RAW_BERYLLIUM.get())
                .add(ModBlocks.ORE_BERYLLIUM.get().asItem())
                .add(ModBlocks.ORE_BERYLLIUM_DEEPSLATE.get().asItem());

        tag(ModItemTags.ORE_NITER)
                .add(ModBlocks.ORE_NITER.get().asItem())
                .add(ModBlocks.ORE_NITER_DEEPSLATE.get().asItem());

        tag(ModItemTags.ORE_SULFUR)
                .add(ModBlocks.ORE_SULFUR.get().asItem())
                .add(ModBlocks.ORE_SULFUR_DEEPSLATE.get().asItem())
                .add(ModBlocks.ORE_NETHER_SULFUR.get().asItem());

        tag(ModItemTags.ORE_FLUORITE)
                .add(ModBlocks.ORE_FLUORITE.get().asItem())
                .add(ModBlocks.ORE_FLUORITE_DEEPSLATE.get().asItem());

        tag(ModItemTags.ORE_CINNABAR)
                .add(ModBlocks.ORE_CINNABAR.get().asItem())
                .add(ModBlocks.ORE_CINNABAR_DEEPSLATE.get().asItem());

        tag(ModItemTags.ORE_RARE_EARTH)
                .add(ModBlocks.ORE_RARE.get().asItem())
                .add(ModBlocks.ORE_RARE_DEEPSLATE.get().asItem())
                .add(ModBlocks.ORE_METEOR_RARE_EARTH.get().asItem())
                .add(ModBlocks.ORE_GNEISS_RARE.get().asItem());

        tag(ModItemTags.ORE_SCHRABIDIUM)
                .add(ModItems.ORE_SCHRABIDIUM.get())
                .add(ModBlocks.ORE_NETHER_SCHRABIDIUM.get().asItem())
                .add(ModBlocks.ORE_GNEISS_SCHRABIDIUM.get().asItem())
                .add(ModBlocks.ORE_SELLAFIELD_SCHRABIDIUM.get().asItem());


        // Слитки
        tag(ItemTags.IRON_ORES);
        // Добавь железные руды если есть

        // === КАСТОМНЫЕ ТЕГИ HBM ===
        tag(ModItemTags.TOOL_CHEMISTRYSET)
                .add(ModItems.CHEMISTRY_SET.get())
                .add(ModItems.CHEMISTRY_SET_BORON.get());
        // Радиоактивные предметы
        tag(ModItemTags.RADIOACTIVE_ITEMS)
                .add(ModItems.INGOT_URANIUM.get())
                .add(ModItems.INGOT_PLUTONIUM.get())
                .add(ModItems.INGOT_THORIUM.get())
                .add(ModItems.INGOT_COBALT.get())
                .add(ModItems.INGOT_BERYLLIUM.get());
        // Добавь другие радиоактивные предметы

        // Слитки HBM
        tag(ModItemTags.INGOTS)
                .add(ModItems.INGOT_ALUMINIUM.get())
                .add(ModItems.INGOT_TITANIUM.get())
                .add(ModItems.INGOT_TUNGSTEN.get())
                .add(ModItems.INGOT_LEAD.get())
                .add(ModItems.INGOT_ZINC.get())
                .add(ModItems.INGOT_URANIUM.get())
                .add(ModItems.INGOT_PLUTONIUM.get())
                .add(ModItems.INGOT_THORIUM.get())
                .add(ModItems.INGOT_COBALT.get())
                .add(ModItems.INGOT_BERYLLIUM.get());

        // Руды (предметы)
        tag(ModItemTags.ORES)
                .add(ModItems.RAW_ALUMINIUM.get())
                .add(ModItems.RAW_TITANIUM.get())
                .add(ModItems.RAW_TUNGSTEN.get())
                .add(ModItems.RAW_LEAD.get())
                .add(ModItems.RAW_BERYLLIUM.get())
                .add(ModItems.RAW_COBALT.get())
                .add(ModItems.RAW_ZINC.get())
                .add(ModItems.RAW_URANIUM.get())
                .add(ModItems.RAW_PLUTONIUM.get())
                .add(ModItems.RAW_THORIUM.get());

        // Горючие предметы
        tag(ModItemTags.FUELS)
                .add(ModItems.LIGNITE.get());
        // Добавь другие горючие предметы

        // Ядерное топливо
        tag(ModItemTags.NUCLEAR_FUELS)
                .add(ModItems.INGOT_URANIUM.get())
                .add(ModItems.INGOT_PLUTONIUM.get())
                .add(ModItems.INGOT_THORIUM.get());

        // === FORGE ТЕГИ ДЛЯ ПРЕССА ===

        // Пыли
        tag(ModItemTags.DUSTS_IRON).add(ModItems.POWDER_IRON.get());
        tag(ModItemTags.DUSTS_GOLD).add(ModItems.POWDER_GOLD.get());
        tag(ModItemTags.DUSTS_COPPER).add(ModItems.POWDER_COPPER.get());
        tag(ModItemTags.DUSTS_COAL).add(ModItems.POWDER_COAL.get());
        tag(ModItemTags.DUSTS_REDSTONE).add(Items.REDSTONE);

        // Новые теги пылей
        tag(ModItemTags.DUSTS_NETHER_QUARTZ).add(ModItems.POWDER_QUARTZ.get());
        tag(ModItemTags.DUSTS_LAPIS).add(ModItems.POWDER_LAPIS.get());
        tag(ModItemTags.DUSTS_DIAMOND).add(ModItems.POWDER_DIAMOND.get());
        tag(ModItemTags.DUSTS_EMERALD).add(ModItems.POWDER_EMERALD.get());
        tag(ModItemTags.DUSTS_LIGNITE).add(ModItems.POWDER_LIGNITE.get());

        // Слитки
        tag(ModItemTags.INGOTS_IRON).add(Items.IRON_INGOT);
        tag(ModItemTags.INGOTS_GOLD).add(Items.GOLD_INGOT);
        tag(ModItemTags.INGOTS_COPPER).add(Items.COPPER_INGOT);
        tag(ModItemTags.INGOTS_STEEL).add(ModItems.INGOT_STEEL.get());
        tag(ModItemTags.INGOTS_LEAD).add(ModItems.INGOT_LEAD.get());
        tag(ModItemTags.INGOTS_ALUMINUM).add(ModItems.INGOT_ALUMINIUM.get());
        tag(ModItemTags.INGOTS_TITANIUM).add(ModItems.INGOT_TITANIUM.get());
        tag(ModItemTags.INGOTS_URANIUM).add(ModItems.INGOT_URANIUM.get());
        tag(ModItemTags.INGOTS_PLUTONIUM).add(ModItems.INGOT_PLUTONIUM.get());

        // Самородки
        tag(ModItemTags.NUGGETS_IRON).add(Items.IRON_NUGGET);
        tag(ModItemTags.NUGGETS_GOLD).add(Items.GOLD_NUGGET);

        // Пластины
        tag(ModItemTags.PLATES_IRON).add(ModItems.PLATE_IRON.get());
        tag(ModItemTags.PLATES_STEEL).add(ModItems.PLATE_STEEL.get());
        tag(ModItemTags.PLATES_COPPER).add(ModItems.PLATE_COPPER.get());
        tag(ModItemTags.PLATES_GOLD).add(ModItems.PLATE_GOLD.get());

        // Руды
        tag(ModItemTags.ORES_IRON)
                .add(Items.IRON_ORE)
                .add(Items.DEEPSLATE_IRON_ORE);


        tag(ModItemTags.ORES_GOLD).add(Items.GOLD_ORE);
        tag(ModItemTags.ORES_COPPER).add(Items.COPPER_ORE);
        tag(ModItemTags.ORES_URANIUM).add(ModItems.RAW_URANIUM.get());

        // Блоки хранения
        tag(ModItemTags.STORAGE_BLOCKS_IRON).add(Items.IRON_BLOCK);
        tag(ModItemTags.STORAGE_BLOCKS_GOLD).add(Items.GOLD_BLOCK);
        tag(ModItemTags.STORAGE_BLOCKS_COPPER).add(Items.COPPER_BLOCK);

        // Драгоценности
        tag(ModItemTags.GEMS_DIAMOND).add(Items.DIAMOND);
        tag(ModItemTags.GEMS_EMERALD).add(Items.EMERALD);
        tag(ModItemTags.GEMS_QUARTZ).add(Items.QUARTZ);

        // Группы для совместимости
        tag(ModItemTags.ANY_STEEL)
                .addTag(ModItemTags.INGOTS_STEEL)
                .addTag(ModItemTags.PLATES_STEEL);

        tag(ModItemTags.ANY_IRON)
                .addTag(ModItemTags.INGOTS_IRON)
                .addTag(ModItemTags.NUGGETS_IRON)
                .addTag(ModItemTags.PLATES_IRON)
                .addTag(ModItemTags.STORAGE_BLOCKS_IRON);

        tag(ModItemTags.ANY_COPPER)
                .addTag(ModItemTags.INGOTS_COPPER)
                .addTag(ModItemTags.PLATES_COPPER)
                .addTag(ModItemTags.STORAGE_BLOCKS_COPPER);

        tag(ModItemTags.ANY_COKE)
                .add(ModItems.COKE.get());

        tag(ModItemTags.ANY_RUBBER_INGOT)
                .add(ModItems.INGOT_BIORUBBER.get())
                .add(ModItems.INGOT_RUBBER.get());

        tag(ModItemTags.ANY_TAR)
                .add(ModItems.OIL_TAR_CRUDE.get())
                .add(ModItems.OIL_TAR_CRACK.get())
                .add(ModItems.OIL_TAR_COAL.get())
                .add(ModItems.OIL_TAR_WOOD.get())
                .add(ModItems.OIL_TAR_WAX.get())
                .add(ModItems.OIL_TAR_PARAFFIN.get());

        tag(ModItemTags.ANY_PLASTIC_INGOT)
                .add(ModItems.INGOT_POLYMER.get())
                .add(ModItems.INGOT_BAKELITE.get());

        tag(ModItemTags.ANY_HARDPLASTIC_INGOT)
                .add(ModItems.INGOT_PC.get())
                .add(ModItems.INGOT_PVC.get());

        tag(ModItemTags.ANY_HIGHEXPLOSIVE)
                .add(ModItems.BALL_TNT.get())
                .add(ModItems.BALL_TATB.get());

        tag(ModItemTags.ANY_SMOKELESS_DUST)
                .add(ModItems.BALLISTITE.get())
                .add(ModItems.CORDITE.get());

        tag(ModItemTags.ANY_BISMOID_BRONZE_PLATE_CAST)
                .add(ModItems.PLATE_CAST_BISMUTH_BRONZE.get())
                .add(ModItems.PLATE_CAST_ARSENIC_BRONZE.get());

        tag(ModItemTags.ANY_RESISTANTALLOY_PLATE_CAST)
                .add(ModItems.PLATE_CAST_TECHNETIUM.get())
                .add(ModItems.PLATE_CAST_CADMIUM_STEEL.get());

        tag(ModItemTags.ANY_BISMOID_NUGGET)
                .add(ModItems.NUGGET_BISMUTH.get())
                .add(ModItems.NUGGET_ARSENIC.get());

        tag(ModItemTags.ANY_RESISTANTALLOY_HEAVY_RECEIVER)
                .add(ModItems.HEAVY_RECEIVER_TECHNETIUM.get())
                .add(ModItems.HEAVY_RECEIVER_CADMIUM_STEEL.get());

        tag(ModItemTags.ANY_BISMOID_BRONZE_HEAVY_RECEIVER)
                .add(ModItems.HEAVY_RECEIVER_BISMUTH_BRONZE.get())
                .add(ModItems.HEAVY_RECEIVER_ARSENIC_BRONZE.get());

        tag(ModItemTags.ANY_RESISTANTALLOY_LIGHT_BARREL)
                .add(ModItems.LIGHT_BARREL_TECHNETIUM.get())
                .add(ModItems.LIGHT_BARREL_CADMIUM_STEEL.get());

        tag(ModItemTags.ANY_RESISTANTALLOY_HEAVY_BARREL)
                .add(ModItems.HEAVY_BARREL_TECHNETIUM.get())
                .add(ModItems.HEAVY_BARREL_CADMIUM_STEEL.get());

        tag(ModItemTags.ANY_BISMOID_BRONZE_LIGHT_BARREL)
                .add(ModItems.LIGHT_BARREL_ARSENIC_BRONZE.get())
                .add(ModItems.LIGHT_BARREL_BISMUTH_BRONZE.get());

        tag(ModItemTags.ANY_BISMOID_BRONZE_LIGHT_RECEIVER)
                .add(ModItems.LIGHT_RECEIVER_ARSENIC_BRONZE.get())
                .add(ModItems.LIGHT_RECEIVER_BISMUTH_BRONZE.get());

        tag(ModItemTags.ANY_RESISTANTALLOY_LIGHT_RECEIVER)
                .add(ModItems.LIGHT_RECEIVER_TECHNETIUM.get())
                .add(ModItems.LIGHT_RECEIVER_CADMIUM_STEEL.get());

        tag(ModItemTags.ANY_PLASTIC_GRIP)
                .add(ModItems.PART_GRIP_BAKELITE.get())
                .add(ModItems.PART_GRIP_POLYMER.get());

        tag(ModItemTags.ANY_HARDPLASTIC_GRIP)
                .add(ModItems.PART_GRIP_PVC.get())
                .add(ModItems.PART_GRIP_POLYCARBONATE.get());

        tag(ModItemTags.ANY_HARDPLASTIC_STOCK)
                .add(ModItems.PART_STOCK_PVC.get())
                .add(ModItems.PART_STOCK_POLYCARBONATE.get());

        tag(ModItemTags.ANY_PLASTIC_STOCK)
                .add(ModItems.PART_STOCK_BAKELITE.get())
                .add(ModItems.PART_STOCK_POLYMER.get());

        tag(ModItemTags.ANY_PLASTICEXPLOSIVE_INGOT)
                .add(ModItems.INGOT_SEMTEX.get())
                .add(ModItems.INGOT_C4.get());

        tag(ModItemTags.ANY_ELECTRODE)
                .add(ModItems.ARC_ELECTRODE_GRAPHITE.get())
                .add(ModItems.ARC_ELECTRODE_LANTHANIUM.get())
                .add(ModItems.ARC_ELECTRODE_DESH.get())
                .add(ModItems.ARC_ELECTRODE_SATURNITE.get());

        tag(ModItemTags.ANY_QUARTZ_BLOCK)
                .add(Items.CHISELED_QUARTZ_BLOCK)
                .add(Items.QUARTZ_PILLAR)
                .add(Items.QUARTZ_STAIRS)
                .add(Items.QUARTZ_SLAB);

    }

    private void registerFluidContainers() {
        // Только ОБЩИЕ теги для типов контейнеров
        tag(ModItemTags.BUCKET_CONTAINERS)
                .add(Items.WATER_BUCKET)
                .add(Items.LAVA_BUCKET);

        // Базовые пустые контейнеры
        tag(ModItemTags.TANK_CONTAINERS)
                .add(ModItems.FLUID_TANK.get());

        tag(ModItemTags.BARREL_CONTAINERS)
                .add(ModItems.FLUID_BARREL.get());

        // ПУСТЫЕ контейнеры
        tag(ModItemTags.EMPTY_BUCKETS)
                .add(Items.BUCKET);

        tag(ModItemTags.EMPTY_TANKS)
                .add(ModItems.FLUID_TANK.get());

        tag(ModItemTags.EMPTY_BARRELS)
                .add(ModItems.FLUID_BARREL.get());

        // Универсальный тег для всех пустых контейнеров
        tag(ModItemTags.EMPTY_FLUID_CONTAINERS)
                .addTag(ModItemTags.EMPTY_BUCKETS)
                .addTag(ModItemTags.EMPTY_TANKS)
                .addTag(ModItemTags.EMPTY_BARRELS);


        this.tag(ModItemTags.WIRE_FINE)
                .add(ModItems.WIRE_COPPER.get())
                .add(ModItems.WIRE_CARBON.get())
                .add(ModItems.WIRE_ALUMINIUM.get())
                .add(ModItems.WIRE_RED_COPPER.get())
                .add(ModItems.WIRE_GOLD.get())
                .add(ModItems.WIRE_LEAD.get())
                .add(ModItems.WIRE_ZIRCONIUM.get())
                .add(ModItems.WIRE_TUNGSTEN.get())
                .add(ModItems.WIRE_ADVANCED_ALLOY.get())
                .add(ModItems.WIRE_STEEL.get())
                .add(ModItems.WIRE_SCHRABIDIUM.get())
                .add(ModItems.WIRE_MAGNETIZED_TUNGSTEN.get());

        this.tag(ModItemTags.GLASS)
                // Ванильные стёкла
                .add(Blocks.GLASS.asItem())
                .add(Blocks.GLASS_PANE.asItem())
                .add(Blocks.WHITE_STAINED_GLASS.asItem())
                .add(Blocks.ORANGE_STAINED_GLASS.asItem())
                .add(Blocks.MAGENTA_STAINED_GLASS.asItem())
                .add(Blocks.LIGHT_BLUE_STAINED_GLASS.asItem())
                .add(Blocks.YELLOW_STAINED_GLASS.asItem())
                .add(Blocks.LIME_STAINED_GLASS.asItem())
                .add(Blocks.PINK_STAINED_GLASS.asItem())
                .add(Blocks.GRAY_STAINED_GLASS.asItem())
                .add(Blocks.LIGHT_GRAY_STAINED_GLASS.asItem())
                .add(Blocks.CYAN_STAINED_GLASS.asItem())
                .add(Blocks.PURPLE_STAINED_GLASS.asItem())
                .add(Blocks.BLUE_STAINED_GLASS.asItem())
                .add(Blocks.BROWN_STAINED_GLASS.asItem())
                .add(Blocks.GREEN_STAINED_GLASS.asItem())
                .add(Blocks.RED_STAINED_GLASS.asItem())
                .add(Blocks.BLACK_STAINED_GLASS.asItem());
                // Ваши модные стёкла
                /*
                .add(ModBlocks.GLASS_BORON.get().asItem())
                .add(ModBlocks.GLASS_TEMPERED.get().asItem())
                .add(ModBlocks.GLASS_LEAD.get().asItem())
                .add(ModBlocks.GLASS_REINFORCED.get().asItem())
                .add(ModBlocks.GLASS_OBSIDIAN.get().asItem()); // если есть
                */

    }

    @Override
    public @NotNull String getName() {
        return "HBM Item Tags";
    }
}