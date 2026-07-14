package com.hbm.util;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import static com.hbm.util.ResLocation.ResLocation;

public class ColorUtil {

    public static int getColorFromDye(ItemStack stack) {
        for (DyeColor dye : DyeColor.values()) {
            if (stack.is(dye.getTag())) {
                return dye.getTextColor();
            }
        }
        return 0;
    }

    public static int getAverageColorFromStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return 0xFFFFFF;

        try {
            BufferedImage tex = getImageFromStack(stack);

            if (tex == null) return 0xFFFFFF;

            int r = 0;
            int g = 0;
            int b = 0;
            int pixels = 0;

            for (int i = 0; i < tex.getWidth(); i++) {
                for (int j = 0; j < tex.getHeight(); j++) {
                    Color pixel = new Color(tex.getRGB(i, j), true);

                    if (pixel.getAlpha() == 255) {
                        r += pixel.getRed();
                        g += pixel.getGreen();
                        b += pixel.getBlue();
                        pixels++;
                    }
                }
            }

            if (pixels == 0) return 0xFFFFFF;

            int avgR = r / pixels;
            int avgG = g / pixels;
            int avgB = b / pixels;

            return (avgR << 16) | (avgG << 8) | avgB;

        } catch (Exception ex) {
            return 0xFFFFFF;
        }
    }

    private static BufferedImage getImageFromStack(ItemStack stack) {
        try {
            // Получаем ResourceLocation текстуры предмета
            ResourceLocation textureLocation = getTextureLocation(stack);
            if (textureLocation == null) return null;

            // Загружаем текстуру
            InputStream stream = Minecraft.getInstance().getResourceManager()
                    .getResource(textureLocation).get().open();
            BufferedImage image = ImageIO.read(stream);
            stream.close();

            return image;
        } catch (IOException e) {
            return null;
        }
    }

    private static ResourceLocation getTextureLocation(ItemStack stack) {
        ResourceLocation registryName = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (registryName != null) {
            return ResLocation(registryName.getNamespace(),
                    "textures/item/" + registryName.getPath() + ".png");
        }
        return null;
    }

    public static int amplifyColor(int hex) {
        return amplifyColor(hex, 255);
    }

    public static int amplifyColor(int hex, int limit) {
        Color color = new Color(hex);
        int r = color.getRed();
        int g = color.getGreen();
        int b = color.getBlue();
        int max = Math.max(Math.max(1, r), Math.max(g, b));

        r = r * limit / max;
        g = g * limit / max;
        b = b * limit / max;

        r = Math.min(r, 255);
        g = Math.min(g, 255);
        b = Math.min(b, 255);

        return new Color(r, g, b).getRGB();
    }

    public static int lightenColor(int hex, double percent) {
        int r = (hex >> 16) & 0xFF;
        int g = (hex >> 8) & 0xFF;
        int b = hex & 0xFF;

        r = (int) (r + (255 - r) * percent);
        g = (int) (g + (255 - g) * percent);
        b = (int) (b + (255 - b) * percent);

        return (r << 16) | (g << 8) | b;
    }
}