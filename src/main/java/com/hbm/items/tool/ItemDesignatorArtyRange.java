package com.hbm.items.tool;

import com.hbm.blocks.BlockDummyable;

import com.hbm.sound.ModSounds;
import com.hbm.tileentity.turret.TileEntityTurretBaseArtillery;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ItemDesignatorArtyRange extends Item {

    public ItemDesignatorArtyRange(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level,
                                @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        if (stack.getTag() == null) {
            tooltip.add(Component.literal(ChatFormatting.RED + "No turret linked!"));
        } else {
            tooltip.add(Component.literal(ChatFormatting.YELLOW + "Linked to " +
                    stack.getTag().getInt("x") + ", " +
                    stack.getTag().getInt("y") + ", " +
                    stack.getTag().getInt("z")));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        Block block = level.getBlockState(pos).getBlock();

        if (block instanceof BlockDummyable dummyable) {
            BlockPos corePos = dummyable.findCore(level, pos);

            if (corePos == null) {
                return InteractionResult.PASS;
            }

            BlockEntity te = level.getBlockEntity(corePos);

            if (te instanceof TileEntityTurretBaseArtillery) {
                if (level.isClientSide) {
                    return InteractionResult.SUCCESS;
                }

                stack.getOrCreateTag().putInt("x", corePos.getX());
                stack.getOrCreateTag().putInt("y", corePos.getY());
                stack.getOrCreateTag().putInt("z", corePos.getZ());

                level.playSound(null, Objects.requireNonNull(player).getX(), player.getY(), player.getZ(),
                        ModSounds.TECH_BLEEP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);

                return InteractionResult.SUCCESS;
            }
        }

        return InteractionResult.PASS;
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!stack.hasTag()) {
            return InteractionResultHolder.pass(stack);
        }

        HitResult hitResult = player.pick(500, 1.0F, false);
        if (!(hitResult instanceof BlockHitResult blockHit)) {
            return InteractionResultHolder.pass(stack);
        }

        BlockPos pos = blockHit.getBlockPos();

        if (!level.isClientSide) {
            BlockEntity te = level.getBlockEntity(new BlockPos(
                    Objects.requireNonNull(stack.getTag()).getInt("x"),
                    stack.getTag().getInt("y"),
                    stack.getTag().getInt("z")
            ));

            if (te instanceof TileEntityTurretBaseArtillery arty) {
                Objects.requireNonNull(arty).enqueueTarget(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        ModSounds.TECH_BOOP.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 1.0F);
            }
        }

        return InteractionResultHolder.success(stack);
    }
}