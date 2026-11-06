package com.hbm.datagen.worldgen.structure;

import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.worldgen.structure.pieces.OreVeinPiece;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class OreCaveStructure extends Structure {

    public static final Codec<OreCaveStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    StructureSettings.CODEC.fieldOf("settings").forGetter(s -> s.settings),
                    Codec.STRING.fieldOf("cave_type").forGetter(s -> s.caveType)
            ).apply(instance, OreCaveStructure::new));

    private final StructureSettings settings;
    private final String caveType;

    public OreCaveStructure(StructureSettings settings, String caveType) {
        super(settings);
        this.settings = settings;
        this.caveType = caveType;
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        int chunkX = context.chunkPos().x;
        int chunkZ = context.chunkPos().z;
        int x = chunkX * 16 + 8;
        int z = chunkZ * 16 + 8;

        int maxX = x + 15, maxZ = z + 15;
        int minY = 5, maxY = 55;

        net.minecraft.world.level.levelgen.structure.BoundingBox box = new net.minecraft.world.level.levelgen.structure.BoundingBox(x, minY, z, maxX, maxY, maxZ);

        long seed = context.chunkPos().toLong() ^ (long) caveType.hashCode();

        int yLevel = 30;
        int maxRange = 4;
        double scale = 0.01;

        double threshold = 0.15;
        if ("asbestos".equals(caveType)) {
            threshold = 0.25;
            yLevel = 25;
        }

        BlockState ore, fluid, stalactite, stalagmite;

        if (caveType.equals("asbestos")) {
            ore = ModBlocks.ORE_ASBESTOS.get().defaultBlockState();
            fluid = Blocks.AIR.defaultBlockState();
            stalactite = ModBlocks.STALACTITE_ASBESTOS.get().defaultBlockState();
            stalagmite = ModBlocks.STALAGMITE_ASBESTOS.get().defaultBlockState();
        } else {
            ore = ModBlocks.ORE_SULFUR.get().defaultBlockState();
            fluid = Blocks.AIR.defaultBlockState();
            stalactite = ModBlocks.STALACTITE_SULFUR.get().defaultBlockState();
            stalagmite = ModBlocks.STALAGMITE_SULFUR.get().defaultBlockState();
        }

        OreVeinPiece piece = new OreVeinPiece(box, seed, yLevel, maxRange, threshold, scale, ore, fluid, stalactite, stalagmite);
        return Optional.of(new GenerationStub(new BlockPos(x, yLevel, z), builder -> builder.addPiece(piece)));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructureTypes.ORE_CAVE.get();
    }
}