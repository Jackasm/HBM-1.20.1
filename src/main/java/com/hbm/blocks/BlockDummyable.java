package com.hbm.blocks;

import com.hbm.handler.ThreeInts;
import com.hbm.interfaces.ICopiable;
import com.hbm.tileentity.IPersistentNBT;
import com.hbm.tileentity.TileEntityProxyBase;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BlockDummyable extends BaseEntityBlock implements ICopiable {

    public static final IntegerProperty DUMMY_STATE = IntegerProperty.create("dummy_state", 0, 2);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    protected static final Set<BlockPos> NEW_STRUCTURES = new HashSet<>();

    public static boolean safeRem = false;

    public abstract ItemStack getMachineItem();

    public static void setSafeRem(boolean value) {
        safeRem = value;
    }

    public BlockDummyable(Properties properties) {
        super(properties);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection())
                .setValue(DUMMY_STATE, 0);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(DUMMY_STATE, FACING);
    }

    // ---------- Работа с состоянием ----------
    protected boolean isDummy(BlockState state) {
        return state.getValue(DUMMY_STATE) == 0;
    }

    protected boolean isExtra(BlockState state) {
        return state.getValue(DUMMY_STATE) == 1;
    }

    public boolean isCore(BlockState state) {
        return state.getValue(DUMMY_STATE) == 2;
    }

    protected Direction getDummyFacing(BlockState state) {
        return state.getValue(FACING);
    }

    public BlockState getDummyState(Direction facing) {
        return defaultBlockState()
                .setValue(DUMMY_STATE, 0)
                .setValue(FACING, facing.getOpposite());
    }

    public BlockState getExtraState(Direction facing) {
        return defaultBlockState()
                .setValue(DUMMY_STATE, 1)
                .setValue(FACING, facing.getOpposite());
    }

    public BlockState getCoreState(Direction facing) {
        return defaultBlockState()
                .setValue(DUMMY_STATE, 2)
                .setValue(FACING, facing);
    }

    // ---------- Поиск ядра ----------
    public BlockPos findCore(LevelAccessor world, BlockPos pos) {
        List<ThreeInts> visited = new ArrayList<>();
        return findCoreRec(world, pos, visited);
    }

    private BlockPos findCoreRec(LevelAccessor world, BlockPos pos, List<ThreeInts> visited) {
        ThreeInts tp = new ThreeInts(pos.getX(), pos.getY(), pos.getZ());
        if (visited.contains(tp)) return null;

        BlockState state = world.getBlockState(pos);
        if (!state.is(this)) return null;

        if (isCore(state)) return pos;

        Direction dir = getDummyFacing(state);
        BlockPos nextPos = pos.relative(dir);

        visited.add(tp);
        return findCoreRec(world, nextPos, visited);
    }

    // ---------- Размещение ----------
    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (!(placer instanceof Player player)) return;
        if (level.isClientSide) return;

        level.removeBlock(pos, false);

        int yaw = Math.floorMod((int) player.getYRot(), 360);
        Direction dir = getDirModified(Direction.fromYRot(yaw));

        int offset = -getZOffset();
        int heightOffset = getHeightOffset();
        BlockPos corePos = pos.relative(dir, offset).above(heightOffset);

        if (!checkRequirement(level, pos, corePos, dir)) {
            if (!player.isCreative()) {
                ItemStack dropStack = stack.copy();
                dropStack.setCount(1);
                ItemEntity itemEntity = new ItemEntity(level,
                        pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
                level.addFreshEntity(itemEntity);
            }
            return;
        }

        BlockState coreState = getCoreStateForPlacement(dir);
        level.setBlock(corePos, coreState, 3);
        IPersistentNBT.restoreData(level, corePos, stack);

        fillSpace(level, pos, corePos, dir);

        NEW_STRUCTURES.add(corePos);
        level.scheduleTick(corePos, this, 100);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);
        NEW_STRUCTURES.remove(pos);
    }

    protected BlockState getCoreStateForPlacement(Direction facing) {
        return getCoreState(facing);
    }

    public boolean checkRequirement(Level level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        return MultiblockUtil.checkSpace(level, corePos, getDimensions(), originalPos, dir);
    }

    protected void fillSpace(LevelAccessor level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        MultiblockUtil.fillSpace(level, corePos, getDimensions(), this, dir);
    }

    public void placeMultiblock(LevelAccessor level, BlockPos corePos, Direction facing) {
        fillSpace(level, corePos, corePos, facing);
    }

    protected Direction getDirModified(Direction dir) {
        return dir;
    }

    // ---------- Взаимодействие ----------
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        return standardOpenBehavior(level, pos, player, getGuiID()) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    protected boolean standardOpenBehavior(Level level, BlockPos pos, Player player, int guiId) {
        if (level.isClientSide) return true;
        if (!player.isShiftKeyDown()) {
            BlockPos core = findCore(level, pos);
            if (core == null) return false;
            if (level.getBlockEntity(core) instanceof net.minecraft.world.MenuProvider provider) {
                NetworkHooks.openScreen((ServerPlayer) player, provider, core);
                return true;
            }
        }
        return true;
    }

    protected abstract int getGuiID();

    // ---------- Разрушение ----------
    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (state.is(newState.getBlock())) return;
        if (safeRem) return;

        if (isCore(state)) {
            handleDrops(level, pos, state);
            removeAllDummies(level, pos, state.getValue(FACING));
            level.removeBlockEntity(pos);
            level.removeBlock(pos, false);
        } else {
            Direction dir = getDummyFacing(state);
            BlockPos corePos = pos.relative(dir);
            if (level.getBlockState(corePos).is(this)) {
                level.removeBlock(corePos, false);
            }
        }
    }

    protected void handleDrops(Level level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be != null) {
            be.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(handler -> {
                for (int i = 0; i < handler.getSlots(); i++) {
                    ItemStack stack = handler.getStackInSlot(i);
                    if (!stack.isEmpty()) {
                        Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    }
                }
            });
        }
    }

    protected void removeAllDummies(Level level, BlockPos corePos, Direction facing) {
        int[] dim = getDimensions();
        int[] rot = MultiblockUtil.rotate(dim, facing);

        for (int a = corePos.getX() - rot[4]; a <= corePos.getX() + rot[5]; a++) {
            for (int b = corePos.getY() - rot[1]; b <= corePos.getY() + rot[0]; b++) {
                for (int c = corePos.getZ() - rot[2]; c <= corePos.getZ() + rot[3]; c++) {
                    BlockPos p = new BlockPos(a, b, c);
                    if (p.equals(corePos)) continue;
                    if (level.getBlockState(p).is(this)) {
                        level.removeBlockEntity(p);
                        level.removeBlock(p, false);
                    }
                }
            }
        }
    }

    // ---------- Рендер и формы ----------
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    public VoxelShape getDetailedShape(BlockState state, CollisionContext context) {
        if (bounding.isEmpty()) return Shapes.block();
        VoxelShape shape = Shapes.empty();
        Direction rot = state.getValue(FACING);
        for (AABB aabb : bounding) {
            AABB rotated = getAABBRotationOffset(aabb, 0, 0, 0, rot);
            shape = Shapes.or(shape, Shapes.create(rotated));
        }
        return shape.optimize();
    }

    protected List<AABB> bounding = new ArrayList<>();

    public boolean useDetailedHitbox() {
        return !bounding.isEmpty();
    }

    @Nullable
    private VoxelShape getMultiblockShape(BlockGetter level, BlockPos pos, BlockState state, CollisionContext context) {
        if (useDetailedHitbox() && level instanceof LevelAccessor accessor) {
            BlockPos core = findCore(accessor, pos);
            if (core != null) {
                return getDetailedShape(state, context);
            }
        }
        return null;
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        VoxelShape shape = getMultiblockShape(level, pos, state, context);
        return shape != null ? shape : super.getShape(state, level, pos, context);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        VoxelShape shape = getMultiblockShape(level, pos, state, context);
        return shape != null ? shape : super.getCollisionShape(state, level, pos, context);
    }

    public static AABB getAABBRotationOffset(AABB aabb, double x, double y, double z, Direction dir) {
        return switch (dir) {
            case SOUTH -> new AABB(-aabb.maxX, aabb.minY, -aabb.maxZ, -aabb.minX, aabb.maxY, -aabb.minZ).move(x, y, z);
            case EAST -> new AABB(aabb.minZ, aabb.minY, -aabb.maxX, aabb.maxZ, aabb.maxY, -aabb.minX).move(x, y, z);
            case WEST -> new AABB(-aabb.maxZ, aabb.minY, aabb.minX, -aabb.minZ, aabb.maxY, aabb.maxX).move(x, y, z);
            case NORTH, UP, DOWN -> aabb.move(x, y, z);
        };
    }

    // ---------- ICopiable ----------
    @Override
    public CompoundTag getSettings(Level world, BlockPos pos) {
        BlockPos core = findCore(world, pos);
        if (core != null && world.getBlockEntity(core) instanceof ICopiable copiable) {
            return copiable.getSettings(world, core);
        }
        return null;
    }

    @Override
    public void pasteSettings(CompoundTag nbt, int index, Level world, Player player, BlockPos pos) {
        BlockPos core = findCore(world, pos);
        if (core != null && world.getBlockEntity(core) instanceof ICopiable copiable) {
            copiable.pasteSettings(nbt, index, world, player, core);
        }
    }

    @Override
    public String[] infoForDisplay(Level world, BlockPos pos) {
        BlockPos core = findCore(world, pos);
        if (core != null && world.getBlockEntity(core) instanceof ICopiable copiable) {
            return copiable.infoForDisplay(world, core);
        }
        return null;
    }

    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    // ---------- Абстрактные методы ----------
    public abstract int[] getDimensions();
    public abstract int getZOffset();
    public int getHeightOffset() { return 0; }

    // ---------- Утилиты ----------
    public void makeExtra(LevelAccessor world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.is(this)) return;
        if (state.getValue(DUMMY_STATE) != 0) return;

        setSafeRem(true);
        BlockState newState = state.setValue(DUMMY_STATE, 1);
        world.setBlock(pos, newState, 3);

        // В генерации block entity ещё нет: WorldGenRegion.setBlock кладёт на заглушку
        // отложенный тег DUMMY, а getBlockEntity сразу после него пишет
        // «Tried to access a block entity before it was created». Прокси-сущности там
        // не бывает вовсе, поэтому тег снимаем, а BE не спрашиваем (#204).
        if (world instanceof Level level && !level.isClientSide) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TileEntityProxyBase proxy) {
                proxy.setExtra(true);
            }
        } else {
            world.getChunk(pos).removeBlockEntity(pos);
        }
        setSafeRem(false);
    }

    /**
     * Возвращает боксы для подсветки мультиблока (мировые координаты).
     * Наследники могут переопределять для своей формы.
     */
    public List<AABB> getHighlightBoxes(Level level, BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();
        int[] dim = getDimensions();
        double minX = -dim[4];
        double maxX = dim[5] + 1;
        double minY = -dim[1];
        double maxY = dim[0] + 1;
        double minZ = -dim[2];
        double maxZ = dim[3] + 1;

        AABB box;
        switch (rotation) {
            case WEST, EAST -> box = new AABB(minZ, minY, minX, maxZ, maxY, maxX)
                    .move(corePos.getX(), corePos.getY(), corePos.getZ());
            default -> box = new AABB(minX, minY, minZ, maxX, maxY, maxZ)
                    .move(corePos.getX(), corePos.getY(), corePos.getZ());
        }
        boxes.add(box);
        return boxes;
    }

    public void removeExtra(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (!state.is(this)) return;
        if (state.getValue(DUMMY_STATE) != 1) return;

        setSafeRem(true);
        BlockState newState = state.setValue(DUMMY_STATE, 0);
        world.setBlock(pos, newState, 3);

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TileEntityProxyBase proxy) {
            proxy.setExtra(false);
        }
        setSafeRem(false);
    }

    public boolean hasExtra(BlockState state) { return isExtra(state); }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.setValue(FACING, mirror.mirror(state.getValue(FACING)));
    }
}