package com.hbm.datagen.worldgen.structure;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.WorldConfig;
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
 * Нефтяная залежь: эллипсоид из ORE_OIL в камне.
 * Частота как в 1.7.10 MapGenBubble: 1/100 чанков, в жарких сухих биомах — 1/33.
 */
public class OilBubbleStructure extends Structure {

    public static final Codec<OilBubbleStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(StructureSettings.CODEC.fieldOf("settings").forGetter(s -> s.settings)
            ).apply(instance, OilBubbleStructure::new));

    private final StructureSettings settings;

    public OilBubbleStructure(StructureSettings settings) {
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

        // 1.7.10: effecFreq /= 3 в жарких сухих биомах
        boolean hotDry = biome.getBaseTemperature() >= 2.0F && !biome.hasPrecipitation();
        int frequency = hotDry ? Math.max(1, WorldConfig.OIL_SPAWN / 3) : WorldConfig.OIL_SPAWN;
        if (context.random().nextInt(frequency) != 0) {
            return Optional.empty();
        }

        int x = context.chunkPos().getMinBlockX() + 8;
        int z = context.chunkPos().getMinBlockZ() + 8;
        int y = 15 + context.random().nextInt(25);
        int radius = 8 + context.random().nextInt(8);

        int minY = context.heightAccessor().getMinBuildHeight();
        int maxY = context.heightAccessor().getMaxBuildHeight() - 1;
        int clampedY = Math.min(Math.max(y, minY + radius), maxY - radius);

        BlockPos center = new BlockPos(x, clampedY, z);
        BoundingBox box = new BoundingBox(
                x - radius, clampedY - radius, z - radius,
                x + radius, clampedY + radius, z + radius
        );

        OilBubblePiece piece = new OilBubblePiece(box, center, radius,
                Blocks.STONE.defaultBlockState(), ModBlocks.ORE_OIL.get().defaultBlockState(),
                false, false, false);
        return Optional.of(new GenerationStub(center, builder -> builder.addPiece(piece)));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructureTypes.OIL_BUBBLE.get();
    }
}
