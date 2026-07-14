package com.hbm.items.block;

import com.hbm.blocks.generic.BlockPlushie.PlushieType;
import com.hbm.items.ModItems;
import com.hbm.render.item.PlushieItemRenderer;
import com.hbm.util.EnumUtil;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class ItemPlushie extends BlockItem {

    public ItemPlushie(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private PlushieItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new PlushieItemRenderer();
                }
                return this.renderer;
            }
        });
    }

    public static ItemStack withType(PlushieType type) {
        ItemStack stack = new ItemStack(ModItems.PLUSHIE.get());
        stack.getOrCreateTag().putInt("CustomModelData", type.ordinal());
        return stack;
    }

    public static ItemStack withType(PlushieType type, int count) {
        ItemStack stack = new ItemStack(ModItems.PLUSHIE.get(), count);
        stack.getOrCreateTag().putInt("CustomModelData", type.ordinal());
        return stack;
    }

    public static PlushieType getType(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int customModelData = 0;
        if (tag != null && tag.contains("CustomModelData")) {
            customModelData = tag.getInt("CustomModelData");
        }
        return EnumUtil.grabEnumSafely(PlushieType.class, customModelData);
    }

    @Override
    public @NotNull String getDescriptionId(@NotNull ItemStack stack) {
        PlushieType type = getType(stack);
        return super.getDescriptionId() + "_" + type.name().toLowerCase(java.util.Locale.US);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        PlushieType type = getType(stack);
        return Component.translatable(this.getDescriptionId(stack));
    }
}