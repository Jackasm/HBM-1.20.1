package com.hbm.blocks.bomb;

import com.hbm.tileentity.block.TileEntityVolcanoCore;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BlockVolcano extends Block implements EntityBlock {

    public static final int META_STATIC_ACTIVE = 0;
    public static final int META_STATIC_EXTINGUISHING = 1;
    public static final int META_GROWING_ACTIVE = 2;
    public static final int META_GROWING_EXTINGUISHING = 3;
    public static final int META_SMOLDERING = 4;

    public BlockVolcano(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        int meta = stack.getDamageValue();
        TileEntityVolcanoCore.addTooltip(meta, tooltip);
    }

    public static boolean isGrowing(int meta) {
        return meta == META_GROWING_ACTIVE || meta == META_GROWING_EXTINGUISHING;
    }

    public static boolean isExtinguishing(int meta) {
        return meta == META_STATIC_EXTINGUISHING || meta == META_GROWING_EXTINGUISHING;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityVolcanoCore(pos, state);
    }

    @Override
    public @Nullable <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        return level.isClientSide ? null : (lvl, pos, blockState, te) -> {
            if (te instanceof TileEntityVolcanoCore core) {
                core.tick();
            }
        };
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL;
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(-1.0F, 10000.0F)
                .noLootTable()
                .pushReaction(PushReaction.BLOCK);
    }
}