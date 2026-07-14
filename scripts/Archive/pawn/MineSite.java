package com.hbm.pawn;

import com.hbm.blocks.ModBlocks;
import com.hbm.entity.mob.PawnEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.*;

import static com.hbm.blocks.ModBlockTags.ALL_ORES;

public class MineSite implements IPawnServicable, INBTSerializable<CompoundTag> {

    public enum Stage {
        DIGGING,
        CLOSED
    }

    private static final Set<Block> requiresSupport = new HashSet<>(Arrays.asList(
            Blocks.LADDER,
            Blocks.WALL_TORCH
    ));

    private record DeferredBlock(BlockPos pos, BlockState state) {}

    private UUID ownerUUID;
    private BlockPos stairwellPos;    // абсолютная позиция, куда ставится stairwell
    private BlockPos workPos;         // рабочая точка пешки
    private BlockPos chestPos;        // позиция сундука
    private Stage stage;
    private Rotation rotation;

    private int currentSegmentY;
    private int currentWorldLayer;
    private int digCooldown = 0;
    private StructureTemplate stairwellTemplate;
    private transient Level serviceLevel;
    private int segmentHeight;

    private static final int DIG_INTERVAL = 5;
    private static final boolean INVERT_MODEL_LAYERS = true;

    public MineSite(UUID owner, BlockPos stairwellPos, BlockPos workPos, BlockPos chestPos, Level level, Rotation rotation) {
        this.ownerUUID = owner;
        this.stairwellPos = stairwellPos;
        this.workPos = workPos;
        this.chestPos = chestPos;
        this.serviceLevel = level;
        this.stage = Stage.DIGGING;
        this.rotation = rotation != null ? rotation : Rotation.NONE;
    }

    public MineSite() {}

    public boolean init(ServerLevel level) {
        stairwellTemplate = StructureLoader.loadTemplate(level, "hbm", "mine_stairwell");
        if (stairwellTemplate == null) return false;
        segmentHeight = stairwellTemplate.getSize().getY();
        currentSegmentY = stairwellPos.getY();
        currentWorldLayer = 0;
        stage = Stage.DIGGING;
        digCooldown = 0;
        return true;
    }

    private BlockPos getWorldPosForLayer(int layerOffset) {
        return new BlockPos(stairwellPos.getX(), currentSegmentY - layerOffset, stairwellPos.getZ());
    }

    private BlockPos rotateLocalPosition(BlockPos pos) {
        if (rotation == Rotation.NONE) return pos;
        int x = pos.getX();
        int z = pos.getZ();
        int width = stairwellTemplate.getSize().getX();
        int depth = stairwellTemplate.getSize().getZ();
        return switch (rotation) {
            case CLOCKWISE_90 -> new BlockPos(z, pos.getY(), width - 1 - x);
            case CLOCKWISE_180 -> new BlockPos(width - 1 - x, pos.getY(), depth - 1 - z);
            case COUNTERCLOCKWISE_90 -> new BlockPos(depth - 1 - z, pos.getY(), x);
            default -> pos;
        };
    }

    private BlockState getBlockStateFromTemplate(int localX, int localY, int localZ) {
        BlockPos rot = rotateLocalPosition(new BlockPos(localX, localY, localZ));
        return StructureLoader.getBlockStateFromTemplate(stairwellTemplate, rot);
    }

    private BlockPos getWorldPos(int localX, int localY, int localZ) {
        BlockPos rotatedLocal = rotateLocalPosition(new BlockPos(localX, localY, localZ));
        return new BlockPos(
                stairwellPos.getX() + rotatedLocal.getX(),
                currentSegmentY - localY,
                stairwellPos.getZ() + rotatedLocal.getZ()
        );
    }

    private boolean isOnWallPosition(int localX, int localZ, int width, int depth) {
        BlockPos rotated = rotateLocalPosition(new BlockPos(localX, 0, localZ));
        int rx = rotated.getX();
        int rz = rotated.getZ();
        return rx == 0 || rx == width - 1 || rz == 0 || rz == depth - 1;
    }

    public boolean performDigging(PawnEntity pawn) {
        if (stage != Stage.DIGGING) return false;
        BlockPos checkPos = getWorldPosForLayer(segmentHeight - 1);
        if (checkPos.getY() <= pawn.level().getMinBuildHeight() + 1) {
            stage = Stage.CLOSED;
            return false;
        }
        if (isInventoryFull(pawn)) {
            unloadToChest(pawn, (ServerLevel) pawn.level());
            return true;
        }
        if (digCooldown > 0) {
            digCooldown--;
            return true;
        }

        // Всегда обрабатываем слой (даже если ничего не копали)
        processCurrentLayer(pawn, (ServerLevel) pawn.level());

        // Всегда переходим к следующему слою
        currentWorldLayer++;
        if (currentWorldLayer >= segmentHeight) {
            currentSegmentY -= segmentHeight;
            currentWorldLayer = 0;
        }
        digCooldown = DIG_INTERVAL;

        return true;
    }

    private void processCurrentLayer(PawnEntity pawn, ServerLevel level) {
        List<DeferredBlock> deferredBlocks = new ArrayList<>();
        Vec3i size = stairwellTemplate.getSize();
        int width = size.getX();
        int depth = size.getZ();
        int modelLayer = INVERT_MODEL_LAYERS ? (segmentHeight - 1) - currentWorldLayer : currentWorldLayer;

        // ЭТАП 1: Обрабатываем только периметр (стены) - затыкаем течи и выкапываем руды
        for (int localX = 0; localX < width; localX++) {
            for (int localZ = 0; localZ < depth; localZ++) {
                // Пропускаем внутренние блоки (не на стене)
                if (!isOnWallPosition(localX, localZ, width, depth)) continue;

                BlockPos worldPos = getWorldPos(localX, currentWorldLayer, localZ);
                BlockState worldState = level.getBlockState(worldPos);

                // 1. Затыкаем течи снаружи
                for (Direction dir : getExternalDirections(localX, localZ, width, depth)) {
                    BlockPos neighbor = worldPos.relative(dir);
                    BlockState neighborState = level.getBlockState(neighbor);
                    if (isLiquid(neighborState) || isPermeableBlock(neighborState)) {
                        level.setBlock(neighbor, ModBlocks.SANDBAG.get().defaultBlockState(), 3);
                    }
                }

                // 2. Выкапываем руды за периметром
                for (Direction dir : getExternalDirections(localX, localZ, width, depth)) {
                    BlockPos neighbor = worldPos.relative(dir);
                    BlockState neighborState = level.getBlockState(neighbor);
                    if (isOre(neighborState)) {
                        mineOreVein(level, neighbor, pawn);
                    }
                }

                // 3. Обрабатываем сам блок на стене (жидкость или обычный блок)
                if (!worldState.isAir()) {
                    if (isLiquid(worldState)) {
                        level.setBlock(worldPos, Blocks.AIR.defaultBlockState(), 3);
                    } else {
                        for (ItemStack drop : Block.getDrops(worldState, level, worldPos, level.getBlockEntity(worldPos), pawn, pawn.getMainHandItem()))
                            addToChest(drop, level);
                        level.destroyBlock(worldPos, false);
                    }
                }
            }
        }

        // ЭТАП 2: Обрабатываем все блоки (включая внутреннюю часть) и ставим шаблон
        for (int localX = 0; localX < width; localX++) {
            for (int localZ = 0; localZ < depth; localZ++) {
                BlockPos worldPos = getWorldPos(localX, currentWorldLayer, localZ);
                BlockState worldState = level.getBlockState(worldPos);
                BlockState templateState = getBlockStateFromTemplate(localX, modelLayer, localZ);

                // Обрабатываем текущий блок (то, что внутри ствола)
                if (!worldState.isAir()) {
                    if (isLiquid(worldState)) {
                        level.setBlock(worldPos, Blocks.AIR.defaultBlockState(), 3);
                    } else {
                        for (ItemStack drop : Block.getDrops(worldState, level, worldPos, level.getBlockEntity(worldPos), pawn, pawn.getMainHandItem()))
                            addToChest(drop, level);
                        level.destroyBlock(worldPos, false);
                    }
                }

                // Устанавливаем блок из шаблона
                if (templateState != null && !templateState.isAir()) {
                    Block block = templateState.getBlock();
                    if (requiresSupport.contains(block)) {
                        deferredBlocks.add(new DeferredBlock(worldPos, templateState));
                    } else {
                        level.setBlock(worldPos, templateState, 3);
                    }
                }
            }
        }

        // Устанавливаем отложенные блоки (лестницы, факелы и т.д.)
        for (int pass = 0; pass < 3; pass++) {
            Iterator<DeferredBlock> iterator = deferredBlocks.iterator();
            while (iterator.hasNext()) {
                DeferredBlock deferred = iterator.next();
                if (hasSupport(level, deferred.pos, deferred.state)) {
                    level.setBlock(deferred.pos, deferred.state, 3);
                    iterator.remove();
                }
            }
            if (deferredBlocks.isEmpty()) break;
        }

        // Если после всех проходов остались блоки без опоры - ставим их в воздух
        for (DeferredBlock deferred : deferredBlocks) {
            level.setBlock(deferred.pos, Blocks.AIR.defaultBlockState(), 3);
        }
    }

    private boolean isPermeableBlock(BlockState state) {
        // Блоки, которые не занимают весь куб (положение, водные растения и т.д.)
        return !state.isSolid() && !state.isAir();
    }

    private List<Direction> getExternalDirections(int localX, int localZ, int width, int depth) {
        List<Direction> dirs = new ArrayList<>();
        // Поворачиваем локальные координаты
        BlockPos rotated = rotateLocalPosition(new BlockPos(localX, 0, localZ));
        int rx = rotated.getX();
        int rz = rotated.getZ();

        if (rx == 0) dirs.add(Direction.WEST);
        if (rx == width - 1) dirs.add(Direction.EAST);
        if (rz == 0) dirs.add(Direction.NORTH);
        if (rz == depth - 1) dirs.add(Direction.SOUTH);

        return dirs;
    }

    private boolean hasSupport(ServerLevel level, BlockPos pos, BlockState state) {
        Block block = state.getBlock();

        if (block == Blocks.LADDER) {
            Direction facing = state.getValue(LadderBlock.FACING);
            BlockPos supportPos = pos.relative(facing.getOpposite());
            return !level.getBlockState(supportPos).isAir();
        }

        if (block == Blocks.WALL_TORCH) {
            Direction facing = state.getValue(WallTorchBlock.FACING);
            BlockPos supportPos = pos.relative(facing.getOpposite());
            return !level.getBlockState(supportPos).isAir();
        }

        return !level.getBlockState(pos.below()).isAir();
    }

    private boolean isOre(BlockState state) {
        return state.is(ALL_ORES);
    }

    private void mineOreVein(ServerLevel level, BlockPos startPos, PawnEntity pawn) {
        Set<BlockPos> vein = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(startPos);
        vein.add(startPos);

        while (!queue.isEmpty()) {
            BlockPos pos = queue.poll();
            BlockState state = level.getBlockState(pos);
            if (!isOre(state)) continue;
            for (Direction dir : Direction.values()) {
                BlockPos neighbor = pos.relative(dir);
                if (!vein.contains(neighbor) && isOre(level.getBlockState(neighbor))) {
                    vein.add(neighbor);
                    queue.add(neighbor);
                }
            }
        }

        for (BlockPos pos : vein) {
            BlockState state = level.getBlockState(pos);
            if (!state.isAir()) {
                for (ItemStack drop : Block.getDrops(state, level, pos, level.getBlockEntity(pos), pawn, pawn.getMainHandItem()))
                    addToChest(drop, level);
                level.destroyBlock(pos, false);
            }
        }
    }

    private void addToChest(ItemStack stack, ServerLevel level) {
        if (chestPos == null) return;
        BlockEntity be = level.getBlockEntity(chestPos);
        if (be == null) return;
        IItemHandler chestHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        ItemStack remaining = ItemHandlerHelper.insertItemStacked(chestHandler, stack, false);
        if (!remaining.isEmpty()) Block.popResource(level, chestPos, remaining);
    }

    private boolean isLiquid(BlockState state) {
        return state.getBlock() == Blocks.WATER || state.getBlock() == Blocks.LAVA;
    }

    private boolean isInventoryFull(PawnEntity pawn) {
        int freeSlots = 0;
        for (int i = 0; i < pawn.getInventory().getContainerSize(); i++)
            if (pawn.getInventory().getItem(i).isEmpty()) freeSlots++;
        return freeSlots < 2;
    }

    private void unloadToChest(PawnEntity pawn, ServerLevel level) {
        if (chestPos == null) return;
        BlockEntity be = level.getBlockEntity(chestPos);
        if (be == null) return;
        IItemHandler chestHandler = be.getCapability(ForgeCapabilities.ITEM_HANDLER).orElse(null);
        for (int i = 0; i < pawn.getInventory().getContainerSize(); i++) {
            ItemStack stack = pawn.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                ItemStack remaining = ItemHandlerHelper.insertItemStacked(chestHandler, stack, false);
                pawn.getInventory().setItem(i, remaining);
            }
        }
    }

    public void setServiceLevel(Level level) {
        this.serviceLevel = level;
        if (level instanceof ServerLevel serverLevel && stairwellTemplate == null) {
            stairwellTemplate = StructureLoader.loadTemplate(serverLevel, "hbm", "mine_stairwell");
            if (stairwellTemplate != null) segmentHeight = stairwellTemplate.getSize().getY();
        }
    }

    // IPawnServicable
    @Override public ServicableType getServicableType() { return ServicableType.MINE; }
    @Override public boolean needsService() { return stage == Stage.DIGGING; }
    @Override public BlockPos getPosition() { return workPos; }
    @Override public Level getServiceLevel() { return serviceLevel; }
    @Override public boolean executeService(PawnEntity pawn) { return performDigging(pawn); }
    @Override public UUID getOwnerUUID() { return ownerUUID; }
    @Override public void setOwnerUUID(UUID owner) { this.ownerUUID = owner; }

    public void setChestPos(BlockPos chest) { this.chestPos = chest; }
    public BlockPos getChestPos() { return chestPos; }

    // NBT
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putUUID("owner", ownerUUID);
        tag.put("stairwellPos", NbtUtils.writeBlockPos(stairwellPos));
        tag.put("workPos", NbtUtils.writeBlockPos(workPos));
        if (chestPos != null) tag.put("chestPos", NbtUtils.writeBlockPos(chestPos));
        if (rotation != null) tag.putString("rotation", rotation.name());
        tag.putString("stage", stage.name());
        tag.putInt("currentSegmentY", currentSegmentY);
        tag.putInt("currentWorldLayer", currentWorldLayer);
        tag.putInt("digCooldown", digCooldown);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        ownerUUID = tag.getUUID("owner");
        stairwellPos = NbtUtils.readBlockPos(tag.getCompound("stairwellPos"));
        workPos = NbtUtils.readBlockPos(tag.getCompound("workPos"));
        if (tag.contains("chestPos")) chestPos = NbtUtils.readBlockPos(tag.getCompound("chestPos"));
        if (tag.contains("rotation")) rotation = Rotation.valueOf(tag.getString("rotation"));
        else rotation = Rotation.NONE;
        stage = Stage.valueOf(tag.getString("stage"));
        currentSegmentY = tag.getInt("currentSegmentY");
        currentWorldLayer = tag.getInt("currentWorldLayer");
        digCooldown = tag.getInt("digCooldown");
    }
}