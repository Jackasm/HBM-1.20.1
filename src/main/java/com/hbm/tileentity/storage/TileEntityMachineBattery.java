package com.hbm.tileentity.storage;

import com.hbm.api.energy.IEnergyConductor;
import com.hbm.api.energy.IEnergyProvider;
import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.inventory.container.ContainerMachineBattery;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.EnergyNodespace;
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
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TileEntityMachineBattery extends TileEntityMachineBase implements IEnergyProvider, IEnergyReceiver,
        IEnergyConductor, MenuProvider {

    // Режимы работы
    public static final int MODE_INPUT = 0;
    public static final int MODE_BUFFER = 1;
    public static final int MODE_OUTPUT = 2;
    public static final int MODE_NONE = 3;

    public enum ConnectionPriority {
        LOW(0), NORMAL(1), HIGH(2);
        public final int id;
        ConnectionPriority(int id) { this.id = id; }
    }

    public long power = 0;
    public long prevPowerState = 0;
    public long delta = 0;
    public short redLow = 0;
    public short redHigh = 2;
    public ConnectionPriority priority = ConnectionPriority.LOW;
    private byte lastRedstone = 0;

    private String customName;
    private final long maxPower;

    private EnergyNode node; // Узел для режима BUFFER

    private final ItemStackHandler inventory = new ItemStackHandler(2) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return true;
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index) {
                case 0 -> (int) (power & 0xFFFFFFFFL);
                case 1 -> (int) (power >> 32);
                case 2 -> (int) (maxPower & 0xFFFFFFFFL);
                case 3 -> (int) (maxPower >> 32);
                case 4 -> redLow & 0xFFFF;
                case 5 -> redHigh & 0xFFFF;
                case 6 -> priority.ordinal();
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {}

        @Override
        public int getCount() {
            return 7;
        }
    };

    public ContainerData getContainerData() {
        return dataAccess;
    }

    public void handleButtonPacket(int value, int meta) {
        if (meta == 0) {
            redHigh = (short) ((redHigh + 1) % 4);
        } else if (meta == 1) {
            redLow = (short) ((redLow + 1) % 4);
        } else if (meta == 2) {
            int ordinal = (priority.ordinal() + 1) % 3;
            priority = ConnectionPriority.values()[ordinal];
        }
        setChanged();
    }

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityMachineBattery(BlockPos pos, BlockState state, long maxPower) {
        super(ModTileEntity.MACHINE_BATTERY.get(), pos, state);
        this.maxPower = maxPower;
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        power = nbt.getLong("power");
        redLow = nbt.getShort("redLow");
        redHigh = nbt.getShort("redHigh");
        lastRedstone = nbt.getByte("lastRedstone");
        priority = ConnectionPriority.values()[nbt.getByte("priority")];
        customName = nbt.getString("name");
        if (customName.isEmpty()) customName = null;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putLong("power", power);
        nbt.putShort("redLow", redLow);
        nbt.putShort("redHigh", redHigh);
        nbt.putByte("lastRedstone", lastRedstone);
        nbt.putByte("priority", (byte) priority.ordinal());
        if (customName != null) nbt.putString("name", customName);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeLong(delta);
        buf.writeShort(redLow);
        buf.writeShort(redHigh);
        buf.writeByte(priority.ordinal());
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
        delta = buf.readLong();
        redLow = buf.readShort();
        redHigh = buf.readShort();
        priority = ConnectionPriority.values()[buf.readByte()];
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    public void setCustomName(String name) {
        this.customName = name;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.literal(customName != null ? customName : "container.battery");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerMachineBattery(windowId, inv, this);
    }

    // === Tick ===
    public void tick() {
        if (level == null) return;
        if (level.isClientSide) return;

        int mode = getRelevantMode(false);
        long prevPower = power;

        // Зарядка от предметов
        power = Library.chargeItemsFromTE(inventory, 1, power, maxPower);
        power = Library.chargeTEFromItems(inventory, 0, power, maxPower);

        // Логика узлов в зависимости от режима
        if (mode == MODE_BUFFER) {
            // Создаём или обновляем узел
            if (node == null || node.expired) {
                node = EnergyNodespace.getNode(level, worldPosition);
                if (node == null || node.expired) {
                    node = createNode();
                    EnergyNodespace.createNode(level, node);
                }
            }
            if (node != null && node.hasValidNet()) {
                node.net.addProvider(this);
                node.net.addReceiver(this);
            }
        } else {
            // Удаляем узел, если он был создан ранее
            if (node != null) {
                EnergyNodespace.destroyNode(level, worldPosition);
                node = null;
            }
            // Для INPUT и OUTPUT используем прямую подписку без узла
            if (mode == MODE_INPUT) {
                for (Direction dir : Direction.values()) {
                    trySubscribe(level, worldPosition.relative(dir), dir);
                }
            } else if (mode == MODE_OUTPUT) {
                for (Direction dir : Direction.values()) {
                    tryProvide(level, worldPosition.relative(dir), dir);
                }
            }
            // MODE_NONE — ничего не делаем
        }

        // Компаратор
        byte comp = getComparatorPower();
        if (comp != lastRedstone) {
            setChanged();
            lastRedstone = comp;
        }

        // Дельта
        long avg = (power + prevPower) / 2;
        delta = avg - log[0];
        for (int i = 1; i < log.length; i++) {
            log[i - 1] = log[i];
        }
        log[19] = avg;

        prevPowerState = power;

        if (level.getGameTime() % 10 == 0) {
            networkPackNT(50);
        }
    }

    private EnergyNode createNode() {
        // Создаём узел с соединениями во все 6 сторон
        com.hbm.util.Library.PosDir[] connections = new com.hbm.util.Library.PosDir[6];
        Direction[] dirs = Direction.values();
        for (int i = 0; i < 6; i++) {
            connections[i] = new com.hbm.util.Library.PosDir(
                    worldPosition.relative(dirs[i]),
                    dirs[i].getOpposite()
            );
        }
        return new EnergyNode(worldPosition).setConnections(connections);
    }

    public long getPowerRemainingScaled(long scale) {
        if (maxPower == 0) return 0;
        return (power * scale) / maxPower;
    }

    // === Режимы и редстоун ===
    public short getRelevantMode(boolean useCache) {
        if (useCache) return modeCache;
        modeCache = (level != null && level.hasNeighborSignal(worldPosition)) ? redHigh : redLow;
        return modeCache;
    }
    private short modeCache = 0;

    // === Интерфейсы энергии ===
    @Override public long getPower() { return power; }
    @Override public void setPower(long power) { this.power = power; }
    @Override public long getMaxPower() { return maxPower; }
    @Override public long getProviderSpeed() {
        int mode = getRelevantMode(true);
        return (mode == MODE_OUTPUT || mode == MODE_BUFFER) ? maxPower / 600 : 0;
    }
    @Override public long getReceiverSpeed() {
        int mode = getRelevantMode(true);
        return (mode == MODE_INPUT || mode == MODE_BUFFER) ? maxPower / 200 : 0;
    }
    @Override public boolean canConnect(Direction dir) { return true; }
    @Override public void usePower(long amount) { power -= amount; }

    // === Компаратор ===
    public byte getComparatorPower() {
        if (power == 0) return 0;
        double frac = (double) power / (double) maxPower * 15D;
        return (byte) (MathHelper.clamp((int) frac + 1, 0, 15));
    }

    private static class MathHelper {
        public static double clamp(double value, double min, double max) {
            return Math.max(min, Math.min(max, value));
        }
    }

    // === Capabilities ===
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCap.invalidate();
    }

    public long[] log = new long[20];
}