package com.hbm.blocks.fluid;

import com.hbm.blocks.ModBlocks;
import com.hbm.util.ContaminationUtil;
import com.hbm.util.ContaminationUtil.ContaminationType;
import com.hbm.util.ContaminationUtil.HazardType;
import com.hbm.util.RefStrings;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidType;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

import static com.hbm.util.ResLocation.ResLocation;

public class ToxicBlock extends LiquidBlock {

    public ToxicBlock(FlowingFluid fluid, Properties properties) {
        super(fluid, properties);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Entity entity) {
        // Эффект паутины (замедление)
        entity.makeStuckInBlock(state, new Vec3(0.8D, 0.8D, 0.8D));

        if (entity instanceof LivingEntity living) {
            ContaminationUtil.contaminate(living, HazardType.RADIATION, ContaminationType.CREATIVE, 1.0F);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);

        if (reactToBlocks(level, pos.east()) ||
                reactToBlocks(level, pos.west()) ||
                reactToBlocks(level, pos.above()) ||
                reactToBlocks(level, pos.below()) ||
                reactToBlocks(level, pos.north()) ||
                reactToBlocks(level, pos.south())) {
            level.setBlock(pos, ModBlocks.SELLAFIELD_SLAKED.get().defaultBlockState(), 3);
        }
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);
        level.scheduleTick(pos, this, getTickDelay());
    }

    private boolean reactToBlocks(Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos);

        // Если это не наш токсичный блок
        if (fluidState.getType() != ModFluids.TOXIC_FLUID.get()) {
            // Если это любая другая жидкость
            return !fluidState.isEmpty();
        }
        return false;
    }

    public int getTickDelay() {
        return 15;
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .noCollission()
                .strength(500.0F)
                .noLootTable()
                .replaceable()
                .liquid()
                .randomTicks();
    }

    // Регистрация текстур через FluidType
    public static class ToxicFluidType extends FluidType {
        public ToxicFluidType(Properties properties) {
            super(properties);
        }

        @Override
        public void initializeClient(Consumer<IClientFluidTypeExtensions> consumer) {
            consumer.accept(new IClientFluidTypeExtensions() {
                private static final ResourceLocation STILL = ResLocation(RefStrings.MODID, "block/toxic_still");
                private static final ResourceLocation FLOWING = ResLocation(RefStrings.MODID, "block/toxic_flowing");

                @Override
                public ResourceLocation getStillTexture() {
                    return STILL;
                }

                @Override
                public ResourceLocation getFlowingTexture() {
                    return FLOWING;
                }

                @Override
                public int getTintColor() {
                    return 0xFF00AA00; // Зеленый оттенок
                }
            });
        }
    }
}