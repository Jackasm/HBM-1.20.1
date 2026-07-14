package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import com.hbm.tileentity.deco.TileEntityDecoBlockAltF;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DecoBlockAlt extends Block implements EntityBlock {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    protected static final VoxelShape SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0); // 8/16 = 0.5

    public DecoBlockAlt(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (this == ModBlocks.STATUE_ELB_F.get()) {
            return new TileEntityDecoBlockAltF(pos, state);
        }
        return null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED; // -1 в старых версиях означало ENTITYBLOCK_ANIMATED
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (placer != null) {
            int dir = Math.floorMod((int) Math.floor(placer.getYRot() * 4.0F / 360.0F + 0.5D), 4);
            Direction facing = switch (dir) {
                case 0 -> Direction.SOUTH;
                case 1 -> Direction.WEST;
                case 2 -> Direction.NORTH;
                case 3 -> Direction.EAST;
                default -> Direction.NORTH;
            };
            level.setBlock(pos, state.setValue(FACING, facing), 3);
        }
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, blockState, te) -> {
            if (te instanceof TileEntityDecoBlockAltF f) {
                TileEntityDecoBlockAltF.tick(lvl, pos, blockState, f);
            }
        };
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!player.isShiftKeyDown()) {
            ItemStack heldItem = player.getItemInHand(hand);
            if (!heldItem.isEmpty()) {

                boolean cracked = heldItem.getItem() == ModItems.KEY_RED_CRACKED.get();

                if (heldItem.getItem() == ModItems.KEY_RED.get() || cracked) {
                    if (cracked) {
                        heldItem.shrink(1);
                    }
                    level.destroyBlock(pos, true);
                    return InteractionResult.CONSUME;
                }

            }
        }
        return InteractionResult.PASS;
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(-1.0F, Float.POSITIVE_INFINITY)
                .lightLevel(state -> 15)
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK);
    }
}