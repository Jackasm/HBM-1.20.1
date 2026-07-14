package com.hbm.items.machine;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemDrillbit extends Item {

    private final EnumDrillType drillType;

    public ItemDrillbit(Properties properties, EnumDrillType type) {
        super(properties);
        this.drillType = type;
    }

    public EnumDrillType getDrillType() {
        return drillType;
    }

    public double getSpeed() {
        return drillType.speed;
    }

    public int getTier() {
        return drillType.tier;
    }

    public int getFortune() {
        return drillType.fortune;
    }

    public boolean hasVeinMiner() {
        return drillType.vein;
    }

    public boolean hasSilkTouch() {
        return drillType.silk;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, List<Component> tooltip, @NotNull TooltipFlag flag) {
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Speed: " + ((int) (drillType.speed * 100)) + "%"));
        tooltip.add(Component.literal(ChatFormatting.YELLOW + "Tier: " + drillType.tier));
        if (drillType.fortune > 0) {
            tooltip.add(Component.literal(ChatFormatting.LIGHT_PURPLE + "Fortune " + drillType.fortune));
        }
        if (drillType.vein) {
            tooltip.add(Component.literal(ChatFormatting.GREEN + "Vein miner"));
        }
        if (drillType.silk) {
            tooltip.add(Component.literal(ChatFormatting.GREEN + "Silk touch"));
        }
    }

    public enum EnumDrillType {
        STEEL(1.0D, 1, 0, false, false),
        STEEL_DIAMOND(1.0D, 1, 2, false, true),
        HSS(1.2D, 2, 0, true, false),
        HSS_DIAMOND(1.2D, 2, 3, true, true),
        DESH(1.5D, 3, 1, true, true),
        DESH_DIAMOND(1.5D, 3, 4, true, true),
        TCALLOY(2.0D, 4, 1, true, true),
        TCALLOY_DIAMOND(2.0D, 4, 4, true, true),
        FERRO(2.5D, 5, 1, true, true),
        FERRO_DIAMOND(2.5D, 5, 4, true, true);

        public final double speed;
        public final int tier;
        public final int fortune;
        public final boolean vein;
        public final boolean silk;

        EnumDrillType(double speed, int tier, int fortune, boolean vein, boolean silk) {
            this.speed = speed;
            this.tier = tier;
            this.fortune = fortune;
            this.vein = vein;
            this.silk = silk;
        }
    }
}