package com.hbm.network.server;

import com.hbm.handler.HbmKeybindsServer;
import com.hbm.handler.HbmKeybinds.EnumKeybind;
import com.hbm.network.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class KeybindPacket extends PacketBase implements PacketBase.DecodablePacket{
    private int key;
    private boolean pressed;

    public KeybindPacket() {
        // Для десериализации
    }

    public KeybindPacket(EnumKeybind key, boolean pressed) {
        this.key = key.ordinal();
        this.pressed = pressed;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(key);
        buf.writeBoolean(pressed);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Получаем игрока на серверной стороне
            net.minecraft.server.level.ServerPlayer player = context.get().getSender();
            if (player != null) {
                // Вызываем обработчик нажатий клавиш на сервере
                HbmKeybindsServer.onPressedServer(player, EnumKeybind.values()[key], pressed);
            }
        });
        context.get().setPacketHandled(true);
    }

    // Реализация DecodablePacket интерфейса
    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.key = buf.readInt();
        this.pressed = buf.readBoolean();
        return this;
    }
}