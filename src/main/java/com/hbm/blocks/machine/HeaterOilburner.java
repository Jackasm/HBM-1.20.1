package com.hbm.blocks.machine;

import com.hbm.api.block.IToolable;
import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.inventory.fluid.FluidTypeHBM;
import com.hbm.inventory.fluid.trait.FT_Flammable;
import com.hbm.items.tool.ItemTooling;
import com.hbm.render.overlay.OverlayContext;
import com.hbm.render.overlay.OverlaySection;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntityHeaterOilburner;
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
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class HeaterOilburner extends BlockDummyable implements ILookOverlay, ITooltipProvider, IToolable {

    public HeaterOilburner(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntityHeaterOilburner(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setFluid().heatSource();
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.HEATER_OILBURNER.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntityHeaterOilburner heater) {
                    heater.tick();
                }
            };
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

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        BlockPos core = findCore(level, pos);
        if (core == null) return InteractionResult.PASS;

        BlockEntity be = level.getBlockEntity(core);
        if (be instanceof TileEntityHeaterOilburner heater) {
            NetworkHooks.openScreen((ServerPlayer) player, heater, core);
            return InteractionResult.CONSUME;
        }
        return InteractionResult.PASS;
    }

    @Override
    public int[] getDimensions() {
        return new int[] {1, 0, 1, 1, 1, 1}; // up, down, north, south, west, east
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

        // Добавляем экстра-блоки
        makeExtra(level, new BlockPos(x + 1, y, z));
        makeExtra(level, new BlockPos(x - 1, y, z));
        makeExtra(level, new BlockPos(x, y, z + 1));
        makeExtra(level, new BlockPos(x, y, z - 1));
        makeExtra(level, new BlockPos(x, y + 1, z));
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @Nullable BlockGetter level, @NotNull List<Component> tooltip, @NotNull TooltipFlag flag) {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            addStandardInfo(stack, player, tooltip, flag.isAdvanced());
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public List<OverlaySection> getSections(Level level, BlockPos pos, OverlayContext context) {
        List<OverlaySection> sections = new ArrayList<>();
        BlockPos core = findCore(level, pos);
        if (core == null) return sections;

        BlockEntity be = level.getBlockEntity(core);
        if (!(be instanceof TileEntityHeaterOilburner heater)) return sections;

        OverlaySection section = new OverlaySection(OverlaySection.Type.BLOCK_MAIN);
        section.setIcon(getMachineItem());

        List<Component> text = new ArrayList<>();
        text.add(Component.literal("§a-> §r" + heater.setting + " mB/t"));
        FluidTypeHBM type = heater.tank.getTankType();
        if (type.hasTrait(FT_Flammable.class)) {
            int heat = (int) (type.getTrait(FT_Flammable.class).getHeatEnergy() * heater.setting / 1000);
            text.add(Component.literal("§c<- §r" + String.format(Locale.US, "%,d", heat) + " TU/t"));
        }

        ILookOverlay.printGeneric(section, Component.translatable(this.getDescriptionId()).getString(), 0xffff00, text);
        sections.add(section);
        return sections;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldRender(Level level, BlockPos pos) {
        return true;
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

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDrawHighlight(Level level, BlockPos pos) {
        return true;
    }

    @OnlyIn(Dist.CLIENT)
    public List<AABB> getHighlightBoxes(Level level, BlockPos corePos, Direction rotation) {
        List<AABB> boxes = new ArrayList<>();

        int[] dim = getDimensions(); // {1, 0, 1, 1, 1, 1}

        double minX = -dim[4]; // -1
        double maxX = dim[5] + 1; // 2
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 2
        double minZ = -dim[2]; // -1
        double maxZ = dim[3] + 1; // 2

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
    public void addInformation(ItemStack stack, Player player, List list, boolean ext) {
        this.addStandardInfo(stack, player, list, ext);
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
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
    public boolean onScrew(Level level, Player player, BlockPos pos, Direction side, float fX, float fY, float fZ, IToolable.ToolType tool) {

        if (tool != IToolable.ToolType.SCREWDRIVER)
            return false;

        if (level.isClientSide) return true;

        BlockPos core = findCore(level, pos);

        if (core == null) return false;

        BlockEntity te = level.getBlockEntity(core);

        if (!(te instanceof TileEntityHeaterOilburner tile)) return false;

        tile.toggleSetting();
        tile.setChanged();

        return true;
    }
}