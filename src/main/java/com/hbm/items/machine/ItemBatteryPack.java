package com.hbm.items.machine;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.render.item.BatteryPackRenderer;
import com.hbm.util.BobMathUtil;
import com.hbm.util.RefStrings;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Consumer;

import static com.hbm.util.ResLocation.ResLocation;

public class ItemBatteryPack extends Item implements IBatteryItem {

    public final EnumBatteryPack type;

    public ItemBatteryPack(EnumBatteryPack type, Properties properties) {
        super(properties.stacksTo(1));
        this.type = type;
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(BatteryPackRenderer.clientExtensions());
    }

    /* ==================== IBatteryItem ==================== */

    @Override
    public void chargeBattery(ItemStack stack, long amount) {
        setCharge(stack, getCharge(stack) + amount);
    }

    @Override
    public void setCharge(ItemStack stack, long amount) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong("charge", amount);
    }

    @Override
    public void dischargeBattery(ItemStack stack, long amount) {
        setCharge(stack, Math.max(0, getCharge(stack) - amount));
    }

    @Override
    public long getCharge(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("charge")) return 0L;
        return tag.getLong("charge");
    }

    @Override
    public long getMaxCharge(ItemStack stack) {
        return type.capacity;
    }

    @Override
    public long getChargeRate() {
        return type.chargeRate;
    }

    @Override
    public long getDischargeRate() {
        return type.dischargeRate;
    }

    /* ==================== Отображение ==================== */

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getCharge(stack) < getMaxCharge(stack);
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        long max = getMaxCharge(stack);
        if (max <= 0) return 0;
        double fraction = (double) getCharge(stack) / (double) max;
        return (int) Math.round(fraction * 13D);
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        return 0x00FF00; // зелёный
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        long maxCharge = getMaxCharge(stack);
        long chargeRate = getChargeRate();
        long dischargeRate = getDischargeRate();
        long charge = getCharge(stack);

        double percent = maxCharge > 0 ? (charge * 1000L / maxCharge / 10D) : 0D;

        tooltip.add(Component.literal(ChatFormatting.GREEN + "Energy stored: "
                + BobMathUtil.getShortNumber(charge) + "/" + BobMathUtil.getShortNumber(maxCharge)
                + "HE (" + percent + "%)"));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Charge rate: "
                + BobMathUtil.getShortNumber(chargeRate) + "HE/t"));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Discharge rate: "
                + BobMathUtil.getShortNumber(dischargeRate) + "HE/t"));
        tooltip.add(Component.literal(ChatFormatting.GOLD + "Time for full charge: "
                + (maxCharge / (double) chargeRate / 20D / 60D) + "min"));
        tooltip.add(Component.literal(ChatFormatting.GOLD + "Charge lasts for: "
                + (maxCharge / (double) dischargeRate / 20D / 60D) + "min"));
    }

    /* ==================== Утилиты ==================== */

    public static ItemStack makeEmptyBattery(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong("charge", 0L);
        return stack;
    }

    public static ItemStack makeFullBattery(ItemStack stack) {
        if (stack.getItem() instanceof IBatteryItem battery) {
            battery.setCharge(stack, battery.getMaxCharge(stack));
        }
        return stack;
    }

    /* ==================== Enum ==================== */

    public enum EnumBatteryPack {
        BATTERY_REDSTONE   ("battery_pack_battery_redstone",       100L, false),
        BATTERY_LEAD       ("battery_pack_battery_lead",         1_000L, false),
        BATTERY_LITHIUM    ("battery_pack_battery_lithium",     10_000L, false),
        BATTERY_SODIUM     ("battery_pack_battery_sodium",      50_000L, false),
        BATTERY_SCHRABIDIUM("battery_pack_battery_schrabidium",250_000L, false),
        BATTERY_QUANTUM    ("battery_pack_battery_quantum",  1_000_000L, 20 * 60 * 60),

        CAPACITOR_COPPER   ("battery_pack_capacitor_copper",     1_000L, true),
        CAPACITOR_GOLD     ("battery_pack_capacitor_gold",      10_000L, true),
        CAPACITOR_NIOBIUM  ("battery_pack_capacitor_niobium",  100_000L, true),
        CAPACITOR_TANTALUM ("battery_pack_capacitor_tantalum", 500_000L, true),
        CAPACITOR_BISMUTH  ("battery_pack_capacitor_bismuth", 2_500_000L, true),
        CAPACITOR_SPARK    ("battery_pack_capacitor_spark",  10_000_000L, true);

        public final String textureName;
        public final long capacity;
        public final long chargeRate;
        public final long dischargeRate;

        EnumBatteryPack(String tex, long dischargeRate, boolean capacitor) {
            this(tex,
                    capacitor ? (dischargeRate * 20 * 30) : (dischargeRate * 20 * 60 * 15),
                    capacitor ? dischargeRate : dischargeRate * 10,
                    dischargeRate);
        }

        EnumBatteryPack(String tex, long dischargeRate, long duration) {
            this(tex, dischargeRate * duration, dischargeRate * 10, dischargeRate);
        }

        EnumBatteryPack(String tex, long capacity, long chargeRate, long dischargeRate) {
            this.textureName = tex;
            this.capacity = capacity;
            this.chargeRate = chargeRate;
            this.dischargeRate = dischargeRate;
        }

        public boolean isCapacitor() {
            return this.ordinal() > BATTERY_QUANTUM.ordinal();
        }

        /** Текстура для OBJ-рендера. Лежит в assets/hbm/textures/item/ */
        public ResourceLocation getTexture() {
            return ResLocation(RefStrings.MODID, "textures/item/" + textureName + ".png");
        }
    }
}