package com.hbm.blocks.gas;

import com.hbm.items.ModArmorItems;
import com.hbm.main.MainRegistry;
import com.hbm.util.ArmorUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

public abstract class BlockGasBase extends Block {

    public static final BooleanProperty GLOWING = BooleanProperty.create("glowing");
    public static final BooleanProperty REPLACEABLE = BooleanProperty.create("replaceable");

    protected float red;
    protected float green;
    protected float blue;

    public BlockGasBase(Properties properties, float r, float g, float b) {
        super(properties);
        this.red = r;
        this.green = g;
        this.blue = b;
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(GLOWING, false)
                .setValue(REPLACEABLE, true));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(GLOWING, REPLACEABLE);
    }

    public void setGlowing(Level level, BlockPos pos, boolean glowing) {
        BlockState state = level.getBlockState(pos);
        if (state.getBlock() instanceof BlockGasBase) {
            if (state.getValue(GLOWING) != glowing) {
                level.setBlock(pos, state.setValue(GLOWING, glowing), 3);
            }
        }
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull VoxelShape getCollisionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return Shapes.empty();
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public float getShadeBrightness(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean propagatesSkylightDown(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return true;
    }

    @Override
    public boolean isCollisionShapeFullBlock(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return false;
    }

    @Override
    public void onPlace(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState oldState, boolean isMoving) {
        if (!level.isClientSide) {
            level.scheduleTick(pos, this, 10);
        }
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        level.scheduleTick(pos, this, 10);
    }

    @Override
    public void tick(@NotNull BlockState state, @NotNull ServerLevel level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        if (!tryMove(level, pos, getFirstDirection(level, pos))) {
            if (!tryMove(level, pos, getSecondDirection(level, pos))) {
                level.scheduleTick(pos, this, getDelay(level));
            }
        }
    }

    @Override
    public boolean canBeReplaced(@NotNull BlockState state, @NotNull BlockPlaceContext useContext) {
        return state.getValue(REPLACEABLE);
    }

    public abstract Direction getFirstDirection(Level level, BlockPos pos);

    public Direction getSecondDirection(Level level, BlockPos pos) {
        return getFirstDirection(level, pos);
    }

    public boolean tryMove(Level level, BlockPos pos, Direction dir) {
        BlockPos targetPos = pos.relative(dir);
        BlockState targetState = level.getBlockState(targetPos);
        BlockState currentState = level.getBlockState(pos);
        boolean wasGlowing = currentState.getValue(GLOWING);

        if (targetState.isAir() || (targetState.getBlock() instanceof BlockGasBase && targetState.getValue(REPLACEABLE))) {
            level.removeBlock(pos, false);
            level.setBlock(targetPos, this.defaultBlockState()
                    .setValue(GLOWING, wasGlowing)
                    .setValue(REPLACEABLE, true), 3);
            level.scheduleTick(targetPos, this, getDelay(level));
            return true;
        }
        return false;
    }

    public int getDelay(Level level) {
        return 2;
    }

    public Direction randomHorizontal(RandomSource random) {
        return Direction.from2DDataValue(random.nextInt(4));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull RandomSource random) {
        Player player = MainRegistry.proxy.me();
        if (player != null && ArmorUtil.checkArmorPiece(player, ModArmorItems.ASHGLASSES.get(), 3)) {
            CompoundTag data = new CompoundTag();
            data.putString("type", "vanillaExt");
            data.putString("mode", "cloud");
            data.putDouble("posX", pos.getX() + 0.5);
            data.putDouble("posY", pos.getY() + 0.5);
            data.putDouble("posZ", pos.getZ() + 0.5);
            data.putFloat("r", red);
            data.putFloat("g", green);
            data.putFloat("b", blue);
            MainRegistry.proxy.effectNT(data);
        }
    }

    public static BlockBehaviour.Properties createGasProperties() {
        return BlockBehaviour.Properties.of()
                .strength(0.0F, 0.0F)
                .noOcclusion()
                .noCollission()
                .noLootTable()
                .replaceable()
                .air();
    }
}