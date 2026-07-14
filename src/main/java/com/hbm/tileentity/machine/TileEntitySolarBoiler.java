package com.hbm.tileentity.machine;

import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.tileentity.IBufPacketReceiver;
import com.hbm.tileentity.IFluidCopiable;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;

public class TileEntitySolarBoiler extends TileEntityLoadedBase implements IFluidStandardTransceiver, IBufPacketReceiver, IFluidCopiable {

    private FluidTankHBM water;
    private FluidTankHBM steam;
    public int display;
    public int heat;

    public HashSet<BlockPos> primary = new HashSet<>();
    public HashSet<BlockPos> secondary = new HashSet<>();

    public TileEntitySolarBoiler(BlockPos pos, BlockState state) {
        super(ModTileEntity.SOLAR_BOILER.get(), pos, state);
        water = new FluidTankHBM(Fluids.WATER.get(), 100);
        steam = new FluidTankHBM(Fluids.STEAM.get(), 10_000);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            this.trySubscribe(water.getTankType(), level, worldPosition.above(3), Direction.UP);
            this.trySubscribe(water.getTankType(), level, worldPosition.below(1), Direction.DOWN);

            int process = heat / 50;
            this.display = process;
            process = Math.min(process, water.getFill());
            process = Math.min(process, (steam.getMaxFill() - steam.getFill()) / 100);

            if (process < 0) process = 0;

            water.setFill(water.getFill() - process);
            steam.setFill(steam.getFill() + process * 100);

            this.tryProvide(steam, level, worldPosition.above(3), Direction.UP);
            this.tryProvide(steam, level, worldPosition.below(1), Direction.DOWN);

            heat = 0;

            networkPackNT(15);
        } else {
            secondary.clear();
            secondary.addAll(primary);
            primary.clear();
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        this.water.readFromNBT(nbt, "water");
        this.steam.readFromNBT(nbt, "steam");
    }

    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        this.water.writeToNBT(nbt, "water");
        this.steam.writeToNBT(nbt, "steam");
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
                    worldPosition.getY() + 3,
                    worldPosition.getZ() + 2
            );
        }
        return renderBoundingBox;
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return new FluidTankHBM[]{steam};
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[]{water};
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return new FluidTankHBM[]{water, steam};
    }

    public HashSet<BlockPos> getSecondaries() {
        return this.secondary;
    }

    @Override
    public void serialize(ByteBuf buf) {
        buf.writeInt(display);
        water.serialize(buf);
        steam.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        this.display = buf.readInt();
        water.deserialize(buf);
        steam.deserialize(buf);
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        return null;
    }

    @Override
    public int[] getFluidIDToCopy() {
        return new int[]{Fluids.getID(water.getTankType()), Fluids.getID(steam.getTankType())};
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir == Direction.UP || dir == Direction.DOWN;
    }
}