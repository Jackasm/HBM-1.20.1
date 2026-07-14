package com.hbm.render.loader;

public class TextureCoordinate {
    public final float u, v, w;

    public TextureCoordinate(float u, float v) {
        this(u, v, 0.0f);
    }

    public TextureCoordinate(float u, float v, float w) {
        this.u = u;
        this.v = v;
        this.w = w;
    }
}