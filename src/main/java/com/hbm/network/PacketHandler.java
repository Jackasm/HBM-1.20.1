package com.hbm.network;

import com.hbm.network.client.*;
import com.hbm.network.server.*;
import com.hbm.util.RefStrings;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static com.hbm.util.ResLocation.ResLocation;

public class PacketHandler {
    private static final Logger LOGGER = LogManager.getLogger("HBM-Network");
    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            ResLocation(RefStrings.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        LOGGER.info("Starting HBM network registration...");

        // Client-bound packets
        registerPacket(ParticlePacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(ModelDataUpdatePacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(HazardsSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(GunAnimationPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(AuxParticlePacketNT.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(SyncLivingPropsPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(BiomeSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(BufPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(ExtPropPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(PermaSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(PlayerInformPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(TeleportLoadingPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(QuestSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(HbmAnimationPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(ParticleBurstPacket.class, NetworkDirection.PLAY_TO_CLIENT);
        registerPacket(CrafterSyncPacket.class, NetworkDirection.PLAY_TO_CLIENT);

        // Server-bound packets
        registerPacket(AnvilCraftPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerPacket(AuxButtonPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerPacket(KeybindPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerPacket(NBTControlPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerPacket(QuestActionPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerPacket(CargoContainerExtractPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerPacket(CargoContainerInsertPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerPacket(ItemDesignatorPacket.class, NetworkDirection.PLAY_TO_SERVER);
        registerPacket(ItemBobmazonPacket.class, NetworkDirection.PLAY_TO_SERVER);



        LOGGER.info("HBM network registration completed - registered {} packets", packetId);
    }

    private static <T extends PacketBase> void registerPacket(Class<T> packetClass, NetworkDirection direction) {
        CHANNEL.registerMessage(
                packetId++,
                packetClass,
                PacketBase::encode,
                buf -> PacketBase.decode(buf, packetClass),
                PacketBase::handle,
                java.util.Optional.of(direction)
        );
    }

    // Методы отправки
    public static void sendToServer(PacketBase packet) {
        CHANNEL.sendToServer(packet);
    }

    public static void sendToPlayer(PacketBase packet, ServerPlayer player) {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToAllPlayers(PacketBase packet) {
        CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }

    // Вспомогательный метод для отправки анимации оружия
    public static void sendToAllTracking(net.minecraft.world.entity.Entity entity, PacketBase packet) {
        CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), packet);
    }
}