package com.hbm.datagen.worldgen.structure;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

import static com.hbm.util.RefStrings.MODID;

public class ModStructureJsonGenerator implements DataProvider {

    private final DataGenerator generator;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    // ============================================================
    //  Биомы групп (БЕЗ пересечений)
    // ============================================================

    private static final List<String> FLAT_BIOMES = Arrays.asList(
            "minecraft:plains", "minecraft:sunflower_plains",
            "minecraft:forest", "minecraft:flower_forest",
            "minecraft:birch_forest", "minecraft:old_growth_birch_forest",
            "minecraft:dark_forest", "minecraft:taiga",
            "minecraft:old_growth_pine_taiga", "minecraft:old_growth_spruce_taiga",
            "minecraft:savanna", "minecraft:savanna_plateau",
            "minecraft:windswept_savanna", "minecraft:meadow"
            // убраны snowy_plains, ice_spikes, snowy_taiga — они в snowy
    );

    private static final List<String> SNOWY_BIOMES = Arrays.asList(
            "minecraft:snowy_plains", "minecraft:snowy_taiga",
            "minecraft:ice_spikes", "minecraft:frozen_river",
            "minecraft:snowy_slopes", "minecraft:frozen_peaks",
            "minecraft:jagged_peaks", "minecraft:grove"
            // убран snowy_beach — он в beach
    );

    private static final List<String> DESERT_BIOMES = Arrays.asList(
            "minecraft:desert", "minecraft:badlands",
            "minecraft:eroded_badlands", "minecraft:wooded_badlands"
    );

    private static final List<String> BEACH_BIOMES = Arrays.asList(
            "minecraft:beach", "minecraft:snowy_beach", "minecraft:stony_shore"
    );

    private static final List<String> OCEAN_BIOMES = Arrays.asList(
            "minecraft:ocean", "minecraft:deep_ocean",
            "minecraft:cold_ocean", "minecraft:deep_cold_ocean",
            "minecraft:lukewarm_ocean", "minecraft:deep_lukewarm_ocean",
            "minecraft:warm_ocean", "minecraft:frozen_ocean",
            "minecraft:deep_frozen_ocean"
    );

    private static final List<String> RUINS_BIOMES = List.of("#minecraft:is_overworld");

    /** Пустой исход в равнинах — вес 4, как plainsNullWeight эталона. */
    private static final List<String> NOTHING_PLAINS_BIOMES = List.of(
            "minecraft:plains", "minecraft:sunflower_plains"
    );

    // Оригинал: dish / falloutshelter_house — только PLAINS
    private static final List<String> PLAINS_BIOMES = List.of(
            "minecraft:plains", "minecraft:sunflower_plains"
    );

    // Оригинал: water_pump — PLAINS или SWAMP
    private static final List<String> WET_PLAINS_BIOMES = List.of(
            "minecraft:plains", "minecraft:sunflower_plains", "minecraft:swamp"
    );

    // Оригинал: lighthouse — OCEAN или BEACH
    private static final List<String> LIGHTHOUSE_BIOMES = new ArrayList<>(OCEAN_BIOMES);
    static {
        LIGHTHOUSE_BIOMES.addAll(BEACH_BIOMES);
    }

    private static final Map<String, List<String>> BIOME_GROUPS = new LinkedHashMap<>() {{
        put("flat", FLAT_BIOMES);
        put("plains", PLAINS_BIOMES);
        put("wet_plains", WET_PLAINS_BIOMES);
        put("desert", DESERT_BIOMES);
        put("beach", BEACH_BIOMES);
        put("ocean", OCEAN_BIOMES);
        put("lighthouse", LIGHTHOUSE_BIOMES);
        put("ruins", RUINS_BIOMES);
        put("snowy", SNOWY_BIOMES);
        put("atom", DESERT_BIOMES);
        put("spaceship", RUINS_BIOMES);
    }};

    // Фиксированные salt'ы: не зависят от hashCode() и порядка JVM
    private static final Map<String, Integer> GROUP_SALTS = Map.ofEntries(
            Map.entry("flat", 11001),
            Map.entry("plains", 11002),
            Map.entry("wet_plains", 11003),
            Map.entry("desert", 11004),
            Map.entry("beach", 11005),
            Map.entry("ocean", 11006),
            Map.entry("lighthouse", 11007),
            Map.entry("ruins", 11008),
            Map.entry("snowy", 11009),
            Map.entry("atom", 11010),
            Map.entry("spaceship", 11011)
    );

    private static final int SPACING = 12;
    private static final int SEPARATION = 4;

    // Крупные структуры: spacing подобран под 1/500 и 1/1000 чанков
    private static final Map<String, Integer> GROUP_SPACING = Map.of(
            "atom", 22,
            "spaceship", 32
    );
    private static final Map<String, Integer> GROUP_SEPARATION = Map.of(
            "atom", 7,
            "spaceship", 10
    );

    // ============================================================
    //  Все структуры
    // ============================================================

    private static final List<StructureData> STRUCTURES = new ArrayList<>();

    static {
        // ---------- FLAT ----------
        STRUCTURES.add(new StructureData("spire",                 "flat",   2,  -1,  0,  0, "none"));
        // crane.nbt — старый шаблон (не crane_mod): его наземный слой на y=5,
        // поэтому −5, а не −13. С −13 база из решёток/мин уходила на 8 в землю.
        STRUCTURES.add(new StructureData("crane",                 "flat",  20,  -5,  0,  0, "none"));
        STRUCTURES.add(new StructureData("factory",               "flat",  40, -10,  0,  0, "none"));
        STRUCTURES.add(new StructureData("laboratory",            "flat",  20, -10, 53, 65, "none"));
        STRUCTURES.add(new StructureData("forest_chem",           "flat",  30,  -9,  0,  0, "none"));
        STRUCTURES.add(new StructureData("forest_post",           "flat",  30, -10,  0,  0, "none"));
        STRUCTURES.add(new StructureData("radio_house",           "flat",  25,  -6,  0,  0, "none"));
        STRUCTURES.add(new StructureData("broadcasting_tower",    "flat",  25,  -9,  0,  0, "none"));
        STRUCTURES.add(new StructureData("dish",                  "plains", 10, -10, 53, 65, "none"));
        STRUCTURES.add(new StructureData("crashed_plane_1",       "flat",  25,  -5,  0,  0, "none"));
        STRUCTURES.add(new StructureData("crashed_plane_2",       "flat",  25,  -8,  0,  0, "none"));
        STRUCTURES.add(new StructureData("tower_base",            "flat",  30,  -6,  0,  0, "none"));
        STRUCTURES.add(new StructureData("water_pump",            "wet_plains", 15, -10,  0,  0, "none"));
        STRUCTURES.add(new StructureData("falloutshelter_house",  "plains", 15,  -8,  0,  0, "none"));

        // ---------- DESERT ----------
        STRUCTURES.add(new StructureData("desert_shack_1",        "desert", 18, -7, 0, 0, "none"));
        STRUCTURES.add(new StructureData("desert_shack_2",        "desert", 20, -7, 0, 0, "none"));
        STRUCTURES.add(new StructureData("desert_shack_3",        "desert", 22, -5, 0, 0, "none"));
        STRUCTURES.add(new StructureData("vertibird",             "desert",  6, -3, 0, 0, "none"));
        STRUCTURES.add(new StructureData("crashed_vertibird",     "desert", 10,-10, 0, 0, "none"));
        STRUCTURES.add(new StructureData("dead_dish_small",       "desert", 15,  -5,  0,  0, "none"));

        // ---------- BEACH ----------
        STRUCTURES.add(new StructureData("beached_patrol",        "beach", 15,  -5, 58, 67, "none"));

        // ---------- LIGHTHOUSE (ocean + beach) ----------
        STRUCTURES.add(new StructureData("lighthouse",            "lighthouse",  1, -40, 28, 29, "none"));

        // ---------- OCEAN ----------
        STRUCTURES.add(new StructureData("aircraft_carrier",      "ocean",  3,  -6, 0, 42, "none"));
        STRUCTURES.add(new StructureData("oil_rig",               "ocean",  5, -20, 11, 12, "none"));
        STRUCTURES.add(new StructureData("submarine",             "ocean",  2, -10, 0, 0, "none"));

        // ---------- RUINS ----------
        STRUCTURES.add(new StructureData("ntmruins_a",            "ruins", 10, -1, 0, 0, "none", true));
        STRUCTURES.add(new StructureData("ntmruins_b",            "ruins", 12, -1, 0, 0, "none", true));
        STRUCTURES.add(new StructureData("ntmruins_c",            "ruins", 12, -1, 0, 0, "none", true));
        STRUCTURES.add(new StructureData("ntmruins_d",            "ruins", 12, -1, 0, 0, "none", true));
        STRUCTURES.add(new StructureData("ntmruins_e",            "ruins", 12, -1, 0, 0, "none", true));
        STRUCTURES.add(new StructureData("ntmruins_f",            "ruins", 12, -1, 0, 0, "none", true));
        STRUCTURES.add(new StructureData("ntmruins_g",            "ruins", 12, -1, 0, 0, "none", true));
        STRUCTURES.add(new StructureData("ntmruins_h",            "ruins", 12, -1, 0, 0, "none", true));
        STRUCTURES.add(new StructureData("ntmruins_i",            "ruins", 12, -1, 0, 0, "none", true));
        STRUCTURES.add(new StructureData("ntmruins_j",            "ruins", 12, -1, 0, 0, "none", true));

        // ---------- SNOWY ----------
        STRUCTURES.add(new StructureData("abandoned_arcticstation","snowy", 5,  0, 0, 0, "none"));
        STRUCTURES.add(new StructureData("military_base",          "snowy", 1, -5, 0, 0, "none"));

        // ---------- LARGE (NBT) ----------
        // DesertAtom ставится кодом (тип hbm:atom, кусок AtomPiece): NBT-шаблон
        // 0.0.15 композицию эталона не воспроизводил (#160).
        // terrain_adaptation = none: Beardifier вырезал блоки внутри коробки и от
        // пятислойной подушки оставлял два слоя — станция висела ровно на 3 блока
        // (замер 03.11.2026). Заглубление делается высотой постановки: подушка
        // уходит в грунт на 3 блока от самой низкой точки подошвы.
        STRUCTURES.add(new StructureData("desert_atom",            "atom", 1, 0, 0, 0, "none", false, true));
        // Корабль в 1.7.10 ставился на поверхность, а его корпус уходит на 3 блока
        // ниже неё — шаблон собран из исходных генераторов, поэтому сдвигаем на -3.
        STRUCTURES.add(new StructureData("spaceship",              "spaceship", 1, -3, 0, 0, "none"));
    }

    public ModStructureJsonGenerator(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Path outputPath = generator.getPackOutput().getOutputFolder();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        for (StructureData data : STRUCTURES) {
            futures.add(generateStructureJson(cache, outputPath, data));
        }

        // Пустые исходы: вес, который ничего не ставит (registerNullWeight эталона).
        futures.add(generateEmptyStructureJson(cache, outputPath, "nothing_plains", "nothing_plains_biomes"));
        futures.add(generateEmptyStructureJson(cache, outputPath, "nothing_ocean", "ocean_biomes"));

        // Один набор на весь Верхний мир: одна попытка на клетку 12×12, как в 1.7.10.
        futures.add(generateOverworldSet(cache, outputPath));

        // Отдельные сетки редких крупных структур — как в эталоне (22 и 32 чанка).
        futures.add(generateStructureSetForGroup(cache, outputPath, "atom", groupStructures("atom")));
        futures.add(generateStructureSetForGroup(cache, outputPath, "spaceship", groupStructures("spaceship")));

        for (Map.Entry<String, List<String>> entry : BIOME_GROUPS.entrySet()) {
            futures.add(generateBiomeTagJson(cache, outputPath, entry.getKey(), entry.getValue()));
        }
        futures.add(generateBiomeTagJson(cache, outputPath, "nothing_plains", NOTHING_PLAINS_BIOMES));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private List<StructureData> groupStructures(String group) {
        List<StructureData> list = new ArrayList<>();
        for (StructureData data : STRUCTURES) {
            if (data.group.equals(group)) list.add(data);
        }
        return list;
    }

    private CompletableFuture<?> generateOverworldSet(CachedOutput cache, Path outputPath) {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();

        for (StructureData data : STRUCTURES) {
            if (data.group.equals("atom") || data.group.equals("spaceship")) continue;
            addEntry(arr, data.name, data.spawnWeight);
        }
        // features в эталоне лежал в том же взвешенном списке (featuresSpawnWeight = 50).
        addEntry(arr, "ntm_features", 50);
        addEntry(arr, "nothing_plains", 4);
        addEntry(arr, "nothing_ocean", 15);

        root.add("structures", arr);

        JsonObject placement = new JsonObject();
        placement.addProperty("type", "minecraft:random_spread");
        placement.addProperty("salt", 11001);
        placement.addProperty("spacing", SPACING);
        placement.addProperty("separation", SEPARATION);
        placement.addProperty("spread_type", "linear");
        root.add("placement", placement);

        Path path = outputPath.resolve("data/" + MODID + "/worldgen/structure_set/overworld_set.json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    private static void addEntry(JsonArray arr, String name, int weight) {
        JsonObject entry = new JsonObject();
        entry.addProperty("structure", MODID + ":" + name);
        entry.addProperty("weight", weight);
        arr.add(entry);
    }

    private CompletableFuture<?> generateEmptyStructureJson(CachedOutput cache, Path outputPath,
                                                            String name, String biomeTag) {
        JsonObject root = new JsonObject();
        root.addProperty("type", MODID + ":nothing");

        JsonObject settings = new JsonObject();
        settings.addProperty("biomes", "#" + MODID + ":" + biomeTag);
        settings.addProperty("step", "surface_structures");
        settings.addProperty("terrain_adaptation", "none");
        settings.add("spawn_overrides", new JsonObject());
        root.add("settings", settings);

        Path path = outputPath.resolve("data/" + MODID + "/worldgen/structure/" + name + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    private CompletableFuture<?> generateStructureJson(CachedOutput cache, Path outputPath, StructureData data) {
        JsonObject root = new JsonObject();
        if (data.codeGenerated) {
            // Постройка рисуется кодом порта, а не шаблоном: размер и содержимое
            // задаёт кусок структуры, в JSON остаются только настройки.
            root.addProperty("type", "hbm:atom");
        } else {
            root.addProperty("type", "hbm:simple_nbt");
            root.addProperty("nbt_file", data.name);
            root.addProperty("height_offset", data.heightOffset);
            root.addProperty("min_height", data.minHeight > 0 ? data.minHeight : 1);
            root.addProperty("max_height", data.maxHeight > 0 ? data.maxHeight : 128);
            // Корабль в оригинале всегда лежал одинаково, без поворота. Половины
            // теперь не отдельные куски, а один шаблон, поэтому крутить его незачем.
            if ("spaceship".equals(data.name)) {
                root.addProperty("rotation", 0);
            }
            // Руины повторяют рельеф по колонкам, как conformToTerrain в эталоне.
            if (data.conform) {
                root.addProperty("conform", true);
            }
        }

        JsonObject settings = new JsonObject();
        settings.addProperty("biomes", "#hbm:" + data.group + "_biomes");
        settings.addProperty("step", "surface_structures");
        settings.addProperty("terrain_adaptation", data.terrainAdaptation);
        settings.add("spawn_overrides", new JsonObject());
        root.add("settings", settings);

        Path path = outputPath.resolve("data/" + MODID + "/worldgen/structure/" + data.name + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    private CompletableFuture<?> generateStructureSetForGroup(CachedOutput cache, Path outputPath,
                                                              String group, List<StructureData> groupStructures) {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();

        for (StructureData data : groupStructures) {
            JsonObject entry = new JsonObject();
            entry.addProperty("structure", MODID + ":" + data.name);
            entry.addProperty("weight", data.spawnWeight);
            arr.add(entry);
        }
        root.add("structures", arr);

        JsonObject placement = new JsonObject();
        placement.addProperty("type", "minecraft:random_spread");
        int salt = GROUP_SALTS.getOrDefault(group, 123456789);
        placement.addProperty("salt", salt);
        placement.addProperty("spacing", GROUP_SPACING.getOrDefault(group, SPACING));
        placement.addProperty("separation", GROUP_SEPARATION.getOrDefault(group, SEPARATION));
        placement.addProperty("spread_type", "linear");
        root.add("placement", placement);

        Path path = outputPath.resolve("data/" + MODID + "/worldgen/structure_set/" + group + "_set.json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    private CompletableFuture<?> generateBiomeTagJson(CachedOutput cache, Path outputPath,
                                                      String group, List<String> biomes) {
        JsonObject root = new JsonObject();
        JsonArray values = new JsonArray();
        biomes.forEach(values::add);
        root.add("values", values);

        Path path = outputPath.resolve("data/" + MODID + "/tags/worldgen/biome/" + group + "_biomes.json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    @Override
    public @NotNull String getName() {
        return "Structure JSON Generator";
    }

    private static class StructureData {
        final String name;
        final String group;
        final int spawnWeight;
        final int heightOffset;
        final int minHeight;
        final int maxHeight;
        final String terrainAdaptation;
        final boolean conform;
        /** Содержимое рисует код порта, а не NBT-шаблон (тип структуры {@code hbm:atom}). */
        final boolean codeGenerated;

        StructureData(String name, String group, int spawnWeight, int heightOffset,
                      int minHeight, int maxHeight, String terrainAdaptation) {
            this(name, group, spawnWeight, heightOffset, minHeight, maxHeight, terrainAdaptation, false, false);
        }

        StructureData(String name, String group, int spawnWeight, int heightOffset,
                      int minHeight, int maxHeight, String terrainAdaptation, boolean conform) {
            this(name, group, spawnWeight, heightOffset, minHeight, maxHeight, terrainAdaptation, conform, false);
        }

        StructureData(String name, String group, int spawnWeight, int heightOffset,
                      int minHeight, int maxHeight, String terrainAdaptation,
                      boolean conform, boolean codeGenerated) {
            this.name = name;
            this.group = group;
            this.spawnWeight = spawnWeight;
            this.heightOffset = heightOffset;
            this.minHeight = minHeight;
            this.maxHeight = maxHeight;
            this.terrainAdaptation = terrainAdaptation;
            this.conform = conform;
            this.codeGenerated = codeGenerated;
        }
    }
}
