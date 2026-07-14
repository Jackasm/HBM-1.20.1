package com.hbm.items.block;

import com.hbm.blocks.conduit.ConduitBlock;
import com.hbm.blocks.network.FluidDuctStandard;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.render.item.RedCableItemRenderer;
import com.hbm.tileentity.conduit.ConduitEntry;
import com.hbm.tileentity.conduit.ConduitTileEntity;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemRedCable  extends BlockItem {
    public ItemRedCable(Block block, Properties properties) {super(block, properties);}

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private final RedCableItemRenderer renderer = new RedCableItemRenderer();

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                return renderer;
            }
        });
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player != null && player.isShiftKeyDown()) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            ItemStack held = context.getItemInHand();
            ConduitEntry handEntry = ConduitEntry.fromStack(held); // кабель

            // Кабель + труба → кондуит
            if (block instanceof FluidDuctStandard) {
                if (level.getBlockEntity(pos) instanceof TileEntityPipeBaseNT pipe) {
                    FluidTypeHBM fluidType = pipe.getFluidType();
                    int pipeType = state.getValue(FluidDuctStandard.PIPE_TYPE);
                    ConduitEntry pipeEntry = ConduitEntry.fluid(fluidType, pipeType);

                    if (!pipeEntry.equals(handEntry)) {
                        ConduitBlock.makeConduit(level, pos, pipeEntry, handEntry, player, held);
                        return InteractionResult.sidedSuccess(level.isClientSide);
                    }
                }
            }

            // Кабель + кондуит → добавить кабель (если ещё нет)
            if (level.getBlockEntity(pos) instanceof ConduitTileEntity conduit) {
                if (conduit.addChannel(handEntry)) {
                    if (!player.isCreative()) held.shrink(1);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        return super.useOn(context);
    }
}
