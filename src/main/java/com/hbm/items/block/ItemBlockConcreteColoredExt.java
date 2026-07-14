package com.hbm.items.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockConcreteColoredExt;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemBlockConcreteColoredExt extends BlockItem {

    private static final String[] TYPE_NAMES = {
            "machine", "machine_stripe", "indigo", "purple", "pink", "hazard", "sand", "bronze"
    };

    public ItemBlockConcreteColoredExt(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        int type = getType(stack);
        return Component.translatable("block.hbm.concrete_colored_ext")
                .append(" ")
                .append(Component.translatable("block.hbm.concrete_ext_type_" + TYPE_NAMES[type]));
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        return "block.hbm.concrete_colored_ext";
    }

    public static int getType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomModelData")) {
            return tag.getInt("CustomModelData");
        }
        return 0;
    }

    public static ItemStack withType(int type) {
        ItemStack stack = new ItemStack(ModBlocks.CONCRETE_COLORED_EXT.get().asItem());
        stack.getOrCreateTag().putInt("CustomModelData", type);
        return stack;
    }

    public static ItemStack withType(BlockConcreteColoredExt.EnumConcreteType type) {
        return withType(type.ordinal());
    }

    public static ItemStack withType(int type, int count) {
        ItemStack stack = new ItemStack(ModBlocks.CONCRETE_COLORED_EXT.get().asItem(), count);
        stack.getOrCreateTag().putInt("CustomModelData", type);
        return stack;
    }
}