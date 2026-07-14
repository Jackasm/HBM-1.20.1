package com.hbm.blocks.machine;

import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntityGeiger;
import com.hbm.util.ContaminationUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GeigerCounter extends BaseEntityBlock {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);

    public GeigerCounter(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction facing = state.getValue(FACING);
        float f = 0.0625F;

        return switch (facing) {
            case WEST -> Shapes.box(0 * f, 0.0F, 0 * f, 14 * f, 9 * f, 14.5F * f);
            case NORTH -> Shapes.box(1.5F * f, 0.0F, 0 * f, 16 * f, 9 * f, 14 * f);
            case EAST -> Shapes.box(2 * f, 0.0F, 1.5F * f, 16 * f, 9 * f, 16 * f);
            case SOUTH -> Shapes.box(0 * f, 0.0F, 2 * f, 14.5F * f, 9 * f, 16 * f);
            default -> Shapes.block();
        };
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityGeiger(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.GEIGER.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntityGeiger geiger) {
                    geiger.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!player.isShiftKeyDown()) {
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TECH_BOOP.get(),
                    net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
            ContaminationUtil.printGeigerData(player);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    public static Properties createProperties() {
        return Properties.of()
                .strength(15.0F, 0.25F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }
}