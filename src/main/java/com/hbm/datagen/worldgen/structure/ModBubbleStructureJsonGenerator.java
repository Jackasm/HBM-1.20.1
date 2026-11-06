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

/**
 * JSON для пузырьковых структур (нефть/песок): worldgen/structure, structure_set и теги биомов.
 * Частота задаётся в самих структурах (1/100 и 1/200), поэтому spacing = 1 — попытка в каждом чанке.
 */
public class ModBubbleStructureJsonGenerator implements DataProvider {

    private final DataGenerator generator;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final List<String> SAND_BUBBLE_BIOMES = List.of(
            "minecraft:desert",
            "minecraft:badlands",
            "minecraft:eroded_badlands",
            "minecraft:wooded_badlands"
    );

    public ModBubbleStructureJsonGenerator(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Path outputPath = generator.getPackOutput().getOutputFolder();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(generateStructure(cache, outputPath, "oil_bubble", "hbm:oil_bubble", "oil_bubble_biomes"));
        futures.add(generateStructure(cache, outputPath, "sand_bubble", "hbm:sand_bubble", "sand_bubble_biomes"));

        futures.add(generateStructureSet(cache, outputPath, "oil_bubble_set", "oil_bubble", 111111111));
        futures.add(generateStructureSet(cache, outputPath, "sand_bubble_set", "sand_bubble", 222222222));

        futures.add(generateBiomeTag(cache, outputPath, "oil_bubble_biomes", List.of("#minecraft:is_overworld")));
        futures.add(generateBiomeTag(cache, outputPath, "sand_bubble_biomes", SAND_BUBBLE_BIOMES));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    private CompletableFuture<?> generateStructure(CachedOutput cache, Path outputPath,
                                                   String name, String type, String biomeTag) {
        JsonObject root = new JsonObject();
        root.addProperty("type", type);

        JsonObject settings = new JsonObject();
        settings.addProperty("biomes", "#" + MODID + ":" + biomeTag);
        settings.addProperty("step", "underground_structures");
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

    private CompletableFuture<?> generateBiomeTag(CachedOutput cache, Path outputPath,
                                                  String tagName, List<String> biomes) {
        JsonObject root = new JsonObject();
        JsonArray values = new JsonArray();
        biomes.forEach(values::add);
        root.add("values", values);

        Path path = outputPath.resolve("data/" + MODID + "/tags/worldgen/biome/" + tagName + ".json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    @Override
    public @NotNull String getName() {
        return "Bubble Structure JSON Generator";
    }
}
