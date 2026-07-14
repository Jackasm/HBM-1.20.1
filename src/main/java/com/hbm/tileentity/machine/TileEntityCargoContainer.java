package com.hbm.tileentity.machine;

import com.hbm.inventory.CargoItemHandler;
import com.hbm.inventory.container.ContainerCargoContainer;

import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityMachineBase;
import com.mojang.brigadier.StringReader;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class TileEntityCargoContainer extends TileEntityMachineBase implements MenuProvider {

    private final CargoItemHandler itemHandler;
    private final LazyOptional<CargoItemHandler> itemHandlerCap;

    public TileEntityCargoContainer(BlockPos pos, BlockState state) {
        super(ModTileEntity.CARGO_CONTAINER.get(), pos, state);
        this.itemHandler = new CargoItemHandler(this);
        this.itemHandlerCap = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public @NotNull Component getDisplayName() {
        return Component.translatable("container.cargo_container");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @NotNull Inventory playerInventory, @NotNull Player player) {
        return new ContainerCargoContainer(containerId, playerInventory, this);
    }

    public boolean stillValid(Player player) {
        return player.distanceToSqr(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5) <= 64;
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        // Периодическая синхронизация
        if (level.getGameTime() % 20 == 0) {
            networkPackNT(150);
        }
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        CompoundTag invTag = nbt.getCompound("inventory");
        for (int i = 0; i < 100; i++) {
            String key = "slot_" + i;
            if (invTag.contains(key)) {
                CompoundTag slotTag = invTag.getCompound(key);
                ItemStack stack = ItemStack.of(slotTag);
                long count = slotTag.getLong("realCount");
                if (!stack.isEmpty()) {
                    itemHandler.setSlotData(i, stack, count);
                } else {
                    itemHandler.setSlotData(i, ItemStack.EMPTY, 0);
                }
            } else {
                itemHandler.setSlotData(i, ItemStack.EMPTY, 0);
            }
        }
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        CompoundTag invTag = new CompoundTag();
        for (int i = 0; i < 100; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            long count = itemHandler.getRealCount(i);
            if (!stack.isEmpty() || count > 0) {
                CompoundTag slotTag = new CompoundTag();
                stack.save(slotTag);
                slotTag.putLong("realCount", count);
                invTag.put("slot_" + i, slotTag);
            }
        }
        nbt.put("inventory", invTag);
    }

    @Override
    public void serialize(ByteBuf buf) {
        super.serialize(buf);
        for (int i = 0; i < 100; i++) {
            ItemStack stack = itemHandler.getStackInSlot(i);
            long count = itemHandler.getRealCount(i);
            buf.writeBoolean(!stack.isEmpty() || count > 0);
            if (!stack.isEmpty() || count > 0) {
                String itemId = Objects.requireNonNull(ForgeRegistries.ITEMS.getKey(stack.getItem())).toString();
                buf.writeInt(itemId.length());
                buf.writeCharSequence(itemId, java.nio.charset.StandardCharsets.UTF_8);
                buf.writeInt(stack.getDamageValue());
                buf.writeBoolean(stack.hasTag());
                if (stack.hasTag()) {
                    byte[] nbtBytes = Objects.requireNonNull(stack.getTag()).toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
                    buf.writeInt(nbtBytes.length);
                    buf.writeBytes(nbtBytes);
                }
                buf.writeLong(count);
            }
        }
    }

    @Override
    public void deserialize(ByteBuf buf) {
        super.deserialize(buf);
        for (int i = 0; i < 100; i++) {
            boolean hasData = buf.readBoolean();
            if (hasData) {
                int idLen = buf.readInt();
                String itemId = buf.readCharSequence(idLen, java.nio.charset.StandardCharsets.UTF_8).toString();
                int damage = buf.readInt();
                boolean hasNbt = buf.readBoolean();
                CompoundTag nbt = null;
                if (hasNbt) {
                    int nbtLen = buf.readInt();
                    byte[] nbtBytes = new byte[nbtLen];
                    buf.readBytes(nbtBytes);
                    String nbtStr = new String(nbtBytes, java.nio.charset.StandardCharsets.UTF_8);
                    try {
                        StringReader reader = new com.mojang.brigadier.StringReader(nbtStr);
                        TagParser parser = new net.minecraft.nbt.TagParser(reader);
                        nbt = (CompoundTag) parser.readValue();
                    } catch (Exception e) {
                        nbt = null;
                    }
                }
                long count = buf.readLong();

                ResourceLocation location = ResourceLocation.parse(itemId);
                Item item = net.minecraftforge.registries.ForgeRegistries.ITEMS.getValue(location);
                if (item != null) {
                    ItemStack stack = new ItemStack(item, 1);
                    stack.setDamageValue(damage);
                    if (nbt != null) stack.setTag(nbt);
                    itemHandler.setSlotData(i, stack, count);
                } else {
                    itemHandler.setSlotData(i, ItemStack.EMPTY, 0);
                }
            } else {
                itemHandler.setSlotData(i, ItemStack.EMPTY, 0);
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return LazyOptional.of(() -> itemHandler).cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        itemHandlerCap.invalidate();
    }

    public CargoItemHandler getItemHandler() {
        return itemHandler;
    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (level != null && !level.isClientSide) {
            networkPackNT(150);
        }
    }

}