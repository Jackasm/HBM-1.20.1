package com.hbm.items.block;

import com.hbm.blocks.ModBlocks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemBlockConcreteColored extends BlockItem {

    public ItemBlockConcreteColored(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        DyeColor color = getColor(stack);
        return Component.translatable("block.hbm.concrete_colored")
                .append(" ")
                .append(Component.translatable("color.minecraft." + color.getName()));
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        return "block.hbm.concrete_colored";
    }

    public static DyeColor getColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomModelData")) {
            int colorOrdinal = tag.getInt("CustomModelData");
            return DyeColor.byId(colorOrdinal);
        }
        return DyeColor.WHITE;
    }

    public static ItemStack withColor(DyeColor color) {
        ItemStack stack = new ItemStack(ModBlocks.CONCRETE_COLORED.get().asItem());
        stack.getOrCreateTag().putInt("CustomModelData", color.getId());
        return stack;
    }
}