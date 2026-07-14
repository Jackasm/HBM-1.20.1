package com.hbm.items.armor;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.handler.ArmorModHandler;
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
import java.util.Objects;

public class ArmorFSBPowered extends ArmorFSB implements IBatteryItem {

    public long maxPower;
    public long chargeRate;
    public long consumption;
    public long drain;

    public ArmorFSBPowered(ArmorMaterial material, Type type, Properties properties,
                           long maxPower, long chargeRate, long consumption, long drain) {
        super(material, type, properties);
        this.maxPower = maxPower;
        this.chargeRate = chargeRate;
        this.consumption = consumption;
        this.drain = drain;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Charge: " + BobMathUtil.getShortNumber(getCharge(stack)) + " / " +
                        BobMathUtil.getShortNumber(getMaxCharge(stack)))
                .withStyle(ChatFormatting.GRAY));
        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isArmorEnabled(ItemStack stack) {
        return getCharge(stack) > 0;
    }

    @Override
    public void chargeBattery(ItemStack stack, long i) {
        if (stack.getItem() instanceof ArmorFSBPowered) {
            long charge = getCharge(stack) + i;
            if (charge > getMaxCharge(stack)) {
                charge = getMaxCharge(stack);
            }
            setCharge(stack, charge);
        }
    }

    @Override
    public void setCharge(ItemStack stack, long i) {
        if (stack.getItem() instanceof ArmorFSBPowered) {
            stack.getOrCreateTag().putLong("charge", Math.min(i, getMaxCharge(stack)));
        }
    }

    @Override
    public void dischargeBattery(ItemStack stack, long i) {
        if (stack.getItem() instanceof ArmorFSBPowered) {
            long charge = getCharge(stack) - i;
            if (charge < 0) charge = 0;
            setCharge(stack, charge);
        }
    }

    @Override
    public long getCharge(ItemStack stack) {
        if (stack.getItem() instanceof ArmorFSBPowered) {
            if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("charge")) {
                return Math.min(stack.getTag().getLong("charge"), getMaxCharge(stack));
            } else {
                long max = getMaxCharge(stack);
                setCharge(stack, max);
                return max;
            }
        }
        return 0;
    }

    @Override
    public long getMaxCharge(ItemStack stack) {
        if (ArmorModHandler.hasMods(stack)) {
            ItemStack mod = ArmorModHandler.pryMod(stack, ArmorModHandler.BATTERY);
            if (!mod.isEmpty() && mod.getItem() instanceof ItemModBattery battery) {
                return (long) (maxPower * battery.mod);
            }
        }
        return maxPower;
    }

    @Override
    public long getChargeRate() {
        return chargeRate;
    }

    @Override
    public long getDischargeRate() {
        return 0;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        this.dischargeBattery(stack, damage * consumption);
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        // Проверяем, что предмет находится в слоте брони
        boolean isInArmorSlot = slotIndex >= 36 && slotIndex <= 39;

        if (isInArmorSlot) {
            // Ваша логика здесь
            if (this.drain > 0 && ArmorFSB.hasFSBArmor(player) && !player.getAbilities().instabuild) {
                this.dischargeBattery(stack, drain);
            }
        }

        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }
}