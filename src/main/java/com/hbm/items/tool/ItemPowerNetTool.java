package com.hbm.items.tool;

import com.hbm.api.energy.EnergyNet;
import com.hbm.api.energy.IEnergyConductor;
import com.hbm.blocks.BlockDummyable;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.AuxParticlePacketNT;
import com.hbm.uninos.EnergyNode;
import com.hbm.uninos.EnergyNodespace;
import com.hbm.util.ChatBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemPowerNetTool extends Item {

    private static final int radius = 20;

    public ItemPowerNetTool(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Player player = context.getPlayer();
        if (player == null) return InteractionResult.PASS;

        BlockState state = level.getBlockState(pos);
        Block block = state.getBlock();

        if (block instanceof BlockDummyable dummy) {
            BlockPos corePos = dummy.findCore(level, pos);
            if (corePos != null) {
                pos = corePos;
            }
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockEntity te = level.getBlockEntity(pos);

        if (te instanceof IEnergyConductor) {
            EnergyNode node = EnergyNodespace.getNode(level, pos);

            if (node != null && node.hasValidNet()) {
                EnergyNet net = node.getNet(); // должен быть метод getNet() в GenNode
                if (net == null) {
                    player.displayClientMessage(
                            ChatBuilder.start("Error: Network is null!").color(ChatFormatting.RED).flush(),
                            false
                    );
                    return InteractionResult.SUCCESS;
                }
                String id = Integer.toHexString(net.hashCode());

                player.displayClientMessage(
                        ChatBuilder.start("Start of diagnostic for network " + id).color(ChatFormatting.GOLD).flush(),
                        false
                );
                player.displayClientMessage(
                        ChatBuilder.start("Links: " + net.getLinks().size()).color(ChatFormatting.YELLOW).flush(),
                        false
                );
                player.displayClientMessage(
                        ChatBuilder.start("Providers: " + net.getProviders().size()).color(ChatFormatting.YELLOW).flush(),
                        false
                );
                player.displayClientMessage(
                        ChatBuilder.start("Receivers: " + net.getReceivers().size()).color(ChatFormatting.YELLOW).flush(),
                        false
                );
                player.displayClientMessage(
                        ChatBuilder.start("End of diagnostic for network " + id).color(ChatFormatting.GOLD).flush(),
                        false
                );

                // Узлы сети (links) - это EnergyNode
                for (EnergyNode link : net.getLinks()) {
                    for (BlockPos linkPos : link.getPositions()) {
                        CompoundTag data = new CompoundTag();
                        data.putString("type", "debug");
                        data.putInt("color", 0xffff00);
                        data.putFloat("scale", 0.5F);
                        data.putString("text", id);

                        AuxParticlePacketNT packet = new AuxParticlePacketNT(
                                data,
                                linkPos.getX() + 0.5,
                                linkPos.getY() + 1.5,
                                linkPos.getZ() + 0.5
                        );
                        PacketDispatcher.sendToAllAround(packet, level, linkPos, radius);
                    }
                }

            } else {
                player.displayClientMessage(
                        ChatBuilder.start("Error: No network found!").color(ChatFormatting.RED).flush(),
                        false
                );
            }

            return InteractionResult.SUCCESS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal("Right-click cable to analyze the power net.").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Links (cables, poles, etc.) are YELLOW").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Subscribers (any receiver) are BLUE").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Links with mismatching network info (BUGGED!) are RED").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Displays stats such as link and subscriber count").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Proxies are connection points for multiblock links (e.g. 4 for substations)").withStyle(ChatFormatting.RED));
        tooltip.add(Component.literal("Particles only spawn in a " + radius + " block radius!").withStyle(ChatFormatting.RED));
    }
}