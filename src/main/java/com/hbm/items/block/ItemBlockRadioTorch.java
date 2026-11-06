package com.hbm.items.block;

import com.hbm.render.item.RadioTorchItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemBlockRadioTorch extends BlockItem {

    private final ResourceLocation texture;

    public ItemBlockRadioTorch(Block block, Properties properties, ResourceLocation texture) {
        super(block, properties);
        this.texture = texture;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RadioTorchItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new RadioTorchItemRenderer(texture);
                return renderer;
            }
        });
    }
}