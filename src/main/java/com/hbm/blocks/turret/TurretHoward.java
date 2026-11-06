package com.hbm.blocks.turret;


import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.turret.TileEntityTurretHoward;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TurretHoward extends TurretBaseNT {

    public TurretHoward(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityTurretHoward(pos, state);
        } else {
            return new TileEntityProxyCombo(pos, state).setInventory().setPower();
        }
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.TURRET_HOWARD.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityTurretHoward turret) {
                    turret.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity blockEntity = level.getBlockEntity(core);
        if (blockEntity instanceof TileEntityTurretHoward turret) {
            NetworkHooks.openScreen((ServerPlayer) player, turret, core);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    protected int getGuiID() {
        return 0; // GUI ID для турели
    }

    @Override
    public int[] getDimensions() {
        return new int[] {2, 0, 1, 0, 1, 0}; // up=2, north=1, south=0, west=1, east=0
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    public int getHeightOffset() {
        return 0;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(ModBlocks.TURRET_HOWARD.get());
    }

    public List<AABB> getHighlightBoxes(BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();

        int[] dim = getDimensions(); // {2, 0, 1, 0, 1, 0}

        double minX = -dim[4]; // -1
        double maxX = dim[5] + 1; // 1
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 3
        double minZ = -dim[2]; // -1
        double maxZ = dim[3] + 1; // 1

        switch(rotation) {
            case NORTH:
                boxes.add(new AABB(minX + 1, minY, minZ + 1, maxX + 1, maxY, maxZ + 1).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case SOUTH:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case WEST:
                boxes.add(new AABB(minZ + 1, minY, minX, maxZ + 1, maxY, maxX).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case EAST:
                boxes.add(new AABB(minZ, minY, minX + 1, maxZ, maxY, maxX + 1).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            default:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
        }

        return boxes;
    }

    @Override
    protected void fillSpace(LevelAccessor level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        Direction forward = dir.getOpposite();
        Direction right = dir.getClockWise();

        // 3 экстраблока на нижней грани
        this.makeExtra(level, corePos.relative(right, 1));
        this.makeExtra(level, corePos.relative(forward, 1));
        this.makeExtra(level, corePos.relative(forward, 1).relative(right, 1));
    }
}