package com.hbm.blocks.machine;

import com.hbm.blocks.ISpotlight;
import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.deco.TileEntitySimpleOBJ;
import com.hbm.util.HBMEnums;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class BlockSpotlight extends BaseEntityBlock implements ISpotlight {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;
    public static final DirectionProperty HORIZONTAL_FACING = DirectionProperty.create("horizontal_facing", Direction.Plane.HORIZONTAL);
    public static final BooleanProperty BROKEN = BooleanProperty.create("broken");
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED; // Инвертированная логика: true = выключен
    public static final EnumProperty<HBMEnums.LightType> TYPE = EnumProperty.create("type", HBMEnums.LightType.class);

    public final int beamLength;

    public enum CapSide { NONE, LEFT, RIGHT }

    public BlockSpotlight(int beamLength, HBMEnums.LightType type, boolean isOn) {
        super(BlockBehaviour.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(0.5F)
                .noOcclusion()
                .lightLevel(state -> state.getValue(BlockSpotlight.POWERED) ? 0 : 15)
        );
        this.beamLength = beamLength;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(BROKEN, false)
                .setValue(POWERED, !isOn) // Если isOn = true, то POWERED = false
                .setValue(TYPE, type));
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntitySimpleOBJ(pos, state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, BROKEN, POWERED, TYPE, HORIZONTAL_FACING);
    }

    @Override
    public @NotNull BlockState rotate(BlockState state, Rotation rot) {
        Direction facing = state.getValue(FACING);
        Direction newFacing = rot.rotate(facing);
        BlockState newState = state.setValue(FACING, newFacing);

        // Для вертикальных ламп (на полу/потолке) нужно также повернуть горизонтальное направление
        if (newFacing.getAxis() == Direction.Axis.Y) {
            Direction horiz = state.getValue(HORIZONTAL_FACING);
            newState = newState.setValue(HORIZONTAL_FACING, rot.rotate(horiz));
        } else {
            // Для настенных ламп сбрасываем HORIZONTAL_FACING (оно не используется)
            newState = newState.setValue(HORIZONTAL_FACING, Direction.NORTH);
        }
        return newState;
    }

    @Override
    public @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction clickedFace = context.getClickedFace();
        Direction facing = clickedFace.getOpposite();
        Level level = context.getLevel();
        BlockPos lampPos = context.getClickedPos();
        BlockPos supportPos = lampPos.relative(facing);

        if (!canAttach(level, supportPos, facing)) {
            return null;
        }

        BlockState state = this.defaultBlockState().setValue(FACING, facing);

        // Для ламп на полу/потолке определяем горизонтальную ориентацию по направлению игрока
        if (facing.getAxis() == Direction.Axis.Y) {
            Direction playerDir = Objects.requireNonNull(context.getPlayer()).getDirection(); // куда смотрит игрок
            state = state.setValue(HORIZONTAL_FACING, playerDir);
        } else {
            // Для настенных ламп можно установить значение по умолчанию, но оно не будет использоваться
            state = state.setValue(HORIZONTAL_FACING, Direction.NORTH);
        }

        return state;
    }

    private boolean canAttach(LevelAccessor level, BlockPos pos, Direction lampFacing) {
        BlockState state = level.getBlockState(pos);
        // Проверяем грань опорного блока, обращённую к лампе
        return state.isFaceSturdy(level, pos, lampFacing.getOpposite());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction facing = state.getValue(FACING);
        Direction horizontalFacing = state.getValue(HORIZONTAL_FACING);
        HBMEnums.LightType type = state.getValue(TYPE);

        // Получаем базовые размеры (полуразмеры, т.е. половины длины/ширины/высоты)
        float[] bounds = getBounds(type).clone();

        // Корректируем длину ТОЛЬКО если это реальный мир (Level), а не EmptyBlockGetter
        if (type == HBMEnums.LightType.FLUORESCENT && facing.getAxis() != Direction.Axis.Y && level instanceof Level realLevel) {
            String partName = getModelPartName(state, realLevel, pos);
            if ("FluoroSingle".equals(partName)) {
                bounds[0] = Math.max(0.01f, bounds[0] - 0.3125f);
            } else if ("FluoroCap".equals(partName)) {
                bounds[0] = Math.max(0.01f, bounds[0] - 0.15625f);
            }
        }

        // Для вертикальных ламп (на потолке/полу)
        if (type == HBMEnums.LightType.FLUORESCENT && facing.getAxis() == Direction.Axis.Y && level instanceof Level realLevel) {
            String partName = getModelPartName(state, realLevel, pos);
            if (horizontalFacing.getAxis() == Direction.Axis.X) {
                float tempX = bounds[0];
                bounds[0] = bounds[1];
                bounds[1] = tempX;
            }
            if ("FluoroSingle".equals(partName)) {
                bounds[2] = Math.max(0.01f, bounds[2]);
            } else if ("FluoroCap".equals(partName)) {
                bounds[2] = Math.max(0.01f, bounds[2]);
            }
        }

        // Переставляем размеры в зависимости от оси facing
        switch (facing.getAxis()) {
            case X:
                float tempX = bounds[0];
                bounds[0] = bounds[2];
                bounds[2] = tempX;
                break;
            case Y:
                float tempY = bounds[1];
                bounds[1] = bounds[2];
                bounds[2] = tempY;
                break;
            default:
                break;
        }

        double centerX = 0.5;
        double centerY = 0.5;
        double centerZ = 0.5;

        // Сдвиг центра
        switch (facing) {
            case EAST  -> centerX += 0.5;
            case WEST  -> centerX -= 0.5;
            case SOUTH -> centerZ += 0.5;
            case NORTH -> centerZ -= 0.5;
            case UP    -> centerY += 0.5;
            case DOWN  -> centerY -= 0.5;
        }

        // Тонкая корректировка
        if (type == HBMEnums.LightType.FLUORESCENT) {
            switch (facing) {
                case EAST  -> centerX -= 0.075;
                case WEST  -> centerX += 0.075;
                case SOUTH -> centerZ -= 0.075;
                case NORTH -> centerZ += 0.075;
                case UP    -> centerY -= 0.075;
                case DOWN  -> centerY += 0.075;
            }
        }
        if (type == HBMEnums.LightType.HALOGEN) {
            switch (facing) {
                case EAST  -> centerX -= 0.15;
                case WEST  -> centerX += 0.15;
                case SOUTH -> centerZ -= 0.15;
                case NORTH -> centerZ += 0.15;
                case UP    -> centerY -= 0.15;
                case DOWN  -> centerY += 0.15;
            }
        }

        double minX = centerX - bounds[0];
        double minY = centerY - bounds[1];
        double minZ = centerZ - bounds[2];
        double maxX = centerX + bounds[0];
        double maxY = centerY + bounds[1];
        double maxZ = centerZ + bounds[2];

        // Проверка на валидность значений
        if (minX >= maxX || minY >= maxY || minZ >= maxZ) {
            // Возвращаем дефолтный маленький бокс, чтобы избежать краша
            return Shapes.box(0.45, 0.45, 0.45, 0.55, 0.55, 0.55);
        }

        return Shapes.box(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public CapSide getCapSide(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(TYPE) != HBMEnums.LightType.FLUORESCENT) return CapSide.NONE;
        Direction facing = state.getValue(FACING);

        if (facing.getAxis() == Direction.Axis.Y) {
            Direction horizontal = state.getValue(HORIZONTAL_FACING);
            Direction left = horizontal.getCounterClockWise();
            Direction right = horizontal.getClockWise();
            boolean hasLeft = canConnectTo(state, level, pos.relative(left));
            boolean hasRight = canConnectTo(state, level, pos.relative(right));
            if (hasLeft && !hasRight) return CapSide.LEFT;
            if (!hasLeft && hasRight) return CapSide.RIGHT;
            return CapSide.NONE;
        } else {
            // Существующая логика для стен
            Direction left = facing.getCounterClockWise();
            Direction right = left.getOpposite();
            boolean hasLeft = canConnectTo(state, level, pos.relative(left));
            boolean hasRight = canConnectTo(state, level, pos.relative(right));
            if (hasLeft && !hasRight) return CapSide.LEFT;
            if (!hasLeft && hasRight) return CapSide.RIGHT;
            return CapSide.NONE;
        }
    }

    private float[] getBounds(HBMEnums.LightType type) {
        return switch (type) {
            case FLUORESCENT -> new float[]{0.5F, 0.3F, 0.075F};
            case HALOGEN -> new float[]{0.35F, 0.25F, 0.15F};
            default -> new float[]{0.25F, 0.2F, 0.15F};
        };
    }

    @Override
    public void onPlace(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            updatePower(level, pos, state);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (level.isClientSide) return;

        Direction facing = state.getValue(FACING);
        BlockPos supportPos = pos.relative(facing);

        // Проверяем опорный блок
        if (neighborPos.equals(supportPos)) {
            if (!canAttach(level, supportPos, facing)) {
                level.destroyBlock(pos, true);
                return;
            }
        } else {
            // Проверяем ТЕКУЩЕЕ состояние соседа, а не neighborBlock
            BlockState currentNeighborState = level.getBlockState(neighborPos);
            if (currentNeighborState.getBlock() instanceof BlockSpotlight) {
                level.sendBlockUpdated(pos, state, state, 3);
                level.sendBlockUpdated(neighborPos, currentNeighborState, currentNeighborState, 3);
            }
        }

        updatePower(level, pos, state);
    }

    private void updatePower(Level level, BlockPos pos, BlockState state) {
        if (state.getValue(BROKEN)) return;

        boolean hasSignal = level.hasNeighborSignal(pos);
        boolean isPowered = state.getValue(POWERED);

        // Инвертированная логика: лампочка горит, когда НЕТ сигнала (POWERED = false)
        if (hasSignal && !isPowered) {
            // Появился сигнал — выключаем
            level.setBlock(pos, state.setValue(POWERED, true), 2);
            unpropagateBeam(level, pos, state.getValue(FACING));
        } else if (!hasSignal && isPowered) {
            // Сигнал пропал — включаем
            level.setBlock(pos, state.setValue(POWERED, false), 2);
            propagateBeam(level, pos, state.getValue(FACING), beamLength);
        }
    }

    // === ЛУЧИ ===
    private void propagateBeam(Level level, BlockPos pos, Direction dir, int distance) {
        if (distance <= 0) return;
        BlockPos nextPos = pos.relative(dir);
        BlockState nextState = level.getBlockState(nextPos);

        // Если там уже луч — не ставим свой, просто продолжаем
        if (nextState.is(ModBlocks.SPOTLIGHT_BEAM.get())) {
            propagateBeam(level, nextPos, dir, distance - 1);
            return;
        }

        // Если блок не воздух и не луч — останавливаемся
        if (!nextState.isAir()) return;

        level.setBlock(nextPos, ModBlocks.SPOTLIGHT_BEAM.get().defaultBlockState(), 3);
        propagateBeam(level, nextPos, dir, distance - 1);
    }

    private void unpropagateBeam(Level level, BlockPos pos, Direction dir) {
        BlockPos nextPos = pos.relative(dir);
        BlockState state = level.getBlockState(nextPos);
        if (state.is(ModBlocks.SPOTLIGHT_BEAM.get())) {
            level.removeBlock(nextPos, false);
            unpropagateBeam(level, nextPos, dir);
        }
    }

    @Override
    public @NotNull InteractionResult use(BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (!state.getValue(BROKEN)) return InteractionResult.PASS;
        // Чиним лампочку
        level.setBlock(pos, state.setValue(BROKEN, false).setValue(POWERED, false), 3);
        if (!level.isClientSide) {
            propagateBeam(level, pos, state.getValue(FACING), beamLength);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    // === ISpotlight ===
    @Override
    public int getBeamLength() {
        return beamLength;
    }

    public boolean isOn(BlockState state) {
        return !state.getValue(BROKEN) && !state.getValue(POWERED);
    }

    @Override
    public @NotNull ItemStack getCloneItemStack(@NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull BlockState state) {
        return new ItemStack(this);
    }

    public boolean canConnectTo(BlockState thisState, Level level, BlockPos pos) {
        BlockState otherState = level.getBlockState(pos);
        if (!(otherState.getBlock() instanceof BlockSpotlight other)) return false;
        if (other.getType(otherState) != HBMEnums.LightType.FLUORESCENT) return false;
        if (this.getType(thisState) != HBMEnums.LightType.FLUORESCENT) return false;

        Direction thisFacing = thisState.getValue(FACING);
        Direction otherFacing = otherState.getValue(FACING);
        if (thisFacing.getAxis() != otherFacing.getAxis()) return false;

        if (thisFacing.getAxis() == Direction.Axis.Y) {
            // Вертикальные лампы: дополнительно проверяем одинаковую горизонтальную ориентацию
            return thisState.getValue(HORIZONTAL_FACING) == otherState.getValue(HORIZONTAL_FACING);
        } else {
            // Горизонтальные лампы: дополнительных условий нет (они уже склеиваются по left/right)
            return true;
        }
    }

    public String getModelPartName(BlockState state, Level level, BlockPos pos) {
        HBMEnums.LightType type = state.getValue(TYPE);
        if (type != HBMEnums.LightType.FLUORESCENT) return null;

        Direction facing = state.getValue(FACING);

        // Вертикальные лампы (пол/потолок)
        if (facing.getAxis() == Direction.Axis.Y) {
            Direction horizontal = state.getValue(HORIZONTAL_FACING);
            // Определяем направления "влево" и "вправо" относительно горизонтальной ориентации
            Direction left = horizontal.getCounterClockWise();
            Direction right = horizontal.getClockWise();

            boolean hasLeft = canConnectTo(state, level, pos.relative(left));
            boolean hasRight = canConnectTo(state, level, pos.relative(right));

            if (!hasLeft && !hasRight) return "FluoroSingle";
            if (hasLeft && !hasRight) return "FluoroCap";
            if (!hasLeft && hasRight) return "FluoroCap";
            return "FluoroMid";
        }

        // Горизонтальные лампы (стены) – существующая логика
        Direction left = facing.getCounterClockWise();
        Direction right = left.getOpposite();
        boolean hasLeft = canConnectTo(state, level, pos.relative(left));
        boolean hasRight = canConnectTo(state, level, pos.relative(right));

        if (!hasLeft && !hasRight) return "FluoroSingle";
        if (hasLeft && !hasRight) return "FluoroCap";
        if (!hasLeft && hasRight) return "FluoroCap";
        return "FluoroMid";
    }

    // Вспомогательный метод для получения типа из BlockState (чтобы не дублировать)
    private HBMEnums.LightType getType(BlockState state) {
        return state.getValue(TYPE);
    }

}