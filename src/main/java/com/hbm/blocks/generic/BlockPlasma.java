package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

public class BlockPlasma extends Block {

    public BlockPlasma(Properties properties) {
        super(properties);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level,
                           @NotNull BlockPos pos, @NotNull RandomSource random) {
        // Используем правильный ключ для game rule
        if (level.getGameRules().getBoolean(GameRules.RULE_DOFIRETICK)) {
            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level,
                     @NotNull BlockPos pos, @NotNull RandomSource random) {
        // Альтернативный вариант, если нужен тик без случайности
        this.randomTick(state, level, pos, random);
    }

    @Override
    public VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty(); // Нет коллизии
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level,
                             @NotNull BlockPos pos, @NotNull Entity entity) {
        entity.setRemainingFireTicks(100);

        if (entity instanceof LivingEntity living) {
            living.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 40, 1));
        }
    }


    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0F;
    }

    @Override
    public @NotNull PushReaction getPistonPushReaction(@NotNull BlockState state) {
        return PushReaction.DESTROY;
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(5.0F, 6000.0F)
                .lightLevel(state -> 15)
                .randomTicks()
                .noOcclusion()
                .noLootTable()
                .pushReaction(PushReaction.DESTROY);
    }
}