package com.hbm.render.util;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixUtils {

    public static Vec3 flashMatrixToWorldPos(Matrix4f flashMatrix, LivingEntity player) {

        Vector3f localPosVec = new Vector3f();
        flashMatrix.getTranslation(localPosVec);
        Vec3 localPos = new Vec3(localPosVec.x, localPosVec.y, -localPosVec.z);

        Vec3 lookDir = player.getLookAngle().normalize();
        Vec3 upDir = new Vec3(0, 1, 0);
        Vec3 rightDir = lookDir.cross(upDir).normalize();

        upDir = rightDir.cross(lookDir).normalize();

        Vec3 worldOffset = rightDir.scale(localPos.x)
                .add(upDir.scale(localPos.y))
                .add(lookDir.scale(localPos.z));

        return player.getEyePosition().add(worldOffset);
    }
}
