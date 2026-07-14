package com.hbm.blocks.gas;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class BlockGasRadon extends BlockGasBase {

    public BlockGasRadon(Properties properties) {
        super(properties, 0.1F, 0.8F, 0.1F);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living))
            return;

        if (ArmorRegistry.hasAllProtection(living, 3, HazardClass.PARTICLE_FINE)) {
            ArmorUtil.damageGasMaskFilter(living, 1);
        } else {
            ContaminationUtil.contaminate(living, HazardType.RADIATION, ContaminationType.RAD_BYPASS, 0.05F);
            HbmLivingProps.incrementAsbestos(living, 1);
        }
    }

    @Override
    public Direction getFirstDirection(Level level, BlockPos pos) {
        if (level.random.nextInt(5) == 0)
            return Direction.UP;
        return Direction.DOWN;
    }

    @Override
    public Direction getSecondDirection(Level level, BlockPos pos) {
        return randomHorizontal(level.random);
    }

    @Override
    public void randomTick(@NotNull BlockState state, ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.isClientSide && random.nextInt(50) == 0) {
            level.removeBlock(pos, false);
            return;
        }
        super.randomTick(state, level, pos, random);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);
    }
}