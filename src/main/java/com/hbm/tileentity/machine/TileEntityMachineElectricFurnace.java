package com.hbm.tileentity.machine;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.blocks.machine.MachineElectricFurnace;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.container.ContainerElectricFurnace;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.tileentity.IUpgradeInfoProvider;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.Library;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class TileEntityMachineElectricFurnace extends TileEntityMachineBase implements MenuProvider, IEnergyReceiver, IUpgradeInfoProvider {

    public long power = 0;
    public int progress = 0;
    public int maxProgress = 100;
    public int consumption = 50;
    private int cooldown = 0;
    private boolean active = false;

    public static final long MAX_POWER = 100000;

    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();

    private final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return stack.getItem() instanceof IBatteryItem;
            }
            if (slot == 1) {
                return Objects.requireNonNull(level).getRecipeManager()
                        .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(stack), level)
                        .isPresent();
            }
            if (slot == 3) {
                return stack.getItem() instanceof ItemMachineUpgrade;
            }
            return false;
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityMachineElectricFurnace(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_ELECTRIC_FURNACE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (cooldown > 0) cooldown--;

        for (Direction dir : Direction.values()) {
            trySubscribe(level, worldPosition.relative(dir), dir);
        }

        // Зарядка от батареек
        power = Library.chargeTEFromItems(inventory, 0, power, MAX_POWER);

        // Обновляем менеджер апгрейдов
        upgradeManager.checkSlots(getSlots(), 3, 3);
        updateUpgrades();

        boolean hasPower = power >= consumption;
        boolean canProcess = hasPower && canProcess();

        // Новая логика - печка активна если может обрабатывать ИЛИ уже обрабатывает
        boolean shouldBeActive = canProcess || (progress > 0 && hasPower);

        if (canProcess) {
            progress++;
            power -= consumption;

            if (progress >= maxProgress) {
                progress = 0;
                processItem();
                setChanged();
            }
        } else {
            if (progress > 0) {
                progress = 0;
                setChanged();
            }
            // Если нет возможности обрабатывать, выключаем подсветку
            shouldBeActive = false;
        }

        // Обновляем состояние блока
        if (active != shouldBeActive) {
            active = shouldBeActive;
            MachineElectricFurnace.updateBlockState(active, level, worldPosition);
        }

        // Синхронизация
        this.networkPackNT(50);
        if (level.getGameTime() % 20 == 0) {
            setChanged();
            level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 3);
        }
    }

    private ItemStack[] getSlots() {
        ItemStack[] slots = new ItemStack[4];
        for (int i = 0; i < 4; i++) {
            slots[i] = inventory.getStackInSlot(i);
        }
        return slots;
    }

    private void updateUpgrades() {
        int speedLevel = upgradeManager.getLevel(ItemMachineUpgrade.UpgradeType.SPEED);
        int powerLevel = upgradeManager.getLevel(ItemMachineUpgrade.UpgradeType.POWER);

        maxProgress = 100;
        consumption = 50;

        maxProgress -= speedLevel * 25;
        consumption += speedLevel * 50;
        maxProgress += powerLevel * 10;
        consumption -= powerLevel * 15;

        maxProgress = Math.max(maxProgress, 1);
        consumption = Math.max(consumption, 1);
    }

    private boolean canProcess() {
        ItemStack input = inventory.getStackInSlot(1);
        if (input.isEmpty()) return false;

        if (level == null) return false;

        var recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input), level);
        if (recipe.isEmpty()) return false;

        ItemStack result = recipe.get().getResultItem(level.registryAccess());
        if (result.isEmpty()) return false;

        ItemStack output = inventory.getStackInSlot(2);
        if (output.isEmpty()) return true;

        if (!output.is(result.getItem())) return false;
        if (output.getCount() + result.getCount() > output.getMaxStackSize()) return false;

        return true;
    }

    private void processItem() {
        ItemStack input = inventory.getStackInSlot(1);
        if (input.isEmpty()) return;

        if (level == null) return;

        var recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new SimpleContainer(input), level);
        if (recipe.isEmpty()) return;

        ItemStack result = recipe.get().getResultItem(level.registryAccess());
        if (result.isEmpty()) return;

        ItemStack output = inventory.getStackInSlot(2);
        if (output.isEmpty()) {
            inventory.setStackInSlot(2, result.copy());
        } else {
            output.grow(result.getCount());
        }

        input.shrink(1);
        if (input.isEmpty()) {
            inventory.setStackInSlot(1, ItemStack.EMPTY);
        }
    }

    public long getPowerScaled(long scale) {
        return (power * scale) / MAX_POWER;
    }

    public int getProgressScaled(int scale) {
        return (progress * scale) / maxProgress;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public long getPower() { return power; }

    @Override
    public void setPower(long power) { this.power = power; }

    @Override
    public long getMaxPower() { return MAX_POWER; }

    @Override
    public long getReceiverSpeed() { return MAX_POWER; }

    @Override
    public long transferPower(long power) {
        long newPower = this.power + power;
        if (newPower <= MAX_POWER) {
            this.power = newPower;
            return 0;
        }
        long received = MAX_POWER - this.power;
        this.power = MAX_POWER;
        return power - received;
    }

    @Override
    public boolean canProvideInfo(ItemMachineUpgrade.UpgradeType type, int level, boolean extendedInfo) {
        return type == ItemMachineUpgrade.UpgradeType.SPEED || type == ItemMachineUpgrade.UpgradeType.POWER;
    }

    @Override
    public void provideInfo(ItemMachineUpgrade.UpgradeType type, int level, List<Component> info, boolean extendedInfo) {
        if (type == ItemMachineUpgrade.UpgradeType.SPEED) {
            info.add(Component.translatable("hbm.text.speed_2", (level * 25)));
            info.add(Component.translatable("hbm.text.consumption", (level * 100)));
        }
        if (type == ItemMachineUpgrade.UpgradeType.POWER) {
            info.add(Component.translatable("hbm.text.consumption_2", (level * 30)));
            info.add(Component.translatable("hbm.text.speed_3", (level * 10)));
        }
    }

    @Override
    public HashMap<ItemMachineUpgrade.UpgradeType, Integer> getValidUpgrades() {
        HashMap<ItemMachineUpgrade.UpgradeType, Integer> upgrades = new HashMap<>();
        upgrades.put(ItemMachineUpgrade.UpgradeType.SPEED, 3);
        upgrades.put(ItemMachineUpgrade.UpgradeType.POWER, 3);
        return upgrades;
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        power = tag.getLong("power");
        progress = tag.getInt("progress");
        active = tag.getBoolean("active");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        tag.putLong("power", power);
        tag.putInt("progress", progress);
        tag.putBoolean("active", active);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeInt(progress);
        buf.writeInt(maxProgress);
        buf.writeBoolean(active);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
        progress = buf.readInt();
        maxProgress = buf.readInt();
        active = buf.readBoolean();
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.electric_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new ContainerElectricFurnace(id, inventory, this);
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

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
}