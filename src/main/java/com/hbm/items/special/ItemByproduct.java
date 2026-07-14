package com.hbm.items.special;

import com.hbm.items.ItemEnumMulti;
import com.hbm.util.EnumUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class ItemByproduct extends ItemEnumMulti<ItemByproduct.EnumByproduct> {

    public ItemByproduct(Properties properties) {
        super(properties, EnumByproduct.class, true);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public int getBarColor(@NotNull ItemStack stack) {
        EnumByproduct product = EnumUtil.grabEnumSafely(theEnum, stack.getDamageValue());
        return product.color;
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(ItemStack stack, int tintIndex) {
        if (tintIndex == 1) { // layer1 — оверлей
            EnumByproduct product = getByproductType(stack);
            return product != null ? product.color : 0xFFFFFF;
        }
        return 0xFFFFFF;
    }

    public EnumByproduct getByproductType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains("CustomModelData")) {
            int customModelData = tag.getInt("CustomModelData");
            return EnumUtil.grabEnumSafely(theEnum, customModelData);
        }
        return EnumByproduct.IRON;
    }

    @Override
    public boolean isBarVisible(@NotNull ItemStack stack) {
        return true;
    }

    @Override
    public int getBarWidth(@NotNull ItemStack stack) {
        return 13; // Полная полоса
    }

    public enum EnumByproduct {
        IRON(0xE2C0AA),
        COPPER(0xEC9A63),
        LITHIUM(0xEDEDED),
        SILICON(0xFFFBD1),
        LEAD(0x646470),
        TITANIUM(0xF2EFE2),
        ALUMINIUM(0xE8F2F9),
        SULFUR(0xEAD377),
        CALCIUM(0xCFCFA6),
        BISMUTH(0x8D8577),
        RADIUM(0xE9FAF6),
        TECHNETIUM(0xCADFDF),
        POLONIUM(0xCADFDF),
        URANIUM(0x868D82);

        public final int color;

        EnumByproduct(int color) {
            this.color = color;
        }
    }
}