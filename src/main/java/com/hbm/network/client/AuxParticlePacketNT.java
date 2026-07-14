package com.hbm.network.client;

import com.hbm.main.MainRegistry;
import com.hbm.network.PacketBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.Objects;
import java.util.function.Supplier;

public class AuxParticlePacketNT extends PacketBase implements PacketBase.DecodablePacket{
    private CompoundTag nbt;

    public AuxParticlePacketNT() {
        // Для десериализации
    }

    public AuxParticlePacketNT(CompoundTag nbt, double x, double y, double z) {
        this.nbt = nbt.copy();
        this.nbt.putDouble("posX", x);
        this.nbt.putDouble("posY", y);
        this.nbt.putDouble("posZ", z);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        // Записываем NBT в буфер
        buf.writeNbt(Objects.requireNonNullElseGet(nbt, CompoundTag::new));
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        context.get().enqueueWork(() -> {
            // Обрабатываем на клиентской стороне
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::handleClient);
        });
        context.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private void handleClient() {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (mc.level == null || nbt == null)
            return;

        // Стандартная обработка через MainRegistry
        MainRegistry.proxy.effectNT(nbt);

    }

    // Реализация DecodablePacket интерфейса
    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        this.nbt = buf.readNbt();
        if (this.nbt == null) {
            this.nbt = new CompoundTag();
        }
        return this;
    }
}