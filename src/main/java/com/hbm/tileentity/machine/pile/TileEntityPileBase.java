package com.hbm.tileentity.machine.pile;

import com.hbm.handler.neutron.NeutronNodeWorld;
import com.hbm.handler.neutron.NeutronNodeWorld.StreamWorld;
import com.hbm.handler.neutron.PileNeutronHandler;
import com.hbm.handler.neutron.PileNeutronHandler.PileNeutronNode;
import com.hbm.handler.neutron.PileNeutronHandler.PileNeutronStream;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public abstract class TileEntityPileBase extends BlockEntity {

    public TileEntityPileBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null && !level.isClientSide) {
            NeutronNodeWorld.removeNode(level, this.worldPosition);
        }
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        if (level != null && !level.isClientSide) {
            NeutronNodeWorld.removeNode(level, this.worldPosition);
        }
    }

    protected void castRay(int flux) {
        BlockPos pos = this.worldPosition;

        if (flux == 0) {
            NeutronNodeWorld.removeNode(level, pos);
            return;
        }

        StreamWorld streamWorld = NeutronNodeWorld.getOrAddWorld(level);
        PileNeutronNode node = (PileNeutronNode) streamWorld.getNode(pos);

        if (node == null) {
            node = PileNeutronHandler.makeNode(streamWorld, this);
            streamWorld.addNode(node);
        }

        Vec3 neutronVector = new Vec3(1, 0, 0);

        neutronVector = neutronVector.zRot((float) (Math.PI * 2D * level.random.nextDouble()));
        neutronVector = neutronVector.yRot((float) (Math.PI * 2D * level.random.nextDouble()));
        neutronVector = neutronVector.xRot((float) (Math.PI * 2D * level.random.nextDouble()));

        new PileNeutronStream(node, neutronVector, flux);
    }
}