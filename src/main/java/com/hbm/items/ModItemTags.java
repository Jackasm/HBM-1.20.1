package com.hbm.items;

import com.hbm.util.RefStrings;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import static com.hbm.util.ResLocation.ResLocation;

public class ModItemTags {

    public static final TagKey<Item> ORE_GOLD = forgeTag("ores/gold");
    public static final TagKey<Item> ORE_IRON = forgeTag("ores/iron");
    public static final TagKey<Item> ORE_COPPER = forgeTag("ores/copper");
    public static final TagKey<Item> ORE_COAL = forgeTag("ores/coal");
    public static final TagKey<Item> ORE_REDSTONE = forgeTag("ores/redstone");
    public static final TagKey<Item> ORE_LAPIS = forgeTag("ores/lapis");
    public static final TagKey<Item> ORE_DIAMOND = forgeTag("ores/diamond");
    public static final TagKey<Item> ORE_EMERALD = forgeTag("ores/emerald");
    public static final TagKey<Item> ORE_NETHER_QUARTZ = forgeTag("ores/nether_quartz");
    public static final TagKey<Item> ORE_URANIUM = forgeTag("ores/uranium");
    public static final TagKey<Item> ORE_THORIUM = forgeTag("ores/thorium");
    public static final TagKey<Item> ORE_LEAD = forgeTag("ores/lead");
    public static final TagKey<Item> ORE_TITANIUM = forgeTag("ores/titanium");
    public static final TagKey<Item> ORE_TUNGSTEN = forgeTag("ores/tungsten");
    public static final TagKey<Item> ORE_ALUMINIUM = forgeTag("ores/aluminium");
    public static final TagKey<Item> ORE_ZINC = forgeTag("ores/zinc");
    public static final TagKey<Item> ORE_COBALT = forgeTag("ores/cobalt");
    public static final TagKey<Item> ORE_BERYLLIUM = forgeTag("ores/beryllium");
    public static final TagKey<Item> ORE_NITER = forgeTag("ores/niter");
    public static final TagKey<Item> ORE_SULFUR = forgeTag("ores/sulfur");
    public static final TagKey<Item> ORE_FLUORITE = forgeTag("ores/fluorite");
    public static final TagKey<Item> ORE_CINNABAR = forgeTag("ores/cinnabar");
    public static final TagKey<Item> ORE_RARE_EARTH = forgeTag("ores/rare_earth");
    public static final TagKey<Item> ORE_PLUTONIUM = forgeTag("ores/plutonium");
    public static final TagKey<Item> ORE_SCHRABIDIUM = forgeTag("ores/schrabidium");

    // === ТЕГИ ДЛЯ КОНТЕЙНЕРОВ (НОВАЯ АРХИТЕКТУРА) ===

    // ОБЩИЕ теги для типов контейнеров (ПОЛНЫЕ)
    public static final TagKey<Item> BUCKET_CONTAINERS = modTag("bucket_containers");
    public static final TagKey<Item> TANK_CONTAINERS = modTag("tank_containers");
    public static final TagKey<Item> BARREL_CONTAINERS = modTag("barrel_containers");

    // ПУСТЫЕ контейнеры
    public static final TagKey<Item> EMPTY_BUCKETS = modTag("empty_buckets");
    public static final TagKey<Item> EMPTY_TANKS = modTag("empty_tanks");
    public static final TagKey<Item> EMPTY_BARRELS = modTag("empty_barrels");

    public static final TagKey<Item> WIRE_FINE = modTag("wire_fine");

    public static final TagKey<Item> GLASS = modTag("glass");

    // УНИВЕРСАЛЬНЫЙ тег для всех пустых контейнеров
    public static final TagKey<Item> EMPTY_FLUID_CONTAINERS = modTag("empty_fluid_containers");

    // === КАСТОМНЫЕ ТЕГИ HBM ===

    public static final TagKey<Item> INGOTS = modTag("ingots");
    public static final TagKey<Item> ORES = modTag("ores");
    public static final TagKey<Item> RADIOACTIVE_ITEMS = modTag("radioactive_items");
    public static final TagKey<Item> NUCLEAR_FUELS = modTag("nuclear_fuels");
    public static final TagKey<Item> FUELS = modTag("fuels");
    public static final TagKey<Item> TOOL_CHEMISTRYSET = modTag("ntmchemistryset");

    // Группы для совместимости
    public static final TagKey<Item> ANY_STEEL = modTag("any_steel");
    public static final TagKey<Item> ANY_IRON = modTag("any_iron");
    public static final TagKey<Item> ANY_COPPER = modTag("any_copper");
    public static final TagKey<Item> ANY_COKE = modTag("any_coke");
    public static final TagKey<Item> ANY_RUBBER_INGOT = modTag("rubber_ingots");
    public static final TagKey<Item> ANY_TAR = modTag("any_tar");
    public static final TagKey<Item> ANY_GLASS_PANES = forgeTag("glass_panes");
    public static final TagKey<Item> ANY_GLASS_BLOCKS = forgeTag("glass_blocks");
    public static final TagKey<Item> ANY_SAND = forgeTag("sand");
    public static final TagKey<Item> ANY_PLASTIC_INGOT = modTag("any_plastic");
    public static final TagKey<Item> ANY_WOOL = forgeTag("any_wool");
    public static final TagKey<Item> ANY_HARDPLASTIC_INGOT = modTag("any_hard_plastic");
    public static final TagKey<Item> ANY_HIGHEXPLOSIVE = modTag("any_highexplosive");
    public static final TagKey<Item> ANY_BISMOID_BRONZE_PLATE_CAST = modTag("any_bismoid_plate_cast");
    public static final TagKey<Item> ANY_RESISTANTALLOY_PLATE_CAST = modTag("any_resistantalloy_plate_cast");
    public static final TagKey<Item> ANY_BISMOID_NUGGET = modTag("any_bismoid_nugget");
    public static final TagKey<Item> ANY_RESISTANTALLOY_HEAVY_RECEIVER = modTag("any_resistantalloy_heavy_receiver");
    public static final TagKey<Item> ANY_BISMOID_BRONZE_HEAVY_RECEIVER = modTag("any_bismoid_bronze_heavy_receiver");
    public static final TagKey<Item> ANY_BISMOID_BRONZE_LIGHT_RECEIVER = modTag("any_bismoid_bronze_light_receiver");
    public static final TagKey<Item> ANY_RESISTANTALLOY_HEAVY_BARREL = modTag("any_resistantalloy_heavy_barrel");
    public static final TagKey<Item> ANY_RESISTANTALLOY_LIGHT_BARREL = modTag("any_resistantalloy_light_barrel");
    public static final TagKey<Item> ANY_BISMOID_BRONZE_LIGHT_BARREL = modTag("any_bismoid_bronze_light_barrel");
    public static final TagKey<Item> ANY_RESISTANTALLOY_LIGHT_RECEIVER = modTag("any_resistantalloy_light_receiver");
    public static final TagKey<Item> ANY_PLASTIC_GRIP = modTag("any_plastic_grip");
    public static final TagKey<Item> ANY_HARDPLASTIC_GRIP = modTag("any_hardplastic_grip");
    public static final TagKey<Item> ANY_PLASTIC_STOCK = modTag("any_plastic_stock");
    public static final TagKey<Item> ANY_HARDPLASTIC_STOCK = modTag("any_hardplastic_stock");
    public static final TagKey<Item> ANY_PLASTICEXPLOSIVE_INGOT = modTag("any_plasticexplosive_ingot");
    public static final TagKey<Item> ANY_SMOKELESS_DUST = modTag("any_smokeless_dust");
    public static final TagKey<Item> ANY_ELECTRODE = modTag("any_electrode");
    public static final TagKey<Item> ANY_QUARTZ_BLOCK = modTag("any_quartz_block");


    // === FORGE ТЕГИ ===

    // Слитки
    public static final TagKey<Item> INGOTS_IRON = forgeTag("ingots/iron");
    public static final TagKey<Item> INGOTS_GOLD = forgeTag("ingots/gold");
    public static final TagKey<Item> INGOTS_COPPER = forgeTag("ingots/copper");
    public static final TagKey<Item> INGOTS_STEEL = forgeTag("ingots/steel");
    public static final TagKey<Item> INGOTS_LEAD = forgeTag("ingots/lead");
    public static final TagKey<Item> INGOTS_ALUMINUM = forgeTag("ingots/aluminum");
    public static final TagKey<Item> INGOTS_TITANIUM = forgeTag("ingots/titanium");
    public static final TagKey<Item> INGOTS_URANIUM = forgeTag("ingots/uranium");
    public static final TagKey<Item> INGOTS_PLUTONIUM = forgeTag("ingots/plutonium");


    // Самородки
    public static final TagKey<Item> NUGGETS_IRON = forgeTag("nuggets/iron");
    public static final TagKey<Item> NUGGETS_GOLD = forgeTag("nuggets/gold");


    // Пыль
    public static final TagKey<Item> DUSTS_IRON = forgeTag("dusts/iron");
    public static final TagKey<Item> DUSTS_GOLD = forgeTag("dusts/gold");
    public static final TagKey<Item> DUSTS_COPPER = forgeTag("dusts/copper");
    public static final TagKey<Item> DUSTS_COAL = forgeTag("dusts/coal");
    public static final TagKey<Item> DUSTS_REDSTONE = forgeTag("dusts/redstone");
    public static final TagKey<Item> DUSTS_NETHER_QUARTZ = forgeTag("dusts/nether_quartz");
    public static final TagKey<Item> DUSTS_LAPIS = forgeTag("dusts/lapis");
    public static final TagKey<Item> DUSTS_DIAMOND = forgeTag("dusts/diamond");
    public static final TagKey<Item> DUSTS_EMERALD = forgeTag("dusts/emerald");
    public static final TagKey<Item> DUSTS_LIGNITE = forgeTag("dusts/lignite");

    // Пластины
    public static final TagKey<Item> PLATES_IRON = forgeTag("plates/iron");
    public static final TagKey<Item> PLATES_STEEL = forgeTag("plates/steel");
    public static final TagKey<Item> PLATES_COPPER = forgeTag("plates/copper");
    public static final TagKey<Item> PLATES_GOLD = forgeTag("plates/copper");

    // Руды
    public static final TagKey<Item> ORES_IRON = forgeTag("ores/iron");
    public static final TagKey<Item> ORES_GOLD = forgeTag("ores/gold");
    public static final TagKey<Item> ORES_COPPER = forgeTag("ores/copper");
    public static final TagKey<Item> ORES_URANIUM = forgeTag("ores/uranium");

    // Блоки хранения
    public static final TagKey<Item> STORAGE_BLOCKS_IRON = forgeTag("storage_blocks/iron");
    public static final TagKey<Item> STORAGE_BLOCKS_GOLD = forgeTag("storage_blocks/gold");
    public static final TagKey<Item> STORAGE_BLOCKS_COPPER = forgeTag("storage_blocks/copper");
    public static final TagKey<Item> STORAGE_BLOCKS_STEEL = forgeTag("storage_blocks/steel");

    // Драгоценности
    public static final TagKey<Item> GEMS_DIAMOND = forgeTag("gems/diamond");
    public static final TagKey<Item> GEMS_EMERALD = forgeTag("gems/emerald");
    public static final TagKey<Item> GEMS_QUARTZ = forgeTag("gems/quartz");


    private static TagKey<Item> forgeTag(String name) {
        return ItemTags.create(ResLocation("forge", name));
    }

    private static TagKey<Item> modTag(String name) {
        return ItemTags.create(ResLocation(RefStrings.MODID, name));
    }

    private static TagKey<Item> create(String name) {
        return ItemTags.create(ResLocation(RefStrings.MODID, name));
    }

    public static TagKey<Item> createTag(String name) {
        if (name.startsWith("forge:")) {
            // Для forge тегов используем стандартное создание тега с namespace "forge"
            return ItemTags.create(ResLocation(name));
        } else {
            // Для модовых тегов
            return ItemTags.create(ResLocation(RefStrings.MODID, name));
        }
    }
}