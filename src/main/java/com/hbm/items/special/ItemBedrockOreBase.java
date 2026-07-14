package com.hbm.items.special;

import com.hbm.items.special.ItemBedrockOreNew.BedrockOreType;
import com.hbm.items.tool.ItemOreDensityScanner;
import net.minecraft.network.chat.Component;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class ItemBedrockOreBase extends Item {

    private static final NormalNoise[] ores = new NormalNoise[BedrockOreType.values().length];
    private static NormalNoise levelNoise;

    public ItemBedrockOreBase(Properties properties) {
        super(properties);
    }

    public static double getOreAmount(ItemStack stack, BedrockOreType type) {
        if (!stack.hasTag()) return 0;
        return Objects.requireNonNull(stack.getTag()).getDouble(type.suffix);
    }

    public static void setOreAmount(ItemStack stack, int x, int z) {
        if (!stack.hasTag()) {
            stack.setTag(new net.minecraft.nbt.CompoundTag());
        }
        var data = stack.getTag();

        for (BedrockOreType type : BedrockOreType.values()) {
            Objects.requireNonNull(data).putDouble(type.suffix, getOreLevel(x, z, type));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        for (BedrockOreType type : BedrockOreType.values()) {
            double amount = getOreAmount(stack, type);
            String typeName = Component.translatable("item.bedrock_ore.type." + type.suffix + ".name").getString();
            String densityKey = ItemOreDensityScanner.translateDensity(amount);
            int densityColor = ItemOreDensityScanner.getColor(amount);
            String densityText = Component.translatable(densityKey).getString();

            tooltip.add(Component.literal(typeName + ": " + ((int) (amount * 100)) / 100D + " (")
                    .append(Component.literal(densityText).withStyle(style -> style.withColor(densityColor)))
                    .append(Component.literal(")")));
        }
    }

    public static double getOreLevel(int x, int z, BedrockOreType type) {
        if (levelNoise == null) {
            levelNoise = NormalNoise.create(RandomSource.create(2114043), 4);
        }
        if (ores[type.ordinal()] == null) {
            ores[type.ordinal()] = NormalNoise.create(RandomSource.create(2082127 + type.ordinal()), 4);
        }

        double scale = 0.01D;

        double noise1 = levelNoise.getValue(x * scale, 0, z * scale);
        double noise2 = ores[type.ordinal()].getValue(x * scale, 0, z * scale);

        return Math.min(Math.max(Math.abs(noise1 * noise2) * 0.05, 0), 2);
    }
}