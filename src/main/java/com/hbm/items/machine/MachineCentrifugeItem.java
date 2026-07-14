package com.hbm.items.machine;

import com.hbm.render.item.MachineCentrifugeItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class MachineCentrifugeItem extends BlockItem {
    public MachineCentrifugeItem(Block blockIn, Properties builder) {super(blockIn, builder);}

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private MachineCentrifugeItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new MachineCentrifugeItemRenderer();
                return this.renderer;
            }
        });
    }
}
