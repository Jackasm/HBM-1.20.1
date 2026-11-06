package com.hbm.blocks.generic;

import com.hbm.blocks.ILookOverlay;

import com.hbm.tileentity.deco.TileEntityPipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class BlockPipe extends BaseEntityBlock implements ILookOverlay {

    public static final EnumProperty<Direction.Axis> AXIS = BlockStateProperties.AXIS;

    private final PipeType pipeType;
    private final PipeColor pipeColor;
    private final boolean rusted;

    public enum PipeType {
        NORMAL(0),
        RIM(1),
        QUAD(2),
        FRAMED(3);

        public final int index;

        PipeType(int index) {
            this.index = index;
        }

    }

    public enum PipeColor {
        DEFAULT,
        GREEN,
        RED,
        MARKED
    }

    public BlockPipe(PipeType pipeType, PipeColor pipeColor, boolean rusted) {
        super(createProperties());
        this.pipeType = pipeType;
        this.pipeColor = pipeColor;
        this.rusted = rusted;
        this.registerDefaultState(this.stateDefinition.any().setValue(AXIS, Direction.Axis.Y));
    }

    private static Properties createProperties() {
        return Properties.of()
                .mapColor(net.minecraft.world.level.material.MapColor.METAL)
                .strength(2.0F, 5.0F)
                .sound(net.minecraft.world.level.block.SoundType.METAL)
                .noOcclusion()
                .isValidSpawn((state, level, pos, entityType) -> false);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AXIS);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction face = context.getClickedFace();
        Direction horizontal = context.getHorizontalDirection();

        // Если кликнули по верху или низу — ставим вертикально (ось Y)
        if (face == Direction.UP || face == Direction.DOWN) {
            return this.defaultBlockState().setValue(AXIS, Direction.Axis.Y);
        } else {
            // Если кликнули по боку — ставим горизонтально
            // Ось зависит от направления клика
            if (face.getAxis() == Direction.Axis.X) {
                return this.defaultBlockState().setValue(AXIS, Direction.Axis.X);
            } else {
                return this.defaultBlockState().setValue(AXIS, Direction.Axis.Z);
            }
        }
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        Direction.Axis axis = state.getValue(AXIS);

        // Базовая форма для всех труб - центральный стержень
        VoxelShape baseShape = switch (axis) {
            case X -> Block.box(0, 4, 4, 16, 12, 12);
            case Y -> Block.box(4, 0, 4, 12, 16, 12);
            case Z -> Block.box(4, 4, 0, 12, 12, 16);
        };

        // Добавляем дополнительные формы в зависимости от типа
        VoxelShape extraShape = Shapes.empty();

        switch (pipeType) {
            case RIM:
                // Добавляем кольца по краям
                extraShape = switch (axis) {
                    case X -> Shapes.or(
                            Block.box(0, 2, 2, 1, 14, 14),
                            Block.box(15, 2, 2, 16, 14, 14)
                    );
                    case Y -> Shapes.or(
                            Block.box(2, 0, 2, 14, 1, 14),
                            Block.box(2, 15, 2, 14, 16, 14)
                    );
                    case Z -> Shapes.or(
                            Block.box(2, 2, 0, 14, 14, 1),
                            Block.box(2, 2, 15, 14, 14, 16)
                    );
                };
                break;

            case QUAD:
                // Добавляем 4 ребра по углам
                extraShape = switch (axis) {
                    case X -> Shapes.or(
                            Block.box(0, 2, 2, 16, 4, 4),
                            Block.box(0, 12, 2, 16, 14, 4),
                            Block.box(0, 2, 12, 16, 4, 14),
                            Block.box(0, 12, 12, 16, 14, 14)
                    );
                    case Y -> Shapes.or(
                            Block.box(2, 0, 2, 4, 16, 4),
                            Block.box(12, 0, 2, 14, 16, 4),
                            Block.box(2, 0, 12, 4, 16, 14),
                            Block.box(12, 0, 12, 14, 16, 14)
                    );
                    case Z -> Shapes.or(
                            Block.box(2, 2, 0, 4, 4, 16),
                            Block.box(12, 2, 0, 14, 4, 16),
                            Block.box(2, 12, 0, 4, 14, 16),
                            Block.box(12, 12, 0, 14, 14, 16)
                    );
                };
                break;

            case FRAMED:
                // Добавляем раму (4 ребра по углам)
                extraShape = switch (axis) {
                    case X -> Shapes.or(
                            Block.box(0, 2, 2, 16, 3, 3),
                            Block.box(0, 13, 2, 16, 14, 3),
                            Block.box(0, 2, 13, 16, 3, 14),
                            Block.box(0, 13, 13, 16, 14, 14)
                    );
                    case Y -> Shapes.or(
                            Block.box(2, 0, 2, 3, 16, 3),
                            Block.box(13, 0, 2, 14, 16, 3),
                            Block.box(2, 0, 13, 3, 16, 14),
                            Block.box(13, 0, 13, 14, 16, 14)
                    );
                    case Z -> Shapes.or(
                            Block.box(2, 2, 0, 3, 3, 16),
                            Block.box(13, 2, 0, 14, 3, 16),
                            Block.box(2, 13, 0, 3, 14, 16),
                            Block.box(13, 13, 0, 14, 14, 16)
                    );
                };
                break;

            default:
                break;
        }

        return Shapes.or(baseShape, extraShape);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        String colorKey = switch (pipeColor) {
            case GREEN -> "green";
            case RED -> "red";
            case MARKED -> "marked";
            default -> "default";
        };

        String typeKey = switch (pipeType) {
            case RIM -> "rim";
            case QUAD -> "quad";
            case FRAMED -> "framed";
            default -> "normal";
        };

        String rustKey = rusted ? "_rusted" : "";

        String langKey = "tile.pipe." + typeKey + "." + colorKey + rustKey + ".desc";

        // Пробуем получить перевод
        Component translated = Component.translatable(langKey);

        tooltip.add(translated);
    }

    public PipeType getPipeType() {
        return pipeType;
    }

    public String getSideTexture() {
        String color = switch (pipeColor) {
            case GREEN -> "_green";
            case RED -> "_red";
            case MARKED -> "_marked";
            default -> "";
        };
        String rust = rusted ? "_rusty" : "";
        return "pipe_side" + color + rust;
    }

    public String getTopTexture() {
        String color = switch (pipeColor) {
            case GREEN -> "_green";
            case RED -> "_red";
            case MARKED -> "_marked";
            default -> "";
        };
        String rust = rusted ? "_rusty" : "";
        return "pipe_top" + color + rust;
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        return new TileEntityPipe(pos, state);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull BlockState rotate(@NotNull BlockState state, @NotNull Rotation rotation) {
        return state.setValue(AXIS, rotation.rotate(Direction.fromAxisAndDirection(state.getValue(AXIS), Direction.AxisDirection.POSITIVE)).getAxis());
    }
}