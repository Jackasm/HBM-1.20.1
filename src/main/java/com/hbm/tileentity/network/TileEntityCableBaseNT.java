package com.hbm.tileentity.network;

import com.hbm.api.energy.EnergyNet;
import com.hbm.api.energy.IEnergyConductor;
import com.hbm.blocks.generic.CTBlock;
import com.hbm.blocks.generic.CTModelData;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.EnergyNodespace;
import com.hbm.tileentity.TileEntityLoadedBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.NotNull;

public class TileEntityCableBaseNT extends TileEntityLoadedBase implements IEnergyConductor {

    protected EnergyNode node;

    public TileEntityCableBaseNT(BlockPos pos, BlockState state) {
        super(ModTileEntity.RED_CABLE.get(), pos, state);
    }

    public TileEntityCableBaseNT(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Выключатель и другие узлы с условием: без узла, пока shouldCreateNode() false
        if (!shouldCreateNode()) {
            if (this.node != null) {
                EnergyNodespace.destroyNode(level, worldPosition);
                this.node = null;
            }
            return;
        }

        if (this.node == null || this.node.expired) {
            this.node = EnergyNodespace.getNode(level, worldPosition);
            if (this.node == null || this.node.expired) {
                this.node = this.createNode(this);
                EnergyNodespace.createNode(level, this.node);
            }
        }

        if (this.node != null && this.node.hasValidNet() && this instanceof TileEntityPylonBase pylon) {
            for (int[] con : pylon.getConnected()) {
                BlockPos conPos = new BlockPos(con[0], con[1], con[2]);
                EnergyNode remoteNode = EnergyNodespace.getNode(level, conPos);
                if (remoteNode != null && remoteNode.hasValidNet()) {
                    EnergyNet localNet = this.node.getNet();
                    EnergyNet remoteNet = remoteNode.getNet();
                    if (localNet != null && remoteNet != null && !localNet.equals(remoteNet)) {
                        localNet.merge(remoteNet);
                    }
                }
            }
        }
    }

    /**
     * Сеть не рушим: выгрузка чанка не должна убивать сеть целиком.
     * Реальный снос блока обрабатывает {@link #setRemoved()}.
     */
    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
    }

    /** Выставляется в onChunkUnloaded: при выгрузке чанка setRemoved тоже вызывается. */
    private boolean chunkUnloaded = false;

    @Override
    public void onChunkUnloaded() {
        this.chunkUnloaded = true;
        super.onChunkUnloaded();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();

        if (!chunkUnloaded && level != null && !level.isClientSide && this.node != null) {
            EnergyNodespace.destroyNode(level, worldPosition);
            this.node = null;
        }
    }

    public boolean shouldCreateNode() {
        return true;
    }

    /**
     * Провод в оболочке — CT-блок в эталоне ({@code WireCoated implements IBlockCT}), но отдельного
     * BlockEntity у него нет: данные модели считает этот же сетевой TE, иначе на каждый обмотанный
     * провод пришлось бы два BE.
     */
    @NotNull
    @Override
    public ModelData getModelData() {
        if (level == null) {
            return ModelData.EMPTY;
        }
        BlockState state = getBlockState();
        if (!(state.getBlock() instanceof CTBlock)) {
            return ModelData.EMPTY;
        }
        return ModelData.builder()
                .with(CTModelData.PROPERTY, CTModelData.compute(level, worldPosition))
                .build();
    }
}