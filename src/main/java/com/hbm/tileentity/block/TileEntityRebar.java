package com.hbm.tileentity.block;

import com.hbm.api.fluid.IFluidReceiver;
import com.hbm.api.rebar.RebarNetwork;
import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.items.tool.ItemRebarPlacer;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.uninos.GenNode;
import com.hbm.uninos.INetworkProvider;
import com.hbm.uninos.UniNodespace;

import com.hbm.uninos.networkproviders.RebarNetworkProvider;
import com.hbm.util.Library;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TileEntityRebar extends TileEntityLoadedBase implements IFluidReceiver, IBufPacketReceiver {

    public Block concrete;
    public int concreteMeta;
    public int progress;
    public int prevProgress;
    protected RebarNode node;
    public boolean hasConnection = false;

    public TileEntityRebar(BlockPos pos, BlockState state) {
        super(ModTileEntity.REBAR.get(), pos, state);
    }

    public TileEntityRebar setup(Block b, int m) {
        this.concrete = b;
        this.concreteMeta = m;
        return this;
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return type == Fluids.CONCRETE.get();
    }

    public void tick() {
        if (level == null) return;
        long time = level.getGameTime();

        if (!level.isClientSide) {
            if (prevProgress != progress) {
                level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
                prevProgress = progress;
            }

            if (this.progress >= 1000) {
                if (concrete != null && ItemRebarPlacer.isValidConk(concrete.asItem(), concreteMeta)) {
                    level.setBlock(worldPosition, concrete.defaultBlockState(), 3);
                } else {
                    level.setBlock(worldPosition, ModBlocks.CONCRETE_REBAR.get().defaultBlockState(), 3);
                }
                return;
            }

            if (time % 60 == 0) {
                for (Direction dir : Direction.values()) {
                    this.trySubscribe(Fluids.CONCRETE.get(), level, worldPosition.relative(dir), dir);
                }
            }

            if (this.node == null || this.node.expired) {
                this.node = (RebarNode) UniNodespace.getNode(level, worldPosition, RebarNetworkProvider.INSTANCE);
                if (this.node == null || this.node.expired) {
                    this.node = this.createNode();
                    UniNodespace.createNode(level, this.node);
                }
            }

            this.networkPackNT(100);
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (level != null && !level.isClientSide) {
            if (this.node != null) {
                UniNodespace.destroyNode(level, worldPosition, RebarNetworkProvider.INSTANCE);
            }
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeInt(progress);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        this.progress = buf.readInt();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.progress = nbt.getInt("progress");
        this.hasConnection = nbt.getBoolean("hasConnection");

        if (nbt.contains("block")) {
            this.concrete = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(nbt.getString("block")));
            this.concreteMeta = nbt.getInt("meta");
        }
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.putInt("progress", this.progress);
        nbt.putBoolean("hasConnection", this.hasConnection);

        if (this.concrete != null) {
            nbt.putString("block", BuiltInRegistries.BLOCK.getKey(this.concrete).toString());
            nbt.putInt("meta", this.concreteMeta);
        }
    }

    public RebarNode createNode() {
        return new RebarNode(RebarNetworkProvider.INSTANCE, worldPosition)
                .setConnections(
                        new Library.PosDir(worldPosition.offset(1, 0, 0), Direction.EAST),
                        new Library.PosDir(worldPosition.offset(-1, 0, 0), Direction.WEST),
                        new Library.PosDir(worldPosition.offset(0, 1, 0), Direction.UP),
                        new Library.PosDir(worldPosition.offset(0, -1, 0), Direction.DOWN),
                        new Library.PosDir(worldPosition.offset(0, 0, 1), Direction.SOUTH),
                        new Library.PosDir(worldPosition.offset(0, 0, -1), Direction.NORTH)
                );
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        FluidTankHBM tank = new FluidTankHBM(Fluids.CONCRETE.get(), 1000);
        tank.setFill(progress);
        return new FluidTankHBM[]{tank};
    }

    @Override
    public long transferFluid(FluidTypeHBM type, int pressure, long amount) {
        if (type != Fluids.CONCRETE.get()) return amount;
        if (this.node == null || this.node.expired || !this.node.hasValidNet()) return amount;

        List<TileEntityRebar> lowestLinks = new ArrayList<>();
        int lowestY = 256;
        int totalProgress = 0;
        int capacity = 0;

        for (RebarNode node : this.node.net.getLinks()) {
            int y = node.getPositions()[0].getY();

            if (y < lowestY) {
                lowestY = y;
                totalProgress = 0;
                capacity = 0;
                lowestLinks.clear();
            }

            if (y == lowestY) {
                BlockEntity te = Objects.requireNonNull(level).getBlockEntity(node.getPositions()[0]);
                if (!(te instanceof TileEntityRebar rebar)) continue;

                totalProgress += rebar.progress;
                capacity += 1000;
                lowestLinks.add(rebar);
            }
        }

        if (capacity > 0 && !lowestLinks.isEmpty()) {
            int maxSpeed = 50;
            long maxAccept = Math.min(capacity - totalProgress, Math.min(amount, maxSpeed * lowestLinks.size()));
            int target = (int) Math.min((totalProgress + maxAccept) / lowestLinks.size(), 1000);

            for (TileEntityRebar rebar : lowestLinks) {
                if (rebar.progress >= target) continue;
                int delta = target - rebar.progress;
                if (delta > amount) continue;

                rebar.progress += delta;
                amount -= delta;
            }
        }

        return amount;
    }

    @Override
    public long getDemand(FluidTypeHBM type, int pressure) {
        return 10000L;
    }

    public static class RebarNode extends GenNode<RebarNetwork> {

        public RebarNode(INetworkProvider<RebarNetwork> provider, BlockPos... positions) {
            super(provider, positions);
        }

        public RebarNode setConnections(Library.PosDir... connections) {
            BlockPos[] positions = new BlockPos[connections.length];
            Direction[] directions = new Direction[connections.length];
            for (int i = 0; i < connections.length; i++) {
                positions[i] = connections[i].pos();
                directions[i] = connections[i].dir();
            }
            super.setConnections(positions, directions);
            return this;
        }
    }
}
