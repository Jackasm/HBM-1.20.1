package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;

import com.hbm.datagen.worldgen.util.DungeonToolbox;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class AncientTombFeature extends Feature<NoneFeatureConfiguration> {

    public AncientTombFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        if (!level.getBiome(origin).is(Biomes.DESERT)
                && !level.getBiome(origin).is(Biomes.BADLANDS)
                && !level.getBiome(origin).is(Biomes.ERODED_BADLANDS)
                && !level.getBiome(origin).is(Biomes.WOODED_BADLANDS)) {
            return false;
        }

        int x = origin.getX();
        int z = origin.getZ();

        // yOff = max(getHeight, 35) - 5
        int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        int yOff = Math.max(surfaceY, 35) - 5;

        // y для гробницы — фиксированный 20
        int y = 20;

        build(level, rand, x, y, z, yOff);
        return true;
    }

    private static void build(WorldGenLevel level, RandomSource rand, int x, int y, int z, int yOff) {

        List<BlockState> concrete = List.of(
                ModBlocks.BRICK_CONCRETE.get().defaultBlockState(),
                ModBlocks.BRICK_CONCRETE_BROKEN.get().defaultBlockState(),
                ModBlocks.BRICK_CONCRETE_CRACKED.get().defaultBlockState()
        );

        /// PYRAMID ///
        int pySize = 15;

        for (int iy = pySize; iy > 0; iy--) {
            int range = (pySize - iy);

            for (int ix = -range; ix <= range; ix++) {
                for (int iz = -range; iz <= range; iz++) {

                    if ((ix <= -range + 1 || ix >= range - 1) && (iz <= -range + 1 || iz >= range - 1)) {
                        set(level, x + ix, yOff + iy, z + iz, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
                        continue;
                    }

                    if (iy == 1) {
                        set(level, x + ix, yOff + iy, z + iz, ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState());
                        continue;
                    }

                    if ((ix <= -range + 1 || ix >= range - 1) || (iz <= -range + 1 || iz >= range - 1)) {
                        set(level, x + ix, yOff + iy, z + iz, ModBlocks.CONCRETE_SMOOTH.get().defaultBlockState());
                        continue;
                    }

                    set(level, x + ix, yOff + iy, z + iz, Blocks.AIR.defaultBlockState());
                }
            }
        }

        // Маркированные кирпичи внутри
        DungeonToolbox.generateBox(level, x - 2, yOff + 2, z - 2, 5, 4, 5, concrete);
        set(level, x + 2, yOff + 3, z, ModBlocks.BRICK_CONCRETE_MARKED.get().defaultBlockState());
        set(level, x - 2, yOff + 3, z, ModBlocks.BRICK_CONCRETE_MARKED.get().defaultBlockState());
        set(level, x, yOff + 3, z + 2, ModBlocks.BRICK_CONCRETE_MARKED.get().defaultBlockState());
        set(level, x, yOff + 3, z - 2, ModBlocks.BRICK_CONCRETE_MARKED.get().defaultBlockState());

        // Колонны по углам
        DungeonToolbox.generateBox(level, x + 5, yOff + 2, z + 5, 1, 7, 1, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        DungeonToolbox.generateBox(level, x + 5, yOff + 2, z - 5, 1, 7, 1, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        DungeonToolbox.generateBox(level, x - 5, yOff + 2, z - 5, 1, 7, 1, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());
        DungeonToolbox.generateBox(level, x - 5, yOff + 2, z + 5, 1, 7, 1, ModBlocks.CONCRETE_PILLAR.get().defaultBlockState());

        /// SPIKES ///
        int spikeCount = 36 + rand.nextInt(15);
        double angle = 0;
        double angleStep = Math.toRadians(360.0 / spikeCount);

        for (int i = 0; i < spikeCount; i++) {
            angle += angleStep;
            double variance = 1.0 + rand.nextDouble() * 0.4;
            int ix = (int) (x + Math.cos(angle) * 20 * variance);
            int iz = (int) (z + Math.sin(angle) * 20 * variance);
            int iy = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, ix, iz) - 3;

            for (int j = iy; j < iy + 7; j++) {
                set(level, ix, j, iz, ModBlocks.DECO_STEEL.get().defaultBlockState());
            }
        }

        /// TUNNEL ///
        double sAngle = 0;
        double sAngleStep = Math.toRadians(360.0 / 32.0);

        for (int i = y - 1; i < yOff + 2; i++) {
            int ix = (int) Math.floor(Math.cos(sAngle) * 10);
            int iz = (int) Math.floor(Math.sin(sAngle) * 10);

            int h = i < yOff ? 3 : 2;

            if (i > 40) {
                DungeonToolbox.generateBox(level, x + ix - 1, i, z + iz - 1, 3, h, 3, Blocks.AIR.defaultBlockState());
            } else {
                DungeonToolbox.generateBox(level, x + ix - 1, i, z + iz - 1, 3, h, 3, ModBlocks.GAS_RADON_TOMB.get().defaultBlockState());
            }

            for (int dx = x + ix - 2; dx < x + ix + 3; dx++) {
                for (int dy = i - 1; dy < i + 4; dy++) {
                    for (int dz = z + iz - 2; dz < z + iz + 3; dz++) {
                        BlockState b = level.getBlockState(new BlockPos(dx, dy, dz));
                        if (!b.isAir() && !b.is(ModBlocks.GAS_RADON_TOMB.get())
                                && !b.is(ModBlocks.CONCRETE.get())
                                && !b.is(ModBlocks.CONCRETE_SMOOTH.get())
                                && !b.is(ModBlocks.BRICK_CONCRETE.get())
                                && !b.is(ModBlocks.BRICK_CONCRETE_CRACKED.get())
                                && !b.is(ModBlocks.BRICK_CONCRETE_BROKEN.get())) {
                            BlockState meta = DungeonToolbox.getRandom(concrete, rand);
                            set(level, dx, dy, dz, meta);
                        }
                    }
                }
            }

            sAngle += sAngleStep;
        }

        /// ACCESS TUNNEL ///
        for (int dx = x + 4; dx < x + 8; dx++) {
            for (int dy = y - 1; dy < y + 4; dy++) {
                for (int dz = z - 2; dz < z + 3; dz++) {
                    BlockState b = level.getBlockState(new BlockPos(dx, dy, dz));
                    if (!b.isAir() && !b.is(ModBlocks.GAS_RADON_TOMB.get())
                            && !b.is(ModBlocks.CONCRETE.get())
                            && !b.is(ModBlocks.CONCRETE_SMOOTH.get())
                            && !b.is(ModBlocks.BRICK_CONCRETE.get())
                            && !b.is(ModBlocks.BRICK_CONCRETE_CRACKED.get())
                            && !b.is(ModBlocks.BRICK_CONCRETE_BROKEN.get())) {
                        BlockState meta = DungeonToolbox.getRandom(concrete, rand);
                        set(level, dx, dy, dz, meta);
                    }
                }
            }
        }

        /// TOMB CHAMBER ///
        int size = 5;
        int cladding = size - 1;
        int core = size - 2;

        int dimOuter = size * 2 + 1;      // 11
        int dimInner = cladding * 2 + 1;  // 9
        int dimCore = core * 2 + 1;       // 7

        // Внешние стены (concrete)
        DungeonToolbox.generateBox(level, x - size, y - size, z - size, 1, dimOuter, dimOuter, concrete);
        DungeonToolbox.generateBox(level, x - size, y - size, z - size, dimOuter, 1, dimOuter, concrete);
        DungeonToolbox.generateBox(level, x - size, y - size, z - size, dimOuter, dimOuter, 1, concrete);
        DungeonToolbox.generateBox(level, x + size, y - size, z - size, 1, dimOuter, dimOuter, concrete);
        DungeonToolbox.generateBox(level, x - size, y + size, z - size, dimOuter, 1, dimOuter, concrete);
        DungeonToolbox.generateBox(level, x - size, y - size, z + size, dimOuter, dimOuter, 1, concrete);

        // Внутренние стены (brick_obsidian)
        BlockState obsidian = ModBlocks.BRICK_OBSIDIAN.get().defaultBlockState();
        DungeonToolbox.generateBox(level, x - cladding, y - cladding, z - cladding, 1, dimInner, dimInner, obsidian);
        DungeonToolbox.generateBox(level, x - cladding, y - cladding, z - cladding, dimInner, 1, dimInner, obsidian);
        DungeonToolbox.generateBox(level, x - cladding, y - cladding, z - cladding, dimInner, dimInner, 1, obsidian);
        DungeonToolbox.generateBox(level, x + cladding, y - cladding, z - cladding, 1, dimInner, dimInner, obsidian);
        DungeonToolbox.generateBox(level, x - cladding, y + cladding, z - cladding, dimInner, 1, dimInner, obsidian);
        DungeonToolbox.generateBox(level, x - cladding, y - cladding, z + cladding, dimInner, dimInner, 1, obsidian);

        // Ядро (ancient_scrap)
        DungeonToolbox.generateBox(level, x - core, y - core, z - core, dimCore, dimCore, dimCore, ModBlocks.ANCIENT_SCRAP.get().defaultBlockState());

        /// ACCESS ///
        DungeonToolbox.generateBox(level, x + 6, y - 2, z - 1, 2, 1, 3, concrete);
        DungeonToolbox.generateBox(level, x + 4, y - 1, z - 1, 5, 3, 3, ModBlocks.GAS_RADON_TOMB.get().defaultBlockState());
        DungeonToolbox.generateBox(level, x + 6, y + 2, z - 1, 2, 1, 3, concrete);
    }

    /** Установка блока с проверкой высоты. */
    private static void set(WorldGenLevel level, int x, int y, int z, BlockState state) {
        if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
            level.setBlock(new BlockPos(x, y, z), state, 2);
        }
    }
}