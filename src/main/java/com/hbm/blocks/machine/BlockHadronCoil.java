package com.hbm.blocks.machine;

import com.hbm.blocks.ITooltipProvider;
import com.hbm.blocks.generic.BlockCTBase;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.SoundType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Locale;

/**
 * Блок катушки адронного коллайдера. В эталоне 1.7.10 — {@code BlockHadronCoil implements IBlockCT}:
 * девять сортов, у каждого своя CT-текстура и правило «соединяется сам с собой».
 */
public class BlockHadronCoil extends BlockCTBase implements ITooltipProvider {

    public final int factor;

    public BlockHadronCoil(Properties properties, int factor) {
        super(properties);
        this.factor = factor;
    }

    @Override
    public void addTooltip(ItemStack stack, @NotNull List<Component> tooltip, boolean showDetails) {
        tooltip.add(Component.translatable("info.coil")
                .append(": ")
                .append(Component.literal(String.format(Locale.US, "%,d", factor)))
                .withStyle(ChatFormatting.GOLD));
    }

    public static Properties createProperties() {
        return Properties.of()
                .strength(5.0F, 10.0F)
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }
}
