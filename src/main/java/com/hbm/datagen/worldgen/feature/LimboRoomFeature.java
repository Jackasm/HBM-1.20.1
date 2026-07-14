package com.hbm.datagen.worldgen.feature;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LimboRoomFeature extends Feature<NoneFeatureConfiguration> {

    public LimboRoomFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        // Строим комнату 7x7x5 вокруг origin (origin — центр пола)
        buildRoom(level, origin);

        return true;
    }

    private void buildRoom(WorldGenLevel level, BlockPos center) {
        int y = center.getY();

        // Пол 7x7 из бедрока
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(center.offset(dx, 0, dz), Blocks.BEDROCK.defaultBlockState(), 3);
            }
        }

        // Стены (высота 4 блока)
        for (int h = 1; h <= 4; h++) {
            for (int dx = -3; dx <= 3; dx++) {
                level.setBlock(center.offset(dx, h, -3), Blocks.BEDROCK.defaultBlockState(), 3);
                level.setBlock(center.offset(dx, h, 3), Blocks.BEDROCK.defaultBlockState(), 3);
            }
            for (int dz = -2; dz <= 2; dz++) {
                level.setBlock(center.offset(-3, h, dz), Blocks.BEDROCK.defaultBlockState(), 3);
                level.setBlock(center.offset(3, h, dz), Blocks.BEDROCK.defaultBlockState(), 3);
            }
        }

        // Потолок
        for (int dx = -3; dx <= 3; dx++) {
            for (int dz = -3; dz <= 3; dz++) {
                level.setBlock(center.offset(dx, 5, dz), Blocks.BEDROCK.defaultBlockState(), 3);
            }
        }

        // Кровать (направление на юг)
        level.setBlock(center.offset(-2, 1, 2), Blocks.RED_BED.defaultBlockState()
                .setValue(net.minecraft.world.level.block.BedBlock.FACING, Direction.SOUTH)
                .setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.FOOT), 3);
        level.setBlock(center.offset(-2, 1, 1), Blocks.RED_BED.defaultBlockState()
                .setValue(net.minecraft.world.level.block.BedBlock.FACING, Direction.SOUTH)
                .setValue(net.minecraft.world.level.block.BedBlock.PART, net.minecraft.world.level.block.state.properties.BedPart.HEAD), 3);

        // Стол (ступеньки)
        level.setBlock(center.offset(0, 1, -1), Blocks.OAK_STAIRS.defaultBlockState()
                .setValue(net.minecraft.world.level.block.StairBlock.FACING, Direction.NORTH), 3);
        level.setBlock(center.offset(1, 1, -1), Blocks.OAK_STAIRS.defaultBlockState()
                .setValue(net.minecraft.world.level.block.StairBlock.FACING, Direction.NORTH), 3);

        // Стул (ступенька)
        level.setBlock(center.offset(0, 1, -2), Blocks.OAK_STAIRS.defaultBlockState()
                .setValue(net.minecraft.world.level.block.StairBlock.FACING, Direction.SOUTH), 3);

        // Факелы
        level.setBlock(center.offset(0, 2, -3), Blocks.TORCH.defaultBlockState(), 3);
        level.setBlock(center.offset(0, 2, 3), Blocks.TORCH.defaultBlockState(), 3);

        // Кнопка возврата на стене
        level.setBlock(center.offset(3, 2, 0), Blocks.STONE_BUTTON.defaultBlockState()
                .setValue(net.minecraft.world.level.block.ButtonBlock.FACING, Direction.WEST), 3);

        // Сундук с предметами для выживания
        level.setBlock(center.offset(-1, 1, -1), Blocks.CHEST.defaultBlockState()
                .setValue(ChestBlock.FACING, Direction.SOUTH), 3);
        BlockEntity te = level.getBlockEntity(center.offset(-1, 1, -1));
        if (te instanceof ChestBlockEntity chest) {
            chest.setItem(0, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.BREAD, 5));
            chest.setItem(1, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.STONE_SWORD, 1));
            chest.setItem(2, new net.minecraft.world.item.ItemStack(net.minecraft.world.item.Items.STONE_PICKAXE, 1));
        }

        // Портал возврата (золотые блоки)
        level.setBlock(center.offset(2, 1, 0), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
        level.setBlock(center.offset(2, 2, 0), Blocks.GOLD_BLOCK.defaultBlockState(), 3);
    }
}