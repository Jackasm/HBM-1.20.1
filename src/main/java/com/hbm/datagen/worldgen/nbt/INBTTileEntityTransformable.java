package com.hbm.datagen.worldgen.nbt;

import net.minecraft.world.level.Level;

public interface INBTTileEntityTransformable {

    /**
     * Like INBTTransformable but for TileEntities, like for randomizing bobbleheads
     * Allows the TE to modify itself when spawned in an NBT structure
     */
    void transformTE(Level level, int coordBaseMode);
}