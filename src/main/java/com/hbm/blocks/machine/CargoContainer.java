package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.machine.TileEntityCargoContainer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CargoContainer extends BlockDummyable {

    public CargoContainer(Properties properties) {
        super(properties);
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    public boolean tryPlace(ServerLevel level, BlockPos pos, Direction facing, Player player) {
        int offset = -getZOffset();
        BlockPos corePos = pos.relative(facing, offset);

        if (!checkRequirement(level, pos, corePos, facing)) return false;

        BlockState coreState = getCoreState(facing);
        level.setBlock(corePos, coreState, 3);
        fillSpace(level, pos, corePos, facing);

        return true;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityCargoContainer(pos, state);
        } else if (isExtra(state)) {
            return ModTileEntity.PROXY_COMBO.get().create(pos, state);
        }
        return null;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.CARGO_CONTAINER.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntityCargoContainer container) {
                    container.tick();
                }
            };
        }
        return null;
    }

    @Override
    protected int getGuiID() {
        return 0; // ID GUI для контейнера
    }

    @Override
    public int[] getDimensions() {
        // up, down, north, south, west, east
        // Размеры как у цистерны: 2 вверх, 0 вниз, 1 север, 1 юг, 2 запад, 2 восток
        return new int[] {2, 0, 1, 1, 2, 2};
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 20.0F)
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY);
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        int y = corePos.getY();

        // Устанавливаем extra-блоки по углам (как у цистерны)
        for (int dx : new int[]{-1, 1}) {
            for (int dz : new int[]{-1, 1}) {
                BlockPos extraPos = new BlockPos(
                        corePos.getX() + dx,
                        y,
                        corePos.getZ() + dz
                );

                if (level.getBlockState(extraPos).is(this)) {
                    this.makeExtra(level, extraPos);
                }
            }
        }
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
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

        switch(rotation) {
            case NORTH:
                boxes.add(new AABB(-2, 0, -1, 3, 3, 2).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case SOUTH:
                boxes.add(new AABB(-2, 0, -1, 3, 3, 2).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case WEST:
                boxes.add(new AABB(-1, 0, -2, 2, 3, 3).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case EAST:
                boxes.add(new AABB(-1, 0, -2, 2, 3, 3).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
        }
        return boxes;
    }

}