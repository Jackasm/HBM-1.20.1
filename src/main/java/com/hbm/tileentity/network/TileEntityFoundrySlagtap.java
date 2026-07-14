package com.hbm.tileentity.network;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.network.FoundryOutlet;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntitySlag;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Objects;

public class TileEntityFoundrySlagtap extends TileEntityFoundryOutlet {

    public TileEntityFoundrySlagtap(BlockPos pos, BlockState state) {
        super(ModTileEntity.FOUNDRY_SLAGTAP.get(), pos, state);
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, TileEntityFoundrySlagtap te) {
        TileEntityFoundryOutlet.serverTick(level, pos, state, te);
    }

    @Override
    public boolean canAcceptPartialFlow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        if (filter != null && (filter != stack.material ^ invertFilter)) return false;
        if (isClosed()) return false;

        Direction facing = getBlockState().getValue(FoundryOutlet.FACING);
        if (side != facing.getOpposite()) return false;

        Vec3 start = new Vec3(pos.getX() + 0.5, pos.getY() - 0.125, pos.getZ() + 0.5);
        Vec3 end = new Vec3(pos.getX() + 0.5, pos.getY() - 14.875, pos.getZ() + 0.5);

        BlockHitResult hit = level.clip(new net.minecraft.world.level.ClipContext(start, end,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE, null));

        return hit.getType() == HitResult.Type.BLOCK;
    }

    @Override
    public MaterialStack flow(Level level, BlockPos pos, Direction side, MaterialStack stack) {
        Vec3 start = new Vec3(pos.getX() + 0.5, pos.getY() - 0.125, pos.getZ() + 0.5);
        Vec3 end = new Vec3(pos.getX() + 0.5, pos.getY() - 14.875, pos.getZ() + 0.5);

        BlockHitResult hit = level.clip(new net.minecraft.world.level.ClipContext(start, end,
                net.minecraft.world.level.ClipContext.Block.COLLIDER,
                net.minecraft.world.level.ClipContext.Fluid.NONE, null));

        if (hit.getType() != HitResult.Type.BLOCK) return stack;

        BlockPos hitPos = hit.getBlockPos();
        Block hitBlock = level.getBlockState(hitPos).getBlock();
        Block aboveBlock = level.getBlockState(hitPos.above()).getBlock();

        boolean didFlow = false;

        if (hitBlock == ModBlocks.SLAG.get()) {
            BlockEntity te = level.getBlockEntity(hitPos);
            if (te instanceof TileEntitySlag slag) {
                if (Objects.requireNonNull(slag).mat == stack.material) {
                    int transfer = Math.min(slag.maxAmount - slag.amount, stack.amount);
                    slag.amount += transfer;
                    stack.amount -= transfer;
                    didFlow = didFlow || transfer > 0;
                }
            }
        } else if (hitBlock.defaultBlockState().canBeReplaced()) {
            level.setBlock(hitPos, ModBlocks.SLAG.get().defaultBlockState(), 3);
            BlockEntity te = level.getBlockEntity(hitPos);
            if (te instanceof TileEntitySlag slag) {
                Objects.requireNonNull(slag).mat = stack.material;
                int transfer = Math.min(slag.maxAmount, stack.amount);
                slag.amount += transfer;
                stack.amount -= transfer;
                didFlow = didFlow || transfer > 0;
            }
        }

        if (stack.amount > 0 && aboveBlock.defaultBlockState().canBeReplaced()) {
            level.setBlock(hitPos.above(), ModBlocks.SLAG.get().defaultBlockState(), 3);
            BlockEntity te = level.getBlockEntity(hitPos.above());
            if (te instanceof TileEntitySlag slag) {
                Objects.requireNonNull(slag).mat = stack.material;
                int transfer = Math.min(slag.maxAmount, stack.amount);
                slag.amount += transfer;
                stack.amount -= transfer;
                didFlow = didFlow || transfer > 0;
            }
        }

        if (didFlow) {
            Direction dir = side.getOpposite();
            CompoundTag data = new CompoundTag();
            data.putString("type", "foundry");
            data.putInt("color", stack.material.moltenColor);
            data.putByte("dir", (byte) dir.ordinal());
            data.putFloat("off", 0.375F);
            data.putFloat("base", 0F);
            data.putFloat("len", Math.max(1F, pos.getY() - (float) Math.ceil(hit.getLocation().y)));
            PacketDispatcher.sendToAllAround(
                    new AuxParticlePacketNT(data, pos.getX() + 0.5 - dir.getStepX() * 0.125, pos.getY() + 0.125, pos.getZ() + 0.5 - dir.getStepZ() * 0.125),
                    level, pos, 50);
        }

        return stack.amount <= 0 ? null : stack;
    }
}