package com.hbm.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public abstract class PacketBase {

    public abstract void encode(FriendlyByteBuf buf);
    public abstract void process(Supplier<NetworkEvent.Context> context);

    @SuppressWarnings("unchecked")
    public static <T extends PacketBase> T decode(FriendlyByteBuf buf, Class<T> packetClass) {
        try {
            // Пробуем найти конструктор с FriendlyByteBuf
            try {
                return packetClass.getDeclaredConstructor(FriendlyByteBuf.class).newInstance(buf);
            } catch (NoSuchMethodException e1) {
                // Если нет конструктора с FriendlyByteBuf, пробуем конструктор по умолчанию
                T instance = packetClass.getDeclaredConstructor().newInstance();
                if (instance instanceof DecodablePacket) {
                    return (T) ((DecodablePacket) instance).decode(buf);
                }
                return instance;
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to decode packet: " + packetClass.getSimpleName(), e);
        }
    }

    public static <T extends PacketBase> void handle(final T packet, Supplier<NetworkEvent.Context> ctx) {
        if (packet != null) {
            NetworkEvent.Context context = ctx.get();
            context.enqueueWork(() -> packet.process(ctx));
            context.setPacketHandled(true);
        }
    }

    public interface DecodablePacket {
        PacketBase decode(FriendlyByteBuf buf);
    }
}