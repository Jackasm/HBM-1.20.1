package com.hbm.blocks.machine;

import com.hbm.inventory.container.ContainerArmorTable;
import com.hbm.inventory.gui.GUIArmorTable;
import com.hbm.tileentity.IGUIProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockArmorTable extends Block implements IGUIProvider {

    public BlockArmorTable(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        } else if (!player.isShiftKeyDown()) {
            player.openMenu(new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable("container.armorTable");
                }

                @Override
                public @Nullable AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inventory, @NotNull Player player) {
                    return new ContainerArmorTable(windowId, inventory);
                }
            });
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public AbstractContainerMenu provideContainer(int ID, Player player, Level world, BlockHitResult hitResult) {
        return new ContainerArmorTable(ID, player.getInventory());
    }

    @Override
    public Object provideGUI(int ID, Player player, Level world, BlockHitResult hitResult) {
        return new GUIArmorTable(new ContainerArmorTable(ID, player.getInventory()), player.getInventory(), Component.translatable("container.armorTable"));
    }
}