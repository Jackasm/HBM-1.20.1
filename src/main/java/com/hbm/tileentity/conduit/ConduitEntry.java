package com.hbm.tileentity.conduit;

import com.hbm.blocks.ModBlocks;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.items.block.ItemRedCable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class ConduitEntry {

    public final ChannelType type;

    @Nullable public final FluidTypeHBM fluid;
    public int pipeType;

    public enum ChannelType {
        ENERGY,
        FLUID
    }

    public ConduitEntry(ChannelType type, @Nullable FluidTypeHBM fluid, int pipeType) {
        this.type = type;
        this.fluid = fluid;
        this.pipeType = pipeType;
    }

    public static ConduitEntry energy() {
        return new ConduitEntry(ChannelType.ENERGY, null, 0);
    }

    public static ConduitEntry fluid(FluidTypeHBM fluid, int pipeType) {
        return new ConduitEntry(ChannelType.FLUID, fluid, pipeType);
    }

    public void save(CompoundTag tag) {
        tag.putInt("type", type.ordinal());
        if (type == ChannelType.FLUID && fluid != null) {
            tag.putInt("fluidId", Fluids.getID(fluid));
            tag.putInt("pipeType", pipeType);
        }
    }

    public static ConduitEntry load(CompoundTag tag) {
        ChannelType type = ChannelType.values()[tag.getInt("type")];
        if (type == ChannelType.FLUID) {
            FluidTypeHBM fluid = Fluids.fromID(tag.getInt("fluidId"));
            int pipeType = tag.contains("pipeType") ? tag.getInt("pipeType") : 0;
            return fluid(fluid, pipeType);
        }
        return energy();
    }

    @Nullable
    public static ConduitEntry fromStack(ItemStack stack) {
        if (stack.isEmpty()) return null;

        Item item = stack.getItem();

        if (item instanceof ItemRedCable) {
            return energy();
        }

        FluidTypeHBM storedFluid = null;
        if (stack.hasTag() && Objects.requireNonNull(stack.getTag()).contains("FluidID")) {
            storedFluid = Fluids.fromID(stack.getTag().getInt("FluidID"));
        }

        int pipeType = -1;
        if (item == ModBlocks.FLUID_DUCT.get().asItem()) pipeType = 0;
        else if (item == ModBlocks.FLUID_DUCT_SILVER.get().asItem()) pipeType = 1;
        else if (item == ModBlocks.FLUID_DUCT_COLORED.get().asItem()) pipeType = 2;

        if (pipeType >= 0) {
            FluidTypeHBM fluid = (storedFluid != null && storedFluid != Fluids.NONE.get()) ? storedFluid : Fluids.NONE.get();
            return fluid(fluid, pipeType);
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ConduitEntry that)) return false;
        if (type != that.type) return false;
        if (type == ChannelType.FLUID) {
            return Objects.equals(fluid, that.fluid) && pipeType == that.pipeType;
        }
        return true;
    }

    @Override
    public int hashCode() {
        if (type == ChannelType.FLUID) {
            return Objects.hash(type, fluid, pipeType);
        }
        return Objects.hash(type);
    }
}