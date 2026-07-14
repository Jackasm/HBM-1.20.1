package com.hbm.blocks.machine;

import com.hbm.api.block.IToolable;
import com.hbm.datagen.worldgen.nbt.INBTBlockTransformable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntityFloodlight;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockFloodlight extends BaseEntityBlock implements IToolable, INBTBlockTransformable {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final IntegerProperty ROTATION = IntegerProperty.create("rotation", 0, 359);

    public BlockFloodlight() {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(ROTATION, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, ROTATION);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter world, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityFloodlight(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.FLOODLIGHT.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityFloodlight floodlight) {
                    floodlight.tick();
                }
            };
        }
        return null;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getNearestLookingDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityFloodlight) {
            setAngle(level, pos, player);
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (placer != null) {
            setAngle(level, pos, placer);
        }
    }

    public void setAngle(Level level, BlockPos pos, LivingEntity player) {
        int i = Mth.floor(player.getYRot() * 4.0F / 360.0F + 0.5D) & 3;
        float rotation = player.getXRot();

        BlockEntity tile = level.getBlockEntity(pos);
        if (tile instanceof TileEntityFloodlight floodlight) {
            BlockState state = level.getBlockState(pos);
            Direction facing = state.getValue(FACING);

            // Корректировка угла в зависимости от направления
            if (facing == Direction.UP || facing == Direction.DOWN) {
                if (facing == Direction.DOWN) {
                    if (i == 0 || i == 1) rotation = 180F - rotation;
                }
                if (facing == Direction.UP) {
                    if (i == 0 || i == 3) rotation = 180F - rotation;
                }
            }

            floodlight.rotation = -Math.round(rotation / 5F) * 5F;
            if (floodlight.isOn) {
                floodlight.destroyLights();
            }
            floodlight.setChanged();
        }
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float hitX, float hitY, float hitZ, IToolable.ToolType tool) {
        if (tool != IToolable.ToolType.SCREWDRIVER) return false;
        setAngle(level, pos, player);
        return true;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(0.5F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    // ==================== INBTBlockTransformable ====================

    @Override
    public BlockState transformState(BlockState state, int rotation) {
        if (rotation == 0 || !state.hasProperty(FACING)) return state;

        Direction facing = state.getValue(FACING);
        Direction newFacing = rotateFacing(facing, rotation);
        return state.setValue(FACING, newFacing);
    }

    private Direction rotateFacing(Direction facing, int rotation) {
        // rotation: 0=юг, 1=запад, 2=север, 3=восток
        if (facing.getAxis() == Direction.Axis.Y) {
            return facing; // Вертикальные направления не меняются
        }

        Direction newFacing = facing;
        for (int i = 0; i < rotation; i++) {
            newFacing = newFacing.getClockWise();
        }
        return newFacing;
    }
}