package com.hbm.items.machine;

import com.hbm.render.item.RenderMachineCrucibleItem;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemMachineCrucible extends BlockItem {
    public ItemMachineCrucible(Block block, Properties properties) { super(block, properties); }

    @Override
    public void initializeClient(@NotNull Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private RenderMachineCrucibleItem renderer;
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (renderer == null) renderer = new RenderMachineCrucibleItem();
                return renderer;
            }
        });
    }
}
