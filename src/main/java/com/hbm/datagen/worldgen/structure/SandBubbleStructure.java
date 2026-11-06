package com.hbm.datagen.worldgen.structure;

import com.hbm.blocks.ModBlocks;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.QuartPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Песчаная нефтяная залежь: эллипсоид из ORE_OIL_SAND в песке, только в жарких сухих биомах.
 * Частота как в 1.7.10 MapGenBubble: 1/200 чанков.
 */
public class SandBubbleStructure extends Structure {

    /** В 1.7.10 sandBubbleSpawn был захардкожен как 200. */
    private static final int SAND_BUBBLE_SPAWN = 200;

    public static final Codec<SandBubbleStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(StructureSettings.CODEC.fieldOf("settings").forGetter(s -> s.settings)
            ).apply(instance, SandBubbleStructure::new));

    private final StructureSettings settings;

    public SandBubbleStructure(StructureSettings settings) {
        super(settings);
        this.settings = settings;
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(GenerationContext context) {
        Holder<Biome> biomeHolder = context.chunkGenerator().getBiomeSource().getNoiseBiome(
                QuartPos.fromBlock(context.chunkPos().getMinBlockX()),
                QuartPos.fromBlock(64),
                QuartPos.fromBlock(context.chunkPos().getMinBlockZ()),
                context.randomState().sampler());
        Biome biome = biomeHolder.value();

        if (!(biome.getBaseTemperature() >= 1.5F && !biome.hasPrecipitation())) {
            return Optional.empty();
        }

        if (context.random().nextInt(SAND_BUBBLE_SPAWN) != 0) {
            return Optional.empty();
        }

        int x = context.chunkPos().getMinBlockX() + 8;
        int z = context.chunkPos().getMinBlockZ() + 8;
        int y = 56 + context.random().nextInt(16);
        int radius = 16 + context.random().nextInt(32);

        // Эллипсоид сплюснут по Y в 3 раза
        int yRadius = (int) Math.ceil(radius / Math.sqrt(3));

        int minY = context.heightAccessor().getMinBuildHeight();
        int maxY = context.heightAccessor().getMaxBuildHeight() - 1;
        int clampedY = Math.min(Math.max(y, minY + yRadius), maxY - yRadius);

        BlockPos center = new BlockPos(x, clampedY, z);
        BoundingBox box = new BoundingBox(
                x - radius, clampedY - yRadius, z - radius,
                x + radius, clampedY + yRadius, z + radius
        );

        OilBubblePiece piece = new OilBubblePiece(box, center, radius,
                Blocks.SAND.defaultBlockState(), ModBlocks.ORE_OIL_SAND.get().defaultBlockState(),
                true, false, true);
        return Optional.of(new GenerationStub(center, builder -> builder.addPiece(piece)));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructureTypes.SAND_BUBBLE.get();
    }
}
