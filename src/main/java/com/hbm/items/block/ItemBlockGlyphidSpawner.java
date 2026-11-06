package com.hbm.items.block;

import com.hbm.render.item.RenderGlyphidSpawnerItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemBlockGlyphidSpawner extends BlockItem {

    private static final String TAG_TYPE = "GlyphidSpawnerType";

    public ItemBlockGlyphidSpawner(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack withType(int type) {
        ItemStack stack = new ItemStack(com.hbm.blocks.ModBlocks.GLYPHID_SPAWNER.get().asItem());
        stack.getOrCreateTag().putInt(TAG_TYPE, type);
        return stack;
    }

    public static int getType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        return tag != null ? tag.getInt(TAG_TYPE) : 0;
    }

    // Тип ставится в setPlacedBy, поэтому getStateForPlacement тут не нужен

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RenderGlyphidSpawnerItem renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new RenderGlyphidSpawnerItem();
                return renderer;
            }
        });
    }
}