package com.hbm.items.block;

import com.hbm.blocks.ModBlocks;
import com.hbm.render.item.LaunchPadItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemLaunchPad extends BlockItem {

    public ItemLaunchPad(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private LaunchPadItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new LaunchPadItemRenderer();
                }
                return this.renderer;
            }
        });
    }
}