package com.hbm.items.block;

import com.hbm.render.item.NukeCustomItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;

import java.util.function.Consumer;

public class ItemBlockNukeCustom  extends BlockItem {

    public ItemBlockNukeCustom(Block blockIn, Properties builder) {super(blockIn, builder);}

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final NukeCustomItemRenderer renderer = new NukeCustomItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }
}
