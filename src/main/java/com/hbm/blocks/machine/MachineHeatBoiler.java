package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.Fluids;
import com.hbm.inventory.fluid.trait.FT_Heatable;
import com.hbm.items.machine.IItemFluidIdentifier;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityHeatBoiler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MachineHeatBoiler extends BlockDummyable implements ILookOverlay, ITooltipProvider {

    public MachineHeatBoiler(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityHeatBoiler(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setFluid();
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.HEAT_BOILER.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntityHeatBoiler boiler) {
                    boiler.tick();
                }
            };
        }
        return null;
    }

    @Override
    public @NotNull InteractionResult use(@NotNull BlockState state, @NotNull Level level, @NotNull BlockPos pos,
                                          @NotNull Player player, @NotNull InteractionHand hand, @NotNull BlockHitResult hit) {
        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity be = level.getBlockEntity(core);
        if (!(be instanceof TileEntityHeatBoiler boiler)) {
            return InteractionResult.PASS;
        }

        // Проверка на идентификатор жидкости
        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.isEmpty() && heldItem.getItem() instanceof IItemFluidIdentifier identifier) {
            FluidTypeHBM type = identifier.getType(level, core, heldItem);

            if (type.hasTrait(FT_Heatable.class) && type.getTrait(FT_Heatable.class).getEfficiency(FT_Heatable.HeatingType.BOILER) > 0) {
                boiler.tanks[0].setType(type);
                boiler.setChanged();
                player.sendSystemMessage(Component.literal("Changed type to ")
                        .append(Component.translatable(type.getDescriptionId()))
                        .append(Component.literal("!"))
                        .withStyle(Style.EMPTY.withColor(0xFFFF55)));
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        return InteractionResult.PASS;
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (stack.getDamageValue() == 1) {
            BlockPos core = findCore(level, pos);
            if (core != null && level.getBlockEntity(core) instanceof TileEntityHeatBoiler boiler) {
                boiler.hasExploded = true;
            }
        }
    }


    @Override
    public int[] getDimensions() {
        return new int[] {3, 0, 1, 1, 1, 1}; // up, down, north, south, west, east
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        int x = corePos.getX();
        int y = corePos.getY();
        int z = corePos.getZ();

        Direction rot = dir.getClockWise();

        // Добавляем экстра-блоки
        makeExtra(level, new BlockPos(x + rot.getStepX(), y, z + rot.getStepZ()));
        makeExtra(level, new BlockPos(x - rot.getStepX(), y, z - rot.getStepZ()));
        makeExtra(level, new BlockPos(x, y + 3, z));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            addStandardInfo(stack, player, tooltip, flag.isAdvanced());
        }
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    public static Block.Properties createProperties() {
        return Block.Properties.of()
                .mapColor(MapColor.METAL)
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .sound(SoundType.METAL)
                .pushReaction(PushReaction.DESTROY)
                .requiresCorrectToolForDrops();
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

        int[] dim = getDimensions(); // {3, 0, 1, 1, 1, 1}

        double minX = -dim[4]; // -1
        double maxX = dim[5] + 1; // 2
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 4
        double minZ = -dim[2]; // -1
        double maxZ = dim[3] + 1; // 2

        switch (rotation) {
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
    public void addInformation(ItemStack stack, Player player, List list, boolean ext) {
        this.addStandardInfo(stack, player, list, ext);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();

        BlockPos core = findCore(level, pos);
        if (core == null) return sections;

        BlockEntity be = level.getBlockEntity(core);
        if (!(be instanceof TileEntityHeatBoiler boiler)) return sections;

        if (boiler.hasExploded) return sections;

        // Основная секция с информацией о котле
        OverlaySection mainSection = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        mainSection.setIcon(getMachineItem());

        // Название котла
        String boilerName = Component.translatable(this.getDescriptionId()).getString();
        mainSection.addLine(Component.literal(boilerName).withStyle(style -> style.withColor(0xFFD700)));

        // Входная жидкость
        if (boiler.tanks[0].getTankType() != null && boiler.tanks[0].getTankType() != Fluids.NONE.get()) {
            String inputAmount = String.format(Locale.US, "%,d", boiler.tanks[0].getFill()) + " / " +
                    String.format(Locale.US, "%,d", boiler.tanks[0].getMaxFill()) + " mB";
            mainSection.addLine(Component.literal("§a-> §r")
                    .append(Component.translatable("overlay.boiler.input"))
                    .append(Component.literal(": "))
                    .append(boiler.tanks[0].getTankType().getLocalizedName())
                    .append(Component.literal(" (" + inputAmount + ")")));
        }

        // Выходная жидкость
        if (boiler.tanks[1].getTankType() != null && boiler.tanks[1].getTankType() != Fluids.NONE.get()) {
            String outputAmount = String.format(Locale.US, "%,d", boiler.tanks[1].getFill()) + " / " +
                    String.format(Locale.US, "%,d", boiler.tanks[1].getMaxFill()) + " mB";
            mainSection.addLine(Component.literal("§c<- §r")
                    .append(Component.translatable("overlay.boiler.output"))
                    .append(Component.literal(": "))
                    .append(boiler.tanks[1].getTankType().getLocalizedName())
                    .append(Component.literal(" (" + outputAmount + ")")));
        }

        sections.add(mainSection);

        return sections;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        BlockPos core = findCore(level, pos);
        if (core == null) return false;
        return level.getBlockEntity(core) instanceof TileEntityHeatBoiler;
    }
}