package com.hbm.blocks.generic;

import com.hbm.tileentity.storage.TileEntityCrateTungsten;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockStorageCrateTungsten extends BlockStorageCrate {

    public static final BooleanProperty HEATED = BooleanProperty.create("heated");

    public BlockStorageCrateTungsten(Properties props) {
        super(props);
        registerDefaultState(this.defaultBlockState().setValue(HEATED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HEATED);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityCrateTungsten(pos, state);
    }
}