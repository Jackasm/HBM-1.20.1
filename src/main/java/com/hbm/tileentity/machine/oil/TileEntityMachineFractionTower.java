package com.hbm.tileentity.machine.oil;

import com.hbm.api.fluid.IFluidCopiable;
import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.inventory.FluidStackHBM;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.recipes.FractionRecipes;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.Tuple.Pair;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

public class TileEntityMachineFractionTower extends TileEntityMachineBase implements IFluidStandardTransceiver, IFluidCopiable {

    public FluidTankHBM[] tanks;

    public TileEntityMachineFractionTower(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_FRACTION_TOWER.get(), pos, state);
        tanks = new FluidTankHBM[3];
        tanks[0] = new FluidTankHBM(Fluids.HEAVYOIL.get(), 4000);
        tanks[1] = new FluidTankHBM(Fluids.BITUMEN.get(), 4000);
        tanks[2] = new FluidTankHBM(Fluids.SMEAR.get(), 4000);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Передача между сегментами по вертикали (снизу вверх)
        BlockEntity above = level.getBlockEntity(worldPosition.above(3));
        if (above instanceof TileEntityMachineFractionTower frac) {

            // 1) выравниваем типы танков у верхнего сегмента
            for (int i = 0; i < 3; i++) {
                frac.tanks[i].setType(tanks[i].getTankType());
            }

            // 2) считаем перенос
            int oil   = Math.min(tanks[0].getFill(), frac.tanks[0].getMaxFill() - frac.tanks[0].getFill());
            int left  = Math.min(frac.tanks[1].getFill(), tanks[1].getMaxFill() - tanks[1].getFill());
            int right = Math.min(frac.tanks[2].getFill(), tanks[2].getMaxFill() - tanks[2].getFill());

            // 3) применяем
            tanks[0].setFill(tanks[0].getFill() - oil);
            tanks[1].setFill(tanks[1].getFill() + left);
            tanks[2].setFill(tanks[2].getFill() + right);
            frac.tanks[0].setFill(frac.tanks[0].getFill() + oil);
            frac.tanks[1].setFill(frac.tanks[1].getFill() - left);
            frac.tanks[2].setFill(frac.tanks[2].getFill() - right);
        }

        setupTanks();
        updateConnections();

        if (level.getGameTime() % 10 == 0) {
            fractionate();
        }

        sendFluid();
        networkPackNT(50);
    }

    private void setupTanks() {
        Pair<FluidStackHBM, FluidStackHBM> quart = FractionRecipes.getFractions(tanks[0].getTankType());
        if (quart != null) {
            tanks[1].setType(quart.key().type);
            tanks[2].setType(quart.value().type);
        } else {
            tanks[0].setType(Fluids.NONE.get());
            tanks[1].setType(Fluids.NONE.get());
            tanks[2].setType(Fluids.NONE.get());
        }
    }

    private void fractionate() {
        Pair<FluidStackHBM, FluidStackHBM> quart = FractionRecipes.getFractions(tanks[0].getTankType());
        if (quart != null) {
            int left = quart.key().fill;
            int right = quart.value().fill;
            if (tanks[0].getFill() >= 100 && hasSpace(left, right)) {
                tanks[0].setFill(tanks[0].getFill() - 100);
                tanks[1].setFill(tanks[1].getFill() + left);
                tanks[2].setFill(tanks[2].getFill() + right);
            }
        }
    }

    private boolean hasSpace(int left, int right) {
        return tanks[1].getFill() + left <= tanks[1].getMaxFill()
                && tanks[2].getFill() + right <= tanks[2].getMaxFill();
    }

    private void updateConnections() {
        for (var pos : getConPos()) {
            this.trySubscribe(tanks[0].getTankType(), level, pos.pos(), pos.dir());
        }
    }

    private void sendFluid() {
        for (var pos : getConPos()) {
            this.sendFluid(tanks[1], level, pos.pos(), pos.dir());
            this.sendFluid(tanks[2], level, pos.pos(), pos.dir());
        }
    }

    private java.util.List<com.hbm.util.Library.PosDir> getConPos() {
        return java.util.List.of(
                new com.hbm.util.Library.PosDir(worldPosition.offset(2, 0, 0), Direction.EAST),
                new com.hbm.util.Library.PosDir(worldPosition.offset(-2, 0, 0), Direction.WEST),
                new com.hbm.util.Library.PosDir(worldPosition.offset(0, 0, 2), Direction.SOUTH),
                new com.hbm.util.Library.PosDir(worldPosition.offset(0, 0, -2), Direction.NORTH)
        );
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        for (int i = 0; i < 3; i++) tanks[i].serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        for (int i = 0; i < 3; i++) tanks[i].deserialize(buf);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        for (int i = 0; i < 3; i++) tanks[i].writeToNBT(nbt, "tank" + i);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        for (int i = 0; i < 3; i++) tanks[i].readFromNBT(nbt, "tank" + i);
    }

    private AABB renderBox;

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        if (renderBox == null) {
            renderBox = new AABB(
                    worldPosition.getX() - 1,
                    worldPosition.getY(),
                    worldPosition.getZ() - 1,
                    worldPosition.getX() + 2,
                    worldPosition.getY() + 3,
                    worldPosition.getZ() + 2
            );
        }
        return renderBox;
    }

    // --- IFluidStandardTransceiver ---

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return new FluidTankHBM[] { tanks[1], tanks[2] };
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[] { tanks[0] };
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return tanks;
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && (type == tanks[0].getTankType()
                || type == tanks[1].getTankType()
                || type == tanks[2].getTankType());
    }

    // --- IFluidCopiable ---

    @Override
    public FluidTankHBM getTankToPaste() {
        return tanks[0];
    }
}