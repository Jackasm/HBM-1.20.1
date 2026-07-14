package com.hbm.items.armor;

import com.hbm.api.fluid.IFillableItem;
import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public abstract class JetpackFueledBase extends JetpackBase implements IFillableItem {

    public Supplier<FluidTypeHBM> fuel;
    public int maxFuel;

    public JetpackFueledBase(ArmorMaterial material, Type type, Properties properties, Supplier<FluidTypeHBM> fuel, int maxFuel) {
        super(material, type, properties);
        this.fuel = fuel;
        this.maxFuel = maxFuel;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(fuel.get().getLocalizedName() + ": " + getFuel(stack) + "mB / " + this.maxFuel + "mB")
                .withStyle(ChatFormatting.LIGHT_PURPLE));
        tooltip.add(Component.empty());
        super.appendHoverText(stack, level, tooltip, flag);
    }

    protected void useUpFuel(Player player, ItemStack stack, int rate) {
        if (player.tickCount % rate == 0) {
            setFuel(stack, getFuel(stack) - 1);
        }
    }

    public static int getFuel(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;
        return tag.getInt("fuel");
    }

    public static void setFuel(ItemStack stack, int fuel) {
        stack.getOrCreateTag().putInt("fuel", Math.max(0, fuel));
    }

    @Override
    public boolean isFull(ItemStack stack) {
        return getFuel(stack) >= this.maxFuel;
    }

    public int getMaxFill(ItemStack stack) {
        return this.maxFuel;
    }

    public int getLoadSpeed(ItemStack stack) {
        return 10;
    }

    @Override
    public boolean acceptsFluid(FluidTypeHBM type, ItemStack stack) {
        return type == this.fuel.get();
    }

    @Override
    public int tryFill(FluidTypeHBM type, int amount, ItemStack stack) {
        if (!acceptsFluid(type, stack)) return amount;

        int fill = getFuel(stack);
        int req = maxFuel - fill;

        int toFill = Math.min(amount, req);
        setFuel(stack, fill + toFill);

        return amount - toFill;
    }

    @Override
    public boolean providesFluid(FluidTypeHBM type, ItemStack stack) {
        return false;
    }

    @Override
    public int tryEmpty(FluidTypeHBM type, int amount, ItemStack stack) {
        return 0;
    }

    @Override
    public FluidTypeHBM getFirstFluidType(ItemStack stack) {
        return this.fuel.get();
    }

    @Override
    public int getFill(ItemStack stack) {
        return getFuel(stack);
    }
}