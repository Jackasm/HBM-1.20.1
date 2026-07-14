package com.hbm.tileentity.machine;

import com.google.gson.JsonObject;
import com.google.gson.stream.JsonWriter;
import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.inventory.UpgradeManagerNT;
import com.hbm.inventory.container.ContainerCentrifuge;
import com.hbm.inventory.recipes.CentrifugeRecipes;
import com.hbm.items.machine.ItemMachineUpgrade;
import com.hbm.items.machine.ItemMachineUpgrade.UpgradeType;
import com.hbm.main.MainRegistry;
import com.hbm.sound.AudioWrapper;
import com.hbm.sound.ModSounds;
import com.hbm.sound.SoundHelper;
import com.hbm.tileentity.*;

import com.hbm.util.Library;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class TileEntityMachineCentrifuge extends TileEntityMachineBase implements MenuProvider, IEnergyReceiver, IUpgradeInfoProvider, IConfigurableMachine {

    public int progress;
    public long power;
    public boolean isProgressing;
    private int audioDuration = 0;
    private AudioWrapper audio;

    public static int maxPower = 100000;
    private static int processingSpeed = 200;
    private static int baseConsumption = 200;

    public UpgradeManagerNT upgradeManager = new UpgradeManagerNT();

    private final ItemStackHandler inventory = new ItemStackHandler(8) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityMachineCentrifuge(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_CENTRIFUGE.get(), pos, state);
    }

    public IItemHandler getItemHandler() {
        return inventory;
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.centrifuge");
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        power = nbt.getLong("power");
        progress = nbt.getInt("progress");
        isProgressing = nbt.getBoolean("isProgressing");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putLong("power", power);
        nbt.putInt("progress", progress);
        nbt.putBoolean("isProgressing", isProgressing);
    }

    public int getCentrifugeProgressScaled(int i) {
        return (progress * i) / processingSpeed;
    }

    public long getPowerRemainingScaled(int i) {
        return (power * i) / maxPower;
    }

    private boolean canProcess() {
        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) return false;

        ItemStack[] out = CentrifugeRecipes.getOutput(input);
        if (out == null) return false;

        for (int i = 0; i < Math.min(4, out.length); i++) {
            if (out[i] == null) continue;

            ItemStack slotStack = inventory.getStackInSlot(i + 2);
            if (slotStack.isEmpty()) continue;

            if (slotStack.getItem() == out[i].getItem() &&
                    slotStack.getCount() + out[i].getCount() <= slotStack.getMaxStackSize()) {
                continue;
            }
            return false;
        }
        return true;
    }

    private void processItem() {
        ItemStack[] out = CentrifugeRecipes.getOutput(inventory.getStackInSlot(0));

        for (int i = 0; i < Math.min(4, out.length); i++) {
            if (out[i] == null) continue;

            ItemStack slotStack = inventory.getStackInSlot(i + 2);
            if (slotStack.isEmpty()) {
                inventory.setStackInSlot(i + 2, out[i].copy());
            } else {
                slotStack.grow(out[i].getCount());
            }
        }

        inventory.extractItem(0, 1, false);
        setChanged();
    }

    public void tick() {
        if (level == null) return;

        if (!level.isClientSide) {
            // Энергия от соседей
            for (Direction dir : Direction.values()) {
                trySubscribe(level, worldPosition.relative(dir), dir);
            }

            // Зарядка от батарейки (слот 1)
            power = Library.chargeTEFromItems(inventory, 1, power, maxPower);

            int consumption = baseConsumption;
            int speed = 1;

            // Проверка апгрейдов
            upgradeManager.checkSlots(getSlots(), 6, 7);
            speed += upgradeManager.getLevel(UpgradeType.SPEED);
            consumption += upgradeManager.getLevel(UpgradeType.SPEED) * baseConsumption;

            speed *= (1 + upgradeManager.getLevel(UpgradeType.OVERDRIVE) * 5);
            consumption += upgradeManager.getLevel(UpgradeType.OVERDRIVE) * baseConsumption * 50;

            consumption /= (1 + upgradeManager.getLevel(UpgradeType.POWER));

            if (hasPower() && isProcessing()) {
                power -= consumption;
                if (power < 0) power = 0;
            }

            isProgressing = hasPower() && canProcess();

            if (isProgressing) {
                progress += speed;
                if (progress >= processingSpeed) {
                    progress = 0;
                    processItem();
                }
            } else {
                progress = 0;
            }

            // Отправка данных на клиент
            this.networkPackNT(50);

        } else {
            // Клиентская сторона - звук
            if (isProgressing) {
                audioDuration += 2;
            } else {
                audioDuration -= 3;
            }

            audioDuration = Mth.clamp(audioDuration, 0, 60);

            if (audioDuration > 10 && MainRegistry.proxy.me()
                    .distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) < 625) {
                if (audio == null) {
                    audio = createAudioLoop();
                    audio.startSound();
                } else if (!audio.isPlaying()) {
                    audio = rebootAudio(audio);
                }

                audio.updateVolume(getVolume(1F));
                audio.updatePitch((audioDuration - 10) / 100F + 0.5F);
                audio.keepAlive();
            } else {
                if (audio != null) {
                    audio.stopSound();
                    audio = null;
                }
            }
        }
    }

    private ItemStack[] getSlots() {
        ItemStack[] slots = new ItemStack[8];
        for (int i = 0; i < 8; i++) {
            slots[i] = inventory.getStackInSlot(i);
        }
        return slots;
    }

    public AudioWrapper createAudioLoop() {
        return SoundHelper.createLoopedSound(ModSounds.CENTRIFUGE_OPERATE.get(),
                worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                1.0F, 10F, 1.0F, 20);
    }

    public AudioWrapper rebootAudio(AudioWrapper wrapper) {
        wrapper.stopSound();
        return createAudioLoop();
    }

    public float getVolume(float base) {
        return base;
    }

    public boolean hasPower() {
        return power > 0;
    }

    public boolean isProcessing() {
        return progress > 0;
    }

    // ==================== IEnergyReceiver ====================
    @Override
    public long getPower() { return power; }

    @Override
    public void setPower(long power) { this.power = power; }

    @Override
    public long getMaxPower() { return maxPower; }

    @Override
    public long getReceiverSpeed() { return 10000; }

    @Override
    public long transferPower(long power) {
        long newPower = this.power + power;
        if (newPower <= maxPower) {
            this.power = newPower;
            return 0;
        }
        long received = maxPower - this.power;
        this.power = maxPower;
        return power - received;
    }

    // ==================== Capabilities ====================
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

    // ==================== GUI ====================
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerCentrifuge(windowId, inv, this);
    }

    // ==================== IUpgradeInfoProvider ====================
    @Override
    public boolean canProvideInfo(UpgradeType type, int level, boolean extendedInfo) {
        return type == UpgradeType.SPEED || type == UpgradeType.POWER || type == UpgradeType.OVERDRIVE;
    }

    @Override
    public void provideInfo(UpgradeType type, int level, List<Component> info, boolean extendedInfo) {
        if (type == UpgradeType.SPEED) {
            info.add(Component.literal("§aDelay: -" + (100 - 100 / (level + 1)) + "%"));
            info.add(Component.literal("§cConsumption: +" + (level * 100) + "%"));
        }
        if (type == UpgradeType.POWER) {
            info.add(Component.literal("§aConsumption: -" + (100 - 100 / (level + 1)) + "%"));
        }
        if (type == UpgradeType.OVERDRIVE) {
            info.add(Component.literal("§cYES"));
        }
    }

    @Override
    public HashMap<ItemMachineUpgrade.UpgradeType, Integer> getValidUpgrades() {
        HashMap<UpgradeType, Integer> upgrades = new HashMap<>();
        upgrades.put(UpgradeType.SPEED, 3);
        upgrades.put(UpgradeType.POWER, 3);
        upgrades.put(UpgradeType.OVERDRIVE, 3);
        return upgrades;
    }


    // ==================== IConfigurableMachine ====================
    @Override
    public String getConfigName() {
        return "centrifuge";
    }

    @Override
    public void readIfPresent(JsonObject obj) {
        maxPower = IConfigurableMachine.grab(obj, "I:powerCap", maxPower);
        processingSpeed = IConfigurableMachine.grab(obj, "I:timeToProcess", processingSpeed);
        baseConsumption = IConfigurableMachine.grab(obj, "I:consumption", baseConsumption);
    }

    @Override
    public void writeConfig(JsonWriter writer) throws IOException {
        writer.name("I:powerCap").value(maxPower);
        writer.name("I:timeToProcess").value(processingSpeed);
        writer.name("I:consumption").value(baseConsumption);
    }

    @Override
    public AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(),
                worldPosition.getX() + 1, worldPosition.getY() + 4, worldPosition.getZ() + 1);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeInt(progress);
        buf.writeBoolean(isProgressing);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
        progress = buf.readInt();
        isProgressing = buf.readBoolean();
    }
}