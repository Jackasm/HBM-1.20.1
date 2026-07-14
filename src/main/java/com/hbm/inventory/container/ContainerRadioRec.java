package com.hbm.inventory.container;

import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.machine.TileEntityRadioRec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ContainerRadioRec extends AbstractContainerMenu {

    private final TileEntityRadioRec radio;
    private final ContainerData data;

    public ContainerRadioRec(int id, Inventory playerInv, TileEntityRadioRec radio) {
        super(ModContainers.RADIOREC.get(), id);
        this.radio = radio;
        this.data = new SimpleContainerData(0);
        this.addDataSlots(data);
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int slot) {
        return ItemStack.EMPTY;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(ContainerLevelAccess.create(radio.getLevel(), radio.getBlockPos()), player, ModBlocks.RADIOREC.get());
    }

    public TileEntityRadioRec getRadio() {
        return radio;
    }
}