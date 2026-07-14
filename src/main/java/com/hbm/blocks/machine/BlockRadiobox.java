package com.hbm.blocks.machine;

import com.hbm.items.ModItems;

import com.hbm.items.machine.ItemBattery;
import com.hbm.tileentity.machine.TileEntityRadiobox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockRadiobox extends BaseEntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED; // 0-1 для включения/выключения

    public BlockRadiobox(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(POWERED, false));
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(POWERED, false);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (placer != null) {
            Direction facing = placer.getDirection().getOpposite();
            level.setBlock(pos, state.setValue(FACING, facing), 2);
        }
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityRadiobox(pos, state);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        TileEntityRadiobox box = (TileEntityRadiobox) level.getBlockEntity(pos);

        if (box == null) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);

        if (!heldItem.isEmpty() && heldItem.getItem() == ModItems.BATTERY_SPARK.get() && !box.infinite) {
            heldItem.shrink(1);
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    net.minecraft.sounds.SoundEvents.IRON_TRAPDOOR_OPEN, net.minecraft.sounds.SoundSource.BLOCKS, 1.5F, 1.0F);
            box.infinite = true;
            box.setChanged();
            return InteractionResult.CONSUME;
        }

        if (!player.isShiftKeyDown()) {
            NetworkHooks.openScreen((ServerPlayer) player, box, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityRadiobox box) {
                if (box.infinite) {
                    Containers.dropItemStack(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            ItemBattery.getEmptyBattery(ModItems.BATTERY_SPARK.get()));
                }
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        float f = 0.0625F;
        Direction facing = state.getValue(FACING);

        VoxelShape base = Shapes.box(0.0F, 0.0F, 2*f, 1.0F, 1.0F, 14*f);

        VoxelShape antenna = switch (facing) {
            case EAST -> Shapes.box(11*f, 1*f, 4*f, 16*f, 15*f, 12*f);
            case WEST -> Shapes.box(0*f, 1*f, 4*f, 5*f, 15*f, 12*f);
            case SOUTH -> Shapes.box(4*f, 1*f, 11*f, 12*f, 15*f, 16*f);
            default -> Shapes.box(4*f, 1*f, 0*f, 12*f, 15*f, 5*f);
        };

        return Shapes.or(base, antenna);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, level, pos, context);
    }
}