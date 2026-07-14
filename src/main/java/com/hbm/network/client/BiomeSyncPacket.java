package com.hbm.network.client;

import com.hbm.network.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class BiomeSyncPacket extends PacketBase implements PacketBase.DecodablePacket {

    private int chunkX;
    private int chunkZ;
    private byte blockX;
    private byte blockZ;
    private short biome;
    private short[] biomeArray;

    public BiomeSyncPacket() {
        // Для десериализации
    }

    // Для синхронизации всего чанка
    public BiomeSyncPacket(int chunkX, int chunkZ, short[] biomeArray) {
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.biomeArray = biomeArray;
    }

    // Для синхронизации отдельного блока
    public BiomeSyncPacket(int blockX, int blockZ, short biome) {
        this.chunkX = blockX >> 4;
        this.chunkZ = blockZ >> 4;
        this.blockX = (byte) (blockX & 15);
        this.blockZ = (byte) (blockZ & 15);
        this.biome = biome;
    }

    // Для совместимости с byte биомами
    public BiomeSyncPacket(int chunkX, int chunkZ, byte[] biomeArray) {
        this(chunkX, chunkZ, bytesToShorts(biomeArray));
    }

    public BiomeSyncPacket(int blockX, int blockZ, byte biome) {
        this(blockX, blockZ, (short) biome);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(this.chunkX);
        buf.writeInt(this.chunkZ);

        if (this.biomeArray == null) {
            buf.writeBoolean(false);
            buf.writeShort(this.biome);
            buf.writeByte(this.blockX);
            buf.writeByte(this.blockZ);
        } else {
            buf.writeBoolean(true);
            for (short s : this.biomeArray) {
                buf.writeShort(s);
            }
        }
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleClient));
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        Level level = mc.level;

        if (level == null) return;
        if (!level.hasChunk(this.chunkX, this.chunkZ)) return;

        LevelChunk chunk = level.getChunk(this.chunkX, this.chunkZ);
        chunk.setUnsaved(true);

        // Помечаем все секции чанка для перерендера
        if (mc.levelRenderer != null) {
            for (int y = 0; y < level.getSectionsCount(); y++) {
                mc.levelRenderer.setSectionDirty(this.chunkX, y, this.chunkZ);
            }
        }
    }

    private static short[] bytesToShorts(byte[] byteArray) {
        short[] shortArray = new short[byteArray.length];
        for (int i = 0; i < byteArray.length; i++) {
            shortArray[i] = (short) (byteArray[i] & 0xFF);
        }
        return shortArray;
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.chunkX = buf.readInt();
        this.chunkZ = buf.readInt();

        if (!buf.readBoolean()) {
            this.biome = buf.readShort();
            this.blockX = buf.readByte();
            this.blockZ = buf.readByte();
            this.biomeArray = null;
        } else {
            this.biomeArray = new short[256];
            for (int i = 0; i < 256; i++) {
                this.biomeArray[i] = buf.readShort();
            }
        }
        return this;
    }
}