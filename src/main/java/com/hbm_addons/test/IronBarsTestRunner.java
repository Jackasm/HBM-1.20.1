package com.hbm_addons.test;

import com.hbm.datagen.worldgen.structure.pieces.NBTStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/** Временный: решётки в NBT-структуре должны соединяться между собой. */
public class IronBarsTestRunner extends TestRunner {
    private static final BlockPos ORIGIN = new BlockPos(700, 100, 700);

    public IronBarsTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        NBTStructurePiece piece = new NBTStructurePiece(server.getStructureManager(), "crane", ORIGIN, 0);
        BoundingBox box = piece.cleanupBox();
        for (int x = box.minX() - 2; x <= box.maxX() + 2; x++)
            for (int y = box.minY() - 2; y <= box.maxY() + 2; y++)
                for (int z = box.minZ() - 2; z <= box.maxZ() + 2; z++)
                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);

        for (int cx = box.minX() >> 4; cx <= box.maxX() >> 4; cx++) {
            for (int cz = box.minZ() >> 4; cz <= box.maxZ() >> 4; cz++) {
                level.getChunk(cx, cz);
                piece.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(),
                        RandomSource.create(1L), box, new ChunkPos(cx, cz), BlockPos.ZERO);
            }
        }

        piece.clean(level, box);

        int bars = 0;
        int connected = 0;
        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int y = box.minY(); y <= box.maxY(); y++) {
                for (int z = box.minZ(); z <= box.maxZ(); z++) {
                    BlockPos pos = new BlockPos(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    if (!state.is(Blocks.IRON_BARS)) continue;
                    bars++;
                    boolean east = state.getValue(BlockStateProperties.EAST);
                    BlockState next = level.getBlockState(pos.east());
                    if (east && next.is(Blocks.IRON_BARS) && next.getValue(BlockStateProperties.WEST)) connected++;
                }
            }
        }
        info("bars", bars);
        info("connected", connected);
        metric("bars", bars);
        metric("connected", connected);
        finish(bars > 0 && connected > 0);
        return true;
    }
}
