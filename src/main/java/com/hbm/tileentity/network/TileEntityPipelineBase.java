package com.hbm.tileentity.network;

import com.hbm.api.fluid.FluidNode;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.uninos.GenNode;
import com.hbm.uninos.UniNodespace;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public abstract class TileEntityPipelineBase extends TileEntityPipeBaseNT {

    public TileEntityPipelineBase(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    protected List<int[]> connected = new ArrayList<>();

    @Override
    public FluidNode createNode(FluidTypeHBM type) {

        // Создаём массив соединений из сохранённых позиций
        GenNode.ConnectionPoint[] connections = new GenNode.ConnectionPoint[this.connected.size()];
        for (int i = 0; i < this.connected.size(); i++) {
            int[] conn = this.connected.get(i);
            BlockPos connPos = new BlockPos(conn[0], conn[1], conn[2]);
            // Если направление не критично, можно использовать NORTH
            connections[i] = new GenNode.ConnectionPoint(connPos, Direction.NORTH);
        }

        FluidNode node = new FluidNode(type.getNetworkProvider(), this.worldPosition);
        if (connections.length > 0) {
            node.setConnections(connections);
        }

        return node;
    }

    public void addConnection(int x, int y, int z) {
        connected.add(new int[]{x, y, z});

        FluidNode node = (FluidNode) UniNodespace.getNode(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), this.type.getNetworkProvider());
        if (node != null) {

            // Определяем направление между текущим блоком и целевым
            BlockPos toPos = new BlockPos(x, y, z);
            Direction dir = getDirectionTo(this.worldPosition, toPos);

            node.addConnection(toPos, dir);
        }

        setChanged();

        if (level instanceof ServerLevel serverLevel) {
            serverLevel.getChunkSource().blockChanged(worldPosition);
        }
    }

    // Вспомогательный метод для определения направления
    private Direction getDirectionTo(BlockPos from, BlockPos to) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();
        int dz = to.getZ() - from.getZ();

        if (Math.abs(dx) > Math.abs(dy) && Math.abs(dx) > Math.abs(dz)) {
            return dx > 0 ? Direction.EAST : Direction.WEST;
        } else if (Math.abs(dy) > Math.abs(dz)) {
            return dy > 0 ? Direction.UP : Direction.DOWN;
        } else {
            return dz > 0 ? Direction.SOUTH : Direction.NORTH;
        }
    }

    public void disconnectAll() {
        for (int[] pos : connected) {
            BlockEntity te = Objects.requireNonNull(level).getBlockEntity(new BlockPos(pos[0], pos[1], pos[2]));
            if (te == this) continue;

            if (te instanceof TileEntityPipelineBase pipeline) {
                UniNodespace.destroyNode(level, pos[0], pos[1], pos[2], this.type.getNetworkProvider());

                for (int i = 0; i < pipeline.connected.size(); i++) {
                    int[] conPos = pipeline.connected.get(i);

                    if (conPos[0] == worldPosition.getX() && conPos[1] == worldPosition.getY() && conPos[2] == worldPosition.getZ()) {
                        pipeline.connected.remove(i);
                        i--;
                    }
                }

                pipeline.setChanged();

                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.getChunkSource().blockChanged(new BlockPos(pipeline.worldPosition.getX(), pipeline.worldPosition.getY(), pipeline.worldPosition.getZ()));
                }
            }
        }

        UniNodespace.destroyNode(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), this.type.getNetworkProvider());
        connected.clear();
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        disconnectAll();
    }

    /**
     * Returns a status code based on the operation.<br>
     * 0: Connected<br>
     * 1: Connections are incompatible<br>
     * 2: Both parties are the same block<br>
     * 3: Connection length exceeds maximum
     * 4: Pipeline fluid types do not match
     */
    public static int canConnect(TileEntityPipelineBase first, TileEntityPipelineBase second) {
        if (first.getConnectionType() != second.getConnectionType()) return 1;
        if (first == second) return 2;
        if (first.type != second.type) return 4;

        double len = Math.min(first.getMaxPipeLength(), second.getMaxPipeLength());

        Vec3 firstPos = first.getConnectionPoint();
        Vec3 secondPos = second.getConnectionPoint();

        Vec3 delta = new Vec3(
                secondPos.x - firstPos.x,
                secondPos.y - firstPos.y,
                secondPos.z - firstPos.z
        );

        return len >= delta.length() ? 0 : 3;
    }

    public abstract ConnectionType getConnectionType();
    public abstract Vec3 getMountPos();
    public abstract double getMaxPipeLength();

    public Vec3 getConnectionPoint() {
        Vec3 mount = this.getMountPos();
        return new Vec3(
                worldPosition.getX() + mount.x,
                worldPosition.getY() + mount.y,
                worldPosition.getZ() + mount.z
        );
    }

    public List<int[]> getConnected() {
        return connected;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);

        nbt.putInt("conCount", connected.size());

        for (int i = 0; i < connected.size(); i++) {
            nbt.putIntArray("con" + i, connected.get(i));
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);

        int count = nbt.getInt("conCount");

        this.connected.clear();

        for (int i = 0; i < count; i++) {
            this.connected.add(nbt.getIntArray("con" + i));
        }
    }

    @Override
    @Nullable
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        CompoundTag nbt = new CompoundTag();
        this.saveAdditional(nbt);
        return ClientboundBlockEntityDataPacket.create(this, (blockEntity) -> nbt);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        this.load(pkt.getTag());
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag nbt = super.getUpdateTag();
        this.saveAdditional(nbt);
        return nbt;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        this.load(tag);
    }

    public enum ConnectionType {
        SMALL
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

}