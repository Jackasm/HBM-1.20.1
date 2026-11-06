package com.hbm.render.block.ct;

import com.hbm.blocks.generic.CTBlock;
import com.hbm.blocks.generic.CTModelData;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Модель CT-блока: каждая из шести граней делится на четыре квадранта, фрагмент для квадранта
 * берётся из {@link CTModelData} (считается по миру), UV — из атласа 32×40
 * ({@link TextureRegions}).
 *
 * <p>Порядок углов и порядок вершин повторяют {@code RenderBlocksCT} эталона 1.7.10, чтобы
 * фрагменты вставали на те же места, что и в оригинале. Квады не несут собственного света:
 * светмапа нулевая, освещение считает рендерер по миру.</p>
 */
public class CTBakedModel implements IDynamicBakedModel {

    /**
     * Углы грани в порядке эталона: TL, TR, BL, BR. Индекс — {@link Direction#ordinal()}
     * (DOWN, UP, NORTH, SOUTH, WEST, EAST).
     */
    private static final float[][][] FACE_CORNERS = {
            // DOWN
            {{0F, 0F, 1F}, {1F, 0F, 1F}, {0F, 0F, 0F}, {1F, 0F, 0F}},
            // UP
            {{0F, 1F, 0F}, {1F, 1F, 0F}, {0F, 1F, 1F}, {1F, 1F, 1F}},
            // NORTH
            {{1F, 1F, 0F}, {0F, 1F, 0F}, {1F, 0F, 0F}, {0F, 0F, 0F}},
            // SOUTH
            {{0F, 1F, 1F}, {1F, 1F, 1F}, {0F, 0F, 1F}, {1F, 0F, 1F}},
            // WEST
            {{0F, 1F, 0F}, {0F, 1F, 1F}, {0F, 0F, 0F}, {0F, 0F, 1F}},
            // EAST
            {{1F, 1F, 1F}, {1F, 1F, 0F}, {1F, 0F, 1F}, {1F, 0F, 0F}}
    };

    /** Отношение высоты атласа (40 px) к его ширине (32 px). */
    private static final float ATLAS_V_SCALE = 32F / 40F;

    private final TextureAtlasSprite sprite;
    private final ItemTransforms transforms;
    private final boolean translucent;

    public CTBakedModel(IGeometryBakingContext context,
                        Function<Material, TextureAtlasSprite> spriteGetter,
                        boolean translucent) {
        Material textureMaterial = context.getMaterial("texture");
        if (textureMaterial == null) {
            textureMaterial = context.getMaterial("particle");
        }
        this.sprite = spriteGetter.apply(textureMaterial);
        this.transforms = context.getTransforms();
        this.translucent = translucent;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                             @NotNull RandomSource rand, @NotNull ModelData modelData,
                                             @Nullable RenderType renderType) {
        // Грани отдаются поштучно проходами по направлениям: отдельный null-проход не нужен.
        if (side == null) {
            return List.of();
        }

        boolean isBlock = state != null && state.getBlock() instanceof CTBlock;
        CTModelData data = modelData.get(CTModelData.PROPERTY);

        if (isBlock && data != null && data.isHidden(side)) {
            return List.of();
        }

        float[][] corners = FACE_CORNERS[side.ordinal()];
        float[] tl = corners[0];
        float[] tr = corners[1];
        float[] bl = corners[2];
        float[] br = corners[3];

        float[] tc = midpoint(tl, tr);
        float[] bc = midpoint(bl, br);
        float[] cl = midpoint(tl, bl);
        float[] cr = midpoint(tr, br);
        float[] cc = midpoint(tc, bc);

        float[][] quadTL = {tl, tc, cl, cc};
        float[][] quadTR = {tc, tr, cc, cr};
        float[][] quadBL = {cl, cc, bl, bc};
        float[][] quadBR = {cc, cr, bc, br};

        List<BakedQuad> quads = new ArrayList<>(4);
        for (int quadrant = 0; quadrant < 4; quadrant++) {
            int fragment = isBlock && data != null ? data.fragment(side, quadrant) : quadrant;
            quads.add(createQuad(side,
                    quadTL[quadrant], quadTR[quadrant], quadBL[quadrant], quadBR[quadrant],
                    TextureRegions.byFragment(fragment)));
        }
        return quads;
    }

    private static float[] midpoint(float[] first, float[] second) {
        return new float[]{
                (first[0] + second[0]) / 2F,
                (first[1] + second[1]) / 2F,
                (first[2] + second[2]) / 2F
        };
    }

    /**
     * Квад по четырём углам суб-грани. Порядок вершин — как в {@code drawSubFace} эталона:
     * TR, TL, BL, BR; UV — (maxU, minV), (minU, minV), (minU, maxV), (maxU, maxV).
     */
    private BakedQuad createQuad(Direction side, float[] ptl, float[] ptr, float[] pbl, float[] pbr,
                                 TextureRegion region) {
        float u0 = sprite.getU(region.startU * 16F);
        float u1 = sprite.getU(region.endU * 16F);
        float v0 = sprite.getV(region.startV * 16F * ATLAS_V_SCALE);
        float v1 = sprite.getV(region.endV * 16F * ATLAS_V_SCALE);

        int[] vertexData = new int[32];
        int normal = encodeNormal(side);

        writeVertex(vertexData, 0, ptr, u1, v0, normal);
        writeVertex(vertexData, 1, ptl, u0, v0, normal);
        writeVertex(vertexData, 2, pbl, u0, v1, normal);
        writeVertex(vertexData, 3, pbr, u1, v1, normal);

        return new BakedQuad(vertexData, -1, side, sprite, true);
    }

    private static void writeVertex(int[] data, int index, float[] pos, float u, float v, int normal) {
        int base = index * 8;
        data[base] = Float.floatToRawIntBits(pos[0]);
        data[base + 1] = Float.floatToRawIntBits(pos[1]);
        data[base + 2] = Float.floatToRawIntBits(pos[2]);
        data[base + 3] = -1;                       // ARGB: белый, без тонировки
        data[base + 4] = Float.floatToRawIntBits(u);
        data[base + 5] = Float.floatToRawIntBits(v);
        data[base + 6] = 0;                        // светмапу считает рендерер по миру
        data[base + 7] = normal;
    }

    private static int encodeNormal(Direction side) {
        int x = (side.getStepX() * 127) & 0xFF;
        int y = (side.getStepY() * 127) & 0xFF;
        int z = (side.getStepZ() * 127) & 0xFF;
        return (x << 16) | (y << 8) | z;
    }

    @Override
    public boolean useAmbientOcclusion() {
        // AO выключено намеренно, как у CT-блоков в других модах. Причина: грань CT-блока — это
        // четыре квада 8×8 с разными фрагментами, а AO в 1.20.1 считается на грань целиком
        // (AmbientOcclusionFace заполняет четыре значения, putQuadData раздаёт их вершинам по
        // индексу, calculateShape смотрит на габарит квада). Для неполного квада это даёт
        // повёрнутые значения — на стыках и у диагональных соседей появляются «тени лесенкой».
        // Направленное затенение граней (верх/бока) остаётся, уходит только затемнение по углам.
        return false;
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand,
                                                      @NotNull ModelData data) {
        return ChunkRenderTypeSet.of(translucent ? RenderType.translucent() : RenderType.solid());
    }

    @Override
    public boolean isGui3d() {
        return true;
    }

    @Override
    public boolean usesBlockLight() {
        return true;
    }

    @Override
    public boolean isCustomRenderer() {
        return false;
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        return sprite;
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return transforms;
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }
}
