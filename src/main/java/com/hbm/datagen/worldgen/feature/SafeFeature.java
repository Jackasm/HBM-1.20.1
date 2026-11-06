package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsSingle;
import com.hbm.tileentity.storage.TileEntitySafe;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class SafeFeature extends Feature<NoneFeatureConfiguration> {

    public SafeFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        int x = origin.getX();
        int z = origin.getZ();
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z) + 1;

        BlockPos pos = new BlockPos(x, y, z);

        // Проверка места
        if (!level.getBlockState(pos.below()).isSolid()) return false;
        if (!level.getBlockState(pos).isAir()) return false;

        level.setBlock(pos, ModBlocks.SAFE.get().defaultBlockState(), 2);

        if (!(level.getBlockEntity(pos) instanceof TileEntitySafe safe)) return false;

        // 4 уровня лута по шансам 40/30/20/10
        int roll = rand.nextInt(10);
        String poolName;
        int count;

        if (roll < 4) {              // 40% — rusty
            safe.setMod(1);
            poolName = ItemPoolsSingle.POOL_VAULT_RUSTY;
            count = rand.nextInt(4) + 3;    // 3-6
        } else if (roll < 7) {       // 30% — standard
            safe.setMod(0.1);
            poolName = ItemPoolsSingle.POOL_VAULT_STANDARD;
            count = rand.nextInt(3) + 2;    // 2-4
        } else if (roll < 9) {       // 20% — reinforced
            safe.setMod(0.02);
            poolName = ItemPoolsSingle.POOL_VAULT_REINFORCED;
            count = rand.nextInt(3) + 1;    // 1-3
        } else {                     // 10% — unbreakable
            safe.setMod(0.0);
            poolName = ItemPoolsSingle.POOL_VAULT_UNBREAKABLE;
            count = rand.nextInt(2) + 1;    // 1-2
        }

        // Заполняем инвентарь
        List<ItemStack> loot = ItemPool.getPool(poolName).generate(rand, count);
        int slot = 0;
        for (ItemStack stack : loot) {
            if (slot >= safe.getContainerSize()) break;
            if (stack == null || stack.isEmpty()) continue;
            safe.setItem(slot++, stack);
        }

        // PIN-код, запирание
        safe.setPins(rand.nextInt(999) + 1);
        safe.lock();

        // 30% шанс пауков
        if (rand.nextInt(10) < 3) {
            safe.fillWithSpiders();
        }

        safe.setChanged();
        return true;
    }
}