package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Порт SiloComponent из 1.7.10. Без int-метаданных, только Direction и BlockState.
 */
public class SiloComponent extends NTMComponent {

    public int freq = 0;
    public int freqHatch = 0;

    public SiloComponent() { }

    public SiloComponent(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        // Высота 41, а не 29: площадка с обвязкой занимает локальные Y 0…36
        // (асфальт на 25, воздух над ним до 36). При высоте 29 клип бокса
        // обрезал fillWithAir, и площадка оставалась под грунтом — #152.
        this.boundingBox = CivilianFeatures.boxFromSize(minX, 64, minZ, 42, 41, 26, this.coordBaseMode);
        this.freq = rand.nextInt();
        this.freqHatch = rand.nextInt();
    }

    /**
     * Площадка, по которой меряется поверхность: локальные X 13…42, Z 2…20
     * на уровне асфальта (локальный Y 25), в мировых координатах с поворотом.
     */
    public BoundingBox platformArea() {
        return getRotatedBoundingBox(
                getXWithOffset(13, 2), getYWithOffset(25), getZWithOffset(13, 2),
                29, 3, 18);
    }

    /**
     * Ставит площадку на уровень поверхности {@code surfaceY}: асфальт ляжет
     * на {@code surfaceY - 1}. Вызывается до создания куска, чтобы бокс был
     * окончательным и не двигался во время генерации чанков.
     */
    public void applySurfaceHeight(int surfaceY) {
        int delta = surfaceY - 1 - getYWithOffset(25);
        this.boundingBox = this.boundingBox.move(0, delta, 0);
        this.hpos = surfaceY;
        this.heightCalculated = true;
    }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        nbt.putInt("freq", freq);
        nbt.putInt("freqHatch", freqHatch);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        this.freq = nbt.getInt("freq");
        this.freqHatch = nbt.getInt("freqHatch");
    }

    @Override
    public String getTypeName() { return "SiloComponent"; }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        if (this.hpos == -1) {
            BoundingBox area = getRotatedBoundingBox(
                    getXWithOffset(13, 2), getYWithOffset(25), getZWithOffset(13, 2),
                    29, 3, 18);
            this.hpos = getAverageHeight(level, area, box, getYWithOffset(25));
            if (this.hpos == -1) return false;
            this.boundingBox = this.boundingBox.move(0, this.hpos - 1 - getYWithOffset(25), 0);
            box = this.boundingBox;
        }

        SiloSurface.build(this, level, rand, box);
        SiloStairway.build(this, level, rand, box);
        SiloBlueControlRoom.build(this, level, rand, box);
        SiloSilo.build(this, level, rand, box);
        SiloRedSector.build(this, level, rand, box);
        SiloYellowSector.build(this, level, rand, box);
        SiloGreenSector.build(this, level, rand, box);
        SiloBlackSector.build(this, level, rand, box);

        updateConnectedBlocks(level, box);
        return true;
    }

    protected static int getAverageHeight(WorldGenLevel level, BoundingBox area, BoundingBox box, int y) {
        int total = 0;
        int iterations = 0;
        for (int z = area.minZ(); z <= area.maxZ(); z++) {
            for (int x = area.minX(); x <= area.maxX(); x++) {
                if (box.isInside(x, y, z)) {
                    total += Math.max(level.getHeight(
                            net.minecraft.world.level.levelgen.Heightmap.Types.WORLD_SURFACE_WG, x, z), 62);
                    iterations++;
                }
            }
        }
        return iterations == 0 ? -1 : total / iterations;
    }

    protected BoundingBox getRotatedBoundingBox(int minX, int minY, int minZ,
                                                int maxX, int maxY, int maxZ) {
        return switch (this.coordBaseMode) {
            default -> BoundingBox.fromCorners(
                    new BlockPos(minX, minY, minZ),
                    new BlockPos(minX + maxX, minY + maxY, minZ + maxZ));
            case 1, 3 -> BoundingBox.fromCorners(
                    new BlockPos(minX, minY, minZ),
                    new BlockPos(minX + maxZ, minY + maxY, minZ + maxX));
        };
    }

    protected void setRTTYFreq(WorldGenLevel level, BoundingBox box, int featureX, int featureY, int featureZ, int freq) {
        int posX = getXWithOffset(featureX, featureZ);
        int posY = getYWithOffset(featureY);
        int posZ = getZWithOffset(featureX, featureZ);
        if (!box.isInside(posX, posY, posZ)) return;

        var be = level.getBlockEntity(new BlockPos(posX, posY, posZ));
        if (be instanceof com.hbm.tileentity.network.TileEntityRadioTorchBase torch) {
            torch.channel = String.valueOf(freq);
            torch.lastState = 0;
        }
    }

    protected void fillWithMines(WorldGenLevel level, BoundingBox box, RandomSource rand,
                                 int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
        if (getYWithOffset(minY) < box.minY() || getYWithOffset(maxY) > box.maxY()) return;
        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                int posX = getXWithOffset(x, z);
                int posZ = getZWithOffset(x, z);
                if (posX < box.minX() || posX > box.maxX() || posZ < box.minZ() || posZ > box.maxZ()) continue;
                for (int y = minY; y <= maxY; y++) {
                    int posY = getYWithOffset(y);
                    BlockPos pos = new BlockPos(posX, posY, posZ);
                    BlockPos below = pos.below();
                    if (rand.nextInt(15) == 0
                            && level.getBlockState(pos).isAir()
                            && !level.getBlockState(below).isAir()) {
                        level.setBlock(pos, ModBlocks.MINE_AP.get().defaultBlockState(), 2);
                        var be = level.getBlockEntity(pos);
                        if (be instanceof com.hbm.tileentity.bomb.TileEntityLandmine mine) {
                            mine.waitingForPlayer = true;
                        }
                    }
                }
            }
        }
    }

    protected void placeCoreLaunchpad(WorldGenLevel level, BoundingBox box, Block block,
                                      Direction localFacing, int x, int y, int z) {
        int posX = getXWithOffset(x, z);
        int posY = getYWithOffset(y);
        int posZ = getZWithOffset(x, z);
        if (!box.isInside(posX, posY, posZ)) return;

        Direction facing = rotate(localFacing);
        BlockPos pos = new BlockPos(posX, posY, posZ);
        if (block instanceof com.hbm.blocks.BlockDummyable dummy) {
            level.setBlock(pos, dummy.getCoreState(facing), 2);
        }
        var be = level.getBlockEntity(pos);
        if (be instanceof com.hbm.tileentity.bomb.TileEntityLaunchPadRusted pad) {
            pad.missileLoaded = true;
        }
    }

    // ==== BlockSelectors ====

    /** Ступени из brick_concrete. Направление и top задаются явно. */
    public static class ConcreteStairs extends NTMComponent.BlockSelector {
        private Direction facing = Direction.NORTH;
        private boolean top = false;

        public void setFacing(Direction facing) { this.facing = facing; }
        public void setTop(boolean top) { this.top = top; }

        @Override
        public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            Block block = c < 0.4F ? ModBlocks.BRICK_CONCRETE_STAIRS.get()
                    : c < 0.7F ? ModBlocks.BRICK_CONCRETE_MOSSY_STAIRS.get()
                    : c < 0.9F ? ModBlocks.BRICK_CONCRETE_CRACKED_STAIRS.get()
                    : ModBlocks.BRICK_CONCRETE_BROKEN_STAIRS.get();
            state = block.defaultBlockState()
                    .setValue(StairBlock.FACING, facing)
                    .setValue(StairBlock.HALF, top ? Half.TOP : Half.BOTTOM);
        }
    }

    /** Разрушенные кирпичи: сляб, ступенька или блок. */
    public static class DestroyedBricks extends NTMComponent.BlockSelector {
        @Override
        public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c < 0.3F) {
                state = ModBlocks.CONCRETE_BRICK_SLAB_BRICK_CONCRETE.get().defaultBlockState();
            } else if (c < 0.6F) {
                float c2 = rand.nextFloat();
                Block block = c2 < 0.4F ? ModBlocks.BRICK_CONCRETE_STAIRS.get()
                        : c2 < 0.7F ? ModBlocks.BRICK_CONCRETE_MOSSY_STAIRS.get()
                        : c2 < 0.9F ? ModBlocks.BRICK_CONCRETE_CRACKED_STAIRS.get()
                        : ModBlocks.BRICK_CONCRETE_BROKEN_STAIRS.get();
                Direction d = Direction.Plane.HORIZONTAL.getRandomDirection(rand);
                state = block.defaultBlockState().setValue(StairBlock.FACING, d);
            } else if (c < 0.9F) {
                float c2 = rand.nextFloat();
                state = c2 < 0.4F ? ModBlocks.BRICK_CONCRETE.get().defaultBlockState()
                        : c2 < 0.7F ? ModBlocks.BRICK_CONCRETE_MOSSY.get().defaultBlockState()
                        : c2 < 0.9F ? ModBlocks.BRICK_CONCRETE_CRACKED.get().defaultBlockState()
                        : ModBlocks.BRICK_CONCRETE_BROKEN.get().defaultBlockState();
            } else {
                state = Blocks.AIR.defaultBlockState();
            }
        }
    }

    /** Бочки, ящики, редкие красные/розовые бочки. */
    public static class SiloSupplies extends NTMComponent.BlockSelector {
        @Override
        public void selectBlocks(RandomSource rand, int x, int y, int z, boolean notInterior) {
            float c = rand.nextFloat();
            if (c < 0.2F) state = ModBlocks.BARREL_CORRODED.get().defaultBlockState();
            else if (c < 0.4F) state = ModBlocks.CRATE_CAN.get().defaultBlockState();
            else if (c < 0.45F) state = ModBlocks.RED_BARREL.get().defaultBlockState();
            else if (c < 0.5F) state = ModBlocks.PINK_BARREL.get().defaultBlockState();
            else state = Blocks.AIR.defaultBlockState();
        }
    }

    public static final net.minecraft.world.item.ItemStack LAUNCH_KEY_STACK =
            new net.minecraft.world.item.ItemStack(com.hbm.items.ModItems.LAUNCH_KEY.get());

    @Override
    public Direction rotate(Direction local) {
        if (local.getAxis().isVertical()) {
            return local;
        }
        return switch (coordBaseMode) {
            default -> local.getOpposite();
            case 1 -> local.getCounterClockWise();
            case 2 -> local;
            case 3 -> local.getClockWise();
        };
    }
}
