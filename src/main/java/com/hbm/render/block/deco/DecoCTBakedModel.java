package com.hbm.render.block.deco;

import com.hbm.blocks.generic.BlockDecoCT;
import com.hbm.blocks.generic.BlockDecoCTModelData;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;


public class DecoCTBakedModel implements IDynamicBakedModel {
    private final TextureAtlasSprite texture;
    private final ItemTransforms transforms;

    public DecoCTBakedModel(IGeometryBakingContext context,
                            Function<Material, TextureAtlasSprite> spriteGetter) {
        Material textureMaterial = context.getMaterial("texture");
        if (textureMaterial == null) {
            textureMaterial = context.getMaterial("particle");
        }

        this.texture = spriteGetter.apply(textureMaterial);
        this.transforms = context.getTransforms();
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                             @NotNull RandomSource rand, @NotNull ModelData extraData,
                                             @Nullable RenderType renderType) {
        List<BakedQuad> quads = new ArrayList<>();


        if (state != null && isSameDecoBlock(state)) {
                addDecoBlockGeometry(quads, state, side, extraData);
            } else {
                addSimpleInventoryGeometry(quads, side);
            }

        return quads;
    }

    private void addDecoBlockGeometry(List<BakedQuad> quads, BlockState state, @Nullable Direction side, ModelData modelData) {
        float cubeSize = 0.5f;

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    float fromX = x * cubeSize;
                    float fromY = y * cubeSize;
                    float fromZ = z * cubeSize;
                    float toX = (x + 1) * cubeSize;
                    float toY = (y + 1) * cubeSize;
                    float toZ = (z + 1) * cubeSize;

                    addDecoCube(quads, state, fromX, fromY, fromZ, toX, toY, toZ, side, x, y, z, modelData);
                }
            }
        }
    }

    private void addDecoCube(List<BakedQuad> quads, BlockState state,
                             float fromX, float fromY, float fromZ,
                             float toX, float toY, float toZ,
                             @Nullable Direction side, int cubeX, int cubeY, int cubeZ, ModelData modelData) {

        for (Direction face : Direction.values()) {
            if (side != null && side != face) continue;

            boolean shouldRenderFace = shouldRenderFace(state, face, cubeX, cubeY, cubeZ);
            if (shouldRenderFace) {
                TextureRegion region = getTextureRegionForCubeFace(state, face, cubeX, cubeY, cubeZ, modelData);
                addCubeFace(quads, face, fromX, fromY, fromZ, toX, toY, toZ, region);
            }
        }
    }

    private boolean isSameDecoBlock(BlockState state) {
        return state.getBlock() instanceof BlockDecoCT;
    }

    private boolean shouldRenderFace(BlockState state, Direction face, int cubeX, int cubeY, int cubeZ) {
        boolean onBorder = isOnBorder(face, cubeX, cubeY, cubeZ);

        if (onBorder) {
            BooleanProperty property = getPropertyForDirection(face);
            return !state.getValue(property);
        } else {
            return true;
        }
    }

    private boolean isOnBorder(Direction face, int cubeX, int cubeY, int cubeZ) {
        return switch (face) {
            case NORTH -> cubeZ == 0;
            case SOUTH -> cubeZ == 1;
            case WEST -> cubeX == 0;
            case EAST -> cubeX == 1;
            case DOWN -> cubeY == 0;
            case UP -> cubeY == 1;
        };
    }

    private TextureRegion getTextureRegionForCubeFace(BlockState state, Direction face,
                                                      int cubeX, int cubeY, int cubeZ, ModelData modelData) {
        int cubeIndex = getCubeIndex(face, cubeX, cubeY, cubeZ);
        TextureRegion baseRegion = getBaseTextureRegion(face, cubeIndex);
        if (state == null) {
            return baseRegion;
        }

        return getConnectedTextureRegion(state, face, cubeX, cubeY, cubeZ, baseRegion, modelData);
    }

    private TextureRegion getBaseTextureRegion(Direction face, int cubeIndex) {
        return switch (face) {
            case UP -> getTopTextureRegion(cubeIndex);
            case DOWN -> getBottomTextureRegion(cubeIndex);
            case NORTH -> getNorthTextureRegion(cubeIndex);
            case SOUTH -> getSouthTextureRegion(cubeIndex);
            case WEST -> getWestTextureRegion(cubeIndex);
            case EAST -> getEastTextureRegion(cubeIndex);
        };
    }

    private TextureRegion getConnectedTextureRegion(BlockState state, Direction face, int cubeX, int cubeY, int cubeZ, TextureRegion baseRegion, ModelData modelData) {
        boolean eastConnected = state.getValue(BlockDecoCT.EAST);
        boolean westConnected = state.getValue(BlockDecoCT.WEST);
        boolean northConnected = state.getValue(BlockDecoCT.NORTH);
        boolean southConnected = state.getValue(BlockDecoCT.SOUTH);
        boolean upConnected = state.getValue(BlockDecoCT.UP);
        boolean downConnected = state.getValue(BlockDecoCT.DOWN);

        boolean hasDiagonalNE = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_NE);
        boolean hasDiagonalNW = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_NW);
        boolean hasDiagonalSE = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_SE);
        boolean hasDiagonalSW = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_SW);
        boolean hasDiagonalUE = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_UE);
        boolean hasDiagonalUW = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_UW);
        boolean hasDiagonalDE = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_DE);
        boolean hasDiagonalDW = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_DW);
        boolean hasDiagonalUN = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_UN);
        boolean hasDiagonalUS = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_US);
        boolean hasDiagonalDN = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_DN);
        boolean hasDiagonalDS = getDiagonalState(modelData, BlockDecoCTModelData.HAS_DIAGONAL_DS);

        if (face == Direction.NORTH || face == Direction.SOUTH) {
            if (hasDiagonalUE && eastConnected && upConnected) {
                if (face == Direction.NORTH && cubeX == 1 && cubeY == 1) {
                    return TextureRegions.DIAGONAL_TOP_LEFT;
                }
                if (face == Direction.SOUTH && cubeX == 1 && cubeY == 1) {
                    return TextureRegions.DIAGONAL_TOP_RIGHT;
                }
            }

            if (hasDiagonalUW && westConnected && upConnected) {
                if (face == Direction.NORTH && cubeX == 0 && cubeY == 1) {
                    return TextureRegions.DIAGONAL_TOP_RIGHT;
                }
                if (face == Direction.SOUTH && cubeX == 0 && cubeY == 1) {
                    return TextureRegions.DIAGONAL_TOP_LEFT;
                }
            }

            if (hasDiagonalDE && eastConnected && downConnected) {
                if (face == Direction.NORTH && cubeX == 1 && cubeY == 0) {
                    return TextureRegions.DIAGONAL_BOTTOM_LEFT;
                }
                if (face == Direction.SOUTH && cubeX == 1 && cubeY == 0) {
                    return TextureRegions.DIAGONAL_BOTTOM_RIGHT;
                }
            }

            if (hasDiagonalDW && westConnected && downConnected) {
                if (face == Direction.NORTH && cubeX == 0 && cubeY == 0) {
                    return TextureRegions.DIAGONAL_BOTTOM_RIGHT;
                }
                if (face == Direction.SOUTH && cubeX == 0 && cubeY == 0) {
                    return TextureRegions.DIAGONAL_BOTTOM_LEFT;
                }
            }
        }

        if (face == Direction.UP || face == Direction.DOWN) {
            if (hasDiagonalNE && eastConnected && northConnected) {
                if (face == Direction.UP && cubeX == 1 && cubeZ == 0) {
                    return TextureRegions.DIAGONAL_TOP_RIGHT;
                }
                if (face == Direction.DOWN && cubeX == 1 && cubeZ == 0) {
                    return TextureRegions.DIAGONAL_TOP_RIGHT;
                }
            }

            if (hasDiagonalNW && westConnected && northConnected) {
                if (face == Direction.UP && cubeX == 0 && cubeZ == 0) {
                    return TextureRegions.DIAGONAL_TOP_LEFT;
                }
                if (face == Direction.DOWN && cubeX == 0 && cubeZ == 0) {
                    return TextureRegions.DIAGONAL_TOP_LEFT;
                }
            }

            if (hasDiagonalSE && eastConnected && southConnected) {
                if (face == Direction.UP && cubeX == 1 && cubeZ == 1) {
                    return TextureRegions.DIAGONAL_BOTTOM_RIGHT;
                }
                if (face == Direction.DOWN && cubeX == 1 && cubeZ == 1) {
                    return TextureRegions.DIAGONAL_BOTTOM_RIGHT;
                }
            }

            if (hasDiagonalSW && westConnected && southConnected) {
                if (face == Direction.UP && cubeX == 0 && cubeZ == 1) {
                    return TextureRegions.DIAGONAL_BOTTOM_LEFT;
                }
                if (face == Direction.DOWN && cubeX == 0 && cubeZ == 1) {
                    return TextureRegions.DIAGONAL_BOTTOM_LEFT;
                }
            }
        }

        if (face == Direction.EAST || face == Direction.WEST) {
            if (hasDiagonalUN && upConnected && northConnected) {
                if (face == Direction.EAST && cubeZ == 0 && cubeY == 1) {
                    return TextureRegions.DIAGONAL_TOP_RIGHT;
                }
                if (face == Direction.WEST && cubeZ == 0 && cubeY == 1) {
                    return TextureRegions.DIAGONAL_BOTTOM_LEFT;
                }
            }

            if (hasDiagonalDN && downConnected && northConnected) {
                if (face == Direction.EAST && cubeZ == 0 && cubeY == 0) {
                    return TextureRegions.DIAGONAL_BOTTOM_RIGHT;
                }
                if (face == Direction.WEST && cubeZ == 0 && cubeY == 0) {
                    return TextureRegions.DIAGONAL_TOP_LEFT;
                }
            }

            if (hasDiagonalUS && upConnected && southConnected) {
                if (face == Direction.EAST && cubeZ == 1 && cubeY == 1) {
                    return TextureRegions.DIAGONAL_TOP_LEFT;
                }
                if (face == Direction.WEST && cubeZ == 1 && cubeY == 1) {
                    return TextureRegions.DIAGONAL_BOTTOM_RIGHT;
                }
            }

            if (hasDiagonalDS && downConnected && southConnected) {
                if (face == Direction.EAST && cubeZ == 1 && cubeY == 0) {
                    return TextureRegions.DIAGONAL_BOTTOM_LEFT;
                }
                if (face == Direction.WEST && cubeZ == 1 && cubeY == 0) {
                    return TextureRegions.DIAGONAL_TOP_RIGHT;
                }
            }
        }

        if (face == Direction.UP || face == Direction.DOWN) {
            if (northConnected && eastConnected && cubeX == 1 && cubeZ == 0) {
                if (face == Direction.UP) {
                    return TextureRegions.NORTH_EAST_CORNER;
                } else {
                    return TextureRegions.SOUTH_EAST_CORNER;
                }
            }

            if (northConnected && westConnected && cubeX == 0 && cubeZ == 0) {
                if (face == Direction.UP) {
                    return TextureRegions.NORTH_WEST_CORNER;
                } else {
                    return TextureRegions.SOUTH_WEST_CORNER;
                }
            }

            if (southConnected && eastConnected && cubeX == 1 && cubeZ == 1) {
                if (face == Direction.UP) {
                    return TextureRegions.SOUTH_EAST_CORNER;
                } else {
                    return TextureRegions.NORTH_EAST_CORNER;
                }
            }

            if (southConnected && westConnected && cubeX == 0 && cubeZ == 1) {
                if (face == Direction.UP) {
                    return TextureRegions.SOUTH_WEST_CORNER;
                } else {
                    return TextureRegions.NORTH_WEST_CORNER;
                }
            }
        }

        if ((face == Direction.NORTH || face == Direction.SOUTH || face == Direction.EAST || face == Direction.WEST) &&
                (upConnected || downConnected)) {

            if (face == Direction.NORTH) {

                if (upConnected && westConnected && cubeX == 0 && cubeY == 1) {
                    return TextureRegions.NORTH_EAST_CORNER;
                }

                if (upConnected && eastConnected && cubeX == 1 && cubeY == 1) {
                    return TextureRegions.NORTH_WEST_CORNER;
                }

                if (downConnected && westConnected && cubeX == 0 && cubeY == 0) {
                    return TextureRegions.SOUTH_EAST_CORNER;
                }

                if (downConnected && eastConnected && cubeX == 1 && cubeY == 0) {
                    return TextureRegions.SOUTH_WEST_CORNER;
                }
            }

            if (face == Direction.SOUTH) {

                if (upConnected && westConnected && cubeX == 0 && cubeY == 1) {
                    return TextureRegions.NORTH_WEST_CORNER;
                }

                if (upConnected && eastConnected && cubeX == 1 && cubeY == 1) {
                    return TextureRegions.NORTH_EAST_CORNER;
                }

                if (downConnected && westConnected && cubeX == 0 && cubeY == 0) {
                    return TextureRegions.SOUTH_WEST_CORNER;
                }

                if (downConnected && eastConnected && cubeX == 1 && cubeY == 0) {
                    return TextureRegions.SOUTH_EAST_CORNER;
                }
            }

            if (face == Direction.EAST) {

                if (upConnected && northConnected && cubeZ == 0 && cubeY == 1) {
                    return TextureRegions.NORTH_EAST_CORNER;
                }

                if (upConnected && southConnected && cubeZ == 1 && cubeY == 1) {
                    return TextureRegions.NORTH_WEST_CORNER;
                }

                if (downConnected && northConnected && cubeZ == 0 && cubeY == 0) {
                    return TextureRegions.SOUTH_EAST_CORNER;
                }

                if (downConnected && southConnected && cubeZ == 1 && cubeY == 0) {
                    return TextureRegions.SOUTH_WEST_CORNER;
                }
            }

            if (face == Direction.WEST) {

                if (upConnected && northConnected && cubeZ == 0 && cubeY == 1) {
                    return TextureRegions.NORTH_WEST_CORNER;
                }

                if (upConnected && southConnected && cubeZ == 1 && cubeY == 1) {
                    return TextureRegions.NORTH_EAST_CORNER;
                }

                if (downConnected && northConnected && cubeZ == 0 && cubeY == 0) {
                    return TextureRegions.SOUTH_WEST_CORNER;
                }

                if (downConnected && southConnected && cubeZ == 1 && cubeY == 0) {
                    return TextureRegions.SOUTH_EAST_CORNER;
                }
            }

            if (upConnected && cubeY == 1) {
                return getVerticalConnectionTexture(face, cubeX, cubeZ, true);
            }
            if (downConnected && cubeY == 0) {
                return getVerticalConnectionTexture(face, cubeX, cubeZ, false);
            }
        }

        if (face == Direction.UP || face == Direction.DOWN || face == Direction.NORTH || face == Direction.SOUTH) {
            if (eastConnected && cubeX == 1) {
                return getEastConnectionTexture(face, cubeY, cubeZ);
            }
            if (westConnected && cubeX == 0) {
                return getWestConnectionTexture(face, cubeY, cubeZ);
            }
        }

        if (face == Direction.UP || face == Direction.DOWN || face == Direction.WEST || face == Direction.EAST) {
            if (northConnected && cubeZ == 0) {
                return getNorthConnectionTexture(face, cubeY, cubeX);
            }
            if (southConnected && cubeZ == 1) {
                return getSouthConnectionTexture(face, cubeY, cubeX);
            }
        }

        return baseRegion;
    }

    private boolean getDiagonalState(ModelData modelData, ModelProperty<Boolean> property) {
        Boolean hasDiagonalBlock = modelData.get(property);
        return hasDiagonalBlock != null && hasDiagonalBlock;
    }

    private TextureRegion getVerticalConnectionTexture(Direction face, int cubeX, int cubeZ, boolean isTop) {
        return switch (face) {
            case NORTH -> {
                if (isTop) {
                    yield cubeX == 0 ? TextureRegions.NORTH_CONN_TOP : TextureRegions.NORTH_CONN_BOTTOM;
                } else {
                    yield cubeX == 0 ? TextureRegions.SOUTH_CONN_TOP : TextureRegions.SOUTH_CONN_BOTTOM;
                }
            }
            case SOUTH -> {
                if (isTop) {
                    yield cubeX == 0 ? TextureRegions.SOUTH_CONN_BOTTOM : TextureRegions.SOUTH_CONN_TOP;
                } else {
                    yield cubeX == 0 ? TextureRegions.NORTH_CONN_BOTTOM : TextureRegions.NORTH_CONN_TOP;
                }
            }
            case EAST -> {
                if (cubeZ == 0) {
                    yield isTop ? TextureRegions.NORTH_CONN_TOP : TextureRegions.SOUTH_CONN_TOP;
                } else {
                    yield isTop ? TextureRegions.NORTH_CONN_BOTTOM : TextureRegions.SOUTH_CONN_BOTTOM;
                }
            }
            case WEST -> {
                if (cubeZ == 0) {
                    yield isTop ? TextureRegions.SOUTH_CONN_BOTTOM : TextureRegions.NORTH_CONN_BOTTOM;
                } else {
                    yield isTop ? TextureRegions.SOUTH_CONN_TOP : TextureRegions.NORTH_CONN_TOP;
                }
            }
            default -> TextureRegions.NORTH_CONN_TOP;
        };
    }

    private TextureRegion getEastConnectionTexture(Direction face, int cubeY, int cubeZ) {
        if (face == Direction.UP || face == Direction.DOWN) {
            if (cubeY == 0) {
                return cubeZ == 0 ? TextureRegions.EAST_CONN_TOP : TextureRegions.EAST_CONN_BOTTOM;
            } else {
                return cubeZ == 0 ? TextureRegions.EAST_CONN_BOTTOM : TextureRegions.EAST_CONN_TOP;
            }
        } else {
            return cubeY == 0 ? TextureRegions.EAST_CONN_TOP : TextureRegions.EAST_CONN_BOTTOM;
        }
    }

    private TextureRegion getWestConnectionTexture(Direction face, int cubeY, int cubeZ) {
        if (face == Direction.UP || face == Direction.DOWN) {
            if (cubeY == 0) {
                return cubeZ == 0 ? TextureRegions.WEST_CONN_TOP : TextureRegions.WEST_CONN_BOTTOM;
            } else {
                return cubeZ == 0 ? TextureRegions.WEST_CONN_BOTTOM : TextureRegions.WEST_CONN_TOP;
            }
        } else {
            return cubeY == 0 ? TextureRegions.WEST_CONN_TOP : TextureRegions.WEST_CONN_BOTTOM;
        }
    }

    private TextureRegion getNorthConnectionTexture(Direction face, int cubeY, int cubeX) {
        if (face == Direction.UP) {
            return cubeX == 0 ? TextureRegions.NORTH_CONN_BOTTOM : TextureRegions.NORTH_CONN_TOP;
        } else if (face == Direction.DOWN) {
            return cubeX == 0 ? TextureRegions.NORTH_CONN_BOTTOM : TextureRegions.NORTH_CONN_TOP;
        } else {
            return cubeY == 0 ? TextureRegions.WEST_CONN_TOP : TextureRegions.WEST_CONN_BOTTOM;
        }
    }

    private TextureRegion getSouthConnectionTexture(Direction face, int cubeY, int cubeX) {
        if (face == Direction.UP) {
            return cubeX == 0 ? TextureRegions.SOUTH_CONN_BOTTOM : TextureRegions.SOUTH_CONN_TOP;
        } else if (face == Direction.DOWN) {
            return cubeX == 0 ? TextureRegions.SOUTH_CONN_BOTTOM : TextureRegions.SOUTH_CONN_TOP;
        } else {
            return cubeY == 0 ? TextureRegions.EAST_CONN_TOP : TextureRegions.EAST_CONN_BOTTOM;
        }
    }



    private TextureRegion getNorthTextureRegion(int cubeIndex) {
            return switch (cubeIndex) {
                case 1 -> TextureRegions.NORTH_CUBE_1;
                case 2 -> TextureRegions.NORTH_CUBE_2;
                case 3 -> TextureRegions.NORTH_CUBE_3;
                default -> TextureRegions.NORTH_CUBE_0;
            };
    }

    private TextureRegion getSouthTextureRegion(int cubeIndex) {
            return switch (cubeIndex) {
                case 1 -> TextureRegions.SOUTH_CUBE_1;
                case 2 -> TextureRegions.SOUTH_CUBE_2;
                case 3 -> TextureRegions.SOUTH_CUBE_3;
                default -> TextureRegions.SOUTH_CUBE_0;
            };
    }

    private TextureRegion getEastTextureRegion(int cubeIndex) {
            return switch (cubeIndex) {
                case 1 -> TextureRegions.EAST_CUBE_1;
                case 2 -> TextureRegions.EAST_CUBE_2;
                case 3 -> TextureRegions.EAST_CUBE_3;
                default -> TextureRegions.EAST_CUBE_0;
            };
    }

    private TextureRegion getWestTextureRegion(int cubeIndex) {
            return switch (cubeIndex) {
                case 1 -> TextureRegions.WEST_CUBE_1;
                case 2 -> TextureRegions.WEST_CUBE_2;
                case 3 -> TextureRegions.WEST_CUBE_3;
                default -> TextureRegions.WEST_CUBE_0;
            };
    }

    private int getCubeIndex(Direction face, int cubeX, int cubeY, int cubeZ) {
        return switch (face) {
            case UP -> cubeX + cubeZ * 2;
            case DOWN -> (1 - cubeX) + (1 - cubeZ) * 2;
            case NORTH -> (1 - cubeX) + (1 - cubeY) * 2;
            case SOUTH -> cubeX + (1 - cubeY) * 2;
            case WEST -> cubeZ + (1 - cubeY) * 2;
            case EAST -> (1 - cubeZ) + (1 - cubeY) * 2;
        };
    }

    private TextureRegion getTopTextureRegion(int cubeIndex) {
        return switch (cubeIndex) {
            case 1 -> TextureRegions.TOP_CUBE_1;
            case 2 -> TextureRegions.TOP_CUBE_2;
            case 3 -> TextureRegions.TOP_CUBE_3;
            default -> TextureRegions.TOP_CUBE_0;
        };
    }

    private TextureRegion getBottomTextureRegion(int cubeIndex) {
        return switch (cubeIndex) {
            case 1 -> TextureRegions.BOTTOM_CUBE_0;
            case 2 -> TextureRegions.BOTTOM_CUBE_3;
            case 3 -> TextureRegions.BOTTOM_CUBE_2;
            default -> TextureRegions.BOTTOM_CUBE_1;
        };
    }

    private void addCubeFace(List<BakedQuad> quads, Direction face,
                             float fromX, float fromY, float fromZ,
                             float toX, float toY, float toZ, TextureRegion region) {

        BakedQuad quad = createQuadForFace(face, fromX, fromY, fromZ, toX, toY, toZ, texture, region);
        quads.add(quad);
    }

    private BakedQuad createQuadForFace(Direction face, float fromX, float fromY, float fromZ,
                                        float toX, float toY, float toZ, TextureAtlasSprite sprite,
                                        TextureRegion region) {

        Vector3f[] vertices = getVerticesForFace(face, fromX, fromY, fromZ, toX, toY, toZ);
        Vec2[] uvs = getUVsForRegion(region, sprite);

        int[] vertexData = new int[32];

        for (int i = 0; i < 4; i++) {
            addVertex(vertexData, i, vertices[i], face, uvs[i]);
        }

        return new BakedQuad(vertexData, 0, face, sprite, true);
    }

    private Vector3f[] getVerticesForFace(Direction face, float fromX, float fromY, float fromZ,
                                          float toX, float toY, float toZ) {
        return switch (face) {
            case DOWN -> new Vector3f[]{
                    new Vector3f(fromX, fromY, toZ),
                    new Vector3f(fromX, fromY, fromZ),
                    new Vector3f(toX, fromY, fromZ),
                    new Vector3f(toX, fromY, toZ)
            };
            case UP -> new Vector3f[]{
                    new Vector3f(fromX, toY, fromZ),
                    new Vector3f(fromX, toY, toZ),
                    new Vector3f(toX, toY, toZ),
                    new Vector3f(toX, toY, fromZ)
            };
            case NORTH -> new Vector3f[]{
                    new Vector3f(toX, toY, fromZ),
                    new Vector3f(toX, fromY, fromZ),
                    new Vector3f(fromX, fromY, fromZ),
                    new Vector3f(fromX, toY, fromZ)
            };
            case SOUTH -> new Vector3f[]{
                    new Vector3f(fromX, toY, toZ),
                    new Vector3f(fromX, fromY, toZ),
                    new Vector3f(toX, fromY, toZ),
                    new Vector3f(toX, toY, toZ)
            };
            case WEST -> new Vector3f[]{
                    new Vector3f(fromX, toY, fromZ),
                    new Vector3f(fromX, fromY, fromZ),
                    new Vector3f(fromX, fromY, toZ),
                    new Vector3f(fromX, toY, toZ)
            };
            case EAST -> new Vector3f[]{
                    new Vector3f(toX, toY, toZ),
                    new Vector3f(toX, fromY, toZ),
                    new Vector3f(toX, fromY, fromZ),
                    new Vector3f(toX, toY, fromZ)
            };
        };
    }

    private Vec2[] getUVsForRegion(TextureRegion region, TextureAtlasSprite sprite) {

        float u0 = sprite.getU(region.startU * 16);
        float u1 = sprite.getU(region.endU * 16);
        float heightScale = 32f / 40f;
        float v0 = sprite.getV(region.startV * 16 * heightScale);
        float v1 = sprite.getV(region.endV * 16 * heightScale);

        return new Vec2[]{
                new Vec2(u0, v0),
                new Vec2(u0, v1),
                new Vec2(u1, v1),
                new Vec2(u1, v0)
        };
    }

    private void addVertex(int[] vertexData, int vertexIndex, Vector3f pos, Direction face,
                           Vec2 uv) {
        int baseIndex = vertexIndex * 8;

        vertexData[baseIndex] = Float.floatToRawIntBits(pos.x());
        vertexData[baseIndex + 1] = Float.floatToRawIntBits(pos.y());
        vertexData[baseIndex + 2] = Float.floatToRawIntBits(pos.z());
        vertexData[baseIndex + 3] = -1;
        vertexData[baseIndex + 4] = Float.floatToRawIntBits(uv.x);
        vertexData[baseIndex + 5] = Float.floatToRawIntBits(uv.y);
        vertexData[baseIndex + 6] = 0x00F000F0;

        Vector3f normal = getNormal(face);
        vertexData[baseIndex + 7] = encodeNormal(normal);
    }

    private Vector3f getNormal(Direction face) {
        return switch (face) {
            case DOWN -> new Vector3f(0, -1, 0);
            case UP -> new Vector3f(0, 1, 0);
            case NORTH -> new Vector3f(0, 0, -1);
            case SOUTH -> new Vector3f(0, 0, 1);
            case WEST -> new Vector3f(-1, 0, 0);
            case EAST -> new Vector3f(1, 0, 0);
        };
    }

    private int encodeNormal(Vector3f normal) {
        int x = (int)(normal.x * 127) & 0xFF;
        int y = (int)(normal.y * 127) & 0xFF;
        int z = (int)(normal.z * 127) & 0xFF;
        return (x << 16) | (y << 8) | z;
    }

    private void addSimpleInventoryGeometry(List<BakedQuad> quads, @Nullable Direction side) {
        float cubeSize = 0.5f;

        for (int x = 0; x < 2; x++) {
            for (int y = 0; y < 2; y++) {
                for (int z = 0; z < 2; z++) {
                    float fromX = x * cubeSize;
                    float fromY = y * cubeSize;
                    float fromZ = z * cubeSize;
                    float toX = (x + 1) * cubeSize;
                    float toY = (y + 1) * cubeSize;
                    float toZ = (z + 1) * cubeSize;

                    for (Direction face : Direction.values()) {
                        if (side != null && side != face) continue;

                        TextureRegion region = getTextureRegionForCubeFace(null, face, x, y, z, ModelData.EMPTY);
                        addCubeFace(quads, face, fromX, fromY, fromZ, toX, toY, toZ, region);
                    }
                }
            }
        }
    }

    private BooleanProperty getPropertyForDirection(Direction direction) {
        return switch (direction) {
            case NORTH -> BlockDecoCT.NORTH;
            case SOUTH -> BlockDecoCT.SOUTH;
            case EAST -> BlockDecoCT.EAST;
            case WEST -> BlockDecoCT.WEST;
            case UP -> BlockDecoCT.UP;
            case DOWN -> BlockDecoCT.DOWN;
        };
    }

    @Override public boolean useAmbientOcclusion() { return true; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean usesBlockLight() { return true; }
    @Override public boolean isCustomRenderer() { return false; }
    @Override public @NotNull TextureAtlasSprite getParticleIcon() { return texture; }
    @Override public @NotNull ItemTransforms getTransforms() { return transforms; }
    @Override public @NotNull ItemOverrides getOverrides() { return ItemOverrides.EMPTY; }
}