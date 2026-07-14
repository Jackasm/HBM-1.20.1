package com.hbm.items.fluid;

import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ItemFluidContainer extends Item {

    private final int capacity;

    public ItemFluidContainer(Properties properties, int capacity) {
        super(properties);
        this.capacity = capacity;
    }

    public int getCapacity() { return capacity; }

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
        FluidTypeHBM fluid = getFluidType(stack);
        return fluid != Fluids.NONE.get() ? fluid.getColor() : 0x888888;
    }

    public static ItemStack createEmpty(Item containerItem) {
        return FluidContainerHelper.createEmpty(containerItem);
    }

    public static ItemStack createForFluid(Item containerItem, FluidTypeHBM fluid) {
        return FluidContainerHelper.createFilled(containerItem, fluid);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        FluidTypeHBM fluid = getFluidType(stack);
        tooltip.add(Component.translatable("item.hbm.fluid_container_capacity", capacity));

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

    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        FluidTypeHBM fluid = getFluidType(stack);
        String baseKey = this.getDescriptionId();
        return Component.translatable(baseKey, fluid.getLocalizedName());
    }
}