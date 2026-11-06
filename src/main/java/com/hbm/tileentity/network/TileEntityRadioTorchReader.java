package com.hbm.tileentity.network;

import com.hbm.api.redstoneoverradio.IRORInfo;
import com.hbm.api.redstoneoverradio.IRORValueProvider;
import com.hbm.blocks.network.RadioTorchBase;
import com.hbm.inventory.container.ContainerRadioTorch;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.BufferUtil;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

/**
 * RTTY-читатель: восемь пар «канал + имя значения». Опрашивает соседний блок,
 * реализующий {@link IRORValueProvider}, и передаёт значение в канал.
 *
 * <p>Порт {@code ref:tileentity/network/TileEntityRadioTorchReader.java}.
 * Как и в эталоне, имя значения приводится к нижнему регистру и склеивается с
 * префиксом {@code VAL:} — так машины объявляют свои значения в
 * {@code getFunctionInfo()}.</p>
 */
public class TileEntityRadioTorchReader extends TileEntityLoadedBase implements MenuProvider,
        com.hbm.interfaces.IControlReceiver {

    public final String[] channels = new String[8];
    public final String[] names = new String[8];
    public final String[] prev = new String[8];
    private final boolean[] forceUpdate = new boolean[8];
    public boolean polling = false;

    public TileEntityRadioTorchReader(BlockPos pos, BlockState state) {
        super(ModTileEntity.RADIO_TORCH_READER.get(), pos, state);
        for (int i = 0; i < 8; i++) {
            this.channels[i] = "";
            this.names[i] = "";
            this.prev[i] = "";
            this.forceUpdate[i] = true;
        }
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        BlockPos attach = worldPosition.relative(getBlockState().getValue(RadioTorchBase.FACING));
        BlockEntity be = level.getBlockEntity(attach);

        if (be instanceof IRORValueProvider provider) {
            for (int i = 0; i < 8; i++) {
                String channel = this.channels[i];
                String name = this.names[i];

                if (channel == null || channel.isEmpty()) continue;
                if (name == null || name.isEmpty()) continue;

                String value = provider.provideRORValue(
                        IRORInfo.PREFIX_VALUE + name.toLowerCase(Locale.US));
                if (value == null) continue;

                if (this.polling || this.forceUpdate[i] || !value.equals(this.prev[i])) {
                    RTTYSystem.broadcast(level, channel, value);
                    this.prev[i] = value;
                    this.forceUpdate[i] = false;
                }
            }
        }

        networkPackNT(50);
    }

    // === NBT ===

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.polling = nbt.getBoolean("p");
        for (int i = 0; i < 8; i++) {
            this.channels[i] = nbt.getString("c" + i);
            this.names[i] = nbt.getString("n" + i);
            this.prev[i] = nbt.getString("p" + i);
            this.forceUpdate[i] = true;
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putBoolean("p", this.polling);
        for (int i = 0; i < 8; i++) {
            if (this.channels[i] != null) nbt.putString("c" + i, this.channels[i]);
            if (this.names[i] != null) nbt.putString("n" + i, this.names[i]);
            if (this.prev[i] != null) nbt.putString("p" + i, this.prev[i]);
        }
    }

    // === Синхронизация ===

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeBoolean(this.polling);
        for (int i = 0; i < 8; i++) BufferUtil.writeString(buf, this.channels[i]);
        for (int i = 0; i < 8; i++) BufferUtil.writeString(buf, this.names[i]);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        this.polling = buf.readBoolean();
        for (int i = 0; i < 8; i++) this.channels[i] = BufferUtil.readString(buf);
        for (int i = 0; i < 8; i++) this.names[i] = BufferUtil.readString(buf);
    }

    public void receiveControl(Player player, CompoundTag data) {
        if (data.contains("p")) this.polling = data.getBoolean("p");
        for (int i = 0; i < 8; i++) {
            if (data.contains("c" + i)) this.setChannel(i, data.getString("c" + i));
            if (data.contains("n" + i)) this.setName(i, data.getString("n" + i));
        }
        setChanged();
    }

    private void setChannel(int index, String channel) {
        if (!channel.equals(this.channels[index])) {
            this.channels[index] = channel;
            this.forceUpdate[index] = true;
        }
    }

    private void setName(int index, String name) {
        if (!name.equals(this.names[index])) {
            this.names[index] = name;
            this.forceUpdate[index] = true;
        }
    }

    // === IControlReceiver ===

    @Override
    public void receiveControl(@NotNull CompoundTag data) {
        receiveControl(null, data);
    }

    @Override
    public boolean hasPermission(@NotNull Player player) {
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 256.0;
    }

    // === MenuProvider ===

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.rttyReader");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerRadioTorch(id, inv, this);
    }
}
