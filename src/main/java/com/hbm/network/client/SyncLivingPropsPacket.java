package com.hbm.network.client;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.network.PacketBase;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class SyncLivingPropsPacket extends PacketBase implements PacketBase.DecodablePacket {

    private int entityId;
    private int fire;
    private int phosphorus;
    private int balefire;
    private int blackFire;
    private int blackLung;
    private int asbestos;
    private float radiation;
    private float digamma;
    private int oil;
    private int contagion;
    private int timer;

    // Конструктор по умолчанию (обязателен для DecodablePacket)
    public SyncLivingPropsPacket() {}

    // Конструктор для создания пакета на сервере
    public SyncLivingPropsPacket(LivingEntity entity) {
        this.entityId = entity.getId();
        this.fire = HbmLivingProps.getFire(entity);
        this.phosphorus = HbmLivingProps.getPhosphorus(entity);
        this.balefire = HbmLivingProps.getBalefire(entity);
        this.blackFire = HbmLivingProps.getBlackFire(entity);
        this.blackLung = HbmLivingProps.getBlackLung(entity);
        this.asbestos = HbmLivingProps.getAsbestos(entity);
        this.radiation = HbmLivingProps.getRadiation(entity);
        this.digamma = HbmLivingProps.getDigamma(entity);
        this.oil = HbmLivingProps.getOil(entity);
        this.contagion = HbmLivingProps.getContagion(entity);
        this.timer = HbmLivingProps.getTimer(entity);
    }

    @Override
    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityId);
        buf.writeInt(fire);
        buf.writeInt(phosphorus);
        buf.writeInt(balefire);
        buf.writeInt(blackFire);
        buf.writeInt(blackLung);
        buf.writeInt(asbestos);
        buf.writeFloat(radiation);
        buf.writeFloat(digamma);
        buf.writeInt(oil);
        buf.writeInt(contagion);
        buf.writeInt(timer);
    }

    @Override
    public PacketBase decode(FriendlyByteBuf buf) {
        SyncLivingPropsPacket packet = new SyncLivingPropsPacket();
        packet.entityId = buf.readInt();
        packet.fire = buf.readInt();
        packet.phosphorus = buf.readInt();
        packet.balefire = buf.readInt();
        packet.blackFire = buf.readInt();
        packet.blackLung = buf.readInt();
        packet.asbestos = buf.readInt();
        packet.radiation = buf.readFloat();
        packet.digamma = buf.readFloat();
        packet.oil = buf.readInt();
        packet.contagion = buf.readInt();
        packet.timer = buf.readInt();
        return packet;
    }

    @Override
    public void process(Supplier<NetworkEvent.Context> context) {
        NetworkEvent.Context ctx = context.get();
        ctx.enqueueWork(() -> {
            // Проверяем, что мы на клиенте
            if (ctx.getDirection().getReceptionSide().isClient()) {
                net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
                if (mc.level == null) return;

                LivingEntity entity = (LivingEntity) mc.level.getEntity(entityId);
                if (entity != null) {
                    // Используем клиентские методы
                    HbmLivingProps.clientSetFire(entity, fire);
                    HbmLivingProps.clientSetPhosphorus(entity, phosphorus);
                    HbmLivingProps.clientSetBalefire(entity, balefire);
                    HbmLivingProps.clientSetBlackFire(entity, blackFire);
                    HbmLivingProps.clientSetBlackLung(entity, blackLung);
                    HbmLivingProps.clientSetAsbestos(entity, asbestos);
                    HbmLivingProps.clientSetRadiation(entity, radiation);
                    HbmLivingProps.clientSetDigamma(entity, digamma);
                    HbmLivingProps.clientSetOil(entity, oil);
                    HbmLivingProps.clientSetContagion(entity, contagion);
                    HbmLivingProps.clientSetTimer(entity, timer);
                }
            }
        });
        ctx.setPacketHandled(true);
    }
}