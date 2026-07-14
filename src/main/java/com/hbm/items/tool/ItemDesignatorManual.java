package com.hbm.items.tool;

import com.hbm.api.item.IDesignatorItem;

import com.hbm.inventory.gui.GUIScreenDesignator;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemDesignatorManual extends Item implements IDesignatorItem, MenuProvider {

    public ItemDesignatorManual(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            Minecraft.getInstance().setScreen(new GUIScreenDesignator(player));
        }

        return InteractionResultHolder.success(stack);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (stack.getTag() != null) {
            tooltip.add(Component.literal("Target Coordinates:"));
            tooltip.add(Component.literal("X: " + stack.getTag().getInt("xCoord")));
            tooltip.add(Component.literal("Z: " + stack.getTag().getInt("zCoord")));
        } else {
            tooltip.add(Component.literal("Please select a target."));
        }
    }

    public boolean isReady(Level level, ItemStack stack, int x, int y, int z) {
        return stack.hasTag();
    }

    public Vec3 getCoords(Level level, ItemStack stack, int x, int y, int z) {
        return new Vec3(Objects.requireNonNull(stack.getTag()).getInt("xCoord"), 0, stack.getTag().getInt("zCoord"));
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("item.hbm.designator_manual");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, net.minecraft.world.entity.player.@NotNull Inventory inv, @NotNull Player player) {
        return null;
    }

}