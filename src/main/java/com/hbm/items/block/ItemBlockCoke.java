package com.hbm.items.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockCoke;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemBlockCoke extends BlockItem {

    public ItemBlockCoke(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public int getBurnTime(ItemStack itemStack, @Nullable RecipeType<?> recipeType) {
        return 32000;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        BlockCoke.CokeType type = getCokeType(stack);
        return Component.translatable("block.hbm.block_coke_" + type.getSerializedName());
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        return "block.hbm.block_coke";
    }

    public static BlockCoke.CokeType getCokeType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int type = 0;
        if (tag != null && tag.contains("CustomModelData")) {
            type = tag.getInt("CustomModelData");
        }
        return BlockCoke.CokeType.byOrdinal(type);
    }

    public static ItemStack withType(BlockCoke.CokeType type) {
        return withType(type.ordinal());
    }

    public static ItemStack withType(int type) {
        ItemStack stack = new ItemStack(ModBlocks.BLOCK_COKE.get());
        stack.getOrCreateTag().putInt("CustomModelData", type);
        return stack;
    }
}