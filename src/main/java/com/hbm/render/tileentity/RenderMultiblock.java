package com.hbm.render.tileentity;

import com.hbm.blocks.ModBlocks;
import com.hbm.tileentity.machine.TileEntityMultiblock;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderMultiblock implements BlockEntityRenderer<TileEntityMultiblock> {

    public RenderMultiblock(BlockEntityRendererProvider.Context ignoredContext) {
    }

    @Override
    public void render(TileEntityMultiblock te, float partialTicks, PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        poseStack.translate(0, 1, 1);

        BlockState coreState = te.getBlockState();
        ResourceLocation coreLoc = getBlockTexture(coreState);
        renderFullBlockAt(poseStack, buffer, coreLoc, 0, 0, 0, packedLight, packedOverlay);

        RenderSystem.enableBlend();
        RenderSystem.enableCull();
        RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.75F);
        RenderSystem.disableCull();



        Block b = te.getBlockState().getBlock();

        if (b == ModBlocks.STRUCT_LAUNCHER_CORE.get())
            renderCompactLauncher(poseStack, buffer, packedLight, packedOverlay);

        if (b == ModBlocks.STRUCT_LAUNCHER_CORE_LARGE.get())
            renderLaunchTable(poseStack, buffer, packedLight, packedOverlay);

        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();
    }

    private ResourceLocation getBlockTexture(BlockState state) {
        ResourceLocation blockId = ForgeRegistries.BLOCKS.getKey(state.getBlock());
        return ResLocation(RefStrings.MODID, "textures/block/" + Objects.requireNonNull(blockId).getPath() + ".png");
    }

    private void renderCompactLauncher(PoseStack poseStack, MultiBufferSource buffer,
                                       int packedLight, int packedOverlay) {
        BlockState state = ModBlocks.STRUCT_LAUNCHER.get().defaultBlockState();
        ResourceLocation loc = getBlockTexture(state);

        for (int i = -1; i <= 1; i++) {
            for (int j = -1; j <= 1; j++) {
                if (i != 0 || j != 0) {
                    renderSmolBlockAt(poseStack, buffer, loc, i, 0, j, packedLight, packedOverlay);
                }
            }
        }
    }

    private void renderLaunchTable(PoseStack poseStack, MultiBufferSource buffer,
                                   int packedLight, int packedOverlay) {
        BlockState stateLauncher = ModBlocks.STRUCT_LAUNCHER.get().defaultBlockState();
        ResourceLocation locLauncher = getBlockTexture(stateLauncher);

        for (int i = -4; i <= 4; i++) {
            for (int j = -4; j <= 4; j++) {
                if (i != 0 || j != 0) {
                    renderSmolBlockAt(poseStack, buffer, locLauncher, i, 0, j, packedLight, packedOverlay);
                }
            }
        }

        BlockState stateScaffold = ModBlocks.STRUCT_SCAFFOLD.get().defaultBlockState();
        ResourceLocation locScaffold = getBlockTexture(stateScaffold);

        long time = System.currentTimeMillis() % 4000 / 1000;
        switch ((int) time) {
            case 0:
                for (int k = 1; k < 12; k++)
                    renderSmolBlockAt(poseStack, buffer, locScaffold, 3, k, 0, packedLight, packedOverlay);
                break;
            case 1:
                for (int k = 1; k < 12; k++)
                    renderSmolBlockAt(poseStack, buffer, locScaffold, 0, k, 3, packedLight, packedOverlay);
                break;
            case 2:
                for (int k = 1; k < 12; k++)
                    renderSmolBlockAt(poseStack, buffer, locScaffold, -3, k, 0, packedLight, packedOverlay);
                break;
            case 3:
                for (int k = 1; k < 12; k++)
                    renderSmolBlockAt(poseStack, buffer, locScaffold, 0, k, -3, packedLight, packedOverlay);
                break;
        }
    }

    private void renderSmolBlockAt(PoseStack poseStack, MultiBufferSource buffer,
                                   ResourceLocation loc, int x, int y, int z,
                                   int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutoutNoCull(loc));

        float pixel = 1F / 16F;
        float size = 11 * pixel / 2;
        float min = 1 - size;
        float max = size;

        // Грань Z- (задняя)
        drawQuad(consumer, poseStack, min, min, max, max, min, max, max, max, max, min, max, max, packedLight, packedOverlay, 0, 0, -1);
        // Грань Z+ (передняя)
        drawQuad(consumer, poseStack, min, min, min, max, min, min, max, max, min, min, max, min, packedLight, packedOverlay, 0, 0, 1);
        // Грань X- (левая)
        drawQuad(consumer, poseStack, min, min, max, min, min, min, min, max, min, min, max, max, packedLight, packedOverlay, -1, 0, 0);
        // Грань X+ (правая)
        drawQuad(consumer, poseStack, max, min, min, max, min, max, max, max, max, max, max, min, packedLight, packedOverlay, 1, 0, 0);
        // Грань Y- (нижняя)
        drawQuad(consumer, poseStack, min, min, max, max, min, max, max, min, min, min, min, min, packedLight, packedOverlay, 0, -1, 0);
        // Грань Y+ (верхняя)
        drawQuad(consumer, poseStack, min, max, min, max, max, min, max, max, max, min, max, max, packedLight, packedOverlay, 0, 1, 0);

        poseStack.popPose();
    }

    private void drawQuad(VertexConsumer consumer, PoseStack poseStack,
                          float x1, float y1, float z1,
                          float x2, float y2, float z2,
                          float x3, float y3, float z3,
                          float x4, float y4, float z4,
                          int packedLight, int packedOverlay,
                          float nx, float ny, float nz) {
        var pose = poseStack.last().pose();

        consumer.vertex(pose, x1, y1, z1)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, 1)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(nx, ny, nz)
                .endVertex();
        consumer.vertex(pose, x2, y2, z2)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1, 1)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(nx, ny, nz)
                .endVertex();
        consumer.vertex(pose, x3, y3, z3)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(1, 0)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(nx, ny, nz)
                .endVertex();
        consumer.vertex(pose, x4, y4, z4)
                .color(1.0F, 1.0F, 1.0F, 1.0F)
                .uv(0, 0)
                .overlayCoords(packedOverlay)
                .uv2(packedLight)
                .normal(nx, ny, nz)
                .endVertex();
    }

    private void renderFullBlockAt(PoseStack poseStack, MultiBufferSource buffer,
                                   ResourceLocation loc, int x, int y, int z,
                                   int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.translate(x, y, z);
        poseStack.mulPose(com.mojang.math.Axis.XP.rotationDegrees(180));

        VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(loc));

        float min = 0;
        float max = 1;

        // Грань Z- (normal 0,0,-1) — задняя
        drawQuad(consumer, poseStack, min, min, max, max, min, max, max, max, max, min, max, max, packedLight, packedOverlay, 0, 0, -1);
        // Грань Z+ (normal 0,0,1) — передняя
        drawQuad(consumer, poseStack, min, min, min, max, min, min, max, max, min, min, max, min, packedLight, packedOverlay, 0, 0, 1);
        // Грань X- (normal -1,0,0) — левая
        drawQuad(consumer, poseStack, min, min, max, min, min, min, min, max, min, min, max, max, packedLight, packedOverlay, -1, 0, 0);
        // Грань X+ (normal 1,0,0) — правая
        drawQuad(consumer, poseStack, max, min, min, max, min, max, max, max, max, max, max, min, packedLight, packedOverlay, 1, 0, 0);
        // Грань Y- (normal 0,-1,0) — нижняя
        drawQuad(consumer, poseStack, min, min, max, max, min, max, max, min, min, min, min, min, packedLight, packedOverlay, 0, -1, 0);
        // Грань Y+ (normal 0,1,0) — верхняя
        drawQuad(consumer, poseStack, min, max, min, max, max, min, max, max, max, min, max, max, packedLight, packedOverlay, 0, 1, 0);

        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityMultiblock te) {
        return true;
    }

    @Override
    public int getViewDistance() {
        return 256;
    }
}