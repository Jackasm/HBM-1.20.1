package com.hbm.tileentity.storage;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TileEntitySupplyCrate extends BlockEntity {

    public List<ItemStack> items = new ArrayList<>();

    public TileEntitySupplyCrate(BlockPos pos, BlockState state) {
        super(ModTileEntity.SUPPLY_CRATE.get(), pos, state);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        items.clear();
        ListTag list = nbt.getList("items", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag nbt1 = list.getCompound(i);
            ItemStack stack = ItemStack.of(nbt1);
            if (!stack.isEmpty()) {
                items.add(stack);
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        ListTag list = new ListTag();
        for (ItemStack item : items) {
            CompoundTag nbt1 = new CompoundTag();
            item.save(nbt1);
            list.add(nbt1);
        }
        nbt.put("items", list);
    }
}