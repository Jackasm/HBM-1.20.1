package com.hbm.blocks.generic;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public class BlockSellafieldSlaked extends Block {

    public ResourceLocation[] iconLocations;

    public BlockSellafieldSlaked(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(this);
    }

    @OnlyIn(Dist.CLIENT)
    public int getColor(BlockState state, BlockGetter level, BlockPos pos, int tintIndex) {
        int meta = 0;
        if (state.hasProperty(BlockStateProperties.LAYERS)) {
            meta = state.getValue(BlockStateProperties.LAYERS) - 1;
        }
        return java.awt.Color.HSBtoRGB(0F, 0F, 1F - meta / 15F);
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(5.0F)
                .pushReaction(PushReaction.DESTROY);
    }
}