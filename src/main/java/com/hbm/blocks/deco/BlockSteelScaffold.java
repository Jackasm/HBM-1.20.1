package com.hbm.blocks.deco;

import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSteelScaffold extends BaseEntityBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    public static final BooleanProperty VERTICAL = BooleanProperty.create("vertical");

    private final ScaffoldType type;

    public enum ScaffoldType {
        STEEL("steel", MapColor.METAL),
        RED("red", MapColor.COLOR_RED),
        WHITE("white", MapColor.SNOW),
        YELLOW("yellow", MapColor.COLOR_YELLOW);

        public final String name;
        public final MapColor color;

        ScaffoldType(String name, MapColor color) {
            this.name = name;
            this.color = color;
        }
    }


    private static final VoxelShape SHAPE_HORIZONTAL = Block.box(0, 2, 0, 16, 14, 16);
    private static final VoxelShape SHAPE_VERTICAL_NS = Block.box(0, 0, 2, 16, 16, 14);
    private static final VoxelShape SHAPE_VERTICAL_EW = Block.box(2, 0, 0, 14, 16, 16);

    public BlockSteelScaffold(ScaffoldType type) {
        super(Properties.of()
                .mapColor(type.color)
                .strength(10.0F, 15.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops()
        );
        this.type = type;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(VERTICAL, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, VERTICAL);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        Direction horizontal = context.getHorizontalDirection();

        // Если кликнули по верху или низу — ставим вертикально (на пол/потолок)
        if (face == Direction.UP || face == Direction.DOWN) {
            return this.defaultBlockState()
                    .setValue(FACING, horizontal)
                    .setValue(VERTICAL, true);
        } else {
            // Если кликнули по боку — ставим горизонтально (на стену)
            return this.defaultBlockState()
                    .setValue(FACING, face.getOpposite())
                    .setValue(VERTICAL, false);
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction facing = state.getValue(FACING);
        boolean vertical = state.getValue(VERTICAL);

        if (vertical) {
            if (facing == Direction.NORTH || facing == Direction.SOUTH) {
                return SHAPE_VERTICAL_NS;
            } else {
                return SHAPE_VERTICAL_EW;
            }
        } else {
            return SHAPE_HORIZONTAL;
        }
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySimpleOBJ(pos, state);
    }

    public ScaffoldType getType() {
        return type;
    }
}