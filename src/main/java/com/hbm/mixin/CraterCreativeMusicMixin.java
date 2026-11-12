package com.hbm.mixin;

import com.hbm.datagen.worldgen.ModBiomes;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.sounds.Music;
import net.minecraft.world.level.biome.Biome;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * In Minecraft 1.20.1, Creative music takes priority over the biome's
 * backgroundMusic. Restore biome selection only inside the three HBM crater
 * zones, without introducing a separate audio player or changing the vanilla
 * music timing/volume and without affecting other biomes or game modes.
 *
 * Client-only Mixin; registered under the "client" key in hbm.mixins.json.
 */
@Mixin(Minecraft.class)
public abstract class CraterCreativeMusicMixin {

    @Inject(method = "getSituationalMusic", at = @At("HEAD"), cancellable = true)
    private void hbm$useCraterMusicInCreative(CallbackInfoReturnable<Music> callback) {
        Minecraft minecraft = (Minecraft) (Object) this;
        LocalPlayer player = minecraft.player;
        ClientLevel level = minecraft.level;
        if (player == null || level == null) return;

        // Match the vanilla Creative-music eligibility check: the player may
        // have modified abilities even if their game mode is nominally Creative.
        if (!player.getAbilities().instabuild || !player.getAbilities().mayfly) return;

        Holder<Biome> biome = level.getBiome(player.blockPosition());
        if (biome.is(ModBiomes.CRATER)
                || biome.is(ModBiomes.CRATER_INNER)
                || biome.is(ModBiomes.CRATER_OUTER)) {
            // Use the actual loaded biome configuration (including datapacks),
            // rather than hardcoding a second event or scheduling parameters.
            biome.value().getBackgroundMusic().ifPresent(callback::setReturnValue);
        }
    }
}
