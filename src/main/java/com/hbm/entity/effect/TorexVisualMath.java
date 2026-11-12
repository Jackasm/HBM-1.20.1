package com.hbm.entity.effect;

import net.minecraft.world.phys.Vec3;

/**
 * Small, server-safe math routines used by the client-side TorEx mushroom cloud.
 * Legacy Vec3.rotateAroundZ rotated in the X/Y convection plane; the original
 * port's xRot rotated in the Y/Z plane and made cloudlets move the wrong way.
 */
public final class TorexVisualMath {

    private TorexVisualMath() {
    }

    public static Vec3 rotateConvectionPlane(Vec3 vector, float radians) {
        return vector.zRot(radians);
    }

    /**
     * Keep the original 1.7.10 greying multiplier and +1 ring bonus in
     * high-dynamic-range RGB. DO NOT clamp here. Legacy Tessellator colour
     * was (interpolated colour * brightness) before the framebuffer clamp.
     * Premature [0,1] clipping causes the modern cloud's dull olive hue.
     */
    public static Vec3 interpolateColor(Vec3 previous, Vec3 current, float partialTicks,
                                        double greying, boolean ring) {
        Vec3 from = previous == null ? current : previous;
        double multiplier = greying + (ring ? 1D : 0D);
        return new Vec3(
                (from.x + (current.x - from.x) * partialTicks) * multiplier,
                (from.y + (current.y - from.y) * partialTicks) * multiplier,
                (from.z + (current.z - from.z) * partialTicks) * multiplier);
    }

    /**
     * Convert the already-interpolated RGB into Forge's packed vertex colour.
     * Apply cloudlet brightness BEFORE the final clamp, as the original
     * gl colour path did. This fixes normal smoke and rings without changing
     * the cloudlet colour formula, opacity, motion, or the accepted flash.
     */
    public static Vec3 vertexColor(Vec3 rgb, double brightness) {
        return new Vec3(
                unit(rgb.x * brightness),
                unit(rgb.y * brightness),
                unit(rgb.z * brightness));
    }

    /**
     * Exact original 1.7.10 TorEx standard palette. The actual transitions
     * come from the original heat/distance estimate, cloudlet motion and
     * the late greying RGB multiplier, not an invented age-driven colour ramp.
     *
     * The modern renderer clamps the final interpolated vertex channels.
     */
    public static Vec3 standardSmokeColor(double intensity) {
        return new Vec3(
                Math.max(intensity * 2D, 0.25D),
                Math.max(intensity * 1.5D, 0.25D),
                Math.max(intensity * 0.5D, 0.25D));
    }

    /**
     * Flash vertex opacity from the legacy 100-tick envelope. A visible
     * flash depends separately on renderer visibility, batching and depth.
     */
    public static float flashAlpha(float ageTicks) {
        return (float) Math.max(0D, Math.min(1D, (100D - ageTicks) / 100D));
    }

    private static double unit(double value) {
        return Math.max(0D, Math.min(1D, value));
    }
}
