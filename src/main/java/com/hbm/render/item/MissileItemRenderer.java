package com.hbm.render.item;

import com.hbm.items.ModItems;
import com.hbm.items.weapon.ItemMissile;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class MissileItemRenderer extends BlockEntityWithoutLevelRenderer {

    public MissileItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(),
                Minecraft.getInstance().getEntityModels());
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        poseStack.pushPose();
        float scale = getMissileScale(stack) * 0.08f;

        switch (context) {
            case GUI:
                poseStack.translate(0.2, 0.2, 0.5);
                poseStack.scale(scale, scale, scale);
                poseStack.mulPose(Axis.ZN.rotationDegrees(45));
                float rotation = (System.currentTimeMillis() / 25) % 360;
                poseStack.mulPose(Axis.YN.rotationDegrees(rotation));

                break;
            case GROUND:
                poseStack.translate(0.5, 0.3, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                break;
            case FIXED:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
                break;
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(scale, scale, scale);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(scale, scale, scale);
                poseStack.mulPose(Axis.YP.rotationDegrees(45));
                break;
            default:
                poseStack.translate(0.5, 0.5, 0.5);
                poseStack.scale(0.8f, 0.8f, 0.8f);
        }

        // Для GROUND контекста используем максимальное освещение
        int light = (context == ItemDisplayContext.GROUND) ? 0xF000F0 : packedLight;

        // === Определяем модель и текстуру по предмету ===
        Item item = stack.getItem();
        MissileRenderData data = getRenderData(item);

        if (data != null) {
            VertexConsumer consumer = buffer.getBuffer(RenderType.entityCutout(data.texture));
            data.model.renderAll(poseStack, consumer, light, OverlayTexture.NO_OVERLAY);
        }

        poseStack.popPose();
    }

    /**
     * Возвращает данные для рендеринга в зависимости от предмета
     */
    private MissileRenderData getRenderData(Item item) {
        // Strong (Tier 2)
        if (item == ModItems.MISSILE_STRONG.get())
            return new MissileRenderData(HBMResourceManager.missileStrong, HBMResourceManager.missileStrong_HE_tex);
        if (item == ModItems.MISSILE_INCENDIARY_STRONG.get())
            return new MissileRenderData(HBMResourceManager.missileStrong, HBMResourceManager.missileStrong_IN_tex);
        if (item == ModItems.MISSILE_CLUSTER_STRONG.get())
            return new MissileRenderData(HBMResourceManager.missileStrong, HBMResourceManager.missileStrong_CL_tex);
        if (item == ModItems.MISSILE_BUSTER_STRONG.get())
            return new MissileRenderData(HBMResourceManager.missileStrong, HBMResourceManager.missileStrong_BU_tex);
        if (item == ModItems.MISSILE_EMP_STRONG.get())
            return new MissileRenderData(HBMResourceManager.missileStrong, HBMResourceManager.missileStrong_EMP_tex);

        // Generic (Tier 1 - V2)
        if (item == ModItems.MISSILE_GENERIC.get())
            return new MissileRenderData(HBMResourceManager.missileV2, HBMResourceManager.missileV2_HE_tex);
        if (item == ModItems.MISSILE_INCENDIARY.get())
            return new MissileRenderData(HBMResourceManager.missileV2, HBMResourceManager.missileV2_IN_tex);
        if (item == ModItems.MISSILE_CLUSTER.get())
            return new MissileRenderData(HBMResourceManager.missileV2, HBMResourceManager.missileV2_CL_tex);
        if (item == ModItems.MISSILE_BUSTER.get())
            return new MissileRenderData(HBMResourceManager.missileV2, HBMResourceManager.missileV2_BU_tex);
        if (item == ModItems.MISSILE_DECOY.get())
            return new MissileRenderData(HBMResourceManager.missileV2, HBMResourceManager.missileV2_decoy_tex);
        if (item == ModItems.MISSILE_STEALTH.get())
            return new MissileRenderData(HBMResourceManager.missileStealth, HBMResourceManager.missileStealth_tex);
        if (item == ModItems.MISSILE_ANTI_BALLISTIC.get())
            return new MissileRenderData(HBMResourceManager.missileABM, HBMResourceManager.missileAA_tex);

        // Huge (Tier 3)
        if (item == ModItems.MISSILE_BURST.get())
            return new MissileRenderData(HBMResourceManager.missileHuge, HBMResourceManager.missileHuge_HE_tex);
        if (item == ModItems.MISSILE_INFERNO.get())
            return new MissileRenderData(HBMResourceManager.missileHuge, HBMResourceManager.missileHuge_IN_tex);
        if (item == ModItems.MISSILE_RAIN.get())
            return new MissileRenderData(HBMResourceManager.missileHuge, HBMResourceManager.missileHuge_CL_tex);
        if (item == ModItems.MISSILE_DRILL.get())
            return new MissileRenderData(HBMResourceManager.missileHuge, HBMResourceManager.missileHuge_BU_tex);

        // Nuclear (Tier 4)
        if (item == ModItems.MISSILE_NUCLEAR.get())
            return new MissileRenderData(HBMResourceManager.missileNuclear, HBMResourceManager.missileNuclear_tex);
        if (item == ModItems.MISSILE_NUCLEAR_CLUSTER.get())
            return new MissileRenderData(HBMResourceManager.missileNuclear, HBMResourceManager.missileThermo_tex);
        if (item == ModItems.MISSILE_VOLCANO.get())
            return new MissileRenderData(HBMResourceManager.missileNuclear, HBMResourceManager.missileVolcano_tex);
        if (item == ModItems.MISSILE_DOOMSDAY.get())
            return new MissileRenderData(HBMResourceManager.missileNuclear, HBMResourceManager.missileDoomsday_tex);
        if (item == ModItems.MISSILE_DOOMSDAY_RUSTED.get())
            return new MissileRenderData(HBMResourceManager.missileNuclear, HBMResourceManager.missileDoomsdayRusted_tex);

        // Micro (Tier 0)
        if (item == ModItems.MISSILE_TAINT.get())
            return new MissileRenderData(HBMResourceManager.missileMicro, HBMResourceManager.missileMicroTaint_tex);
        if (item == ModItems.MISSILE_MICRO.get())
            return new MissileRenderData(HBMResourceManager.missileMicro, HBMResourceManager.missileMicro_tex);
        if (item == ModItems.MISSILE_BHOLE.get())
            return new MissileRenderData(HBMResourceManager.missileMicro, HBMResourceManager.missileMicroBHole_tex);
        if (item == ModItems.MISSILE_SCHRABIDIUM.get())
            return new MissileRenderData(HBMResourceManager.missileMicro, HBMResourceManager.missileMicroSchrab_tex);
        if (item == ModItems.MISSILE_EMP.get())
            return new MissileRenderData(HBMResourceManager.missileMicro, HBMResourceManager.missileMicroEMP_tex);
        if (item == ModItems.MISSILE_TEST.get())
            return new MissileRenderData(HBMResourceManager.missileMicro, HBMResourceManager.missileMicroTest_tex);

        // Shuttle
        if (item == ModItems.MISSILE_SHUTTLE.get())
            return new MissileRenderData(HBMResourceManager.missileShuttle, HBMResourceManager.missileShuttle_tex);

        return null;
    }

    /**
     * Вспомогательный класс для хранения данных рендеринга
     */
    private static class MissileRenderData {
        final HFRWavefrontObject model;
        final ResourceLocation texture;

        MissileRenderData(HFRWavefrontObject model, ResourceLocation texture) {
            this.model = model;
            this.texture = texture;
        }
    }

    private float getMissileScale(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 1.0f;

        if (stack.getItem() instanceof ItemMissile missile) {
            return switch (missile.formFactor) {
                case ABM -> 1.45f;
                case MICRO -> 2.5f;
                case V2 -> 1.75f;
                case STRONG -> 1.375f;
                case HUGE -> 0.925f;
                case ATLAS -> 0.875f;
                default -> 1.0f;
            };
        }
        return 1.0f;
    }
}