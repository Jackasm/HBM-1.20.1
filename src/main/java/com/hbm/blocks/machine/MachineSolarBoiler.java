package com.hbm.blocks.machine;

import com.hbm.blocks.BlockDummyable;
import com.hbm.blocks.ICustomBlockHighlight;
import com.hbm.blocks.ILookOverlay;
import com.hbm.inventory.fluid.tank.FluidTankHBM;
import com.hbm.tileentity.ModTileEntity;
import com.hbm.tileentity.TileEntityProxyCombo;
import com.hbm.tileentity.machine.TileEntitySolarBoiler;
import com.hbm.util.BobMathUtil;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.ChatFormatting;
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
import net.minecraftforge.client.event.RenderGuiOverlayEvent;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineSolarBoiler extends BlockDummyable implements ILookOverlay {

    public MachineSolarBoiler(Properties properties) {
        super(properties);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state) {
        if (isCore(state)) {
            return new TileEntitySolarBoiler(pos, state);
        } else if (isExtra(state)) {
            return new TileEntityProxyCombo(pos, state).setFluid();
        }
        return null;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type) {
        if (type == ModTileEntity.SOLAR_BOILER.get()) {
            return (level1, pos, state1, blockEntity) -> {
                if (blockEntity instanceof TileEntitySolarBoiler boiler) {
                    boiler.tick();
                }
            };
        }
        return null;
    }

    @Override
    public int[] getDimensions() {
        return new int[]{2, 0, 1, 1, 1, 1};
    }

    @Override
    public int getZOffset() {
        return -1;
    }

    @Override
    public ItemStack getMachineItem() {
        return new ItemStack(this);
    }

    @Override
    protected int getGuiID() {
        return 0;
    }

    @Override
    protected void fillSpace(Level level, BlockPos pos, BlockPos corePos, Direction dir) {
        super.fillSpace(level, pos, corePos, dir);

        // Добавляем extra блок на 2 блока выше
        this.makeExtra(level, corePos.above(2));
    }

    @Override
    public @NotNull RenderShape getRenderShape(@NotNull BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    @Override
    public void printHook(RenderGuiOverlayEvent.Pre event, Level level, BlockPos pos) {
        BlockPos corePos = findCore(level, pos);
        if (corePos == null) return;

        BlockEntity te = level.getBlockEntity(corePos);
        if (!(te instanceof TileEntitySolarBoiler boiler)) return;

        List<Component> text = new ArrayList<>();

        FluidTankHBM[] tanks = boiler.getAllTanks();

        for (int i = 0; i < tanks.length; i++) {
            String arrow = i < 1 ? (ChatFormatting.GREEN + "-> ") : (ChatFormatting.RED + "<- ");
            text.add(Component.literal(arrow + ChatFormatting.RESET + tanks[i].getTankType().getLocalizedName() + ": " + tanks[i].getFill() + "/" + tanks[i].getMaxFill() + "mB"));
        }

        if (boiler.display < 1) {
            int color = BobMathUtil.getBlink() ? 0xff0000 : 0xffff00;
            text.add(Component.literal("[").append(Component.literal("Too cold!").withStyle(style -> style.withColor(color))).append(Component.literal("]")));
        }

        ILookOverlay.printGeneric(event, Component.translatable(this.getDescriptionId() + ".name").getString(), 0xffff00, 0x404000, text);
    }

    public static Properties createProperties() {
        return Properties.of()
                .strength(5.0F, 10.0F)
                .noOcclusion()
                .requiresCorrectToolForDrops();
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

        int[] dim = getDimensions();
        AABB box = new AABB(
                core.getX() - dim[4],
                core.getY() - dim[1],
                core.getZ() - dim[2],
                core.getX() + dim[5] + 1,
                core.getY() + dim[0] + 1,
                core.getZ() + dim[3] + 1
        );

        ICustomBlockHighlight.renderAABB(poseStack, consumer, box, 0.0F, 1.0F, 0.0F, 0.4F);

        poseStack.popPose();
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public boolean shouldDrawHighlight(Level level, BlockPos pos) {
        return true;
    }
}