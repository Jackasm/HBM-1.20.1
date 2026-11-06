package com.hbm.datagen.worldgen.structure.pieces;

import com.hbm.datagen.worldgen.structure.ModStructurePieceTypes;
import com.hbm.datagen.worldgen.structure.cleanup.CleanablePiece;
import com.hbm.datagen.worldgen.structure.cleanup.StructureCleanup;
import com.hbm.datagen.worldgen.structure.cleanup.TemplateLayout;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.ResLocation.ResLocation;

/**
 * Руины, повторяющие рельеф: каждая колонка шаблона ставится на свой верхний
 * твёрдый блок — порт {@code conformToTerrain} из эталона 1.7.10
 * ({@code ref:world/gen/nbt/NBTStructure.java:649}).
 *
 * <p>Ванильный {@link net.minecraft.world.level.levelgen.structure.TemplateStructurePiece}
 * ставит весь шаблон от одной высоты, поэтому на склоне часть руины висит в
 * воздухе. Здесь шаблон ставится по колонкам: у каждой колонки своё смещение по Y,
 * и шаблон обрезается рамкой этой колонки. Блоки и их NBT ставит сам
 * {@link StructureTemplate#placeInWorld}, так что содержимое сундуков и блоков
 * с данными не теряется.</p>
 *
 * <p>Высоты колонок считаются один раз при генерации и лежат в куске: при
 * постановке по чанкам heightmap мира уже включает ранее поставленные блоки,
 * поэтому пересчитывать её на месте нельзя.</p>
 *
 * <p>Сущности шаблона не ставятся ({@code ignoreEntities}): у руин их нет, а при
 * постановке по колонкам каждая колонка дублировала бы всех сущностей.</p>
 */
public class ConformingNBTStructurePiece extends StructurePiece implements CleanablePiece {

    private final String nbtPath;
    private final int rotation;
    private final int heightOffset;
    private final int gridX;
    private final int gridZ;
    private final int[] heights;

    /** Настройки только для пересчёта локальных позиций — без изменяемого bounding box. */
    private final StructurePlaceSettings transformSettings;

    private StructureTemplate template;
    private BlockPos templatePosition;
    private StructurePlaceSettings placeSettings;

    public ConformingNBTStructurePiece(StructureTemplateManager manager, String nbtPath, BlockPos pos,
                                       int rotation, int heightOffset, int gridX, int gridZ, int[] heights) {
        super(ModStructurePieceTypes.CONFORMING_NBT_PIECE.get(), 0,
                bounds(manager, nbtPath, pos, rotation, heightOffset, heights));
        this.nbtPath = nbtPath;
        this.rotation = rotation;
        this.heightOffset = heightOffset;
        this.gridX = gridX;
        this.gridZ = gridZ;
        this.heights = heights;
        this.templatePosition = pos;
        this.placeSettings = makeSettings(rotation).setIgnoreEntities(true);
        this.transformSettings = makeSettings(rotation);
        this.template = manager.getOrCreate(ResLocation(nbtPath));
    }

    /** Загрузка из NBT сохранённого мира: границы и высоты уже лежат в теге. */
    public ConformingNBTStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieceTypes.CONFORMING_NBT_PIECE.get(), tag);
        this.nbtPath = tag.getString("NBT");
        this.rotation = tag.getInt("Rot");
        this.heightOffset = tag.getInt("HO");
        this.gridX = tag.getInt("SX");
        this.gridZ = tag.getInt("SZ");
        this.heights = tag.getIntArray("H");
        this.templatePosition = new BlockPos(tag.getInt("TPX"), tag.getInt("TPY"), tag.getInt("TPZ"));
        this.placeSettings = makeSettings(rotation).setIgnoreEntities(true);
        this.transformSettings = makeSettings(rotation);
        this.template = context.structureTemplateManager().getOrCreate(ResLocation(nbtPath));
    }

    private static BoundingBox bounds(StructureTemplateManager manager, String nbtPath, BlockPos pos,
                                      int rotation, int heightOffset, int[] heights) {
        StructureTemplate template = manager.getOrCreate(ResLocation(nbtPath));
        BoundingBox footprint = template.getBoundingBox(makeSettings(rotation), pos);
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (int h : heights) {
            if (h < min) min = h;
            if (h > max) max = h;
        }
        if (min == Integer.MAX_VALUE) {
            min = pos.getY();
            max = pos.getY();
        }
        int sizeY = template.getSize().getY();
        return new BoundingBox(
                footprint.minX(), min + heightOffset - 1, footprint.minZ(),
                footprint.maxX(), max + heightOffset + sizeY, footprint.maxZ()
        );
    }

    private static StructurePlaceSettings makeSettings(int rotation) {
        Rotation rot = switch (rotation % 4) {
            case 1 -> Rotation.CLOCKWISE_90;
            case 2 -> Rotation.CLOCKWISE_180;
            case 3 -> Rotation.COUNTERCLOCKWISE_90;
            default -> Rotation.NONE;
        };
        return new StructurePlaceSettings().setRotation(rot).setMirror(Mirror.NONE);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context,
                                         @NotNull CompoundTag tag) {
        tag.putString("NBT", this.nbtPath);
        tag.putInt("Rot", this.rotation);
        tag.putInt("HO", this.heightOffset);
        tag.putInt("SX", this.gridX);
        tag.putInt("SZ", this.gridZ);
        tag.putIntArray("H", this.heights);
        tag.putInt("TPX", this.templatePosition.getX());
        tag.putInt("TPY", this.templatePosition.getY());
        tag.putInt("TPZ", this.templatePosition.getZ());
    }

    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager manager,
                            @NotNull ChunkGenerator generator, @NotNull RandomSource random,
                            @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
        StructurePlaceSettings settings = this.placeSettings.copy().setIgnoreEntities(true);
        // Якорь — средний уровень подошвы, от которого считался templatePosition:
        // смещение колонки = её поверхность − якорь, тогда низ руины ложится
        // ровно на верхний блок своей колонки (эталон: top + heightOffset).
        int anchor = this.templatePosition.getY() - this.heightOffset;
        int originX = this.templatePosition.getX();
        int originZ = this.templatePosition.getZ();

        for (int dx = 0; dx < this.gridX; dx++) {
            for (int dz = 0; dz < this.gridZ; dz++) {
                int x = originX + dx;
                int z = originZ + dz;
                if (!new ChunkPos(x, z).equals(chunkPos)) continue;

                int idx = dx * this.gridZ + dz;
                if (idx < 0 || idx >= this.heights.length) continue;
                int delta = this.heights[idx] - anchor;

                settings.setBoundingBox(new BoundingBox(x, this.boundingBox.minY(), z,
                        x, this.boundingBox.maxY(), z));
                BlockPos shifted = this.templatePosition.offset(0, delta, 0);
                this.template.placeInWorld(level, shifted, shifted, settings, random, 2);
            }
        }

        // #102: запоминаем кусок для чистки после декорации.
        StructureCleanup.register(level, chunkPos, this);
    }

    @Override
    public BoundingBox cleanupBox() {
        return this.boundingBox;
    }

    @Override
    public int cleanupBaseY(int x, int z) {
        return baseYAt(x, z);
    }

    @Override
    public BlockPos localToWorld(BlockPos local) {
        BlockPos rel = StructureTemplate.calculateRelativePosition(this.transformSettings, local);
        int x = this.templatePosition.getX() + rel.getX();
        int z = this.templatePosition.getZ() + rel.getZ();
        int base = baseYAt(x, z);
        if (base < 0) return null;
        return new BlockPos(x, base + rel.getY(), z);
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

    public StructureTemplate template() {
        return this.template;
    }

    public BlockPos templatePosition() {
        return this.templatePosition;
    }

    /** Высота рельефа в колонке, какой она была при генерации. */
    public int heightAt(int x, int z) {
        int dx = x - this.templatePosition.getX();
        int dz = z - this.templatePosition.getZ();
        if (dx < 0 || dz < 0 || dx >= this.gridX || dz >= this.gridZ) return -1;
        return this.heights[dx * this.gridZ + dz];
    }

    /** Низ руины в колонке — для стенда и отладки. */
    public int baseYAt(int x, int z) {
        int top = heightAt(x, z);
        return top < 0 ? -1 : top + this.heightOffset;
    }
}
