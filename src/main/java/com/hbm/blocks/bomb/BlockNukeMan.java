package com.hbm.blocks.bomb;

import com.hbm.entity.logic.EntityNukeExplosionMK5;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.config.BombConfig;
import com.hbm.interfaces.IBomb;
import com.hbm.tileentity.bomb.TileEntityNukeMan;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockNukeMan extends BaseEntityBlock implements IBomb {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

    public BlockNukeMan(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityNukeMan(pos, state);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityNukeMan nuke && player instanceof ServerPlayer sp) {
            NetworkHooks.openScreen(sp, nuke, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         @NotNull BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityNukeMan nuke) {
                Containers.dropContents(level, pos, nuke);
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (level.isClientSide) return;
        if (!level.hasNeighborSignal(pos)) return;

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityNukeMan nuke && nuke.isReady()) {
            nuke.clearSlots();
            level.removeBlock(pos, false);
            igniteTestBomb(level, pos);
        }
    }

    public void igniteTestBomb(Level level, BlockPos pos) {
        if (level.isClientSide) return;

        level.playSound(null, pos, net.minecraft.sounds.SoundEvents.GENERIC_EXPLODE,
                net.minecraft.sounds.SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.1F + 0.9F);

        level.addFreshEntity(EntityNukeExplosionMK5.statFac(level,
                BombConfig.MAN_RADIUS, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5));
        EntityNukeTorex.statFacStandard(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                BombConfig.MAN_RADIUS);
    }

    @Override
    public BombReturnCode explode(Level level, BlockPos pos) {
        if (level.isClientSide) return BombReturnCode.UNDEFINED;

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityNukeMan nuke && nuke.isReady()) {
            nuke.clearSlots();
            level.removeBlock(pos, false);
            igniteTestBomb(level, pos);
            return BombReturnCode.DETONATED;
        }
        return BombReturnCode.ERROR_MISSING_COMPONENT;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 200.0F)
                .sound(SoundType.METAL)
                .noOcclusion();
    }
}