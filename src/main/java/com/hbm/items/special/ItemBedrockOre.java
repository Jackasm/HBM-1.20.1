package com.hbm.items.special;

import com.hbm.items.ItemEnumMulti;
import com.hbm.items.special.ItemByproduct.EnumByproduct;
import com.hbm.util.EnumUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class ItemBedrockOre extends ItemEnumMulti<ItemBedrockOre.EnumBedrockOre> {

    public ItemBedrockOre(Properties properties) {
        super(properties, EnumBedrockOre.class, true);
    }


    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable Level level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Enum<?> type = getType(stack);
        if (type instanceof EnumBedrockOre ore) {
            if (ore.byproducts != null && ore.byproducts.length > 0) {
                tooltip.add(Component.translatable("item.ore.byproduct")
                        .append(Component.literal(": "))
                        .append(Component.translatable("item.hbm.ore_byproduct_" + ore.byproducts[0].name().toLowerCase(Locale.US)))
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) { // Оверлей
            EnumBedrockOre ore = getOreType(stack);
            return ore != null ? ore.color : 0xFFFFFF;
        }
        return 0xFFFFFF; // Базовый слой — белый
    }

    public EnumBedrockOre getOreType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomModelData")) {
            int customModelData = tag.getInt("CustomModelData");
            return EnumUtil.grabEnumSafely(theEnum, customModelData);
        }
        return EnumBedrockOre.IRON;
    }

    public enum EnumBedrockOre {

        IRON("Iron", 0xE2C0AA,                      EnumByproduct.SULFUR,   EnumByproduct.TITANIUM, EnumByproduct.TITANIUM),
        COPPER("Copper", 0xEC9A63,                  EnumByproduct.SULFUR,   EnumByproduct.SULFUR,   EnumByproduct.SULFUR),
        BORAX("Borax", 0xE4BE74,                    EnumByproduct.LITHIUM,  EnumByproduct.CALCIUM, EnumByproduct.CALCIUM),
        ASBESTOS("Asbestos", 0xBFBFB9,              EnumByproduct.SILICON,  EnumByproduct.SILICON, EnumByproduct.SILICON),
        NIOBIUM("Niobium", 0xAF58D8,                EnumByproduct.IRON,     EnumByproduct.IRON,    EnumByproduct.IRON),
        TITANIUM("Titanium", 0xF2EFE2,              EnumByproduct.SILICON,  EnumByproduct.CALCIUM, EnumByproduct.ALUMINIUM),
        TUNGSTEN("Tungsten", 0x2C293C,              EnumByproduct.LEAD,     EnumByproduct.IRON,    EnumByproduct.BISMUTH),
        GOLD("Gold", 0xF9D738,                      EnumByproduct.LEAD,     EnumByproduct.COPPER,  EnumByproduct.BISMUTH),
        URANIUM("Uranium", 0x868D82,                EnumByproduct.LEAD,     EnumByproduct.RADIUM,  EnumByproduct.POLONIUM),
        THORIUM("Thorium232", 0x7D401D,             EnumByproduct.SILICON,  EnumByproduct.URANIUM, EnumByproduct.TECHNETIUM),
        CHLOROCALCITE("Chlorocalcite", 0xCDE036,    EnumByproduct.LITHIUM,  EnumByproduct.SILICON, EnumByproduct.SILICON),
        FLUORITE("Fluorite", 0xF6F3E7,              EnumByproduct.SILICON,  EnumByproduct.LITHIUM, EnumByproduct.ALUMINIUM),
        HEMATITE("Hematite", 0xA37B72,              EnumByproduct.SULFUR,   EnumByproduct.TITANIUM, EnumByproduct.TITANIUM),
        MALACHITE("Malachite", 0x66B48C,            EnumByproduct.SULFUR,   EnumByproduct.SULFUR,  EnumByproduct.SULFUR),
        NEODYMIUM("Neodymium", 0x8F8F5F,            EnumByproduct.LITHIUM,  EnumByproduct.SILICON, EnumByproduct.BISMUTH);

        public final String oreName;
        public final int color;
        public final EnumByproduct[] byproducts;

        EnumBedrockOre(String name, int color, EnumByproduct... by) {
            this.oreName = name;
            this.color = color;
            this.byproducts = by;
        }
    }
}