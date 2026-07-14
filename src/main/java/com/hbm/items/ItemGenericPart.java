package com.hbm.items;

import com.hbm.util.EnumUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class ItemGenericPart extends ItemEnumMulti<ItemGenericPart.EnumPartType> {

    public ItemGenericPart(Properties properties) {
        super(properties, EnumPartType.class, true);
    }

    public enum EnumPartType {
        PISTON_PNEUMATIC("piston_pneumatic"),
        PISTON_HYDRAULIC("piston_hydraulic"),
        PISTON_ELECTRIC("piston_electric"),
        LDE("low_density_element"),
        HDE("heavy_duty_element"),
        GLASS_POLARIZED("glass_polarized");

        public final String texName;

        EnumPartType(String texName) {
            this.texName = texName;
        }
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (multiName) {
            CompoundTag tag = stack.getTag();
            int customModelData = 0;
            if (tag != null && tag.contains("CustomModelData")) {
                customModelData = tag.getInt("CustomModelData");
            }
            EnumPartType num = EnumUtil.grabEnumSafely(theEnum, customModelData);
            return Component.translatable(this.getDescriptionId() + "_" + num.name().toLowerCase(Locale.US));
        }
        return super.getName(stack);
    }

    public ItemStack stackFromEnum(int count, EnumPartType num) {
        ItemStack stack = new ItemStack(this, count);
        stack.getOrCreateTag().putInt("CustomModelData", num.ordinal());
        return stack;
    }

    public ItemStack stackFromEnum(EnumPartType num) {
        return stackFromEnum(1, num);
    }
}