package com.hbm_addons.test;

import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.worldgen.structure.pieces.ConformingNBTStructurePiece;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Руины: шаблон ставится по колонкам, а {@code wand_loot} заменяется на целевой блок.
 *
 * <p>У {@link ConformingNBTStructurePiece} было две дыры: колонка искалась
 * через {@code new ChunkPos(x, z)} (конструктор ждёт номер чанка, а не блок),
 * поэтому не ставился ни один блок; и не вызывалась пост-обработка
 * {@code wand_loot}, поэтому даже поставленный блок оставался белой заглушкой
 * с дефолтным {@code LOOT_SHIT} вместо сундука.</p>
 *
 * <p>Стенд ставит настоящую руину {@code ntmruins_a} (два лут-блока), ждёт тик
 * TE и проверяет и посадку, и замену.</p>
 */
public class RuinLootTestRunner extends TestRunner {

    private static final BlockPos ORIGIN = new BlockPos(16, 80, 16);
    /** Колонки ниже якоря: проверяет, что пост-обработка учитывает delta. */
    private static final BlockPos PLACED = ORIGIN.offset(0, -3, 0);
    private static final String RUIN = "ntmruins_a";

    private int stage = 0;
    private int placedLoot = 0;
    private final List<BlockPos> lootPositions = new ArrayList<>();

    public RuinLootTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        if (stage == 0) {
            placeRuin(server);
            stage = 1;
            return false;
        }

        boolean originPlaced = !level.getBlockState(PLACED.above()).isAir();
        int replaced = 0;
        for (BlockPos pos : lootPositions) {
            if (level.getBlockState(pos).is(ModBlocks.DECO_LOOT.get())) replaced++;
        }

        boolean ok = true;
        ok &= expect("ruin_placed", originPlaced);
        ok &= expect("loot_blocks_found", lootPositions.size() == 2);
        ok &= expect("loot_placed_as_wand", placedLoot == lootPositions.size());
        ok &= expect("loot_blocks_replaced", replaced == lootPositions.size());

        info("loot_positions", lootPositions);
        metric("loot_blocks", lootPositions.size());
        metric("placed_wand", placedLoot);
        metric("replaced", replaced);
        finish(ok);
        return true;
    }

    private void placeRuin(MinecraftServer server) {
        clearArea(PLACED, 12, 14, 12);

        StructureTemplate template = server.getStructureManager()
                .getOrCreate(new ResourceLocation("hbm", RUIN));
        Vec3i size = template.getSize();

        int[] heights = new int[size.getX() * size.getZ()];
        Arrays.fill(heights, PLACED.getY());

        ConformingNBTStructurePiece piece = new ConformingNBTStructurePiece(
                server.getStructureManager(), RUIN, ORIGIN, 0, 0, size.getX(), size.getZ(), heights);

        BoundingBox box = piece.getBoundingBox();
        RandomSource random = RandomSource.create(20261109L);
        for (int cx = box.minX() >> 4; cx <= box.maxX() >> 4; cx++) {
            for (int cz = box.minZ() >> 4; cz <= box.maxZ() >> 4; cz++) {
                ChunkPos chunkPos = new ChunkPos(cx, cz);
                level.getChunk(cx, cz);
                piece.postProcess(level, level.structureManager(), level.getChunkSource().getGenerator(),
                        random, box, chunkPos, BlockPos.ZERO);
            }
        }

        StructurePlaceSettings settings = new StructurePlaceSettings()
                .setRotation(Rotation.NONE)
                .setMirror(Mirror.NONE);
        for (StructureTemplate.StructureBlockInfo info
                : template.filterBlocks(PLACED, settings, ModBlocks.WAND_LOOT.get())) {
            lootPositions.add(info.pos());
            if (level.getBlockState(info.pos()).is(ModBlocks.WAND_LOOT.get())) placedLoot++;
        }
    }

    private void clearArea(BlockPos center, int rx, int ry, int rz) {
        for (int dx = -rx; dx <= rx; dx++) {
            for (int dy = -1; dy <= ry; dy++) {
                for (int dz = -rz; dz <= rz; dz++) {
                    level.setBlock(center.offset(dx, dy, dz), Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    private boolean expect(String name, boolean pass) {
        check(name, pass);
        return pass;
    }
}
