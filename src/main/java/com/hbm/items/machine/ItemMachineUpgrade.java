package com.hbm.items.machine;

import com.hbm.items.ModItems;
import com.hbm.tileentity.IUpgradeInfoProvider;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemMachineUpgrade extends Item {

    public UpgradeType type;
    public int tier = 0;

    public ItemMachineUpgrade(Properties properties) {
        super(properties);
        this.type = UpgradeType.SPECIAL;
    }

    public ItemMachineUpgrade(Properties properties, UpgradeType type) {
        super(properties);
        this.type = type;
    }

    public ItemMachineUpgrade(Properties properties, UpgradeType type, int tier) {
        this(properties, type);
        this.tier = tier;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level world, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {

        Screen open = Minecraft.getInstance().screen;

        if (open != null && open instanceof AbstractContainerScreen<?> containerScreen) {
            AbstractContainerMenu container = containerScreen.getMenu();
            if (!container.slots.isEmpty()) {
                Slot first = container.getSlot(0);
                if (first.container instanceof IUpgradeInfoProvider provider) {
                    if (provider.canProvideInfo(this.type, this.tier, flag.isAdvanced())) {
                        provider.provideInfo(this.type, this.tier, tooltip, flag.isAdvanced());
                        return;
                    }
                }
            }
        }

        if (this == ModItems.UPGRADE_RADIUS.get()) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Forcefield Range Upgrade"));
            tooltip.add(Component.literal("Radius +16 / Consumption +500"));
            tooltip.add(Component.literal("Stacks to 16"));
        }

        if (this == ModItems.UPGRADE_HEALTH.get()) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Forcefield Health Upgrade"));
            tooltip.add(Component.literal("Max. Health +50 / Consumption +250"));
            tooltip.add(Component.literal("Stacks to 16"));
        }

        if (this == ModItems.UPGRADE_SMELTER.get()) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Mining Laser Upgrade"));
            tooltip.add(Component.literal("Smelts blocks. Easy enough."));
        }

        if (this == ModItems.UPGRADE_SHREDDER.get()) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Mining Laser Upgrade"));
            tooltip.add(Component.literal("Crunches ores"));
        }

        if (this == ModItems.UPGRADE_CENTRIFUGE.get()) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Mining Laser Upgrade"));
            tooltip.add(Component.literal("Hopefully self-explanatory"));
        }

        if (this == ModItems.UPGRADE_CRYSTALLIZER.get()) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Mining Laser Upgrade"));
            tooltip.add(Component.literal("Your new best friend"));
        }

        if (this == ModItems.UPGRADE_SCREM.get()) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Mining Laser Upgrade"));
            tooltip.add(Component.literal("It's like in Super Mario where all blocks are"));
            tooltip.add(Component.literal("actually Toads, but here it's Half-Life scientists"));
            tooltip.add(Component.literal("and they scream. A lot."));
        }

        if (this == ModItems.UPGRADE_NULLIFIER.get()) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Mining Laser Upgrade"));
            tooltip.add(Component.literal("50% chance to override worthless items with /dev/zero"));
            tooltip.add(Component.literal("50% chance to move worthless items to /dev/null"));
        }

        if (this == ModItems.UPGRADE_GC_SPEED.get()) {
            tooltip.add(Component.literal(ChatFormatting.RED + "Gas Centrifuge Upgrade"));
            tooltip.add(Component.literal("Allows for total isotopic separation of HEUF6"));
            tooltip.add(Component.literal(ChatFormatting.YELLOW + "also your centrifuge goes sicko mode"));
        }
    }

    public enum UpgradeType {
        SPEED,
        EFFECT,
        POWER,
        FORTUNE,
        AFTERBURN,
        OVERDRIVE,
        SPECIAL,
        LM_DESROYER,
        LM_SCREM,
        LM_SMELTER(true),
        LM_SHREDDER(true),
        LM_CENTRIFUGE(true),
        LM_CRYSTALLIZER(true),
        GS_SPEED;

        public boolean mutex = false;

        UpgradeType() {}

        UpgradeType(boolean mutex) {
            this.mutex = mutex;
        }
    }
}