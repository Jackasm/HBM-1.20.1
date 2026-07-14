package com.hbm.blocks.generic;

import com.hbm.extprop.HbmLivingProps;
import com.hbm.extprop.HbmLivingProps.ContaminationEffect;
import com.hbm.items.ModItems;
import com.hbm.potion.HbmPotion;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.SupportType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockFallout extends Block {

    public static final IntegerProperty LAYERS = IntegerProperty.create("layers", 1, 8);

    // Тонкая форма как у снега (высота 2 пикселя = 1/8 блока)
    protected static final VoxelShape[] SHAPE_BY_LAYER = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),   // 1 слой (2 пикселя)
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),   // 2 слоя (4 пикселя)
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),   // 3 слоя (6 пикселей)
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),   // 4 слоя (8 пикселей)
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 10.0D, 16.0D),  // 5 слоёв (10 пикселей)
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 12.0D, 16.0D),  // 6 слоёв (12 пикселей)
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 14.0D, 16.0D),  // 7 слоёв (14 пикселей)
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 16.0D, 16.0D)   // 8 слоёв (полный блок)
    };

    public BlockFallout(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LAYERS, 1));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LAYERS);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS) - 1];
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE_BY_LAYER[state.getValue(LAYERS) - 1];
    }

    @Override
    public boolean isCollisionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public boolean useShapeForLightOcclusion(@NotNull BlockState state) {
        return true;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(ModItems.FALLOUT.get());
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        BlockPos belowPos = pos.below();
        BlockState belowState = level.getBlockState(belowPos);
        Block block = belowState.getBlock();

        if (block == Blocks.ICE || block == Blocks.PACKED_ICE || block == Blocks.BLUE_ICE) return false;
        if (belowState.is(BlockTags.LEAVES) && !belowState.isAir()) return true;
        if (block == this && belowState.getBlock() == this) return true;

        return belowState.isSolid() &&
                belowState.isFaceSturdy(level, belowPos, Direction.UP, SupportType.FULL);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!this.canSurvive(state, level, pos)) {
            level.removeBlock(pos, false);
            return;
        }

        if (!fromPos.equals(pos.above())) return;

        BlockPos abovePos = pos.above();
        BlockState aboveState = level.getBlockState(abovePos);

        if (aboveState.getBlock() != this) return;

        int currentLayers = state.getValue(LAYERS);
        int aboveLayers = aboveState.getValue(LAYERS);

        if (currentLayers >= 8) return;

        int canTake = Math.min(aboveLayers, 8 - currentLayers);

        level.setBlock(pos, state.setValue(LAYERS, currentLayers + canTake), 3);

        int remaining = aboveLayers - canTake;
        if (remaining <= 0) {
            level.removeBlock(abovePos, false);
        } else {
            level.setBlock(abovePos, aboveState.setValue(LAYERS, remaining), 3);
        }
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (!level.isClientSide && entity instanceof LivingEntity living) {
            if (entity instanceof Player player && player.isCreative()) return;

            MobEffectInstance effect = new MobEffectInstance(HbmPotion.RADIATION.get(), 10 * 60 * 20, 0);
            effect.setCurativeItems(List.of()); // Пустой список, чтобы нельзя было снять
            living.addEffect(effect);
        }
    }

    @Override
    public void attack(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player) {
        if (!level.isClientSide) {
            HbmLivingProps.addCont(player, new ContaminationEffect(1.0F, 200, false));
        }
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext context) {
        return false;
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return true;
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.COLOR_GRAY)
                .strength(0.1F)
                .sound(SoundType.GRAVEL)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY)
                .isViewBlocking((state, level, pos) -> false);
    }
}