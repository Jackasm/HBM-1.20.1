package com.hbm.render.tileentity;

import com.hbm.blocks.machine.MachineElectricFurnace;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.tileentity.machine.TileEntityMachineElectricFurnace;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

public class RenderElectricFurnace implements BlockEntityRenderer<TileEntityMachineElectricFurnace> {


    public RenderElectricFurnace(BlockEntityRendererProvider.Context ignoredContext) {}

    @Override
    public void render(@NotNull TileEntityMachineElectricFurnace furnace, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        BlockState state = furnace.getBlockState();
        if (!(state.getBlock() instanceof MachineElectricFurnace)) return;

        boolean isActive = state.getValue(MachineElectricFurnace.ACTIVE);
        Direction facing = state.getValue(MachineElectricFurnace.FACING);

        // Поворот как в BlastFurnace
        float rotation = switch (facing) {
            case SOUTH -> 270f;
            case WEST -> 180f;
            case EAST -> 0f;
            default -> 90f;
        };

        // Выбираем текстуру в зависимости от состояния
        ResourceLocation texture = isActive ?
                ResLocation(MODID, "textures/block/machines/machine_electric_furnace_on.png") :
                ResLocation(MODID, "textures/block/machines/machine_electric_furnace.png");

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(rotation));

        HFRWavefrontObject furnaceModel = HBMResourceManager.blast_furnace;
        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(texture));
        furnaceModel.renderAll(poseStack, consumer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMachineElectricFurnace blockEntity) {
        return true;
    }
}