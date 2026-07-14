package com.hbm.tileentity.storage;

import com.hbm.inventory.container.ContainerCrateSteel;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityCrateSteel extends TileEntityCrateBase {

    public TileEntityCrateSteel(BlockPos pos, BlockState state) {
        super(ModTileEntity.CRATE_STEEL.get(), pos, state, 54);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.crateSteel");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerCrateSteel(id, inv, this);
    }
}