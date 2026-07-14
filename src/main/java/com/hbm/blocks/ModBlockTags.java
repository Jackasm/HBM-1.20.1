package com.hbm.blocks;

import com.hbm.util.RefStrings;
import com.hbm.util.ResLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class ModBlockTags {

    // Кастомные теги для вашего мода
    public static final TagKey<Block> RADIOACTIVE_BLOCKS = tag("radioactive_blocks");
    public static final TagKey<Block> OIL_BEARING_BLOCKS = tag("oil_bearing_blocks");
    public static final TagKey<Block> NUCLEAR_REACTOR_BLOCKS = tag("nuclear_reactor_blocks");

    // Теги для инструментов
    public static final TagKey<Block> NEEDS_STEEL_TOOL = tag("needs_steel_tool");
    public static final TagKey<Block> NEEDS_TITANIUM_TOOL = tag("needs_titanium_tool");
    public static final TagKey<Block> NEEDS_SCHRABIDIUM_TOOL = tag("needs_schrabidium_tool");

    private static TagKey<Block> tag(String name) {
        return BlockTags.create(ResLocation.ResLocation(RefStrings.MODID, name));
    }

}