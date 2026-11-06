package com.hbm.blocks.bomb;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.entity.item.EntityTNTPrimedBase;
import com.hbm.interfaces.IBomb;
import com.hbm.tileentity.bomb.TileEntityCharge;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
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

public abstract class BlockChargeBase extends BaseEntityBlock implements IBomb, IToolable, ITooltipProvider {

    public static boolean safe = false;

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public BlockChargeBase(Properties properties) {
        super(properties);
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
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return switch (state.getValue(FACING)) {
            case DOWN    -> Shapes.box(0.0, 10/16.0, 0.0, 1.0, 1.0, 1.0);
            case UP  -> Shapes.box(0.0, 0.0, 0.0, 1.0, 6/16.0, 1.0);
            case NORTH -> Shapes.box(0.0, 0.0, 10/16.0, 1.0, 1.0, 1.0);
            case SOUTH -> Shapes.box(0.0, 0.0, 0.0, 1.0, 1.0, 6/16.0);
            case WEST  -> Shapes.box(10/16.0, 0.0, 0.0, 1.0, 1.0, 1.0);
            case EAST  -> Shapes.box(0.0, 0.0, 0.0, 6/16.0, 1.0, 1.0);
        };
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityCharge(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == com.hbm.tileentity.ModTileEntity.CHARGE.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityCharge charge) charge.tick();
            };
        }
        return null;
    }

    @Override
    public BlockState getStateForPlacement(net.minecraft.world.item.context.BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getClickedFace());
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        Direction dir = state.getValue(FACING);
        BlockPos supportPos = pos.relative(dir.getOpposite());
        return level.getBlockState(supportPos).isFaceSturdy(level, supportPos, dir);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        Direction dir = state.getValue(FACING);
        BlockPos supportPos = pos.relative(dir.getOpposite());
        if (!level.getBlockState(supportPos).isFaceSturdy(level, supportPos, dir)) {
            level.removeBlock(pos, false);
        }
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                         BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (!safe && !level.isClientSide) {
                explode(level, pos);
            }
            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    @Override
    public void wasExploded(@NotNull Level level, @NotNull BlockPos pos, @NotNull Explosion explosion) {
        if (!level.isClientSide) {
            LivingEntity owner = explosion.getIndirectSourceEntity();
            EntityTNTPrimedBase tnt = new EntityTNTPrimedBase(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, owner, this);
            tnt.fuse = 0;
            tnt.detonateOnCollision = false;
            level.addFreshEntity(tnt);
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityCharge charge)) return InteractionResult.PASS;

        if (!charge.started) {
            if (player.isShiftKeyDown()) {
                if (charge.timer > 0) {
                    charge.started = true;
                    level.playSound(null, pos, com.hbm.sound.ModSounds.FSTBMB_START.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
                }
            } else {
                int t = charge.timer;
                if (t == 0) charge.timer = 100;
                else if (t == 100) charge.timer = 200;
                else if (t == 200) charge.timer = 300;
                else if (t == 300) charge.timer = 600;
                else if (t == 600) charge.timer = 1200;
                else if (t == 1200) charge.timer = 3600;
                else if (t == 3600) charge.timer = 6000;
                else charge.timer = 0;

                level.playSound(null, pos, com.hbm.sound.ModSounds.TECH_BOOP.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            charge.setChanged();
        }

        return InteractionResult.CONSUME;
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side,
                           float fX, float fY, float fZ, ToolType tool) {
        if (tool != ToolType.DEFUSER) return false;

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityCharge charge)) return false;

        if (charge.started) {
            charge.started = false;
            level.playSound(null, pos, com.hbm.sound.ModSounds.FSTBMB_START.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            charge.setChanged();
        } else {
            safe = true;
            level.removeBlock(pos, false);
            safe = false;
        }
        return true;
    }

    @Override
    public String[] getTooltipKeys(ItemStack stack) {
        return new String[]{
                this.getDescriptionId() + ".desc1",
                this.getDescriptionId() + ".desc2",
                this.getDescriptionId() + ".desc3"
        };
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.FIRE)
                .strength(0.1F, 1.0F)
                .sound(SoundType.GRASS)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }
}