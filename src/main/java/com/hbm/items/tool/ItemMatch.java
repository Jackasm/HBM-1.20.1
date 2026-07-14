package com.hbm.items.tool;

import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ItemMatch extends Item {

    public ItemMatch(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos firePos = pos.relative(context.getClickedFace());

        if (!Objects.requireNonNull(player).mayUseItemAt(firePos, context.getClickedFace(), stack)) {
            return InteractionResult.FAIL;
        }

        BlockState state = level.getBlockState(firePos);

        if (state.isAir()) {
            level.playSound(null, firePos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.random.nextFloat() * 0.4F + 0.8F);
            level.setBlock(firePos, Blocks.FIRE.defaultBlockState(), 11);

            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.FAIL;
    }
}