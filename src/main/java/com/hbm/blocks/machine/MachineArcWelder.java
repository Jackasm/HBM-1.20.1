package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityMachineArcWelder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineArcWelder extends BlockDummyable {

    public MachineArcWelder(Properties properties) {
        super(properties);
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityMachineArcWelder(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setInventory().setPower().setFluid();
        }
        return null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.MACHINE_ARC_WELDER.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityMachineArcWelder welder) {
                    welder.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(core);
        if (te instanceof TileEntityMachineArcWelder welder) {
            return standardOpenBehavior(level, pos, player, getGuiID()) ? InteractionResult.CONSUME : InteractionResult.PASS;
        }
        return InteractionResult.PASS;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    @Override
    public int[] getDimensions() {
        return new int[] {1, 0, 1, 0, 1, 1}; // up, down, north, south, west, east
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    protected void fillSpace(LevelAccessor level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        Direction forward = dir.getOpposite();
        Direction right = dir.getClockWise();

        // Блоки extra
        makeExtra(level, corePos.relative(right, 1));           // справа
        makeExtra(level, corePos.relative(right, -1));          // слева
        makeExtra(level, corePos.relative(forward, 1));         // спереди
        makeExtra(level, corePos.relative(forward, 1).relative(right, 1));   // спереди-справа
        makeExtra(level, corePos.relative(forward, 1).relative(right, -1));  // спереди-слева
        makeExtra(level, corePos.above(1));                     // над ядром
    }

    public List<AABB> getHighlightBoxes(BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();

        int[] dim = getDimensions(); // {1, 0, 1, 0, 1, 1}

        double minX = -dim[4]; // -1
        double maxX = dim[5] + 1; // 1 + 1 = 2
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 1 + 1 = 2
        double minZ = -dim[2]; // -1
        double maxZ = dim[3] + 1; // 0 + 1 = 1

        switch (rotation) {
            case NORTH -> boxes.add(new AABB(minX, minY, minZ + 1, maxX, maxY, maxZ + 1).move(corePos.getX(), corePos.getY(), corePos.getZ()));
            case SOUTH -> boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(corePos.getX(), corePos.getY(), corePos.getZ()));
            case WEST -> boxes.add(new AABB(minZ + 1, minY, minX, maxZ + 1, maxY, maxX).move(corePos.getX(), corePos.getY(), corePos.getZ()));
            case EAST -> boxes.add(new AABB(minZ, minY, minX, maxZ, maxY, maxX).move(corePos.getX(), corePos.getY(), corePos.getZ()));
            default -> boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(corePos.getX(), corePos.getY(), corePos.getZ()));
        }

        return boxes;
    }
}