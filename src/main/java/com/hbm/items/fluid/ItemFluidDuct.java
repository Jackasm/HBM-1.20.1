package com.hbm.items.fluid;

import com.hbm.blocks.conduit.ConduitBlock;
import com.hbm.blocks.network.BlockCable;
import com.hbm.blocks.network.FluidDuctStandard;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.render.item.FluidDuctItemRenderer;
import com.hbm.tileentity.conduit.ConduitEntry;
import com.hbm.tileentity.conduit.ConduitTileEntity;
import com.hbm.tileentity.network.TileEntityPipeBaseNT;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

public class ItemFluidDuct extends BlockItem {

    public ItemFluidDuct(Block blockIn, Properties builder) {
        super(blockIn, builder);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private FluidDuctItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null)
                    this.renderer = new FluidDuctItemRenderer();
                return this.renderer;
            }
        });
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        String baseName = super.getName(stack).getString();
        FluidTypeHBM fluid = Fluids.fromID(stack.getDamageValue());
        String fluidName = fluid.getLocalizedName();

        return Component.literal(baseName + " " + fluidName);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        FluidTypeHBM fluid = Fluids.fromID(stack.getDamageValue());
        tooltip.add(Component.translatable("info.fluid_duct", fluid.getLocalizedName()));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();

        if (player != null && player.isShiftKeyDown()) {
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            ItemStack held = context.getItemInHand();
            ConduitEntry handEntry = ConduitEntry.fromStack(held);

            if (handEntry == null) return super.useOn(context);

            // Труба + кабель → кондуит
            if (block instanceof BlockCable && handEntry.type == ConduitEntry.ChannelType.FLUID) {
                ConduitBlock.makeConduit(level, pos, ConduitEntry.energy(), handEntry, player, held);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }

            // Труба + инициализированная труба → кондуит
            if (block instanceof FluidDuctStandard && handEntry.type == ConduitEntry.ChannelType.FLUID) {
                if (level.getBlockEntity(pos) instanceof TileEntityPipeBaseNT pipe) {
                    FluidTypeHBM fluidType = pipe.getFluidType();
                    if (fluidType != Fluids.NONE.get()) {
                        int pipeType = state.getValue(FluidDuctStandard.PIPE_TYPE);
                        ConduitEntry existingEntry = ConduitEntry.fluid(fluidType, pipeType);
                        if (!existingEntry.equals(handEntry)) {
                            ConduitBlock.makeConduit(level, pos, existingEntry, handEntry, player, held);
                            return InteractionResult.sidedSuccess(level.isClientSide);
                        }
                    }
                }
            }

            // Труба + кондуит → добавить канал
            if (level.getBlockEntity(pos) instanceof ConduitTileEntity conduit) {
                if (conduit.addChannel(handEntry)) {
                    if (!player.isCreative()) held.shrink(1);
                    return InteractionResult.sidedSuccess(level.isClientSide);
                }
            }
        }

        return super.useOn(context);
    }

    @Override
    public boolean isFoil(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return 64;
    }
}
