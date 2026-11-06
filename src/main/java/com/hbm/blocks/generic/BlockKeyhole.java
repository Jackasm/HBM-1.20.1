package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.items.ModArmorItems;
import com.hbm.items.ModItems;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;

public class BlockKeyhole extends Block {

    public BlockKeyhole(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target,
                                       @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Player player) {
        return new ItemStack(Blocks.STONE);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack held = player.getItemInHand(hand);
        if (held.isEmpty()) return InteractionResult.PASS;

        boolean cracked = held.getItem() == ModItems.KEY_RED_CRACKED.get();
        boolean normal = held.getItem() == ModItems.KEY_RED.get();

        if ((normal || cracked) && hit.getDirection().getAxis().isHorizontal()) {
            if (cracked) held.shrink(1);
            if (level.isClientSide) return InteractionResult.SUCCESS;

            Direction dir = hit.getDirection();
            BlockPos center = pos.offset(-dir.getStepX() * 4, -2, -dir.getStepZ() * 4);

            generateRoom(level, center);

            int b = switch (dir) {
                case NORTH -> 1;
                case WEST  -> 2;
                case SOUTH -> 3;
                case EAST  -> 0;
                default    -> 0;
            };
            placeRedDoor(level, pos.below(), b);

            level.playSound(null, pos, SoundEvents.IRON_DOOR_OPEN, SoundSource.BLOCKS, 1.0F, 1.0F);

            if (player instanceof ServerPlayer sp) {
                ModCriteriaTriggers.RED_ROOM.trigger(sp);
            }
            return InteractionResult.CONSUME;
        }

        return InteractionResult.PASS;
    }

    private static void placeRedDoor(Level level, BlockPos pos, int meta) {
        Direction facing = switch (meta) {
            case 1 -> Direction.NORTH;
            case 2 -> Direction.WEST;
            case 3 -> Direction.SOUTH;
            default -> Direction.EAST;
        };

        Block doorBlock = ModBlocks.DOOR_RED.get();
        BlockState base = doorBlock.defaultBlockState()
                .setValue(DoorBlock.FACING, facing)
                .setValue(DoorBlock.OPEN, false)
                .setValue(DoorBlock.POWERED, false)
                .setValue(DoorBlock.HINGE, DoorHingeSide.LEFT);

        level.setBlock(pos, base.setValue(DoorBlock.HALF, DoubleBlockHalf.LOWER), 3);
        level.setBlock(pos.above(), base.setValue(DoorBlock.HALF, DoubleBlockHalf.UPPER), 3);
    }

    // ============================================================
    // Генерация комнаты
    // ============================================================

    protected static void generateRoom(Level level, BlockPos center) {
        int size = 9;
        int height = 5;
        int width = size / 2;

        // Каркас (верх/низ/углы)
        for (int i = -width; i <= width; i++) {
            setBrick(level, center.offset(i, 0, width), Direction.UP);
            setBrick(level, center.offset(i, 0, -width), Direction.UP);
            setBrick(level, center.offset(width, 0, i), Direction.UP);
            setBrick(level, center.offset(-width, 0, i), Direction.UP);
            setBrick(level, center.offset(i, height - 1, width), Direction.DOWN);
            setBrick(level, center.offset(i, height - 1, -width), Direction.DOWN);
            setBrick(level, center.offset(width, height - 1, i), Direction.DOWN);
            setBrick(level, center.offset(-width, height - 1, i), Direction.DOWN);
        }

        // Стены + углы
        for (int i = 1; i <= height - 2; i++) {
            setBrick(level, center.offset(width, i, width), Direction.UP);
            setBrick(level, center.offset(width, i, -width), Direction.UP);
            setBrick(level, center.offset(-width, i, width), Direction.UP);
            setBrick(level, center.offset(-width, i, -width), Direction.UP);

            for (int j = -width + 1; j <= width - 1; j++) {
                setBrick(level, center.offset(width, i, j), Direction.EAST);
                setBrick(level, center.offset(-width, i, j), Direction.WEST);
                setBrick(level, center.offset(j, i, width), Direction.SOUTH);
                setBrick(level, center.offset(j, i, -width), Direction.NORTH);
            }
        }

        // Иногда в стене — keyhole-блок
        if (level.random.nextInt(1) == 0) {
            int r = level.random.nextInt(4);
            BlockPos kp = switch (r) {
                case 0 -> center.offset(width, 2, 0);
                case 1 -> center.offset(-width, 2, 0);
                case 2 -> center.offset(0, 2, width);
                default -> center.offset(0, 2, -width);
            };
            level.setBlock(kp, ModBlocks.STONE_KEYHOLE_META.get().defaultBlockState(), 3);
        }

        // Пол и потолок из brick_red (низ/верх)
        for (int i = -width + 1; i <= width - 1; i++) {
            for (int j = -width + 1; j <= width - 1; j++) {
                setBrick(level, center.offset(i, 0, j), Direction.DOWN);
                setBrick(level, center.offset(i, height - 1, j), Direction.UP);
                for (int k = 1; k <= height - 2; k++) {
                    level.setBlock(center.offset(i, k, j), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        // Факелы
        int torchDist = width - 1;
        int torchOff = torchDist - 1;

        placeTorch(level, center.offset(torchDist, 2, torchOff), Direction.WEST);
        placeTorch(level, center.offset(torchDist, 2, -torchOff), Direction.WEST);

        placeTorch(level, center.offset(-torchDist, 2, torchOff), Direction.EAST);
        placeTorch(level, center.offset(-torchDist, 2, -torchOff), Direction.EAST);

        placeTorch(level, center.offset(torchOff, 2, torchDist), Direction.NORTH);
        placeTorch(level, center.offset(-torchOff, 2, torchDist), Direction.NORTH);

        placeTorch(level, center.offset(torchOff, 2, -torchDist), Direction.SOUTH);
        placeTorch(level, center.offset(-torchOff, 2, -torchDist), Direction.SOUTH);

        // Паутина
        if (level.random.nextInt(4) == 0) {
            for (int i = -width + 1; i <= width - 1; i++) {
                for (int j = -width + 1; j <= width - 1; j++) {
                    if (level.random.nextBoolean()) {
                        level.setBlock(center.offset(i, height - 2, j), Blocks.COBWEB.defaultBlockState(), 3);
                    }
                }
            }
        }

        // Колонны
        if (level.random.nextInt(4) == 0) {
            for (int i = 1; i <= height - 2; i++) {
                setConcrete(level, center.offset(width - 2, i, width - 2));
                setConcrete(level, center.offset(width - 2, i, -width + 2));
                setConcrete(level, center.offset(-width + 2, i, width - 2));
                setConcrete(level, center.offset(-width + 2, i, -width + 2));
            }
        }

        // Огонь
        if (level.random.nextInt(4) == 0) {
            placeNetherrackFire(level, center.offset(width - 1, 0, width - 1));
            placeNetherrackFire(level, center.offset(width - 1, 0, -width + 1));
            placeNetherrackFire(level, center.offset(-width + 1, 0, width - 1));
            placeNetherrackFire(level, center.offset(-width + 1, 0, -width + 1));
        }

        // Круг на полу
        if (level.random.nextInt(4) == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i != 0 || j != 0) {
                        setConcrete(level, center.offset(i, 0, j));
                    }
                }
            }
        }

        // Лава
        if (level.random.nextInt(4) == 0) {
            placeLavaRing(level, center, width);
        }

        // Лут
        int rand = level.random.nextInt(20);
        if (rand == 0) {
            level.setBlock(center.offset(0, 1, 0), ModBlocks.DECO_LOOT.get().defaultBlockState(), 3);
            if (level.getBlockEntity(center.offset(0, 1, 0)) instanceof com.hbm.tileentity.deco.TileEntityLoot loot) {
                if (level.random.nextInt(5) == 0) {
                    loot.addItem(new ItemStack(ModArmorItems.RPA_HELMET.get()), 0, 0, 0);
                    loot.addItem(new ItemStack(ModArmorItems.RPA_CHESTPLATE.get()), 0, 0, 0);
                    loot.addItem(new ItemStack(ModArmorItems.RPA_LEGGINGS.get()), 0, 0, 0);
                    loot.addItem(new ItemStack(ModArmorItems.RPA_BOOTS.get()), 0, 0, 0);
                } else {
                    loot.addItem(new ItemStack(ModArmorItems.TRENCHMASTER_HELMET.get()), 0, 0, 0);
                    loot.addItem(new ItemStack(ModArmorItems.TRENCHMASTER_CHESTPLATE.get()), 0, 0, 0);
                    loot.addItem(new ItemStack(ModArmorItems.TRENCHMASTER_LEGGINGS.get()), 0, 0, 0);
                    loot.addItem(new ItemStack(ModArmorItems.TRENCHMASTER_BOOTS.get()), 0, 0, 0);
                }
            }
        } else {
            spawnPedestalItem(level, center.offset(0, 1, 0));
        }

        // Очистка дропа
        AABB box = new AABB(
                center.getX() + 0.5, center.getY(), center.getZ() + 0.5,
                center.getX() + 0.5, center.getY() + height, center.getZ() + 0.5
        ).inflate(size / 2D, 0, size / 2D);
        for (ItemEntity item : level.getEntitiesOfClass(ItemEntity.class, box)) {
            item.discard();
        }
    }

    private static void setBrick(Level level, BlockPos pos, Direction facing) {
        level.setBlock(pos, ModBlocks.BRICK_RED.get().defaultBlockState()
                .setValue(BlockRedBrick.FACING, facing), 3);
    }

    private static void setConcrete(Level level, BlockPos pos) {
        // В оригинале meta=14 (красный). У тебя concrete_colored_red — отдельный блок.
        level.setBlock(pos, ModBlocks.CONCRETE_COLORED_RED.get().defaultBlockState(), 3);
    }

    private static void placeTorch(Level level, BlockPos pos, Direction wallDirection) {
        level.setBlock(pos, Blocks.WALL_TORCH.defaultBlockState()
                .setValue(BlockStateProperties.HORIZONTAL_FACING, wallDirection), 3);
    }

    private static void placeNetherrackFire(Level level, BlockPos pos) {
        level.setBlock(pos, Blocks.NETHERRACK.defaultBlockState(), 3);
        level.setBlock(pos.above(), Blocks.FIRE.defaultBlockState(), 3);
    }

    private static void placeLavaRing(Level level, BlockPos center, int width) {
        level.setBlock(center.offset(width - 2, 0, width - 1), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(width - 3, 0, width - 1), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(-width + 2, 0, width - 1), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(-width + 3, 0, width - 1), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(width - 2, 0, -width + 1), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(width - 3, 0, -width + 1), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(-width + 2, 0, -width + 1), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(-width + 3, 0, -width + 1), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(width - 1, 0, width - 2), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(width - 1, 0, width - 3), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(width - 1, 0, -width + 2), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(width - 1, 0, -width + 3), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(-width + 1, 0, width - 2), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(-width + 1, 0, width - 3), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(-width + 1, 0, -width + 2), Blocks.LAVA.defaultBlockState(), 3);
        level.setBlock(center.offset(-width + 1, 0, -width + 3), Blocks.LAVA.defaultBlockState(), 3);
    }

    public static void spawnPedestalItem(Level level, BlockPos pos) {
        level.setBlock(pos, ModBlocks.PEDESTAL.get().defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof BlockPedestal.TileEntityPedestal pedestal) {
            pedestal.item = com.hbm.itempool.ItemPool.getStack(
                    com.hbm.itempool.ItemPoolsRedRoom.POOL_RED_PEDESTAL, level.random);
            pedestal.setChanged();
        }
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.STONE)
                .strength(1.5F, 10.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops();
    }
}