package com.hbm.tileentity;

import com.hbm.api.tile.ILoadedTile;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.BufPacket;
import com.hbm.sound.AudioWrapper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class TileEntityLoadedBase extends BlockEntity implements ILoadedTile, IBufPacketReceiver {

    public boolean isLoaded = true;
    public boolean muffled = false;

    public TileEntityLoadedBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Override
    public boolean isLoaded() {
        return isLoaded;
    }

    @Override
    public void onChunkUnloaded() {
        super.onChunkUnloaded();
        this.isLoaded = false;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        this.isLoaded = false;
    }

    @Override
    public void clearRemoved() {
        super.clearRemoved();
        this.isLoaded = true;
    }

    @OnlyIn(Dist.CLIENT)
    public AudioWrapper createAudioLoop() {
        return null;
    }

    @OnlyIn(Dist.CLIENT)
    public AudioWrapper rebootAudio(AudioWrapper wrapper) {
        if (wrapper != null) {
            wrapper.stopSound();
        }
        AudioWrapper audio = createAudioLoop();
        if (audio != null) {
            audio.startSound();
        }
        return audio;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.muffled = nbt.getBoolean("muffled");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("muffled", muffled);
    }

    public float getVolume(float baseVolume) {
        return muffled ? baseVolume * 0.1F : baseVolume;
    }

    private ByteBuf lastPackedBuf;

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeBoolean(muffled);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        this.muffled = buf.readBoolean();
    }

    /**
     * Sends a sync packet that uses ByteBuf for efficient information-cramming
     */
    public void networkPackNT(int range) {
        if (level == null || level.isClientSide) return;

        ByteBuf buf = Unpooled.buffer();
        this.serialize(buf);

        boolean shouldSend = !buf.equals(lastPackedBuf) || level.getGameTime() % 20 == 0;

        if (shouldSend) {
            this.lastPackedBuf = buf.copy();

            BufPacket packet = new BufPacket(worldPosition, this);
            PacketDispatcher.sendToAllAround(packet, level, worldPosition, range);
        }
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            this.load(pkt.getTag());
        }
    }
}