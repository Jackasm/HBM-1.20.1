package com.hbm.items.block;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemBlockPlantTall extends BlockItem {

    public ItemBlockPlantTall(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        int type = 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomModelData")) {
            type = tag.getInt("CustomModelData");
        }
        String name = switch (type) {
            case 0 -> "block.hbm.plant_tall.weed";
            case 1 -> "block.hbm.plant_tall.cd2";
            case 2 -> "block.hbm.plant_tall.cd3";
            case 3 -> "block.hbm.plant_tall.cd4";
            default -> "block.hbm.plant_tall";
        };
        return Component.translatable(name);
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack stack) {
        int type = 0;
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomModelData")) {
            type = tag.getInt("CustomModelData");
        }
        return switch (type) {
            case 0 -> "block.hbm.plant_tall.weed";
            case 1 -> "block.hbm.plant_tall.cd2";
            case 2 -> "block.hbm.plant_tall.cd3";
            case 3 -> "block.hbm.plant_tall.cd4";
            default -> "block.hbm.plant_tall";
        };
    }
}
