package com.hbm.datagen.worldgen.structure.pieces;

import com.hbm.datagen.worldgen.component.*;
import com.hbm.datagen.worldgen.structure.ModStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.NotNull;

public class NTMFeaturesPiece extends StructurePiece {

    private final NTMComponent component;

    public NTMFeaturesPiece(NTMComponent component) {
        super(ModStructurePieceTypes.NTM_FEATURES_PIECE.get(), 0, component.getBoundingBox());
        this.component = component;
    }

    public NTMFeaturesPiece(CompoundTag tag) {
        super(ModStructurePieceTypes.NTM_FEATURES_PIECE.get(), tag);
        CompoundTag compTag = tag.getCompound("Component");
        this.component = createFromNBT(compTag);
    }

    /** Компонент куска — нужен стендам, чтобы отличить шахту от дома. */
    public NTMComponent component() {
        return this.component;
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext ctx, @NotNull CompoundTag tag) {
        if (component != null) {
            CompoundTag compTag = new CompoundTag();
            component.writeNBT(compTag);
            compTag.putString("Type", component.getTypeName());

            BoundingBox bb = component.getBoundingBox();
            compTag.putIntArray("BB", new int[]{bb.minX(), bb.minY(), bb.minZ(), bb.maxX(), bb.maxY(), bb.maxZ()});

            tag.put("Component", compTag);
        }
    }

    private static NTMComponent createFromNBT(CompoundTag tag) {
        String type = tag.getString("Type");
        NTMComponent comp = switch (type) {
            // Одиночные структуры
            case "SiloComponent" -> new SiloComponent();
            case "NTMHouse1" -> new NTMHouse1();
            case "NTMHouse2" -> new NTMHouse2();
            case "NTMLab1" -> new NTMLab1();
            case "NTMLab2" -> new NTMLab2();
            case "RuralHouse1" -> new RuralHouse1();
            case "LargeOffice" -> new OfficeFeatures.LargeOffice();
            case "LargeOfficeCorner" -> new OfficeFeatures.LargeOfficeCorner();

            // Бункер
            case "StartingHub" -> new StartingHub();
            case "Corridor" -> new Corridor();
            case "BathroomL" -> new BathroomL();
            case "BedroomL" -> new BedroomL();
            case "FunJunction" -> new FunJunction();
            case "Laboratory" -> new Laboratory();
            case "PowerRoom" -> new PowerRoom();

            default -> null;
        };

        if (comp != null) {
            int[] bb = tag.getIntArray("BB");
            if (bb.length == 6) {
                comp.setBoundingBox(new BoundingBox(bb[0], bb[1], bb[2], bb[3], bb[4], bb[5]));
            }
            comp.readNBT(tag); // Восстанавливаем hpos, coordBaseMode и локальные поля
        }
        return comp;
    }

    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager manager,
                            @NotNull ChunkGenerator generator, @NotNull RandomSource rand,
                            @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
        if (component != null) {
            component.addComponentParts(ChunkBoundLevel.clip(level, chunkPos), rand, box);
        }
    }
}