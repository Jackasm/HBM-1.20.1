package com.hbm.tileentity;

import com.hbm.util.CompatExternal;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;

public interface IPersistentNBT {

    String NBT_PERSISTENT_KEY = "persistent";

    void writeNBT(CompoundTag nbt);

    void readNBT(CompoundTag nbt);

    default ArrayList<ItemStack> getDrops(Block block) {
        ArrayList<ItemStack> list = new ArrayList<>();
        ItemStack stack = new ItemStack(block);
        CompoundTag data = new CompoundTag();
        writeNBT(data);
        if (!data.isEmpty()) {
            stack.setTag(data);
        }
        list.add(stack);
        return list;
    }

    static ArrayList<ItemStack> getDrops(Level level, BlockPos pos, Block block) {

        BlockEntity tile = CompatExternal.getCoreFromPos(level, pos);

        if (tile instanceof IPersistentNBT persistent) {
            return persistent.getDrops(block);
        }

        return new ArrayList<>();
    }

    static void restoreData(Level level, BlockPos pos, ItemStack stack) {
        try {
            if (!stack.hasTag()) return;
            if (level.getBlockEntity(pos) instanceof IPersistentNBT persistent) {
                persistent.readNBT(stack.getTag());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}