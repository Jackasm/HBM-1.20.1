package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;

import com.hbm.potion.HbmPotion;
import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import com.hbm.util.ArmorUtil;
import com.hbm.util.ModDamageSource;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;

import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class BarbedWire extends BaseEntityBlock {

    public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
    private static final VoxelShape SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 1.0D, 16.0D);

    public BarbedWire(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySimpleOBJ(pos, state);
    }

    @Override
    public void entityInside(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, Entity entity) {
        // Замедление (как паутина)
        entity.makeStuckInBlock(state, new net.minecraft.world.phys.Vec3(0.15D, 0.1D, 0.15D));

        // Урон и эффекты в зависимости от типа блока
        if (this == ModBlocks.BARBED_WIRE.get()) {
            entity.hurt(level.damageSources().cactus(), 2.0F);
        }

        if (this == ModBlocks.BARBED_WIRE_FIRE.get()) {
            entity.hurt(level.damageSources().cactus(), 2.0F);
            entity.setSecondsOnFire(1);
        }

        if (this == ModBlocks.BARBED_WIRE_POISON.get()) {
            entity.hurt(level.damageSources().cactus(), 2.0F);
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.POISON, 5 * 20, 2));
            }
        }

        if (this == ModBlocks.BARBED_WIRE_ACID.get()) {
            entity.hurt(level.damageSources().cactus(), 2.0F);
            if (entity instanceof Player player) {
                ArmorUtil.damageSuit(player, 0, 1);
                ArmorUtil.damageSuit(player, 1, 1);
                ArmorUtil.damageSuit(player, 2, 1);
                ArmorUtil.damageSuit(player, 3, 1);
            }
        }

        if (this == ModBlocks.BARBED_WIRE_WITHER.get()) {
            entity.hurt(level.damageSources().cactus(), 2.0F);
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(MobEffects.WITHER, 5 * 20, 4));
            }
        }

        if (this == ModBlocks.BARBED_WIRE_ULTRA_DEATH.get()) {
            entity.hurt(ModDamageSource.pc(level), 5.0F);
            if (entity instanceof LivingEntity living) {
                living.addEffect(new MobEffectInstance(HbmPotion.RADIATION.get(), 5 * 20, 9));
            }
        }
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }
}