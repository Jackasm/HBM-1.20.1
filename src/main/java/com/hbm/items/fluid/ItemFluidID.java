package com.hbm.items.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.items.machine.IItemFluidIdentifier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemFluidID extends Item implements IItemFluidIdentifier {

    public ItemFluidID(Properties properties) {
        super(properties);
    }

    @Override
    public FluidTypeHBM getType(Level world, BlockPos pos, ItemStack stack) {
        return getFluidType(stack);
    }

    public static FluidTypeHBM getFluidType(ItemStack stack) {
        return FluidContainerHelper.getFluid(stack);
    }

    public static void setFluidType(ItemStack stack, FluidTypeHBM fluid) {
        FluidContainerHelper.setFluid(stack, fluid);
    }

    public static boolean isEmpty(ItemStack stack) {
        return FluidContainerHelper.isEmpty(stack);
    }

    public static int getFluidColor(ItemStack stack) {
        return FluidContainerHelper.getFluidColor(stack);
    }

    public static ItemStack createForFluid(FluidTypeHBM fluid) {
        return FluidContainerHelper.createFilled(ModItems.FLUID_IDENTIFIER.get(), fluid);
    }

    public static ItemStack createEmpty() {
        return FluidContainerHelper.createEmpty(ModItems.FLUID_IDENTIFIER.get());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        FluidTypeHBM fluid = getFluidType(stack);

        if (fluid != Fluids.NONE.get()) {
            tooltip.add(Component.translatable("item.hbm.fluid_identifier.fluid", fluid.getLocalizedName()));
            if (fluid.isHot()) {
                tooltip.add(Component.literal("§6Temperature: " + fluid.getTemperature() + "°C"));
            }
            if (fluid.isFlammable()) {
                tooltip.add(Component.literal("§cFlammable"));
            }
            if (fluid.isCorrosive()) {
                tooltip.add(Component.literal("§2Corrosive"));
            }
            if (fluid.isRadioactive()) {
                tooltip.add(Component.literal("§aRadioactive"));
            }
        } else {
            tooltip.add(Component.translatable("item.hbm.fluid_identifier.empty"));
        }

        tooltip.add(Component.literal("§7" + Component.translatable("item.hbm.fluid_identifier.hint").getString()));
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        FluidTypeHBM fluid = getFluidType(stack);
        if (fluid != Fluids.NONE.get()) {
            return Component.translatable("item.hbm.fluid_identifier", fluid.getLocalizedName());
        }
        return Component.translatable("item.hbm.fluid_identifier.empty");
    }
}