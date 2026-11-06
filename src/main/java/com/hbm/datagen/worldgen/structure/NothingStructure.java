package com.hbm.datagen.worldgen.structure;

import com.hbm.datagen.worldgen.structure.pieces.EmptyStructurePiece;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * «Пустой исход» выбора структуры: вес, который ничего не ставит.
 *
 * <p>В 1.7.10 у выбора структуры были веса-пустышки ({@code registerNullWeight}):
 * в равнинах 4, в океане 15 из общей суммы. В 1.20 пустой {@code Optional} не
 * годится: ванильный выбор считает неудачей и пробует следующую структуру из
 * набора, то есть пустышка сработала бы как «ещё одна попытка». Поэтому кусок
 * пустышки валиден, но ничего не ставит: клетка 12×12 считается занятой, и
 * структура в ней не появляется.</p>
 */
public class NothingStructure extends Structure {

    public static final Codec<NothingStructure> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    StructureSettings.CODEC.fieldOf("settings").forGetter(s -> s.settings)
            ).apply(instance, NothingStructure::new)
    );

    private final StructureSettings settings;

    public NothingStructure(StructureSettings settings) {
        super(settings);
        this.settings = settings;
    }

    @Override
    protected @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {
        BlockPos pos = new BlockPos(context.chunkPos().getMinBlockX(), 0, context.chunkPos().getMinBlockZ());
        return Optional.of(new GenerationStub(pos, builder -> builder.addPiece(new EmptyStructurePiece(pos))));
    }

    @Override
    public @NotNull StructureType<?> type() {
        return ModStructureTypes.NOTHING.get();
    }
}
