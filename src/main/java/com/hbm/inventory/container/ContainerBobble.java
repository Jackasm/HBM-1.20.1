package com.hbm.inventory.container;

import com.hbm.tileentity.deco.TileEntityBobble;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerBobble extends AbstractContainerMenu {

    public final TileEntityBobble bobble;

    public ContainerBobble(int windowId, Inventory playerInv, TileEntityBobble bobble) {
        super(ModContainers.BOBBLEHEAD.get(), windowId);
        this.bobble = bobble;
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