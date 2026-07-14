package com.hbm.tileentity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

//TODO Убрать это
@Deprecated
public interface IGUIProvider {

    public AbstractContainerMenu provideContainer(int ID, Player player, Level world, BlockHitResult hitResult);
    public Object provideGUI(int ID, Player player, Level world, BlockHitResult hitResult);

    // Для обратной совместимости (если старый код всё ещё использует x,y,z)
    default public AbstractContainerMenu provideContainer(int ID, Player player, Level world, int x, int y, int z) {
        return provideContainer(ID, player, world, null);
    }

    default public Object provideGUI(int ID, Player player, Level world, int x, int y, int z) {
        return provideGUI(ID, player, world, null);
    }
}