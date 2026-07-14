package com.hbm.items.machine;

import com.hbm.render.item.RenderArcWelderItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemArcWelder extends BlockItem {
    public ItemArcWelder(Block blockIn, Properties builder) {super(blockIn, builder);}

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RenderArcWelderItem renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new RenderArcWelderItem();
                return renderer;
            }
        });
    }
}
