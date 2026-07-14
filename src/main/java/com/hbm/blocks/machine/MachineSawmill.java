package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.ILookOverlay;
import com.hbm.blocks.ITooltipProvider;
import com.hbm.items.ModItems;
import com.hbm.sound.ModSounds;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntitySawmill;
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
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineSawmill extends BlockDummyable implements ILookOverlay, ITooltipProvider {

    public MachineSawmill(Properties properties) {
        super(properties);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntitySawmill(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setInventory();
        } else {
            return null;
        }
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.SAWMILL.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntitySawmill sawmill) {
                    sawmill.tick();
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
        if (!(be instanceof TileEntitySawmill sawmill)) {
            return InteractionResult.PASS;
        }

        // Проверка на установку пильного диска
        ItemStack heldItem = player.getItemInHand(hand);
        if (!sawmill.hasBlade && !heldItem.isEmpty() && heldItem.getItem() == ModItems.SAWBLADE.get()) {
            heldItem.shrink(1);
            sawmill.hasBlade = true;
            sawmill.setChanged();
            level.playSound(null, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    ModSounds.UPGRADE_PLUG.get(), SoundSource.BLOCKS, 1.5F, 0.75F);
            return InteractionResult.SUCCESS;
        }

        // Извлечение готовой продукции
        if (!sawmill.getItemHandler().getStackInSlot(1).isEmpty() ||
                !sawmill.getItemHandler().getStackInSlot(2).isEmpty()) {
            for (int i = 1; i < 3; i++) {
                ItemStack stack = sawmill.getItemHandler().getStackInSlot(i);
                if (!stack.isEmpty()) {
                    if (!player.getInventory().add(stack.copy())) {
                        player.drop(stack.copy(), false);
                    }
                    sawmill.getInventoryHandler().setStackInSlot(i, ItemStack.EMPTY);
                }
            }
            player.getInventory().setChanged();
            sawmill.setChanged();
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.CONSUME;
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
    }

    @Override
    public void setPlacedBy(@NotNull Level level, @NotNull BlockPos pos, @NotNull BlockState state,
                            @Nullable LivingEntity placer, @NotNull ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);

        if (stack.getDamageValue() == 1) {
            BlockPos core = findCore(level, pos);
            if (core != null && level.getBlockEntity(core) instanceof TileEntitySawmill sawmill) {
                sawmill.hasBlade = false;
            }
        }
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
    public void printHook(RenderGuiOverlayEvent.Pre event, Level level, BlockPos pos) {
        BlockPos core = findCore(level, pos);
        if (core == null) return;

        BlockEntity be = level.getBlockEntity(core);
        if (!(be instanceof TileEntitySawmill sawmill)) return;

        List<Component> text = new ArrayList<>();
        text.add(Component.literal(sawmill.heat + " TU/t"));

        double percent = (double) sawmill.heat / 300D;
        int color = ((int) (0xFF - 0xFF * percent)) << 16 | ((int) (0xFF * percent) << 8);
        if (percent > 1D) color = 0xff0000;

        text.add(Component.literal("§x" + Integer.toHexString(color) + ((sawmill.heat * 1000 / 300) / 10D) + "%"));

        int limiter = sawmill.progress * 26 / TileEntitySawmill.processingTime;
        StringBuilder bar = new StringBuilder("§a[ ");
        for (int i = 0; i < 25; i++) {
            if (i == limiter) {
                bar.append("§r");
            }
            bar.append("▏");
        }
        bar.append("§a ]");
        text.add(Component.literal(bar.toString()));

        for (int i = 0; i < 3; i++) {
            ItemStack stack = sawmill.getItemHandler().getStackInSlot(i);
            if (!stack.isEmpty()) {
                String prefix = (i == 0 ? "§a-> " : "§c<- ");
                text.add(Component.literal(prefix + stack.getHoverName().getString() +
                        (stack.getCount() > 1 ? " x" + stack.getCount() : "")));
            }
        }

        if (sawmill.heat > 300) {
            int blinkColor = (System.currentTimeMillis() / 500 % 2 == 0) ? 0xff0000 : 0xffff00;
            text.add(Component.literal("§x" + Integer.toHexString(blinkColor) + "! ! ! OVERSPEED ! ! !"));
        }

        if (!sawmill.hasBlade) {
            text.add(Component.literal("§cBlade missing!"));
        }

        ILookOverlay.printGeneric(event, this.getDescriptionId() + ".name", 0xffff00, 0x404000, text);
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

        int[] dim = getDimensions(); // {1, 0, 1, 1, 1, 1}

        double minX = -dim[4]; // -1
        double maxX = dim[5] + 1; // 2
        double minY = -dim[1]; // 0
        double maxY = dim[0] + 1; // 2
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
}