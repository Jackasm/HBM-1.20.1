package com.hbm.render.model;

import com.hbm.entity.mob.EntityUndeadSoldier;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;

public class ModelUndeadSoldier extends HumanoidModel<EntityUndeadSoldier> {

    public ModelUndeadSoldier(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(EntityUndeadSoldier entity, float limbSwing, float limbSwingAmount,
                          float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);

        int attackTicks = entity.getEntityData().get(EntityUndeadSoldier.ATTACK_TICKS);
        float attackProgress = attackTicks / 20.0F;
        if (attackProgress > 0) {
            float armPitch = attackProgress * 90.0F;
            this.rightArm.xRot = -Mth.DEG_TO_RAD * armPitch;
            this.leftArm.xRot = -Mth.DEG_TO_RAD * armPitch * 0.3F;
        }
    }
}