package com.hbm.pawn;

import com.hbm.blocks.BlockDummyable;
import com.hbm.tileentity.TileEntityProxyBase;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class RegistryEventHandler {

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof Level level)) return;
        if (!(event.getEntity() instanceof Player player)) return;

        BlockPos pos = event.getPos();
        BlockEntity be = level.getBlockEntity(pos);

        // Для мультиблоков: находим ядро
        BlockPos corePos = findCorePos(level, pos, be);
        BlockEntity coreBe = corePos != null ? level.getBlockEntity(corePos) : be;
        if (coreBe == null) return;

        // Позиция для регистрации: для мультиблоков используем corePos, для обычных - pos
        BlockPos registerPos = corePos != null ? corePos : pos;

        // 1. Регистрация в InventoryRegistry
        if (coreBe instanceof Container) {
            InventoryRegistry registry = InventoryRegistry.get(level);
            if (BlockClassifier.isStorage(coreBe)) {
                registry.addStorage(registerPos, BlockClassifier.getStorageType(coreBe));
            } else if (BlockClassifier.isConsumer(coreBe)) {
                registry.addConsumer(registerPos, BlockClassifier.getConsumerType(coreBe));
            }
        }

        // 2. Регистрация в PawnJobManager
        IPawnServicable servicable = null;
        if (coreBe instanceof IPawnServicable) {
            servicable = (IPawnServicable) coreBe;
        } else if (coreBe instanceof AbstractFurnaceBlockEntity) {
            servicable = new VanillaFurnaceAdapter((AbstractFurnaceBlockEntity) coreBe);
        }

        if (servicable != null) {
            // Устанавливаем владельца, если блок ещё не имеет
            if (servicable.getOwnerUUID() == null) {
                servicable.setOwnerUUID(player.getUUID());
            }
            PawnJobManager.get(level).registerServicable(servicable);
        }
    }

    @SubscribeEvent
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        if (event.getLevel().isClientSide()) return;
        if (!(event.getLevel() instanceof Level level)) return;

        BlockPos pos = event.getPos();
        BlockEntity be = level.getBlockEntity(pos);

        // Находим ядро мультиблока
        BlockPos corePos = findCorePos(level, pos, be);
        if (corePos == null) corePos = pos;

        // Удаляем из InventoryRegistry
        InventoryRegistry registry = InventoryRegistry.get(level);
        registry.remove(corePos);

        // Удаляем из PawnJobManager
        String serviceId = level.dimension().location() + ":" + corePos;
        PawnJobManager.get(level).unregisterServicable(serviceId);
    }

    /**
     * Находит позицию ядра мультиблока, если блок является его частью.
     * @return позиция ядра или null, если блок не часть мультиблока
     */
    private static BlockPos findCorePos(Level level, BlockPos pos, BlockEntity be) {
        if (be instanceof TileEntityProxyBase) {
            // Прокси-блок – ищем ядро через BlockDummyable
            if (be.getBlockState().getBlock() instanceof BlockDummyable dummy) {
                return dummy.findCore(level, pos);
            }
        } else if (be == null && level.getBlockState(pos).getBlock() instanceof BlockDummyable dummy) {
            // Блок-заглушка без TileEntity – тоже ищем ядро
            return dummy.findCore(level, pos);
        }
        return null;
    }
}