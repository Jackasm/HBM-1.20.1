package com.hbm.pawn;

import net.minecraft.world.entity.player.Player;
import java.util.UUID;

/**
 * Интерфейс для объектов (TileEntity, сущности), которые имеют владельца.
 */
public interface IOwnable {

    /**
     * @return UUID владельца или null, если блок общий (публичный)
     */
    UUID getOwnerUUID();

    /**
     * Устанавливает владельца.
     */
    void setOwnerUUID(UUID owner);

    /**
     * Проверяет, является ли игрок владельцем.
     */
    default boolean isOwner(Player player) {
        return player != null && player.getUUID().equals(getOwnerUUID());
    }

    /**
     * Проверяет, является ли UUID владельцем.
     */
    default boolean isOwner(UUID uuid) {
        return uuid != null && uuid.equals(getOwnerUUID());
    }

    /**
     * @return true, если блок является общим (публичным)
     */
    default boolean isPublic() {
        return getOwnerUUID() == null;
    }
}