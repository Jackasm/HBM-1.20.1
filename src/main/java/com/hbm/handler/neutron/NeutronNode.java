package com.hbm.handler.neutron;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.HashMap;
import java.util.Map;

public abstract class NeutronNode {
    public final NeutronStream.NeutronType type;
    public final BlockPos pos;
    public final BlockEntity tile;
    public final Map<String, Object> data = new HashMap<>();

    public NeutronNode(BlockEntity tile, NeutronStream.NeutronType type) {
        this.tile = tile;
        this.type = type;
        this.pos = tile.getBlockPos();
    }
}