package com.hbm.blocks.deco;

import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BlockSteelBeam extends BaseEntityBlock {

    private static final VoxelShape SHAPE = box(7, 0, 7, 9, 16, 9);

    public BlockSteelBeam() {
        super(Properties.of()
                .mapColor(MapColor.METAL)
                .strength(10.0F, 15.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops()
        );
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySimpleOBJ(pos, state);
    }
}
