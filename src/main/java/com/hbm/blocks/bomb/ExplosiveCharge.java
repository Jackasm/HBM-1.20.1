package com.hbm.blocks.bomb;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.BombConfig;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.logic.EntityNukeExplosionMK5;

import com.hbm.explosion.vanillant.ExplosionVNT;
import com.hbm.interfaces.IBomb;
import com.hbm.particle.helper.ExplosionCreator;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import org.jetbrains.annotations.NotNull;

public class ExplosiveCharge extends BlockDetonatable implements IBomb, IDetConnectible {

    public ExplosiveCharge(Properties properties) {
        super(properties);
    }

    public static Properties createChargeProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(0.1F, 0.0F)
                .requiresCorrectToolForDrops();
    }

    public static Properties createNukeProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(0.1F, 0.0F)
                .requiresCorrectToolForDrops();
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (level.hasNeighborSignal(pos)) {
            this.explode(level, pos);
        }
    }

    @Override
    public IBomb.BombReturnCode explode(Level level, BlockPos pos) {
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            level.removeBlock(pos, false);

            if (this == ModBlocks.DET_CORD.get()) {
                level.explode(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 1.5F, Level.ExplosionInteraction.TNT);
            }

            if (this == ModBlocks.DET_CHARGE.get()) {
                ExplosionVNT explosion = new ExplosionVNT(level,  pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 15, null);

                explosion.explode();
                ExplosionCreator.composeEffectStandard(level, pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5);
            }

            if (this == ModBlocks.DET_NUKE.get()) {
                EntityNukeExplosionMK5.statFac(serverLevel, BombConfig.missileRadius.get(), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                EntityNukeTorex.statFacStandard(serverLevel, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, BombConfig.missileRadius.get());
            }
        }
        return BombReturnCode.DETONATED;
    }

    @Override
    public void explodeEntity(Level level, BlockPos pos, LivingEntity entity) {
        explode(level, pos);
    }
}