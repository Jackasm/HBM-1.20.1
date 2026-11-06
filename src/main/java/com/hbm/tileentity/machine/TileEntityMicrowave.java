package com.hbm.tileentity.machine;

import com.hbm.api.energy.IBatteryItem;
import com.hbm.api.energy.IEnergyReceiver;
import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.explosion.vanillant.standard.EntityProcessorCrossSmooth;
import com.hbm.explosion.vanillant.standard.ExplosionEffectWeapon;
import com.hbm.explosion.vanillant.standard.PlayerProcessorStandard;
import com.hbm.inventory.container.ContainerMicrowave;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
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
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class TileEntityMicrowave extends TileEntityMachineBase implements IEnergyReceiver, MenuProvider {

    public long power;
    public static final long maxPower = 50000;
    public static final int consumption = 50;
    public static final int maxTime = 300;
    public int time;
    public int speed;
    public static final int maxSpeed = 5;

    private final ItemStackHandler inventory = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            if (slot == 0) {
                return !getSmeltingResult(stack).isEmpty();
            }
            if (slot == 2) {
                return stack.getItem() instanceof IBatteryItem;
            }
            return false; // slot 1 — output, ничего не принимает
        }
    };

    private final LazyOptional<IItemHandler> inventoryCap = LazyOptional.of(() -> inventory);

    public TileEntityMicrowave(BlockPos pos, BlockState state) {
        super(ModTileEntity.MACHINE_MICROWAVE.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Энергоснабжение
        for (Direction dir : Direction.values()) {
            this.trySubscribe(level, worldPosition.relative(dir), dir);
        }

        this.power = Library.chargeTEFromItems(inventory, 2, this.power, maxPower);

        if (canProcess()) {
            if (speed >= maxSpeed) {
                level.destroyBlock(worldPosition, false);
                ExplosionVNT vnt = new ExplosionVNT(level, worldPosition.getX() + 0.5,
                        worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5, 5);
                vnt.setEntityProcessor(new EntityProcessorCrossSmooth(1, 50));
                vnt.setPlayerProcessor(new PlayerProcessorStandard());
                vnt.setSFX(new ExplosionEffectWeapon(10, 2.5F, 1F));
                vnt.explode();
                return;
            }

            if (time >= maxTime) {
                process();
                time = 0;
            }

            if (canProcess()) {
                power -= consumption;
                time += speed * 2;
            }
        }

        setChanged();
        networkPackNT(50);
    }

    public void handleButtonPacket(int value) {
        if (value == 0) speed++;
        if (value == 1) speed--;
        speed = Math.max(0, Math.min(speed, maxSpeed));
    }

    private void process() {
        ItemStack input = inventory.getStackInSlot(0);
        ItemStack result = getSmeltingResult(input);
        if (result.isEmpty()) return;

        ItemStack output = inventory.getStackInSlot(1);
        if (output.isEmpty()) {
            inventory.setStackInSlot(1, result.copy());
        } else {
            output.grow(result.getCount());
        }

        inventory.extractItem(0, 1, false);
        setChanged();
    }

    private boolean canProcess() {
        if (speed == 0) return false;
        if (power < consumption) return false;

        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) return false;

        ItemStack result = getSmeltingResult(input);
        if (result.isEmpty()) return false;

        if (!(input.getItem().isEdible()) && !(result.getItem().isEdible())) return false;

        ItemStack output = inventory.getStackInSlot(1);
        if (output.isEmpty()) return true;
        if (!ItemStack.isSameItemSameTags(result, output)) return false;

        return output.getCount() + result.getCount() <= output.getMaxStackSize();
    }

    private ItemStack getSmeltingResult(ItemStack input) {
        if (level == null || input.isEmpty()) return ItemStack.EMPTY;
        Optional<SmeltingRecipe> recipe = level.getRecipeManager()
                .getRecipeFor(RecipeType.SMELTING, new net.minecraft.world.SimpleContainer(input), level);
        return recipe.map(r -> r.getResultItem(level.registryAccess())).orElse(ItemStack.EMPTY);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        buf.writeLong(power);
        buf.writeInt(time);
        buf.writeInt(speed);
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        power = buf.readLong();
        time = buf.readInt();
        speed = buf.readInt();
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put("inventory", inventory.serializeNBT());
        nbt.putLong("power", power);
        nbt.putInt("speed", speed);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        inventory.deserializeNBT(nbt.getCompound("inventory"));
        power = nbt.getLong("power");
        speed = nbt.getInt("speed");
    }

    public long getPowerScaled(int i) { return (power * i) / maxPower; }
    public int getProgressScaled(int i) { return (time * i) / maxTime; }
    public int getSpeedScaled(int i) { return (speed * i) / maxSpeed; }

    @Override
    public @NotNull AABB getRenderBoundingBox() {
        return new AABB(worldPosition.getX() - 1, worldPosition.getY() - 1, worldPosition.getZ() - 1,
                worldPosition.getX() + 2, worldPosition.getY() + 2, worldPosition.getZ() + 2);
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

    // IEnergyReceiver

    @Override public void setPower(long i) { power = i; }
    @Override public long getPower() { return power; }
    @Override public long getMaxPower() { return maxPower; }

    // MenuProvider

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.microwave");
    }

    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerMicrowave(id, inv, this);
    }

    public IItemHandler getInventory() {
        return inventory;
    }
}