package com.hbm.render.block.deco;

public class TextureRegion {
    public final float startU, startV, endU, endV;

    public TextureRegion(int pixelX, int pixelY, int width, int height) {
        this(pixelX, pixelY, width, height, 32); // По умолчанию текстура 32x32
    }

    public TextureRegion(int pixelX, int pixelY, int width, int height, int textureSize) {
        this.startU = (float) pixelX / textureSize;
        this.startV = (float) pixelY / textureSize;
        this.endU = (float) (pixelX + width) / textureSize;
        this.endV = (float) (pixelY + height) / textureSize;
    }
}