package com.hbm.network.server;

import com.hbm.items.ModItems;
import com.hbm.network.PacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ItemDesignatorPacket extends PacketBase implements PacketBase.DecodablePacket {

    // 0: Add
    // 1: Subtract
    // 2: Set
    private int operator;
    private int value;
    private int reference;

    public ItemDesignatorPacket() {}

    public ItemDesignatorPacket(int operator, int value, int reference) {
        this.operator = operator;
        this.value = value;
        this.reference = reference;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        return new ItemDesignatorPacket(buf.readInt(), buf.readInt(), buf.readInt());
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(operator);
        buf.writeInt(value);
        buf.writeInt(reference);
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        ServerPlayer player = context.get().getSender();
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();

        if (stack != null && stack.getItem() == ModItems.DESIGNATOR_MANUAL.get()) {
            CompoundTag tag = stack.getTag();
            if (tag == null) {
                tag = new CompoundTag();
                stack.setTag(tag);
            }

            int x = tag.getInt("xCoord");
            int z = tag.getInt("zCoord");

            int result = 0;

            if (operator == 0) {
                result += value;
            }
            if (operator == 1) {
                result -= value;
            }
            if (operator == 2) {
                if (reference == 0) {
                    tag.putInt("xCoord", (int) Math.round(player.getX()));
                } else {
                    tag.putInt("zCoord", (int) Math.round(player.getZ()));
                }
                return;
            }

            if (reference == 0) {
                tag.putInt("xCoord", x + result);
            } else {
                tag.putInt("zCoord", z + result);
            }
        }
    }
}