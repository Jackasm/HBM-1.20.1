package com.hbm.blocks.machine;

import com.hbm.blocks.*;
import com.hbm.items.ModItems;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityStirling;
import com.hbm.util.BobMathUtil;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MachineStirling extends BlockDummyable implements ILookOverlay, ITooltipProvider {

    public final StirlingType type;

    public enum StirlingType {
        NORMAL,
        STEEL,
        CREATIVE
    }

    public MachineStirling(Properties properties, StirlingType type) {
        super(properties);
        this.type = type;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityStirling(pos, state, type);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setPower();
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (isCore(state)) {
            if (type == ModTileEntity.MACHINE_STIRLING.get()) {
                return (lvl, pos, st, tile) -> {
                    if (tile instanceof TileEntityStirling stirling) {
                        stirling.tick();
                    }
                };
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

        if (!player.isShiftKeyDown()) {
            BlockPos core = findCore(level, pos);
            if (core == null) return InteractionResult.PASS;

            BlockEntity te = level.getBlockEntity(core);
            if (te instanceof TileEntityStirling stirling) {
                ItemStack heldItem = player.getItemInHand(hand);
                int gearMeta = stirling.getGearMeta();

                if (!stirling.hasCog && !heldItem.isEmpty() && heldItem.getItem() == ModItems.GEAR_LARGE.get() && heldItem.getDamageValue() == gearMeta) {
                    heldItem.shrink(1);
                    stirling.hasCog = true;
                    stirling.setChanged();
                    level.playSound(null, core.getX() + 0.5, core.getY() + 0.5, core.getZ() + 0.5,
                            ModSounds.UPGRADE_PLUG.get(), SoundSource.BLOCKS, 1.5F, 0.75F);
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (type == StirlingType.STEEL && stack.getDamageValue() == 1) {
            BlockPos core = findCore(level, pos);
            if (core == null) return;

            BlockEntity te = level.getBlockEntity(core);
            if (te instanceof TileEntityStirling stirling) {
                stirling.hasCog = false;
                stirling.setChanged();
            }
        }
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
        return new int[]{1, 0, 1, 1, 1, 1};
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    public int getXOffset() {
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

        BlockEntity te = level.getBlockEntity(core);
        if (!(te instanceof TileEntityStirling stirling)) return sections;

        OverlaySection main = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        main.setIcon(getMachineItem());

        if (type != StirlingType.CREATIVE) {
            int maxHeat = stirling.maxHeat();
            double percent = (double) stirling.heat / (double) maxHeat;
            int color = ((int) (0xFF - 0xFF * percent)) << 16 | ((int) (0xFF * percent) << 8);
            if (percent > 1D) color = 0xff0000;

            main.addLine(Component.literal(String.format(Locale.US, "%,d", stirling.heat) + " TU/t"));
            main.addLine(Component.literal((stirling.hasCog ? stirling.powerBuffer : 0) + " HE/t"));
            main.addLine(Component.literal("§" + (color == 0xff0000 ? "c" : "a") + ((stirling.heat * 1000 / maxHeat) / 10D) + "%"));

            if (stirling.heat > maxHeat) {
                main.addLine(Component.literal("§" + (BobMathUtil.getBlink() ? "c" : "e") + "! ! ! OVERSPEED ! ! !"));
            }
            if (!stirling.hasCog) {
                main.addLine(Component.literal("§cGear missing!"));
            }
        } else {
            main.addLine(Component.literal(stirling.heat + " TU/t"));
            main.addLine(Component.literal(stirling.powerBuffer + " HE/t"));
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
    private List<AABB> getHighlightBoxes(Level ignoredLevel, BlockPos core, Direction dir) {
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