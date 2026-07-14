package com.hbm.blocks.machine;

import com.hbm.api.block.ICrucibleAcceptor;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.inventory.material.Mats.MaterialStack;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityCrucible;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineCrucible extends BlockDummyable implements ICrucibleAcceptor {

    public static final DirectionProperty FACING = BlockStateProperties.FACING;

    public MachineCrucible(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityCrucible(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setInventory();
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.CRUCIBLE.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityCrucible crucible) {
                    crucible.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) return InteractionResult.SUCCESS;
        if (player.isShiftKeyDown()) return InteractionResult.PASS;

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.FAIL;

        // Лопата — выгребаем содержимое
        if (player.getItemInHand(hand).getItem() instanceof net.minecraft.world.item.ShovelItem) {
            BlockEntity te = level.getBlockEntity(core);
            if (te instanceof TileEntityCrucible crucible) {
                crucible.scoopOut(player);
                return InteractionResult.CONSUME;
            }
        }

        return standardOpenBehavior(level, pos, player, getGuiID()) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{1, 0, 1, 1, 1, 1};
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDrawHighlight(Level level, BlockPos pos) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawHighlight(PoseStack stack, MultiBufferSource buffer, Level level, BlockPos pos) {
        BlockPos core = findCore(level, pos);
        if (core == null) return;

        BlockState state = level.getBlockState(core);
        Direction dir = state.getValue(FACING);
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        stack.pushPose();
        stack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());
        List<AABB> boxes = getHighlightBoxes(dir, core);

        for (AABB box : boxes) {
            ICustomBlockHighlight.renderAABB(stack, consumer, box, 0.0F, 1.0F, 0.0F, 0.4F);
        }

        stack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private List<AABB> getHighlightBoxes(Direction dir, BlockPos core) {
        List<AABB> boxes = new ArrayList<>();

        double offsetX = 0.5;
        double offsetZ = 0.5;

        boxes.add(new AABB(-1.5 + offsetX, 0, -1.5 + offsetZ, 1.5 + offsetX, 0.5, 1.5 + offsetZ));
        boxes.add(new AABB(-1.25 + offsetX, 0.5, -1.25 + offsetZ, 1.25 + offsetX, 1.5, -1 + offsetZ));
        boxes.add(new AABB(-1.25 + offsetX, 0.5, -1.25 + offsetZ, -1 + offsetX, 1.5, 1.25 + offsetZ));
        boxes.add(new AABB(-1.25 + offsetX, 0.5, 1 + offsetZ, 1.25 + offsetX, 1.5, 1.25 + offsetZ));
        boxes.add(new AABB(1 + offsetX, 0.5, -1.25 + offsetZ, 1.25 + offsetX, 1.5, 1.25 + offsetZ));

        boxes.replaceAll(box -> box.move(core.getX(), core.getY(), core.getZ()));
        return boxes;
    }

    // ICrucibleAcceptor — делегируем TileEntity
    @Override
    public boolean canAcceptPartialPour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        BlockPos core = findCore(level, pos);
        if (core == null) return false;
        BlockEntity te = level.getBlockEntity(core);
        return te instanceof TileEntityCrucible crucible && crucible.canAcceptPartialPour(level, pos, dX, dY, dZ, side, stack);
    }

    @Override
    public MaterialStack pour(Level level, BlockPos pos, double dX, double dY, double dZ, Direction side, MaterialStack stack) {
        BlockPos core = findCore(level, pos);
        if (core == null) return stack;
        BlockEntity te = level.getBlockEntity(core);
        if (te instanceof TileEntityCrucible crucible) return crucible.pour(level, pos, dX, dY, dZ, side, stack);
        return stack;
    }

    @Override
    public boolean canAcceptPartialFlow(Level level, BlockPos pos, Direction side, MaterialStack stack) { return false; }

    @Override
    public MaterialStack flow(Level level, BlockPos pos, Direction side, MaterialStack stack) { return null; }

    public static Block.Properties createProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .sound(SoundType.METAL)
                .requiresCorrectToolForDrops();
    }
}