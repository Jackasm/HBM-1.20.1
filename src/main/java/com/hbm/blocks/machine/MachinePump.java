package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityMachinePumpBase;
import com.hbm.tileentity.machine.TileEntityMachinePumpElectric;
import com.hbm.tileentity.machine.TileEntityMachinePumpSteam;
import com.hbm.util.BobMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
import java.util.Locale;

public class MachinePump extends BlockDummyable implements ILookOverlay, ITooltipProvider {

    private final boolean isElectric;

    public MachinePump(Properties properties, boolean isElectric) {
        super(properties);
        this.isElectric = isElectric;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            if (isElectric) {
                return new TileEntityMachinePumpElectric(pos, state);
            } else {
                return new TileEntityMachinePumpSteam(pos, state);
            }
        } else if (isExtra(state)) {
            if (isElectric) {
                return new TileEntityProxyCombo(pos, state).setFluid().setPower();
            } else {
                return new TileEntityProxyCombo(pos, state).setFluid();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (isCore(state)) {
            if (isElectric) {
                if (type == ModTileEntity.MACHINE_PUMP_ELECTRIC.get()) {
                    return (lvl, pos, st, tile) -> ((TileEntityMachinePumpElectric) tile).tick();
                }
            } else {
                if (type == ModTileEntity.MACHINE_PUMP_STEAM.get()) {
                    return (lvl, pos, st, tile) -> ((TileEntityMachinePumpSteam) tile).tick();
                }
            }
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        this.addStandardInfo(stack, null, tooltip, flag.isAdvanced());
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{3, 0, 1, 1, 1, 1};
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        this.makeExtra(level, corePos.offset(1, 0, 0));
        this.makeExtra(level, corePos.offset(-1, 0, 0));
        this.makeExtra(level, corePos.offset(0, 0, 1));
        this.makeExtra(level, corePos.offset(0, 0, -1));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockPos core = findCore(level, pos);
        if (core == null) return sections;

        BlockEntity be = level.getBlockEntity(core);
        if (!(be instanceof TileEntityMachinePumpBase pump)) return sections;

        OverlaySection main = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        main.setIcon(getMachineItem());

        if (pump instanceof TileEntityMachinePumpSteam steamPump) {
            main.addLine(Component.literal("§a-> " + steamPump.steam.getTankType().getLocalizedName() + ": " +
                    String.format(Locale.US, "%,d", steamPump.steam.getFill()) + " / " + String.format(Locale.US, "%,d", steamPump.steam.getMaxFill()) + " mB"));
            main.addLine(Component.literal("§c<- " + steamPump.lps.getTankType().getLocalizedName() + ": " +
                    String.format(Locale.US, "%,d", steamPump.lps.getFill()) + " / " + String.format(Locale.US, "%,d", steamPump.lps.getMaxFill()) + " mB"));
            main.addLine(Component.literal("§c<- " + steamPump.water.getTankType().getLocalizedName() + ": " +
                    String.format(Locale.US, "%,d", steamPump.water.getFill()) + " / " + String.format(Locale.US, "%,d", steamPump.water.getMaxFill()) + " mB"));
        } else if (pump instanceof TileEntityMachinePumpElectric electricPump) {
            main.addLine(Component.literal("§a-> " + String.format(Locale.US, "%,d", electricPump.getPower()) + " / " +
                    String.format(Locale.US, "%,d", electricPump.getMaxPower()) + " HE"));
            main.addLine(Component.literal("§c<- " + electricPump.water.getTankType().getLocalizedName() + ": " +
                    String.format(Locale.US, "%,d", electricPump.water.getFill()) + " / " + String.format(Locale.US, "%,d", electricPump.water.getMaxFill()) + " mB"));
        }

        if (core.getY() > TileEntityMachinePumpBase.groundHeight) {
            main.addLine(Component.literal("§" + (BobMathUtil.getBlink() ? "c" : "e") + "! ! ! ALTITUDE ! ! !"));
        }

        if (!pump.onGround) {
            main.addLine(Component.literal("§" + (BobMathUtil.getBlink() ? "c" : "e") + "! ! ! NO VALID GROUND ! ! !"));
        }

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
    private List<AABB> getHighlightBoxes(Level level, BlockPos core, Direction dir) {
        List<AABB> boxes = new ArrayList<>();
        int[] dim = getDimensions();

        double minX = -dim[4];
        double maxX = dim[5] + 1;
        double minY = -dim[1];
        double maxY = dim[0] + 1;
        double minZ = -dim[2];
        double maxZ = dim[3] + 1;

        switch (dir) {
            case WEST:
            case EAST:
                boxes.add(new AABB(minZ, minY, minX, maxZ, maxY, maxX).move(core.getX(), core.getY(), core.getZ()));
                break;
            case NORTH:
            case SOUTH:
            default:
                boxes.add(new AABB(minX, minY, minZ, maxX, maxY, maxZ).move(core.getX(), core.getY(), core.getZ()));
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