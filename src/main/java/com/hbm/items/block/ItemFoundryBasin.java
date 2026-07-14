package com.hbm.items.block;

import com.hbm.render.item.RenderFoundryBasinItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemFoundryBasin extends BlockItem {

    public ItemFoundryBasin(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RenderFoundryBasinItem renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) {
                    renderer = new RenderFoundryBasinItem();
                }
                return renderer;
            }
        });
    }
}