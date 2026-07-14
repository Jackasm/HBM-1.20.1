package com.hbm.blocks.machine;

import com.hbm.tileentity.machine.TileEntityFloodlightBeam;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;

public class BlockFloodlightBeam extends BaseEntityBlock {

    public BlockFloodlightBeam() {
        super(Properties.of()
                .mapColor(MapColor.NONE)
                .noCollission()
                .instabreak()
                .noLootTable()
                .lightLevel(state -> 15)
                .pushReaction(PushReaction.DESTROY));
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityFloodlightBeam(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }
}