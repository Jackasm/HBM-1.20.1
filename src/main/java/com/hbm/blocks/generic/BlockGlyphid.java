package com.hbm.blocks.generic;

import com.hbm.blocks.IBlockMulti;
import com.hbm.items.block.ItemGlyphid;
import com.hbm.tileentity.ModTileEntity;
import net.minecraft.core.BlockPos;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockGlyphid extends BaseEntityBlock implements IBlockMulti {

    public BlockGlyphid(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.@NotNull Builder<Block, BlockState> builder) {
        // нет свойств blockstate — тип живёт в BlockEntity
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityGlyphid(pos, state);
    }

    @Override
    public int getSubCount() {
        return GlyphidType.values().length;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityGlyphid glyphid) {
            return ItemGlyphid.withType(glyphid.type);
        }
        return super.getCloneItemStack(state, target, level, pos, player);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityGlyphid glyphid) {
            glyphid.type = ItemGlyphid.getType(stack);
            glyphid.setChanged();
        }
    }

    @Override
    public void playerWillDestroy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, Player player) {
        if (!player.isCreative() && !level.isClientSide) {
            BlockEntity te = level.getBlockEntity(pos);
            if (te instanceof TileEntityGlyphid glyphid) {
                ItemStack stack = ItemGlyphid.withType(glyphid.type);
                ItemEntity item = new ItemEntity(level, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, stack);
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
                .noOcclusion()
                .noLootTable()
                .pushReaction(PushReaction.DESTROY);
    }

    public enum GlyphidType {
        STANDARD,
        INFESTED,
        RAD
    }

    public static class TileEntityGlyphid extends BlockEntity {

        public GlyphidType type = GlyphidType.STANDARD;

        public TileEntityGlyphid(BlockPos pos, BlockState state) {
            super(ModTileEntity.GLYPHID.get(), pos, state);
        }
    }
}