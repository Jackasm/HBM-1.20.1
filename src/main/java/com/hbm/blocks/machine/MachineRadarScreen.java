package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.machine.TileEntityMachineRadarNT;
import com.hbm.tileentity.machine.TileEntityMachineRadarScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MachineRadarScreen extends BlockDummyable {

    public MachineRadarScreen(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityMachineRadarScreen(pos, state);
        }
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{1, 0, 0, 0, 1, 0};
    }

    @Override
    public int getZOffset() {
        return 0;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos corePos = findCore(level, pos);
        if (corePos == null) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(corePos);
        if (te instanceof TileEntityMachineRadarScreen screen) {
            if (screen.linked) {
                BlockEntity radarTe = level.getBlockEntity(new BlockPos(screen.refX, screen.refY, screen.refZ));
                if (radarTe instanceof TileEntityMachineRadarNT) {
                    if (!player.isShiftKeyDown()) {
                        // Открываем GUI радара
                        NetworkHooks.openScreen((ServerPlayer) player, (TileEntityMachineRadarNT) radarTe,
                                new BlockPos(screen.refX, screen.refY, screen.refZ));
                        return InteractionResult.CONSUME;
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }

    public static Properties createProperties() {
        return Properties.of()
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }
}