package com.hbm.tileentity;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.MultiblockUtil;
import com.hbm.util.Library;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public abstract class TileEntityMachineBase extends TileEntityLoadedBase{

    private UUID ownerUUID;

    public TileEntityMachineBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public boolean isUsableByPlayer(Player player) {
        if(level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        if (ownerUUID != null) {
            tag.putUUID("OwnerUUID", ownerUUID);
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        if (tag.hasUUID("OwnerUUID")) {
            ownerUUID = tag.getUUID("OwnerUUID");
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        if (level != null && level.getBlockState(worldPosition).getBlock() instanceof BlockDummyable dummy) {
            // Если это core-блок, возвращаем большой бокс
            if (dummy.isCore(level.getBlockState(worldPosition))) {
                int[] dim = dummy.getDimensions();
                Direction facing = level.getBlockState(worldPosition).getValue(BlockDummyable.FACING);
                int[] rot = MultiblockUtil.rotate(dim, facing);

                double minX = worldPosition.getX() - rot[4];
                double minY = worldPosition.getY() - rot[1];
                double minZ = worldPosition.getZ() - rot[2];
                double maxX = worldPosition.getX() + rot[5] + 1;
                double maxY = worldPosition.getY() + rot[0] + 1;
                double maxZ = worldPosition.getZ() + rot[3] + 1;

                return new AABB(minX, minY, minZ, maxX, maxY, maxZ);
            }
        }
        return super.getRenderBoundingBox();
    }

    public void updateRedstoneConnection(Library.PosDir pos) {
        if (level == null) return;

        BlockPos targetPos = pos.pos();
        Direction dir = pos.dir();
        BlockState state = level.getBlockState(targetPos);
        Block block = state.getBlock();

        // Уведомляем блок о изменении соседа
        block.neighborChanged(state, level, targetPos, this.getBlockState().getBlock(), worldPosition, false);

        // Если блок является нормальным кубом, проверяем следующий блок за ним
        if (state.isSolidRender(level, targetPos)) {
            BlockPos nextPos = targetPos.relative(dir);
            BlockState nextState = level.getBlockState(nextPos);
            Block nextBlock = nextState.getBlock();

            // Проверяем, нужно ли уведомить следующий блок
            if (nextState.isRedstoneConductor(level, nextPos)) {
                nextBlock.neighborChanged(nextState, level, nextPos, this.getBlockState().getBlock(), worldPosition, false);
            }
        }
    }
}