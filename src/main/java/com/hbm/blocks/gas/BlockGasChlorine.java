package com.hbm.blocks.gas;

import com.hbm.util.ArmorRegistry;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ArmorRegistry.HazardClass;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlockGasChlorine extends BlockGasBase {

    public BlockGasChlorine(Properties properties) {
        super(properties, 0.7F, 0.8F, 0.6F);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;

        if (ArmorRegistry.hasAllProtection(living, 3, HazardClass.GAS_LUNG)) {
            ArmorUtil.damageGasMaskFilter(living, 1);
        } else {
            living.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 5 * 20, 0));
            living.addEffect(new MobEffectInstance(MobEffects.POISON, 20 * 20, 2));
            living.addEffect(new MobEffectInstance(MobEffects.WITHER, 1 * 20, 1));
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 30 * 20, 1));
            living.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 30 * 20, 2));
        }
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.isClientSide && random.nextInt(10) == 0) {
            level.removeBlock(pos, false);
            return;
        }

        super.tick(state, level, pos, random);
    }

    @Override
    public int getLightBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 0;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean skipRendering(@NotNull BlockState state, @NotNull BlockState adjacentState, @NotNull Direction side) {
        return adjacentState.getBlock() instanceof BlockGasBase || super.skipRendering(state, adjacentState, side);
    }

    @Override
    public Direction getFirstDirection(Level level, BlockPos pos) {
        if (level.random.nextInt(5) == 0) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }

    @Override
    public Direction getSecondDirection(Level level, BlockPos pos) {
        return randomHorizontal(level.random);
    }

    public static Properties createProperties() {
        return Properties.of()
                .noCollission()
                .air()
                .strength(0.0F)
                .noOcclusion()
                .noLootTable()
                .lightLevel(state -> 0)
                .randomTicks()
                .isValidSpawn((state, level, pos, entityType) -> false);
    }
}