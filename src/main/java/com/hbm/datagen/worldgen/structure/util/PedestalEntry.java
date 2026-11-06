package com.hbm.datagen.worldgen.structure.util;

import net.minecraft.core.BlockPos;

public class PedestalEntry {
    public PedestalEntryType type;
    public BlockPos pos;
    public long timestamp;

    public PedestalEntry(PedestalEntryType type, BlockPos pos, long timestamp) {
        this.type = type;
        this.pos = pos;
        this.timestamp = timestamp;
    }
}
