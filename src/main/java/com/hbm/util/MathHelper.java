package com.hbm.util;

public class MathHelper {

    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public static int clampToInt(double value, int min, int max) {
        return (int) Math.max(min, Math.min(max, value));
    }

    public static float clampToFloat(double value, float min, float max) {
        return (float) Math.max(min, Math.min(max, value));
    }

    public static float wrapAngleTo180_float(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) {
            angle -= 360.0F;
        }
        if (angle < -180.0F) {
            angle += 360.0F;
        }
        return angle;
    }

    public static double wrapAngleTo180_double(double angle) {
        angle %= 360.0D;
        if (angle >= 180.0D) {
            angle -= 360.0D;
        }
        if (angle < -180.0D) {
            angle += 360.0D;
        }
        return angle;
    }
}