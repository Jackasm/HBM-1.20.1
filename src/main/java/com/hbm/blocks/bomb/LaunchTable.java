package com.hbm.blocks.bomb;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.interfaces.IBomb;
import com.hbm.items.ModItems;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.bomb.TileEntityLaunchTable;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class LaunchTable extends BlockDummyable implements IBomb {

    private static final VoxelShape SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public LaunchTable(Properties properties) {
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, DUMMY_STATE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Direction facing = context.getHorizontalDirection().getOpposite();
        return this.defaultBlockState().setValue(FACING, facing).setValue(DUMMY_STATE, 0);
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public @NotNull VoxelShape getOcclusionShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos) {
        return Shapes.empty();
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityLaunchTable(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setInventory().setPower().setFluid();
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (isCore(state) && type == ModTileEntity.LAUNCH_TABLE.get()) {
            return (lvl, pos, st, be) -> {
                if (be instanceof TileEntityLaunchTable launcher) {
                    launcher.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Player player,
                                          @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(core);
        if (te instanceof TileEntityLaunchTable launcher) {
            NetworkHooks.openScreen((ServerPlayer) player, launcher, core);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(ModItems.STRUCT_LAUNCHER_CORE_LARGE.get());
    }

    @Override
    public int[] getDimensions() {
        // {up, down, north, south, west, east} — 9x9 в горизонтали
        return new int[]{0, 0, 4, 4, 4, 4};
    }

    @Override
    public int getZOffset() {
        return 0;
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Override
    public BombReturnCode explode(Level level, BlockPos pos) {
        if (level.isClientSide) return BombReturnCode.UNDEFINED;

        BlockPos corePos = findCore(level, pos);
        if (corePos != null) {
            BlockEntity te = level.getBlockEntity(corePos);
            if (te instanceof TileEntityLaunchTable launcher) {
                if (launcher.canLaunch()) {
                    launcher.launchFromDesignator();
                    return BombReturnCode.LAUNCHED;
                }
                return BombReturnCode.ERROR_MISSING_COMPONENT;
            }
        }
        return BombReturnCode.UNDEFINED;
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        // Расставляем extra-блоки по всей области 9x9
        int[] dim = getDimensions(); // {0, 0, 4, 4, 4, 4}
        for (int x = -dim[4]; x <= dim[5]; x++) {
            for (int z = -dim[2]; z <= dim[3]; z++) {
                if (x == 0 && z == 0) continue;
                BlockPos extraPos = corePos.offset(x, 0, z);
                makeExtra(level, extraPos);
            }
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawHighlight(PoseStack poseStack, MultiBufferSource bufferSource, Level level, BlockPos pos) {
        BlockPos core = findCore(level, pos);
        if (core == null) return;

        BlockState state = level.getBlockState(core);
        Direction rot = state.getValue(FACING);

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        List<AABB> boxes = getHighlightBoxes(level, core, rot);

        for (AABB box : boxes) {
            ICustomBlockHighlight.renderAABB(poseStack, consumer, box, 0.0F, 1.0F, 0.0F, 0.4F);
        }

        poseStack.popPose();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDrawHighlight(Level level, BlockPos pos) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public List<AABB> getHighlightBoxes(Level level, BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();

        int[] dim = getDimensions();

        double minX = -dim[4];
        double maxX = dim[5] + 1;
        double minY = -dim[1];
        double maxY = dim[0] + 1;
        double minZ = -dim[2];
        double maxZ = dim[3] + 1;

        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        boxes.add(box.move(corePos.getX(), corePos.getY(), corePos.getZ()));

        return boxes;
    }
}