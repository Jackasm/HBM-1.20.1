package com.hbm.network.server;

import com.hbm.network.PacketBase;
import com.hbm.inventory.container.ContainerAnvil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class AnvilCraftPacket extends PacketBase implements PacketBase.DecodablePacket {
    public int recipeIndex;
    public int shift;

    public AnvilCraftPacket() {}

    public AnvilCraftPacket(int recipeIndex, int shift) {
        this.recipeIndex = recipeIndex;
        this.shift = shift;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new AnvilCraftPacket(buf.readInt(), buf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(recipeIndex);
        buf.writeInt(shift);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        assert player != null;
        ContainerAnvil container = (ContainerAnvil) player.containerMenu;
        container.tryCraft(recipeIndex, shift > 0);
    }
}