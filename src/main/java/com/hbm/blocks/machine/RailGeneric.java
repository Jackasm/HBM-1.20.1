package com.hbm.blocks.machine;

import com.hbm.blocks.ITooltipProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.RailBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import org.jetbrains.annotations.NotNull;

public class RailGeneric extends RailBlock implements ITooltipProvider {

    public RailGeneric(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public String[] getTooltipKeys(ItemStack stack) {
        return new String[]{ this.getDescriptionId() + ".desc" };
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        RailShape shape = state.getValue(SHAPE);
        RailShape rotated = switch (rotation) {
            case NONE -> shape;
            case CLOCKWISE_90 -> rotateOnce(shape);
            case CLOCKWISE_180 -> rotateOnce(rotateOnce(shape));
            case COUNTERCLOCKWISE_90 -> rotateOnce(rotateOnce(rotateOnce(shape)));
        };
        return state.setValue(SHAPE, rotated);
    }

    private static RailShape rotateOnce(RailShape shape) {
        return switch (shape) {
            case NORTH_SOUTH -> RailShape.EAST_WEST;
            case EAST_WEST   -> RailShape.NORTH_SOUTH;

            case ASCENDING_EAST  -> RailShape.ASCENDING_SOUTH;
            case ASCENDING_SOUTH -> RailShape.ASCENDING_WEST;
            case ASCENDING_WEST  -> RailShape.ASCENDING_NORTH;
            case ASCENDING_NORTH -> RailShape.ASCENDING_EAST;

            case SOUTH_EAST -> RailShape.SOUTH_WEST;
            case SOUTH_WEST -> RailShape.NORTH_WEST;
            case NORTH_WEST -> RailShape.NORTH_EAST;
            case NORTH_EAST -> RailShape.SOUTH_EAST;
        };
    }
}