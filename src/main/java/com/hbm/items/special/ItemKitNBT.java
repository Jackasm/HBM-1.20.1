package com.hbm.items.special;

import com.hbm.items.ModItems;
import com.hbm.sound.ModSounds;
import com.hbm.util.ItemStackUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemHandlerHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemKitNBT extends Item {

    public ItemKitNBT(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!level.isClientSide) {
            ItemStack[] stacks = ItemStackUtil.readStacksFromNBT(stack);

            if (stacks != null) {
                for (ItemStack item : stacks) {
                    if (item != null && !item.isEmpty()) {
                        ItemHandlerHelper.giveItemToPlayer(player, item.copy());
                    }
                }
            }

            ItemStack container = stack.getItem().getCraftingRemainingItem(stack);

            stack.shrink(1);

            if (container != null && !container.isEmpty()) {
                if (stack.getCount() > 0) {
                    ItemHandlerHelper.giveItemToPlayer(player, container.copy());
                } else {
                    stack = container.copy();
                }
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.UNPACK.get(),
                    SoundSource.PLAYERS, 1.0F, 1.0F);
        }

        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        ItemStack[] stacks = ItemStackUtil.readStacksFromNBT(stack);

        if (stacks != null) {
            tooltip.add(Component.literal("Contains:"));

            for (ItemStack item : stacks) {
                if (item != null && !item.isEmpty()) {
                    String line = "- " + item.getHoverName().getString();
                    if (item.getCount() > 1) {
                        line += " x" + item.getCount();
                    }
                    tooltip.add(Component.literal(line));
                }
            }
        }
    }

    public static ItemStack create(ItemStack... contents) {
        ItemStack stack = new ItemStack(ModItems.LEGACY_TOOLBOX.get());
        stack.setTag(new net.minecraft.nbt.CompoundTag());
        ItemStackUtil.addStacksToNBT(stack, contents);
        return stack;
    }
}