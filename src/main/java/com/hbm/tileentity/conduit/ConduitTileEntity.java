package com.hbm.tileentity.conduit;

import com.hbm.api.energy.IEnergyConductor;
import com.hbm.api.energy.IEnergyConnector;
import com.hbm.api.fluid.IFluidConnector;
import com.hbm.api.fluid.IFluidPipe;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ConduitTileEntity extends TileEntityLoadedBase implements IEnergyConnector, IEnergyConductor, IFluidConnector, IFluidPipe {

    private final List<ConduitChannel> channels = new ArrayList<>();
    private final Map<ConduitEntry, ConduitChannel> channelMap = new LinkedHashMap<>();

    public ConduitTileEntity(BlockPos pos, BlockState state) {
        super(ModTileEntity.CONDUIT.get(), pos, state);
    }

    public Map<ConduitEntry, ConduitChannel> getChannelMap() {
        return channelMap;
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        for (ConduitChannel channel : channels) {
            if (channel instanceof FluidChannel fluidChan && fluidChan.getEntry().fluid == type) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canConnect(Direction dir) {
        for (ConduitChannel channel : channels) {
            if (channel instanceof EnergyChannel) {
                return true;
            }
        }
        return false;
    }

    public boolean addChannel(ConduitEntry entry) {
        if (channels.size() >= 4) return false;

        if (entry.type == ConduitEntry.ChannelType.ENERGY) {
            for (ConduitChannel ch : channels) {
                if (ch instanceof EnergyChannel) return false;
            }
        } else if (entry.type == ConduitEntry.ChannelType.FLUID) {
            if (entry.fluid == Fluids.NONE.get()) {
                for (ConduitChannel ch : channels) {
                    if (ch instanceof FluidChannel fc && fc.getEntry().fluid == Fluids.NONE.get()) {
                        return false;
                    }
                }
            } else {
                if (channelMap.containsKey(entry)) return false;
            }
        }

        ConduitChannel channel;
        if (entry.type == ConduitEntry.ChannelType.ENERGY) {
            channel = new EnergyChannel();
        } else {
            channel = new FluidChannel(entry);
        }
        channels.add(channel);
        channelMap.put(entry, channel);
        setChanged();
        updateConnections();
        return true;
    }

    public void removeChannel(ConduitEntry entry) {
        ConduitChannel channel = channelMap.remove(entry);
        if (channel != null) {
            channels.remove(channel);
            setChanged();
            updateConnections();
        }
    }

    public void removeLastChannel() {
        if (!channels.isEmpty()) {
            ConduitChannel last = channels.get(channels.size() - 1);
            channelMap.remove(last.getEntry());
            channels.remove(last);
            setChanged();
            updateConnections();
        }
    }

    public List<ConduitChannel> getChannels() {
        return Collections.unmodifiableList(channels);
    }

    public void updateConnections() {
        if (level == null || level.isClientSide) return;

        boolean changed = false;
        for (ConduitChannel channel : channels) {
            EnumSet<Direction> oldSides = channel.getConnections();
            EnumSet<Direction> newSides = EnumSet.noneOf(Direction.class);
            for (Direction dir : Direction.values()) {
                BlockPos neighborPos = worldPosition.relative(dir);
                BlockEntity neighbor = level.getBlockEntity(neighborPos);
                if (canConnectTo(channel, neighbor, dir)) {
                    newSides.add(dir);
                }
            }
            if (!oldSides.equals(newSides)) {
                channel.setConnections(newSides);
                changed = true;
            }
        }
        if (changed) {
            setChanged();
        }
    }

    private boolean canConnectTo(ConduitChannel channel, BlockEntity neighbor, Direction dir) {
        if (neighbor instanceof ConduitTileEntity other) {
            return other.channelMap.containsKey(channel.getEntry());
        }

        if (channel instanceof EnergyChannel) {
            if (neighbor instanceof IEnergyConnector connector) {
                return connector.canConnect(dir.getOpposite());
            }
        } else if (channel instanceof FluidChannel fluidChan) {
            if (neighbor instanceof IFluidConnector connector) {
                return connector.canConnect(fluidChan.getEntry().fluid, dir.getOpposite());
            }
        }
        return false;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        updateConnections();
        networkPackNT(20);

        for (ConduitChannel channel : channels) {
            channel.tick(this);
        }
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        channels.clear();
        channelMap.clear();
        int size = tag.getInt("channels");
        for (int i = 0; i < size; i++) {
            CompoundTag chanTag = tag.getCompound("chan_" + i);
            ConduitEntry entry = ConduitEntry.load(chanTag);
            ConduitChannel channel;
            if (entry.type == ConduitEntry.ChannelType.ENERGY) {
                channel = new EnergyChannel();
            } else {
                channel = new FluidChannel(entry);
            }
            channels.add(channel);
            channelMap.put(entry, channel);
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("channels", channels.size());
        int i = 0;
        for (ConduitChannel channel : channels) {
            CompoundTag chanTag = new CompoundTag();
            channel.getEntry().save(chanTag);
            tag.put("chan_" + i, chanTag);
            i++;
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeInt(channels.size());
        for (ConduitChannel channel : channels) {
            buf.writeByte(channel.getEntry().type.ordinal());
            if (channel instanceof FluidChannel fluidChan) {
                buf.writeInt(Fluids.getID(fluidChan.getEntry().fluid));
                buf.writeByte(fluidChan.getEntry().pipeType);
            }
            int mask = 0;
            for (Direction dir : channel.getConnections()) {
                mask |= 1 << dir.ordinal();
            }
            buf.writeByte(mask);
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        int size = buf.readInt();
        channels.clear();
        channelMap.clear();
        for (int i = 0; i < size; i++) {
            byte type = buf.readByte();
            ConduitEntry entry;
            if (type == 0) {
                entry = ConduitEntry.energy();
            } else {
                int fluidId = buf.readInt();
                int pipeType = buf.readByte();
                entry = ConduitEntry.fluid(Fluids.fromID(fluidId), pipeType);
            }
            ConduitChannel channel = entry.type == ConduitEntry.ChannelType.ENERGY
                    ? new EnergyChannel()
                    : new FluidChannel(entry);
            byte mask = buf.readByte();
            EnumSet<Direction> sides = EnumSet.noneOf(Direction.class);
            for (Direction dir : Direction.values()) {
                if ((mask & (1 << dir.ordinal())) != 0) {
                    sides.add(dir);
                }
            }
            channel.setConnections(sides);
            channels.add(channel);
            channelMap.put(entry, channel);
        }
    }
}