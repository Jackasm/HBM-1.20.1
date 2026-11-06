package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class BlockRedBrick extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public BlockRedBrick(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Аналог BlockPistonBase.determineOrientation: смотрит и вверх/вниз, и в стороны
        return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
    }

    @Override
    public ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target,
                                       @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Player player) {
        // Оригинал: getItemDropped -> null
        return ItemStack.EMPTY;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.NETHER)
                .strength(1.5F, 10000.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops();
    }
}