package com.hbm.client;

import com.hbm.tileentity.ModTileEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Клиентская часть ModelDataUpdatePacket. Живёт в client-пакете и
 * вызывается только через DistExecutor, чтобы сервер не грузил Minecraft.
 *
 * <p>Соединение CT-блоков зависит не только от прямой соседки, но и от диагоналей, поэтому по
 * одной позиции пересобирается область 3×3×3: обнуляем кэш данных модели у всех CT-блоков
 * области и метим грязными секции, которые её накрывают.
 */
@OnlyIn(Dist.CLIENT)
public final class ClientModelDataHandler {

    private ClientModelDataHandler() {
    }

    public static void handle(BlockPos pos) {
        Minecraft minecraft = Minecraft.getInstance();
        Level level = minecraft.level;
        if (level == null) return;

        invalidateModelData(level, pos);

        int minX = (pos.getX() - 1) >> 4;
        int maxX = (pos.getX() + 1) >> 4;
        int minY = (pos.getY() - 1) >> 4;
        int maxY = (pos.getY() + 1) >> 4;
        int minZ = (pos.getZ() - 1) >> 4;
        int maxZ = (pos.getZ() + 1) >> 4;

        for (int sectionX = minX; sectionX <= maxX; sectionX++) {
            for (int sectionY = minY; sectionY <= maxY; sectionY++) {
                for (int sectionZ = minZ; sectionZ <= maxZ; sectionZ++) {
                    minecraft.levelRenderer.setSectionDirty(sectionX, sectionY, sectionZ);
                }
            }
        }
    }

    private static void invalidateModelData(Level level, BlockPos center) {
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    BlockEntity blockEntity = level.getBlockEntity(center.offset(dx, dy, dz));
                    if (blockEntity != null && blockEntity.getType() == ModTileEntity.CT_BLOCK_ENTITY.get()) {
                        blockEntity.requestModelDataUpdate();
                    }
                }
            }
        }
    }
}
