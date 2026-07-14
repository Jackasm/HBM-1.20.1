package com.hbm.render.entity.mob;

import com.hbm.entity.mob.glyphid.*;

import com.hbm.main.HBMResourceManager;
import com.hbm.render.model.ModelGlyphid;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderGlyphidUniversal extends MobRenderer<EntityGlyphid, ModelGlyphid> {

    private static final Map<Class<? extends EntityGlyphid>, ResourceLocation> TEXTURE_MAP = new HashMap<>();

    private static final ResourceLocation GLYPHID_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid.png");
    private static final ResourceLocation GLYPHID_DIGGER_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid_digger.png");
    private static final ResourceLocation GLYPHID_NUCLEAR_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid_nuclear.png");
    private static final ResourceLocation GLYPHID_SCOUT_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid_scout.png");
    private static final ResourceLocation GLYPHID_BRAWLER_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid_brawler.png");
    private static final ResourceLocation GLYPHID_BEHEMOTH_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid_behemoth.png");
    private static final ResourceLocation GLYPHID_BOMBARDIER_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid_bombardier.png");
    private static final ResourceLocation GLYPHID_BLASTER_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid_blaster.png");
    private static final ResourceLocation GLYPHID_BRENDA_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid_brenda.png");
    private static final ResourceLocation GLYPHID_INFESTED_TEX = ResLocation(RefStrings.MODID, "textures/entity/glyphid_infestation.png");

    private static final ResourceLocation MINI_NUKE_TEX = ResLocation(RefStrings.MODID, "textures/models/projectiles/mini_nuke.png");

    private final float scale;

    static {
        // Регистрируем текстуры для каждого типа
        TEXTURE_MAP.put(EntityGlyphid.class, GLYPHID_TEX);
        TEXTURE_MAP.put(EntityGlyphidDigger.class, GLYPHID_DIGGER_TEX);
        TEXTURE_MAP.put(EntityGlyphidNuclear.class, GLYPHID_NUCLEAR_TEX);
        TEXTURE_MAP.put(EntityGlyphidScout.class, GLYPHID_SCOUT_TEX);
        TEXTURE_MAP.put(EntityGlyphidBrawler.class, GLYPHID_BRAWLER_TEX);
        TEXTURE_MAP.put(EntityGlyphidBehemoth.class, GLYPHID_BEHEMOTH_TEX);
        TEXTURE_MAP.put(EntityGlyphidBombardier.class, GLYPHID_BOMBARDIER_TEX);
        TEXTURE_MAP.put(EntityGlyphidBlaster.class, GLYPHID_BLASTER_TEX);
        TEXTURE_MAP.put(EntityGlyphidBrenda.class, GLYPHID_BRENDA_TEX);
    }

    public RenderGlyphidUniversal(EntityRendererProvider.Context context, float scale) {
        super(context, new ModelGlyphid(), 1.0F);
        this.shadowRadius = 0.0F;
        this.scale = scale;
    }



    @Override
    public void render(@NotNull EntityGlyphid entity, float entityYaw, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer, int packedLight) {

        int packedOverlay = getOverlayCoords(entity, 0.0F);

        ResourceLocation texture = getTextureLocation(entity);
        this.model.render(entity, entityYaw, partialTicks, 0, entityYaw, 0,
                1.0F, poseStack, buffer, texture, packedLight, packedOverlay);

        if (entity instanceof EntityGlyphidNuclear) {
            poseStack.pushPose();
            poseStack.translate(0, 1.5, 0.1);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(entityYaw));
            poseStack.mulPose(new org.joml.Quaternionf().rotationXYZ((float) Math.toRadians(80), 0, 0));

            var consumer = buffer.getBuffer(net.minecraft.client.renderer.RenderType.entityCutout(MINI_NUKE_TEX));
            HBMResourceManager.projectiles.renderPart(poseStack, consumer, "MiniNuke", packedLight, packedOverlay);
            poseStack.popPose();
        }

        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
    }

    @Override
    protected void scale(@NotNull EntityGlyphid entity, @NotNull PoseStack poseStack, float partialTick) {
        // Базовый скейл
        float baseScale = entity.getScale() * scale;
        poseStack.scale(baseScale, baseScale, baseScale);

        // Особый скейл для ядерного глифида (вздутие при смерти)
        if (entity instanceof EntityGlyphidNuclear nuclear) {
            float swell = (nuclear.deathTicks + partialTick) / 95F;
            float flash = 1.0F + Mth.sin(swell * 100.0F) * swell * 0.01F;

            if (swell < 0.0F) swell = 0.0F;
            if (swell > 1.0F) swell = 1.0F;

            swell *= swell;
            swell *= swell;

            float scaleHorizontal = (1.0F + swell * 0.4F) * flash;
            float scaleVertical = (1.0F + swell * 0.1F) / flash;
            poseStack.scale(scaleHorizontal, scaleVertical, scaleHorizontal);
        }
    }

    @Override
    public @NotNull ResourceLocation getTextureLocation(@NotNull EntityGlyphid entity) {
        // Получаем текстуру по классу сущности
        Class<?> entityClass = entity.getClass();
        ResourceLocation texture = TEXTURE_MAP.get(entityClass);

        // Если текстура не найдена, используем дефолтную
        if (texture == null) {
            texture = GLYPHID_TEX;
        }

        // Если глифид заражённый - используем текстуру заражения
        if (entity.getEntityData().get(EntityGlyphid.DW_SUBTYPE) == EntityGlyphid.TYPE_INFECTED) {
            return GLYPHID_INFESTED_TEX;
        }

        return texture;
    }
}