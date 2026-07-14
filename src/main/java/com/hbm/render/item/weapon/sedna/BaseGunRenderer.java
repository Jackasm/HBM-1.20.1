package com.hbm.render.item.weapon.sedna;

import com.hbm.items.weapon.sedna.GunItem;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.List;

import static com.hbm.util.ResLocation.ResLocation;

public abstract class BaseGunRenderer {

    // Константы для эффектов
    protected static final ResourceLocation FLASH_PLUME = ResLocation(RefStrings.MODID, "textures/item/weapon/lilmac_plume.png");
    protected static final ResourceLocation LASER_FLASH = ResLocation(RefStrings.MODID, "textures/item/weapon/laser_flash.png");
    protected static final ResourceLocation WHITE_TEXTURE = ResLocation(RefStrings.MODID, "textures/item/weapon/smoke_white.png");

    // Состояние рендеринга
    protected float interp;
    protected boolean isLeftHanded = false;
    protected ItemDisplayContext currentHandContext;

    protected static Matrix4f lastMuzzleFlashMatrix = null;
    protected static long lastMuzzleFlashTime = 0;
    private static final long MATRIX_VALID_TIME = 100;

    public void renderFirstPerson(ItemStack stack, ItemDisplayContext context,
                                  PoseStack poseStack, MultiBufferSource buffer,
                                  int packedLight, int packedOverlay, float partialTick) {
        this.interp = partialTick;
        this.currentHandContext = context;
        poseStack.pushPose();

        //КОСТЫЛЬ!!!! Переделать при первой возможности!!!
        // === СОХРАНЯЕМ ИСХОДНУЮ ПОЗИЦИЮ ===
        // Создаем копию текущей матрицы
        Matrix4f originalMatrix = new Matrix4f();
        poseStack.last().pose().get(originalMatrix);

        // Извлекаем компоненты из матрицы
        Vector3f translation = new Vector3f();
        Vector3f scale = new Vector3f();
        Quaternionf rotation = new Quaternionf();

        // Разбираем матрицу на компоненты
        originalMatrix.getTranslation(translation);
        originalMatrix.getScale(scale);
        originalMatrix.getUnnormalizedRotation(rotation);

        // === СОЗДАЕМ ФИКСИРОВАННУЮ МАТРИЦУ ===
        Matrix4f fixedMatrix = new Matrix4f();
        fixedMatrix.rotation(rotation);
        fixedMatrix.scale(scale);

        // Применяем трансляцию: X и Z оставляем, Y фиксируем
        fixedMatrix.translate(translation.x, -0.38640177f, translation.z);

        // Устанавливаем новую матрицу
        poseStack.last().pose().set(fixedMatrix);
        //Конец костыля

        setupFirstPersonTransforms(poseStack, stack, partialTick);
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
            renderFirstPersonWeapon(stack, poseStack, buffer, packedLight, packedOverlay, partialTick);
        } else {
            renderGroundWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
        }

        poseStack.popPose();
    }

    public void renderThirdPerson(ItemStack stack, ItemDisplayContext context,
                                  PoseStack poseStack, MultiBufferSource buffer,
                                  int packedLight, int packedOverlay) {
        if (isLeftHanded()) return;

        poseStack.pushPose();
        setupThirdPersonTransforms(poseStack, stack);
        renderThirdPersonWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }


    public void renderGUI(ItemStack stack, PoseStack poseStack,
                          MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        setupGUITransforms(poseStack, stack);
        renderGUIWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }


    public void renderGround(ItemStack stack, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        setupGroundTransforms(poseStack, stack);
        renderGroundWeapon(stack, poseStack, buffer, packedLight, packedOverlay);
        poseStack.popPose();
    }

    public void renderModTable(ItemStack stack, PoseStack poseStack,
                               MultiBufferSource buffer, int packedLight, int packedOverlay) {
        renderGround(stack, poseStack, buffer, packedLight, packedOverlay);
    }

    // Абстрактные методы, которые должны быть реализованы в подклассах
    protected abstract ResourceLocation getWeaponTexture();
    protected abstract HFRWavefrontObject getWeaponModel();
    protected abstract void renderFirstPersonWeapon(ItemStack stack, PoseStack poseStack,
                                                    MultiBufferSource buffer, int packedLight,
                                                    int packedOverlay, float partialTick);

    // Методы с реализацией по умолчанию
    protected void renderThirdPersonWeapon(ItemStack stack, PoseStack poseStack,
                                           MultiBufferSource buffer, int packedLight,
                                           int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    protected void renderGUIWeapon(ItemStack stack, PoseStack poseStack,
                                   MultiBufferSource buffer, int packedLight,
                                   int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    protected void renderGroundWeapon(ItemStack stack, PoseStack poseStack,
                                      MultiBufferSource buffer, int packedLight,
                                      int packedOverlay) {
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutout(getWeaponTexture()));
        getWeaponModel().renderAll(poseStack, builder, packedLight, packedOverlay);
    }

    // Трансформации по умолчанию
    protected void setupFirstPersonTransforms(PoseStack poseStack, ItemStack stack, float partialTick) {

        float xOffset;
        if (currentHandContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND) {
            xOffset = -4.0f;
        } else {
            xOffset = 8.0f;
        }
        poseStack.translate(xOffset, -3, -15);
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        float scale = 1.7f;
        poseStack.scale(scale, scale, scale);
    }

    protected void applyFirstPersonTransforms(PoseStack poseStack, double x, double y, double z, float xOffset, float yOffset,
                                              float scale)
    {
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
        poseStack.translate(x + xOffset, y + yOffset, z);
        poseStack.scale(scale, scale, scale);
    }

    protected void setupThirdPersonTransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 0.06D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.translate(8, 8, 6);
        poseStack.mulPose(Axis.YP.rotationDegrees(180F));
    }

    protected void setupGUITransforms(PoseStack poseStack, ItemStack stack) {
        double scale = 1.5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.XP.rotationDegrees(25));
        poseStack.mulPose(Axis.YP.rotationDegrees(45));
        poseStack.translate(0.5, 0.5, 0);
    }

    protected void setupGroundTransforms(PoseStack poseStack, ItemStack stack) {
        float scale = 0.1f;
        poseStack.scale(scale, scale, scale);
        poseStack.translate(5, 6, 5);
    }

    protected void renderSmokeNodes(PoseStack poseStack, MultiBufferSource buffer,
                                    List<GunItem.SmokeNode> nodes, double scale,
                                    int packedLight, int packedOverlay) {
        if (nodes == null || nodes.size() < 2) return;

        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucent(WHITE_TEXTURE));

        PoseStack.Pose pose = poseStack.last();
        Matrix4f matrix = pose.pose();
        Matrix3f normal = pose.normal();

        for (int i = 0; i < nodes.size() - 1; i++) {
            GunItem.SmokeNode node = nodes.get(i);
            GunItem.SmokeNode next = nodes.get(i + 1);

            float alpha = (float) node.alpha;
            float nextAlpha = (float) next.alpha;
            float width = (float) (node.width * scale);
            float nextWidth = (float) (next.width * scale);

            // Первая полоса дыма
            addVertex(builder, matrix, normal,
                    (float) node.forward, (float) node.lift, (float) node.side,
                    1, 1, 1, alpha, 0, 0, packedLight, packedOverlay);
            addVertex(builder, matrix, normal,
                    (float) node.forward, (float) node.lift, (float) (node.side + width),
                    1, 1, 1, 0, 1, 0, packedLight, packedOverlay);
            addVertex(builder, matrix, normal,
                    (float) next.forward, (float) next.lift, (float) (next.side + nextWidth),
                    1, 1, 1, 0, 1, 1, packedLight, packedOverlay);
            addVertex(builder, matrix, normal,
                    (float) next.forward, (float) next.lift, (float) next.side,
                    1, 1, 1, nextAlpha, 0, 1, packedLight, packedOverlay);

            // Вторая полоса дыма
            addVertex(builder, matrix, normal,
                    (float) node.forward, (float) node.lift, (float) node.side,
                    1, 1, 1, alpha, 0, 0, packedLight, packedOverlay);
            addVertex(builder, matrix, normal,
                    (float) node.forward, (float) node.lift, (float) (node.side - width),
                    1, 1, 1, 0, 1, 0, packedLight, packedOverlay);
            addVertex(builder, matrix, normal,
                    (float) next.forward, (float) next.lift, (float) (next.side - nextWidth),
                    1, 1, 1, 0, 1, 1, packedLight, packedOverlay);
            addVertex(builder, matrix, normal,
                    (float) next.forward, (float) next.lift, (float) next.side,
                    1, 1, 1, nextAlpha, 0, 1, packedLight, packedOverlay);


        }
    }

    protected void renderMuzzleFlash(PoseStack poseStack, long lastShot, double scale, float partialTicks) {
        int flash = 75;

        if (System.currentTimeMillis() - lastShot < flash) {
            float progress = (float)((System.currentTimeMillis() - lastShot + partialTicks * 50) / (double) flash);
            scale = scale * 0.025;

            float width = (float)(6 * progress * scale) * 4;
            float length = (float)(15 * progress * scale);
            float inset = (float)(2 * scale);
            float offset = 0.1f;

            poseStack.pushPose();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, FLASH_PLUME);
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);

            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();

            Matrix4f matrix = poseStack.last().pose();

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            addTexQuad(buffer, matrix,
                    0, -width, -inset, 1, 1,
                    0, width, -inset, 0, 1,
                    offset, width, length - inset, 0, 0,
                    offset, -width, length - inset, 1, 0);

            addTexQuad(buffer, matrix,
                    0, width, inset, 0, 1,
                    0, -width, inset, 1, 1,
                    offset, -width, -length + inset, 1, 0,
                    offset, width, -length + inset, 0, 0);

            addTexQuad(buffer, matrix,
                    0, -inset, width, 0, 1,
                    0, -inset, -width, 1, 1,
                    offset, length - inset, -width, 1, 0,
                    offset, length - inset, width, 0, 0);

            addTexQuad(buffer, matrix,
                    0, inset, -width, 1, 1,
                    0, inset, width, 0, 1,
                    offset, -length + inset, width, 0, 0,
                    offset, -length + inset, -width, 1, 0);

            tessellator.end();

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();

            poseStack.popPose();
        }
    }

    private void addTexQuad(BufferBuilder buffer, Matrix4f matrix,
                            float x1, float y1, float z1, float u1, float v1,
                            float x2, float y2, float z2, float u2, float v2,
                            float x3, float y3, float z3, float u3, float v3,
                            float x4, float y4, float z4, float u4, float v4) {
        buffer.vertex(matrix, x1, y1, z1).uv(u1, v1).endVertex();
        buffer.vertex(matrix, x2, y2, z2).uv(u2, v2).endVertex();
        buffer.vertex(matrix, x3, y3, z3).uv(u3, v3).endVertex();
        buffer.vertex(matrix, x4, y4, z4).uv(u4, v4).endVertex();
    }

    protected void renderGapFlash(PoseStack poseStack, long lastShot, float partialTicks) {
        int flash = 75;

        if (System.currentTimeMillis() - lastShot < flash) {
            float progress = (float)((System.currentTimeMillis() - lastShot + partialTicks * 50) / (double) flash);


            float height = (4 * progress);
            float length = (15 * progress);
            float lift = (3 * progress);
            float offsetFlash = (1 * progress );
            float lengthOffset = 0.125f;

            poseStack.pushPose();

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderTexture(0, FLASH_PLUME);
            RenderSystem.enableBlend();
            RenderSystem.depthMask(false);
            RenderSystem.enableDepthTest();
            RenderSystem.depthFunc(GL11.GL_LEQUAL);

            RenderSystem.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);

            Tesselator tessellator = Tesselator.getInstance();
            BufferBuilder buffer = tessellator.getBuilder();
            Matrix4f matrix = poseStack.last().pose();

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);

            addTexQuad(buffer, matrix,
                    0, -height, -offsetFlash, 1, 1,
                    0, height, -offsetFlash, 0, 1,
                    0, height + lift, length - offsetFlash, 0, 0,
                    0, -height + lift, length - offsetFlash, 1, 0);

            addTexQuad(buffer, matrix,
                    0, height, offsetFlash, 0, 1,
                    0, -height, offsetFlash, 1, 1,
                    0, -height + lift, -length + offsetFlash, 1, 0,
                    0, height + lift, -length + offsetFlash, 0, 0);

            addTexQuad(buffer, matrix,
                    0, -height, -offsetFlash, 1, 1,
                    0, height, -offsetFlash, 0, 1,
                    lengthOffset, height, length - offsetFlash, 0, 0,
                    lengthOffset, -height, length - offsetFlash, 1, 0);

            addTexQuad(buffer, matrix,
                    0, height, offsetFlash, 0, 1,
                    0, -height, offsetFlash, 1, 1,
                    lengthOffset, -height, -length + offsetFlash, 1, 0,
                    lengthOffset, height, -length + offsetFlash, 0, 0);

            tessellator.end();

            RenderSystem.enableDepthTest();
            RenderSystem.depthMask(true);
            RenderSystem.defaultBlendFunc();
            RenderSystem.disableBlend();

            poseStack.popPose();
        }
    }


    public static void renderLaserFlash(PoseStack poseStack, long lastShot,
                                                 int flash, double scale, int color) {

        lastMuzzleFlashMatrix = new Matrix4f(poseStack.last().pose());
        lastMuzzleFlashTime = System.currentTimeMillis();

        if (System.currentTimeMillis() - lastShot >= flash) {
            return;
        }

        // Вычисляем прогресс как в оригинале
        double fire = (System.currentTimeMillis() - lastShot) / (double) flash;
        double size = 4 * fire * scale;

        poseStack.pushPose();

        // Настройки рендеринга как в оригинале
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);

        // Настройка шейдера и текстуры
        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
        RenderSystem.setShaderTexture(0, LASER_FLASH);

        // Извлекаем цвет
        float r = ((color >> 16) & 0xFF) / 255.0f;
        float g = ((color >> 8) & 0xFF) / 255.0f;
        float b = (color & 0xFF) / 255.0f;

        // Используем Immediate режим как Tessellator в оригинале
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferBuilder = tessellator.getBuilder();

        Matrix4f pose = poseStack.last().pose();
        float fSize = (float) size;

        bufferBuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);

        // Первый треугольник
        bufferBuilder.vertex(pose, 0, -fSize, -fSize)
                .color(r, g, b, 1.0f)
                .uv(1, 1)
                .uv2(240, 240) // Яркость как в оригинале
                .endVertex();

        bufferBuilder.vertex(pose, 0, fSize, -fSize)
                .color(r, g, b, 1.0f)
                .uv(0, 1)
                .uv2(240, 240)
                .endVertex();

        bufferBuilder.vertex(pose, 0, fSize, fSize)
                .color(r, g, b, 1.0f)
                .uv(0, 0)
                .uv2(240, 240)
                .endVertex();

        bufferBuilder.vertex(pose, 0, -fSize, fSize)
                .color(r, g, b, 1.0f)
                .uv(1, 0)
                .uv2(240, 240)
                .endVertex();

        tessellator.end();

        // Восстанавливаем настройки
        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();

        poseStack.popPose();
    }

    // Вспомогательные методы
    private void addVertex(VertexConsumer builder, Matrix4f matrix, Matrix3f normal,
                           float x, float y, float z,
                           float r, float g, float b, float a,
                           float u, float v, int light, int overlay) {
        builder.vertex(matrix, x, y, z)
                .color(r, g, b, a)
                .uv(u, v)
                .overlayCoords(overlay)
                .uv2(light)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }

    private void addQuad(VertexConsumer builder, Matrix4f matrix, Matrix3f normal,
                         float x1, float y1, float z1, float u1, float v1,
                         float x2, float y2, float z2, float u2, float v2,
                         float x3, float y3, float z3, float u3, float v3,
                         float x4, float y4, float z4, float u4, float v4,
                         float r, float g, float b, float a, int light, int packedOverlay) {
        builder.vertex(matrix, x1, y1, z1)
                .color(r, g, b, a)
                .uv(u1, v1)
                .overlayCoords(packedOverlay)
                .uv2(light)
                .normal(normal, 0, 1, 0)
                .endVertex();
        builder.vertex(matrix, x2, y2, z2)
                .color(r, g, b, a)
                .uv(u2, v2)
                .overlayCoords(packedOverlay)
                .uv2(light)
                .normal(normal, 0, 1, 0)
                .endVertex();
        builder.vertex(matrix, x3, y3, z3)
                .color(r, g, b, a)
                .uv(u3, v3)
                .overlayCoords(packedOverlay)
                .uv2(light)
                .normal(normal, 0, 1, 0)
                .endVertex();
        builder.vertex(matrix, x4, y4, z4)
                .color(r, g, b, a)
                .uv(u4, v4)
                .overlayCoords(packedOverlay)
                .uv2(light)
                .normal(normal, 0, 1, 0)
                .endVertex();
    }

    // Методы для настроек
    protected void applyAimingTransform(PoseStack poseStack, ItemStack stack, float partialTick) {
        // Базовая реализация - можно переопределить в подклассах
    }

    protected float getAimingProgress(float partialTick) {
        if (Minecraft.getInstance().player != null) {
            ItemStack mainHand = Minecraft.getInstance().player.getMainHandItem();
            if (mainHand.getItem() instanceof GunItem) {
                return GunItem.prevAimingProgress +
                        (GunItem.aimingProgress - GunItem.prevAimingProgress) * partialTick;
            }
        }
        return 0;
    }

    protected float getTurnMagnitude(ItemStack stack) {
        if (stack.getItem() instanceof GunItem) {
            return GunItem.getIsAiming(stack) ? 0.1F : 0.5F;
        }
        return 0.5F;
    }

    protected float getSwayPeriod(ItemStack stack) { return 0.75F; }
    protected float getTurnMagnitudeDefault(ItemStack stack) { return 2.75F; }

    public float getViewFOV(ItemStack stack, float fov) { return fov; }

    public boolean isLeftHanded() { return isLeftHanded; }

    public void setLeftHanded(boolean leftHanded) { this.isLeftHanded = leftHanded; }

    protected void setupModTableTransforms(PoseStack poseStack, ItemStack stack) {
        // Базовая реализация - можно переопределить
        double scale = -5D;
        poseStack.scale((float)scale, (float)scale, (float)scale);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));
    }

    public static @Nullable Matrix4f getLastMuzzleFlashMatrix() {
        if (lastMuzzleFlashMatrix == null ||
                System.currentTimeMillis() - lastMuzzleFlashTime > MATRIX_VALID_TIME) {
            return null;
        }
        return lastMuzzleFlashMatrix;
    }
}