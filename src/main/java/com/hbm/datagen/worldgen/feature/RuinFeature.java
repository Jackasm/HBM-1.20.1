package com.hbm.datagen.worldgen.feature;

import com.hbm.datagen.worldgen.structure.Ruin001;
import com.hbm.datagen.worldgen.structure.RuinConfiguration;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

public class RuinFeature extends Feature<RuinConfiguration> {

    public RuinFeature(Codec<RuinConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<RuinConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();

        // Определяем какую руину генерировать
        switch (context.config().index) {
            case 1:
                new Ruin001().generate(level, origin.getX() >> 4, origin.getZ() >> 4, context.random());
                break;
            // Добавить другие руины
            default:
                new Ruin001().generate(level, origin.getX() >> 4, origin.getZ() >> 4, context.random());
                return true;
        }

        return true;
    }
}