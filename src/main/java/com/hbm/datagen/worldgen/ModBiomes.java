package com.hbm.datagen.worldgen;

import com.hbm.sound.ModSounds;
import com.hbm.util.RefStrings;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeGenerationSettings;
import net.minecraft.world.level.biome.BiomeSpecialEffects;
import net.minecraft.world.level.biome.MobSpawnSettings;

import static com.hbm.util.ResLocation.ResLocation;

public class ModBiomes {

    public static final ResourceKey<Biome> CRATER = register("crater");
    public static final ResourceKey<Biome> CRATER_INNER = register("crater_inner");
    public static final ResourceKey<Biome> CRATER_OUTER = register("crater_outer");

    private static ResourceKey<Biome> register(String name) {
        return ResourceKey.create(Registries.BIOME, ResLocation(RefStrings.MODID, name));
    }

    public static void bootstrap(BootstapContext<Biome> context) {
        context.register(CRATER, createCraterBiome(context, false, false));
        context.register(CRATER_INNER, createCraterBiome(context, true, false));
        context.register(CRATER_OUTER, createCraterBiome(context, false, true));
    }

    private static Biome createCraterBiome(BootstapContext<Biome> context, boolean inner, boolean outer) {

        MobSpawnSettings.Builder spawnSettings = new MobSpawnSettings.Builder();
        spawnSettings.creatureGenerationProbability(0.0F);

        int waterColor = 0x505020;
        int waterFogColor = 0x505020;
        int skyColor = 0x525A52;
        int grassColor = 0x505050;
        int foliageColor = 0x6A7039;

        if (inner) {
            grassColor = 0x303030;
            skyColor = 0x424A42;
        } else if (outer) {
            grassColor = 0x6F6752;
            skyColor = 0x6B9189;
        }

        BiomeSpecialEffects effects = new BiomeSpecialEffects.Builder()
                .waterColor(waterColor)
                .waterFogColor(waterFogColor)
                .fogColor(0xC0D8FF)
                .skyColor(skyColor)
                .foliageColorOverride(foliageColor)
                .grassColorOverride(grassColor)
                .build();

        // Пустая генерация (без фич) - можно добавить позже
        BiomeGenerationSettings.Builder generationSettings = new BiomeGenerationSettings.Builder(
                context.lookup(Registries.PLACED_FEATURE),
                context.lookup(Registries.CONFIGURED_CARVER)
        );

        return new Biome.BiomeBuilder()
                .hasPrecipitation(false)
                .temperature(0.8F)
                .downfall(0.0F)
                .specialEffects(effects)
                .mobSpawnSettings(spawnSettings.build())
                .generationSettings(generationSettings.build())
                .build();
    }
}