package com.hbm.entity;

import com.hbm.blocks.generic.BlockDecoCTModelData;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class DecoCTBlockEntity extends BlockEntity {

    public DecoCTBlockEntity(BlockPos pos, BlockState state) {
        super(ModTileEntity.DECO_CT_BLOCK_ENTITY.get(), pos, state);
    }

    @NotNull
    @Override
    public ModelData getModelData() {
        if (level == null) return ModelData.EMPTY;

        return BlockDecoCTModelData.getModelData(level, worldPosition, getBlockState());
    }

}