package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.GlassBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class BlockNTMGlass extends GlassBlock {

    private final boolean dropSelf;
    private final int lightLevel;

    public BlockNTMGlass(Properties properties, boolean dropSelf, int lightLevel) {
        super(properties);
        this.dropSelf = dropSelf;
        this.lightLevel = lightLevel;
    }

    public BlockNTMGlass(Properties properties, boolean dropSelf) {
        this(properties, dropSelf, 0);
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return lightLevel;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter reader, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public boolean skipRendering(@NotNull BlockState state, BlockState adjacentState, @NotNull Direction side) {
        return adjacentState.getBlock() == this || super.skipRendering(state, adjacentState, side);
    }

    // Статические методы для создания свойств
    public static Properties createProperties(MapColor color, float hardness, float resistance, SoundType sound) {
        return Properties.of()
                .mapColor(color)
                .strength(hardness, resistance)
                .sound(sound)
                .noOcclusion()
                .isValidSpawn((state, level, pos, entity) -> false)
                .isRedstoneConductor((state, level, pos) -> false)
                .isSuffocating((state, level, pos) -> false)
                .isViewBlocking((state, level, pos) -> false)
                .pushReaction(PushReaction.NORMAL);
    }

    public static Properties createProperties(MapColor color, float hardness, SoundType sound) {
        return createProperties(color, hardness, 1.0F, sound);
    }

    public static Properties createProperties(MapColor color) {
        return createProperties(color, 0.3F, SoundType.GLASS);
    }

    public static Properties createProperties(MapColor color, float hardness) {
        return createProperties(color, hardness, SoundType.GLASS);
    }
}