package com.hbm.datagen.worldgen.structure;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;

public abstract class BaseRuinGenerator {

    protected WorldGenLevel level;
    protected RandomSource rand;
    protected int originX;
    protected int originY;
    protected int originZ;

    protected static final BlockState CONCRETE = ModBlocks.BRICK_CONCRETE.get().defaultBlockState();
    protected static final BlockState CONCRETE_CRACKED = ModBlocks.BRICK_CONCRETE_CRACKED.get().defaultBlockState();
    protected static final BlockState CONCRETE_BROKEN = ModBlocks.BRICK_CONCRETE_BROKEN.get().defaultBlockState();

    protected static final BlockState DECO_STEEL = ModBlocks.DECO_STEEL.get().defaultBlockState();
    protected static final BlockState STEEL_WALL = ModBlocks.STEEL_WALL.get().defaultBlockState();
    protected static final BlockState RED_CABLE = ModBlocks.RED_CABLE.get().defaultBlockState();
    protected static final BlockState CABLE_SWITCH = ModBlocks.CABLE_SWITCH.get().defaultBlockState();
    protected static final BlockState MACHINE_SHREDDER = ModBlocks.MACHINE_SHREDDER.get().defaultBlockState();
    protected static final BlockState CRASHED_BALEFIRE = ModBlocks.CRASHED_BALEFIRE.get().defaultBlockState();

    protected static final BlockState STONE_STAIRS = Blocks.STONE_STAIRS.defaultBlockState();
    protected static final BlockState COBBLESTONE = Blocks.COBBLESTONE.defaultBlockState();
    protected static final BlockState MOSSY_COBBLESTONE = Blocks.MOSSY_COBBLESTONE.defaultBlockState();
    protected static final BlockState STONE_SLAB = Blocks.STONE_SLAB.defaultBlockState();
    protected static final BlockState DIRT = Blocks.DIRT.defaultBlockState();
    protected static final BlockState GRASS = Blocks.GRASS_BLOCK.defaultBlockState();
    protected static final BlockState SPRUCE_STAIRS = Blocks.SPRUCE_STAIRS.defaultBlockState();
    protected static final BlockState SPRUCE_PLANKS = Blocks.SPRUCE_PLANKS.defaultBlockState();
    protected static final BlockState SPRUCE_SLAB = Blocks.SPRUCE_SLAB.defaultBlockState();
    protected static final BlockState SPRUCE_DOOR = Blocks.SPRUCE_DOOR.defaultBlockState();
    protected static final BlockState OAK_FENCE = Blocks.OAK_FENCE.defaultBlockState();
    protected static final BlockState WHITE_WOOL = Blocks.WHITE_WOOL.defaultBlockState();
    protected static final BlockState CAULDRON = Blocks.CAULDRON.defaultBlockState();
    protected static final BlockState LADDER = Blocks.LADDER.defaultBlockState();
    protected static final BlockState TORCH = Blocks.TORCH.defaultBlockState();
    protected static final BlockState STONE_BUTTON = Blocks.STONE_BUTTON.defaultBlockState();
    protected static final BlockState LEVER = Blocks.LEVER.defaultBlockState();
    protected static final BlockState LIT_REDSTONE_LAMP = Blocks.REDSTONE_LAMP.defaultBlockState(); // lit state
    protected static final BlockState DIAMOND_BLOCK = Blocks.DIAMOND_BLOCK.defaultBlockState();
    protected static final BlockState IRON_BLOCK = Blocks.IRON_BLOCK.defaultBlockState();
    protected static final BlockState NETHERRACK = Blocks.NETHERRACK.defaultBlockState();
    protected static final BlockState STAINED_GLASS = Blocks.GLASS.defaultBlockState();

    protected void setBlock(int x, int y, int z, BlockState state) {
        if (y >= level.getMinBuildHeight() && y < level.getMaxBuildHeight()) {
            level.setBlock(new BlockPos(x, y, z), state, 3);
        }
    }

    protected void setBlock(BlockPos pos, BlockState state) {
        if (pos.getY() >= level.getMinBuildHeight() && pos.getY() < level.getMaxBuildHeight()) {
            level.setBlock(pos, state, 3);
        }
    }

    protected boolean isAir(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return level.getBlockState(pos).isAir();
    }

    protected boolean isSolid(int x, int y, int z) {
        BlockPos pos = new BlockPos(x, y, z);
        return level.getBlockState(pos).isSolid();
    }

    protected int getSurfaceY(int x, int z) {
        return level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
    }
}
