package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.block.TileEntityBedrockOre;
import com.hbm.util.WeightedRandomGeneric;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import java.util.List;

public class BedrockOreFeature extends Feature<BedrockOreConfiguration> {

    public BedrockOreFeature(Codec<BedrockOreConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BedrockOreConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        BedrockOreConfiguration config = context.config();

        // Шанс генерации
        if (rand.nextInt(config.chance()) != 0) return false;

        // Выбираем случайную руду
        WeightedRandomGeneric<BedrockOreDefinition> randomItem = getRandomItem(rand, BedrockOre.weightedOres);
        if (randomItem == null) return false;
        BedrockOreDefinition def = randomItem.get();

        // Координата центра (в пределах чанка, но с отступом 3 блока от края)
        int x = origin.getX() + 3 + rand.nextInt(10);
        int z = origin.getZ() + 3 + rand.nextInt(10);

        // Ищем подходящий Y (только на уровне бедрока)
        int y = findBedrockY(level, x, z);
        if (y == -1) return false;

        BlockPos center = new BlockPos(x, y, z);

        // Устанавливаем руду
        level.setBlock(center, ModBlocks.ORE_BEDROCK.get().defaultBlockState(), 3);
        if (level.getBlockEntity(center) instanceof TileEntityBedrockOre te) {
            te.resource = def.stack.copy();
            te.acidRequirement = def.acid != null ? def.acid.copy() : null;
            te.tier = def.tier;
            te.color = def.color;
            te.shape = rand.nextInt(10);
            te.setChanged();
        }

        // Замена окружающих блоков на глубинный камень (только в пределах текущего чанка)
        int chunkMinX = origin.getX();
        int chunkMaxX = origin.getX() + 15;
        int chunkMinZ = origin.getZ();
        int chunkMaxZ = origin.getZ() + 15;

        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                for (int dy = 1; dy <= 6; dy++) {
                    BlockPos p = center.offset(dx, dy, dz);

                    // Проверяем, что блок в пределах текущего чанка
                    if (p.getX() < chunkMinX || p.getX() > chunkMaxX ||
                            p.getZ() < chunkMinZ || p.getZ() > chunkMaxZ) {
                        continue;
                    }

                    if (p.getY() < level.getMinBuildHeight() || p.getY() >= level.getMaxBuildHeight()) {
                        continue;
                    }

                    BlockState state = level.getBlockState(p);
                    if (state.getBlock() == Blocks.STONE || state.getBlock() == Blocks.BEDROCK) {
                        level.setBlock(p, ModBlocks.STONE_DEPTH.get().defaultBlockState(), 3);
                    }
                }
            }
        }
        return true;
    }

    private int findBedrockY(WorldGenLevel level, int x, int z) {
        // Поиск сверху вниз для быстрого нахождения bedrock
        for (int y = level.getMaxBuildHeight() - 1; y >= level.getMinBuildHeight(); y--) {
            BlockPos pos = new BlockPos(x, y, z);
            if (level.getBlockState(pos).is(Blocks.BEDROCK)) {
                // Возвращаем позицию бедрока, не выше
                return y;
            }
        }
        return -1;
    }

    private static <T> WeightedRandomGeneric<T> getRandomItem(RandomSource rand, List<WeightedRandomGeneric<T>> list) {
        int totalWeight = list.stream().mapToInt(WeightedRandomGeneric::getWeight).sum();
        if (totalWeight <= 0) return null;
        int roll = rand.nextInt(totalWeight);
        int current = 0;
        for (WeightedRandomGeneric<T> entry : list) {
            current += entry.getWeight();
            if (roll < current) return entry;
        }
        return null;
    }
}