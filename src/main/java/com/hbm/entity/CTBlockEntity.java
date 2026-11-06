package com.hbm.entity;

import com.hbm.blocks.generic.CTModelData;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

/**
 * Носитель модели connected textures. Сам ничего не хранит и не тикает: данные модели
 * ({@link CTModelData}) считаются по миру в момент сборки чанка, поэтому рассинхрона
 * состояния блока и картинки быть не может.
 *
 * <p>Тип зарегистрирован под историческим именем {@code hbm:deco_ct} — так старые миры
 * не теряют блок-энтити; обслуживает он все CT-блоки, не только декоративные.</p>
 */
public class CTBlockEntity extends BlockEntity {

    public CTBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntity.CT_BLOCK_ENTITY.get(), pos, state);
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        if (level == null) {
            return ModelData.EMPTY;
        }
        return ModelData.builder()
                .with(CTModelData.PROPERTY, CTModelData.compute(level, worldPosition))
                .build();
    }
}
