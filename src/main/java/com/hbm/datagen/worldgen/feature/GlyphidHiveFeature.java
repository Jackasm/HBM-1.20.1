package com.hbm.datagen.worldgen.feature;

import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockGlyphid.GlyphidType;
import com.hbm.blocks.generic.BlockGlyphid.TileEntityGlyphid;
import com.hbm.config.MobConfig;
import com.hbm.datagen.worldgen.feature.config.GlyphidHiveConfiguration;
import com.hbm.itempool.LootGenerator;
import com.hbm.tileentity.deco.TileEntityLoot;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;
import org.jetbrains.annotations.NotNull;

public class GlyphidHiveFeature extends Feature<GlyphidHiveConfiguration> {

    public static final int[][][] SCHEMATIC_SMALL = new int[][][] {
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,1,1,1,1,1,0,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,0,1,1,1,1,1,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,1,1,1,3,3,3,1,1,1,0},
                    {0,1,1,1,3,3,3,1,1,1,0},
                    {0,1,1,1,3,3,3,1,1,1,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,2,2,2,1,1,0,0},
                    {0,1,1,2,2,2,2,2,1,1,0},
                    {0,1,1,2,2,2,2,2,1,1,0},
                    {0,1,1,2,2,2,2,2,1,1,0},
                    {0,0,1,1,2,2,2,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            },
            {
                    {0,0,0,0,0,0,0,0,0,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,1,1,1,1,1,1,1,1,1,0},
                    {0,1,1,1,1,1,1,1,1,1,0},
                    {0,1,1,1,1,1,1,1,1,1,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,1,1,1,1,1,1,1,0,0},
                    {0,0,0,0,1,1,1,0,0,0,0},
                    {0,0,0,0,0,0,0,0,0,0,0},
            }
    };

    public GlyphidHiveFeature(com.mojang.serialization.Codec<GlyphidHiveConfiguration> codec) {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<GlyphidHiveConfiguration> context) {
        if (!MobConfig.enableHives) return false;

        WorldGenLevel level = context.level();
        BlockPos origin = context.origin();
        RandomSource rand = context.random();
        GlyphidHiveConfiguration cfg = context.config();

        boolean infected = cfg.infected();
        boolean loot = cfg.loot();

        int x = origin.getX();
        int z = origin.getZ();

        // Находим поверхность (WORLD_SURFACE_WG уже дал её в origin.getY())
        int surfaceY = origin.getY();

        // Проверка: на поверхности не вода
        BlockPos surfacePos = new BlockPos(x, surfaceY, z);
        if (!level.getFluidState(surfacePos).isEmpty()) return false;

        // Проверка: под поверхностью solid
        BlockState below = level.getBlockState(surfacePos.below());
        if (!below.isSolid()) return false;

        // Улей ставится на грунт: схема занимает y-2..y+2, а поверхность (WORLD_SURFACE_WG)
        // отдаёт первый воздух над землёй. Раньше здесь было +3 — улей висел на блок в воздухе.
        int y = surfaceY + 2;

        // Проверка: 5 блоков вверх свободны
        for (int dy = 0; dy < 5; dy++) {
            for (int dx = -5; dx <= 5; dx++) {
                for (int dz = -5; dz <= 5; dz++) {
                    BlockPos pos = new BlockPos(x + dx, y - 2 + dy, z + dz);
                    BlockState current = level.getBlockState(pos);

                    if (!current.getFluidState().isEmpty()) return false;
                    if (!current.isAir() && !current.canBeReplaced()) return false;
                }
            }
        }

        GlyphidType glyphidType = infected ? GlyphidType.INFESTED : GlyphidType.STANDARD;

        for (int i = 0; i < 11; i++) {
            for (int j = 0; j < 5; j++) {
                for (int k = 0; k < 11; k++) {
                    int block = SCHEMATIC_SMALL[4 - j][i][k];
                    int iX = x + i - 5;
                    int iY = y + j - 2;
                    int iZ = z + k - 5;
                    BlockPos pos = new BlockPos(iX, iY, iZ);

                    switch (block) {
                        case 1 -> placeGlyphid(level, pos, glyphidType);
                        case 2 -> {
                            if (rand.nextInt(3) == 0) {
                                level.setBlock(pos,
                                        ModBlocks.GLYPHID_SPAWNER.get().defaultBlockState(), 2);
                            } else {
                                placeGlyphid(level, pos, glyphidType);
                            }
                        }
                        case 3 -> {
                            int r = rand.nextInt(3);
                            if (r == 0) {
                                level.setBlock(pos, Blocks.SKELETON_SKULL.defaultBlockState()
                                        .setValue(SkullBlock.ROTATION, rand.nextInt(16)), 3);
                            } else if (r == 1) {
                                level.setBlock(pos,
                                        ModBlocks.DECO_LOOT.get().defaultBlockState(), 2);
                                if (level.getBlockEntity(pos) instanceof TileEntityLoot lootTE) {
                                    LootGenerator.lootBones(lootTE, rand);
                                }
                            } else {
                                if (loot) {
                                    level.setBlock(pos,
                                            ModBlocks.DECO_LOOT.get().defaultBlockState(), 2);
                                    if (level.getBlockEntity(pos) instanceof TileEntityLoot lootTE) {
                                        LootGenerator.lootGlyphidHive(lootTE, rand);
                                    }
                                } else {
                                    placeGlyphid(level, pos, glyphidType);
                                }
                            }
                        }
                    }
                }
            }
        }
        return true;
    }

    /** Ставит GLYPHID_BASE и настраивает TE type. */
    private void placeGlyphid(WorldGenLevel level, BlockPos pos, GlyphidType type) {
        level.setBlock(pos, ModBlocks.GLYPHID_BASE.get().defaultBlockState(), 2);
        if (level.getBlockEntity(pos) instanceof TileEntityGlyphid glyphid) {
            glyphid.type = type;
            glyphid.setChanged();
        }
    }
}