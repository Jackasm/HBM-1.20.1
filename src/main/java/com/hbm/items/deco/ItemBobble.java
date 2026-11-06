package com.hbm.items.deco;

import com.hbm.blocks.generic.BlockBobble.BobbleType;
import com.hbm.items.ModItems;
import com.hbm.render.item.RenderBobbleItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemBobble extends BlockItem {

    public ItemBobble(Block block, Item.Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RenderBobbleItem renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new RenderBobbleItem();
                return renderer;
            }
        });
    }

    public static ItemStack withType(BobbleType type) {
        ItemStack stack = new ItemStack(ModItems.BOBBLEHEAD.get());
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("CustomModelData", type.ordinal());
        return stack;
    }

    public static BobbleType getType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int cmd = tag != null ? tag.getInt("CustomModelData") : 0;
        return BobbleType.values()[Math.abs(cmd) % BobbleType.values().length];
    }
}