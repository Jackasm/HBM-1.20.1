package com.hbm.blocks.generic;

import com.hbm.blocks.IBlockMulti;
import com.hbm.datagen.worldgen.nbt.INBTBlockTransformable;
import com.hbm.items.block.ItemPlushie;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.block.TileEntityPlushie;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
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
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockPlushie extends BaseEntityBlock implements IBlockMulti, INBTBlockTransformable {

    public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE = Shapes.box(0.25D, 0.0D, 0.25D, 0.75D, 0.75D, 0.75D);

    public BlockPlushie(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    public enum PlushieType {
        NONE("NONE", null),
        YOMI("Yomi", "Hi! Can I be your rabbit friend?"),
        NUMBERNINE("Number Nine", "None of y'all deserve coal."),
        HUNDUN("Hundun", "混沌");

        public final String label;
        public final String inscription;

        PlushieType(String label, String inscription) {
            this.label = label;
            this.inscription = inscription;
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
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
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.DESTROY;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityPlushie plushie) {
            return ItemPlushie.withType(plushie.type);
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        Direction facing = placer != null ? placer.getDirection().getOpposite() : Direction.NORTH;
        level.setBlock(pos, state.setValue(FACING, facing), 3);

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityPlushie plushie) {
            CompoundTag tag = stack.getTag();
            int customModelData = 0;
            if (tag != null && tag.contains("CustomModelData")) {
                customModelData = tag.getInt("CustomModelData");
            }
            plushie.type = PlushieType.values()[Math.abs(customModelData) % PlushieType.values().length];
            plushie.setChanged();
        }
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, Player player) {
        if (!player.isCreative() && !level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityPlushie plushie) {
                ItemStack stack = ItemPlushie.withType(plushie.type);
                ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
                item.setDeltaMovement(0, 0, 0);
                level.addFreshEntity(item);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityPlushie plushie) {
            if (level.isClientSide) {
                plushie.squishTimer = 11;
                return InteractionResult.SUCCESS;
            } else {
                if (plushie.type == PlushieType.HUNDUN) {
                    level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            SoundEvents.WOLF_HOWL, SoundSource.BLOCKS, 100F, 1F);
                } else {
                    level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                            ModSounds.BLOCK_SQUEAKYTOY.get(), SoundSource.BLOCKS, 0.25F, 1F);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPlushie(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.PLUSHIE.get()) {
            return (lvl, pos, st, blockEntity) -> {
                if (blockEntity instanceof TileEntityPlushie plushie) {
                    plushie.tick();
                }
            };
        }
        return null;
    }

    @Override
    public int getSubCount() {
        return PlushieType.values().length;
    }

    @Override
    public Component getOverrideDisplayName(ItemStack stack) {
        PlushieType type = ItemPlushie.getType(stack);
        String name = type == PlushieType.NONE ? "" : type.label;
        return Component.translatable(this.getDescriptionId() + ".name", name);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        PlushieType type = ItemPlushie.getType(stack);
        if (type.inscription != null) {
            tooltip.add(Component.literal(type.inscription));
        }
    }

    @Override
    public BlockState transformState(BlockState state, int rotation) {
        Direction facing = state.getValue(FACING);
        for (int i = 0; i < rotation; i++) {
            facing = facing.getClockWise();
        }
        return state.setValue(FACING, facing);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.WOOL)
                .strength(0.5F, 0.5F)
                .sound(SoundType.WOOL)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }
}