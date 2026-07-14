package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class WasteLog extends RotatedPillarBlock {

    private final ResourceLocation topTexture;
    private final ResourceLocation sideTexture;

    public WasteLog(Properties properties, ResourceLocation topTexture, ResourceLocation sideTexture) {
        super(properties);
        this.topTexture = topTexture;
        this.sideTexture = sideTexture;
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(this);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        Level level = builder.getLevel();
        RandomSource random = level.random;

        if (this == ModBlocks.WASTE_LOG.get() && random.nextInt(1000) == 0) {
            List<ItemStack> drops = new ArrayList<>();
            drops.add(new ItemStack(ModItems.BURNT_BARK.get()));
            return drops;
        }

        return super.getDrops(state, builder);
    }

    @Override
    public void spawnAfterBreak(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ItemStack stack, boolean dropXp) {
        super.spawnAfterBreak(state, level, pos, stack, dropXp);
    }

    public static BlockBehaviour.Properties createPropertiesWasteLog() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(5.0F, 2.5F)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY);
    }

    public static BlockBehaviour.Properties createPropertiesFrozenLog() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.WOOD)
                .strength(0.5F, 2.5F)
                .sound(SoundType.GLASS)
                .pushReaction(PushReaction.DESTROY);
    }

    public ResourceLocation getTopTexture() {
        return topTexture;
    }

    public ResourceLocation getSideTexture() {
        return sideTexture;
    }
}