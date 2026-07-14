package com.hbm.items;

import com.hbm.util.EnumUtil;
import com.hbm.util.HBMEnums;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ItemEnumMulti<T extends Enum<T>> extends Item {

    public Class<T> theEnum;
    protected boolean multiName;

    private final int burnTime;

    public ItemEnumMulti(Properties properties, Class<T> theEnum, boolean multiName, int burnTime) {
        super(properties);
        this.theEnum = theEnum;
        this.multiName = multiName;
        this.burnTime = burnTime;
    }

    public ItemEnumMulti(Properties properties, Class<T> theEnum, boolean multiName) {
        this(properties, theEnum, multiName, 0);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        if (multiName) {
            CompoundTag tag = stack.getTag();
            int customModelData = 0;
            if (tag != null && tag.contains("CustomModelData")) {
                customModelData = tag.getInt("CustomModelData");
            }
            T num = EnumUtil.grabEnumSafely(theEnum, customModelData);
            return Component.translatable(this.getDescriptionId() + "_" + num.name().toLowerCase(Locale.US));
        }
        return super.getName(stack);
    }



    public ItemStack stackFromEnum(int count, T num) {
        ItemStack stack = new ItemStack(this, count);
        stack.getOrCreateTag().putInt("CustomModelData", num.ordinal());
        return stack;
    }

    public ItemStack stackFromEnum(T num) {
        return stackFromEnum(1, num);
    }


    @Override
    public boolean isDamageable(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isEnchantable(@NotNull ItemStack stack) {
        return false;
    }

    @Override
    public int getBurnTime(@NotNull ItemStack stack, @Nullable RecipeType<?> recipeType) {
        // Специальная обработка для брикетов
        if (this == ModItems.BRIQUETTE.get()) {
            int single = 200;
            Enum<?> type = getType(stack);
            if (type instanceof HBMEnums.EnumBriquetteType briquetteType) {
                return switch (briquetteType) {
                    case COAL -> single * 10;      // 2000
                    case LIGNITE -> single * 8;    // 1600
                    case WOOD -> single * 2;       // 400
                    default -> 0;
                };
            }
            return 0;
        }

        // Специальная обработка для золы
        if (this == ModItems.POWDER_ASH.get()) {
            int single = 200;
            Enum<?> type = getType(stack);
            if (type instanceof HBMEnums.EnumAshType ashType) {
                return switch (ashType) {
                    case WOOD -> single / 2;      // 100
                    case COAL -> single;          // 200
                    case MISC -> single / 2;      // 100
                    case FLY -> single;           // 200
                    case SOOT -> single / 2;      // 100
                    default -> 0;
                };
            }
            return 0;
        }

        // Для всех остальных предметов
        if (burnTime > 0) {
            return burnTime;
        }
        return super.getBurnTime(stack, recipeType);
    }

    public Enum<?> getType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int customModelData = 0;
        if (tag != null && tag.contains("CustomModelData")) {
            customModelData = tag.getInt("CustomModelData");
        }
        return EnumUtil.grabEnumSafely(theEnum, customModelData);
    }

    public static <T extends Enum<T>> ItemStack createStackWithMeta(RegistryObject<Item> item, int count, int customModelData) {
        ItemStack stack = new ItemStack(item.get(), count);
        if (customModelData > 0) {
            stack.getOrCreateTag().putInt("CustomModelData", customModelData);
        }
        return stack;
    }
}