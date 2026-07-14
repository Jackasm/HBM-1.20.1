package com.hbm.items.machine;

import com.hbm.inventory.recipes.loader.GenericRecipe;
import com.hbm.inventory.recipes.loader.GenericRecipes;
import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemBlueprints extends Item {

    public ItemBlueprints(Properties properties) {
        super(properties);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        CompoundTag tag = stack.getTag();
        if (tag == null) return;

        String poolName = tag.getString("pool");
        List<String> pool = GenericRecipes.blueprintPools.get(poolName);

        if (pool == null || pool.isEmpty()) return;

        if (poolName.startsWith(GenericRecipes.POOL_PREFIX_SECRET)) {
            tooltip.add(Component.literal("Cannot be copied!").withStyle(ChatFormatting.RED));
        } else {
            tooltip.add(Component.literal("Right-click to copy (requires paper)").withStyle(ChatFormatting.YELLOW));
        }

        for (String name : pool) {
            GenericRecipe recipe = GenericRecipes.pooledBlueprints.get(name);
            if (recipe != null) {
                tooltip.add(Component.literal(recipe.getLocalizedName()));
            }
        }
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (level.isClientSide) {
            return InteractionResultHolder.pass(stack);
        }

        CompoundTag tag = stack.getTag();
        if (tag == null) {
            return InteractionResultHolder.pass(stack);
        }

        String poolName = tag.getString("pool");

        if (poolName.startsWith(GenericRecipes.POOL_PREFIX_SECRET)) {
            return InteractionResultHolder.pass(stack);
        }

        // Проверяем наличие бумаги в инвентаре
        boolean hasPaper = false;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.getItem() == net.minecraft.world.item.Items.PAPER) {
                hasPaper = true;
                break;
            }
        }

        if (!hasPaper) {
            return InteractionResultHolder.pass(stack);
        }

        // Удаляем одну бумагу
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack invStack = player.getInventory().getItem(i);
            if (invStack.getItem() == net.minecraft.world.item.Items.PAPER) {
                invStack.shrink(1);
                break;
            }
        }

        player.swing(hand);

        ItemStack copy = stack.copy();
        copy.setCount(1);

        if (!player.getAbilities().instabuild) {
            if (stack.getCount() < stack.getMaxStackSize()) {
                stack.grow(1);
                return InteractionResultHolder.success(stack);
            }

            if (!player.getInventory().add(copy)) {
                copy = stack.copy();
                copy.setCount(1);
                player.drop(copy, false);
            }

            player.containerMenu.broadcastChanges();
        } else {
            player.drop(copy, false);
        }

        return InteractionResultHolder.success(stack);
    }

    public static String grabPool(ItemStack stack) {
        if (stack == null) return null;
        if (stack.getItem() != ModItems.BLUEPRINTS.get()) return null;
        CompoundTag tag = stack.getTag();
        if (tag == null) return null;
        if (!tag.contains("pool")) return null;
        return tag.getString("pool");
    }

    public static ItemStack make(String pool) {
        ItemStack stack = new ItemStack(ModItems.BLUEPRINTS.get());
        CompoundTag tag = new CompoundTag();
        tag.putString("pool", pool);
        stack.setTag(tag);
        return stack;
    }
}