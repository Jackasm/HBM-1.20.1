package com.hbm.inventory.container;

import com.hbm.items.tool.ItemGuideBook;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerGuideBook extends AbstractContainerMenu {

    private final ItemGuideBook.BookType type;

    public ContainerGuideBook(int windowId, Inventory inv, ItemGuideBook.BookType type) {
        super(ModContainers.GUIDE_BOOK.get(), windowId);
        this.type = type;
    }

    /** Какой вариант книги открыт. Тип приходит с сервера, а не угадывается по руке. */
    public ItemGuideBook.BookType getBookType() {
        return this.type;
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return true;
    }
}
