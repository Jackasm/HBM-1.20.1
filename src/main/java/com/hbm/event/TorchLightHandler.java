package com.hbm.event;

import com.hbm.blocks.gas.BlockGasBase;
import com.hbm.items.armor.ArmorNo9;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Mod.EventBusSubscriber(modid = RefStrings.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class TorchLightHandler {

    // Храним UUID игрока -> позиция света
    private static final ConcurrentHashMap<UUID, BlockPos> playerLightPositions = new ConcurrentHashMap<>();
    private static final int UPDATE_INTERVAL = 5;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        Level level = player.level();
        if (level.isClientSide()) return;

        UUID uuid = player.getUUID();
        BlockPos currentLightPos = playerLightPositions.get(uuid);
        boolean shouldHaveLight = shouldHaveTorchLight(player);

        // Проверяем, не надет ли шлем No9 (он сам управляет светом)
        ItemStack helmet = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof ArmorNo9) {
            // Если шлем No9 надет — удаляем свет от факела и выходим
            if (currentLightPos != null) {
                removeLightBlock(level, currentLightPos);
                playerLightPositions.remove(uuid);
            }
            return;
        }

        if (!shouldHaveLight) {
            if (currentLightPos != null) {
                removeLightBlock(level, currentLightPos);
                playerLightPositions.remove(uuid);
            }
            return;
        }

        if (level.getGameTime() % UPDATE_INTERVAL != 0) return;

        BlockPos desiredLightPos = calculateLightPosition(player);
        if (desiredLightPos == null) {
            desiredLightPos = findAlternativePosition(player);
            if (desiredLightPos == null) return;
        }

        if (currentLightPos == null) {
            if (placeLightBlock(level, desiredLightPos)) {
                playerLightPositions.put(uuid, desiredLightPos);
            }
        } else if (!currentLightPos.equals(desiredLightPos)) {
            // Если позиция изменилась, переносим свет
            removeLightBlock(level, currentLightPos);
            if (placeLightBlock(level, desiredLightPos)) {
                playerLightPositions.put(uuid, desiredLightPos);
            } else {
                // Если не получилось поставить свет на новое место — оставляем старый
                playerLightPositions.put(uuid, currentLightPos);
            }
        }
    }

    private static BlockPos calculateLightPosition(Player player) {
        double x = player.getX();
        double y = player.getY() + 1.4;
        double z = player.getZ();

        float yaw = player.getYRot();
        double offsetX = -Math.sin(Math.toRadians(yaw)) * 0.5;
        double offsetZ = Math.cos(Math.toRadians(yaw)) * 0.5;

        BlockPos pos = BlockPos.containing(x + offsetX, y, z + offsetZ);

        return canPlaceLightBlock(player.level(), pos) ? pos : null;
    }

    private static BlockPos findAlternativePosition(Player player) {
        Level level = player.level();
        BlockPos base = player.blockPosition();

        BlockPos above = base.above(2);
        if (canPlaceLightBlock(level, above)) return above;

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                if (dx == 0 && dz == 0) continue;
                BlockPos nearby = base.offset(dx, 1, dz);
                if (canPlaceLightBlock(level, nearby)) return nearby;
            }
        }

        if (canPlaceLightBlock(level, base)) return base;
        return null;
    }

    private static boolean placeLightBlock(Level level, BlockPos pos) {
        if (canPlaceLightBlock(level, pos)) {
            BlockState existing = level.getBlockState(pos);
            // Если это газ — включаем подсветку
            if (existing.getBlock() instanceof BlockGasBase gasBlock) {
                gasBlock.setGlowing(level, pos, true);
                return true;
            }
            // Иначе ставим LightBlock
            level.setBlock(pos, Blocks.LIGHT.defaultBlockState().setValue(LightBlock.LEVEL, 15), 3);
            return true;
        }
        return false;
    }

    private static boolean canPlaceLightBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return level.isEmptyBlock(pos) ||
                state.canBeReplaced() ||
                state.getBlock() instanceof BlockGasBase ||
                state.getBlock() instanceof LightBlock;
    }

    private static void removeLightBlock(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BlockGasBase gasBlock) {
            gasBlock.setGlowing(level, pos, false);
        } else if (state.getBlock() == Blocks.LIGHT) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        } else if (state.getBlock() instanceof LightBlock) {
            level.removeBlock(pos, false);
        }
    }

    private static boolean shouldHaveTorchLight(Player player) {
        return isHoldingTorch(player) && !player.isUnderWater() && !player.isInLava();
    }

    private static boolean isHoldingTorch(Player player) {
        ItemStack main = player.getMainHandItem();
        ItemStack off = player.getOffhandItem();
        return main.is(Items.TORCH) || off.is(Items.TORCH);
    }

    // === Очистка ===

    @SubscribeEvent
    public static void onPlayerLoggedOut(PlayerEvent.PlayerLoggedOutEvent event) {
        cleanupPlayerLight(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerChangedDimension(net.minecraftforge.event.entity.player.PlayerEvent.PlayerChangedDimensionEvent event) {
        cleanupPlayerLight(event.getEntity());
    }

    @SubscribeEvent
    public static void onPlayerRespawn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerRespawnEvent event) {
        cleanupPlayerLight(event.getEntity());
    }

    private static void cleanupPlayerLight(Player player) {
        UUID uuid = player.getUUID();
        BlockPos pos = playerLightPositions.remove(uuid);
        if (pos != null) {
            removeLightBlock(player.level(), pos);
        }
    }
}