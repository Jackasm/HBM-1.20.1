package com.hbm.blocks.network;

import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public abstract class RadioTorchBase extends BaseEntityBlock implements ILookOverlay, ITooltipProvider {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    /** Текстуры факела: выключенное и включённое состояние (второй может не быть). */
    private final String textureOff;
    private final String textureOn;

    public RadioTorchBase(Properties properties, String textureOff, String textureOn) {
        super(properties);
        this.textureOff = textureOff;
        this.textureOn = textureOn;
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
    }

    /** Имя текстуры для рендера: включённое состояние, если оно у блока есть. */
    public String textureName(boolean on) {
        return on && textureOn != null ? textureOn : textureOff;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        // Соглашение порта: FACING смотрит ОТ блока К опоре (в 1.7.10 мета
        // смотрела наоборот, от опоры к блоку, и была равна стороне клика).
        // От FACING считаются canSurvive и вход редстоуна у передатчика.
        return this.defaultBlockState().setValue(FACING, context.getClickedFace().getOpposite());
    }

    @Override
    public boolean canSurvive(@NotNull BlockState state, @NotNull LevelReader level, @NotNull BlockPos pos) {
        Direction dir = state.getValue(FACING);
        BlockPos attached = pos.relative(dir);
        BlockState attachedState = level.getBlockState(attached);
        return attachedState.isFaceSturdy(level, attached, dir.getOpposite())
                || attachedState.hasAnalogOutputSignal()
                || attachedState.isRedstoneConductor(level, attached);
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block neighborBlock, @NotNull BlockPos neighborPos, boolean isMoving) {
        if (!level.isClientSide && !canSurvive(state, level, pos)) {
            level.destroyBlock(pos, true);
        }
        super.neighborChanged(state, level, pos, neighborBlock, neighborPos, isMoving);
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                                 @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level,
                                        @NotNull BlockPos pos, @NotNull CollisionContext context) {
        // Коробка стоит на стороне, КУДА смотрит факел, то есть противоположной
        // опоре: формула эталона считалась от меты, а мета смотрела от опоры.
        Direction dir = state.getValue(FACING).getOpposite();
        double x0 = dir.getStepX() == 1 ? 0.375 : (dir.getStepX() == -1 ? 0.625 : 0.375);
        double y0 = dir.getStepY() == 1 ? 0.375 : (dir.getStepY() == -1 ? 0.625 : 0.375);
        double z0 = dir.getStepZ() == 1 ? 0.375 : (dir.getStepZ() == -1 ? 0.625 : 0.375);
        double x1 = dir.getStepX() == -1 ? 1.0 : 0.625;
        double y1 = dir.getStepY() == -1 ? 1.0 : 0.625;
        double z1 = dir.getStepZ() == -1 ? 1.0 : 0.625;
        return Block.box(x0 * 16, y0 * 16, z0 * 16, x1 * 16, y1 * 16, z1 * 16);
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(pos);
        // Экран открывает любой RTTY-факел: у counter/reader/controller свои TE
        // и свои меню, поэтому проверяем MenuProvider, а не базовый тип.
        if (te instanceof MenuProvider menu && player instanceof ServerPlayer sp) {
            NetworkHooks.openScreen(sp, menu, pos);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(0.1F, 10.0F)
                .sound(SoundType.METAL)
                .noCollission()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }
}