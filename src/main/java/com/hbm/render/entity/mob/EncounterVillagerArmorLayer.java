package com.hbm.render.entity.mob;

import com.hbm.entity.mob.EntityEncounterVillager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.VillagerModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;

@OnlyIn(Dist.CLIENT)
public class EncounterVillagerArmorLayer extends RenderLayer<EntityEncounterVillager, VillagerModel<EntityEncounterVillager>> {

    private final HumanoidModel<EntityEncounterVillager> innerModel;
    private final HumanoidModel<EntityEncounterVillager> outerModel;

    public EncounterVillagerArmorLayer(
            RenderLayerParent<EntityEncounterVillager, VillagerModel<EntityEncounterVillager>> renderer,
            HumanoidModel<EntityEncounterVillager> inner,
            HumanoidModel<EntityEncounterVillager> outer) {
        super(renderer);
        this.innerModel = inner;
        this.outerModel = outer;
    }

    @Override
    public void render(@NotNull PoseStack pose, @NotNull MultiBufferSource buffer, int light,
                       @NotNull EntityEncounterVillager entity,
                       float limbSwing, float limbSwingAmount, float partialTick,
                       float ageInTicks, float netHeadYaw, float headPitch) {

        VillagerModel<EntityEncounterVillager> parent = getParentModel();

        // Скрываем руки — у Villager их нет
        innerModel.rightArm.visible = false;
        innerModel.leftArm.visible = false;
        outerModel.rightArm.visible = false;
        outerModel.leftArm.visible = false;

        // Копируем позы из VillagerModel в armor-модели
        syncPose(parent, innerModel);
        syncPose(parent, outerModel);

        // По слоту — своя броня
        renderPiece(pose, buffer, entity, EquipmentSlot.HEAD, light, outerModel);
        renderPiece(pose, buffer, entity, EquipmentSlot.CHEST, light, outerModel);
        renderPiece(pose, buffer, entity, EquipmentSlot.LEGS, light, innerModel);
        renderPiece(pose, buffer, entity, EquipmentSlot.FEET, light, outerModel);
    }

    /** Копирует позы head/hat/body/rightLeg/leftLeg из VillagerModel в armor-модель. */
    private static void syncPose(VillagerModel<EntityEncounterVillager> src,
                                 HumanoidModel<EntityEncounterVillager> dst) {
        dst.head.copyFrom(src.head);
        dst.hat.copyFrom(src.hat);
        dst.body.copyFrom(src.body);
        dst.rightLeg.copyFrom(src.rightLeg);
        dst.leftLeg.copyFrom(src.leftLeg);
    }

    private void renderPiece(PoseStack pose, MultiBufferSource buffer,
                             EntityEncounterVillager entity, EquipmentSlot slot,
                             int light, HumanoidModel<EntityEncounterVillager> model) {

        ItemStack stack = entity.getItemBySlot(slot);
        if (stack.isEmpty()) return;
        if (!(stack.getItem() instanceof ArmorItem armorItem)) return;
        if (armorItem.getEquipmentSlot() != slot) return;

        model.setAllVisible(false);
        switch (slot) {
            case HEAD -> {
                model.head.visible = true;
                model.hat.visible = true;
            }
            case CHEST -> model.body.visible = true;
            case LEGS -> {
                model.body.visible = true;
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
            case FEET -> {
                model.rightLeg.visible = true;
                model.leftLeg.visible = true;
            }
        }

        ResourceLocation tex = getArmorTexture(stack, slot);
        boolean glint = stack.hasFoil();
        VertexConsumer vc = ItemRenderer.getArmorFoilBuffer(
                buffer, RenderType.armorCutoutNoCull(tex), false, glint);

        model.renderToBuffer(pose, vc, light, OverlayTexture.NO_OVERLAY, 1F, 1F, 1F, 1F);
    }

    /** Стандартная логика определения текстуры брони (как в ванильном HumanoidArmorLayer). */
    private static ResourceLocation getArmorTexture(ItemStack stack, EquipmentSlot slot) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String material = item.getMaterial().getName();
        ResourceLocation itemId = ForgeRegistries.ITEMS.getKey(item);
        String namespace = itemId != null ? itemId.getNamespace() : "minecraft";
        boolean inner = slot == EquipmentSlot.LEGS;
        String layer = inner ? "layer_2" : "layer_1";
        return new ResourceLocation(namespace,
                "textures/models/armor/" + material + "_" + layer + ".png");
    }
}
