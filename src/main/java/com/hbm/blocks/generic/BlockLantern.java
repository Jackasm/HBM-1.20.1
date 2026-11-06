package com.hbm.blocks.generic;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.deco.TileEntityLantern;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockLantern extends BlockDummyable {

    public BlockLantern(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return isCore(state) ? new TileEntityLantern(pos, state) : null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (!isCore(state)) return null;
        if (type == ModTileEntity.LANTERN.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityLantern l) l.tick();
            };
        }
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[] {4, 0, 0, 0, 0, 0};
    }

    @Override
    public int getZOffset() {
        return 0;
    }

    @Override
    protected int getGuiID() {
        return -1;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                .strength(3.0F)
                .sound(net.minecraft.world.level.block.SoundType.METAL)
                .lightLevel(s -> 15)
                .noOcclusion()
                .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK);
    }
}