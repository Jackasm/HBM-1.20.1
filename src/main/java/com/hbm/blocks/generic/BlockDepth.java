package com.hbm.blocks.generic;

import com.hbm.api.item.IDepthRockTool;
import com.hbm.blocks.ITooltipProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockDepth extends Block implements ITooltipProvider {

    public BlockDepth(Properties properties) {
        super(properties);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.STONE)
                .strength(-1.0F, 10.0F) // неразрушимый, но с сопротивлением
                .pushReaction(PushReaction.BLOCK);
    }

    @Override
    public float getDestroyProgress(@NotNull BlockState state, @NotNull Player player, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        ItemStack heldItem = player.getMainHandItem();
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof IDepthRockTool tool) {
            if (tool.canBreakRock(level, player, heldItem, this, pos)) {
                return 1.0F / 50.0F;
            }
        }
        return super.getDestroyProgress(state, player, level, pos);
    }

    @Override
    public void addInformation(ItemStack stack, Player player, List<Component> tooltip, boolean advanced) {
        tooltip.add(Component.translatable("trait.tile.depth").withStyle(ChatFormatting.YELLOW));
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Player player) {
        return ItemStack.EMPTY;
    }
}