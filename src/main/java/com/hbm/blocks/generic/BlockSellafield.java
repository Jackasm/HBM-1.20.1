package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;

import com.hbm.handler.radiation.RadiationEvents;
import com.hbm.items.block.ItemBlockSellafield;
import com.hbm.potion.HbmPotion;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockSellafield extends BaseEntityBlock {

    public static final int SELLAFITE_LEVELS = 6;
    public static final int TEXTURE_VARIANTS = 4;
    public static final IntegerProperty LEVEL = IntegerProperty.create("level", 0, SELLAFITE_LEVELS - 1);

    public float rad = 0.5F;

    public BlockSellafield(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LEVEL, 0));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LEVEL);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        int level = 0;
        ItemStack stack = context.getItemInHand();
        if (stack.hasTag()) {
            level = Objects.requireNonNull(stack.getTag()).getInt("SellafieldLevel");
        }
        return this.defaultBlockState().setValue(LEVEL, Math.min(level, SELLAFITE_LEVELS - 1));
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySellafield(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.SELLAFIELD.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntitySellafield s) s.tick();
            };
        }
        return null;
    }

    @Override
    public void stepOn(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Entity entity) {
        if (entity instanceof LivingEntity living) {
            int meta = state.getValue(LEVEL);
            living.addEffect(new MobEffectInstance(
                    HbmPotion.RADIATION.get(),
                    30 * 20,
                    meta < 5 ? meta : meta * 2
            ));
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos,
                           @NotNull RandomSource random) {
        int meta = state.getValue(LEVEL);

        RadiationEvents.incrementRadiation(level, pos, this.rad * (meta + 1));

        int chance = (meta == 0) ? 25 : 15;
        if (random.nextInt(chance) == 0) {
            if (meta > 0) {
                level.setBlock(pos, state.setValue(LEVEL, meta - 1), 2);
            } else {
                level.setBlock(pos, ModBlocks.SELLAFIELD_SLAKED.get().defaultBlockState(), 3);
            }
        }
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull Player player) {
        if (!level.isClientSide && !player.isCreative()) {
            int level_ = state.getValue(LEVEL);
            ItemStack drop = ItemBlockSellafield.withLevel(this, level_);
            ItemEntity item = new ItemEntity(level,
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
            item.setDeltaMovement(0, 0, 0);
            level.addFreshEntity(item);
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.COLOR_GREEN)
                .strength(5.0F)
                .sound(SoundType.STONE)
                .randomTicks()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }

    // ============================================================
    // TileEntity
    // ============================================================

    public static class TileEntitySellafield extends BlockEntity {
        public TileEntitySellafield(BlockPos pos, BlockState state) {
            super(ModTileEntity.SELLAFIELD.get(), pos, state);
        }

        public void tick() {
            // логика в randomTick блока
        }
    }
}