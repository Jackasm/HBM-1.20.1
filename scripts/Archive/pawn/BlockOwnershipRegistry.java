package com.hbm.pawn;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Хранит привязку ванильных блоков к владельцам.
 * Для модальных блоков владелец хранится в самом TileEntity.
 */
public class BlockOwnershipRegistry extends SavedData {
    private static final String ID = "hbm_pawn_ownership";

    // позиция -> UUID владельца
    private final Map<BlockPos, UUID> owners = new HashMap<>();

    public static BlockOwnershipRegistry get(Level level) {
        if (level instanceof ServerLevel serverLevel) {
            return serverLevel.getDataStorage().computeIfAbsent(
                    BlockOwnershipRegistry::new,
                    BlockOwnershipRegistry::new,
                    ID
            );
        }
        return new BlockOwnershipRegistry();
    }

    public BlockOwnershipRegistry() {}

    public BlockOwnershipRegistry(CompoundTag tag) {
        ListTag list = tag.getList("owners", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            BlockPos pos = NbtUtils.readBlockPos(entry.getCompound("pos"));
            UUID owner = entry.getUUID("owner");
            owners.put(pos, owner);
        }
    }

    @Override
    public @NotNull CompoundTag save(@NotNull CompoundTag tag) {
        ListTag list = new ListTag();
        for (Map.Entry<BlockPos, UUID> entry : owners.entrySet()) {
            CompoundTag entryTag = new CompoundTag();
            entryTag.put("pos", NbtUtils.writeBlockPos(entry.getKey()));
            entryTag.putUUID("owner", entry.getValue());
            list.add(entryTag);
        }
        tag.put("owners", list);
        return tag;
    }

    /**
     * Устанавливает владельца для блока (обычно ванильного).
     */
    public void setOwner(BlockPos pos, UUID owner) {
        if (owner == null) {
            owners.remove(pos);
        } else {
            owners.put(pos, owner);
        }
        setDirty();
    }

    /**
     * Возвращает владельца блока или null, если блок не зарегистрирован (публичный).
     */
    @Nullable
    public UUID getOwner(BlockPos pos) {
        return owners.get(pos);
    }

    /**
     * Удаляет запись о блоке (при разрушении).
     */
    public void remove(BlockPos pos) {
        owners.remove(pos);
        setDirty();
    }

    /**
     * Проверяет, принадлежит ли блок указанному владельцу.
     */
    public boolean isOwner(BlockPos pos, UUID owner) {
        UUID blockOwner = owners.get(pos);
        return blockOwner == null || blockOwner.equals(owner);
    }
}