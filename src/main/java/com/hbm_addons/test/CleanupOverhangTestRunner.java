package com.hbm_addons.test;

import com.hbm.datagen.worldgen.structure.cleanup.TemplateLayout;
import com.hbm.datagen.worldgen.structure.pieces.NBTStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

import java.util.HashMap;
import java.util.Map;

/** Временный: чистка не должна выкапывать землю под высоким блоком (стрела крана). */
public class CleanupOverhangTestRunner extends TestRunner {
    private static final BlockPos ORIGIN = new BlockPos(500, 100, 500);

    public CleanupOverhangTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        NBTStructurePiece piece = new NBTStructurePiece(server.getStructureManager(), "crane", ORIGIN, 0);
        TemplateLayout layout = piece.layout(level);

        Map<Long, Integer> bottom = new HashMap<>();
        for (BlockPos local : layout.blocks().keySet()) {
            BlockPos world = piece.localToWorld(local);
            if (world != null) bottom.merge(((long) world.getX() << 32) ^ (world.getZ() & 0xffffffffL),
                    world.getY(), Math::min);
        }

        BlockPos ground = null;
        int groundY = -1;
        for (BlockPos local : layout.blocks().keySet()) {
            BlockPos world = piece.localToWorld(local);
            if (world == null) continue;
            long key = ((long) world.getX() << 32) ^ (world.getZ() & 0xffffffffL);
            if (bottom.getOrDefault(key, 0) > ORIGIN.getY() + 30) {
                ground = new BlockPos(world.getX(), ORIGIN.getY() + 1, world.getZ());
                groundY = ORIGIN.getY() + 1;
                break;
            }
        }
        if (ground == null) {
            info("no_overhang_column", true);
            finish(false);
            return true;
        }

        BoundingBox box = piece.cleanupBox();
        for (int x = box.minX() - 2; x <= box.maxX() + 2; x++)
            for (int y = ORIGIN.getY() - 2; y <= ORIGIN.getY() + 60; y++)
                for (int z = box.minZ() - 2; z <= box.maxZ() + 2; z++)
                    level.setBlock(new BlockPos(x, y, z), Blocks.AIR.defaultBlockState(), 2);
        level.setBlock(ground, Blocks.STONE.defaultBlockState(), 2);
        level.setBlock(ground.above(), Blocks.STONE.defaultBlockState(), 2);

        piece.clean(level, piece.cleanupBox());

        boolean kept = level.getBlockState(ground).is(Blocks.STONE);
        info("overhang_ground_y", groundY);
        info("overhang_kept", kept);
        metric("overhang_kept", kept ? 1 : 0);
        finish(kept);
        return true;
    }
}
