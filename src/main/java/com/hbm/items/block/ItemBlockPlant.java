package com.hbm.items.block;

import com.hbm.blocks.generic.BlockNTMFlower;
import com.hbm.blocks.generic.BlockTallPlant;
import com.hbm.blocks.generic.BlockDeadPlant;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

public class ItemBlockPlant extends BlockItem {

    private final PlantType plantType;

    public enum PlantType {
        FLOWER(BlockNTMFlower.EnumFlowerType.class),
        TALL(BlockTallPlant.EnumTallFlower.class),
        DEAD(BlockDeadPlant.EnumDeadPlantType.class);

        public final Class<? extends Enum<?>> enumClass;

        PlantType(Class<? extends Enum<?>> enumClass) {
            this.enumClass = enumClass;
        }
    }

    public ItemBlockPlant(Block block, Properties properties, PlantType plantType) {
        super(block, properties);
        this.plantType = plantType;
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        int type = getCustomModelData(stack);
        String translationKey = getTranslationKeyForType(type);
        return Component.translatable(translationKey);
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        int type = getCustomModelData(stack);
        return getTranslationKeyForType(type);
    }

    private int getCustomModelData(ItemStack stack) {
        return ItemBlockResourceStone.getType(stack);
    }

    private String getTranslationKeyForType(int type) {
        String blockName = getBlock().getDescriptionId();
        String typeName = getTypeName(type);
        return blockName + "." + typeName;
    }

    private String getTypeName(int type) {
        Enum<?>[] values = (Enum<?>[]) plantType.enumClass.getEnumConstants();
        if (type >= 0 && type < values.length) {
            return values[type].name().toLowerCase(java.util.Locale.ROOT);
        }
        return values[0].name().toLowerCase(java.util.Locale.ROOT);
    }
}