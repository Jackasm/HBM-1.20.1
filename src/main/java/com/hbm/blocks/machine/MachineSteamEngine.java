package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntitySteamEngine;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MachineSteamEngine extends BlockDummyable implements ILookOverlay, ITooltipProvider {

    public MachineSteamEngine(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntitySteamEngine(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setPower().setFluid();
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (isCore(state)) {
            if (type == ModTileEntity.MACHINE_STEAM_ENGINE.get()) {
                return (lvl, pos, st, tile) -> {
                    if (tile instanceof TileEntitySteamEngine engine) {
                        engine.tick();
                    }
                };
            }
        }
        return null;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        addStandardInfo(stack, null, tooltip, flag.isAdvanced());
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{1, 0, 1, 1, 3, 3};
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        Direction right = dir.getClockWise();
        Direction forward = dir.getOpposite();

        BlockPos basePos = corePos.relative(forward, 1);

        this.makeExtra(level, basePos.relative(right, 1).above(1));
        this.makeExtra(level, basePos.relative(right, 2).above(1));
        this.makeExtra(level, basePos.relative(right, 3).above(1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockPos core = findCore(level, pos);
        if (core == null) return sections;

        BlockEntity te = level.getBlockEntity(core);
        if (!(te instanceof TileEntitySteamEngine engine)) return sections;

        OverlaySection main = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        main.setIcon(getMachineItem());

        main.addLine(Component.literal("§a-> " + engine.tanks[0].getTankType().getLocalizedName() + ": " +
                String.format(Locale.US, "%,d", engine.tanks[0].getFill()) + " / " + String.format(Locale.US, "%,d", engine.tanks[0].getMaxFill()) + " mB"));
        main.addLine(Component.literal("§c<- " + engine.tanks[1].getTankType().getLocalizedName() + ": " +
                String.format(Locale.US, "%,d", engine.tanks[1].getFill()) + " / " + String.format(Locale.US, "%,d", engine.tanks[1].getMaxFill()) + " mB"));

        sections.add(main);
        return sections;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void drawHighlight(PoseStack poseStack, MultiBufferSource buffer, Level level, BlockPos pos) {
        BlockPos core = findCore(level, pos);
        if (core == null) return;

        BlockState state = level.getBlockState(core);
        Direction dir = state.getValue(FACING);
        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        VertexConsumer consumer = buffer.getBuffer(RenderType.lines());

        List<AABB> boxes = getHighlightBoxes(level, core, dir);
        for (AABB box : boxes) {
            ICustomBlockHighlight.renderAABB(poseStack, consumer, box, 0.0F, 1.0F, 0.0F, 0.4F);
        }

        poseStack.popPose();
    }

    @OnlyIn(Dist.CLIENT)
    private List<AABB> getHighlightBoxes(Level ignoredLevel, BlockPos core, Direction dir) {
        List<AABB> boxes = new ArrayList<>();
        int[] dim = getDimensions(); // up=1, down=0, north=1, south=1, west=3, east=3

        double minX = -dim[4]; // -3
        double maxX = dim[5] + 1; // 4
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 2
        double minZ = -dim[2]; // -1
        double maxZ = dim[3] + 1; // 2

        // Поворачиваем бокс в зависимости от направления
        switch (dir) {
            case NORTH:
            case SOUTH:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(core.getX(), core.getY(), core.getZ()));
                break;
            case WEST:
            case EAST:
                // Меняем местами X и Z
                boxes.add(new AABB(minZ, minY, minX, maxZ, maxY, maxX).move(core.getX(), core.getY(), core.getZ()));
                break;
        }

        return boxes;
    }

    @Override
    public boolean shouldDrawHighlight(Level level, BlockPos pos) {
        return true;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    public void addInformation(ItemStack stack, Player player, List<Component> list, boolean ext) {
        addStandardInfo(stack, player, list, ext);
    }

    public static Properties createProperties() {
        return Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
    }

    @Override
    protected int getGuiID() {
        return 0;
    }
}