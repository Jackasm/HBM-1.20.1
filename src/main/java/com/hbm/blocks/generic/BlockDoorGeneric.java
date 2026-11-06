package com.hbm.blocks.generic;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.MultiblockUtil;
import com.hbm.interfaces.IBomb;

import com.hbm.tileentity.DoorDecl;
import com.hbm.tileentity.TileEntityDoorGeneric;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class BlockDoorGeneric extends BlockDummyable implements IBomb, IToolable {

    public final DoorDecl type;

    public BlockDoorGeneric(Properties properties, DoorDecl type) {
        super(properties);
        this.type = type;
    }

    // ---------- BlockDummyable ----------

    @Override
    public int[] getDimensions() {
        return type.getDimensions();
    }

    @Override
    public int getZOffset() {
        return type.getBlockOffset();
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityDoorGeneric(pos, state);
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level,
                                                                  @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == com.hbm.tileentity.ModTileEntity.DOOR_GENERIC.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityDoorGeneric door) door.tick();
            };
        }
        return null;
    }

    // ---------- IBomb ----------

    @Override
    public BombReturnCode explode(Level level, BlockPos pos) {
        BlockPos core = findCore(level, pos);
        if (core == null) return BombReturnCode.ERROR_INCOMPATIBLE;

        BlockEntity te = level.getBlockEntity(core);
        if (!(te instanceof TileEntityDoorGeneric door)) return BombReturnCode.ERROR_INCOMPATIBLE;

        DoorDecl decl = door.getDoorType();
        if (decl == null || !decl.remoteControllable()) return BombReturnCode.ERROR_INCOMPATIBLE;

        if (door.tryToggle(null)) return BombReturnCode.TRIGGERED;
        return BombReturnCode.ERROR_INCOMPATIBLE;
    }

    // ---------- Interaction ----------

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand,
                                          @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(core);
        if (!(te instanceof TileEntityDoorGeneric door)) return InteractionResult.PASS;

        DoorDecl decl = door.getDoorType();
        if (decl == null) return InteractionResult.PASS;

        // Раскомментируй, если у тебя есть ULTRA_LARP_MODE
        // if (!ServerConfig.ULTRA_LARP_MODE.get() || decl.neverDemandRedstone())
        if (door.tryToggle(player)) return InteractionResult.CONSUME;

        return InteractionResult.PASS;
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side,
                           float fX, float fY, float fZ, IToolable.ToolType tool) {
        if (tool != IToolable.ToolType.SCREWDRIVER || !player.isShiftKeyDown()) return false;

        BlockPos core = findCore(level, pos);
        if (core == null) return false;

        BlockEntity te = level.getBlockEntity(core);
        if (!(te instanceof TileEntityDoorGeneric door)) return false;

        DoorDecl decl = door.getDoorType();
        if (decl == null || !decl.hasSkins()) return false;

        if (level.isClientSide) return true;
        door.cycleSkinIndex();
        return true;
    }

    // ---------- Ladder / pathfinding ----------

    @Override
    public boolean isLadder(BlockState state, net.minecraft.world.level.LevelReader level, BlockPos pos, LivingEntity entity) {
        BlockEntity te = level.getBlockEntity(pos);
        boolean open = hasExtra(state) || (te instanceof TileEntityDoorGeneric door && door.shouldUseBB);
        return type.isLadder(open);
    }

    @Override
    public boolean isPathfindable(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos,
                                  @NotNull PathComputationType type) {
        return hasExtra(state);
    }

    // ---------- Collision ----------

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return shapeFor(state, level, pos, false);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return shapeFor(state, level, pos, true);
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getVisualShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                              @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.block();
    }

    private VoxelShape shapeFor(BlockState state, BlockGetter level, BlockPos pos, boolean forCollision) {

        if (isDummy(state)) {
            return Shapes.block();
        }

        if (!(level instanceof LevelAccessor accessor)) {
            return Shapes.block();
        }

        BlockPos core = findCore(accessor, pos);
        if (core == null) {
            return Shapes.block();
        }

        BlockState coreState = level.getBlockState(core);
        Direction dir = coreState.hasProperty(FACING) ? coreState.getValue(FACING) : Direction.NORTH;

        int lx = pos.getX() - core.getX();
        int ly = pos.getY() - core.getY();
        int lz = pos.getZ() - core.getZ();

        int[] rotated = rotateLocal(lx, ly, lz, dir);

        boolean open;
        if (isExtra(state)) {
            open = true;
        } else if (isCore(state)) {
            BlockEntity te = level.getBlockEntity(pos);
            open = te instanceof TileEntityDoorGeneric door
                    && door.state != TileEntityDoorGeneric.STATE_CLOSED;
        } else {
            open = false;
        }

        AABB box = type.getBlockBound(rotated[0], rotated[1], rotated[2], open, forCollision);
        if (box == null) return Shapes.empty();

        if (box.getXsize() == 0 && box.getYsize() == 0 && box.getZsize() == 0) {
            return Shapes.empty();
        }

        AABB local = switch (dir) {
            case SOUTH -> new AABB(
                    1 - box.maxX, box.minY, 1 - box.maxZ,
                    1 - box.minX, box.maxY, 1 - box.minZ);
            case WEST -> new AABB(
                    1 - box.minZ, box.minY, box.minX,
                    1 - box.maxZ, box.maxY, box.maxX);
            case NORTH -> new AABB(
                    box.minX, box.minY, box.minZ,
                    box.maxX, box.maxY, box.maxZ);
            case EAST -> new AABB(
                    box.minZ, box.minY, 1 - box.maxX,
                    box.maxZ, box.maxY, 1 - box.minX);
            default -> new AABB(0, 0, 0, 1, 1, 1);
        };
        return Shapes.create(local);
    }


    // ---------- Neighbor ----------

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide) {
            BlockPos core = findCore(level, pos);
            if (core != null) {
                BlockEntity te = level.getBlockEntity(core);
                if (te instanceof TileEntityDoorGeneric door) {
                    door.updateRedstonePower(pos);
                }
            }
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }

    // ---------- AABB ----------

    @Nullable
    public AABB getBoundingBox(BlockGetter level, BlockPos pos, BlockState state, boolean forCollision) {

        if (!(level instanceof LevelAccessor accessor)) {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        }

        BlockEntity te = level.getBlockEntity(pos);
        BlockPos core = findCore(accessor, pos);

        boolean open = hasExtra(state)
                || (te instanceof TileEntityDoorGeneric door && door.state != TileEntityDoorGeneric.STATE_CLOSED);

        if (core == null) {
            return new AABB(pos.getX(), pos.getY(), pos.getZ(),
                    pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1);
        }

        BlockState coreState = level.getBlockState(core);
        Direction dir = coreState.hasProperty(FACING) ? coreState.getValue(FACING) : Direction.NORTH;

        int lx = pos.getX() - core.getX();
        int ly = pos.getY() - core.getY();
        int lz = pos.getZ() - core.getZ();

        int[] rotated = rotateLocal(lx, ly, lz, dir);

        AABB box = type.getBlockBound(rotated[0], rotated[1], rotated[2], open, forCollision);
        if (box == null) return null;

        double x = pos.getX();
        double y = pos.getY();
        double z = pos.getZ();

        return switch (dir) {
            case SOUTH -> new AABB(
                    x + 1 - box.maxX, y + box.minY, z + 1 - box.maxZ,
                    x + 1 - box.minX, y + box.maxY, z + 1 - box.minZ);
            case WEST -> new AABB(
                    x + 1 - box.minZ, y + box.minY, z + box.minX,
                    x + 1 - box.maxZ, y + box.maxY, z + box.maxX);
            case NORTH -> new AABB(
                    x + box.minX, y + box.minY, z + box.minZ,
                    x + box.maxX, y + box.maxY, z + box.maxZ);
            case EAST -> new AABB(
                    x + box.minZ, y + box.minY, z + 1 - box.maxX,
                    x + box.maxZ, y + box.maxY, z + 1 - box.minX);
            default -> new AABB(x, y, z, x + 1, y + 1, z + 1);
        };
    }

    private int[] rotateLocal(int lx, int ly, int lz, Direction dir) {
        return switch (dir) {
            case NORTH -> new int[]{lx, ly, lz};
            case SOUTH -> new int[]{-lx, ly, -lz};
            case EAST  -> new int[]{-lz, ly, lx};
            case WEST  -> new int[]{lz, ly, -lx};
            default -> new int[]{lx, ly, lz};
        };
    }

    // ---------- Multiblock ----------

    @Override
    public boolean checkRequirement(Level level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        if (!super.checkRequirement(level, originalPos, corePos, dir)) return false;

        if (type.getExtraDimensions() != null) {
            for (int[] dims : type.getExtraDimensions()) {
                if (!com.hbm.blocks.MultiblockUtil.checkSpace(level, corePos, dims, originalPos, dir)) return false;
            }
        }
        return true;
    }

    @Override
    protected void fillSpace(LevelAccessor level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, originalPos, corePos, dir);

        if (type.getExtraDimensions() != null) {
            for (int[] dims : type.getExtraDimensions()) {
                com.hbm.blocks.MultiblockUtil.fillSpace(level, corePos, dims, this, dir);
            }
        }
    }

    public static Properties createProperties(float hardness, float resistance) {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(hardness, resistance)
                .sound(SoundType.METAL)
                .noOcclusion()
                .pushReaction(PushReaction.BLOCK);
    }

    @Override
    protected Direction getDirModified(Direction dir) {
        if (type == DoorDecl.SILO_HATCH || type == DoorDecl.SILO_HATCH_LARGE) {
            return dir.getOpposite();
        }
        return super.getDirModified(dir);
    }

    @Override
    public List<AABB> getHighlightBoxes(Level level, BlockPos corePos, Direction rotation) {
        // VAULT_DOOR, SILO_HATCH, SILO_HATCH_LARGE — большая обводка по габаритам мультиблока
        if (type == DoorDecl.VAULT_DOOR
                || type == DoorDecl.SILO_HATCH
                || type == DoorDecl.SILO_HATCH_LARGE) {
            return getBigHighlightBoxes(corePos, rotation);
        }

        // Остальные — тонкая обводка по форме блоков
        return getThinHighlightBoxes(level, corePos, rotation);
    }

    /** Большая обводка по габаритам мультиблока (с учётом extraDimensions). */
    private List<AABB> getBigHighlightBoxes(BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();

        int[] dim = MultiblockUtil.rotate(getDimensions(), rotation);
        boxes.add(new AABB(
                corePos.getX() - dim[4],        corePos.getY() - dim[1],        corePos.getZ() - dim[2],
                corePos.getX() + dim[5] + 1,    corePos.getY() + dim[0] + 1,    corePos.getZ() + dim[3] + 1));

        if (type.getExtraDimensions() != null) {
            for (int[] extra : type.getExtraDimensions()) {
                int[] rotE = MultiblockUtil.rotate(extra, rotation);
                boxes.add(new AABB(
                        corePos.getX() - rotE[4],      corePos.getY() - rotE[1],      corePos.getZ() - rotE[2],
                        corePos.getX() + rotE[5] + 1,  corePos.getY() + rotE[0] + 1,  corePos.getZ() + rotE[3] + 1));
            }
        }

        return boxes;
    }

    /** Тонкая обводка: объединяет визуальные боксы всех блоков двери. */
    private List<AABB> getThinHighlightBoxes(Level level, BlockPos corePos, Direction rotation) {
        BlockEntity coreTe = level.getBlockEntity(corePos);
        boolean open = coreTe instanceof TileEntityDoorGeneric door
                && door.state != TileEntityDoorGeneric.STATE_CLOSED;

        int[] dim = MultiblockUtil.rotate(getDimensions(), rotation);

        double minX = Double.POSITIVE_INFINITY, minY = Double.POSITIVE_INFINITY, minZ = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY, maxY = Double.NEGATIVE_INFINITY, maxZ = Double.NEGATIVE_INFINITY;
        boolean any = false;

        for (int dx = -dim[4]; dx <= dim[5]; dx++) {
            for (int dy = -dim[1]; dy <= dim[0]; dy++) {
                for (int dz = -dim[2]; dz <= dim[3]; dz++) {
                    BlockPos p = corePos.offset(dx, dy, dz);
                    BlockState s = level.getBlockState(p);
                    if (!(s.getBlock() instanceof BlockDoorGeneric)) continue;

                    int[] local = rotateLocal(dx, dy, dz, rotation);
                    AABB localBox = type.getBlockBound(local[0], local[1], local[2], open, false);
                    if (localBox == null) continue;
                    if (localBox.getXsize() == 0 && localBox.getYsize() == 0 && localBox.getZsize() == 0) continue;

                    AABB worldBox = switch (rotation) {
                        case NORTH -> new AABB(
                                p.getX() + localBox.minX, p.getY() + localBox.minY, p.getZ() + localBox.minZ,
                                p.getX() + localBox.maxX, p.getY() + localBox.maxY, p.getZ() + localBox.maxZ);
                        case SOUTH -> new AABB(
                                p.getX() + 1 - localBox.maxX, p.getY() + localBox.minY, p.getZ() + 1 - localBox.maxZ,
                                p.getX() + 1 - localBox.minX, p.getY() + localBox.maxY, p.getZ() + 1 - localBox.minZ);
                        case EAST -> new AABB(
                                p.getX() + localBox.minZ, p.getY() + localBox.minY, p.getZ() + 1 - localBox.maxX,
                                p.getX() + localBox.maxZ, p.getY() + localBox.maxY, p.getZ() + 1 - localBox.minX);
                        case WEST -> new AABB(
                                p.getX() + 1 - localBox.minZ, p.getY() + localBox.minY, p.getZ() + localBox.minX,
                                p.getX() + 1 - localBox.maxZ, p.getY() + localBox.maxY, p.getZ() + localBox.maxX);
                        default -> new AABB(
                                p.getX() + localBox.minX, p.getY() + localBox.minY, p.getZ() + localBox.minZ,
                                p.getX() + localBox.maxX, p.getY() + localBox.maxY, p.getZ() + localBox.maxZ);
                    };

                    minX = Math.min(minX, worldBox.minX);
                    minY = Math.min(minY, worldBox.minY);
                    minZ = Math.min(minZ, worldBox.minZ);
                    maxX = Math.max(maxX, worldBox.maxX);
                    maxY = Math.max(maxY, worldBox.maxY);
                    maxZ = Math.max(maxZ, worldBox.maxZ);
                    any = true;
                }
            }
        }

        return any ? List.of(new AABB(minX, minY, minZ, maxX, maxY, maxZ)) : List.of();
    }

    @Override
    protected void removeAllDummies(Level level, BlockPos corePos, Direction facing) {
        // Стандартные dummy
        super.removeAllDummies(level, corePos, facing);

        // Extra dimensions (например, у VAULT_DOOR)
        if (type.getExtraDimensions() != null) {
            for (int[] dims : type.getExtraDimensions()) {
                removeDummiesForDims(level, corePos, facing, dims);
            }
        }
    }

    private void removeDummiesForDims(Level level, BlockPos corePos, Direction facing, int[] dim) {
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
}