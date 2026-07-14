package com.hbm.network.client;

import com.hbm.extprop.HbmPlayerProps;
import com.hbm.inventory.recipes.RecipeType;
import com.hbm.network.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Пакет для синхронизации разблокированных крафтеров с клиентом
 */
public class CrafterSyncPacket extends PacketBase implements PacketBase.DecodablePacket {

    private Set<RecipeType> unlockedCrafters;

    public CrafterSyncPacket() {
        // Для десериализации
    }

    public CrafterSyncPacket(Set<RecipeType> unlockedCrafters) {
        this.unlockedCrafters = new HashSet<>(unlockedCrafters);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(unlockedCrafters.size());
        for (RecipeType type : unlockedCrafters) {
            buf.writeInt(type.ordinal());
        }
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Получаем игрока на клиентской стороне
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.player != null) {
                HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(mc.player);
                if (props != null) {
                    // Очищаем и добавляем полученные крафтеры
                    // Используем рефлексию или добавляем метод setUnlockedCrafters
                    // Пока просто очищаем и добавляем через публичные методы
                    for (RecipeType type : unlockedCrafters) {
                        props.addCrafter(type);
                    }
                }
            }
        });
        context.get().setPacketHandled(true);
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.unlockedCrafters = new HashSet<>();
        int size = buf.readInt();
        RecipeType[] values = RecipeType.values();
        for (int i = 0; i < size; i++) {
            int ordinal = buf.readInt();
            if (ordinal >= 0 && ordinal < values.length) {
                unlockedCrafters.add(values[ordinal]);
            }
        }
        return this;
    }
}