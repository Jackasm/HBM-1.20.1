package com.hbm.tileentity.network;

import com.hbm.api.fluid.FluidNode;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.uninos.UniNodespace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

public class TileEntityPipeExhaust extends TileEntityPipeBaseNT {

    private static final FluidTypeHBM[] SMOKES = {Fluids.SMOKE.get(), Fluids.SMOKE_LEADED.get(), Fluids.SMOKE_POISON.get()};

    /** У каждого типа дыма свой узел: один общий не даёт создать остальные. */
    private final FluidNode[] smokeNodes = new FluidNode[SMOKES.length];

    public TileEntityPipeExhaust(BlockPos pos, BlockState state) {
        super(ModTileEntity.FLUID_DUCT_EXHAUST.get(), pos, state);
    }

    @Override
    public void tick() {
        if (level == null || level.isClientSide) return;

        for (int i = 0; i < SMOKES.length; i++) {
            FluidNode node = smokeNodes[i];
            if (node == null || node.expired) {
                node = (FluidNode) UniNodespace.getNode(level, worldPosition, SMOKES[i].getNetworkProvider());

                if (node == null || node.expired) {
                    node = this.createNode(SMOKES[i]);
                    UniNodespace.createNode(level, node);
                }
                smokeNodes[i] = node;
            }
        }
    }

    @Override
    public boolean setFluidType(FluidTypeHBM type) {
        // Выхлопная труба не меняет тип жидкости, она работает с тремя типами дыма
        return false;
    }

    @Override
    public FluidTypeHBM getFluidType() {
        // Возвращаем первый тип дыма как основной (для рендера)
        return SMOKES[0];
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && (type == Fluids.SMOKE.get() || type == Fluids.SMOKE_LEADED.get() || type == Fluids.SMOKE_POISON.get());
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (!chunkUnloaded && level != null && !level.isClientSide) {
            for (FluidTypeHBM smoke : SMOKES) {
                UniNodespace.destroyNode(level, worldPosition, smoke.getNetworkProvider());
            }
        }
    }
}