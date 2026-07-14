package com.hbm.blocks.generic;

import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.Nullable;

public class BlockPinkLog extends RotatedPillarBlock {

    public BlockPinkLog(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.COLOR_PINK)
                .strength(0.5F)
                .sound(SoundType.WOOD)
                .ignitedByLava()
                .pushReaction(PushReaction.NORMAL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction.Axis axis = context.getClickedFace().getAxis();
        return this.defaultBlockState().setValue(AXIS, axis);
    }

}