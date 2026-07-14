package com.hbm.util;

import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("removal")
public class ResLocation {
    public static ResourceLocation ResLocation(String path) {
        return new ResourceLocation(RefStrings.MODID, path);
    }

    public static ResourceLocation ResLocation(String namespace, String path)
    {
        return new ResourceLocation(namespace, path);
    }
}
