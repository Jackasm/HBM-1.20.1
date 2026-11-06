package com.hbm.blocks.network;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.network.TileEntityRadioTelex;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class RadioTelex extends BlockDummyable {

    public RadioTelex(Properties properties) {
        super(properties);
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityRadioTelex(pos, state);
        }
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[] { 0, 0, 0, 0, 1, 0 };
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
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(core);
        if (te instanceof TileEntityRadioTelex telex) {
            NetworkHooks.openScreen((ServerPlayer) player,
                    new SimpleMenuProvider(
                            (windowId, inv, p) -> new com.hbm.inventory.container.ContainerRadioTelex(windowId, inv, telex),
                            Component.translatable("container.radio_telex")
                    ),
                    core);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public List<AABB> getHighlightBoxes(Level level, BlockPos corePos, Direction rotation) {
        // Блок занимает 2 блока: ядро + один блок справа от игрока
        Direction right = rotation.getClockWise();
        BlockPos rightPos = corePos.relative(right);

        double minX = Math.min(corePos.getX(), rightPos.getX());
        double maxX = Math.max(corePos.getX(), rightPos.getX()) + 1;
        double minY = corePos.getY();
        double maxY = corePos.getY() + 1;
        double minZ = Math.min(corePos.getZ(), rightPos.getZ());
        double maxZ = Math.max(corePos.getZ(), rightPos.getZ()) + 1;

        return List.of(new AABB(minX, minY, minZ, maxX, maxY, maxZ));
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(3.0F, 10.0F)
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK);
    }
}