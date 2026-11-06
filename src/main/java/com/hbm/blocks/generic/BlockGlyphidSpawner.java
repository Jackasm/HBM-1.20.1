package com.hbm.blocks.generic;

import com.hbm.blocks.IBlockMulti;
import com.hbm.items.block.ItemBlockGlyphidSpawner;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.block.TileEntityGlyphidSpawner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGlyphidSpawner extends BaseEntityBlock implements IBlockMulti {

    public BlockGlyphidSpawner(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        // нет свойств — тип живёт в TE
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int getSubCount() {
        return 3;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityGlyphidSpawner(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.GLYPHID_SPAWNER.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityGlyphidSpawner spawner) spawner.tick();
            };
        }
        return null;
    }

    // ==== дроп яйца ====

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityGlyphidSpawner spawner) {
            return ItemBlockGlyphidSpawner.withType(spawner.type);
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityGlyphidSpawner spawner) {
            spawner.type = ItemBlockGlyphidSpawner.getType(stack);
            spawner.setChanged();
        }
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, Player player) {
        if (!player.isCreative() && !level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityGlyphidSpawner spawner) {
                // В оригинале: 1 + rand.nextInt(3) + fortune
                int count = 1 + level.random.nextInt(3);
                ItemStack drop = ItemBlockGlyphidSpawner.withType(spawner.type);
                drop.setCount(count);
                ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, drop);
                item.setDeltaMovement(0, 0, 0);
                level.addFreshEntity(item);
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.COLOR_PURPLE)
                .strength(0.5F)
                .sound(SoundType.SLIME_BLOCK)
                .noLootTable()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }
}