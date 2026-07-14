package com.hbm.tileentity;

import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.api.fluid.IFluidUser;
import com.hbm.interfaces.ICopiable;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.util.BobMathUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.ArrayList;

public interface IFluidCopiable extends ICopiable {

    /**
     * @return First type for the normal paste, second type for the alt paste,
     *         none if there is no alt paste support
     */
    default int[] getFluidIDToCopy() {
        IFluidUser tile = (IFluidUser) this;
        ArrayList<Integer> types = new ArrayList<>();

        for (FluidTankHBM tank : tile.getAllTanks()) {
            if (!tank.getTankType().hasNoID())
                types.add(Fluids.getID(tank.getTankType()));
        }

        return BobMathUtil.intCollectionToArray(types);
    }

    default FluidTankHBM getTankToPaste() {
        BlockEntity te = (BlockEntity) this;
        if (te instanceof IFluidStandardTransceiver tile) {
            return tile.getReceivingTanks() != null && tile.getReceivingTanks().length > 0 ? tile.getReceivingTanks()[0] : null;
        }
        return null;
    }

    @Override
    default CompoundTag getSettings(Level world, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        int[] fluidIDs = getFluidIDToCopy();
        if (fluidIDs.length > 0) {
            tag.putIntArray("fluidID", fluidIDs);
        }
        return tag;
    }

    @Override
    default void pasteSettings(CompoundTag nbt, int index, Level world, Player player, BlockPos pos) {
        FluidTankHBM tank = getTankToPaste();
        if (tank != null) {
            int[] ids = nbt.getIntArray("fluidID");
            if (ids.length > 0 && index < ids.length) {
                int id = ids[index];
                tank.setType(Fluids.fromID(id));
            }
        }
    }

    @Override
    default String[] infoForDisplay(Level world, BlockPos pos) {
        int[] ids = getFluidIDToCopy();
        String[] names = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            names[i] = Fluids.fromID(ids[i]).getLocalizedName();
        }
        return names;
    }
}