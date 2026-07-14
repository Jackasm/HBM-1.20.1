package com.hbm.items.tool;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDiscord extends Item {

    public ItemDiscord(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, @NotNull Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        Vec3 start = player.getEyePosition(1.0F);
        Vec3 look = player.getViewVector(1.0F);
        Vec3 end = start.add(look.scale(100.0D));

        BlockHitResult hitResult = level.clip(new ClipContext(start, end, ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player));

        if (hitResult != null && hitResult.getType() == HitResult.Type.BLOCK) {
            Direction dir = hitResult.getDirection();

            if (!level.isClientSide) {
                if (player.isVehicle()) {
                    player.stopRiding();
                }

                Vec3 hitPos = hitResult.getLocation();
                BlockPos pos = BlockPos.containing(hitPos.x, hitPos.y, hitPos.z);

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                player.teleportTo(hitPos.x + dir.getStepX(),
                        hitPos.y + dir.getStepY() - 1,
                        hitPos.z + dir.getStepZ());

                level.playSound(null, player.getX(), player.getY(), player.getZ(),
                        SoundEvents.ENDERMAN_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);

                player.fallDistance = 0.0F;
            }

            // Частицы портала
            for (int i = 0; i < 32; ++i) {
                level.addParticle(ParticleTypes.PORTAL,
                        player.getX(),
                        player.getY() + player.getRandom().nextDouble() * 2.0D,
                        player.getZ(),
                        player.getRandom().nextGaussian(),
                        0.0D,
                        player.getRandom().nextGaussian());
            }

            return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
        }

        return InteractionResultHolder.pass(stack);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("I've seen the Rod of Discord and honestly")
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
        tooltip.add(Component.literal("it's not as amazing as people say.")
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Rod of Discord is crucial in so many boss fights.")
                .withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
        tooltip.add(Component.literal("Imagine getting coiled by worm bosses.")
                .withStyle(ChatFormatting.RED, ChatFormatting.ITALIC));
        tooltip.add(Component.empty());
        tooltip.add(Component.literal("Oh, you mean the Terraria item.")
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
        tooltip.add(Component.literal("Idk about that thing.")
                .withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
    }
}