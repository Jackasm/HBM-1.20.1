package com.hbm.blocks.generic;

import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsLegacy;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BlockMeteoriteTreasure extends Block {

    public BlockMeteoriteTreasure(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(@NotNull BlockState state, @NotNull LootParams.Builder builder) {
        // Получаем уровень и позицию из параметров
        ServerLevel level = builder.getLevel();
        BlockPos pos = builder.getParameter(LootContextParams.ORIGIN) != null ?
                BlockPos.containing(builder.getParameter(LootContextParams.ORIGIN)) : BlockPos.ZERO;

        // Проверяем наличие инструмента для фортуны
        int fortune = 0;
        var tool = builder.getParameter(LootContextParams.TOOL);
        if (tool != null) {
             fortune = EnchantmentHelper.getItemEnchantmentLevel(Enchantments.BLOCK_FORTUNE, tool);
        }

        return getDrops(level, pos, state, fortune);
    }

    // Кастомный метод для получения дропа
    public List<ItemStack> getDrops(Level world, BlockPos pos, BlockState state, int fortune) {
        List<ItemStack> ret = new ArrayList<>();

        RandomSource rand = world.random;
        int count = 1 + rand.nextInt(3);

        // Используем статический пул из ItemPoolsLegacy
        ItemPool pool = ItemPoolsLegacy.POOL_METEORITE_TREASURE;

        for (int i = 0; i < count; i++) {
            ItemStack stack = pool.generateOne(rand);
            if (!stack.isEmpty()) {
                ret.add(stack);
            }
        }

        return ret;
    }
}