package com.hbm.items.tool;

import com.hbm.handler.ability.IWeaponAbility;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemSwordAbilityPower extends ItemSwordAbility {

    private final long maxPower;
    private final long chargeRate;
    private final long consumption;

    public ItemSwordAbilityPower(Properties properties, Tier tier, float attackDamage, float attackSpeed,
                                 long maxPower, long chargeRate, long consumption) {
        super(properties, tier, attackDamage, attackSpeed);
        this.maxPower = maxPower;
        this.chargeRate = chargeRate;
        this.consumption = consumption;
    }

    public ItemSwordAbilityPower addWeaponAbility(IWeaponAbility ability, int level) {
        this.availableAbilities.addAbility(ability, level);
        return this;
    }

    public void chargeBattery(ItemStack stack, long amount) {
        setCharge(stack, getCharge(stack) + amount);
    }

    public void setCharge(ItemStack stack, long amount) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putLong("charge", Math.min(amount, maxPower));
    }

    public void dischargeBattery(ItemStack stack, long amount) {
        long charge = getCharge(stack) - amount;
        setCharge(stack, Math.max(charge, 0));
    }

    public long getCharge(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("charge")) {
            setCharge(stack, maxPower);
            return maxPower;
        }
        return tag.getLong("charge");
    }

    public long getMaxCharge(ItemStack stack) {
        return maxPower;
    }

    public long getChargeRate() {
        return chargeRate;
    }

    public long getDischargeRate() {
        return 0;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getCharge(stack) < maxPower;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float charge = (float) getCharge(stack) / (float) maxPower;
        return Math.round(charge * 0xFF) << 16 | Math.round((1 - charge) * 0xFF) << 8;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round((float) getCharge(stack) / (float) maxPower * 13F);
    }

    @Override
    public boolean hurtEnemy(@NotNull ItemStack stack, @NotNull LivingEntity target, @NotNull LivingEntity attacker) {
        if (!attacker.level().isClientSide && attacker instanceof Player && canOperate(stack)) {
            dischargeBattery(stack, consumption);
            availableAbilities.getWeaponAbilities().forEach((ability, level) ->
                    ability.onHit(level, attacker.level(), (Player) attacker, target, this));
        }
        return super.hurtEnemy(stack, target, attacker);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        dischargeBattery(stack, (long) damage * consumption);
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        return 1;
    }

    @Override
    protected boolean canOperate(ItemStack stack) {
        return getCharge(stack) >= consumption;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Charge: " + BobMathUtil.getShortNumber(getCharge(stack)) + " / " +
                BobMathUtil.getShortNumber(maxPower)).withStyle(ChatFormatting.GOLD));
        super.appendHoverText(stack, level, tooltip, flag);
    }
}