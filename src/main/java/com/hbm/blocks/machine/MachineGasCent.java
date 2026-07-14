package com.hbm.blocks.machine;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.items.tool.ItemTooling;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityMachineGasCent;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
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

public class MachineGasCent extends BlockDummyable implements ILookOverlay, ITooltipProvider, IToolable {

    public MachineGasCent(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityMachineGasCent(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setFluid();
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.GAS_CENTRIFUGE.get()) {
            return (lvl, pos, st, tile) -> ((TileEntityMachineGasCent) tile).tick();
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty() && stack.getItem() instanceof ItemTooling) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide) return InteractionResult.SUCCESS;
        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;
        BlockEntity te = level.getBlockEntity(core);
        if (te instanceof TileEntityMachineGasCent cent) {
            NetworkHooks.openScreen((ServerPlayer) player, cent, core);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{3, 0, 0, 0, 0, 0}; // высота 3 блока
    }

    @Override
    public int getZOffset() {
        return 0;
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);
        // добавляем два блока над ядром
        for (int i = 1; i <= 2; i++) {
            makeExtra(level, corePos.above(i));
        }
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Player player = Minecraft.getInstance().player;
        if (player != null) addStandardInfo(stack, player, tooltip, flag.isAdvanced());
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        // можно добавить информационный оверлей, но для центрифуги не обязателен
        return List.of();
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float fX, float fY, float fZ, ToolType tool) {
        if (tool != ToolType.SCREWDRIVER) return false;
        if (level.isClientSide) return true;
        BlockPos core = findCore(level, pos);
        if (core == null) return false;
        BlockEntity te = level.getBlockEntity(core);
        if (te instanceof TileEntityMachineGasCent cent) {
            cent.setChanged();
            return true;
        }
        return false;
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.DESTROY)
                .requiresCorrectToolForDrops();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDrawHighlight(Level level, BlockPos pos) {
        return true;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawHighlight(PoseStack poseStack, MultiBufferSource bufferSource, Level level, BlockPos pos) {
        // Находим ядро мультиблока
        BlockPos core = findCore(level, pos);
        if (core == null) return;

        BlockState state = level.getBlockState(core);
        Direction rot = state.getValue(FACING);

        // Получаем позицию камеры для правильного смещения
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer consumer = bufferSource.getBuffer(RenderType.lines());

        // Получаем боксы для выделения
        List<AABB> boxes = getHighlightBoxes(level, core, rot);

        for (AABB box : boxes) {
            ICustomBlockHighlight.renderAABB(poseStack, consumer, box, 0.0F, 1.0F, 0.0F, 0.4F);
        }

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private List<AABB> getHighlightBoxes(Level level, BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();

        int[] dim = getDimensions(); // {3, 0, 0, 0, 0, 0} - высота 3 блока

        double minX = -dim[4]; // 0
        double maxX = dim[5] + 1; // 1
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 4 (высота 3 блока + 1)
        double minZ = -dim[2]; // 0
        double maxZ = dim[3] + 1; // 1

        switch(rotation) {
            case NORTH:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case SOUTH:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case WEST:
                boxes.add(new AABB(minZ, minY, minX, maxZ, maxY, maxX).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            case EAST:
                boxes.add(new AABB(minZ, minY, minX, maxZ, maxY, maxX).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
            default:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(corePos.getX(), corePos.getY(), corePos.getZ()));
                break;
        }

        return boxes;
    }

    @Override
    public void addInformation(ItemStack stack, Player player, List<Component> list, boolean ext) {
        addStandardInfo(stack, player, list, ext);
    }

    @Override
    protected int getGuiID() {
        return 0;
    }
}