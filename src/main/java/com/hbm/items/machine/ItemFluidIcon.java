package com.hbm.items.machine;

import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.ModItems;
import com.hbm.util.BobMathUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemFluidIcon extends Item {

    public ItemFluidIcon(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag != null) {
            if (getQuantity(stack) > 0) {
                tooltip.add(Component.literal(getQuantity(stack) + "mB"));
            }
            if (getPressure(stack) > 0) {
                tooltip.add(Component.literal("" + getPressure(stack) + "PU").withStyle(ChatFormatting.RED));
                tooltip.add(Component.literal("Pressurized, use compressor!")
                        .withStyle(BobMathUtil.getBlink() ? ChatFormatting.RED : ChatFormatting.DARK_RED));
            }
        }

        // Добавляем информацию о жидкости
        FluidTypeHBM fluid = Fluids.fromID(stack.getDamageValue());
        if (fluid != null && fluid != Fluids.NONE.get()) {
            String info = fluid.getLocalizedName();
            tooltip.add(Component.literal(info));
        }
    }

    public static ItemStack addQuantity(ItemStack stack, int i) {
        stack.getOrCreateTag().putInt("fill", i);
        return stack;
    }

    public static ItemStack addPressure(ItemStack stack, int i) {
        stack.getOrCreateTag().putInt("pressure", i);
        return stack;
    }

    public static ItemStack make(FluidStackHBM stack) {
        return make(stack.type, stack.fill, stack.pressure);
    }

    public static ItemStack make(FluidTypeHBM fluid, int i) {
        return make(fluid, i, 0);
    }

    public static ItemStack make(FluidTypeHBM fluid, int i, int pressure) {
        ItemStack stack = new ItemStack(ModItems.FLUID_ICON.get(), 1);
        stack.setDamageValue(Fluids.getID(fluid));
        return addPressure(addQuantity(stack, i), pressure);
    }

    public static int getQuantity(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;
        return tag.getInt("fill");
    }

    public static int getPressure(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return 0;
        return tag.getInt("pressure");
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack stack) {
        FluidTypeHBM fluid = Fluids.fromID(stack.getDamageValue());
        if (fluid != null && fluid != Fluids.NONE.get()) {
            return fluid.getLocalizedName();
        }
        return super.getDescriptionId(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBarColor(@NotNull ItemStack stack) {
        int color = Fluids.fromID(stack.getDamageValue()).getColor();
        return color >= 0 ? color : 0xFFFFFF;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        // Можно показывать заполненность, если нужно
        return 13;
    }
}