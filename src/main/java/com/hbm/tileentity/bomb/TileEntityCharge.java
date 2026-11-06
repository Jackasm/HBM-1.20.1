package com.hbm.tileentity.bomb;

import com.hbm.blocks.bomb.BlockChargeBase;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class TileEntityCharge extends TileEntityLoadedBase {

    public boolean started;
    public int timer;

    public TileEntityCharge(BlockPos pos, BlockState state) {
        super(ModTileEntity.CHARGE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (started) {
            timer--;

            if (timer % 20 == 0 && timer > 0) {
                level.playSound(null, worldPosition, ModSounds.FSTBMB_PING.get(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            if (timer <= 0) {
                if (getBlockState().getBlock() instanceof BlockChargeBase charge) {
                    charge.explode(level, worldPosition);
                }
            }
        }

        networkPackNT(100);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(this.timer);
        buf.writeBoolean(this.started);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.timer = buf.readInt();
        this.started = buf.readBoolean();
    }

    public String getMinutes() {
        String mins = "" + (timer / 1200);
        if (mins.length() == 1) mins = "0" + mins;
        return mins;
    }

    public String getSeconds() {
        String secs = "" + ((timer / 20) % 60);
        if (secs.length() == 1) secs = "0" + secs;
        return secs;
    }

    @Override
    protected void saveAdditional(net.minecraft.nbt.@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("timer", timer);
        nbt.putBoolean("started", started);
    }

    @Override
    public void load(net.minecraft.nbt.@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.timer = nbt.getInt("timer");
        this.started = nbt.getBoolean("started");
    }
}