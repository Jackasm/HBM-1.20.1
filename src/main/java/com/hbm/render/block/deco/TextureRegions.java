package com.hbm.render.block.deco;

public class TextureRegions {
    private static final int TEXTURE_WIDTH = 32;

    public static final TextureRegion CUBE_0 = new TextureRegion(0, 0, 8, 8, TEXTURE_WIDTH);
    public static final TextureRegion CUBE_1 = new TextureRegion(8, 0, 8, 8, TEXTURE_WIDTH);
    public static final TextureRegion CUBE_2 = new TextureRegion(16, 0, 8, 8, TEXTURE_WIDTH);
    public static final TextureRegion CUBE_3 = new TextureRegion(24, 0, 8, 8, TEXTURE_WIDTH);

    public static final TextureRegion TOP_CUBE_0 = CUBE_0;
    public static final TextureRegion TOP_CUBE_1 = CUBE_1;
    public static final TextureRegion TOP_CUBE_2 = CUBE_2;
    public static final TextureRegion TOP_CUBE_3 = CUBE_3;

    public static final TextureRegion BOTTOM_CUBE_0 = CUBE_0;
    public static final TextureRegion BOTTOM_CUBE_1 = CUBE_1;
    public static final TextureRegion BOTTOM_CUBE_2 = CUBE_2;
    public static final TextureRegion BOTTOM_CUBE_3 = CUBE_3;

    public static final TextureRegion NORTH_CUBE_0 = CUBE_0;
    public static final TextureRegion NORTH_CUBE_1 = CUBE_1;
    public static final TextureRegion NORTH_CUBE_2 = CUBE_2;
    public static final TextureRegion NORTH_CUBE_3 = CUBE_3;

    public static final TextureRegion SOUTH_CUBE_0 = CUBE_0;
    public static final TextureRegion SOUTH_CUBE_1 = CUBE_1;
    public static final TextureRegion SOUTH_CUBE_2 = CUBE_2;
    public static final TextureRegion SOUTH_CUBE_3 = CUBE_3;

    public static final TextureRegion WEST_CUBE_0 = CUBE_0;
    public static final TextureRegion WEST_CUBE_1 = CUBE_1;
    public static final TextureRegion WEST_CUBE_2 = CUBE_2;
    public static final TextureRegion WEST_CUBE_3 = CUBE_3;

    public static final TextureRegion EAST_CUBE_0 = CUBE_0;
    public static final TextureRegion EAST_CUBE_1 = CUBE_1;
    public static final TextureRegion EAST_CUBE_2 = CUBE_2;
    public static final TextureRegion EAST_CUBE_3 = CUBE_3;

    public static final TextureRegion EAST_CONN_BOTTOM = new TextureRegion(0, 24, 8, 8, 32);
    public static final TextureRegion EAST_CONN_TOP = new TextureRegion(0, 32, 8, 8, 32);
    public static final TextureRegion WEST_CONN_BOTTOM = new TextureRegion(8, 24, 8, 8, 32);
    public static final TextureRegion WEST_CONN_TOP = new TextureRegion(8, 32, 8, 8, 32);

    public static final TextureRegion NORTH_CONN_BOTTOM = new TextureRegion(16, 8, 8, 8, 32);
    public static final TextureRegion NORTH_CONN_TOP = new TextureRegion(24, 8, 8, 8, 32);
    public static final TextureRegion SOUTH_CONN_BOTTOM = new TextureRegion(16, 16, 8, 8, 32);
    public static final TextureRegion SOUTH_CONN_TOP = new TextureRegion(24, 16, 8, 8, 32);

    public static final TextureRegion NORTH_EAST_CORNER = new TextureRegion(24, 24, 8, 8, 32);
    public static final TextureRegion NORTH_WEST_CORNER = new TextureRegion(16, 24, 8, 8, 32);
    public static final TextureRegion SOUTH_EAST_CORNER = new TextureRegion(24, 32, 8, 8, 32);
    public static final TextureRegion SOUTH_WEST_CORNER = new TextureRegion(16, 32, 8, 8, 32);

    public static final TextureRegion DIAGONAL_TOP_LEFT = new TextureRegion(0, 8, 8, 8, 32);
    public static final TextureRegion DIAGONAL_TOP_RIGHT = new TextureRegion(8, 8, 8, 8, 32);
    public static final TextureRegion DIAGONAL_BOTTOM_LEFT = new TextureRegion(0, 16, 8, 8, 32);
    public static final TextureRegion DIAGONAL_BOTTOM_RIGHT = new TextureRegion(8, 16, 8, 8, 32);
}