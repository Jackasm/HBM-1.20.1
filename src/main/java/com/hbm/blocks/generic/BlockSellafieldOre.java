package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.items.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class BlockSellafieldOre extends BlockSellafieldSlaked {

    public BlockSellafieldOre(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        RandomSource rand = builder.getLevel().random;

        ItemStack dropStack = ItemStack.EMPTY;
        if (this == ModBlocks.ORE_SELLAFIELD_DIAMOND.get()) {
            dropStack = new ItemStack(Items.DIAMOND);
        } else if (this == ModBlocks.ORE_SELLAFIELD_EMERALD.get()) {
            dropStack = new ItemStack(Items.EMERALD);
        } else if (this == ModBlocks.ORE_SELLAFIELD_RADGEM.get()) {
            dropStack = new ItemStack(ModItems.GEM_RAD.get());
        } else {
            dropStack = new ItemStack(this);
        }

        // Количество с учётом фортуны
        int fortune = 0;
        var tool = builder.getParameter(LootContextParams.TOOL);
        if (tool != null && !tool.isEmpty()) {
            // Получаем уровень фортуны из инструмента
            // В реальном коде нужно получить enchantment level
        }

        int count = 1;
        if (fortune > 0 && dropStack.getItem() != this.asItem()) {
            int j = rand.nextInt(fortune + 2) - 1;
            if (j < 0) j = 0;
            count = (j + 1);
        }

        return List.of(new ItemStack(dropStack.getItem(), count));
    }

    @Override
    public void spawnAfterBreak(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull ItemStack stack, boolean dropXp) {
        super.spawnAfterBreak(state, level, pos, stack, dropXp);

        RandomSource rand = level.random;
        int fortune = 0; // Получить из инструмента

        int exp = 0;
        if (this == ModBlocks.ORE_SELLAFIELD_DIAMOND.get() ||
                this == ModBlocks.ORE_SELLAFIELD_EMERALD.get() ||
                this == ModBlocks.ORE_SELLAFIELD_RADGEM.get()) {
            exp = Mth.nextInt(rand, 3, 7);
        }

        if (exp > 0) {
            this.popExperience(level, pos, exp);
        }
    }

    public static BlockBehaviour.Properties createProperties() {
        return BlockBehaviour.Properties.of()
                .mapColor(MapColor.STONE)
                .strength(5.0F)
                .requiresCorrectToolForDrops()
                .pushReaction(PushReaction.DESTROY);
    }
}