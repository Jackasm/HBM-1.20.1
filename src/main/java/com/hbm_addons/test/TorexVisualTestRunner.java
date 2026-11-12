package com.hbm_addons.test;

import com.hbm.entity.ModEntities;
import com.hbm.entity.effect.EntityNukeTorex;
import com.hbm.entity.effect.TorexVisualMath;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

/**
 * Headless TorEx maths + real ServerLevel entity age regression.
 * A manually constructed entity is not a valid substitute for world ticking.
 * Visual colour/shape and the client flash still need in-game acceptance.
 */
public class TorexVisualTestRunner extends TestRunner {

    private static final double EPS = 1.0E-5D;
    private static final int FIRST_SAMPLE = 21;
    private static final int FINAL_SAMPLE = 31;

    private EntityNukeTorex probe;
    private int firstAge;

    public TorexVisualTestRunner(ServerLevel level, String scenario) {
        super(level, scenario);
    }

    @Override
    public boolean tick(MinecraftServer server) {
        tickCount++;
        if (tickCount == 1) {
            runMathChecks();

            // Chunk (0,0) is already force-loaded by TestBootstrap.
            // Only the cloud entity is spawned, never any destructive explosion.
            probe = new EntityNukeTorex(ModEntities.NUKE_TOREX.get(), level);
            probe.setPos(8.5D, 150.0D, 8.5D);
            boolean spawned = level.addFreshEntity(probe);
            check("torex_world_spawned", spawned);
            if (!spawned) {
                check("torex_world_age_started", false);
                check("torex_world_age_advanced", false);
                metric("cases", 26);
                finish(false);
                return true;
            }
            return false;
        }

        if (tickCount == FIRST_SAMPLE) {
            firstAge = probe.tickCount;
            check("torex_world_age_started", firstAge > 0);
            metric("age_first", firstAge);
            return false;
        }

        if (tickCount < FINAL_SAMPLE) return false;

        int finalAge = probe.tickCount;
        check("torex_world_age_advanced", finalAge > firstAge);
        metric("age_final", finalAge);
        probe.discard();
        metric("cases", 26);
        finish(checks.stream().allMatch(result -> result.pass));
        return true;
    }

    private void runMathChecks() {
        Vec3 quarter = TorexVisualMath.rotateConvectionPlane(new Vec3(1, 0, 0), (float) (Math.PI / 2));
        check("convection_xy", near(quarter.x, 0) && near(quarter.y, -1) && near(quarter.z, 0));

        Vec3 half = TorexVisualMath.rotateConvectionPlane(new Vec3(2, 0, 0), (float) Math.PI);
        check("convection_halfturn", near(half.x, -2) && near(half.y, 0) && near(half.z, 0));

        Vec3 prev = new Vec3(0.4, 0.2, 0.1);
        Vec3 next = new Vec3(0.5, 0.3, 0.15);
        check("normal_color", rgb(TorexVisualMath.interpolateColor(prev, next, 0.5F, 1D, false), 0.45, 0.25, 0.125));
        check("greying_rgb", rgb(TorexVisualMath.interpolateColor(prev, next, 0.5F, 1.5D, false), 0.675, 0.375, 0.1875));
        check("ring_bonus_hdr", rgb(TorexVisualMath.interpolateColor(prev, next, 0.5F, 1.5D, true), 1.125, 0.625, 0.3125));
        check("first_frame", rgb(TorexVisualMath.interpolateColor(null, next, 0.5F, 1D, false), 0.5, 0.3, 0.15));
        Vec3 bright = new Vec3(0.8, 0.8, 0.8);
        check("hdr_interpolate_preserved", rgb(TorexVisualMath.interpolateColor(bright, bright, 1F, 2D, false), 1.6, 1.6, 1.6));
        check("packed_color_clamp", rgb(TorexVisualMath.vertexColor(new Vec3(1.6, 1.6, 1.6), 0.75D), 1, 1, 1));
        check("original_hot_vertex_yellow", rgb(TorexVisualMath.vertexColor(
                TorexVisualMath.standardSmokeColor(1D), 0.75D), 1D, 1D, 0.375D));
        check("olive_clamp_order_regression", rgb(TorexVisualMath.vertexColor(
                TorexVisualMath.standardSmokeColor(0.75D), 0.75D), 1D, 0.84375D, 0.28125D));
        check("dim_smoke_preserved", rgb(TorexVisualMath.vertexColor(
                TorexVisualMath.standardSmokeColor(0.18D), 0.75D), 0.27D, 0.2025D, 0.1875D));

        // Legacy 1.7.10 type-0 TorEx: fixed channel ratios, not a timed
        // red-to-grey fade. Late RGB greying remains tested separately.
        check("legacy_rgb_hot", rgb(TorexVisualMath.standardSmokeColor(1D), 2, 1.5, 0.5));
        check("legacy_rgb_half", rgb(TorexVisualMath.standardSmokeColor(0.5D), 1, 0.75, 0.25));
        check("legacy_rgb_mid", rgb(TorexVisualMath.standardSmokeColor(0.4D), 0.8, 0.6, 0.25));
        check("legacy_rgb_cooling", rgb(TorexVisualMath.standardSmokeColor(0.2D), 0.4, 0.3, 0.25));
        check("legacy_rgb_dim", rgb(TorexVisualMath.standardSmokeColor(0.18D), 0.36, 0.27, 0.25));
        check("legacy_rgb_floor", rgb(TorexVisualMath.standardSmokeColor(0D), 0.25, 0.25, 0.25));
        check("legacy_rgb_threshold", rgb(TorexVisualMath.standardSmokeColor(0.125D), 0.25, 0.25, 0.25));
        check("legacy_rgb_high_heat", rgb(TorexVisualMath.standardSmokeColor(2D), 4, 3, 1));
        check("legacy_rgb_negative_floor", rgb(TorexVisualMath.standardSmokeColor(-1D), 0.25, 0.25, 0.25));
        check("flash_start_alpha", near(TorexVisualMath.flashAlpha(0F), 1D));
        check("flash_mid_alpha", near(TorexVisualMath.flashAlpha(50F), 0.5D));
        check("flash_end_alpha", near(TorexVisualMath.flashAlpha(100F), 0D));
    }

    private static boolean near(double actual, double expected) {
        return Math.abs(actual - expected) <= EPS;
    }

    private static boolean rgb(Vec3 color, double r, double g, double b) {
        return near(color.x, r) && near(color.y, g) && near(color.z, b);
    }
}
