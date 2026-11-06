package com.hbm.render.block;

import com.hbm.blocks.network.CableDiode;
import com.hbm.blocks.network.CableDiode.TileEntityDiode;
import com.hbm.main.HBMResourceManager;
import com.hbm.util.Library;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Matrix4f;

@OnlyIn(Dist.CLIENT)
public class RenderDiode implements BlockEntityRenderer<TileEntityDiode> {

    private static final ResourceLocation PAD_TEX =
            new ResourceLocation(RefStrings.MODID, "block/hadron_coil_alloy");
    private static final ResourceLocation CABLE_TEX =
            new ResourceLocation(RefStrings.MODID, "textures/block/red_cable.png");
    private static final ResourceLocation BLOCK_TEX =
            new ResourceLocation(RefStrings.MODID, "block/cable_diode");

    public RenderDiode(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityDiode te, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Level level = te.getLevel();
        if (level == null) return;

        BlockPos pos = te.getBlockPos();
        Direction facing = te.getBlockState().getValue(CableDiode.FACING);

        // Получаем спрайты из атласа блоков
        TextureAtlasSprite blockSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(BLOCK_TEX);
        TextureAtlasSprite padSprite = Minecraft.getInstance()
                .getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(PAD_TEX);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);

        // 1) Крышка — сдвиг к FACING
        float plateMinX = -0.5f, plateMinY = -0.5f, plateMinZ = -0.5f;
        float plateMaxX =  0.5f, plateMaxY =  0.5f, plateMaxZ =  0.5f;

        switch (facing) {
            case UP    -> plateMinY = 0.375f;
            case DOWN  -> plateMaxY = -0.375f;
            case NORTH -> plateMinZ = 0.375f;
            case SOUTH -> plateMaxZ = -0.375f;
            case WEST  -> plateMinX = 0.375f;
            case EAST  -> plateMaxX = -0.375f;
        }

        // Используем cutout() (атлас блоков)
        VertexConsumer solid = buffer.getBuffer(RenderType.cutout());

        drawBox(poseStack, solid,
                plateMinX, plateMinY, plateMinZ,
                plateMaxX, plateMaxY, plateMaxZ,
                blockSprite, packedLight, packedOverlay);

        // 2) Pad
        drawBox(poseStack, solid,
                -0.375f, -0.375f, -0.375f,
                0.375f,  0.375f,  0.375f,
                padSprite, packedLight, packedOverlay);

        // 3) Кабели
        // ВАЖНО: Убедись, что CABLE_TEX указывает на валидную standalone-текстуру,
        // или используй renderPart с VertexConsumer из cutout() и пересчитанными UV.
        // В текущем виде оставляем как есть, если кабель рендерится.
        boolean pX = canConnect(level, pos, Direction.EAST);
        boolean nX = canConnect(level, pos, Direction.WEST);
        boolean pY = canConnect(level, pos, Direction.UP);
        boolean nY = canConnect(level, pos, Direction.DOWN);
        boolean pZ = canConnect(level, pos, Direction.SOUTH);
        boolean nZ = canConnect(level, pos, Direction.NORTH);

        if (pX) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posX", CABLE_TEX, packedLight, packedOverlay);
        if (nX) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negX", CABLE_TEX, packedLight, packedOverlay);
        if (pY) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posY", CABLE_TEX, packedLight, packedOverlay);
        if (nY) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negY", CABLE_TEX, packedLight, packedOverlay);
        if (pZ) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "posZ", CABLE_TEX, packedLight, packedOverlay);
        if (nZ) HBMResourceManager.cable_neo.renderPart(poseStack, buffer, "negZ", CABLE_TEX, packedLight, packedOverlay);

        poseStack.popPose();
    }

    private void drawBox(PoseStack poseStack, VertexConsumer c,
                         float x1, float y1, float z1,
                         float x2, float y2, float z2,
                         TextureAtlasSprite sprite,
                         int light, int overlay) {
        Matrix4f m = poseStack.last().pose();
        float u0 = sprite.getU0(), u1 = sprite.getU1();
        float v0 = sprite.getV0(), v1 = sprite.getV1();

        // X-
        v(m, c, x1, y1, z1, u0, v1, -1, 0, 0, light, overlay);
        v(m, c, x1, y1, z2, u1, v1, -1, 0, 0, light, overlay);
        v(m, c, x1, y2, z2, u1, v0, -1, 0, 0, light, overlay);
        v(m, c, x1, y2, z1, u0, v0, -1, 0, 0, light, overlay);
        // X+
        v(m, c, x2, y1, z1, u0, v1, 1, 0, 0, light, overlay);
        v(m, c, x2, y2, z1, u0, v0, 1, 0, 0, light, overlay);
        v(m, c, x2, y2, z2, u1, v0, 1, 0, 0, light, overlay);
        v(m, c, x2, y1, z2, u1, v1, 1, 0, 0, light, overlay);
        // Y-
        v(m, c, x2, y1, z1, u0, v1, 0, -1, 0, light, overlay);
        v(m, c, x2, y1, z2, u1, v1, 0, -1, 0, light, overlay);
        v(m, c, x1, y1, z2, u1, v0, 0, -1, 0, light, overlay);
        v(m, c, x1, y1, z1, u0, v0, 0, -1, 0, light, overlay);
        // Y+
        v(m, c, x1, y2, z1, u0, v1, 0, 1, 0, light, overlay);
        v(m, c, x1, y2, z2, u1, v1, 0, 1, 0, light, overlay);
        v(m, c, x2, y2, z2, u1, v0, 0, 1, 0, light, overlay);
        v(m, c, x2, y2, z1, u0, v0, 0, 1, 0, light, overlay);
        // Z-
        v(m, c, x1, y1, z1, u0, v1, 0, 0, -1, light, overlay);
        v(m, c, x1, y2, z1, u0, v0, 0, 0, -1, light, overlay);
        v(m, c, x2, y2, z1, u1, v0, 0, 0, -1, light, overlay);
        v(m, c, x2, y1, z1, u1, v1, 0, 0, -1, light, overlay);
        // Z+
        v(m, c, x2, y1, z2, u0, v1, 0, 0, 1, light, overlay);
        v(m, c, x2, y2, z2, u0, v0, 0, 0, 1, light, overlay);
        v(m, c, x1, y2, z2, u1, v0, 0, 0, 1, light, overlay);
        v(m, c, x1, y1, z2, u1, v1, 0, 0, 1, light, overlay);
    }

    /** Проверка подключения соседнего блока к нашему — через energy capability или через твою Library. */
    private boolean canConnect(Level level, BlockPos pos, Direction dir) {
        BlockPos neighbor = pos.relative(dir);
        return Library.canConnect(level, neighbor, dir);
    }

    private void v(Matrix4f m, VertexConsumer c, float x, float y, float z,
                   float u, float v, float nx, float ny, float nz, int light, int overlay) {
        c.vertex(m, x, y, z).color(255, 255, 255, 255).uv(u, v)
                .overlayCoords(overlay).uv2(light).normal(nx, ny, nz).endVertex();
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityDiode te) { return true; }

    @Override
    public int getViewDistance() { return 128; }
}