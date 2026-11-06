package com.hbm.blocks.generic;

import com.hbm.blocks.ModBlocks;
import com.hbm.advancements.ModCriteriaTriggers;
import com.hbm.items.ModItems;

import com.hbm.itempool.ItemPool;
import com.hbm.itempool.ItemPoolsRedRoom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BlockRedBrickKeyhole extends Block {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public BlockRedBrickKeyhole(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        Direction dir = placer != null ? placer.getDirection().getOpposite() : Direction.NORTH;
        level.setBlock(pos, state.setValue(FACING, dir), 3);
    }

    @Override
    public ItemStack getCloneItemStack(@NotNull BlockState state, @NotNull HitResult target,
                                       @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull Player player) {
        return new ItemStack(ModBlocks.BRICK_RED.get());
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

            generateRoom(level, center, dir);

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

    protected static void generateRoom(Level level, BlockPos center, Direction dir) {
        int size = 9;
        int height = 5;
        int width = size / 2;

        // Каркас
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

        // Стены — оставляем открытой сторону dir
        for (int i = 1; i <= height - 2; i++) {
            setBrick(level, center.offset(width, i, width), Direction.UP);
            setBrick(level, center.offset(width, i, -width), Direction.UP);
            setBrick(level, center.offset(-width, i, width), Direction.UP);
            setBrick(level, center.offset(-width, i, -width), Direction.UP);

            for (int j = -width + 1; j <= width - 1; j++) {
                if (dir != Direction.EAST)  setBrick(level, center.offset(width, i, j), Direction.EAST);
                if (dir != Direction.WEST)  setBrick(level, center.offset(-width, i, j), Direction.WEST);
                if (dir != Direction.SOUTH) setBrick(level, center.offset(j, i, width), Direction.SOUTH);
                if (dir != Direction.NORTH) setBrick(level, center.offset(j, i, -width), Direction.NORTH);
            }
        }

        // Пол и потолок
        for (int i = -width + 1; i <= width - 1; i++) {
            for (int j = -width + 1; j <= width - 1; j++) {
                setBrick(level, center.offset(i, 0, j), Direction.DOWN);
                setBrick(level, center.offset(i, height - 1, j), Direction.UP);
                for (int k = 1; k <= height - 2; k++) {
                    level.setBlock(center.offset(i, k, j), Blocks.AIR.defaultBlockState(), 3);
                }
            }
        }

        // Пьедесталы с лутом
        spawnPedestalItem(level, center.offset(0, 1, 0), ItemPoolsRedRoom.POOL_BLACK_SLAB);
        if (level.random.nextBoolean()) spawnPedestalItem(level, center.offset(2, 1, 0), ItemPoolsRedRoom.POOL_BLACK_PART);
        if (level.random.nextBoolean()) spawnPedestalItem(level, center.offset(-2, 1, 0), ItemPoolsRedRoom.POOL_BLACK_PART);
        if (level.random.nextBoolean()) spawnPedestalItem(level, center.offset(0, 1, 2), ItemPoolsRedRoom.POOL_BLACK_PART);
        if (level.random.nextBoolean()) spawnPedestalItem(level, center.offset(0, 1, -2), ItemPoolsRedRoom.POOL_BLACK_PART);

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

    public static void spawnPedestalItem(Level level, BlockPos pos, String pool) {
        level.setBlock(pos, ModBlocks.PEDESTAL.get().defaultBlockState(), 3);
        if (level.getBlockEntity(pos) instanceof BlockPedestal.TileEntityPedestal pedestal) {
            pedestal.item = ItemPool.getStack(pool, level.random);
            pedestal.setChanged();
        }
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.STONE)
                .strength(1.5F, 10000.0F)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops();
    }
}