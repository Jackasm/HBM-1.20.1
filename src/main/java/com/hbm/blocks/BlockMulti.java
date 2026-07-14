package com.hbm.blocks;

import net.minecraft.world.level.material.MapColor;

public abstract class BlockMulti extends BlockBase implements IBlockMulti {

    public BlockMulti() {
        super();
    }

    public BlockMulti(Properties properties) {
        super(properties);
    }

    @Deprecated
    public BlockMulti(MapColor color) {
        this(Properties.of().mapColor(color));
    }



    @Override
    public int getSubCount() {
        return 1;
    }
}