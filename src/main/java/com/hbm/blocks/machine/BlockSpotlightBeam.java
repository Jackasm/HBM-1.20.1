package com.hbm.blocks.machine;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockSpotlightBeam extends Block {

    public BlockSpotlightBeam() {
        super(Properties.of()
                .mapColor(MapColor.NONE)
                .noCollission()
                .instabreak()
                .noLootTable()
                .lightLevel(state -> 15)
                .pushReaction(PushReaction.DESTROY));
    }
}