package com.hbm.tileentity;

import com.hbm.api.fluid.IFluidStandardSender;

import com.hbm.handler.pollution.PollutionHandler;
import com.hbm.handler.pollution.PollutionType;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.FT_Polluting;
import com.hbm.inventory.fluid.trait.FluidTrait;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public abstract class TileEntityMachinePolluting extends TileEntityMachineBase implements IFluidStandardSender {

    public FluidTankHBM smoke;
    public FluidTankHBM smokeLeaded;
    public FluidTankHBM smokePoison;

    public TileEntityMachinePolluting(BlockEntityType<?> type, BlockPos pos, BlockState state, int buffer) {
        super(type, pos, state);
        smoke = new FluidTankHBM(Fluids.SMOKE.get(), buffer);
        smokeLeaded = new FluidTankHBM(Fluids.SMOKE_LEADED.get(), buffer);
        smokePoison = new FluidTankHBM(Fluids.SMOKE_POISON.get(), buffer);
    }

    public void pollute(PollutionType type, float amount) {
        FluidTankHBM tank = switch (type) {
            case SOOT -> smoke;
            case HEAVYMETAL -> smokeLeaded;
            default -> smokePoison;
        };

        int fluidAmount = (int) Math.ceil(amount * 100);
        tank.setFill(tank.getFill() + fluidAmount);

        if (tank.getFill() > tank.getMaxFill()) {
            int overflow = tank.getFill() - tank.getMaxFill();
            tank.setFill(tank.getMaxFill());

            if (level != null) {
                PollutionHandler.incrementPollution(level, worldPosition, type, overflow / 100F);

                if (level.random.nextInt(3) == 0) {
                    level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 1.5F);
                }
            }
        }
    }

    public void pollute(FluidTypeHBM type, FluidTrait.FluidReleaseType release, float amount) {
        FT_Polluting trait = type.getTrait(FT_Polluting.class);
        if (trait == null || release == FluidTrait.FluidReleaseType.VOID) return;

        HashMap<PollutionType, Float> map = release == FluidTrait.FluidReleaseType.BURN ? trait.burnMap : trait.releaseMap;

        for (Map.Entry<PollutionType, Float> entry : map.entrySet()) {
            FluidTankHBM tank = switch (entry.getKey()) {
                case SOOT -> smoke;
                case HEAVYMETAL -> smokeLeaded;
                default -> smokePoison;
            };

            int fluidAmount = (int) Math.ceil(entry.getValue() * amount * 100);
            tank.setFill(tank.getFill() + fluidAmount);

            if (tank.getFill() > tank.getMaxFill()) {
                int overflow = tank.getFill() - tank.getMaxFill();
                tank.setFill(tank.getMaxFill());

                if (level != null) {
                    PollutionHandler.incrementPollution(level, worldPosition, entry.getKey(), overflow / 100F);

                    if (level.random.nextInt(3) == 0) {
                        level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.1F, 1.5F);
                    }
                }
            }
        }
    }

    public void sendSmoke(BlockPos pos, Direction dir) {
        if (smoke.getFill() > 0) sendFluid(smoke, level, pos, dir);
        if (smokeLeaded.getFill() > 0) sendFluid(smokeLeaded, level, pos, dir);
        if (smokePoison.getFill() > 0) sendFluid(smokePoison, level, pos, dir);
    }

    public FluidTankHBM[] getSmokeTanks() {
        return new FluidTankHBM[]{smoke, smokeLeaded, smokePoison};
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        return getSmokeTanks();
    }

    @Override
    public FluidTankHBM[] getSendingTanks() {
        return getSmokeTanks();
    }

    public FluidTankHBM[] getReceivingTanks() {
        return new FluidTankHBM[0];
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        smoke.readFromNBT(nbt, "smoke0");
        smokeLeaded.readFromNBT(nbt, "smoke1");
        smokePoison.readFromNBT(nbt, "smoke2");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        smoke.writeToNBT(nbt, "smoke0");
        smokeLeaded.writeToNBT(nbt, "smoke1");
        smokePoison.writeToNBT(nbt, "smoke2");
    }
}