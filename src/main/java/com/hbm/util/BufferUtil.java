package com.hbm.util;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class BufferUtil {

    private static final Charset CHARSET = StandardCharsets.UTF_8;

    // Writes a string to a byte buffer by encoding the length and raw bytes
    public static void writeString(ByteBuf buf, String value) {
        if (value == null) {
            buf.writeInt(-1);
            return;
        }

        byte[] bytes = value.getBytes(CHARSET);
        buf.writeInt(bytes.length);
        buf.writeBytes(bytes);
    }

    // Reads a string from a byte buffer via the written length and raw bytes
    public static String readString(ByteBuf buf) {
        int count = buf.readInt();
        if (count < 0) return null;

        byte[] bytes = new byte[count];
        buf.readBytes(bytes);

        return new String(bytes, CHARSET);
    }

    /**
     * Writes an integer array to a buffer.
     */
    public static void writeIntArray(ByteBuf buf, int[] array) {
        buf.writeInt(array.length);
        for (int value : array) {
            buf.writeInt(value);
        }
    }

    /**
     * Reads an integer array from a buffer.
     */
    public static int[] readIntArray(ByteBuf buf) {
        int length = buf.readInt();
        int[] array = new int[length];

        for (int i = 0; i < length; i++) {
            array[i] = buf.readInt();
        }

        return array;
    }

    /**
     * Writes a vector to a buffer.
     */
    public static void writeVec3(ByteBuf buf, Vec3 vector) {
        buf.writeBoolean(vector != null);
        if (vector == null) return;
        buf.writeDouble(vector.x);
        buf.writeDouble(vector.y);
        buf.writeDouble(vector.z);
    }

    /**
     * Reads a vector from a buffer.
     */
    public static Vec3 readVec3(ByteBuf buf) {
        boolean vectorExists = buf.readBoolean();
        if (!vectorExists) {
            return null;
        }
        double x = buf.readDouble();
        double y = buf.readDouble();
        double z = buf.readDouble();

        return new Vec3(x, y, z);
    }

    /**
     * Writes a CompoundTag to a buffer.
     */
    public static void writeNBT(ByteBuf buf, CompoundTag compound) {
        if (compound != null) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                NbtIo.writeCompressed(compound, baos);
                byte[] nbtData = baos.toByteArray();
                buf.writeShort((short) nbtData.length);
                buf.writeBytes(nbtData);
            } catch (IOException e) {
                e.printStackTrace();
                buf.writeShort(-1);
            }
        } else {
            buf.writeShort(-1);
        }
    }

    /**
     * Reads a CompoundTag from a buffer.
     */
    public static CompoundTag readNBT(ByteBuf buf) {
        short nbtLength = buf.readShort();

        if (nbtLength == -1) {
            return new CompoundTag();
        }

        byte[] tags = new byte[nbtLength];
        buf.readBytes(tags);

        try {
            ByteArrayInputStream bais = new ByteArrayInputStream(tags);
            DataInputStream dis = new DataInputStream(bais);
            return NbtIo.read(dis, NbtAccounter.UNLIMITED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new CompoundTag();
    }

    /**
     * Writes the ItemStack to the buffer.
     */
    public static void writeItemStack(ByteBuf buf, ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            buf.writeShort(-1);
        } else {
            buf.writeShort(Item.getId(stack.getItem()));
            buf.writeByte(stack.getCount());
            buf.writeShort(stack.getDamageValue());

            CompoundTag tag = stack.getTag();
            writeNBT(buf, tag);
        }
    }

    /**
     * Reads an ItemStack from a buffer.
     */
    public static ItemStack readItemStack(ByteBuf buf) {
        short id = buf.readShort();

        if (id >= 0) {
            int quantity = buf.readByte();
            int meta = buf.readShort();
            ItemStack stack = new ItemStack(Item.byId(id), quantity);
            stack.setDamageValue(meta);

            CompoundTag tag = readNBT(buf);
            if (!tag.isEmpty()) {
                stack.setTag(tag);
            }

            return stack;
        }
        return ItemStack.EMPTY;
    }
}