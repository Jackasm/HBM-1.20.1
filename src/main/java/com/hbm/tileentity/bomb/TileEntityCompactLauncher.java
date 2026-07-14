package com.hbm.tileentity.bomb;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.api.fluid.IFluidStandardReceiver;
import com.hbm.api.item.IDesignatorItem;
import com.hbm.entity.missile.EntityMissileCustom;

import com.hbm.entity.missile.MissileStruct;
import com.hbm.inventory.container.ContainerCompactLauncher;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemCustomMissile;
import com.hbm.items.weapon.ItemCustomMissilePart;
import com.hbm.items.weapon.ItemCustomMissilePart.FuelType;
import com.hbm.items.weapon.ItemCustomMissilePart.PartSize;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.IRadarCommandReceiver;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.Library;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.Objects;

public class TileEntityCompactLauncher extends TileEntityMachineBase implements
        IEnergyReceiver, IFluidStandardReceiver, IRadarCommandReceiver, MenuProvider {

    private final ItemStackHandler inventory = new ItemStackHandler(8) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return switch (slot) {
                case 0 -> true; // missile
                case 1 -> stack.getItem() instanceof IDesignatorItem;
                case 4 -> stack.getItem() == ModItems.ROCKET_FUEL.get(); // solid fuel
                case 2, 3, 5, 6, 7 -> true;
                default -> false;
            };
        }
    };

    private final LazyOptional<ItemStackHandler> itemHandlerCap = LazyOptional.of(() -> inventory);

    public long power;
    public static final long MAX_POWER = 100_000;
    public int solidFuel;
    public static final int MAX_SOLID = 25_000;

    public FluidTankHBM[] tanks;
    public MissileStruct load; // для отображения

    public TileEntityCompactLauncher(BlockPos pos, BlockState state) {
        super(ModTileEntity.COMPACT_LAUNCHER.get(), pos, state);
        this.tanks = new FluidTankHBM[2];
        this.tanks[0] = new FluidTankHBM(Fluids.NONE.get(), 25_000);
        this.tanks[1] = new FluidTankHBM(Fluids.NONE.get(), 25_000);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Обновление типов жидкостей по установленной ракете
        updateFluidTypes();

        // Загрузка жидкостей из слотов 2->6, 3->7
        tanks[0].loadTank(2, 6, toArray(inventory), inventory);
        tanks[1].loadTank(3, 7, toArray(inventory), inventory);

        // Зарядка от батареи (слот 5)
        power = Library.chargeTEFromItems(inventory, 5, power, MAX_POWER);

        // Добавление твёрдого топлива (слот 4)
        ItemStack fuelStack = inventory.getStackInSlot(4);
        if (!fuelStack.isEmpty() && fuelStack.getItem() == ModItems.ROCKET_FUEL.get() && solidFuel + 250 <= MAX_SOLID) {
            int amount = Math.min(fuelStack.getCount(), (MAX_SOLID - solidFuel) / 250);
            if (amount > 0) {
                fuelStack.shrink(amount);
                solidFuel += amount * 250;
            }
        }

        // Обновление подключений (каждые 20 тиков)
        if (level.getGameTime() % 20 == 0) {
            updateConnections();
        }

        // Запуск по редстоуну
        if (canLaunch() && level.hasNeighborSignal(worldPosition)) {
            launchFromDesignator();
        }

        // Отправка данных клиенту
        if (level.getGameTime() % 10 == 0) {
            setChanged();
        }
        this.networkPackNT(50);
    }

    private ItemStack[] toArray(ItemStackHandler handler) {
        ItemStack[] array = new ItemStack[handler.getSlots()];
        for (int i = 0; i < handler.getSlots(); i++) {
            array[i] = handler.getStackInSlot(i);
        }
        return array;
    }

    private void updateConnections() {
        Library.PosDir[] positions = getConPos();
        for (Library.PosDir pos : positions) {
            trySubscribe(level, pos.pos(), pos.dir());
            trySubscribe(tanks[0].getTankType(), level, pos.pos(), pos.dir());
            trySubscribe(tanks[1].getTankType(), level, pos.pos(), pos.dir());
        }
    }

    public Library.PosDir[] getConPos() {
        // Аналогично LaunchPad, но с учётом 3x3
        return new Library.PosDir[]{
                new Library.PosDir(worldPosition.offset(2, 0, 1), Direction.EAST),
                new Library.PosDir(worldPosition.offset(2, 0, -1), Direction.EAST),
                new Library.PosDir(worldPosition.offset(-2, 0, 1), Direction.WEST),
                new Library.PosDir(worldPosition.offset(-2, 0, -1), Direction.WEST),
                new Library.PosDir(worldPosition.offset(1, 0, 2), Direction.SOUTH),
                new Library.PosDir(worldPosition.offset(-1, 0, 2), Direction.SOUTH),
                new Library.PosDir(worldPosition.offset(1, 0, -2), Direction.NORTH),
                new Library.PosDir(worldPosition.offset(-1, 0, -2), Direction.NORTH),
                new Library.PosDir(worldPosition.offset(1, -1, 1), Direction.DOWN),
                new Library.PosDir(worldPosition.offset(1, -1, -1), Direction.DOWN),
                new Library.PosDir(worldPosition.offset(-1, -1, 1), Direction.DOWN),
                new Library.PosDir(worldPosition.offset(-1, -1, -1), Direction.DOWN)
        };
    }

    private void updateFluidTypes() {
        MissileStruct struct = getStruct();
        if (struct == null || struct.fuselage == null) return;

        ItemCustomMissilePart fuselage = struct.fuselage;
        FuelType fuelType = (FuelType) fuselage.attributes[0];

        switch (fuelType) {
            case KEROSENE -> {
                tanks[0].setType(Fluids.KEROSENE.get());
                tanks[1].setType(Fluids.PEROXIDE.get());
            }
            case HYDROGEN -> {
                tanks[0].setType(Fluids.HYDROGEN.get());
                tanks[1].setType(Fluids.OXYGEN.get());
            }
            case XENON -> {
                tanks[0].setType(Fluids.XENON.get());
                tanks[1].setType(Fluids.NONE.get());
            }
            case BALEFIRE -> {
                tanks[0].setType(Fluids.BALEFIRE.get());
                tanks[1].setType(Fluids.PEROXIDE.get());
            }
            default -> {
                tanks[0].setType(Fluids.NONE.get());
                tanks[1].setType(Fluids.NONE.get());
            }
        }
    }

    public MissileStruct getStruct() {
        return TileEntityCompactLauncher.getStruct(inventory.getStackInSlot(0));
    }

    public static MissileStruct getStruct(ItemStack stack) {
        return ItemCustomMissile.getStruct(stack);
    }

    public boolean isMissileValid() {
        MissileStruct struct = getStruct();
        if (struct == null || struct.fuselage == null) return false;
        ItemCustomMissilePart fuselage = struct.fuselage;
        return fuselage.top == PartSize.SIZE_10;
    }

    public boolean hasDesignator() {
        ItemStack stack = inventory.getStackInSlot(1);
        if (stack.isEmpty() || !(stack.getItem() instanceof IDesignatorItem designator)) return false;
        return designator.isReady(level, stack, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
    }

    public boolean canLaunch() {
        return power >= MAX_POWER * 0.75 && isMissileValid() && hasDesignator() && hasFuel();
    }

    private boolean hasFuel() {
        return solidState() != 0 && liquidState() != 0 && oxidizerState() != 0;
    }

    public int solidState() {
        MissileStruct struct = getStruct();
        if (struct == null || struct.fuselage == null) return -1;
        ItemCustomMissilePart fuselage = struct.fuselage;
        FuelType fuelType = (FuelType) fuselage.attributes[0];
        if (fuelType == FuelType.SOLID) {
            int needed = (int) (float) fuselage.attributes[1];
            return solidFuel >= needed ? 1 : 0;
        }
        return -1;
    }

    public int liquidState() {
        MissileStruct struct = getStruct();
        if (struct == null || struct.fuselage == null) return -1;
        ItemCustomMissilePart fuselage = struct.fuselage;
        FuelType fuelType = (FuelType) fuselage.attributes[0];
        if (fuelType == FuelType.SOLID) return -1;
        int needed = (int) (float) fuselage.attributes[1];
        return tanks[0].getFill() >= needed ? 1 : 0;
    }

    public int oxidizerState() {
        MissileStruct struct = getStruct();
        if (struct == null || struct.fuselage == null) return -1;
        ItemCustomMissilePart fuselage = struct.fuselage;
        FuelType fuelType = (FuelType) fuselage.attributes[0];
        if (fuelType == FuelType.SOLID || fuelType == FuelType.XENON) return -1;
        int needed = (int) (float) fuselage.attributes[1];
        return tanks[1].getFill() >= needed ? 1 : 0;
    }

    public void launchFromDesignator() {
        if (!canLaunch()) return;

        ItemStack designatorStack = inventory.getStackInSlot(1);
        if (designatorStack.getItem() instanceof IDesignatorItem designator) {
            Vec3 coords = designator.getCoords(level, designatorStack, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
            int tX = (int) Math.floor(coords.x);
            int tZ = (int) Math.floor(coords.z);
            launchTo(tX, tZ);
        }
    }

    private void launchTo(int tX, int tZ) {
        // Звук запуска
        Objects.requireNonNull(level).playSound(null, worldPosition, ModSounds.MISSILE_TAKEOFF.get(),
                net.minecraft.sounds.SoundSource.PLAYERS, 10.0F, 1.0F);

        // Создание ракеты
        MissileStruct struct = getStruct();
        EntityMissileCustom missile = new EntityMissileCustom(level,
                worldPosition.getX() + 0.5, worldPosition.getY() + 2.5, worldPosition.getZ() + 0.5,
                tX, tZ, struct);
        level.addFreshEntity(missile);

        // Списание топлива
        subtractFuel();
        inventory.setStackInSlot(0, ItemStack.EMPTY);
    }

    private void subtractFuel() {
        MissileStruct struct = getStruct();
        if (struct == null || struct.fuselage == null) return;
        ItemCustomMissilePart fuselage = struct.fuselage;
        FuelType fuelType = (FuelType) fuselage.attributes[0];
        int needed = (int) (float) fuselage.attributes[1];

        switch (fuelType) {
            case KEROSENE, HYDROGEN, BALEFIRE -> {
                tanks[0].setFill(tanks[0].getFill() - needed);
                tanks[1].setFill(tanks[1].getFill() - needed);
            }
            case XENON -> tanks[0].setFill(tanks[0].getFill() - needed);
            case SOLID -> solidFuel -= needed;
        }
        power -= (long) (MAX_POWER * 0.75);
    }

    // ================= Capabilities =================
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCap.invalidate();
    }

    // ================= IEnergyReceiver =================
    @Override
    public void setPower(long power) {
        this.power = power;
    }

    @Override
    public long getPower() {
        return power;
    }

    @Override
    public long getMaxPower() {
        return MAX_POWER;
    }

    @Override
    public long transferPower(long power) {
        this.power += power;
        if (this.power > MAX_POWER) {
            long overshoot = this.power - MAX_POWER;
            this.power = MAX_POWER;
            return overshoot;
        }
        return 0;
    }

    @Override
    public boolean canConnect(Direction dir) {
        return dir != Direction.UP && dir != Direction.DOWN;
    }

    // ================= IFluidStandardReceiver =================
    @Override
    public FluidTankHBM[] getAllTanks() {
        return tanks;
    }

    @Override
    public FluidTankHBM[] getReceivingTanks() {
        return tanks;
    }

    @Override
    public boolean canConnect(FluidTypeHBM type, Direction dir) {
        return dir != Direction.UP && dir != Direction.DOWN;
    }

    // ================= IRadarCommandReceiver =================
    @Override
    public boolean sendCommandPosition(int x, int y, int z) {
        if (!canLaunch()) return false;
        launchTo(x, z);
        return true;
    }

    @Override
    public boolean sendCommandEntity(Entity target) {
        return sendCommandPosition((int) target.getX(), (int) target.getY(), (int) target.getZ());
    }

    // ================= IGUIProvider & MenuProvider =================
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerCompactLauncher(id, inv, this);
    }

    @Override
    public net.minecraft.network.chat.@NotNull Component getDisplayName() {
        return net.minecraft.network.chat.Component.translatable("container.hbm.compact_launcher");
    }

    // ================= NBT =================
    @Override
    public void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putLong("power", power);
        nbt.putInt("solidFuel", solidFuel);
        tanks[0].writeToNBT(nbt, "tank0");
        tanks[1].writeToNBT(nbt, "tank1");
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        power = nbt.getLong("power");
        solidFuel = nbt.getInt("solidFuel");
        tanks[0].readFromNBT(nbt, "tank0");
        tanks[1].readFromNBT(nbt, "tank1");
    }

    // ================= Сетевые данные =================
    @Override
    public void serialize(ByteBuf buf) {
        buf.writeLong(power);
        buf.writeInt(solidFuel);
        tanks[0].serialize(buf);
        tanks[1].serialize(buf);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        this.power = buf.readLong();
        this.solidFuel = buf.readInt();
        tanks[0].deserialize(buf);
        tanks[1].deserialize(buf);
    }

    public int getSolidScaled(int scale) {
        return (int) ((long) solidFuel * scale / MAX_SOLID);
    }

    public long getPowerScaled(long scale) {
        return power * scale / MAX_POWER;
    }
}