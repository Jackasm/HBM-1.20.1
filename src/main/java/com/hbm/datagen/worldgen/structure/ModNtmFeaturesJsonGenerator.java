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
 * Датаген для процедурной структуры NTM Features (LargeOffice, LargeOfficeCorner,
 * а в будущем — CivilianFeatures, BunkerComponents, SiloComponent).
 */
public class ModNtmFeaturesJsonGenerator implements DataProvider {

    private final DataGenerator generator;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    /** Аналог StructureConfig: structureMinChunks=4, structureMaxChunks=12. */
    private static final int SPACING = 12;
    private static final int SEPARATION = 4;

    private static final int SALT = 14357617;  // как в MapGenNTMFeatures

    public ModNtmFeaturesJsonGenerator(DataGenerator generator) {
        this.generator = generator;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput cache) {
        Path outputPath = generator.getPackOutput().getOutputFolder();
        List<CompletableFuture<?>> futures = new ArrayList<>();

        futures.add(generateStructure(cache, outputPath));
        // Сета нет намеренно: ntm_features входит записью в общий overworld_set
        // (вес 50, как featuresSpawnWeight эталона), иначе на клетку приходилось
        // бы две попытки — своя сетка плюс общая (#103).
        futures.add(generateBiomeTag(cache, outputPath));

        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
    }

    // ============================================================
    //  structure/ntm_features.json
    // ============================================================

    private CompletableFuture<?> generateStructure(CachedOutput cache, Path outputPath) {
        JsonObject root = new JsonObject();
        root.addProperty("type", "hbm:ntm_features");

        JsonObject settings = new JsonObject();
        settings.addProperty("biomes", "#" + MODID + ":ntm_features_biomes");
        settings.addProperty("step", "surface_structures");
        settings.addProperty("terrain_adaptation", "none");
        settings.add("spawn_overrides", new JsonObject());
        root.add("settings", settings);

        Path path = outputPath.resolve("data/" + MODID + "/worldgen/structure/ntm_features.json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    // ============================================================
    //  tags/worldgen/biome/ntm_features_biomes.json
    // ============================================================

    private CompletableFuture<?> generateBiomeTag(CachedOutput cache, Path outputPath) {
        JsonObject root = new JsonObject();
        JsonArray values = new JsonArray();

        // Оригинал исключал ocean/river/frozenOcean/frozenRiver/deepOcean.
        // В 1.20.1 используем is_overworld и отсекаем океаны внутри findGenerationPoint
        // (проще, чем городить кастомный тег с исключениями).
        values.add("#minecraft:is_overworld");

        root.add("values", values);

        Path path = outputPath.resolve("data/" + MODID + "/tags/worldgen/biome/ntm_features_biomes.json");
        return DataProvider.saveStable(cache, GSON.toJsonTree(root).getAsJsonObject(), path);
    }

    @Override
    public @NotNull String getName() {
        return "NTM Features Structure JSON Generator";
    }
}