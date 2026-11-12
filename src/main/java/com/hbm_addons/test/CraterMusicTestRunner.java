package com.hbm_addons.test;

import com.hbm.datagen.worldgen.ModBiomes;
import com.hbm.sound.ModSounds;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.biome.Biome;

import java.util.Optional;

/**
 * Non-destructive registration/data regression.
 *
 * This scenario verifies three real crater biome definitions reference the
 * same registered SoundEvent with the correct delays and replacement mode.
 * It does not decode OGG files or simulate Minecraft's client MusicManager.
 */
public final class CraterMusicTestRunner extends TestRunner {

    private static final int MIN_DELAY = 200;
    private static final int MAX_DELAY = 1200;

    public CraterMusicTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        if (tickCount++ != 0) return true;

        check("crater_sound_registered", ModSounds.MUSIC_CRATER.isPresent());
        check("crater_sound_id", ModSounds.MUSIC_CRATER.getId().toString().equals("hbm:music.crater"));

        var biomes = level.registryAccess().registryOrThrow(Registries.BIOME);
        validateBiome("outer", biomes.getHolderOrThrow(ModBiomes.CRATER_OUTER).value());
        validateBiome("main", biomes.getHolderOrThrow(ModBiomes.CRATER).value());
        validateBiome("inner", biomes.getHolderOrThrow(ModBiomes.CRATER_INNER).value());

        metric("cases", 17);
        finish(checks.stream().allMatch(result -> result.pass));
        return true;
    }

    private void validateBiome(String label, Biome biome) {
        Optional<Music> candidate = biome.getBackgroundMusic();
        check(label + "_has_music", candidate.isPresent());
        if (candidate.isEmpty()) {
            // All checks are still reported when one biome lacks music.
            check(label + "_event", false);
            check(label + "_min_delay", false);
            check(label + "_max_delay", false);
            check(label + "_replace_vanilla", false);
            return;
        }

        Music music = candidate.get();
        check(label + "_event", music.getEvent().unwrapKey()
                .map(key -> key.location().equals(ModSounds.MUSIC_CRATER.getId()))
                .orElse(false));
        check(label + "_min_delay", music.getMinDelay() == MIN_DELAY);
        check(label + "_max_delay", music.getMaxDelay() == MAX_DELAY);
        check(label + "_replace_vanilla", music.replaceCurrentMusic());
    }
}
