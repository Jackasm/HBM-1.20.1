package com.hbm.items.tool;


import com.hbm.inventory.gui.GUIScreenBobmazon;
import com.hbm.items.BobmazonOfferFactory;
import com.hbm.items.ModItems;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemCatalog extends Item {

    public ItemCatalog(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            List<GUIScreenBobmazon.Offer> offers = BobmazonOfferFactory.getOffers(stack);
            if (offers != null) {
                Minecraft.getInstance().setScreen(new GUIScreenBobmazon(player, offers));
            }
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (this == ModItems.BOBMAZON_HIDDEN.get()) {
            tooltip.add(Component.literal("For a guide on how to obtain this, visit https://bit.ly/2TPgcqT"));
            tooltip.add(Component.literal("No tricks this time, i promise."));
        }
    }
}