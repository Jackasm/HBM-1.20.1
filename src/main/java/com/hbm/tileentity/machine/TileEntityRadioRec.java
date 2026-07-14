package com.hbm.tileentity.machine;

import com.hbm.interfaces.IControlReceiver;
import com.hbm.inventory.container.ContainerRadioRec;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.tileentity.network.RTTYSystem;
import com.hbm.tileentity.network.RTTYSystem.RTTYChannel;
import com.hbm.util.BufferUtil;
import com.hbm.util.NoteBuilder;
import com.hbm.util.NoteBuilder.Instrument;
import com.hbm.util.NoteBuilder.Note;
import com.hbm.util.NoteBuilder.Octave;
import com.hbm.util.Tuple.Triplet;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class TileEntityRadioRec extends TileEntityLoadedBase implements IControlReceiver, MenuProvider {

    public String channel = "";
    public boolean isOn = false;

    public TileEntityRadioRec(BlockPos pos, BlockState state) {
        super(ModTileEntity.RADIOREC.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (this.isOn && !this.channel.isEmpty()) {
            RTTYChannel chan = RTTYSystem.listen(level, this.channel);

            if (chan != null && chan.timeStamp == level.getGameTime() - 1) {
                Triplet<Instrument, Note, Octave>[] notes = NoteBuilder.translate(chan.signal + "");

                for (Triplet<Instrument, Note, Octave> note : notes) {
                    Instrument i = note.getX();
                    Note n = note.getY();
                    Octave o = note.getZ();

                    int noteId = n.ordinal() + o.ordinal() * 12;
                    SoundEvent sound;
                    String soundName = "note.harp";

                    if (i == Instrument.BASSDRUM) soundName = "note.bd";
                    else if (i == Instrument.SNARE) soundName = "note.snare";
                    else if (i == Instrument.CLICKS) soundName = "note.hat";
                    else if (i == Instrument.BASSGUITAR) soundName = "note.bassattack";

                    sound = SoundEvents.NOTE_BLOCK_HARP.get();
                    sound = switch (soundName) {
                        case "note.bd" -> SoundEvents.NOTE_BLOCK_BASEDRUM.value();
                        case "note.snare" -> SoundEvents.NOTE_BLOCK_SNARE.value();
                        case "note.hat" -> SoundEvents.NOTE_BLOCK_HAT.value();
                        case "note.bassattack" -> SoundEvents.NOTE_BLOCK_BASS.value();
                        default -> sound;
                    };

                    float f = (float) Math.pow(2.0D, (double) (noteId - 12) / 12.0D);
                    level.playSound(null, worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D,
                            sound, SoundSource.RECORDS, 3.0F, f);
                }
            }
        }

        networkPackNT(15);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        channel = nbt.getString("channel");
        isOn = nbt.getBoolean("isOn");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putString("channel", channel);
        nbt.putBoolean("isOn", isOn);
    }

    @Override
    public void serialize(ByteBuf buf) {
        BufferUtil.writeString(buf, this.channel);
        buf.writeBoolean(this.isOn);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        this.channel = BufferUtil.readString(buf);
        this.isOn = buf.readBoolean();
    }

    @Override
    public boolean hasPermission(Player player) {
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) < 256;
    }

    @Override
    public void receiveControl(CompoundTag data) {
        if (data.contains("channel")) this.channel = data.getString("channel");
        if (data.contains("isOn")) this.isOn = data.getBoolean("isOn");
        this.setChanged();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.radio_rec");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ContainerRadioRec(id, inventory, this);
    }



}