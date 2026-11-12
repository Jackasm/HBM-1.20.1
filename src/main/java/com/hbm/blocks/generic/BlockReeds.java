package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

/**
 * Камыш: один надводный блок и водонаполненные стебли до дна.
 *
 * <p>1.7.10 подводную часть не ставил блоками: {@code RenderReeds} считал глубину и рисовал
 * столбик вниз до дна, а вода оставалась на месте. В 1.20 то же самое делается честно —
 * {@link SimpleWaterloggedBlock} держит воду в клетке вместе с блоком: стебель физически стоит
 * на грунте, вода не вытесняется и не течёт (ADR-0021).</p>
 *
 * <p>Тип влияет только на условия стояния: 0 — низ (на воде или, водонаполненный, на грунте),
 * 1 — середина, 2 — верх. Камыш не растёт: в эталоне у блока тоже не было {@code updateTick},
 * высоту задаёт генерация (карточка #137).</p>
 */
public class BlockReeds extends Block implements SimpleWaterloggedBlock {

    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public static final int TYPE_BOTTOM = 0;
    public static final int TYPE_MID = 1;
    public static final int TYPE_TOP = 2;

    private final int type;

    public BlockReeds(Properties properties, int type) {
        super(properties);
        this.type = type;
        registerDefaultState(stateDefinition.any().setValue(WATERLOGGED, false));
    }

    public int getType() {
        return type;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(WATERLOGGED);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
        return defaultBlockState().setValue(WATERLOGGED, fluid.is(FluidTags.WATER));
    }

    @Override
    public @NotNull FluidState getFluidState(@NotNull BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public @NotNull BlockState updateShape(@NotNull BlockState state, @NotNull Direction direction,
                                           @NotNull BlockState neighborState, @NotNull LevelAccessor level,
                                           @NotNull BlockPos pos, @NotNull BlockPos neighborPos) {
        if (state.getValue(WATERLOGGED)) {
            level.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }
        return super.updateShape(state, direction, neighborState, level, pos, neighborPos);
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockState below = level.getBlockState(pos.below());

        // Столбик: любой камыш стоит на камыше. Это и подводный стебель, и надводные блоки.
        if (below.getBlock() instanceof BlockReeds) return true;
        if (type == TYPE_TOP) return false;

        // Водонаполненный стебель может стоять на грунте — это самый нижний блок под водой.
        if (state.getValue(WATERLOGGED) && below.isSolid()) return true;

        // Надводный низ стоит на воде.
        return type == TYPE_BOTTOM && below.getFluidState().is(FluidTags.WATER);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide && !canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Block.box(2.0, 0.0, 2.0, 14.0, 16.0, 14.0);
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.DESTROY;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.PLANT)
                .strength(0.0F)
                .sound(SoundType.GRASS)
                .noCollission()
                .instabreak()
                .pushReaction(PushReaction.DESTROY);
    }
}
