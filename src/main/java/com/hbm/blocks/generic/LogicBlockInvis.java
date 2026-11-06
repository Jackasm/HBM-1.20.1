package com.hbm.blocks.generic;

public class LogicBlockInvis extends LogicBlock {

    public LogicBlockInvis(Properties properties) {
        super(properties);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.STONE)
                .strength(-1.0F, 3600000.0F)
                .noOcclusion()
                .noLootTable()
                .pushReaction(net.minecraft.world.level.material.PushReaction.BLOCK);
    }
}