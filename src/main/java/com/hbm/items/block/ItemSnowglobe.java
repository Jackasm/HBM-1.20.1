package com.hbm.items.block;

import com.hbm.blocks.generic.BlockSnowglobe.SnowglobeType;
import com.hbm.items.ModItems;
import com.hbm.render.item.SnowglobeItemRenderer;
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

public class ItemSnowglobe extends BlockItem {

    public ItemSnowglobe(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private SnowglobeItemRenderer renderer;

            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) {
                    this.renderer = new SnowglobeItemRenderer();
                }
                return this.renderer;
            }
        });
    }

    public static ItemStack withType(SnowglobeType type) {
        ItemStack stack = new ItemStack(ModItems.SNOWGLOBE.get());
        stack.getOrCreateTag().putInt("CustomModelData", type.ordinal());
        return stack;
    }

    @Override
    public @NotNull String getDescriptionId(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        int customModelData = 0;
        if (tag != null && tag.contains("CustomModelData")) {
            customModelData = tag.getInt("CustomModelData");
        }
        SnowglobeType type = EnumUtil.grabEnumSafely(SnowglobeType.class, customModelData);
        return super.getDescriptionId() + "_" + type.name().toLowerCase(java.util.Locale.US);
    }

    @Override
    public @NotNull Component getName(@NotNull ItemStack stack) {
        return Component.translatable(this.getDescriptionId(stack));
    }
}