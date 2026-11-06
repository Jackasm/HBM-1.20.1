package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BarbedWire;
import com.hbm.blocks.bomb.BlockCrashedBomb;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsLegacy;
import com.hbm.util.Library;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class DesertAtomFeature extends Feature<NoneFeatureConfiguration> {

    /** Начало постройки: по нему считается сторона для проволоки. */
    private int originX;
    private int originZ;


    // Блоки, как в оригинале (оригинальные имена -> твои ModBlocks)
    private final BlockState BLOCK_2 = ModBlocks.YELLOW_BARREL.get().defaultBlockState();
    private final BlockState BLOCK_3 = ModBlocks.REINFORCED_SAND.get().defaultBlockState();
    private final BlockState BLOCK_4 = ModBlocks.CRASHED_BALEFIRE.get().defaultBlockState()
            .setValue(BlockCrashedBomb.TYPE, BlockCrashedBomb.EnumDudType.NUKE);
    private final BlockState BLOCK_7 = ModBlocks.DECO_TUNGSTEN.get().defaultBlockState();

    private final BlockState AIR = Blocks.AIR.defaultBlockState();

    public DesertAtomFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    /** Валидные блоки для спавна — как GetValidSpawnBlocks() в оригинале. */
    private List<Block> getValidSpawnBlocks() {
        return List.of(
                Blocks.GRASS_BLOCK,
                Blocks.DIRT,
                Blocks.SAND,
                Blocks.SANDSTONE,
                Blocks.STONE,
                Blocks.TERRACOTTA,           // hardened_clay
                // stained_hardened_clay — это все цветные терракоты, их проще проверить тегом
                Blocks.WHITE_TERRACOTTA, Blocks.ORANGE_TERRACOTTA, Blocks.MAGENTA_TERRACOTTA,
                Blocks.LIGHT_BLUE_TERRACOTTA, Blocks.YELLOW_TERRACOTTA, Blocks.LIME_TERRACOTTA,
                Blocks.PINK_TERRACOTTA, Blocks.GRAY_TERRACOTTA, Blocks.LIGHT_GRAY_TERRACOTTA,
                Blocks.CYAN_TERRACOTTA, Blocks.PURPLE_TERRACOTTA, Blocks.BLUE_TERRACOTTA,
                Blocks.BROWN_TERRACOTTA, Blocks.GREEN_TERRACOTTA, Blocks.RED_TERRACOTTA,
                Blocks.BLACK_TERRACOTTA
        );
    }

    /** Аналог LocationIsValidSpawn из 1.7.10. */
    private boolean isValidSpawn(WorldGenLevel level, BlockPos pos) {
        BlockState checkBlock = level.getBlockState(pos.below());
        BlockState blockAbove = level.getBlockState(pos);
        BlockState blockBelow = level.getBlockState(pos.below(2));

        // blockAbove должен быть воздухом
        if (!blockAbove.isAir()) {
            return false;
        }

        for (Block valid : getValidSpawnBlocks()) {
            if (checkBlock.is(valid)) {
                return true;
            } else if (checkBlock.is(Blocks.SNOW) && blockBelow.is(valid)) {
                return true;
            } else if (checkBlock.canBeReplaced() && blockBelow.is(valid)) {
                // аналог getMaterial() == Material.plants
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        WorldGenLevel level = context.level();
        RandomSource rand = context.random();
        BlockPos origin = context.origin();

        int x = origin.getX();
        int z = origin.getZ();
        int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);

        BlockPos pos = new BlockPos(x, y, z);

        // В оригинале проверялась только точка (x+20, y, z+16)
        if (!isValidSpawn(level, pos.offset(20, 0, 16))) {
            return false;
        }

        generate(level, rand, x, y, z);
        return true;
    }

    /**
     * Аналог generate_r0 из оригинала.
     *
     * <p>Публичный, потому что постройку теперь ставит кусок структуры
     * ({@code AtomPiece}), а не фича: контент NBT-шаблона не совпадал с эталоном
     * (#160), а код генератора перенесён из 1.7.10 построчно.</p>
     */
    public void generate(WorldGenLevel level, RandomSource rand, int x, int y, int z) {
        this.originX = x;
        this.originZ = z;

        set(level, x + 6, y - 5, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 5, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 5, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 5, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 5, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 5, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 5, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 5, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 5, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 5, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 5, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 5, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 5, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 5, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 5, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 5, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 5, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 5, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 5, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 5, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 5, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 5, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 5, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 5, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 20, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 20, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 20, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 5, z + 21, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 5, z + 21, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 5, z + 21, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 4, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 4, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 4, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 4, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 4, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 4, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 4, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 4, z + 10, BLOCK_2);
        set(level, x + 8, y - 4, z + 10, BLOCK_2);
        set(level, x + 9, y - 4, z + 10, BLOCK_2);
        set(level, x + 10, y - 4, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 4, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 4, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 4, z + 11, BLOCK_2);
        set(level, x + 7, y - 4, z + 11, BLOCK_2);
        set(level, x + 8, y - 4, z + 11, Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 4, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 4, z + 11, BLOCK_2);
        set(level, x + 11, y - 4, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 4, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 4, z + 12, BLOCK_2);
        set(level, x + 7, y - 4, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 4, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 4, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 4, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 4, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 4, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 4, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 4, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 4, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 4, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 4, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 4, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 4, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 4, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 4, z + 14, BLOCK_2);
        set(level, x + 8, y - 4, z + 14,Blocks.WATER.defaultBlockState());
        setChest(level, rand, x + 9, y - 4, z + 14, Direction.NORTH, ItemPoolsLegacy.POOL_NUKE_MISC, 10);
        set(level, x + 10, y - 4, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 4, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 4, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 4, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 4, z + 15,Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 8));
        set(level, x + 9, y - 4, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 4, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 4, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 4, z + 16,Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 8));
        set(level, x + 9, y - 4, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 4, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 4, z + 17,Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 8));
        set(level, x + 9, y - 4, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 4, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 4, z + 18,Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 8));
        set(level, x + 9, y - 4, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 4, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 4, z + 19,Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 8));
        set(level, x + 9, y - 4, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 4, z + 20, BLOCK_3);
        set(level, x + 7, y - 4, z + 20, BLOCK_3);
        set(level, x + 8, y - 4, z + 20,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 4, z + 20, BLOCK_3);
        set(level, x + 10, y - 4, z + 20, BLOCK_3);
        set(level, x + 4, y - 4, z + 21, BLOCK_3);
        set(level, x + 5, y - 4, z + 21, BLOCK_3);
        set(level, x + 6, y - 4, z + 21, BLOCK_3);
        set(level, x + 7, y - 4, z + 21, BLOCK_3);
        set(level, x + 8, y - 4, z + 21,Blocks.WATER.defaultBlockState().setValue(LiquidBlock.LEVEL, 8));
        set(level, x + 9, y - 4, z + 21, BLOCK_3);
        set(level, x + 10, y - 4, z + 21, BLOCK_3);
        set(level, x + 11, y - 4, z + 21, BLOCK_3);
        set(level, x + 12, y - 4, z + 21, BLOCK_3);
        set(level, x + 4, y - 4, z + 22, BLOCK_3);
        set(level, x + 5, y - 4, z + 22, BLOCK_3);
        set(level, x + 6, y - 4, z + 22, BLOCK_3);
        set(level, x + 7, y - 4, z + 22, BLOCK_3);
        set(level, x + 8, y - 4, z + 22, BLOCK_3);
        set(level, x + 9, y - 4, z + 22, BLOCK_3);
        set(level, x + 10, y - 4, z + 22, BLOCK_3);
        set(level, x + 11, y - 4, z + 22, BLOCK_3);
        set(level, x + 12, y - 4, z + 22, BLOCK_3);
        set(level, x + 3, y - 4, z + 23, BLOCK_3);
        set(level, x + 4, y - 4, z + 23, BLOCK_3);
        set(level, x + 5, y - 4, z + 23, BLOCK_3);
        set(level, x + 6, y - 4, z + 23, BLOCK_3);
        set(level, x + 7, y - 4, z + 23, BLOCK_3);
        set(level, x + 8, y - 4, z + 23, BLOCK_3);
        set(level, x + 9, y - 4, z + 23, BLOCK_3);
        set(level, x + 10, y - 4, z + 23, BLOCK_3);
        set(level, x + 11, y - 4, z + 23, BLOCK_3);
        set(level, x + 12, y - 4, z + 23, BLOCK_3);
        set(level, x + 13, y - 4, z + 23, BLOCK_3);
        set(level, x + 3, y - 4, z + 24, BLOCK_3);
        set(level, x + 4, y - 4, z + 24, BLOCK_3);
        set(level, x + 5, y - 4, z + 24, BLOCK_3);
        set(level, x + 6, y - 4, z + 24, BLOCK_3);
        set(level, x + 7, y - 4, z + 24, BLOCK_3);
        set(level, x + 8, y - 4, z + 24, BLOCK_3);
        set(level, x + 9, y - 4, z + 24, BLOCK_3);
        set(level, x + 10, y - 4, z + 24, BLOCK_3);
        set(level, x + 11, y - 4, z + 24, BLOCK_3);
        set(level, x + 12, y - 4, z + 24, BLOCK_3);
        set(level, x + 13, y - 4, z + 24, BLOCK_3);
        set(level, x + 3, y - 4, z + 25, BLOCK_3);
        set(level, x + 4, y - 4, z + 25, BLOCK_3);
        set(level, x + 5, y - 4, z + 25, BLOCK_3);
        set(level, x + 6, y - 4, z + 25, BLOCK_3);
        set(level, x + 7, y - 4, z + 25, BLOCK_3);
        set(level, x + 8, y - 4, z + 25, BLOCK_3);
        set(level, x + 9, y - 4, z + 25, BLOCK_3);
        set(level, x + 10, y - 4, z + 25, BLOCK_3);
        set(level, x + 11, y - 4, z + 25, BLOCK_3);
        set(level, x + 12, y - 4, z + 25, BLOCK_3);
        set(level, x + 13, y - 4, z + 25, BLOCK_3);
        set(level, x + 3, y - 4, z + 26, BLOCK_3);
        set(level, x + 4, y - 4, z + 26, BLOCK_3);
        set(level, x + 5, y - 4, z + 26, BLOCK_3);
        set(level, x + 6, y - 4, z + 26, BLOCK_3);
        set(level, x + 7, y - 4, z + 26, BLOCK_3);
        set(level, x + 8, y - 4, z + 26, BLOCK_3);
        set(level, x + 9, y - 4, z + 26, BLOCK_3);
        set(level, x + 10, y - 4, z + 26, BLOCK_3);
        set(level, x + 11, y - 4, z + 26, BLOCK_3);
        set(level, x + 12, y - 4, z + 26, BLOCK_3);
        set(level, x + 13, y - 4, z + 26, BLOCK_3);
        set(level, x + 3, y - 4, z + 27, BLOCK_3);
        set(level, x + 4, y - 4, z + 27, BLOCK_3);
        set(level, x + 5, y - 4, z + 27, BLOCK_3);
        set(level, x + 6, y - 4, z + 27, BLOCK_3);
        set(level, x + 7, y - 4, z + 27, BLOCK_3);
        set(level, x + 8, y - 4, z + 27, BLOCK_3);
        set(level, x + 9, y - 4, z + 27, BLOCK_3);
        set(level, x + 10, y - 4, z + 27, BLOCK_3);
        set(level, x + 11, y - 4, z + 27, BLOCK_3);
        set(level, x + 12, y - 4, z + 27, BLOCK_3);
        set(level, x + 13, y - 4, z + 27, BLOCK_3);
        set(level, x + 4, y - 4, z + 28, BLOCK_3);
        set(level, x + 5, y - 4, z + 28, BLOCK_3);
        set(level, x + 6, y - 4, z + 28, BLOCK_3);
        set(level, x + 7, y - 4, z + 28, BLOCK_3);
        set(level, x + 8, y - 4, z + 28, BLOCK_3);
        set(level, x + 9, y - 4, z + 28, BLOCK_3);
        set(level, x + 10, y - 4, z + 28, BLOCK_3);
        set(level, x + 11, y - 4, z + 28, BLOCK_3);
        set(level, x + 12, y - 4, z + 28, BLOCK_3);
        set(level, x + 4, y - 4, z + 29, BLOCK_3);
        set(level, x + 5, y - 4, z + 29, BLOCK_3);
        set(level, x + 6, y - 4, z + 29, BLOCK_3);
        set(level, x + 7, y - 4, z + 29, BLOCK_3);
        set(level, x + 8, y - 4, z + 29, BLOCK_3);
        set(level, x + 9, y - 4, z + 29, BLOCK_3);
        set(level, x + 10, y - 4, z + 29, BLOCK_3);
        set(level, x + 11, y - 4, z + 29, BLOCK_3);
        set(level, x + 12, y - 4, z + 29, BLOCK_3);
        set(level, x + 6, y - 4, z + 30, BLOCK_3);
        set(level, x + 7, y - 4, z + 30, BLOCK_3);
        set(level, x + 8, y - 4, z + 30, BLOCK_3);
        set(level, x + 9, y - 4, z + 30, BLOCK_3);
        set(level, x + 10, y - 4, z + 30, BLOCK_3);
        set(level, x + 6, y - 3, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 3, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 3, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 3, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 3, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 3, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 3, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 3, z + 10, BLOCK_2);
        set(level, x + 8, y - 3, z + 10,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 10,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 3, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 3, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 3, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 3, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 3, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 3, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 3, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 3, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 3, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 3, z + 14,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 14,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 14,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 3, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 3, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 3, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 3, z + 15,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 3, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 3, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 3, z + 16,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 3, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 3, z + 17,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 3, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 3, z + 18,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 3, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 3, z + 19,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 3, z + 20, BLOCK_3);
        set(level, x + 7, y - 3, z + 20, BLOCK_3);
        set(level, x + 8, y - 3, z + 20,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 20, BLOCK_3);
        set(level, x + 10, y - 3, z + 20, BLOCK_3);
        set(level, x + 4, y - 3, z + 21, BLOCK_3);
        set(level, x + 5, y - 3, z + 21, BLOCK_3);
        set(level, x + 6, y - 3, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 21, BLOCK_3);
        set(level, x + 12, y - 3, z + 21, BLOCK_3);
        set(level, x + 4, y - 3, z + 22, BLOCK_3);
        set(level, x + 5, y - 3, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 3, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 3, z + 22, BLOCK_3);
        set(level, x + 3, y - 3, z + 23, BLOCK_3);
        set(level, x + 4, y - 3, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 3, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 3, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 3, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 3, z + 23, BLOCK_3);
        set(level, x + 3, y - 3, z + 24, BLOCK_3);
        set(level, x + 4, y - 3, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 3, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 3, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 3, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 3, z + 24, BLOCK_3);
        set(level, x + 3, y - 3, z + 25, BLOCK_3);
        set(level, x + 4, y - 3, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 3, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 3, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 25, BLOCK_4);
        set(level, x + 9, y - 3, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 3, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 3, z + 25, BLOCK_3);
        set(level, x + 3, y - 3, z + 26, BLOCK_3);
        set(level, x + 4, y - 3, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 3, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 3, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 3, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 3, z + 26, BLOCK_3);
        set(level, x + 3, y - 3, z + 27, BLOCK_3);
        set(level, x + 4, y - 3, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 3, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 3, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 3, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 3, z + 27, BLOCK_3);
        set(level, x + 4, y - 3, z + 28, BLOCK_3);
        set(level, x + 5, y - 3, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 3, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 3, z + 28, BLOCK_3);
        set(level, x + 4, y - 3, z + 29, BLOCK_3);
        set(level, x + 5, y - 3, z + 29, BLOCK_3);
        set(level, x + 6, y - 3, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 3, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 3, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 3, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 3, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 3, z + 29, BLOCK_3);
        set(level, x + 12, y - 3, z + 29, BLOCK_3);
        set(level, x + 6, y - 3, z + 30, BLOCK_3);
        set(level, x + 7, y - 3, z + 30, BLOCK_3);
        set(level, x + 8, y - 3, z + 30, BLOCK_3);
        set(level, x + 9, y - 3, z + 30, BLOCK_3);
        set(level, x + 10, y - 3, z + 30, BLOCK_3);
        set(level, x, y - 2, z, BLOCK_3);
        set(level, x + 1, y - 2, z, BLOCK_3);
        set(level, x + 2, y - 2, z, BLOCK_3);
        set(level, x + 3, y - 2, z, BLOCK_3);
        set(level, x + 4, y - 2, z, BLOCK_3);
        set(level, x + 5, y - 2, z, BLOCK_3);
        set(level, x + 6, y - 2, z, BLOCK_3);
        set(level, x + 7, y - 2, z, BLOCK_3);
        set(level, x + 8, y - 2, z, BLOCK_3);
        set(level, x + 9, y - 2, z, BLOCK_3);
        set(level, x + 10, y - 2, z, BLOCK_3);
        set(level, x + 11, y - 2, z, BLOCK_3);
        set(level, x + 12, y - 2, z, BLOCK_3);
        set(level, x + 13, y - 2, z, BLOCK_3);
        set(level, x + 14, y - 2, z, BLOCK_3);
        set(level, x + 15, y - 2, z, BLOCK_3);
        set(level, x + 16, y - 2, z, BLOCK_3);
        set(level, x + 17, y - 2, z, BLOCK_3);
        set(level, x + 18, y - 2, z, BLOCK_3);
        set(level, x + 19, y - 2, z, BLOCK_3);
        set(level, x + 20, y - 2, z, BLOCK_3);
        set(level, x + 21, y - 2, z, BLOCK_3);
        set(level, x + 22, y - 2, z, BLOCK_3);
        set(level, x + 23, y - 2, z, BLOCK_3);
        set(level, x + 24, y - 2, z, BLOCK_3);
        set(level, x + 25, y - 2, z, BLOCK_3);
        set(level, x + 26, y - 2, z, BLOCK_3);
        set(level, x + 27, y - 2, z, BLOCK_3);
        set(level, x + 28, y - 2, z, BLOCK_3);
        set(level, x + 29, y - 2, z, BLOCK_3);
        set(level, x + 30, y - 2, z, BLOCK_3);
        set(level, x + 31, y - 2, z, BLOCK_3);
        set(level, x + 32, y - 2, z, BLOCK_3);
        set(level, x + 33, y - 2, z, BLOCK_3);
        set(level, x + 34, y - 2, z, BLOCK_3);
        set(level, x + 35, y - 2, z, BLOCK_3);
        set(level, x + 36, y - 2, z, BLOCK_3);
        set(level, x + 37, y - 2, z, BLOCK_3);
        set(level, x + 38, y - 2, z, BLOCK_3);
        set(level, x + 39, y - 2, z, BLOCK_3);
        set(level, x + 40, y - 2, z, BLOCK_3);
        set(level, x, y - 2, z + 1, BLOCK_3);
        set(level, x + 40, y - 2, z + 1, BLOCK_3);
        set(level, x, y - 2, z + 2, BLOCK_3);
        set(level, x + 40, y - 2, z + 2, BLOCK_3);
        set(level, x, y - 2, z + 3, BLOCK_3);
        set(level, x + 8, y - 2, z + 3, BLOCK_3);
        set(level, x + 9, y - 2, z + 3, BLOCK_3);
        set(level, x + 10, y - 2, z + 3, BLOCK_3);
        set(level, x + 11, y - 2, z + 3, BLOCK_3);
        set(level, x + 12, y - 2, z + 3, BLOCK_3);
        set(level, x + 13, y - 2, z + 3, BLOCK_3);
        set(level, x + 14, y - 2, z + 3, BLOCK_3);
        set(level, x + 15, y - 2, z + 3, BLOCK_3);
        set(level, x + 16, y - 2, z + 3, BLOCK_3);
        set(level, x + 17, y - 2, z + 3, BLOCK_3);
        set(level, x + 18, y - 2, z + 3, BLOCK_3);
        set(level, x + 19, y - 2, z + 3, BLOCK_3);
        set(level, x + 20, y - 2, z + 3, BLOCK_3);
        set(level, x + 21, y - 2, z + 3, BLOCK_3);
        set(level, x + 22, y - 2, z + 3, BLOCK_3);
        set(level, x + 23, y - 2, z + 3, BLOCK_3);
        set(level, x + 24, y - 2, z + 3, BLOCK_3);
        set(level, x + 25, y - 2, z + 3, BLOCK_3);
        set(level, x + 26, y - 2, z + 3, BLOCK_3);
        set(level, x + 27, y - 2, z + 3, BLOCK_3);
        set(level, x + 28, y - 2, z + 3, BLOCK_3);
        set(level, x + 29, y - 2, z + 3, BLOCK_3);
        set(level, x + 30, y - 2, z + 3, BLOCK_3);
        set(level, x + 31, y - 2, z + 3, BLOCK_3);
        set(level, x + 32, y - 2, z + 3, BLOCK_3);
        set(level, x + 33, y - 2, z + 3, BLOCK_3);
        set(level, x + 34, y - 2, z + 3, BLOCK_3);
        set(level, x + 35, y - 2, z + 3, BLOCK_3);
        set(level, x + 36, y - 2, z + 3, BLOCK_3);
        set(level, x + 37, y - 2, z + 3, BLOCK_3);
        set(level, x + 38, y - 2, z + 3, BLOCK_3);
        set(level, x + 39, y - 2, z + 3, BLOCK_3);
        set(level, x + 40, y - 2, z + 3, BLOCK_3);
        set(level, x, y - 2, z + 4, BLOCK_3);
        set(level, x + 8, y - 2, z + 4, BLOCK_3);
        set(level, x + 9, y - 2, z + 4, BLOCK_3);
        set(level, x + 10, y - 2, z + 4, BLOCK_3);
        set(level, x + 11, y - 2, z + 4, BLOCK_3);
        set(level, x + 12, y - 2, z + 4, BLOCK_3);
        set(level, x + 13, y - 2, z + 4, BLOCK_3);
        set(level, x + 14, y - 2, z + 4, BLOCK_3);
        set(level, x + 15, y - 2, z + 4, BLOCK_3);
        set(level, x + 16, y - 2, z + 4, BLOCK_3);
        set(level, x + 17, y - 2, z + 4, BLOCK_3);
        set(level, x + 18, y - 2, z + 4, BLOCK_3);
        set(level, x + 19, y - 2, z + 4, BLOCK_3);
        set(level, x + 20, y - 2, z + 4, BLOCK_3);
        set(level, x + 21, y - 2, z + 4, BLOCK_3);
        set(level, x + 22, y - 2, z + 4, BLOCK_3);
        set(level, x + 23, y - 2, z + 4, BLOCK_3);
        set(level, x + 24, y - 2, z + 4, BLOCK_3);
        set(level, x + 25, y - 2, z + 4, BLOCK_3);
        set(level, x + 26, y - 2, z + 4, BLOCK_3);
        set(level, x + 27, y - 2, z + 4, BLOCK_3);
        set(level, x + 28, y - 2, z + 4, BLOCK_3);
        set(level, x + 29, y - 2, z + 4, BLOCK_3);
        set(level, x + 30, y - 2, z + 4, BLOCK_3);
        set(level, x + 31, y - 2, z + 4, BLOCK_3);
        set(level, x + 32, y - 2, z + 4, BLOCK_3);
        set(level, x + 33, y - 2, z + 4, BLOCK_3);
        set(level, x + 34, y - 2, z + 4, BLOCK_3);
        set(level, x + 35, y - 2, z + 4, BLOCK_3);
        set(level, x + 36, y - 2, z + 4, BLOCK_3);
        set(level, x + 37, y - 2, z + 4, BLOCK_3);
        set(level, x + 38, y - 2, z + 4, BLOCK_3);
        set(level, x + 39, y - 2, z + 4, BLOCK_3);
        set(level, x + 40, y - 2, z + 4, BLOCK_3);
        set(level, x, y - 2, z + 5, BLOCK_3);
        set(level, x + 8, y - 2, z + 5, BLOCK_3);
        set(level, x + 38, y - 2, z + 5, BLOCK_3);
        set(level, x + 39, y - 2, z + 5, BLOCK_3);
        set(level, x + 40, y - 2, z + 5, BLOCK_3);
        set(level, x, y - 2, z + 6, BLOCK_3);
        set(level, x + 38, y - 2, z + 6, BLOCK_3);
        set(level, x + 39, y - 2, z + 6, BLOCK_3);
        set(level, x + 40, y - 2, z + 6, BLOCK_3);
        set(level, x, y - 2, z + 7, BLOCK_3);
        set(level, x + 38, y - 2, z + 7, BLOCK_3);
        set(level, x + 39, y - 2, z + 7, BLOCK_3);
        set(level, x + 40, y - 2, z + 7, BLOCK_3);
        set(level, x, y - 2, z + 8, BLOCK_3);
        set(level, x + 6, y - 2, z + 8, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 2, z + 8, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 2, z + 8, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 2, z + 8, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 2, z + 8, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 8, BLOCK_3);
        set(level, x + 39, y - 2, z + 8, BLOCK_3);
        set(level, x + 40, y - 2, z + 8, BLOCK_3);
        set(level, x, y - 2, z + 9, BLOCK_3);
        set(level, x + 5, y - 2, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 2, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 2, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 2, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 2, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 2, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 2, z + 9, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 9, BLOCK_3);
        set(level, x + 39, y - 2, z + 9, BLOCK_3);
        set(level, x + 40, y - 2, z + 9, BLOCK_3);
        set(level, x, y - 2, z + 10, BLOCK_3);
        set(level, x + 4, y - 2, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 2, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 2, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 2, z + 10,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 10,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 10,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 2, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 12, y - 2, z + 10, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 10, BLOCK_3);
        set(level, x + 39, y - 2, z + 10, BLOCK_3);
        set(level, x + 40, y - 2, z + 10, BLOCK_3);
        set(level, x, y - 2, z + 11, BLOCK_3);
        set(level, x + 4, y - 2, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 2, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 2, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 11,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 12, y - 2, z + 11, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 11, BLOCK_3);
        set(level, x + 39, y - 2, z + 11, BLOCK_3);
        set(level, x + 40, y - 2, z + 11, BLOCK_3);
        set(level, x, y - 2, z + 12, BLOCK_3);
        set(level, x + 4, y - 2, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 2, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 2, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 12,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 12, y - 2, z + 12, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 12, BLOCK_3);
        set(level, x + 39, y - 2, z + 12, BLOCK_3);
        set(level, x + 40, y - 2, z + 12, BLOCK_3);
        set(level, x, y - 2, z + 13, BLOCK_3);
        set(level, x + 4, y - 2, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 2, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 2, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 13,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 12, y - 2, z + 13, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 13, BLOCK_3);
        set(level, x + 39, y - 2, z + 13, BLOCK_3);
        set(level, x + 40, y - 2, z + 13, BLOCK_3);
        set(level, x, y - 2, z + 14, BLOCK_3);
        set(level, x + 4, y - 2, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 5, y - 2, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 2, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 2, z + 14,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 14,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 14,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 2, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 12, y - 2, z + 14, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 14, BLOCK_3);
        set(level, x + 39, y - 2, z + 14, BLOCK_3);
        set(level, x + 40, y - 2, z + 14, BLOCK_3);
        set(level, x, y - 2, z + 15, BLOCK_3);
        set(level, x + 5, y - 2, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 6, y - 2, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 2, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 2, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 2, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 2, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 11, y - 2, z + 15, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 15, BLOCK_3);
        set(level, x + 39, y - 2, z + 15, BLOCK_3);
        set(level, x + 40, y - 2, z + 15, BLOCK_3);
        set(level, x, y - 2, z + 16, BLOCK_3);
        set(level, x + 6, y - 2, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 7, y - 2, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 2, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 2, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 10, y - 2, z + 16, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 16, BLOCK_3);
        set(level, x + 39, y - 2, z + 16, BLOCK_3);
        set(level, x + 40, y - 2, z + 16, BLOCK_3);
        set(level, x, y - 2, z + 17, BLOCK_3);
        set(level, x + 7, y - 2, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 2, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 2, z + 17, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 17, BLOCK_3);
        set(level, x + 39, y - 2, z + 17, BLOCK_3);
        set(level, x + 40, y - 2, z + 17, BLOCK_3);
        set(level, x, y - 2, z + 18, BLOCK_3);
        set(level, x + 7, y - 2, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 2, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 2, z + 18, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 18, BLOCK_3);
        set(level, x + 39, y - 2, z + 18, BLOCK_3);
        set(level, x + 40, y - 2, z + 18, BLOCK_3);
        set(level, x, y - 2, z + 19, BLOCK_3);
        set(level, x + 7, y - 2, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 8, y - 2, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 9, y - 2, z + 19, Library.getRandomConcrete(rand));
        set(level, x + 38, y - 2, z + 19, BLOCK_3);
        set(level, x + 39, y - 2, z + 19, BLOCK_3);
        set(level, x + 40, y - 2, z + 19, BLOCK_3);
        set(level, x, y - 2, z + 20, BLOCK_3);
        set(level, x + 6, y - 2, z + 20, BLOCK_3);
        set(level, x + 7, y - 2, z + 20, BLOCK_3);
        set(level, x + 8, y - 2, z + 20, BLOCK_3);
        set(level, x + 9, y - 2, z + 20, BLOCK_3);
        set(level, x + 10, y - 2, z + 20, BLOCK_3);
        set(level, x + 38, y - 2, z + 20, BLOCK_3);
        set(level, x + 39, y - 2, z + 20, BLOCK_3);
        set(level, x + 40, y - 2, z + 20, BLOCK_3);
        set(level, x, y - 2, z + 21, BLOCK_3);
        set(level, x + 4, y - 2, z + 21, BLOCK_3);
        set(level, x + 5, y - 2, z + 21, BLOCK_3);
        set(level, x + 6, y - 2, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 21,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 21, BLOCK_3);
        set(level, x + 12, y - 2, z + 21, BLOCK_3);
        set(level, x + 16, y - 2, z + 21, BLOCK_3);
        set(level, x + 17, y - 2, z + 21, BLOCK_3);
        set(level, x + 18, y - 2, z + 21, BLOCK_3);
        set(level, x + 19, y - 2, z + 21, BLOCK_3);
        set(level, x + 20, y - 2, z + 21, BLOCK_3);
        set(level, x + 21, y - 2, z + 21, BLOCK_3);
        set(level, x + 22, y - 2, z + 21, BLOCK_3);
        set(level, x + 23, y - 2, z + 21, BLOCK_3);
        set(level, x + 24, y - 2, z + 21, BLOCK_3);
        set(level, x + 25, y - 2, z + 21, BLOCK_3);
        set(level, x + 26, y - 2, z + 21, BLOCK_3);
        set(level, x + 27, y - 2, z + 21, BLOCK_3);
        set(level, x + 28, y - 2, z + 21, BLOCK_3);
        set(level, x + 29, y - 2, z + 21, BLOCK_3);
        set(level, x + 30, y - 2, z + 21, BLOCK_3);
        set(level, x + 31, y - 2, z + 21, BLOCK_3);
        set(level, x + 32, y - 2, z + 21, BLOCK_3);
        set(level, x + 33, y - 2, z + 21, BLOCK_3);
        set(level, x + 34, y - 2, z + 21, BLOCK_3);
        set(level, x + 35, y - 2, z + 21, BLOCK_3);
        set(level, x + 36, y - 2, z + 21, BLOCK_3);
        set(level, x + 37, y - 2, z + 21, BLOCK_3);
        set(level, x + 38, y - 2, z + 21, BLOCK_3);
        set(level, x + 39, y - 2, z + 21, BLOCK_3);
        set(level, x + 40, y - 2, z + 21, BLOCK_3);
        set(level, x, y - 2, z + 22, BLOCK_3);
        set(level, x + 4, y - 2, z + 22, BLOCK_3);
        set(level, x + 5, y - 2, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 2, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 22,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 2, z + 22, BLOCK_3);
        set(level, x + 16, y - 2, z + 22, BLOCK_3);
        set(level, x + 17, y - 2, z + 22, BLOCK_3);
        set(level, x + 18, y - 2, z + 22, BLOCK_3);
        set(level, x + 19, y - 2, z + 22, BLOCK_3);
        set(level, x + 20, y - 2, z + 22, BLOCK_3);
        set(level, x + 21, y - 2, z + 22, BLOCK_3);
        set(level, x + 22, y - 2, z + 22, BLOCK_3);
        set(level, x + 23, y - 2, z + 22, BLOCK_3);
        set(level, x + 24, y - 2, z + 22, BLOCK_3);
        set(level, x + 25, y - 2, z + 22, BLOCK_3);
        set(level, x + 26, y - 2, z + 22, BLOCK_3);
        set(level, x + 27, y - 2, z + 22, BLOCK_3);
        set(level, x + 28, y - 2, z + 22, BLOCK_3);
        set(level, x + 29, y - 2, z + 22, BLOCK_3);
        set(level, x + 30, y - 2, z + 22, BLOCK_3);
        set(level, x + 31, y - 2, z + 22, BLOCK_3);
        set(level, x + 32, y - 2, z + 22, BLOCK_3);
        set(level, x + 33, y - 2, z + 22, BLOCK_3);
        set(level, x + 34, y - 2, z + 22, BLOCK_3);
        set(level, x + 35, y - 2, z + 22, BLOCK_3);
        set(level, x + 36, y - 2, z + 22, BLOCK_3);
        set(level, x + 37, y - 2, z + 22, BLOCK_3);
        set(level, x + 38, y - 2, z + 22, BLOCK_3);
        set(level, x + 39, y - 2, z + 22, BLOCK_3);
        set(level, x + 40, y - 2, z + 22, BLOCK_3);
        set(level, x, y - 2, z + 23, BLOCK_3);
        set(level, x + 3, y - 2, z + 23, BLOCK_3);
        set(level, x + 4, y - 2, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 2, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 2, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 2, z + 23,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 2, z + 23, BLOCK_3);
        set(level, x + 16, y - 2, z + 23, BLOCK_3);
        set(level, x + 17, y - 2, z + 23, BLOCK_3);
        set(level, x + 18, y - 2, z + 23, BLOCK_3);
        set(level, x + 19, y - 2, z + 23, BLOCK_3);
        set(level, x + 20, y - 2, z + 23, BLOCK_3);
        set(level, x + 21, y - 2, z + 23, BLOCK_3);
        set(level, x + 22, y - 2, z + 23, BLOCK_3);
        set(level, x + 23, y - 2, z + 23, BLOCK_3);
        set(level, x + 24, y - 2, z + 23, BLOCK_3);
        set(level, x + 25, y - 2, z + 23, BLOCK_3);
        set(level, x + 26, y - 2, z + 23, BLOCK_3);
        set(level, x + 27, y - 2, z + 23, BLOCK_3);
        set(level, x + 28, y - 2, z + 23, BLOCK_3);
        set(level, x + 29, y - 2, z + 23, BLOCK_3);
        set(level, x + 30, y - 2, z + 23, BLOCK_3);
        set(level, x + 31, y - 2, z + 23, BLOCK_3);
        set(level, x + 32, y - 2, z + 23, BLOCK_3);
        set(level, x + 33, y - 2, z + 23, BLOCK_3);
        set(level, x + 34, y - 2, z + 23, BLOCK_3);
        set(level, x + 35, y - 2, z + 23, BLOCK_3);
        set(level, x + 36, y - 2, z + 23, BLOCK_3);
        set(level, x + 37, y - 2, z + 23, BLOCK_3);
        set(level, x + 38, y - 2, z + 23, BLOCK_3);
        set(level, x + 39, y - 2, z + 23, BLOCK_3);
        set(level, x + 40, y - 2, z + 23, BLOCK_3);
        set(level, x, y - 2, z + 24, BLOCK_3);
        set(level, x + 3, y - 2, z + 24, BLOCK_3);
        set(level, x + 4, y - 2, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 2, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 2, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 2, z + 24,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 2, z + 24, BLOCK_3);
        set(level, x + 16, y - 2, z + 24, BLOCK_3);
        set(level, x + 17, y - 2, z + 24, BLOCK_3);
        set(level, x + 18, y - 2, z + 24, BLOCK_3);
        set(level, x + 19, y - 2, z + 24, BLOCK_3);
        set(level, x + 20, y - 2, z + 24, BLOCK_3);
        set(level, x + 21, y - 2, z + 24, BLOCK_3);
        set(level, x + 22, y - 2, z + 24, BLOCK_3);
        set(level, x + 23, y - 2, z + 24, BLOCK_3);
        set(level, x + 24, y - 2, z + 24, BLOCK_3);
        set(level, x + 25, y - 2, z + 24, BLOCK_3);
        set(level, x + 26, y - 2, z + 24, BLOCK_3);
        set(level, x + 27, y - 2, z + 24, BLOCK_3);
        set(level, x + 28, y - 2, z + 24, BLOCK_3);
        set(level, x + 29, y - 2, z + 24, BLOCK_3);
        set(level, x + 30, y - 2, z + 24, BLOCK_3);
        set(level, x + 31, y - 2, z + 24, BLOCK_3);
        set(level, x + 32, y - 2, z + 24, BLOCK_3);
        set(level, x + 33, y - 2, z + 24, BLOCK_3);
        set(level, x + 34, y - 2, z + 24, BLOCK_3);
        set(level, x + 35, y - 2, z + 24, BLOCK_3);
        set(level, x + 36, y - 2, z + 24, BLOCK_3);
        set(level, x + 37, y - 2, z + 24, BLOCK_3);
        set(level, x + 38, y - 2, z + 24, BLOCK_3);
        set(level, x + 39, y - 2, z + 24, BLOCK_3);
        set(level, x + 40, y - 2, z + 24, BLOCK_3);
        set(level, x, y - 2, z + 25, BLOCK_3);
        set(level, x + 3, y - 2, z + 25, BLOCK_3);
        set(level, x + 4, y - 2, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 2, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 2, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 2, z + 25,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 2, z + 25, BLOCK_3);
        set(level, x + 16, y - 2, z + 25, BLOCK_3);
        set(level, x + 17, y - 2, z + 25, BLOCK_3);
        set(level, x + 18, y - 2, z + 25, BLOCK_3);
        set(level, x + 19, y - 2, z + 25, BLOCK_3);
        set(level, x + 20, y - 2, z + 25, BLOCK_3);
        set(level, x + 21, y - 2, z + 25, BLOCK_3);
        set(level, x + 22, y - 2, z + 25, BLOCK_3);
        set(level, x + 23, y - 2, z + 25, BLOCK_3);
        set(level, x + 24, y - 2, z + 25, BLOCK_3);
        set(level, x + 25, y - 2, z + 25, BLOCK_3);
        set(level, x + 26, y - 2, z + 25, BLOCK_3);
        set(level, x + 27, y - 2, z + 25, BLOCK_3);
        set(level, x + 28, y - 2, z + 25, BLOCK_3);
        set(level, x + 29, y - 2, z + 25, BLOCK_3);
        set(level, x + 30, y - 2, z + 25, BLOCK_3);
        set(level, x + 31, y - 2, z + 25, BLOCK_3);
        set(level, x + 32, y - 2, z + 25, BLOCK_3);
        set(level, x + 33, y - 2, z + 25, BLOCK_3);
        set(level, x + 34, y - 2, z + 25, BLOCK_3);
        set(level, x + 35, y - 2, z + 25, BLOCK_3);
        set(level, x + 36, y - 2, z + 25, BLOCK_3);
        set(level, x + 37, y - 2, z + 25, BLOCK_3);
        set(level, x + 38, y - 2, z + 25, BLOCK_3);
        set(level, x + 39, y - 2, z + 25, BLOCK_3);
        set(level, x + 40, y - 2, z + 25, BLOCK_3);
        set(level, x, y - 2, z + 26, BLOCK_3);
        set(level, x + 3, y - 2, z + 26, BLOCK_3);
        set(level, x + 4, y - 2, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 2, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 2, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 2, z + 26,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 2, z + 26, BLOCK_3);
        set(level, x + 16, y - 2, z + 26, BLOCK_3);
        set(level, x + 17, y - 2, z + 26, BLOCK_3);
        set(level, x + 18, y - 2, z + 26, BLOCK_3);
        set(level, x + 19, y - 2, z + 26, BLOCK_3);
        set(level, x + 20, y - 2, z + 26, BLOCK_3);
        set(level, x + 21, y - 2, z + 26, BLOCK_3);
        set(level, x + 22, y - 2, z + 26, BLOCK_3);
        set(level, x + 23, y - 2, z + 26, BLOCK_3);
        set(level, x + 24, y - 2, z + 26, BLOCK_3);
        set(level, x + 25, y - 2, z + 26, BLOCK_3);
        set(level, x + 26, y - 2, z + 26, BLOCK_3);
        set(level, x + 27, y - 2, z + 26, BLOCK_3);
        set(level, x + 28, y - 2, z + 26, BLOCK_3);
        set(level, x + 29, y - 2, z + 26, BLOCK_3);
        set(level, x + 30, y - 2, z + 26, BLOCK_3);
        set(level, x + 31, y - 2, z + 26, BLOCK_3);
        set(level, x + 32, y - 2, z + 26, BLOCK_3);
        set(level, x + 33, y - 2, z + 26, BLOCK_3);
        set(level, x + 34, y - 2, z + 26, BLOCK_3);
        set(level, x + 35, y - 2, z + 26, BLOCK_3);
        set(level, x + 36, y - 2, z + 26, BLOCK_3);
        set(level, x + 37, y - 2, z + 26, BLOCK_3);
        set(level, x + 38, y - 2, z + 26, BLOCK_3);
        set(level, x + 39, y - 2, z + 26, BLOCK_3);
        set(level, x + 40, y - 2, z + 26, BLOCK_3);
        set(level, x, y - 2, z + 27, BLOCK_3);
        set(level, x + 3, y - 2, z + 27, BLOCK_3);
        set(level, x + 4, y - 2, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 5, y - 2, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 2, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 2, z + 27,Blocks.WATER.defaultBlockState());
        set(level, x + 13, y - 2, z + 27, BLOCK_3);
        set(level, x + 16, y - 2, z + 27, BLOCK_3);
        set(level, x + 17, y - 2, z + 27, BLOCK_3);
        set(level, x + 18, y - 2, z + 27, BLOCK_3);
        set(level, x + 19, y - 2, z + 27, BLOCK_3);
        set(level, x + 20, y - 2, z + 27, BLOCK_3);
        set(level, x + 21, y - 2, z + 27, BLOCK_3);
        set(level, x + 22, y - 2, z + 27, BLOCK_3);
        set(level, x + 23, y - 2, z + 27, BLOCK_3);
        set(level, x + 24, y - 2, z + 27, BLOCK_3);
        set(level, x + 25, y - 2, z + 27, BLOCK_3);
        set(level, x + 26, y - 2, z + 27, BLOCK_3);
        set(level, x + 27, y - 2, z + 27, BLOCK_3);
        set(level, x + 28, y - 2, z + 27, BLOCK_3);
        set(level, x + 29, y - 2, z + 27, BLOCK_3);
        set(level, x + 30, y - 2, z + 27, BLOCK_3);
        set(level, x + 31, y - 2, z + 27, BLOCK_3);
        set(level, x + 32, y - 2, z + 27, BLOCK_3);
        set(level, x + 33, y - 2, z + 27, BLOCK_3);
        set(level, x + 34, y - 2, z + 27, BLOCK_3);
        set(level, x + 35, y - 2, z + 27, BLOCK_3);
        set(level, x + 36, y - 2, z + 27, BLOCK_3);
        set(level, x + 37, y - 2, z + 27, BLOCK_3);
        set(level, x + 38, y - 2, z + 27, BLOCK_3);
        set(level, x + 39, y - 2, z + 27, BLOCK_3);
        set(level, x + 40, y - 2, z + 27, BLOCK_3);
        set(level, x, y - 2, z + 28, BLOCK_3);
        set(level, x + 4, y - 2, z + 28, BLOCK_3);
        set(level, x + 5, y - 2, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 6, y - 2, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 28,Blocks.WATER.defaultBlockState());
        set(level, x + 12, y - 2, z + 28, BLOCK_3);
        set(level, x + 16, y - 2, z + 28, BLOCK_3);
        set(level, x + 17, y - 2, z + 28, BLOCK_3);
        set(level, x + 18, y - 2, z + 28, BLOCK_3);
        set(level, x + 19, y - 2, z + 28, BLOCK_3);
        set(level, x + 20, y - 2, z + 28, BLOCK_3);
        set(level, x + 21, y - 2, z + 28, BLOCK_3);
        set(level, x + 22, y - 2, z + 28, BLOCK_3);
        set(level, x + 23, y - 2, z + 28, BLOCK_3);
        set(level, x + 24, y - 2, z + 28, BLOCK_3);
        set(level, x + 25, y - 2, z + 28, BLOCK_3);
        set(level, x + 26, y - 2, z + 28, BLOCK_3);
        set(level, x + 27, y - 2, z + 28, BLOCK_3);
        set(level, x + 28, y - 2, z + 28, BLOCK_3);
        set(level, x + 29, y - 2, z + 28, BLOCK_3);
        set(level, x + 30, y - 2, z + 28, BLOCK_3);
        set(level, x + 31, y - 2, z + 28, BLOCK_3);
        set(level, x + 32, y - 2, z + 28, BLOCK_3);
        set(level, x + 33, y - 2, z + 28, BLOCK_3);
        set(level, x + 34, y - 2, z + 28, BLOCK_3);
        set(level, x + 35, y - 2, z + 28, BLOCK_3);
        set(level, x + 36, y - 2, z + 28, BLOCK_3);
        set(level, x + 37, y - 2, z + 28, BLOCK_3);
        set(level, x + 38, y - 2, z + 28, BLOCK_3);
        set(level, x + 39, y - 2, z + 28, BLOCK_3);
        set(level, x + 40, y - 2, z + 28, BLOCK_3);
        set(level, x, y - 2, z + 29, BLOCK_3);
        set(level, x + 4, y - 2, z + 29, BLOCK_3);
        set(level, x + 5, y - 2, z + 29, BLOCK_3);
        set(level, x + 6, y - 2, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 7, y - 2, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 8, y - 2, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 9, y - 2, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 10, y - 2, z + 29,Blocks.WATER.defaultBlockState());
        set(level, x + 11, y - 2, z + 29, BLOCK_3);
        set(level, x + 12, y - 2, z + 29, BLOCK_3);
        set(level, x + 40, y - 2, z + 29, BLOCK_3);
        set(level, x, y - 2, z + 30, BLOCK_3);
        set(level, x + 6, y - 2, z + 30, BLOCK_3);
        set(level, x + 7, y - 2, z + 30, BLOCK_3);
        set(level, x + 8, y - 2, z + 30, BLOCK_3);
        set(level, x + 9, y - 2, z + 30, BLOCK_3);
        set(level, x + 10, y - 2, z + 30, BLOCK_3);
        set(level, x + 40, y - 2, z + 30, BLOCK_3);
        set(level, x, y - 2, z + 31, BLOCK_3);
        set(level, x + 16, y - 2, z + 31, BLOCK_3);
        set(level, x + 17, y - 2, z + 31, BLOCK_3);
        set(level, x + 18, y - 2, z + 31, BLOCK_3);
        set(level, x + 19, y - 2, z + 31, BLOCK_3);
        set(level, x + 20, y - 2, z + 31, BLOCK_3);
        set(level, x + 21, y - 2, z + 31, BLOCK_3);
        set(level, x + 22, y - 2, z + 31, BLOCK_3);
        set(level, x + 23, y - 2, z + 31, BLOCK_3);
        set(level, x + 24, y - 2, z + 31, BLOCK_3);
        set(level, x + 25, y - 2, z + 31, BLOCK_3);
        set(level, x + 26, y - 2, z + 31, BLOCK_3);
        set(level, x + 27, y - 2, z + 31, BLOCK_3);
        set(level, x + 28, y - 2, z + 31, BLOCK_3);
        set(level, x + 29, y - 2, z + 31, BLOCK_3);
        set(level, x + 30, y - 2, z + 31, BLOCK_3);
        set(level, x + 31, y - 2, z + 31, BLOCK_3);
        set(level, x + 32, y - 2, z + 31, BLOCK_3);
        set(level, x + 33, y - 2, z + 31, BLOCK_3);
        set(level, x + 34, y - 2, z + 31, BLOCK_3);
        set(level, x + 35, y - 2, z + 31, BLOCK_3);
        set(level, x + 36, y - 2, z + 31, BLOCK_3);
        set(level, x + 37, y - 2, z + 31, BLOCK_3);
        set(level, x + 38, y - 2, z + 31, BLOCK_3);
        set(level, x + 39, y - 2, z + 31, BLOCK_3);
        set(level, x + 40, y - 2, z + 31, BLOCK_3);
        set(level, x, y - 2, z + 32, BLOCK_3);
        set(level, x + 16, y - 2, z + 32, BLOCK_3);
        set(level, x, y - 2, z + 33, BLOCK_3);
        set(level, x + 1, y - 2, z + 33, BLOCK_3);
        set(level, x + 2, y - 2, z + 33, BLOCK_3);
        set(level, x + 3, y - 2, z + 33, BLOCK_3);
        set(level, x + 4, y - 2, z + 33, BLOCK_3);
        set(level, x + 5, y - 2, z + 33, BLOCK_3);
        set(level, x + 6, y - 2, z + 33, BLOCK_3);
        set(level, x + 7, y - 2, z + 33, BLOCK_3);
        set(level, x + 8, y - 2, z + 33, BLOCK_3);
        set(level, x + 9, y - 2, z + 33, BLOCK_3);
        set(level, x + 10, y - 2, z + 33, BLOCK_3);
        set(level, x + 11, y - 2, z + 33, BLOCK_3);
        set(level, x + 12, y - 2, z + 33, BLOCK_3);
        set(level, x + 13, y - 2, z + 33, BLOCK_3);
        set(level, x + 14, y - 2, z + 33, BLOCK_3);
        set(level, x + 15, y - 2, z + 33, BLOCK_3);
        set(level, x + 16, y - 2, z + 33, BLOCK_3);
        set(level, x, y - 1, z, BLOCK_3);
        set(level, x + 1, y - 1, z, BLOCK_3);
        set(level, x + 2, y - 1, z, BLOCK_3);
        set(level, x + 3, y - 1, z, BLOCK_3);
        set(level, x + 4, y - 1, z, BLOCK_3);
        set(level, x + 5, y - 1, z, BLOCK_3);
        set(level, x + 6, y - 1, z, BLOCK_3);

        // === Вторая часть структуры ===
        new DesertAtomPart2().generate_r00(level, rand, x, y, z);

        // Проволоку разворачиваем, когда вся постройка уже стоит.
        fixWireRuns(level, x, y, z);
    }

    /**
     * Ставит колючую проволоку вдоль её ряда.
     *
     * <p>Проволока есть и на кольце, и внутри площадки — ряд вдоль площадки с
     * бочками. Раньше ориентация бралась по положению в кольце, и внутренние
     * ряды, идущие вдоль X, получали запад: панель повёрнута на 90°, владелец
     * 03.11.2026 — «вишу прямо над кусочком и смотрю вдоль него». Теперь
     * направление определяется соседями: ряд вдоль X — на север, вдоль Z — на
     * запад, одиночный блок без соседей остаётся на севере.</p>
     */
    private void fixWireRuns(WorldGenLevel level, int ox, int oy, int oz) {
        BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();
        int sizeX = com.hbm.datagen.worldgen.structure.pieces.AtomPiece.SIZE_X;
        int sizeZ = com.hbm.datagen.worldgen.structure.pieces.AtomPiece.SIZE_Z;
        int topY = com.hbm.datagen.worldgen.structure.pieces.AtomPiece.TOP_Y;

        for (int lx = 0; lx < sizeX; lx++) {
            for (int lz = 0; lz < sizeZ; lz++) {
                for (int ly = 0; ly <= topY; ly++) {
                    int wx = ox + lx;
                    int wy = oy + ly;
                    int wz = oz + lz;

                    cursor.set(wx, wy, wz);
                    BlockState state = level.getBlockState(cursor);
                    if (state.getBlock() != ModBlocks.BARBED_WIRE.get()) continue;
                    if (!state.hasProperty(BarbedWire.FACING)) continue;

                    boolean alongX = isWire(level, wx - 1, wy, wz) || isWire(level, wx + 1, wy, wz);
                    boolean alongZ = isWire(level, wx, wy, wz - 1) || isWire(level, wx, wy, wz + 1);

                    Direction facing;
                    if (alongX && !alongZ) {
                        facing = Direction.NORTH;
                    } else if (alongZ && !alongX) {
                        facing = Direction.WEST;
                    } else if (!alongX && !alongZ) {
                        // Одиночный блок на стыке двух рядов (соседи только по
                        // диагонали) — разворачиваем поперёк, иначе он стоит
                        // поперёк стыка (владелец, 03.11.2026).
                        boolean diagonal = isWire(level, wx - 1, wy, wz - 1) || isWire(level, wx - 1, wy, wz + 1)
                                || isWire(level, wx + 1, wy, wz - 1) || isWire(level, wx + 1, wy, wz + 1);
                        facing = diagonal ? Direction.WEST : Direction.NORTH;
                    } else {
                        // Т-образный узел: соседи по обеим осям, оставляем север.
                        facing = Direction.NORTH;
                    }

                    if (state.getValue(BarbedWire.FACING) != facing) {
                        level.setBlock(cursor, state.setValue(BarbedWire.FACING, facing), 2);
                    }
                }
            }
        }
    }

    private boolean isWire(WorldGenLevel level, int x, int y, int z) {
        return level.getBlockState(new BlockPos(x, y, z)).getBlock() == ModBlocks.BARBED_WIRE.get();
    }

    private void set(WorldGenLevel level, int x, int y, int z, BlockState state) {
        if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
            level.setBlock(new BlockPos(x, y, z), state, 2);
        }
    }

    private void setChest(WorldGenLevel level, RandomSource rand, int x, int y, int z,
                          Direction facing, String poolName, int rolls) {
        BlockPos pos = new BlockPos(x, y, z);
        set(level, x, y, z, Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, facing));
        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof ChestBlockEntity chest) {
            ItemPool pool = ItemPool.getPool(poolName);
            List<ItemStack> loot = pool.generate(rand, rolls);
            for (ItemStack stack : loot) {
                for (int slot = 0; slot < chest.getContainerSize(); slot++) {
                    if (chest.getItem(slot).isEmpty()) {
                        chest.setItem(slot, stack);
                        break;
                    }
                }
            }
        }
    }
}