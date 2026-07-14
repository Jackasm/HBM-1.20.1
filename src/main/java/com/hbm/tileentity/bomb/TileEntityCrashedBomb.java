package com.hbm.tileentity.bomb;

import com.hbm.blocks.bomb.BlockCrashedBomb;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

import java.util.List;
import java.util.function.BiConsumer;

public class TileEntityCrashedBomb extends BlockEntity {

    public TileEntityCrashedBomb(BlockPos pos, BlockState state) {
        super(ModTileEntity.CRASHED_BOMB.get(), pos, state);
    }

    public void tick() {
        if (level == null || level.isClientSide) return;

        if (level.getGameTime() % 2 == 0) {
            BlockCrashedBomb.EnumDudType type = getBlockState().getValue(BlockCrashedBomb.TYPE);

            if (type == BlockCrashedBomb.EnumDudType.BALEFIRE) {
                affectEntities((entity, intensity) -> ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, 1.0F * intensity), 15D);
            }
            if (type == BlockCrashedBomb.EnumDudType.NUKE) {
                affectEntities((entity, intensity) -> ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, 0.25F * intensity), 10D);
            }
            if (type == BlockCrashedBomb.EnumDudType.SALTED) {
                affectEntities((entity, intensity) -> ContaminationUtil.contaminate(entity, HazardType.RADIATION, ContaminationType.CREATIVE, 0.5F * intensity), 10D);
            }
        }
    }

    public void affectEntities(BiConsumer<LivingEntity, Float> effect, double range) {
        if (level == null) return;

        AABB aabb = new AABB(worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5).inflate(range);

        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, aabb);

        for (LivingEntity entity : entities) {
            double dx = entity.getX() - (worldPosition.getX() + 0.5);
            double dy = (entity.getY() + entity.getBbHeight() / 2) - (worldPosition.getY() + 0.5);
            double dz = entity.getZ() - (worldPosition.getZ() + 0.5);
            double dist = Math.sqrt(dx * dx + dy * dy + dz * dz);

            if (dist > range) continue;
            float intensity = (float) (1.0D - dist / range);
            effect.accept(entity, intensity);
        }
    }

    @Override
    public AABB getRenderBoundingBox() {
        return INFINITE_EXTENT_AABB;
    }

}