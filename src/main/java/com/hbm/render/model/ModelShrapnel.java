package com.hbm.render.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModelShrapnel extends Model {

    private final ModelPart bullet;

    public ModelShrapnel() {
        super(RenderType::entityCutout);

        ModelPart.Cube cube = new ModelPart.Cube(
                0,                          // u - текстурная координата X
                0,                          // v - текстурная координата Y
                0.0F,                       // x - позиция X
                0.0F,                       // y - позиция Y
                0.0F,                       // z - позиция Z
                4.0F,                       // width - ширина
                4.0F,                       // height - высота
                4.0F,                       // depth - глубина
                0.0F,                       // inflateX - расширение по X
                0.0F,                       // inflateY - расширение по Y
                0.0F,                       // inflateZ - расширение по Z
                false,                      // mirror - зеркальное отображение
                16.0F,                      // texWidth - ширина текстуры
                8.0F,                       // texHeight - высота текстуры
                Set.of(Direction.values())   // faces - какие грани рендерить (все)
        );

        // Создаём ModelPart с одним кубом
        this.bullet = new ModelPart(
                java.util.List.of(cube),    // список кубов
                java.util.Map.of()           // дочерние части (пусто)
        );

        // Устанавливаем позицию
        this.bullet.setPos(1.0F, -0.5F, -0.5F);
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshDefinition = new MeshDefinition();
        PartDefinition partDefinition = meshDefinition.getRoot();

        partDefinition.addOrReplaceChild("bullet",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(0.0F, 0.0F, 0.0F, 4, 4, 4),
                PartPose.offset(1.0F, -0.5F, -0.5F));

        return LayerDefinition.create(meshDefinition, 16, 8);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer vertexConsumer,
                               int packedLight, int packedOverlay,
                               float red, float green, float blue, float alpha) {
        bullet.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}