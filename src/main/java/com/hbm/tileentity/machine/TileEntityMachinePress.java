package com.hbm.tileentity.machine;

import com.hbm.inventory.container.ContainerMachinePress;
import com.hbm.inventory.recipes.PressRecipes;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

import static com.hbm.util.ResLocation.ResLocation;

public class TileEntityMachinePress extends TileEntityMachineBase implements MenuProvider{

    public int speed = 0;
    public static final int maxSpeed = 400;
    public static final int progressAtMax = 25;
    public int burnTime = 0;
    public int press = 0;
    public static final int maxPress = 200;
    boolean isRetracting = false;
    private int delay = 0;
    private boolean wasIdle = true;

    // ========== КЛИЕНТСКИЕ ПОЛЯ ДЛЯ ИНТЕРПОЛЯЦИИ ==========
    // Для хранения клиентских данных интерполяции
    private InterpolationData interpolationData;

    // Внутренний класс для клиентских данных
    private static class InterpolationData {
        double renderPress = 0;
        double lastPress = 0;
        int syncPress = 0;
        int turnProgress = 0;

        public void update() {
            lastPress = renderPress;

            if (turnProgress > 0) {
                renderPress = renderPress + ((syncPress - renderPress) / (double) turnProgress);
                turnProgress--;
            } else {
                renderPress = syncPress;
            }
        }

        public double getInterpolatedPress(float partialTicks) {
            return lastPress + (renderPress - lastPress) * partialTicks;
        }
    }

    // ========== ГЕТТЕР ДЛЯ РЕНДЕРЕРА ==========
    public double getInterpolatedPress(float partialTicks) {
        if (level != null && level.isClientSide()) {
            // Инициализируем данные, если их нет
            if (interpolationData == null) {
                interpolationData = new InterpolationData();
                interpolationData.syncPress = press;
                interpolationData.renderPress = press;
                interpolationData.lastPress = press;
            }

            // Синхронизируем с серверным значением
            if (press != interpolationData.syncPress) {
                interpolationData.syncPress = press;
                interpolationData.turnProgress = 2;
            }

            return interpolationData.getInterpolatedPress(partialTicks);
        }
        return press; // На сервере возвращаем простое значение
    }

    private final ItemStackHandler inventory = new ItemStackHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide) {
                syncWithClient();
            }
        }

        @Override
        public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
            return switch(slot) {
                case 0 -> ForgeHooks.getBurnTime(stack, null) > 0;
                case 1, 2 -> true;
                case 3 -> false;
                default -> super.isItemValid(slot, stack);
            };
        }
    };

    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch(index) {
                case 0 -> speed;
                case 1 -> burnTime;
                case 2 -> press;
                case 3 -> isRetracting ? 1 : 0;
                case 4 -> delay;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch(index) {
                case 0 -> speed = value;
                case 1 -> burnTime = value;
                case 2 -> press = value;
                case 3 -> isRetracting = value == 1;
                case 4 -> delay = value;
            }
            setChanged();
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    private final LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> inventory);

    public TileEntityMachinePress(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_PRESS.get(), pos, state);
    }


    public void clientTick() {
        if (level != null && level.isClientSide()) {
            if (interpolationData == null) {
                interpolationData = new InterpolationData();
                interpolationData.syncPress = press;
                interpolationData.renderPress = press;
                interpolationData.lastPress = press;
            }

            interpolationData.update();

            // Синхронизируем с серверным значением
            if (press != interpolationData.syncPress) {
                interpolationData.syncPress = press;
                interpolationData.turnProgress = 2;
            }
        }
    }

    @Override
    public @NotNull CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        saveAdditional(tag);
        return tag;
    }



    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        if (pkt.getTag() != null) {
            CompoundTag tag = pkt.getTag();
            load(tag);

            // Обновляем интерполяцию при получении данных с сервера
            if (level != null && level.isClientSide()) {
                if (interpolationData == null) {
                    interpolationData = new InterpolationData();
                }
                interpolationData.syncPress = press;
                interpolationData.turnProgress = 2;
            }
        }
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        load(tag);
    }

    public void syncWithClient() {
        if (level != null && !level.isClientSide) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 2);
            setChanged();
        }
    }

    public static void serverTick(Level level, BlockPos ignoredPos, BlockState ignoredState, TileEntityMachinePress press) {
        if(!level.isClientSide) {
            press.updateServer();
        }
    }

    private void updateServer() {

        int oldSpeed = speed;
        int oldPress = press;
        boolean oldRetracting = isRetracting;
        boolean canProcess = canProcess();

        if(burnTime > 0 && (canProcess || isRetracting)) {
            speed = Math.min(maxSpeed, speed + 2);
        } else {
            speed = Math.max(0, speed - 2);
        }

        if(delay <= 0) {
            int stampSpeed = Math.max(1, speed * progressAtMax / maxSpeed);

            if(isRetracting) {
                press -= stampSpeed;
                if(press <= 0) {
                    press = 0;
                    isRetracting = false;
                    delay = 10;
                    if (speed == 0) {
                        wasIdle = true;
                    }
                }
            } else if(canProcess && burnTime > 0) {
                press += stampSpeed;

                if (wasIdle && press > 0) {
                    wasIdle = false;
                }

                if(press >= maxPress) {
                    completeOperation();
                    isRetracting = true;
                    delay = 10;
                    burnTime = Math.max(0, burnTime - 200);
                    setChanged();
                }
            } else if(press > 0) {
                isRetracting = true;
            }
        } else {
            delay--;
        }

        if (speed == 0 && press == 0 && !isRetracting && delay <= 0) {
            wasIdle = true;
        }

        ItemStack fuel = inventory.getStackInSlot(0);
        if(!fuel.isEmpty() && ForgeHooks.getBurnTime(fuel, null) > 0) {
            if(burnTime < 200) {
                int burnTimeToAdd = ForgeHooks.getBurnTime(fuel, null);
                burnTime += burnTimeToAdd;

                if(fuel.hasCraftingRemainingItem()) {
                    inventory.setStackInSlot(0, fuel.getCraftingRemainingItem());
                } else {
                    fuel.shrink(1);
                    if(fuel.isEmpty()) {
                        inventory.setStackInSlot(0, ItemStack.EMPTY);
                    }
                }
                setChanged();
            }
        }

        if (oldSpeed != speed || oldPress != press || oldRetracting != isRetracting) {
            syncWithClient();
            setChanged();
        }
    }

    private boolean canProcess() {
        if(burnTime <= 0) {
            return false;
        }

        ItemStack stamp = inventory.getStackInSlot(1);
        ItemStack input = inventory.getStackInSlot(2);

        if(stamp.isEmpty() || input.isEmpty()) {
            return false;
        }

        ItemStack output = PressRecipes.getOutput(input, stamp);
        if(output.isEmpty()) {
            return false;
        }

        ItemStack currentOutput = inventory.getStackInSlot(3);
        if(currentOutput.isEmpty()) {
            return true;
        }

        return currentOutput.getCount() + output.getCount() <= currentOutput.getMaxStackSize() &&
                ItemStack.isSameItemSameTags(currentOutput, output);
    }

    private void completeOperation() {
        ItemStack stamp = inventory.getStackInSlot(1);
        ItemStack input = inventory.getStackInSlot(2);
        ItemStack output = PressRecipes.getOutput(input, stamp);

        if(output.isEmpty()) {
            return;
        }

        ItemStack currentOutput = inventory.getStackInSlot(3);
        if(currentOutput.isEmpty()) {
            inventory.setStackInSlot(3, output.copy());
        } else {
            currentOutput.grow(output.getCount());
        }

        input.shrink(1);
        if(input.isEmpty()) {
            inventory.setStackInSlot(2, ItemStack.EMPTY);
        }

        if(stamp.isDamageableItem()) {
            stamp.setDamageValue(stamp.getDamageValue() + 1);
            if(stamp.getDamageValue() >= stamp.getMaxDamage()) {
                inventory.setStackInSlot(1, ItemStack.EMPTY);
            }
        }

        if(level != null) {
            playPressSound();
        }

        setChanged();
    }

    private void playPressSound() {
        SoundEvent soundEvent = SoundEvent.createFixedRangeEvent(
                ResLocation(RefStrings.MODID, "press.operate"),
                16.0f
        );

        if (level != null) {
            level.playSound(null, worldPosition, soundEvent, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public ContainerData getContainerData() {
        return dataAccess;
    }

    public boolean canPlayerAccessInventory(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }


    public void dropInventoryItems(Level level, BlockPos pos) {
        if (level.isClientSide) return;

        for (int i = 0; i < inventory.getSlots(); i++) {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty()) {
                Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
            }
        }
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.press");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerMachinePress(windowId, playerInventory, this);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("speed", speed);
        tag.putInt("burnTime", burnTime);
        tag.putInt("press", press);
        tag.putBoolean("isRetracting", isRetracting);
        tag.putInt("delay", delay);
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        speed = tag.getInt("speed");
        burnTime = tag.getInt("burnTime");
        press = tag.getInt("press");
        isRetracting = tag.getBoolean("isRetracting");
        delay = tag.getInt("delay");
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryHandler.invalidate();
    }
}