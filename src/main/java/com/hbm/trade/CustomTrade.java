package com.hbm.trade;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.entity.npc.VillagerTrades.ItemListing;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class CustomTrade implements ItemListing {
    private final ItemStack price;
    private final ItemStack result;
    private final int maxUses;
    private final int xp;
    private final float priceMultiplier;

    public CustomTrade(ItemStack price, ItemStack result, int maxUses, int xp, float priceMultiplier) {
        this.price = price;
        this.result = result;
        this.maxUses = maxUses;
        this.xp = xp;
        this.priceMultiplier = priceMultiplier;
    }

    @Nullable
    @Override
    public MerchantOffer getOffer(@NotNull Entity trader, @NotNull RandomSource random) {
        return new MerchantOffer(this.price, this.result, this.maxUses, this.xp, this.priceMultiplier);
    }
}