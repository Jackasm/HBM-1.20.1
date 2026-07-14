package com.hbm.pawn;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class InventoryRegistry extends SavedData {
    private static final String ID = "hbm_pawn_inventory_registry";

    public record StorageEntry(BlockPos pos, StorageType type) {}
    public record ConsumerEntry(BlockPos pos, ConsumerType type) {}

    private final Map<BlockPos, StorageEntry> storages = new HashMap<>();
    private final Map<BlockPos, ConsumerEntry> consumers = new HashMap<>();

    public Map<BlockPos, StorageEntry> getStorages() { return storages; }
    public Map<BlockPos, ConsumerEntry> getConsumers() { return consumers; }

    public void addStorage(BlockPos pos, StorageType type) {
        storages.put(pos, new StorageEntry(pos, type));
        setDirty();
    }

    public void addConsumer(BlockPos pos, ConsumerType type) {
        consumers.put(pos, new ConsumerEntry(pos, type));
        setDirty();
    }

    public void remove(BlockPos pos) {
        if (storages.remove(pos) != null) setDirty();
        if (consumers.remove(pos) != null) setDirty();
    }

    public static InventoryRegistry get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            // Правильный синтаксис для Forge 1.20.1
            return serverLevel.getDataStorage().computeIfAbsent(
                    InventoryRegistry::createFromNbt,  // функция загрузки из NBT
                    InventoryRegistry::new,             // фабрика для нового
                    ID
            );
        }
        return new InventoryRegistry();
    }

    // Статический метод для загрузки из NBT
    public static InventoryRegistry createFromNbt(CompoundTag tag) {
        return new InventoryRegistry(tag);
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag storageList = new ListTag();
        for (StorageEntry e : storages.values()) {
            if (e == null || e.pos() == null) continue; // пропускаем null
            CompoundTag entry = new CompoundTag();
            entry.put("pos", NbtUtils.writeBlockPos(e.pos()));
            entry.putString("type", e.type().name());
            storageList.add(entry);
        }
        tag.put("storages", storageList);

        ListTag consumerList = new ListTag();
        for (ConsumerEntry e : consumers.values()) {
            if (e == null || e.pos() == null) continue; // пропускаем null
            CompoundTag entry = new CompoundTag();
            entry.put("pos", NbtUtils.writeBlockPos(e.pos()));
            entry.putString("type", e.type().name());
            consumerList.add(entry);
        }
        tag.put("consumers", consumerList);
        return tag;
    }

    public InventoryRegistry() {}

    private InventoryRegistry(CompoundTag tag) {
        ListTag storageList = tag.getList("storages", 10);
        for (int i = 0; i < storageList.size(); i++) {
            CompoundTag entry = storageList.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(entry.getCompound("pos"));
            StorageType type = StorageType.valueOf(entry.getString("type"));
            storages.put(pos, new StorageEntry(pos, type));
        }

        ListTag consumerList = tag.getList("consumers", 10);
        for (int i = 0; i < consumerList.size(); i++) {
            CompoundTag entry = consumerList.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(entry.getCompound("pos"));
            ConsumerType type = ConsumerType.valueOf(entry.getString("type"));
            consumers.put(pos, new ConsumerEntry(pos, type));
        }
    }

    /**
     * Находит хранилища, содержащие указанный предмет и принадлежащие владельцу пешки.
     */
    public List<BlockPos> findStoragesWithItemForOwner(ItemStack item, Level level, UUID ownerUUID) {
        List<BlockPos> result = new ArrayList<>();
        for (Map.Entry<BlockPos, StorageEntry> entry : storages.entrySet()) {
            if (!entry.getValue().type().canStoreItems()) continue;

            // Проверка владельца для ванильных хранилищ
            UUID storageOwner = BlockOwnershipRegistry.get(level).getOwner(entry.getKey());
            if (storageOwner != null && !storageOwner.equals(ownerUUID)) continue;

            BlockEntity be = level.getBlockEntity(entry.getKey());
            if (be == null) continue;

            IItemHandler handler = getItemHandler(be);
            if (handler == null) continue;

            for (int i = 0; i < handler.getSlots(); i++) {
                ItemStack stack = handler.getStackInSlot(i);
                if (!stack.isEmpty() && ItemStack.isSameItem(stack, item)) {
                    result.add(entry.getKey());
                    break;
                }
            }
        }
        return result;
    }

    private IItemHandler getItemHandler(BlockEntity be) {
        var capability = be.getCapability(ForgeCapabilities.ITEM_HANDLER).resolve();
        if (capability.isPresent()) return capability.get();
        if (be instanceof Container container) return new ContainerWrapper(container);
        return null;
    }

}