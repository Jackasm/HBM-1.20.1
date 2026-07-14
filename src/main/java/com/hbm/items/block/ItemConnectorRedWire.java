package com.hbm.items.block;

import com.hbm.blocks.network.ConnectorRedWire;
import com.hbm.render.item.ConnectorRedItemRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemConnectorRedWire extends BlockItem {

    public ItemConnectorRedWire(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final ConnectorRedItemRenderer renderer = new ConnectorRedItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    protected boolean canPlace(@NotNull BlockPlaceContext context, @NotNull BlockState state) {
        return super.canPlace(context, state);
    }

    @Override
    protected BlockState getPlacementState(@NotNull BlockPlaceContext context) {
        Direction facing = context.getClickedFace();
        return this.getBlock().defaultBlockState()
                .setValue(ConnectorRedWire.FACING, facing);
    }
}