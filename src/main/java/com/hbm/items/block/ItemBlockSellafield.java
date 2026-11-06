package com.hbm.items.block;

import com.hbm.render.item.SellafieldItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemBlockSellafield extends BlockItem {

    private static final String TAG_LEVEL = "SellafieldLevel";

    public ItemBlockSellafield(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack withLevel(Block block, int level) {
        ItemStack stack = new ItemStack(block);
        stack.getOrCreateTag().putInt(TAG_LEVEL, level);
        return stack;
    }

    public static int getLevel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(TAG_LEVEL) : 0;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        int level = getLevel(stack);
        return Component.translatable("block.hbm.sellafield." + level);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private SellafieldItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new SellafieldItemRenderer();
                return renderer;
            }
        });
    }
}