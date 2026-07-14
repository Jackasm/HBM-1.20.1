package com.hbm.datagen.worldgen.nbt;

import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.StairsShape;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.AttachFace;


/**
 * Интерфейс для блоков, которые требуют трансформации состояния при загрузке из NBT-структур.
 * В 1.20.1 вместо метаданных используются BlockState и Property.
 */
public interface INBTBlockTransformable {

    /**
     * Трансформирует состояние блока в соответствии с заданным поворотом.
     *
     * @param state Исходное состояние блока
     * @param rotation Направление поворота (0 = юг, 1 = запад, 2 = север, 3 = восток)
     * @return Трансформированное состояние
     */
    BlockState transformState(BlockState state, int rotation);

    /**
     * При необходимости заменяет блок на другой (например, для отключения света).
     */
    default Block transformBlock(Block block) {
        return block;
    }

    /**
     * Утилитные методы для трансформации различных типов свойств
     */

    // Трансформация Direction
    static Direction transformDirection(Direction dir, int rotation) {
        if (rotation == 0) return dir;
        Direction.Axis axis = dir.getAxis();
        if (axis.isVertical()) return dir; // вертикальные направления не меняются при горизонтальном повороте

        Direction newDir = dir;
        for (int i = 0; i < rotation; i++) {
            newDir = newDir.getClockWise();
        }
        return newDir;
    }


    static BlockState transformFacing(BlockState state, DirectionProperty property, int rotation) {
        if (!state.hasProperty(property)) return state;
        Direction dir = state.getValue(property);
        Direction newDir = transformDirection(dir, rotation);
        return state.setValue(property, newDir);
    }

    // Трансформация для свойств, основанных на горизонтальном направлении
    static int transformRotationIndex(int index, int rotation) {
        return (index + rotation) % 4;
    }

    // Трансформация для IntegerProperty, представляющего вращение (0-3)
    static BlockState transformRotationInteger(BlockState state, IntegerProperty property, int rotation) {
        if (!state.hasProperty(property)) return state;
        int val = state.getValue(property);
        int newVal = (val + rotation) % 4;
        return state.setValue(property, newVal);
    }

    /**
     * Преобразует координатный режим из NBT в индекс поворота
     * @param coordBaseMode 0=юг, 1=запад, 2=север, 3=восток
     */
    static int getRotationFromMode(int coordBaseMode) {
        return coordBaseMode;
    }

    /**
     * Вспомогательные методы для совместимости с оригинальной логикой,
     * но работающие с BlockState вместо метаданных
     */

    // Для декоративных блоков (направления 2-5)
    static BlockState transformMetaDeco(BlockState state, int rotation) {
        if (rotation == 0 || !state.hasProperty(BlockStateProperties.FACING)) return state;

        Direction dir = state.getValue(BlockStateProperties.FACING);
        if (dir.getAxis().isVertical()) return state;

        return transformFacing(state, BlockStateProperties.FACING, rotation);
    }

    // Для блоков с моделью, где направление хранится в младших битах
    static BlockState transformMetaDecoModel(BlockState state, int rotation) {
        // Предполагается, что младшие 2 бита - поворот, старшие - тип
        // Для таких блоков нужно реализовать специфичную логику
        return state;
    }

    // Для лестниц
    static BlockState transformStairs(BlockState state, int rotation) {
        state = transformFacing(state, BlockStateProperties.HORIZONTAL_FACING, rotation);

        if (state.hasProperty(BlockStateProperties.STAIRS_SHAPE)) {
            StairsShape shape = state.getValue(BlockStateProperties.STAIRS_SHAPE);
            // Трансформация формы лестницы (сложно, нужно учитывать соседей)
        }
        return state;
    }

    // Для люков
    static BlockState transformTrapdoor(BlockState state, int rotation) {
        state = transformFacing(state, BlockStateProperties.HORIZONTAL_FACING, rotation);

        if (state.hasProperty(BlockStateProperties.HALF)) {
            Half half = state.getValue(BlockStateProperties.HALF);
            // Половина (верх/низ) не меняется при повороте
        }
        return state;
    }

    // Для столбов (бревна, кварцевые колонны)
    static BlockState transformPillar(BlockState state, int rotation) {
        if (!state.hasProperty(BlockStateProperties.AXIS)) return state;

        Direction.Axis axis = state.getValue(BlockStateProperties.AXIS);
        if (rotation == 1 || rotation == 3) { // запад или восток
            if (axis == Direction.Axis.X) {
                axis = Direction.Axis.Z;
            } else if (axis == Direction.Axis.Z) {
                axis = Direction.Axis.X;
            }
        }
        return state.setValue(BlockStateProperties.AXIS, axis);
    }

    // Для направленных блоков (печи, раздатчики и т.д.)
    static BlockState transformDirectional(BlockState state, int rotation) {
        return transformFacing(state, BlockStateProperties.FACING, rotation);
    }

    // Для факелов
    static BlockState transformTorch(BlockState state, int rotation) {
        if (!state.hasProperty(BlockStateProperties.FACING)) return state;

        Direction dir = state.getValue(BlockStateProperties.FACING);
        if (dir == Direction.UP || dir == Direction.DOWN) return state; // факелы на полу/потолке не вращаются

        return transformFacing(state, BlockStateProperties.FACING, rotation);
    }

    // Для дверей
    static BlockState transformDoor(BlockState state, int rotation) {
        state = transformFacing(state, BlockStateProperties.HORIZONTAL_FACING, rotation);

        if (state.hasProperty(BlockStateProperties.DOOR_HINGE)) {
            DoorHingeSide hinge = state.getValue(BlockStateProperties.DOOR_HINGE);
            // Петля не меняется при повороте?
        }
        return state;
    }

    // Для рычагов
    static BlockState transformLever(BlockState state, int rotation) {
        if (!state.hasProperty(BlockStateProperties.ATTACH_FACE)) return state;
        if (!state.hasProperty(BlockStateProperties.HORIZONTAL_FACING)) return state;

        AttachFace face = state.getValue(BlockStateProperties.ATTACH_FACE);
        Direction facing = state.getValue(BlockStateProperties.HORIZONTAL_FACING);

        if (face == AttachFace.CEILING || face == AttachFace.FLOOR) {
            // Рычаги на полу/потолке вращаются иначе
            return transformRotationInteger(state, BlockStateProperties.ROTATION_16, rotation * 4);
        } else {
            // Рычаги на стенах
            return transformFacing(state, BlockStateProperties.HORIZONTAL_FACING, rotation);
        }
    }

    // Для лиан
    static BlockState transformVine(BlockState state, int rotation) {
        if (!state.hasProperty(BlockStateProperties.NORTH)) return state;

        boolean north = state.getValue(BlockStateProperties.NORTH);
        boolean east = state.getValue(BlockStateProperties.EAST);
        boolean south = state.getValue(BlockStateProperties.SOUTH);
        boolean west = state.getValue(BlockStateProperties.WEST);

        // Циклический сдвиг
        for (int i = 0; i < rotation; i++) {
            boolean oldNorth = north;
            boolean oldEast = east;
            boolean oldSouth = south;
            boolean oldWest = west;

            north = oldWest;
            east = oldNorth;
            south = oldEast;
            west = oldSouth;
        }

        return state
                .setValue(BlockStateProperties.NORTH, north)
                .setValue(BlockStateProperties.EAST, east)
                .setValue(BlockStateProperties.SOUTH, south)
                .setValue(BlockStateProperties.WEST, west);
    }

    /**
     * Конвертирует старые метаданные в BlockState для совместимости при загрузке старых структур
     */
    static BlockState convertMetaToState(Block block, int meta, int coordBaseMode) {
        // Этот метод нужно реализовать для конкретных блоков,
        // если вы загружаете структуры из 1.7.10
        return block.defaultBlockState();
    }
}
