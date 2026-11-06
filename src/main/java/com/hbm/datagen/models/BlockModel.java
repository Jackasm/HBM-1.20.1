package com.hbm.datagen.models;

public enum BlockModel {
    CUBE_ALL,             // обычный куб с одной текстурой
    CUBE_ALL_TRANSLUCENT,
    CUBE_ALL_SWITCH,
    BLAST_FURNACE,
    ORE,                  // руда
    CUBE_TOP_BOTTOM,      // верх, низ и сторона
    CROSS,                // крест для растений/крестовых моделей
    LAYERED,              // блок со слоями (например, foam_layer, sand_boron_layer)
    DECO_CT,              // CT-блок (историческое имя, то же, что CT)
    CT,                   // блок с connected textures: общий лоадер hbm:ct_geometry
    CT_TRANSLUCENT,       // то же, но в RenderType.translucent (стёкла)
    AIR,
    MULTIBLOCK,
    MULTIBLOCK_NO_ITEM,
    PANE,  // стеклянная панель
    ENTITYBLOCK_ANIMATED,  // блок с BlockEntityRenderer (кастомный рендер)
    MODEL_ITEM,
    MODEL_ITEM_STATIC,
    TRAPDOOR,
    CHAIN,
    BEDROCK_ORE,          // специальная модель для BedrockOre с двумя слоями (основа + оверлей)
    GRATE,                // блоки со слоями (layer)
    OBJ_MODEL_FULL,       // OBJ с поддержкой поворота по всем направлениям
    OBJ_MODEL_HORIZONTAL, // OBJ с поддержкой поворота только по горизонтали
    STATIC_OBJ,           // OBJ без поворотов
    DOOR,                 // дверь
    PILLAR,               // колонна (cube_column)
    ENUM_BLOCK,           // блок с перечисляемыми типами (concrete_colored_ext)
    ENUM_OBJ,  // для блоков с enum типами и OBJ моделями
    ENUM_PILLAR,          // колонна с перечисляемыми типами
    CONCRETE_SUPER,       // специальный блок с повреждениями
    PLANT,        // один блок с cross-моделью (цветы)
    TALL_PLANT,   // один блок с tall-моделью (нижняя/верхняя часть)
    CUBE_FRONT_SIDE,
    CUBE_FRONT_SIDE_HORIZONTAL,
    STAIRS,               // ступени
    SLAB,  // для слябов
    LADDER,  // простой блок с обычной моделью (как LadderBlock)
    RAIL,  // рельсы (rail_flat / rail_raised_ne / rail_raised_sw / rail_corner)
    NONE                  // без генерации (по умолчанию)
}