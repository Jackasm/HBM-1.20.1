package com.hbm.render.tileentity;

import com.hbm.blocks.generic.BlockBobble;
import com.hbm.blocks.generic.BlockBobble.BobbleType;
import com.hbm.items.ModItems;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.deco.TileEntityBobble;
import com.hbm.util.RefStrings;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;
import org.joml.Quaternionf;

import static com.hbm.util.ResLocation.ResLocation;

@OnlyIn(Dist.CLIENT)
public class RenderBobble implements BlockEntityRenderer<TileEntityBobble> {

    public RenderBobble(BlockEntityRendererProvider.Context ignored) {}

    @Override
    public void render(@NotNull TileEntityBobble tile, float partialTicks,
                       @NotNull PoseStack poseStack, @NotNull MultiBufferSource buffer,
                       int packedLight, int packedOverlay) {

        long time = System.currentTimeMillis();

        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);

        BlockState state = tile.getBlockState();
        Direction facing = state.getValue(BlockBobble.FACING);
        float rot = switch (facing) {
            case EAST -> 0F;
            case SOUTH -> 270F;
            case WEST -> 180F;
            default -> 90F;
        };
        poseStack.mulPose(Axis.YP.rotationDegrees(rot));

        poseStack.mulPose(Axis.YP.rotationDegrees(180));

        poseStack.scale(0.25F, 0.25F, 0.25F);

        renderBobble(tile.type, time, poseStack, buffer, packedLight, packedOverlay, true);

        poseStack.popPose();
    }

    // ---------- главный диспатчер (static, переиспользуется item-рендером) ----------

    public static void renderBobble(BobbleType type, long time, PoseStack poseStack, MultiBufferSource buffer,
                                    int packedLight, int packedOverlay, boolean renderLabel) {

        // Socket
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "Socket",
                HBMResourceManager.bobble_socket_tex, packedLight, packedOverlay);

        ResourceLocation bodyTex = getBobbleTexture(type);

        switch (type) {
            case PU238 -> renderPellet(poseStack, buffer, packedLight, packedOverlay);
            case UFFR -> renderFumo(poseStack, buffer, packedLight, packedOverlay, time);
            case DRILLGON -> renderDrillgon(poseStack, buffer, packedLight, packedOverlay);
            default -> renderGuy(type, bodyTex, poseStack, buffer, packedLight, packedOverlay, time);
        }

        renderPost(type, poseStack, buffer, packedLight, packedOverlay);

        if (renderLabel) {
            renderSocketLabel(type, poseStack, buffer, packedLight);
        }
    }

    public static ResourceLocation getBobbleTexture(BobbleType type) {
        return switch (type) {
            case STRENGTH, PERCEPTION, ENDURANCE, CHARISMA, INTELLIGENCE, AGILITY, LUCK ->
                    HBMResourceManager.bobble_vaultboy_tex;
            case BOB -> HBMResourceManager.bobble_hbm_tex;
            case PU238 -> HBMResourceManager.bobble_pu238_tex;
            case FRIZZLE -> HBMResourceManager.bobble_frizzle_tex;
            case VT -> HBMResourceManager.bobble_vt_tex;
            case DOC -> HBMResourceManager.bobble_doc_tex;
            case BLUEHAT -> HBMResourceManager.bobble_blue_tex;
            case PHEO -> HBMResourceManager.bobble_pheo_tex;
            case CIRNO -> HBMResourceManager.bobble_cirno_tex;
            case ADAM29 -> HBMResourceManager.bobble_adam_tex;
            case UFFR -> HBMResourceManager.bobble_uffr_tex;
            case VAER -> HBMResourceManager.bobble_vaer_tex;
            case NOS -> HBMResourceManager.bobble_nos_tex;
            case DRILLGON -> HBMResourceManager.bobble_drillgon_tex;
            case MICROWAVE -> HBMResourceManager.bobble_microwave_tex;
            case PEEP -> HBMResourceManager.bobble_peep_tex;
            case MELLOW -> HBMResourceManager.bobble_mellow_tex;
            case ABEL -> HBMResourceManager.bobble_abel_tex;
            default -> HBMResourceManager.universal_tex;
        };
    }

    // ---------- стандартная фигурка ----------

    public static void renderGuy(BobbleType type, ResourceLocation bodyTex, PoseStack poseStack,
                                 MultiBufferSource buffer, int packedLight, int packedOverlay, long time) {

        double[] rotLeftArm = {0, 0, 0};
        double[] rotRightArm = {0, 0, 0};
        double[] rotLeftLeg = {0, 0, 0};
        double[] rotRightLeg = {0, 0, 0};
        double rotBody = 0;
        double[] rotHead = {0, 0, 0};

        switch (type) {
            case STRENGTH -> { rotLeftArm = new double[]{0, 25, 135}; rotRightArm = new double[]{0, -45, 135}; rotLeftLeg = new double[]{0, 0, -5}; rotRightLeg = new double[]{0, 0, 5}; rotHead = new double[]{15, 0, 0}; }
            case PERCEPTION -> { rotLeftArm = new double[]{0, -15, 135}; rotRightArm = new double[]{-5, 0, 0}; }
            case ENDURANCE -> { rotBody = 45; rotLeftArm = new double[]{0, -25, 30}; rotRightArm = new double[]{0, 45, 30}; rotHead = new double[]{0, -45, 0}; }
            case CHARISMA -> { rotBody = 45; rotRightArm = new double[]{0, -45, 90}; rotLeftLeg = new double[]{0, 0, -5}; rotRightLeg = new double[]{0, 0, 5}; rotHead = new double[]{-5, -45, 0}; }
            case INTELLIGENCE -> { rotHead = new double[]{0, 30, 0}; rotLeftArm = new double[]{5, 0, 0}; rotRightArm = new double[]{15, 0, 170}; }
            case AGILITY -> { rotLeftArm = new double[]{0, 0, 60}; rotRightArm = new double[]{0, 0, -45}; rotLeftLeg = new double[]{0, 0, -15}; rotRightLeg = new double[]{0, 0, 45}; }
            case LUCK -> { rotLeftArm = new double[]{135, 45, 0}; rotRightArm = new double[]{-135, -45, 0}; rotRightLeg = new double[]{-5, 0, 0}; }
            case VT -> { rotLeftArm = new double[]{0, -45, 60}; rotRightArm = new double[]{0, 0, 45}; rotLeftLeg = new double[]{2, 0, 0}; rotRightLeg = new double[]{-2, 0, 0}; }
            case BLUEHAT -> rotLeftArm = new double[]{0, 90, 60};
            case FRIZZLE -> { rotLeftArm = new double[]{0, 15, 45}; rotRightArm = new double[]{0, 0, 80}; rotLeftLeg = new double[]{0, 0, 2}; rotRightLeg = new double[]{0, 0, -2}; }
            case ADAM29 -> rotRightArm = new double[]{0, 0, 60};
            case PHEO -> { rotLeftArm = new double[]{0, 0, 80}; rotRightArm = new double[]{0, 0, 45}; }
            case VAER -> { rotLeftArm = new double[]{0, -5, 45}; rotRightArm = new double[]{0, 15, 45}; }
            case PEEP -> { rotLeftArm = new double[]{0, 0, 1}; rotRightArm = new double[]{0, 0, 1}; }
            case MELLOW -> { rotLeftArm = new double[]{0, 10, 0}; rotRightArm = new double[]{0, -10, 0}; rotLeftLeg = new double[]{3, 5, 2}; rotRightLeg = new double[]{-3, -5, 0}; }
            case ABEL -> { rotLeftArm = new double[]{0, 80, 90}; rotRightArm = new double[]{0, -80, 90}; }
        }

        poseStack.pushPose();
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotBody));

        if (type == BobbleType.PEEP) {
            HBMResourceManager.bobble.renderPart(poseStack, buffer, "PeepTail", bodyTex, packedLight, packedOverlay);
        }

        RenderSystem.disableCull();

        String suffix = type.skinLayers ? "" : "17";

        // Левая нога
        poseStack.pushPose();
        poseStack.translate(0, 1, -0.125);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotLeftLeg[0]));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotLeftLeg[1]));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotLeftLeg[2]));
        poseStack.translate(0, -1, 0.125);
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "LL" + suffix, bodyTex, packedLight, packedOverlay);
        poseStack.popPose();

        // Правая нога
        poseStack.pushPose();
        poseStack.translate(0, 1, 0.125);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotRightLeg[0]));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotRightLeg[1]));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotRightLeg[2]));
        poseStack.translate(0, -1, -0.125);
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "RL" + suffix, bodyTex, packedLight, packedOverlay);
        poseStack.popPose();

        // Левая рука
        poseStack.pushPose();
        poseStack.translate(0, 1.625, -0.25);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotLeftArm[0]));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotLeftArm[1]));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotLeftArm[2]));
        poseStack.translate(0, -1.625, 0.25);
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "LA" + suffix, bodyTex, packedLight, packedOverlay);
        poseStack.popPose();

        // Правая рука
        poseStack.pushPose();
        poseStack.translate(0, 1.625, 0.25);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotRightArm[0]));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotRightArm[1]));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotRightArm[2]));
        poseStack.translate(0, -1.625, -0.25);
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "RA" + suffix, bodyTex, packedLight, packedOverlay);
        poseStack.popPose();

        // Тело
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "Body" + suffix, bodyTex, packedLight, packedOverlay);

        // Голова (с покачиванием)
        double speed = 0.005;
        double amplitude = 1;

        poseStack.pushPose();
        poseStack.translate(0, 1.75, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) (Math.sin(time * speed) * amplitude)));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (Math.sin(time * speed + Math.PI * 0.5) * amplitude)));
        poseStack.mulPose(Axis.XP.rotationDegrees((float) rotHead[0]));
        poseStack.mulPose(Axis.YP.rotationDegrees((float) rotHead[1]));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) rotHead[2]));
        poseStack.translate(0, -1.75, 0);
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "Head" + suffix, bodyTex, packedLight, packedOverlay);

        if (type == BobbleType.VT) {
            HBMResourceManager.bobble.renderPart(poseStack, buffer, "Horn", bodyTex, packedLight, packedOverlay);
        }
        if (type == BobbleType.PEEP) {
            HBMResourceManager.bobble.renderPart(poseStack, buffer, "PeepHat", bodyTex, packedLight, packedOverlay);
        }

        if (type == BobbleType.NOS) {
            poseStack.pushPose();
            poseStack.translate(0, 1.75, 0);
            poseStack.mulPose(Axis.XP.rotationDegrees(180));
            poseStack.scale(0.095F, 0.095F, 0.095F);
            ResourceLocation texture = ResLocation(RefStrings.MODID, "textures/models/armor/armor_hat.png");
            HBMResourceManager.armor_hat.renderPart(poseStack, buffer, "Head",
                    texture, packedLight, packedOverlay);
            poseStack.popPose();
        }

        poseStack.popPose();

        RenderSystem.enableCull();
        poseStack.popPose();
    }

    // ---------- Pu-238 (гранула) ----------

    public static void renderPellet(PoseStack poseStack, MultiBufferSource buffer,
                                    int packedLight, int packedOverlay) {
        RenderSystem.enableCull();

        HBMResourceManager.bobble.renderPart(poseStack, buffer, "Pellet",
                HBMResourceManager.bobble_pu238_tex, LightTexture.FULL_BRIGHT, packedOverlay);

        VertexConsumer glow = buffer.getBuffer(RenderType.eyes(HBMResourceManager.bobble_pellet_glow_tex));
        HBMResourceManager.bobble.renderPart(poseStack, glow, "PelletShine", packedLight, packedOverlay);
    }

    // ---------- UFFR (фумо) ----------

    public static void renderFumo(PoseStack poseStack, MultiBufferSource buffer,
                                  int packedLight, int packedOverlay, long time) {
        RenderSystem.enableCull();
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "Fumo",
                HBMResourceManager.bobble_uffr_tex, packedLight, packedOverlay);

        double speed = 0.005;
        double amplitude = 1;

        poseStack.pushPose();
        poseStack.translate(0, 0.75, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees((float) (Math.sin(time * speed) * amplitude)));
        poseStack.mulPose(Axis.ZP.rotationDegrees((float) (Math.sin(time * speed + Math.PI * 0.5) * amplitude)));
        poseStack.translate(0, -0.75, 0);
        RenderSystem.disableCull();
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "FumoHead",
                HBMResourceManager.bobble_uffr_tex, packedLight, packedOverlay);
        poseStack.popPose();
    }

    // ---------- Drillgon ----------

    public static void renderDrillgon(PoseStack poseStack, MultiBufferSource buffer,
                                      int packedLight, int packedOverlay) {
        HBMResourceManager.bobble.renderPart(poseStack, buffer, "Drillgon",
                HBMResourceManager.bobble_drillgon_tex, packedLight, packedOverlay);
    }

    // ---------- Спец. декор (шляпы, предметы, glow) ----------

    public static void renderPost(BobbleType type, PoseStack poseStack, MultiBufferSource buffer,
                                  int packedLight, int packedOverlay) {
        switch (type) {
            case BLUEHAT -> {
                poseStack.pushPose();
                poseStack.translate(0, 0.875, -0.5);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90));
                poseStack.mulPose(Axis.ZP.rotationDegrees(-160));
                poseStack.scale(0.0625F, 0.0625F, 0.0625F);
                ResourceLocation texture = ResLocation(RefStrings.MODID, "textures/models/armor/hev_helmet.png");
                HBMResourceManager.armor_hev.renderPart(poseStack, buffer, "Head",
                        texture, packedLight, packedOverlay);
                poseStack.popPose();
            }
            case FRIZZLE -> {
                poseStack.pushPose();
                poseStack.translate(0.8, 1.6, 0.4);
                poseStack.scale(0.125F, 0.125F, 0.125F);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.XP.rotationDegrees(10));
                HBMResourceManager.n_i_4_n_i.renderPart(poseStack, buffer, "FrameDark",
                        HBMResourceManager.n_i_4_n_i_tex, packedLight, packedOverlay);
                HBMResourceManager.n_i_4_n_i.renderPart(poseStack, buffer, "Grip",
                        HBMResourceManager.n_i_4_n_i_tex, packedLight, packedOverlay);
                HBMResourceManager.n_i_4_n_i.renderPart(poseStack, buffer, "FrameLight",
                        HBMResourceManager.n_i_4_n_i_tex, packedLight, packedOverlay);
                HBMResourceManager.n_i_4_n_i.renderPart(poseStack, buffer, "Cylinder",
                        HBMResourceManager.n_i_4_n_i_tex, packedLight, packedOverlay);
                HBMResourceManager.n_i_4_n_i.renderPart(poseStack, buffer, "Barrel",
                        HBMResourceManager.n_i_4_n_i_tex, packedLight, packedOverlay);
                poseStack.popPose();

                poseStack.pushPose();
                poseStack.translate(0.3, 1.4, -0.2);
                poseStack.mulPose(Axis.XP.rotationDegrees(-100));
                poseStack.scale(0.5F, 0.5F, 0.5F);
                renderItemStack(new ItemStack(ModItems.WEAPON_MOD_SPECIAL_DOUBLOONS.get()),
                        poseStack, buffer, packedLight, packedOverlay);
                poseStack.popPose();
            }
            case ADAM29 -> {
                poseStack.pushPose();
                poseStack.translate(0.4, 1.15, 0.4);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                renderItemStack(new ItemStack(ModItems.CAN_REDBOMB.get()),
                        poseStack, buffer, packedLight, packedOverlay);
                poseStack.popPose();
            }
            case PHEO -> {
                poseStack.pushPose();
                poseStack.translate(0.5, 1.15, 0.45);
                poseStack.mulPose(Axis.XP.rotationDegrees(-60));
                poseStack.scale(2F, 2F, 2F);
                HBMResourceManager.shimmer_axe.renderAll(poseStack, buffer,
                        HBMResourceManager.shimmer_axe_tex, packedLight, packedOverlay);
                poseStack.popPose();
            }
            case BOB -> {
                poseStack.pushPose();
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.pushPose();
                poseStack.translate(0.75, 1, 0.9);
                for (int i = 0; i < 3; i++) {
                    HBMResourceManager.projectiles.renderPart(poseStack, buffer, "MiniNuke",
                            HBMResourceManager.mini_nuke_tex, packedLight, packedOverlay);
                    poseStack.translate(-0.75, 0, 0);
                }
                poseStack.popPose();
                poseStack.translate(0, 0.75, -0.9);
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.mulPose(Axis.XP.rotationDegrees(90));
                HBMResourceManager.projectiles.renderPart(poseStack, buffer, "MiniMIRV",
                        HBMResourceManager.mini_mirv_tex, packedLight, packedOverlay);
                poseStack.popPose();
            }
            case VAER -> {
                poseStack.pushPose();
                poseStack.translate(0.6, 1.5, 0);
                poseStack.mulPose(Axis.ZP.rotationDegrees(140));
                poseStack.mulPose(Axis.YP.rotationDegrees(-60));
                poseStack.translate(-0.2, 0, 0);
                poseStack.scale(0.5F, 0.5F, 0.5F);
                poseStack.popPose();
            }
            case MELLOW -> {
                VertexConsumer glow = buffer.getBuffer(RenderType.eyes(HBMResourceManager.bobble_mellow_glow_tex));
                HBMResourceManager.bobble.renderPart(poseStack, glow, "Fluoro", LightTexture.FULL_BRIGHT, packedOverlay);

                VertexConsumer glow2 = buffer.getBuffer(RenderType.eyes(HBMResourceManager.bobble_glow_tex));
                HBMResourceManager.bobble.renderPart(poseStack, glow2, "Glow", LightTexture.FULL_BRIGHT, packedOverlay);
            }
            case ABEL -> {
                VertexConsumer glow = buffer.getBuffer(RenderType.eyes(HBMResourceManager.bobble_abel_glow_tex));
                HBMResourceManager.bobble.renderPart(poseStack, glow, "Glow", LightTexture.FULL_BRIGHT, packedOverlay);
            }
        }
    }

    // ---------- Текст на подставке ----------

    public static void renderSocketLabel(BobbleType type, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        if (type == null || type == BobbleType.NONE) return;

        poseStack.pushPose();
        float f = 0.01F;
        poseStack.translate(0.63F, 0.175F, 0);
        poseStack.scale(f, -f, f);

        Font font = Minecraft.getInstance().font;
        String label = type.label;
        poseStack.translate(0, 0, font.width(label) * 0.5D);
        poseStack.mulPose(new Quaternionf().rotationY((float) Math.PI / 2));
        poseStack.translate(0, 1, 0);

        int color = type == BobbleType.VT ? 0xff0000 : 0xffffff;
        font.drawInBatch(label, 0, 0, color, false,
                poseStack.last().pose(), buffer,
                Font.DisplayMode.NORMAL, 0, packedLight);

        poseStack.popPose();
    }

    // ---------- Вспомогательный рендер предмета в 3D ----------

    public static void renderItemStack(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer,
                                       int packedLight, int packedOverlay) {
        Minecraft.getInstance().getItemRenderer().renderStatic(
                stack, ItemDisplayContext.FIXED,
                packedLight, packedOverlay,
                poseStack, buffer, Minecraft.getInstance().level, 0
        );
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityBobble tile) {
        return false;
    }
}