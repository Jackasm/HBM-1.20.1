package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsSingle;
import com.hbm.tileentity.deco.TileEntityLanternBehemoth;
import com.hbm.tileentity.deco.TileEntityLoot;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

public class LanternBehemothFeature extends Feature<NoneFeatureConfiguration> {

    private static final int LANTERN_HEIGHT = 3;

    public LanternBehemothFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        int x = origin.getX();
        int z = origin.getZ();

        // Ищем поверхность (первый твёрдый блок сверху вниз)
        int groundY = findGround(level, x, z);
        if (groundY == Integer.MIN_VALUE) return false;

        int y = groundY + 1;
        BlockPos corePos = new BlockPos(x, y, z);

        // Проверка свободного места над фонарём (3 блока)
        for (int dy = 0; dy < LANTERN_HEIGHT; dy++) {
            BlockPos check = new BlockPos(x, y + dy, z);
            BlockState state = level.getBlockState(check);
            if (!state.getFluidState().isEmpty()) return false;
            if (!state.isAir() && !state.canBeReplaced()) return false;
        }

        // Проверка, что фонарь не выйдет за мир
        if (y + LANTERN_HEIGHT - 1 >= level.getMaxBuildHeight()) return false;

        Block lanternBlock = ModBlocks.LANTERN_BEHEMOTH.get();
        if (!(lanternBlock instanceof BlockDummyable dummy)) return false;

        Direction facing = Direction.from2DDataValue(rand.nextInt(4));

        // Core-блок
        BlockState coreState = dummy.getCoreState(facing);
        level.setBlock(corePos, coreState, 3);

        // Dummy-части
        dummy.placeMultiblock(level, corePos, facing);

        // TE
        if (level.getBlockEntity(corePos) instanceof TileEntityLanternBehemoth lantern) {
            lantern.isBroken = true;
        }

        // Лут рядом (50%)
        if (rand.nextInt(2) == 0) {
            placeLoot(level, rand, x, z - 2);
        }

        return true;
    }

    /**
     * Высота лута считается по земле под ним, а не по высоте фонаря.
     * Если под точкой вода/воздух — возвращаем MIN_VALUE, лут не ставим.
     */
    private void placeLoot(WorldGenLevel level, RandomSource rand, int lootX, int lootZ) {
        int groundY = findGround(level, lootX, lootZ);
        if (groundY == Integer.MIN_VALUE) return;

        BlockPos lootPos = new BlockPos(lootX, groundY + 1, lootZ);
        if (!level.ensureCanWrite(lootPos)) return;

        BlockState spot = level.getBlockState(lootPos);
        if (!spot.isAir() && !spot.canBeReplaced()) return;

        // Не ставим лут в воду
        if (!spot.getFluidState().isEmpty()) return;

        level.setBlock(lootPos, ModBlocks.DECO_LOOT.get().defaultBlockState(), 2);
        if (level.getBlockEntity(lootPos) instanceof TileEntityLoot loot) {
            ItemStack stack = ItemPool.getStack(ItemPoolsSingle.POOL_BLUEPRINTS, rand);
            if (stack != null && !stack.isEmpty()) {
                loot.addItem(stack, 0, 0, 0);
            }
        }
    }

    /**
     * Ищет поверхность: сверху вниз до первого solid блока, без жидкостей, без листьев.
     * Возвращает Y твёрдого блока. Если не нашёл — Integer.MIN_VALUE.
     */
    private int findGround(WorldGenLevel level, int x, int z) {
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
        while (y > level.getMinBuildHeight()) {
            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);

            if (state.isAir()) { y--; continue; }
            if (!state.getFluidState().isEmpty()) { y--; continue; }
            if (state.is(net.minecraft.tags.BlockTags.LEAVES)) { y--; continue; }
            if (!state.isSolid()) { y--; continue; }

            return y;
        }
        return Integer.MIN_VALUE;
    }
}