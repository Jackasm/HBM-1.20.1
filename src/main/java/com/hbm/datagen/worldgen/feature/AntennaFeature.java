package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.deco.DecoPoleSatelliteReceiver;
import com.hbm.blocks.deco.DecoSteelPoles;
import com.hbm.blocks.deco.DecoTapeRecorder;
import com.hbm.blocks.generic.BlockDecoCT;
import com.hbm.entity.DecoCTBlockEntity;
import com.hbm.itempool.ItemPoolsLegacy;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class AntennaFeature extends Feature<AntennaConfiguration> {

    public AntennaFeature(Codec<AntennaConfiguration> codec) {
        super(codec);
    }

    private boolean isValidSpawn(WorldGenLevel level, BlockPos pos) {

        for (int dx = 0; dx <= 2; dx++) {
            for (int dz = 0; dz <= 2; dz++) {
                BlockPos checkPos = pos.offset(dx, -1, dz);
                BlockState below = level.getBlockState(checkPos);

                if (!below.isSolid()) {
                    return false;
                }

                if (below.is(BlockTags.ICE)) {
                    return false;
                }
            }
        }

        for (int dx = 0; dx <= 2; dx++) {
            for (int dz = 0; dz <= 2; dz++) {
                BlockPos airPos = pos.offset(dx, 0, dz);
                BlockState current = level.getBlockState(airPos);

                if (!current.isAir()) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public boolean place(FeaturePlaceContext<AntennaConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        // Ищем подходящее место для спавна в пределах чанка
        for (int i = 0; i < 16; i++) {
            for (int j = 0; j < 16; j++) {
                int x = origin.getX() + i;
                int z = origin.getZ() + j;
                int y = level.getHeight(Heightmap.Types.WORLD_SURFACE, x, z);
                BlockPos pos = new BlockPos(x, y, z);

                if (isValidSpawn(level, pos)) {
                    generateStructure(level, rand, pos);
                    return true;
                }
            }
        }
        return false;
    }

    private void generateStructure(WorldGenLevel level, RandomSource rand, BlockPos pos) {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        setBlock(level, x + 1, y, z, ModBlocks.STEEL_POLES.get().defaultBlockState().setValue(DecoSteelPoles.FACING, Direction.EAST));
        setBlock(level, x, y, z + 1, ModBlocks.STEEL_POLES.get().defaultBlockState().setValue(DecoSteelPoles.FACING, Direction.SOUTH));
        placeDecoSteel(level, new BlockPos(x + 1, y, z + 1));
        setBlock(level, x + 2, y, z + 1, ModBlocks.TAPE_RECORDER.get().defaultBlockState().setValue(DecoTapeRecorder.FACING, Direction.EAST));
        setBlock(level, x + 1, y, z + 2, ModBlocks.STEEL_POLES.get().defaultBlockState().setValue(DecoSteelPoles.FACING, Direction.NORTH));

        //setBlock(level, x + 2, y, z + 2, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.EAST));
        generateBonusChest(level, rand, new BlockPos(x + 2, y, z + 2));
        BlockEntity te = level.getBlockEntity(new BlockPos(x + 2, y, z + 2));
        if (te instanceof ChestBlockEntity chest) {
            ItemPoolsLegacy.getAntennaPool().generate(rand, 8).forEach(stack -> {
                for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                    if (chest.getItem(slot).isEmpty()) {
                        chest.setItem(slot, stack);
                        break;
                    }
                }
            });
        }

        for (int h = 1; h <= 3; h++) {
            setBlock(level, x + 1, y + h, z, ModBlocks.STEEL_POLES.get().defaultBlockState().setValue(DecoSteelPoles.FACING, Direction.EAST));
            setBlock(level, x, y + h, z + 1, ModBlocks.STEEL_POLES.get().defaultBlockState().setValue(DecoSteelPoles.FACING, Direction.SOUTH));
            placeDecoSteel(level, new BlockPos(x + 1, y + h, z + 1));
            setBlock(level, x + 2, y + h, z + 1, ModBlocks.TAPE_RECORDER.get().defaultBlockState().setValue(DecoTapeRecorder.FACING, Direction.EAST));
            setBlock(level, x + 1, y + h, z + 2, ModBlocks.STEEL_POLES.get().defaultBlockState().setValue(DecoSteelPoles.FACING, Direction.NORTH));
        }

        for (int h = 2; h <= 3; h++) {
            placeDecoSteel(level, new BlockPos(x + 1, y + h, z + 1));
            placeDecoSteel(level, new BlockPos(x + 0, y + h, z + 1));
            placeDecoSteel(level, new BlockPos(x + 2, y + h, z + 1));
            placeDecoSteel(level, new BlockPos(x + 1, y + h, z + 0));
            placeDecoSteel(level, new BlockPos(x + 1, y + h, z + 2));

        }

        for (int h = 4; h <= 20; h++) {
            if (h == 13 || h == 17) {
                setBlock(level, x + 1, y + h, z + 1, ModBlocks.POLE_SATELLITE_RECEIVER.get().defaultBlockState().setValue(DecoPoleSatelliteReceiver.FACING, Direction.SOUTH));
            } else {
                setBlock(level, x + 1, y + h, z + 1, ModBlocks.STEEL_POLES.get().defaultBlockState().setValue(DecoSteelPoles.FACING, Direction.SOUTH));
            }
        }

        setBlock(level, x + 1, y + 20, z + 1, ModBlocks.POLE_TOP.get().defaultBlockState());

        // После завершения генерации структуры — несколько проходов для полного соединения
        for (int pass = 0; pass < 3; pass++) {
            for (int dx = -2; dx <= 2; dx++) {
                for (int dy = -1; dy <= 22; dy++) {
                    for (int dz = -2; dz <= 2; dz++) {
                        BlockPos checkPos = new BlockPos(x + dx, y + dy, z + dz);
                        if (isSameDecoSteel(level, checkPos)) {
                            updateDecoSteelConnections(level, checkPos);
                        }
                    }
                }
            }
        }
    }

    private void generateBonusChest(WorldGenLevel level, RandomSource rand, BlockPos pos) {
        int roll = rand.nextInt(100);

        if (roll < 50) {
            // Обычный сундук
            setBlock(level, pos.getX(), pos.getY(), pos.getZ(),
                    Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.EAST));

            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof ChestBlockEntity chest) {
                ItemPoolsLegacy.getAntennaPool().generate(rand, 8).forEach(stack -> {
                    for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                        if (chest.getItem(slot).isEmpty()) {
                            chest.setItem(slot, stack);
                            break;
                        }
                    }
                });
            }
        } else if (roll < 65) {
            // Ящик с припасами
            setBlock(level, pos.getX(), pos.getY(), pos.getZ(),
                    ModBlocks.CRATE.get().defaultBlockState());
        } else if (roll < 75) {
            // Оружейный ящик
            setBlock(level, pos.getX(), pos.getY(), pos.getZ(),
                    ModBlocks.CRATE_WEAPON.get().defaultBlockState());
        } else if (roll < 85) {
            // Свинцовый ящик (радиоактивные материалы)
            setBlock(level, pos.getX(), pos.getY(), pos.getZ(),
                    ModBlocks.CRATE_LEAD.get().defaultBlockState());
        } else if (roll < 92) {
            // Металлический ящик (механизмы)
            setBlock(level, pos.getX(), pos.getY(), pos.getZ(),
                    ModBlocks.CRATE_METAL.get().defaultBlockState());
        } else {
            // Красный ящик (редкие/пасхальные предметы)
            setBlock(level, pos.getX(), pos.getY(), pos.getZ(),
                    ModBlocks.CRATE_RED.get().defaultBlockState());
        }
    }

    private void setBlock(WorldGenLevel level, int x, int y, int z, BlockState state) {
        if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
            level.setBlock(new BlockPos(x, y, z), state, 2);
        }
    }

    private void placeDecoSteel(WorldGenLevel level, BlockPos pos) {
        BlockState state = ModBlocks.DECO_STEEL.get().defaultBlockState();

        // Проверяем соседей
        boolean north = isSameDecoSteel(level, pos.north());
        boolean south = isSameDecoSteel(level, pos.south());
        boolean east = isSameDecoSteel(level, pos.east());
        boolean west = isSameDecoSteel(level, pos.west());
        boolean up = isSameDecoSteel(level, pos.above());
        boolean down = isSameDecoSteel(level, pos.below());

        state = state.setValue(BlockDecoCT.NORTH, north)
                .setValue(BlockDecoCT.SOUTH, south)
                .setValue(BlockDecoCT.EAST, east)
                .setValue(BlockDecoCT.WEST, west)
                .setValue(BlockDecoCT.UP, up)
                .setValue(BlockDecoCT.DOWN, down);

        level.setBlock(pos, state, 3);
    }

    private boolean isSameDecoSteel(WorldGenLevel level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() == ModBlocks.DECO_STEEL.get();
    }

    private void updateDecoSteelConnections(WorldGenLevel level, BlockPos pos) {
        BlockState currentState = level.getBlockState(pos);
        if (!currentState.is(ModBlocks.DECO_STEEL.get())) return;

        boolean north = isSameDecoSteel(level, pos.north());
        boolean south = isSameDecoSteel(level, pos.south());
        boolean east = isSameDecoSteel(level, pos.east());
        boolean west = isSameDecoSteel(level, pos.west());
        boolean up = isSameDecoSteel(level, pos.above());
        boolean down = isSameDecoSteel(level, pos.below());

        BlockState newState = currentState
                .setValue(BlockDecoCT.NORTH, north)
                .setValue(BlockDecoCT.SOUTH, south)
                .setValue(BlockDecoCT.EAST, east)
                .setValue(BlockDecoCT.WEST, west)
                .setValue(BlockDecoCT.UP, up)
                .setValue(BlockDecoCT.DOWN, down);

        if (newState != currentState) {
            level.setBlock(pos, newState, 3);
            if (level.getBlockEntity(pos) instanceof DecoCTBlockEntity be) {
                be.requestModelDataUpdate();
            }
        }
    }
}
