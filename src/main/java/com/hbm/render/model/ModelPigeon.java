package com.hbm.render.model;

import com.hbm.entity.mob.EntityPigeon;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.NotNull;

import static com.hbm.util.RefStrings.MODID;
import static com.hbm.util.ResLocation.ResLocation;

/**
 * Модель голубя — перенос {@code ModelPigeon} из 1.7.10 один в один по геометрии.
 *
 * <p>Отличия только технические: пивоты задаются через {@link PartPose}, жирная и
 * обычная тушка — два набора частей с переключением видимости, а взмах крыла
 * считается по тем же полям {@code fallTime}/{@code dest}, что и в оригинале
 * (там их подставлял {@code RenderPigeon.handleRotationFloat}).</p>
 */
@OnlyIn(Dist.CLIENT)
public class ModelPigeon extends EntityModel<EntityPigeon> {

    public static final ModelLayerLocation LAYER =
            new ModelLayerLocation(ResLocation(MODID, "pigeon"), "main");

    private final ModelPart root;
    private final ModelPart head;
    private final ModelPart beak;
    private final ModelPart body;
    private final ModelPart bodyFat;
    private final ModelPart ass;
    private final ModelPart feathers;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart bodyLeftWing;
    private final ModelPart bodyRightWing;
    private final ModelPart fatLeftWing;
    private final ModelPart fatRightWing;

    public ModelPigeon(ModelPart root) {
        this.root = root;
        this.head = root.getChild("head");
        this.beak = root.getChild("beak");
        this.body = root.getChild("body");
        this.bodyFat = root.getChild("body_fat");
        this.ass = root.getChild("ass");
        this.feathers = root.getChild("feathers");
        this.leftLeg = root.getChild("left_leg");
        this.rightLeg = root.getChild("right_leg");
        this.bodyLeftWing = this.body.getChild("left_wing");
        this.bodyRightWing = this.body.getChild("right_wing");
        this.fatLeftWing = this.bodyFat.getChild("left_wing");
        this.fatRightWing = this.bodyFat.getChild("right_wing");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition mesh = new MeshDefinition();
        PartDefinition root = mesh.getRoot();

        root.addOrReplaceChild("head",
                CubeListBuilder.create().texOffs(0, 0).addBox(-2F, -6F, -2F, 4, 6, 4),
                PartPose.offset(0F, 16F, -2F));

        root.addOrReplaceChild("beak",
                CubeListBuilder.create().texOffs(14, 0).addBox(-1F, -4F, -4F, 2, 2, 2),
                PartPose.offset(0F, 16F, -2F));

        PartDefinition body = root.addOrReplaceChild("body",
                CubeListBuilder.create().texOffs(0, 10).addBox(-3F, -3F, -4F, 6, 6, 8),
                PartPose.offset(0F, 17F, 0F));

        body.addOrReplaceChild("left_wing",
                CubeListBuilder.create().texOffs(28, 0).addBox(0F, 0F, -3F, 1, 4, 6),
                PartPose.offset(3F, -2F, 0F));
        body.addOrReplaceChild("right_wing",
                CubeListBuilder.create().texOffs(28, 10).addBox(-1F, 0F, -3F, 1, 4, 6),
                PartPose.offset(-3F, -2F, 0F));

        // Жирная тушка: та же геометрия с деформацией 1, как addBox(..., 1) в 1.7.10.
        PartDefinition bodyFat = root.addOrReplaceChild("body_fat",
                CubeListBuilder.create().texOffs(0, 10)
                        .addBox(-3F, -3F, -4F, 6, 6, 8, new CubeDeformation(1.0F)),
                PartPose.offset(0F, 17F, 0F));

        bodyFat.addOrReplaceChild("left_wing",
                CubeListBuilder.create().texOffs(28, 0).addBox(0F, 0F, -3F, 1, 4, 6),
                PartPose.offset(3F, -2F, 0F));
        bodyFat.addOrReplaceChild("right_wing",
                CubeListBuilder.create().texOffs(28, 10).addBox(-1F, 0F, -3F, 1, 4, 6),
                PartPose.offset(-3F, -2F, 0F));

        root.addOrReplaceChild("ass",
                CubeListBuilder.create().texOffs(0, 24).addBox(-2F, -2F, -2F, 4, 4, 4),
                PartPose.offset(0F, 20F, 4F));

        root.addOrReplaceChild("feathers",
                CubeListBuilder.create().texOffs(16, 24).addBox(-1F, -0.5F, -2F, 2, 1, 4),
                PartPose.offset(0F, 21.5F, 7.5F));

        root.addOrReplaceChild("left_leg",
                CubeListBuilder.create().texOffs(20, 0).addBox(-1F, 0F, 0F, 2, 4, 2),
                PartPose.offset(1F, 20F, -1F));

        root.addOrReplaceChild("right_leg",
                CubeListBuilder.create().texOffs(20, 0).addBox(-1F, 0F, 0F, 2, 4, 2),
                PartPose.offset(-1F, 20F, -1F));

        return LayerDefinition.create(mesh, 64, 32);
    }

    @Override
    public void renderToBuffer(@NotNull PoseStack poseStack, @NotNull VertexConsumer buffer, int packedLight,
                               int packedOverlay, float red, float green, float blue, float alpha) {
        this.root.render(poseStack, buffer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void setupAnim(@NotNull EntityPigeon entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        float partial = ageInTicks - entity.tickCount;
        float fallTime = Mth.lerp(partial, entity.prevFallTime, entity.fallTime);
        float dest = Mth.lerp(partial, entity.prevDest, entity.dest);

        this.head.xRot = this.beak.xRot = headPitch * (Mth.PI / 180F);
        this.head.yRot = this.beak.yRot = netHeadYaw * (Mth.PI / 180F);

        this.body.xRot = this.bodyFat.xRot = this.ass.xRot = -(Mth.PI / 4F);
        this.feathers.xRot = -(Mth.PI / 8F);

        this.rightLeg.xRot = Mth.cos(limbSwing * 0.6662F) * 1.4F * limbSwingAmount;
        this.leftLeg.xRot = Mth.cos(limbSwing * 0.6662F + Mth.PI) * 1.4F * limbSwingAmount;

        // Взмах крыла: в 1.7.10 его подставлял RenderPigeon.handleRotationFloat,
        // здесь считаем прямо из тех же полей.
        float flap = (Mth.sin(fallTime) + 1.0F) * dest;
        this.bodyRightWing.zRot = flap;
        this.bodyLeftWing.zRot = -flap;
        this.fatRightWing.zRot = flap;
        this.fatLeftWing.zRot = -flap;

        boolean fat = entity.isFat();
        this.body.visible = !fat;
        this.bodyFat.visible = fat;

        if (fat) {
            this.head.z = -4F;
            this.beak.z = -4F;
            this.ass.z = 5F;
            this.feathers.z = 8.5F;
        } else {
            this.head.z = -2F;
            this.beak.z = -2F;
            this.ass.z = 4F;
            this.feathers.z = 7.5F;
        }
    }
}
