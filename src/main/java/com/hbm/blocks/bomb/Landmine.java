package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.ServerConfig;
import com.hbm.explosion.ExplosionLarge;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.*;
import com.hbm.interfaces.IBomb;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.sedna.factory.XFactoryCatapult;
import com.hbm.main.MainRegistry;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.tileentity.bomb.TileEntityLandmine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class Landmine extends Block implements EntityBlock, IBomb, LiquidBlockContainer {

    public static boolean safeMode = false;

    public final double range;
    public final double height;

    private static final VoxelShape SHAPE_AP = Block.box(5, 0, 5, 11, 1, 11);
    private static final VoxelShape SHAPE_HE = Block.box(4, 0, 4, 12, 2, 12);
    private static final VoxelShape SHAPE_SHRAP = Block.box(5, 0, 5, 11, 1, 11);
    private static final VoxelShape SHAPE_FAT = Block.box(5, 0, 4, 11, 6, 12);
    private static final VoxelShape SHAPE_NAVAL = Block.box(5, 0, 5, 11, 1, 11);

    public Landmine(Properties properties, double range, double height) {
        super(properties);
        this.range = range;
        this.height = height;
        this.registerDefaultState(this.stateDefinition.any().setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.NORTH));
    }

    @Override
    public boolean canPlaceLiquid(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Fluid fluid) {
        return false;  // Нельзя налить жидкость на мину
    }

    @Override
    public boolean placeLiquid(@NotNull LevelAccessor level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull FluidState fluidState) {
        return false;  // Не заменять мину жидкостью
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(BlockStateProperties.HORIZONTAL_FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (this == ModBlocks.MINE_AP.get()) return SHAPE_AP;
        if (this == ModBlocks.MINE_SHRAP.get()) return SHAPE_SHRAP;
        if (this == ModBlocks.MINE_HE.get()) return SHAPE_HE;
        if (this == ModBlocks.MINE_FAT.get()) return SHAPE_FAT;
        if (this == ModBlocks.MINE_NAVAL.get()) return SHAPE_NAVAL;
        return SHAPE_AP;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShape(state, level, pos, context);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityLandmine(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, blockState, te) -> {
            if (te instanceof TileEntityLandmine landmine) {
                landmine.tick();
            }
        };
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
        return false;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (level.hasNeighborSignal(pos)) {
            explode(level, pos);
            return;
        }

        boolean canExist;

        if (this == ModBlocks.MINE_NAVAL.get()) {
            // Морская мина существует, если рядом есть вода (по горизонтали)
            canExist = false;
            for (Direction dir : Direction.Plane.HORIZONTAL) {
                BlockPos neighborPos = pos.relative(dir);
                if (level.getBlockState(neighborPos).liquid()) {
                    canExist = true;
                    break;
                }
            }
        } else {
            BlockState below = level.getBlockState(pos.below());
            canExist = below.isSolid() && !below.liquid();
        }

        if (!canExist) {
            if (!safeMode) {
                explode(level, pos);
            } else {
                level.removeBlock(pos, false);
            }
        }
    }

    @Override
    public void onRemove(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState newState, boolean isMoving) {
        if (!safeMode && state.getBlock() != newState.getBlock()) {
            explode(level, pos);
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide) {
            explode(level, pos);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.getItem() == ModItems.DEFUSER.get()) {
            safeMode = true;
            level.removeBlock(pos, false);

            ItemStack itemstack = new ItemStack(this, 1);
            ItemEntity entityitem = new ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    itemstack);

            if (!level.isClientSide) {
                level.addFreshEntity(entityitem);
            }

            safeMode = false;
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    private boolean isWaterAbove(Level level, BlockPos pos) {
        for (int xo = -1; xo <= 1; xo++) {
            for (int zo = -1; zo <= 1; zo++) {
                BlockPos checkPos = pos.offset(xo, 1, zo);
                Block block = level.getBlockState(checkPos).getBlock();
                if (block == net.minecraft.world.level.block.Blocks.WATER) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public BombReturnCode explode(Level level, BlockPos pos) {
        if (!level.isClientSide) {
            safeMode = true;
            level.removeBlock(pos, false);
            safeMode = false;

            if (this == ModBlocks.MINE_AP.get()) {
                ExplosionVNT vnt = new ExplosionVNT(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3F);
                vnt.setEntityProcessor(new EntityProcessorCrossSmooth(0.5, ServerConfig.MINE_AP_DAMAGE).setupPiercing(5F, 0.2F));
                vnt.setPlayerProcessor(new PlayerProcessorStandard());
                vnt.setSFX(new ExplosionEffectWeapon(5, 1F, 0.5F));
                vnt.explode();
            } else if (this == ModBlocks.MINE_SHRAP.get()) {
                ExplosionVNT vnt = new ExplosionVNT(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 3F);
                vnt.setEntityProcessor(new EntityProcessorCrossSmooth(0.5, ServerConfig.MINE_SHRAP_DAMAGE));
                vnt.setPlayerProcessor(new PlayerProcessorStandard());
                vnt.setSFX(new ExplosionEffectWeapon(5, 1F, 0.5F));
                vnt.explode();

                ExplosionLarge.spawnShrapnelShower(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 1D, 0, 45, 0.2D);
                ExplosionLarge.spawnShrapnels(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5);
            } else if (this == ModBlocks.MINE_HE.get()) {
                ExplosionVNT vnt = new ExplosionVNT(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 4F);
                vnt.setBlockAllocator(new BlockAllocatorStandard());
                vnt.setBlockProcessor(new BlockProcessorStandard());
                vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, ServerConfig.MINE_HE_DAMAGE).setupPiercing(15F, 0.2F));
                vnt.setPlayerProcessor(new PlayerProcessorStandard());
                vnt.setSFX(new ExplosionEffectWeapon(15, 3.5F, 1.25F));
                vnt.explode();
            }else if (this == ModBlocks.MINE_NAVAL.get()) {
                ExplosionVNT vnt = new ExplosionVNT(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5F);
                vnt.setBlockAllocator(new BlockAllocatorWater(32));
                vnt.setBlockProcessor(new BlockProcessorStandard());
                vnt.setEntityProcessor(new EntityProcessorCrossSmooth(0.5, ServerConfig.MINE_NAVAL_DAMAGE).setupPiercing(5F, 0.2F));
                vnt.setPlayerProcessor(new PlayerProcessorStandard());
                vnt.setSFX(new ExplosionEffectWeapon(10, 1F, 0.5F));
                vnt.explode();

                ExplosionLarge.spawnParticlesRadial(level, pos.getX() + 0.5, pos.getY() + 2, pos.getZ() + 0.5, 30);
                ExplosionLarge.spawnRubble(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 5);

                if (isWaterAbove(level, pos)) {
                    ExplosionLarge.spawnFoam(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 60);
                }
            } else if (this == ModBlocks.MINE_FAT.get()) {
                ExplosionVNT vnt = new ExplosionVNT(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 10);
                vnt.setBlockAllocator(new BlockAllocatorStandard(64));
                vnt.setBlockProcessor(new BlockProcessorStandard());
                vnt.setEntityProcessor(new EntityProcessorCrossSmooth(2, ServerConfig.MINE_NUKE_DAMAGE).withRangeMod(1.5F));
                vnt.setPlayerProcessor(new PlayerProcessorStandard());
                vnt.explode();

                XFactoryCatapult.incrementRad(level, pos.getX(), pos.getY(), pos.getZ(), 1.5F);

                CompoundTag data = new CompoundTag();
                data.putString("type", "muke");
                data.putBoolean("balefire", MainRegistry.polaroidID == 11 || level.random.nextInt(100) == 0);
                PacketDispatcher.sendToAllAround(
                        new AuxParticlePacketNT(data, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5),
                        level,
                        pos,
                        250
                );

                level.playSound(null, pos, SoundEvents.GENERIC_EXPLODE, SoundSource.BLOCKS, 25.0F, 0.9F);
            }
        }
        return BombReturnCode.DETONATED;
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(1.0F)
                .noLootTable()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }
}