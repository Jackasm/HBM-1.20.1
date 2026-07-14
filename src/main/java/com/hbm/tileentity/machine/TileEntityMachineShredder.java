package com.hbm.tileentity.machine;

import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.inventory.container.ContainerMachineShredder;

import com.hbm.inventory.recipes.ShredderRecipes;
import com.hbm.items.machine.ItemBlades;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityLoadedBase;
import com.hbm.util.Library;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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

import javax.annotation.Nullable;

public class TileEntityMachineShredder extends TileEntityLoadedBase implements IEnergyReceiver, MenuProvider {

    private final ItemStackHandler inventory = new ItemStackHandler(30) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private final LazyOptional<IItemHandler> handlerCap = LazyOptional.of(() -> inventory);

    public long power;
    public int progress;
    public int soundCycle = 0;
    public static final long MAX_POWER = 10000;
    public static final int PROCESSING_SPEED = 60;

    private String customName;



    public TileEntityMachineShredder(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_SHREDDER.get(), pos, state);
    }

    public int getProgressScaled(int scale) {
        return progress * scale / PROCESSING_SPEED;
    }

    public long getPowerScaled(long scale) {
        return power * scale / MAX_POWER;
    }

    public boolean hasPower() {
        return power > 0;
    }

    public boolean isProcessing() {
        return progress > 0;
    }

    public int getGearLeft() {
        ItemStack stack = inventory.getStackInSlot(27);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemBlades) {
            if (stack.getMaxDamage() == 0) return 1;
            if (stack.getDamageValue() < stack.getMaxDamage() / 2) return 1;
            if (stack.getDamageValue() != stack.getMaxDamage()) return 2;
            return 3;
        }
        return 0;
    }

    public int getGearRight() {
        ItemStack stack = inventory.getStackInSlot(28);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemBlades) {
            if (stack.getMaxDamage() == 0) return 1;
            if (stack.getDamageValue() < stack.getMaxDamage() / 2) return 1;
            if (stack.getDamageValue() != stack.getMaxDamage()) return 2;
            return 3;
        }
        return 0;
    }

    private boolean hasSpace(ItemStack stack) {
        ItemStack result = ShredderRecipes.getShredderResult(stack);
        if (result == null) return false;

        for (int i = 9; i < 27; i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (slot.isEmpty()) return true;
            if (slot.getItem() == result.getItem() &&
                    slot.getCount() + result.getCount() <= result.getMaxStackSize()) {
                return true;
            }
        }
        return false;
    }

    private boolean canProcess() {
        if (!inventory.getStackInSlot(27).isEmpty() && !inventory.getStackInSlot(28).isEmpty()) {
            int left = getGearLeft();
            int right = getGearRight();
            if (left > 0 && left < 3 && right > 0 && right < 3) {
                for (int i = 0; i < 9; i++) {
                    ItemStack stack = inventory.getStackInSlot(i);
                    if (!stack.isEmpty() && stack.getCount() > 0 && hasSpace(stack)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private void processItem() {
        for (int inpSlot = 0; inpSlot < 9; inpSlot++) {
            ItemStack inp = inventory.getStackInSlot(inpSlot);
            if (inp.isEmpty()) continue;

            ItemStack outp = ShredderRecipes.getShredderResult(inp);
            if (outp == null || !hasSpace(inp)) continue;

            boolean placed = false;
            for (int outSlot = 9; outSlot < 27; outSlot++) {
                ItemStack slot = inventory.getStackInSlot(outSlot);
                if (!slot.isEmpty() && slot.getItem() == outp.getItem() &&
                        slot.getDamageValue() == outp.getDamageValue() &&
                        slot.getCount() + outp.getCount() <= slot.getMaxStackSize()) {
                    slot.grow(outp.getCount());
                    inp.shrink(1);
                    placed = true;
                    break;
                }
            }

            if (!placed) {
                for (int outSlot = 9; outSlot < 27; outSlot++) {
                    if (inventory.getStackInSlot(outSlot).isEmpty()) {
                        inventory.setStackInSlot(outSlot, outp.copy());
                        inp.shrink(1);
                        break;
                    }
                }
            }

            if (inp.getCount() <= 0) {
                inventory.setStackInSlot(inpSlot, ItemStack.EMPTY);
            }
        }
    }

    public void tick() {
        if (level == null || level.isClientSide) return;


        networkPackNT(10);


        boolean flag = false;

        updateConnections();

        if (progress == 0) soundCycle = 0;

        if (hasPower() && canProcess()) {
            progress++;
            power -= 5;

            if (progress >= PROCESSING_SPEED) {
                // Повреждение лезвий
                for (int i = 27; i <= 28; i++) {
                    ItemStack blade = inventory.getStackInSlot(i);
                    if (!blade.isEmpty() && blade.getMaxDamage() > 0) {
                        blade.setDamageValue(blade.getDamageValue() + 1);
                    }
                }
                progress = 0;
                processItem();
                flag = true;
            }

            if (soundCycle == 0) {
                level.playSound(null, worldPosition, SoundEvents.MINECART_RIDING,
                        SoundSource.BLOCKS, getVolume(2.0F), 0.75F);
            }
            soundCycle++;
            if (soundCycle >= 50) soundCycle = 0;
        } else {
            progress = 0;
        }

        power = Library.chargeTEFromItems(inventory, 29, power, MAX_POWER);

        setChanged();
    }

    private void updateConnections() {
        for (Direction dir : Direction.values()) {
            trySubscribe(level, worldPosition.relative(dir), dir);
        }
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeInt(progress);
        buf.writeInt(soundCycle);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
        progress = buf.readInt();
        soundCycle = buf.readInt();
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        power = nbt.getLong("power");
        progress = nbt.getInt("progress");
        if (nbt.contains("name")) {
            customName = nbt.getString("name");
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putLong("power", power);
        nbt.putInt("progress", progress);
        if (customName != null) {
            nbt.putString("name", customName);
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return handlerCap.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        handlerCap.invalidate();
    }

    // IEnergyReceiverMK2
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

    // IGUIProvider
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
        return new ContainerMachineShredder(id, inv, this);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(customName != null ? customName : "container.machine_shredder");
    }

    public IItemHandler getInventory() {
        return inventory;
    }
}