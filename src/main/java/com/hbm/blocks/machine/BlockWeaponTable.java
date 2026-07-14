package com.hbm.blocks.machine;

import com.hbm.inventory.container.ContainerWeaponTable;
import com.hbm.inventory.gui.GUIWeaponTable;
import com.hbm.tileentity.IGUIProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class BlockWeaponTable extends Block implements IGUIProvider {

    public BlockWeaponTable(Properties properties) {
        super(properties);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .requiresCorrectToolForDrops();
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        if (!player.isShiftKeyDown()) {
            NetworkHooks.openScreen((ServerPlayer) player, new MenuProvider() {
                @Override
                public @NotNull Component getDisplayName() {
                    return Component.translatable(getDescriptionId());
                }

                @Override
                public @NotNull AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
                    return new ContainerWeaponTable(windowId, inv);
                }
            }, pos);
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    @Override
    public AbstractContainerMenu provideContainer(int ID, Player player, Level world, BlockHitResult hitResult) {
        return new ContainerWeaponTable(ID, player.getInventory());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public Object provideGUI(int ID, Player player, Level world, BlockHitResult hitResult) {
        return new GUIWeaponTable(new ContainerWeaponTable(ID, player.getInventory()), player.getInventory(), Component.translatable("container.weaponTable"));
    }
}