package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.config.GeneralConfig;
import com.hbm.config.RadiationConfig;
import com.hbm.potion.HbmPotion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import org.jetbrains.annotations.NotNull;

public class WasteEarth extends Block {

    public WasteEarth(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        if (this == ModBlocks.WASTE_EARTH.get() || this == ModBlocks.WASTE_MYCELIUM.get() || this == ModBlocks.BURNING_EARTH.get()) {
            return new ItemStack(Blocks.DIRT);
        }
        if (this == ModBlocks.FROZEN_GRASS.get()) {
            return new ItemStack(Items.SNOWBALL);
        }
        return super.getCloneItemStack(level, pos, state);
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (entity instanceof LivingEntity living) {
            if (this == ModBlocks.FROZEN_GRASS.get()) {
                living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2 * 60 * 20, 2));
            }
            if (this == ModBlocks.WASTE_MYCELIUM.get()) {
                living.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 30 * 20, 3));
            }
            if (this == ModBlocks.BURNING_EARTH.get()) {
                living.setRemainingFireTicks(100); // 5 секунд (20 тиков/сек * 5)
            }
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.animateTick(state, level, pos, random);

        if (this == ModBlocks.WASTE_MYCELIUM.get()) {
            level.addParticle(ParticleTypes.MYCELIUM,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 1.1,
                    pos.getZ() + random.nextDouble(),
                    0.0, 0.0, 0.0);
        }
        if (this == ModBlocks.BURNING_EARTH.get()) {
            level.addParticle(ParticleTypes.FLAME,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 1.1,
                    pos.getZ() + random.nextDouble(),
                    0.0, 0.0, 0.0);
            level.addParticle(ParticleTypes.SMOKE,
                    pos.getX() + random.nextDouble(),
                    pos.getY() + 1.1,
                    pos.getZ() + random.nextDouble(),
                    0.0, 0.0, 0.0);
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.randomTick(state, level, pos, random);

        if (this == ModBlocks.WASTE_MYCELIUM.get() && GeneralConfig.enableMycelium.get()) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    for (int k = -1; k <= 1; k++) {
                        BlockPos targetPos = pos.offset(i, j, k);
                        BlockPos abovePos = targetPos.above();
                        BlockState targetState = level.getBlockState(targetPos);
                        BlockState aboveState = level.getBlockState(abovePos);

                        if (!aboveState.isSolid() &&
                                (targetState.is(Blocks.DIRT) ||
                                        targetState.is(Blocks.GRASS_BLOCK) ||
                                        targetState.is(Blocks.MYCELIUM) ||
                                        targetState.is(ModBlocks.WASTE_EARTH.get()))) {
                            level.setBlock(targetPos, ModBlocks.WASTE_MYCELIUM.get().defaultBlockState(), 3);
                        }
                    }
                }
            }
        }

        if (this == ModBlocks.BURNING_EARTH.get()) {
            if (random.nextInt(5) == 0) {
                for (int i = -1; i <= 1; i++) {
                    for (int j = -1; j <= 1; j++) {
                        for (int k = -1; k <= 1; k++) {
                            BlockPos targetPos = pos.offset(i, j, k);
                            BlockPos abovePos = targetPos.above();

                            if (!level.isLoaded(targetPos)) continue;

                            BlockState targetState = level.getBlockState(targetPos);
                            BlockState aboveState = level.getBlockState(abovePos);

                            if (!aboveState.isSolid() &&
                                    (targetState.is(Blocks.GRASS_BLOCK) ||
                                            targetState.is(Blocks.MYCELIUM) ||
                                            targetState.is(ModBlocks.WASTE_EARTH.get()) ||
                                            targetState.is(ModBlocks.FROZEN_GRASS.get()) ||
                                            targetState.is(ModBlocks.WASTE_MYCELIUM.get())) &&
                                    !level.canSeeSky(pos)) {
                                level.setBlock(targetPos, ModBlocks.BURNING_EARTH.get().defaultBlockState(), 3);
                            }

                            if (targetState.getBlock() instanceof LeavesBlock ||
                                    targetState.getBlock() instanceof BushBlock) {
                                level.removeBlock(targetPos, false);
                            }

                            if (targetState.is(ModBlocks.FROZEN_DIRT.get())) {
                                level.setBlock(targetPos, Blocks.DIRT.defaultBlockState(), 3);
                            }

                            if (aboveState.isFlammable(level, abovePos, Direction.UP) &&
                                    !(aboveState.getBlock() instanceof LeavesBlock ||
                                            aboveState.getBlock() instanceof BushBlock) &&
                                    level.getBlockState(pos.above()).isAir()) {
                                level.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), 3);
                            }
                        }
                    }
                }
            }
            level.setBlock(pos, ModBlocks.IMPACT_DIRT.get().defaultBlockState(), 3);
        }

        if (this == ModBlocks.WASTE_EARTH.get() || this == ModBlocks.WASTE_MYCELIUM.get()) {
            BlockPos abovePos = pos.above();
            if (RadiationConfig.cleanupDeadDirt.get() ||
                    (level.getMaxLocalRawBrightness(abovePos) < 4 &&
                            !level.getBlockState(abovePos).canOcclude())) {
                level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
            }

            if (level.getBlockState(abovePos).getBlock() instanceof MushroomBlock) {
                level.setBlock(abovePos, ModBlocks.MUSH.get().defaultBlockState(), 3);
            }
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (this == ModBlocks.BURNING_EARTH.get()) {
            BlockPos abovePos = pos.above();
            BlockState aboveState = level.getBlockState(abovePos);

            if (aboveState.getBlock() instanceof LiquidBlock ||
                    aboveState.getFluidState() != Fluids.EMPTY.defaultFluidState() ||
                    aboveState.isSolid()) {
                level.setBlock(pos, Blocks.DIRT.defaultBlockState(), 3);
            }
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    public boolean canSustainPlant(@NotNull BlockState state, @NotNull BlockGetter level,
                                   @NotNull BlockPos pos, @NotNull Direction direction,
                                   @NotNull IPlantable plantable) {
        if (this == ModBlocks.WASTE_EARTH.get() || this == ModBlocks.WASTE_MYCELIUM.get()) {
            PlantType plantType = plantable.getPlantType(level, pos.relative(direction));
            return plantType == PlantType.CAVE;
        }
        return false;
    }

    // Фабричный метод для создания свойств блока
    public static BlockBehaviour.Properties createProperties(MapColor color, float hardness, float resistance, boolean randomTick) {
        Properties props = BlockBehaviour.Properties.of()
                .mapColor(color)
                .strength(hardness, resistance)
                .sound(SoundType.GRAVEL)
                .pushReaction(PushReaction.DESTROY);

        if (randomTick) {
            props.randomTicks();
        }

        return props;
    }

    // Специфические свойства для разных типов
    public static BlockBehaviour.Properties wasteEarthProps() {
        return createProperties(MapColor.COLOR_BROWN, 0.5F, 10.0F, true);
    }

    public static BlockBehaviour.Properties wasteMyceliumProps() {
        return createProperties(MapColor.COLOR_PURPLE, 0.6F, 10.0F, true);
    }

    public static BlockBehaviour.Properties burningEarthProps() {
        return createProperties(MapColor.COLOR_RED, 0.5F, 10.0F, true);
    }

    public static BlockBehaviour.Properties frozenGrassProps() {
        return createProperties(MapColor.COLOR_LIGHT_BLUE, 0.5F, 10.0F, false);
    }

    public static BlockBehaviour.Properties frozenDirtProps() {
        return createProperties(MapColor.COLOR_LIGHT_BLUE, 0.5F, 10.0F, false);
    }

    public static BlockBehaviour.Properties impactDirtProps() {
        return createProperties(MapColor.COLOR_GRAY, 0.5F, 10.0F, false);
    }
}