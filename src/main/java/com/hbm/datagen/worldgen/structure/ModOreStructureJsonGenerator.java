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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.hbm.util.RefStrings.MODID;

public class ModOreStructureJsonGenerator implements DataProvider {

    private final DataGenerator generator;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public ModOreStructureJsonGenerator(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Path outputPath = generator.getPackOutput().getOutputFolder();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(generateStructure(cache, outputPath, "ore_cave_sulfur", "hbm:ore_cave", "sulfur", "ore_caves_biomes"));
        futures.add(generateStructure(cache, outputPath, "ore_cave_asbestos", "hbm:ore_cave", "asbestos", "ore_caves_biomes"));
        futures.add(generateStructure(cache, outputPath, "schist_stratum", "hbm:schist_stratum", null, "schist_stratum_biomes"));

        futures.add(generateStructureSet(cache, outputPath, "ore_cave_sulfur_set", "ore_cave_sulfur", 123456789));
        futures.add(generateStructureSet(cache, outputPath, "ore_cave_asbestos_set", "ore_cave_asbestos", 987654321));
        futures.add(generateStructureSet(cache, outputPath, "schist_stratum_set", "schist_stratum", 777777777));

        futures.add(generateBiomeTag(cache, outputPath, "ore_caves_biomes"));
        futures.add(generateBiomeTag(cache, outputPath, "schist_stratum_biomes"));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<?> generateStructure(CachedOutput cache, Path outputPath,
                                                   String name, String type, String caveType, String biomeTag) {
        JsonObject root = new JsonObject();
        root.addProperty("type", type);
        if (caveType != null) {
            root.addProperty("cave_type", caveType);
        }

        JsonObject settings = new JsonObject();
        settings.addProperty("biomes", "#" + MODID + ":" + biomeTag);
        settings.addProperty("step", "underground_ores");
        settings.addProperty("terrain_adaptation", "none");
        settings.add("spawn_overrides", new JsonObject());
        root.add("settings", settings);

        Path path = outputPath.resolve("data/" + MODID + "/worldgen/structure/" + name + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    private CompletableFuture<?> generateStructureSet(CachedOutput cache, Path outputPath,
                                                      String setName, String structureName, int salt) {
        JsonObject root = new JsonObject();
        JsonArray arr = new JsonArray();

        JsonObject entry = new JsonObject();
        entry.addProperty("structure", MODID + ":" + structureName);
        entry.addProperty("weight", 1);
        arr.add(entry);
        root.add("structures", arr);

        JsonObject placement = new JsonObject();
        placement.addProperty("type", "minecraft:random_spread");
        placement.addProperty("salt", salt);
        placement.addProperty("spacing", 1);
        placement.addProperty("separation", 0);
        placement.addProperty("spread_type", "linear");
        root.add("placement", placement);

        Path path = outputPath.resolve("data/" + MODID + "/worldgen/structure_set/" + setName + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    private CompletableFuture<?> generateBiomeTag(CachedOutput cache, Path outputPath, String tagName) {
        JsonObject root = new JsonObject();
        JsonArray values = new JsonArray();
        values.add("#minecraft:is_overworld");
        root.add("values", values);

        Path path = outputPath.resolve("data/" + MODID + "/tags/worldgen/biome/" + tagName + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    @Override
    public @NotNull String getName() {
        return "Ore Structure JSON Generator";
    }
}