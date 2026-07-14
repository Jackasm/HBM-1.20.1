package com.hbm.tileentity.network;

import com.hbm.api.energy.EnergyNet;
import com.hbm.api.energy.IEnergyConnector;
import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.EnergyNodespace;
import com.hbm.uninos.GenNode;
import com.hbm.util.ColorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TileEntityPylonBase extends TileEntityCableBaseNT implements IEnergyConnector {

    protected List<int[]> connected = new ArrayList<>();
    public int color;

    public TileEntityPylonBase(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    public static int canConnect(TileEntityPylonBase first, TileEntityPylonBase second) {
        if (first.getConnectionType() != second.getConnectionType())
            return 1;

        if (first == second)
            return 2;

        double len = Math.min(first.getMaxWireLength(), second.getMaxWireLength());

        Vec3 firstPos = first.getConnectionPoint();
        Vec3 secondPos = second.getConnectionPoint();

        Vec3 delta = new Vec3(
                secondPos.x - firstPos.x,
                secondPos.y - firstPos.y,
                secondPos.z - firstPos.z
        );

        return len >= delta.length() ? 0 : 3; // 0 = можно, 3 = слишком далеко
    }

    public boolean setColor(ItemStack stack, Player player) {
        if (stack == null || stack.isEmpty()) return false;
        int color = ColorUtil.getColorFromDye(stack);
        if (color == 0 || color == this.color) return false;
        if (!player.isCreative()) stack.shrink(1);
        this.color = color;

        this.setChanged();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(worldPosition);
        }
        return true;
    }

    public EnergyNode createNode() {
        EnergyNode node = new EnergyNode(getBlockPos());
        List<GenNode.ConnectionPoint> points = new ArrayList<>();
        points.add(new GenNode.ConnectionPoint(getBlockPos(), null));
        for (int[] pos : this.connected) {
            BlockPos conPos = new BlockPos(pos[0], pos[1], pos[2]);
            points.add(new GenNode.ConnectionPoint(conPos, null));
        }
        return node.setConnections(points.toArray(new GenNode.ConnectionPoint[0]));
    }

    public void addConnection(int x, int y, int z) {
        connected.add(new int[]{x, y, z});
        BlockPos remotePos = new BlockPos(x, y, z);

        EnergyNode localNode = this.node;
        EnergyNode remoteNode = EnergyNodespace.getNode(level, remotePos);

        if (localNode != null && localNode.hasValidNet() && remoteNode != null && remoteNode.hasValidNet()) {
            EnergyNet localNet = localNode.getNet();
            EnergyNet remoteNet = remoteNode.getNet();
            if (localNet != null && remoteNet != null && localNet != remoteNet) {
                localNet.merge(remoteNet);
            }
        } else if (remoteNode != null && remoteNode.hasValidNet() && (localNode == null || !localNode.hasValidNet())) {
            if (this.node == null || this.node.expired) {
                this.node = this.createNode();
                EnergyNodespace.createNode(level, this.node);
            }
            remoteNode.getNet().joinLink(this.node);
        } else if (localNode != null && localNode.hasValidNet() && (remoteNode == null || !remoteNode.hasValidNet())) {
            remoteNode = remoteNode != null ? remoteNode : EnergyNodespace.getNode(level, remotePos);
            if (remoteNode == null) {
                BlockEntity te = Objects.requireNonNull(level).getBlockEntity(remotePos);
                if (te instanceof TileEntityPylonBase remotePylon) {
                    remoteNode = remotePylon.createNode();
                    EnergyNodespace.createNode(level, remoteNode);
                }
            }
            if (remoteNode != null && localNode.getNet() != null) {
                localNode.getNet().joinLink(remoteNode);
            }
        } else {
            if (this.node == null || this.node.expired) {
                this.node = this.createNode();
                EnergyNodespace.createNode(level, this.node);
            }
            if (remoteNode == null) {
                BlockEntity te = Objects.requireNonNull(level).getBlockEntity(remotePos);
                if (te instanceof TileEntityPylonBase remotePylon) {
                    remoteNode = remotePylon.createNode();
                    EnergyNodespace.createNode(level, remoteNode);
                }
            }
        }

        setChanged();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(worldPosition);
        }
    }

    public void disconnectAll() {
        for (int[] pos : connected) {
            BlockPos conPos = new BlockPos(pos[0], pos[1], pos[2]);
            BlockEntity te = Objects.requireNonNull(level).getBlockEntity(conPos);

            if (te == this) continue;

            if (te instanceof TileEntityPylonBase pylon) {
                EnergyNodespace.destroyNode(level, conPos);

                pylon.connected.removeIf(con -> con[0] == worldPosition.getX()
                        && con[1] == worldPosition.getY()
                        && con[2] == worldPosition.getZ());

                pylon.setChanged();
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.getChunkSource().blockChanged(pylon.worldPosition);
                }
            }
        }

        EnergyNodespace.destroyNode(level, worldPosition);
    }

    public abstract ConnectionType getConnectionType();
    public abstract Vec3[] getMountPos();
    public abstract double getMaxWireLength();

    public Vec3 getConnectionPoint() {
        Vec3[] mounts = this.getMountPos();
        if (mounts == null || mounts.length == 0)
            return new Vec3(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5);
        return mounts[0].add(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    }

    public List<int[]> getConnected() {
        return connected;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        ListTag list = new ListTag();
        for (int[] pos : connected) {
            list.add(new IntArrayTag(pos));
        }
        nbt.put("connected", list);
        nbt.putInt("color", color);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        connected.clear();
        ListTag list = nbt.getList("connected", Tag.TAG_INT_ARRAY);
        for (int i = 0; i < list.size(); i++) {
            connected.add(list.getIntArray(i));
        }
        this.color = nbt.getInt("color");
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        this.saveAdditional(nbt);
        return nbt;
    }

    public enum ConnectionType {
        SINGLE,
        TRIPLE,
        QUAD
    }
}