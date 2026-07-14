package com.hbm.render.item;

import com.hbm.inventory.recipes.common.ComparableStack;
import com.hbm.items.ModItems;
import com.hbm.main.HBMResourceManager;
import com.hbm.render.loader.HFRWavefrontObject;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;


import java.util.HashMap;
import java.util.function.Consumer;

public class ItemRenderMissileGeneric {

    public static final HashMap<ComparableStack, Consumer<RenderContext>> renderers = new HashMap<>();

    public enum RenderMissileType {
        TYPE_TIER0,
        TYPE_TIER1,
        TYPE_TIER2,
        TYPE_TIER3,
        TYPE_STEALTH,
        TYPE_ABM,
        TYPE_NUCLEAR,
        TYPE_ROBIN
    }

    public record RenderContext(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {}

    private final RenderMissileType type;
    private final ItemRenderer itemRenderer;

    public ItemRenderMissileGeneric(RenderMissileType type) {
        this.type = type;
        this.itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack,
                             MultiBufferSource buffer, int packedLight, int packedOverlay) {

        Consumer<RenderContext> renderer = renderers.get(new ComparableStack(stack).makeSingular());
        if (renderer == null) return;

        poseStack.pushPose();

        double guiScale = 1;
        double guiOffset = 0;

        switch (this.type) {
            case TYPE_TIER0 -> { guiScale = 3.75D; guiOffset = 10.75D; }
            case TYPE_TIER1 -> { guiScale = 2.5D; guiOffset = 8.5D; }
            case TYPE_TIER2 -> { guiScale = 2D; guiOffset = 6.5D; }
            case TYPE_TIER3 -> { guiScale = 1.25D; guiOffset = 1D; }
            case TYPE_STEALTH -> { guiScale = 1.75D; guiOffset = 4.75D; }
            case TYPE_ABM -> { guiScale = 2.25D; guiOffset = 7D; }
            case TYPE_NUCLEAR -> { guiScale = 1.375D; guiOffset = 1.5D; }
            case TYPE_ROBIN -> { guiScale = 1.25D; guiOffset = 2D; }
        }

        switch (context) {
            case THIRD_PERSON_LEFT_HAND:
            case THIRD_PERSON_RIGHT_HAND:
                double s = 0.15;
                poseStack.translate(0.5, -0.25, 0);
                poseStack.scale((float) s, (float) s, (float) s);
                break;
            case FIRST_PERSON_LEFT_HAND:
            case FIRST_PERSON_RIGHT_HAND:
                double heldScale = 0.1;
                poseStack.translate(0.5, 0.25, 0);
                poseStack.scale((float) heldScale, (float) heldScale, (float) heldScale);
                break;
            case GROUND:
                double s2 = 0.15;
                poseStack.scale((float) s2, (float) s2, (float) s2);
                break;
            case GUI:
                poseStack.scale((float) guiScale, (float) guiScale, (float) guiScale);
                poseStack.mulPose(com.mojang.math.Axis.ZP.rotationDegrees(135));
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees((System.currentTimeMillis() / 15) % 360));
                poseStack.translate(0, -16 + guiOffset, 0);
                break;
            default:
                break;
        }

        RenderContext renderContext = new RenderContext(poseStack, buffer, packedLight, packedOverlay);
        renderer.accept(renderContext);

        poseStack.popPose();
    }

    public static Consumer<RenderContext> generateStandard(ResourceLocation texture, HFRWavefrontObject model) {
        return generateWithScale(texture, model, 1F);
    }

    public static Consumer<RenderContext> generateLarge(ResourceLocation texture, HFRWavefrontObject model) {
        return generateWithScale(texture, model, 1.5F);
    }

    public static Consumer<RenderContext> generateDouble(ResourceLocation texture, HFRWavefrontObject model) {
        return generateWithScale(texture, model, 2F);
    }

    public static Consumer<RenderContext> generateWithScale(ResourceLocation texture, HFRWavefrontObject model, float scale) {
        return ctx -> {
            ctx.poseStack().scale(scale, scale, scale);
            var consumer = ctx.buffer().getBuffer(RenderType.entityCutout(texture));
            model.renderAll(ctx.poseStack(), consumer, ctx.packedLight(), ctx.packedOverlay());
        };
    }

    public static void init() {
        // Tier 0
        renderers.put(new ComparableStack(ModItems.MISSILE_TEST.get()),
                generateStandard(HBMResourceManager.missileMicroTest_tex, HBMResourceManager.missileMicro));
        renderers.put(new ComparableStack(ModItems.MISSILE_TAINT.get()),
                generateStandard(HBMResourceManager.missileMicroTaint_tex, HBMResourceManager.missileMicro));
        renderers.put(new ComparableStack(ModItems.MISSILE_MICRO.get()),
                generateStandard(HBMResourceManager.missileMicro_tex, HBMResourceManager.missileMicro));
        renderers.put(new ComparableStack(ModItems.MISSILE_BHOLE.get()),
                generateStandard(HBMResourceManager.missileMicroBHole_tex, HBMResourceManager.missileMicro));
        renderers.put(new ComparableStack(ModItems.MISSILE_SCHRABIDIUM.get()),
                generateStandard(HBMResourceManager.missileMicroSchrab_tex, HBMResourceManager.missileMicro));
        renderers.put(new ComparableStack(ModItems.MISSILE_EMP.get()),
                generateStandard(HBMResourceManager.missileMicroEMP_tex, HBMResourceManager.missileMicro));

        // Stealth
        renderers.put(new ComparableStack(ModItems.MISSILE_STEALTH.get()), ctx -> {
            var consumer = ctx.buffer().getBuffer(RenderType.entityCutout(HBMResourceManager.missileStealth_tex));
            HBMResourceManager.missileStealth.renderAll(ctx.poseStack(), consumer, ctx.packedLight(), ctx.packedOverlay());
        });

        // Tier 1
        renderers.put(new ComparableStack(ModItems.MISSILE_GENERIC.get()),
                generateStandard(HBMResourceManager.missileV2_HE_tex, HBMResourceManager.missileV2));
        renderers.put(new ComparableStack(ModItems.MISSILE_INCENDIARY.get()),
                generateStandard(HBMResourceManager.missileV2_IN_tex, HBMResourceManager.missileV2));
        renderers.put(new ComparableStack(ModItems.MISSILE_CLUSTER.get()),
                generateStandard(HBMResourceManager.missileV2_CL_tex, HBMResourceManager.missileV2));
        renderers.put(new ComparableStack(ModItems.MISSILE_BUSTER.get()),
                generateStandard(HBMResourceManager.missileV2_BU_tex, HBMResourceManager.missileV2));
        renderers.put(new ComparableStack(ModItems.MISSILE_DECOY.get()),
                generateStandard(HBMResourceManager.missileV2_decoy_tex, HBMResourceManager.missileV2));
        renderers.put(new ComparableStack(ModItems.MISSILE_ANTI_BALLISTIC.get()),
                generateStandard(HBMResourceManager.missileAA_tex, HBMResourceManager.missileABM));

        // Tier 2
        renderers.put(new ComparableStack(ModItems.MISSILE_STRONG.get()),
                generateLarge(HBMResourceManager.missileStrong_HE_tex, HBMResourceManager.missileStrong));
        renderers.put(new ComparableStack(ModItems.MISSILE_INCENDIARY_STRONG.get()),
                generateLarge(HBMResourceManager.missileStrong_IN_tex, HBMResourceManager.missileStrong));
        renderers.put(new ComparableStack(ModItems.MISSILE_CLUSTER_STRONG.get()),
                generateLarge(HBMResourceManager.missileStrong_CL_tex, HBMResourceManager.missileStrong));
        renderers.put(new ComparableStack(ModItems.MISSILE_BUSTER_STRONG.get()),
                generateLarge(HBMResourceManager.missileStrong_BU_tex, HBMResourceManager.missileStrong));
        renderers.put(new ComparableStack(ModItems.MISSILE_EMP_STRONG.get()),
                generateLarge(HBMResourceManager.missileStrong_EMP_tex, HBMResourceManager.missileStrong));

        // Tier 3
        renderers.put(new ComparableStack(ModItems.MISSILE_BURST.get()),
                generateStandard(HBMResourceManager.missileHuge_HE_tex, HBMResourceManager.missileHuge));
        renderers.put(new ComparableStack(ModItems.MISSILE_INFERNO.get()),
                generateStandard(HBMResourceManager.missileHuge_IN_tex, HBMResourceManager.missileHuge));
        renderers.put(new ComparableStack(ModItems.MISSILE_RAIN.get()),
                generateStandard(HBMResourceManager.missileHuge_CL_tex, HBMResourceManager.missileHuge));
        renderers.put(new ComparableStack(ModItems.MISSILE_DRILL.get()),
                generateStandard(HBMResourceManager.missileHuge_BU_tex, HBMResourceManager.missileHuge));

        // Tier 4
        renderers.put(new ComparableStack(ModItems.MISSILE_NUCLEAR.get()),
                generateStandard(HBMResourceManager.missileNuclear_tex, HBMResourceManager.missileNuclear));
        renderers.put(new ComparableStack(ModItems.MISSILE_NUCLEAR_CLUSTER.get()),
                generateStandard(HBMResourceManager.missileThermo_tex, HBMResourceManager.missileNuclear));
        renderers.put(new ComparableStack(ModItems.MISSILE_VOLCANO.get()),
                generateStandard(HBMResourceManager.missileVolcano_tex, HBMResourceManager.missileNuclear));
        renderers.put(new ComparableStack(ModItems.MISSILE_DOOMSDAY.get()),
                generateStandard(HBMResourceManager.missileDoomsday_tex, HBMResourceManager.missileNuclear));
        renderers.put(new ComparableStack(ModItems.MISSILE_DOOMSDAY_RUSTED.get()),
                generateStandard(HBMResourceManager.missileDoomsdayRusted_tex, HBMResourceManager.missileNuclear));

        // Shuttle
        renderers.put(new ComparableStack(ModItems.MISSILE_SHUTTLE.get()),
                generateStandard(HBMResourceManager.missileShuttle_tex, HBMResourceManager.missileShuttle));
    }
}