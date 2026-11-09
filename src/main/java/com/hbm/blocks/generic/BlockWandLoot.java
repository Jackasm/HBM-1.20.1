package com.hbm.blocks.generic;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ModBlocks;

import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.block.TileEntityWandLoot;
import com.hbm.util.MathHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockWandLoot extends Block implements EntityBlock, ILookOverlay, IToolable {

    public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.HORIZONTAL);
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D);

    public BlockWandLoot(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (placer != null) {
            Direction facing = placer.getDirection().getOpposite();
            level.setBlock(pos, state.setValue(FACING, facing), 3);
        }
        // Устанавливаем поворот в TileEntity
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityWandLoot loot) {
            loot.setPlacedRotation(placer != null ? placer.getYRot() : 0);
        }
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityWandLoot loot)) return InteractionResult.PASS;

        if (!player.isShiftKeyDown()) {
            ItemStack heldItem = player.getItemInHand(hand);
            Block block = getLootableBlock(level, heldItem);
            if (block != null) {
                loot.setReplaceBlock(block);
                loot.setReplaceMeta(heldItem.getDamageValue());

                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    private Block getLootableBlock(Level level, ItemStack stack) {
        if (stack == null || stack.isEmpty()) return null;

        if (stack.getItem() instanceof net.minecraft.world.item.BlockItem blockItem) {
            Block block = blockItem.getBlock();
            if (block == ModBlocks.DECO_LOOT.get()) return block;
            if (block instanceof net.minecraft.world.level.block.EntityBlock) {
                BlockEntity te = ((net.minecraft.world.level.block.EntityBlock) block).newBlockEntity(new BlockPos(0, 0, 0), block.defaultBlockState());
                if (te instanceof net.minecraft.world.Container) return block;
            }
        }
        return null;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityWandLoot(pos, state);
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        return (lvl, pos, st, tile) -> {
            if (tile instanceof TileEntityWandLoot loot) {
                loot.tick();
            }
        };
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockEntity te = level.getBlockEntity(pos);
        if (!(te instanceof TileEntityWandLoot loot)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        section.setIcon(new ItemStack(this));

        section.addLine(Component.translatable("hbm.text.loot_block")
                .withStyle(style -> style.withColor(0xffff00)));

        String blockName = loot.getReplaceBlock() != null ?
                loot.getReplaceBlock().getDescriptionId() : "null";
        section.addLine(Component.translatable("hbm.text.replace", blockName));
        section.addLine(Component.translatable("hbm.text.meta", loot.getReplaceMeta()));
        section.addLine(Component.translatable("hbm.text.pool", loot.getPoolName()));
        section.addLine(Component.translatable("hbm.text.min", loot.getMinItems(), loot.getMaxItems()));

        sections.add(section);
        return sections;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        return level.getBlockEntity(pos) instanceof TileEntityWandLoot;
    }

    @Override
    public boolean onScrew(Level world, Player player, BlockPos pos, Direction side, float fX, float fY, float fZ, ToolType tool) {
        BlockEntity te = world.getBlockEntity(pos);
        if (!(te instanceof TileEntityWandLoot loot)) return false;

        switch (tool) {
            case SCREWDRIVER:
                if (player.isShiftKeyDown()) {
                    loot.setMinItems(loot.getMinItems() - 1);
                    if (loot.getMinItems() < 0) loot.setMinItems(0);
                } else {
                    loot.setMinItems(loot.getMinItems() + 1);
                    if (loot.getMaxItems() < loot.getMinItems()) {
                        loot.setMaxItems(loot.getMinItems());
                    }
                }
                loot.setChanged();
                return true;

            case HAND_DRILL:
                if (player.isShiftKeyDown()) {
                    loot.setMaxItems(loot.getMaxItems() - 1);
                    if (loot.getMaxItems() < 0) loot.setMaxItems(0);
                    if (loot.getMinItems() > loot.getMaxItems()) {
                        loot.setMinItems(loot.getMaxItems());
                    }
                } else {
                    loot.setMaxItems(loot.getMaxItems() + 1);
                }
                loot.setChanged();
                return true;

            case DEFUSER:
                List<String> poolNames = loot.getPoolNames(loot.getReplaceBlock() == ModBlocks.DECO_LOOT.get());
                int index = poolNames.indexOf(loot.getPoolName());
                index += player.isShiftKeyDown() ? -1 : 1;
                index = MathHelper.clamp(index, 0, poolNames.size() - 1);
                loot.setPoolName(poolNames.get(index));
                loot.setChanged();
                return true;

            default:
                return false;
        }
    }
}