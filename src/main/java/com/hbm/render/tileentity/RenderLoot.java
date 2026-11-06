package com.hbm.render.tileentity;

import com.hbm.items.ModAmmoItems;
import com.hbm.items.ModGunItems;
import com.hbm.main.HBMResourceManager;
import com.hbm.tileentity.deco.TileEntityLoot;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderLoot implements BlockEntityRenderer<TileEntityLoot> {

    private final ItemRenderer itemRenderer;

    public RenderLoot(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    @Override
    public void render(@NotNull TileEntityLoot loot, float partialTick, @NotNull PoseStack poseStack,
                       @NotNull MultiBufferSource buffer, int packedLight, int packedOverlay) {

        if (loot.getItems().isEmpty()) return;

        //poseStack.translate(0.5, 0, 0.5);

        for (TileEntityLoot.LootEntry entry : loot.getItems()) {
            ItemStack stack = entry.stack();
            if (stack == null || stack.isEmpty()) continue;

            poseStack.pushPose();

            // Позиция предмета
            double x = Math.max(0, Math.min(1, entry.x() + 0.5));
            double y = entry.y();
            double z = Math.max(0, Math.min(1, entry.z() + 0.5));
            poseStack.translate(x, y, z);

            // Масштаб и поворот для 3D предметов
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));

            renderItem(stack, poseStack, buffer, packedLight, packedOverlay);

            poseStack.popPose();
        }
    }

    private void renderItem(ItemStack stack, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        // Проверяем, есть ли кастомный рендер для специфичных предметов
        if (stack.getItem() == ModGunItems.GUN_MARESLEG.get()) {
            renderMaresleg(poseStack, buffer, packedLight, packedOverlay);
            return;
        }

        if (stack.getItem() == ModGunItems.GUN_FLAREGUN.get()) {
            renderFlaregun(poseStack, buffer, packedLight, packedOverlay);
            return;
        }

        if (stack.getItem() == ModAmmoItems.AMMO_NUKE_STANDARD.get() ||
                stack.getItem() == ModAmmoItems.AMMO_NUKE_HIGH.get() ||
                stack.getItem() == ModAmmoItems.AMMO_NUKE_TOTS.get()) {
            renderNuke(poseStack, buffer, packedLight, packedOverlay);
            return;
        }

        // Стандартный рендер предмета
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);
        itemRenderer.render(stack, ItemDisplayContext.FIXED, false, poseStack, buffer, packedLight, packedOverlay, model);
    }

    private void renderFlaregun(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.scale(0.12f, 0.12f, 0.12f);
        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(90));
        HBMResourceManager.flaregun.renderAll(poseStack, buffer, HBMResourceManager.flaregun_tex, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void renderNuke(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.scale(0.3f, 0.3f, 0.3f);
        poseStack.translate(0, 0.5, 0);
        HBMResourceManager.projectiles.renderPart(poseStack, buffer, "MiniNuke", HBMResourceManager.fatman_mininuke_tex, packedLight, packedOverlay);
        poseStack.popPose();
    }

    private void renderMaresleg(PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        poseStack.scale(0.12f, 0.12f, 0.12f);
        poseStack.translate(0, 0.5, 0);
        poseStack.mulPose(Axis.XP.rotationDegrees(90));
        poseStack.mulPose(Axis.ZP.rotationDegrees(45));
        HBMResourceManager.maresleg.renderAll(poseStack, buffer, HBMResourceManager.maresleg_tex, packedLight, packedOverlay);
        poseStack.popPose();
    }

    @Override
    public int getViewDistance() {
        return 256;
    }

    @Override
    public boolean shouldRenderOffScreen(@NotNull TileEntityLoot blockEntity) {
        return true;
    }
}