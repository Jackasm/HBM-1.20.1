package com.hbm.items.tool;

import com.hbm.blocks.ModBlocks;
import com.hbm.network.PacketHandler;
import com.hbm.network.client.PlayerInformPacket;
import com.hbm.sound.ModSounds;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class ItemOilDetector extends Item {

    public ItemOilDetector(Properties properties) {
        super(properties);
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.translatable(this.getDescriptionId() + ".desc1"));
        tooltip.add(Component.translatable(this.getDescriptionId() + ".desc2"));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level level, Player player, @NotNull InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        boolean direct = false;
        boolean nearby = false;
        int x = (int) player.getX();
        int y = (int) player.getY();
        int z = (int) player.getZ();

        // Проверка прямо под игроком
        for (int i = y + 15; i > 5; i--) {
            BlockState state = level.getBlockState(new BlockPos(x, i, z));
            if (state.getBlock() == ModBlocks.ORE_OIL.get()) {
                direct = true;
                break;
            }
        }

        // Проверка всех блоков в радиусе 10 по горизонтали
        if (!direct) {
            outer:
            for (int dx = -10; dx <= 10; dx++) {
                for (int dz = -10; dz <= 10; dz++) {
                    for (int i = y + 15; i > 5; i--) {
                        BlockState state = level.getBlockState(new BlockPos(x + dx, i, z + dz));
                        if (state.getBlock() == ModBlocks.ORE_OIL.get()) {
                            nearby = true;
                            break outer;
                        }
                    }
                }
            }
        }

        if (!level.isClientSide) {
            if (direct) {
                Component message = Component.translatable(this.getDescriptionId() + ".bullseye")
                        .withStyle(ChatFormatting.DARK_GREEN);
                PacketHandler.sendToPlayer(new PlayerInformPacket(message, 0), (ServerPlayer) player);
            } else if (nearby) {
                Component message = Component.translatable(this.getDescriptionId() + ".detected")
                        .withStyle(ChatFormatting.GOLD);
                PacketHandler.sendToPlayer(new PlayerInformPacket(message, 0), (ServerPlayer) player);
            } else {
                Component message = Component.translatable(this.getDescriptionId() + ".noOil")
                        .withStyle(ChatFormatting.RED);
                PacketHandler.sendToPlayer(new PlayerInformPacket(message, 0), (ServerPlayer) player);
            }
        }

        level.playSound(player, player.getX(), player.getY(), player.getZ(),
                ModSounds.TECH_BLEEP.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

        player.swing(hand);

        return InteractionResultHolder.success(stack);
    }

    private void checkPosition(Level level, int x, int z, int y, boolean oil) {
        for (int i = y + 15; i > 5; i--) {
            BlockState state = level.getBlockState(new net.minecraft.core.BlockPos(x, i, z));
            if (state.getBlock() == ModBlocks.ORE_OIL.get()) {
                oil = true;
                break;
            }
        }
    }
}