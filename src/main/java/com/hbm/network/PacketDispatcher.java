package com.hbm.network;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.extprop.HbmPlayerProps;
import com.hbm.handler.HbmKeybinds;
import com.hbm.inventory.recipes.RecipeType;
import com.hbm.network.client.*;

import com.hbm.network.server.*;
import com.hbm.quests.QuestInstance;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.PacketDistributor;

import java.util.Map;
import java.util.Set;


public class PacketDispatcher {

    public static void sendAuxButtonPacket(BlockPos pos, int button, int data) {
        PacketHandler.sendToServer(new AuxButtonPacket(pos, button, data));
    }

    public static void sendAnvilCraft(int recipeIndex, boolean shift) {
        PacketHandler.sendToServer(new AnvilCraftPacket(recipeIndex, shift ? 1 : 0));
    }

    public static void sendParticleToTracking(String mode, int entityId, LivingEntity entity) {
        PacketHandler.CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                new ParticlePacket(mode, entityId)
        );
    }

    /**
     * Отправляет синхронизацию разблокированных крафтеров игроку
     */
    public static void sendCrafterSync(ServerPlayer player, Set<RecipeType> unlockedCrafters) {
        sendTo(new CrafterSyncPacket(unlockedCrafters), player);
    }

    /**
     * Отправляет обновление при добавлении нового крафтера
     */
    public static void sendCrafterUpdate(Player player, RecipeType type) {
        if (player instanceof ServerPlayer serverPlayer) {
            HbmPlayerProps.IHbmPlayerProps props = HbmPlayerProps.getData(serverPlayer);
            if (props != null) {
                sendCrafterSync(serverPlayer, props.getUnlockedCrafters());
            }
        }
    }

    public static void sendModelDataUpdate(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            PacketHandler.CHANNEL.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> serverLevel.getChunkAt(pos)),
                    new ModelDataUpdatePacket(pos)
            );
        }
    }

    public static void sendQuestSync(ServerPlayer player, Map<String, QuestInstance> activeQuests, Set<String> completedQuests) {
        PacketHandler.sendToPlayer(new QuestSyncPacket(activeQuests, completedQuests), player);
    }

    public static void sendToServer(PacketBase packet) {
        PacketHandler.sendToServer(packet);
    }

    public static void sendHazardsSync(net.minecraft.server.level.ServerPlayer player, int blackLung, int asbestos) {
        PacketHandler.sendToPlayer(new HazardsSyncPacket(blackLung, asbestos), player);
    }

    public static void sendGunAnimation(GunAnimationPacket packet, net.minecraft.world.entity.player.Player player) {
        if (player.level().isClientSide) {
            // Клиентская сторона - отправляем на сервер
            PacketHandler.sendToServer(packet);
        } else {
            // Серверная сторона - отправляем всем игрокам, которые видят этого игрока
            PacketHandler.CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    packet
            );
        }
    }

    public static void sendAuxParticleNT(CompoundTag nbt, double x, double y, double z, Entity entity) {
        PacketHandler.CHANNEL.send(
                PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                new AuxParticlePacketNT(nbt, x, y, z)
        );
    }

    // Альтернативный метод для отправки в определенную позицию
    public static void sendAuxParticleNT(CompoundTag nbt, double x, double y, double z, Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            PacketHandler.CHANNEL.send(
                    PacketDistributor.TRACKING_CHUNK.with(() -> serverLevel.getChunkAt(pos)),
                    new AuxParticlePacketNT(nbt, x, y, z)
            );
        }
    }

    public static void sendKeybind(HbmKeybinds.EnumKeybind key, boolean pressed) {
        PacketHandler.sendToServer(new KeybindPacket(key, pressed));
    }

    // НОВЫЙ МЕТОД: Синхронизация HbmLivingProps со всеми, кто видит сущность
    public static void syncLivingPropsToTracking(LivingEntity entity) {
        if (entity != null && !entity.level().isClientSide()) {
            PacketHandler.CHANNEL.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity),
                    new SyncLivingPropsPacket(entity)
            );
        }
    }

    public static void sendBiomeSyncToAllAround(ServerLevel level, int x, int z, short biome) {
        PacketHandler.CHANNEL.send(
                PacketDistributor.NEAR.with(PacketDistributor.TargetPoint.p(x, 128, z, 1024D, level.dimension())),
                new BiomeSyncPacket(x, z, biome)
        );
    }

    public static void sendBiomeSyncBlock(ServerLevel level, BlockPos pos, short biome) {
        PacketHandler.CHANNEL.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(pos)),
                new BiomeSyncPacket(pos.getX(), pos.getZ(), biome)
        );
    }

    public static void sendBiomeSync(ServerLevel level, ChunkPos chunkPos, short[] biomeArray) {
        PacketHandler.CHANNEL.send(
                PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunk(chunkPos.x, chunkPos.z)),
                new BiomeSyncPacket(chunkPos.x, chunkPos.z, biomeArray)
        );
    }

    public static void sendToAllAround(PacketBase packet, Level level, BlockPos pos, int range) {
        if (level instanceof ServerLevel) {
            PacketHandler.CHANNEL.send(
                    PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(
                            pos.getX(), pos.getY(), pos.getZ(), range, level.dimension()
                    )),
                    packet
            );
        }
    }

    public static void sendExtPropsToPlayer(HbmLivingProps livingProps, HbmPlayerProps.IHbmPlayerProps playerProps, ServerPlayer player) {
        PacketHandler.sendToPlayer(new ExtPropPacket(livingProps, playerProps), player);
    }

    public static void sendPermaSync(ServerPlayer player) {
        PacketHandler.sendToPlayer(new PermaSyncPacket(player), player);
    }

    public static void sendTo(PacketBase packet, ServerPlayer player) {
        PacketHandler.sendToPlayer(packet, player);
    }

}