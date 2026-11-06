package com.hbm.datagen.worldgen.structure.pieces;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;

/**
 * Обёртка над {@link WorldGenLevel}, которая гасит запись за пределы текущего чанка.
 *
 * <p>Структуры, написанные кодом, а не шаблоном, пишут всю постройку за один
 * проход, а {@code StructurePiece#postProcess} вызывается на каждый чанк габарита.
 * Без этой обёртки {@code WorldGenRegion} кидает «Detected setBlock in a far chunk»,
 * а блок может не сохраниться в чужом чанке (ср. #156).</p>
 */
public final class ChunkBoundLevel {

    private ChunkBoundLevel() {
    }

    public static WorldGenLevel clip(WorldGenLevel level, ChunkPos chunkPos) {
        BoundingBox chunkBox = new BoundingBox(
                chunkPos.getMinBlockX(), level.getMinBuildHeight(), chunkPos.getMinBlockZ(),
                chunkPos.getMaxBlockX(), level.getMaxBuildHeight() - 1, chunkPos.getMaxBlockZ());

        return (WorldGenLevel) Proxy.newProxyInstance(
                WorldGenLevel.class.getClassLoader(),
                new Class<?>[]{WorldGenLevel.class},
                (proxy, method, args) -> {
                    if (args != null && args.length > 0 && args[0] instanceof BlockPos blockPos
                            && (method.getName().equals("setBlock") || method.getName().equals("setBlockAndUpdate"))) {
                        if (!chunkBox.isInside(blockPos)) return false;
                    }
                    try {
                        return method.invoke(level, args);
                    } catch (InvocationTargetException e) {
                        throw e.getCause();
                    }
                });
    }
}
