package com.hbm.items.tool;

import com.hbm.items.special.ItemBedrockOreBase;
import com.hbm.items.special.ItemBedrockOreNew.BedrockOreType;
import com.hbm.network.PacketDispatcher;
import com.hbm.network.client.PlayerInformPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ItemOreDensityScanner extends Item {

    public ItemOreDensityScanner(Properties properties) {
        super(properties);
    }

    @Override
    public void inventoryTick(@NotNull ItemStack stack, @NotNull Level level, @NotNull Entity entity,
                              int slot, boolean selected) {
        if (!(entity instanceof Player player) || level.isClientSide) return;
        if (level.getGameTime() % 5 != 0) return;

        if (player instanceof net.minecraft.server.level.ServerPlayer serverPlayer) {
            BlockPos pos = BlockPos.containing(entity.getX(), entity.getY(), entity.getZ());

            for (BedrockOreType type : BedrockOreType.values()) {
                double levelDensity = ItemBedrockOreBase.getOreLevel(pos.getX(), pos.getZ(), type);
                String densityKey = translateDensity(levelDensity);
                int color = getColor(levelDensity);

                Component message = Component.literal("")
                        .append(Component.translatable("item.bedrock_ore.type." + type.suffix + ".name"))
                        .append(Component.literal(": " + ((int) (levelDensity * 100) / 100D) + " ("))
                        .append(Component.translatable(densityKey).withStyle(Style.EMPTY.withColor(color)))
                        .append(Component.literal(")"));

                PacketDispatcher.sendTo(new PlayerInformPacket(message, 777 + type.ordinal(), 4000), serverPlayer);
            }
        }
    }

    public static String translateDensity(double density) {
        if (density <= 0.1) return "item.ore_density_scanner.verypoor";
        if (density <= 0.35) return "item.ore_density_scanner.poor";
        if (density <= 0.75) return "item.ore_density_scanner.low";
        if (density >= 1.9) return "item.ore_density_scanner.excellent";
        if (density >= 1.65) return "item.ore_density_scanner.veryhigh";
        if (density >= 1.25) return "item.ore_density_scanner.high";
        return "item.ore_density_scanner.moderate";
    }

    public static int getColor(double density) {
        if (density <= 0.1) return 0x8B0000; // Dark Red
        if (density <= 0.35) return 0xFF0000; // Red
        if (density <= 0.75) return 0xFFA500; // Gold
        if (density >= 1.9) return 0x00FFFF; // Aqua
        if (density >= 1.65) return 0x0000FF; // Blue
        if (density >= 1.25) return 0x00FF00; // Green
        return 0xFFFF00; // Yellow
    }
}