package com.hbm.items.block;

import com.hbm.blocks.generic.BlockOreBasalt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemBlockOreBasalt extends BlockItem {

    public ItemBlockOreBasalt(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        int type = 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomModelData")) {
            type = tag.getInt("CustomModelData");
        }
        BlockOreBasalt.EnumBasaltOreType[] values = BlockOreBasalt.EnumBasaltOreType.values();
        BlockOreBasalt.EnumBasaltOreType oreType = values[Math.min(type, values.length - 1)];
        return Component.translatable("block.hbm.ore_basalt." + oreType.getSerializedName());
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack stack) {
        int type = 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomModelData")) {
            type = tag.getInt("CustomModelData");
        }
        BlockOreBasalt.EnumBasaltOreType[] values = BlockOreBasalt.EnumBasaltOreType.values();
        BlockOreBasalt.EnumBasaltOreType oreType = values[Math.min(type, values.length - 1)];
        return "block.hbm.ore_basalt." + oreType.getSerializedName();
    }
}
