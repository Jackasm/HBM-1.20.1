package com.hbm.tileentity.machine;

import com.hbm.tileentity.TileEntityMachineBase;
import com.hbm.blocks.machine.BlastFurnaceBlock;
import com.hbm.inventory.container.ContainerBlastFurnace;
import com.hbm.inventory.recipes.BlastFurnaceRecipes;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class TileEntityBlastFurnace extends TileEntityMachineBase implements MenuProvider{

    // Константы
    public static final int SLOT_UPPER = 0;
    public static final int SLOT_LOWER = 1;
    public static final int SLOT_FUEL = 2;
    public static final int SLOT_OUTPUT = 3;

    public static final int MAX_FUEL = 12800;
    public static final int PROCESSING_SPEED = 400;

    // Прогресс и топливо
    private int progress = 0;
    private int fuel = 0;

    // Настройки сторон для автоматизации
    public byte sideFuel = 1;
    public byte sideUpper = 1;
    public byte sideLower = 1;

    public boolean hasExtension = false;

    // ItemHandler для Forge Capability
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
                case SLOT_FUEL -> isFuel(stack);
                case SLOT_OUTPUT -> false;
                default -> true;
            };
        }
    };

    private final LazyOptional<IItemHandler> inventoryHandler = LazyOptional.of(() -> inventory);

    // ContainerData для синхронизации с GUI
    private final ContainerData dataAccess = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> progress;
                case 1 -> fuel;
                case 2 -> sideFuel;
                case 3 -> sideUpper;
                case 4 -> sideLower;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) {
            switch (index) {
                case 0 -> progress = value;
                case 1 -> fuel = value;
                case 2 -> sideFuel = (byte) value;
                case 3 -> sideUpper = (byte) value;
                case 4 -> sideLower = (byte) value;
            }
            setChanged();
        }

        @Override
        public int getCount() {
            return 5;
        }
    };

    public TileEntityBlastFurnace(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_BLAST_FURNACE.get(), pos, state);
    }

    // Синхронизация с клиентом
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

    // Основной метод обновления
    public static void serverTick(Level level, BlockPos ignoredPos, BlockState ignoredState, TileEntityBlastFurnace furnace) {
        if(!level.isClientSide) {
            furnace.updateServer();
        }
    }

    public void updateServer() {
        if (level == null) return;

        boolean hasExtension = this.hasExtension;

        processFuel();

        if(canProcess()) {
            progress += hasExtension ? 3 : 1;
            fuel--;

            if(progress >= PROCESSING_SPEED) {
                progress = 0;
                processItem();
            }
        } else {
            progress = 0;
        }

        // Обновление состояния блока
        boolean shouldBeLit = canProcess() && fuel > 0;
        boolean isCurrentlyLit = getBlockState().getValue(BlastFurnaceBlock.LIT);

        if (shouldBeLit != isCurrentlyLit) {
            level.setBlock(worldPosition, getBlockState().setValue(BlastFurnaceBlock.LIT, shouldBeLit), 3);
        }

        setChanged();
    }

    private void processFuel() {
        ItemStack fuelStack = inventory.getStackInSlot(SLOT_FUEL);
        if(!fuelStack.isEmpty() && fuel <= (MAX_FUEL - getItemPower(fuelStack))) {
            int fuelPower = getItemPower(fuelStack);

            if(fuelPower > 0) {
                fuel += fuelPower;
                fuelStack.shrink(1);

                if(fuelStack.isEmpty()) {
                    ItemStack container = fuelStack.getItem().getCraftingRemainingItem(fuelStack);
                    inventory.setStackInSlot(SLOT_FUEL, container != null ? container : ItemStack.EMPTY);
                }

                setChanged();
            }
        }
    }

    private int getItemPower(ItemStack stack) {
        // Временная реализация - расширь по необходимости
        if (stack.getItem() == Items.COAL) return 200;
        if (stack.getItem() == Items.CHARCOAL) return 200;
        if (stack.getItem() == Items.COAL_BLOCK) return 2000;
        if (stack.getItem() == Items.BLAZE_ROD) return 1000;
        if (stack.getItem() == Items.BLAZE_POWDER) return 300;
        if (stack.getItem() == Items.LAVA_BUCKET) return 12800;
        return 0;
    }

    private boolean canProcess() {
        if(!hasPower()) return false;

        ItemStack upper = inventory.getStackInSlot(SLOT_UPPER);
        ItemStack lower = inventory.getStackInSlot(SLOT_LOWER);
        if(upper.isEmpty() || lower.isEmpty()) return false;

        ItemStack output = BlastFurnaceRecipes.getOutput(upper, lower);
        if(output.isEmpty()) return false;

        ItemStack outputSlot = inventory.getStackInSlot(SLOT_OUTPUT);
        return outputSlot.isEmpty() ||
                (ItemStack.isSameItemSameTags(outputSlot, output) && outputSlot.getCount() + output.getCount() <= outputSlot.getMaxStackSize());
    }

    private void processItem() {
        ItemStack upper = inventory.getStackInSlot(SLOT_UPPER);
        ItemStack lower = inventory.getStackInSlot(SLOT_LOWER);
        ItemStack output = BlastFurnaceRecipes.getOutput(upper, lower);

        if(output.isEmpty()) return;

        ItemStack outputSlot = inventory.getStackInSlot(SLOT_OUTPUT);
        if(outputSlot.isEmpty()) {
            inventory.setStackInSlot(SLOT_OUTPUT, output.copy());
        } else {
            outputSlot.grow(output.getCount());
        }

        upper.shrink(1);
        lower.shrink(1);
    }

    public boolean hasPower() {
        return fuel > 0;
    }

    private boolean isFuel(ItemStack stack) {
        return getItemPower(stack) > 0;
    }

    public ContainerData getContainerData() {
        return dataAccess;
    }

    public IItemHandler getInventory() {
        return inventory;
    }

    public boolean canPlayerAccessInventory(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5D, worldPosition.getY() + 0.5D, worldPosition.getZ() + 0.5D) <= 64.0D;
    }

    public void drops() {
        if(level != null && !level.isClientSide) {
            for(int i = 0; i < inventory.getSlots(); i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if(!stack.isEmpty()) {
                    Containers.dropItemStack(level, worldPosition.getX(), worldPosition.getY(), worldPosition.getZ(), stack);
                }
            }
        }
    }

    // MenuProvider
    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.blast_furnace");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerBlastFurnace(containerId, playerInventory, this);
    }




    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        tag.putInt("Progress", progress);
        tag.putInt("Fuel", fuel);
        tag.putByte("SideFuel", sideFuel);
        tag.putByte("SideUpper", sideUpper);
        tag.putByte("SideLower", sideLower);
        tag.putBoolean("hasExtension", hasExtension); // сохраняем флаг
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        progress = tag.getInt("Progress");
        fuel = tag.getInt("Fuel");
        sideFuel = tag.getByte("SideFuel");
        sideUpper = tag.getByte("SideUpper");
        sideLower = tag.getByte("SideLower");
        hasExtension = tag.getBoolean("hasExtension"); // загружаем флаг
    }

    // Capability система
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