package com.hbm.tileentity.storage;

import com.hbm.inventory.container.ContainerSafe;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntitySafe extends TileEntityCrateBase {

    public TileEntitySafe(BlockPos pos, BlockState state) {
        super(ModTileEntity.SAFE.get(), pos, state, 15);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.safe");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerSafe(id, inv, this);
    }
}