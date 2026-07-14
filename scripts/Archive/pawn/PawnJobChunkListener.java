package com.hbm.pawn;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class PawnJobChunkListener {

    @SubscribeEvent
    public static void onChunkLoad(ChunkEvent.Load event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        PawnJobManager manager = PawnJobManager.get(serverLevel);
        MineRegistry mineRegistry = MineRegistry.get(serverLevel);

        // 1. Регистрация TileEntity (ваши и ванильные печи)
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            // Ванильные печи
            if (be instanceof AbstractFurnaceBlockEntity && !(be instanceof IPawnServicable)) {
                manager.registerServicable(new VanillaFurnaceAdapter((AbstractFurnaceBlockEntity) be));
            }
            // Ваши модальные IPawnServicable
            else if (be instanceof IPawnServicable servicable) {
                manager.registerServicable(servicable);
            }
        }

        // 2. Восстанавливаем шахты, которые находятся в этом чанке
        for (MineSite site : mineRegistry.getAllMines()) {
            if (isInChunk(site.getPosition(), chunk)) {
                site.setServiceLevel(serverLevel);
                // Не вызываем init() заново! Только синхронизируем состояние
                manager.registerServicable(site);
                System.out.println("[PawnJobChunkListener] Restored mine at " + site.getPosition());
            }
        }
    }

    @SubscribeEvent
    public static void onChunkUnload(ChunkEvent.Unload event) {
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) return;
        if (!(event.getChunk() instanceof LevelChunk chunk)) return;

        PawnJobManager manager = PawnJobManager.get(serverLevel);
        MineRegistry mineRegistry = MineRegistry.get(serverLevel);

        // Удаляем TileEntity
        for (BlockEntity be : chunk.getBlockEntities().values()) {
            String id = serverLevel.dimension().location() + ":" + be.getBlockPos();
            manager.unregisterServicable(id);
        }

        // Удаляем шахты в этом чанке
        for (MineSite site : mineRegistry.getAllMines()) {
            if (isInChunk(site.getPosition(), chunk)) {
                manager.unregisterServicable(site.getServiceId());
                System.out.println("[PawnJobChunkListener] Unregistered mine at " + site.getPosition());
            }
        }
    }

    private static boolean isInChunk(BlockPos pos, LevelChunk chunk) {
        int minX = chunk.getPos().getMinBlockX();
        int maxX = chunk.getPos().getMaxBlockX();
        int minZ = chunk.getPos().getMinBlockZ();
        int maxZ = chunk.getPos().getMaxBlockZ();
        return pos.getX() >= minX && pos.getX() <= maxX &&
                pos.getZ() >= minZ && pos.getZ() <= maxZ;
    }
}