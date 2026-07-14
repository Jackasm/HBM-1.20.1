package com.hbm.items.machine;

import com.hbm.render.item.MachineCrystallizerItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class MachineCrystallizerItem extends BlockItem {
    public MachineCrystallizerItem(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private MachineCrystallizerItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new MachineCrystallizerItemRenderer();
                }
                return this.renderer;
            }
        });
    }
}