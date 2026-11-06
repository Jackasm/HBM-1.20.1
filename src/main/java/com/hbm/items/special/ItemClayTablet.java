package com.hbm.items.special;

import com.hbm.inventory.container.ContainerClayTablet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemClayTablet extends Item {

    public ItemClayTablet(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide && !stack.hasTag()) {
            CompoundTag tag = new CompoundTag();
            tag.putLong("tabletSeed", level.random.nextLong());
            stack.setTag(tag);
        }

        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            serverPlayer.openMenu(new SimpleMenuProvider(
                    (windowId, inv, p) -> new ContainerClayTablet(windowId, inv),
                    Component.translatable("item.hbm.clay_tablet")
            ));
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}