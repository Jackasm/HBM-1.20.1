package com.hbm.render.block.ct;

/**
 * Раскладка атласа CT-блока (32×40 px, шаг фрагмента 8×8):
 *
 * <pre>
 * строка 0:     4 квадранта базовой текстуры (f): TL, TR, BL, BR подряд
 * строки 1…4:   сетка 4×4 из 16 фрагментов — как {@code <текстура>_ct.png} в эталоне 1.7.10:
 *               (0,0) c|TL (1,0) c|TR (0,1) c|BL (1,1) c|BR      — внутренний угол
 *               (2,0) v|TL (3,0) v|TR (2,1) v|BL (3,1) v|BR      — вертикальный стык
 *               (0,2) h|TL (1,2) h|TR (0,3) h|BL (1,3) h|BR      — горизонтальный стык
 *               (2,2) j|TL (3,2) j|TR (2,3) j|BL (3,3) j|BR      — Т-стык
 * </pre>
 *
 * <p>Раскладка повторяет {@code IconCT} эталона: смещение по U — для вертикального стыка и
 * Т-стыка, по V — для горизонтального и Т-стыка, дальше биты «правый» и «нижний».</p>
 */
public final class TextureRegions {

    /** Текстура в пикселях по U; по V используется та же шкала, атлас выше на 8 px. */
    private static final int TEXTURE_WIDTH = 32;

    private static final TextureRegion[] FRAGMENTS = new TextureRegion[20];

    static {
        // 0…3 — квадранты базовой текстуры. В порте они лежат в строке 0 подряд:
        // TL (x=0), TR (x=8), BL (x=16), BR (x=24), а не сеткой 2×2, как в эталонной базе 16×16.
        for (int i = 0; i < 4; i++) {
            FRAGMENTS[i] = new TextureRegion(i * 8, 0, 8, 8, TEXTURE_WIDTH);
        }

        // 4…19 — сетка 4×4, сдвинутая на строку вниз, потому что строка 0 занята базой.
        for (int i = 4; i < FRAGMENTS.length; i++) {
            int type = i & ~3;                                  // 4=c, 8=j, 12=h, 16=v
            int column = ((type == 16 || type == 8) ? 2 : 0) + (i & 1);
            int row = ((type == 12 || type == 8) ? 2 : 0) + ((i >> 1) & 1);
            FRAGMENTS[i] = new TextureRegion(column * 8, (row + 1) * 8, 8, 8, TEXTURE_WIDTH);
        }
    }

    private TextureRegions() {
    }

    /** Регион по индексу фрагмента 0…19. */
    public static TextureRegion byFragment(int fragment) {
        return FRAGMENTS[Math.floorMod(fragment, FRAGMENTS.length)];
    }
}
