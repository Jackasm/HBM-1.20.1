package com.hbm.tileentity.machine;

import com.hbm.api.entity.RadarEntry;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class TileEntityMachineRadarScreen extends TileEntityLoadedBase implements IBufPacketReceiver {

    public List<RadarEntry> entries = new ArrayList<>();
    public int refX;
    public int refY;
    public int refZ;
    public int range;
    public boolean linked;

    public TileEntityMachineRadarScreen(BlockPos pos, BlockState state) {
        super(ModTileEntity.RADAR_SCREEN.get(), pos, state);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            this.setChanged();
            entries.clear();
            this.linked = false;
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        FriendlyByteBuf fBuf = new FriendlyByteBuf(buf);
        fBuf.writeBoolean(linked);
        fBuf.writeInt(refX);
        fBuf.writeInt(refY);
        fBuf.writeInt(refZ);
        fBuf.writeInt(range);
        fBuf.writeInt(entries.size());
        for (RadarEntry entry : entries) {
            entry.toBytes(fBuf);
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        FriendlyByteBuf fBuf = new FriendlyByteBuf(buf);
        linked = fBuf.readBoolean();
        refX = fBuf.readInt();
        refY = fBuf.readInt();
        refZ = fBuf.readInt();
        range = fBuf.readInt();
        int count = fBuf.readInt();
        this.entries.clear();
        for (int i = 0; i < count; i++) {
            RadarEntry entry = new RadarEntry();
            entry.fromBytes(fBuf);
            this.entries.add(entry);
        }
    }

    private AABB renderBoundingBox = null;

    @Override
    public AABB getRenderBoundingBox() {
        if (renderBoundingBox == null) {
            renderBoundingBox = new AABB(
                    worldPosition.getX() - 1,
                    worldPosition.getY(),
                    worldPosition.getZ() - 1,
                    worldPosition.getX() + 2,
                    worldPosition.getY() + 2,
                    worldPosition.getZ() + 2
            );
        }
        return renderBoundingBox;
    }
}