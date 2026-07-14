package com.hbm.blocks;

import com.hbm.handler.ThreeInts;
import com.hbm.interfaces.ICopiable;
import com.hbm.items.ModItems;
import com.hbm.tileentity.IPersistentNBT;
import com.hbm.datagen.worldgen.nbt.INBTBlockTransformable;
import com.hbm.tileentity.TileEntityProxyBase;
import com.hbm.tileentity.machine.TileEntityHeatBoiler;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class BlockDummyable extends BaseEntityBlock
        implements ICustomBlockHighlight, ICopiable, INBTBlockTransformable {

    // Свойства состояния блока
    public static final IntegerProperty DUMMY_STATE = IntegerProperty.create("dummy_state", 0, 2); // 0=dummy,1=extra,2=core
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    protected static final Set<BlockPos> NEW_STRUCTURES = new HashSet<>();


    // Статический флаг безопасного удаления (как в оригинале)
    public static boolean safeRem = false;

    public abstract ItemStack getMachineItem();

    public static void setSafeRem(boolean value) {
        safeRem = value;
    }

    public static boolean isSafeRem() {
        return safeRem;
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

    /**
     * Для dummy-блоков возвращает направление к ядру (противоположно FACING)
     */
    protected Direction getDummyFacing(BlockState state) {
        return state.getValue(FACING);
    }

    /**
     * Создаёт состояние для dummy-блока с заданным направлением (от ядра к заглушке)
     */
    public BlockState getDummyState(Direction facing) {
        return defaultBlockState()
                .setValue(DUMMY_STATE, 0)
                .setValue(FACING, facing.getOpposite());
    }

    /**
     * Создаёт состояние для extra-блока
     */
    public BlockState getExtraState(Direction facing) {
        return defaultBlockState()
                .setValue(DUMMY_STATE, 1)
                .setValue(FACING, facing.getOpposite());
    }

    /**
     * Создаёт состояние для ядра
     */
    public BlockState getCoreState(Direction facing) {
        return defaultBlockState()
                .setValue(DUMMY_STATE, 2)
                .setValue(FACING, facing);
    }

    // ---------- Поиск ядра ----------
    private final List<ThreeInts> positions = new ArrayList<>();

    public BlockPos findCore(LevelAccessor world, BlockPos pos) {
        positions.clear();
        return findCoreRec(world, pos);
    }

    private BlockPos findCoreRec(LevelAccessor world, BlockPos pos) {
        ThreeInts tp = new ThreeInts(pos.getX(), pos.getY(), pos.getZ());
        if (positions.contains(tp)) return null;

        BlockState state = world.getBlockState(pos);
        if (!state.is(this)) return null;

        if (isCore(state))
            return pos;

        Direction dir = getDummyFacing(state);
        BlockPos nextPos = pos.relative(dir);
        if (!world.isAreaLoaded(nextPos, 1)) return null;

        positions.add(tp);
        return findCoreRec(world, nextPos);
    }

    // ---------- Проверка целостности (сиротство) ----------
    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
        if (!level.isClientSide) {
            //destroyIfOrphan(level, pos, state);
        }
    }

    @Override
    public void randomTick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.randomTick(state, level, pos, random);
    }

    private void destroyIfOrphan(Level level, BlockPos pos, BlockState state) {
        if (isCore(state)) return; // ядро не бывает сиротой

        // Защита для новых структур (первые 5 секунд)
        BlockPos coreCandidate = findCore(level, pos);
        if (coreCandidate != null && NEW_STRUCTURES.contains(coreCandidate)) {
            return; // не удаляем, структура новая
        }

        Direction dir = getDummyFacing(state);
        BlockPos corePos = pos.relative(dir);

        if (!level.isAreaLoaded(corePos, 1)) return;

        BlockState coreState = level.getBlockState(corePos);

        if (!coreState.is(this) || !isCore(coreState)) {
            level.removeBlock(pos, false);
        }
    }


    // ---------- Размещение структуры ----------
    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        if (!(placer instanceof Player player)) return;

        if (level.isClientSide) {
            return;
        }

        // Удаляем временный блок, который поставил игрок (это заглушка, её заменят ядром)
        level.removeBlock(pos, false);

        // Определяем направление взгляда игрока
        int yaw = Math.floorMod((int) player.getYRot(), 360);
        Direction dir = Direction.fromYRot(yaw); // примерно

        // Модифицируем направление (если нужно)
        dir = getDirModified(dir);

        int offset = -getZOffset(); // отрицательное смещение (ядро сдвинуто назад)

        int heightOffset = getHeightOffset();
        BlockPos corePos = pos
                .relative(dir, offset)
                .above(heightOffset);

        if (!checkRequirement(level, pos, corePos, dir)) {
            if (!player.isCreative()) {
                ItemStack dropStack = stack.copy();
                dropStack.setCount(1);
                ItemEntity itemEntity = new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, dropStack);
                level.addFreshEntity(itemEntity);
            }
            return;
        }

        // Устанавливаем ядро
        BlockState coreState = getCoreStateForPlacement(level, corePos, dir, player, stack);
        level.setBlock(corePos, coreState, 3);
        // Восстанавливаем NBT данные TileEntity из предмета
        IPersistentNBT.restoreData(level, corePos, stack);

        // Заполняем пространство заглушками
        fillSpace(level, pos, corePos, dir);

        NEW_STRUCTURES.add(corePos);
        level.scheduleTick(corePos, this, 100);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        super.tick(state, level, pos, random);
        // Убираем защиту для запланированных блоков
        NEW_STRUCTURES.remove(pos);
    }

    /**
     * Возвращает состояние для ядра, учитывая возможные особенности размещения.
     * По умолчанию просто getCoreState(dir).
     */
    protected BlockState getCoreStateForPlacement(Level level, BlockPos corePos, Direction facing, Player player, ItemStack stack) {
        return getCoreState(facing);
    }

    protected boolean checkRequirement(Level level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        return MultiblockUtil.checkSpace(level, corePos, getDimensions(), originalPos, dir);
    }

    protected void fillSpace(Level level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        MultiblockUtil.fillSpace(level, corePos, getDimensions(), this, dir);
    }

    /**
     * Позволяет изменить направление размещения (например, ограничить)
     */
    protected Direction getDirModified(Direction dir) {
        return dir;
    }

    // ---------- Взаимодействие с игроком ----------
    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
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

    protected abstract int getGuiID(); // должен быть реализован в потомках, если используется

    // ---------- Разрушение ----------
    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            if (safeRem) {
                return;
            }
            if (isCore(state)) {
                dropInventoryAndItem(level, pos, state);
                removeAllDummies(level, pos, state.getValue(FACING));
            } else {
                Direction dir = getDummyFacing(state);
                BlockPos corePos = pos.relative(dir);
                if (level.getBlockState(corePos).is(this)) {
                    level.removeBlock(corePos, false);
                }
            }

        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    private void dropInventoryAndItem(Level level, BlockPos pos, BlockState state) {
        BlockEntity be = level.getBlockEntity(pos);
        if (be instanceof TileEntityHeatBoiler boiler && boiler.hasExploded) {
            // Взорванный бойлер дропает 4 слитка и 8 пластин
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(ModItems.INGOT_STEEL.get(), 4));
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(),
                    new ItemStack(ModItems.PLATE_COPPER.get(), 8));
        } else {
            // Обычный дроп - сам блок
            Block.popResource(level, pos, getMachineItem());
        }
    }


    protected void removeAllDummies(Level level, BlockPos corePos, Direction facing) {
        // Перебираем все возможные позиции заглушек и удаляем их
        int[] dim = getDimensions();
        int[] rot = MultiblockUtil.rotate(dim, facing);

        for (int a = corePos.getX() - rot[4]; a <= corePos.getX() + rot[5]; a++) {
            for (int b = corePos.getY() - rot[1]; b <= corePos.getY() + rot[0]; b++) {
                for (int c = corePos.getZ() - rot[2]; c <= corePos.getZ() + rot[3]; c++) {
                    BlockPos p = new BlockPos(a, b, c);
                    if (p.equals(corePos)) continue;
                    if (level.getBlockState(p).is(this)) {
                        level.removeBlock(p, false);
                    }
                }
            }
        }
    }

    // ---------- Рендер и формы ----------
    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.MODEL; // или INVISIBLE, если используется TESR
    }

    /**
     * Детализированная форма для коллизии/выделения (если нужна).
     * По умолчанию возвращает полную форму из bounding (см. ниже).
     */
    public VoxelShape getDetailedShape(BlockState state, BlockGetter level, BlockPos corePos, CollisionContext context) {
        if (bounding.isEmpty()) return Shapes.block();
        // Собираем из списка AABB (которые нужно будет преобразовать в VoxelShape)
        VoxelShape shape = Shapes.empty();
        Direction rot = state.getValue(FACING); // используем Direction напрямую
        for (AABB aabb : bounding) {
            AABB rotated = getAABBRotationOffset(aabb, 0, 0, 0, rot);
            shape = Shapes.or(shape, Shapes.create(rotated));
        }
        return shape.optimize();
    }

    // Для обратной совместимости с оригинальным bounding
    protected List<AABB> bounding = new ArrayList<>();

    public boolean useDetailedHitbox() {
        return !bounding.isEmpty();
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (useDetailedHitbox() && level instanceof LevelAccessor accessor) {
            BlockPos core = findCore(accessor, pos);
            if (core != null) {
                return getDetailedShape(state, level, core, context);
            }
        }
        return super.getShape(state, level, pos, context);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        if (useDetailedHitbox() && level instanceof LevelAccessor accessor) {
            BlockPos core = findCore(accessor, pos);
            if (core != null) {
                return getDetailedShape(state, level, core, context);
            }
        }
        return super.getCollisionShape(state, level, pos, context);
    }

    // ---------- Вспомогательные методы для поворота AABB ----------
    public static AABB getAABBRotationOffset(AABB aabb, double x, double y, double z, Direction dir) {
        return switch (dir) {
            case NORTH -> aabb.move(x, y, z);
            case SOUTH -> new AABB(-aabb.maxX, aabb.minY, -aabb.maxZ, -aabb.minX, aabb.maxY, -aabb.minZ).move(x, y, z);
            case EAST -> new AABB(aabb.minZ, aabb.minY, -aabb.maxX, aabb.maxZ, aabb.maxY, -aabb.minX).move(x, y, z);
            case WEST -> new AABB(-aabb.maxZ, aabb.minY, aabb.minX, -aabb.minZ, aabb.maxY, aabb.maxX).move(x, y, z);
            default -> aabb.move(x, y, z);
        };
    }

    // ---------- ICustomBlockHighlight ----------
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDrawHighlight(Level world, BlockPos pos) {
        return useDetailedHitbox();
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

    // ---------- INBTBlockTransformable ----------
    @Override
    public BlockState transformState(BlockState state, int rotation) {
        // Поворачиваем FACING
        Direction newFacing = state.getValue(FACING);
        for (int i = 0; i < rotation; i++) {
            newFacing = newFacing.getClockWise();
        }
        return state.setValue(FACING, newFacing);
    }

    // ---------- Прочие переопределения ----------
    @Override
    public PushReaction getPistonPushReaction(BlockState state) {
        return PushReaction.BLOCK;
    }

    // ---------- Абстрактные методы для потомков ----------
    public abstract int[] getDimensions(); // {up, down, north, south, west, east} в блоках
    public abstract int getZOffset(); // смещение ядра от места установки (обычно положительное)
    public int getXOffset() {return 0;}
    public int getHeightOffset() { return 0; } // смещение по высоте



    // Для совместимости с оригинальным кодом, где использовалась мета-логика
    // Можно оставить заглушки
    public void makeExtra(Level world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);

        // Проверяем, что это наш блок
        if (!state.is(this))
            return;

        // Получаем текущее состояние
        int dummyState = state.getValue(DUMMY_STATE);

        // Проверяем, что это обычный dummy (не extra и не core)
        if (dummyState != 0)
            return;

        // Устанавливаем safeRem для предотвращения рекурсивного удаления
        setSafeRem(true);

        // Создаём новое состояние с DUMMY_STATE = 1 (extra)
        BlockState newState = state.setValue(DUMMY_STATE, 1);
        world.setBlock(pos, newState, 3);

        BlockEntity be = world.getBlockEntity(pos);
        if (be instanceof TileEntityProxyBase proxy) {
            proxy.setExtra(true);
        }

        setSafeRem(false);
    }

    public void removeExtra(Level world, BlockPos pos) {}
    public boolean hasExtra(BlockState state) { return isExtra(state); }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }
}