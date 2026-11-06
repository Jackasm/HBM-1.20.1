package com.hbm.blocks.generic;

import com.hbm.tileentity.ModTileEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockSandbags extends BaseEntityBlock {

    public BlockSandbags(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    /**
     * Форма зависит от соседей. В 1.7.10: если сосед opaque или такой же блок — граница 0/1, иначе 0.25/0.75.
     * Здесь возвращаем приблизительную форму (обновляется в BER для рендера), но для коллизии — с учётом соседей.
     */
    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShapeFromNeighbors(state, level, pos);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return getShapeFromNeighbors(state, level, pos);
    }

    private VoxelShape getShapeFromNeighbors(BlockState state, BlockGetter level, BlockPos pos) {
        boolean nx = isConnectable(level, pos.offset(-1, 0, 0), state);
        boolean nz = isConnectable(level, pos.offset(0, 0, -1), state);
        boolean px = isConnectable(level, pos.offset(1, 0, 0), state);
        boolean pz = isConnectable(level, pos.offset(0, 0, 1), state);

        double minX = nx ? 0.0 : 0.25;
        double minZ = nz ? 0.0 : 0.25;
        double maxX = px ? 1.0 : 0.75;
        double maxZ = pz ? 1.0 : 0.75;

        return Shapes.box(minX, 0, minZ, maxX, 1, maxZ);
    }

    private boolean isConnectable(BlockGetter level, BlockPos pos, BlockState self) {
        BlockState other = level.getBlockState(pos);
        return other.getBlock() == self.getBlock() || other.isCollisionShapeFullBlock(level, pos);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySandbags(pos, state);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.SAND)
                .strength(5.0F, 30.0F)
                .sound(SoundType.GRASS)
                .noOcclusion();
    }

    public static class TileEntitySandbags extends BlockEntity {

        public TileEntitySandbags(BlockPos pos, BlockState state) {
            super(ModTileEntity.SANDBAGS.get(), pos, state);
        }
    }
}