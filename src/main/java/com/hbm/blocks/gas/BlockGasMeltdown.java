package com.hbm.blocks.gas;

import com.hbm.blocks.ModBlocks;
import com.hbm.extprop.HbmLivingProps;
import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.potion.HbmPotion;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlockGasMeltdown extends BlockGasBase {

    public BlockGasMeltdown(Properties properties) {
        super(properties, 0.1F, 0.4F, 0.1F);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        if (!(entity instanceof LivingEntity living)) return;

        // Заражение радиацией
        ContaminationUtil.contaminate(living, HazardType.RADIATION, ContaminationType.CREATIVE, 0.5F);

        // Эффект радиации
        living.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 60 * 20, 2));

        // Проверка защиты
        if (ArmorRegistry.hasAllProtection(living, 3, HazardClass.PARTICLE_FINE)) {
            ArmorUtil.damageGasMaskFilter(living, 1);
        } else {
            HbmLivingProps.incrementAsbestos(living, 5);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);
        level.addParticle(net.minecraft.core.particles.ParticleTypes.TOTEM_OF_UNDYING,
                pos.getX() + random.nextFloat(),
                pos.getY() + random.nextFloat(),
                pos.getZ() + random.nextFloat(),
                0.0D, 0.0D, 0.0D);
    }

    @Override
    public Direction getFirstDirection(Level level, BlockPos pos) {
        if (level.random.nextInt(2) == 0) {
            return Direction.UP;
        }
        return Direction.DOWN;
    }

    @Override
    public Direction getSecondDirection(Level level, BlockPos pos) {
        return randomHorizontal(level.random);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!level.isClientSide) {
            Direction dir = Direction.values()[random.nextInt(6)];
            BlockPos targetPos = pos.relative(dir);

            // Случайное распространение
            if (random.nextInt(7) == 0 && level.getBlockState(targetPos).getBlock() == Blocks.AIR) {
                level.setBlock(targetPos, ModBlocks.GAS_RADON_DENSE.get().defaultBlockState(), 3);
            }

            // Радиация в небо
            if (level.canSeeSky(pos)) {
                RadiationEvents.incrementRadiation(level, pos, 5);
            }

            // Исчезновение
            if (random.nextInt(350) == 0) {
                level.removeBlock(pos, false);
                return;
            }
        }

        super.tick(state, level, pos, random);
    }

    public static Properties createProperties() {
        return Properties.of()
                .noCollission()
                .air()
                .strength(0.0F)
                .noOcclusion()
                .noLootTable()
                .lightLevel(state -> 0)
                .randomTicks();
    }
}