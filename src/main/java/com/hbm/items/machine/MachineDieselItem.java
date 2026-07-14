package com.hbm.items.machine;

import com.hbm.render.item.MachineDieselItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class MachineDieselItem  extends BlockItem {
    public MachineDieselItem(Block block, Properties properties) {super(block, properties);}

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private MachineDieselItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new MachineDieselItemRenderer();
                return this.renderer;
            }
        });
    }
}
