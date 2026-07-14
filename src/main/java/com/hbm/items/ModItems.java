package com.hbm.items;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.*;
import com.hbm.datagen.models.ItemModelType;
import com.hbm.entity.ModEntities;
import com.hbm.items.armor.*;
import com.hbm.items.block.*;
import com.hbm.items.bomb.ItemN2;
import com.hbm.items.deco.*;
import com.hbm.items.fluid.*;
import com.hbm.items.fluid.ItemDisperser;
import com.hbm.items.food.*;
import com.hbm.items.machine.*;
import com.hbm.items.special.*;
import com.hbm.items.storage.*;
import com.hbm.items.tool.*;
import com.hbm.items.weapon.*;
import com.hbm.util.HBMEnums;
import com.hbm.util.RefStrings;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

import static com.hbm.util.HBMEnums.CreativeTabRegistry.*;

@SuppressWarnings("all")
public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, RefStrings.MODID);

    public static final Map<RegistryObject<Item>, ItemModelType> ITEM_MODELS = new LinkedHashMap<>();
    public static final Map<RegistryObject<Item>, Object[]> ITEM_MODEL_DATA = new LinkedHashMap<>();

    public static RegistryObject<Item> register(String name, Supplier<Item> supplier) {
        return ITEMS.register(name, supplier);
    }

    public static RegistryObject<Item> register(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier) {
        return ItemRegistryHelper.register(tab, name, supplier);
    }

    public static RegistryObject<Item> register(HBMEnums.CreativeTabRegistry tab, String name, Supplier<Item> supplier, ItemModelType modelType, Object... modelData) {
        RegistryObject<Item> item = ItemRegistryHelper.register(tab, name, supplier);
        ITEM_MODELS.put(item, modelType);
        if (modelData.length > 0) {
            ITEM_MODEL_DATA.put(item, modelData);
        }
        return item;
    }

    public static RegistryObject<Item> register(String name, Supplier<Item> supplier, ItemModelType modelType, Object... modelData) {
        RegistryObject<Item> item = ITEMS.register(name, supplier);
        ITEM_MODELS.put(item, modelType);
        if (modelData.length > 0) {
            ITEM_MODEL_DATA.put(item, modelData);
        }
        return item;
    }

    // ================================================== РУДЫ И БЛОКИ (модель уже есть)================================================

    public static final RegistryObject<Item> ORE_TITANIUM = register(BLOCK_TAB, "ore_titanium",
            () -> new BlockItem(ModBlocks.ORE_TITANIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_TITANIUM_DEEPSLATE = register(BLOCK_TAB, "ore_titanium_deepslate",
            () -> new BlockItem(ModBlocks.ORE_TITANIUM_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_SULFUR = register(BLOCK_TAB, "ore_sulfur",
            () -> new BlockItem(ModBlocks.ORE_SULFUR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_SULFUR_DEEPSLATE = register(BLOCK_TAB, "ore_sulfur_deepslate",
            () -> new BlockItem(ModBlocks.ORE_SULFUR_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_ALUMINIUM = register(BLOCK_TAB, "ore_aluminium",
            () -> new BlockItem(ModBlocks.ORE_ALUMINIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_ALUMINIUM_DEEPSLATE = register(BLOCK_TAB, "ore_aluminium_deepslate",
            () -> new BlockItem(ModBlocks.ORE_ALUMINIUM_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_ZINC = register(BLOCK_TAB, "ore_zinc",
            () -> new BlockItem(ModBlocks.ORE_ZINC.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_ZINC_DEEPSLATE = register(BLOCK_TAB, "ore_zinc_deepslate",
            () -> new BlockItem(ModBlocks.ORE_ZINC_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_FLUORITE = register(BLOCK_TAB, "ore_fluorite",
            () -> new BlockItem(ModBlocks.ORE_FLUORITE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_FLUORITE_DEEPSLATE = register(BLOCK_TAB, "ore_fluorite_deepslate",
            () -> new BlockItem(ModBlocks.ORE_FLUORITE_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_NITER = register(BLOCK_TAB, "ore_niter",
            () -> new BlockItem(ModBlocks.ORE_NITER.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_NITER_DEEPSLATE = register(BLOCK_TAB, "ore_niter_deepslate",
            () -> new BlockItem(ModBlocks.ORE_NITER_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_TUNGSTEN = register(BLOCK_TAB, "ore_tungsten",
            () -> new BlockItem(ModBlocks.ORE_TUNGSTEN.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_TUNGSTEN_DEEPSLATE = register(BLOCK_TAB, "ore_tungsten_deepslate",
            () -> new BlockItem(ModBlocks.ORE_TUNGSTEN_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_LEAD = register(BLOCK_TAB, "ore_lead",
            () -> new BlockItem(ModBlocks.ORE_LEAD.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_LEAD_DEEPSLATE = register(BLOCK_TAB, "ore_lead_deepslate",
            () -> new BlockItem(ModBlocks.ORE_LEAD_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_BERYLLIUM = register(BLOCK_TAB, "ore_beryllium",
            () -> new BlockItem(ModBlocks.ORE_BERYLLIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_BERYLLIUM_DEEPSLATE = register(BLOCK_TAB, "ore_beryllium_deepslate",
            () -> new BlockItem(ModBlocks.ORE_BERYLLIUM_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_LIGNITE = register(BLOCK_TAB, "ore_lignite",
            () -> new BlockItem(ModBlocks.ORE_LIGNITE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_LIGNITE_DEEPSLATE = register(BLOCK_TAB, "ore_lignite_deepslate",
            () -> new BlockItem(ModBlocks.ORE_LIGNITE_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_ASBESTOS = register(BLOCK_TAB, "ore_asbestos",
            () -> new BlockItem(ModBlocks.ORE_ASBESTOS.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_ASBESTOS_DEEPSLATE = register(BLOCK_TAB, "ore_asbestos_deepslate",
            () -> new BlockItem(ModBlocks.ORE_ASBESTOS_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_GNEISS_ASBESTOS = register(BLOCK_TAB, "ore_gneiss_asbestos",
            () -> new BlockItem(ModBlocks.ORE_GNEISS_ASBESTOS.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_RARE = register(BLOCK_TAB, "ore_rare",
            () -> new BlockItem(ModBlocks.ORE_RARE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_RARE_DEEPSLATE = register(BLOCK_TAB, "ore_rare_deepslate",
            () -> new BlockItem(ModBlocks.ORE_RARE_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_CINNABAR = register(BLOCK_TAB, "ore_cinnabar",
            () -> new BlockItem(ModBlocks.ORE_CINNABAR.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_CINNABAR_DEEPSLATE = register(BLOCK_TAB, "ore_cinnabar_deepslate",
            () -> new BlockItem(ModBlocks.ORE_CINNABAR_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_COBALT = register(BLOCK_TAB, "ore_cobalt",
            () -> new BlockItem(ModBlocks.ORE_COBALT.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_COBALT_DEEPSLATE = register(BLOCK_TAB, "ore_cobalt_deepslate",
            () -> new BlockItem(ModBlocks.ORE_COBALT_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_URANIUM = register(BLOCK_TAB, "ore_uranium",
            () -> new BlockItem(ModBlocks.ORE_URANIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_URANIUM_DEEPSLATE = register(BLOCK_TAB, "ore_uranium_deepslate",
            () -> new BlockItem(ModBlocks.ORE_URANIUM_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_PLUTONIUM = register(BLOCK_TAB, "ore_plutonium",
            () -> new BlockItem(ModBlocks.ORE_NETHER_PLUTONIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_NETHER_SMOLDERING = register(BLOCK_TAB, "ore_nether_smoldering",
            () -> new BlockItem(ModBlocks.ORE_NETHER_SMOLDERING.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_NETHER_TUNGSTEN = register(BLOCK_TAB, "ore_nether_tungsten",
            () -> new BlockItem(ModBlocks.ORE_NETHER_TUNGSTEN.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_THORIUM = register(BLOCK_TAB, "ore_thorium",
            () -> new BlockItem(ModBlocks.ORE_THORIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_THORIUM_DEEPSLATE = register(BLOCK_TAB, "ore_thorium_deepslate",
            () -> new BlockItem(ModBlocks.ORE_THORIUM_DEEPSLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> STONE_RESOURCE_LIMESTONE = register(BLOCK_TAB, "stone_resource_limestone",
            () -> new BlockItem(ModBlocks.STONE_RESOURCE_LIMESTONE.get(), new Item.Properties()));
    public static final RegistryObject<Item> STONE_GNEISS = register(BLOCK_TAB, "stone_gneiss",
            () -> new BlockItem(ModBlocks.STONE_GNEISS.get(), new Item.Properties()));
    public static final RegistryObject<Item> STONE_RESOURCE = register("stone_resource",
            () -> new ItemBlockResourceStone(ModBlocks.STONE_RESOURCE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_TIKITE = register(BLOCK_TAB, "ore_tikite",
            () -> new BlockItem(ModBlocks.ORE_TIKITE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_AUSTRALIUM = register(BLOCK_TAB, "ore_australium",
            () -> new BlockItem(ModBlocks.ORE_AUSTRALIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_OIL = register(BLOCK_TAB, "ore_oil",
            () -> new BlockItem(ModBlocks.ORE_OIL.get(), new Item.Properties()));
    public static final RegistryObject<Item> ORE_GNEISS_GAS = register(BLOCK_TAB, "ore_gneiss_gas",
            () -> new BlockItem(ModBlocks.ORE_GNEISS_GAS.get(), new Item.Properties()));
    public static final RegistryObject<Item> CLUSTER_IRON = register(BLOCK_TAB, "cluster_iron",
            () -> new BlockItem(ModBlocks.CLUSTER_IRON.get(), new Item.Properties()));
    public static final RegistryObject<Item> CLUSTER_TITANIUM = register(BLOCK_TAB, "cluster_titanium",
            () -> new BlockItem(ModBlocks.CLUSTER_TITANIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> CLUSTER_ALUMINIUM = register(BLOCK_TAB, "cluster_aluminium",
            () -> new BlockItem(ModBlocks.CLUSTER_ALUMINIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> CLUSTER_COPPER = register(BLOCK_TAB, "cluster_copper",
            () -> new BlockItem(ModBlocks.CLUSTER_COPPER.get(), new Item.Properties()));

    public static final RegistryObject<Item> WASTE_EARTH = register(BLOCK_TAB, "waste_earth",
            () -> new BlockItem(ModBlocks.WASTE_EARTH.get(), new Item.Properties()));
    public static final RegistryObject<Item> WASTE_MYCELIUM = register(BLOCK_TAB, "waste_mycelium",
            () -> new BlockItem(ModBlocks.WASTE_MYCELIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> FROZEN_GRASS = register(BLOCK_TAB, "frozen_grass",
            () -> new BlockItem(ModBlocks.FROZEN_GRASS.get(), new Item.Properties()));
    public static final RegistryObject<Item> BURNING_EARTH = register(BLOCK_TAB, "burning_earth",
            () -> new BlockItem(ModBlocks.BURNING_EARTH.get(), new Item.Properties()));

    // Декоративные блоки
    public static final RegistryObject<Item> DECO_TITANIUM = register(BLOCK_TAB, "deco/deco_titanium",
            () -> new BlockItem(ModBlocks.DECO_TITANIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> DECO_RED_COPPER = register(BLOCK_TAB, "deco/deco_red_copper",
            () -> new BlockItem(ModBlocks.DECO_RED_COPPER.get(), new Item.Properties()));
    public static final RegistryObject<Item> DECO_TUNGSTEN = register(BLOCK_TAB, "deco/deco_tungsten",
            () -> new BlockItem(ModBlocks.DECO_TUNGSTEN.get(), new Item.Properties()));
    public static final RegistryObject<Item> DECO_ALUMINIUM = register(BLOCK_TAB, "deco/deco_aluminium",
            () -> new BlockItem(ModBlocks.DECO_ALUMINIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> DECO_STEEL = register(BLOCK_TAB, "deco/deco_steel",
            () -> new BlockItem(ModBlocks.DECO_STEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> DECO_LEAD = register(BLOCK_TAB, "deco/deco_lead",
            () -> new BlockItem(ModBlocks.DECO_LEAD.get(), new Item.Properties()));
    public static final RegistryObject<Item> DECO_BERYLLIUM = register(BLOCK_TAB, "deco/deco_beryllium",
            () -> new BlockItem(ModBlocks.DECO_BERYLLIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> DECO_STAINLESS = register(BLOCK_TAB, "deco/deco_stainless",
            () -> new BlockItem(ModBlocks.DECO_STAINLESS.get(), new Item.Properties()));
    public static final RegistryObject<Item> DECO_RUSTY_STEEL = register(BLOCK_TAB, "deco/deco_rusty_steel",
            () -> new BlockItem(ModBlocks.DECO_RUSTY_STEEL.get(), new Item.Properties()));

    // Блоки металлов
    public static final RegistryObject<Item> BLOCK_STEEL = register(BLOCK_TAB, "block_steel",
            () -> new BlockItem(ModBlocks.BLOCK_STEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_DESH = register(BLOCK_TAB, "block_desh",
            () -> new BlockItem(ModBlocks.BLOCK_DESH.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_ALUMINIUM = register(BLOCK_TAB, "block_aluminium",
            () -> new BlockItem(ModBlocks.BLOCK_ALUMINIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_TITANIUM = register(BLOCK_TAB, "block_titanium",
            () -> new BlockItem(ModBlocks.BLOCK_TITANIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_TUNGSTEN = register(BLOCK_TAB, "block_tungsten",
            () -> new BlockItem(ModBlocks.BLOCK_TUNGSTEN.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_LITHIUM = register(BLOCK_TAB, "block_lithium",
            () -> new BlockItem(ModBlocks.BLOCK_LITHIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_MAGNETIZED_TUNGSTEN = register(BLOCK_TAB, "block_magnetized_tungsten",
            () -> new BlockItem(ModBlocks.BLOCK_MAGNETIZED_TUNGSTEN.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_LEAD = register(BLOCK_TAB, "block_lead",
            () -> new BlockItem(ModBlocks.BLOCK_LEAD.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_BERYLLIUM = register(BLOCK_TAB, "block_beryllium",
            () -> new BlockItem(ModBlocks.BLOCK_BERYLLIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_BORON = register(BLOCK_TAB, "block_boron",
            () -> new BlockItem(ModBlocks.BLOCK_BORON.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_COBALT = register(BLOCK_TAB, "block_cobalt",
            () -> new BlockItem(ModBlocks.BLOCK_COBALT.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_URANIUM = register(BLOCK_TAB, "block_uranium",
            () -> new BlockItem(ModBlocks.BLOCK_URANIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_U233 = register(BLOCK_TAB, "block_u233",
            () -> new BlockItem(ModBlocks.BLOCK_U233.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_U235 = register(BLOCK_TAB, "block_u235",
            () -> new BlockItem(ModBlocks.BLOCK_U235.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_U238 = register(BLOCK_TAB, "block_u238",
            () -> new BlockItem(ModBlocks.BLOCK_U238.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_PLUTONIUM = register(BLOCK_TAB, "block_plutonium",
            () -> new BlockItem(ModBlocks.BLOCK_PLUTONIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_THORIUM = register(BLOCK_TAB, "block_thorium",
            () -> new BlockItem(ModBlocks.BLOCK_THORIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_POLONIUM = register(BLOCK_TAB, "block_polonium",
            () -> new BlockItem(ModBlocks.BLOCK_POLONIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_TRINITITE = register(BLOCK_TAB, "block_trinitite",
            () -> new BlockItem(ModBlocks.BLOCK_TRINITITE.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_STARMETAL = register(BLOCK_TAB, "block_starmetal",
            () -> new BlockItem(ModBlocks.BLOCK_STARMETAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> BLOCK_METEOR = register(BLOCK_TAB, "block_meteor",
            () -> new BlockItem(ModBlocks.BLOCK_METEOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> METEOR_POLISHED = register(BLOCK_TAB, "meteor_polished",
            () -> new BlockItem(ModBlocks.METEOR_POLISHED.get(), new Item.Properties()));
    public static final RegistryObject<Item> METEOR_BRICK = register(BLOCK_TAB, "meteor_brick",
            () -> new BlockItem(ModBlocks.METEOR_BRICK.get(), new Item.Properties()));

    public static final RegistryObject<Item> REINFORCED_STONE = register(BLOCK_TAB, "reinforced_stone",
            () -> new BlockItem(ModBlocks.REINFORCED_STONE.get(), new Item.Properties()));

    // =============================================== Предметы =================================================
    // Порошки
    public static final RegistryObject<Item> POWDER_ALUMINIUM = register(PARTS_TAB, "powder_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_ASBESTOS = register(PARTS_TAB, "powder_asbestos",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_BERYLLIUM = register(PARTS_TAB, "powder_beryllium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_COAL = register(PARTS_TAB, "powder_coal",
            () -> new FuelItem(new Item.Properties(), 1600), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_COBALT = register(PARTS_TAB, "powder_cobalt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_LEAD = register(PARTS_TAB, "powder_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_LIGNITE = register(PARTS_TAB, "powder_lignite",
            () -> new FuelItem(new Item.Properties(), 1200), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_THORIUM = register(PARTS_TAB, "powder_thorium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_TITANIUM = register(PARTS_TAB, "powder_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_LITHIUM = register(PARTS_TAB, "powder_lithium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_TUNGSTEN = register(PARTS_TAB, "powder_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_MAGNETIZED_TUNGSTEN = register(PARTS_TAB, "powder_magnetized_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_ZINC = register(PARTS_TAB, "powder_zinc",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_URANIUM = register(PARTS_TAB, "powder_uranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_PLUTONIUM = register(PARTS_TAB, "powder_plutonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_EUPHEMIUM = register(PARTS_TAB, "powder_euphemium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_IRON = register(PARTS_TAB, "powder_iron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_GOLD = register(PARTS_TAB, "powder_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_COPPER = register(PARTS_TAB, "powder_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_RED_COPPER = register(PARTS_TAB, "powder_red_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_ADVANCED_ALLOY = register(PARTS_TAB, "powder_advanced_alloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_STEEL = register(PARTS_TAB, "powder_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SULFUR = register(PARTS_TAB, "powder_sulfur",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NITER = register(PARTS_TAB, "powder_niter",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_CINNABAR = register(PARTS_TAB, "powder_cinnabar",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_FLUORITE = register(PARTS_TAB, "powder_fluorite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_DESH = register(PARTS_TAB, "powder_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_DESH_MIX = register(PARTS_TAB, "powder_desh_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_QUARTZ = register(PARTS_TAB, "powder_quartz",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_LAPIS = register(PARTS_TAB, "powder_lapis",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_DIAMOND = register(PARTS_TAB, "powder_diamond",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_EMERALD = register(PARTS_TAB, "powder_emerald",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_SAWDUST = register(PARTS_TAB, "powder_sawdust",
            () -> new FuelItem(new Item.Properties(), 100), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_METEORITE = register(PARTS_TAB, "powder_meteorite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_FLUX = register(PARTS_TAB, "powder_flux",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_FIRE = register(PARTS_TAB, "powder_red_phosphorus",
            () -> new FuelItem(new Item.Properties(), 6400), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NITRA = register(PARTS_TAB, "nitra",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_MOLYSITE = register(PARTS_TAB, "powder_molysite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_CHLOROCALCITE = register(PARTS_TAB, "powder_chlorocalcite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_CALCIUM = register(PARTS_TAB, "powder_calcium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_SODIUM = register(PARTS_TAB, "powder_sodium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_LIMESTONE = register(PARTS_TAB, "powder_limestone",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_CEMENT = register(PARTS_TAB, "powder_cement",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_SCHRABIDIUM = register(PARTS_TAB, "powder_schrabidium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_SCHRABIDATE = register(PARTS_TAB, "powder_schrabidate",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_NEPTUNIUM = register(PARTS_TAB, "powder_neptunium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_NIOBIUM = register(PARTS_TAB, "powder_niobium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_TEKTITE = register(PARTS_TAB, "powder_tektite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_POLONIUM = register(PARTS_TAB, "powder_polonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_NITAN_MIX = register(PARTS_TAB, "powder_nitan_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_POWER = register(PARTS_TAB, "powder_power",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_ASTATINE = register(PARTS_TAB, "powder_astatine",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_DURA_STEEL = register(PARTS_TAB, "powder_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_DESH_READY = register(PARTS_TAB, "powder_desh_ready",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_YELLOWCAKE = register(PARTS_TAB, "powder_yellowcake",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_BORAX = register(PARTS_TAB, "powder_borax",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_IODINE = register(PARTS_TAB, "powder_iodine",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_BROMINE = register(PARTS_TAB, "powder_bromine",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_CAESIUM = register(PARTS_TAB, "powder_caesium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_TENNESSINE = register(PARTS_TAB, "powder_tennessine",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_BORON = register(PARTS_TAB, "powder_boron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_CERIUM = register(PARTS_TAB, "powder_cerium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_NEODYMIUM = register(PARTS_TAB, "powder_neodymium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_LANTHANIUM = register(PARTS_TAB, "powder_lanthanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_THERMITE = register(PARTS_TAB, "powder_thermite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_STRONTIUM = register(PARTS_TAB, "powder_strontium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_SPARK_MIX = register(PARTS_TAB, "powder_spark_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_SEMTEX_MIX = register(PARTS_TAB, "powder_semtex_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_MAGIC = register(PARTS_TAB, "powder_magic",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_POISON = register(PARTS_TAB, "powder_poison",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_CHLOROPHYTE = register(PARTS_TAB, "powder_chlorophyte",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_DINEUTRONIUM = register(PARTS_TAB, "powder_dineutronium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_FERTILIZER = register(PARTS_TAB, "powder_fertilizer",
            () -> new ItemFertilizer(new Item.Properties().stacksTo(64)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_URANIUM_TINY = register(PARTS_TAB, "powder_tiny_uranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_PLUTONIUM_TINY = register(PARTS_TAB, "powder_tiny_plutonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_THORIUM_TINY = register(PARTS_TAB, "powder_tiny_thorium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_IRON_TINY = register(PARTS_TAB, "powder_tiny_iron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_GOLD_TINY = register(PARTS_TAB, "powder_tiny_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_COPPER_TINY = register(PARTS_TAB, "powder_tiny_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_RED_COPPER_TINY = register(PARTS_TAB, "powder_tiny_red_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_STEEL_TINY = register(PARTS_TAB, "powder_tiny_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_ALUMINIUM_TINY = register(PARTS_TAB, "powder_tiny_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_ASBESTOS_TINY = register(PARTS_TAB, "powder_tiny_asbestos",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_BERYLLIUM_TINY = register(PARTS_TAB, "powder_tiny_beryllium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_COAL_TINY = register(PARTS_TAB, "powder_tiny_coal",
            () -> new FuelItem(new Item.Properties(), 178), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_COBALT_TINY = register(PARTS_TAB, "powder_tiny_cobalt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_LEAD_TINY = register(PARTS_TAB, "powder_tiny_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_LIGNITE_TINY = register(PARTS_TAB, "powder_tiny_lignite",
            () -> new FuelItem(new Item.Properties(), 178), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_TITANIUM_TINY = register(PARTS_TAB, "powder_tiny_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_LITHIUM_TINY = register(PARTS_TAB, "powder_tiny_lithium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_TUNGSTEN_TINY = register(PARTS_TAB, "powder_tiny_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_MAGNETIZED_TUNGSTEN_TINY = register(PARTS_TAB, "powder_tiny_magnetized_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_ZINC_TINY = register(PARTS_TAB, "powder_tiny_zinc",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_FLUORITE_TINY = register(PARTS_TAB, "powder_tiny_fluorite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_SULFUR_TINY = register(PARTS_TAB, "powder_tiny_sulfur",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_NITER_TINY = register(PARTS_TAB, "powder_tiny_niter",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> NITRA_SMALL = register(PARTS_TAB, "nitra_small",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_CINNABAR_TINY = register(PARTS_TAB, "powder_tiny_cinnabar",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_NEODYMIUM_TINY = register(PARTS_TAB, "powder_tiny_neodymium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_CERIUM_TINY = register(PARTS_TAB, "powder_tiny_cerium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_LANTHANIUM_TINY = register(PARTS_TAB, "powder_tiny_lanthanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_ACTINIUM_TINY = register(PARTS_TAB, "powder_tiny_actinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_ASH = register("powder_ash",
            () -> new ItemEnumMulti(new Item.Properties(), HBMEnums.EnumAshType.class, true),
            ItemModelType.ENUM_ITEM,
            HBMEnums.EnumAshType.class);

    public static final RegistryObject<Item> POWDER_BORON_TINY = register(PARTS_TAB, "powder_tiny_boron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_NIOBIUM_TINY = register(PARTS_TAB, "powder_tiny_niobium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_METEORITE_TINY = register(PARTS_TAB, "powder_tiny_meteorite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> POWDER_PALEOGENITE_TINY = register(PARTS_TAB, "powder_tiny_paleogenite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> DUST_TINY = register(PARTS_TAB, "powder_tiny_dust",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED_TINY);

    public static final RegistryObject<Item> CRYSTAL_ALUMINIUM = register(PARTS_TAB, "crystal_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_BERYLLIUM = register(PARTS_TAB, "crystal_beryllium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_CHARRED = register(PARTS_TAB, "crystal_charred",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_CINNABAR = register(PARTS_TAB, "crystal_cinnabar",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_COAL = register(PARTS_TAB, "crystal_coal",
            () -> new FuelItem(new Item.Properties(), 6400), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_COBALT = register(PARTS_TAB, "crystal_cobalt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_COPPER = register(PARTS_TAB, "crystal_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_DIAMOND = register(PARTS_TAB, "crystal_diamond",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_EMERALD = register(PARTS_TAB, "crystal_emerald",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_FLUORITE = register(PARTS_TAB, "crystal_fluorite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_GOLD = register(PARTS_TAB, "crystal_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_HORN = register(PARTS_TAB, "crystal_horn",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_IRON = register(PARTS_TAB, "crystal_iron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_ZINC = register(PARTS_TAB, "crystal_zinc",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_LAPIS = register(PARTS_TAB, "crystal_lapis",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_LEAD = register(PARTS_TAB, "crystal_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_LITHIUM = register(PARTS_TAB, "crystal_lithium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_NITER = register(PARTS_TAB, "crystal_niter",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_OSMIRIDIUM = register(PARTS_TAB, "crystal_osmiridium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_PHOSPHORUS = register(PARTS_TAB, "crystal_phosphorus",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_PLUTONIUM = register(PARTS_TAB, "crystal_plutonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_RARE = register(PARTS_TAB, "crystal_rare",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_REDSTONE = register(PARTS_TAB, "crystal_redstone",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_SCHRABIDIUM = register(PARTS_TAB, "crystal_schrabidium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_SCHRARANIUM = register(PARTS_TAB, "crystal_schraranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_STARMETAL = register(PARTS_TAB, "crystal_starmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_SULFUR = register(PARTS_TAB, "crystal_sulfur",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_THORIUM = register(PARTS_TAB, "crystal_thorium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_TITANIUM = register(PARTS_TAB, "crystal_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_TRIXITE = register(PARTS_TAB, "crystal_trixite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_TUNGSTEN = register(PARTS_TAB, "crystal_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_URANIUM = register(PARTS_TAB, "crystal_uranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRYSTAL_XEN = register(PARTS_TAB, "crystal_xen",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CINNABAR = register(PARTS_TAB, "cinnabar",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FLUORITE = register(PARTS_TAB, "fluorite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RARE_EARTH_CHUNK = register(PARTS_TAB, "rare_earth_chunk",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGNITE = register(PARTS_TAB, "lignite",
            () -> new FuelItem(new Item.Properties(), 1200), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_ALUMINIUM = register(PARTS_TAB, "raw_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_BERYLLIUM = register(PARTS_TAB, "raw_beryllium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_COBALT = register(PARTS_TAB, "raw_cobalt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_LEAD = register(PARTS_TAB, "raw_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_PLUTONIUM = register(PARTS_TAB, "raw_plutonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_THORIUM = register(PARTS_TAB, "raw_thorium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_TITANIUM = register(PARTS_TAB, "raw_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_TUNGSTEN = register(PARTS_TAB, "raw_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_URANIUM = register(PARTS_TAB, "raw_uranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAW_ZINC = register(PARTS_TAB, "raw_zinc",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_ALUMINIUM = register(PARTS_TAB, "ingot_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_TITANIUM = register(PARTS_TAB, "ingot_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_TUNGSTEN = register(PARTS_TAB, "ingot_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_LEAD = register(PARTS_TAB, "ingot_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_BERYLLIUM = register(PARTS_TAB, "ingot_beryllium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_GRAPHITE = register(PARTS_TAB, "ingot_graphite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_COBALT = register(PARTS_TAB, "ingot_cobalt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_BORON = register(PARTS_TAB, "ingot_boron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_ZINC = register(PARTS_TAB, "ingot_zinc",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_STEEL = register(PARTS_TAB, "ingot_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_STAINLESS = register(PARTS_TAB, "ingot_stainless",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_RED_COPPER = register(PARTS_TAB, "ingot_red_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_URANIUM = register(PARTS_TAB, "ingot_uranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_U233 = register(PARTS_TAB, "ingot_u233",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_PLUTONIUM = register(PARTS_TAB, "ingot_plutonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_THORIUM = register(PARTS_TAB, "ingot_thorium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_DESH = register(PARTS_TAB, "ingot_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_TANTALIUM = register(PARTS_TAB, "ingot_tantalium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_DINEUTRONIUM = register(PARTS_TAB, "ingot_dineutronium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_SATURNITE = register(PARTS_TAB, "ingot_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_FERROURANIUM = register(PARTS_TAB, "ingot_ferrouranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_BISMUTH_BRONZE = register(PARTS_TAB, "ingot_bismuth_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_ARSENIC_BRONZE = register(PARTS_TAB, "ingot_arsenic_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_SCHRABIDATE = register(PARTS_TAB, "ingot_schrabidate",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_DNT = register(PARTS_TAB, "ingot_dnt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_OSMIRIDIUM = register(PARTS_TAB, "ingot_osmiridium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_SCHRABIDIUM = register(PARTS_TAB, "ingot_schrabidium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_SCHRARANIUM = register(PARTS_TAB, "ingot_schraranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // ==================== СЛИТКИ ====================

    public static final RegistryObject<Item> INGOT_LANTHANIUM = register(PARTS_TAB, "ingot_lanthanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_RA226 = register(PARTS_TAB, "ingot_ra226",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_ACTINIUM = register(PARTS_TAB, "ingot_actinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_SEMTEX = register(PARTS_TAB, "ingot_semtex",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_C4 = register(PARTS_TAB, "ingot_c4",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_POLYMER = register(PARTS_TAB, "ingot_polymer",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_BAKELITE = register(PARTS_TAB, "ingot_bakelite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_RUBBER = register(PARTS_TAB, "ingot_rubber",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_CADMIUM = register(PARTS_TAB, "ingot_cadmium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);


    public static final RegistryObject<Item> INGOT_CDALLOY = register(PARTS_TAB, "ingot_cdalloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_ADVANCED_ALLOY = register(PARTS_TAB, "ingot_advanced_alloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_COMBINE_STEEL = register(PARTS_TAB, "ingot_combine_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_GUNMETAL = register(PARTS_TAB, "ingot_gunmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_WEAPON_STEEL = register(PARTS_TAB, "ingot_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_DURA_STEEL = register(PARTS_TAB, "ingot_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_MAGNETIZED_TUNGSTEN = register(PARTS_TAB, "ingot_magnetized_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_ZIRCONIUM = register(PARTS_TAB, "ingot_zirconium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_FIREBRICK = register(PARTS_TAB, "ingot_firebrick",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_TCALLOY = register(PARTS_TAB, "ingot_tcalloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_STARMETAL = register(PARTS_TAB, "ingot_starmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_EUPHEMIUM = register(PARTS_TAB, "ingot_euphemium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_CMB_STEEL = register(PARTS_TAB, "ingot_cmb_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_AUSTRALIUM = register(PARTS_TAB, "ingot_australium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_BISMUTH = register(PARTS_TAB, "ingot_bismuth",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_FIBERGLASS = register(PARTS_TAB, "ingot_fiberglass",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_PC = register(PARTS_TAB, "ingot_pc",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_PVC = register(PARTS_TAB, "ingot_pvc",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_ASBESTOS = register(PARTS_TAB, "ingot_asbestos",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_U235 = register(PARTS_TAB, "ingot_u235",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_PU239 = register(PARTS_TAB, "ingot_pu239",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_NEPTUNIUM = register(PARTS_TAB, "ingot_neptunium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_U238 = register(PARTS_TAB, "ingot_u238",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_PU238 = register(PARTS_TAB, "ingot_pu238",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_PU240 = register(PARTS_TAB, "ingot_pu240",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_BIORUBBER = register(PARTS_TAB, "ingot_biorubber",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_MERCURY = register(PARTS_TAB, "ingot_mercury",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_PHOSPHORUS = register(PARTS_TAB, "ingot_phosphorus",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_BSCCO = register(PARTS_TAB, "ingot_bscco",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_CFT = register(PARTS_TAB, "ingot_cft",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_METEORITE = register(PARTS_TAB, "ingot_meteorite",
            () -> new ItemHot(new Item.Properties(), 200), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_METEORITE_FORGED = register(PARTS_TAB, "ingot_meteorite_forged",
            () -> new ItemHot(new Item.Properties(), 200), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLADE_METEORITE = register(PARTS_TAB, "blade_meteorite",
            () -> new ItemHot(new Item.Properties(), 200), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_STEEL_DUSTED = register(PARTS_TAB, "ingot_steel_dusted",
            () -> new ItemHotDusted(new Item.Properties(), 200), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_CHAINSTEEL = register(PARTS_TAB, "ingot_chainsteel",
            () -> new ItemHot(new Item.Properties(), 100), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_NIOBIUM = register(PARTS_TAB, "ingot_niobium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_NEODYMIUM = register(PARTS_TAB, "ingot_neodymium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_URANIUM_FUEL = register(PARTS_TAB, "ingot_uranium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_THORIUM_FUEL = register(PARTS_TAB, "ingot_thorium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_PLUTONIUM_FUEL = register(PARTS_TAB, "ingot_plutonium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_MOX_FUEL = register(PARTS_TAB, "ingot_mox_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_SILICON = register(PARTS_TAB, "ingot_silicon",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_GH336 = register(PARTS_TAB, "ingot_gh336",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_ELECTRONIUM = register(PARTS_TAB, "ingot_electronium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_TECHNETIUM = register(PARTS_TAB, "nugget_technetium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_LEAD = register(PARTS_TAB, "nugget_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_SCHRABIDIUM = register(PARTS_TAB, "nugget_schrabidium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_MERCURY = register(PARTS_TAB, "nugget_mercury",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_U235 = register(PARTS_TAB, "nugget_u235",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_U233 = register(PARTS_TAB, "nugget_u233",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_PU239 = register(PARTS_TAB, "nugget_pu239",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_PU240 = register(PARTS_TAB, "nugget_pu240",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_NEPTUNIUM = register(PARTS_TAB, "nugget_neptunium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_EUPHEMIUM = register(PARTS_TAB, "nugget_euphemium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_URANIUM = register(PARTS_TAB, "nugget_uranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_URANIUM_FUEL = register(PARTS_TAB, "nugget_uranium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_PLUTONIUM = register(PARTS_TAB, "nugget_plutonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_PLUTONIUM_FUEL = register(PARTS_TAB, "nugget_plutonium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_MOX_FUEL = register(PARTS_TAB, "nugget_mox_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_U238 = register(PARTS_TAB, "nugget_u238",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_TH232 = register(PARTS_TAB, "nugget_th232",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_PU238 = register(PARTS_TAB, "nugget_pu238",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_PU_MIX = register(PARTS_TAB, "nugget_pu_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_ZIRCONIUM = register(PARTS_TAB, "nugget_zirconium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_BISMUTH = register(PARTS_TAB, "nugget_bismuth",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_SOLINIUM = register(PARTS_TAB, "nugget_solinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_POLONIUM = register(PARTS_TAB, "nugget_polonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_RA_226 = register(PARTS_TAB, "nugget_ra226",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_ARSENIC = register(PARTS_TAB, "nugget_arsenic",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_NIOBIUM = register(PARTS_TAB, "nugget_niobium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_TANTALIUM = register(PARTS_TAB, "nugget_tantalium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_AU198 = register(PARTS_TAB, "nugget_au198",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_IRON = register(PARTS_TAB, "plate_iron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_GOLD = register(PARTS_TAB, "plate_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_TITANIUM = register(PARTS_TAB, "plate_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_ALUMINIUM = register(PARTS_TAB, "plate_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_STEEL = register(PARTS_TAB, "plate_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_LEAD = register(PARTS_TAB, "plate_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_COPPER = register(PARTS_TAB, "plate_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_ADVANCED_ALLOY = register(PARTS_TAB, "plate_advanced_alloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_SCHRABIDIUM = register(PARTS_TAB, "plate_schrabidium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_COMBINE_STEEL = register(PARTS_TAB, "plate_combine_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_GUNMETAL = register(PARTS_TAB, "plate_gunmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WEAPON_STEEL = register(PARTS_TAB, "plate_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_SATURNITE = register(PARTS_TAB, "plate_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_DURA_STEEL = register(PARTS_TAB, "plate_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_MIXED = register(PARTS_TAB, "plate_mixed",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_PAA = register(PARTS_TAB, "plate_paa",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_POLYMER = register(PARTS_TAB, "plate_polymer",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_DESH = register(PARTS_TAB, "plate_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_DINEUTRONIUM = register(PARTS_TAB, "plate_dineutronium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_EUPHEMIUM = register(PARTS_TAB, "plate_euphemium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_BISMUTH = register(PARTS_TAB, "plate_bismuth",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_KEVLAR = register(PARTS_TAB, "plate_kevlar",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_ARMOR_TITANIUM = register(PARTS_TAB, "plate_armor_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_ARMOR_AJR = register(PARTS_TAB, "plate_armor_ajr",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_ARMOR_HEV = register(PARTS_TAB, "plate_armor_hev",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_ARMOR_FAU = register(PARTS_TAB, "plate_armor_fau",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_ARMOR_DNT = register(PARTS_TAB, "plate_armor_dnt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_ARMOR_LUNAR = register(PARTS_TAB, "plate_armor_lunar",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_ADVANCED_ALLOY = register(PARTS_TAB, "plate_cast_advanced_alloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_ALUMINIUM = register(PARTS_TAB, "plate_cast_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_ARSENIC_BRONZE = register(PARTS_TAB, "plate_cast_arsenic_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_BISMUTH_BRONZE = register(PARTS_TAB, "plate_cast_bismuth_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_CADMIUM_STEEL = register(PARTS_TAB, "plate_cast_cadmium_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_CMB_STEEL = register(PARTS_TAB, "plate_cast_cmb_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_COPPER = register(PARTS_TAB, "plate_cast_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_DESH = register(PARTS_TAB, "plate_cast_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_DURA = register(PARTS_TAB, "plate_cast_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_FERROURANIUM = register(PARTS_TAB, "plate_cast_ferrouranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_GOLD = register(PARTS_TAB, "plate_cast_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_IRON = register(PARTS_TAB, "plate_cast_iron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_LEAD = register(PARTS_TAB, "plate_cast_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_OSMIRIDIUM = register(PARTS_TAB, "plate_cast_osmiridium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_SATURNITE = register(PARTS_TAB, "plate_cast_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_SCHRABIDATE = register(PARTS_TAB, "plate_cast_schrabidate",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_SHRABIDIUM = register(PARTS_TAB, "plate_cast_shrabidium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_STEEL = register(PARTS_TAB, "plate_cast_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_TECHNETIUM = register(PARTS_TAB, "plate_cast_technetium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_TITANIUM = register(PARTS_TAB, "plate_cast_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_TUNGSTEN = register(PARTS_TAB, "plate_cast_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_WEAPON_STEEL = register(PARTS_TAB, "plate_cast_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_CAST_ZIRCONIUM = register(PARTS_TAB, "plate_cast_zirconium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_ALUMINIUM = register(PARTS_TAB, "plate_welded_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_CADMIUM_STEEL = register(PARTS_TAB, "plate_welded_cadmium_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_CMB_STEEL = register(PARTS_TAB, "plate_welded_cmb_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_COPPER = register(PARTS_TAB, "plate_welded_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_IRON = register(PARTS_TAB, "plate_welded_iron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_OSMIRIDIUM = register(PARTS_TAB, "plate_welded_osmiridium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_STEEL = register(PARTS_TAB, "plate_welded_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_TECHNETIUM = register(PARTS_TAB, "plate_welded_technetium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_TITANIUM = register(PARTS_TAB, "plate_welded_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_TUNGSTEN = register(PARTS_TAB, "plate_welded_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLATE_WELDED_ZIRCONIUM = register(PARTS_TAB, "plate_welded_zirconium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_COPPER = register(PARTS_TAB, "wire_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_CARBON = register(PARTS_TAB, "wire_carbon",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_ALUMINIUM = register(PARTS_TAB, "wire_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_RED_COPPER = register(PARTS_TAB, "wire_red_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_GOLD = register(PARTS_TAB, "wire_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_LEAD = register(PARTS_TAB, "wire_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_ZIRCONIUM = register(PARTS_TAB, "wire_zirconium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_TUNGSTEN = register(PARTS_TAB, "wire_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_ADVANCED_ALLOY = register(PARTS_TAB, "wire_advanced_alloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_STEEL = register(PARTS_TAB, "wire_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_SCHRABIDIUM = register(PARTS_TAB, "wire_schrabidium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_MAGNETIZED_TUNGSTEN = register(PARTS_TAB, "wire_magnetized_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CASING_LARGE = register(PARTS_TAB, "casing_large",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CASING_LARGE_STEEL = register(PARTS_TAB, "casing_large_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CASING_SMALL = register(PARTS_TAB, "casing_small",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CASING_SMALL_STEEL = register(PARTS_TAB, "casing_small_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CASING_SHOTSHELL = register(PARTS_TAB, "casing_shotshell",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CASING_BUCKSHOT = register(PARTS_TAB, "casing_buckshot",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CASING_BUCKSHOT_ADVANCED = register(PARTS_TAB, "casing_buckshot_advanced",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PAGE_OF = register( "page_of",
            () -> new ItemEnumMulti<>(new Item.Properties(), HBMEnums.EnumPages.class, true),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_VACUUM_TUBE = register(PARTS_TAB, "circuit_vacuum_tube",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CAPACITOR = register(PARTS_TAB, "circuit_capacitor",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CAPACITOR_TANTALIUM = register(PARTS_TAB, "circuit_capacitor_tantalium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_PCB = register(PARTS_TAB, "circuit_pcb",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CHIP = register(PARTS_TAB, "circuit_chip",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CHIP_BISMOID = register(PARTS_TAB, "circuit_chip_bismoid",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CHIP_QUANTUM = register(PARTS_TAB, "circuit_chip_quantum",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CONTROLLER_CHASSIS = register(PARTS_TAB, "circuit_controller_chassis",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_ATOMIC_CLOCK = register(PARTS_TAB, "circuit_atomic_clock",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_SILICON = register(PARTS_TAB, "circuit_silicon",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_ANALOG = register(PARTS_TAB, "circuit_analog",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_BASIC = register(PARTS_TAB, "circuit_basic",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_ADVANCED = register(PARTS_TAB, "circuit_advanced",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CAPACITOR_BOARD = register(PARTS_TAB, "circuit_capacitor_board",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_BISMOID = register(PARTS_TAB, "circuit_bismoid",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_QUANTUM = register(PARTS_TAB, "circuit_quantum",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CONTROLLER = register(PARTS_TAB, "circuit_controller",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CONTROLLER_ADVANCED = register(PARTS_TAB, "circuit_controller_advanced",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIRCUIT_CONTROLLER_QUANTUM = register(PARTS_TAB, "circuit_controller_quantum",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRT_DISPLAY = register(PARTS_TAB, "crt_display",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NEUTRON_REFLECTOR = register(PARTS_TAB, "neutron_reflector",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BALL_FIRECLAY = register(PARTS_TAB, "ball_fireclay",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BIOMASS = register(PARTS_TAB, "biomass",
            () -> new FuelItem(new Item.Properties(), 400), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BIOMASS_COMPRESSED = register(PARTS_TAB, "biomass_compressed",
            () -> new FuelItem(new Item.Properties(), 800), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BRIQUETTE = register("briquette",
            () -> new ItemEnumMulti(new Item.Properties(), HBMEnums.EnumBriquetteType.class, true),
            ItemModelType.ENUM_ITEM,
            HBMEnums.EnumBriquetteType.class);

    public static final RegistryObject<Item> COKE = register("coke",
            () -> new ItemEnumMulti(new Item.Properties(), HBMEnums.EnumCokeType.class, true, 32000),
            ItemModelType.ENUM_ITEM,
            HBMEnums.EnumCokeType.class);

    public static final RegistryObject<Item> BALL_RESIN = register(PARTS_TAB, "ball_resin",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SCRAP = register(PARTS_TAB, "scrap",
            () -> new FuelItem(new Item.Properties(), 50), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COAL_INFERNAL = register(PARTS_TAB, "coal_infernal",
            () -> new FuelItem(new Item.Properties(), 4800), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_STONE_FLAT = register(CONTROL_TAB, "stamp_stone_flat",
            () -> new ItemStamp(32, ItemStamp.StampType.FLAT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_STONE_PLATE = register(CONTROL_TAB, "stamp_stone_plate",
            () -> new ItemStamp(32, ItemStamp.StampType.PLATE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_STONE_WIRE = register(CONTROL_TAB, "stamp_stone_wire",
            () -> new ItemStamp(32, ItemStamp.StampType.WIRE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_STONE_CIRCUIT = register(CONTROL_TAB, "stamp_stone_circuit",
            () -> new ItemStamp(32, ItemStamp.StampType.CIRCUIT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_IRON_FLAT = register(CONTROL_TAB, "stamp_iron_flat",
            () -> new ItemStamp(64, ItemStamp.StampType.FLAT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_IRON_PLATE = register(CONTROL_TAB, "stamp_iron_plate",
            () -> new ItemStamp(64, ItemStamp.StampType.PLATE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_IRON_WIRE = register(CONTROL_TAB, "stamp_iron_wire",
            () -> new ItemStamp(64, ItemStamp.StampType.WIRE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_IRON_CIRCUIT = register(CONTROL_TAB, "stamp_iron_circuit",
            () -> new ItemStamp(64, ItemStamp.StampType.CIRCUIT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_STEEL_FLAT = register(CONTROL_TAB, "stamp_steel_flat",
            () -> new ItemStamp(192, ItemStamp.StampType.FLAT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_STEEL_PLATE = register(CONTROL_TAB, "stamp_steel_plate",
            () -> new ItemStamp(192, ItemStamp.StampType.PLATE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_STEEL_WIRE = register(CONTROL_TAB, "stamp_steel_wire",
            () -> new ItemStamp(192, ItemStamp.StampType.WIRE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_STEEL_CIRCUIT = register(CONTROL_TAB, "stamp_steel_circuit",
            () -> new ItemStamp(192, ItemStamp.StampType.CIRCUIT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_TITANIUM_FLAT = register(CONTROL_TAB, "stamp_titanium_flat",
            () -> new ItemStamp(256, ItemStamp.StampType.FLAT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_TITANIUM_PLATE = register(CONTROL_TAB, "stamp_titanium_plate",
            () -> new ItemStamp(256, ItemStamp.StampType.PLATE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_TITANIUM_WIRE = register(CONTROL_TAB, "stamp_titanium_wire",
            () -> new ItemStamp(256, ItemStamp.StampType.WIRE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_TITANIUM_CIRCUIT = register(CONTROL_TAB, "stamp_titanium_circuit",
            () -> new ItemStamp(256, ItemStamp.StampType.CIRCUIT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_OBSIDIAN_FLAT = register(CONTROL_TAB, "stamp_obsidian_flat",
            () -> new ItemStamp(512, ItemStamp.StampType.FLAT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_OBSIDIAN_PLATE = register(CONTROL_TAB, "stamp_obsidian_plate",
            () -> new ItemStamp(512, ItemStamp.StampType.PLATE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_OBSIDIAN_WIRE = register(CONTROL_TAB, "stamp_obsidian_wire",
            () -> new ItemStamp(512, ItemStamp.StampType.WIRE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_OBSIDIAN_CIRCUIT = register(CONTROL_TAB, "stamp_obsidian_circuit",
            () -> new ItemStamp(512, ItemStamp.StampType.CIRCUIT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_DESH_FLAT = register(CONTROL_TAB, "stamp_desh_flat",
            () -> new ItemStamp(0, ItemStamp.StampType.FLAT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_DESH_PLATE = register(CONTROL_TAB, "stamp_desh_plate",
            () -> new ItemStamp(0, ItemStamp.StampType.PLATE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_DESH_WIRE = register(CONTROL_TAB, "stamp_desh_wire",
            () -> new ItemStamp(0, ItemStamp.StampType.WIRE, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_DESH_CIRCUIT = register(CONTROL_TAB, "stamp_desh_circuit",
            () -> new ItemStamp(0, ItemStamp.StampType.CIRCUIT, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_357 = register(CONTROL_TAB, "stamp_357",
            () -> new ItemStamp(1000, ItemStamp.StampType.C357, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_DESH_357 = register(CONTROL_TAB, "stamp_desh_357",
            () -> new ItemStamp(0, ItemStamp.StampType.C357, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_44 = register(CONTROL_TAB, "stamp_44",
            () -> new ItemStamp(1000, ItemStamp.StampType.C44, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_DESH_44 = register(CONTROL_TAB, "stamp_desh_44",
            () -> new ItemStamp(0, ItemStamp.StampType.C44, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_9 = register(CONTROL_TAB, "stamp_9",
            () -> new ItemStamp(1000, ItemStamp.StampType.C9, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_DESH_9 = register(CONTROL_TAB, "stamp_desh_9",
            () -> new ItemStamp(0, ItemStamp.StampType.C9, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_50 = register(CONTROL_TAB, "stamp_50",
            () -> new ItemStamp(1000, ItemStamp.StampType.C50, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_DESH_50 = register(CONTROL_TAB, "stamp_desh_50",
            () -> new ItemStamp(0, ItemStamp.StampType.C50, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STAMP_BOOK = register(CONTROL_TAB, "stamp_book",
            () -> new ItemStamp(0, ItemStamp.StampType.PRINTING1, new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MACHINE_PRESS_ITEM = register(MACHINE_TAB, "machines/press",
            () -> new MachinePressItem(ModBlocks.MACHINE_PRESS.get(), new Item.Properties()));
    
    public static final RegistryObject<Item> MACHINE_BLAST_FURNACE = register(MACHINE_TAB, "machines/machine_blast_furnace",
            () -> new ItemBlastFurnace(ModBlocks.MACHINE_BLAST_FURNACE.get(), new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_BLAST_FURNACE_EXTENSION =
            register(MACHINE_TAB, "machines/machine_difurnace_extension",
            () -> new ItemDiFurnaceExtension(new Item.Properties()), ItemModelType.OBJ_ITEM, "models/block/machines/machine_difurnace_extension");


    public static final RegistryObject<Item> ANVIL_IRON = register(MACHINE_TAB, "machines/anvil_iron",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_IRON.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_LEAD = register(MACHINE_TAB, "machines/anvil_lead",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_LEAD.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_STEEL = register(MACHINE_TAB, "machines/anvil_steel",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_STEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_DESH = register(MACHINE_TAB, "machines/anvil_desh",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_DESH.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_SATURNITE = register(MACHINE_TAB, "machines/anvil_saturnite",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_SATURNITE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_FERROURANIUM = register(MACHINE_TAB, "machines/anvil_ferrouranium",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_FERROURANIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_BISMUTH_BRONZE = register(MACHINE_TAB, "machines/anvil_bismuth_bronze",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_BISMUTH_BRONZE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_ARSENIC_BRONZE = register(MACHINE_TAB, "machines/anvil_arsenic_bronze",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_ARSENIC_BRONZE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_SCHRABIDATE = register(MACHINE_TAB, "machines/anvil_schrabidate",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_SCHRABIDATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_DNT = register(MACHINE_TAB, "machines/anvil_dnt",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_DNT.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_OSMIRIDIUM = register(MACHINE_TAB, "machines/anvil_osmiridium",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_OSMIRIDIUM.get(), new Item.Properties()));
    public static final RegistryObject<Item> ANVIL_MURKY = register(MACHINE_TAB, "machines/anvil_murky",
            () -> new SimpleOBJItem(ModBlocks.ANVIL_MURKY.get(), new Item.Properties()));
    public static final RegistryObject<Item> BARREL_IRON = register(MACHINE_TAB, "storage/barrel_iron",
            () -> new BarrelItem(ModBlocks.BARREL_IRON.get(), new Item.Properties()));
    public static final RegistryObject<Item> BARREL_STEEL = register(MACHINE_TAB, "storage/barrel_steel",
            () -> new BarrelItem(ModBlocks.BARREL_STEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> BARREL_ANTIMATTER = register(MACHINE_TAB, "storage/barrel_antimatter",
            () -> new BarrelItem(ModBlocks.BARREL_ANTIMATTER.get(), new Item.Properties()));
    public static final RegistryObject<Item> BARREL_CORRODED = register(MACHINE_TAB, "storage/barrel_corroded",
            () -> new BarrelItem(ModBlocks.BARREL_CORRODED.get(), new Item.Properties()));
    public static final RegistryObject<Item> BARREL_PLASTIC = register(MACHINE_TAB, "storage/barrel_plastic",
            () -> new BarrelItem(ModBlocks.BARREL_PLASTIC.get(), new Item.Properties()));
    public static final RegistryObject<Item> BARREL_TCALLOY = register(MACHINE_TAB, "storage/barrel_tcalloy",
            () -> new BarrelItem(ModBlocks.BARREL_TCALLOY.get(), new Item.Properties()));
    public static final RegistryObject<Item> RED_BARREL = register(BLOCK_TAB, "red_barrel",
            () -> new SimpleOBJItem(ModBlocks.RED_BARREL.get(), new Item.Properties()));
    public static final RegistryObject<Item> PINK_BARREL = register(BLOCK_TAB, "pink_barrel",
            () -> new SimpleOBJItem(ModBlocks.PINK_BARREL.get(), new Item.Properties()));
    public static final RegistryObject<Item> YELLOW_BARREL = register(BLOCK_TAB, "yellow_barrel",
            () -> new SimpleOBJItem(ModBlocks.YELLOW_BARREL.get(), new Item.Properties()));
    public static final RegistryObject<Item> LOX_BARREL = register(BLOCK_TAB, "lox_barrel",
            () -> new SimpleOBJItem(ModBlocks.LOX_BARREL.get(), new Item.Properties()));
    public static final RegistryObject<Item> TAINT_BARREL = register(BLOCK_TAB, "taint_barrel",
            () -> new SimpleOBJItem(ModBlocks.TAINT_BARREL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_FLUID_TANK = register(MACHINE_TAB, "storage/fluid_tank",
            () -> new MachineFluidTankItem(ModBlocks.MACHINE_FLUIDTANK.get(), new Item.Properties()));
    public static final RegistryObject<Item> FILING_CABINET_ITEM = register(MACHINE_TAB, "storage/filing_cabinet",
            () -> new FilingCabinetItem(ModBlocks.FILING_CABINET.get(), new Item.Properties()));
    public static final RegistryObject<Item> FILING_CABINET_STEEL_ITEM = register(MACHINE_TAB, "storage/filing_cabinet_steel",
            () -> new FilingCabinetItem(ModBlocks.FILING_CABINET_STEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> STEEL_GRATE = register(MACHINE_TAB, "deco/steel_grate",
            () -> new SteelGrateItem(ModBlocks.STEEL_GRATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> STEEL_GRATE_WIDE = register(MACHINE_TAB, "deco/steel_grate_wide",
            () -> new SteelGrateItem(ModBlocks.STEEL_GRATE_WIDE.get(), new Item.Properties()));
    public static final RegistryObject<Item> STEEL_BEAM = register(MACHINE_TAB, "deco/steel_beam",
            () -> new SimpleOBJItem(ModBlocks.STEEL_BEAM.get(), new Item.Properties()));
    public static final RegistryObject<Item> STEEL_SCAFFOLD = register(MACHINE_TAB, "deco/steel_scaffold",
            () -> new SteelScaffoldItem(ModBlocks.STEEL_SCAFFOLD.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_ROTARY_FURNACE = register(MACHINE_TAB,"machines/rotary_furnace",
            () -> new ItemRotaryFurnace(ModBlocks.MACHINE_ROTARY_FURNACE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_CRUCIBLE = register(MACHINE_TAB,"machines/machine_crucible",
            () -> new ItemMachineCrucible(ModBlocks.MACHINE_CRUCIBLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_SOLDERING_STATION = register(MACHINE_TAB,"machines/machine_soldering_station",
            () -> new ItemSolderingStation(ModBlocks.MACHINE_SOLDERING_STATION.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_ARC_WELDER = register(MACHINE_TAB,"machines/machine_arc_welder",
            () -> new ItemArcWelder(ModBlocks.MACHINE_ARC_WELDER.get(), new Item.Properties()));

    public static final RegistryObject<Item> FLUID_CANISTER = register("fluid_canister",
            () -> new ItemFluidCanister(new Item.Properties().stacksTo(16)),
            ItemModelType.GENERATED_LAYERED,
            "fluid_canister_base", "fluid_canister_overlay");

    public static final RegistryObject<Item> GAS_TANK = register("gas_tank",
            () -> new ItemGasTank(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED_LAYERED,
            "gas_tank_base", "gas_tank_overlay");

    public static final RegistryObject<Item> DISPERSER_CANISTER = register("disperser_canister",
            () -> new ItemDisperser(new Item.Properties()), ItemModelType.GENERATED_LAYERED,
            "disperser_canister_base", "disperser_canister_overlay");

    public static final RegistryObject<Item> GLYPHID_GLAND = register("glyphid_gland",
            () -> new ItemDisperserGland(new Item.Properties()), ItemModelType.GENERATED_LAYERED,
            "glyphid_gland_base", "glyphid_gland_overlay");

    public static final RegistryObject<Item> FLUID_TANK_LEAD = register("fluid_tank_lead",
            () -> new ItemFluidTankLead(new Item.Properties()), ItemModelType.GENERATED_LAYERED,
            "fluid_tank_lead_base", "fluid_tank_lead_overlay");

    public static final RegistryObject<Item> FLUID_IDENTIFIER = register("fluid_identifier",
            () -> new ItemFluidID(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED_LAYERED,
            "fluid_identifier_base", "fluid_identifier_overlay");

    public static final RegistryObject<Item> FLUID_TANK = register("fluid_tank",
            () -> new ItemFluidTank(new Item.Properties().stacksTo(64)),
            ItemModelType.GENERATED_LAYERED,
            "fluid_tank_base", "fluid_tank_overlay");

    public static final RegistryObject<Item> FLUID_BARREL = register("fluid_barrel",
            () -> new ItemFluidBarrel(new Item.Properties().stacksTo(64)),
            ItemModelType.GENERATED_LAYERED,
            "fluid_barrel_base", "fluid_barrel_overlay");

    public static final RegistryObject<Item> FLUID_BUCKET = register("fluid_bucket",
            () -> new ItemFluidBucket(new Item.Properties().stacksTo(64)),
            ItemModelType.GENERATED_LAYERED,
            "fluid_bucket_base", "fluid_bucket_overlay");

    public static final RegistryObject<Item> EGG_GLYPHID = register(CONSUMABLE_TAB, "egg_glyphid",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GLYPHID_MEAT = register(CONSUMABLE_TAB, "glyphid_meat",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GLYPHID_MEAT_GRILLED = register(CONSUMABLE_TAB, "glyphid_meat_grilled",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationMod(0.75F)
                            .meat()
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 1), 1.0F)
                            .build())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CHEESE = register(CONSUMABLE_TAB, "cheese",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(8)
                            .saturationMod(0.75F)
                            .meat()
                            .effect(() -> new MobEffectInstance(MobEffects.DAMAGE_BOOST, 3600, 1), 1.0F)
                            .build())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEFINITELYFOOD = register(CONSUMABLE_TAB, "definitelyfood",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(3)
                            .saturationMod(0.5F)
                            .meat()
                            .build())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_EMPTY = register(CONSUMABLE_TAB, "syringe_empty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_METAL_STIMPAK = register(CONSUMABLE_TAB, "syringe_metal_stimpak",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_METAL_EMPTY = register(CONSUMABLE_TAB, "syringe_metal_empty",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_METAL_MEDX = register(CONSUMABLE_TAB, "syringe_metal_medx",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_METAL_PSYCHO = register(CONSUMABLE_TAB, "syringe_metal_psycho",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_METAL_SUPER = register(CONSUMABLE_TAB, "syringe_metal_super",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_MKUNICORN = register(CONSUMABLE_TAB, "syringe_mkunicorn",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_TAINT = register(CONSUMABLE_TAB, "syringe_taint",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_ANTIDOTE = register(CONSUMABLE_TAB, "syringe_antidote",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_AWESOME = register(CONSUMABLE_TAB, "syringe_awesome",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> JETPACK_TANK = register(CONSUMABLE_TAB, "jetpack_tank",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SYRINGE_POISON = register(CONSUMABLE_TAB, "syringe_poison",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CBT_DEVICE = register(CONSUMABLE_TAB, "cbt_device",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILL_IODINE = register(CONSUMABLE_TAB, "pill_iodine",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLAN_C = register(CONSUMABLE_TAB, "plan_c",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILL_RED = register(CONSUMABLE_TAB, "pill_red",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RADX = register(CONSUMABLE_TAB, "radx",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SIOX = register(CONSUMABLE_TAB, "siox",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILL_HERBAL = register(CONSUMABLE_TAB, "pill_herbal",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> XANAX = register(CONSUMABLE_TAB, "xanax",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CHOCOLATE = register(CONSUMABLE_TAB, "chocolate",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FMN = register(CONSUMABLE_TAB, "fmn",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FIVE_HTP = register(CONSUMABLE_TAB, "five_htp",
            () -> new ItemPill(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GEIGER_COUNTER = register(CONSUMABLE_TAB, "geiger_counter",
            () -> new ItemGeigerCounter(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MED_BAG = register(CONSUMABLE_TAB, "med_bag",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RADAWAY = register(CONSUMABLE_TAB, "radaway",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RADAWAY_STRONG = register(CONSUMABLE_TAB, "radaway_strong",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RADAWAY_FLUSH = register(CONSUMABLE_TAB, "radaway_flush",
            () -> new ItemSyringe(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE2_EMPTY = register(CONSUMABLE_TAB, "bottle2_empty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GAS_MASK_FILTER = register(CONSUMABLE_TAB, "gas_mask_filter",
            () -> new ItemFilter(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GAS_MASK_FILTER_MONO = register(CONSUMABLE_TAB, "gas_mask_filter_mono",
            () -> new ItemFilter(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GAS_MASK_FILTER_COMBO = register(CONSUMABLE_TAB, "gas_mask_filter_combo",
            () -> new ItemFilter(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GAS_MASK_FILTER_RAG = register(CONSUMABLE_TAB, "gas_mask_filter_rag",
            () -> new ItemFilter(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GAS_MASK_FILTER_PISS = register(CONSUMABLE_TAB, "gas_mask_filter_piss",
            () -> new ItemFilter(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GUN_KIT_1 = register(CONSUMABLE_TAB, "gun_kit_1",
            () -> new ItemRepairKit(10), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GUN_KIT_2 = register(CONSUMABLE_TAB, "gun_kit_2",
            () -> new ItemRepairKit(100), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOOK_GUIDE = register(CONSUMABLE_TAB, "book_guide",
            () -> new ItemGuideBook(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOOK_OF_ = register(CONSUMABLE_TAB, "book_of_",
            () -> new ItemBook(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> RAG = register(CONSUMABLE_TAB, "rag",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_CLOTH = register(CONSUMABLE_TAB, "fabric_hazmat",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_CLOTH_RED = register(CONSUMABLE_TAB, "fabric_hazmat2",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HAZMAT_CLOTH_GREY = register(CONSUMABLE_TAB, "fabric_hazmat3",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ASBESTOS_CLOTH = register(CONSUMABLE_TAB, "asbestos_cloth",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NOTHING = register(WEAPON_TAB, "nothing",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FLUID_ICON = register("fluid_icon",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> AMMO_BAG_INFINITE = register(WEAPON_TAB, "ammo_bag_infinite",
            () -> new ItemAmmoBag(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CASING_BAG = register(WEAPON_TAB, "casing_bag",
            () -> new ItemAmmoBag(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> AMMO_BAG = register(WEAPON_TAB, "ammo_bag",
            () -> new ItemAmmoBag(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRATE_SUPPLIES_ITEM = register(BLOCK_TAB, "block_supply_crate",
            () -> new SupplyCrateItem(ModBlocks.CRATE_SUPPLIES.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRATE_WEAPONS_ITEM = register(BLOCK_TAB, "block_weapon_crate",
            () -> new SupplyCrateItem(ModBlocks.CRATE_WEAPONS.get(), new Item.Properties()));

    public static final RegistryObject<Item> COIL_COPPER = register(PARTS_TAB, "coil_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIL_GOLD = register(PARTS_TAB, "coil_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIL_TUNGSTEN = register(PARTS_TAB, "coil_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIL_ADVANCED_ALLOY = register(PARTS_TAB, "coil_advanced_alloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIL_MAGNETIZED_TUNGSTEN = register(PARTS_TAB, "coil_magnetized_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIL_COPPER_TORUS = register(PARTS_TAB, "coil_copper_torus",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIL_GOLD_TORUS = register(PARTS_TAB, "coil_gold_torus",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIL_ADVANCED_ALLOY_TORUS = register(PARTS_TAB, "coil_advanced_alloy_torus",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIL_MAGNETIZED_TUNGSTEN_TORUS = register(PARTS_TAB, "coil_magnetized_tungsten_torus",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIN_CREEPER = register(PARTS_TAB, "coin_creeper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIN_MASKMAN = register(PARTS_TAB, "coin_maskman",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIN_WORM = register(PARTS_TAB, "coin_worm",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIN_UFO = register(PARTS_TAB, "coin_ufo",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> COIN_RADIATION = register(PARTS_TAB, "coin_radiation",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PELLET_ANTIMATTER = register(PARTS_TAB, "pellet_antimatter",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FLAME_PONY = register(PARTS_TAB, "flame_pony",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MAN_CORE = register(PARTS_TAB, "man_core",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STICK_DYNAMITE = register(WEAPON_TAB, "weapon/grenade/stick_dynamite",
            () -> new ItemGrenade(new Item.Properties(), 3));
    public static final RegistryObject<Item> GRENADE_GENERIC = register(WEAPON_TAB, "weapon/grenade/grenade_generic",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_STRONG = register(WEAPON_TAB, "weapon/grenade/grenade_strong",
            () -> new ItemGrenade(new Item.Properties(), 5));
    public static final RegistryObject<Item> GRENADE_FRAG = register(WEAPON_TAB, "weapon/grenade/grenade_frag",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_FIRE = register(WEAPON_TAB, "weapon/grenade/grenade_fire",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_SHRAPNEL = register(WEAPON_TAB, "weapon/grenade/grenade_shrapnel",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_CLUSTER = register(WEAPON_TAB, "weapon/grenade/grenade_cluster",
            () -> new ItemGrenade(new Item.Properties(), 5));
    public static final RegistryObject<Item> GRENADE_FLARE = register(WEAPON_TAB, "weapon/grenade/grenade_flare",
            () -> new ItemGrenade(new Item.Properties(), 0));
    public static final RegistryObject<Item> GRENADE_ELECTRIC = register(WEAPON_TAB, "weapon/grenade/grenade_electric",
            () -> new ItemGrenade(new Item.Properties(), 5));
    public static final RegistryObject<Item> GRENADE_POISON = register(WEAPON_TAB, "weapon/grenade/grenade_poison",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_GAS = register(WEAPON_TAB, "weapon/grenade/grenade_gas",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_PULSE = register(WEAPON_TAB, "weapon/grenade/grenade_pulse",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_PLASMA = register(WEAPON_TAB, "weapon/grenade/grenade_plasma",
            () -> new ItemGrenade(new Item.Properties(), 5));
    public static final RegistryObject<Item> GRENADE_TAU = register(WEAPON_TAB, "weapon/grenade/grenade_tau",
            () -> new ItemGrenade(new Item.Properties(), 5));
    public static final RegistryObject<Item> GRENADE_SCHRABIDIUM = register(WEAPON_TAB, "weapon/grenade/grenade_schrabidium",
            () -> new ItemGrenade(new Item.Properties(), 7));
    public static final RegistryObject<Item> GRENADE_LEMON = register(WEAPON_TAB, "weapon/grenade/grenade_lemon",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_GASCAN = register(WEAPON_TAB, "weapon/grenade/grenade_gascan",
            () -> new ItemGrenade(new Item.Properties(), -1));
    public static final RegistryObject<Item> GRENADE_MK2 = register(WEAPON_TAB, "weapon/grenade/grenade_mk2",
            () -> new ItemGrenade(new Item.Properties(), 5));
    public static final RegistryObject<Item> GRENADE_ASCHRAB = register(WEAPON_TAB, "weapon/grenade/grenade_aschrab",
            () -> new ItemGrenade(new Item.Properties(), -1));
    public static final RegistryObject<Item> GRENADE_NUKE = register(WEAPON_TAB, "weapon/grenade/grenade_nuke",
            () -> new ItemGrenade(new Item.Properties(), -1));
    public static final RegistryObject<Item> GRENADE_NUCLEAR = register(WEAPON_TAB, "weapon/grenade/grenade_nuclear",
            () -> new ItemGrenade(new Item.Properties(), 7));
    public static final RegistryObject<Item> GRENADE_ZOMG = register(WEAPON_TAB, "weapon/grenade/grenade_zomg",
            () -> new ItemGrenade(new Item.Properties(), 7));
    public static final RegistryObject<Item> GRENADE_BLACK_HOLE = register(WEAPON_TAB, "weapon/grenade/grenade_black_hole",
            () -> new ItemGrenade(new Item.Properties(), 7));
    public static final RegistryObject<Item> GRENADE_CLOUD = register(WEAPON_TAB, "weapon/grenade/grenade_cloud",
            () -> new ItemGrenade(new Item.Properties(), -1));
    public static final RegistryObject<Item> GRENADE_PINK_CLOUD = register(WEAPON_TAB, "weapon/grenade/grenade_pink_cloud",
            () -> new ItemGrenade(new Item.Properties(), -1));
    public static final RegistryObject<Item> GRENADE_IF_GENERIC = register(WEAPON_TAB, "weapon/grenade/grenade_if_generic",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_IF_HE = register(WEAPON_TAB, "weapon/grenade/grenade_if_he",
            () -> new ItemGrenade(new Item.Properties(), 5));
    public static final RegistryObject<Item> GRENADE_IF_BOUNCY = register(WEAPON_TAB, "weapon/grenade/grenade_if_bouncy",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_IF_STICKY = register(WEAPON_TAB, "weapon/grenade/grenade_if_sticky",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_IF_IMPACT = register(WEAPON_TAB, "weapon/grenade/grenade_if_impact",
            () -> new ItemGrenade(new Item.Properties(), -1));
    public static final RegistryObject<Item> GRENADE_IF_INCENDIARY = register(WEAPON_TAB, "weapon/grenade/grenade_if_incendiary",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_IF_TOXIC = register(WEAPON_TAB, "weapon/grenade/grenade_if_toxic",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_IF_CONCUSSION = register(WEAPON_TAB, "weapon/grenade/grenade_if_concussion",
            () -> new ItemGrenade(new Item.Properties(), 4));
    public static final RegistryObject<Item> GRENADE_IF_BRIMSTONE = register(WEAPON_TAB, "weapon/grenade/grenade_if_brimstone",
            () -> new ItemGrenade(new Item.Properties(), 5));
    public static final RegistryObject<Item> GRENADE_IF_MYSTERY = register(WEAPON_TAB, "weapon/grenade/grenade_if_mystery",
            () -> new ItemGrenade(new Item.Properties(), 5));
    public static final RegistryObject<Item> GRENADE_IF_SPARK = register(WEAPON_TAB, "weapon/grenade/grenade_if_spark",
            () -> new ItemGrenade(new Item.Properties(), 7));
    public static final RegistryObject<Item> GRENADE_IF_HOPWIRE = register(WEAPON_TAB, "weapon/grenade/grenade_if_hopwire",
            () -> new ItemGrenade(new Item.Properties(), 7));
    public static final RegistryObject<Item> GRENADE_IF_NULL = register(WEAPON_TAB, "weapon/grenade/grenade_if_null",
            () -> new ItemGrenade(new Item.Properties(), 7));
    public static final RegistryObject<Item> GRENADE_SMART = register(WEAPON_TAB, "weapon/grenade/grenade_smart",
            () -> new ItemGrenade(new Item.Properties(), -1));
    public static final RegistryObject<Item> GRENADE_MIRV = register(WEAPON_TAB, "weapon/grenade/grenade_mirv",
            () -> new ItemGrenade(new Item.Properties(), 1));
    public static final RegistryObject<Item> GRENADE_BREACH = register(WEAPON_TAB, "weapon/grenade/grenade_breach",
            () -> new ItemGrenade(new Item.Properties(), -1));
    public static final RegistryObject<Item> GRENADE_BURST = register(WEAPON_TAB, "weapon/grenade/grenade_burst",
            () -> new ItemGrenade(new Item.Properties(), 1));
    public static final RegistryObject<Item> GRENADE_KYIV = register(WEAPON_TAB, "grenade_kyiv",
            () -> new ItemGrenade(new Item.Properties(), -1),
            ItemModelType.GENERATED);
    public static final RegistryObject<Item> NUCLEAR_WASTE_PEARL = register(PARTS_TAB, "nuclear_waste_pearl",
            () -> new ItemGrenade(new Item.Properties(), -1));

    public static final RegistryObject<Item> TRINITITE = register("trinitite",
            () -> new ItemNuclearWaste(new Item.Properties().stacksTo(64)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FRAGMENT_METEORITE = register(PARTS_TAB, "fragment_meteorite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FRAGMENT_COLTAN = register(PARTS_TAB, "fragment_coltan",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FRAGMENT_COBALT = register(PARTS_TAB, "fragment_cobalt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FRAGMENT_NEODYMIUM = register(PARTS_TAB, "fragment_neodymium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FRAGMENT_NIOBIUM = register(PARTS_TAB, "fragment_niobium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FRAGMENT_CERIUM = register(PARTS_TAB, "fragment_cerium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FRAGMENT_LANTHANIUM = register(PARTS_TAB, "fragment_lanthanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FRAGMENT_ACTINIUM = register(PARTS_TAB, "fragment_actinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FRAGMENT_BORON = register(PARTS_TAB, "fragment_boron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CHUNK_ORE = register("chunk_ore",
            () -> new ItemEnumMulti<>(new Item.Properties().stacksTo(64), HBMEnums.EnumChunkType.class, true),
            ItemModelType.ENUM_ITEM,
            HBMEnums.EnumChunkType.class);

    public static final RegistryObject<Item> FALLOUT = register(PARTS_TAB, "fallout",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> REACHER = register(CONTROL_TAB, "reacher",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GEM_VOLCANIC = register(PARTS_TAB, "gem_volcanic",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BURNT_BARK = register(PARTS_TAB, "burnt_bark",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GEM_RAD = register(PARTS_TAB, "gem_rad",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLOWTORCH = register(PARTS_TAB, "blowtorch",
            () -> new ItemBlowtorch(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ACETYLENE_TORCH = register(PARTS_TAB, "acetylene_torch",
            () -> new ItemBlowtorch(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FLUID_BARREL_INFINITE = register(PARTS_TAB, "fluid_barrel_infinite",
            () -> new ItemInfiniteFluid(new Item.Properties(), null, 1_000_000_000), ItemModelType.GENERATED);
    public static final RegistryObject<Item> INF_WATER_MK2 = register(PARTS_TAB, "inf_water_mk2",
            () -> new ItemInfiniteFluid(new Item.Properties(), null, 500), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FLUID_DUCT = register(MACHINE_TAB, "storage/fluid_duct",
            () -> new ItemFluidDuct(ModBlocks.FLUID_DUCT.get(), new Item.Properties()));
    public static final RegistryObject<Item> FLUID_DUCT_SILVER = register(MACHINE_TAB, "storage/fluid_duct_silver",
            () -> new ItemFluidDuct(ModBlocks.FLUID_DUCT_SILVER.get(), new Item.Properties()));
    public static final RegistryObject<Item> FLUID_DUCT_COLORED = register(MACHINE_TAB, "storage/fluid_duct_colored",
            () -> new ItemFluidDuct(ModBlocks.FLUID_DUCT_COLORED.get(), new Item.Properties()));

    public static final RegistryObject<Item> FLUID_DUCT_BOX_SILVER = register(MACHINE_TAB, "storage/fluid_duct_box_silver",
            () -> new ItemFluidDuctBox(ModBlocks.FLUID_DUCT_BOX_SILVER.get(), new Item.Properties()),
            ItemModelType.NONE,
            "storage/fluid_duct_box_silver_end");

    public static final RegistryObject<Item> FLUID_DUCT_BOX_COPPER = register(MACHINE_TAB, "storage/fluid_duct_box_copper",
            () -> new ItemFluidDuctBox(ModBlocks.FLUID_DUCT_BOX_COPPER.get(), new Item.Properties()),
            ItemModelType.NONE,
            "storage/fluid_duct_box_copper_end");

    public static final RegistryObject<Item> FLUID_DUCT_BOX_WHITE = register(MACHINE_TAB, "storage/fluid_duct_box_white",
            () -> new ItemFluidDuctBox(ModBlocks.FLUID_DUCT_BOX_WHITE.get(), new Item.Properties()),
            ItemModelType.NONE,
            "storage/fluid_duct_box_white_end");

    public static final RegistryObject<Item> FLUID_DUCT_EXHAUST = register(MACHINE_TAB, "storage/fluid_duct_exhaust",
            () -> new ItemFluidDuctBox(ModBlocks.FLUID_DUCT_EXHAUST.get(), new Item.Properties()),
            ItemModelType.NONE,
            "storage/fluid_duct_exhaust_end");

    public static final RegistryObject<Item> UPGRADE_TEMPLATE = register(CONTROL_TAB, "upgrade/upgrade_template",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_SPEED_1 = register(CONTROL_TAB, "upgrade/upgrade_speed_1",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.SPEED, 1), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_SPEED_2 = register(CONTROL_TAB, "upgrade/upgrade_speed_2",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.SPEED, 2), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_SPEED_3 = register(CONTROL_TAB, "upgrade/upgrade_speed_3",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.SPEED, 3), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_EFFECT_1 = register(CONTROL_TAB, "upgrade/upgrade_effect_1",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.EFFECT, 1), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_EFFECT_2 = register(CONTROL_TAB, "upgrade/upgrade_effect_2",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.EFFECT, 2), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_EFFECT_3 = register(CONTROL_TAB, "upgrade/upgrade_effect_3",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.EFFECT, 3), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_POWER_1 = register(CONTROL_TAB, "upgrade/upgrade_power_1",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.POWER, 1), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_POWER_2 = register(CONTROL_TAB, "upgrade/upgrade_power_2",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.POWER, 2), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_POWER_3 = register(CONTROL_TAB, "upgrade/upgrade_power_3",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.POWER, 3), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_FORTUNE_1 = register(CONTROL_TAB, "upgrade/upgrade_fortune_1",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.FORTUNE, 1), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_FORTUNE_2 = register(CONTROL_TAB, "upgrade/upgrade_fortune_2",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.FORTUNE, 2), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_FORTUNE_3 = register(CONTROL_TAB, "upgrade/upgrade_fortune_3",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.FORTUNE, 3), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_AFTERBURN_1 = register(CONTROL_TAB, "upgrade/upgrade_afterburn_1",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.AFTERBURN, 1), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_AFTERBURN_2 = register(CONTROL_TAB, "upgrade/upgrade_afterburn_2",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.AFTERBURN, 2), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_AFTERBURN_3 = register(CONTROL_TAB, "upgrade/upgrade_afterburn_3",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.AFTERBURN, 3), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_OVERDRIVE_1 = register(CONTROL_TAB, "upgrade/upgrade_overdrive_1",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.OVERDRIVE, 1), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_OVERDRIVE_2 = register(CONTROL_TAB, "upgrade/upgrade_overdrive_2",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.OVERDRIVE, 2), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_OVERDRIVE_3 = register(CONTROL_TAB, "upgrade/upgrade_overdrive_3",
            () -> new ItemMachineUpgrade(new Item.Properties(), ItemMachineUpgrade.UpgradeType.OVERDRIVE, 3), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_RADIUS = register(CONTROL_TAB, "upgrade/upgrade_radius",
            () -> new ItemMachineUpgrade(new Item.Properties().stacksTo(16)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_HEALTH = register(CONTROL_TAB, "upgrade/upgrade_health",
            () -> new ItemMachineUpgrade(new Item.Properties().stacksTo(16)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_SMELTER = register(CONTROL_TAB, "upgrade/upgrade_smelter",
            () -> new ItemMachineUpgrade(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_SHREDDER = register(CONTROL_TAB, "upgrade/upgrade_shredder",
            () -> new ItemMachineUpgrade(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_CENTRIFUGE = register(CONTROL_TAB, "upgrade/upgrade_centrifuge",
            () -> new ItemMachineUpgrade(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_CRYSTALLIZER = register(CONTROL_TAB, "upgrade/upgrade_crystallizer",
            () -> new ItemMachineUpgrade(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_NULLIFIER = register(CONTROL_TAB, "upgrade/upgrade_nullifier",
            () -> new ItemMachineUpgrade(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_SCREM = register(CONTROL_TAB, "upgrade/upgrade_screm",
            () -> new ItemMachineUpgrade(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_GC_SPEED = register(CONTROL_TAB, "upgrade/upgrade_gc_speed",
            () -> new ItemMachineUpgrade(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> UPGRADE_5G = register(CONTROL_TAB, "upgrade/upgrade_5g",
            () -> new ItemMachineUpgrade(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SOLID_FUEL = register(PARTS_TAB, "solid_fuel",
            () -> new FuelItem(new Item.Properties(), 3200), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SOLID_FUEL_PRESTO = register(PARTS_TAB, "solid_fuel_presto",
            () -> new FuelItem(new Item.Properties(), 8000), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SOLID_FUEL_PRESTO_TRIPLET = register(PARTS_TAB, "solid_fuel_presto_triplet",
            () -> new FuelItem(new Item.Properties(), 40000), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SOLID_FUEL_BF = register(PARTS_TAB, "solid_fuel_bf",
            () -> new FuelItem(new Item.Properties(), 32000), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SOLID_FUEL_PRESTO_BF = register(PARTS_TAB, "solid_fuel_presto_bf",
            () -> new FuelItem(new Item.Properties(), 80000), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SOLID_FUEL_PRESTO_TRIPLET_BF = register(PARTS_TAB, "solid_fuel_presto_triplet_bf",
            () -> new FuelItem(new Item.Properties(), 400000), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROCKET_FUEL = register(PARTS_TAB, "rocket_fuel",
            () -> new FuelItem(new Item.Properties(), 6400), ItemModelType.GENERATED);

    public static final RegistryObject<Item> DUST = register(PARTS_TAB, "powder_dust",
            () -> new FuelItem(new Item.Properties(), 25), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MACHINE_ASHPIT = register(MACHINE_TAB, "machines/machine_ashpit",
            () -> new AshpitItem(ModBlocks.MACHINE_ASHPIT.get(), new Item.Properties()));

    public static final RegistryObject<Item> HEATER_FIREBOX = register(MACHINE_TAB, "machines/heater_firebox",
            () -> new HeaterFireboxItem(ModBlocks.HEATER_FIREBOX.get(), new Item.Properties()));

    public static final RegistryObject<Item> HEATER_OVEN = register(MACHINE_TAB, "machines/heater_oven",
            () -> new HeaterOvenItem(ModBlocks.HEATER_OVEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> HEATER_OILBURNER = register(MACHINE_TAB, "machines/heater_oilburner",
            () -> new HeaterOilburnerItem(ModBlocks.HEATER_OILBURNER.get(), new Item.Properties()));

    public static final RegistryObject<Item> SCREWDRIVER = register(CONTROL_TAB, "tools/screwdriver",
            () -> new ItemTooling(IToolable.ToolType.SCREWDRIVER, 100), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SCREWDRIVER_DESH = register(CONTROL_TAB, "tools/screwdriver_desh",
            () -> new ItemTooling(IToolable.ToolType.SCREWDRIVER, 0), ItemModelType.GENERATED);

    public static final RegistryObject<Item> TANK_STEEL = register(PARTS_TAB, "tank_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MOTOR = register(PARTS_TAB, "motor",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MOTOR_DESH = register(PARTS_TAB, "motor_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MOTOR_BISMUTH = register(PARTS_TAB, "motor_bismuth",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PIPE_COPPER = register(PARTS_TAB, "pipe_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PIPE_IRON = register(PARTS_TAB, "pipe_iron",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PIPE_ALUMINIUM = register(PARTS_TAB, "pipe_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PIPE_LEAD = register(PARTS_TAB, "pipe_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PIPE_STEEL = register(PARTS_TAB, "pipe_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PIPE_DURA_STEEL = register(PARTS_TAB, "pipe_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PIPE_RUBBER = register(PARTS_TAB, "pipe_rubber",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SHELL_COPPER = register(PARTS_TAB, "shell_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SHELL_ALUMINIUM = register(PARTS_TAB, "shell_aluminium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SHELL_TITANIUM = register(PARTS_TAB, "shell_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SHELL_STEEL = register(PARTS_TAB, "shell_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SHELL_WEAPON_STEEL = register(PARTS_TAB, "shell_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SHELL_SATURNITE = register(PARTS_TAB, "shell_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_GOLD = register(PARTS_TAB, "wire_dense_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_SCHRABIDIUM = register(PARTS_TAB, "wire_dense_schrabidium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_SCHRABIDATE = register(PARTS_TAB, "wire_dense_schrabidate",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_TITANIUM = register(PARTS_TAB, "wire_dense_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_COPPER = register(PARTS_TAB, "wire_dense_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_TUNGSTEN = register(PARTS_TAB, "wire_dense_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_NEODYMIUM = register(PARTS_TAB, "wire_dense_neodymium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_NIOBIUM = register(PARTS_TAB, "wire_dense_niobium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_RED_COPPER = register(PARTS_TAB, "wire_dense_red_copper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_ADVANCED_ALLOY = register(PARTS_TAB, "wire_dense_advanced_alloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_STARMETAL = register(PARTS_TAB, "wire_dense_starmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_BSCCO = register(PARTS_TAB, "wire_dense_bscco",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_MAGTUNG = register(PARTS_TAB, "wire_dense_magnetized_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRE_DENSE_DNT = register(PARTS_TAB, "wire_dense_dnt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FURNACE_STEEL = register(MACHINE_TAB, "machines/furnace_steel",
            () -> new FurnaceSteelItem(ModBlocks.FURNACE_STEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> FURNACE_IRON = register(MACHINE_TAB, "machines/furnace_iron",
            () -> new FurnaceIronItem(ModBlocks.FURNACE_IRON.get(), new Item.Properties()));
    public static final RegistryObject<Item> FURNACE_COMBINATION = register(MACHINE_TAB, "machines/furnace_combination",
            () -> new FurnaceCombinationItem(ModBlocks.FURNACE_COMBINATION.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_SAWMILL = register(MACHINE_TAB, "machines/sawmill",
            () -> new SawmillItem(ModBlocks.MACHINE_SAWMILL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_HEAT_BOILER = register(MACHINE_TAB, "machines/heat_boiler",
            () -> new BoilerItem(ModBlocks.MACHINE_HEAT_BOILER.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_AUTOSAW = register(MACHINE_TAB, "machines/autosaw",
            () -> new AutosawItem(ModBlocks.MACHINE_AUTOSAW.get(), new Item.Properties()));

    public static final RegistryObject<Item> OIL_TAR_CRUDE = register(PARTS_TAB,"oil_tar_crude",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> OIL_TAR_CRACK = register(PARTS_TAB,"oil_tar_crack",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> OIL_TAR_COAL = register(PARTS_TAB,"oil_tar_coal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> OIL_TAR_WOOD = register(PARTS_TAB,"oil_tar_wood",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> OIL_TAR_WAX = register(PARTS_TAB,"oil_tar_wax",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> OIL_TAR_PARAFFIN = register(PARTS_TAB,"oil_tar_paraffin",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GEM_SODALITE = register(PARTS_TAB, "gem_sodalite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAWBLADE = register(PARTS_TAB, "sawblade",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BETA = register(PARTS_TAB, "beta",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> DETONATOR_DEADMAN = register(PARTS_TAB, "detonator_deadman",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> DETONATOR_DE = register(PARTS_TAB, "detonator_de",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CELL_ANTIMATTER = register(PARTS_TAB, "cell_antimatter",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CELL_ANTI_SCHRABIDIUM = register(PARTS_TAB, "cell_antischrabidium",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CELL_EMPTY = register(PARTS_TAB, "cell_empty",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SINGULARITY = register(PARTS_TAB, "singularity",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SINGULARITY_COUNTER_RESONANT = register(PARTS_TAB, "singularity_counter_resonant",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SINGULARITY_SUPER_HEATED = register(PARTS_TAB, "singularity_super_heated",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLACK_HOLE = register(PARTS_TAB, "black_hole",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SINGULARITY_SPARK = register(PARTS_TAB, "singularity_spark",
            () -> new ItemDrop(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CUSTOM_TNT = register(PARTS_TAB, "custom_tnt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CUSTOM_NUKE = register(PARTS_TAB, "custom_nuke",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CELL_DEUTERIUM = register(PARTS_TAB, "cell_deuterium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CELL_TRITIUM = register(PARTS_TAB, "cell_tritium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CELL_UF6 = register(PARTS_TAB, "cell_uf6",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CELL_PUF6 = register(PARTS_TAB, "cell_puf6",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LITHIUM = register(PARTS_TAB, "lithium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> TRITIUM_DEUTERIUM_CAKE = register(PARTS_TAB, "tritium_deuterium_cake",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CUSTOM_HYDRO = register(PARTS_TAB, "custom_hydro",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CUSTOM_AMAT = register(PARTS_TAB, "custom_amat",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> EGG_BALEFIRE_SHARD = register(PARTS_TAB, "egg_balefire_shard",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> EGG_BALEFIRE = register(PARTS_TAB, "egg_balefire",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CUSTOM_DIRTY = register(PARTS_TAB, "custom_dirty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CELL_SAS3 = register(PARTS_TAB, "cell_sas3",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CUSTOM_SCHRAB = register(PARTS_TAB, "custom_schrab",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUCLEAR_WASTE = register(PARTS_TAB, "nuclear_waste",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CUSTOM_FALL = register(PARTS_TAB, "custom_fall",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GEM_ALEXANDRITE = register(PARTS_TAB, "gem_alexandrite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> OIL_DETECTOR = register(CONTROL_TAB, "oil_detector",
            () -> new ItemOilDetector(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BROADCASTER_PC = register(MACHINE_TAB, "broadcaster_pc",
            () -> new BroadcasterItem(ModBlocks.BROADCASTER_PC.get(), new Item.Properties()));

    public static final RegistryObject<Item> RADIOREC = register(MACHINE_TAB, "radiorec",
            () -> new RadiorecItem(ModBlocks.RADIOREC.get(), new Item.Properties()));

    public static final RegistryObject<Item> RADIOBOX = register(MACHINE_TAB, "radiobox",
            () -> new RadiorecItem(ModBlocks.RADIOBOX.get(), new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_ARMOR_TABLE = register(MACHINE_TAB, "machines/machine_armor_table",
            () -> new BlockItem(ModBlocks.MACHINE_ARMOR_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_WEAPON_TABLE = register(MACHINE_TAB, "machines/machine_weapon_table",
            () -> new BlockItem(ModBlocks.MACHINE_WEAPON_TABLE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_DIESEL = register(MACHINE_TAB, "machines/machine_diesel",
            () -> new MachineDieselItem(ModBlocks.MACHINE_DIESEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_GAS_CENT = register(MACHINE_TAB, "machines/machine_gas_cent",
            () -> new MachineCentrifugeItem(ModBlocks.MACHINE_GAS_CENT.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_CENTRIFUGE = register(MACHINE_TAB, "machines/machine_centrifuge",
            () -> new MachineCentrifugeItem(ModBlocks.MACHINE_CENTRIFUGE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_CRYSTALLIZER = register(MACHINE_TAB, "machines/machine_crystallizer",
            () -> new MachineCrystallizerItem(ModBlocks.MACHINE_CRYSTALLIZER.get(), new Item.Properties()));
    public static final RegistryObject<Item> SPOTLIGHT_INCANDESCENT_ITEM = register(MACHINE_TAB, "spotlight_incandescent",
            () -> new SimpleOBJItem(ModBlocks.SPOTLIGHT_INCANDESCENT.get(), new Item.Properties()));
    public static final RegistryObject<Item> SPOTLIGHT_INCANDESCENT_OFF_ITEM = register(MACHINE_TAB, "spotlight_incandescent_off",
            () -> new SimpleOBJItem(ModBlocks.SPOTLIGHT_INCANDESCENT_OFF.get(), new Item.Properties()));
    public static final RegistryObject<Item> SPOTLIGHT_FLUORO_ITEM = register(MACHINE_TAB, "spotlight_fluoro",
            () -> new SimpleOBJItem(ModBlocks.SPOTLIGHT_FLUORO.get(), new Item.Properties()));
    public static final RegistryObject<Item> SPOTLIGHT_FLUORO_OFF_ITEM = register(MACHINE_TAB, "spotlight_fluoro_off",
            () -> new SimpleOBJItem(ModBlocks.SPOTLIGHT_FLUORO_OFF.get(), new Item.Properties()));
    public static final RegistryObject<Item> SPOTLIGHT_HALOGEN_ITEM = register(MACHINE_TAB, "spotlight_halogen",
            () -> new SimpleOBJItem(ModBlocks.SPOTLIGHT_HALOGEN.get(), new Item.Properties()));
    public static final RegistryObject<Item> SPOTLIGHT_HALOGEN_OFF_ITEM = register(MACHINE_TAB, "spotlight_halogen_off",
            () -> new SimpleOBJItem(ModBlocks.SPOTLIGHT_HALOGEN_OFF.get(), new Item.Properties()));
    public static final RegistryObject<Item> FLOODLIGHT_ITEM = register(MACHINE_TAB, "floodlight",
            () -> new SimpleOBJItem(ModBlocks.FLOODLIGHT.get(), new Item.Properties()));
    public static final RegistryObject<Item> CARGO_CONTAINER = register(MACHINE_TAB, "storage/cargo_container",
            () -> new CargoContainerItem(ModBlocks.CARGO_CONTAINER.get(), new Item.Properties()));
    public static final RegistryObject<Item> PUMP_STEAM = register(MACHINE_TAB, "machines/pump_steam",
            () -> new MachinePumpItem(ModBlocks.PUMP_STEAM.get(), new Item.Properties()));
    public static final RegistryObject<Item> PUMP_ELECTRIC = register(MACHINE_TAB, "machines/pump_electric",
            () -> new MachinePumpItem(ModBlocks.PUMP_ELECTRIC.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_STIRLING_ITEM = register(MACHINE_TAB, "machines/machine_stirling",
            () -> new MachineStirlingItem(ModBlocks.MACHINE_STIRLING.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_STIRLING_STEEL_ITEM = register(MACHINE_TAB, "machines/machine_stirling_steel",
            () -> new MachineStirlingItem(ModBlocks.MACHINE_STIRLING_STEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_STIRLING_CREATIVE_ITEM = register(MACHINE_TAB, "machines/machine_stirling_creative",
            () -> new MachineStirlingItem(ModBlocks.MACHINE_STIRLING_CREATIVE.get(), new Item.Properties()));
    public static final RegistryObject<Item> MACHINE_STEAM_ENGINE_ITEM = register(MACHINE_TAB, "machines/machine_steam_engine",
            () -> new MachineSteamEngineItem(ModBlocks.MACHINE_STEAM_ENGINE.get(), new Item.Properties()));
    public static final RegistryObject<Item> RED_CONNECTOR = register(MACHINE_TAB,"network/red_connector",
                    () -> new ItemConnectorRedWire(ModBlocks.RED_CONNECTOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> FOUNDRY_MOLD = register(MACHINE_TAB,"network/foundry_mold",
            () -> new ItemFoundryMold(ModBlocks.FOUNDRY_MOLD.get(), new Item.Properties()));
    public static final RegistryObject<Item> FOUNDRY_BASIN = register(MACHINE_TAB,"network/foundry_basin",
            () -> new ItemFoundryBasin(ModBlocks.FOUNDRY_BASIN.get(), new Item.Properties()));
    public static final RegistryObject<Item> FOUNDRY_CHANNEL = register(MACHINE_TAB,"network/foundry_channel",
            () -> new ItemFoundryChannel(ModBlocks.FOUNDRY_CHANNEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> FOUNDRY_OUTLET = register(MACHINE_TAB,"network/foundry_outlet",
            () -> new ItemFoundryOutlet(ModBlocks.FOUNDRY_OUTLET.get(), new Item.Properties()));
    public static final RegistryObject<Item> FOUNDRY_SLAGTAP = register(MACHINE_TAB,"network/foundry_slagtap",
            () -> new ItemFoundryOutlet(ModBlocks.FOUNDRY_SLAGTAP.get(), new Item.Properties()));

    public static final RegistryObject<Item> DEFUSER = register(NUKE_TAB, "defuser",
            () -> new ItemTooling(IToolable.ToolType.DEFUSER, 100), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MINE_AP = register(WEAPON_TAB, "bomb/mine/mine_ap",
            () -> new LandmineItem(ModBlocks.MINE_AP.get(), new Item.Properties()));
    public static final RegistryObject<Item> MINE_SHRAP = register(WEAPON_TAB, "bomb/mine/mine_shrap",
            () -> new LandmineItem(ModBlocks.MINE_SHRAP.get(), new Item.Properties()));
    public static final RegistryObject<Item> MINE_FAT = register(WEAPON_TAB, "bomb/mine/mine_fat",
            () -> new LandmineItem(ModBlocks.MINE_FAT.get(), new Item.Properties()));
    public static final RegistryObject<Item> MINE_NAVAL = register(WEAPON_TAB, "bomb/mine/mine_naval",
            () -> new LandmineItem(ModBlocks.MINE_NAVAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> MINE_HE = register(WEAPON_TAB, "bomb/mine/mine_he",
            () -> new LandmineItem(ModBlocks.MINE_HE.get(), new Item.Properties()));

    public static final RegistryObject<Item> STEEL_POLES = register(MACHINE_TAB, "deco/steel_poles",
            () -> new SimpleOBJItem(ModBlocks.STEEL_POLES.get(), new Item.Properties()));
    public static final RegistryObject<Item> TAPE_RECORDER = register(MACHINE_TAB, "deco/tape_recorder",
            () -> new SimpleOBJItem(ModBlocks.TAPE_RECORDER.get(), new Item.Properties()));
    public static final RegistryObject<Item> POLE_TOP = register(MACHINE_TAB, "deco/pole_top",
            () -> new BlockItem(ModBlocks.POLE_TOP.get(), new Item.Properties()));
    public static final RegistryObject<Item> POLE_SATELLITE_RECEIVER = register(MACHINE_TAB, "deco/pole_satellite_receiver",
            () -> new DecoBlockItem(ModBlocks.POLE_SATELLITE_RECEIVER.get(), new Item.Properties()));

    public static final RegistryObject<Item> CREEPER_TAINTED_SPAWN_EGG = register(CONSUMABLE_TAB, "creeper_tainted_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CREEPER_TAINTED, 0x4A6A3B, 0x8A2BE2, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> CREEPER_NUCLEAR_SPAWN_EGG = register(CONSUMABLE_TAB, "creeper_nuclear_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CREEPER_NUCLEAR, 0x4CFF4C, 0xFFFF00, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> CREEPER_GOLD_SPAWN_EGG = register(CONSUMABLE_TAB, "creeper_gold_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CREEPER_GOLD, 0xFFD700, 0xB8860B, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> CREEPER_PHOSGENE_SPAWN_EGG = register(CONSUMABLE_TAB, "creeper_phosgene_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CREEPER_PHOSGENE, 0x8FBC8F, 0x6B8E23, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> CREEPER_VOLATILE_SPAWN_EGG = register(CONSUMABLE_TAB, "creeper_volatile_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.CREEPER_VOLATILE, 0xCD5C5C, 0x8B3A3A, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> FBI_SPAWN_EGG = register(CONSUMABLE_TAB, "fbi_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FBI, 0x2B2B2B, 0x8B0000, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> FBI_DRONE_SPAWN_EGG = register(CONSUMABLE_TAB, "fbi_drone_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.FBI_DRONE, 0x4A4A4A, 0x00CED1, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> UFO_SPAWN_EGG = register(CONSUMABLE_TAB, "ufo_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.UFO, 0x2F4F4F, 0x9370DB, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> MASKMAN_SPAWN_EGG = register(CONSUMABLE_TAB, "maskman_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.MASKMAN, 0x1A1A1A, 0xFF4500, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GLYPHID_SPAWN_EGG = register(CONSUMABLE_TAB, "glyphid_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GLYPHID, 0x4A6A3B, 0x8A2BE2, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GLYPHID_DIGGER_SPAWN_EGG = register(CONSUMABLE_TAB, "glyphid_digger_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GLYPHID_DIGGER, 0x6B8E23, 0x8B4513, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GLYPHID_NUCLEAR_SPAWN_EGG = register(CONSUMABLE_TAB, "glyphid_nuclear_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GLYPHID_NUCLEAR, 0x4CFF4C, 0xFF4500, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GLYPHID_SCOUT_SPAWN_EGG = register(CONSUMABLE_TAB, "glyphid_scout_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GLYPHID_SCOUT, 0x8FBC8F, 0x00CED1, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GLYPHID_BRAWLER_SPAWN_EGG = register(CONSUMABLE_TAB, "glyphid_brawler_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GLYPHID_BRAWLER, 0x8B0000, 0x2F4F4F, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GLYPHID_BOMBARDIER_SPAWN_EGG = register(CONSUMABLE_TAB, "glyphid_bombardier_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GLYPHID_BOMBARDIER, 0xCD853F, 0x8B0000, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GLYPHID_BLASTER_SPAWN_EGG = register(CONSUMABLE_TAB, "glyphid_blaster_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GLYPHID_BLASTER, 0x1A1A1A, 0xFF4500, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GLYPHID_BEHEMOTH_SPAWN_EGG = register(CONSUMABLE_TAB, "glyphid_behemoth_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GLYPHID_BEHEMOTH, 0x556B2F, 0x8B4513, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GLYPHID_BRENDA_SPAWN_EGG = register(CONSUMABLE_TAB, "glyphid_brenda_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GLYPHID_BRENDA, 0x9370DB, 0x8B008B, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> DUCK_SPAWN_EGG = register(CONSUMABLE_TAB, "duck_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.DUCK, 0x8B6914, 0xFFD700, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> QUACKOS_SPAWN_EGG = register(CONSUMABLE_TAB, "quackos_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.QUACKOS, 0x8B6914, 0xFF4500, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> GHOST_SPAWN_EGG = register(CONSUMABLE_TAB, "ghost_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.GHOST, 0x808080, 0xC0C0C0, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> RADBEAST_SPAWN_EGG = register(CONSUMABLE_TAB, "radbeast_spawn_egg",
            () -> new ForgeSpawnEggItem(ModEntities.RADBEAST, 0x004000, 0x00FF00, new Item.Properties()),
            ItemModelType.SPAWN_EGG);

    public static final RegistryObject<Item> STICK_TNT = register(PARTS_TAB, "stick_tnt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STICK_SEMTEX = register(PARTS_TAB, "stick_semtex",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> STICK_C4 = register(PARTS_TAB, "stick_c4",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CARD_AOS = register(PARTS_TAB, "card_aos",
            () -> new ItemModCard(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CARD_QOS = register(PARTS_TAB, "card_qos",
            () -> new ItemModCard(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CLADDING_IRON = register(PARTS_TAB, "cladding_iron",
            () -> new ItemModIron(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CLADDING_OBSIDIAN = register(PARTS_TAB, "cladding_obsidian",
            () -> new ItemModObsidian(new Item.Properties().stacksTo(64)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CLADDING_PAINT = register(PARTS_TAB, "cladding_paint",
            () -> new ItemModCladding(0.025),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CLADDING_RUBBER = register(PARTS_TAB, "cladding_rubber",
            () -> new ItemModCladding(0.005),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CLADDING_LEAD = register(PARTS_TAB, "cladding_lead",
            () -> new ItemModCladding(0.1),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CLADDING_DESH = register(PARTS_TAB, "cladding_desh",
            () -> new ItemModCladding(0.2),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CLADDING_GHIORSIUM = register(PARTS_TAB, "cladding_ghiorsium",
            () -> new ItemModCladding(0.5),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE_CAP = register(CONSUMABLE_TAB, "bottle_cap",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLOCK_COKE = register("block_coke",
            () -> new ItemBlockCoke(ModBlocks.BLOCK_COKE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ANCIENT_SCRAP = register(BLOCK_TAB, "ancient_scrap",
            () -> new BlockItem(ModBlocks.ANCIENT_SCRAP.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_CORIUM_COBBLE = register(BLOCK_TAB, "block_corium_cobble",
            () -> new BlockItem(ModBlocks.BLOCK_CORIUM_COBBLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_ASBESTOS = register(BLOCK_TAB, "block_asbestos",
            () -> new BlockItem(ModBlocks.BLOCK_ASBESTOS.get(), new Item.Properties()));

    public static final RegistryObject<Item> DECO_ASBESTOS = register(BLOCK_TAB, "deco/deco_asbestos",
            () -> new BlockItem(ModBlocks.DECO_ASBESTOS.get(), new Item.Properties()));

    public static final RegistryObject<Item> BRICK_ASBESTOS = register(BLOCK_TAB, "brick_asbestos",
            () -> new BlockItem(ModBlocks.BRICK_ASBESTOS.get(), new Item.Properties()));

    public static final RegistryObject<Item> TILE_LAB = register(BLOCK_TAB, "tile_lab",
            () -> new BlockItem(ModBlocks.TILE_LAB.get(), new Item.Properties()));

    public static final RegistryObject<Item> TILE_LAB_CRACKED = register(BLOCK_TAB, "tile_lab_cracked",
            () -> new BlockItem(ModBlocks.TILE_LAB_CRACKED.get(), new Item.Properties()));

    public static final RegistryObject<Item> TILE_LAB_BROKEN = register(BLOCK_TAB, "tile_lab_broken",
            () -> new BlockItem(ModBlocks.TILE_LAB_BROKEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_COAL_OIL_BURNING = register(BLOCK_TAB, "ore_coal_oil_burning",
            () -> new BlockItem(ModBlocks.ORE_COAL_OIL_BURNING.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_NETHER_COAL = register(BLOCK_TAB, "ore_nether_coal",
            () -> new BlockItem(ModBlocks.ORE_NETHER_COAL.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_COLTAN = register(BLOCK_TAB, "ore_coltan",
            () -> new BlockItem(ModBlocks.ORE_COLTAN.get(), new Item.Properties()));

    public static final RegistryObject<Item> BRICK_JUNGLE_OOZE = register(BLOCK_TAB, "brick_jungle_ooze",
            () -> new BlockItem(ModBlocks.BRICK_JUNGLE_OOZE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BRICK_JUNGLE_MYSTIC = register(BLOCK_TAB, "brick_jungle_mystic",
            () -> new BlockItem(ModBlocks.ORE_COLTAN.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_NETHER_COBALT = register(BLOCK_TAB, "ore_nether_cobalt",
            () -> new BlockItem(ModBlocks.ORE_NETHER_COBALT.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_METEOR_MOLTEN = register(BLOCK_TAB, "block_meteor_molten",
            () -> new BlockItem(ModBlocks.BLOCK_METEOR_MOLTEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> IMPACT_DIRT = register(BLOCK_TAB, "impact_dirt",
            () -> new BlockItem(ModBlocks.IMPACT_DIRT.get(), new Item.Properties()));

    public static final RegistryObject<Item> MUSH = register(BLOCK_TAB, "mush",
            () -> new BlockItem(ModBlocks.MUSH.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAND_DIRTY = register(BLOCK_TAB, "sand_dirty",
            () -> new BlockItem(ModBlocks.SAND_DIRTY.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAND_BORON = register(BLOCK_TAB, "sand_boron",
            () -> new BlockItem(ModBlocks.SAND_BORON.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAND_DIRTY_RED = register(BLOCK_TAB, "sand_dirty_red",
            () -> new BlockItem(ModBlocks.SAND_DIRTY_RED.get(), new Item.Properties()));

    public static final RegistryObject<Item> NTM_DIRT = register(BLOCK_TAB, "ntm_dirt",
            () -> new BlockItem(ModBlocks.NTM_DIRT.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_FOAM = register(BLOCK_TAB, "block_foam",
            () -> new BlockItem(ModBlocks.BLOCK_FOAM.get(), new Item.Properties()));

    public static final RegistryObject<Item> WASTE_LEAVES = register(BLOCK_TAB, "waste_leaves",
            () -> new BlockItem(ModBlocks.WASTE_LEAVES.get(), new Item.Properties()));

    public static final RegistryObject<Item> TAINT = register(BLOCK_TAB, "taint",
            () -> new BlockItem(ModBlocks.TAINT.get(), new Item.Properties()));

    public static final RegistryObject<Item> WASTE_LOG = register(BLOCK_TAB, "waste_log",
            () -> new BlockItem(ModBlocks.WASTE_LOG.get(), new Item.Properties()));

    public static final RegistryObject<Item> WASTE_PLANKS = register(BLOCK_TAB, "waste_planks",
            () -> new BlockItem(ModBlocks.WASTE_PLANKS.get(), new Item.Properties()));

    public static final RegistryObject<Item> PINK_LOG = register(BLOCK_TAB, "pink_log",
            () -> new BlockItem(ModBlocks.PINK_LOG.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLYPHID_BASE = register(BLOCK_TAB, "glyphid_base",
            () -> new BlockItem(ModBlocks.GLYPHID_BASE.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLYPHID_SPAWNER = register(BLOCK_TAB, "glyphid_spawner",
            () -> new BlockItem(ModBlocks.GLYPHID_SPAWNER.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_NETHER_URANIUM = register(BLOCK_TAB, "ore_nether_uranium",
            () -> new BlockItem(ModBlocks.ORE_NETHER_URANIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_NETHER_SCHRABIDIUM = register(BLOCK_TAB, "ore_nether_schrabidium",
            () -> new BlockItem(ModBlocks.ORE_NETHER_SCHRABIDIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_NETHER_SULFUR = register(BLOCK_TAB, "ore_nether_sulfur",
            () -> new BlockItem(ModBlocks.ORE_NETHER_SULFUR.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_SELLAFIELD_DIAMOND = register(BLOCK_TAB, "ore_sellafield_diamond",
            () -> new BlockItem(ModBlocks.ORE_SELLAFIELD_DIAMOND.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_SELLAFIELD_EMERALD = register(BLOCK_TAB, "ore_sellafield_emerald",
            () -> new BlockItem(ModBlocks.ORE_SELLAFIELD_EMERALD.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_SELLAFIELD_SCHRABIDIUM = register(BLOCK_TAB, "ore_sellafield_schrabidium",
            () -> new BlockItem(ModBlocks.ORE_SELLAFIELD_SCHRABIDIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_SELLAFIELD_RADGEM = register(BLOCK_TAB, "ore_sellafield_radgem",
            () -> new BlockItem(ModBlocks.ORE_SELLAFIELD_RADGEM.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_NETHER_URANIUM_SCORCHED = register(BLOCK_TAB, "ore_nether_uranium_scorched",
            () -> new BlockItem(ModBlocks.ORE_NETHER_URANIUM_SCORCHED.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_SELLAFIELD_URANIUM_SCORCHED = register(BLOCK_TAB, "ore_sellafield_uranium_scorched",
            () -> new BlockItem(ModBlocks.ORE_SELLAFIELD_URANIUM_SCORCHED.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_BEDROCK_OIL = register(BLOCK_TAB, "ore_bedrock_oil",
            () -> new BlockItem(ModBlocks.ORE_BEDROCK_OIL.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_GNEISS_URANIUM = register(BLOCK_TAB, "ore_gneiss_uranium",
            () -> new BlockItem(ModBlocks.ORE_GNEISS_URANIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_NETHER_FIRE = register(BLOCK_TAB, "ore_nether_fire",
            () -> new BlockItem(ModBlocks.ORE_NETHER_FIRE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_METEOR_COBBLE = register(BLOCK_TAB, "block_meteor_cobble",
            () -> new BlockItem(ModBlocks.BLOCK_METEOR_COBBLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_METEOR_BROKEN = register(BLOCK_TAB, "block_meteor_broken",
            () -> new BlockItem(ModBlocks.BLOCK_METEOR_BROKEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_GNEISS_RARE = register(BLOCK_TAB, "ore_gneiss_rare",
            () -> new BlockItem(ModBlocks.ORE_GNEISS_RARE.get(), new Item.Properties()));

    public static final RegistryObject<Item> WASTE_TRINITITE = register(BLOCK_TAB, "waste_trinitite",
            () -> new BlockItem(ModBlocks.WASTE_TRINITITE.get(), new Item.Properties()));

    public static final RegistryObject<Item> WASTE_TRINITITE_RED = register(BLOCK_TAB, "waste_trinitite_red",
            () -> new BlockItem(ModBlocks.WASTE_TRINITITE_RED.get(), new Item.Properties()));

    public static final RegistryObject<Item> VOLCANO_CORE = register(BLOCK_TAB, "volcano_core",
            () -> new BlockItem(ModBlocks.VOLCANO_CORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> VOLCANO_RAD_CORE = register(BLOCK_TAB, "volcano_rad_core",
            () -> new BlockItem(ModBlocks.VOLCANO_RAD_CORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> STATUE_ELB_F = register(BLOCK_TAB, "deco/statue_elb_f",
            () -> new DecoBlockAltItem(ModBlocks.STATUE_ELB_F.get(), new Item.Properties()),
            ItemModelType.NONE,
            "deco/statue_elb_f");

    public static final RegistryObject<Item> PLASMA = register(BLOCK_TAB, "plasma",
            () -> new BlockItem(ModBlocks.PLASMA.get(), new Item.Properties()));

    public static final RegistryObject<Item> FROZEN_DIRT = register(BLOCK_TAB, "frozen_dirt",
            () -> new BlockItem(ModBlocks.FROZEN_DIRT.get(), new Item.Properties()));

    public static final RegistryObject<Item> FROZEN_LOG = register(BLOCK_TAB, "frozen_log",
            () -> new BlockItem(ModBlocks.FROZEN_LOG.get(), new Item.Properties()));

    public static final RegistryObject<Item> FROZEN_PLANKS = register(BLOCK_TAB, "frozen_planks",
            () -> new BlockItem(ModBlocks.FROZEN_PLANKS.get(), new Item.Properties()));

    public static final RegistryObject<Item> GRAVEL_OBSIDIAN = register(BLOCK_TAB, "gravel_obsidian",
            () -> new BlockItem(ModBlocks.GRAVEL_OBSIDIAN.get(), new Item.Properties()));

    public static final RegistryObject<Item> GAS_RADON = register(BLOCK_TAB, "gas_radon",
            () -> new BlockItem(ModBlocks.GAS_RADON.get(), new Item.Properties()));

    public static final RegistryObject<Item> GAS_RADON_DENSE = register(BLOCK_TAB, "gas_radon_dense",
            () -> new BlockItem(ModBlocks.GAS_RADON_DENSE.get(), new Item.Properties()));

    public static final RegistryObject<Item> GAS_RADON_TOMB = register(BLOCK_TAB, "gas_radon_tomb",
            () -> new BlockItem(ModBlocks.GAS_RADON_TOMB.get(), new Item.Properties()));

    public static final RegistryObject<Item> GAS_MONOXIDE = register(BLOCK_TAB, "gas_monoxide",
            () -> new BlockItem(ModBlocks.GAS_MONOXIDE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BRICK_CONCRETE = register(BLOCK_TAB, "brick_concrete",
            () -> new BlockItem(ModBlocks.BRICK_CONCRETE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONCRETE = register(BLOCK_TAB, "concrete",
            () -> new BlockItem(ModBlocks.CONCRETE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONCRETE_SMOOTH = register(BLOCK_TAB, "concrete_smooth",
            () -> new BlockItem(ModBlocks.CONCRETE_SMOOTH.get(), new Item.Properties()));

    public static final RegistryObject<Item> BRICK_CONCRETE_BROKEN = register(BLOCK_TAB, "brick_concrete_broken",
            () -> new BlockItem(ModBlocks.BRICK_CONCRETE_BROKEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> BALEFIRE = register(BLOCK_TAB, "balefire",
            () -> new BlockItem(ModBlocks.BALEFIRE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BRICK_LIGHT = register(BLOCK_TAB, "brick_light",
            () -> new BlockItem(ModBlocks.BRICK_LIGHT.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SCRAP = register(BLOCK_TAB, "block_scrap",
            () -> new FuelBlockItem(ModBlocks.BLOCK_SCRAP.get(), new Item.Properties(), 400));

    public static final RegistryObject<Item> BRICK_OBSIDIAN = register(BLOCK_TAB, "brick_obsidian",
            () -> new BlockItem(ModBlocks.BRICK_OBSIDIAN.get(), new Item.Properties()));

    public static final RegistryObject<Item> RED_CABLE = register(MACHINE_TAB, "red_cable",
            () -> new ItemRedCable(ModBlocks.RED_CABLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CABLE_SWITCH = register(MACHINE_TAB, "cable_switch",
            () -> new BlockItem(ModBlocks.CABLE_SWITCH.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_SCHRABIDIUM = register(BLOCK_TAB, "ore_schrabidium",
            () -> new BlockItem(ModBlocks.ORE_SCHRABIDIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_URANIUM_SCORCHED = register(BLOCK_TAB, "ore_uranium_scorched",
            () -> new BlockItem(ModBlocks.ORE_URANIUM_SCORCHED.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_GNEISS_SCHRABIDIUM = register(BLOCK_TAB, "ore_gneiss_schrabidium",
            () -> new BlockItem(ModBlocks.ORE_GNEISS_SCHRABIDIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_GNEISS_URANIUM_SCORCHED = register(BLOCK_TAB, "ore_gneiss_uranium_scorched",
            () -> new BlockItem(ModBlocks.ORE_GNEISS_URANIUM_SCORCHED.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_ELECTRICAL_SCRAP = register(BLOCK_TAB, "block_electrical_scrap",
            () -> new BlockItem(ModBlocks.BLOCK_ELECTRICAL_SCRAP.get(), new Item.Properties()));

    public static final RegistryObject<Item> FUSION_CONDUCTOR = register(BLOCK_TAB, "fusion_conductor",
            () -> new BlockItem(ModBlocks.FUSION_CONDUCTOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> FUSION_MOTOR = register(BLOCK_TAB, "fusion_motor",
            () -> new BlockItem(ModBlocks.FUSION_MOTOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> FUSION_HEATER = register(BLOCK_TAB, "fusion_heater",
            () -> new BlockItem(ModBlocks.FUSION_HEATER.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_WASTE = register(BLOCK_TAB, "block_waste",
            () -> new BlockItem(ModBlocks.BLOCK_WASTE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_WASTE_PAINTED = register(BLOCK_TAB, "block_waste_painted",
            () -> new BlockItem(ModBlocks.BLOCK_WASTE_PAINTED.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_WASTE_VITRIFIED = register(BLOCK_TAB, "block_waste_vitrified",
            () -> new BlockItem(ModBlocks.BLOCK_WASTE_VITRIFIED.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SCHRARANIUM = register(BLOCK_TAB, "block_schraranium",
            () -> new BlockItem(ModBlocks.BLOCK_SCHRARANIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SCHRABIDIUM = register(BLOCK_TAB, "block_schrabidium",
            () -> new BlockItem(ModBlocks.BLOCK_SCHRABIDIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SCHRABIDATE = register(BLOCK_TAB, "block_schrabidate",
            () -> new BlockItem(ModBlocks.BLOCK_SCHRABIDATE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SOLINIUM = register(BLOCK_TAB, "block_solinium",
            () -> new BlockItem(ModBlocks.BLOCK_SOLINIUM.get(), new Item.Properties()));

    // ==================== ПРЕДМЕТЫ ДЛЯ БЛОКОВ ====================

    public static final RegistryObject<Item> BLOCK_LANTHANIUM = register(BLOCK_TAB, "block_lanthanium",
            () -> new BlockItem(ModBlocks.BLOCK_LANTHANIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_RA226 = register(BLOCK_TAB, "block_ra226",
            () -> new BlockItem(ModBlocks.BLOCK_RA226.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_ACTINIUM = register(BLOCK_TAB, "block_actinium",
            () -> new BlockItem(ModBlocks.BLOCK_ACTINIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_COLTAN = register(BLOCK_TAB, "block_coltan",
            () -> new BlockItem(ModBlocks.BLOCK_COLTAN.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SMORE = register(BLOCK_TAB, "block_smore",
            () -> new BlockItem(ModBlocks.BLOCK_SMORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SEMTEX = register(BLOCK_TAB, "block_semtex",
            () -> new BlockItem(ModBlocks.BLOCK_SEMTEX.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_C4 = register(BLOCK_TAB, "block_c4",
            () -> new BlockItem(ModBlocks.BLOCK_C4.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_POLYMER = register(BLOCK_TAB, "block_polymer",
            () -> new BlockItem(ModBlocks.BLOCK_POLYMER.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_BAKELITE = register(BLOCK_TAB, "block_bakelite",
            () -> new BlockItem(ModBlocks.BLOCK_BAKELITE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_RUBBER = register(BLOCK_TAB, "block_rubber",
            () -> new BlockItem(ModBlocks.BLOCK_RUBBER.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_CADMIUM = register(BLOCK_TAB, "block_cadmium",
            () -> new BlockItem(ModBlocks.BLOCK_CADMIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_TCALLOY = register(BLOCK_TAB, "block_tcalloy",
            () -> new BlockItem(ModBlocks.BLOCK_TCALLOY.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_CDALLOY = register(BLOCK_TAB, "block_cdalloy",
            () -> new BlockItem(ModBlocks.BLOCK_CDALLOY.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SCHRABIDIUM_FUEL = register(BLOCK_TAB, "block_schrabidium_fuel",
            () -> new BlockItem(ModBlocks.BLOCK_SCHRABIDIUM_FUEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> BASALT = register(BLOCK_TAB, "basalt",
            () -> new BlockItem(ModBlocks.BASALT.get(), new Item.Properties()));

    public static final RegistryObject<Item> GAS_ASBESTOS = register(BLOCK_TAB, "gas_asbestos",
            () -> new BlockItem(ModBlocks.GAS_ASBESTOS.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_BASALT = register("ore_basalt",
            () -> new ItemBlockOreBasalt(ModBlocks.ORE_BASALT.get(), new Item.Properties()));

    public static final RegistryObject<Item> VOLCANIC_LAVA_BLOCK = register(BLOCK_TAB, "volcanic_lava_block",
            () -> new BlockItem(ModBlocks.VOLCANIC_LAVA_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> RAD_LAVA_BLOCK = register(BLOCK_TAB, "rad_lava_block",
            () -> new BlockItem(ModBlocks.RAD_LAVA_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> MUD_BLOCK = register(BLOCK_TAB, "mud_block",
            () -> new BlockItem(ModBlocks.MUD_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> SELLAFIELD_BEDROCK = register(BLOCK_TAB, "sellafield_bedrock",
            () -> new BlockItem(ModBlocks.SELLAFIELD_BEDROCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> SELLAFIELD_SLAKED = register(BLOCK_TAB, "sellafield_slaked",
            () -> new BlockItem(ModBlocks.SELLAFIELD_SLAKED.get(), new Item.Properties()));

    public static final RegistryObject<Item> PWR_CASING = register(BLOCK_TAB, "pwr_casing",
            () -> new BlockItem(ModBlocks.PWR_CASING.get(), new Item.Properties()));

    public static final RegistryObject<Item> PLANT_TALL = register("plant_tall",
            () -> new ItemBlockPlant(ModBlocks.PLANT_TALL.get(), new Item.Properties(), ItemBlockPlant.PlantType.TALL));

    public static final RegistryObject<Item> DIRT_DEAD = register(BLOCK_TAB, "dirt_dead",
            () -> new BlockItem(ModBlocks.DIRT_DEAD.get(), new Item.Properties()));

    public static final RegistryObject<Item> DIRT_OILY = register(BLOCK_TAB, "dirt_oily",
            () -> new BlockItem(ModBlocks.DIRT_OILY.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_ALEXANDRITE = register(BLOCK_TAB, "ore_alexandrite",
            () -> new BlockItem(ModBlocks.ORE_ALEXANDRITE.get(), new Item.Properties()));

    public static final RegistryObject<Item> GAS_FLAMMABLE = register(BLOCK_TAB, "gas_flammable",
            () -> new BlockItem(ModBlocks.GAS_FLAMMABLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> GAS_EXPLOSIVE = register(BLOCK_TAB, "gas_explosive",
            () -> new BlockItem(ModBlocks.GAS_EXPLOSIVE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SCHRABIDIUM_CLUSTER = register(BLOCK_TAB, "block_schrabidium_cluster",
            () -> new BlockItem(ModBlocks.BLOCK_SCHRABIDIUM_CLUSTER.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_EUPHEMIUM_CLUSTER = register(BLOCK_TAB, "block_euphemium_cluster",
            () -> new BlockItem(ModBlocks.BLOCK_EUPHEMIUM_CLUSTER.get(), new Item.Properties()));

    public static final RegistryObject<Item> DET_CORD = register(BLOCK_TAB, "det_cord",
            () -> new ItemDetCord(ModBlocks.DET_CORD.get(), new Item.Properties()));

    public static final RegistryObject<Item> DET_CHARGE = register(BLOCK_TAB, "det_charge",
            () -> new BlockItem(ModBlocks.DET_CHARGE.get(), new Item.Properties()));

    public static final RegistryObject<Item> TOXIC_BLOCK = register(BLOCK_TAB, "toxic_block",
            () -> new BlockItem(ModBlocks.TOXIC_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> NUKE_CUSTOM = register(NUKE_TAB, "bomb/nuke_custom",
            () -> new ItemBlockNukeCustom(ModBlocks.NUKE_CUSTOM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SLAG = register(BLOCK_TAB, "block_slag",
            () -> new BlockItem(ModBlocks.BLOCK_SLAG.get(), new Item.Properties()));

    public static final RegistryObject<Item> BOBBLEHEAD = register(BLOCK_TAB, "bobble",
            () -> new BlockItem(ModBlocks.BOBBLEHEAD.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONCRETE_COLORED =
            register("concrete_colored",
                    () -> new ItemBlockConcreteColored(ModBlocks.CONCRETE_COLORED.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONCRETE_COLORED_EXT =
            register("concrete_colored_ext",
                    () -> new ItemBlockConcreteColoredExt(ModBlocks.CONCRETE_COLORED_EXT.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONCRETE_ASBESTOS =
            register(BLOCK_TAB,"concrete_asbestos", () -> new BlockItem(ModBlocks.CONCRETE_ASBESTOS.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONCRETE_REBAR =
            register(BLOCK_TAB,"concrete_rebar", () -> new BlockItem(ModBlocks.CONCRETE_REBAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONCRETE_SUPER =
            register(BLOCK_TAB,"concrete_super", () -> new BlockItem(ModBlocks.CONCRETE_SUPER.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONCRETE_SUPER_BROKEN =
            register(BLOCK_TAB,"concrete_super_broken", () -> new BlockItem(ModBlocks.CONCRETE_SUPER_BROKEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> CONCRETE_PILLAR =
            register(BLOCK_TAB,"concrete_pillar", () -> new BlockItem(ModBlocks.CONCRETE_PILLAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> PIN = register(PARTS_TAB, "pin",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> KEY_RED = register(PARTS_TAB, "key_red",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> KEY_RED_CRACKED = register(PARTS_TAB, "key_red_cracked",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> KEY_FAKE = register(PARTS_TAB, "key_fake",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SANDBAG = register(BLOCK_TAB, "sandbag",
            () -> new BlockItem(ModBlocks.SANDBAG.get(), new Item.Properties()));

    public static final RegistryObject<Item> DOOR_METAL_ITEM = register(MACHINE_TAB, "door_metal",
            () -> new BlockItem(ModBlocks.DOOR_METAL.get(), new Item.Properties()));

    public static final RegistryObject<Item> DOOR_OFFICE_ITEM = register(MACHINE_TAB,"door_office",
            () -> new BlockItem(ModBlocks.DOOR_OFFICE.get(), new Item.Properties()));

    public static final RegistryObject<Item> DOOR_BUNKER_ITEM = register(MACHINE_TAB,"door_bunker",
            () -> new BlockItem(ModBlocks.DOOR_BUNKER.get(), new Item.Properties()));

    public static final RegistryObject<Item> DOOR_RED_ITEM = register(MACHINE_TAB,"door_red",
            () -> new BlockItem(ModBlocks.DOOR_RED.get(), new Item.Properties()));

    public static final RegistryObject<Item> BATTERY_CREATIVE = register(PARTS_TAB, "battery_creative",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUCLEAR_WASTE_TINY = register(PARTS_TAB, "nuclear_waste_tiny",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOLT_STEEL = register(PARTS_TAB, "bolt_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOLT_LEAD = register(PARTS_TAB, "bolt_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOLT_TUNGSTEN = register(PARTS_TAB, "bolt_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOLT_DURA_STEEL = register(PARTS_TAB, "bolt_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GEAR_LARGE = register(PARTS_TAB, "gear_large",
            () -> new ItemGear(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WIRING_RED_COPPER = register(MACHINE_TAB,"tools/wiring_red_copper",
                    () -> new ItemWiring(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SCRAPS = register("scraps",
            () -> new ItemScraps(new Item.Properties()),
            ItemModelType.GENERATED_LAYERED,
            "scraps", "scraps_liquid");

    public static final RegistryObject<Item> CRUCIBLE_TEMPLATE = register("crucible_template",
            () -> new ItemCrucibleTemplate(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MOLD = register("mold",
            () -> new ItemMold(new Item.Properties()),
            ItemModelType.ENUM_ITEM,
            ItemMold.MoldType.class);

    public static final RegistryObject<Item> MOLD_BASE = register(PARTS_TAB,"mold_base",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLADE_TITANIUM = register(PARTS_TAB,"blade_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> BLADE_TUNGSTEN = register(PARTS_TAB,"blade_tungsten",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLADES_STEEL = register(PARTS_TAB,"blades_steel",
            () -> new ItemBlades(new Item.Properties(), 200), ItemModelType.GENERATED);
    public static final RegistryObject<Item> BLADES_TITANIUM = register(PARTS_TAB,"blades_titanium",
            () -> new ItemBlades(new Item.Properties(), 350), ItemModelType.GENERATED);
    public static final RegistryObject<Item> BLADES_ADVANCED_ALLOY = register(PARTS_TAB,"blades_advanced_alloy",
            () -> new ItemBlades(new Item.Properties(), 700), ItemModelType.GENERATED);
    public static final RegistryObject<Item> BLADES_DESH = register(PARTS_TAB,"blades_desh",
            () -> new ItemBlades(new Item.Properties(), 0), ItemModelType.GENERATED);

    public static final RegistryObject<Item> DUCTTAPE = register(PARTS_TAB, "duct_tape",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SC_URANIUM = register("battery_sc_uranium",
            () -> new ItemSelfcharger(5, new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SC_TECHNETIUM = register("battery_sc_technetium",
            () -> new ItemSelfcharger(25, new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SC_PLUTONIUM = register("battery_sc_plutonium",
            () -> new ItemSelfcharger(100, new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SC_POLONIUM = register("battery_sc_polonium",
            () -> new ItemSelfcharger(500, new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SC_GOLD = register("battery_sc_gold",
            () -> new ItemSelfcharger(2500, new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SC_LEAD = register("battery_sc_lead",
            () -> new ItemSelfcharger(5000, new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SC_AMERICIUM = register("battery_sc_americium",
            () -> new ItemSelfcharger(10000, new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BALL_TNT = register(PARTS_TAB, "ball_tnt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BALL_DYNAMITE = register(PARTS_TAB, "ball_dynamite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PISTON_PNEUMATIC = register(PARTS_TAB, "piston_pneumatic",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PISTON_HYDRAULIC = register(PARTS_TAB, "piston_hydraulic",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PISTON_ELECTRIC = register(PARTS_TAB, "piston_electric",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LDE = register(PARTS_TAB, "low_density_element",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HDE = register(PARTS_TAB, "heavy_duty_element",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BALL_TATB = register(PARTS_TAB, "ball_tatb",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FUEL_TANK_SMALL = register(PARTS_TAB, "fuel_tank_small",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FUEL_TANK_MEDIUM = register(PARTS_TAB, "fuel_tank_medium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FUEL_TANK_LARGE = register(PARTS_TAB, "fuel_tank_large",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> THRUSTER_SMALL = register(PARTS_TAB, "thruster_small",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> THRUSTER_MEDIUM = register(PARTS_TAB, "thruster_medium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> THRUSTER_LARGE = register(PARTS_TAB, "thruster_large",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_HEAD_MAPPER = register(PARTS_TAB, "sat_head_mapper",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_HEAD_SCANNER = register(PARTS_TAB, "sat_head_scanner",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_HEAD_RADAR = register(PARTS_TAB, "sat_head_radar",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_HEAD_LASER = register(PARTS_TAB, "sat_head_laser",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_HEAD_RESONATOR = register(PARTS_TAB, "sat_head_resonator",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MISSILE_ASSEMBLY = register(PARTS_TAB, "missile_assembly",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MISSILE_TEST = register(MISSILE_TAB, "missile_test",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_GENERIC = register(MISSILE_TAB, "missile_generic",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_ANTI_BALLISTIC = register(MISSILE_TAB, "missile_anti_ballistic",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.ABM, ItemMissile.MissileTier.TIER1),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_INCENDIARY = register(MISSILE_TAB, "missile_incendiary",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_CLUSTER = register(MISSILE_TAB, "missile_cluster",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_BUSTER = register(MISSILE_TAB, "missile_buster",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_DECOY = register(MISSILE_TAB, "missile_decoy",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.V2, ItemMissile.MissileTier.TIER1),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_STRONG = register(MISSILE_TAB, "missile_strong",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_INCENDIARY_STRONG = register(MISSILE_TAB, "missile_incendiary_strong",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_CLUSTER_STRONG = register(MISSILE_TAB, "missile_cluster_strong",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_BUSTER_STRONG = register(MISSILE_TAB, "missile_buster_strong",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_EMP_STRONG = register(MISSILE_TAB, "missile_emp_strong",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER2),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_BURST = register(MISSILE_TAB, "missile_burst",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.HUGE, ItemMissile.MissileTier.TIER3),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_INFERNO = register(MISSILE_TAB, "missile_inferno",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.HUGE, ItemMissile.MissileTier.TIER3),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_RAIN = register(MISSILE_TAB, "missile_rain",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.HUGE, ItemMissile.MissileTier.TIER3),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_DRILL = register(MISSILE_TAB, "missile_drill",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.HUGE, ItemMissile.MissileTier.TIER3),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_NUCLEAR = register(MISSILE_TAB, "missile_nuclear",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_NUCLEAR_CLUSTER = register(MISSILE_TAB, "missile_nuclear_cluster",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_VOLCANO = register(MISSILE_TAB, "missile_volcano",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_DOOMSDAY = register(MISSILE_TAB, "missile_doomsday",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_DOOMSDAY_RUSTED = register(MISSILE_TAB, "missile_doomsday_rusted",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.ATLAS, ItemMissile.MissileTier.TIER4).notLaunchable(),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_TAINT = register(MISSILE_TAB, "missile_taint",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_MICRO = register(MISSILE_TAB, "missile_micro",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_BHOLE = register(MISSILE_TAB, "missile_bhole",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_SCHRABIDIUM = register(MISSILE_TAB, "missile_schrabidium",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_EMP = register(MISSILE_TAB, "missile_emp",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.MICRO, ItemMissile.MissileTier.TIER0),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_SHUTTLE = register(MISSILE_TAB, "missile_shuttle",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.OTHER, ItemMissile.MissileTier.TIER3, ItemMissile.MissileFuel.KEROSENE_PEROXIDE),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> MISSILE_STEALTH = register(MISSILE_TAB, "missile_stealth",
            () -> new ItemMissile(new Item.Properties().stacksTo(1), ItemMissile.MissileFormFactor.STRONG, ItemMissile.MissileTier.TIER1),
            ItemModelType.BUILTIN_ENTITY);

    public static final RegistryObject<Item> WARHEAD_GENERIC_SMALL = register(PARTS_TAB, "warhead_generic_small",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_GENERIC_MEDIUM = register(PARTS_TAB, "warhead_generic_medium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_GENERIC_LARGE = register(PARTS_TAB, "warhead_generic_large",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_INCENDIARY_SMALL = register(PARTS_TAB, "warhead_incendiary_small",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_INCENDIARY_MEDIUM = register(PARTS_TAB, "warhead_incendiary_medium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_INCENDIARY_LARGE = register(PARTS_TAB, "warhead_incendiary_large",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_CLUSTER_SMALL = register(PARTS_TAB, "warhead_cluster_small",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_CLUSTER_MEDIUM = register(PARTS_TAB, "warhead_cluster_medium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_CLUSTER_LARGE = register(PARTS_TAB, "warhead_cluster_large",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_BUSTER_SMALL = register(PARTS_TAB, "warhead_buster_small",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_BUSTER_MEDIUM = register(PARTS_TAB, "warhead_buster_medium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_BUSTER_LARGE = register(PARTS_TAB, "warhead_buster_large",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_NUCLEAR = register(PARTS_TAB, "warhead_nuclear",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_MIRV = register(PARTS_TAB, "warhead_mirv",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WARHEAD_VOLCANO = register(PARTS_TAB, "warhead_volcano",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_MAPPER = register(MISSILE_TAB, "sat_mapper",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_SCANNER = register(MISSILE_TAB, "sat_scanner",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_RADAR = register(MISSILE_TAB, "sat_radar",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_LASER = register(MISSILE_TAB, "sat_laser",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_FOEQ = register(MISSILE_TAB, "sat_foeq",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_RESONATOR = register(MISSILE_TAB, "sat_resonator",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_MINER = register(MISSILE_TAB, "sat_miner",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_LUNAR_MINER = register(MISSILE_TAB, "sat_lunar_miner",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_GERALD = register(MISSILE_TAB, "sat_gerald",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_CHIP = register(MISSILE_TAB, "sat_chip",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_RELAY = register(MISSILE_TAB, "sat_relay",
            () -> new ItemSatChip(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAT_BASE = register(MISSILE_TAB, "sat_base",
            () -> new Item(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PELLET_CHARGED = register(PARTS_TAB, "pellet_charged",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PELLET_RTG = register(PARTS_TAB, "pellet_rtg",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PELLET_RTG_ACTINIUM = register(PARTS_TAB, "pellet_rtg_actinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_AMERICIUM = register(PARTS_TAB, "pellet_rtg_americium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_AMERICIUM_TIER2 = register(PARTS_TAB, "pellet_rtg_americium_tier2",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_BERKELIUM = register(PARTS_TAB, "pellet_rtg_berkelium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_COBALT = register(PARTS_TAB, "pellet_rtg_cobalt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_DEPLETED_BISMUTH = register(PARTS_TAB, "pellet_rtg_depleted.bismuth",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_DEPLETED_LEAD = register(PARTS_TAB, "pellet_rtg_depleted.lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_DEPLETED_MERCURY = register(PARTS_TAB, "pellet_rtg_depleted.mercury",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_DEPLETED_NEPTUNIUM = register(PARTS_TAB, "pellet_rtg_depleted.neptunium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_DEPLETED_NICKEL = register(PARTS_TAB, "pellet_rtg_depleted.nickel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_DEPLETED_ZIRCONIUM = register(PARTS_TAB, "pellet_rtg_depleted.zirconium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_GOLD = register(PARTS_TAB, "pellet_rtg_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_LEAD = register(PARTS_TAB, "pellet_rtg_lead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_POLONIUM = register(PARTS_TAB, "pellet_rtg_polonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_RADIUM = register(PARTS_TAB, "pellet_rtg_radium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> PELLET_RTG_STRONTIUM = register(PARTS_TAB, "pellet_rtg_strontium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PELLET_RTG_WEAK = register(PARTS_TAB, "pellet_rtg_weak",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PELLET_BUCKSHOT = register(PARTS_TAB, "pellet_buckshot",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PELLET_GAS = register(PARTS_TAB, "pellet_gas",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PELLET_CLUSTER = register(PARTS_TAB, "pellet_cluster",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_BAKELITE = register(PARTS_TAB, "part_grip_bakelite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_DESH = register(PARTS_TAB, "part_grip_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_DURA_STEEL = register(PARTS_TAB, "part_grip_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_GUNMETAL = register(PARTS_TAB, "part_grip_gunmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_POLYCARBONATE = register(PARTS_TAB, "part_grip_polycarbonate",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_IVORY = register(PARTS_TAB, "part_grip_ivory",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_POLYMER = register(PARTS_TAB, "part_grip_polymer",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_PVC = register(PARTS_TAB, "part_grip_pvc",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_RUBBER = register(PARTS_TAB, "part_grip_rubber",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_SATURNITE = register(PARTS_TAB, "part_grip_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_STEEL = register(PARTS_TAB, "part_grip_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_WEAPON_STEEL = register(PARTS_TAB, "part_grip_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GRIP_WOOD = register(PARTS_TAB, "part_grip_wood",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_STOCK_BAKELITE = register(PARTS_TAB, "part_stock_bakelite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_STOCK_DESH = register(PARTS_TAB, "part_stock_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_STOCK_GUNMETAL = register(PARTS_TAB, "part_stock_gunmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_STOCK_POLYCARBONATE = register(PARTS_TAB, "part_stock_polycarbonate",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_STOCK_POLYMER = register(PARTS_TAB, "part_stock_polymer",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_STOCK_PVC = register(PARTS_TAB, "part_stock_pvc",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_STOCK_SATURNITE = register(PARTS_TAB, "part_stock_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_STOCK_WEAPON_STEEL = register(PARTS_TAB, "part_stock_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_STOCK_WOOD = register(PARTS_TAB, "part_stock_wood",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_BARREL_CADMIUM_STEEL = register(PARTS_TAB, "heavy_barrel_cadmium_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_BARREL_DESH = register(PARTS_TAB, "heavy_barrel_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_BARREL_DURA_STEEL = register(PARTS_TAB, "heavy_barrel_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_BARREL_FERROURANIUM = register(PARTS_TAB, "heavy_barrel_ferrouranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_BARREL_GUNMETAL = register(PARTS_TAB, "heavy_barrel_gunmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_BARREL_STEEL = register(PARTS_TAB, "heavy_barrel_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_BARREL_TECHNETIUM = register(PARTS_TAB, "heavy_barrel_technetium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_BARREL_SATURNITE = register(PARTS_TAB, "heavy_barrel_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_BARREL_WEAPON_STEEL = register(PARTS_TAB, "heavy_barrel_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_ARSENIC_BRONZE = register(PARTS_TAB, "light_barrel_arsenic_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_BISMUTH_BRONZE = register(PARTS_TAB, "light_barrel_bismuth_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_CADMIUM_STEEL = register(PARTS_TAB, "light_barrel_cadmium_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_DESH = register(PARTS_TAB, "light_barrel_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_DURA_STEEL = register(PARTS_TAB, "light_barrel_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_GUNMETAL = register(PARTS_TAB, "light_barrel_gunmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_SATURNITE = register(PARTS_TAB, "light_barrel_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_STEEL = register(PARTS_TAB, "light_barrel_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_TECHNETIUM = register(PARTS_TAB, "light_barrel_technetium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_BARREL_WEAPON_STEEL = register(PARTS_TAB, "light_barrel_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GUN_MECHANISM_GUNMETAL = register(PARTS_TAB, "gun_mechanism_gunmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GUN_MECHANISM_SATURNITE = register(PARTS_TAB, "gun_mechanism_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> GUN_MECHANISM_WEAPON_STEEL = register(PARTS_TAB, "gun_mechanism_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_RECEIVER_ARSENIC_BRONZE = register(PARTS_TAB, "heavy_receiver_arsenic_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_RECEIVER_BISMUTH_BRONZE = register(PARTS_TAB, "heavy_receiver_bismuth_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_RECEIVER_CADMIUM_STEEL = register(PARTS_TAB, "heavy_receiver_cadmium_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_RECEIVER_DURA_STEEL = register(PARTS_TAB, "heavy_receiver_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_RECEIVER_FERROURANIUM = register(PARTS_TAB, "heavy_receiver_ferrouranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_RECEIVER_GUNMETAL = register(PARTS_TAB, "heavy_receiver_gunmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_RECEIVER_SATURNITE = register(PARTS_TAB, "heavy_receiver_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_RECEIVER_TECHNETIUM = register(PARTS_TAB, "heavy_receiver_technetium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEAVY_RECEIVER_WEAPON_STEEL = register(PARTS_TAB, "heavy_receiver_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_ARSENIC_BRONZE = register(PARTS_TAB, "light_receiver_arsenic_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_BISMUTH_BRONZE = register(PARTS_TAB, "light_receiver_bismuth_bronze",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_CADMIUM_STEEL = register(PARTS_TAB, "light_receiver_cadmium_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_DESH = register(PARTS_TAB, "light_receiver_desh",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_DURA_STEEL = register(PARTS_TAB, "light_receiver_dura_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_GUNMETAL = register(PARTS_TAB, "light_receiver_gunmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_SATURNITE = register(PARTS_TAB, "light_receiver_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_STEEL = register(PARTS_TAB, "light_receiver_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_TECHNETIUM = register(PARTS_TAB, "light_receiver_technetium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> LIGHT_RECEIVER_WEAPON_STEEL = register(PARTS_TAB, "light_receiver_weapon_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Weapon Mod Specials
    public static final RegistryObject<Item> WEAPON_MOD_SILENCER = register(PARTS_TAB, "weapon_mod_silencer",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_SCOPE = register(PARTS_TAB, "weapon_mod_scope",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_SAW = register(PARTS_TAB, "weapon_mod_saw",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_GREASEGUN = register(PARTS_TAB, "weapon_mod_greasegun",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_SLOWDOWN = register(PARTS_TAB, "weapon_mod_slowdown",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_SPEEDUP = register(PARTS_TAB, "weapon_mod_speedup",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_CHOKE = register(PARTS_TAB, "weapon_mod_choke",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_SPEEDLOADER = register(PARTS_TAB, "weapon_mod_speedloader",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_FURNITURE_GREEN = register(PARTS_TAB, "weapon_mod_furniture_green",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_FURNITURE_BLACK = register(PARTS_TAB, "weapon_mod_furniture_black",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_BAYONET = register(PARTS_TAB, "weapon_mod_bayonet",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_STACK_MAG = register(PARTS_TAB, "weapon_mod_stack_mag",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_SKIN_SATURNITE = register(PARTS_TAB, "weapon_mod_skin_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_LAS_SHOTGUN = register(PARTS_TAB, "weapon_mod_las_shotgun",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_LAS_CAPACITOR = register(PARTS_TAB, "weapon_mod_las_capacitor",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_LAS_AUTO = register(PARTS_TAB, "weapon_mod_las_auto",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_NICKEL = register(PARTS_TAB, "weapon_mod_nickel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_DOUBLOONS = register(PARTS_TAB, "weapon_mod_doubloons",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_DRILL_HSS = register(PARTS_TAB, "weapon_mod_drill_hss",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_DRILL_WEAPONSTEEL = register(PARTS_TAB, "weapon_mod_drill_weaponsteel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_DRILL_TCALLOY = register(PARTS_TAB, "weapon_mod_drill_tcalloy",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_DRILL_SATURNITE = register(PARTS_TAB, "weapon_mod_drill_saturnite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_ENGINE_DIESEL = register(PARTS_TAB, "weapon_mod_engine_diesel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_ENGINE_AVIATION = register(PARTS_TAB, "weapon_mod_engine_aviation",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_ENGINE_ELECTRIC = register(PARTS_TAB, "weapon_mod_engine_electric",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_ENGINE_TURBO = register(PARTS_TAB, "weapon_mod_engine_turbo",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SECRET_CANISTER = register(PARTS_TAB, "secret_canister",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SECRET_CONTROLLER = register(PARTS_TAB, "secret_controller",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SECRET_SELENIUM_STEEL = register(PARTS_TAB, "secret_selenium_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SECRET_ABERRATOR = register(PARTS_TAB, "secret_aberrator",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SECRET_FOLLY = register(PARTS_TAB, "secret_folly",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_IRON_DAMAGE = register(PARTS_TAB, "weapon_mod_iron_damage",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_IRON_DURA = register(PARTS_TAB, "weapon_mod_iron_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_STEEL_DAMAGE = register(PARTS_TAB, "weapon_mod_steel_damage",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_STEEL_DURA = register(PARTS_TAB, "weapon_mod_steel_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_DURA_DAMAGE = register(PARTS_TAB, "weapon_mod_dura_damage",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_DURA_DURA = register(PARTS_TAB, "weapon_mod_dura_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_DESH_DAMAGE = register(PARTS_TAB, "weapon_mod_desh_damage",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_DESH_DURA = register(PARTS_TAB, "weapon_mod_desh_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_WSTEEL_DAMAGE = register(PARTS_TAB, "weapon_mod_wsteel_damage",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_WSTEEL_DURA = register(PARTS_TAB, "weapon_mod_wsteel_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_FERRO_DAMAGE = register(PARTS_TAB, "weapon_mod_ferro_damage",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_FERRO_DURA = register(PARTS_TAB, "weapon_mod_ferro_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_TCALLOY_DAMAGE = register(PARTS_TAB, "weapon_mod_tcalloy_damage",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_TCALLOY_DURA = register(PARTS_TAB, "weapon_mod_tcalloy_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_BIGMT_DAMAGE = register(PARTS_TAB, "weapon_mod_bigmt_damage",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_BIGMT_DURA = register(PARTS_TAB, "weapon_mod_bigmt_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_BRONZE_DAMAGE = register(PARTS_TAB, "weapon_mod_bronze_damage",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPON_MOD_BRONZE_DURA = register(PARTS_TAB, "weapon_mod_bronze_dura",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MISSILE_CUSTOM = register(MISSILE_TAB, "missile_custom",
            () -> new ItemCustomMissile(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    // MP Thrusters - Size 10
    public static final RegistryObject<Item> MP_THRUSTER_10_KEROSENE = register(MISSILE_TAB, "mp_thruster_10_kerosene",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.KEROSENE, 1F, 1.5F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(10F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_10_kerosene");

    public static final RegistryObject<Item> MP_THRUSTER_10_SOLID = register(MISSILE_TAB, "mp_thruster_10_solid",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.SOLID, 1F, 1.5F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(15F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_10_solid");

    public static final RegistryObject<Item> MP_THRUSTER_10_XENON = register(MISSILE_TAB, "mp_thruster_10_xenon",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.XENON, 1F, 1.5F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(5F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_10_xenon");

    // MP Thrusters - Size 15
    public static final RegistryObject<Item> MP_THRUSTER_15_KEROSENE = register(MISSILE_TAB, "mp_thruster_15_kerosene",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.KEROSENE, 1F, 7.5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(15F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_kerosene");

    public static final RegistryObject<Item> MP_THRUSTER_15_KEROSENE_DUAL = register(MISSILE_TAB, "mp_thruster_15_kerosene_dual",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.KEROSENE, 1F, 2.5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(15F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_kerosene_dual");

    public static final RegistryObject<Item> MP_THRUSTER_15_KEROSENE_TRIPLE = register(MISSILE_TAB, "mp_thruster_15_kerosene_triple",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.KEROSENE, 1F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(15F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_kerosene_triple");

    public static final RegistryObject<Item> MP_THRUSTER_15_SOLID = register(MISSILE_TAB, "mp_thruster_15_solid",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.SOLID, 1F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(20F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_solid");

    public static final RegistryObject<Item> MP_THRUSTER_15_SOLID_HEXADECUPLE = register(MISSILE_TAB, "mp_thruster_15_solid_hexadecuple",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.SOLID, 1F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(25F).setRarity(ItemCustomMissilePart.Rarity.UNCOMMON),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_solid_hexdecuple");

    public static final RegistryObject<Item> MP_THRUSTER_15_HYDROGEN = register(MISSILE_TAB, "mp_thruster_15_hydrogen",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.HYDROGEN, 1F, 7.5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(20F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_kerosene");

    public static final RegistryObject<Item> MP_THRUSTER_15_HYDROGEN_DUAL = register(MISSILE_TAB, "mp_thruster_15_hydrogen_dual",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.HYDROGEN, 1F, 2.5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(15F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_kerosene_dual");

    public static final RegistryObject<Item> MP_THRUSTER_15_BALEFIRE_SHORT = register(MISSILE_TAB, "mp_thruster_15_balefire_short",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.BALEFIRE, 1F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(25F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_balefire_short");

    public static final RegistryObject<Item> MP_THRUSTER_15_BALEFIRE = register(MISSILE_TAB, "mp_thruster_15_balefire",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.BALEFIRE, 1F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(25F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_balefire");

    public static final RegistryObject<Item> MP_THRUSTER_15_BALEFIRE_LARGE = register(MISSILE_TAB, "mp_thruster_15_balefire_large",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.BALEFIRE, 1F, 7.5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(35F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_balefire_large");

    public static final RegistryObject<Item> MP_THRUSTER_15_BALEFIRE_LARGE_RAD = register(MISSILE_TAB, "mp_thruster_15_balefire_large_rad",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.BALEFIRE, 1F, 7.5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(35F).setAuthor("The Master").setRarity(ItemCustomMissilePart.Rarity.UNCOMMON),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_15_balefire_large");

    // MP Thrusters - Size 20
    public static final RegistryObject<Item> MP_THRUSTER_20_KEROSENE = register(MISSILE_TAB, "mp_thruster_20_kerosene",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.KEROSENE, 1F, 100F, ItemCustomMissilePart.PartSize.SIZE_20).setHealth(30F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_20_kerosene");

    public static final RegistryObject<Item> MP_THRUSTER_20_KEROSENE_DUAL = register(MISSILE_TAB, "mp_thruster_20_kerosene_dual",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.KEROSENE, 1F, 100F, ItemCustomMissilePart.PartSize.SIZE_20).setHealth(30F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_20_kerosene_dual");

    public static final RegistryObject<Item> MP_THRUSTER_20_KEROSENE_TRIPLE = register(MISSILE_TAB, "mp_thruster_20_kerosene_triple",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.KEROSENE, 1F, 100F, ItemCustomMissilePart.PartSize.SIZE_20).setHealth(30F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_20_kerosene_triple");

    public static final RegistryObject<Item> MP_THRUSTER_20_SOLID = register(MISSILE_TAB, "mp_thruster_20_solid",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.SOLID, 1F, 100F, ItemCustomMissilePart.PartSize.SIZE_20).setHealth(35F).setWittyText("It's basically just a big hole at the end of the fuel tank."),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_20_solid");

    public static final RegistryObject<Item> MP_THRUSTER_20_SOLID_MULTI = register(MISSILE_TAB, "mp_thruster_20_solid_multi",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.SOLID, 1F, 100F, ItemCustomMissilePart.PartSize.SIZE_20).setHealth(35F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_20_solid_multi");

    public static final RegistryObject<Item> MP_THRUSTER_20_SOLID_MULTIER = register(MISSILE_TAB, "mp_thruster_20_solid_multier",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeThruster(ItemCustomMissilePart.FuelType.SOLID, 1F, 100F, ItemCustomMissilePart.PartSize.SIZE_20).setHealth(35F).setWittyText("Did I miscount? Hope not."),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_t_20_solid_multi");

    // MP Stability - Size 10
    public static final RegistryObject<Item> MP_STABILITY_10_FLAT = register(MISSILE_TAB, "mp_stability_10_flat",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeStability(0.5F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(10F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_s_10_flat");

    public static final RegistryObject<Item> MP_STABILITY_10_CRUISE = register(MISSILE_TAB, "mp_stability_10_cruise",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeStability(0.25F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(5F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_s_10_cruise");

    public static final RegistryObject<Item> MP_STABILITY_10_SPACE = register(MISSILE_TAB, "mp_stability_10_space",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeStability(0.35F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(5F).setRarity(ItemCustomMissilePart.Rarity.COMMON).setWittyText("Standing there alone, the ship is waiting / All systems are go, are you sure?"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_s_10_space");

    // MP Stability - Size 15
    public static final RegistryObject<Item> MP_STABILITY_15_FLAT = register(MISSILE_TAB, "mp_stability_15_flat",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeStability(0.5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(10F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_s_15_flat");

    public static final RegistryObject<Item> MP_STABILITY_15_THIN = register(MISSILE_TAB, "mp_stability_15_thin",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeStability(0.35F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(5F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_s_15_thin");

    public static final RegistryObject<Item> MP_STABILITY_15_SOYUZ = register(MISSILE_TAB, "mp_stability_15_soyuz",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeStability(0.25F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(15F).setRarity(ItemCustomMissilePart.Rarity.COMMON).setWittyText("Союз!"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_s_15_soyuz");

    // MP Stability - Size 20
    public static final RegistryObject<Item> MP_STABILITY_20_FLAT = register(MISSILE_TAB, "mp_stability_20_flat",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeStability(0.5F, ItemCustomMissilePart.PartSize.SIZE_20),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_s_20");

    // MP Fuselage - Size 10 Kerosene
    public static final RegistryObject<Item> MP_FUSELAGE_10_KEROSENE = register(MISSILE_TAB, "mp_fuselage_10_kerosene",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(20F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_CAMO = register(MISSILE_TAB, "mp_fuselage_10_kerosene_camo",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(20F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Camo"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_DESERT = register(MISSILE_TAB, "mp_fuselage_10_kerosene_desert",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(20F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Desert Camo"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_SKY = register(MISSILE_TAB, "mp_fuselage_10_kerosene_sky",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(20F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Sky Camo"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_FLAMES = register(MISSILE_TAB, "mp_fuselage_10_kerosene_flames",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(20F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setTitle("Sick Flames"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_INSULATION = register(MISSILE_TAB, "mp_fuselage_10_kerosene_insulation",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(20F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Orange Insulation").setHealth(25F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_SLEEK = register(MISSILE_TAB, "mp_fuselage_10_kerosene_sleek",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(20F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setTitle("IF-R&D").setHealth(35F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_METAL = register(MISSILE_TAB, "mp_fuselage_10_kerosene_metal",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(20F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setTitle("Bolted Metal").setHealth(30F).setAuthor("Hoboy"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_KEROSENE_TAINT = register(MISSILE_TAB, "mp_fuselage_10_kerosene_taint",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(20F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Sam").setTitle("Tainted"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    // MP Fuselage - Size 10 Solid
    public static final RegistryObject<Item> MP_FUSELAGE_10_SOLID = register(MISSILE_TAB, "mp_fuselage_10_solid",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(25F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_SOLID_FLAMES = register(MISSILE_TAB, "mp_fuselage_10_solid_flames",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(25F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setTitle("Sick Flames"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_SOLID_INSULATION = register(MISSILE_TAB, "mp_fuselage_10_solid_insulation",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(25F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Orange Insulation").setHealth(30F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_SOLID_SLEEK = register(MISSILE_TAB, "mp_fuselage_10_solid_sleek",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(25F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setTitle("IF-R&D").setHealth(35F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_SOLID_SOVIET_GLORY = register(MISSILE_TAB, "mp_fuselage_10_solid_soviet_glory",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(25F))
                    .setRarity(ItemCustomMissilePart.Rarity.EPIC).setAuthor("Hoboy").setHealth(35F).setTitle("Soviet Glory"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_SOLID_CATHEDRAL = register(MISSILE_TAB, "mp_fuselage_10_solid_cathedral",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(25F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setAuthor("Satan").setTitle("Unholy Cathedral").setWittyText("Quakeesque!"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_SOLID_MOONLIT = register(MISSILE_TAB, "mp_fuselage_10_solid_moonlit",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(25F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("The Master & Hoboy").setTitle("Moonlit"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_SOLID_BATTERY = register(MISSILE_TAB, "mp_fuselage_10_solid_battery",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(25F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("wolfmonster222").setHealth(30F).setTitle("Ecstatic").setWittyText("I got caught eating batteries again :("),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_SOLID_DURACELL = register(MISSILE_TAB, "mp_fuselage_10_solid_duracell",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 2500F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(25F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setAuthor("Hoboy").setTitle("Duracell").setHealth(30F).setWittyText("The crunchiest battery on the market!"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    // MP Fuselage - Size 10 Xenon
    public static final RegistryObject<Item> MP_FUSELAGE_10_XENON = register(MISSILE_TAB, "mp_fuselage_10_xenon",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.XENON, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(20F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_XENON_BHOLE = register(MISSILE_TAB, "mp_fuselage_10_xenon_bhole",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.XENON, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(20F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setAuthor("Sten89").setTitle("Morceus-1457"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_kerosene");

    // MP Fuselage - Size 10 Long Kerosene
    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_CAMO = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_camo",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Camo"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_DESERT = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_desert",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Desert Camo"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_SKY = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_sky",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Sky Camo"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_FLAMES = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_flames",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setTitle("Sick Flames"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_INSULATION = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_insulation",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Orange Insulation").setHealth(35F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_SLEEK = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_sleek",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setTitle("IF-R&D").setHealth(40F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_METAL = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_metal",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Hoboy").setHealth(35F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_DASH = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_dash",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.EPIC).setAuthor("Sam").setTitle("Dash").setWittyText("I wash my hands of it."),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_TAINT = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_taint",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Sam").setTitle("Tainted"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_KEROSENE_VAP = register(MISSILE_TAB, "mp_fuselage_10_long_kerosene_vap",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setAuthor("Hoboy").setHealth(30F))
                    .setRarity(ItemCustomMissilePart.Rarity.EPIC).setAuthor("VT-6/24").setTitle("Minty Contrail").setWittyText("Upper rivet!"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    // MP Fuselage - Size 10 Long Solid
    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID = register(MISSILE_TAB, "mp_fuselage_10_long_solid",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(35F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_FLAMES = register(MISSILE_TAB, "mp_fuselage_10_long_solid_flames",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(35F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setTitle("Sick Flames"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_INSULATION = register(MISSILE_TAB, "mp_fuselage_10_long_solid_insulation",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(35F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Orange Insulation").setHealth(40F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_SLEEK = register(MISSILE_TAB, "mp_fuselage_10_long_solid_sleek",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(35F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setTitle("IF-R&D").setHealth(45F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_SOVIET_GLORY = register(MISSILE_TAB, "mp_fuselage_10_long_solid_soviet_glory",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(35F))
                    .setRarity(ItemCustomMissilePart.Rarity.EPIC).setAuthor("Hoboy").setHealth(45F).setTitle("Soviet Glory").setWittyText("Fully Automated Luxury Gay Space Communism!"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_BULLET = register(MISSILE_TAB, "mp_fuselage_10_long_solid_bullet",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(35F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setAuthor("Sam").setTitle("Bullet Bill"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_LONG_SOLID_SILVERMOONLIGHT = register(MISSILE_TAB, "mp_fuselage_10_long_solid_silvermoonlight",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 5000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(35F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("The Master").setTitle("Silver Moonlight"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_long_kerosene");

    // MP Fuselage - Size 10 to 15
    public static final RegistryObject<Item> MP_FUSELAGE_10_15_KEROSENE = register(MISSILE_TAB, "mp_fuselage_10_15_kerosene",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 10000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(40F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_15_SOLID = register(MISSILE_TAB, "mp_fuselage_10_15_solid",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 10000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(40F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_15_HYDROGEN = register(MISSILE_TAB, "mp_fuselage_10_15_hydrogen",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.HYDROGEN, 10000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(40F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_10_15_BALEFIRE = register(MISSILE_TAB, "mp_fuselage_10_15_balefire",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.BALEFIRE, 10000F, ItemCustomMissilePart.PartSize.SIZE_10, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(40F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_10_15_kerosene");

    // MP Fuselage - Size 15 Kerosene
    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE = register(MISSILE_TAB, "mp_fuselage_15_kerosene",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_CAMO = register(MISSILE_TAB, "mp_fuselage_15_kerosene_camo",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Camo"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_DESERT = register(MISSILE_TAB, "mp_fuselage_15_kerosene_desert",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Desert Camo"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_SKY = register(MISSILE_TAB, "mp_fuselage_15_kerosene_sky",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Sky Camo"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_INSULATION = register(MISSILE_TAB, "mp_fuselage_15_kerosene_insulation",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Orange Insulation").setHealth(55F).setWittyText("Rest in spaghetti Columbia :("),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_METAL = register(MISSILE_TAB, "mp_fuselage_15_kerosene_metal",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Hoboy").setTitle("Bolted Metal").setHealth(60F).setWittyText("Metal frame with metal plating reinforced with bolted metal sheets and metal."),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_DECORATED = register(MISSILE_TAB, "mp_fuselage_15_kerosene_decorated",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Hoboy").setTitle("Decorated").setHealth(60F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_STEAMPUNK = register(MISSILE_TAB, "mp_fuselage_15_kerosene_steampunk",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setAuthor("Hoboy").setTitle("Steampunk").setHealth(60F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_POLITE = register(MISSILE_TAB, "mp_fuselage_15_kerosene_polite",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.LEGENDARY).setAuthor("Hoboy").setTitle("Polite").setHealth(60F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_BLACKJACK = register(MISSILE_TAB, "mp_fuselage_15_kerosene_blackjack",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.LEGENDARY).setTitle("Queen Whiskey").setHealth(100F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_LAMBDA = register(MISSILE_TAB, "mp_fuselage_15_kerosene_lambda",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setAuthor("VT-6/24").setTitle("Lambda Complex").setHealth(75F).setWittyText("MAGNIFICENT MICROWAVE CASSEROLE"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_MINUTEMAN = register(MISSILE_TAB, "mp_fuselage_15_kerosene_minuteman",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Spexta").setTitle("MX 1702"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_PIP = register(MISSILE_TAB, "mp_fuselage_15_kerosene_pip",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.EPIC).setAuthor("The Doctor").setTitle("LittlePip").setWittyText("31!"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_TAINT = register(MISSILE_TAB, "mp_fuselage_15_kerosene_taint",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Sam").setTitle("Tainted").setWittyText("DUN-DUN!"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_KEROSENE_YUCK = register(MISSILE_TAB, "mp_fuselage_15_kerosene_yuck",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setAuthor("Hoboy").setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.EPIC).setAuthor("Hoboy").setTitle("Flesh").setWittyText("Note: Never clean DNA vials with your own spit.").setHealth(60F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    // MP Fuselage - Size 15 Solid
    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID = register(MISSILE_TAB, "mp_fuselage_15_solid",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_INSULATION = register(MISSILE_TAB, "mp_fuselage_15_solid_insulation",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.COMMON).setTitle("Orange Insulation").setHealth(65F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_DESH = register(MISSILE_TAB, "mp_fuselage_15_solid_desh",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setAuthor("Hoboy").setTitle("Desh Plating").setHealth(80F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_SOVIET_GLORY = register(MISSILE_TAB, "mp_fuselage_15_solid_soviet_glory",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setAuthor("Hoboy").setTitle("Soviet Glory").setHealth(70F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_SOVIET_STANK = register(MISSILE_TAB, "mp_fuselage_15_solid_soviet_stank",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.EPIC).setAuthor("Hoboy").setTitle("Soviet Stank").setHealth(15F).setWittyText("Aged like a fine wine! Well, almost."),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_FAUST = register(MISSILE_TAB, "mp_fuselage_15_solid_faust",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.LEGENDARY).setAuthor("Dr.Nostalgia").setTitle("Mighty Lauren").setHealth(250F).setWittyText("Welcome to Subway, may I take your order?"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_SILVERMOONLIGHT = register(MISSILE_TAB, "mp_fuselage_15_solid_silvermoonlight",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("The Master").setTitle("Silver Moonlight"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_SNOWY = register(MISSILE_TAB, "mp_fuselage_15_solid_snowy",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Dr.Nostalgia").setTitle("Chilly Day"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_PANORAMA = register(MISSILE_TAB, "mp_fuselage_15_solid_panorama",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setAuthor("Hoboy").setTitle("Panorama"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_ROSES = register(MISSILE_TAB, "mp_fuselage_15_solid_roses",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Hoboy").setTitle("Bed of roses"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_SOLID_MIMI = register(MISSILE_TAB, "mp_fuselage_15_solid_mimi",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(60F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setTitle("Mimi-chan"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_kerosene");

    // MP Fuselage - Size 15 Hydrogen
    public static final RegistryObject<Item> MP_FUSELAGE_15_HYDROGEN = register(MISSILE_TAB, "mp_fuselage_15_hydrogen",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.HYDROGEN, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(50F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_hydrogen");

    public static final RegistryObject<Item> MP_FUSELAGE_15_HYDROGEN_CATHEDRAL = register(MISSILE_TAB, "mp_fuselage_15_hydrogen_cathedral",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.HYDROGEN, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(50F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setAuthor("Satan").setTitle("Unholy Cathedral"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_hydrogen");

    // MP Fuselage - Size 15 Balefire
    public static final RegistryObject<Item> MP_FUSELAGE_15_BALEFIRE = register(MISSILE_TAB, "mp_fuselage_15_balefire",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.BALEFIRE, 15000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(75F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_hydrogen");

    // MP Fuselage - Size 15 to 20
    public static final RegistryObject<Item> MP_FUSELAGE_15_20_KEROSENE = register(MISSILE_TAB, "mp_fuselage_15_20_kerosene",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 20000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_20).setAuthor("Hoboy").setHealth(70F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_20_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_20_KEROSENE_MAGNUSSON = register(MISSILE_TAB, "mp_fuselage_15_20_kerosene_magnusson",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.KEROSENE, 20000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_20).setAuthor("Hoboy").setHealth(70F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setAuthor("VT-6/24").setTitle("White Forest Rocket").setWittyText("And get your cranio-conjugal parasite away from my nose cone!"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_20_kerosene");

    public static final RegistryObject<Item> MP_FUSELAGE_15_20_SOLID = register(MISSILE_TAB, "mp_fuselage_15_20_solid",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeFuselage(ItemCustomMissilePart.FuelType.SOLID, 20000F, ItemCustomMissilePart.PartSize.SIZE_15, ItemCustomMissilePart.PartSize.SIZE_20).setHealth(70F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_f_15_20_kerosene");

    // MP Warheads - Size 10
    public static final RegistryObject<Item> MP_WARHEAD_10_HE = register(MISSILE_TAB, "mp_warhead_10_he",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.HE, 15F, 1.5F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(5F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_10_he");

    public static final RegistryObject<Item> MP_WARHEAD_10_INCENDIARY = register(MISSILE_TAB, "mp_warhead_10_incendiary",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.INC, 15F, 1.5F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(5F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_10_incendiary");

    public static final RegistryObject<Item> MP_WARHEAD_10_BUSTER = register(MISSILE_TAB, "mp_warhead_10_buster",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.BUSTER, 5F, 1.5F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(5F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_10_buster");

    public static final RegistryObject<Item> MP_WARHEAD_10_NUCLEAR = register(MISSILE_TAB, "mp_warhead_10_nuclear",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.NUCLEAR, 35F, 1.5F, ItemCustomMissilePart.PartSize.SIZE_10).setTitle("Tater Tot").setHealth(10F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_10_nuclear");

    public static final RegistryObject<Item> MP_WARHEAD_10_NUCLEAR_LARGE = register(MISSILE_TAB, "mp_warhead_10_nuclear_large",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.NUCLEAR, 75F, 2.5F, ItemCustomMissilePart.PartSize.SIZE_10).setTitle("Chernobyl Boris").setHealth(15F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_10_nuclear_large");

    public static final RegistryObject<Item> MP_WARHEAD_10_TAINT = register(MISSILE_TAB, "mp_warhead_10_taint",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.TAINT, 15F, 1.5F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(20F).setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setWittyText("Eat my taint! Bureaucracy is dead and we killed it!"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_10_taint");

    public static final RegistryObject<Item> MP_WARHEAD_10_CLOUD = register(MISSILE_TAB, "mp_warhead_10_cloud",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.CLOUD, 15F, 1.5F, ItemCustomMissilePart.PartSize.SIZE_10).setHealth(20F).setRarity(ItemCustomMissilePart.Rarity.RARE),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_10_taint");

    // MP Warheads - Size 15
    public static final RegistryObject<Item> MP_WARHEAD_15_HE = register(MISSILE_TAB, "mp_warhead_15_he",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.HE, 50F, 2.5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(10F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_15_he");

    public static final RegistryObject<Item> MP_WARHEAD_15_INCENDIARY = register(MISSILE_TAB, "mp_warhead_15_incendiary",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.INC, 35F, 2.5F, ItemCustomMissilePart.PartSize.SIZE_15).setHealth(10F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_15_incendiary");

    public static final RegistryObject<Item> MP_WARHEAD_15_NUCLEAR = register(MISSILE_TAB, "mp_warhead_15_nuclear",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.NUCLEAR, 125F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setTitle("Auntie Bertha").setHealth(15F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_15_nuclear");

    public static final RegistryObject<Item> MP_WARHEAD_15_NUCLEAR_SHARK = register(MISSILE_TAB, "mp_warhead_15_nuclear_shark",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.NUCLEAR, 125F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setTitle("Auntie Bertha").setHealth(15F))
                    .setRarity(ItemCustomMissilePart.Rarity.UNCOMMON).setTitle("Discount Bullet Bill").setWittyText("Nose art on a cannon bullet? Who does that?"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_15_nuclear");

    public static final RegistryObject<Item> MP_WARHEAD_15_NUCLEAR_MIMI = register(MISSILE_TAB, "mp_warhead_15_nuclear_mimi",
            () -> (new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.NUCLEAR, 125F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setTitle("Auntie Bertha").setHealth(15F))
                    .setRarity(ItemCustomMissilePart.Rarity.RARE).setTitle("FASHIONABLE MISSILE"),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_15_nuclear");

    public static final RegistryObject<Item> MP_WARHEAD_15_BOXCAR = register(MISSILE_TAB, "mp_warhead_15_boxcar",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.TX, 250F, 7.5F, ItemCustomMissilePart.PartSize.SIZE_15).setWittyText("?!?!").setHealth(35F).setRarity(ItemCustomMissilePart.Rarity.LEGENDARY),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_15_boxcar");

    public static final RegistryObject<Item> MP_WARHEAD_15_N2 = register(MISSILE_TAB, "mp_warhead_15_n2",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.N2, 100F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setWittyText("[screams geometrically]").setHealth(20F).setRarity(ItemCustomMissilePart.Rarity.RARE),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_15_n2");

    public static final RegistryObject<Item> MP_WARHEAD_15_BALEFIRE = register(MISSILE_TAB, "mp_warhead_15_balefire",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.BALEFIRE, 100F, 7.5F, ItemCustomMissilePart.PartSize.SIZE_15).setRarity(ItemCustomMissilePart.Rarity.LEGENDARY).setAuthor("VT-6/24").setHealth(15F).setWittyText("Hightower, never forgetti."),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_15_balefire");

    public static final RegistryObject<Item> MP_WARHEAD_15_TURBINE = register(MISSILE_TAB, "mp_warhead_15_turbine",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeWarhead(ItemCustomMissilePart.WarheadType.TURBINE, 200F, 5F, ItemCustomMissilePart.PartSize.SIZE_15).setRarity(ItemCustomMissilePart.Rarity.SEWS_CLOTHES_AND_SUCKS_HORSE_COCK).setHealth(250F),
            ItemModelType.OBJ_ITEM, "models/item/missile_parts/mp_w_15_turbine");

    // MP Chips
    public static final RegistryObject<Item> MP_CHIP_1 = register(MISSILE_TAB, "mp_c_1",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeChip(0.1F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MP_CHIP_2 = register(MISSILE_TAB, "mp_c_2",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeChip(0.05F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MP_CHIP_3 = register(MISSILE_TAB, "mp_c_3",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeChip(0.01F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MP_CHIP_4 = register(MISSILE_TAB, "mp_c_4",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeChip(0.005F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MP_CHIP_5 = register(MISSILE_TAB, "mp_c_5",
            () -> new ItemCustomMissilePart(new Item.Properties()).makeChip(0.0F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BALLISTITE = register(PARTS_TAB, "ballistite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CORDITE = register(PARTS_TAB, "cordite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOY_BULLET = register(PARTS_TAB, "boy_bullet",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> BOY_TARGET = register(PARTS_TAB, "boy_target",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> BOY_SHIELDING = register(PARTS_TAB, "boy_shielding",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> SPHERE_STEEL = register(PARTS_TAB, "sphere_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Billets
    public static final RegistryObject<Item> BILLET_URANIUM = register(PARTS_TAB, "billet_uranium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_U233 = register(PARTS_TAB, "billet_u233",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_U235 = register(PARTS_TAB, "billet_u235",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_U238 = register(PARTS_TAB, "billet_u238",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_TH232 = register(PARTS_TAB, "billet_th232",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PLUTONIUM = register(PARTS_TAB, "billet_plutonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PU238 = register(PARTS_TAB, "billet_pu238",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PU239 = register(PARTS_TAB, "billet_pu239",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PU240 = register(PARTS_TAB, "billet_pu240",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PU241 = register(PARTS_TAB, "billet_pu241",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PU_MIX = register(PARTS_TAB, "billet_pu_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_AM241 = register(PARTS_TAB, "billet_am241",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_AM242 = register(PARTS_TAB, "billet_am242",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_AM_MIX = register(PARTS_TAB, "billet_am_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_NEPTUNIUM = register(PARTS_TAB, "billet_neptunium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_POLONIUM = register(PARTS_TAB, "billet_polonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_TECHNETIUM = register(PARTS_TAB, "billet_technetium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_COBALT = register(PARTS_TAB, "billet_cobalt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_CO60 = register(PARTS_TAB, "billet_co60",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_SR90 = register(PARTS_TAB, "billet_sr90",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_AU198 = register(PARTS_TAB, "billet_au198",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PB209 = register(PARTS_TAB, "billet_pb209",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_RA226 = register(PARTS_TAB, "billet_ra226",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_ACTINIUM = register(PARTS_TAB, "billet_actinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_SCHRABIDIUM = register(PARTS_TAB, "billet_schrabidium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_SOLINIUM = register(PARTS_TAB, "billet_solinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_GH336 = register(PARTS_TAB, "billet_gh336",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_AUSTRALIUM = register(PARTS_TAB, "billet_australium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_AUSTRALIUM_LESSER = register(PARTS_TAB, "billet_australium_lesser",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_AUSTRALIUM_GREATER = register(PARTS_TAB, "billet_australium_greater",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_URANIUM_FUEL = register(PARTS_TAB, "billet_uranium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_THORIUM_FUEL = register(PARTS_TAB, "billet_thorium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PLUTONIUM_FUEL = register(PARTS_TAB, "billet_plutonium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_NEPTUNIUM_FUEL = register(PARTS_TAB, "billet_neptunium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_MOX_FUEL = register(PARTS_TAB, "billet_mox_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_AMERICIUM_FUEL = register(PARTS_TAB, "billet_americium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_LES = register(PARTS_TAB, "billet_les",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_SCHRABIDIUM_FUEL = register(PARTS_TAB, "billet_schrabidium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_HES = register(PARTS_TAB, "billet_hes",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PO210BE = register(PARTS_TAB, "billet_po210be",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_RA226BE = register(PARTS_TAB, "billet_ra226be",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_PU238BE = register(PARTS_TAB, "billet_pu238be",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_YHARONITE = register(PARTS_TAB, "billet_yharonite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_BALEFIRE_GOLD = register(PARTS_TAB, "billet_balefire_gold",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_FLASHLEAD = register(PARTS_TAB, "billet_flashlead",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_ZFB_BISMUTH = register(PARTS_TAB, "billet_zfb_bismuth",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_ZFB_PU241 = register(PARTS_TAB, "billet_zfb_pu241",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_ZFB_AM_MIX = register(PARTS_TAB, "billet_zfb_am_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_BERYLLIUM = register(PARTS_TAB, "billet_beryllium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_BISMUTH = register(PARTS_TAB, "billet_bismuth",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_ZIRCONIUM = register(PARTS_TAB, "billet_zirconium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_NUCLEAR_WASTE = register(PARTS_TAB, "billet_nuclear_waste",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BILLET_SILICON = register(PARTS_TAB, "billet_silicon",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEMON_CORE_CLOSED = register(PARTS_TAB, "demon_core_closed",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ASSEMBLY_NUKE = register(PARTS_TAB, "assembly_nuke",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WEAPONIZED_STARBLASTER_CELL = register(WEAPON_TAB, "weaponized_starblaster_cell",
            () -> new WeaponizedCell(new Item.Properties().stacksTo(1)), ItemModelType.GENERATED);

    public static final RegistryObject<Item> N2_CHARGE = register(NUKE_TAB, "bomb/n2",
            () -> new ItemN2(ModBlocks.NUKE_N2.get(), new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DET_NUKE = register(BLOCK_TAB, "det_nuke",
            () -> new BlockItem(ModBlocks.DET_NUKE.get(), new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ARC_ELECTRODE_GRAPHITE = register(MACHINE_TAB, "arc_electrode_graphite",
            () -> new ItemArcElectrode(new Item.Properties(), 10),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ARC_ELECTRODE_LANTHANIUM = register(MACHINE_TAB, "arc_electrode_lanthanium",
            () -> new ItemArcElectrode(new Item.Properties(), 100),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ARC_ELECTRODE_DESH = register(MACHINE_TAB, "arc_electrode_desh",
            () -> new ItemArcElectrode(new Item.Properties(), 500),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ARC_ELECTRODE_SATURNITE = register(MACHINE_TAB, "arc_electrode_saturnite",
            () -> new ItemArcElectrode(new Item.Properties(), 1500),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LAMP_DEMON = register(MACHINE_TAB, "machines/lamp_demon",
            () -> new DemonLampItem(ModBlocks.LAMP_DEMON.get(), new Item.Properties()));

    public static final RegistryObject<Item> BOXCAR_ITEM = register(MACHINE_TAB, "boxcar",
            () -> new BoxcarItem(ModBlocks.BOXCAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> BOAT_ITEM = register(MACHINE_TAB, "boat",
            () -> new BoatItem(ModBlocks.BOAT.get(), new Item.Properties()));

    public static final RegistryObject<Item> WATCH = register(PARTS_TAB, "watch",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Drill bits
    public static final RegistryObject<Item> DRILLBIT_STEEL = register(MACHINE_TAB, "drillbit_steel",
            () -> new ItemDrillbit(new Item.Properties().durability(500), ItemDrillbit.EnumDrillType.STEEL),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRILLBIT_STEEL_DIAMOND = register(MACHINE_TAB, "drillbit_steel_diamond",
            () -> new ItemDrillbit(new Item.Properties().durability(1200), ItemDrillbit.EnumDrillType.STEEL_DIAMOND),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRILLBIT_HSS = register(MACHINE_TAB, "drillbit_hss",
            () -> new ItemDrillbit(new Item.Properties().durability(1500), ItemDrillbit.EnumDrillType.HSS),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRILLBIT_HSS_DIAMOND = register(MACHINE_TAB, "drillbit_hss_diamond",
            () -> new ItemDrillbit(new Item.Properties().durability(2500), ItemDrillbit.EnumDrillType.HSS_DIAMOND),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRILLBIT_DESH = register(MACHINE_TAB, "drillbit_desh",
            () -> new ItemDrillbit(new Item.Properties().durability(3000), ItemDrillbit.EnumDrillType.DESH),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRILLBIT_DESH_DIAMOND = register(MACHINE_TAB, "drillbit_desh_diamond",
            () -> new ItemDrillbit(new Item.Properties().durability(5000), ItemDrillbit.EnumDrillType.DESH_DIAMOND),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRILLBIT_TCALLOY = register(MACHINE_TAB, "drillbit_tcalloy",
            () -> new ItemDrillbit(new Item.Properties().durability(6000), ItemDrillbit.EnumDrillType.TCALLOY),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRILLBIT_TCALLOY_DIAMOND = register(MACHINE_TAB, "drillbit_tcalloy_diamond",
            () -> new ItemDrillbit(new Item.Properties().durability(10000), ItemDrillbit.EnumDrillType.TCALLOY_DIAMOND),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRILLBIT_FERRO = register(MACHINE_TAB, "drillbit_ferro",
            () -> new ItemDrillbit(new Item.Properties().durability(8000), ItemDrillbit.EnumDrillType.FERRO),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DRILLBIT_FERRO_DIAMOND = register(MACHINE_TAB, "drillbit_ferro_diamond",
            () -> new ItemDrillbit(new Item.Properties().durability(15000), ItemDrillbit.EnumDrillType.FERRO_DIAMOND),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STONE_DEPTH_ITEM = register(BLOCK_TAB, "stone_depth",
            () -> new BlockItem(ModBlocks.STONE_DEPTH.get(), new Item.Properties()));

    public static final RegistryObject<Item> STONE_DEPTH_NETHER_ITEM = register(BLOCK_TAB, "stone_depth_nether",
            () -> new BlockItem(ModBlocks.STONE_DEPTH_NETHER.get(), new Item.Properties()));

    public static final RegistryObject<Item> TURRET_SENTRY = ModItems.register(WEAPON_TAB, "turrets/turret_sentry",
            () -> new TurretSentryItem(ModBlocks.TURRET_SENTRY.get(), new Item.Properties()));
    public static final RegistryObject<Item> TURRET_SENTRY_DAMAGED = ModItems.register(WEAPON_TAB, "turrets/turret_sentry_damaged",
            () -> new TurretSentryDamagedItem(ModBlocks.TURRET_SENTRY_DAMAGED.get(), new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_TRANSFORMER = ModItems.register(MACHINE_TAB, "machine_transformer",
            () -> new BlockItem(ModBlocks.MACHINE_TRANSFORMER.get(), new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_TRANSFORMER_DNT = ModItems.register(MACHINE_TAB, "machine_transformer_dnt",
            () -> new BlockItem(ModBlocks.MACHINE_TRANSFORMER_DNT.get(), new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_SHREDDER = ModItems.register(MACHINE_TAB, "machines/machine_shredder",
            () -> new BlockItem(ModBlocks.MACHINE_SHREDDER.get(), new Item.Properties()));

    public static final RegistryObject<Item> EMP_BOMB = ModItems.register(NUKE_TAB, "emp_bomb",
            () -> new BlockItem(ModBlocks.EMP_BOMB.get(), new Item.Properties()));

    public static final RegistryObject<Item> FLOAT_BOMB = ModItems.register(NUKE_TAB, "float_bomb",
            () -> new BlockItem(ModBlocks.FLOAT_BOMB.get(), new Item.Properties()));

    public static final RegistryObject<Item> STEEL_CHAIN = ModItems.register(NUKE_TAB, "steel_chain",
            () -> new BlockItem(ModBlocks.STEEL_CHAIN.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRATE_ITEM = ModItems.register(MACHINE_TAB, "storage/crate",
            () -> new BlockItem(ModBlocks.CRATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_WEAPON_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_weapon",
            () -> new BlockItem(ModBlocks.CRATE_WEAPON.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_AMMO = register(MACHINE_TAB, "storage/crate_ammo",
            () -> new BlockItem(ModBlocks.CRATE_AMMO.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_CAN_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_can",
            () -> new BlockItem(ModBlocks.CRATE_CAN.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_LEAD_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_lead",
            () -> new BlockItem(ModBlocks.CRATE_LEAD.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_METAL_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_metal",
            () -> new BlockItem(ModBlocks.CRATE_METAL.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_RED_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_red",
            () -> new BlockItem(ModBlocks.CRATE_RED.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_JUNGLE_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_jungle",
            () -> new BlockItem(ModBlocks.CRATE_JUNGLE.get(), new Item.Properties()));

    public static final RegistryObject<Item> CRATE_IRON_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_iron",
            () -> new BlockItem(ModBlocks.CRATE_IRON.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_STEEL_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_steel",
            () -> new BlockItem(ModBlocks.CRATE_STEEL.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_DESH_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_desh",
            () -> new BlockItem(ModBlocks.CRATE_DESH.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_TUNGSTEN_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_tungsten",
            () -> new BlockItem(ModBlocks.CRATE_TUNGSTEN.get(), new Item.Properties()));
    public static final RegistryObject<Item> CRATE_TEMPLATE_ITEM = ModItems.register(MACHINE_TAB, "storage/crate_template",
            () -> new BlockItem(ModBlocks.CRATE_TEMPLATE.get(), new Item.Properties()));
    public static final RegistryObject<Item> SAFE_ITEM = ModItems.register(MACHINE_TAB, "storage/safe",
            () -> new BlockItem(ModBlocks.SAFE.get(), new Item.Properties()));


    public static final RegistryObject<Item> TURRET_CHIP = register(WEAPON_TAB, "turret_chip",
            () -> new ItemTurretChip(ItemTurretChip.createProperties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> TURRET_BIOMETRY = register(WEAPON_TAB, "turret_biometry",
            () -> new ItemTurretBiometry(ItemTurretBiometry.createProperties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> STEEL_WALL = register(BLOCK_TAB, "steel_wall",
            () -> new BlockItem(ModBlocks.STEEL_WALL.get(), new Item.Properties()));

    public static final RegistryObject<Item> STEEL_CORNER = register(BLOCK_TAB, "steel_corner",
            () -> new BlockItem(ModBlocks.STEEL_CORNER.get(), new Item.Properties()));

    public static final RegistryObject<Item> STEEL_ROOF = register(BLOCK_TAB, "steel_roof",
            () -> new BlockItem(ModBlocks.STEEL_ROOF.get(), new Item.Properties()));

    public static final RegistryObject<Item> MUSH_BLOCK = ModItems.register(MACHINE_TAB, "mush_block",
            () -> new BlockItem(ModBlocks.MUSH_BLOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> MUSH_BLOCK_STEM = ModItems.register(MACHINE_TAB, "mush_block_stem",
            () -> new BlockItem(ModBlocks.MUSH_BLOCK_STEM.get(), new Item.Properties()));

    public static final RegistryObject<Item> LEMON = register(CONSUMABLE_TAB, "lemon",
            () -> new ItemLemon(ItemLemon.createFoodProperties(4, 0.3F)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MED_IPECAC = register(CONSUMABLE_TAB, "med_ipecac",
            () -> new ItemLemon(ItemLemon.createFoodProperties(0, 0, true)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MED_PTSD = register(CONSUMABLE_TAB, "med_ptsd",
            () -> new ItemLemon(ItemLemon.createFoodProperties(0, 0, true)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MED_SCHIZOPHRENIA = register(CONSUMABLE_TAB, "med_schizophrenia",
            () -> new ItemLemon(ItemLemon.createFoodProperties(0, 0, true)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LOOPS = register(CONSUMABLE_TAB, "loops",
            () -> new ItemLemon(ItemLemon.createFoodProperties(5, 0.6F)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LOOP_STEW = register(CONSUMABLE_TAB, "loop_stew",
            () -> new ItemLemon(ItemLemon.createFoodProperties(10, 1.2F)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> TWINKIE = register(CONSUMABLE_TAB, "twinkie",
            () -> new ItemLemon(ItemLemon.createFoodProperties(6, 0.5F)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PUDDING = register(CONSUMABLE_TAB, "pudding",
            () -> new ItemLemon(ItemLemon.createFoodProperties(4, 0.4F)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PEAS = register(CONSUMABLE_TAB, "peas",
            () -> new ItemLemon(ItemLemon.createFoodProperties(3, 0.3F)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> QUESADILLA = register(CONSUMABLE_TAB, "quesadilla",
            () -> new ItemLemon(ItemLemon.createFoodProperties(8, 0.8F)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLOCK_FALLOUT = ModItems.register(BLOCK_TAB, "block_fallout",
            () -> new BlockItem(ModBlocks.BLOCK_FALLOUT.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLASS_BORON = register(BLOCK_TAB, "glass_boron",
            () -> new BlockItem(ModBlocks.GLASS_BORON.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLASS_LEAD = register(BLOCK_TAB, "glass_lead",
            () -> new BlockItem(ModBlocks.GLASS_LEAD.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLASS_URANIUM = register(BLOCK_TAB, "glass_uranium",
            () -> new BlockItem(ModBlocks.GLASS_URANIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLASS_TRINITITE = register(BLOCK_TAB, "glass_trinitite",
            () -> new BlockItem(ModBlocks.GLASS_TRINITITE.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLASS_POLONIUM = register(BLOCK_TAB, "glass_polonium",
            () -> new BlockItem(ModBlocks.GLASS_POLONIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLASS_ASH = register(BLOCK_TAB, "glass_ash",
            () -> new BlockItem(ModBlocks.GLASS_ASH.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLASS_QUARTZ = register(BLOCK_TAB, "glass_quartz",
            () -> new BlockItem(ModBlocks.GLASS_QUARTZ.get(), new Item.Properties()));

    public static final RegistryObject<Item> GLASS_POLARIZED = register(BLOCK_TAB, "glass_polarized",
            () -> new BlockItem(ModBlocks.GLASS_POLARIZED.get(), new Item.Properties()));

    public static final RegistryObject<Item> BOTTLE_MERCURY = register(CONSUMABLE_TAB, "bottle_mercury",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> IV_BLOOD = register(CONSUMABLE_TAB, "iv_blood",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> IV_XP = register(CONSUMABLE_TAB, "iv_xp",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> IV_EMPTY = register(CONSUMABLE_TAB, "iv_empty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> IV_XP_EMPTY = register(CONSUMABLE_TAB, "iv_xp_empty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_TRITIUM = register(PARTS_TAB, "rod_zirnox_tritium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_EMPTY = register(PARTS_TAB, "rod_zirnox_empty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_EMPTY = register(CONTROL_TAB, "particle_empty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_HYDROGEN = register(CONTROL_TAB, "particle_hydrogen",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_COPPER = register(CONTROL_TAB, "particle_copper",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_LEAD = register(CONTROL_TAB, "particle_lead",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_APROTON = register(CONTROL_TAB, "particle_aproton",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_AELECTRON = register(CONTROL_TAB, "particle_aelectron",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_AMAT = register(CONTROL_TAB, "particle_amat",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_ASCHRAB = register(CONTROL_TAB, "particle_aschrab",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_HIGGS = register(CONTROL_TAB, "particle_higgs",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_MUON = register(CONTROL_TAB, "particle_muon",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_TACHYON = register(CONTROL_TAB, "particle_tachyon",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_STRANGE = register(CONTROL_TAB, "particle_strange",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_DARK = register(CONTROL_TAB, "particle_dark",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_SPARKTICLE = register(CONTROL_TAB, "particle_sparkticle",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_DIGAMMA = register(CONTROL_TAB, "particle_digamma",
            () -> new ItemDigamma(new Item.Properties().stacksTo(1), 60) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTICLE_LUTECE = register(CONTROL_TAB, "particle_lutece",
            () -> new Item(new Item.Properties()) {
                @Override
                public ItemStack getCraftingRemainingItem(ItemStack stack) {
                    return new ItemStack(PARTICLE_EMPTY.get());
                }
            }, ItemModelType.GENERATED);

    public static final RegistryObject<Item> RING_PULL = register(CONSUMABLE_TAB, "ring_pull",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_EMPTY = register(CONSUMABLE_TAB, "can_empty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_SMART = register(CONSUMABLE_TAB, "can_smart",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)).makeCan(() -> CAN_EMPTY.get(), () -> RING_PULL.get()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_CREATURE = register(CONSUMABLE_TAB, "can_creature",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)).makeCan(() -> CAN_EMPTY.get(), () -> RING_PULL.get()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_REDBOMB = register(CONSUMABLE_TAB, "can_redbomb",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)).makeCan(() -> CAN_EMPTY.get(), () -> RING_PULL.get()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_MRSUGAR = register(CONSUMABLE_TAB, "can_mrsugar",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)).makeCan(() -> CAN_EMPTY.get(), () -> RING_PULL.get()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_OVERCHARGE = register(CONSUMABLE_TAB, "can_overcharge",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)).makeCan(() -> CAN_EMPTY.get(), () -> RING_PULL.get()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_LUNA = register(CONSUMABLE_TAB, "can_luna",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)).makeCan(() -> CAN_EMPTY.get(), () -> RING_PULL.get()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_BEPIS = register(CONSUMABLE_TAB, "can_bepis",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)).makeCan(() -> CAN_EMPTY.get(), () -> RING_PULL.get()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_BREEN = register(CONSUMABLE_TAB, "can_breen",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)).makeCan(() -> CAN_EMPTY.get(), () -> RING_PULL.get()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAN_MUG = register(CONSUMABLE_TAB, "can_mug",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)).makeCan(() -> CAN_EMPTY.get(), () -> RING_PULL.get()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAP_NUKA = register(CONSUMABLE_TAB, "cap_nuka",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAP_QUANTUM = register(CONSUMABLE_TAB, "cap_quantum",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAP_SPARKLE = register(CONSUMABLE_TAB, "cap_sparkle",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAP_RAD = register(CONSUMABLE_TAB, "cap_rad",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAP_KORL = register(CONSUMABLE_TAB, "cap_korl",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAP_FRITZ = register(CONSUMABLE_TAB, "cap_fritz",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE_EMPTY = register(CONSUMABLE_TAB, "bottle_empty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE_NUKA = register(CONSUMABLE_TAB, "bottle_nuka",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16))
                    .makeBottle(() -> BOTTLE_EMPTY.get(), () -> CAP_NUKA.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE_CHERRY = register(CONSUMABLE_TAB, "bottle_cherry",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16))
                    .makeBottle(() -> BOTTLE_EMPTY.get(), () -> CAP_NUKA.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE_QUANTUM = register(CONSUMABLE_TAB, "bottle_quantum",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16))
                    .makeBottle(() -> BOTTLE_EMPTY.get(), () -> CAP_QUANTUM.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE_SPARKLE = register(CONSUMABLE_TAB, "bottle_sparkle",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16))
                    .makeBottle(() -> BOTTLE_EMPTY.get(), () -> CAP_SPARKLE.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE_RAD = register(CONSUMABLE_TAB, "bottle_rad",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16))
                    .makeBottle(() -> BOTTLE_EMPTY.get(), () -> CAP_RAD.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE2_KORL = register(CONSUMABLE_TAB, "bottle2_korl",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16))
                    .makeBottle(() -> BOTTLE2_EMPTY.get(), () -> CAP_KORL.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLE2_FRITZ = register(CONSUMABLE_TAB, "bottle2_fritz",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16))
                    .makeBottle(() -> BOTTLE2_EMPTY.get(), () -> CAP_FRITZ.get()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CHOCOLATE_MILK = register(CONSUMABLE_TAB, "chocolate_milk",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> COFFEE = register(CONSUMABLE_TAB, "coffee",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> COFFEE_RADIUM = register(CONSUMABLE_TAB, "coffee_radium",
            () -> new ItemEnergy(new Item.Properties().stacksTo(16)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SPARK = register(CONTROL_TAB, "battery_spark",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 100000000, 2000000, 2000000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_POTATO = register(CONTROL_TAB, "battery_potato",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 1000, 0, 100),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_POTATOS = register(CONTROL_TAB, "battery_potatos",
            () -> new ItemPotatos(new Item.Properties().stacksTo(1), 500000, 0, 100),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_GENERIC = register(CONTROL_TAB, "battery_generic",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 5000, 100, 100),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_ADVANCED = register(CONTROL_TAB, "battery_advanced",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 20000, 500, 500),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_LITHIUM = register(CONTROL_TAB, "battery_lithium",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 250000, 1000, 1000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SCHRABIDIUM = register(CONTROL_TAB, "battery_schrabidium",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 1000000, 5000, 5000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_TRIXITE = register(CONTROL_TAB, "battery_trixite",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 5000000, 40000, 200000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_RED_CELL = register(CONTROL_TAB, "battery_red_cell",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 15000, 100, 100),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_RED_CELL_6 = register(CONTROL_TAB, "battery_red_cell_6",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 15000 * 6, 100, 100),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_RED_CELL_24 = register(CONTROL_TAB, "battery_red_cell_24",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 15000 * 24, 100, 100),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_ADVANCED_CELL = register(CONTROL_TAB, "battery_advanced_cell",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 60000, 500, 500),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_ADVANCED_CELL_4 = register(CONTROL_TAB, "battery_advanced_cell_4",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 60000 * 4, 500, 500),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_ADVANCED_CELL_12 = register(CONTROL_TAB, "battery_advanced_cell_12",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 60000 * 12, 500, 500),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_LITHIUM_CELL = register(CONTROL_TAB, "battery_lithium_cell",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 750000, 1000, 1000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_LITHIUM_CELL_3 = register(CONTROL_TAB, "battery_lithium_cell_3",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 750000 * 3, 1000, 1000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_LITHIUM_CELL_6 = register(CONTROL_TAB, "battery_lithium_cell_6",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 750000 * 6, 1000, 1000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SCHRABIDIUM_CELL = register(CONTROL_TAB, "battery_schrabidium_cell",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 3000000, 5000, 5000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SCHRABIDIUM_CELL_2 = register(CONTROL_TAB, "battery_schrabidium_cell_2",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 3000000 * 2, 5000, 5000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SCHRABIDIUM_CELL_4 = register(CONTROL_TAB, "battery_schrabidium_cell_4",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 3000000 * 4, 5000, 5000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SPARK_CELL_6 = register(CONTROL_TAB, "battery_spark_cell_6",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 100000000L * 6L, 2000000, 2000000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SPARK_CELL_25 = register(CONTROL_TAB, "battery_spark_cell_25",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 100000000L * 25L, 2000000, 2000000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SPARK_CELL_100 = register(CONTROL_TAB, "battery_spark_cell_100",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 100000000L * 100L, 2000000, 2000000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SPARK_CELL_1000 = register(CONTROL_TAB, "battery_spark_cell_1000",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 100000000L * 1000L, 20000000, 20000000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SPARK_CELL_2500 = register(CONTROL_TAB, "battery_spark_cell_2500",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 100000000L * 2500L, 20000000, 20000000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SPARK_CELL_10000 = register(CONTROL_TAB, "battery_spark_cell_10000",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 100000000L * 10000L, 200000000, 200000000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATTERY_SPARK_CELL_POWER = register(CONTROL_TAB, "battery_spark_cell_power",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 100000000L * 1000000L, 200000000, 200000000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CUBE_POWER = register(CONTROL_TAB, "cube_power",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 1000000000000000000L, 1000000000000000L, 1000000000000000L),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEV_BATTERY = register(CONTROL_TAB, "hev_battery",
            () -> new ItemFusionCore(new Item.Properties().stacksTo(4), 150000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> FUSION_CORE = register(CONTROL_TAB, "fusion_core",
            () -> new ItemFusionCore(new Item.Properties().stacksTo(1), 2500000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ENERGY_CORE = register(CONTROL_TAB, "energy_core",
            () -> new ItemBattery(new Item.Properties().stacksTo(1), 10000000, 0, 1000),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BOTTLED_CLOUD = register(CONSUMABLE_TAB, "bottle_cloud",
            () -> new ItemModCloud(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_NATURAL_URANIUM = register(PARTS_TAB, "waste_natural_uranium",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED, "waste_uranium");

    public static final RegistryObject<Item> WASTE_URANIUM = register(PARTS_TAB, "waste_uranium",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_THORIUM = register(PARTS_TAB, "waste_thorium",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_MOX = register(PARTS_TAB, "waste_mox",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_PLUTONIUM = register(PARTS_TAB, "waste_plutonium",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_U233 = register(PARTS_TAB, "waste_u233",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED, "waste_uranium");

    public static final RegistryObject<Item> WASTE_U235 = register(PARTS_TAB, "waste_u235",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED, "waste_uranium");

    public static final RegistryObject<Item> WASTE_SCHRABIDIUM = register(PARTS_TAB, "waste_schrabidium",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_ZFB_MOX = register(PARTS_TAB, "waste_zfb_mox",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_PLATE_U233 = register(PARTS_TAB, "waste_plate_u233",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED, "waste_plate_uranium");

    public static final RegistryObject<Item> WASTE_PLATE_U235 = register(PARTS_TAB, "waste_plate_u235",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED, "waste_plate_uranium");

    public static final RegistryObject<Item> WASTE_PLATE_MOX = register(PARTS_TAB, "waste_plate_mox",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_PLATE_PU239 = register(PARTS_TAB, "waste_plate_pu239",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED, "waste_plate_mox");

    public static final RegistryObject<Item> WASTE_PLATE_SA326 = register(PARTS_TAB, "waste_plate_sa326",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_PLATE_RA226BE = register(PARTS_TAB, "waste_plate_ra226be",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> WASTE_PLATE_PU238BE = register(PARTS_TAB, "waste_plate_pu238be",
            () -> new ItemDepletedFuel(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_URANIUM_FUEL_DEPLETED = register(CONTROL_TAB, "rod_zirnox_uranium_fuel_depleted",
            () -> new Item(new Item.Properties().stacksTo(64).craftRemainder(ModItems.ROD_ZIRNOX_EMPTY.get())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_THORIUM_FUEL_DEPLETED = register(CONTROL_TAB, "rod_zirnox_thorium_fuel_depleted",
            () -> new Item(new Item.Properties().stacksTo(64).craftRemainder(ModItems.ROD_ZIRNOX_EMPTY.get())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_MOX_FUEL_DEPLETED = register(CONTROL_TAB, "rod_zirnox_mox_fuel_depleted",
            () -> new Item(new Item.Properties().stacksTo(64).craftRemainder(ModItems.ROD_ZIRNOX_EMPTY.get())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_PLUTONIUM_FUEL_DEPLETED = register(CONTROL_TAB, "rod_zirnox_plutonium_fuel_depleted",
            () -> new Item(new Item.Properties().stacksTo(64).craftRemainder(ModItems.ROD_ZIRNOX_EMPTY.get())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_U233_FUEL_DEPLETED = register(CONTROL_TAB, "rod_zirnox_u233_fuel_depleted",
            () -> new Item(new Item.Properties().stacksTo(64).craftRemainder(ModItems.ROD_ZIRNOX_EMPTY.get())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_U235_FUEL_DEPLETED = register(CONTROL_TAB, "rod_zirnox_u235_fuel_depleted",
            () -> new Item(new Item.Properties().stacksTo(64).craftRemainder(ModItems.ROD_ZIRNOX_EMPTY.get())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_LES_FUEL_DEPLETED = register(CONTROL_TAB, "rod_zirnox_les_fuel_depleted",
            () -> new Item(new Item.Properties().stacksTo(64).craftRemainder(ModItems.ROD_ZIRNOX_EMPTY.get())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_ZIRNOX_ZFB_MOX_DEPLETED = register(CONTROL_TAB, "rod_zirnox_zfb_mox_depleted",
            () -> new Item(new Item.Properties().stacksTo(64).craftRemainder(ModItems.ROD_ZIRNOX_EMPTY.get())), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PROTECTION_CHARM = register(WEAPON_TAB, "protection_charm",
            () -> new ItemModCharm(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> METEOR_CHARM = register(WEAPON_TAB, "meteor_charm",
            () -> new ItemModCharm(), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILE_ROD_URANIUM = register(CONTROL_TAB, "pile_rod_uranium",
            () -> new ItemPileRod(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILE_ROD_PU239 = register(CONTROL_TAB, "pile_rod_pu239",
            () -> new ItemPileRod(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILE_ROD_PLUTONIUM = register(CONTROL_TAB, "pile_rod_plutonium",
            () -> new ItemPileRod(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILE_ROD_SOURCE = register(CONTROL_TAB, "pile_rod_source",
            () -> new ItemPileRod(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILE_ROD_BORON = register(CONTROL_TAB, "pile_rod_boron",
            () -> new ItemPileRod(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILE_ROD_LITHIUM = register(CONTROL_TAB, "pile_rod_lithium",
            () -> new ItemPileRod(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PILE_ROD_DETECTOR = register(CONTROL_TAB, "pile_rod_detector",
            () -> new ItemPileRod(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLOCK_METEOR_TREASURE = register(BLOCK_TAB, "block_meteor_treasure",
            () -> new BlockItem(ModBlocks.BLOCK_METEOR_TREASURE.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_OIL_EMPTY = register(BLOCK_TAB, "ore_oil_empty",
            () -> new BlockItem(ModBlocks.ORE_OIL_EMPTY.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_METEOR_IRON_ITEM = register(BLOCK_TAB, "ore_meteor_iron",
            () -> new BlockItem(ModBlocks.ORE_METEOR_IRON.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_METEOR_COPPER_ITEM = register(BLOCK_TAB, "ore_meteor_copper",
            () -> new BlockItem(ModBlocks.ORE_METEOR_COPPER.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_METEOR_ALUMINIUM_ITEM = register(BLOCK_TAB, "ore_meteor_aluminium",
            () -> new BlockItem(ModBlocks.ORE_METEOR_ALUMINIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_METEOR_RARE_EARTH_ITEM = register(BLOCK_TAB, "ore_meteor_rare_earth",
            () -> new BlockItem(ModBlocks.ORE_METEOR_RARE_EARTH.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_METEOR_COBALT_ITEM = register(BLOCK_TAB, "ore_meteor_cobalt",
            () -> new BlockItem(ModBlocks.ORE_METEOR_COBALT.get(), new Item.Properties()));

    public static final RegistryObject<Item> DECO_CRT = register(MACHINE_TAB, "deco_crt",
            () -> new SimpleOBJItem(ModBlocks.DECO_CRT.get(), new Item.Properties()));

    public static final RegistryObject<Item> BOMB_CALLER_CARPET = register(CONSUMABLE_TAB, "bomb_caller_carpet",
            () -> new ItemBombCaller(new Item.Properties().stacksTo(1), ItemBombCaller.Type.CARPET),
            ItemModelType.GENERATED, "bomb_caller");

    public static final RegistryObject<Item> BOMB_CALLER_NAPALM = register(CONSUMABLE_TAB, "bomb_caller_napalm",
            () -> new ItemBombCaller(new Item.Properties().stacksTo(1), ItemBombCaller.Type.NAPALM),
            ItemModelType.GENERATED, "bomb_caller");

    public static final RegistryObject<Item> BOMB_CALLER_GAS = register(CONSUMABLE_TAB, "bomb_caller_gas",
            () -> new ItemBombCaller(new Item.Properties().stacksTo(1), ItemBombCaller.Type.GAS),
            ItemModelType.GENERATED, "bomb_caller");

    public static final RegistryObject<Item> BOMB_CALLER_ORANGE = register(CONSUMABLE_TAB, "bomb_caller_orange",
            () -> new ItemBombCaller(new Item.Properties().stacksTo(1), ItemBombCaller.Type.ORANGE),
            ItemModelType.GENERATED, "bomb_caller");

    public static final RegistryObject<Item> BOMB_CALLER_NUKE = register(CONSUMABLE_TAB, "bomb_caller_nuke",
            () -> new ItemBombCaller(new Item.Properties().stacksTo(1), ItemBombCaller.Type.NUKE),
            ItemModelType.GENERATED, "bomb_caller");

    public static final RegistryObject<Item> BOMB_CALLER_STINGER = register(CONSUMABLE_TAB, "bomb_caller_stinger",
            () -> new ItemBombCaller(new Item.Properties().stacksTo(1), ItemBombCaller.Type.STINGER),
            ItemModelType.GENERATED, "bomb_caller");

    public static final RegistryObject<Item> BOMB_CALLER_BOXCAR = register(CONSUMABLE_TAB, "bomb_caller_boxcar",
            () -> new ItemBombCaller(new Item.Properties().stacksTo(1), ItemBombCaller.Type.BOXCAR),
            ItemModelType.GENERATED, "bomb_caller");

    public static final RegistryObject<Item> BOMB_CALLER_CLOUD = register(CONSUMABLE_TAB, "bomb_caller_cloud",
            () -> new ItemBombCaller(new Item.Properties().stacksTo(1), ItemBombCaller.Type.CLOUD),
            ItemModelType.GENERATED, "bomb_caller");

    public static final RegistryObject<Item> STEALTH_BOY = register(CONSUMABLE_TAB, "stealth_boy",
            () -> new ItemStealthBoy(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SERUM = register(WEAPON_TAB, "serum",
            () -> new ItemModSerum(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEART_PIECE = register(WEAPON_TAB, "heart_piece",
            () -> new ItemModHealth(new Item.Properties().stacksTo(1), 5F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEART_CONTAINER = register(WEAPON_TAB, "heart_container",
            () -> new ItemModHealth(new Item.Properties().stacksTo(1), 20F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEART_BOOSTER = register(WEAPON_TAB, "heart_booster",
            () -> new ItemModHealth(new Item.Properties().stacksTo(1), 40F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HEART_FAB = register(WEAPON_TAB, "heart_fab",
            () -> new ItemModHealth(new Item.Properties().stacksTo(1), 60F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLACK_DIAMOND = register(WEAPON_TAB, "black_diamond",
            () -> new ItemModHealth(new Item.Properties().stacksTo(1), 40F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SCRUMPY = register(WEAPON_TAB, "scrumpy",
            () -> new ItemModRevive(new Item.Properties().stacksTo(1).durability(1), 1),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> WILD_P = register(WEAPON_TAB, "wild_p",
            () -> new ItemModRevive(new Item.Properties().stacksTo(1).durability(3), 3),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LAUNCH_CODE_PIECE = register(PARTS_TAB, "launch_code_piece",
            () -> new Item(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LAUNCH_CODE = register(PARTS_TAB, "launch_code",
            () -> new Item(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LAUNCH_KEY = register(PARTS_TAB, "launch_key",
            () -> new Item(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CANNED_CONSERVE = register("canned_conserve",
            () -> new ItemConserve(new Item.Properties().stacksTo(64)),
            ItemModelType.ENUM_ITEM,
            ItemConserve.EnumFoodType.class);

    public static final RegistryObject<Item> CAN_KEY = register(CONSUMABLE_TAB, "can_key",
            () -> new Item(new Item.Properties().stacksTo(64)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLUEPRINT_FOLDER = register(TEMPLATE_TAB, "blueprint_folder",
            () -> new ItemBlueprintFolder(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLUEPRINTS = register(TEMPLATE_TAB, "blueprints",
            () -> new ItemBlueprints(new Item.Properties().stacksTo(64)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SPIDER_MILK = register(WEAPON_TAB, "spider_milk",
            () -> new ItemModMilk(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BANDAID = register(WEAPON_TAB, "bandaid",
            () -> new ItemModBandaid(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> WD40 = register(WEAPON_TAB, "wd40",
            () -> new ItemModWD40(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BLOCK_GRAPHITE = register(BLOCK_TAB, "block_graphite",
            () -> new BlockItem(ModBlocks.BLOCK_GRAPHITE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_GRAPHITE_DRILLED = register(BLOCK_TAB, "block_graphite_drilled",
            () -> new BlockItem(ModBlocks.BLOCK_GRAPHITE_DRILLED.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_GRAPHITE_FUEL = register(BLOCK_TAB, "block_graphite_fuel",
            () -> new BlockItem(ModBlocks.BLOCK_GRAPHITE_FUEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_GRAPHITE_PLUTONIUM = register(BLOCK_TAB, "block_graphite_plutonium",
            () -> new BlockItem(ModBlocks.BLOCK_GRAPHITE_PLUTONIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_GRAPHITE_ROD = register(BLOCK_TAB, "block_graphite_rod",
            () -> new BlockItem(ModBlocks.BLOCK_GRAPHITE_ROD.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_GRAPHITE_SOURCE = register(BLOCK_TAB, "block_graphite_source",
            () -> new BlockItem(ModBlocks.BLOCK_GRAPHITE_SOURCE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_GRAPHITE_LITHIUM = register(BLOCK_TAB, "block_graphite_lithium",
            () -> new BlockItem(ModBlocks.BLOCK_GRAPHITE_LITHIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_GRAPHITE_TRITIUM = register(BLOCK_TAB, "block_graphite_tritium",
            () -> new BlockItem(ModBlocks.BLOCK_GRAPHITE_TRITIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_GRAPHITE_DETECTOR = register(BLOCK_TAB, "block_graphite_detector",
            () -> new BlockItem(ModBlocks.BLOCK_GRAPHITE_DETECTOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAT_MAPPER_ITEM = register(MISSILE_TAB, "sat/sat_mapper",
            () -> new SatItem(ModBlocks.SAT_MAPPER.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAT_RADAR_ITEM = register(MISSILE_TAB, "sat/sat_radar",
            () -> new SatItem(ModBlocks.SAT_RADAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAT_SCANNER_ITEM = register(MISSILE_TAB, "sat/sat_scanner",
            () -> new SatItem(ModBlocks.SAT_SCANNER.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAT_LASER_ITEM = register(MISSILE_TAB, "sat/sat_laser",
            () -> new SatItem(ModBlocks.SAT_LASER.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAT_FOEQ_ITEM = register(MISSILE_TAB, "sat/sat_foeq",
            () -> new SatItem(ModBlocks.SAT_FOEQ.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAT_RESONATOR_ITEM = register(MISSILE_TAB, "sat/sat_resonator",
            () -> new SatItem(ModBlocks.SAT_RESONATOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> SAT_DOCK = register(MISSILE_TAB, "sat/sat_dock",
            () -> new SatItem(ModBlocks.SAT_DOCK.get(), new Item.Properties()));

    public static final RegistryObject<Item> TEKTITE = register(BLOCK_TAB, "tektite",
            () -> new BlockItem(ModBlocks.TEKTITE.get(), new Item.Properties()));

    public static final RegistryObject<Item> PRIBRIS = register(BLOCK_TAB, "rbmk/rbmk_debris",
            () -> new BlockItem(ModBlocks.PRIBRIS.get(), new Item.Properties()));

    public static final RegistryObject<Item> ORE_TEKTITE_OSMIRIDIUM = register(BLOCK_TAB, "ore_tektite_osmiridium",
            () -> new BlockItem(ModBlocks.ORE_TEKTITE_OSMIRIDIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> RING_STARMETAL = register(PARTS_TAB, "ring_starmetal",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> MYSTERYSHOVEL = register(CONTROL_TAB, "mystery_shovel",
            () -> new ItemMS(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_U238M2 = register(PARTS_TAB, "ingot_u238m2",
            () -> new ItemUnstable(new Item.Properties().stacksTo(1), 350, 200),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEBRIS_GRAPHITE = register(CONTROL_TAB, "debris_graphite",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEBRIS_METAL = register(CONTROL_TAB, "debris_metal",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEBRIS_FUEL = register(CONTROL_TAB, "debris_fuel",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEBRIS_CONCRETE = register(CONTROL_TAB, "debris_concrete",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEBRIS_EXCHANGER = register(CONTROL_TAB, "debris_exchanger",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEBRIS_SHRAPNEL = register(CONTROL_TAB, "debris_shrapnel",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEBRIS_ELEMENT = register(CONTROL_TAB, "debris_element",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ORE_BEDROCK = register("ore_bedrock_raw",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_bedrock", "ore_overlay");

    public static final RegistryObject<Item> ORE_CENTRIFUGED = register("ore_centrifuged",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_centrifuged", "ore_overlay");

    public static final RegistryObject<Item> ORE_CLEANED = register("ore_cleaned",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_cleaned", "ore_overlay");

    public static final RegistryObject<Item> ORE_SEPARATED = register("ore_separated",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_separated", "ore_overlay");

    public static final RegistryObject<Item> ORE_PURIFIED = register("ore_purified",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_purified", "ore_overlay");

    public static final RegistryObject<Item> ORE_NITRATED = register("ore_nitrated",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_nitrated", "ore_overlay");

    public static final RegistryObject<Item> ORE_NITROCRYSTALLINE = register("ore_nitrocrystalline",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_nitrocrystalline", "ore_overlay");

    public static final RegistryObject<Item> ORE_DEEPCLEANED = register("ore_deepcleaned",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_deepcleaned", "ore_overlay");

    public static final RegistryObject<Item> ORE_SEARED = register("ore_seared",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_seared", "ore_overlay");

    public static final RegistryObject<Item> ORE_ENRICHED = register("ore_enriched",
            () -> new ItemBedrockOre(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_enriched", "ore_overlay");

    public static final RegistryObject<Item> ORE_BYPRODUCT = register("ore_byproduct",
            () -> new ItemByproduct(new Item.Properties()),
            ItemModelType.ENUM_ITEM_TINTED,
            "ore_byproduct", "ore_byproduct_overlay");

    public static final RegistryObject<Item> FINS_FLAT = register(PARTS_TAB, "fins_flat",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FINS_SMALL_STEEL = register(PARTS_TAB, "fins_small_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FINS_BIG_STEEL = register(PARTS_TAB, "fins_big_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FINS_TRI_STEEL = register(PARTS_TAB, "fins_tri_steel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> FINS_QUAD_TITANIUM = register(PARTS_TAB, "fins_quad_titanium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTS_LEGENDARY_T1 = register(PARTS_TAB, "parts_legendary_t1",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTS_LEGENDARY_T2 = register(PARTS_TAB, "parts_legendary_t2",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PARTS_LEGENDARY_T3 = register(PARTS_TAB, "parts_legendary_t3",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LASER_CRYSTAL_CO2 = register(CONTROL_TAB, "laser_crystal_co2",
            () -> new ItemFELCrystal(new Item.Properties().stacksTo(1), ItemFELCrystal.EnumWavelengths.IR),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LASER_CRYSTAL_BISMUTH = register(CONTROL_TAB, "laser_crystal_bismuth",
            () -> new ItemFELCrystal(new Item.Properties().stacksTo(1), ItemFELCrystal.EnumWavelengths.VISIBLE),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LASER_CRYSTAL_CMB = register(CONTROL_TAB, "laser_crystal_cmb",
            () -> new ItemFELCrystal(new Item.Properties().stacksTo(1), ItemFELCrystal.EnumWavelengths.UV),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LASER_CRYSTAL_DNT = register(CONTROL_TAB, "laser_crystal_dnt",
            () -> new ItemFELCrystal(new Item.Properties().stacksTo(1), ItemFELCrystal.EnumWavelengths.GAMMA),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LASER_CRYSTAL_DIGAMMA = register(CONTROL_TAB, "laser_crystal_digamma",
            () -> new ItemFELCrystal(new Item.Properties().stacksTo(1), ItemFELCrystal.EnumWavelengths.DRX),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> RANGEFINDER = register(WEAPON_TAB, "rangefinder",
            () -> new ItemRangefinder(new Item.Properties().stacksTo(1)),
            ItemModelType.HANDHELD);

    public static final RegistryObject<Item> BOMB_WAFFLE = register(CONSUMABLE_TAB, "bomb_waffle",
            () -> new ItemWaffle(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(20)
                            .saturationMod(0.6F)
                            .build())
                    .stacksTo(16)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SCHNITZEL_VEGAN = register(CONSUMABLE_TAB, "schnitzel_vegan",
            () -> new ItemSchnitzelVegan(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0.1F)
                            .alwaysEat()
                            .build())
                    .stacksTo(64)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> COTTON_CANDY = register(CONSUMABLE_TAB, "cotton_candy",
            () -> new ItemCottonCandy(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(5)
                            .saturationMod(0.6F)
                            .alwaysEat()
                            .build())
                    .stacksTo(64)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> APPLE_SCHRABIDIUM_NUGGET = register(CONSUMABLE_TAB, "apple_schrabidium_nugget",
            () -> new ItemAppleSchrabidium(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(20).saturationMod(100).alwaysEat().build())
                    .stacksTo(64), 0),
            ItemModelType.GENERATED, "apple_schrabidium");

    public static final RegistryObject<Item> APPLE_SCHRABIDIUM_INGOT = register(CONSUMABLE_TAB, "apple_schrabidium_ingot",
            () -> new ItemAppleSchrabidium(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(20).saturationMod(100).alwaysEat().build())
                    .stacksTo(64), 1),
            ItemModelType.GENERATED, "apple_schrabidium");

    public static final RegistryObject<Item> APPLE_SCHRABIDIUM_BLOCK = register(CONSUMABLE_TAB, "apple_schrabidium_block",
            () -> new ItemAppleSchrabidium(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(20).saturationMod(100).alwaysEat().build())
                    .stacksTo(64), 2),
            ItemModelType.GENERATED, "apple_schrabidium");

    public static final RegistryObject<Item> APPLE_LEAD_NUGGET = register(CONSUMABLE_TAB, "apple_lead_nugget",
            () -> new ItemAppleSchrabidium(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(20).saturationMod(100).alwaysEat().build())
                    .stacksTo(64), 0),
            ItemModelType.GENERATED, "apple_lead");

    public static final RegistryObject<Item> APPLE_LEAD_INGOT = register(CONSUMABLE_TAB, "apple_lead_ingot",
            () -> new ItemAppleSchrabidium(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(20).saturationMod(100).alwaysEat().build())
                    .stacksTo(64), 1),
            ItemModelType.GENERATED, "apple_lead");

    public static final RegistryObject<Item> APPLE_LEAD_BLOCK = register(CONSUMABLE_TAB, "apple_lead_block",
            () -> new ItemAppleSchrabidium(new Item.Properties()
                    .food(new FoodProperties.Builder().nutrition(20).saturationMod(100).alwaysEat().build())
                    .stacksTo(64), 2),
            ItemModelType.GENERATED, "apple_lead");

    public static final RegistryObject<Item> APPLE_EUPHEMIUM = register(CONSUMABLE_TAB, "apple_euphemium",
            () -> new ItemAppleEuphemium(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(20)
                            .saturationMod(100)
                            .alwaysEat()
                            .build())
                    .stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> TEM_FLAKES_CHEAP = register(CONSUMABLE_TAB, "tem_flakes_cheap",
            () -> new ItemTemFlakes(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0)
                            .alwaysEat()
                            .build())
                    .stacksTo(64), 0),
            ItemModelType.GENERATED, "tem_flakes");

    public static final RegistryObject<Item> TEM_FLAKES_NORMAL = register(CONSUMABLE_TAB, "tem_flakes_normal",
            () -> new ItemTemFlakes(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0)
                            .alwaysEat()
                            .build())
                    .stacksTo(64), 1),
            ItemModelType.GENERATED, "tem_flakes");

    public static final RegistryObject<Item> TEM_FLAKES_EXPENSIVE = register(CONSUMABLE_TAB, "tem_flakes_expensive",
            () -> new ItemTemFlakes(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(0)
                            .saturationMod(0)
                            .alwaysEat()
                            .build())
                    .stacksTo(64), 2),
            ItemModelType.GENERATED, "tem_flakes");

    public static final RegistryObject<Item> GLOWING_STEW = register(CONSUMABLE_TAB, "glowing_stew",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(6)
                            .saturationMod(0.6F)
                            .build())
                    .stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BALEFIRE_SCRAMBLED = register(CONSUMABLE_TAB, "balefire_scrambled",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(6)
                            .saturationMod(0.6F)
                            .build())
                    .stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BALEFIRE_AND_HAM = register(CONSUMABLE_TAB, "balefire_and_ham",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(6)
                            .saturationMod(0.6F)
                            .build())
                    .stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PANCAKE = register(CONSUMABLE_TAB, "pancake",
            () -> new ItemPancake(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(20)
                            .saturationMod(20)
                            .alwaysEat()
                            .build())
                    .stacksTo(16)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_SMORE = register(PARTS_TAB, "ingot_smore",
            () -> new Item(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(10)
                            .saturationMod(20)
                            .build())
                    .stacksTo(64)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MARSHMALLOW_NORMAL = register(CONSUMABLE_TAB, "marshmallow_normal",
            () -> new ItemMarshmallow(new Item.Properties()
                    .stacksTo(1), 0),
            ItemModelType.GENERATED, "marshmallow");

    public static final RegistryObject<Item> MARSHMALLOW_ROASTED = register(CONSUMABLE_TAB, "marshmallow_roasted",
            () -> new ItemMarshmallow(new Item.Properties()
                    .stacksTo(1), 1),
            ItemModelType.GENERATED, "marshmallow_roasted");

    public static final RegistryObject<Item> MUCHO_MANGO = register(CONSUMABLE_TAB, "mucho_mango",
            () -> new ItemMuchoMango(new Item.Properties()
                    .food(new FoodProperties.Builder()
                            .nutrition(10)
                            .saturationMod(0.6F)
                            .alwaysEat()
                            .build())
                    .stacksTo(16)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CANTEEN_VODKA = register(CONSUMABLE_TAB, "canteen_vodka",
            () -> new ItemCanteen(new Item.Properties()
                    .durability(180)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ROD_EMPTY = register(PARTS_TAB, "rod_empty",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CIGARETTE = register(CONSUMABLE_TAB, "cigarette",
            () -> new ItemCigarette(new Item.Properties()
                    .stacksTo(16), false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CRACKPIPE = register(CONSUMABLE_TAB, "crackpipe",
            () -> new ItemCigarette(new Item.Properties()
                    .stacksTo(16), true),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLANT_TOBACCO = register(PARTS_TAB, "plant_tobacco",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED, "plant_tobacco");

    public static final RegistryObject<Item> PLANT_ROPE = register(PARTS_TAB, "plant_rope",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED, "plant_rope");

    public static final RegistryObject<Item> PLANT_MUSTARDWILLOW = register(PARTS_TAB, "plant_mustardwillow",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED, "plant_mustardwillow");

    public static final RegistryObject<Item> CATALYTIC_CONVERTER = register(PARTS_TAB, "catalytic_converter",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_KEVLAR = register(PARTS_TAB, "insert_kevlar",
            () -> new ItemModInsert(new Item.Properties().durability(1500), 1F, 0.9F, 1F, 1F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_SAPI = register(PARTS_TAB, "insert_sapi",
            () -> new ItemModInsert(new Item.Properties().durability(1750), 1F, 0.85F, 1F, 1F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_ESAPI = register(PARTS_TAB, "insert_esapi",
            () -> new ItemModInsert(new Item.Properties().durability(2000), 0.95F, 0.8F, 1F, 1F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_XSAPI = register(PARTS_TAB, "insert_xsapi",
            () -> new ItemModInsert(new Item.Properties().durability(2500), 0.9F, 0.75F, 1F, 1F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_STEEL = register(PARTS_TAB, "insert_steel",
            () -> new ItemModInsert(new Item.Properties().durability(1000), 1F, 0.95F, 0.75F, 0.95F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_DU = register(PARTS_TAB, "insert_du",
            () -> new ItemModInsert(new Item.Properties().durability(1500), 0.9F, 0.85F, 0.5F, 0.9F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_POLONIUM = register(PARTS_TAB, "insert_polonium",
            () -> new ItemModInsert(new Item.Properties().durability(500), 0.9F, 1F, 0.95F, 0.9F, true),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_GHIORSIUM = register(PARTS_TAB, "insert_ghiorsium",
            () -> new ItemModInsert(new Item.Properties().durability(2000), 0.8F, 0.75F, 0.35F, 0.9F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_ERA = register(PARTS_TAB, "insert_era",
            () -> new ItemModInsert(new Item.Properties().durability(25), 0.5F, 1F, 0.25F, 1F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_YHARONITE = register(PARTS_TAB, "insert_yharonite",
            () -> new ItemModInsert(new Item.Properties().durability(9999), 0.01F, 1F, 1F, 1F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INSERT_DOXIUM = register(PARTS_TAB, "insert_doxium",
            () -> new ItemModInsert(new Item.Properties().durability(9999), 5.0F, 1F, 1F, 1F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> AUSTRALIUM_III = register(PARTS_TAB, "australium_iii",
            () -> new ItemModShield(new Item.Properties().stacksTo(1), 25F),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SERVO_SET = register(PARTS_TAB, "servo_set",
            () -> new ItemModServos(new Item.Properties().stacksTo(1), false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SERVO_SET_DESH = register(PARTS_TAB, "servo_set_desh",
            () -> new ItemModServos(new Item.Properties().stacksTo(1), true),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PADS_RUBBER = register(PARTS_TAB, "pads_rubber",
            () -> new ItemModPads(new Item.Properties().stacksTo(1), 0.5F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PADS_SLIME = register(PARTS_TAB, "pads_slime",
            () -> new ItemModPads(new Item.Properties().stacksTo(1), 0.25F, false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PADS_STATIC = register(PARTS_TAB, "pads_static",
            () -> new ItemModPads(new Item.Properties().stacksTo(1), 0.75F, true),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ARMOR_BATTERY = register(PARTS_TAB, "armor_battery",
            () -> new ItemModBattery(1.25D),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ARMOR_BATTERY_MK2 = register(PARTS_TAB, "armor_battery_mk2",
            () -> new ItemModBattery(1.5D),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ARMOR_BATTERY_MK3 = register(PARTS_TAB, "armor_battery_mk3",
            () -> new ItemModBattery(2D),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CAPACITOR_BUS = register(MACHINE_TAB, "capacitor_bus",
            () -> new BlockItem(ModBlocks.CAPACITOR_BUS.get(), new Item.Properties()));

    public static final RegistryObject<Item> CAPACITOR_COPPER = register(MACHINE_TAB, "capacitor_copper",
            () -> new CapacitorItem(ModBlocks.CAPACITOR_COPPER.get(), new Item.Properties()));

    public static final RegistryObject<Item> CAPACITOR_GOLD = register(MACHINE_TAB, "capacitor_gold",
            () -> new CapacitorItem(ModBlocks.CAPACITOR_GOLD.get(), new Item.Properties()));

    public static final RegistryObject<Item> CAPACITOR_NIOBIUM = register(MACHINE_TAB, "capacitor_niobium",
            () -> new CapacitorItem(ModBlocks.CAPACITOR_NIOBIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> CAPACITOR_TANTALIUM = register(MACHINE_TAB, "capacitor_tantalium",
            () -> new CapacitorItem(ModBlocks.CAPACITOR_TANTALIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> CAPACITOR_SCHRABIDATE = register(MACHINE_TAB, "capacitor_schrabidate",
            () -> new CapacitorItem(ModBlocks.CAPACITOR_SCHRABIDATE.get(), new Item.Properties()));

    public static final RegistryObject<Item> LODESTONE = register(PARTS_TAB, "lodestone",
            () -> new ItemModLodestone(new Item.Properties().stacksTo(1), 5),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> HORSESHOE_MAGNET = register(PARTS_TAB, "horseshoe_magnet",
            () -> new ItemModLodestone(new Item.Properties().stacksTo(1), 8),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INDUSTRIAL_MAGNET = register(PARTS_TAB, "industrial_magnet",
            () -> new ItemModLodestone(new Item.Properties().stacksTo(1), 12),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MORNING_GLORY = register(PARTS_TAB, "morning_glory",
            () -> new ItemModMorningGlory(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INK = register(PARTS_TAB, "ink",
            () -> new ItemModInk(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> ARMOR_POLISH = register(PARTS_TAB, "armor_polish",
            () -> new ItemModPolish(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATHWATER = register(PARTS_TAB, "bathwater",
            () -> new ItemModBathwater(new Item.Properties().stacksTo(1), false),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BATHWATER_MK2 = register(PARTS_TAB, "bathwater_mk2",
            () -> new ItemModBathwater(new Item.Properties().stacksTo(1), true),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> TESLA = register(MACHINE_TAB, "tesla",
            () -> new TeslaItem(ModBlocks.TESLA.get(), new Item.Properties()));

    public static final RegistryObject<Item> METEOR_BATTERY = register(BLOCK_TAB, "meteor_battery",
            () -> new BlockItem(ModBlocks.METEOR_BATTERY.get(), new Item.Properties()));

    public static final RegistryObject<Item> MEDAL_LIQUIDATOR = register(PARTS_TAB, "medal_liquidator",
            () -> new ItemModMedal(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INJECTOR_5HTP = register(PARTS_TAB, "injector_5htp",
            () -> new ItemModAuto(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> INJECTOR_KNIFE = register(PARTS_TAB, "injector_knife",
            () -> new ItemModKnife(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SHACKLES = register(PARTS_TAB, "shackles",
            () -> new ItemModShackles(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> NEUTRINO_LENS = register(PARTS_TAB, "neutrino_lens",
            () -> new ItemModLens(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> GAS_TESTER = register(PARTS_TAB, "gas_tester",
            () -> new ItemModSensor(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DEFUSER_GOLD = register(PARTS_TAB, "defuser_gold",
            () -> new ItemModDefuser(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SAFETY_FUSE = register(PARTS_TAB, "safety_fuse",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BALLISTIC_GAUNTLET = register(PARTS_TAB, "ballistic_gauntlet",
            () -> new ItemModTwoKick(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> NIGHT_VISION = register(PARTS_TAB, "night_vision",
            () -> new ItemModNightVision(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> FILTER_COAL = register(PARTS_TAB, "filter_coal",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CATALYST_CLAY = register(PARTS_TAB, "catalyst_clay",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CHEMISTRY_SET = register(CONTROL_TAB, "chemistry_set",
            () -> new ItemCraftingDegradation(100),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CHEMISTRY_SET_BORON = register(CONTROL_TAB, "chemistry_set_boron",
            () -> new ItemCraftingDegradation(0),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> CENTRIFUGE_ELEMENT = register(PARTS_TAB, "centrifuge_element",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SHIMMER_HANDLE = register(PARTS_TAB, "shimmer_handle",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PISTON_SELENIUM = register(PARTS_TAB, "piston_selenium",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MATCHSTICK = register(WEAPON_TAB, "matchstick",
            () -> new ItemMatch(new Item.Properties().stacksTo(64)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DESIGNATOR = register(MISSILE_TAB, "designator",
            () -> new ItemDesignator(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DESIGNATOR_RANGE = register(MISSILE_TAB, "designator_range",
            () -> new ItemDesignatorRange(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DESIGNATOR_MANUAL = register(MISSILE_TAB, "designator_manual",
            () -> new ItemDesignatorManual(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DESIGNATOR_ARTY_RANGE = register(MISSILE_TAB, "designator_arty_range",
            () -> new ItemDesignatorArtyRange(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LAUNCH_PAD = register(MISSILE_TAB, "launch_pad",
            () -> new ItemLaunchPad(ModBlocks.LAUNCH_PAD.get(), new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_RADAR = register(MACHINE_TAB, "machines/machine_radar",
            () -> new RadarItem(ModBlocks.MACHINE_RADAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> RADAR_SCREEN = register(MACHINE_TAB, "machines/radar_screen",
            () -> new RadarScreenItem(ModBlocks.RADAR_SCREEN.get(), new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_TELEPORTER = register(MACHINE_TAB, "machines/machine_teleporter",
            () -> new BlockItem(ModBlocks.MACHINE_TELEPORTER.get(), new Item.Properties()));

    public static final RegistryObject<Item> GEIGER = register(MACHINE_TAB, "geiger",
            () -> new GeigerItem(ModBlocks.GEIGER.get(), new Item.Properties()));

    public static final RegistryObject<Item> RADAR_LINKER = register(CONSUMABLE_TAB, "radar_linker",
            () -> new ItemRadarLinker(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "radar_linker");

    public static final RegistryObject<Item> LINKER = register(CONSUMABLE_TAB,"linker",
            () -> new ItemTeleLink(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "linker");

    public static final RegistryObject<Item> SURVEY_SCANNER = register(CONSUMABLE_TAB,"survey_scanner",
            () -> new ItemSurveyScanner(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "survey_scanner");

    public static final RegistryObject<Item> ENTANGLEMENT_KIT = register(CONSUMABLE_TAB,"entanglement_kit",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED,
            "entanglement_kit");

    public static final RegistryObject<Item> DOSIMETER = register(CONSUMABLE_TAB,"dosimeter",
            () -> new ItemDosimeter(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "dosimeter");

    public static final RegistryObject<Item> DIGAMMA_DIAGNOSTIC = register(CONSUMABLE_TAB,"digamma_diagnostic",
            () -> new ItemDigammaDiagnostic(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "digamma_diagnostic");

    public static final RegistryObject<Item> POLLUTION_DETECTOR = register(CONSUMABLE_TAB,"pollution_detector",
            () -> new ItemPollutionDetector(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "pollution_detector");

    public static final RegistryObject<Item> ORE_DENSITY_SCANNER = register(CONSUMABLE_TAB,"ore_density_scanner",
            () -> new ItemOreDensityScanner(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "ore_density_scanner");

    public static final RegistryObject<Item> BEDROCK_ORE = register("bedrock_ore",
            () -> new ItemBedrockOreNew(new Item.Properties().stacksTo(64)),
            ItemModelType.ENUM_ITEM_TINTED,
            "bedrock_ore");

    public static final RegistryObject<Item> BEDROCK_ORE_FRAGMENT = register(PARTS_TAB, "bedrock_ore_fragment",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED,
            "bedrock_ore_fragment");

    public static final RegistryObject<Item> COLTAN_TOOL = register(CONSUMABLE_TAB,"coltan_tool",
            () -> new ItemColtanCompass(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "coltan_tool");

    public static final RegistryObject<Item> SAT_DESIGNATOR = register(CONSUMABLE_TAB,"sat_designator",
            () -> new ItemSatDesignator(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "sat_designator");

    public static final RegistryObject<Item> SETTINGS_TOOL = register(CONSUMABLE_TAB,"settings_tool",
            () -> new ItemSettingsTool(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "settings_tool");

    public static final RegistryObject<Item> PIPETTE = register(CONTROL_TAB, "pipette",
            () -> new ItemPipette(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED_LAYERED,
            "pipette", "pipette_overlay");

    public static final RegistryObject<Item> PIPETTE_BORON = register(CONTROL_TAB,"pipette_boron",
            () -> new ItemPipette(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "pipette_boron");

    public static final RegistryObject<Item> PIPETTE_LABORATORY = register(CONTROL_TAB,"pipette_laboratory",
            () -> new ItemPipette(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED_LAYERED,
            "pipette_laboratory", "pipette_laboratory_overlay");

    public static final RegistryObject<Item> SIPHON = register(CONTROL_TAB,"siphon",
            () -> new ItemFluidSiphon(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "siphon");

    public static final RegistryObject<Item> MIRROR_TOOL = register(CONSUMABLE_TAB, "mirror_tool",
            () -> new ItemMirrorTool(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "mirror_tool");

    public static final RegistryObject<Item> SOLAR_MIRROR = register(MACHINE_TAB, "machines/solar_mirror",
            () -> new SolarMirrorItem(ModBlocks.SOLAR_MIRROR.get(), new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_SOLAR_BOILER = register(MACHINE_TAB, "machines/machine_solar_boiler",
            () -> new SolarBoilerItem(ModBlocks.MACHINE_SOLAR_BOILER.get(), new Item.Properties()));

    public static final RegistryObject<Item> RBMK_TOOL = register(CONSUMABLE_TAB, "rbmk_tool",
            () -> new Item(new Item.Properties().stacksTo(1)), // TODO: ItemRBMKTool
            ItemModelType.GENERATED,
            "rbmk_tool");

    public static final RegistryObject<Item> POWER_NET_TOOL = register(CONSUMABLE_TAB, "power_net_tool",
            () -> new ItemPowerNetTool(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "power_net_tool");

    public static final RegistryObject<Item> ANALYSIS_TOOL = register(CONSUMABLE_TAB, "analysis_tool",
            () -> new ItemAnalysisTool(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "analysis_tool");

    public static final RegistryObject<Item> TOOLBOX = register(CONSUMABLE_TAB, "toolbox",
            () -> new ItemToolBox(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "toolbox");

    public static final RegistryObject<Item> BOLT_SPIKE = register(PARTS_TAB, "bolt_spike",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED,
            "bolt_spike");

    public static final RegistryObject<Item> REBAR_ITEM = register(BLOCK_TAB, "rebar",
            () -> new BlockItem(ModBlocks.REBAR.get(), new Item.Properties()));

    public static final RegistryObject<Item> BOBMAZON = register(CONSUMABLE_TAB, "bobmazon",
            () -> new ItemCatalog(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "bobmazon");

    public static final RegistryObject<Item> BOBMAZON_HIDDEN = register(CONSUMABLE_TAB,"bobmazon_hidden",
            () -> new ItemCatalog(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "bobmazon_hidden");

    public static final RegistryObject<Item> KIT_CUSTOM = register(CONSUMABLE_TAB, "kit_custom",
            () -> new ItemKitCustom(new Item.Properties()),
            ItemModelType.GENERATED,
            "kit_custom");

    public static final RegistryObject<Item> LEGACY_TOOLBOX = register(CONSUMABLE_TAB, "toolbox_legacy",
            () -> new ItemKitNBT(new Item.Properties().stacksTo(1).craftRemainder(ModItems.TOOLBOX.get())),
            ItemModelType.GENERATED,
            "toolbox_legacy");

    public static final RegistryObject<Item> BOAT_RUBBER = register(CONSUMABLE_TAB, "boat_rubber",
            () -> new ItemBoatRubber(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "boat_rubber");

    public static final RegistryObject<Item> PLANT_DEAD = register("plant_dead",
            () -> new ItemBlockPlant(ModBlocks.PLANT_DEAD.get(),new Item.Properties(), ItemBlockPlant.PlantType.DEAD),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> PLANT_FLOWER = register("plant_flower",
            () -> new ItemBlockPlant(ModBlocks.PLANT_FLOWER.get(), new Item.Properties(), ItemBlockPlant.PlantType.FLOWER));

    public static final RegistryObject<Item> PLUSHIE = register("plushie",
            () -> new ItemPlushie(ModBlocks.PLUSHIE.get(), new Item.Properties()));

    public static final RegistryObject<Item> SNOWGLOBE = register("snowglobe",
            () -> new ItemSnowglobe(ModBlocks.SNOWGLOBE.get(), new Item.Properties()));

    public static final RegistryObject<Item> AMMO_CONTAINER = register(WEAPON_TAB, "ammo_container",
            () -> new ItemAmmoContainer(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "ammo_container");

    public static final RegistryObject<Item> NUKE_STARTER_KIT = register(CONSUMABLE_TAB, "nuke_starter_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "nuke_starter_kit");

    public static final RegistryObject<Item> NUKE_ADVANCED_KIT = register(CONSUMABLE_TAB, "nuke_advanced_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "nuke_advanced_kit");

    public static final RegistryObject<Item> NUKE_COMMERCIALLY_KIT = register(CONSUMABLE_TAB, "nuke_commercially_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "nuke_commercially_kit");

    public static final RegistryObject<Item> NUKE_ELECTRIC_KIT = register(CONSUMABLE_TAB, "nuke_electric_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "nuke_electric_kit");

    public static final RegistryObject<Item> GADGET_KIT = register(CONSUMABLE_TAB, "gadget_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "gadget_kit");

    public static final RegistryObject<Item> BOY_KIT = register(CONSUMABLE_TAB, "boy_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "boy_kit");

    public static final RegistryObject<Item> MAN_KIT = register(CONSUMABLE_TAB, "man_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "man_kit");

    public static final RegistryObject<Item> MIKE_KIT = register(CONSUMABLE_TAB, "mike_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "mike_kit");

    public static final RegistryObject<Item> TSAR_KIT = register(CONSUMABLE_TAB, "tsar_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "tsar_kit");

    public static final RegistryObject<Item> MULTI_KIT = register(CONSUMABLE_TAB, "multi_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "multi_kit");

    public static final RegistryObject<Item> CUSTOM_KIT = register(CONSUMABLE_TAB, "custom_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "custom_kit");

    public static final RegistryObject<Item> GRENADE_KIT = register(CONSUMABLE_TAB, "grenade_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "grenade_kit");

    public static final RegistryObject<Item> FLEIJA_KIT = register(CONSUMABLE_TAB, "fleija_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "fleija_kit");

    public static final RegistryObject<Item> SOLINIUM_KIT = register(CONSUMABLE_TAB, "solinium_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "solinium_kit");

    public static final RegistryObject<Item> PROTOTYPE_KIT = register(CONSUMABLE_TAB, "prototype_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "prototype_kit");

    public static final RegistryObject<Item> MISSILE_KIT = register(CONSUMABLE_TAB, "missile_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "missile_kit");

    public static final RegistryObject<Item> EUPHEMIUM_KIT = register(CONSUMABLE_TAB, "euphemium_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "euphemium_kit");

    public static final RegistryObject<Item> HAZMAT_KIT = register(CONSUMABLE_TAB, "hazmat_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "hazmat_kit");

    public static final RegistryObject<Item> HAZMAT_RED_KIT = register(CONSUMABLE_TAB, "hazmat_red_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "hazmat_red_kit");

    public static final RegistryObject<Item> HAZMAT_GREY_KIT = register(CONSUMABLE_TAB, "hazmat_grey_kit",
            () -> new ItemStarterKit(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED,
            "hazmat_grey_kit");

    public static final RegistryObject<Item> STRUCT_LAUNCHER = register(MISSILE_TAB, "struct_launcher",
            () -> new BlockItem(ModBlocks.STRUCT_LAUNCHER.get(), new Item.Properties()));

    public static final RegistryObject<Item> STRUCT_SCAFFOLD = register(MISSILE_TAB, "struct_scaffold",
            () -> new BlockItem(ModBlocks.STRUCT_SCAFFOLD.get(), new Item.Properties()));

    public static final RegistryObject<Item> STRUCT_LAUNCHER_CORE = register(MISSILE_TAB, "struct_launcher_core",
            () -> new BlockItem(ModBlocks.STRUCT_LAUNCHER_CORE.get(), new Item.Properties()));

    public static final RegistryObject<Item> STRUCT_LAUNCHER_CORE_LARGE = register(MISSILE_TAB, "struct_launcher_core_large",
            () -> new BlockItem(ModBlocks.STRUCT_LAUNCHER_CORE_LARGE.get(), new Item.Properties()));

    public static final RegistryObject<Item> LOOT_10 = register(MISSILE_TAB, "loot_10",
            () -> new ItemLootCrate(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LOOT_15 = register(MISSILE_TAB, "loot_15",
            () -> new ItemLootCrate(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> LOOT_MISC = register(MISSILE_TAB, "loot_misc",
            () -> new ItemLootCrate(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SPAWN_CHOPPER = register(CONSUMABLE_TAB, "spawn_chopper",
            () -> new ItemChopper(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SPAWN_WORM = register(CONSUMABLE_TAB, "spawn_worm",
            () -> new ItemChopper(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SPAWN_UFO = register(CONSUMABLE_TAB, "spawn_ufo",
            () -> new ItemChopper(new Item.Properties().stacksTo(1)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> SPAWN_DUCK = register(CONSUMABLE_TAB, "spawn_duck",
            () -> new ItemChopper(new Item.Properties().stacksTo(16)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> COMBINE_SCRAP = register(PARTS_TAB, "combine_scrap",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> BALEFIRE_AND_STEEL = register(CONSUMABLE_TAB, "balefire_and_steel",
            () -> new ItemBalefireMatch(new Item.Properties().stacksTo(1).durability(256)),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> MAGNETRON = register(PARTS_TAB, "magnetron",
            () -> new Item(new Item.Properties()),
            ItemModelType.GENERATED);

    public static final RegistryObject<Item> DETONATOR = register(MISSILE_TAB, "detonator",
            () -> new ItemDetonator(new Item.Properties()),
            ItemModelType.GENERATED);



    public static final RegistryObject<Item> GEYSIR_WATER = register(BLOCK_TAB, "geysir_water",
            () -> new BlockItem(ModBlocks.GEYSIR_WATER.get(), new Item.Properties()));
    public static final RegistryObject<Item> GEYSIR_CHLORINE = register(BLOCK_TAB, "geysir_chlorine",
            () -> new BlockItem(ModBlocks.GEYSIR_CHLORINE.get(), new Item.Properties()));
    public static final RegistryObject<Item> GEYSIR_VAPOR = register(BLOCK_TAB, "geysir_vapor",
            () -> new BlockItem(ModBlocks.GEYSIR_VAPOR.get(), new Item.Properties()));
    public static final RegistryObject<Item> GEYSIR_NETHER = register(BLOCK_TAB, "geysir_nether",
            () -> new BlockItem(ModBlocks.GEYSIR_NETHER.get(), new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_BATTERY = register(MACHINE_TAB, "machine_battery",
            () -> new BlockItem(ModBlocks.MACHINE_BATTERY.get(),
            new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_BATTERY_POTATO = register(MACHINE_TAB,"machine_battery_potato",
            () -> new BlockItem(ModBlocks.MACHINE_BATTERY_POTATO.get(),
            new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_LITHIUM_BATTERY = register(MACHINE_TAB,"machine_lithium_battery",
            () -> new BlockItem(ModBlocks.MACHINE_LITHIUM_BATTERY.get(),
            new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_SCHRABIDIUM_BATTERY = register(MACHINE_TAB,"machine_schrabidium_battery",
            () -> new BlockItem(ModBlocks.MACHINE_SCHRABIDIUM_BATTERY.get(),
            new Item.Properties()));

    public static final RegistryObject<Item> MACHINE_DINEUTRONIUM_BATTERY = register(MACHINE_TAB,"machine_dineutronium_battery",
            () -> new BlockItem(ModBlocks.MACHINE_DINEUTRONIUM_BATTERY.get(),
            new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_NIOBIUM = register(BLOCK_TAB, "block_niobium",
            () -> new BlockItem(ModBlocks.BLOCK_NIOBIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_BISMUTH = register(BLOCK_TAB, "block_bismuth",
            () -> new BlockItem(ModBlocks.BLOCK_BISMUTH.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_TANTALIUM = register(BLOCK_TAB, "block_tantalium",
            () -> new BlockItem(ModBlocks.BLOCK_TANTALIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_ZIRCONIUM = register(BLOCK_TAB, "block_zirconium",
            () -> new BlockItem(ModBlocks.BLOCK_ZIRCONIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_DINEUTRONIUM = register(BLOCK_TAB, "block_dineutronium",
            () -> new BlockItem(ModBlocks.BLOCK_DINEUTRONIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> NUGGET_DINEUTRONIUM = register(PARTS_TAB, "nugget_dineutronium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUCLEAR_WASTE_VITRIFIED_TINY = register(PARTS_TAB, "nuclear_waste_vitrified_tiny",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUCLEAR_WASTE_VITRIFIED = register(PARTS_TAB, "nuclear_waste_vitrified",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_SILICON = register(PARTS_TAB, "nugget_silicon",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_SR90 = register(PARTS_TAB, "powder_sr90",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> POWDER_SR90_TINY = register(PARTS_TAB, "powder_sr90_tiny",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_XE135 = register(PARTS_TAB, "powder_xe135",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> POWDER_XE135_TINY = register(PARTS_TAB, "powder_xe135_tiny",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_CS137 = register(PARTS_TAB, "powder_cs137",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> POWDER_CS137_TINY = register(PARTS_TAB, "powder_cs137_tiny",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_I131 = register(PARTS_TAB, "powder_i131",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> POWDER_I131_TINY = register(PARTS_TAB, "powder_i131_tiny",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> INGOT_TECHNETIUM = register(PARTS_TAB, "ingot_technetium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_CO60 = register(PARTS_TAB, "ingot_co60",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> NUGGET_CO60 = register(PARTS_TAB, "nugget_co60",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_SR90 = register(PARTS_TAB, "ingot_sr90",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> NUGGET_SR90 = register(PARTS_TAB, "nugget_sr90",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> INGOT_AU198 = register(PARTS_TAB, "ingot_au198",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> INGOT_PB209 = register(PARTS_TAB, "ingot_pb209",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> NUGGET_PB209 = register(PARTS_TAB, "nugget_pb209",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_ACTINIUM = register(PARTS_TAB, "nugget_actinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> INGOT_ARSENIC = register(PARTS_TAB, "ingot_arsenic",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> INGOT_PU241 = register(PARTS_TAB, "ingot_pu241",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> NUGGET_PU241 = register(PARTS_TAB, "nugget_pu241",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_AM241 = register(PARTS_TAB, "ingot_am241",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> NUGGET_AM241 = register(PARTS_TAB, "nugget_am241",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_AM242 = register(PARTS_TAB, "ingot_am242",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> NUGGET_AM242 = register(PARTS_TAB, "nugget_am242",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_AM_MIX = register(PARTS_TAB, "ingot_am_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> NUGGET_AM_MIX = register(PARTS_TAB, "nugget_am_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_AMERICIUM_FUEL = register(PARTS_TAB, "ingot_americium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);
    public static final RegistryObject<Item> NUGGET_AMERICIUM_FUEL = register(PARTS_TAB, "nugget_americium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_GH336 = register(PARTS_TAB, "nugget_gh336",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_PU_MIX = register(PARTS_TAB, "ingot_pu_mix",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_NEPTUNIUM_FUEL = register(PARTS_TAB, "ingot_neptunium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_NEPTUNIUM_FUEL = register(PARTS_TAB, "nugget_neptunium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // ==================== НЕДОСТАЮЩИЕ ПРЕДМЕТЫ ====================

    // Кобальт
    public static final RegistryObject<Item> NUGGET_COBALT = register(PARTS_TAB, "nugget_cobalt",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Торий-232
    public static final RegistryObject<Item> INGOT_TH232 = register(PARTS_TAB, "ingot_th232",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Полоний
    public static final RegistryObject<Item> INGOT_POLONIUM = register(PARTS_TAB, "ingot_polonium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Солиний
    public static final RegistryObject<Item> INGOT_SOLINIUM = register(PARTS_TAB, "ingot_solinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Ториевое топливо (самородок)
    public static final RegistryObject<Item> NUGGET_THORIUM_FUEL = register(PARTS_TAB, "nugget_thorium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // LES (самородок)
    public static final RegistryObject<Item> NUGGET_LES = register(PARTS_TAB, "nugget_les",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Шрабридиевое топливо (самородок)
    public static final RegistryObject<Item> NUGGET_SCHRABIDIUM_FUEL = register(PARTS_TAB, "nugget_schrabidium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // HES (самородок)
    public static final RegistryObject<Item> NUGGET_HES = register(PARTS_TAB, "nugget_hes",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Австралий (самородок)
    public static final RegistryObject<Item> NUGGET_AUSTRALIUM = register(PARTS_TAB, "nugget_australium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Австралий greater (самородок)
    public static final RegistryObject<Item> NUGGET_AUSTRALIUM_GREATER = register(PARTS_TAB, "nugget_australium_greater",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Австралий lesser (самородок)
    public static final RegistryObject<Item> NUGGET_AUSTRALIUM_LESSER = register(PARTS_TAB, "nugget_australium_lesser",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    // Бериллий (самородок)
    public static final RegistryObject<Item> NUGGET_BERYLLIUM = register(PARTS_TAB, "nugget_beryllium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_LES = register(PARTS_TAB, "ingot_les",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_HES = register(PARTS_TAB, "ingot_hes",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> INGOT_SCHRABIDIUM_FUEL = register(PARTS_TAB, "ingot_schrabidium_fuel",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PELLET_RTG_DEPLETED = register("pellet_rtg_depleted",
            () -> new ItemRTGPelletDepleted(new Item.Properties()),
            ItemModelType.ENUM_ITEM,
            ItemRTGPelletDepleted.DepletedRTGMaterial.class);

    public static final RegistryObject<Item> BLOCK_FLUORITE = register(BLOCK_TAB, "block_fluorite",
            () -> new BlockItem(ModBlocks.BLOCK_FLUORITE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_NITER = register(BLOCK_TAB, "block_niter",
            () -> new BlockItem(ModBlocks.BLOCK_NITER.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_RED_COPPER = register(BLOCK_TAB, "block_red_copper",
            () -> new BlockItem(ModBlocks.BLOCK_RED_COPPER.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_SULFUR = register(BLOCK_TAB, "block_sulfur",
            () -> new BlockItem(ModBlocks.BLOCK_SULFUR.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_EUPHEMIUM = register(BLOCK_TAB, "block_euphemium",
            () -> new BlockItem(ModBlocks.BLOCK_EUPHEMIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_ADVANCED_ALLOY = register(BLOCK_TAB, "block_advanced_alloy",
            () -> new BlockItem(ModBlocks.BLOCK_ADVANCED_ALLOY.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_COMBINE_STEEL = register(BLOCK_TAB, "block_combine_steel",
            () -> new BlockItem(ModBlocks.BLOCK_COMBINE_STEEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_AUSTRALIUM = register(BLOCK_TAB, "block_australium",
            () -> new BlockItem(ModBlocks.BLOCK_AUSTRALIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_DURA_STEEL = register(BLOCK_TAB, "block_dura_steel",
            () -> new BlockItem(ModBlocks.BLOCK_DURA_STEEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_YELLOWCAKE = register(BLOCK_TAB, "block_yellowcake",
            () -> new BlockItem(ModBlocks.BLOCK_YELLOWCAKE.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_URANIUM_FUEL = register(BLOCK_TAB, "block_uranium_fuel",
            () -> new BlockItem(ModBlocks.BLOCK_URANIUM_FUEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_NEPTUNIUM = register(BLOCK_TAB, "block_neptunium",
            () -> new BlockItem(ModBlocks.BLOCK_NEPTUNIUM.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_PU238 = register(BLOCK_TAB, "block_pu238",
            () -> new BlockItem(ModBlocks.BLOCK_PU238.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_PU239 = register(BLOCK_TAB, "block_pu239",
            () -> new BlockItem(ModBlocks.BLOCK_PU239.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_PU240 = register(BLOCK_TAB, "block_pu240",
            () -> new BlockItem(ModBlocks.BLOCK_PU240.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_MOX_FUEL = register(BLOCK_TAB, "block_mox_fuel",
            () -> new BlockItem(ModBlocks.BLOCK_MOX_FUEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_PLUTONIUM_FUEL = register(BLOCK_TAB, "block_plutonium_fuel",
            () -> new BlockItem(ModBlocks.BLOCK_PLUTONIUM_FUEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_THORIUM_FUEL = register(BLOCK_TAB, "block_thorium_fuel",
            () -> new BlockItem(ModBlocks.BLOCK_THORIUM_FUEL.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_WHITE_PHOSPHORUS = register(BLOCK_TAB, "block_white_phosphorus",
            () -> new BlockItem(ModBlocks.BLOCK_WHITE_PHOSPHORUS.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_RED_PHOSPHORUS = register(BLOCK_TAB, "block_red_phosphorus",
            () -> new BlockItem(ModBlocks.BLOCK_RED_PHOSPHORUS.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_INSULATOR = register(BLOCK_TAB, "block_insulator",
            () -> new BlockItem(ModBlocks.BLOCK_INSULATOR.get(), new Item.Properties()));

    public static final RegistryObject<Item> BLOCK_FIBERGLASS = register(BLOCK_TAB, "block_fiberglass",
            () -> new BlockItem(ModBlocks.BLOCK_FIBERGLASS.get(), new Item.Properties()));

    public static final RegistryObject<Item> POWDER_ACTINIUM = register(PARTS_TAB, "powder_actinium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> POWDER_PALEOGENITE = register(PARTS_TAB, "powder_paleogenite",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> NUGGET_OSMIRIDIUM = register(PARTS_TAB, "nugget_osmiridium",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> PART_GENERIC = register("part_generic",
            () -> new ItemGenericPart(new Item.Properties()),
            ItemModelType.ENUM_ITEM,
            ItemGenericPart.EnumPartType.class);

    public static final RegistryObject<Item> POWDER_BALEFIRE = register(PARTS_TAB, "powder_balefire",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static final RegistryObject<Item> CELL_BALEFIRE = register(PARTS_TAB, "cell_balefire",
            () -> new Item(new Item.Properties()), ItemModelType.GENERATED);

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}