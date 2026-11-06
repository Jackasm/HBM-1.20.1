package com.hbm.util;

import com.hbm.itempool.ItemPool;
import com.hbm.tileentity.deco.TileEntityLoot;
import com.hbm.tileentity.storage.TileEntitySupplyCrate;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;

public class ContainerHelper {

    /**
     * Заполняет контейнер предметами из пула.
     *
     * @param poolName имя пула (например "POOL_WEAPONS")
     * @param min      минимум предметов (включительно)
     * @param max      максимум предметов (включительно). Если max <= min — берётся min
     */
    public static void fill(Level level, BlockPos pos, String poolName, int min, int max, RandomSource rand) {
        if (level == null || level.isClientSide) return;

        ItemPool pool = ItemPool.getPool(poolName);
        if (pool == null) return;

        int count = min;
        if (max > min) {
            count += rand.nextInt(max - min + 1);
        }
        if (count <= 0) return;

        List<ItemStack> items = pool.generate(rand, count);
        if (items.isEmpty()) return;

        BlockEntity be = level.getBlockEntity(pos);
        if (be == null) return;

        // Специальный случай: SupplyCrate (не Container)
        if (be instanceof TileEntitySupplyCrate crate) {
            fillSupplyCrate(crate, items);
            return;
        }

        // Специальный случай: Loot (свой формат)
        if (be instanceof TileEntityLoot loot) {
            fillLoot(loot, items, rand);
            return;
        }

        // Универсальный случай: Container (крейты, сундуки, бочки, safe, file cabinet)
        if (be instanceof Container container) {
            fillContainer(container, items);
        }
    }

    private static void fillSupplyCrate(TileEntitySupplyCrate crate, List<ItemStack> items) {
        for (ItemStack stack : items) {
            if (!stack.isEmpty()) {
                crate.items.add(stack.copy());
            }
        }
        crate.setChanged();
    }

    private static void fillLoot(TileEntityLoot loot, List<ItemStack> items, RandomSource rand) {
        double yStep = 0.03125;
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) continue;

            double x = (rand.nextDouble() - 0.5) * 0.8;
            double z = (rand.nextDouble() - 0.5) * 0.8;
            loot.addItem(stack.copy(), x, i * yStep, z);
        }
        loot.setChanged();
    }

    private static void fillContainer(Container container, List<ItemStack> items) {
        int size = container.getContainerSize();
        int slot = 0;
        for (ItemStack stack : items) {
            if (stack.isEmpty()) continue;
            if (slot >= size) break;

            while (slot < size && !container.getItem(slot).isEmpty()) {
                slot++;
            }
            if (slot >= size) break;

            container.setItem(slot, stack.copy());
            slot++;
        }
        if (container instanceof BlockEntity be) {
            be.setChanged();
        }
    }
}