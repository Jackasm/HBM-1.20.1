package com.hbm.items.tool;

import com.hbm.api.item.IDesignatorItem;
import com.hbm.blocks.bomb.LaunchPad;

import com.hbm.sound.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemDesignatorRange extends Item implements IDesignatorItem {

    public ItemDesignatorRange(Properties properties) {
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
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        HitResult hitResult = player.pick(300, 1.0F, false);
        if (!(hitResult instanceof BlockHitResult blockHit)) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos pos = blockHit.getBlockPos();
        Block block = level.getBlockState(pos).getBlock();

        if (!(block instanceof LaunchPad)) {
            stack.getOrCreateTag().putInt("xCoord", pos.getX());
            stack.getOrCreateTag().putInt("zCoord", pos.getZ());

            if (level.isClientSide) {
                player.displayClientMessage(Component.literal("Position set to X:" + pos.getX() + ", Z:" + pos.getZ()), true);
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.TECH_BLEEP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

            return InteractionResultHolder.success(stack);
        }

        return InteractionResultHolder.pass(stack);
    }

    public boolean isReady(Level level, ItemStack stack, int x, int y, int z) {
        return stack.hasTag();
    }

    public Vec3 getCoords(Level level, ItemStack stack, int x, int y, int z) {
        return new Vec3(Objects.requireNonNull(stack.getTag()).getInt("xCoord"), 0, stack.getTag().getInt("zCoord"));
    }
}