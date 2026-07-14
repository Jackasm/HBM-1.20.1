package com.hbm.network.server;

import com.hbm.interfaces.IControlReceiver;
import com.hbm.network.PacketBase;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class NBTControlPacket extends PacketBase implements PacketBase.DecodablePacket {

    private CompoundTag nbt;
    private BlockPos pos;

    public NBTControlPacket() {
        // Для десериализации
    }

    public NBTControlPacket(CompoundTag nbt, BlockPos pos) {
        this.nbt = nbt;
        this.pos = pos;
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeBlockPos(pos);
        buf.writeNbt(nbt);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            ServerPlayer player = ctx.getSender();
            if (player == null || player.level() == null) return;

            BlockEntity te = player.level().getBlockEntity(pos);

            if (te instanceof IControlReceiver receiver) {
                if (receiver.hasPermission(player)) {
                    receiver.receiveControl(nbt);
                }
            }
        });
        ctx.setPacketHandled(true);
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.pos = buf.readBlockPos();
        this.nbt = buf.readNbt();
        if (this.nbt == null) {
            this.nbt = new CompoundTag();
        }
        return this;
    }
}