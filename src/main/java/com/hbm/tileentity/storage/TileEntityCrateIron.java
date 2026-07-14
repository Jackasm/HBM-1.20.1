package com.hbm.tileentity.storage;

import com.hbm.inventory.container.ContainerCrateIron;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityCrateIron extends TileEntityCrateBase {

    public TileEntityCrateIron(BlockPos pos, BlockState state) {
        super(ModTileEntity.CRATE_IRON.get(), pos, state, 36);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.crateIron");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerCrateIron(id, inv, this);
    }
}