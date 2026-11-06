package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class BlockNTMGlassPane extends IronBarsBlock {

    private final boolean doesDrop;

    public BlockNTMGlassPane(Properties properties, boolean doesDrop) {
        super(properties);
        this.doesDrop = doesDrop;
    }

    public boolean doesDrop() {
        return doesDrop;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target,
                                                @NotNull BlockGetter level, @NotNull BlockPos pos,
                                                @NotNull Player player) {
        return new ItemStack(this.asItem());
    }
}