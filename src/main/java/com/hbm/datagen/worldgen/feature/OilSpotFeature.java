package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.worldgen.feature.config.OilSpotConfiguration;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class OilSpotFeature extends Feature<OilSpotConfiguration> {

    public OilSpotFeature(com.mojang.serialization.Codec<OilSpotConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<OilSpotConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        OilSpotConfiguration cfg = context.config();

        for (int i = 0; i < cfg.count(); i++) {
            int rX = origin.getX() + (int) (rand.nextGaussian() * cfg.width());
            int rZ = origin.getZ() + (int) (rand.nextGaussian() * cfg.width());
            int rY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, rX, rZ);

            for (int y = rY; y > rY - 4; y--) {
                BlockPos pos = new BlockPos(rX, y, rZ);
                BlockPos belowPos = pos.below();
                BlockState ground = level.getBlockState(pos);
                BlockState below = level.getBlockState(belowPos);

                if (!below.isSolid()) break;

                // === НЕ ТРОГАЕМ MUSTARD WILLOWS ===
                if (isMustardWillow(ground)) continue;

                // === Мёртвые растения ===
                Block deadReplacement = getDeadReplacement(ground);
                if (deadReplacement != null) {
                    // Для травы — 10% шанс оставить мёртвую траву, 90% удалить
                    if (ground.is(Blocks.GRASS) || ground.is(Blocks.TALL_GRASS)) {
                        if (rand.nextInt(10) == 0) {
                            level.setBlock(pos, deadReplacement.defaultBlockState(), 3);
                        } else {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        }
                    } else {
                        level.setBlock(pos, deadReplacement.defaultBlockState(), 3);
                    }
                    continue;
                }

                // === Земля/трава ===
                if (ground.is(Blocks.GRASS_BLOCK) || ground.is(Blocks.DIRT)) {
                    BlockState newState = rand.nextInt(10) == 0
                            ? ModBlocks.DIRT_OILY.get().defaultBlockState()
                            : ModBlocks.DIRT_DEAD.get().defaultBlockState();
                    level.setBlock(pos, newState, 2);

                    if (cfg.addWillows() && rand.nextInt(50) == 0) {
                        BlockPos abovePos = pos.above();
                        if (level.getBlockState(abovePos).isAir()) {
                            level.setBlock(abovePos,
                                    ModBlocks.PLANT_FLOWER_CD0.get().defaultBlockState(), 3);
                        }
                    }
                    break;
                }

                // === Песок ===
                if (ground.is(Blocks.SAND) || ground.is(ModBlocks.ORE_OIL_SAND.get())) {
                    level.setBlock(pos, ModBlocks.SAND_DIRTY.get().defaultBlockState(), 2);
                    break;
                }

                // === Камень ===
                if (ground.is(Blocks.STONE)) {
                    level.setBlock(pos, ModBlocks.STONE_CRACKED.get().defaultBlockState(), 2);
                    break;
                }

                // === Листва ===
                if (ground.is(BlockTags.LEAVES)) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                    break;
                }
            }
        }

        return true;
    }

    /**
     * Mustard willows — не трогаем.
     * CD0/CD1 — маленькие willows, большие (CD2/CD3/CD4) — если есть.
     */
    private boolean isMustardWillow(BlockState state) {
        if (state.is(ModBlocks.PLANT_FLOWER_CD0.get())
                || state.is(ModBlocks.PLANT_FLOWER_CD1.get())) {
            return true;
        }
        // Если есть tall-версии CD2, CD3, CD4 — раскомментируй:
        // if (state.is(ModBlocks.PLANT_TALL_CD2.get())
        //         || state.is(ModBlocks.PLANT_TALL_CD3.get())
        //         || state.is(ModBlocks.PLANT_TALL_CD4.get())) {
        //     return true;
        // }
        return false;
    }

    /**
     * Подбирает мёртвый блок по типу растения.
     */
    private Block getDeadReplacement(BlockState ground) {
        // Трава → PLANT_DEAD_GRASS
        if (ground.is(Blocks.GRASS) || ground.is(Blocks.TALL_GRASS)) {
            return ModBlocks.PLANT_DEAD_GRASS.get();
        }
        // Папоротник → PLANT_DEAD_FERN
        if (ground.is(Blocks.FERN) || ground.is(Blocks.LARGE_FERN)) {
            return ModBlocks.PLANT_DEAD_FERN.get();
        }
        // Цветы (ванильные + HBM) → PLANT_DEAD_FLOWER
        if (ground.is(BlockTags.FLOWERS)
                || ground.is(ModBlocks.PLANT_FLOWER_FOXGLOVE.get())
                || ground.is(ModBlocks.PLANT_FLOWER_TOBACCO.get())
                || ground.is(ModBlocks.PLANT_FLOWER_NIGHTSHADE.get())
                || ground.is(ModBlocks.PLANT_FLOWER_WEED.get())) {
            return ModBlocks.PLANT_DEAD_FLOWER.get();
        }
        // Большие растения → PLANT_DEAD_BIGFLOWER
        if (ground.is(Blocks.SUNFLOWER)
                || ground.is(Blocks.LILAC)
                || ground.is(Blocks.ROSE_BUSH)
                || ground.is(Blocks.PEONY)) {
            return ModBlocks.PLANT_DEAD_BIGFLOWER.get();
        }
        // Саженцы, кусты → PLANT_DEAD_GENERIC
        if (ground.is(BlockTags.SAPLINGS)
                || ground.is(Blocks.DEAD_BUSH)
                || ground.is(Blocks.SWEET_BERRY_BUSH)) {
            return ModBlocks.PLANT_DEAD_GENERIC.get();
        }
        return null;
    }
}