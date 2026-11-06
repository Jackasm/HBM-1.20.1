package com.hbm.items.block;

import com.hbm.blocks.generic.BlockGlyphid.GlyphidType;
import com.hbm.render.item.RenderGlyphidItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemGlyphid extends BlockItem {

    private static final String TAG_TYPE = "GlyphidType";

    public ItemGlyphid(Block block, Properties properties) {
        super(block, properties);
    }

    public static ItemStack withType(GlyphidType type) {
        ItemStack stack = new ItemStack(com.hbm.blocks.ModBlocks.GLYPHID_BASE.get().asItem());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putString(TAG_TYPE, type.name());
        return stack;
    }

    public static GlyphidType getType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains(TAG_TYPE)) return GlyphidType.STANDARD;
        try {
            return GlyphidType.valueOf(tag.getString(TAG_TYPE));
        } catch (IllegalArgumentException e) {
            return GlyphidType.STANDARD;
        }
    }


    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RenderGlyphidItem renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new RenderGlyphidItem();
                return renderer;
            }
        });
    }
}