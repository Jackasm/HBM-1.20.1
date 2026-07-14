package com.hbm.items.tool;

import com.hbm.api.item.IDesignatorItem;
import com.hbm.blocks.bomb.LaunchPad;

import com.hbm.sound.ModSounds;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemDesignator extends Item implements IDesignatorItem {

    public ItemDesignator(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (stack.getTag() != null) {
            tooltip.add(Component.literal("Target Coordinates:"));
            tooltip.add(Component.literal("X: " + stack.getTag().getInt("xCoord")));
            tooltip.add(Component.literal("Z: " + stack.getTag().getInt("zCoord")));
        } else {
            tooltip.add(Component.literal("Please select a target."));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();
        Block block = level.getBlockState(pos).getBlock();

        if (!(block instanceof LaunchPad)) {
            stack.getOrCreateTag().putInt("xCoord", pos.getX());
            stack.getOrCreateTag().putInt("zCoord", pos.getZ());

            if (level.isClientSide) {
                assert player != null;
                player.displayClientMessage(Component.literal("Position set!"), true);
            }

            assert player != null;
            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TECH_BLEEP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    public boolean isReady(Level level, ItemStack stack, int x, int y, int z) {
        return stack.hasTag();
    }

    public Vec3 getCoords(Level level, ItemStack stack, int x, int y, int z) {
        assert stack.getTag() != null;
        return new Vec3(stack.getTag().getInt("xCoord"), 0, stack.getTag().getInt("zCoord"));
    }
}