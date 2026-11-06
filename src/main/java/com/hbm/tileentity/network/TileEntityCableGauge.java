package com.hbm.tileentity.network;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityCableGauge extends TileEntityCableBaseNT {

    private long deltaTick = 0;
    private long deltaSecond = 0;
    private long deltaLastSecond = 0;

    public TileEntityCableGauge(BlockPos pos, BlockState state) {
        super(ModTileEntity.CABLE_GAUGE.get(), pos, state);
    }

    public void tick() {
        super.tick();
        if (level == null || level.isClientSide) return;

        if (this.node != null && this.node.net != null) {
            this.deltaTick = this.node.getNet().energyTracker;

            if (level.getGameTime() % 20 == 0) {
                this.deltaLastSecond = this.deltaSecond;
                this.deltaSecond = 0;
            }
            this.deltaSecond += deltaTick;
        }

        networkPackNT(25);
    }

    public long getDeltaTick() { return deltaTick; }
    public long getDeltaLastSecond() { return deltaLastSecond; }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putLong("deltaTick", deltaTick);
        nbt.putLong("deltaLastSecond", deltaLastSecond);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.deltaTick = nbt.getLong("deltaTick");
        this.deltaLastSecond = nbt.getLong("deltaLastSecond");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        tag.putLong("deltaTick", deltaTick);
        tag.putLong("deltaLastSecond", deltaLastSecond);
        return tag;
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        super.onDataPacket(net, pkt);
        if (pkt.getTag() != null) {
            this.deltaTick = Math.max(pkt.getTag().getLong("deltaTick"), 0);
            this.deltaLastSecond = Math.max(pkt.getTag().getLong("deltaLastSecond"), 0);
        }
    }
}