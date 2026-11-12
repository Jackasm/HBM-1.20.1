package com.hbm.datagen.worldgen.structure.pieces;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.worldgen.structure.ModStructurePieceTypes;
import com.hbm.datagen.worldgen.structure.cleanup.CleanablePiece;
import com.hbm.datagen.worldgen.structure.cleanup.StructureCleanup;
import com.hbm.datagen.worldgen.structure.cleanup.TemplateLayout;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsLegacy;
import com.hbm.tileentity.block.TileEntityWandLogic;
import com.hbm.tileentity.block.TileEntityWandLoot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.TemplateStructurePiece;
import com.hbm.blocks.generic.BarbedWire;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.hbm.util.ResLocation.ResLocation;

public class NBTStructurePiece extends TemplateStructurePiece implements CleanablePiece {

    private final String nbtPath;

    /** Настройки только для пересчёта локальных позиций — без изменяемого bounding box. */
    private final StructurePlaceSettings transformSettings;

    /**
     * Конструктор для генерации в мире.
     * Благодаря {@link TemplateStructurePiece} bounds вычисляются автоматически
     * с учётом rotation и размеров шаблона — вручную ничего считать не нужно.
     */
    public NBTStructurePiece(StructureTemplateManager manager, String nbtPath, BlockPos pos, int rotation) {
        super(ModStructurePieceTypes.NBT_PIECE.get(), 0, manager,
                ResLocation(nbtPath),
                ResLocation(nbtPath).toString(),
                makeSettings(rotation),
                pos);
        this.nbtPath = nbtPath;
        this.transformSettings = makeSettings(rotation);
    }

    /**
     * Конструктор для загрузки из NBT (сохранение/загрузка мира).
     */
    public NBTStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieceTypes.NBT_PIECE.get(), tag,
                context.structureTemplateManager(),
                (loc) -> makeSettings(tag.getInt("Rot")));
        this.nbtPath = tag.getString("NBT");
        this.transformSettings = makeSettings(tag.getInt("Rot"));
    }

    private static StructurePlaceSettings makeSettings(int rotation) {
        Rotation rot = switch (rotation % 4) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
        return new StructurePlaceSettings()
                .setRotation(rot)
                .setMirror(Mirror.NONE)
                .setIgnoreEntities(false);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context,
                                         @NotNull CompoundTag tag) {
        super.addAdditionalSaveData(context, tag);
        tag.putInt("Rot", this.placeSettings.getRotation().ordinal());
        tag.putString("NBT", nbtPath);
    }

    /**
     * HBM-структуры не используют ванильные data markers
     * ({@code minecraft:structure_block} с NBT-тегом metadata).
     * Если конвертер начнёт их создавать — обработать здесь.
     */
    @Override
    protected void handleDataMarker(@NotNull String name, @NotNull BlockPos pos,
                                    @NotNull ServerLevelAccessor level,
                                    @NotNull RandomSource random, @NotNull BoundingBox box) {
        // no-op
    }

    private static final Block[] MULTIBLOCK_TYPES = {

            ModBlocks.TURRET_HOWARD_DAMAGED.get(),
            ModBlocks.MACHINE_FLUIDTANK.get(),
            ModBlocks.VENDING_MACHINE_SODA.get(),
            ModBlocks.VENDING_MACHINE_OBAMNA.get(),
            ModBlocks.RADIO_TELEX.get(),
            ModBlocks.CARGO_DOOR.get(),
            ModBlocks.VAULT_DOOR.get(),
            ModBlocks.FIRE_DOOR.get(),
            ModBlocks.SILO_HATCH.get(),
            ModBlocks.SILO_HATCH_LARGE.get(),
            ModBlocks.SLIDING_BLAST_DOOR.get(),
            ModBlocks.SECURE_ACCESS_DOOR.get(),
            ModBlocks.LARGE_VEHICLE_DOOR.get(),
            ModBlocks.QE_CONTAINMENT.get(),
            ModBlocks.QE_SLIDING_DOOR.get(),
            ModBlocks.ROUND_AIRLOCK_DOOR.get(),
            ModBlocks.SLIDING_SEAL_DOOR.get(),
            ModBlocks.WATER_DOOR.get(),

            // добавляй по мере необходимости
    };

    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager manager,
                            @NotNull ChunkGenerator generator, @NotNull RandomSource random,
                            @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
        super.postProcess(level, manager, generator, random, box, chunkPos, pos);

        List<BlockPos> irons = new ArrayList<>();
        for (StructureTemplate.StructureBlockInfo info
                : this.template.filterBlocks(this.templatePosition, this.placeSettings, Blocks.IRON_BARS)) {
            if (new ChunkPos(info.pos()).equals(chunkPos)) {
                StructurePostProcess.refreshIronBars(level, info.pos());
                irons.add(info.pos());
            }
        }
        StructurePostProcess.scheduleIronBars(level, irons);

        fixBarbedWire(level, chunkPos);
        placeMultiblocks(level);
        StructurePostProcess.processWandBlock(level, this.template, this.templatePosition, this.placeSettings, chunkPos,
                ModBlocks.WAND_LOOT.get(), TileEntityWandLoot.class, "wand_loot");
        StructurePostProcess.processWandBlock(level, this.template, this.templatePosition, this.placeSettings, chunkPos,
                ModBlocks.WAND_LOGIC.get(), TileEntityWandLogic.class, "wand_logic");
        StructurePostProcess.processBobbleheads(level, this.template, this.templatePosition, this.placeSettings,
                chunkPos, random);
        fillChests(level);
        // #102: запоминаем кусок, чтобы фича чистки на top_layer_modification
        // убрала декор, который вырастет после нас.
        StructureCleanup.register(level, chunkPos, this);
    }

    @Override
    public BoundingBox cleanupBox() {
        return this.boundingBox;
    }

    @Override
    public int cleanupBaseY(int x, int z) {
        return this.templatePosition.getY();
    }

    @Override
    public BlockPos localToWorld(BlockPos local) {
        return this.templatePosition.offset(
                StructureTemplate.calculateRelativePosition(this.transformSettings, local));
    }

    @Override
    public BlockState transformState(BlockState local) {
        return local.rotate(this.transformSettings.getRotation());
    }

    @Override
    public TemplateLayout layout(WorldGenLevel level) {
        HolderGetter<Block> blocks = level.registryAccess().lookup(Registries.BLOCK).orElseThrow();
        return TemplateLayout.of(this.nbtPath, this.template, blocks);
    }

    // ==================== Лут в обычных сундуках ====================

    private record ChestLoot(BlockPos localPos, String pool, int count) {}

    /**
     * Сундуки, которые в 1.7.10 наполнялись кодом через ItemPool, а в NBT-шаблоне
     * остались пустыми. Позиции локальные, от угла шаблона. Порядок и количество
     * предметов взяты из исходных генераторов (Spaceship, DesertAtom002/003).
     */
    private static final Map<String, List<ChestLoot>> CHEST_LOOT = Map.of(
            "spaceship", List.of(
                    new ChestLoot(new BlockPos(4, 1, 25), ItemPoolsLegacy.POOL_SPACESHIP, 12),
                    new ChestLoot(new BlockPos(4, 1, 26), ItemPoolsLegacy.POOL_SPACESHIP, 12),
                    new ChestLoot(new BlockPos(7, 1, 25), ItemPoolsLegacy.POOL_SPACESHIP, 12),
                    new ChestLoot(new BlockPos(7, 1, 26), ItemPoolsLegacy.POOL_SPACESHIP, 12),
                    new ChestLoot(new BlockPos(7, 1, 38), ItemPoolsLegacy.POOL_EXPENSIVE, 12)),
            "desert_atom", List.of(
                    new ChestLoot(new BlockPos(6, 0, 29), ItemPoolsLegacy.POOL_GENERIC, 8),
                    new ChestLoot(new BlockPos(4, 0, 44), ItemPoolsLegacy.POOL_GENERIC, 8),
                    new ChestLoot(new BlockPos(4, 4, 41), ItemPoolsLegacy.POOL_NUKE_MISC, 12))
    );

    private void fillChests(WorldGenLevel level) {
        List<ChestLoot> loots = CHEST_LOOT.get(this.nbtPath);
        if (loots == null) return;

        for (ChestLoot loot : loots) {
            BlockPos worldPos = this.templatePosition.offset(this.template.getZeroPositionWithTransform(
                    loot.localPos(), Mirror.NONE, this.placeSettings.getRotation()));

            // Сундук может оказаться в чанке вне региона генерации: ванильный
            // WorldGenRegion на такой запрос бросает исключение, и генерация
            // падает целиком. Свой чанк сундук получит, когда дойдёт генерация
            // соседа — тогда и наполним. Поймано стендом worldgen_bench (#134).
            if (!level.hasChunk(worldPos.getX() >> 4, worldPos.getZ() >> 4)) continue;

            if (!(level.getBlockEntity(worldPos) instanceof RandomizableContainerBlockEntity container)) continue;
            // Шаблон мог принести содержимое сам; пустые — наполняем.
            if (!container.isEmpty()) continue;

            int slot = 0;
            for (ItemStack stack : ItemPool.getPool(loot.pool()).generate(level.getRandom(), loot.count())) {
                if (slot >= container.getContainerSize()) break;
                container.setItem(slot++, stack);
            }
            container.setChanged();
        }
    }

// ==================== Колючая проволока ====================

    private static final Block[] WIRE_BLOCKS = {
            ModBlocks.BARBED_WIRE.get(),
            ModBlocks.BARBED_WIRE_FIRE.get(),
            ModBlocks.BARBED_WIRE_POISON.get(),
            ModBlocks.BARBED_WIRE_ACID.get(),
            ModBlocks.BARBED_WIRE_WITHER.get(),
            ModBlocks.BARBED_WIRE_ULTRA_DEATH.get()
    };

    /**
     * В NBT вся проволока лежит с одинаковым {@code facing}, поэтому после
     * поворота структуры панели на двух сторонах забора смотрят вдоль него.
     * Ориентируем панель по направлению забора: вдоль X — на север, вдоль Z — на запад
     * (та же конвенция, что в {@code SiloSurface}).
     */
    private void fixBarbedWire(WorldGenLevel level, ChunkPos chunkPos) {
        List<StructureTemplate.StructureBlockInfo> all = new ArrayList<>();
        for (Block wire : WIRE_BLOCKS) {
            all.addAll(this.template.filterBlocks(this.templatePosition, this.placeSettings, wire));
        }
        if (all.isEmpty()) return;

        // Соседей ищем по всему шаблону, а не только по текущему чанку: прогон
        // проволоки часто пересекает границу, и панели у границы иначе не видят
        // соседа с другой стороны и остаются с исходным (неверным) facing.
        // Ставим блоки по-прежнему только в свой чанк — чужие во время генерации нельзя.
        Set<BlockPos> local = new HashSet<>();
        for (StructureTemplate.StructureBlockInfo info : all) {
            local.add(StructureTemplate.calculateRelativePosition(this.placeSettings, info.pos()));
        }

        for (StructureTemplate.StructureBlockInfo info : all) {
            if (!new ChunkPos(info.pos()).equals(chunkPos)) continue;

            BlockPos rel = StructureTemplate.calculateRelativePosition(this.placeSettings, info.pos());
            Direction facing;
            if (local.contains(rel.east()) || local.contains(rel.west())) {
                facing = Direction.NORTH;
            } else if (local.contains(rel.north()) || local.contains(rel.south())) {
                facing = Direction.WEST;
            } else {
                continue;
            }
            Direction rotated = this.placeSettings.getRotation().rotate(facing);
            BlockState fixedState = info.state().setValue(BarbedWire.FACING, rotated);
            level.setBlock(info.pos(), fixedState, 2);
        }
    }

// ==================== Мультиблоки ====================

    private void placeMultiblocks(WorldGenLevel level) {
        for (Block b : MULTIBLOCK_TYPES) {
            if (!(b instanceof BlockDummyable dummy)) continue;

            List<StructureTemplate.StructureBlockInfo> found = this.template.filterBlocks(
                    this.templatePosition, this.placeSettings, b);

            for (StructureTemplate.StructureBlockInfo info : found) {
                if (!dummy.isCore(info.state())) continue;

                Direction facing = info.state().getValue(BlockDummyable.FACING);
                dummy.placeMultiblock(level, info.pos(), facing);
            }
        }
    }

}
