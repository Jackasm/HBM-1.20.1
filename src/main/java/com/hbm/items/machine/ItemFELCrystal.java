package com.hbm.items.machine;

import com.hbm.items.ModItems;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemFELCrystal extends Item {

    public EnumWavelengths wavelength;

    public ItemFELCrystal(Properties properties, EnumWavelengths wavelength) {
        super(properties);
        this.wavelength = wavelength;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        String desc = (stack.getItem() == ModItems.LASER_CRYSTAL_DIGAMMA.get()) ?
                ChatFormatting.OBFUSCATED + "THERADIANCEOFATHOUSANDSUNS" :
                this.getDescriptionId() + ".desc";
        tooltip.add(Component.translatable(desc));
        tooltip.add(Component.literal(wavelength.textColor + Component.translatable(wavelength.name).getString() +
                " - " + wavelength.textColor + Component.translatable(wavelength.wavelengthRange).getString()));
    }

    public enum EnumWavelengths {
        NULL("la creatura", "6 dollar", 0x010101, 0x010101, ChatFormatting.WHITE),

        IR("wavelengths.name.ir", "wavelengths.waveRange.ir", 0xBB1010, 0xCC4040, ChatFormatting.RED),
        VISIBLE("wavelengths.name.visible", "wavelengths.waveRange.visible", 0, 0, ChatFormatting.GREEN),
        UV("wavelengths.name.uv", "wavelengths.waveRange.uv", 0x0A1FC4, 0x00EFFF, ChatFormatting.AQUA),
        GAMMA("wavelengths.name.gamma", "wavelengths.waveRange.gamma", 0x150560, 0xEF00FF, ChatFormatting.LIGHT_PURPLE),
        DRX("wavelengths.name.drx", "wavelengths.waveRange.drx", 0xFF0000, 0xFF0000, ChatFormatting.DARK_RED);

        public final String name;
        public final String wavelengthRange;
        public final int renderedBeamColor;
        public final int guiColor;
        public final ChatFormatting textColor;

        EnumWavelengths(String name, String wavelength, int color, int guiColor, ChatFormatting textColor) {
            this.name = name;
            this.wavelengthRange = wavelength;
            this.renderedBeamColor = color;
            this.guiColor = guiColor;
            this.textColor = textColor;
        }
    }
}