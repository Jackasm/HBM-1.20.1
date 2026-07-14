package com.hbm.items.weapon.sedna.factory;

import java.util.Objects;
import java.util.function.BiConsumer;
import com.hbm.entity.projectile.EntityBulletBeamBase;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.hbm.render.util.BeamPronter;
import com.hbm.render.util.BeamPronter.EnumWaveType;
import com.hbm.render.util.BeamPronter.EnumBeamType;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import com.hbm.entity.projectile.EntityBulletBaseMK4;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.lwjgl.opengl.GL11;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class LegoClient {

    private static final ResourceLocation FLARE_TEXTURE = ResLocation("textures/particles/flare.png");

    private static final ResourceLocation WIRE_GREYSCALE_TEX =
            ResLocation("hbm", "textures/block/network/wire_greyscale.png");


    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_STANDARD_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0xFFBF00,
                    0xFFFFFF, false);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FLECHETTE_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0x7F006E,
                    0xFF7FED, true);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_AP_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0x7F006E,
                    0xFF7FED, true);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_EXPRESS_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0x7F006E,
                    0xFF7FED, true);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_DU_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0x7F006E,
                    0xFF7FED, true);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_HE_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0x7F006E,
                    0xFF7FED, true);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_SM_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0x7F006E,
                    0xFF7FED, true);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_BLACK_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0x7F006E,
                    0xFF7FED, true);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_TRACER_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0x7F006E,
                    0xFF7FED, true);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_LEGENDARY_BULLET =
            (bullet, interp) -> renderTracer(bullet, interp, 0x7F006E,
                    0xFF7FED, true);

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_RED = (bullet, interp) -> renderStandardLaser(bullet, interp, 0x80, 0x15, 0x15);
    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_EMERALD = (bullet, interp) -> renderStandardLaser(bullet, interp, 0x15, 0x80, 0x15);
    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_CYAN = (bullet, interp) -> renderStandardLaser(bullet, interp, 0x15, 0x15, 0x80);
    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_PURPLE = (bullet, interp) -> renderStandardLaser(bullet, interp, 0x60, 0x15, 0x80);
    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LASER_WHITE = (bullet, interp) -> renderStandardLaser(bullet, interp, 0x15, 0x15, 0x15);

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FLARE = (bullet, interp) -> renderFlare(bullet, interp, 1F, 0.5F, 0.5F);
    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FLARE_SUPPLY = (bullet, interp) -> renderFlare(bullet, interp, 0.5F, 0.5F, 1F);
    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_FLARE_WEAPON = (bullet, interp) -> renderFlare(bullet, interp, 0.5F, 1F, 0.5F);

    public static void renderFlare(EntityBulletBaseMK4 bullet, float partialTicks, float r, float g, float b) {
        if (bullet.tickCount < 2) return;

        double scale = Math.min(5, (bullet.tickCount + partialTicks - 2) * 0.5) * (0.8 + bullet.level().random.nextDouble() * 0.4);
        renderFlareSprite(bullet, r, g, b, scale, 0.5F, 0.75F);
    }

    public static void renderFlareSprite(Entity bullet, float r, float g, float b,
                                         double scale, float outerAlpha, float innerAlpha) {

        if (bullet.tickCount < 2) return;

        if (!(bullet instanceof EntityBulletBaseMK4 bulletMK4)) return;

        Matrix4f poseMatrix = bulletMK4.getRenderPose();

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE);
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.enableDepthTest();
        RenderSystem.depthFunc(GL11.GL_LEQUAL);

        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
        RenderSystem.setShaderTexture(0, FLARE_TEXTURE);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);

        Matrix4f matrix = poseStack.last().pose();
        float halfScale = (float) scale;

        int light = 15728880;

        buffer.vertex(matrix, -halfScale, -halfScale, 0)
                .color(r, g, b, outerAlpha)
                .uv(1, 1)
                .uv2(light)
                .endVertex();

        buffer.vertex(matrix, -halfScale, halfScale, 0)
                .color(r, g, b, outerAlpha)
                .uv(1, 0)
                .uv2(light)
                .endVertex();

        buffer.vertex(matrix, halfScale, halfScale, 0)
                .color(r, g, b, outerAlpha)
                .uv(0, 0)
                .uv2(light)
                .endVertex();

        buffer.vertex(matrix, halfScale, -halfScale, 0)
                .color(r, g, b, outerAlpha)
                .uv(0, 1)
                .uv2(light)
                .endVertex();

        halfScale *= 0.5F;

        buffer.vertex(matrix, -halfScale, -halfScale, 0.001F)
                .color(1.0F, 1.0F, 1.0F, innerAlpha)
                .uv(1, 1)
                .uv2(light)
                .endVertex();

        buffer.vertex(matrix, -halfScale, halfScale, 0.001F)
                .color(1.0F, 1.0F, 1.0F, innerAlpha)
                .uv(1, 0)
                .uv2(light)
                .endVertex();

        buffer.vertex(matrix, halfScale, halfScale, 0.001F)
                .color(1.0F, 1.0F, 1.0F, innerAlpha)
                .uv(0, 0)
                .uv2(light)
                .endVertex();

        buffer.vertex(matrix, halfScale, -halfScale, 0.001F)
                .color(1.0F, 1.0F, 1.0F, innerAlpha)
                .uv(0, 1)
                .uv2(light)
                .endVertex();

        BufferUploader.drawWithShader(buffer.end());

        RenderSystem.depthMask(true);
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    }

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_BLACK_LIGHTNING = (bullet, interp) -> {

        if (bullet.getConfig() == null) return;

        double age = Mth.clamp(
                1D - ((double) bullet.tickCount - 2 + interp) / (double) bullet.getConfig().expires,
                0, 1
        );

        float beamLength = bullet.getBeamLength();
        if (beamLength <= 0) return;

        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        double scale = 5D;
        float ageScale = (float) (age * scale);
        poseStack.scale(ageScale, 1.0f, ageScale);

        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        int darkColor = 0x4C3093;
        int lightColor = 0x000000;

        MultiBufferSource.BufferSource bufferSource = bullet.getBufferSource();

        renderBulletStandard(poseStack, darkColor, lightColor, beamLength, true,0xF000F0, bufferSource);

    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_CRACKLE_SIMPLE = (bullet, interp) -> {
        if (bullet.getConfig() == null) return;

        double age = Mth.clamp(
                1D - ((double) bullet.tickCount - 2 + interp) / (double) bullet.getConfig().expires,
                0, 1
        );

        float beamLength = bullet.getBeamLength();
        if (beamLength <= 0) return;

        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        // Эффект появления/исчезания
        double scale = 5D;
        float ageScale = (float) (age * scale);
        poseStack.scale(ageScale, 1.0f, ageScale);

        // Перемещаем к концу луча и вращаем
        //poseStack.translate(0, 0,  beamLength);
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        // Цвета как в оригинале
        int darkColor = 0xE3D692;  // Бежево-золотой
        int lightColor = 0xFFFFFF; // Белый

        MultiBufferSource.BufferSource bufferSource = bullet.getBufferSource();

        // Рисуем луч используя уже существующий метод renderBulletStandard
        renderBulletStandard(poseStack, darkColor, lightColor, beamLength, true,0xF000F0, bufferSource);

    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LIGHTNING = (beam, interp) -> {
        if (beam.getConfig() == null) return;

        // Эффект затухания по времени
        double age = Mth.clamp(
                1D - ((double) beam.tickCount - 2 + interp) / (double) beam.getConfig().expires,
                0, 1
        );

        float beamLength = beam.getBeamLength();
        if (beamLength <= 0) return;

        Matrix4f poseMatrix = beam.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);
        MultiBufferSource.BufferSource bufferSource = beam.getBufferSource();

        poseStack.pushPose();

        float ageScale = (float)(age / 2 + 0.5);
        poseStack.scale(ageScale, 1.0f, ageScale);

        double scale = 0.075D;

        int colorInner = ((int)(0xE0 * age) << 16) | ((int)(0xF0 * age) << 8) | (int) (0xFF * age);

        Vec3 delta = new Vec3(0, 0, -beam.getBeamLength());
        int segments = (int)(beamLength / 2 + 1);

        BeamPronter.prontBeam(
                poseStack,
                bufferSource,
                delta,
                EnumWaveType.RANDOM,
                EnumBeamType.SOLID,
                colorInner,
                colorInner,
                beam.tickCount / 3,
                segments,
                (float)scale,
                4,
                0.25F
        );

        // ВТОРОЙ ЛУЧ (внешний) - толстый, с большой амплитудой
        BeamPronter.prontBeam(
                poseStack,
                bufferSource,
                delta,
                EnumWaveType.RANDOM,
                EnumBeamType.SOLID,
                colorInner,
                colorInner,
                beam.tickCount,            // start seed
                segments,
                (float)scale * 7F,           // size (большая амплитуда)
                2,                            // layers (меньше слоев)
                0.0625F                       // thickness
        );

        // ТРЕТИЙ ЛУЧ (еще один внешний) - с другим seed для разнообразия
        BeamPronter.prontBeam(
                poseStack,
                bufferSource,
                delta,
                EnumWaveType.RANDOM,
                EnumBeamType.SOLID,
                colorInner,
                colorInner,
                beam.tickCount / 2,        // start seed (половина)
                segments,
                (float)scale * 7F,           // size
                2,                            // layers
                0.0625F                       // thickness
        );

        poseStack.popPose();

        // Восстановление
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    };



    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_LIGHTNING_SUB = (beam, interp) -> {
        if (beam.getConfig() == null) return;

        double age = Mth.clamp(
                1D - ((double) beam.tickCount - 2 + interp) / (double) beam.getConfig().expires,
                0, 1
        );

        float beamLength = beam.getBeamLength();
        if (beamLength <= 0) return;

        Matrix4f poseMatrix = beam.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);
        MultiBufferSource.BufferSource bufferSource = beam.getBufferSource();

        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        // Измененный масштаб для подводной версии
        float ageScale = (float)(age / 2 + 0.15);
        poseStack.scale(ageScale, 1.0f, ageScale);

        Vec3 delta = new Vec3(0, 0, -beamLength);
        int segments = (int)(beamLength / 2 + 1);

        double scale = 0.075D;
        int colorInner = ((int)(0x20 * age) << 16) | ((int)(0x20 * age) << 8) | (int) (0x40 * age);
        int colorOuter = ((int)(0x40 * age) << 16) | ((int)(0x40 * age) << 8) | (int) (0x80 * age);

        BeamPronter.prontBeam(
                poseStack,
                bufferSource,
                delta,
                EnumWaveType.RANDOM,
                EnumBeamType.SOLID,
                colorInner,
                colorInner,
                beam.tickCount / 3,
                segments,
                (float)scale,
                4,
                0.25F
        );

        BeamPronter.prontBeam(
                poseStack,
                bufferSource,
                delta,
                EnumWaveType.RANDOM,
                EnumBeamType.SOLID,
                colorOuter,
                colorOuter,
                beam.tickCount,
                segments,
                (float)scale * 7F,
                2,
                0.0625F
        );

        BeamPronter.prontBeam(
                poseStack,
                bufferSource,
                delta,
                EnumWaveType.RANDOM,
                EnumBeamType.SOLID,
                colorOuter,
                colorOuter,
                beam.tickCount / 2,
                segments,
                (float)scale * 7F,
                2,
                0.0625F
        );

    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_TAU = (beam, interp) -> {
        if (beam.getConfig() == null) return;

        // Эффект затухания по времени
        double age = Mth.clamp(
                1D - ((double) beam.tickCount - 2 + interp) / (double) beam.getConfig().expires,
                0, 1
        );

        float beamLength = beam.getBeamLength();
        if (beamLength <= 0) return;

        // Получаем MultiBufferSource из Minecraft
        Matrix4f poseMatrix = beam.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        MultiBufferSource.BufferSource bufferSource = beam.getBufferSource();
        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.pushPose();
        {
            // Второй слой: основной луч (оранжевый)
            float ageScale2 = (float) (age * 2);
            poseStack.scale(ageScale2, 1.0f, ageScale2);

            poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

            // Рисуем стандартный луч (оранжевый)
            renderBulletStandard(poseStack, 0xFFBF00, 0xFFFFFF, beamLength, true,0xF000F0, bufferSource);
        }
        poseStack.popPose();

            float ageScale = (float)(age / 2 + 0.5);
            poseStack.scale(ageScale, 1.0f, ageScale);

            double scale = 0.075D;
            int colorInner = ((int)(0xFF * age) << 16) | ((int)(0xBF * age) << 8) | (int) (0x00 * age);

            BeamPronter.prontBeam(
                    poseStack,
                    bufferSource,
                    new Vec3(0, 0, beam.getBeamLength()),
                    EnumWaveType.RANDOM,
                    EnumBeamType.SOLID,
                    colorInner,
                    colorInner,
                    (beam.tickCount + beam.getId()) / 2,
                    (int)(beam.getBeamLength() / 2 + 1),
                    (float)scale * 4F,
                    2,
                    0.0625F
            );

    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_TAU_CHARGE = (beam, interp) -> {
        if (beam.getConfig() == null) return;

        // Эффект затухания по времени
        double age = Mth.clamp(
                1D - ((double) beam.tickCount - 2 + interp) / (double) beam.getConfig().expires,
                0, 1
        );

        float beamLength = beam.getBeamLength();
        if (beamLength <= 0) return;

        // Получаем матрицу позы
        Matrix4f poseMatrix = beam.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        MultiBufferSource.BufferSource bufferSource = beam.getBufferSource();

        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.translate(1.4, 0,0);
        poseStack.pushPose();
        {

            float ageScale = (float)(age / 2 + 0.5);
            poseStack.scale(ageScale, 1.0f, ageScale);

            double scale = 0.075D;
            // Цвет для заряженной версии: ((int)(0x60 * age) << 16) | ((int)(0x50 * age) << 8) | (int) (0x30 * age)
            int colorInner = ((int)(0xFF * age) << 16) | ((int)(0xF0 * age) << 8) | (int) (0xA0 * age);

            BeamPronter.prontBeam(
                    poseStack,
                    bufferSource,
                    new Vec3(0, 0, beamLength),
                    EnumWaveType.RANDOM,
                    EnumBeamType.SOLID,
                    colorInner,
                    colorInner,
                    (beam.tickCount + beam.getId()) / 2,
                    (int)(beamLength / 2 + 1),
                    (float)scale * 4F,
                    2,
                    0.0625F
            );
        }
        poseStack.popPose();

        // ========== ВНЕШНИЙ ЛУЧ (стандартный, через renderBulletStandard) ==========
        poseStack.pushPose();
        {

            float ageScale2 = (float) (age * 2);
            poseStack.scale(ageScale2, 1.0f, ageScale2);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            // Рисуем стандартный луч с цветом для заряженной версии: 0xFFF0A0
            renderBulletStandard(poseStack, 0xFFF0A0, 0xFFFFFF, beamLength, true,0xF000F0, bufferSource);
        }
        poseStack.popPose();
    };

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_NI4NI_BOLT = (beam, interp) -> {
        if (beam.getConfig() == null) return;

        double age = Mth.clamp(
                1D - ((double) beam.tickCount - 2 + interp) / (double) beam.getConfig().expires,
                0, 1
        );

        float beamLength = beam.getBeamLength();
        if (beamLength <= 0) return;

        Matrix4f poseMatrix = beam.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        MultiBufferSource.BufferSource bufferSource = beam.getBufferSource();

        renderBulletStandard(poseStack, 0xAAD2E5, 0xFFFFFF, beamLength, true,0xF000F0, bufferSource);


    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_GRENADE = (bullet, interp) -> {
        // Получаем матрицу позы пули
        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        // Получаем MultiBufferSource
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        // Применяем преобразования как в оригинале
        poseStack.scale(0.25F, 0.25F, 0.25F);
        poseStack.mulPose(Axis.XP.rotationDegrees(75));

        // Настройки рендеринга
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

        // Получаем текстуру и модель гранаты
        // Предполагаем, что у вас есть доступ к ResourceManager
        ResourceLocation grenadeTexture = HBMResourceManager.grenade_tex;
        HFRWavefrontObject grenadeModel = HBMResourceManager.projectiles;

        if (grenadeTexture != null && grenadeModel != null) {
            VertexConsumer builder = bufferSource.getBuffer(RenderType.entityCutout(grenadeTexture));

            // Рендерим часть "Grenade" как в оригинале
            grenadeModel.renderPart(poseStack, builder, "Grenade", 15728880, OverlayTexture.NO_OVERLAY);

            // Завершаем рендеринг
            bufferSource.endBatch();
        }

        // Восстанавливаем состояние
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_RPZB = (bullet, interp) -> {
        // Получаем матрицу позы пули
        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        MultiBufferSource.BufferSource bufferSource = bullet.getBufferSource();

        // Сохраняем состояние для модели ракеты
        poseStack.pushPose();
        {
            // Применяем преобразования как в оригинале
            float scale = 0.125F;
            poseStack.scale(scale, scale, scale);

            // GL11.glRotated(90, 0, -1, 0) -> поворот вокруг оси Y на -90 градусов
            poseStack.mulPose(Axis.YN.rotationDegrees(180));

            // GL11.glTranslatef(0, 0, 3.5F)
            poseStack.translate(0, 0, 3.5F);

            // Настройки рендеринга
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            // Получаем текстуру и модель
            ResourceLocation texture = HBMResourceManager.panzerschreck_tex;
            HFRWavefrontObject model = HBMResourceManager.panzerschreck;

            if (texture != null && model != null) {
                VertexConsumer builder = bufferSource.getBuffer(RenderType.entityCutout(texture));
                model.renderPart(poseStack, builder, "Rocket", 15728880, OverlayTexture.NO_OVERLAY);
                bufferSource.endBatch();
            }

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }
        poseStack.popPose();

        // Рендерим след пули (трассер)
        poseStack.pushPose();
        {
            double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
            if (length > 0) {
                // renderBulletStandard с цветами 0x808080, 0xFFF2A7
                renderBulletStandard(poseStack, 0x808080, 0xFFF2A7, length * 2,
                        false, bullet.getPackedLight(), bufferSource);
            }
        }
        poseStack.popPose();
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_QD = (bullet, interp) -> {
        // Получаем матрицу позы пули
        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        // Сохраняем состояние для модели ракеты
        poseStack.pushPose();
        {
            // GL11.glRotated(90, 0, 0, 1) -> поворот вокруг оси Z на 90 градусов
            poseStack.mulPose(Axis.XP.rotationDegrees(-90));

            // Настройки рендеринга
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            // Получаем текстуру и модель
            ResourceLocation texture = HBMResourceManager.rocket_tex;
            HFRWavefrontObject model = HBMResourceManager.projectiles;

            if (texture != null && model != null) {
                VertexConsumer builder = bufferSource.getBuffer(RenderType.entityCutout(texture));
                model.renderPart(poseStack, builder, "Rocket", 15728880, OverlayTexture.NO_OVERLAY);
                bufferSource.endBatch();
            }

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }
        poseStack.popPose();

        // Рендерим след пули (трассер)
        poseStack.pushPose();
        {

            double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
            if (length > 0) {
                // renderBulletStandard с цветами 0x808080, 0xFFF2A7
                LegoClient.renderBulletStandard(poseStack, 0x808080, 0xFFF2A7, length * 2,
                        false, bullet.getPackedLight(), bufferSource);
            }
        }
        poseStack.popPose();
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_ML = (bullet, interp) -> {
        // Получаем матрицу позы пули
        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();

        // Сохраняем состояние для модели ракеты
        poseStack.pushPose();
        {
            // Применяем преобразования как в оригинале
            float scale = 0.25F;
            poseStack.scale(scale, scale, scale);

            // GL11.glRotated(-90, 0, 1, 0) -> поворот вокруг оси Y на -90 градусов
            poseStack.mulPose(Axis.YN.rotationDegrees(180));

            // GL11.glTranslatef(0, -1, -4.5F)
            poseStack.translate(0, -1, -4.5F);

            // Настройки рендеринга
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.depthMask(true);
            RenderSystem.enableDepthTest();
            RenderSystem.enableCull();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

            // Получаем текстуру и модель
            ResourceLocation texture = HBMResourceManager.missile_launcher_tex;
            HFRWavefrontObject model = HBMResourceManager.missile_launcher;

            if (texture != null && model != null) {
                VertexConsumer builder = bufferSource.getBuffer(RenderType.entityCutout(texture));
                model.renderPart(poseStack, builder, "Missile", 15728880, OverlayTexture.NO_OVERLAY);
                bufferSource.endBatch();
            }

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        }
        poseStack.popPose();

        // Рендерим след пули (трассер)
        poseStack.pushPose();
        {

            double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
            if (length > 0) {
                // renderBulletStandard с цветами 0x808080, 0xFFF2A7
                LegoClient.renderBulletStandard(poseStack, 0x808080, 0xFFF2A7, length * 2,
                        true, bullet.getPackedLight(), bufferSource);
            }
        }
        poseStack.popPose();
    };

    public static void renderStandardLaser(EntityBulletBeamBase beam, float partialTicks,
                                           int r, int g, int b) {

        Matrix4f poseMatrix = beam.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        MultiBufferSource.BufferSource bufferSource = beam.getBufferSource();

        Vec3 delta = new Vec3(0, 0, -beam.getBeamLength());

        // Вычисляем age (затухание)
        double maxAge = Objects.requireNonNull(beam.getConfig()).expires;
        double age = Mth.clamp(1.0D - ((double) beam.tickCount - 2 + partialTicks) / maxAge, 0.0D, 1.0D);

        // Масштабирование по X и Z
        float scale = (float) (age / 2.0 + 0.5);
        poseStack.scale(scale, 1.0F, scale);
        // Цвет с учётом age
        int colorInner = ((int)(r * age) << 16) | ((int)(g * age) << 8) | (int) (b * age);
        int colorOuter = ((int)(r * age * 0.7) << 16) | ((int)(g * age * 0.7) << 8) | (int) (b * age * 0.7);

        // Рендер луча вдоль Z (вперёд)
        BeamPronter.prontBeam(
                poseStack,                 // PoseStack из контекста рендера
                bufferSource,              // MultiBufferSource из контекста рендера
                delta,                     // Vec3 - направление луча
                EnumWaveType.RANDOM,       // тип волны
                EnumBeamType.SOLID,        // тип луча (твердый)
                colorOuter,                // внешний цвет (в оригинале colorInner)
                colorInner,                // внутренний цвет (тоже colorInner)
                beam.tickCount / 3,   // start - seed для случайных колебаний
                (int)(beam.getBeamLength() / 2 + 1), // segments - количество сегментов
                0F,                        // size - амплитуда колебаний (0 - нет колебаний)
                8,                         // layers - количество слоев (8)
                0.0625F                    // thickness - толщина (1/16 пикселя)
        );


    }

    private static void renderTracer(EntityBulletBaseMK4 bullet, float interp,
                                     int darkColor, int lightColor, boolean fullbright) {
        if (bullet == null) return;

        double length = bullet.prevVelocity + (bullet.velocity - bullet.prevVelocity) * interp;
        if (length <= 0) return;

        int packedLight = bullet.getPackedLight();

        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        poseStack.mulPose(Axis.XP.rotationDegrees(180));
        poseStack.mulPose(Axis.ZP.rotationDegrees(-90));

        //renderBulletStandard(poseStack, darkColor, lightColor, length, fullbright, packedLight);
        MultiBufferSource.BufferSource bufferSource = bullet.getBufferSource();
        renderBulletStandard(poseStack, darkColor, lightColor, length, fullbright, packedLight, bufferSource);
    }

    public static void renderBulletStandard(PoseStack poseStack, int dark, int light, double length,
                                            boolean fullbright, int packedLight,
                                            MultiBufferSource bufferSource) {

        double widthF = 0.03125D;
        double widthB = 0.03125D * 0.25D;

        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.disableCull();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);
        RenderSystem.enableDepthTest();

        RenderSystem.setShader(GameRenderer::getPositionColorShader);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);

        // Цвета
        float startR = ((dark >> 16) & 0xFF) / 255.0f;
        float startG = ((dark >> 8) & 0xFF) / 255.0f;
        float startB = (dark & 0xFF) / 255.0f;

        float endR = ((light >> 16) & 0xFF) / 255.0f;
        float endG = ((light >> 8) & 0xFF) / 255.0f;
        float endB = (light & 0xFF) / 255.0f;

        Matrix4f matrix = poseStack.last().pose();

        // Сторона 1 (правая) - X+
        buffer.vertex(matrix, (float)widthF, (float)widthF, 0)
                .color(startR, startG, startB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)widthF, (float)widthF, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)widthB, (float)-widthB, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)widthB, (float)-widthB, 0)
                .color(startR, startG, startB, 1.0f).endVertex();

        // Сторона 2 (левая) - X-
        buffer.vertex(matrix, (float)-widthF, (float)widthF, 0)
                .color(startR, startG, startB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)-widthF, (float)widthF, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)-widthB, (float)-widthB, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)-widthB, (float)-widthB, 0)
                .color(startR, startG, startB, 1.0f).endVertex();

        // Сторона 3 (верхняя) - Y+
        buffer.vertex(matrix, (float)-widthF, (float)widthF, 0)
                .color(startR, startG, startB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)-widthF, (float)widthF, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)widthF, (float)widthF, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)widthF, (float)widthF, 0)
                .color(startR, startG, startB, 1.0f).endVertex();

        // Сторона 4 (нижняя) - Y-
        buffer.vertex(matrix, (float)-widthB, (float)-widthB, 0)
                .color(startR, startG, startB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)-widthB, (float)-widthB, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)widthB, (float)-widthB, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)widthB, (float)-widthB, 0)
                .color(startR, startG, startB, 1.0f).endVertex();

        // Торцы
        // Передний торец (широкий, где Z = 0) - смотрит ВПЕРЕД (туда, где летит пуля)
        buffer.vertex(matrix, (float)widthF, (float)widthF, 0)
                .color(startR, startG, startB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)widthF, (float)-widthB, 0)
                .color(startR, startG, startB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)-widthF, (float)-widthB, 0)
                .color(startR, startG, startB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)-widthF, (float)widthF, 0)
                .color(startR, startG, startB, 1.0f).endVertex();

        // Задний торец (тонкий, где Z = length) - смотрит НАЗАД (исчезающий след)
        buffer.vertex(matrix, (float)widthB, (float)widthB, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)widthB, (float)-widthB, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)-widthB, (float)-widthB, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();
        buffer.vertex(matrix, (float)-widthB, (float)widthB, (float)length)
                .color(endR, endG, endB, 1.0f).endVertex();

        BufferUploader.drawWithShader(buffer.end());

        poseStack.popPose();
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
        RenderSystem.setShaderColor(1F, 1F, 1F, 1F);

    }

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_CT_MORTAR = (bullet, interp) -> {
        if (!bullet.level().isClientSide) return;

        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        // Применяем преобразования как в оригинале
        poseStack.scale(0.125F, 0.125F, 0.125F);
        poseStack.mulPose(Axis.YN.rotationDegrees(180));
        poseStack.translate(0, 0, -6F);

        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                .getBuffer(RenderType.entityCutout(HBMResourceManager.charge_thrower_mortar_tex));
        HBMResourceManager.charge_thrower.renderPart(poseStack, consumer, "Mortar", 15728880, OverlayTexture.NO_OVERLAY);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_CT_MORTAR_CHARGE = (bullet, interp) -> {
        if (!bullet.level().isClientSide) return;

        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        // Применяем преобразования как в оригинале
        poseStack.scale(0.125F, 0.125F, 0.125F);
        poseStack.mulPose(Axis.YN.rotationDegrees(180));
        poseStack.translate(0, 0, -6F);

        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                .getBuffer(RenderType.entityCutout(HBMResourceManager.charge_thrower_mortar_tex));

        // Рендерим "Mortar"
        HBMResourceManager.charge_thrower.renderPart(poseStack, consumer, "Mortar", 15728880, OverlayTexture.NO_OVERLAY);
        // Рендерим "Oomph" (дополнительная часть для заряженной версии)
        HBMResourceManager.charge_thrower.renderPart(poseStack, consumer, "Oomph", 15728880, OverlayTexture.NO_OVERLAY);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_CT_HOOK = (bullet, interp) -> {
        if (bullet.level().isClientSide) {
            ResourceLocation HOOK_TEXTURE = HBMResourceManager.charge_thrower_hook_tex;
            HFRWavefrontObject HOOK_MODEL = HBMResourceManager.charge_thrower;

            // Получаем матрицу позы пули
            Matrix4f poseMatrix = bullet.getRenderPose();
            if (poseMatrix == null) return;

            PoseStack poseStack = new PoseStack();
            poseStack.last().pose().set(poseMatrix);

            float yaw = bullet.yRotO + (bullet.getYRot() - bullet.yRotO) * interp;
            float pitch = bullet.xRotO + (bullet.getXRot() - bullet.xRotO) * interp;
            poseStack.mulPose(Axis.YP.rotationDegrees(yaw - 90.0F));
            poseStack.mulPose(Axis.ZP.rotationDegrees(pitch + 180.0F));

            poseStack.scale(0.125F, 0.125F, 0.125F);
            poseStack.translate(0, 0, -6F);

            VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                    .getBuffer(RenderType.entityCutout(HOOK_TEXTURE));
            HOOK_MODEL.renderPart(poseStack, consumer, "Hook", 15728880, OverlayTexture.NO_OVERLAY);

            // ========== УПРОЩЁННАЯ ПРОВЕРКА ДЛЯ ПРОВОДА ==========
            Player clientPlayer = Minecraft.getInstance().player;
            if (clientPlayer != null &&
                    bullet.getOwnerUUID() != null &&
                    bullet.getOwnerUUID().equals(clientPlayer.getUUID()) &&
                    bullet.config == BulletConfigRegistry.ct_hook) {
                renderWire(bullet, interp, clientPlayer);
            }
        }
    };

    private static void renderWire(EntityBulletBaseMK4 bullet, float interp, Player player) {
        // Получаем позиции с интерполяцией
        double bx = Mth.lerp(interp, bullet.xo, bullet.getX());
        double by = Mth.lerp(interp, bullet.yo, bullet.getY());
        double bz = Mth.lerp(interp, bullet.zo, bullet.getZ());

        double x = Mth.lerp(interp, player.xo, player.getX());
        double y = Mth.lerp(interp, player.yo, player.getY());
        double z = Mth.lerp(interp, player.zo, player.getZ());

        float yaw = Mth.lerp(interp, player.yRotO, player.getYRot());
        float pitch = Mth.lerp(interp, player.xRotO, player.getXRot());

        // Смещение от руки игрока
        Vec3 offset = new Vec3(0.125D, 0.25, -0.75);
        offset = offset.xRot(-pitch * (float) (Math.PI / 180.0));
        offset = offset.yRot(-yaw * (float) (Math.PI / 180.0));

        Vec3 target = new Vec3(x - offset.x, y + player.getEyeHeight() - offset.y, z - offset.z);

        // Настройки рендера
        RenderSystem.disableCull();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);

        ResourceLocation tex = WIRE_GREYSCALE_TEX; // используем существующую текстуру
        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                .getBuffer(RenderType.entitySolid(tex));

        PoseStack poseStack = new PoseStack();

        float r = 0.376F;
        float g = 0.376F;
        float b = 0.376F;
        int light = 15728880; // fullbright

        double deltaX = target.x - bx;
        double deltaY = target.y - by;
        double deltaZ = target.z - bz;
        double distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);

        int segments = 10;
        double hang = Math.min(distance / 15.0, 2.5);

        for (int j = 0; j < segments; j++) {
            double t1 = (double) j / segments;
            double t2 = (double) (j + 1) / segments;

            double sag1 = Math.sin(t1 * Math.PI) * hang;
            double sag2 = Math.sin(t2 * Math.PI) * hang;

            double x1 = bx + deltaX * t1;
            double y1 = by + deltaY * t1 - sag1;
            double z1 = bz + deltaZ * t1;

            double x2 = bx + deltaX * t2;
            double y2 = by + deltaY * t2 - sag2;
            double z2 = bz + deltaZ * t2;

            drawLineSegmentBillboard(consumer, poseStack, r, g, b, light, x1, y1, z1, x2, y2, z2);
        }

        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    private static void drawLineSegmentBillboard(VertexConsumer consumer, PoseStack stack,
                                                 float r, float g, float b, int light,
                                                 double x, double y, double z,
                                                 double a, double bx, double c) {
        double girth = 0.03125D;
        double length = Math.sqrt((a - x) * (a - x) + (bx - y) * (bx - y) + (c - z) * (c - z));
        float u1 = 0;
        float u2 = (float) length * 8;

        Matrix4f matrix = stack.last().pose();
        Matrix3f normalMatrix = stack.last().normal();

        Vec3 cameraPos = Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();

        double cx = (x + a) / 2.0;
        double cy = (y + bx) / 2.0;
        double cz = (z + c) / 2.0;

        double dx = a - x;
        double dy = bx - y;
        double dz = c - z;
        double wireLen = Math.sqrt(dx * dx + dy * dy + dz * dz);
        if (wireLen > 0) {
            dx /= wireLen;
            dy /= wireLen;
            dz /= wireLen;
        }

        double toCameraX = cameraPos.x - cx;
        double toCameraY = cameraPos.y - cy;
        double toCameraZ = cameraPos.z - cz;

        double rightX = dy * toCameraZ - dz * toCameraY;
        double rightY = dz * toCameraX - dx * toCameraZ;
        double rightZ = dx * toCameraY - dy * toCameraX;
        double rightLen = Math.sqrt(rightX * rightX + rightY * rightY + rightZ * rightZ);
        if (rightLen > 0) {
            rightX /= rightLen;
            rightY /= rightLen;
            rightZ /= rightLen;
        } else {
            rightX = -dz;
            rightY = 0;
            rightZ = dx;
            rightLen = Math.sqrt(rightX * rightX + rightZ * rightZ);
            if (rightLen > 0) {
                rightX /= rightLen;
                rightZ /= rightLen;
            }
        }

        double upX = dy * rightZ - dz * rightY;
        double upY = dz * rightX - dx * rightZ;
        double upZ = dx * rightY - dy * rightX;

        double halfLen = wireLen / 2.0;

        float v1x = (float)(cx - rightX * girth - dx * halfLen);
        float v1y = (float)(cy - rightY * girth - dy * halfLen);
        float v1z = (float)(cz - rightZ * girth - dz * halfLen);

        float v2x = (float)(cx + rightX * girth - dx * halfLen);
        float v2y = (float)(cy + rightY * girth - dy * halfLen);
        float v2z = (float)(cz + rightZ * girth - dz * halfLen);

        float v3x = (float)(cx + rightX * girth + dx * halfLen);
        float v3y = (float)(cy + rightY * girth + dy * halfLen);
        float v3z = (float)(cz + rightZ * girth + dz * halfLen);

        float v4x = (float)(cx - rightX * girth + dx * halfLen);
        float v4y = (float)(cy - rightY * girth + dy * halfLen);
        float v4z = (float)(cz - rightZ * girth + dz * halfLen);

        float nx = (float) upX;
        float ny = (float) upY;
        float nz = (float) upZ;

        // Передняя грань
        consumer.vertex(matrix, v1x, v1y, v1z)
                .color(r, g, b, 1.0f).uv(u1, 0).overlayCoords(0).uv2(light)
                .normal(normalMatrix, nx, ny, nz).endVertex();
        consumer.vertex(matrix, v2x, v2y, v2z)
                .color(r, g, b, 1.0f).uv(u1, 1).overlayCoords(0).uv2(light)
                .normal(normalMatrix, nx, ny, nz).endVertex();
        consumer.vertex(matrix, v3x, v3y, v3z)
                .color(r, g, b, 1.0f).uv(u2, 1).overlayCoords(0).uv2(light)
                .normal(normalMatrix, nx, ny, nz).endVertex();
        consumer.vertex(matrix, v4x, v4y, v4z)
                .color(r, g, b, 1.0f).uv(u2, 0).overlayCoords(0).uv2(light)
                .normal(normalMatrix, nx, ny, nz).endVertex();

        // Задняя грань
        consumer.vertex(matrix, v4x, v4y, v4z)
                .color(r, g, b, 1.0f).uv(u2, 0).overlayCoords(0).uv2(light)
                .normal(normalMatrix, -nx, -ny, -nz).endVertex();
        consumer.vertex(matrix, v3x, v3y, v3z)
                .color(r, g, b, 1.0f).uv(u2, 1).overlayCoords(0).uv2(light)
                .normal(normalMatrix, -nx, -ny, -nz).endVertex();
        consumer.vertex(matrix, v2x, v2y, v2z)
                .color(r, g, b, 1.0f).uv(u1, 1).overlayCoords(0).uv2(light)
                .normal(normalMatrix, -nx, -ny, -nz).endVertex();
        consumer.vertex(matrix, v1x, v1y, v1z)
                .color(r, g, b, 1.0f).uv(u1, 0).overlayCoords(0).uv2(light)
                .normal(normalMatrix, -nx, -ny, -nz).endVertex();
    }

    public static BiConsumer<EntityBulletBeamBase, Float> RENDER_FOLLY = (beam, interp) -> {
        if (beam.getConfig() == null) return;

        double age = Mth.clamp(1D - ((double) beam.tickCount - 2 + interp) / (double) beam.getConfig().expires, 0, 1);

        // Включаем fullbright
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        RenderSystem.disableCull();
        RenderSystem.disableDepthTest();
        RenderSystem.enableBlend();
        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        // Рендерим спрайт вспышки
        renderFlareSprite(beam, (float)((1 - age) * 7.5 + 1.5), 0.5F * (float) age, 0.75F * (float) age);

        // Получаем матрицу позы луча
        Matrix4f poseMatrix = beam.getRenderPose();
        if (poseMatrix != null) {
            PoseStack poseStack = new PoseStack();
            poseStack.last().pose().set(poseMatrix);

            // Применяем вращения
            float yaw = beam.yRotO + (beam.getYRot() - beam.yRotO) * interp;
            float pitch = beam.xRotO + (beam.getXRot() - beam.xRotO) * interp;

            poseStack.mulPose(Axis.YP.rotationDegrees(180 - yaw));
            poseStack.mulPose(Axis.XP.rotationDegrees(-pitch - 90));

            // Масштаб
            double scale = (1 - age) * 25 + 2.5;
            poseStack.scale((float) scale, 1.0F, (float) scale);

            // Цвет луча
            int color = ((int)(0x20 * age) << 16) | ((int)(0x20 * age) << 8) | (int) (0x20 * age);

            // Рендерим луч
            Vec3 delta = new Vec3(0, beam.getBeamLength(), 0);
            MultiBufferSource.BufferSource bufferSource = beam.getBufferSource();

            BeamPronter.prontBeam(
                    poseStack,
                    bufferSource,
                    delta,
                    EnumWaveType.RANDOM,
                    EnumBeamType.SOLID,
                    color,
                    color,
                    (beam.tickCount) / 3,
                    (int)(beam.getBeamLength() / 2 + 1),
                    0F,      // size (амплитуда)
                    8,       // layers
                    0.0625F  // thickness
            );
        }

        // Восстанавливаем состояние
        RenderSystem.disableBlend();
        RenderSystem.enableDepthTest();
        RenderSystem.enableCull();
        RenderSystem.defaultBlendFunc();
    };

    private static void renderFlareSprite(EntityBulletBeamBase beam, float scale, float outerAlpha, float innerAlpha) {
        Matrix4f poseMatrix = beam.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        RenderSystem.setShader(GameRenderer::getPositionColorTexLightmapShader);
        RenderSystem.setShaderTexture(0, FLARE_TEXTURE);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP);

        Matrix4f matrix = poseStack.last().pose();
        float halfScale = scale / 2.0F;
        int light = 15728880;

        float r = 1.0F;
        float g = 1.0F;
        float b = 1.0F;

        // Внешний слой
        buffer.vertex(matrix, -halfScale, -halfScale, 0).color(r, g, b, outerAlpha).uv(1, 1).uv2(light).endVertex();
        buffer.vertex(matrix, -halfScale, halfScale, 0).color(r, g, b, outerAlpha).uv(1, 0).uv2(light).endVertex();
        buffer.vertex(matrix, halfScale, halfScale, 0).color(r, g, b, outerAlpha).uv(0, 0).uv2(light).endVertex();
        buffer.vertex(matrix, halfScale, -halfScale, 0).color(r, g, b, outerAlpha).uv(0, 1).uv2(light).endVertex();

        // Внутренний слой (меньший, белый)
        halfScale *= 0.5F;
        buffer.vertex(matrix, -halfScale, -halfScale, 0.001F).color(1.0F, 1.0F, 1.0F, innerAlpha).uv(1, 1).uv2(light).endVertex();
        buffer.vertex(matrix, -halfScale, halfScale, 0.001F).color(1.0F, 1.0F, 1.0F, innerAlpha).uv(1, 0).uv2(light).endVertex();
        buffer.vertex(matrix, halfScale, halfScale, 0.001F).color(1.0F, 1.0F, 1.0F, innerAlpha).uv(0, 0).uv2(light).endVertex();
        buffer.vertex(matrix, halfScale, -halfScale, 0.001F).color(1.0F, 1.0F, 1.0F, innerAlpha).uv(0, 1).uv2(light).endVertex();

        tesselator.end();
    }

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_NUKE = (bullet, interp) -> {
        if (!bullet.level().isClientSide) return;

        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        poseStack.scale(0.125F, 0.125F, 0.125F);
        poseStack.mulPose(Axis.YN.rotationDegrees(90));  // -90 градусов вокруг Y
        poseStack.translate(0, -1, 1F);

        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                .getBuffer(RenderType.entityCutout(HBMResourceManager.fatman_mininuke_tex));
        HBMResourceManager.fatman.renderPart(poseStack, consumer, "MiniNuke", 15728880, OverlayTexture.NO_OVERLAY);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_NUKE_BALEFIRE = (bullet, interp) -> {
        if (!bullet.level().isClientSide) return;

        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        poseStack.scale(0.125F, 0.125F, 0.125F);
        poseStack.mulPose(Axis.YN.rotationDegrees(90));
        poseStack.translate(0, -1, 1F);

        // Рендерим балефайр эффект (как в ItemRenderFatMan)
        renderBalefireProjectile(poseStack, bullet, interp);
    };

    public static BiConsumer<EntityBulletBaseMK4, Float> RENDER_HIVE = (bullet, interp) -> {
        if (!bullet.level().isClientSide) return;

        Matrix4f poseMatrix = bullet.getRenderPose();
        if (poseMatrix == null) return;

        PoseStack poseStack = new PoseStack();
        poseStack.last().pose().set(poseMatrix);

        poseStack.scale(0.125F, 0.125F, 0.125F);
        poseStack.mulPose(Axis.YP.rotationDegrees(90));  // 90 градусов вокруг Y
        poseStack.translate(0, 0, 3.5F);

        VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                .getBuffer(RenderType.entityCutout(HBMResourceManager.panzerschreck_tex));
        HBMResourceManager.panzerschreck.renderPart(poseStack, consumer, "Rocket", 15728880, OverlayTexture.NO_OVERLAY);
    };

    // Вспомогательный метод для балефайр эффекта
    private static void renderBalefireProjectile(PoseStack poseStack, EntityBulletBaseMK4 bullet, float interp) {
        Minecraft mc = Minecraft.getInstance();

        VertexConsumer builder = mc.renderBuffers().bufferSource()
                .getBuffer(RenderType.entityCutout(HBMResourceManager.fatman_balefire_tex));
        HBMResourceManager.fatman.renderPart(poseStack, builder, "MiniNuke", 15728880, OverlayTexture.NO_OVERLAY);

        // Glint effect for balefire
        VertexConsumer glintBuilder = mc.renderBuffers().bufferSource()
                .getBuffer(RenderType.glint());

        float offset = mc.player.tickCount + interp;
        float scale = 2F;
        float speed = -6F;
        int layers = 3;

        poseStack.pushPose();

        for (int k = 0; k < layers; ++k) {
            poseStack.pushPose();
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Axis.ZP.rotationDegrees(30.0F - k * 60.0F));

            float movement = offset * (0.001F + k * 0.003F) * speed;
            poseStack.translate(0, movement, 0);

            HBMResourceManager.fatman.renderPart(poseStack, glintBuilder, "MiniNuke", 15728880, OverlayTexture.NO_OVERLAY);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

}