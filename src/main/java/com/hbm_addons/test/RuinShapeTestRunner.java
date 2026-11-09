package com.hbm_addons.test;

import com.hbm.datagen.worldgen.structure.pieces.ConformingNBTStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.Arrays;

public class RuinShapeTestRunner extends TestRunner {
    private static final BlockPos A = new BlockPos(300, 100, 300);
    private static final BlockPos B = new BlockPos(300, 100, 340);
    private static final BlockPos C = new BlockPos(300, 100, 380);
    private static final BlockPos D = new BlockPos(300, 100, 420);
    private static final String RUIN = "ntmruins_a";

    public RuinShapeTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        StructureTemplate template = server.getStructureManager().getOrCreate(new ResourceLocation("hbm", RUIN));
        int a = place(server, template, A, 0);
        int b = place(server, template, B, 1);
        int c = place(server, template, C, 2);
        int d = place(server, template, D, 3);
        info("blocks_rot0", a);
        info("blocks_rot1", b);
        info("blocks_rot2", c);
        info("blocks_rot3", d);
        metric("blocks_rot0", a);
        metric("blocks_rot1", b);
        metric("blocks_rot2", c);
        metric("blocks_rot3", d);
        finish(a > 400 && b > 400 && c > 400 && d > 400);
        return true;
    }

    private int place(MinecraftServer server, StructureTemplate template, BlockPos origin, int rotation) {
        Vec3i size = template.getSize();
        for (int dx = -4; dx <= size.getX() + 4; dx++)
            for (int dy = -4; dy <= size.getY() + 4; dy++)
                for (int dz = -4; dz <= size.getZ() + 4; dz++)
                    level.setBlock(origin.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 2);

        int[] heights = new int[size.getX() * size.getZ()];
        Arrays.fill(heights, origin.getY());
        ConformingNBTStructurePiece piece = new ConformingNBTStructurePiece(
                server.getStructureManager(), RUIN, origin, rotation, 0, size.getX(), size.getZ(), heights);
        BoundingBox box = piece.getBoundingBox();
        info("box_rot" + rotation, box);
        for (int cx = box.minX() >> 4; cx <= box.maxX() >> 4; cx++) {
            for (int cz = box.minZ() >> 4; cz <= box.maxZ() >> 4; cz++) {
                level.getChunk(cx, cz);
                piece.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(),
                        RandomSource.create(1L), box, new ChunkPos(cx, cz), BlockPos.ZERO);
            }
        }
        int n = 0;
        for (int x = box.minX(); x <= box.maxX(); x++)
            for (int y = box.minY(); y <= box.maxY(); y++)
                for (int z = box.minZ(); z <= box.maxZ(); z++)
                    if (!level.getBlockState(new BlockPos(x, y, z)).isAir()) n++;
        return n;
    }
}
