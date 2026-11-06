package com.hbm.datagen.worldgen.structure.pieces;

import com.hbm.blocks.ModBlocks;
import com.hbm.datagen.worldgen.structure.ModStructurePieceTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import net.minecraft.world.level.levelgen.synth.NormalNoise;
import org.jetbrains.annotations.NotNull;

public class OreVeinPiece extends StructurePiece {

    public enum VeinType { ORE_CAVE, SCHIST }

    private VeinType type;
    private long seed;
    private int yLevel;
    private int maxRange;
    private double threshold;
    private double scale;

    // Блоки для OreCave
    private BlockState oreBlock;
    private BlockState fluidBlock;
    private BlockState stalactite;
    private BlockState stalagmite;

    // Конструктор для OreCave
    public OreVeinPiece(BoundingBox box, long seed, int yLevel, int maxRange, double threshold, double scale,
                        BlockState oreBlock, BlockState fluidBlock, BlockState stalactite, BlockState stalagmite) {
        super(ModStructurePieceTypes.ORE_VEIN_PIECE.get(), 0, box);
        this.type = VeinType.ORE_CAVE;
        this.seed = seed;
        this.yLevel = yLevel;
        this.maxRange = maxRange;
        this.threshold = threshold;
        this.scale = scale;
        this.oreBlock = oreBlock;
        this.fluidBlock = fluidBlock;
        this.stalactite = stalactite;
        this.stalagmite = stalagmite;
    }

    // Конструктор для Schist
    public OreVeinPiece(BoundingBox box, long seed, int yLevel, int maxRange, double threshold, double scale) {
        super(ModStructurePieceTypes.ORE_VEIN_PIECE.get(), 0, box);
        this.type = VeinType.SCHIST;
        this.seed = seed;
        this.yLevel = yLevel;
        this.maxRange = maxRange;
        this.threshold = threshold;
        this.scale = scale;
    }

    // Конструктор для NBT
    public OreVeinPiece(CompoundTag tag) {
        super(ModStructurePieceTypes.ORE_VEIN_PIECE.get(), tag);
        this.type = VeinType.valueOf(tag.getString("VeinType"));
        this.seed = tag.getLong("Seed");
        this.yLevel = tag.getInt("YLevel");
        this.maxRange = tag.getInt("MaxRange");
        this.threshold = tag.getDouble("Threshold");
        this.scale = tag.getDouble("Scale");

        if (type == VeinType.ORE_CAVE) {
            this.oreBlock = net.minecraft.nbt.NbtUtils.readBlockState(net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("OreBlock"));
            this.fluidBlock = net.minecraft.nbt.NbtUtils.readBlockState(net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("FluidBlock"));
            this.stalactite = net.minecraft.nbt.NbtUtils.readBlockState(net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("Stalactite"));
            this.stalagmite = net.minecraft.nbt.NbtUtils.readBlockState(net.minecraft.core.registries.BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("Stalagmite"));
        }
    }

    public OreVeinPiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(tag);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext ctx, @NotNull CompoundTag tag) {
        tag.putString("VeinType", type.name());
        tag.putLong("Seed", seed);
        tag.putInt("YLevel", yLevel);
        tag.putInt("MaxRange", maxRange);
        tag.putDouble("Threshold", threshold);
        tag.putDouble("Scale", scale);

        if (type == VeinType.ORE_CAVE) {
            tag.put("OreBlock", net.minecraft.nbt.NbtUtils.writeBlockState(oreBlock));
            tag.put("FluidBlock", net.minecraft.nbt.NbtUtils.writeBlockState(fluidBlock));
            tag.put("Stalactite", net.minecraft.nbt.NbtUtils.writeBlockState(stalactite));
            tag.put("Stalagmite", net.minecraft.nbt.NbtUtils.writeBlockState(stalagmite));
        }
    }

    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager manager, @NotNull ChunkGenerator generator, @NotNull RandomSource rand, @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {
        NormalNoise noise;
        if (type == VeinType.ORE_CAVE) {
            noise = NormalNoise.create(RandomSource.create(seed), -3, 1.0, 1.0);
        } else {
            noise = NormalNoise.create(RandomSource.create(seed), -3, 1.0, 1.0, 1.0, 1.0);
        }

        for (int x = box.minX(); x <= box.maxX(); x++) {
            for (int z = box.minZ(); z <= box.maxZ(); z++) {
                double n = noise.getValue(x * scale, 0, z * scale);

                if (n <= threshold) continue;

                int range = (int) ((n - threshold) * 3);
                if (range > maxRange) range = (maxRange * 2) - range;
                if (range < 0) continue;

                for (int y = yLevel - range; y <= yLevel + range; y++) {
                    if (!box.isInside(x, y, z)) continue;

                    BlockPos blockPos = new BlockPos(x, y, z);
                    if (!level.ensureCanWrite(blockPos)) continue;

                    if (type == VeinType.ORE_CAVE) {
                        processOreCave(level, rand, blockPos);
                    } else if (type == VeinType.SCHIST) {
                        processSchist(level, blockPos);
                    }
                }
            }
        }
    }

    private void processOreCave(WorldGenLevel level, RandomSource rand, BlockPos pos) {
        BlockState current = level.getBlockState(pos);

        if (current.is(BlockTags.STONE_ORE_REPLACEABLES)) {
            boolean nearAir = false;
            for (Direction dir : Direction.values()) {
                BlockState neighbor = level.getBlockState(pos.relative(dir));
                if (neighbor.isAir() || neighbor.is(stalactite.getBlock()) || neighbor.is(stalagmite.getBlock())) {
                    nearAir = true;
                    break;
                }
            }

            if (nearAir) {
                boolean fluidSet = false;
                if (!fluidBlock.isAir() && rand.nextBoolean()) {
                    BlockState up = level.getBlockState(pos.above());
                    BlockState down = level.getBlockState(pos.below());
                    if (up.isAir() && down.isSolid()) {
                        if (level.ensureCanWrite(pos) && level.ensureCanWrite(pos.below())) {
                            level.setBlock(pos, fluidBlock, 2);
                            level.setBlock(pos.below(), oreBlock, 2);
                            for (Direction dir : Direction.Plane.HORIZONTAL) {
                                BlockPos side = pos.relative(dir);
                                if (level.getBlockState(side).isSolid() && level.ensureCanWrite(side)) {
                                    level.setBlock(side, oreBlock, 2);
                                }
                            }
                        }
                        fluidSet = true;
                    }
                }

                if (!fluidSet && level.ensureCanWrite(pos)) {
                    level.setBlock(pos, oreBlock, 2);
                }
            }
        } else if (current.isAir() && rand.nextInt(5) == 0) {
            if (level.getBlockState(pos.above()).isSolid()) {
                if (level.ensureCanWrite(pos)) level.setBlock(pos, stalactite, 2);
            } else if (level.getBlockState(pos.below()).isSolid()) {
                if (level.ensureCanWrite(pos)) level.setBlock(pos, stalagmite, 2);
            }
        }
    }

    private void processSchist(WorldGenLevel level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        if (state.is(BlockTags.BASE_STONE_OVERWORLD) && level.ensureCanWrite(pos)) {
            level.setBlock(pos, ModBlocks.STONE_GNEISS.get().defaultBlockState(), 2);
        }
    }
}