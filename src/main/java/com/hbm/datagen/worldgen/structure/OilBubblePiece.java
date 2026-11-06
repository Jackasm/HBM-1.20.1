package com.hbm.datagen.worldgen.structure;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceSerializationContext;
import org.jetbrains.annotations.NotNull;

/**
 * Одна «пузырьковая» залежь: эллипсоид (сплюснут по Y в 3 раза), заполняющий целевой блок
 * и рисующий характерные поверхностные повреждения (oil spot + дыры), как в 1.7.10 MapGenBubble.
 */
public class OilBubblePiece extends StructurePiece {

    private final int radius;
    private final BlockPos centerPos;
    private final BlockState targetBlock;
    private final BlockState placeBlock;
    private final boolean fuzzy;
    private final boolean isGas;
    private final boolean isSand;

    public OilBubblePiece(BoundingBox box, BlockPos centerPos, int radius, BlockState targetBlock,
                          BlockState placeBlock, boolean fuzzy, boolean isGas, boolean isSand) {
        super(ModStructurePieceTypes.OIL_BUBBLE_PIECE.get(), 0, box);
        this.centerPos = centerPos;
        this.radius = radius;
        this.targetBlock = targetBlock;
        this.placeBlock = placeBlock;
        this.fuzzy = fuzzy;
        this.isGas = isGas;
        this.isSand = isSand;
    }

    public OilBubblePiece(CompoundTag tag) {
        super(ModStructurePieceTypes.OIL_BUBBLE_PIECE.get(), tag);
        this.centerPos = new BlockPos(tag.getInt("CenterX"), tag.getInt("CenterY"), tag.getInt("CenterZ"));
        this.radius = tag.getInt("Radius");
        this.targetBlock = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("TargetBlock"));
        this.placeBlock = NbtUtils.readBlockState(BuiltInRegistries.BLOCK.asLookup(), tag.getCompound("PlaceBlock"));
        this.fuzzy = tag.getBoolean("Fuzzy");
        this.isGas = tag.getBoolean("IsGas");
        this.isSand = tag.getBoolean("IsSand");
    }

    public OilBubblePiece(StructurePieceSerializationContext context, CompoundTag tag) {
        this(tag);
    }

    @Override
    protected void addAdditionalSaveData(@NotNull StructurePieceSerializationContext ctx, @NotNull CompoundTag tag) {
        tag.putInt("CenterX", centerPos.getX());
        tag.putInt("CenterY", centerPos.getY());
        tag.putInt("CenterZ", centerPos.getZ());
        tag.putInt("Radius", radius);
        tag.put("TargetBlock", NbtUtils.writeBlockState(targetBlock));
        tag.put("PlaceBlock", NbtUtils.writeBlockState(placeBlock));
        tag.putBoolean("Fuzzy", fuzzy);
        tag.putBoolean("IsGas", isGas);
        tag.putBoolean("IsSand", isSand);
    }

    @Override
    public void postProcess(@NotNull WorldGenLevel level, @NotNull StructureManager manager,
                            @NotNull ChunkGenerator generator, @NotNull RandomSource rand,
                            @NotNull BoundingBox box, @NotNull ChunkPos chunkPos, @NotNull BlockPos pos) {

        // Пересечение собственного бокса с боксом текущего чанка (в 1.20.1 нет BoundingBox.intersect)
        int minX = Math.max(this.boundingBox.minX(), box.minX());
        int minY = Math.max(this.boundingBox.minY(), box.minY());
        int minZ = Math.max(this.boundingBox.minZ(), box.minZ());
        int maxX = Math.min(this.boundingBox.maxX(), box.maxX());
        int maxY = Math.min(this.boundingBox.maxY(), box.maxY());
        int maxZ = Math.min(this.boundingBox.maxZ(), box.maxZ());

        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return;
        }

        BoundingBox chunkIntersection = new BoundingBox(minX, minY, minZ, maxX, maxY, maxZ);

        double radiusSqr = (radius * radius) / 2.0;

        for (int x = chunkIntersection.minX(); x <= chunkIntersection.maxX(); x++) {
            for (int z = chunkIntersection.minZ(); z <= chunkIntersection.maxZ(); z++) {
                for (int y = chunkIntersection.minY(); y <= chunkIntersection.maxY(); y++) {
                    double dx = x - centerPos.getX();
                    double dz = z - centerPos.getZ();
                    double dy = y - centerPos.getY();
                    double rSqr = dx * dx + dz * dz + dy * dy * 3.0;

                    if (fuzzy) {
                        rSqr -= rand.nextDouble() * radiusSqr / 3.0;
                    }

                    if (rSqr < radiusSqr) {
                        BlockPos blockPos = new BlockPos(x, y, z);
                        if (level.getBlockState(blockPos).is(targetBlock.getBlock()) && level.ensureCanWrite(blockPos)) {
                            level.setBlock(blockPos, placeBlock, 2);
                        }
                    }
                }
            }
        }

        RandomSource spotRand = RandomSource.create(centerPos.asLong() ^ 0x5DEECE66DL);
        generateSurfaceDamage(level, spotRand, chunkIntersection);
    }

    private void generateSurfaceDamage(WorldGenLevel level, RandomSource rand, BoundingBox chunkBox) {
        int spotCount = 150;
        int spotWidth = 7;

        for (int i = 0; i < spotCount; i++) {
            int offX = (int) (rand.nextGaussian() * spotWidth);
            int offZ = (int) (rand.nextGaussian() * spotWidth);
            int x = centerPos.getX() + offX;
            int z = centerPos.getZ() + offZ;

            if (x < chunkBox.minX() || x > chunkBox.maxX() || z < chunkBox.minZ() || z > chunkBox.maxZ()) {
                continue;
            }

            int y = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, x, z);
            while (y > level.getMinBuildHeight()) {
                BlockPos checkPos = new BlockPos(x, y, z);
                BlockState state = level.getBlockState(checkPos);
                if (state.isAir() || !state.getFluidState().isEmpty()) {
                    y--;
                } else {
                    break;
                }
            }
            if (y <= level.getMinBuildHeight()) continue;

            BlockPos pos = new BlockPos(x, y, z);
            BlockState state = level.getBlockState(pos);
            if (!state.isSolidRender(level, pos)) continue;

            if (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.DIRT)) {
                int distSq = offX * offX + offZ * offZ;
                boolean inner = distSq < (spotWidth / 2) * (spotWidth / 2);
                if (level.ensureCanWrite(pos)) {
                    level.setBlock(pos, inner ? ModBlocks.DIRT_OILY.get().defaultBlockState()
                            : ModBlocks.DIRT_DEAD.get().defaultBlockState(), 2);
                }

                if (!inner && rand.nextInt(20) == 0) {
                    BlockPos above = pos.above();
                    if (level.getBlockState(above).isAir() && level.ensureCanWrite(above)) {
                        // 1.7.10: rand.nextInt(EnumDeadPlantType.values().length) — все 5 подтипов
                        BlockState plant = switch (rand.nextInt(5)) {
                            case 0 -> ModBlocks.PLANT_DEAD_GENERIC.get().defaultBlockState();
                            case 1 -> ModBlocks.PLANT_DEAD_GRASS.get().defaultBlockState();
                            case 2 -> ModBlocks.PLANT_DEAD_FLOWER.get().defaultBlockState();
                            case 3 -> ModBlocks.PLANT_DEAD_BIGFLOWER.get().defaultBlockState();
                            default -> ModBlocks.PLANT_DEAD_FERN.get().defaultBlockState();
                        };
                        level.setBlock(above, plant, 2);
                    }
                }
            } else if (state.is(Blocks.SAND)) {
                if (level.ensureCanWrite(pos)) {
                    level.setBlock(pos, ModBlocks.SAND_DIRTY.get().defaultBlockState(), 2);
                }
            } else if (state.is(ModBlocks.ORE_OIL_SAND.get())) {
                if (level.ensureCanWrite(pos)) {
                    level.setBlock(pos, ModBlocks.SAND_DIRTY_RED.get().defaultBlockState(), 2);
                }
            } else if (state.is(Blocks.STONE)) {
                if (level.ensureCanWrite(pos)) {
                    level.setBlock(pos, ModBlocks.STONE_CRACKED.get().defaultBlockState(), 2);
                }
            }
        }

        // Пять «дыр»: центр + 4 горизонтальных направления (как ForgeDirection 1..5 в 1.7.10)
        for (int i = 1; i <= 5; i++) {
            int dx = 0, dz = 0;
            switch (i) {
                case 1 -> { /* UP — центральная колонна */ }
                case 2 -> dz = -1; // NORTH
                case 3 -> dz = 1;  // SOUTH
                case 4 -> dx = -1; // WEST
                default -> dx = 1; // EAST
            }

            int holeX = centerPos.getX() + dx;
            int holeZ = centerPos.getZ() + dz;

            if (holeX < chunkBox.minX() || holeX > chunkBox.maxX() || holeZ < chunkBox.minZ() || holeZ > chunkBox.maxZ()) {
                continue;
            }

            int surfaceY = level.getHeight(Heightmap.Types.WORLD_SURFACE_WG, holeX, holeZ);
            int solids = 0;
            for (int y = surfaceY; y > surfaceY - 10; y--) {
                BlockPos pos = new BlockPos(holeX, y, holeZ);
                BlockState state = level.getBlockState(pos);
                if (state.isAir() || !state.getFluidState().isEmpty()) break;

                if (state.isSolidRender(level, pos)) {
                    solids++;
                    if (!level.ensureCanWrite(pos)) continue;

                    if (i == 1) {
                        if (solids < 3) {
                            level.setBlock(pos, Blocks.AIR.defaultBlockState(), 2);
                        } else if (solids == 3) {
                            level.setBlock(pos, ModBlocks.OIL_SPILL.get().defaultBlockState(), 2);
                        } else if (solids < 7) {
                            level.setBlock(pos, ModBlocks.STONE_CRACKED.get().defaultBlockState(), 2);
                        } else {
                            break;
                        }
                    } else {
                        level.setBlock(pos, ModBlocks.STONE_CRACKED.get().defaultBlockState(), 2);
                        if (solids >= 4) break;
                    }
                }
            }
        }
    }
}
