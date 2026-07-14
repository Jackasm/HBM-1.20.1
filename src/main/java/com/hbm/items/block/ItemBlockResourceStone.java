package com.hbm.items.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockResourceStone.EnumStoneType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemBlockResourceStone extends BlockItem {

    public ItemBlockResourceStone(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        int type = getType(stack);
        EnumStoneType stoneType = EnumStoneType.values()[Math.abs(type) % EnumStoneType.values().length];
        return Component.translatable("block.hbm.stone_resource")
                .append(" ")
                .append(Component.translatable("block.hbm.stone_resource_type_" + stoneType.name().toLowerCase()));
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        return "block.hbm.stone_resource";
    }

    public static int getType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomModelData")) {
            return tag.getInt("CustomModelData");
        }
        return 0;
    }

    public static ItemStack withType(int type) {
        ItemStack stack = new ItemStack(ModBlocks.STONE_RESOURCE.get().asItem());
        stack.getOrCreateTag().putInt("CustomModelData", type);
        return stack;
    }

    public static ItemStack withType(EnumStoneType type) {
        return withType(type.ordinal());
    }
}