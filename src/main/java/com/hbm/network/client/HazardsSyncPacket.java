package com.hbm.network.client;

import com.hbm.network.PacketBase;
import com.hbm.render.overlay.HazardOverlay;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class HazardsSyncPacket extends PacketBase implements PacketBase.DecodablePacket {
    private int blackLung;
    private int asbestos;

    public HazardsSyncPacket() {}

    public HazardsSyncPacket(int blackLung, int asbestos) {
        this.blackLung = blackLung;
        this.asbestos = asbestos;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(blackLung);
        buf.writeInt(asbestos);
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new HazardsSyncPacket(buf.readInt(), buf.readInt());
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Сохраняем данные на клиенте
            HazardOverlay.ClientHazardsData.setBlackLung(blackLung);
            HazardOverlay.ClientHazardsData.setAsbestos(asbestos);
        });
        context.get().setPacketHandled(true);
    }
}