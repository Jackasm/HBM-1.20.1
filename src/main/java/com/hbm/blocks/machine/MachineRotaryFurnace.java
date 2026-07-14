package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.ILookOverlay;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityMachineRotaryFurnace;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineRotaryFurnace extends BlockDummyable implements ILookOverlay {

    public MachineRotaryFurnace(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityMachineRotaryFurnace(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setInventory().setFluid();
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state,
                                                                  @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.MACHINE_ROTARY_FURNACE.get()) {
            return (lvl, pos, st, tile) -> {
                if (tile instanceof TileEntityMachineRotaryFurnace furnace) {
                    furnace.tick();
                }
            };
        }
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{4, 0, 1, 1, 2, 2}; // up, down, north, south, west, east
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        Direction forward = dir.getOpposite();
        Direction right = dir.getClockWise();

        BlockPos basePos = corePos.relative(forward, getZOffset());

        for (int i = -2; i <= 2; i++) {
            this.makeExtra(level, basePos.relative(right, i));
        }

        this.makeExtra(level, basePos.relative(forward, 1).relative(right, 2));

        this.makeExtra(level, basePos.relative(right, 1).above(4));

        this.makeExtra(level, basePos.relative(forward, 1).relative(right, 1));
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
        int[] dim = getDimensions(); // {4, 0, 1, 1, 2, 2}

        double minX = -dim[4]; // -2
        double maxX = dim[5] + 1; // 3
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 5
        double minZ = -dim[2]; // -1
        double maxZ = dim[3] + 1; // 2

        switch (dir) {
            case NORTH:
            case SOUTH:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(core.getX(), core.getY(), core.getZ()));
                break;
            case WEST:
            case EAST:
                boxes.add(new AABB(minZ, minY, minX, maxZ, maxY, maxX).move(core.getX(), core.getY(), core.getZ()));
                break;
            default:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(core.getX(), core.getY(), core.getZ()));
                break;
        }

        return boxes;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<com.hbm.render.overlay.OverlaySection> getSections(Level level, BlockPos pos,
                                                                   com.hbm.render.overlay.OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockPos core = findCore(level, pos);
        if (core == null) return sections;

        BlockEntity te = level.getBlockEntity(core);
        if (!(te instanceof TileEntityMachineRotaryFurnace furnace)) return sections;

        Direction dir = level.getBlockState(core).getValue(FACING);
        Direction right = dir.getClockWise();
        Direction forward = dir.getOpposite();

        // Steam info
        if (isLookingAt(level, pos, core, forward, right, -1, -1, 0) ||
                isLookingAt(level, pos, core, forward, right, -1, -2, 0)) {
            com.hbm.render.overlay.OverlaySection steamSection =
                    new com.hbm.render.overlay.OverlaySection(com.hbm.render.overlay.OverlaySection.Type.BLOCK_MAIN);
            steamSection.addLine(Component.literal("§a-> §r" + furnace.tanks[1].getTankType().getLocalizedName()));
            steamSection.addLine(Component.literal("§c<- §r" + furnace.tanks[2].getTankType().getLocalizedName()));
            sections.add(steamSection);
        }

        // Fluid info
        if (isLookingAt(level, pos, core, forward, right, 1, 2, 0) ||
                isLookingAt(level, pos, core, forward, right, -1, 2, 0)) {
            com.hbm.render.overlay.OverlaySection fluidSection =
                    new com.hbm.render.overlay.OverlaySection(com.hbm.render.overlay.OverlaySection.Type.BLOCK_MAIN);
            fluidSection.addLine(Component.literal("§a-> §r" + furnace.tanks[0].getTankType().getLocalizedName()));
            sections.add(fluidSection);
        }

        // Fuel info
        if (isLookingAt(level, pos, core, forward, right, 1, 1, 0)) {
            com.hbm.render.overlay.OverlaySection fuelSection =
                    new com.hbm.render.overlay.OverlaySection(com.hbm.render.overlay.OverlaySection.Type.BLOCK_MAIN);
            fuelSection.addLine(Component.literal("§e-> §rFuel"));
            sections.add(fuelSection);
        }

        return sections;
    }

    private boolean isLookingAt(Level level, BlockPos hitPos, BlockPos core,
                                Direction forward, Direction right, int exDir, int exRot, int exY) {
        int offset = getZOffset();
        BlockPos basePos = core.relative(forward, offset);

        int iX = basePos.getX() + forward.getStepX() * exDir + right.getStepX() * exRot;
        int iY = core.getY() + exY;
        int iZ = basePos.getZ() + forward.getStepZ() * exDir + right.getStepZ() * exRot;

        return iX == hitPos.getX() && iZ == hitPos.getZ() && iY == hitPos.getY();
    }
}