package com.hbm.blocks.bomb;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.interfaces.IBomb;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.bomb.TileEntityLaunchPadLarge;
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
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
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

import java.util.ArrayList;
import java.util.List;

public class LaunchPadLarge extends BlockDummyable implements IBomb {

    private static final VoxelShape SHAPE = Shapes.box(0.0, 0.0, 0.0, 1.0, 0.5, 1.0);
    public static final DirectionProperty FACING = DirectionalBlock.FACING;

    public LaunchPadLarge(Properties properties) {
        super(properties);
    }

    public static Properties createProperties() {
        return Properties.of()
                .strength(5.0F, 10.0F)
                .noOcclusion();
    }

    @Override
    public @NotNull VoxelShape getShape(@NotNull BlockState state, @NotNull BlockGetter level, @NotNull BlockPos pos, @NotNull CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityLaunchPadLarge(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setInventory().setPower().setFluid();
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity te = level.getBlockEntity(core);
        if (te instanceof TileEntityLaunchPadLarge launchPad) {
            NetworkHooks.openScreen((ServerPlayer) player, launchPad, core);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    public int[] getDimensions() {
        return new int[]{0, 0, 4, 4, 4, 4};
    }

    @Override
    public int getZOffset() {
        return -4;
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
            if (te instanceof TileEntityLaunchPadLarge launchPad) {
                return launchPad.launchFromDesignator();
            }
        }
        return BombReturnCode.UNDEFINED;
    }

    @Override
    public void neighborChanged(@NotNull BlockState state, Level level, @NotNull BlockPos pos, @NotNull Block block, @NotNull BlockPos fromPos, boolean isMoving) {
        if (!level.isClientSide) {
            BlockPos corePos = findCore(level, pos);
            if (corePos != null) {
                BlockEntity te = level.getBlockEntity(corePos);
                if (te instanceof TileEntityLaunchPadLarge launchPad) {
                    launchPad.updateRedstonePower(pos);
                }
            }
        }
        super.neighborChanged(state, level, pos, block, fromPos, isMoving);
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        Direction left = dir.getCounterClockWise();
        Direction right = dir.getClockWise();
        Direction forward = dir.getOpposite();
        Direction backward = dir;

        // 8 extra блоков по углам платформы
        BlockPos extra1 = corePos.relative(forward, 4).relative(left, 2);
        BlockPos extra2 = corePos.relative(forward, 4).relative(right, 2);
        BlockPos extra3 = corePos.relative(backward, 4).relative(left, 2);
        BlockPos extra4 = corePos.relative(backward, 4).relative(right, 2);
        BlockPos extra5 = corePos.relative(left, 4).relative(forward, 2);
        BlockPos extra6 = corePos.relative(left, 4).relative(backward, 2);
        BlockPos extra7 = corePos.relative(right, 4).relative(forward, 2);
        BlockPos extra8 = corePos.relative(right, 4).relative(backward, 2);

        makeExtra(level, extra1);
        makeExtra(level, extra2);
        makeExtra(level, extra3);
        makeExtra(level, extra4);
        makeExtra(level, extra5);
        makeExtra(level, extra6);
        makeExtra(level, extra7);
        makeExtra(level, extra8);
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

    @OnlyIn(Dist.CLIENT)
    public List<AABB> getHighlightBoxes(Level level, BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();

        int[] dim = getDimensions(); // {0, 0, 4, 4, 4, 4}

        double minX = -dim[4]; // -4
        double maxX = dim[5] + 1; // 5
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 1
        double minZ = -dim[2]; // -4
        double maxZ = dim[3] + 1; // 5

        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);

        // Поворачиваем бокс в зависимости от направления
        AABB rotatedBox = switch (rotation) {
            case NORTH -> box;
            case SOUTH -> new AABB(-box.maxX, box.minY, -box.maxZ, -box.minX, box.maxY, -box.minZ);
            case EAST -> new AABB(box.minZ, box.minY, -box.maxX, box.maxZ, box.maxY, -box.minX);
            case WEST -> new AABB(-box.maxZ, box.minY, box.minX, -box.minZ, box.maxY, box.maxX);
            default -> box;
        };

        boxes.add(rotatedBox.move(corePos.getX(), corePos.getY(), corePos.getZ()));

        return boxes;
    }
}