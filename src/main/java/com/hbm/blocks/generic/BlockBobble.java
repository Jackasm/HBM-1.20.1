package com.hbm.blocks.generic;

import com.hbm.blocks.BlockBase;

import com.hbm.datagen.worldgen.nbt.INBTBlockTransformable;
import com.hbm.tileentity.deco.TileEntityBobble;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class BlockBobble extends BlockBase implements EntityBlock, INBTBlockTransformable {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Block.box(5.5, 0, 5.5, 10.5, 10, 10.5);

    public BlockBobble() {
        super(Properties.of().strength(1.0F).noOcclusion());
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
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
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityBobble(pos, state);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityBobble bobble) {
            int damage = stack.getDamageValue();
            bobble.type = TileEntityBobble.BobbleType.values()[Math.abs(damage) % TileEntityBobble.BobbleType.values().length];
            bobble.setChanged();
        }
    }

    @Override
    public @Nullable BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityBobble bobble) {
                NetworkHooks.openScreen((ServerPlayer) player, bobble, pos);
            }
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void playerDestroy(Level level, @NotNull Player player, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable BlockEntity te, @NotNull ItemStack stack) {
        if (!level.isClientSide && !player.isCreative()) {
            if (te instanceof TileEntityBobble bobble && bobble.type != null && bobble.type != TileEntityBobble.BobbleType.NONE) {
                ItemStack drop = new ItemStack(this, 1);
                drop.setDamageValue(bobble.type.ordinal());
                ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, drop);
                item.setDeltaMovement(0, 0, 0);
                level.addFreshEntity(item);
            }
        }
        super.playerDestroy(level, player, pos, state, te, stack);
    }

    @Override
    public BlockState transformState(BlockState state, int rotation) {
        if (state.hasProperty(FACING)) {
            Direction dir = state.getValue(FACING);
            Direction newDir = INBTBlockTransformable.transformDirection(dir, rotation);
            return state.setValue(FACING, newDir);
        }
        return state;
    }
}