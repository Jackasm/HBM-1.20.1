package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.items.ModItems;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityBlastFurnace;
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
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineBlastFurnace extends BlockDummyable {

    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public MachineBlastFurnace(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(DUMMY_STATE, 0)
                .setValue(LIT, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(LIT);
    }

    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityBlastFurnace(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state);
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (player.getItemInHand(hand).getItem() == ModItems.MACHINE_BLAST_FURNACE_EXTENSION.get()) {
            return InteractionResult.PASS;
        }

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;
        BlockEntity te = level.getBlockEntity(core);
        if (te instanceof TileEntityBlastFurnace furnace) {
            if (!level.isClientSide) {
                NetworkHooks.openScreen((ServerPlayer) player, furnace, core);
            }
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{1, 0, 0, 0, 0, 0};
    }

    @Override
    public int getZOffset() {
        return 0;
    }

    @Override
    protected void fillSpace(Level level, BlockPos originalPos, BlockPos corePos, Direction dir) {
        makeExtra(level, corePos.above());
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.MACHINE_BLAST_FURNACE.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityBlastFurnace furnace) {
                    TileEntityBlastFurnace.serverTick(lvl, pos, st, furnace);
                }
            };
        }
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(DUMMY_STATE, 0);
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state, @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        // Не создаём extra автоматически – только при улучшении
        super.setPlacedBy(level, pos, state, placer, stack);
    }

    @Override
    public void onRemove(BlockState state, @NotNull Level level, @NotNull BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity be = level.getBlockEntity(pos);
            if (be instanceof TileEntityBlastFurnace furnace && furnace.hasExtension) {
                Block.popResource(level, pos, new ItemStack(ModItems.MACHINE_BLAST_FURNACE_EXTENSION.get()));
            }
            if (isCore(state)) {
                // Удаляем extra, если есть
                BlockPos above = pos.above();
                if (level.getBlockState(above).getBlock() == this && isExtra(level.getBlockState(above))) {
                    level.removeBlock(above, false);
                }
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }

    // ========== HIGHLIGHT ==========
    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDrawHighlight(Level level, BlockPos pos) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawHighlight(PoseStack poseStack, MultiBufferSource bufferSource, Level level, BlockPos pos) {
        BlockPos core = findCore(level, pos);
        if (core == null) return;

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());
        List<AABB> boxes = getHighlightBoxes(level, core);

        for (AABB box : boxes) {
            ICustomBlockHighlight.renderAABB(poseStack, consumer, box, 0.0F, 1.0F, 0.0F, 0.4F);
        }

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private List<AABB> getHighlightBoxes(Level level, BlockPos corePos) {
        List<AABB> boxes = new ArrayList<>();
        int[] dim = getDimensions(); // {1, 0, 0, 0, 0, 0}

        double minX = -dim[4]; // 0
        double maxX = dim[5] + 1; // 1
        double minZ = -dim[2]; // 0
        double maxZ = dim[3] + 1; // 1

        // Высота: 1 блок (ядро) по умолчанию
        double minY = 0;
        double maxY = 1;

        BlockEntity block = level.getBlockEntity(corePos);
        if (block instanceof TileEntityBlastFurnace te && te.hasExtension) {
            maxY = 2; // с расширением — 2 блока
        }

        AABB box = new AABB(minX, minY, minZ, maxX, maxY, maxZ);
        boxes.add(box.move(corePos.getX(), corePos.getY(), corePos.getZ()));
        return boxes;
    }

    // ========== PARTICLES ==========
    // Можно оставить пустым, так как частицы лучше рисовать в рендере
    @Override
    public void animateTick(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos, @NotNull net.minecraft.util.RandomSource random) {
        // Частицы в рендере
    }
}