package com.hbm.render.entity.effect;

import com.hbm.entity.effect.EntityFalloutRain;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

import static com.hbm.util.ResLocation.ResLocation;

public class RenderFallout extends EntityRenderer<EntityFalloutRain> {

    private static final ResourceLocation FALLOUT_TEXTURE = ResLocation(RefStrings.MODID, "textures/entity/fallout.png");
    private final Random random = new Random();
    private float[] rainXCoords;
    private float[] rainYCoords;
    private long lastTime = System.nanoTime();

    public RenderFallout(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(@NotNull EntityFalloutRain entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {
        Minecraft mc = Minecraft.getInstance();
        Camera camera = mc.gameRenderer.getMainCamera();
        LivingEntity cameraEntity = mc.player;

        if (cameraEntity == null) return;

        Vec3 cameraPos = camera.getPosition();
        Vec3 entityPos = entity.position();
        Vec3 vector = new Vec3(cameraPos.x - entityPos.x, cameraPos.y - entityPos.y, cameraPos.z - entityPos.z);

        if (vector.length() <= entity.getScale()) {
            long time = System.nanoTime();
            float t = (time - lastTime) / 50_000_000.0f;
            if (t <= 1.0F) {
                renderRainSnow(partialTicks, poseStack, buffer);
            } else {
                renderRainSnow(1.0F, poseStack, buffer);
            }
            lastTime = time;
        }
    }

    private void renderRainSnow(float interp, PoseStack poseStack, MultiBufferSource buffer) {
        Minecraft mc = Minecraft.getInstance();
        ClientLevel level = mc.level;
        Camera camera = mc.gameRenderer.getMainCamera();
        LivingEntity cameraEntity = mc.player;

        if (level == null || cameraEntity == null) return;

        // Включаем прозрачность и отключаем запись в depth buffer для прозрачных частиц
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.disableCull();

        int timer = cameraEntity.tickCount;
        float intensity = 1.0F;

        if (intensity > 0.0F) {
            if (rainXCoords == null) {
                rainXCoords = new float[1024];
                rainYCoords = new float[1024];

                for (int i = 0; i < 32; ++i) {
                    for (int j = 0; j < 32; ++j) {
                        float f2 = j - 16;
                        float f3 = i - 16;
                        float f4 = Mth.sqrt(f2 * f2 + f3 * f3);
                        rainXCoords[i << 5 | j] = -f3 / f4;
                        rainYCoords[i << 5 | j] = f2 / f4;
                    }
                }
            }

            Vec3 cameraPos = camera.getPosition();
            int playerX = Mth.floor(cameraPos.x);
            int playerY = Mth.floor(cameraPos.y);
            int playerZ = Mth.floor(cameraPos.z);
            double dY = cameraEntity.yo + (cameraEntity.getY() - cameraEntity.yo) * interp;
            int playerHeight = Mth.floor(dY);
            int renderLayerCount = mc.options.graphicsMode().get().ordinal() == 2 ? 10 : 5;

            RenderType renderType = RenderType.entityTranslucent(FALLOUT_TEXTURE);
            VertexConsumer builder = buffer.getBuffer(renderType);
            var normal = poseStack.last().normal();

            for (int layerZ = playerZ - renderLayerCount; layerZ <= playerZ + renderLayerCount; ++layerZ) {
                for (int layerX = playerX - renderLayerCount; layerX <= playerX + renderLayerCount; ++layerX) {
                    int rainCoord = (layerZ - playerZ + 16) * 32 + layerX - playerX + 16;
                    float rainCoordX = rainXCoords[rainCoord] * 0.5F;
                    float rainCoordY = rainYCoords[rainCoord] * 0.5F;

                    int rainHeight = level.getHeight(net.minecraft.world.level.levelgen.Heightmap.Types.MOTION_BLOCKING, layerX, layerZ);
                    int minHeight = playerY - renderLayerCount;
                    int maxHeight = playerY + renderLayerCount;

                    if (minHeight < rainHeight) minHeight = rainHeight;
                    if (maxHeight < rainHeight) maxHeight = rainHeight;

                    int layerY = Math.max(rainHeight, playerHeight);

                    if (minHeight != maxHeight) {
                        random.setSeed(layerX * layerX * 3121L + layerX * 45238971L ^ layerZ * layerZ * 418711L + layerZ * 13761L);

                        float fallSpeed = 1.0F;
                        float swayLoop = ((timer & 511) + interp) / 512.0F;
                        float fallVariation = 0.4F + random.nextFloat() * 0.2F;
                        float swayVariation = random.nextFloat();
                        double distX = layerX + 0.5F - cameraPos.x;
                        double distZ = layerZ + 0.5F - cameraPos.z;
                        float intensityMod = Mth.sqrt((float) (distX * distX + distZ * distZ)) / renderLayerCount;
                        float colorMod = 1.0F;

                        int blockLight = level.getBrightness(net.minecraft.world.level.LightLayer.BLOCK,
                                new net.minecraft.core.BlockPos(layerX, layerY, layerZ));
                        int skyLight = level.getBrightness(net.minecraft.world.level.LightLayer.SKY,
                                new net.minecraft.core.BlockPos(layerX, layerY, layerZ));
                        int light = (skyLight << 20) | (blockLight << 4);
                        float alpha = ((1.0F - intensityMod * intensityMod) * 0.3F + 0.5F) * intensity;

                        // Временно для теста - яркий белый цвет с высокой альфой
                        alpha = 0.8f;
                        float r = 1.0f;
                        float g = 1.0f;
                        float b = 1.0f;

                        // Координаты относительно камеры!
                        float x1 = layerX - rainCoordX + 0.5F - (float) cameraPos.x;
                        float z1 = layerZ - rainCoordY + 0.5F - (float) cameraPos.z;
                        float x2 = layerX + rainCoordX + 0.5F - (float) cameraPos.x;
                        float z2 = layerZ + rainCoordY + 0.5F - (float) cameraPos.z;
                        float yMin = minHeight - (float) cameraPos.y;
                        float yMax = maxHeight - (float) cameraPos.y;

                        float u1 = 0.0F * fallSpeed + fallVariation;
                        float u2 = 1.0F * fallSpeed + fallVariation;
                        float v1 = minHeight * fallSpeed / 4.0F + swayLoop * fallSpeed + swayVariation;
                        float v2 = maxHeight * fallSpeed / 4.0F + swayLoop * fallSpeed + swayVariation;

                        var pose = poseStack.last().pose();

                        builder.vertex(pose, x1, yMin, z1)
                                .color(r, g, b, alpha)
                                .uv(u1, v1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(light)
                                .normal(normal, 0, 1, 0)
                                .endVertex();

                        builder.vertex(pose, x2, yMin, z2)
                                .color(r, g, b, alpha)
                                .uv(u2, v1)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(light)
                                .normal(normal, 0, 1, 0)
                                .endVertex();

                        builder.vertex(pose, x2, yMax, z2)
                                .color(r, g, b, alpha)
                                .uv(u2, v2)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(light)
                                .normal(normal, 0, 1, 0)
                                .endVertex();

                        builder.vertex(pose, x1, yMax, z1)
                                .color(r, g, b, alpha)
                                .uv(u1, v2)
                                .overlayCoords(OverlayTexture.NO_OVERLAY)
                                .uv2(light)
                                .normal(normal, 0, 1, 0)
                                .endVertex();
                    }
                }
            }
        }

        // Восстанавливаем состояние
        RenderSystem.depthMask(true);
        RenderSystem.enableCull();
        RenderSystem.disableBlend();
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityFalloutRain entity) {
        return FALLOUT_TEXTURE;
    }
}