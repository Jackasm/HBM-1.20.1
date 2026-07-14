package com.hbm.creativetabs;

import com.hbm.blocks.generic.*;
import com.hbm.items.ModItems;
import com.hbm.items.block.*;
import com.hbm.util.HBMEnums;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

public class BlockTab {
    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.block_tab"))
                .icon(() -> ModItems.ORE_URANIUM.get().getDefaultInstance())
                .displayItems((parameters, output) -> {
                    TabContentProvider.addItemsToTab(HBMEnums.CreativeTabRegistry.BLOCK_TAB, output);

                    for (BlockCoke.CokeType type : BlockCoke.CokeType.values()) {
                        output.accept(ItemBlockCoke.withType(type));
                    }

                    for (BlockOreBasalt.EnumBasaltOreType type : BlockOreBasalt.EnumBasaltOreType.values()) {
                        int ordinal = type.ordinal();
                        ItemStack stack = new ItemStack(ModItems.ORE_BASALT.get(), 1);
                        stack.getOrCreateTag().putInt("CustomModelData", ordinal);
                        output.accept(stack);
                    }

                    for (DyeColor color : DyeColor.values()) {
                        output.accept(ItemBlockConcreteColored.withColor(color));
                    }

                    for (BlockConcreteColoredExt.EnumConcreteType type : BlockConcreteColoredExt.EnumConcreteType.values()) {
                        output.accept(ItemBlockConcreteColoredExt.withType(type));
                    }

                    for (BlockResourceStone.EnumStoneType type : BlockResourceStone.EnumStoneType.values()) {
                        output.accept(ItemBlockResourceStone.withType(type));
                    }

                    for (BlockSnowglobe.SnowglobeType type : BlockSnowglobe.SnowglobeType.values()) {
                        if (type != BlockSnowglobe.SnowglobeType.NONE) {
                            output.accept(ItemSnowglobe.withType(type));
                        }
                    }

                    for (BlockPlushie.PlushieType type : BlockPlushie.PlushieType.values()) {
                        if (type != BlockPlushie.PlushieType.NONE) {
                            output.accept(ItemPlushie.withType(type));
                        }
                    }

                    for (BlockNTMFlower.EnumFlowerType type : BlockNTMFlower.EnumFlowerType.values()) {
                        ItemStack stack = new ItemStack(ModItems.PLANT_FLOWER.get(), 1);
                        stack.getOrCreateTag().putInt("CustomModelData", type.ordinal());
                        output.accept(stack);
                    }

                    for (BlockDeadPlant.EnumDeadPlantType type : BlockDeadPlant.EnumDeadPlantType.values()) {
                        ItemStack stack = new ItemStack(ModItems.PLANT_DEAD.get(), 1);
                        stack.getOrCreateTag().putInt("CustomModelData", type.ordinal());
                        output.accept(stack);
                    }

                    for (BlockTallPlant.EnumTallFlower type : BlockTallPlant.EnumTallFlower.values()) {
                        ItemStack stack = new ItemStack(ModItems.PLANT_TALL.get(), 1);
                        stack.getOrCreateTag().putInt("CustomModelData", type.ordinal());
                        output.accept(stack);
                    }
                })
                .build();
    }
}