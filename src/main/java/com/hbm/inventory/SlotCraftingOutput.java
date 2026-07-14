package com.hbm.inventory;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Слот для вывода крафта, работает с IItemHandler
 */
public class SlotCraftingOutput extends SlotItemHandler {
    private final Player player;

    public SlotCraftingOutput(Player player, IItemHandler itemHandler, int index, int x, int y) {
        super(itemHandler, index, x, y);
        this.player = player;
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return false; // В выходной слот нельзя помещать предметы вручную
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        this.checkTakeAchievements(stack);
        super.onTake(player, stack);
    }

    @Override
    protected void onQuickCraft(@NotNull ItemStack stack, int amount) {
        // Логика для достижений/статистики может быть добавлена здесь
    }

    protected void checkTakeAchievements(@NotNull ItemStack stack) {
        if (this.player != null) {
            stack.onCraftedBy(this.player.level(), this.player, stack.getCount());
            // Дополнительная логика достижений может быть добавлена здесь
        }
    }
}