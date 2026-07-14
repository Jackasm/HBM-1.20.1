package com.hbm.datagen.worldgen;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.registries.ForgeRegistries;

public record MobSpawnBiomeModifier(HolderSet<Biome> biomes, EntityType<?> entityType, int weight, int minCount, int maxCount, MobCategory category) implements BiomeModifier {

    public static final Codec<MobSpawnBiomeModifier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Biome.LIST_CODEC.fieldOf("biomes").forGetter(MobSpawnBiomeModifier::biomes),
            ForgeRegistries.ENTITY_TYPES.getCodec().fieldOf("entity_type").forGetter(MobSpawnBiomeModifier::entityType),
            Codec.INT.fieldOf("weight").forGetter(MobSpawnBiomeModifier::weight),
            Codec.INT.fieldOf("min_count").forGetter(MobSpawnBiomeModifier::minCount),
            Codec.INT.fieldOf("max_count").forGetter(MobSpawnBiomeModifier::maxCount),
            MobCategory.CODEC.fieldOf("category").forGetter(MobSpawnBiomeModifier::category)
    ).apply(instance, MobSpawnBiomeModifier::new));

    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && biomes.contains(biome)) {
            builder.getMobSpawnSettings().addSpawn(category, new MobSpawnSettings.SpawnerData(entityType, weight, minCount, maxCount));
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return CODEC;
    }
}