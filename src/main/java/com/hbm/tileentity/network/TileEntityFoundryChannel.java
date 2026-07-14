package com.hbm.tileentity.network;

import com.hbm.api.block.ICrucibleAcceptor;
import com.hbm.blocks.network.FoundryChannel;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats;
import com.hbm.inventory.material.NTMMaterial;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class TileEntityFoundryChannel extends TileEntityFoundryBase {

    public int nextUpdate;
    public int lastFlow = 0;
    protected NTMMaterial neighborType;
    protected boolean hasCheckedNeighbors;
    protected int unpropagateTime;

    public TileEntityFoundryChannel(BlockPos pos, BlockState state) {
        super(ModTileEntity.FOUNDRY_CHANNEL.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityFoundryChannel te) {
        TileEntityFoundryBase.serverTick(level, pos, state, te);

        if (!te.hasCheckedNeighbors) {
            List<TileEntityFoundryChannel> visited = new ArrayList<>();
            visited.add(te);
            te.neighborType = te.checkNeighbors(visited);
            te.hasCheckedNeighbors = true;
        }

        if (te.type == null && te.amount != 0) te.amount = 0;

        te.nextUpdate--;
        if (te.nextUpdate <= 0 && te.amount > 0) {
            boolean hasOp = false;
            te.nextUpdate = 5;

            List<Direction> dirs = new ArrayList<>(List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));
            Collections.shuffle(dirs);
            if (te.lastFlow > 0) {
                Direction lastDir = Direction.from3DDataValue(te.lastFlow);
                dirs.remove(lastDir);
                dirs.add(lastDir);
            }

            for (Direction dir : dirs) {
                BlockPos neighborPos = pos.relative(dir);
                Block block = level.getBlockState(neighborPos).getBlock();

                if (block instanceof ICrucibleAcceptor acc && !(block instanceof FoundryChannel)) {
                    MaterialStack stack = new MaterialStack(te.type, te.amount);
                    if (acc.canAcceptPartialFlow(level, neighborPos, dir.getOpposite(), stack)) {
                        MaterialStack left = acc.flow(level, neighborPos, dir.getOpposite(), stack);
                        if (left == null) {
                            te.type = null;
                            te.amount = 0;
                            te.propagateMaterial(null);
                        } else {
                            te.amount = left.amount;
                        }
                        hasOp = true;
                        break;
                    }
                }
            }

            if (!hasOp) {
                for (Direction dir : dirs) {
                    BlockPos neighborPos = pos.relative(dir);
                    BlockEntity neighbor = level.getBlockEntity(neighborPos);
                    if (neighbor instanceof TileEntityFoundryChannel acc) {
                        if (acc.type == null || acc.type == te.type || acc.amount == 0) {
                            acc.type = te.type;
                            acc.lastFlow = dir.getOpposite().ordinal();
                            if (level.random.nextInt(5) == 0 || te.amount == 1) {
                                int buf = te.amount;
                                te.amount = acc.amount;
                                acc.amount = buf;
                            } else {
                                int diff = te.amount - acc.amount;
                                if (diff > 0) {
                                    diff /= 2;
                                    te.amount -= diff;
                                    acc.amount += diff;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (te.neighborType != null && te.amount == 0) te.unpropagateTime++;
        if (te.unpropagateTime > 100) te.propagateMaterial(null);
        if (te.amount == 0) {
            te.lastFlow = 0;
            te.nextUpdate = 5;
        } else {
            te.unpropagateTime = 0;
        }
    }

    @Override
    public int getCapacity() {
        return MaterialShapes.INGOT.q(2);
    }

    @Override
    public boolean canAcceptPartialPour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        if (!hasCheckedNeighbors || (neighborType != null && neighborType != stack.material)) return false;
        return super.canAcceptPartialPour(level, pos, dX, dY, dZ, side, stack);
    }

    @Override
    public MaterialStack pour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        propagateMaterial(stack.material);
        return super.pour(level, pos, dX, dY, dZ, side, stack);
    }

    public void propagateMaterial(NTMMaterial propType) {
        if (propType != null && neighborType != null) return;
        List<TileEntityFoundryChannel> visited = new ArrayList<>();
        visited.add(this);
        boolean hasMaterial = propagateMaterial(propType, visited, false);
        if (propType == null && !hasMaterial) {
            for (TileEntityFoundryChannel ch : visited) ch.neighborType = null;
        }
    }

    protected boolean propagateMaterial(NTMMaterial propType, List<TileEntityFoundryChannel> visited, boolean hasMaterial) {
        if (propType != null) neighborType = propType;
        else unpropagateTime = 0;

        for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            BlockEntity neighbor = Objects.requireNonNull(level).getBlockEntity(worldPosition.relative(dir));
            if (neighbor instanceof TileEntityFoundryChannel acc && !visited.contains(neighbor)) {
                visited.add(acc);
                if (acc.amount > 0) hasMaterial = true;
                hasMaterial = acc.propagateMaterial(propType, visited, hasMaterial);
            }
        }
        return hasMaterial;
    }

    protected NTMMaterial checkNeighbors(List<TileEntityFoundryChannel> visited) {
        if (neighborType != null) return neighborType;
        for (Direction dir : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST}) {
            BlockEntity neighbor = Objects.requireNonNull(level).getBlockEntity(worldPosition.relative(dir));
            if (neighbor instanceof TileEntityFoundryChannel acc && !visited.contains(neighbor)) {
                visited.add(acc);
                NTMMaterial mat = acc.checkNeighbors(visited);
                if (mat != null) return mat;
            }
            if (neighbor instanceof TileEntityFoundryOutlet outlet) {
                return outlet.type;
            }
        }
        return null;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.lastFlow = nbt.getByte("flow");
        this.neighborType = Mats.matById.get(nbt.getInt("nType"));
        this.hasCheckedNeighbors = nbt.getBoolean("init");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putByte("flow", (byte) this.lastFlow);
        nbt.putInt("nType", this.neighborType != null ? this.neighborType.id : -1);
        nbt.putBoolean("init", hasCheckedNeighbors);
    }
}