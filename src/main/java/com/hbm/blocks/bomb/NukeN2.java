package com.hbm.blocks.bomb;

import com.hbm.config.BombConfig;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.interfaces.IBomb;
import com.hbm.tileentity.bomb.TileEntityNukeN2;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
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
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class NukeN2 extends BaseEntityBlock implements IBomb {

    public static final DirectionProperty ROTATION = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Shapes.box(0.0625, 0, 0.0625, 0.9375, 0.5, 0.9375);
    private static final boolean keepInventory = false;
    private final Random random = new Random();

    public NukeN2(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(ROTATION, Direction.NORTH));
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 200.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(ROTATION);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(ROTATION, context.getHorizontalDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityNukeN2(pos, state);
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!keepInventory) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityNukeN2 tile) {
                for (int i = 0; i < tile.getContainerSize(); i++) {
                    ItemStack stack = tile.getItem(i);
                    if (!stack.isEmpty()) {
                        double x = pos.getX() + random.nextFloat() * 0.8F + 0.1F;
                        double y = pos.getY() + random.nextFloat() * 0.8F + 0.1F;
                        double z = pos.getZ() + random.nextFloat() * 0.8F + 0.1F;

                        while (!stack.isEmpty()) {
                            int count = random.nextInt(21) + 10;
                            if (count > stack.getCount()) {
                                count = stack.getCount();
                            }

                            ItemStack drop = stack.copy();
                            drop.setCount(count);
                            stack.shrink(count);

                            ItemEntity itemEntity = new ItemEntity(level, x, y, z, drop);
                            itemEntity.setDeltaMovement(
                                    random.nextGaussian() * 0.05F,
                                    random.nextGaussian() * 0.05F + 0.2F,
                                    random.nextGaussian() * 0.05F
                            );
                            level.addFreshEntity(itemEntity);
                        }
                    }
                }
                level.updateNeighbourForOutputSignal(pos, state.getBlock());
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (!player.isShiftKeyDown()) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityNukeN2 tile) {
                NetworkHooks.openScreen((net.minecraft.server.level.ServerPlayer) player, tile, pos);
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityNukeN2 tile && level.hasNeighborSignal(pos) && !level.isClientSide) {
            if (tile.isReady()) {
                level.removeBlock(pos, false);
                tile.clearSlots();
                igniteTestBomb(level, pos, BombConfig.n2Radius.get());
            }
        }
    }

    public void igniteTestBomb(Level level, BlockPos pos, int radius) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);

            EntityNukeExplosionMK5.statFacNoRad(serverLevel, radius, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            EntityNukeTorex.statFacStandard(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, radius);
        }
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
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (placer != null) {
            Direction facing = placer.getDirection().getOpposite();
            level.setBlock(pos, state.setValue(ROTATION, facing), 2);
        }
    }

    @Override
    public IBomb.BombReturnCode explode(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityNukeN2 tile && tile.isReady()) {
                level.removeBlock(pos, false);
                tile.clearSlots();
                igniteTestBomb(level, pos, BombConfig.n2Radius.get());
                return BombReturnCode.DETONATED;
            }
            return BombReturnCode.ERROR_MISSING_COMPONENT;
        }
        return BombReturnCode.UNDEFINED;
    }

}