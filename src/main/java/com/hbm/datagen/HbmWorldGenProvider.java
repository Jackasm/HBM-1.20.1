package com.hbm.datagen;

import com.hbm.datagen.worldgen.ModBiomeModifiers;
import com.hbm.datagen.worldgen.ModBiomes;
import com.hbm.datagen.worldgen.ModConfiguredFeatures;
import com.hbm.datagen.worldgen.ModPlacedFeatures;
import com.hbm.util.ModDamageSource;
import com.hbm.util.RefStrings;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class HbmWorldGenProvider extends DatapackBuiltinEntriesProvider {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            // Damage Types
            .add(Registries.DAMAGE_TYPE, ModDamageSource::bootstrap)
            // World Gen
            .add(Registries.CONFIGURED_FEATURE, ModConfiguredFeatures::bootstrap)
            .add(Registries.PLACED_FEATURE, ModPlacedFeatures::bootstrap)
            .add(ForgeRegistries.Keys.BIOME_MODIFIERS, ModBiomeModifiers::bootstrap)
            .add(Registries.BIOME, ModBiomes::bootstrap);

    public HbmWorldGenProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries, BUILDER, Set.of(RefStrings.MODID));
    }
}