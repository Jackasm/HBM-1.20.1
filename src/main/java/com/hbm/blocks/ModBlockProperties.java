package com.hbm.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;

public class ModBlockProperties {

    // Обычные руды (как камень)
    public static BlockBehaviour.Properties ORE_PROPERTIES() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(3.0F, 3.0F)
                .sound(SoundType.STONE);
    }

    public static BlockBehaviour.Properties EARTH_PROPERTIES() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.DIRT)
                .strength(0.5F)
                .sound(SoundType.GRASS);
    }

    // Deepslate руды
    public static BlockBehaviour.Properties DEEPSLATE_ORE_PROPERTIES() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.DEEPSLATE)
                .requiresCorrectToolForDrops()
                .strength(4.5F, 3.0F)
                .sound(SoundType.DEEPSLATE);
    }

    // Радиоактивные руды (уран, плутоний, торий)
    public static BlockBehaviour.Properties RADIOACTIVE_ORE_PROPERTIES() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> 5)
                .strength(4.0F, 3.0F) // Чуть прочнее обычных
                .sound(SoundType.STONE);
    }

    // Радиоактивные deepslate руды
    public static BlockBehaviour.Properties RADIOACTIVE_DEEPSLATE_ORE_PROPERTIES() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.DEEPSLATE)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> 5)
                .strength(5.5F, 3.0F) // Чуть прочнее обычных deepslate
                .sound(SoundType.DEEPSLATE);
    }

    // Твердые руды (кобальт)
    public static BlockBehaviour.Properties HARD_ORE_PROPERTIES() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .requiresCorrectToolForDrops()
                .strength(5.0F, 6.0F)
                .sound(SoundType.STONE);
    }

    // Твердые deepslate руды
    public static BlockBehaviour.Properties HARD_DEEPSLATE_ORE_PROPERTIES() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.DEEPSLATE)
                .requiresCorrectToolForDrops()
                .strength(6.0F, 6.0F)
                .sound(SoundType.DEEPSLATE);
    }

    public static Block.Properties GLOWING_ORE_PROPERTIES(int lightLevel) {
        return Block.Properties.copy(Blocks.STONE)
                .strength(3.0f, 3.0f)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> lightLevel);
    }

    public static Block.Properties GLOWING_DEEPSLATE_ORE_PROPERTIES(int lightLevel) {
        return Block.Properties.copy(Blocks.DEEPSLATE)
                .strength(4.5f, 3.0f)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> lightLevel);
    }

    // Для радиоактивных руд можно сделать особое свечение
    public static Block.Properties RADIOACTIVE_GLOWING_ORE_PROPERTIES(int lightLevel) {
        return Block.Properties.copy(Blocks.STONE)
                .strength(3.0f, 3.0f)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> lightLevel);
    }

    public static Block.Properties RADIOACTIVE_GLOWING_DEEPSLATE_ORE_PROPERTIES(int lightLevel) {
        return Block.Properties.copy(Blocks.DEEPSLATE)
                .strength(4.5f, 3.0f)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> lightLevel);
    }
    public static Block.Properties METAL_BLOCK_PROPERTIES() {
        return Block.Properties.copy(net.minecraft.world.level.block.Blocks.IRON_BLOCK);
    }
}
