package com.hbm.items.tool;

import com.hbm.handler.pollution.IPollutionData;
import com.hbm.handler.pollution.PollutionData;
import com.hbm.handler.pollution.PollutionHandler;

import com.hbm.handler.pollution.PollutionType;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.PlayerInformPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemPollutionDetector extends Item {

    public ItemPollutionDetector(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                              int slot, boolean selected) {
        if (!(entity instanceof Player player) || level.isClientSide) return;
        if (level.getGameTime() % 10 != 0) return;

        BlockPos pos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());
        IPollutionData data = PollutionHandler.getPollutionData(level, pos);
        if (data == null) data = new PollutionData();

        float soot = data.getPollution(PollutionType.SOOT);
        float poison = data.getPollution(PollutionType.POISON);
        float heavymetal = data.getPollution(PollutionType.HEAVYMETAL);

        soot = ((int) (soot * 100)) / 100F;
        poison = ((int) (poison * 100)) / 100F;
        heavymetal = ((int) (heavymetal * 100)) / 100F;

        if (player instanceof ServerPlayer serverPlayer) {
            PacketDispatcher.sendTo(new PlayerInformPacket(
                    Component.literal("Soot: " + soot).withStyle(style -> style.withColor(0xFFFF00)),
                    100, 4000), serverPlayer);

            PacketDispatcher.sendTo(new PlayerInformPacket(
                    Component.literal("Poison: " + poison).withStyle(style -> style.withColor(0xFFFF00)),
                    101, 4000), serverPlayer);

            PacketDispatcher.sendTo(new PlayerInformPacket(
                    Component.literal("Heavy metal: " + heavymetal).withStyle(style -> style.withColor(0xFFFF00)),
                    102, 4000), serverPlayer);
        }
    }
}