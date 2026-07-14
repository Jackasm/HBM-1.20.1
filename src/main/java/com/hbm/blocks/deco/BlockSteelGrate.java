package com.hbm.blocks.deco;

import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockSteelGrate extends BaseEntityBlock {

    public static final IntegerProperty LAYER = IntegerProperty.create("layer", 0, 7);

    private static final VoxelShape[] SHAPES = new VoxelShape[8];

    static {
        for (int layer = 0; layer <= 7; layer++) {
            float y = layer * 0.125F; // 0, 0.125, 0.25, ..., 0.875
            SHAPES[layer] = box(0, y * 16, 0, 16, (y + 0.125F) * 16, 16);
        }
    }

    public BlockSteelGrate() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(10.0F, 15.0F)
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK)
                .requiresCorrectToolForDrops()
        );
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LAYER, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<net.minecraft.world.level.block.Block, BlockState> builder) {
        builder.add(LAYER);
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.BLOCK;
    }

    public static float getRenderYOffset(int layer) {
        return layer * 0.125F;
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Vec3 clickLocation = context.getClickLocation();
        BlockPos pos = context.getClickedPos();

        int layer;

        if (clickedFace == Direction.UP) {
            layer = 0;
        } else if (clickedFace == Direction.DOWN) {
            layer = 7;
        } else {
            double clickYRelative = clickLocation.y - pos.getY();
            layer = (int) Math.floor(clickYRelative * 8D);
            layer = Math.max(0, Math.min(7, layer));
        }

        return this.defaultBlockState().setValue(LAYER, layer);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        int layer = state.getValue(LAYER);
        return SHAPES[layer];
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySimpleOBJ(pos, state);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level,
                             @NotNull BlockPos pos, @NotNull Entity entity) {
    }
}