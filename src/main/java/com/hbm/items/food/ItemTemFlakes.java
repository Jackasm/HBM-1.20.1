package com.hbm.items.food;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemTemFlakes extends Item {

    private final int tier;

    public ItemTemFlakes(Properties properties, int tier) {
        super(properties);
        this.tier = tier;
    }

    @Override
    public @NotNull ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level level, @NotNull LivingEntity living) {
        if (living instanceof Player player) {
            player.heal(2F);
        }
        return super.finishUsingItem(stack, level, living);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        switch (tier) {
            case 0 -> tooltip.add(Component.literal("Heals 2HP DISCOUNT FOOD OF TEM!!!").withStyle(ChatFormatting.GOLD));
            case 1 -> tooltip.add(Component.literal("Heals 2HP food of tem"));
            case 2 -> tooltip.add(Component.literal("Heals food of tem (expensiv)"));
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean isFoil(@NotNull ItemStack stack) {
        return tier == 2;
    }
}