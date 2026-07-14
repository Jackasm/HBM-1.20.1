package com.hbm.network.client;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.extprop.HbmPlayerProps.IHbmPlayerProps;
import com.hbm.network.PacketBase;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ExtPropPacket extends PacketBase implements PacketBase.DecodablePacket {

    private HbmLivingProps livingProps;
    private IHbmPlayerProps playerProps;
    private FriendlyByteBuf buffer;

    public ExtPropPacket() {}

    public ExtPropPacket(HbmLivingProps livingProps, IHbmPlayerProps playerProps) {
        this.livingProps = livingProps;
        this.playerProps = playerProps;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        if (livingProps != null) {
            livingProps.serialize(buf);
        }
        if (playerProps != null) {
            playerProps.serialize(buf);
        }
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        ExtPropPacket packet = new ExtPropPacket();
        packet.buffer = new FriendlyByteBuf(buf.copy());
        return packet;
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            if (ctx.getDirection().getReceptionSide().isClient()) {
                Minecraft mc = Minecraft.getInstance();
                if (mc.level == null) return;

                Player player = mc.player;
                if (player == null) return;

                HbmLivingProps props = HbmLivingProps.getData(player);
                IHbmPlayerProps pprps = HbmPlayerProps.getData(player);

                if (props != null && pprps != null && buffer != null) {
                    props.deserialize(buffer);
                    pprps.deserialize(buffer);
                    buffer.release();
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}