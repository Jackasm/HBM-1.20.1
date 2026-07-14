package com.hbm.items.fluid;

import com.hbm.render.item.FluidDuctBoxItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemFluidDuctBox  extends BlockItem {
    public ItemFluidDuctBox(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private FluidDuctBoxItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new FluidDuctBoxItemRenderer();
                return this.renderer;
            }
        });
    }
}
