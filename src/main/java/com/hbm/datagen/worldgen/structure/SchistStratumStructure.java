package com.hbm.datagen.worldgen.structure;

import com.hbm.datagen.worldgen.structure.pieces.OreVeinPiece;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SchistStratumStructure extends Structure {

    public static final Codec<SchistStratumStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    StructureSettings.CODEC.fieldOf("settings").forGetter(s -> s.settings)
            ).apply(instance, SchistStratumStructure::new));

    private final StructureSettings settings;

    public SchistStratumStructure(StructureSettings settings) {
        super(settings);
        this.settings = settings;
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int chunkX = context.chunkPos().x;
        int chunkZ = context.chunkPos().z;
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;

        int maxX = x + 15, maxZ = z + 15;
        int minY = 20, maxY = 40;

        BoundingBox box = new BoundingBox(x, minY, z, maxX, maxY, maxZ);

        long seed = context.chunkPos().toLong() ^ 4444444L;
        int yLevel = 30;
        int maxRange = 4;
        double threshold = 0.3;
        double scale = 0.01;

        OreVeinPiece piece = new OreVeinPiece(box, seed, yLevel, maxRange, threshold, scale);
        return Optional.of(new GenerationStub(new BlockPos(x, yLevel, z), builder -> builder.addPiece(piece)));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructureTypes.SCHIST_STRATUM.get();
    }
}