package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;

import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsLegacy;
import com.hbm.tileentity.storage.TileEntityCrateSteel;
import com.hbm.util.Library;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;

import java.util.List;

public class BarrelFeature extends Feature<NoneFeatureConfiguration> {

    public BarrelFeature(com.mojang.serialization.Codec<NoneFeatureConfiguration> codec) {
        super(codec);
    }

    /** Случайный бетон — 16 цветов. */


    private BlockState getSellafield(int level) {
        return ModBlocks.SELLAFIELD.get().defaultBlockState()
                .setValue(com.hbm.blocks.generic.BlockSellafield.LEVEL, level);
    }

    private boolean isValidSpawn(WorldGenLevel level, int x, int y, int z) {
        BlockState below = level.getBlockState(new BlockPos(x, y - 1, z));

        // Снизу solid, не жидкость
        if (!below.isSolid()) return false;
        if (!below.getFluidState().isEmpty()) return false;

        // Сверху есть место (5 блоков вверх)
        for (int dy = 0; dy < 14; dy++) {
            BlockState above = level.getBlockState(new BlockPos(x, y + dy, z));
            if (!above.isAir() && !above.canBeReplaced()) return false;
        }

        return true;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();

        int x = origin.getX();
        int y = origin.getY();
        int z = origin.getZ();

        if (!isValidSpawn(level, x + 2, y, z + 3)) {
            return false;
        }

        set(level, x + 1, y - 1, z + 0, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 2, y - 1, z + 0, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 3, y - 1, z + 0, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 0, y - 1, z + 1, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 1, y - 1, z + 1, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 2, y - 1, z + 1, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 3, y - 1, z + 1, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 4, y - 1, z + 1, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 0, y - 1, z + 2, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 1, y - 1, z + 2, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 2, y - 1, z + 2, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 3, y - 1, z + 2, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 4, y - 1, z + 2, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 0, y - 1, z + 3, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 1, y - 1, z + 3, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 2, y - 1, z + 3, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 3, y - 1, z + 3, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 4, y - 1, z + 3, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 1, y - 1, z + 4, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 2, y - 1, z + 4, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 3, y - 1, z + 4, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 1, y - 1, z + 5, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 2, y - 1, z + 5, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 3, y - 1, z + 5, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 1, y - 1, z + 6, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 2, y - 1, z + 6, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 3, y - 1, z + 6, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());

        // Основной слой (y + 0) — SLaked, concrete, sellafield
        BlockState slaked = ModBlocks.SELLAFIELD_SLAKED.get().defaultBlockState();

        set(level, x + 1, y + 0, z + 0, slaked);
        set(level, x + 2, y + 0, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 0, z + 0, slaked);
        set(level, x + 0, y + 0, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 0, z + 1, getSellafield(3));
        set(level, x + 2, y + 0, z + 1, getSellafield(4));
        set(level, x + 3, y + 0, z + 1, getSellafield(3));
        set(level, x + 4, y + 0, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 0, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 0, z + 2, getSellafield(4));
        set(level, x + 2, y + 0, z + 2, getSellafield(5));
        set(level, x + 3, y + 0, z + 2, getSellafield(4));
        set(level, x + 4, y + 0, z + 2, slaked);
        set(level, x + 0, y + 0, z + 3, slaked);
        set(level, x + 1, y + 0, z + 3, getSellafield(4));
        set(level, x + 2, y + 0, z + 3, getSellafield(3));
        set(level, x + 3, y + 0, z + 3, getSellafield(4));
        set(level, x + 4, y + 0, z + 3, slaked);
        set(level, x + 1, y + 0, z + 4, slaked);
        set(level, x + 2, y + 0, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 0, z + 4, Library.getRandomConcrete(rand));

        // y + 1 — с сундуком
        set(level, x + 1, y + 1, z + 0, slaked);
        set(level, x + 2, y + 1, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 1, z + 0, slaked);
        set(level, x + 0, y + 1, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 1, z + 1, getSellafield(2));
        set(level, x + 2, y + 1, z + 1, getSellafield(3));
        set(level, x + 3, y + 1, z + 1, getSellafield(3));
        set(level, x + 4, y + 1, z + 1, slaked);
        set(level, x + 0, y + 1, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 1, z + 2, getSellafield(3));

        // Сундук с лутом (стальной ящик)
        BlockPos cratePos = new BlockPos(x + 2, y + 1, z + 2);
        set(level, cratePos.getX(), cratePos.getY(), cratePos.getZ(), ModBlocks.CRATE_STEEL.get().defaultBlockState());
        if (level.getBlockEntity(cratePos) instanceof TileEntityCrateSteel crate) {
            List<ItemStack> loot = ItemPool.getPool(ItemPoolsLegacy.POOL_EXPENSIVE).generate(rand, 16);
            int slot = 0;
            for (ItemStack stack : loot) {
                if (slot >= crate.getContainerSize()) break;
                if (stack == null || stack.isEmpty()) continue;
                crate.setItem(slot++, stack);
            }
            crate.setChanged();
        }

        set(level, x + 3, y + 1, z + 2, getSellafield(3));
        set(level, x + 4, y + 1, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 1, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 1, z + 3, getSellafield(3));
        set(level, x + 2, y + 1, z + 3, getSellafield(2));
        set(level, x + 3, y + 1, z + 3, getSellafield(3));
        set(level, x + 4, y + 1, z + 3, slaked);
        set(level, x + 1, y + 1, z + 4, slaked);
        set(level, x + 2, y + 1, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 1, z + 4, Library.getRandomConcrete(rand));

        // y + 2
        set(level, x + 1, y + 2, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 2, z + 0, slaked);
        set(level, x + 3, y + 2, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 2, z + 1, slaked);
        set(level, x + 1, y + 2, z + 1, getSellafield(1));
        set(level, x + 2, y + 2, z + 1, getSellafield(2));
        set(level, x + 3, y + 2, z + 1, getSellafield(2));
        set(level, x + 4, y + 2, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 2, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 2, z + 2, getSellafield(2));
        set(level, x + 2, y + 2, z + 2, getSellafield(4));
        set(level, x + 3, y + 2, z + 2, getSellafield(2));
        set(level, x + 4, y + 2, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 2, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 2, z + 3, getSellafield(2));
        set(level, x + 2, y + 2, z + 3, getSellafield(1));
        set(level, x + 3, y + 2, z + 3, getSellafield(2));
        set(level, x + 4, y + 2, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 2, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 2, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 2, z + 4, Library.getRandomConcrete(rand));

        // y + 3
        set(level, x + 1, y + 3, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 3, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 3, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 3, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 3, z + 1, getSellafield(1));
        set(level, x + 2, y + 3, z + 1, getSellafield(1));
        set(level, x + 3, y + 3, z + 1, getSellafield(1));
        set(level, x + 4, y + 3, z + 1, slaked);
        set(level, x + 0, y + 3, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 3, z + 2, getSellafield(1));
        set(level, x + 2, y + 3, z + 2, getSellafield(3));
        set(level, x + 3, y + 3, z + 2, getSellafield(1));
        set(level, x + 4, y + 3, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 3, z + 3, slaked);
        set(level, x + 1, y + 3, z + 3, getSellafield(1));
        set(level, x + 2, y + 3, z + 3, getSellafield(0));
        set(level, x + 3, y + 3, z + 3, getSellafield(1));
        set(level, x + 4, y + 3, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 3, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 3, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 3, z + 4, Library.getRandomConcrete(rand));

        // y + 4
        set(level, x + 1, y + 4, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 4, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 4, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 4, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 4, z + 1, getSellafield(0));
        set(level, x + 2, y + 4, z + 1, getSellafield(1));
        set(level, x + 3, y + 4, z + 1, getSellafield(0));
        set(level, x + 4, y + 4, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 4, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 4, z + 2, getSellafield(0));
        set(level, x + 2, y + 4, z + 2, getSellafield(2));
        set(level, x + 3, y + 4, z + 2, getSellafield(1));
        set(level, x + 4, y + 4, z + 2, slaked);
        set(level, x + 0, y + 4, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 4, z + 3, getSellafield(1));
        set(level, x + 2, y + 4, z + 3, getSellafield(0));
        set(level, x + 3, y + 4, z + 3, getSellafield(0));
        set(level, x + 4, y + 4, z + 3, slaked);
        set(level, x + 1, y + 4, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 4, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 4, z + 4, slaked);

        // y + 5
        set(level, x + 1, y + 5, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 5, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 5, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 5, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 5, z + 1, getSellafield(0));
        set(level, x + 2, y + 5, z + 1, getSellafield(0));
        set(level, x + 3, y + 5, z + 1, getSellafield(0));
        set(level, x + 4, y + 5, z + 1, slaked);
        set(level, x + 0, y + 5, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 5, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 5, z + 2, getSellafield(1));
        set(level, x + 3, y + 5, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 5, z + 2, Blocks.AIR.defaultBlockState());
        set(level, x + 0, y + 5, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 5, z + 3, getSellafield(0));
        set(level, x + 2, y + 5, z + 3, getSellafield(0));
        set(level, x + 3, y + 5, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 5, z + 3, slaked);
        set(level, x + 1, y + 5, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 5, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 5, z + 4, Library.getRandomConcrete(rand));

        // y + 6 — DECO_LEAD, TOXIC_BLOCK
        set(level, x + 1, y + 6, z + 0, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 2, y + 6, z + 0, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 3, y + 6, z + 0, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 0, y + 6, z + 1, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 1, y + 6, z + 1, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 6, z + 1, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 3, y + 6, z + 1, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 6, z + 1, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 0, y + 6, z + 2, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 1, y + 6, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 6, z + 2, getSellafield(0));
        set(level, x + 3, y + 6, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 6, z + 2, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 0, y + 6, z + 3, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 1, y + 6, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 6, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 3, y + 6, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 6, z + 3, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 1, y + 6, z + 4, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 2, y + 6, z + 4, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 3, y + 6, z + 4, ModBlocks.DECO_LEAD.get().defaultBlockState());

        // y + 7
        set(level, x + 1, y + 7, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 7, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 7, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 7, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 7, z + 1, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 7, z + 1, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 3, y + 7, z + 1, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 7, z + 1, slaked);
        set(level, x + 0, y + 7, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 7, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 7, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 3, y + 7, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 7, z + 2, slaked);
        set(level, x + 0, y + 7, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 7, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 7, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 3, y + 7, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 7, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 7, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 7, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 7, z + 4, Library.getRandomConcrete(rand));

        // y + 8 — DECO_LEAD
        set(level, x + 1, y + 8, z + 0, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 2, y + 8, z + 0, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 3, y + 8, z + 0, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 0, y + 8, z + 1, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 1, y + 8, z + 1, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 8, z + 1, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 3, y + 8, z + 1, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 8, z + 1, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 0, y + 8, z + 2, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 1, y + 8, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 8, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 3, y + 8, z + 2, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 8, z + 2, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 0, y + 8, z + 3, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 1, y + 8, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 2, y + 8, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 3, y + 8, z + 3, ModBlocks.TOXIC_BLOCK.get().defaultBlockState());
        set(level, x + 4, y + 8, z + 3, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 1, y + 8, z + 4, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 2, y + 8, z + 4, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 3, y + 8, z + 4, ModBlocks.DECO_LEAD.get().defaultBlockState());

        // y + 9 — рамка (без центра)
        set(level, x + 1, y + 9, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 9, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 9, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 9, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 4, y + 9, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 9, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 4, y + 9, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 9, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 4, y + 9, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 9, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 9, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 9, z + 4, Library.getRandomConcrete(rand));

        // y + 10, 11, 12 — с дверью на +10, +11
        set(level, x + 1, y + 10, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 10, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 10, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 10, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 4, y + 10, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 10, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 4, y + 10, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 10, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 4, y + 10, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 10, z + 4, Library.getRandomConcrete(rand));
        // железная дверь (низ) на x+2, z+4
        placeIronDoor(level, new BlockPos(x + 2, y + 10, z + 4), Direction.SOUTH);
        set(level, x + 3, y + 10, z + 4, Library.getRandomConcrete(rand));

        set(level, x + 1, y + 11, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 11, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 11, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 11, z + 1, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        set(level, x + 0, y + 11, z + 2, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        set(level, x + 4, y + 11, z + 2, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        set(level, x + 0, y + 11, z + 3, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        set(level, x + 4, y + 11, z + 3, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        set(level, x + 1, y + 11, z + 4, Library.getRandomConcrete(rand));
        // железная дверь (верх) на x+2, y+11, z+4 — от doorBlock place
        set(level, x + 3, y + 11, z + 4, Library.getRandomConcrete(rand));

        set(level, x + 1, y + 12, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 12, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 12, z + 0, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 12, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 4, y + 12, z + 1, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 12, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 4, y + 12, z + 2, Library.getRandomConcrete(rand));
        set(level, x + 0, y + 12, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 4, y + 12, z + 3, Library.getRandomConcrete(rand));
        set(level, x + 1, y + 12, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 2, y + 12, z + 4, Library.getRandomConcrete(rand));
        set(level, x + 3, y + 12, z + 4, Library.getRandomConcrete(rand));

        // y + 13 — крыша
        set(level, x + 1, y + 13, z + 0, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 2, y + 13, z + 0, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 3, y + 13, z + 0, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 0, y + 13, z + 1, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 1, y + 13, z + 1, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 2, y + 13, z + 1, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 3, y + 13, z + 1, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 4, y + 13, z + 1, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 0, y + 13, z + 2, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 1, y + 13, z + 2, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 2, y + 13, z + 2, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 4, y + 13, z + 2, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 0, y + 13, z + 3, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 1, y + 13, z + 3, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 2, y + 13, z + 3, ModBlocks.DECO_LEAD.get().defaultBlockState());
        set(level, x + 1, y + 13, z + 4, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 2, y + 13, z + 4, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());
        set(level, x + 3, y + 13, z + 4, ModBlocks.REINFORCED_BRICK.get().defaultBlockState());

        // === Лестница (ladder) ===
        for (int h = 0; h <= 9; h++) {
            set(level, x + 2, y + h, z + 5, Blocks.LADDER.defaultBlockState()
                    .setValue(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH));
        }

        return true;
    }

    /** Установка блока с проверкой высоты. */
    private void set(WorldGenLevel level, int x, int y, int z, BlockState state) {
        if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
            level.setBlock(new BlockPos(x, y, z), state, 2);
        }
    }

    /** Железная дверь — low + upper. */
    private void placeIronDoor(WorldGenLevel level, BlockPos pos, Direction facing) {
        // Сначала НИЖНЯЯ половина
        BlockState lower = Blocks.IRON_DOOR.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.LOWER)
                .setValue(BlockStateProperties.OPEN, false)
                .setValue(BlockStateProperties.POWERED, false);

        // Потом ВЕРХНЯЯ — с тем же facing
        BlockState upper = Blocks.IRON_DOOR.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, facing)
                .setValue(BlockStateProperties.DOUBLE_BLOCK_HALF, DoubleBlockHalf.UPPER)
                .setValue(BlockStateProperties.OPEN, false)
                .setValue(BlockStateProperties.POWERED, false);

        level.setBlock(pos, lower, 3);
        level.setBlock(pos.above(), upper, 3);
    }
}