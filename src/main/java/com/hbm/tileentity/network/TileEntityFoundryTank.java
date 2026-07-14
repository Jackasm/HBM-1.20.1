package com.hbm.tileentity.network;

import com.hbm.api.block.ICrucibleAcceptor;
import com.hbm.blocks.network.FoundryChannel;
import com.hbm.inventory.material.MaterialShapes;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.*;

public class TileEntityFoundryTank extends TileEntityFoundryBase {

    public int nextUpdate;

    public TileEntityFoundryTank(BlockPos pos, BlockState state) {
        super(ModTileEntity.FOUNDRY_TANK.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityFoundryTank te) {
        TileEntityFoundryBase.serverTick(level, pos, state, te);

        if (te.type == null && te.amount != 0) te.amount = 0;

        te.nextUpdate--;
        if (te.nextUpdate <= 0 && te.amount > 0 && te.type != null) {
            boolean hasOp = false;
            te.nextUpdate = level.random.nextInt(6) + 5;

            // Сначала пытаемся слить в танк снизу
            BlockEntity teBelow = level.getBlockEntity(pos.below());
            if (teBelow instanceof TileEntityFoundryTank tank) {
                if ((tank.type == null || tank.type == te.type) && tank.amount < tank.getCapacity()) {
                    tank.type = te.type;
                    int toFill = Math.min(te.amount, tank.getCapacity() - tank.amount);
                    te.amount -= toFill;
                    tank.amount += toFill;
                    hasOp = true;
                }
            }

            List<Direction> dirs = new ArrayList<>(List.of(Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST));
            Collections.shuffle(dirs);

            if (!hasOp) {
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
                            } else {
                                te.amount = left.amount;
                            }
                            hasOp = true;
                            break;
                        }
                    }
                }
            }

            if (!hasOp) {
                for (Direction dir : dirs) {
                    BlockPos neighborPos = pos.relative(dir);
                    BlockEntity neighbor = level.getBlockEntity(neighborPos);
                    if (neighbor instanceof TileEntityFoundryTank acc) {
                        if (acc.type == null || acc.type == te.type || acc.amount == 0) {
                            acc.type = te.type;
                            if (level.random.nextInt(5) == 0) {
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
    }

    @Override
    public int getCapacity() {
        return MaterialShapes.BLOCK.q(4);
    }
}