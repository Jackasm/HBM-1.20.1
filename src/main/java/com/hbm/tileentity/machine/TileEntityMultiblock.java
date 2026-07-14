package com.hbm.tileentity.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.MultiblockUtil;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class TileEntityMultiblock extends BlockEntity {

    public TileEntityMultiblock(BlockPos pos, BlockState state) {
        super(ModTileEntity.MULTIBLOCK.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        Block block = getBlockState().getBlock();

        if (block == ModBlocks.STRUCT_LAUNCHER_CORE.get() && isCompact()) {
            buildCompact();
        }

        if (block == ModBlocks.STRUCT_LAUNCHER_CORE_LARGE.get()) {
            int meta = isTable();
            if (meta != -1) {
                buildTable(meta);
            }
        }
    }

    private boolean isCompact() {
        if (level == null) return false;

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (!(i == 0 && j == 0)) {
                    BlockPos pos = worldPosition.offset(i, 0, j);
                    if (level.getBlockState(pos).getBlock() != ModBlocks.STRUCT_LAUNCHER.get()) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private int isTable() {
        if (level == null) return -1;

        for (int i = -4; i <= 4; i++) {
            for (int j = -4; j <= 4; j++) {
                if (!(i == 0 && j == 0)) {
                    BlockPos pos = worldPosition.offset(i, 0, j);
                    if (level.getBlockState(pos).getBlock() != ModBlocks.STRUCT_LAUNCHER.get()) {
                        return -1;
                    }
                }
            }
        }

        // Проверка направления scaffolding
        for (int dir = 0; dir < 4; dir++) {
            boolean flag = true;
            int dx = 0, dz = 0;

            switch (dir) {
                case 0 -> dx = 3;
                case 1 -> dx = -3;
                case 2 -> dz = 3;
                case 3 -> dz = -3;
            }

            for (int k = 1; k < 12; k++) {
                BlockPos pos = worldPosition.offset(dx, k, dz);
                if (level.getBlockState(pos).getBlock() != ModBlocks.STRUCT_SCAFFOLD.get()) {
                    flag = false;
                    break;
                }
            }

            if (flag) return dir;
        }

        return -1;
    }

    private void buildCompact() {
        if (level == null) return;

        // Удаляем область
        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                level.removeBlock(worldPosition.offset(i, 0, j), false);
            }
        }

        // Устанавливаем ядро
        Direction facing = Direction.NORTH;
        BlockState coreState = ModBlocks.COMPACT_LAUNCHER.get().defaultBlockState()
                .setValue(BlockDummyable.FACING, facing)
                .setValue(BlockDummyable.DUMMY_STATE, 2);
        level.setBlock(worldPosition, coreState, 3);

        // Используем MultiblockUtil.fillSpace (он публичный)
        BlockDummyable launcher = (BlockDummyable) ModBlocks.COMPACT_LAUNCHER.get();
        MultiblockUtil.fillSpace(level, worldPosition, launcher.getDimensions(), launcher, facing);
    }

    private void buildTable(int meta) {
        if (level == null) return;

        // Удаляем область 9x9 (кроме центра)
        for (int i = -4; i <= 4; i++) {
            for (int j = -4; j <= 4; j++) {
                if (i == 0 && j == 0) continue;
                level.removeBlock(worldPosition.offset(i, 0, j), false);
            }
        }

        // Определяем направление scaffolding (для удаления башни)
        int dx = 0, dz = 0;
        switch (meta) {
            case 0 -> dx = 3;
            case 1 -> dx = -3;
            case 2 -> dz = 3;
            case 3 -> dz = -3;
        }

        // Удаляем scaffolding (башню)
        for (int k = 1; k < 12; k++) {
            level.removeBlock(worldPosition.offset(dx, k, dz), false);
        }

        // Определяем направление для блока
        Direction facing = Direction.NORTH;
        switch (meta) {
            case 0 -> facing = Direction.EAST;
            case 1 -> facing = Direction.WEST;
            case 2 -> facing = Direction.SOUTH;
            case 3 -> facing = Direction.NORTH;
        }

        // Устанавливаем ядро
        BlockState coreState = ModBlocks.LAUNCH_TABLE.get().defaultBlockState()
                .setValue(BlockDummyable.FACING, facing)
                .setValue(BlockDummyable.DUMMY_STATE, 2);
        level.setBlock(worldPosition, coreState, 3);

        // Используем MultiblockUtil.fillSpace
        BlockDummyable launcher = (BlockDummyable) ModBlocks.LAUNCH_TABLE.get();
        MultiblockUtil.fillSpace(level, worldPosition, launcher.getDimensions(), launcher, facing);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }
}