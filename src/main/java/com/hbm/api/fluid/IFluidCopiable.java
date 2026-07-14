package com.hbm.api.fluid;

import com.hbm.interfaces.ICopiable;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.util.BobMathUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.ArrayList;

public interface IFluidCopiable extends ICopiable {

    /**
     * @return First type for the normal paste, second type for the alt paste,
     *         none if there is no alt paste support
     */
    default int[] getFluidIDToCopy() {
        if (!(this instanceof IFluidUser tile)) return new int[0];

        ArrayList<Integer> types = new ArrayList<>();

        for (FluidTankHBM tank : tile.getAllTanks()) {
            if (tank.getTankType() != Fluids.NONE.get()) {
                types.add(Fluids.getID(tank.getTankType()));
            }
        }

        return BobMathUtil.intCollectionToArray(types);
    }

    default FluidTankHBM getTankToPaste() {
        if (this instanceof IFluidStandardTransceiver tile) {
            FluidTankHBM[] tanks = tile.getReceivingTanks();
            return tanks != null && tanks.length > 0 ? tanks[0] : null;
        }
        return null;
    }

    @Override
    default CompoundTag getSettings(Level world, BlockPos pos) {
        CompoundTag tag = new CompoundTag();
        int[] ids = getFluidIDToCopy();
        if (ids.length > 0) {
            tag.putIntArray("fluidID", ids);
        }
        return tag;
    }

    @Override
    default void pasteSettings(CompoundTag nbt, int index, Level world, Player player, BlockPos pos) {
        FluidTankHBM tank = getTankToPaste();
        if (tank != null && nbt.contains("fluidID")) {
            int[] ids = nbt.getIntArray("fluidID");
            if (ids.length > 0 && index < ids.length) {
                int id = ids[index];
                FluidTypeHBM type = Fluids.fromID(id);
                if (type != null) {
                    tank.setType(type);
                }
            }
        }
    }

    @Override
    default String[] infoForDisplay(Level world, BlockPos pos) {
        int[] ids = getFluidIDToCopy();
        String[] names = new String[ids.length];
        for (int i = 0; i < ids.length; i++) {
            FluidTypeHBM type = Fluids.fromID(ids[i]);
            names[i] = type != null ? type.getLocalizedName() : "NONE";
        }
        return names;
    }
}
