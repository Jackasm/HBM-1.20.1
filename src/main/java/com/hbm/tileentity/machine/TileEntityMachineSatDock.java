package com.hbm.tileentity.machine;

import com.hbm.entity.ModEntities;
import com.hbm.entity.missile.EntityMinerRocket;
import com.hbm.explosion.ExplosionNukeSmall;
import com.hbm.inventory.container.ContainerSatDock;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsSatellite;
import com.hbm.items.ISatChip;
import com.hbm.saveddata.SatelliteSavedData;
import com.hbm.saveddata.satellites.Satellite;
import com.hbm.saveddata.satellites.SatelliteMiner;
import com.hbm.tileentity.ModTileEntity;

import com.hbm.tileentity.TileEntityMachineBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

import static com.hbm.entity.missile.EntityMinerRocket.FREQ;

public class TileEntityMachineSatDock extends TileEntityMachineBase implements MenuProvider {

    private final ItemStackHandler inventory = new ItemStackHandler(16) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }

        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            return slot == 15; // Только в последний слот можно класть чип
        }
    };

    private final LazyOptional<IItemHandler> handler = LazyOptional.of(() -> inventory);

    private String customName;

    public TileEntityMachineSatDock(BlockPos pos, BlockState state) {
        super(ModTileEntity.SAT_DOCK.get(), pos, state);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.satDock");
    }

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        inventory.deserializeNBT(tag.getCompound("inventory"));
        if (tag.contains("name")) {
            customName = tag.getString("name");
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.put("inventory", inventory.serializeNBT());
        if (customName != null) {
            tag.putString("name", customName);
        }
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        SatelliteSavedData data = SatelliteSavedData.getData(level);

        ItemStack chip = inventory.getStackInSlot(15);
        if (!chip.isEmpty()) {
            int freq = ISatChip.getFreq(chip);
            Satellite sat = Objects.requireNonNull(data).getSatFromFreq(freq);

            int delay = 10 * 60 * 1000;

            if (sat instanceof SatelliteMiner miner) {
                if (miner.lastOp + delay < System.currentTimeMillis()) {
                    EntityMinerRocket rocket = new EntityMinerRocket(ModEntities.MINER_ROCKET.get(), level);
                    rocket.setPos(worldPosition.getX() + 0.5, 300, worldPosition.getZ() + 0.5);
                    rocket.getEntityData().set(FREQ, freq);
                    level.addFreshEntity(rocket);
                    miner.lastOp = System.currentTimeMillis();
                    data.setDirty();
                }
            }
        }

        // Проверяем ракеты
        AABB aabb = new AABB(
                worldPosition.getX() - 0.25 + 0.5, worldPosition.getY() + 0.75, worldPosition.getZ() - 0.25 + 0.5,
                worldPosition.getX() + 0.25 + 0.5, worldPosition.getY() + 2, worldPosition.getZ() + 0.25 + 0.5
        );
        List<EntityMinerRocket> rockets = level.getEntitiesOfClass(EntityMinerRocket.class, aabb);

        for (EntityMinerRocket rocket : rockets) {
            if (!chip.isEmpty() && ISatChip.getFreq(chip) != rocket.getFreq()) {
                rocket.discard();
                ExplosionNukeSmall.explode(level, worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                        ExplosionNukeSmall.PARAMS_TOTS);
                break;
            }

            if (rocket.isReturning() && rocket.timer == 50) {
                Satellite sat = Objects.requireNonNull(data).getSatFromFreq(ISatChip.getFreq(chip));
                if (sat instanceof SatelliteMiner miner) {
                    unloadCargo(miner);
                }
            }
        }

        // Выброс в соседние контейнеры
        ejectInto(worldPosition.offset(2, 0, 0));
        ejectInto(worldPosition.offset(-2, 0, 0));
        ejectInto(worldPosition.offset(0, 0, 2));
        ejectInto(worldPosition.offset(0, 0, -2));
    }

    private void unloadCargo(SatelliteMiner satellite) {
        if (level == null) return;

        int itemAmount = level.random.nextInt(6) + 10;
        String cargoKey = satellite.getCargo();

        if (cargoKey == null || cargoKey.isEmpty()) return;

        // Получаем пул по ключу
        ItemPool pool = switch (cargoKey) {
            case ItemPoolsSatellite.POOL_SAT_MINER -> ItemPoolsSatellite.getMinerPool();
            case ItemPoolsSatellite.POOL_SAT_LUNAR -> ItemPoolsSatellite.getLunarPool();
            default -> null;
        };

        if (pool == null) return;

        for (int i = 0; i < itemAmount; i++) {
            ItemStack stack = pool.generateOne(level.random);
            if (!stack.isEmpty()) {
                addToInv(stack);
            }
        }
    }

    private void addToInv(ItemStack stack) {
        if (level == null) return;

        for (int i = 0; i < 15; i++) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (!slotStack.isEmpty() && ItemStack.isSameItem(slotStack, stack) &&
                    slotStack.getCount() < slotStack.getMaxStackSize()) {
                int toAdd = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), stack.getCount());
                slotStack.grow(toAdd);
                stack.shrink(toAdd);
                if (stack.isEmpty()) return;
            }
        }

        for (int i = 0; i < 15; i++) {
            if (inventory.getStackInSlot(i).isEmpty()) {
                inventory.setStackInSlot(i, stack.copy());
                return;
            }
        }
    }

    private void ejectInto(BlockPos pos) {
        if (level == null) return;

        BlockEntity te = level.getBlockEntity(pos);
        if (te == null) return;

        LazyOptional<IItemHandler> cap = te.getCapability(ForgeCapabilities.ITEM_HANDLER);
        if (!cap.isPresent()) return;

        cap.ifPresent(handler -> {
            for (int i = 0; i < 15; i++) {
                ItemStack stack = inventory.getStackInSlot(i);
                if (stack.isEmpty()) continue;

                ItemStack remaining = stack.copy();
                for (int j = 0; j < handler.getSlots(); j++) {
                    ItemStack slotStack = handler.getStackInSlot(j);
                    if (slotStack.isEmpty()) {
                        ItemStack inserted = handler.insertItem(j, remaining, false);
                        remaining = inserted.isEmpty() ? ItemStack.EMPTY : inserted;
                        if (remaining.isEmpty()) break;
                    } else if (ItemStack.isSameItem(slotStack, remaining) && slotStack.getCount() < slotStack.getMaxStackSize()) {
                        int moved = Math.min(slotStack.getMaxStackSize() - slotStack.getCount(), remaining.getCount());
                        slotStack.grow(moved);
                        remaining.shrink(moved);
                        if (remaining.isEmpty()) break;
                    }
                }

                inventory.setStackInSlot(i, remaining);
            }
        });
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        handler.invalidate();
    }

    // ==================== GUI ====================

    public ItemStackHandler getInventory() {
        return inventory;
    }

    public boolean stillValid(Player player) {
        if (level == null || level.getBlockEntity(worldPosition) != this) {
            return false;
        }
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowId, @NotNull Inventory inv, @NotNull Player player) {
        return new ContainerSatDock(windowId, inv, this);
    }
}