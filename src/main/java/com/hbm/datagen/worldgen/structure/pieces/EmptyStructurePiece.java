package com.hbm.datagen.worldgen.structure.pieces;

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

/**
 * Кусок-заглушка для «пустого исхода» ({@link com.hbm.datagen.worldgen.structure.NothingStructure}):
 * делает старт валидным, но не ставит ни одного блока.
 */
public class EmptyStructurePiece extends StructurePiece {

    public EmptyStructurePiece(BlockPos pos) {
        super(ModStructurePieceTypes.EMPTY_PIECE.get(), 0,
                new BoundingBox(pos.getX(), pos.getY(), pos.getZ(), pos.getX(), pos.getY(), pos.getZ()));
    }

    public EmptyStructurePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        super(ModStructurePieceTypes.EMPTY_PIECE.get(), tag);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext context,
                                         @NotNull CompoundTag tag) {
    }

    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager manager,
                            @NotNull ChunkGenerator generator, @NotNull RandomSource random,
                            @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
    }
}
