package com.hbm.render.loader;

import org.joml.Vector3f;

public class Vertex {
    public final float x, y, z;

    public Vertex(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vertex(Vector3f vec) {
        this.x = vec.x();
        this.y = vec.y();
        this.z = vec.z();
    }

    public Vector3f toVector() {
        return new Vector3f(x, y, z);
    }
}