package com.hbm.tileentity.machine;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.api.energy.IEnergyProvider;
import com.hbm.api.fluid.IFluidCopiable;
import com.hbm.api.fluid.IFluidStandardTransceiver;
import com.hbm.inventory.FluidContainerRegistry;
import com.hbm.inventory.container.ContainerMachineDiesel;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.inventory.fluid.trait.FT_Combustible;
import com.hbm.inventory.fluid.trait.FT_Combustible.FuelGrade;
import com.hbm.inventory.fluid.trait.FluidTrait.FluidReleaseType;
import com.hbm.main.MainRegistry;
import com.hbm.sound.AudioWrapper;
import com.hbm.sound.ModSounds;
import com.hbm.sound.SoundHelper;
import com.hbm.tileentity.IConfigurableMachine;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachinePolluting;
import com.hbm.util.Library;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TileEntityMachineDiesel extends TileEntityMachinePolluting implements IEnergyProvider, IFluidStandardTransceiver, IConfigurableMachine, MenuProvider, IFluidCopiable {

    public long power;
    public long powerCap = maxPower;
    public FluidTankHBM tank;

    public boolean wasOn = false;
    private int audioDuration = 0;
    private AudioWrapper audio;

    public static long maxPower = 50000;
    public static int fluidCap = 16000;
    public static Map<FuelGrade, Double> fuelEfficiency = new HashMap<>();

    static {
        fuelEfficiency.put(FuelGrade.MEDIUM, 0.5D);
        fuelEfficiency.put(FuelGrade.HIGH, 0.75D);
        fuelEfficiency.put(FuelGrade.AERO, 0.1D);
    }

    private final ItemStackHandler inventory = new ItemStackHandler(5) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) return FluidContainerRegistry.getFluidContent(stack, tank.getTankType()) > 0;
            if (slot == 2) return stack.getItem() instanceof com.hbm.api.energy.IBatteryItem;
            return false;
        }
    };
    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityMachineDiesel(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_DIESEL.get(), pos, state, 100);
        tank = new FluidTankHBM(Fluids.DIESEL.get(), 4000);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.machineDiesel");
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        power = nbt.getLong("power");
        powerCap = nbt.getLong("powerCap");
        tank.readFromNBT(nbt, "fuel");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putLong("power", power);
        nbt.putLong("powerCap", powerCap);
        tank.writeToNBT(nbt, "fuel");
    }

    public long getPowerScaled(long i) {
        return (power * i) / powerCap;
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeLong(powerCap);
        buf.writeBoolean(wasOn);
        tank.serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
        powerCap = buf.readLong();
        wasOn = buf.readBoolean();
        tank.deserialize(buf);
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            wasOn = false;

            // Energy provide
            for (Direction dir : Direction.values()) {
                tryProvide(level, worldPosition.relative(dir), dir);
                sendSmoke(worldPosition.relative(dir), dir);
            }

            // Tank Management
            FluidTypeHBM last = tank.getTankType();
            tank.loadTank(0, 1, getSlots(), inventory);
            tank.setType(3, 4, getSlots(), inventory);

            FluidTypeHBM type = tank.getTankType();
            if (type == Fluids.NITAN.get())
                powerCap = maxPower * 10;
            else
                powerCap = maxPower;

            // Battery Item
            power = Library.chargeTEFromItems(inventory, 2, power, powerCap);

            generate();

            this.networkPackNT(50);
        } else {
            // Client sound
            if (wasOn) {
                audioDuration = Math.min(audioDuration + 2, 60);
            } else {
                audioDuration = Math.max(audioDuration - 3, 0);
            }

            if (audioDuration > 10 && MainRegistry.proxy.me()
                    .distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) < 625) {
                if (audio == null) {
                    audio = createAudioLoop();
                    audio.startSound();
                } else if (!audio.isPlaying()) {
                    audio = rebootAudio(audio);
                }

                audio.updateVolume(getVolume(1F));
                audio.keepAlive();
            } else {
                if (audio != null) {
                    audio.stopSound();
                    audio = null;
                }
            }
        }
    }

    public AudioWrapper createAudioLoop() {
        return SoundHelper.createLoopedSound(ModSounds.DIESEL_ENGINE.get(),
                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                1.0F, 10F, 1.0F, 10);
    }

    public AudioWrapper rebootAudio(AudioWrapper wrapper) {
        wrapper.stopSound();
        return createAudioLoop();
    }

    public float getVolume(float base) {
        return base;
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (audio != null) {
            audio.stopSound();
            audio = null;
        }
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        if (audio != null) {
            audio.stopSound();
            audio = null;
        }
        inventoryCap.invalidate();
    }

    private ItemStack[] getSlots() {
        ItemStack[] slots = new ItemStack[5];
        for (int i = 0; i < 5; i++) {
            slots[i] = inventory.getStackInSlot(i);
        }
        return slots;
    }

    public boolean hasAcceptableFuel() {
        return getHEFromFuel() > 0;
    }

    public long getHEFromFuel() {
        return getHEFromFuel(tank.getTankType());
    }

    public static long getHEFromFuel(FluidTypeHBM type) {
        if (type.hasTrait(FT_Combustible.class)) {
            FT_Combustible fuel = type.getTrait(FT_Combustible.class);
            FuelGrade grade = fuel.getGrade();
            double efficiency = fuelEfficiency.containsKey(grade) ? fuelEfficiency.get(grade) : 0;

            if (grade != FuelGrade.LOW) {
                return (long) (fuel.getCombustionEnergy() / 1000L * efficiency);
            }
        }
        return 0;
    }

    public void generate() {
        if (level == null) return;
        if (level.hasNeighborSignal(worldPosition)) return;

        if (hasAcceptableFuel()) {
            if (tank.getFill() > 0) {
                wasOn = true;
                tank.setFill(tank.getFill() - 1);
                if (tank.getFill() < 0) tank.setFill(0);

                if (level.getGameTime() % 5 == 0) {
                    super.pollute(tank.getTankType(), FluidReleaseType.BURN, 5);
                }

                long toAdd = getHEFromFuel();
                if (power + toAdd <= powerCap) {
                    power += toAdd;
                } else {
                    power = powerCap;
                }
            }
        }
    }

    // ==================== IEnergyProvider ====================
    @Override
    public long getPower() { return power; }

    @Override
    public void setPower(long power) { this.power = power; }

    @Override
    public long getMaxPower() { return powerCap; }

    @Override
    public long getProviderSpeed() { return getHEFromFuel(); }

    @Override
    public void usePower(long power) { this.power -= power; }

    // ==================== IFluidStandardTransceiver ====================
    @Override
    public FluidTankHBM[] getReceivingTanks() { return new FluidTankHBM[]{tank}; }

    @Override
    public FluidTankHBM[] getSendingTanks() { return getSmokeTanks(); }

    @Override
    public FluidTankHBM[] getAllTanks() { return new FluidTankHBM[]{tank}; }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != null && type == tank.getTankType();
    }

    // ==================== IConfigurableMachine ====================
    @Override
    public String getConfigName() { return "dieselgen"; }

    @Override
    public void readIfPresent(JsonObject obj) {
        maxPower = IConfigurableMachine.grab(obj, "L:powerCap", maxPower);
        fluidCap = IConfigurableMachine.grab(obj, "I:fuelCap", fluidCap);

        if (obj.has("D[:efficiency")) {
            JsonArray array = obj.get("D[:efficiency").getAsJsonArray();
            for (FuelGrade grade : FuelGrade.values()) {
                fuelEfficiency.put(grade, array.get(grade.ordinal()).getAsDouble());
            }
        }
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("L:powerCap").value(maxPower);
        writer.name("I:fuelCap").value(fluidCap);

        StringBuilder info = new StringBuilder("Fuel grades in order: ");
        for (FuelGrade grade : FuelGrade.values()) info.append(grade.name()).append(" ");
        info = new StringBuilder(info.toString().trim());
        writer.name("INFO").value(info.toString());

        writer.name("D[:efficiency").beginArray();
        for (FuelGrade grade : FuelGrade.values()) {
            double d = fuelEfficiency.getOrDefault(grade, 0.0D);
            writer.value(d);
        }
        writer.endArray();
    }

    // ==================== Capabilities ====================
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        return super.getCapability(cap, side);
    }

    // ==================== GUI ====================
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerMachineDiesel(windowId, inv, this);
    }


    // ==================== IFluidCopiable ====================
    private FluidTypeHBM lastType;

    @Override
    public int[] getFluidIDToCopy() {
        return new int[]{Fluids.getID(tank.getTankType())};
    }

    @Override
    public FluidTankHBM getTankToPaste() {
        FluidTankHBM copy = new FluidTankHBM(tank.getTankType(), tank.getMaxFill());
        copy.setFill(tank.getFill());
        return copy;
    }
}