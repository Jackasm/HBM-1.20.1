package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockReeds;
import com.hbm.datagen.worldgen.ModBiomes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;

/**
 * Server-side regression for biome tint inputs and the registered reed family.
 * Actual Minecraft tinted-cross model baking and visual colors are client-only,
 * and require a local runClient check; this test does not assert rendered pixels.
 */
public final class ReedsBiomeTintTestRunner extends TestRunner {

    public ReedsBiomeTintTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        if (tickCount++ != 0) return true;

        check("reeds_bottom_registered", ModBlocks.REEDS_BOTTOM.get() instanceof BlockReeds);
        check("reeds_mid_registered", ModBlocks.REEDS_MID.get() instanceof BlockReeds);
        check("reeds_top_registered", ModBlocks.REEDS_TOP.get() instanceof BlockReeds);

        var biomes = level.registryAccess().registryOrThrow(net.minecraft.core.registries.Registries.BIOME);
        Biome outer = biomes.getHolderOrThrow(ModBiomes.CRATER_OUTER).value();
        Biome main = biomes.getHolderOrThrow(ModBiomes.CRATER).value();
        Biome inner = biomes.getHolderOrThrow(ModBiomes.CRATER_INNER).value();

        check("outer_grass_tint", outer.getGrassColor(0, 0) == 0x6F6752);
        check("main_grass_tint", main.getGrassColor(0, 0) == 0x505050);
        check("inner_grass_tint", inner.getGrassColor(0, 0) == 0x303030);
        check("biomes_have_distinct_grass_colors",
                outer.getGrassColor(0, 0) != main.getGrassColor(0, 0)
                && main.getGrassColor(0, 0) != inner.getGrassColor(0, 0));
        check("inner_darker_than_main",
                (inner.getGrassColor(0, 0) & 255) < (main.getGrassColor(0, 0) & 255));

        metric("cases", 8);
        finish(checks.stream().allMatch(result -> result.pass));
        return true;
    }
}
