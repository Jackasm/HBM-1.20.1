package com.hbm.blocks.generic;

import com.hbm.blocks.BlockBase;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

public class BlockGeneric extends BlockBase {

    public BlockGeneric(Properties properties) {
        super(properties);
    }

    // Фабричный метод для создания свойств (опционально)
    public static Properties createProperties(MapColor color, float hardness, float resistance) {
        return Properties.of()
                .mapColor(color)
                .strength(hardness, resistance)
                .pushReaction(PushReaction.DESTROY);
    }

    public static Properties createOreProperties(MapColor color, float hardness, float resistance) {
        return createProperties(color, hardness, resistance)
                .requiresCorrectToolForDrops();
    }
}