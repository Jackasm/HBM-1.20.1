package com.hbm.inventory.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerBookLore extends AbstractContainerMenu {

    private final ItemStack book;

    /** Клиентский конструктор — данные приходят через FriendlyByteBuf. */
    public ContainerBookLore(int id, Inventory inv, FriendlyByteBuf data) {
        super(ModContainers.BOOK_LORE.get(), id);
        this.book = data.readItem();
    }

    /** Серверный конструктор. */
    public ContainerBookLore(int id, Inventory inv, ItemStack book) {
        super(ModContainers.BOOK_LORE.get(), id);
        this.book = book.copy();
    }

    public ItemStack getBook() {
        return book;
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