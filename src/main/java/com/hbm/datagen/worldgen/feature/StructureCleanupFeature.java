package com.hbm.datagen.worldgen.feature;

import com.hbm.config.StructureConfig;
import com.hbm.datagen.worldgen.structure.cleanup.StructureCleanup;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

/**
 * Чистка габарита NBT-структур после декорации (#102).
 *
 * <p>Фича стоит на шаге {@code top_layer_modification} — позже деревьев и травы
 * ({@code vegetal_decoration}), поэтому видит уже наросший декор. Ставится в каждом
 * чанке Верхнего мира, работу делает {@link StructureCleanup}: если структур в чанке
 * нет, это дешёвая проверка.</p>
 */
public class StructureCleanupFeature extends Feature<NoneFeatureConfiguration> {

    public StructureCleanupFeature(Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        if (!StructureConfig.isCleanDecor()) return false;
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        StructureCleanup.clean(level, new ChunkPos(origin));
        return true;
    }
}
