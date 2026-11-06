package com.hbm.network.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import com.hbm.network.PacketBase;
import com.hbm.network.PermaSyncHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PermaSyncPacket extends PacketBase implements PacketBase.DecodablePacket {

    private FriendlyByteBuf buffer;

    public PermaSyncPacket() {}

    // Конструктор для сервера
    public PermaSyncPacket(ServerPlayer player) {
        FriendlyByteBuf temp = new FriendlyByteBuf(io.netty.buffer.Unpooled.buffer());
        PermaSyncHandler.writePacket(temp, player.level(), player);
        this.buffer = temp;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        if (buffer == null) {
            throw new IllegalStateException("PermaSyncPacket buffer is null - use constructor with ServerPlayer on server side");
        }
        buf.writeBytes(buffer);
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        PermaSyncPacket packet = new PermaSyncPacket();
        packet.buffer = new FriendlyByteBuf(buf.copy());
        return packet;
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleClient));
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        Player player = mc.player;
        if (player != null && buffer != null) {
            PermaSyncHandler.readPacket(buffer, player);
            buffer.release();
        }
    
    }
}