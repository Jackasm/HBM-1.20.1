package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockBobble.BobbleType;

import com.hbm.config.WorldConfig;
import com.hbm.datagen.worldgen.util.DungeonToolbox;
import com.hbm.tileentity.deco.TileEntityBobble;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BiomeTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class ArcticVaultFeature extends Feature<NoneFeatureConfiguration> {

    public ArcticVaultFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        int x = origin.getX();
        int y = origin.getY() - 1;  // y--, как в оригинале
        int z = origin.getZ();

        // Проверка: холодный биом (температура < 0.2) и под землёй камень
        if (!level.getBiome(origin).is(BiomeTags.IS_OVERWORLD)) return false;

        float temp = level.getBiome(origin).value().getBaseTemperature();
        if (temp >= 0.2F) return false;

        BlockState below = level.getBlockState(new BlockPos(x, y, z));
        if (!below.isSolid()) return false;

        build(level, rand, x, y, z);
        return true;
    }

    private void build(WorldGenLevel level, RandomSource rand, int x, int y, int z) {

        // ==================== СПИСКИ БЛОКОВ ====================
        List<BlockState> brick = List.of(
                Blocks.STONE_BRICKS.defaultBlockState(),
                Blocks.CRACKED_STONE_BRICKS.defaultBlockState()
        );
        List<BlockState> web = List.of(
                Blocks.AIR.defaultBlockState(),
                Blocks.AIR.defaultBlockState(),
                Blocks.COBWEB.defaultBlockState()
        );
        List<BlockState> crates = List.of(
                ModBlocks.CRATE.get().defaultBlockState(),
                ModBlocks.CRATE_METAL.get().defaultBlockState(),
                ModBlocks.CRATE_AMMO.get().defaultBlockState(),
                ModBlocks.CRATE_CAN.get().defaultBlockState(),
                ModBlocks.CRATE_JUNGLE.get().defaultBlockState()
        );
        BlockState snow = Blocks.SNOW.defaultBlockState()
                .setValue(BlockStateProperties.LAYERS, 1);

        // ==================== КАРКАС ====================
        // Пол (11x1x11)
        DungeonToolbox.generateBox(level, x - 5, y, z - 5, 11, 1, 11, brick);
        // Потолок (11x1x11)
        DungeonToolbox.generateBox(level, x - 5, y + 6, z - 5, 11, 1, 11, brick);
        // Северная стена (11x5x1)
        DungeonToolbox.generateBox(level, x - 5, y + 1, z - 5, 11, 5, 1, brick);
        // Южная стена (11x5x1)
        DungeonToolbox.generateBox(level, x - 5, y + 1, z + 5, 11, 5, 1, brick);
        // Западная стена (1x5x11)
        DungeonToolbox.generateBox(level, x - 5, y + 1, z - 5, 1, 5, 11, brick);
        // Восточная стена (1x5x11)
        DungeonToolbox.generateBox(level, x + 5, y + 1, z - 5, 1, 5, 11, brick);

        // Внутреннее пространство (9x3x9)
        DungeonToolbox.generateBox(level, x - 4, y + 1, z - 4, 9, 3, 9, Blocks.AIR.defaultBlockState());
        // Снежный слой внутри (9x1x9)
        DungeonToolbox.generateBox(level, x - 4, y + 1, z - 4, 9, 1, 9, snow);

        // ==================== TAPE_RECORDERS ====================
        BlockState tapeRecSouth = ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH);
        BlockState tapeRecNorth = ModBlocks.TAPE_RECORDER.get().defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH);

        // Северная сторона: 5x2x1 tape_recorder (meta 3 = юг)
        DungeonToolbox.generateBox(level, x - 2, y + 1, z - 2, 5, 2, 1, tapeRecSouth);
        // Снег над ней: 5x1x1
        DungeonToolbox.generateBox(level, x - 2, y + 3, z - 2, 5, 1, 1, snow);

        // Южная сторона: 5x2x1 tape_recorder (meta 2 = север)
        DungeonToolbox.generateBox(level, x - 2, y + 1, z + 2, 5, 2, 1, tapeRecNorth);
        // Снег над ней: 5x1x1
        DungeonToolbox.generateBox(level, x - 2, y + 3, z + 2, 5, 1, 1, snow);

        // Паутина (9x2x9)
        DungeonToolbox.generateBox(level, x - 4, y + 4, z - 4, 9, 2, 9, web);

        // ==================== 15 СЛУЧАЙНЫХ ТОЧЕК ====================
        for (int i = 0; i < 15; i++) {
            int ix = x - 4 + rand.nextInt(10);
            int iz = z - 4 + rand.nextInt(10);

            BlockPos checkPos = new BlockPos(ix, y + 1, iz);
            BlockState checkState = level.getBlockState(checkPos);

            // Проверяем, что там снег
            if (!checkState.is(Blocks.SNOW)) continue;

            if (i == 0) {
                // Первый — боблхед
                level.setBlock(checkPos,
                        ModBlocks.BOBBLEHEAD.get().defaultBlockState(), 3);
                if (level.getBlockEntity(checkPos) instanceof TileEntityBobble bobble) {
                    BobbleType[] types = BobbleType.values();
                    bobble.type = types[rand.nextInt(types.length - 1) + 1];
                    bobble.setChanged();
                }
            } else {
                // Остальные — ящики + снег сверху
                BlockState crateState = DungeonToolbox.getRandom(crates, rand);
                if (crateState != null) {
                    level.setBlock(checkPos, crateState, 2);
                    level.setBlock(checkPos.above(), snow, 2);
                }
            }
        }

        // ==================== TAPE_RECORDER СВЕРХУ ====================
        int surfaceY = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, x, z);
        BlockPos surfacePos = new BlockPos(x, surfaceY, z);
        BlockState surfaceBelow = level.getBlockState(surfacePos.below());

        if (surfaceBelow.isSolid()) {
            level.setBlock(surfacePos, ModBlocks.TAPE_RECORDER.get().defaultBlockState(), 2);
        }
    }
}