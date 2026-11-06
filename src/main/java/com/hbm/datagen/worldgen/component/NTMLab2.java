package com.hbm.datagen.worldgen.component;

import com.hbm.blocks.ModBlocks;
import com.hbm.itempool.ItemPoolsComponent;
import com.hbm.itempool.ItemPoolsLegacy;
import com.hbm.util.HbmChestContents;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

/**
 * Порт NTMLab2 из CivilianFeatures 1.7.10 (двухуровневая лаборатория).
 */
public class NTMLab2 extends NTMComponent {

    private final SuperConcrete superConcrete = new SuperConcrete();
    private final ConcreteBricks bricks = new ConcreteBricks();
    private final LabTiles tiles = new LabTiles();
    private final boolean[] hasPlacedLoot = new boolean[2];

    public NTMLab2() { }

    public NTMLab2(RandomSource rand, int minX, int minZ) {
        this.coordBaseMode = rand.nextInt(4);
        this.boundingBox = CivilianFeatures.boxFromSize(minX, 64, minZ, 12, 11, 8, this.coordBaseMode);
    }

    @Override
    public String getTypeName() { return "NTMLab2"; }

    @Override
    public void writeNBT(CompoundTag nbt) {
        super.writeNBT(nbt);
        nbt.putBoolean("hasLoot1", hasPlacedLoot[0]);
        nbt.putBoolean("hasLoot2", hasPlacedLoot[1]);
    }

    @Override
    public void readNBT(CompoundTag nbt) {
        super.readNBT(nbt);
        hasPlacedLoot[0] = nbt.getBoolean("hasLoot1");
        hasPlacedLoot[1] = nbt.getBoolean("hasLoot2");
    }

    @Override
    public boolean addComponentParts(WorldGenLevel level, RandomSource rand, BoundingBox box) {

        if (!heightCalculated) {
            if (!setAverageHeight(level, box, this.boundingBox.minY())) return false;
            this.boundingBox = this.boundingBox.move(0, -7, 0);
            heightCalculated = true;
            box = this.boundingBox;
        }

        // Фундамент под подвалом (пол подвала теперь на y=2, фундамент от y=1 вниз)
        placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, 0, 12, 6, 1, box);
        placeFoundationUnderneath(level, Blocks.STONE_BRICKS.defaultBlockState(), 0, 7, 6, 8, 1, box);

        // Воздух в наземной части
        fillWithAir(level, box, 1, 7, 1, 11, 11, 5);
        fillWithAir(level, box, 1, 7, 6, 5, 11, 7);
        fillWithAir(level, box, 9, 8, 6, 10, 9, 6);
        // Воздух в подвале
        fillWithAir(level, box, 2, 2, 2, 10, 5, 6);

        // Upper walls
        fillWithRandomizedBlocks(level, box, 0, 7, 0, 12, 11, 0, rand, superConcrete);
        fillWithRandomizedBlocks(level, box, 0, 7, 0, 0, 11, 8, rand, superConcrete);
        fillWithRandomizedBlocks(level, box, 1, 7, 8, 5, 7, 8, rand, superConcrete);
        fillWithBlocks(level, box, 1, 8, 8, 1, 10, 8, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        fillWithRandomizedBlocks(level, box, 2, 7, 8, 2, 10, 8, rand, superConcrete);
        fillWithBlocks(level, box, 3, 8, 8, 3, 10, 8, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        fillWithRandomizedBlocks(level, box, 4, 7, 8, 4, 10, 8, rand, superConcrete);
        fillWithBlocks(level, box, 5, 8, 8, 5, 10, 8, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        fillWithRandomizedBlocks(level, box, 1, 11, 8, 5, 11, 8, rand, superConcrete);
        fillWithRandomizedBlocks(level, box, 6, 7, 7, 6, 11, 8, rand, superConcrete);
        fillWithRandomizedBlocks(level, box, 6, 7, 6, 7, 9, 6, rand, superConcrete);
        fillWithBlocks(level, box, 6, 10, 6, 7, 10, 6, ModBlocks.CONCRETE_SUPER_BROKEN.get().defaultBlockState());
        fillWithRandomizedBlocks(level, box, 8, 7, 6, 12, 7, 6, rand, superConcrete);
        fillWithRandomizedBlocks(level, box, 8, 8, 6, 8, 11, 6, rand, superConcrete);
        fillWithRandomizedBlocks(level, box, 9, 10, 6, 10, 11, 6, rand, superConcrete);
        fillWithRandomizedBlocks(level, box, 11, 7, 6, 12, 11, 6, rand, superConcrete);
        fillWithRandomizedBlocks(level, box, 12, 7, 1, 12, 7, 5, rand, superConcrete);
        fillWithBlocks(level, box, 12, 8, 5, 12, 10, 5, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        fillWithRandomizedBlocks(level, box, 12, 8, 4, 12, 10, 4, rand, superConcrete);
        fillWithBlocks(level, box, 12, 8, 3, 12, 10, 3, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        fillWithRandomizedBlocks(level, box, 12, 8, 2, 12, 10, 2, rand, superConcrete);
        fillWithBlocks(level, box, 12, 8, 1, 12, 10, 1, ModBlocks.REINFORCED_GLASS.get().defaultBlockState());
        fillWithRandomizedBlocks(level, box, 12, 11, 1, 12, 11, 5, rand, superConcrete);

        // Lower walls (подвал поднят на 2 блока: y=2..5)
        fillWithBlocks(level, box, 1, 2, 1, 11, 5, 1, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 2, 2, 1, 5, 6, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 2, 7, 11, 5, 7, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithBlocks(level, box, 11, 2, 2, 11, 5, 6, ModBlocks.REINFORCED_STONE.get().defaultBlockState());
        fillWithBlocks(level, box, 6, 2, 3, 6, 5, 6, ModBlocks.REINFORCED_STONE.get().defaultBlockState());

        // Floors / Ceiling (upper)
        fillWithRandomizedBlocks(level, box, 1, 7, 1, 3, 7, 7, rand, tiles);
        fillWithRandomizedBlocks(level, box, 4, 7, 6, 5, 7, 7, rand, tiles);
        fillWithRandomizedBlocks(level, box, 8, 7, 1, 11, 7, 5, rand, tiles);
        fillWithRandomizedBlocks(level, box, 9, 7, 6, 10, 7, 6, rand, tiles);

        fillWithBlocks(level, box, 4, 7, 1, 7, 7, 1, ModBlocks.TILE_LAB_BROKEN.get().defaultBlockState());
        placeBlockAtCurrentPosition(level, ModBlocks.TILE_LAB_BROKEN.get().defaultBlockState(), 4, 7, 2, box);
        fillWithBlocks(level, box, 4, 7, 3, 4, 7, 5, ModBlocks.TILE_LAB_CRACKED.get().defaultBlockState());
        placeBlockAtCurrentPosition(level, ModBlocks.TILE_LAB_BROKEN.get().defaultBlockState(), 5, 7, 3, box);
        fillWithBlocks(level, box, 5, 7, 4, 5, 7, 5, ModBlocks.TILE_LAB_CRACKED.get().defaultBlockState());
        placeBlockAtCurrentPosition(level, ModBlocks.TILE_LAB_BROKEN.get().defaultBlockState(), 6, 7, 4, box);
        placeBlockAtCurrentPosition(level, ModBlocks.TILE_LAB_CRACKED.get().defaultBlockState(), 6, 7, 5, box);
        fillWithBlocks(level, box, 7, 7, 2, 7, 7, 3, ModBlocks.TILE_LAB_BROKEN.get().defaultBlockState());
        fillWithBlocks(level, box, 7, 7, 4, 7, 7, 5, ModBlocks.TILE_LAB_CRACKED.get().defaultBlockState());

        fillWithBlocks(level, box, 1, 11, 1, 2, 11, 7, ModBlocks.BRICK_LIGHT.get().defaultBlockState());
        fillWithBlocks(level, box, 3, 11, 6, 4, 11, 7, ModBlocks.BRICK_LIGHT.get().defaultBlockState());
        fillWithBlocks(level, box, 9, 11, 1, 11, 11, 5, ModBlocks.BRICK_LIGHT.get().defaultBlockState());

        fillWithBlocks(level, box, 3, 11, 1, 8, 11, 1, ModBlocks.WASTE_PLANKS.get().defaultBlockState());
        fillWithBlocks(level, box, 3, 11, 2, 4, 11, 2, ModBlocks.WASTE_PLANKS.get().defaultBlockState());
        fillWithBlocks(level, box, 7, 11, 2, 8, 11, 2, ModBlocks.WASTE_PLANKS.get().defaultBlockState());
        fillWithBlocks(level, box, 3, 11, 3, 3, 11, 5, ModBlocks.WASTE_PLANKS.get().defaultBlockState());
        fillWithBlocks(level, box, 4, 11, 4, 4, 11, 5, ModBlocks.WASTE_PLANKS.get().defaultBlockState());
        fillWithBlocks(level, box, 5, 11, 6, 5, 11, 7, ModBlocks.WASTE_PLANKS.get().defaultBlockState());
        fillWithBlocks(level, box, 8, 11, 3, 8, 11, 5, ModBlocks.WASTE_PLANKS.get().defaultBlockState());

        // Пол подвала
        fillWithRandomizedBlocks(level, box, 2, 2, 2, 5, 2, 6, rand, tiles);
        fillWithRandomizedBlocks(level, box, 6, 2, 2, 6, 2, 3, rand, tiles);
        fillWithRandomizedBlocks(level, box, 7, 2, 2, 10, 2, 6, rand, tiles);

        // Потолок подвала
        fillWithRandomizedBlocks(level, box, 1, 6, 1, 11, 6, 7, rand, bricks);

        // Пробиваем потолок подвала под местом падения бомбы (x=6, z=3)
        fillWithAir(level, box, 5, 6, 2, 5, 6, 3);

        placeDeco(level, box, ModBlocks.CRASHED_BALEFIRE.get(), Direction.NORTH, 5, 9, 2);

        placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, false, false, 9, 8, 6);
        placeDoor(level, box, ModBlocks.DOOR_OFFICE.get(), Direction.NORTH, true, false, 10, 8, 6);

        fillWithBlocks(level, box, 1, 8, 1, 1, 10, 1, ModBlocks.DECO_STEEL.get().defaultBlockState());
        fillWithBlocks(level, box, 1, 8, 2, 1, 9, 3, grate(7));

        placeDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.EAST, 1, 10, 2);
        placeBlockAtCurrentPosition(level, ModBlocks.STEEL_BEAM.get().defaultBlockState(), 1, 10, 3, box);
        fillWithBlocks(level, box, 1, 8, 6, 1, 10, 6, ModBlocks.DECO_PIPE_FRAMED_RUSTED.get().defaultBlockState());


        fillWithDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.WEST, 8, 8, 1, 8, 10, 1);
        fillWithBlocks(level, box, 9, 10, 1, 10, 10, 1, grate(7));

        fillWithDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.SOUTH, 9, 9, 1, 10, 9, 1);
        fillWithBlocks(level, box, 9, 8, 1, 10, 8, 1, ModBlocks.DECO_STEEL.get().defaultBlockState());
        fillWithDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.EAST, 11, 8, 1, 11, 10, 1);

        // Подвал (все y подняты на 2)
        fillWithBlocks(level, box, 2, 3, 2, 2, 3, 6, grate(7));
        placeBlockAtCurrentPosition(level, ModBlocks.VITRIFIED_BARREL.get().defaultBlockState(), 2, 4, 2, box);

        fillWithDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.EAST, 3, 3, 2, 3, 5, 2);
        fillWithDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.EAST, 3, 3, 4, 3, 5, 4);
        fillWithDeco(level, box, ModBlocks.STEEL_WALL.get(), Direction.EAST, 3, 3, 6, 3, 5, 6);
        placeBlockAtCurrentPosition(level, ModBlocks.CRATE.get().defaultBlockState(), 4, 3, 6, box);
        placeBlockAtCurrentPosition(level, ModBlocks.CRATE_LEAD.get().defaultBlockState(), 4, 4, 6, box);

        if (!hasPlacedLoot[0]) {
            hasPlacedLoot[0] = generateInvContents(level, box, rand, ModBlocks.CRATE_IRON.get(),
                    Direction.NORTH, 5, 3, 6, ItemPoolsComponent.POOL_NUKE_FUEL, 10);
        }
        fillWithBlocks(level, box, 4, 3, 5, 5, 3, 5, ModBlocks.CRATE_LEAD.get().defaultBlockState());

        fillWithBlocks(level, box, 7, 3, 6, 7, 5, 6, ModBlocks.DECO_STEEL.get().defaultBlockState());
        fillWithBlocks(level, box, 8, 3, 6, 10, 3, 6, grate(7));

        fillWithDeco(level, box, ModBlocks.TAPE_RECORDER.get(), Direction.NORTH, 8, 4, 6, 9, 4, 6);
        placeBlockAtCurrentPosition(level, ModBlocks.STEEL_BEAM.get().defaultBlockState(), 10, 4, 6, box);
        fillWithBlocks(level, box, 8, 5, 6, 10, 5, 6, ModBlocks.STEEL_ROOF.get().defaultBlockState());

        if (!hasPlacedLoot[1]) {
            hasPlacedLoot[1] = generateInvContents(level, box, rand, ModBlocks.CRATE_IRON.get(),
                    Direction.NORTH, 10, 3, 3, ItemPoolsLegacy.POOL_NUKE_TRASH, 9);
            if (rand.nextInt(2) == 0) {
                applyLoreBook(level, box, 10, 3, 3,
                        HbmChestContents.generateOfficeBook(rand));
            }
        }

        updateConnectedBlocks(level, box);
        return true;
    }
}