package com.hbm.items.tool;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.IAnalyzable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ItemAnalysisTool extends Item {

    public ItemAnalysisTool(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof BlockDummyable dummy) {
            BlockPos corePos = dummy.findCore(level, pos);
            if (corePos != null) {
                pos = corePos;
            }
        }

        Block finalBlock = level.getBlockState(pos).getBlock();
        if (finalBlock instanceof IAnalyzable analyzable) {
            List<String> debug = analyzable.getDebugInfo(level, pos);

            if (debug != null && !level.isClientSide) {
                for (String line : debug) {
                    player.displayClientMessage(
                            Component.literal(line).withStyle(ChatFormatting.YELLOW),
                            false
                    );
                }
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }
}