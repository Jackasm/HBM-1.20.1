package com.hbm.items.tool;

import com.hbm.api.block.IToolable;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.network.PacketDispatcher;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemBlowtorch extends Item {

    public ItemBlowtorch(Properties properties) {
        super(properties.stacksTo(1));
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof IToolable toolable) {
            // Получаем значения из NBT как в оригинале
            if (this == ModItems.BLOWTORCH.get()) {
                int gasFill = getFill(stack, Fluids.GAS.get());
                if (gasFill < 250) return InteractionResult.PASS;
            }
            if (this == ModItems.ACETYLENE_TORCH.get()) {
                int unsatFill = getFill(stack, Fluids.UNSATURATEDS.get());
                int oxygenFill = getFill(stack, Fluids.OXYGEN.get());
                if (unsatFill < 20 || oxygenFill < 10) return InteractionResult.PASS;
            }

            if (toolable.onScrew(level, player, pos,
                    context.getClickedFace(),
                    (float) context.getClickLocation().x - pos.getX(),
                    (float) context.getClickLocation().y - pos.getY(),
                    (float) context.getClickLocation().z - pos.getZ(),
                    IToolable.ToolType.TORCH)) {

                if (!level.isClientSide) {
                    if (this == ModItems.BLOWTORCH.get()) {
                        setFill(stack, Fluids.GAS.get(),
                                getFill(stack, Fluids.GAS.get()) - 250);
                    }
                    if (this == ModItems.ACETYLENE_TORCH.get()) {
                        setFill(stack, Fluids.UNSATURATEDS.get(),
                                getFill(stack, Fluids.UNSATURATEDS.get()) - 20);
                        setFill(stack, Fluids.OXYGEN.get(),
                                getFill(stack, Fluids.OXYGEN.get()) - 10);
                    }

                    CompoundTag data = new CompoundTag();
                    data.putString("type", "tau");
                    data.putByte("count", (byte) 10);
                    PacketDispatcher.sendAuxParticleNT(data,
                            pos.getX() + context.getClickLocation().x - pos.getX(),
                            pos.getY() + context.getClickLocation().y - pos.getY(),
                            pos.getZ() + context.getClickLocation().z - pos.getZ(), player);
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        return InteractionResult.PASS;
    }

    public int getFill(ItemStack stack, FluidTypeHBM type) {
        if (!stack.hasTag()) {
            initNBT(stack);
        }
        return Objects.requireNonNull(stack.getTag()).getInt(String.valueOf(Fluids.getID(type)));
    }

    public void setFill(ItemStack stack, FluidTypeHBM type, int fill) {
        if (!stack.hasTag()) {
            initNBT(stack);
        }
        Objects.requireNonNull(stack.getTag()).putInt(String.valueOf(Fluids.getID(type)), fill);
    }

    public int getMaxFill(FluidTypeHBM type) {
        if (type == Fluids.GAS.get()) return 4000;
        if (type == Fluids.UNSATURATEDS.get()) return 8000;
        if (type == Fluids.OXYGEN.get()) return 16000;
        return 0;
    }

    public void initNBT(ItemStack stack) {
        CompoundTag tag = new CompoundTag();

        if (this == ModItems.BLOWTORCH.get()) {
            setFill(stack, Fluids.GAS.get(), getMaxFill(Fluids.GAS.get()));
        }
        if (this == ModItems.ACETYLENE_TORCH.get()) {
            setFill(stack, Fluids.UNSATURATEDS.get(), getMaxFill(Fluids.UNSATURATEDS.get()));
            setFill(stack, Fluids.OXYGEN.get(), getMaxFill(Fluids.OXYGEN.get()));
        }
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        IFluidHandlerItem fluidHandler = stack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (fluidHandler == null) return false;

        FluidStack fluid1 = fluidHandler.getFluidInTank(0);
        FluidStack fluid2 = fluidHandler.getFluidInTank(1);

        return fluid1.getAmount() > 0 || fluid2.getAmount() > 0;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        IFluidHandlerItem fluidHandler = stack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (fluidHandler == null) return 0;

        double frac = 0D;

        if (this == ModItems.BLOWTORCH.get()) {
            FluidStack fluid = fluidHandler.getFluidInTank(0);
            frac = (double) fluid.getAmount() / (double) fluidHandler.getTankCapacity(0);
        }
        if (this == ModItems.ACETYLENE_TORCH.get()) {
            FluidStack fluid1 = fluidHandler.getFluidInTank(0);
            FluidStack fluid2 = fluidHandler.getFluidInTank(1);
            frac = Math.min(
                    (double) fluid1.getAmount() / (double) fluidHandler.getTankCapacity(0),
                    (double) fluid2.getAmount() / (double) fluidHandler.getTankCapacity(1)
            );
        }

        return (int) Math.round(frac * 13.0);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00FF00;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        IFluidHandlerItem fluidHandler = stack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (fluidHandler == null) return;

        if (this == ModItems.BLOWTORCH.get()) {
            FluidStack fluid = fluidHandler.getFluidInTank(0);
            tooltip.add(Component.literal(ChatFormatting.YELLOW +
                    fluid.getDisplayName().getString() + ": " + fluid.getAmount() + "/" + fluidHandler.getTankCapacity(0)));
        }
        if (this == ModItems.ACETYLENE_TORCH.get()) {
            FluidStack fluid1 = fluidHandler.getFluidInTank(0);
            FluidStack fluid2 = fluidHandler.getFluidInTank(1);
            tooltip.add(Component.literal(ChatFormatting.YELLOW +
                    fluid1.getDisplayName().getString() + ": " + fluid1.getAmount() + "/" + fluidHandler.getTankCapacity(0)));
            tooltip.add(Component.literal(ChatFormatting.AQUA +
                    fluid2.getDisplayName().getString() + ": " + fluid2.getAmount() + "/" + fluidHandler.getTankCapacity(1)));
        }
    }

    public static ItemStack getEmptyTool(Item item) {
        ItemStack stack = new ItemStack(item);
        IFluidHandlerItem fluidHandler = stack.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER_ITEM).orElse(null);
        if (fluidHandler != null) {
            fluidHandler.drain(Integer.MAX_VALUE, IFluidHandlerItem.FluidAction.EXECUTE);
        }
        return stack;
    }
}