package com.hbm.items.tool;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ItemBalefireMatch extends Item {

    public ItemBalefireMatch(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(@NotNull UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        BlockPos pos = context.getClickedPos();
        Direction side = context.getClickedFace();
        ItemStack stack = context.getItemInHand();

        if (player == null) return InteractionResult.PASS;

        BlockPos placePos = pos.relative(side);

        if (!player.mayUseItemAt(placePos, side, stack)) {
            return InteractionResult.PASS;
        }

        if (level.isEmptyBlock(placePos)) {
            level.playSound(null, placePos.getX() + 0.5D, placePos.getY() + 0.5D, placePos.getZ() + 0.5D,
                    net.minecraft.sounds.SoundEvents.FLINTANDSTEEL_USE, SoundSource.PLAYERS, 1.0F,
                    level.random.nextFloat() * 0.4F + 0.8F);

            BlockState state = ModBlocks.BALEFIRE.get().defaultBlockState();
            level.setBlock(placePos, state, 3);
        }

        if (!player.getAbilities().instabuild) {
            stack.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(context.getHand()));
        }

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}