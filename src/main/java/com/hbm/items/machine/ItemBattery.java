package com.hbm.items.machine;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.items.ModItems;
import com.hbm.util.BobMathUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBattery extends Item implements IBatteryItem {

    protected long maxCharge;
    protected long chargeRate;
    protected long dischargeRate;

    public ItemBattery(Properties properties, long maxCharge, long chargeRate, long dischargeRate) {
        super(properties);
        this.maxCharge = maxCharge;
        this.chargeRate = chargeRate;
        this.dischargeRate = dischargeRate;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        long charge = getCharge(stack);

        if (this != ModItems.FUSION_CORE.get() && this != ModItems.ENERGY_CORE.get()) {
            tooltip.add(Component.literal("Energy stored: " + BobMathUtil.getShortNumber(charge) + "/" + BobMathUtil.getShortNumber(maxCharge) + " HE"));
        } else {
            String charge1 = BobMathUtil.getShortNumber((charge * 100) / this.maxCharge);
            tooltip.add(Component.literal("Charge: " + charge1 + "%"));
            tooltip.add(Component.literal("(" + BobMathUtil.getShortNumber(charge) + "/" + BobMathUtil.getShortNumber(maxCharge) + " HE)"));
        }
        tooltip.add(Component.literal("Charge rate: " + BobMathUtil.getShortNumber(chargeRate) + " HE/t"));
        tooltip.add(Component.literal("Discharge rate: " + BobMathUtil.getShortNumber(dischargeRate) + " HE/t"));
    }

    public void chargeBattery(ItemStack stack, long amount) {
        if (stack.getItem() instanceof ItemBattery) {
            CompoundTag tag = stack.getOrCreateTag();
            long current = tag.getLong("charge");
            tag.putLong("charge", Math.min(current + amount, maxCharge));
        }
    }

    public void setCharge(ItemStack stack, long amount) {
        if (stack.getItem() instanceof ItemBattery) {
            CompoundTag tag = stack.getOrCreateTag();
            tag.putLong("charge", Math.min(amount, maxCharge));
        }
    }

    public void dischargeBattery(ItemStack stack, long amount) {
        if (stack.getItem() instanceof ItemBattery) {
            CompoundTag tag = stack.getOrCreateTag();
            long current = tag.getLong("charge");
            tag.putLong("charge", Math.max(current - amount, 0));
        }
    }

    public long getCharge(ItemStack stack) {
        if (stack.getItem() instanceof ItemBattery) {
            CompoundTag tag = stack.getOrCreateTag();
            if (tag.contains("charge")) {
                return tag.getLong("charge");
            } else {
                tag.putLong("charge", maxCharge);
                return maxCharge;
            }
        }
        return 0;
    }

    @Override
    public long getMaxCharge(ItemStack stack) {
        return maxCharge;
    }

    @Override
    public long getChargeRate() {
        return chargeRate;
    }

    @Override
    public long getDischargeRate() {
        return dischargeRate;
    }

    public static ItemStack getEmptyBattery(Item item) {
        if (item instanceof ItemBattery) {
            ItemStack stack = new ItemStack(item);
            CompoundTag tag = stack.getOrCreateTag();
            tag.putLong("charge", 0);
            return stack;
        }
        return ItemStack.EMPTY;
    }

    public static ItemStack getFullBattery(Item item) {
        if (item instanceof ItemBattery) {
            ItemStack stack = new ItemStack(item);
            CompoundTag tag = stack.getOrCreateTag();
            tag.putLong("charge", ((ItemBattery) item).maxCharge);
            return stack;
        }
        return new ItemStack(item);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        long charge = getCharge(stack);
        return Math.round(13.0F * (float) charge / (float) maxCharge);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        long charge = getCharge(stack);
        float ratio = (float) charge / (float) maxCharge;
        return java.awt.Color.HSBtoRGB(ratio * 0.333F, 1.0F, 1.0F);
    }
}