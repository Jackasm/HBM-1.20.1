package com.hbm.items.block;

import com.hbm.render.item.RenderFoundryChannelItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemFoundryChannel extends BlockItem {

    public ItemFoundryChannel(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RenderFoundryChannelItem renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new RenderFoundryChannelItem();
                }
                return renderer;
            }
        });
    }
}