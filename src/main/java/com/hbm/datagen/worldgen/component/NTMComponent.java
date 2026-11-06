package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ModBlocks;
import com.hbm.blocks.generic.BlockBobble;
import com.hbm.config.StructureConfig;
import com.hbm.itempool.ItemPool;
import com.hbm.itempool.LootGenerator;
import com.hbm.tileentity.TileEntityLockableBase;
import com.hbm.tileentity.deco.TileEntityBobble;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.LeverBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.TrapDoorBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BedPart;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Порт Component из 1.7.10. Никаких int-метаданных — только Direction и BlockState.
 * coordBaseMode: 0=South, 1=West, 2=North, 3=East.
 */
public abstract class NTMComponent {

    protected int coordBaseMode;
    protected BoundingBox boundingBox;
    protected int hpos = -1;
    protected boolean heightCalculated = false;
    protected int componentType = 0;
    public long generationSeed = 0;

    public NTMComponent() { }

    public abstract boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box);

    // ==================== NBT ====================

    public void writeNBT(CompoundTag nbt) {
        nbt.putInt("HPos", hpos);
        nbt.putLong("genSeed", generationSeed);
        nbt.putInt("CoordBaseMode", coordBaseMode);
        nbt.putBoolean("HeightCalculated", heightCalculated);
    }

    public void readNBT(CompoundTag nbt) {
        hpos = nbt.getInt("HPos");
        generationSeed = nbt.getLong("genSeed");
        coordBaseMode = nbt.getInt("CoordBaseMode");
        heightCalculated = nbt.getBoolean("HeightCalculated");
    }

    // ==================== Accessors ====================

    public int getCoordMode() { return coordBaseMode; }
    public BoundingBox getBoundingBox() { return boundingBox; }
    public void setBoundingBox(BoundingBox box) { this.boundingBox = box; }
    public void setCoordBaseMode(int mode) { this.coordBaseMode = mode; }
    public int getHpos() { return hpos; }
    public void setHpos(int h) { this.hpos = h; }
    public int getComponentType() { return componentType; }
    public abstract String getTypeName();
    public void setComponentType(int t) { this.componentType = t; }
    public void skipHeightCalculation() { this.heightCalculated = true; }

    // ==================== Координатные трансформы ====================

    public int getXWithOffset(int x, int z) {
        return switch (coordBaseMode) {
            case 0 -> boundingBox.minX() + x;
            case 1 -> boundingBox.maxX() - z;
            case 2 -> boundingBox.maxX() - x;
            case 3 -> boundingBox.minX() + z;
            default -> x;
        };
    }

    public int getZWithOffset(int x, int z) {
        return switch (coordBaseMode) {
            case 0 -> boundingBox.minZ() + z;
            case 1 -> boundingBox.minZ() + x;
            case 2 -> boundingBox.maxZ() - z;
            case 3 -> boundingBox.maxZ() - x;
            default -> z;
        };
    }

    public int getYWithOffset(int y) { return boundingBox.minY() + y; }

    // ==================== Поворот Direction ====================

    public Direction rotate(Direction local) {
        if (local.getAxis().isVertical()) return local;
        return switch (coordBaseMode) {
            default -> local;                       // South
            case 1 -> local.getClockWise();         // West
            case 2 -> local.getOpposite();          // North
            case 3 -> local.getCounterClockWise();  // East
        };
    }

    /** Поворот оси для RotatedPillarBlock. */
    public Direction.Axis rotateAxis(Direction.Axis local) {
        if (coordBaseMode % 2 != 0) {
            if (local == Direction.Axis.X) return Direction.Axis.Z;
            if (local == Direction.Axis.Z) return Direction.Axis.X;
        }
        return local;
    }

    // ==================== Рельеф ====================

    protected boolean setAverageHeight(WorldGenLevel level, BoundingBox box, int y) {
        int total = 0;
        int iterations = 0;
        for (int z = boundingBox.minZ(); z <= boundingBox.maxZ(); z++) {
            for (int x = boundingBox.minX(); x <= boundingBox.maxX(); x++) {
                if (box.isInside(x, y, z)) {
                    total += Math.max(level.getHeight(
                            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, x, z), 62);
                    iterations++;
                }
            }
        }
        if (iterations == 0) return false;
        this.hpos = total / iterations;
        this.boundingBox = this.boundingBox.move(0, this.hpos - this.boundingBox.minY(), 0);
        return true;
    }

    protected static int getAverageHeight(WorldGenLevel level, BoundingBox area) {
        int total = 0;
        int iterations = 0;
        for (int z = area.minZ(); z <= area.maxZ(); z++) {
            for (int x = area.minX(); x <= area.maxX(); x++) {
                total += Math.max(level.getHeight(
                        net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, x, z), 62);
                iterations++;
            }
        }
        return iterations == 0 ? -1 : total / iterations;
    }

    // ==================== Хелперы блоков ====================

    protected void placeStairs(WorldGenLevel level, BoundingBox box, Block block,
                               Direction localFacing, boolean top, int x, int y, int z) {
        Direction facing = rotate(localFacing);
        placeBlockAtCurrentPosition(level,
                block.defaultBlockState()
                        .setValue(StairBlock.FACING, facing)
                        .setValue(StairBlock.HALF, top ? Half.TOP : Half.BOTTOM),
                x, y, z, box);
    }

    protected void placeDoor(WorldGenLevel level, BoundingBox box, Block door,
                             Direction localFacing, boolean opensRight, boolean isOpen,
                             int featureX, int featureY, int featureZ) {
        int posX = getXWithOffset(featureX, featureZ);
        int posY = getYWithOffset(featureY);
        int posZ = getZWithOffset(featureX, featureZ);
        if (!box.isInside(posX, posY, posZ)) return;

        Direction facing = rotate(localFacing);

        BlockState base = door.defaultBlockState()
                .setValue(DoorBlock.FACING, facing)
                .setValue(DoorBlock.OPEN, isOpen)
                .setValue(DoorBlock.POWERED, false)
                .setValue(DoorBlock.HINGE, opensRight ? DoorHingeSide.RIGHT : DoorHingeSide.LEFT);

        BlockPos below = new BlockPos(posX, posY - 1, posZ);
        if (!level.getBlockState(below).isAir()) {
            level.setBlock(new BlockPos(posX, posY, posZ), base.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 2);
            level.setBlock(new BlockPos(posX, posY + 1, posZ), base.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 2);
        }
    }

    protected void placeLever(WorldGenLevel level, BoundingBox box, Direction localFacing, AttachFace face, boolean on,
                              int featureX, int featureY, int featureZ) {
        int posX = getXWithOffset(featureX, featureZ);
        int posY = getYWithOffset(featureY);
        int posZ = getZWithOffset(featureX, featureZ);
        if (!box.isInside(posX, posY, posZ)) return;

        Direction facing = rotate(localFacing);
        BlockState state = Blocks.LEVER.defaultBlockState()
                .setValue(LeverBlock.FACE, face)
                .setValue(LeverBlock.FACING, facing)
                .setValue(LeverBlock.POWERED, on);
        level.setBlock(new BlockPos(posX, posY, posZ), state, 2);
    }

    protected void placeBed(WorldGenLevel level, BoundingBox box, Direction localFacing,
                            int featureX, int featureY, int featureZ) {
        Direction facing = rotate(localFacing);

        int posX = getXWithOffset(featureX, featureZ);
        int posY = getYWithOffset(featureY);
        int posZ = getZWithOffset(featureX, featureZ);
        if (!box.isInside(posX, posY, posZ)) return;

        BlockState base = Blocks.RED_BED.defaultBlockState().setValue(BedBlock.FACING, facing);
        BlockPos footPos = new BlockPos(posX, posY, posZ);
        BlockPos headPos = footPos.relative(facing);

        if (box.isInside(headPos.getX(), headPos.getY(), headPos.getZ())) {
            level.setBlock(footPos, base.setValue(BedBlock.PART, BedPart.FOOT).setValue(BedBlock.OCCUPIED, false), 2);
            level.setBlock(headPos, base.setValue(BedBlock.PART, BedPart.HEAD).setValue(BedBlock.OCCUPIED, false), 2);
        }
    }

    protected void placeTrapdoor(WorldGenLevel level, BoundingBox box, Block block, Direction localFacing,
                                 boolean top, boolean open, int x, int y, int z) {
        Direction facing = rotate(localFacing);
        placeBlockAtCurrentPosition(level,
                block.defaultBlockState()
                        .setValue(TrapDoorBlock.FACING, facing)
                        .setValue(TrapDoorBlock.HALF, top ? Half.TOP : Half.BOTTOM)
                        .setValue(TrapDoorBlock.OPEN, open)
                        .setValue(TrapDoorBlock.POWERED, false),
                x, y, z, box);
    }

    protected void placePillar(WorldGenLevel level, BoundingBox box, Block block, Direction.Axis localAxis,
                               int x, int y, int z) {
        Direction.Axis axis = rotateAxis(localAxis);
        placeBlockAtCurrentPosition(level,
                block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis),
                x, y, z, box);
    }

    protected void fillWithPillars(WorldGenLevel level, BoundingBox box, Block block, Direction.Axis localAxis,
                                   int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Direction.Axis axis = rotateAxis(localAxis);
        BlockState state = block.defaultBlockState().setValue(RotatedPillarBlock.AXIS, axis);
        fillWithBlocks(level, box, minX, minY, minZ, maxX, maxY, maxZ, state);
    }

    /** Универсальный deco: ставит FACING, если блок его поддерживает. */
    protected void placeDeco(WorldGenLevel level, BoundingBox box, Block block, Direction localFacing,
                             int x, int y, int z) {
        placeBlockAtCurrentPosition(level, applyFacing(block.defaultBlockState(), rotate(localFacing)),
                x, y, z, box);
    }

    protected BlockState applyFacing(BlockState state, Direction worldFacing) {
        for (var prop : state.getProperties()) {
            if (prop.getName().equals("facing") && prop.getValueClass() == Direction.class) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                BlockState s = state.setValue((net.minecraft.world.level.block.state.properties.Property) prop, worldFacing);
                return s;
            }
        }
        return state;
    }

    protected void fillWithLadders(WorldGenLevel level, BoundingBox box, Block ladder, Direction localFacing,
                                   int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Direction facing = rotate(localFacing);
        BlockState state = ladder.defaultBlockState().setValue(LadderBlock.FACING, facing);
        fillWithBlocks(level, box, minX, minY, minZ, maxX, maxY, maxZ, state);
    }

    protected void fillWithStairs(WorldGenLevel level, BoundingBox box, Block block, Direction localFacing,
                                  boolean top, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Direction facing = rotate(localFacing);
        BlockState state = block.defaultBlockState()
                .setValue(StairBlock.FACING, facing)
                .setValue(StairBlock.HALF, top ? Half.TOP : Half.BOTTOM);
        fillWithBlocks(level, box, minX, minY, minZ, maxX, maxY, maxZ, state);
    }

    protected void fillWithDeco(WorldGenLevel level, BoundingBox box, Block block, Direction localFacing,
                                int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        BlockState state = applyFacing(block.defaultBlockState(), rotate(localFacing));
        fillWithBlocks(level, box, minX, minY, minZ, maxX, maxY, maxZ, state);
    }

    // ==================== Заполнение объёмов ====================

    protected void placeBlockAtCurrentPosition(WorldGenLevel level, BlockState state, int x, int y, int z, BoundingBox box) {
        int posX = getXWithOffset(x, z);
        int posY = getYWithOffset(y);
        int posZ = getZWithOffset(x, z);
        if (box.isInside(posX, posY, posZ)) {
            level.setBlock(new BlockPos(posX, posY, posZ), state, 2);
        }
    }

    protected void fillWithAir(WorldGenLevel level, BoundingBox box, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (getYWithOffset(minY) < box.minY() || getYWithOffset(maxY) > box.maxY()) return;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int posX = getXWithOffset(x, z);
                int posZ = getZWithOffset(x, z);
                if (posX < box.minX() || posX > box.maxX() || posZ < box.minZ() || posZ > box.maxZ()) continue;
                for (int y = minY; y <= maxY; y++) {
                    level.setBlock(new BlockPos(posX, getYWithOffset(y), posZ), Blocks.AIR.defaultBlockState(), 2);
                }
            }
        }
    }

    protected void fillWithBlocks(WorldGenLevel level, BoundingBox box, int minX, int minY, int minZ,
                                  int maxX, int maxY, int maxZ, BlockState state) {
        if (getYWithOffset(minY) < box.minY() || getYWithOffset(maxY) > box.maxY()) return;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int posX = getXWithOffset(x, z);
                int posZ = getZWithOffset(x, z);
                if (posX < box.minX() || posX > box.maxX() || posZ < box.minZ() || posZ > box.maxZ()) continue;
                for (int y = minY; y <= maxY; y++) {
                    level.setBlock(new BlockPos(posX, getYWithOffset(y), posZ), state, 2);
                }
            }
        }
    }

    protected void fillWithRandomizedBlocks(WorldGenLevel level, BoundingBox box, int minX, int minY, int minZ,
                                            int maxX, int maxY, int maxZ, RandomSource rand, BlockSelector selector) {
        if (getYWithOffset(minY) < box.minY() || getYWithOffset(maxY) > box.maxY()) return;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int posX = getXWithOffset(x, z);
                int posZ = getZWithOffset(x, z);
                if (posX < box.minX() || posX > box.maxX() || posZ < box.minZ() || posZ > box.maxZ()) continue;
                for (int y = minY; y <= maxY; y++) {
                    int posY = getYWithOffset(y);
                    selector.selectBlocks(rand, posX, posY, posZ, false);
                    level.setBlock(new BlockPos(posX, posY, posZ), selector.getState(), 2);
                }
            }
        }
    }

    protected void randomlyFillWithBlocks(WorldGenLevel level, BoundingBox box, RandomSource rand, float randLimit,
                                          int minX, int minY, int minZ, int maxX, int maxY, int maxZ, BlockState state) {
        if (getYWithOffset(minY) < box.minY() || getYWithOffset(maxY) > box.maxY()) return;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int posX = getXWithOffset(x, z);
                int posZ = getZWithOffset(x, z);
                if (posX < box.minX() || posX > box.maxX() || posZ < box.minZ() || posZ > box.maxZ()) continue;
                for (int y = minY; y <= maxY; y++) {
                    if (rand.nextFloat() <= randLimit) {
                        level.setBlock(new BlockPos(posX, getYWithOffset(y), posZ), state, 2);
                    }
                }
            }
        }
    }

    protected void randomlyFillWithBlocks(WorldGenLevel level, BoundingBox box, RandomSource rand, float randLimit,
                                          int minX, int minY, int minZ, int maxX, int maxY, int maxZ,
                                          BlockState state, BlockState replace, boolean onlyReplace) {
        if (getYWithOffset(minY) < box.minY() || getYWithOffset(maxY) > box.maxY()) return;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int posX = getXWithOffset(x, z);
                int posZ = getZWithOffset(x, z);
                if (posX < box.minX() || posX > box.maxX() || posZ < box.minZ() || posZ > box.maxZ()) continue;
                for (int y = minY; y <= maxY; y++) {
                    if (rand.nextFloat() <= randLimit) {
                        int posY = getYWithOffset(y);
                        if (!onlyReplace || !level.getBlockState(new BlockPos(posX, posY, posZ)).isAir()) {
                            boolean edge = x == minX || x == maxX || y == minY || y == maxY || z == minZ || z == maxZ;
                            level.setBlock(new BlockPos(posX, posY, posZ), edge ? state : replace, 2);
                        }
                    }
                }
            }
        }
    }

    // ==================== Фундамент ====================

    protected void placeFoundationUnderneath(WorldGenLevel level, BlockState placeState,
                                             int minX, int minZ, int maxX, int maxZ, int featureY, BoundingBox box) {
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int posX = getXWithOffset(x, z);
                int posY = getYWithOffset(featureY);
                int posZ = getZWithOffset(x, z);
                if (!box.isInside(posX, posY, posZ)) continue;
                int brake = 0;
                while (posY > 1 && brake <= 15) {
                    BlockPos pos = new BlockPos(posX, posY, posZ);
                    BlockState bs = level.getBlockState(pos);
                    if (!(bs.isAir() || !bs.isSolid() || bs.is(BlockTags.LEAVES))) break;
                    level.setBlock(pos, placeState, 2);
                    posY--;
                    brake++;
                }
            }
        }
    }

    // ==================== Инвентари ====================

    protected boolean generateInvContents(WorldGenLevel level, BoundingBox box, RandomSource rand, Block block,
                                          Direction localFacing, int featureX, int featureY, int featureZ,
                                          String poolName, int amount) {
        int posX = getXWithOffset(featureX, featureZ);
        int posY = getYWithOffset(featureY);
        int posZ = getZWithOffset(featureX, featureZ);
        if (!box.isInside(posX, posY, posZ)) return true;
        BlockPos pos = new BlockPos(posX, posY, posZ);
        if (level.getBlockState(pos).getBlock() == block) return true;

        BlockState state = applyFacing(block.defaultBlockState(), rotate(localFacing));
        level.setBlock(pos, state, 2);

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof Container inv) {
            amount = (int) Math.floor(amount * StructureConfig.LOOT_AMOUNT_FACTOR_VALUE);
            ItemPool pool = ItemPool.getPool(poolName);
            for (ItemStack stack : pool.generate(rand, Math.max(amount, 1))) {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    if (inv.getItem(i).isEmpty()) { inv.setItem(i, stack); break; }
                }
            }
            return true;
        }
        return false;
    }

    protected boolean generateLockableContents(WorldGenLevel level, BoundingBox box, RandomSource rand, Block block,
                                               Direction localFacing, int featureX, int featureY, int featureZ,
                                               String poolName, int amount, double mod) {
        int posX = getXWithOffset(featureX, featureZ);
        int posY = getYWithOffset(featureY);
        int posZ = getZWithOffset(featureX, featureZ);
        if (!box.isInside(posX, posY, posZ)) return false;
        BlockPos pos = new BlockPos(posX, posY, posZ);
        if (level.getBlockState(pos).getBlock() == block) return false;

        BlockState state = applyFacing(block.defaultBlockState(), rotate(localFacing));
        level.setBlock(pos, state, 2);

        BlockEntity te = level.getBlockEntity(pos);
        if (te instanceof TileEntityLockableBase lock && te instanceof Container inv) {
            amount = (int) Math.floor(amount * StructureConfig.LOOT_AMOUNT_FACTOR_VALUE);
            ItemPool pool = ItemPool.getPool(poolName);
            for (ItemStack stack : pool.generate(rand, Math.max(amount, 1))) {
                for (int i = 0; i < inv.getContainerSize(); i++) {
                    if (inv.getItem(i).isEmpty()) { inv.setItem(i, stack); break; }
                }
            }
            lock.setPins(rand.nextInt(999) + 1);
            lock.setMod(mod);
            lock.lock();
            return true;
        }
        return false;
    }

    /** Случайное направление. */
    protected void placeRandomBobble(WorldGenLevel level, BoundingBox box, RandomSource rand,
                                     int featureX, int featureY, int featureZ) {
        int posX = getXWithOffset(featureX, featureZ);
        int posY = getYWithOffset(featureY);
        int posZ = getZWithOffset(featureX, featureZ);
        if (!box.isInside(posX, posY, posZ)) return;

        Direction facing = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
        BlockState state = ModBlocks.BOBBLEHEAD.get().defaultBlockState()
                .setValue(BlockBobble.FACING, facing);

        level.setBlock(new BlockPos(posX, posY, posZ), state, 2);
        BlockEntity te = level.getBlockEntity(new BlockPos(posX, posY, posZ));
        if (te instanceof TileEntityBobble bobble) {
            BlockBobble.BobbleType[] types = BlockBobble.BobbleType.values();
            bobble.type = types[rand.nextInt(types.length - 1) + 1];
            bobble.setChanged();
        }
    }

    // ==================== Loot helpers ====================

    protected void applyLoot(WorldGenLevel level, BoundingBox box, int x, int y, int z, String lootName) {
        int posX = getXWithOffset(x, z);
        int posY = getYWithOffset(y);
        int posZ = getZWithOffset(x, z);
        if (!box.isInside(posX, posY, posZ)) return;
        LootGenerator.applyLoot(level, new BlockPos(posX, posY, posZ), lootName);
    }

    protected void applyLoreBook(WorldGenLevel level, BoundingBox box, int x, int y, int z,
                                 ItemStack stack) {
        int posX = getXWithOffset(x, z);
        int posY = getYWithOffset(y);
        int posZ = getZWithOffset(x, z);
        if (!box.isInside(posX, posY, posZ)) return;
        BlockEntity te = level.getBlockEntity(new BlockPos(posX, posY, posZ));
        if (te instanceof com.hbm.tileentity.deco.TileEntityLoot loot) {
            loot.addItem(stack, 0.0, 0.0, 0.0);
        }
    }

    protected void applyMakeshiftGun(WorldGenLevel level, BoundingBox box, int x, int y, int z) {
        int posX = getXWithOffset(x, z);
        int posY = getYWithOffset(y);
        int posZ = getZWithOffset(x, z);
        if (!box.isInside(posX, posY, posZ)) return;
        LootGenerator.applyLoot(level, new BlockPos(posX, posY, posZ),
                LootGenerator.LOOT_MAKESHIFT_GUN);
    }

    // ==================== BlockDummyable ====================

    protected void placeCore(WorldGenLevel level, BoundingBox box, Block block, Direction localFacing,
                             int x, int y, int z) {
        int posX = getXWithOffset(x, z);
        int posY = getYWithOffset(y);
        int posZ = getZWithOffset(x, z);
        if (!box.isInside(posX, posY, posZ)) return;
        Direction facing = rotate(localFacing);
        if (block instanceof BlockDummyable dummy) {
            level.setBlock(new BlockPos(posX, posY, posZ), dummy.getCoreState(facing), 2);
        }
    }

    protected void fillSpace(WorldGenLevel level, BoundingBox box, int x, int y, int z,
                             int[] dim, Block block, Direction localFacing) {
        Direction facing = rotate(localFacing);
        int posX = getXWithOffset(x, z);
        int posY = getYWithOffset(y);
        int posZ = getZWithOffset(x, z);
        if (block instanceof BlockDummyable dummy) {
            dummy.placeMultiblock(level, new BlockPos(posX, posY, posZ), facing);
        }
    }

    protected void makeExtra(WorldGenLevel level, BoundingBox box, Block block, int x, int y, int z) {
        int posX = getXWithOffset(x, z);
        int posY = getYWithOffset(y);
        int posZ = getZWithOffset(x, z);
        if (!box.isInside(posX, posY, posZ)) return;
        BlockPos pos = new BlockPos(posX, posY, posZ);

        // Всю работу с состоянием и отложенным тегом делает BlockDummyable.makeExtra:
        // он сам различает живой мир и генерацию (#204).
        if (block instanceof BlockDummyable dummy) dummy.makeExtra(level, pos);
    }

    protected void updateConnectedBlocks(WorldGenLevel level, BoundingBox box) {
        for (int pass = 0; pass < 3; pass++) {
            for (int x = box.minX(); x <= box.maxX(); x++) {
                for (int y = box.minY(); y <= box.maxY(); y++) {
                    for (int z = box.minZ(); z <= box.maxZ(); z++) {
                        BlockPos pos = new BlockPos(x, y, z);
                        BlockState state = level.getBlockState(pos);
                        Block block = state.getBlock();

                        if (block instanceof StairBlock stair) {
                            BlockState newState = state;
                            for (Direction dir : Direction.Plane.HORIZONTAL) {
                                newState = stair.updateShape(newState, dir,
                                        level.getBlockState(pos.relative(dir)),
                                        level, pos, pos.relative(dir));
                            }
                            if (newState != state) level.setBlock(pos, newState, 3);
                        } else if (block instanceof net.minecraft.world.level.block.CrossCollisionBlock cross) {
                            BlockState newState = state;
                            for (Direction dir : Direction.Plane.HORIZONTAL) {
                                newState = cross.updateShape(newState, dir,
                                        level.getBlockState(pos.relative(dir)),
                                        level, pos, pos.relative(dir));
                            }
                            if (newState != state) level.setBlock(pos, newState, 3);
                        }
                    }
                }
            }
        }
    }

    // ==================== BlockSelector ====================

    public abstract static class BlockSelector {
        protected BlockState state = Blocks.AIR.defaultBlockState();
        public BlockState getState() { return state; }
        public abstract void selectBlocks(RandomSource rand, int posX, int posY, int posZ, boolean notInterior);
    }

    public static class ConcreteBricks extends BlockSelector {
        @Override public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c < 0.4F) state = ModBlocks.BRICK_CONCRETE.get().defaultBlockState();
            else if (c < 0.7F) state = ModBlocks.BRICK_CONCRETE_MOSSY.get().defaultBlockState();
            else if (c < 0.9F) state = ModBlocks.BRICK_CONCRETE_CRACKED.get().defaultBlockState();
            else state = ModBlocks.BRICK_CONCRETE_BROKEN.get().defaultBlockState();
        }
    }

    public static class LabTiles extends BlockSelector {
        @Override public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c < 0.5F) state = ModBlocks.TILE_LAB.get().defaultBlockState();
            else if (c < 0.9F) state = ModBlocks.TILE_LAB_CRACKED.get().defaultBlockState();
            else state = ModBlocks.TILE_LAB_BROKEN.get().defaultBlockState();
        }
    }

    public static class Sandstone extends BlockSelector {
        @Override public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c > 0.6F) state = Blocks.SANDSTONE.defaultBlockState();
            else if (c < 0.5F) state = ModBlocks.REINFORCED_SAND.get().defaultBlockState();
            else state = Blocks.SAND.defaultBlockState();
        }
    }

    public static class SuperConcrete extends BlockSelector {
        @Override public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            state = ModBlocks.CONCRETE_SUPER.get().defaultBlockState();
        }
    }

    public static class SupplyCrates extends BlockSelector {
        @Override public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c < 0.6F) state = Blocks.AIR.defaultBlockState();
            else if (c < 0.8F) state = ModBlocks.CRATE_AMMO.get().defaultBlockState();
            else if (c < 0.9F) state = ModBlocks.CRATE_CAN.get().defaultBlockState();
            else state = ModBlocks.CRATE.get().defaultBlockState();
        }
    }

    public static class GreenOoze extends BlockSelector {
        @Override public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            state = rand.nextFloat() < 0.8F
                    ? ModBlocks.TOXIC_BLOCK.get().defaultBlockState()
                    : ModBlocks.METEOR_POLISHED.get().defaultBlockState();
        }
    }

    public static class MeteorBricks extends BlockSelector {
        @Override public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c < 0.4F) state = ModBlocks.METEOR_BRICK.get().defaultBlockState();
            else if (c < 0.7F) state = ModBlocks.METEOR_BRICK_MOSSY.get().defaultBlockState();
            else state = ModBlocks.METEOR_BRICK_CRACKED.get().defaultBlockState();
        }
    }

    protected void placeScaffold(WorldGenLevel level, BoundingBox box, Block scaffold,
                                 Direction localFacing, boolean vertical, int x, int y, int z) {
        Direction facing = rotate(localFacing);
        BlockState state = scaffold.defaultBlockState();
        // FACING и VERTICAL есть у BlockSteelScaffold
        for (var prop : state.getProperties()) {
            if (prop.getName().equals("facing") && prop.getValueClass() == Direction.class) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                BlockState s = state.setValue((net.minecraft.world.level.block.state.properties.Property) prop, facing);
                state = s;
            } else if (prop.getName().equals("vertical") && prop.getValueClass() == Boolean.class) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                BlockState s = state.setValue((net.minecraft.world.level.block.state.properties.Property) prop, vertical);
                state = s;
            }
        }
        placeBlockAtCurrentPosition(level, state, x, y, z, box);
    }

    protected void fillWithScaffold(WorldGenLevel level, BoundingBox box, Block scaffold,
                                    Direction localFacing, boolean vertical,
                                    int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        Direction facing = rotate(localFacing);
        BlockState state = scaffold.defaultBlockState();
        for (var prop : state.getProperties()) {
            if (prop.getName().equals("facing") && prop.getValueClass() == Direction.class) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                BlockState s = state.setValue((net.minecraft.world.level.block.state.properties.Property) prop, facing);
                state = s;
            } else if (prop.getName().equals("vertical") && prop.getValueClass() == Boolean.class) {
                @SuppressWarnings({"unchecked", "rawtypes"})
                BlockState s = state.setValue((net.minecraft.world.level.block.state.properties.Property) prop, vertical);
                state = s;
            }
        }
        fillWithBlocks(level, box, minX, minY, minZ, maxX, maxY, maxZ, state);
    }

    /** Ставит BlockDecoCRT с заданным типом (моделью) и направлением. */
    protected void placeCRT(WorldGenLevel level, BoundingBox box, Block crt, int type,
                            Direction localFacing, int x, int y, int z) {
        Direction facing = rotate(localFacing);
        placeBlockAtCurrentPosition(level,
                crt.defaultBlockState()
                        .setValue(com.hbm.blocks.generic.BlockDecoCRT.FACING, facing)
                        .setValue(com.hbm.blocks.generic.BlockDecoCRT.TYPE, type & 3),
                x, y, z, box);
    }

    protected BlockState grate(int layer) {
        return ModBlocks.STEEL_GRATE.get().defaultBlockState()
                .setValue(com.hbm.blocks.deco.BlockSteelGrate.LAYER, layer & 7);
    }
}
