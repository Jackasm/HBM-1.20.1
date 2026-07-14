package com.hbm.items.machine;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemSelfcharger extends Item implements IBatteryItem {

    private final long charge;

    public ItemSelfcharger(long charge, Properties properties) {
        super(properties);
        this.charge = charge;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(BobMathUtil.getShortNumber(charge) + "HE/t").withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public void chargeBattery(ItemStack stack, long i) { }

    @Override
    public void setCharge(ItemStack stack, long i) { }

    @Override
    public void dischargeBattery(ItemStack stack, long i) { }

    @Override
    public long getCharge(ItemStack stack) {
        return charge;
    }

    @Override
    public long getMaxCharge(ItemStack stack) {
        return charge;
    }

    @Override
    public long getChargeRate() {
        return 0;
    }

    @Override
    public long getDischargeRate() {
        return charge;
    }
}