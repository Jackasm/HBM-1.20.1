package com.hbm.datagen.worldgen.structure.pieces;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockBobble;
import com.hbm.tileentity.block.TileEntityWandLogic;
import com.hbm.tileentity.block.TileEntityWandLoot;
import com.hbm.tileentity.deco.TileEntityBobble;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;

import java.util.ArrayList;
import java.util.List;

/**
 * Пост-обработка NBT-шаблона, общая для обычных структур и руин.
 *
 * <p>Руины ставятся по колонкам ({@link ConformingNBTStructurePiece}) и долго
 * не вызывали эти шаги: {@code wand_loot} оставался белой заглушкой вместо
 * сундука, потому что {@code triggerReplace} никто не выставлял.</p>
 *
 * <p>Во время генерации чанк ещё {@code ProtoChunk}, и {@code getBlockEntity}
 * для только что поставленного блока возвращает {@code null} — NBT в TE не
 * загружается. Такие позиции откладываются и обрабатываются на серверном тике,
 * когда чанк уже промотирован и TE существует.</p>
 */
final class StructurePostProcess {

    private static final List<Pending> PENDING = new ArrayList<>();
    private static final List<BarPending> BAR_PENDING = new ArrayList<>();

    private StructurePostProcess() {
    }

    private static final class BarPending {
        final ServerLevel level;
        final BlockPos pos;

        BarPending(ServerLevel level, BlockPos pos) {
            this.level = level;
            this.pos = pos;
        }
    }

    private static final class Pending {
        final ServerLevel level;
        final BlockPos pos;
        final CompoundTag nbt;
        final boolean loot;

        Pending(ServerLevel level, BlockPos pos, CompoundTag nbt, boolean loot) {
            this.level = level;
            this.pos = pos;
            this.nbt = nbt;
            this.loot = loot;
        }
    }

    /**
     * Загружает NBT в блок-сущность {@code wand_loot}/{@code wand_logic}
     * и запускает замену. Только блоки текущего чанка: остальные обработает
     * postProcess их собственного чанка.
     */
    static <T extends BlockEntity> void processWandBlock(WorldGenLevel level, StructureTemplate template,
                                                         BlockPos templatePosition, StructurePlaceSettings settings,
                                                         ChunkPos chunkPos, Block block, Class<T> teClass,
                                                         String name) {
        List<StructureTemplate.StructureBlockInfo> blocks =
                template.filterBlocks(templatePosition, settings, block);
        net.minecraft.world.level.levelgen.structure.BoundingBox bounds = settings.getBoundingBox();
        for (StructureTemplate.StructureBlockInfo info : blocks) {
            if (bounds != null && !bounds.isInside(info.pos())) continue;
            if (!new ChunkPos(info.pos()).equals(chunkPos)) continue;

            CompoundTag nbt = info.nbt();
            if (nbt == null || nbt.isEmpty()) {
                System.err.println("[HBM] " + name + " без nbt в структуре: " + info.pos());
                continue;
            }

            BlockEntity be = level.getBlockEntity(info.pos());
            if (!teClass.isInstance(be)) {
                // WorldGenRegion: TE ещё не создан. Догрузим после промоушена чанка.
                boolean loot = TileEntityWandLoot.class.isAssignableFrom(teClass);
                schedule(level, info.pos(), nbt, loot);
                continue;
            }

            apply(be, nbt, name);
        }
    }

    /** Пересчитать соединения решёток после постановки шаблона. */
    static void refreshIronBars(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof net.minecraft.world.level.block.IronBarsBlock)) return;
        BlockState updated = net.minecraft.world.level.block.Block.updateFromNeighbourShapes(state, level, pos);
        if (updated != state) {
            level.setBlock(pos, updated, 3);
        }
    }

    /**
     * Решётки на границе чанков нельзя чинить сразу: соседний чанк может быть
     * ещё протоплазмой, а чистка после декорации вернёт состояние шаблона.
     * Ждём полный чанк и пересчитываем ещё раз на серверном тике.
     */
    static void scheduleIronBars(WorldGenLevel level, List<BlockPos> positions) {
        ServerLevel serverLevel = level.getLevel();
        for (BlockPos pos : positions) {
            BAR_PENDING.add(new BarPending(serverLevel, pos.immutable()));
        }
    }

    /** Постановка блока вручную: TE может ещё не быть — тогда в очередь. */
    static void processWandBlockAt(WorldGenLevel level, BlockPos pos, CompoundTag nbt, boolean loot) {
        BlockEntity be = level.getBlockEntity(pos);
        if (loot && be instanceof TileEntityWandLoot || !loot && be instanceof TileEntityWandLogic) {
            apply(be, nbt, loot ? "wand_loot" : "wand_logic");
        } else {
            schedule(level, pos, nbt, loot);
        }
    }

    private static void schedule(WorldGenLevel level, BlockPos pos, CompoundTag nbt, boolean loot) {
        ServerLevel serverLevel = level.getLevel();
        PENDING.add(new Pending(serverLevel, pos.immutable(), nbt.copy(), loot));
    }

    /**
     * Серверный тик: обрабатывает только загруженные чанки. Позиции из
     * незагруженных чанков ждут здесь — таймаута нет, иначе далёкая структура
     * теряла замену до того, как игрок до неё дойдёт.
     */
    static void drain() {
        if (!BAR_PENDING.isEmpty()) {
            List<BarPending> batch = new ArrayList<>(BAR_PENDING);
            BAR_PENDING.clear();
            for (BarPending pending : batch) {
                if (!pending.level.hasChunkAt(pending.pos)) {
                    BAR_PENDING.add(pending);
                    continue;
                }
                var chunk = pending.level.getChunk(pending.pos);
                if (!(chunk instanceof net.minecraft.world.level.chunk.LevelChunk)) {
                    BAR_PENDING.add(pending);
                    continue;
                }
                refreshIronBars(pending.level, pending.pos);
            }
        }

        if (PENDING.isEmpty()) return;

        // Снимок: getBlockEntity/load могут снова вызвать postProcess.
        List<Pending> batch = new ArrayList<>(PENDING);
        PENDING.clear();
        for (Pending pending : batch) {
            if (!process(pending)) {
                PENDING.add(pending);
            }
        }
    }

    /** true — позиция обработана или больше не нужна; false — ждать чанк. */
    private static boolean process(Pending pending) {
        if (!pending.level.hasChunkAt(pending.pos)) return false;

        Block block = pending.loot ? ModBlocks.WAND_LOOT.get() : ModBlocks.WAND_LOGIC.get();
        if (!pending.level.getBlockState(pending.pos).is(block)) return true;

        BlockEntity be = pending.level.getBlockEntity(pending.pos);
        if (be == null) return false;

        if (pending.loot && be instanceof TileEntityWandLoot
                || !pending.loot && be instanceof TileEntityWandLogic) {
            apply(be, pending.nbt, pending.loot ? "wand_loot" : "wand_logic");
        }
        return true;
    }

    private static void apply(BlockEntity be, CompoundTag nbt, String name) {
        try {
            be.load(nbt);
        } catch (Exception e) {
            System.err.println("[HBM] Ошибка загрузки NBT " + name + " на " + be.getBlockPos() + ": " + e.getMessage());
            return;
        }

        if (be instanceof TileEntityWandLoot loot) {
            loot.setTriggerReplace(true);
        } else if (be instanceof TileEntityWandLogic logic) {
            logic.setTriggerReplace(true);
        }
    }

    /** Руины тоже получают случайный тип болванчика, а не дефолтный. */
    static void processBobbleheads(WorldGenLevel level, StructureTemplate template, BlockPos templatePosition,
                                   StructurePlaceSettings settings, ChunkPos chunkPos, RandomSource random) {
        net.minecraft.world.level.levelgen.structure.BoundingBox bounds = settings.getBoundingBox();
        for (StructureTemplate.StructureBlockInfo info
                : template.filterBlocks(templatePosition, settings, ModBlocks.BOBBLEHEAD.get())) {
            if (bounds != null && !bounds.isInside(info.pos())) continue;
            if (!new ChunkPos(info.pos()).equals(chunkPos)) continue;
            BlockEntity be = level.getBlockEntity(info.pos());
            if (be instanceof TileEntityBobble bobble) {
                BlockBobble.BobbleType[] types = BlockBobble.BobbleType.values();
                bobble.type = types[random.nextInt(types.length - 1) + 1];
                bobble.setChanged();
            }
        }
    }
}
