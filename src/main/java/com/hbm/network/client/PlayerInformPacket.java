package com.hbm.network.client;

import com.hbm.network.PacketBase;
import com.hbm.render.util.RenderInfoSystem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerInformPacket extends PacketBase implements PacketBase.DecodablePacket {

    private boolean fancy;
    private String message = "";
    private int id;
    private Component component;
    private int millis = 0;

    public PlayerInformPacket() {
        // Для десериализации
    }

    public PlayerInformPacket(String message, int id) {
        this.fancy = false;
        this.message = message;
        this.id = id;
    }

    public PlayerInformPacket(Component component, int id) {
        this.fancy = true;
        this.component = component;
        this.id = id;
    }

    public PlayerInformPacket(String message, int id, int millis) {
        this.fancy = false;
        this.message = message;
        this.millis = millis;
        this.id = id;
    }

    public PlayerInformPacket(Component component, int id, int millis) {
        this.fancy = true;
        this.component = component;
        this.millis = millis;
        this.id = id;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(id);
        buf.writeInt(millis);
        buf.writeBoolean(fancy);
        if (!fancy) {
            buf.writeUtf(message);
        } else {
            buf.writeUtf(Component.Serializer.toJson(component));
        }
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleClient));
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        int displayMillis = millis == 0 ? 3000 : millis;

        if (fancy) {
            int color = component.getStyle().getColor() != null ?
                    component.getStyle().getColor().getValue() : 0xFFFFFF;
            RenderInfoSystem.push(new RenderInfoSystem.InfoEntry(component.getString(), displayMillis).withColor(color), id);
        } else {
            RenderInfoSystem.push(new RenderInfoSystem.InfoEntry(message, displayMillis).withColor(getColorFromId(id)), id);
        }
    }

    private int getColorFromId(int id) {
        // Можно задать разные цвета для разных типов сообщений
        // ID_DETONATOR = 0 в оригинале
        return switch (id) {
            case 0 -> 0xFFFFFF; // Белый
            default -> 0xFFFFFF;
        };
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.id = buf.readInt();
        this.millis = buf.readInt();
        this.fancy = buf.readBoolean();

        if (!fancy) {
            this.message = buf.readUtf();
        } else {
            String json = buf.readUtf();
            this.component = Component.Serializer.fromJson(json);
        }
        return this;
    }
}