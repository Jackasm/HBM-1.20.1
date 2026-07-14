package com.hbm.tileentity;

import com.hbm.api.energy.IEnergyHandler;
import com.hbm.api.energy.IEnergyProvider;
import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.api.fluid.IFluidConnector;
import com.hbm.api.fluid.IFluidReceiver;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.util.CompatHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.WorldlyContainer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.wrapper.SidedInvWrapper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


public class TileEntityProxyCombo extends TileEntityProxyBase implements WorldlyContainer, IFluidHandler, IFluidReceiver, IEnergyProvider, IEnergyReceiver {

    public long power;
    // Флаги возможностей
    private boolean hasInventory = false;
    private boolean hasPower = false;
    private boolean hasFluid = false;
    private boolean hasHeat = false;
    public boolean hasMoltenMetal = false;
    public static final FluidTankHBM[] EMPTY_TANKS = new FluidTankHBM[0];

    // Кэш основного TileEntity
    private BlockEntity cachedCore = null;

    // Для OpenComputers

    private String componentName = CompatHelper.nullComponent;

    // Конструкторы
    public TileEntityProxyCombo(BlockPos pos, BlockState state) {
        super(ModTileEntity.PROXY_COMBO.get(), pos, state);
    }

    public TileEntityProxyCombo setInventory() {
        this.hasInventory = true;
        return this;
    }


    public TileEntityProxyCombo setPower() {
        this.hasPower = true;
        return this;
    }

    public TileEntityProxyCombo setFluid() {
        this.hasFluid = true;
        return this;
    }

    public TileEntityProxyCombo heatSource() {
        this.hasHeat = true;
        return this;
    }

    public TileEntityProxyCombo setMoltenMetal() {
        this.hasMoltenMetal = true;
        return this;
    }

    /**
     * Получает основной TileEntity (ядро мультиблока)
     */
    private BlockEntity getCore() {
        if (cachedCore == null || cachedCore.isRemoved()) {
            cachedCore = this.getTE();
        }
        return cachedCore;
    }

    /**
     * Получает основной объект с проверкой типа
     */
    private <T> T getCoreAs(Class<T> type) {
        BlockEntity core = getCore();
        if (core != null && type.isAssignableFrom(core.getClass())) {
            return type.cast(core);
        }
        return null;
    }

    // ========== IItemHandler / WorldlyContainer ==========

    @Override
    public int @NotNull [] getSlotsForFace(@NotNull Direction side) {
        if (!hasInventory) return new int[0];

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            return inv.getSlotsForFace(side);
        }
        return new int[0];
    }

    @Override
    public boolean canPlaceItemThroughFace(int index, @NotNull ItemStack stack, @Nullable Direction direction) {
        if (!hasInventory) return false;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            return inv.canPlaceItemThroughFace(index, stack, direction);
        }
        return false;
    }

    @Override
    public boolean canTakeItemThroughFace(int index, @NotNull ItemStack stack, @NotNull Direction direction) {
        if (!hasInventory) return false;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            return inv.canTakeItemThroughFace(index, stack, direction);
        }
        return false;
    }

    @Override
    public int getContainerSize() {
        if (!hasInventory) return 0;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            return inv.getContainerSize();
        }
        return 0;
    }

    @Override
    public boolean isEmpty() {
        if (!hasInventory) return true;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            return inv.isEmpty();
        }
        return true;
    }

    @Override
    public @NotNull ItemStack getItem(int slot) {
        if (!hasInventory) return ItemStack.EMPTY;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            return inv.getItem(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItem(int slot, int amount) {
        if (!hasInventory) return ItemStack.EMPTY;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            return inv.removeItem(slot, amount);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull ItemStack removeItemNoUpdate(int slot) {
        if (!hasInventory) return ItemStack.EMPTY;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            return inv.removeItemNoUpdate(slot);
        }
        return ItemStack.EMPTY;
    }

    @Override
    public void setItem(int slot, @NotNull ItemStack stack) {
        if (!hasInventory) return;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            inv.setItem(slot, stack);
        }
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        if (!hasInventory) return false;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            return inv.stillValid(player);
        }
        return false;
    }

    @Override
    public void clearContent() {
        if (!hasInventory) return;

        WorldlyContainer inv = getCoreAs(WorldlyContainer.class);
        if (inv != null) {
            inv.clearContent();
        }
    }

    // ========== IFluidHandler ==========

    @Override
    public int getTanks() {
        if (!hasFluid) return 0;

        IFluidHandler fluidHandler = getCoreAs(IFluidHandler.class);
        if (fluidHandler != null) {
            return fluidHandler.getTanks();
        }
        return 0;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (!hasFluid) return FluidStack.EMPTY;

        IFluidHandler fluidHandler = getCoreAs(IFluidHandler.class);
        if (fluidHandler != null) {
            return fluidHandler.getFluidInTank(tank);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public int getTankCapacity(int tank) {
        if (!hasFluid) return 0;

        IFluidHandler fluidHandler = getCoreAs(IFluidHandler.class);
        if (fluidHandler != null) {
            return fluidHandler.getTankCapacity(tank);
        }
        return 0;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        if (!hasFluid) return false;

        IFluidHandler fluidHandler = getCoreAs(IFluidHandler.class);
        if (fluidHandler != null) {
            return fluidHandler.isFluidValid(tank, stack);
        }
        return false;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action) {
        if (!hasFluid) return 0;

        IFluidHandler fluidHandler = getCoreAs(IFluidHandler.class);
        if (fluidHandler != null) {
            return fluidHandler.fill(resource, action);
        }
        return 0;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
        if (!hasFluid) return FluidStack.EMPTY;

        IFluidHandler fluidHandler = getCoreAs(IFluidHandler.class);
        if (fluidHandler != null) {
            return fluidHandler.drain(resource, action);
        }
        return FluidStack.EMPTY;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (!hasFluid) return FluidStack.EMPTY;

        IFluidHandler fluidHandler = getCoreAs(IFluidHandler.class);
        if (fluidHandler != null) {
            return fluidHandler.drain(maxDrain, action);
        }
        return FluidStack.EMPTY;
    }

    // ========== Capabilities ==========

    private final LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> new SidedInvWrapper(this, null));
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> this);

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (!this.isRemoved()) {
            if (hasInventory && cap == ForgeCapabilities.ITEM_HANDLER) {
                return inventoryHandler.cast();
            }
            if (hasFluid && cap == ForgeCapabilities.FLUID_HANDLER) {
                return fluidHandler.cast();
            }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryHandler.invalidate();
        fluidHandler.invalidate();
    }

    // ========== NBT ==========

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putBoolean("inventory", hasInventory);
        tag.putBoolean("power", hasPower);
        tag.putBoolean("fluid", hasFluid);
        tag.putBoolean("heat", hasHeat);
        tag.putBoolean("moltenMetal", hasMoltenMetal);
        tag.putString("ocname", componentName);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        hasInventory = tag.getBoolean("inventory");
        hasPower = tag.getBoolean("power");
        hasFluid = tag.getBoolean("fluid");
        hasHeat = tag.getBoolean("heat");
        hasMoltenMetal = tag.getBoolean("moltenMetal");
        componentName = tag.getString("ocname");
    }

    @Override
    public long transferFluid(FluidTypeHBM type, int pressure, long amount) {
        if(!hasFluid) return amount;

        if(getCoreObject() instanceof IFluidReceiver) {
            return ((IFluidReceiver)getCoreObject()).transferFluid(type, pressure, amount);
        }

        return amount;
    }

    protected BlockEntity getCoreObject() {
        return getTE();
    }

    @Override
    public long getDemand(FluidTypeHBM type, int pressure) {
        if(!hasFluid) return 0;

        if(getCoreObject() instanceof IFluidReceiver) {
            return ((IFluidReceiver)getCoreObject()).getDemand(type, pressure);
        }

        return 0;
    }

    @Override
    public FluidTankHBM[] getAllTanks() {
        if(!hasFluid) return EMPTY_TANKS;

        if(getCoreObject() instanceof IFluidReceiver) {
            return ((IFluidReceiver)getCoreObject()).getAllTanks();
        }

        return EMPTY_TANKS;
    }

    @Override
    public boolean setFluidType(FluidTypeHBM type) {
        return false;
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        if (!hasFluid) return false;

        IFluidConnector connector = getCoreAs(IFluidConnector.class);
        if (connector != null) {
            return connector.canConnect(type, dir);
        }

        return false;
    }

    @Override
    public long getPower() {
        if (!hasPower) return 0;

        IEnergyHandler handler = getCoreAs(IEnergyHandler.class);
        if (handler != null) {
            return handler.getPower();
        }
        return power; // fallback
    }

    @Override
    public void setPower(long power) {
        if (!hasPower) return;

        IEnergyHandler handler = getCoreAs(IEnergyHandler.class);
        if (handler != null) {
            handler.setPower(power);
        } else {
            this.power = power;
        }
    }

    @Override
    public long getMaxPower() {
        if (!hasPower) return 0;

        IEnergyHandler handler = getCoreAs(IEnergyHandler.class);
        if (handler != null) {
            return handler.getMaxPower();
        }
        return 0;
    }

    @Override
    public long getProviderSpeed() {
        if (!hasPower) return 0;

        IEnergyProvider provider = getCoreAs(IEnergyProvider.class);
        if (provider != null) {
            return provider.getProviderSpeed();
        }
        return 0;
    }

    @Override
    public void usePower(long power) {
        if (!hasPower) return;

        IEnergyProvider provider = getCoreAs(IEnergyProvider.class);
        if (provider != null) {
            provider.usePower(power);
        } else {
            this.power -= power;
        }
    }

}