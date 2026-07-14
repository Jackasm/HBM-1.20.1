package com.hbm.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CompositeArmorModel extends HumanoidModel<LivingEntity> {

    private final HumanoidModel<?> baseModel;
    private final List<RenderLayer> layers = new ArrayList<>();

    public static class RenderLayer {
        public final HumanoidModel<?> model;
        public final ResourceLocation texture;

        public RenderLayer(HumanoidModel<?> model, ResourceLocation texture) {
            this.model = model;
            this.texture = texture;
        }
    }

    public CompositeArmorModel(HumanoidModel<?> baseModel) {
        super(createEmptyModelPart());
        this.baseModel = baseModel;
    }

    private static ModelPart createEmptyModelPart() {
        MeshDefinition mesh = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition part = mesh.getRoot();
        return part.bake(64, 32);
    }

    public void addLayer(HumanoidModel<?> model, ResourceLocation texture) {
        layers.add(new RenderLayer(model, texture));
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // Рендерим базовый шлем
        if (baseModel != null) {
            baseModel.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        }

        // Рендерим все слои модов поверх
        for (RenderLayer layer : layers) {
            if (layer.model != null && layer.texture != null) {
                VertexConsumer consumer = Minecraft.getInstance().renderBuffers().bufferSource()
                        .getBuffer(RenderType.entityCutout(layer.texture));
                layer.model.renderToBuffer(poseStack, consumer, packedLight, packedOverlay, red, green, blue, alpha);
            }
        }
    }
}