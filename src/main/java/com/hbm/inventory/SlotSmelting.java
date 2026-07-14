package com.hbm.inventory;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class SlotSmelting extends SlotItemHandler {

    private final Player player;
    private int itemCountBuffer;

    public SlotSmelting(Player player, IItemHandler itemHandler, int index, int x, int y) {
        super(itemHandler, index, x, y);
        this.player = player;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false; // Can't put items into output slots
    }

    @Override
    @NotNull
    public ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.itemCountBuffer += Math.min(amount, this.getItem().getCount());
        }
        return super.remove(amount);
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        this.onCrafting(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void onQuickCraft(@NotNull ItemStack stack, int amount) {
        this.itemCountBuffer += amount;
        this.onCrafting(stack);
    }

    protected void onCrafting(ItemStack stack) {
        stack.onCraftedBy(player.level(), player, this.itemCountBuffer);

        Level level = player.level();

        if (!level.isClientSide) {
            int buffer = this.itemCountBuffer;
            float exp = getSmeltingExperience(stack);

            if (exp == 0.0F) {
                buffer = 0;
            } else if (exp < 1.0F) {
                int remainingExp = Mth.floor((float) buffer * exp);

                if (remainingExp < Mth.ceil((float) buffer * exp) && Math.random() < (float) buffer * exp - (float) remainingExp) {
                    ++remainingExp;
                }

                buffer = remainingExp;
            }

            while (buffer > 0) {
                int remainingExp = ExperienceOrb.getExperienceValue(buffer);
                buffer -= remainingExp;
                level.addFreshEntity(new ExperienceOrb(level,
                        player.getX(), player.getY() + 0.5D, player.getZ() + 0.5D,
                        remainingExp));
            }
        }

        this.itemCountBuffer = 0;

        // Fire smelted event (Forge)
        net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.player.PlayerEvent.ItemSmeltedEvent(player, stack));

        // Vanilla achievements/advancements
        if (stack.getItem() == Items.IRON_INGOT) {
            // In 1.20.1, achievements are replaced with advancements
            // You can trigger an advancement here if needed
        }

        if (stack.getItem() == Items.COOKED_COD) { // cooked_fished is now cooked_cod
            // Trigger advancement if needed
        }
    }

    private float getSmeltingExperience(ItemStack stack) {
        Level level = player.level();
        if (level == null) return 0.0F;

        Collection<Recipe<?>> recipes = level.getRecipeManager().getRecipes();

        for (Recipe<?> recipe : recipes) {
            if (recipe instanceof AbstractCookingRecipe cookingRecipe) {
                ItemStack result = cookingRecipe.getResultItem(level.registryAccess());
                if (ItemStack.isSameItem(result, stack)) {
                    return cookingRecipe.getExperience();
                }
            }
        }

        return 0.0F;
    }
}