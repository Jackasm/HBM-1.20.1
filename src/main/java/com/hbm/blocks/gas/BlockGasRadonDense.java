package com.hbm.blocks.gas;

import com.hbm.blocks.ModBlocks;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.potion.HbmPotion;
import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ArmorRegistry.HazardClass;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlockGasRadonDense extends BlockGasBase {

    public BlockGasRadonDense(Properties properties) {
        super(properties, 0.1F, 0.5F, 0.1F);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living))
            return;

        if (ArmorRegistry.hasAllProtection(living, 3, HazardClass.PARTICLE_FINE)) {
            ArmorUtil.damageGasMaskFilter(living, 1);
        } else {
            ContaminationUtil.contaminate(living, HazardType.RADIATION, ContaminationType.CREATIVE, 0.5F);
            living.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 15 * 20, 0));
            HbmLivingProps.incrementAsbestos(living, 5);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);
        level.addParticle(ParticleTypes.MYCELIUM,
                pos.getX() + random.nextFloat(),
                pos.getY() + random.nextFloat(),
                pos.getZ() + random.nextFloat(),
                0.0D, 0.0D, 0.0D);
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
        if (!level.isClientSide) {
            if (random.nextInt(20) == 0) {
                BlockPos below = pos.below();
                if (level.getBlockState(below).getBlock() == Blocks.GRASS_BLOCK) {
                    level.setBlock(below, ModBlocks.WASTE_EARTH.get().defaultBlockState(), 3);
                }
            }

            if (random.nextInt(30) == 0) {
                level.removeBlock(pos, false);

                if (ModBlocks.FALLOUT.get().defaultBlockState().canSurvive(level, pos)) {
                    level.setBlock(pos, ModBlocks.FALLOUT.get().defaultBlockState(), 3);
                }

                return;
            }
        }

        super.randomTick(state, level, pos, random);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);
    }
}