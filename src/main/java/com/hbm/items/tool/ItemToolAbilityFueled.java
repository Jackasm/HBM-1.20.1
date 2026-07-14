package com.hbm.items.tool;

import com.hbm.api.fluid.IFillableItem;
import com.hbm.handler.ability.AvailableAbilities;
import com.hbm.inventory.fluid.FluidTypeHBM;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Supplier;

public class ItemToolAbilityFueled extends ItemToolAbility implements IFillableItem {

    private final int maxFuel;
    private final int consumption;
    private final int fillRate;
    private final List<Supplier<FluidTypeHBM>> acceptedFuelSuppliers = new ArrayList<>();
    private Set<FluidTypeHBM> acceptedFuels = null;

    // Конструктор с Supplier (для RegistryObject) - НЕ ВЫЗЫВАЕМ supplier.get()!
    @SafeVarargs
    public ItemToolAbilityFueled(Properties properties, Tier tier, TagKey<Block> effectiveBlocks,
                                 float attackDamage, float attackSpeed,
                                 int maxFuel, int consumption, int fillRate,
                                 AvailableAbilities abilities,
                                 Supplier<FluidTypeHBM>... acceptedFuels) {
        super(attackDamage, attackSpeed, tier, effectiveBlocks, properties, abilities);
        this.maxFuel = maxFuel;
        this.consumption = consumption;
        this.fillRate = fillRate;
        Collections.addAll(this.acceptedFuelSuppliers, acceptedFuels);
    }

    // Конструктор с обычными FluidTypeHBM
    public ItemToolAbilityFueled(Properties properties, Tier tier, TagKey<Block> effectiveBlocks,
                                 float attackDamage, float attackSpeed,
                                 int maxFuel, int consumption, int fillRate,
                                 AvailableAbilities abilities,
                                 FluidTypeHBM... acceptedFuels) {
        super(attackDamage, attackSpeed, tier, effectiveBlocks, properties, abilities);
        this.maxFuel = maxFuel;
        this.consumption = consumption;
        this.fillRate = fillRate;
        this.acceptedFuels = new HashSet<>(Arrays.asList(acceptedFuels));
    }

    // Конструктор без abilities
    public ItemToolAbilityFueled(Properties properties, Tier tier, TagKey<Block> effectiveBlocks,
                                 float attackDamage, float attackSpeed,
                                 int maxFuel, int consumption, int fillRate,
                                 FluidTypeHBM... acceptedFuels) {
        this(properties, tier, effectiveBlocks, attackDamage, attackSpeed,
                maxFuel, consumption, fillRate, null, acceptedFuels);
    }

    // Конструктор без abilities с Supplier - НЕ ВЫЗЫВАЕМ supplier.get()!
    @SafeVarargs
    public ItemToolAbilityFueled(Properties properties, Tier tier, TagKey<Block> effectiveBlocks,
                                 float attackDamage, float attackSpeed,
                                 int maxFuel, int consumption, int fillRate,
                                 Supplier<FluidTypeHBM>... acceptedFuels) {
        this(properties, tier, effectiveBlocks, attackDamage, attackSpeed,
                maxFuel, consumption, fillRate, (AvailableAbilities) null, acceptedFuels);
    }

    private Set<FluidTypeHBM> getAcceptedFuels() {
        if (acceptedFuels == null) {
            acceptedFuels = new HashSet<>();
            for (Supplier<FluidTypeHBM> supplier : acceptedFuelSuppliers) {
                acceptedFuels.add(supplier.get());
            }
        }
        return acceptedFuels;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.GOLD + "Fuel: " + getFill(stack) + "/" + maxFuel + "mB"));

        for (FluidTypeHBM type : getAcceptedFuels()) {
            tooltip.add(Component.literal(ChatFormatting.YELLOW + "- " + type.getLocalizedName()));
        }

        super.appendHoverText(stack, level, tooltip, flag);
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return getFill(stack) < maxFuel;
    }

    @Override
    public int getBarColor(@NotNull ItemStack stack) {
        float fill = (float) getFill(stack) / (float) maxFuel;
        return Math.round(fill * 0xFF) << 16 | Math.round((1 - fill) * 0xFF) << 8;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return Math.round((float) getFill(stack) / (float) maxFuel * 13F);
    }

    @Override
    public boolean canOperate(ItemStack stack) {
        return getFill(stack) >= consumption;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        setFill(stack, Math.max(getFill(stack) - damage * consumption, 0));
    }

    @Override
    public boolean isDamageable(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getMaxDamage(@NotNull ItemStack stack) {
        return 1;
    }

    public int getFill(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null || !tag.contains("fuel")) {
            setFill(stack, maxFuel);
            return maxFuel;
        }
        return tag.getInt("fuel");
    }

    public void setFill(ItemStack stack, int fill) {
        CompoundTag tag = stack.getOrCreateTag();
        tag.putInt("fuel", Math.min(fill, maxFuel));
    }

    public int getMaxFill(ItemStack stack) {
        return maxFuel;
    }

    public int getLoadSpeed(ItemStack stack) {
        return fillRate;
    }

    @Override
    public boolean acceptsFluid(FluidTypeHBM type, ItemStack stack) {
        return getAcceptedFuels().contains(type);
    }

    @Override
    public int tryFill(FluidTypeHBM type, int amount, ItemStack stack) {
        if (!acceptsFluid(type, stack)) return amount;

        int toFill = Math.min(amount, fillRate);
        toFill = Math.min(toFill, maxFuel - getFill(stack));
        setFill(stack, getFill(stack) + toFill);

        return amount - toFill;
    }

    @Override
    public boolean providesFluid(FluidTypeHBM type, ItemStack stack) {
        return false;
    }

    @Override
    public int tryEmpty(FluidTypeHBM type, int amount, ItemStack stack) {
        return amount;
    }

    @Override
    public FluidTypeHBM getFirstFluidType(ItemStack stack) {
        return null;
    }

    @Override
    public boolean isFull(ItemStack stack) {
        return getFill(stack) >= maxFuel;
    }

    public static ItemStack getEmptyTool(ItemStack stack) {
        if (stack.getItem() instanceof ItemToolAbilityFueled tool) {
            ItemStack copy = stack.copy();
            tool.setFill(copy, 0);
            return copy;
        }
        return stack;
    }
}