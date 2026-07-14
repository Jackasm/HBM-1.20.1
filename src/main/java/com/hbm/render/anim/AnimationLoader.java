package com.hbm.render.anim;

import com.google.gson.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

public class AnimationLoader {

    public static final Gson gson = new GsonBuilder().create();

    public static HashMap<String, BusAnimation> load(ResourceManager resourceManager, ResourceLocation file) {
        HashMap<String, BusAnimation> animations = new HashMap<>();

        try {
            Resource resource = resourceManager.getResource(file).orElseThrow();
            InputStream in = resource.open();
            InputStreamReader reader = new InputStreamReader(in);
            JsonObject json = gson.fromJson(reader, JsonObject.class);

            // Load offsets
            HashMap<String, double[]> offsets = new HashMap<>();
            if (json.has("offset")) {
                JsonObject offsetObj = json.getAsJsonObject("offset");
                for (Map.Entry<String, JsonElement> entry : offsetObj.entrySet()) {
                    JsonArray array = entry.getValue().getAsJsonArray();
                    double[] offset = new double[3];
                    for (int i = 0; i < 3; i++) {
                        offset[i] = array.get(i).getAsDouble();
                    }
                    offsets.put(entry.getKey(), offset);
                }
            }

            // Load rotation modes
            HashMap<String, double[]> rotModes = new HashMap<>();
            if (json.has("rotmode")) {
                JsonObject rotModeObj = json.getAsJsonObject("rotmode");
                for (Map.Entry<String, JsonElement> entry : rotModeObj.entrySet()) {
                    String mode = entry.getValue().getAsString();
                    double[] rotMode = new double[3];
                    rotMode[0] = getRot(mode.charAt(2));
                    rotMode[1] = getRot(mode.charAt(0));
                    rotMode[2] = getRot(mode.charAt(1));
                    rotModes.put(entry.getKey(), rotMode);
                }
            }

            // Load animations
            if (json.has("anim")) {
                JsonObject animObj = json.getAsJsonObject("anim");
                for (Map.Entry<String, JsonElement> animEntry : animObj.entrySet()) {
                    String animName = animEntry.getKey();
                    BusAnimation animation = new BusAnimation();

                    JsonObject busObj = animEntry.getValue().getAsJsonObject();
                    for (Map.Entry<String, JsonElement> busEntry : busObj.entrySet()) {
                        String busName = busEntry.getKey();
                        double[] offset = offsets.getOrDefault(busName, new double[3]);
                        double[] rotMode = rotModes.getOrDefault(busName, new double[]{0, 1, 2});

                        BusAnimationSequence sequence = loadSequence(
                                busEntry.getValue().getAsJsonObject(),
                                offset,
                                rotMode
                        );
                        animation.addBus(busName, sequence);
                    }

                    animations.put(animName, animation);
                }
            }

            reader.close();
            return animations;

        } catch (Exception e) {
            System.err.println("Failed to load animation file: " + file);
            e.printStackTrace();
            return animations;
        }
    }

    private static double getRot(char value) {
        return switch (value) {
            case 'X' -> 0;
            case 'Y' -> 1;
            case 'Z' -> 2;
            default -> 0;
        };
    }

    private static BusAnimationSequence loadSequence(JsonObject json, double[] offset, double[] rotMode) {
        BusAnimationSequence sequence = new BusAnimationSequence();

        // Location
        if (json.has("location")) {
            JsonObject location = json.getAsJsonObject("location");
            if (location.has("x")) addToSequence(sequence, BusAnimationSequence.Dimension.TX, location.getAsJsonArray("x"));
            if (location.has("y")) addToSequence(sequence, BusAnimationSequence.Dimension.TY, location.getAsJsonArray("y"));
            if (location.has("z")) addToSequence(sequence, BusAnimationSequence.Dimension.TZ, location.getAsJsonArray("z"));
        }

        // Rotation
        if (json.has("rotation_euler")) {
            JsonObject rotation = json.getAsJsonObject("rotation_euler");
            if (rotation.has("x")) addToSequence(sequence, BusAnimationSequence.Dimension.RX, rotation.getAsJsonArray("x"));
            if (rotation.has("y")) addToSequence(sequence, BusAnimationSequence.Dimension.RY, rotation.getAsJsonArray("y"));
            if (rotation.has("z")) addToSequence(sequence, BusAnimationSequence.Dimension.RZ, rotation.getAsJsonArray("z"));
        }

        // Scale
        if (json.has("scale")) {
            JsonObject scale = json.getAsJsonObject("scale");
            if (scale.has("x")) addToSequence(sequence, BusAnimationSequence.Dimension.SX, scale.getAsJsonArray("x"));
            if (scale.has("y")) addToSequence(sequence, BusAnimationSequence.Dimension.SY, scale.getAsJsonArray("y"));
            if (scale.has("z")) addToSequence(sequence, BusAnimationSequence.Dimension.SZ, scale.getAsJsonArray("z"));
        }

        sequence.offset = offset;
        sequence.rotMode = rotMode;

        return sequence;
    }

    private static void addToSequence(BusAnimationSequence sequence, BusAnimationSequence.Dimension dimension, JsonArray array) {
        BusAnimationKeyframe.IType prevInterp = null;
        for (JsonElement element : array) {
            BusAnimationKeyframe keyframe = loadKeyframe(element, prevInterp);
            prevInterp = keyframe.interpolationType;
            sequence.addKeyframe(dimension, keyframe);
        }
    }

    private static BusAnimationKeyframe loadKeyframe(JsonElement element, BusAnimationKeyframe.IType prevInterp) {
        JsonArray array = element.getAsJsonArray();

        double value = array.get(0).getAsDouble();
        int duration = array.get(1).getAsInt();
        BusAnimationKeyframe.IType interpolation = array.size() >= 3 ?
                BusAnimationKeyframe.IType.valueOf(array.get(2).getAsString()) :
                BusAnimationKeyframe.IType.LINEAR;
        BusAnimationKeyframe.EType easing = array.size() >= 4 ?
                BusAnimationKeyframe.EType.valueOf(array.get(3).getAsString()) :
                BusAnimationKeyframe.EType.AUTO;

        BusAnimationKeyframe keyframe = new BusAnimationKeyframe(value, duration, interpolation, easing);

        int i = 4;

        if (prevInterp == BusAnimationKeyframe.IType.BEZIER) {
            keyframe.leftX = array.get(i++).getAsDouble();
            keyframe.leftY = array.get(i++).getAsDouble();
            keyframe.leftType = BusAnimationKeyframe.HType.valueOf(array.get(i++).getAsString());
        }

        if (interpolation == BusAnimationKeyframe.IType.LINEAR || interpolation == BusAnimationKeyframe.IType.CONSTANT)
            return keyframe;

        if (interpolation == BusAnimationKeyframe.IType.BEZIER) {
            keyframe.rightX = array.get(i++).getAsDouble();
            keyframe.rightY = array.get(i++).getAsDouble();
            keyframe.rightType = BusAnimationKeyframe.HType.valueOf(array.get(i++).getAsString());
        }

        if (interpolation == BusAnimationKeyframe.IType.ELASTIC) {
            keyframe.amplitude = array.get(i++).getAsDouble();
            keyframe.period = array.get(i++).getAsDouble();
        } else if (interpolation == BusAnimationKeyframe.IType.BACK) {
            keyframe.back = array.get(i++).getAsDouble();
        }

        return keyframe;
    }
}