package com.hbm.network.client;

import com.hbm.network.PacketBase;
import com.hbm.world.teleporter.TeleportationLoadingScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class TeleportLoadingPacket extends PacketBase implements PacketBase.DecodablePacket {

    private boolean show;

    public TeleportLoadingPacket() {}

    public TeleportLoadingPacket(boolean show) {
        this.show = show;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(show);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> handleClient());
        });
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        if (show) {
            net.minecraft.client.Minecraft.getInstance().setScreen(new TeleportationLoadingScreen());
        } else {
            net.minecraft.client.Minecraft.getInstance().setScreen(null);
        }
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.show = buf.readBoolean();
        return this;
    }
}