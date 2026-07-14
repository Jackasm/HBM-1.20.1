package com.hbm.items.armor;

import com.hbm.api.fluid.IFillableItem;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class ArmorFSBFueled extends ArmorFSB implements IFillableItem {

    protected Supplier<FluidTypeHBM> fuelTypeSupplier;
    public int maxFuel = 1;
    public int fillRate;
    public int consumption;
    public int drain;

    public ArmorFSBFueled(ArmorMaterial material, Type type, Properties properties,
                          Supplier<FluidTypeHBM> fuelType, int maxFuel, int fillRate, int consumption, int drain) {
        super(material, type, properties);
        this.fuelTypeSupplier = fuelType;
        this.fillRate = fillRate;
        this.consumption = consumption;
        this.drain = drain;
        this.maxFuel = maxFuel;
    }

    public FluidTypeHBM getFuelType() {
        return fuelTypeSupplier.get();
    }

    @Override
    public int getFill(ItemStack stack) {
        if (!stack.hasTag()) {
            setFill(stack, maxFuel);
            return maxFuel;
        }
        return stack.getOrCreateTag().getInt("fuel");
    }

    @Override
    public boolean isFull(ItemStack stack) {
        return getFill(stack) >= this.maxFuel;
    }

    public void setFill(ItemStack stack, int fill) {
        stack.getOrCreateTag().putInt("fuel", Math.min(fill, maxFuel));
    }

    public int getMaxFill(ItemStack stack) {
        return this.maxFuel;
    }

    public int getLoadSpeed(ItemStack stack) {
        return this.fillRate;
    }

    public int getUnloadSpeed(ItemStack stack) {
        return 0;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        this.setFill(stack, Math.max(this.getFill(stack) - (damage * consumption), 0));
    }

    @Override
    public boolean isArmorEnabled(ItemStack stack) {
        return getFill(stack) > 0;
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);

        // Проверяем, что предмет находится в слоте брони (36-39)
        boolean isInArmorSlot = slotIndex >= 36 && slotIndex <= 39;

        if (isInArmorSlot && this.drain > 0 && ArmorFSB.hasFSBArmor(player) && !player.getAbilities().instabuild && level.getGameTime() % 10 == 0) {
            this.setFill(stack, Math.max(this.getFill(stack) - this.drain, 0));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(this.getFuelType().getLocalizedName() + ": " +
                        BobMathUtil.getShortNumber(getFill(stack)) + " / " +
                        BobMathUtil.getShortNumber(getMaxFill(stack)))
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }


    @Override
    public boolean acceptsFluid(FluidTypeHBM type, ItemStack stack) {
        return type == this.getFuelType();
    }

    @Override
    public int tryFill(FluidTypeHBM type, int amount, ItemStack stack) {
        if (!acceptsFluid(type, stack)) {
            return amount;
        }

        int toFill = Math.min(amount, this.fillRate);
        toFill = Math.min(toFill, this.maxFuel - this.getFill(stack));
        this.setFill(stack, this.getFill(stack) + toFill);

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
        return null;
    }
}