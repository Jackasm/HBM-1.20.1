package com.hbm.tileentity.storage;

import com.hbm.inventory.container.ContainerCrateTemplate;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityCrateTemplate extends TileEntityCrateBase {

    public TileEntityCrateTemplate(BlockPos pos, BlockState state) {
        super(ModTileEntity.CRATE_TEMPLATE.get(), pos, state, 27);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.crateTemplate");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerCrateTemplate(id, inv, this);
    }
}